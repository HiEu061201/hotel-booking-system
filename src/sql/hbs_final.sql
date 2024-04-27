
DROP DATABASE IF EXISTS hotelsystem;
CREATE DATABASE hotelsystem;

USE hotelsystem;
-- v1.3: Tạo thêm bảng transactions_type
-- v1.1: Thêm thuộc tính cho hotel_booking (check_in_actual, check_out_actual)
-- v1.1: Thêm thuộc tính cho hotel_booking
-- v1.0: Cập nhật trạng thái cho room_category
-- v0.9: Cập nhật thuộc tính cho  Hotel_Booking
-- v0.8: Thêm table refund_account 
-- v0.7: Cập nhật thêm thuộc tính trong Hotel_Booking
-- v0.6: thêm  Yêu cầu hoàn tiền
-- v0.5  thêm  biên nhận thanh toán và hoàn tiền
-- v0.4: userId trong hotel_booking để null
-- v0.3: Thêm liên kết hotel_booking với phòng và type phòng
-- V0.2: Cập nhật Hotel_Booking, Thêm giá phòng thay đổi theo khoảng thời gian và ghi lại lịch sử
-- v0.1: Cập nhật các thuộc tính của các bảng
CREATE TABLE user (
    user_id			BIGINT  		NOT NULL 	AUTO_INCREMENT,
    name 			VARCHAR(50)		NOT NULL,
    dob 			DATE 					,
    email 			VARCHAR(100) 	NOT NULL  UNIQUE ,
    password 		VARCHAR(500) 	NOT NULL  	,
    address 		VARCHAR(100) 			,
    gender			VARCHAR(50)				,
    phone 			VARCHAR(10)     	UNIQUE	,
    image			VARCHAR(500)			,
    status 			BIT  					,
    PRIMARY KEY (user_id)
);
-- Create the Role table
CREATE TABLE role (
    role_id 		BIGINT 			NOT NULL	AUTO_INCREMENT,
    role_name 		VARCHAR(50) 	NOT NULL,
    status 			BIT 			NOT NULL,
    PRIMARY KEY (role_id)
);

