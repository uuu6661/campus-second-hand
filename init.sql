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

INSERT INTO `goods` (`title`, `description`, `price`, `original_price`, `category_id`, `images`, `contact_type`, `contact_info`, `user_id`, `status`, `audit_status`) VALUES
('二手笔记本电脑', '9成新，配置:i5-1035G1,8G内存,512G固态,独立显卡', 2500.00, 5000.00, 2, 'http://localhost:8080/uploads/images/test1.jpg', 'wx', 'wx123456', 2, 1, 1),
('考研英语真题', '2024版考研英语一真题解析，九成新', 35.00, 58.00, 1, 'http://localhost:8080/uploads/images/test2.jpg', 'wx', 'wx654321', 2, 1, 1),
('无线蓝牙耳机', '苹果AirPods Pro 2代，使用半年', 800.00, 1899.00, 2, 'http://localhost:8080/uploads/images/test3.jpg', 'phone', '13800138001', 3, 1, 1),
('运动背包', '户外登山背包，防水材质，40L容量', 120.00, 299.00, 5, 'http://localhost:8080/uploads/images/test4.jpg', 'wx', 'wx_user123', 2, 1, 1),
('闲置羽绒服', '优衣库轻薄羽绒服，M码，黑色', 150.00, 499.00, 4, 'http://localhost:8080/uploads/images/test5.jpg', 'phone', '13800138002', 3, 2, 1);