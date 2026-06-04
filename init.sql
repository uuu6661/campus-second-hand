DROP DATABASE IF EXISTS sc_db;
CREATE DATABASE sc_db DEFAULT CHARACTER SET utf8mb4;
USE sc_db;

CREATE TABLE `user` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `nickname` VARCHAR(50) DEFAULT NULL,
  `phone` VARCHAR(20) DEFAULT NULL,
  `avatar` VARCHAR(255) DEFAULT NULL,
  `status` TINYINT DEFAULT 1,
  `role` TINYINT DEFAULT 0 COMMENT '0-普通用户 1-管理员',
  `audit_status` TINYINT DEFAULT 0 COMMENT '0-待审核 1-审核通过 2-审核驳回',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `user` (`username`, `password`, `nickname`, `phone`, `role`, `audit_status`, `status`) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '管理员', '13800138000', 1, 1, 1),
('test1', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '测试用户1', '13800138001', 0, 1, 1),
('test2', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '测试用户2', '13800138002', 0, 1, 1);

CREATE TABLE `category` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(50) NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `category` (`name`) VALUES
('图书教材'),
('数码产品'),
('生活用品'),
('服装鞋包'),
('运动户外');

CREATE TABLE `goods` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `price` DECIMAL(10,2) NOT NULL,
  `original_price` DECIMAL(10,2),
  `category_id` BIGINT,
  `images` VARCHAR(500),
  `contact_type` VARCHAR(10),
  `contact_info` VARCHAR(100),
  `user_id` BIGINT NOT NULL,
  `status` TINYINT DEFAULT 1 COMMENT '0-下架 1-在售 2-已售',
  `audit_status` TINYINT DEFAULT 1 COMMENT '0-待审核 1-审核通过 2-审核驳回',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`category_id`) REFERENCES `category`(`id`),
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `announcement` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(100) NOT NULL,
  `content` TEXT NOT NULL,
  `is_pinned` TINYINT DEFAULT 0 COMMENT '0-不置顶 1-置顶',
  `is_active` TINYINT DEFAULT 1 COMMENT '0-失效 1-有效',
  `start_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `end_time` DATETIME,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `message` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `sender_id` BIGINT NOT NULL COMMENT '发送者',
  `receiver_id` BIGINT NOT NULL COMMENT '接收者',
  `content` VARCHAR(500) NOT NULL COMMENT '消息内容',
  `is_read` TINYINT DEFAULT 0 COMMENT '0未读 1已读',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_sender_receiver (sender_id, receiver_id),
  INDEX idx_receiver_time (receiver_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `report` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `reporter_id` BIGINT NOT NULL COMMENT '举报人',
  `goods_id` BIGINT NOT NULL COMMENT '被举报商品',
  `seller_id` BIGINT NOT NULL COMMENT '被举报卖家',
  `reason` VARCHAR(50) NOT NULL COMMENT '举报原因',
  `description` VARCHAR(500) COMMENT '详细描述',
  `status` TINYINT DEFAULT 0 COMMENT '0待处理 1已处理 2已驳回',
  `admin_remark` VARCHAR(255) COMMENT '管理员处理备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `handle_time` DATETIME COMMENT '处理时间',
  INDEX idx_reporter_goods (reporter_id, goods_id),
  INDEX idx_goods_id (goods_id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `announcement` (`title`, `content`, `is_pinned`, `is_active`, `start_time`, `end_time`) VALUES
('欢迎使用校园二手交易平台', '亲爱的同学们，欢迎使用我们的校园二手交易平台！在这里你可以轻松买卖闲置物品，让资源得到更好的利用。', 1, 1, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY)),
('平台交易安全提示', '请勿私下转账，平台交易有保障。如遇诈骗请及时联系管理员。', 0, 1, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY));

INSERT INTO `goods` (`title`, `description`, `price`, `original_price`, `category_id`, `images`, `contact_type`, `contact_info`, `user_id`, `status`, `audit_status`) VALUES
('二手笔记本电脑', '9成新，配置:i5-1035G1,8G内存,512G固态,独立显卡', 2500.00, 5000.00, 2, 'http://localhost:8080/uploads/images/test1.jpg', 'wx', 'wx123456', 2, 1, 1),
('考研英语真题', '2024版考研英语一真题解析，九成新', 35.00, 58.00, 1, 'http://localhost:8080/uploads/images/test2.jpg', 'wx', 'wx654321', 2, 1, 1),
('无线蓝牙耳机', '苹果AirPods Pro 2代，使用半年', 800.00, 1899.00, 2, 'http://localhost:8080/uploads/images/test3.jpg', 'phone', '13800138001', 3, 1, 1),
('运动背包', '户外登山背包，防水材质，40L容量', 120.00, 299.00, 5, 'http://localhost:8080/uploads/images/test4.jpg', 'wx', 'wx_user123', 2, 1, 1),
('闲置羽绒服', '优衣库轻薄羽绒服，M码，黑色', 150.00, 499.00, 4, 'http://localhost:8080/uploads/images/test5.jpg', 'phone', '13800138002', 3, 2, 1);