-- Create the User_Role table
CREATE TABLE user_role (
	id 				BIGINT 		 	NOT NULL	AUTO_INCREMENT,
    user_id 		BIGINT 		 	NOT NULL,
    role_id 		BIGINT          NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (role_id) REFERENCES role(role_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- Create the Token table
CREATE TABLE token (
    id 					BIGINT 			NOT NULL	AUTO_INCREMENT,
	user_id 			BIGINT 		 	NOT NULL,
    token 				VARCHAR(500) 	NOT NULL,
    expiration_date		datetime(6)		NOT NULL,
    PRIMARY KEY (id),
	FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- Create the Contact table
CREATE TABLE contact (
    contact_id 		BIGINT	 		 NOT NULL 	AUTO_INCREMENT, 
    title 			VARCHAR(500) 	 NOT NULL,
    email 			VARCHAR(100)	 NOT NULL,
    content 		VARCHAR(255) 	 NOT NULL,
    user_id 		BIGINT 		 	 		 ,
    status 			BIT 			 		 ,
	FOREIGN KEY (user_id) REFERENCES user(user_id),
    PRIMARY KEY (contact_id)
);


-- Create the Room_Image table
CREATE TABLE room_image (
    room_image_id 	BIGINT	 		 NOT NULL 	AUTO_INCREMENT,
    room_image1	    VARCHAR(255) 	 NOT NULL,
    room_image2 	VARCHAR(255) 	 NOT NULL,
    room_image3 	VARCHAR(255) 	 NOT NULL,
    room_image4 	VARCHAR(255) 	 NOT NULL,
    PRIMARY KEY (room_image_id)
);
-- Create the Service table
CREATE TABLE service (
    service_id 			BIGINT	 		 NOT NULL 	AUTO_INCREMENT,
    service_name 		VARCHAR(50) 	 NOT NULL,
    service_des 		VARCHAR(500)			 ,
    service_price		DECIMAL(15, 2)   NOT NULL,
    service_note 		VARCHAR(500)			 ,
    service_image		VARCHAR(500)	 NOT NULL,
    status 				BIT 			 NOT NULL,
    PRIMARY KEY (service_id)
);

-- Create the News table
CREATE TABLE news (
    new_id 			BIGINT 			 NOT NULL 	AUTO_INCREMENT,
    user_id 		BIGINT 		 	 NOT NULL,
    image 			VARCHAR(255)			 ,
    descriptions 	VARCHAR(1000)			 NOT NULL,
    date 			DATETIME(6) 				 ,
    title 			VARCHAR(200)		 NOT NULL,
	status 			BIT 			 		 ,
    PRIMARY KEY (new_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- Create the room_furniture table
CREATE TABLE room_furniture  (
    furniture_id 			BIGINT	 		 NOT NULL 	AUTO_INCREMENT,
    furniture_name 			VARCHAR(50) 	 NOT NULL,
    furniture_price 		DECIMAL(15,2) 	 NOT NULL,
    status 					BIT,
    PRIMARY KEY (furniture_id)
);
-- Create the room_categories table
CREATE TABLE room_categories (
    room_category_id 		BIGINT 			 NOT NULL 	AUTO_INCREMENT,
    room_category_name 		VARCHAR(50)      NOT NULL,
    description 		    VARCHAR(500)				,
    square 					DOUBLE 			 NOT NULL,
    number_person 			INT 			 NOT NULL,
    image 					VARCHAR(5000)			 ,
    status					bit				 NOT NULL,
    PRIMARY KEY (room_category_id)
);


-- Create the Category_Amenities table
CREATE TABLE category_room_furniture (
	id 						BIGINT 		NOT NULL 	AUTO_INCREMENT,
	furniture_id 			BIGINT 		NOT NULL,
    room_category_id		BIGINT 		NOT NULL,
    quantity 				INT					,
    status 					BIT					,
    note 					VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (furniture_id) REFERENCES room_furniture(furniture_id),
    FOREIGN KEY (room_category_id) REFERENCES room_categories(room_category_id)
);
-- Create the Price_Categories_Change_History table
CREATE TABLE category_room_price	 (
	room_price_id 			BIGINT 			 NOT NULL 	AUTO_INCREMENT,
    room_category_id		BIGINT 			 NOT NULL,
    start_date 				DATE 			 NOT NULL,				
    end_date  				DATE 			 NOT NULL,   
	price 					DECIMAL(15, 2)   NOT NULL,
    created_date 			DATE 			 NOT NULL,
    flag					int				 NOT NULL,
    PRIMARY KEY (room_price_id),
    FOREIGN KEY (room_category_id) REFERENCES room_categories(room_category_id)
);



-- create the  room_status table 
CREATE TABLE room_status (
	room_status_id 		BIGINT		     NOT NULL		 AUTO_INCREMENT,
	status_name 		    		VARCHAR(50) 	 NOT NULL,
   PRIMARY KEY (room_status_id )
);
-- Create the Room table
CREATE TABLE room (
    room_id 			 	BIGINT			 NOT NULL	AUTO_INCREMENT,
    room_image_id 			BIGINT 			 NOT NULL,
    room_category_id 		BIGINT 		 	 NOT NULL,
    room_status_id 			BIGINT           NOT NULL,
    description 			VARCHAR(500)			 ,
    floor					int				 NOT NULL,
    room_name				VARCHAR(50)		 NOT NULL,
    bed_size				VARCHAR(100),
	view_city				VARCHAR(100),
    PRIMARY KEY (room_id),
	FOREIGN KEY (room_status_id) REFERENCES room_status(room_status_id),
    FOREIGN KEY (room_category_id) REFERENCES room_categories(room_category_id),
    FOREIGN KEY (room_image_id) REFERENCES room_image(room_image_id)

);





-- Create the hotel_booking_status table
CREATE TABLE hotel_booking_status (
    status_id 				BIGINT           NOT NULL  AUTO_INCREMENT,
    status_name 		    VARCHAR(50) 	 NOT NULL,
    PRIMARY KEY (status_id)
);
-- Create the hotel_booking table
CREATE TABLE hotel_booking (
    hotel_booking_id 		BIGINT		     NOT NULL		 AUTO_INCREMENT,
    user_id 				BIGINT 		 			,
	status_id				BIGINT 		 	 DEFAULT 1		  NOT NULL,
    total_room				INT				 NOT NULL,
    service_id				BIGINT			 ,
    name 					VARCHAR(255),
    email				    VARCHAR(100),
    address 				VARCHAR(255),
    phone 					VARCHAR(255),
    deposit_price			DECIMAL(10, 2)		NOT NULL,
    total_price				DECIMAL(10, 2)		NOT NULL,
    check_in 				DATETIME(6) 		NOT NULL,
    check_out 				DATETIME(6)  		NOT NULL,
	check_in_actual     	DATETIME(6)			NOT NULL, 
    check_out_actual    	DATETIME(6)			NOT NULL,  
    valid_booking			bit					NOT NULL,
    PRIMARY KEY (hotel_booking_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id),
	FOREIGN KEY (status_id) REFERENCES hotel_booking_status(status_id),
	FOREIGN KEY (service_id) REFERENCES service(service_id)
  
);

-- Create the Room_Feedback table
CREATE TABLE feedback (
	feedback_id   		 BIGINT			AUTO_INCREMENT  NOT NULL,
	user_id 			 BIGINT 		NOT NULL,
     hotel_booking_id 		BIGINT		     NOT NULL,
    title				varchar(255)	NOT NULL,
    comment 			VARCHAR(255)	NOT NULL,
    create_date 		DATETIME(6) ,
    status 				BIT,
    PRIMARY KEY (feedback_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id),
	FOREIGN KEY (hotel_booking_id) REFERENCES hotel_booking(hotel_booking_id)
);

-- Create the hotel_booking_service table 
CREATE TABLE hotel_booking_service (
    hotel_booking_service_id 	 BIGINT 		NOT NULL 	AUTO_INCREMENT,
    hotel_booking_id        	 BIGINT 		NOT NULL,
    service_id               	 BIGINT 		NOT NULL,
    create_date					DATETIME(6) 	NOT NULL,
    quantity					int	,
    total_price					DECIMAL(10, 2),
    PRIMARY KEY (hotel_booking_service_id),
    FOREIGN KEY (hotel_booking_id) REFERENCES hotel_booking(hotel_booking_id),
    FOREIGN KEY (service_id) REFERENCES service(service_id)
);


-- create table booking_room_details
CREATE TABLE booking_room_details (
    booking_room_id     BIGINT         NOT NULL   AUTO_INCREMENT,
    hotel_booking_id    BIGINT         NOT NULL,
    room_category_id	BIGINT         NOT NULL,
	room_id             BIGINT         NOT NULL, 
    PRIMARY KEY (booking_room_id),
    FOREIGN KEY (hotel_booking_id) REFERENCES hotel_booking(hotel_booking_id),
	FOREIGN KEY (room_id) REFERENCES room(room_id) ,
	FOREIGN KEY (room_category_id) REFERENCES room_categories(room_category_id)

);

-- Create the customer_cancellation_reasons table
CREATE TABLE customer_cancellation_reasons (
    reason_id 				BIGINT 			NOT NULL	AUTO_INCREMENT ,
	reason_description 		VARCHAR(255) 	NOT NULL,
	PRIMARY KEY (reason_id)
    
);
-- Create the bank_list table
CREATE TABLE bank_list (
    bank_id      BIGINT       NOT NULL  AUTO_INCREMENT,
    bank_name   VARCHAR(255)  NOT NULL,
    PRIMARY KEY (bank_id)
);
-- Create the refund_account table
CREATE TABLE refund_account (
    account_id      BIGINT       AUTO_INCREMENT,
	user_id 		BIGINT 		 ,
    bank_id			BIGINT 		 NOT NULL,
    account_name    VARCHAR(255) NOT NULL,
    account_number  VARCHAR(20)  NOT NULL,
    PRIMARY KEY (account_id),
	FOREIGN KEY (user_id) REFERENCES user(user_id),
	FOREIGN KEY (bank_id) REFERENCES bank_list (bank_id)
);


CREATE TABLE payment_type (
	payment_id			 BIGINT				 	NOT NULL	 AUTO_INCREMENT,
    payment_name		 VARCHAR(50)			 NOT NULL,
	PRIMARY KEY (payment_id)
);

CREATE TABLE transactions_type (
	transactions_type_id			 BIGINT				 	NOT NULL	 AUTO_INCREMENT,
    transactions_type_name			 VARCHAR(50)			NOT NULL,
	PRIMARY KEY (transactions_type_id)
);

CREATE TABLE transactions (
    transaction_id 	  		BIGINT				NOT NULL		 AUTO_INCREMENT ,
    hotel_booking_id 		BIGINT			 	NOT NULL,
    transactions_type_id	BIGINT			 	NOT NULL,
    status 					VARCHAR(50)			NOT NULL,		-- Trạng thái của giao dịch
    amount 					DECIMAL(10, 2)		NOT NULL,	
    payment_id 				BIGINT  			NOT NULL,
    vnpay_transaction_id 	VARCHAR(255)        NOT NULL,		-- Mã giao dịch từ VNPay.
    created_date			DATETIME(6)  		NOT NULL,
    content 				VARCHAR(255),
	PRIMARY KEY (transaction_id),
	FOREIGN KEY (hotel_booking_id) REFERENCES hotel_booking (hotel_booking_id),
    FOREIGN KEY (transactions_type_id) REFERENCES transactions_type (transactions_type_id),
	FOREIGN KEY (payment_id) REFERENCES payment_type (payment_id)
);



-- Create the customer_cancellation table
CREATE TABLE customer_cancellation (
    cancellation_id			 BIGINT 		NOT NULL 	AUTO_INCREMENT,
    hotel_booking_id 		 BIGINT			NOT NULL,
	transaction_id 	  		 BIGINT			NOT NULL,
    cancel_time 			DATETIME(6) 	NOT NULL,
    reason_id 				 BIGINT,
	account_id      		 BIGINT      	NOT NULL,
    refund_amount 			 DECIMAL(18, 2) NOT NULL,
    other_reason			 VARCHAR(255),
    status 					 int		    NOT NULL,       -- 0 : Chờ xét duyệt 
															-- 1 : Đã duyệt / chờ hoàn tinè
													       -- 2: Đã hoàn tiền
	PRIMARY KEY (cancellation_id),
    FOREIGN KEY (hotel_booking_id) REFERENCES hotel_booking (hotel_booking_id),
    FOREIGN KEY (reason_id) REFERENCES 	customer_cancellation_reasons (reason_id),
	FOREIGN KEY (account_id) REFERENCES 	refund_account (account_id),
    FOREIGN KEY (transaction_id) REFERENCES transactions (transaction_id)
);

CREATE TABLE early_checkout_refunds (
    early_checkout_refund_id    BIGINT               NOT NULL         AUTO_INCREMENT,
    transaction_id           	BIGINT               NOT NULL,
    refunded_amount             DECIMAL(10, 2),
    refund_date                 DATETIME(6)          NOT NULL,
    reason                      VARCHAR(255),
	status                     bit  		NOT NULL,    
    PRIMARY KEY (early_checkout_refund_id),
    FOREIGN KEY (transaction_id) REFERENCES transactions (transaction_id)
);


-- Thêm dữ liệu user
INSERT INTO user (name, dob, email, password, address, phone, status , image, gender)
VALUES
    ('HieuLBM', '1990-05-15', 'hieulbm@gmail.com', '$2a$10$pvseQWHIsX64G.CEVaAJKeoC2c18UFvqAVMevQJ.YdxYclVnLf0b6', '123 Main St', '0326505417', 1,'','Nam'),
    ('KhoaNN', '1985-08-20', 'khoann@gmail.com', '$2a$10$pvseQWHIsX64G.CEVaAJKeoC2c18UFvqAVMevQJ.YdxYclVnLf0b6', '456 Elm St', '0376535417', 1,'','Nam'),
    ('TruongMQ', '1995-02-10', 'truongmq@gmail.com', '$2a$10$pvseQWHIsX64G.CEVaAJKeoC2c18UFvqAVMevQJ.YdxYclVnLf0b6', '789 Oak St', '0316505417', 1,'','Nam'),
    ('HieuDT78', '1982-03-25', 'hieudt78@gmail.com', '$2a$10$pvseQWHIsX64G.CEVaAJKeoC2c18UFvqAVMevQJ.YdxYclVnLf0b6', '101 Pine St', '0346505417', 1,'','Nam'),
    ('HieuDT15', '1998-12-05', 'lebaminhhieuyh@gmail.com', '$2a$10$pvseQWHIsX64G.CEVaAJKeoC2c18UFvqAVMevQJ.YdxYclVnLf0b6', '222 Maple St', '0316502417', 1,'','Nam'),
	('HieuLBM2', '1998-12-05', 'lebaminhhieugl0612@gmail.com', '$2a$10$pvseQWHIsX64G.CEVaAJKeoC2c18UFvqAVMevQJ.YdxYclVnLf0b6', '222 Maple St', '0323525417', 1,'DreamShaper_v5_Moon_in_a_beautiful_and_wide_galaxy_0.jpg','Nam');



-- Thêm dữ liệu role
INSERT INTO role (role_name, status)              
VALUES
    ('ADMIN', 1),
    ('SALEMANAGER', 1),
    ('RECEPTIONISTS', 1),
    ('ACCOUNTING', 1),
    ('MANAGEMENT', 1),
    ('CUSTOMER', 1);

-- Thêm dữ liệu user_role
INSERT INTO user_role (id,user_id, role_id)
VALUES 
    (1,1, 1),
    (2,2, 2),
    (3,3, 3),
    (4,4, 4),
    (5,5, 5),  
	(6,6, 6); 

-- Thêm 6 bản ghi vào bảng
INSERT INTO room_categories (room_category_name, description, square, number_person, image,status)
VALUES ('Phòng Classic', 'Phòng tiêu chuẩn thoải mái với diện tích 25m², tạo cảm giác như ở nhà.', 25.0, 2, 'room1.webp',1),
       ('Phòng Deluxe', 'Phòng rộng rãi với thiết kế hiện đại, trang bị đầy đủ tiện nghi cần thiết.', 35.5, 2, 'room2.webp',1),
       ('Phòng Executive Suite', 'Phòng cao cấp với không gian riêng biệt, phòng khách riêng và các tiện ích sang trọng.', 60.0, 4, 'room3.webp',1),
       ('Phòng Superior', 'Phòng thoáng đãng với thiết kế truyền thống, phù hợp cho gia đình hoặc nhóm bạn.', 20.0, 3, 'room1.webp',1),
       ('Phòng Family Suite', 'Phòng dành cho gia đình với không gian riêng biệt cho trẻ em, đảm bảo sự thoải mái cho mọi người.', 40.0, 4, 'room2.webp',1),
       ('Phòng Business', 'Phòng thiết kế chuyên nghiệp dành cho những khách hàng công tác, đi kèm với một không gian làm việc riêng biệt.', 50.0, 2, 'room3.webp',1);




-- Thêm dữ liệu vào bảng "category_room_price"
INSERT INTO category_room_price (room_category_id, start_date, end_date, price, created_date, flag) VALUES
(1, '2023-01-01', '2023-12-31', 1000000.00, '2023-10-23', 1),
(2, '2023-01-01', '2023-12-31', 1500000.00, '2023-10-23', 1),
(3, '2023-01-01', '2023-12-31', 3500000.00, '2023-10-23', 1),
(4, '2023-01-01', '2023-12-31', 2000000.00, '2023-10-23', 1),
(5, '2023-01-01', '2023-12-31', 2500000.00, '2023-10-23', 1),
(6, '2023-01-01', '2023-12-31', 2800000.00, '2023-10-23', 1);

    
    
    -- Thêm dữ liệu Contact
INSERT INTO contact (user_id,title, email, content,status)
VALUES
    (6,'General Inquiry', 'contact@example.com', 'I have some questions about your hotel services.',  1),
    (6,'Reservation Request', 'reservation@example.com', 'I would like to book a room for two nights.', 1),
    (6,'Feedback', 'feedback@example.com', 'I had a great stay at your hotel. Thank you!',  1),
    (6,'Complaint', 'complaint@example.com', 'I had some issues with my room. Please address this.', 1),
    (6,'Job Application', 'job@example.com', 'I am interested in a job opportunity at your hotel.', 1);

INSERT INTO news (user_id, image, descriptions, date, title, status) 
VALUES
('1', 'news1.jpg', 'Afternoon Tea được xem là nét văn hóa tinh túy nhất ở nước Anh, được giới thiệu bởi nữ công tước thứ bảy của xứ Bedford, Anna trong những năm 1840. Phong tục “trà chiều” phổ biến đến mức mà trong suốt những năm 80, những người phụ nữ tầng lớp thượng lưu và trong xã hội sẽ đeo gang tay và đội mũ để tham dự tiệc trà chiều vào khoảng lúc 4:00pm đến 5:00pm.', '2023-10-25', 'Trà chiều tại khách sạn 3HKT', 1),
('1', 'news2.png', 'Với tên gọi đầy cảm hứng “Moonlight Symphony - Vũ Khúc Ánh Trăng”, bộ sưu tập bánh trung thu của khách sạn Hà Nội 3HKT mang đến một làn gió mới trẻ trung, phong cách và đẳng cấp cho mùa đoàn viên 2023.', '2023-10-22', 'Bộ sưu tập bánh trung thu khách sạn Hà Nội 3HKT 2023', 1),
('1', 'b6.webp', 'Ngay cả khi bạn không muốn đi lang thang xa, bạn vẫn có thể mở rộng tầm nhìn của mình và tận hưởng những điều đặc biệt. Cho dù bạn đã sẵn sàng khám phá một kỳ nghỉ ngắn ngày hay tìm kiếm sự sang trọng đích thực ở những địa điểm quyến rũ, chúng tôi đã tập hợp năm nơi nghỉ ngơi cuối tuần kỳ diệu có thể phù hợp với bạn.', '2023-10-15', 'Năm kỳ nghỉ cuối năm kỳ diệu', 1),
('1', 'b4.jpg', 'Ngày 20/4, tại khách sạn 3HKT - Hà Nội, Văn phòng Cơ quan Thường trực Ban Chỉ đạo quốc gia khắc phục hậu quả bom mìn và chất độc hóa học sau chiến tranh (gọi tắt là văn phòng 701) và Cơ quan phát triển quốc tế Hoa Kỳ (USAID) đã diễn ra lễ ký kết bản ghi nhận ý định về hỗ trợ người khuyết tật Việt Nam.', '2023-10-10', '3HKT Hotel tổ chức thành công lễ ký bản ghi nhận ý nghĩa giữa Việt Nam và Hoa Kỳ về hỗ trợ người khuyết tật Việt Nam tại các tỉnh ưu tiên', 1),
('1', 'b5.jpg', 'Lần này, một khách hàng đã sử dụng nhà hàng của chúng tôi để tổ chức đám cưới. Thật là một ngày tuyệt vời khi khách sạn trở thành một phần của niềm hạnh phúc. Cảm ơn rất nhiều. Chúng tôi cũng đã chuẩn bị hai nhiếp ảnh gia. Chúng tôi đã chuẩn bị thức ăn theo kiểu tự chọn.', '2023-10-05', 'Kế hoạch tổ chức tiệc: Nhà hàng tiệc cưới', 1),
('1', 'b1.jpg', 'Để đảm bảo an toàn sức khoẻ của toàn bộ khách hàng và cộng đồng The 3HKT Hotel đã chủ động triển khai thông điệp “5K” của Bộ Y Tế', '2023-09-25', 'The 3HKT Hotel đã chủ động triển khai thông điệp \"5K\" của Bộ Y Tế', 1);

INSERT INTO room_status (status_name) VALUES
('Đang ở'),					-- 1;		('Đã check_in'),   			-- 2 
('Sẵn sàng'),       		-- 2; 		
('Chưa dọn'),			 	-- 3; 	    ('Đã check_out'),			-- 3
('Đã dọn'),			 	    -- 4; 	    ('Đã check_out'),			-- 3
('Ngưng hoạt động');		-- 5; 


INSERT INTO hotel_booking_status (status_name) VALUES
('Chưa check_in'),   		-- 1
('Đã check_in'),   			-- 2 
('Đã check_out'),			-- 3
('Đã huỷ');					-- 4


-----------------------
INSERT INTO room_furniture (furniture_name, furniture_price, status) VALUES
('Điều hòa nhiệt độ', 100000.00, 1),
('Bồn tắm', 75000.00, 1),
('Thiết bị pha cà phê/trà', 25000.00, 1),
('Bàn trang điểm', 50000.00, 1),
('Máy sấy tóc', 30000.00, 1),
('Bàn làm việc', 20000.00, 1),
('Két an toàn trong phòng', 50000.00, 1),
('WiFi', 10000.00, 1),
('Tủ lạnh mini', 75000.00, 1),
('TiVi', 80000.00, 1),
('Điện thoại bàn', 30000.00, 1),
('Khăn tắm', 5000.00, 1),
('Dầu gội', 5000.00, 1),
('Bàn chải-kem đánh răng- dao cạo râu', 10000.00, 1),
('Tủ quần áo', 15000.00, 1);


INSERT INTO category_room_furniture (furniture_id, room_category_id, quantity, status, note) VALUES
-- Type Room1
(1, 1, 1, 1, '1 điều hòa nhiệt độ'), 
(2, 1, 1, 1, '1 phòng tắm có bồn tắm và vòi hoa sen'),
(3, 1, 1, 1, '1 thiết bị pha cà phê/trà'),
(4, 1, 1, 1, '1 bàn trang điểm'),
(5, 1, 1, 1, '1 máy sấy tóc'),
(6, 1, 1, 1, '1 tắm nước nóng'),
(7, 1, 1, 1, '1 két an toàn trong phòng'),
(8, 1, 1, 1, '1 Internet WiFi trong phòng'),
(9, 1, 1, 1, '1 tủ lạnh mini'),
(10,1, 1, 1, '1 TV– cáp/Vệ tinh'),

-- Type Room2
(1, 2, 1, 1, '1 điều hòa nhiệt độ'), 
(2, 2, 1, 1, '1 phòng tắm có bồn tắm và vòi hoa sen'),
(3, 2, 1, 1, '1 thiết bị pha cà phê/trà'),
(4, 2, 1, 1, '1 bàn trang điểm'),
(5, 2, 1, 1, '1 máy sấy tóc'),
(6, 2, 1, 1, '1 tắm nước nóng'),
(7, 2, 1, 1, '1 két an toàn trong phòng'),
(8, 2, 1, 1, '1 Internet WiFi trong phòng'),
(9, 2, 1, 1, '1 tủ lạnh mini'),
(10, 2, 1, 1, '1 TV– cáp/Vệ tinh'),
-- Type Room3
(1, 3, 1, 1, '1 điều hòa nhiệt độ'), 
(2, 3, 1, 1, '1 phòng tắm có bồn tắm và vòi hoa sen'),
(3, 3, 1, 1, '1 thiết bị pha cà phê/trà'),
(4, 3, 1, 1, '1 bàn trang điểm'),
(5, 3, 1, 1, '1 máy sấy tóc'),
(6, 3, 1, 1, '1 tắm nước nóng'),
(7, 3, 1, 1, '1 két an toàn trong phòng'),
(8, 3, 1, 1, '1 Internet WiFi trong phòng'),
(9, 3, 1, 1, '1 tủ lạnh mini'),
(10, 3, 1, 1, '1 TV– cáp/Vệ tinh'),

-- Type Room4
(1, 4, 1, 1, '1 điều hòa nhiệt độ'), 
(2, 4, 1, 1, '1 phòng tắm có bồn tắm và vòi hoa sen'),
(3, 4, 1, 1, '1 thiết bị pha cà phê/trà'),
(4, 4, 1, 1, '1 bàn trang điểm'),
(5, 4, 1, 1, '1 máy sấy tóc'),
(6, 4, 1, 1, '1 tắm nước nóng'),
(7, 4, 1, 1, '1 két an toàn trong phòng'),
(8, 4, 1, 1, '1 Internet WiFi trong phòng'),
(9, 4, 1, 1, '1 tủ lạnh mini'),
(10, 4, 1, 1, '1 TV– cáp/Vệ tinh'),
-- Type Room5
(1, 5, 1, 1, '1 điều hòa nhiệt độ'), 
(2, 5, 1, 1, '1 phòng tắm có bồn tắm và vòi hoa sen'),
(3, 5, 1, 1, '1 thiết bị pha cà phê/trà'),
(4, 5, 1, 1, '1 bàn trang điểm'),
(5, 5, 1, 1, '1 máy sấy tóc'),
(6, 5, 1, 1, '1 tắm nước nóng'),
(7, 5, 1, 1, '1 két an toàn trong phòng'),
(8, 5, 1, 1, '1 Internet WiFi trong phòng'),
(9, 5, 1, 1, '1 tủ lạnh mini'),
(10, 5, 1, 1, '1 TV– cáp/Vệ tinh'),
-- Type Room6
(1, 6, 1, 1, '1 điều hòa nhiệt độ'), 
(2, 6, 1, 1, '1 phòng tắm có bồn tắm và vòi hoa sen'),
(3, 6, 1, 1, '1 thiết bị pha cà phê/trà'),
(4, 6, 1, 1, '1 bàn trang điểm'),
(5, 6, 1, 1, '1 máy sấy tóc'),
(6, 6, 1, 1, '1 tắm nước nóng'),
(7, 6, 1, 1, '1 két an toàn trong phòng'),
(8, 6, 1, 1, '1 Internet WiFi trong phòng'),
(9, 6, 1, 1, '1 tủ lạnh mini'),
(10, 6, 1, 1, '1 TV– cáp/Vệ tinh');




-- Assuming room_image entries related to the rooms
INSERT INTO room_image (room_image1, room_image2, room_image3, room_image4) VALUES
('roomCate1.jpg', 'roomCate1.1.jpg', 'roomCate1.2.jpg', 'roomCate1.3.jpg'),
('roomCate1.jpg', 'roomCate1.1.jpg', 'roomCate1.2.jpg', 'roomCate1.3.jpg'),
('roomCate1.jpg', 'roomCate1.1.jpg', 'roomCate1.2.jpg', 'roomCate1.3.jpg'),
('roomCate1.jpg', 'roomCate1.1.jpg', 'roomCate1.2.jpg', 'roomCate1.3.jpg'),
('roomCate1.jpg', 'roomCate1.1.jpg', 'roomCate1.2.jpg', 'roomCate1.3.jpg'),
('roomCate1.jpg', 'roomCate1.1.jpg', 'roomCate1.2.jpg', 'roomCate1.3.jpg'),

('roomCate2.jpg', 'roomCate2.1.jpg', 'roomCate2.2.jpg', 'roomCate2.3.jpg'),
('roomCate2.jpg', 'roomCate2.1.jpg', 'roomCate2.2.jpg', 'roomCate2.3.jpg'),
('roomCate2.jpg', 'roomCate2.1.jpg', 'roomCate2.2.jpg', 'roomCate2.3.jpg'),
('roomCate2.jpg', 'roomCate2.1.jpg', 'roomCate2.2.jpg', 'roomCate2.3.jpg'),
('roomCate2.jpg', 'roomCate2.1.jpg', 'roomCate2.2.jpg', 'roomCate2.3.jpg'),
('roomCate2.jpg', 'roomCate2.1.jpg', 'roomCate2.2.jpg', 'roomCate2.3.jpg'),

('roomCate3.jpg', 'roomCate3.1.jpg', 'roomCate3.2.jpg', 'roomCate3.3.jpg'),
('roomCate3.jpg', 'roomCate3.1.jpg', 'roomCate3.2.jpg', 'roomCate3.3.jpg'),
('roomCate3.jpg', 'roomCate3.1.jpg', 'roomCate3.2.jpg', 'roomCate3.3.jpg'),

('roomCate4.jpg', 'roomCate4.1.jpg', 'roomCate4.2.jpg', 'roomCate4.3.jpg'),
('roomCate4.jpg', 'roomCate4.1.jpg', 'roomCate4.2.jpg', 'roomCate4.3.jpg'),

('roomCate5.jpg', 'roomCate5.1.jpg', 'roomCate5.2.jpg', 'roomCate5.3.jpg'),
('roomCate5.jpg', 'roomCate5.1.jpg', 'roomCate5.2.jpg', 'roomCate5.3.jpg'),

('roomCate6.jpg', 'roomCate6.1.jpg', 'roomCate6.2.jpg', 'roomCate5.3.jpg'),
('roomCate6.jpg', 'roomCate6.1.jpg', 'roomCate6.2.jpg', 'roomCate5.3.jpg'),
('roomCate6.jpg', 'roomCate6.1.jpg', 'roomCate6.2.jpg', 'roomCate5.3.jpg');


INSERT INTO service (service_name, service_price, service_des, service_note, service_image, status) VALUES
('Đưa đón sân bay', 300000.00, 'Dịch vụ đưa đón từ sân bay', NULL, 'airport_transfer.jpg', 1),
('Dịch vụ bảo vệ', 100000.00, 'Dịch vụ an ninh', NULL, 'security_service.jpg', 1),
('Giặt sấy', 100000.00, 'Dịch vụ giặt và sấy đồ', NULL, 'laundry.jpg', 1),
('Nước uống', 20000.00, 'Nhiều loại nước', NULL, 'drinks.jpg', 1),
('Massage', 500000.00, 'Spa, làm đẹp & sức khoẻ', NULL, 'massage.jpg', 1),
('Hội trường', 1000000.00, 'Họp và hợp tác làm ăn', NULL, 'service6.webp', 1);



-- Chèn dữ liệu mẫu vào bảng Room

INSERT INTO room (room_image_id, room_category_id, description,room_status_id,room_name, bed_size, floor, view_city) VALUES

-- Phòng loại 1 (room_category_id = 1)
(1, 1, 'Phòng được thiết kế với các trang thiết bị hiện đại, cách bày trí sang trọng và ấn tượng. Các cửa sổ rộng tạo cho Quý khách cảm giác thư giãn, thoải mái. Hạng phòng này rất phù hợp cho các khách thương gia có nhu cầu lưu trú dài hạn hoặc khách từ các công ty lữ hành.',2,   'A101', '1 giường đôi', 1, 'Tầm nhìn ra Thành phố'),
(2, 1, 'Phòng được thiết kế với các trang thiết bị hiện đại, cách bày trí sang trọng và ấn tượng. Các cửa sổ rộng tạo cho Quý khách cảm giác thư giãn, thoải mái. Hạng phòng này rất phù hợp cho các khách thương gia có nhu cầu lưu trú dài hạn hoặc khách từ các công ty lữ hành.',2,  'A102', '1 giường đôi', 2, 'Tầm nhìn ra Thành phố'),
(4, 1, 'Phòng được thiết kế với các trang thiết bị hiện đại, cách bày trí sang trọng và ấn tượng. Các cửa sổ rộng tạo cho Quý khách cảm giác thư giãn, thoải mái. Hạng phòng này rất phù hợp cho các khách thương gia có nhu cầu lưu trú dài hạn hoặc khách từ các công ty lữ hành.',2,   'A103', '1 giường đôi', 3, 'Tầm nhìn ra Biển'),
(5, 1, 'Phòng được thiết kế với các trang thiết bị hiện đại, cách bày trí sang trọng và ấn tượng. Các cửa sổ rộng tạo cho Quý khách cảm giác thư giãn, thoải mái. Hạng phòng này rất phù hợp cho các khách thương gia có nhu cầu lưu trú dài hạn hoặc khách từ các công ty lữ hành.',2, 'A104', '1 giường đôi', 4, 'Tầm nhìn ra Biển'),
(6, 1, 'Phòng được thiết kế với các trang thiết bị hiện đại, cách bày trí sang trọng và ấn tượng. Các cửa sổ rộng tạo cho Quý khách cảm giác thư giãn, thoải mái. Hạng phòng này rất phù hợp cho các khách thương gia có nhu cầu lưu trú dài hạn hoặc khách từ các công ty lữ hành.',2, 'A105', '1 giường đôi', 5, 'Tầm nhìn ra Thành phố'),

-- Phòng loại 2 (room_category_id = 2)
(7, 2, 'Được thiết kế với chi tiết hiện đại và tinh tế, tầm nhìn thành phố, hồ bơi hoặc vườn; phù hợp cho các cặp đôi và gia đình trẻ.',2,  'A106', '1 giường đôi', 1, 'Tầm nhìn ra Thành phố'),
(8, 2,'Được thiết kế với chi tiết hiện đại và tinh tế, tầm nhìn thành phố, hồ bơi hoặc vườn; phù hợp cho các cặp đôi và gia đình trẻ.',2,  'A107', '2 giường đơn', 2, 'Tầm nhìn ra Thành phố'),
(9, 2, 'Được thiết kế với chi tiết hiện đại và tinh tế, tầm nhìn thành phố, hồ bơi hoặc vườn; phù hợp cho các cặp đôi và gia đình trẻ.',2,   'A108', '2 giường đơn', 3, 'Tầm nhìn ra Biển'),
(10, 2, 'Được thiết kế với chi tiết hiện đại và tinh tế, tầm nhìn thành phố, hồ bơi hoặc vườn; phù hợp cho các cặp đôi và gia đình trẻ.',2,   'A109', '1 giường đôi', 4, 'Tầm nhìn ra Biển'),
(11, 2, 'Được thiết kế với chi tiết hiện đại và tinh tế, tầm nhìn thành phố, hồ bơi hoặc vườn; phù hợp cho các cặp đôi và gia đình trẻ.',2,   'A110', '1 giường đôi', 5, 'Tầm nhìn ra Thành phố'),
(12, 2, 'Được thiết kế với chi tiết hiện đại và tinh tế, tầm nhìn thành phố, hồ bơi hoặc vườn; phù hợp cho các cặp đôi và gia đình trẻ.',2,   'A111', '2 giường đơn', 1, 'Tầm nhìn ra Thành phố'),

-- Phòng loại 3 (room_category_id = 3)
(13, 3, 'Phòng cao cấp với không gian riêng biệt, phòng khách riêng và các tiện ích sang trọng.',2,   'A112', '2 giường đôi', 1, 'Tầm nhìn ra Thành phố'),
(14, 3, 'Phòng cao cấp với không gian riêng biệt, phòng khách riêng và các tiện ích sang trọng.',2,   'A113', '2 giường đôi',2, 'Tầm nhìn ra Thành phố'),
(15, 3, 'PPhòng cao cấp với không gian riêng biệt, phòng khách riêng và các tiện ích sang trọng.', 2,  'A114', '2 giường đôi', 3, 'Tầm nhìn ra Thành phố'),
(14, 3, 'Phòng cao cấp với không gian riêng biệt, phòng khách riêng và các tiện ích sang trọng.',2,   'A130', '1 giường đôi, 1 giường đơn',2, 'Tầm nhìn ra Thành phố'),
(15, 3, 'Phòng cao cấp với không gian riêng biệt, phòng khách riêng và các tiện ích sang trọng.', 2,  'A129', '1 giường đôi, 1 giường đơn', 3, 'Tầm nhìn ra Thành phố'),

-- Phòng loại 4 (room_category_id = 4)
(16, 4, 'Phòng thoáng đãng với thiết kế truyền thống, phù hợp cho gia đình hoặc nhóm bạn.',2,   'A115', '1 giường đôi, 1 giường đơn', 4, 'Tầm nhìn ra Biển'),
(17, 4, 'Phòng thoáng đãng với thiết kế truyền thống, phù hợp cho gia đình hoặc nhóm bạn.',2,   'A116', '1 giường đôi, 1 giường đơn', 5, 'Tầm nhìn ra Biển'),
(16, 4, 'Phòng thoáng đãng với thiết kế truyền thống, phù hợp cho gia đình hoặc nhóm bạn.',2,   'A127', '1 giường đôi, 1 giường đơn', 4, 'Tầm nhìn ra Biển'),
(17, 4, 'Phòng thoáng đãng với thiết kế truyền thống, phù hợp cho gia đình hoặc nhóm bạn.',2,   'A128', '1 giường đôi, 1 giường đơn', 5, 'Tầm nhìn ra Biển'),

-- Phòng loại 5 (room_category_id = 5)
(18, 5, 'Phòng tiêu chuẩn với tầm nhìn ra vườn để có kỳ nghỉ thư giãn.',2,  'A117', '1 giường đôi, 1 giường đơn', 2, 'Tầm nhìn ra Thành phố'),
(19, 5, 'Phòng thiết kế chuyên nghiệp dành cho những khách hàng công tác, đi kèm với một không gian làm việc riêng biệt.',2,   'A118', '1 giường đôi, 1 giường đơn', 1, 'Tầm nhìn ra Thành phố'),
(18, 5, 'Phòng thiết kế chuyên nghiệp dành cho những khách hàng công tác, đi kèm với một không gian làm việc riêng biệt.',2,  'A126', '1 giường đôi, 1 giường đơn', 2, 'Tầm nhìn ra Thành phố'),
(19, 5, 'Phòng thiết kế chuyên nghiệp dành cho những khách hàng công tác, đi kèm với một không gian làm việc riêng biệt.',2,   'A125', '1 giường đôi, 1 giường đơn', 1, 'Tầm nhìn ra Thành phố'),

-- Phòng loại 6 (room_category_id = 6)
(20, 6, 'Phòng thiết kế chuyên nghiệp dành cho những khách hàng công tác, đi kèm với một không gian làm việc riêng biệt.',2,   'A119', '1 giường đôi', 3, 'Tầm nhìn ra Thành phố'),
(21, 6, 'Phòng thiết kế chuyên nghiệp dành cho những khách hàng công tác, đi kèm với một không gian làm việc riêng biệt.',2,   'A120', '1 giường đôi', 4, 'Tầm nhìn ra Biển'),
(22, 6, 'Phòng thiết kế chuyên nghiệp dành cho những khách hàng công tác, đi kèm với một không gian làm việc riêng biệt.',2,  'A121', '1 giường đôi', 5, 'Tầm nhìn ra Thành phố'),
(22, 6, 'Phòng thiết kế chuyên nghiệp dành cho những khách hàng công tác, đi kèm với một không gian làm việc riêng biệt.',2,  'A123', '1 giường đôi', 5, 'Tầm nhìn ra Thành phố'),
(22, 6, 'Phòng thiết kế chuyên nghiệp dành cho những khách hàng công tác, đi kèm với một không gian làm việc riêng biệt.',2,  'A124', '1 giường đôi', 5, 'Tầm nhìn ra Thành phố');




INSERT INTO customer_cancellation_reasons (reason_description)
VALUES
    ('Khách hàng tìm được giá tốt hơn'),
    ('Khách hàng thay đổi ý định'),
    ('Khách hàng có khoản chi phí phát sinh ngoài dự kiến'),
    ('Khách hàng không hài lòng với khách sạn'),
    ('Khác');

INSERT INTO bank_list (bank_name) VALUES
    ('Ngân hàng Nhà nước Việt Nam'),
    ('Ngân hàng Công thương Việt Nam'),
    ('Ngân hàng Nông nghiệp và Phát triển Nông thôn'),
    ('Ngân hàng Quân đội'),
    ('Ngân hàng Thương mại Cổ phần Á Châu (ACB)'),
    ('Ngân hàng Đầu tư và Phát triển Việt Nam (BIDV)'),
    ('Ngân hàng TMCP Ngoại thương Việt Nam (Vietcombank)'),
    ('Ngân hàng TMCP Kỹ thương Việt Nam (Techcombank)'),
    ('Ngân hàng TMCP Sài Gòn Thương Tín (Sacombank)'),
    ('Ngân hàng TMCP Đầu tư và Phát triển Việt Nam (VIDBANK)');


INSERT INTO refund_account (user_id, bank_id, account_name, account_number) VALUES
    (6, 1, 'HieuLBM', '1234567890'),
    (6, 2, 'HieuLBM', '9876543210'),
    (6, 3, 'Bob Jones Account', '5555555555');
    

INSERT INTO payment_type (payment_name) VALUES
('Trực tuyến'),
('Trực tiếp');


INSERT INTO transactions_type (transactions_type_name) VALUES
('Đặt cọc'),
('Thanh toán hết'),
('Trả phòng sớm'),
('Huỷ phòng');

