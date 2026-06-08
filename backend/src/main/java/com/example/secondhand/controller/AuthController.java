package com.example.secondhand.controller;

/**
 * 认证控制器 - 处理用户注册和登录
 * 
 * 功能说明：
 * 1. 用户注册 - 创建新用户账号，密码使用BCrypt加密
 * 2. 用户登录 - 验证用户身份，生成JWT Token
 * 
 * 安全机制：
 * - 密码使用BCrypt单向加密存储，不可逆
 * - 登录失败不区分具体原因，防止用户枚举攻击
 * - 新用户需要管理员审核才能登录
 * 
 * @author SecondHand Team
 * @version 1.0
 */

import com.example.secondhand.dto.ApiResponse;
import com.example.secondhand.dto.LoginRequest;
import com.example.secondhand.dto.LoginResponse;
import com.example.secondhand.dto.RegisterRequest;
import com.example.secondhand.dto.RegisterResponse;
import com.example.secondhand.entity.User;
import com.example.secondhand.repository.UserRepository;
import com.example.secondhand.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // 用户数据仓库 - 用于操作用户表
    private final UserRepository userRepository;
    
    // 密码加密器 - BCrypt算法，单向加密，无法解密
    private final BCryptPasswordEncoder passwordEncoder;
    
    // JWT工具类 - 用于生成和验证JSON Web Token
    private final JwtUtil jwtUtil;

    // 构造函数注入依赖
    public AuthController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        // 初始化BCrypt密码加密器，默认强度10
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * 用户注册接口
     * 
     * 接口路径：POST /api/auth/register
     * 访问权限：公开（无需登录）
     * 
     * 请求参数：
     * - username: 用户名（3-20位字母或数字）
     * - password: 密码（6-20位）
     * - nickname: 昵称（可选）
     * 
     * 业务流程：
     * 1. 验证用户名格式
     * 2. 验证密码格式
     * 3. 检查用户名是否已存在
     * 4. 使用BCrypt加密密码
     * 5. 创建用户对象，设置待审核状态
     * 6. 保存到数据库
     * 
     * 返回结果：
     * - 成功：返回用户ID和用户名，提示等待审核
     * - 失败：返回错误信息（用户名格式错误/已存在/密码格式错误）
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody RegisterRequest request) {
        // 1️⃣ 获取请求参数
        String username = request.getUsername();
        String password = request.getPassword();

        // 2️⃣ 验证用户名格式：必须是3-20位字母或数字
        if (!isValidUsername(username)) {
            // 返回400错误：用户名格式不符合要求
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "用户名必须是3-20位字母或数字"));
        }

        // 3️⃣ 验证密码格式：必须是6-20位
        if (!isValidPassword(password)) {
            // 返回400错误：密码格式不符合要求
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "密码必须是6-20位"));
        }

        // 4️⃣ 检查用户名是否已被注册
        // existsByUsername 是 Spring Data JPA 自动生成的方法
        if (userRepository.existsByUsername(username)) {
            // 返回400错误：用户名已被使用
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "用户名已存在"));
        }

        // 5️⃣ 创建用户对象
        User user = new User();
        user.setUsername(username);                                    // 设置用户名
        user.setPassword(passwordEncoder.encode(password));           // 使用BCrypt加密密码
        user.setNickname(request.getNickname());                      // 设置昵称（可选）
        user.setAuditStatus(0);                                       // 设置审核状态：0=待审核

        // 6️⃣ 保存用户到数据库
        // save方法会返回带有自增ID的完整用户对象
        User savedUser = userRepository.save(user);

        // 7️⃣ 构建响应数据
        RegisterResponse response = new RegisterResponse(
            savedUser.getId(),      // 用户ID
            savedUser.getUsername()  // 用户名
        );
        
        // 8️⃣ 返回成功响应，提示需要等待管理员审核
        return ResponseEntity.ok(ApiResponse.success("注册成功，请等待管理员审核", response));
    }

    /**
     * 用户登录接口
     * 
     * 接口路径：POST /api/auth/login
     * 访问权限：公开（无需登录）
     * 
     * 请求参数：
     * - username: 用户名
     * - password: 密码
     * 
     * 业务流程：
     * 1. 验证用户名和密码不为空
     * 2. 根据用户名查找用户
     * 3. 使用BCrypt验证密码
     * 4. 检查账号状态（是否封禁）
     * 5. 检查审核状态（是否通过审核）
     * 6. 生成JWT Token
     * 
     * 安全说明：
     * - 登录失败统一返回"账号或密码错误"，不区分具体原因
     * - 防止攻击者通过不同错误信息枚举有效用户名
     * 
     * JWT Token包含的信息：
     * - userId: 用户ID
     * - username: 用户名
     * - nickname: 昵称
     * - role: 角色（0=普通用户，1=管理员）
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        // 1️⃣ 获取请求参数
        String username = request.getUsername();
        String password = request.getPassword();

        // 2️⃣ 验证用户名不能为空
        if (username == null || username.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "用户名不能为空"));
        }

        // 3️⃣ 验证密码不能为空
        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "密码不能为空"));
        }

        // 4️⃣ 根据用户名查找用户
        // findByUsername 是 Spring Data JPA 自动生成的方法，返回 Optional<User>
        User user = userRepository.findByUsername(username).orElse(null);

        // 5️⃣ 检查用户是否存在
        // 注意：这里不区分"用户不存在"和"密码错误"，统一返回相同错误
        if (user == null) {
            // 返回400错误：账号或密码错误（模糊处理）
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "账号或密码错误"));
        }

        // 6️⃣ 验证密码是否正确
        // passwordEncoder.matches(明文密码, 加密密码) 自动处理BCrypt验证
        if (!passwordEncoder.matches(password, user.getPassword())) {
            // 返回400错误：账号或密码错误
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "账号或密码错误"));
        }

        // 7️⃣ 检查账号是否被封禁
        // status=0 表示账号已被封禁
        if (user.getStatus() == 0) {
            // 返回403错误：账号已被封禁
            return ResponseEntity.badRequest().body(ApiResponse.error(403, "账号已被封禁"));
        }

        // 8️⃣ 检查账号是否通过审核
        // auditStatus=1 表示已通过审核，role=1 表示管理员（管理员无需审核）
        if (user.getAuditStatus() != 1 && user.getRole() != 1) {
            // 返回403错误：账号未通过审核
            return ResponseEntity.badRequest().body(ApiResponse.error(403, "账号未通过审核，请等待管理员审核"));
        }

        // 9️⃣ 生成JWT Token
        // JwtUtil.generateToken会创建一个包含用户信息的Token，有效期24小时
        String token = jwtUtil.generateToken(
            user.getId(),                    // 用户ID
            user.getUsername(),              // 用户名
            user.getNickname(),              // 昵称
            user.getRole()                   // 角色
        );

        // 🔟 构建登录响应数据
        LoginResponse response = new LoginResponse(
            token,                           // JWT Token
            user.getId(),                    // 用户ID
            user.getUsername(),              // 用户名
            user.getNickname(),              // 昵称
            user.getRole()                   // 角色
        );
        
        // 1️⃣1️⃣ 返回成功响应，包含Token和用户信息
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 验证用户名格式
     * 
     * 规则：3-20位，只能包含字母和数字
     * 正则表达式：^[a-zA-Z0-9]{3,20}$
     * 
     * @param username 用户名
     * @return true=格式正确，false=格式错误
     */
    private boolean isValidUsername(String username) {
        // 使用正则表达式验证：必须是字母或数字，长度3-20位
        return username != null && username.matches("^[a-zA-Z0-9]{3,20}$");
    }

    /**
     * 验证密码格式
     * 
     * 规则：6-20位
     * 
     * @param password 密码
     * @return true=格式正确，false=格式错误
     */
    private boolean isValidPassword(String password) {
        // 检查密码长度是否在6-20位之间
        return password != null && password.length() >= 6 && password.length() <= 20;
    }
}
