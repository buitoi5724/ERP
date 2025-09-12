-- Bảng CUSTOMERS
CREATE TABLE CUSTOMERS (
    CUSTOMER_ID INT PRIMARY KEY IDENTITY(1,1),
    COMPANY_NAME NVARCHAR(40) NOT NULL,
    CONTACT_NAME NVARCHAR(30),
    CONTACT_TITLE NVARCHAR(30),
    ADDRESS NVARCHAR(60),
    CITY NVARCHAR(15),
    COUNTRY NVARCHAR(15),
    PHONE NVARCHAR(24),
    FAX NVARCHAR(24)
);

-- Bảng CATEGORIES
CREATE TABLE CATEGORIES (
    CATEGORY_ID INT PRIMARY KEY IDENTITY(1,1),
    CATEGORY_NAME NVARCHAR(15) NOT NULL,
    DESCRIPTION NVARCHAR(MAX),
    PICTURE IMAGE
);

-- Bảng EMPLOYEES
CREATE TABLE EMPLOYEES (
    EMPLOYEE_ID INT PRIMARY KEY IDENTITY(1,1),
    LAST_NAME NVARCHAR(20) NOT NULL,
    FIRST_NAME NVARCHAR(10) NOT NULL,
    TITLE NVARCHAR(30),
    TITLE_OF_COURTESY NVARCHAR(25),
    BIRTH_DATE DATETIME,
    HIRE_DATE DATETIME,
    ADDRESS NVARCHAR(60),
    CITY NVARCHAR(15),
    REGION NVARCHAR(15),
    POSTAL_CODE NVARCHAR(10),
    COUNTRY NVARCHAR(15),
    HOME_PHONE NVARCHAR(24),
    EXTENSION NVARCHAR(4),
    PHOTO IMAGE,
    NOTES NVARCHAR(MAX),
    REPORTS_TO INT,
    PHOTO_PATH NVARCHAR(255),
    FOREIGN KEY (REPORTS_TO) REFERENCES EMPLOYEES(EMPLOYEE_ID)
);

-- Bảng SUPPLIERS
CREATE TABLE SUPPLIERS (
    SUPPLIER_ID INT PRIMARY KEY IDENTITY(1,1),
    COMPANY_NAME NVARCHAR(40) NOT NULL,
    CONTACT_NAME NVARCHAR(30),
    CONTACT_TITLE NVARCHAR(30),
    ADDRESS NVARCHAR(60),
    CITY NVARCHAR(15),
    REGION NVARCHAR(15),
    POSTAL_CODE NVARCHAR(10),
    COUNTRY NVARCHAR(15),
    PHONE NVARCHAR(24),
    FAX NVARCHAR(24),
    HOME_PAGE NVARCHAR(MAX)
);

-- Bảng SHIPPERS
CREATE TABLE SHIPPERS (
    SHIPPER_ID INT PRIMARY KEY IDENTITY(1,1),
    COMPANY_NAME NVARCHAR(40) NOT NULL,
    PHONE NVARCHAR(24)
);

-- Bảng PRODUCTS
CREATE TABLE PRODUCTS (
    PRODUCT_ID INT PRIMARY KEY IDENTITY(1,1),
    PRODUCT_NAME NVARCHAR(40) NOT NULL,
    SUPPLIER_ID INT,
    CATEGORY_ID INT,
    QUANTITY_PER_UNIT NVARCHAR(20),
    UNIT_PRICE MONEY,
    UNITS_IN_STOCK SMALLINT,
    UNITS_ON_ORDER SMALLINT,
    REORDER_LEVEL SMALLINT,
    DISCONTINUED BIT NOT NULL,
    FOREIGN KEY (SUPPLIER_ID) REFERENCES SUPPLIERS(SUPPLIER_ID),
    FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORIES(CATEGORY_ID)
);

-- Bảng ORDERS
CREATE TABLE ORDERS (
    ORDER_ID INT PRIMARY KEY IDENTITY(1,1),
    CUSTOMER_ID INT,  -- chỉnh lại INT cho khớp với CUSTOMERS
    EMPLOYEE_ID INT,
    ORDER_DATE DATETIME,
    REQUIRED_DATE DATETIME,
    SHIPPED_DATE DATETIME,
    SHIP_VIA INT,
    FREIGHT MONEY,
    SHIP_NAME NVARCHAR(40),
    SHIP_ADDRESS NVARCHAR(60),
    SHIP_CITY NVARCHAR(15),
    SHIP_REGION NVARCHAR(15),
    SHIP_POSTAL_CODE NVARCHAR(10),
    SHIP_COUNTRY NVARCHAR(15),
    FOREIGN KEY (CUSTOMER_ID) REFERENCES CUSTOMERS(CUSTOMER_ID),
    FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEES(EMPLOYEE_ID),
    FOREIGN KEY (SHIP_VIA) REFERENCES SHIPPERS(SHIPPER_ID)
);

-- Bảng ORDER_DETAILS
CREATE TABLE ORDER_DETAILS (
    ORDER_ID INT NOT NULL,
    PRODUCT_ID INT NOT NULL,
    UNIT_PRICE MONEY NOT NULL,
    QUANTITY SMALLINT NOT NULL,
    DISCOUNT REAL NOT NULL,
    PRIMARY KEY (ORDER_ID, PRODUCT_ID),
    FOREIGN KEY (ORDER_ID) REFERENCES ORDERS(ORDER_ID),
    FOREIGN KEY (PRODUCT_ID) REFERENCES PRODUCTS(PRODUCT_ID)
);
---------
------------------------------------------------------------
-- CUSTOMERS: Khách hàng
------------------------------------------------------------
INSERT INTO CUSTOMERS (COMPANY_NAME, CONTACT_NAME, CONTACT_TITLE, ADDRESS, CITY, COUNTRY, PHONE, FAX)
VALUES
(N'Công ty Thực phẩm An Phát', N'Nguyễn Văn A', N'Giám đốc', N'12 Lý Thường Kiệt', N'Hà Nội', N'Việt Nam', '024-1111111', '024-2222222'),
(N'Cửa hàng Tạp hóa Bình Minh', N'Trần Thị B', N'Chủ cửa hàng', N'34 Nguyễn Trãi', N'Hà Nội', N'Việt Nam', '024-3333333', '024-4444444'),
(N'Nhà hàng Quê Hương', N'Lê Văn C', N'Chủ nhà hàng', N'56 Hai Bà Trưng', N'Hà Nội', N'Việt Nam', '024-5555555', NULL),
(N'Công ty Thủy sản Đại Dương', N'Đặng Văn F', N'Giám đốc', N'78 Lê Lợi', N'Hải Phòng', N'Việt Nam', '0225-8888888', NULL),
(N'Nhà hàng Sen Tây Hồ', N'Ngô Thị G', N'Chủ nhà hàng', N'614 Lạc Long Quân', N'Hà Nội', N'Việt Nam', '024-9999999', NULL),
(N'Công ty Thép Việt', N'Đỗ Văn H', N'Giám đốc', N'12 Điện Biên Phủ', N'Hải Phòng', N'Việt Nam', '0225-1234567', NULL),
(N'Siêu thị BigMart', N'Nguyễn Thị I', N'Quản lý', N'88 Phạm Văn Đồng', N'Hà Nội', N'Việt Nam', '024-88889999', NULL),
(N'Nhà hàng Hải Sản Biển Đông', N'Phan Văn J', N'Chủ nhà hàng', N'45 Hoàng Sa', N'Đà Nẵng', N'Việt Nam', '0236-1111222', NULL);

------------------------------------------------------------
-- CATEGORIES: Loại sản phẩm
------------------------------------------------------------
INSERT INTO CATEGORIES (CATEGORY_NAME, DESCRIPTION)
VALUES
(N'Đồ uống', N'Nước ngọt, bia, trà, cà phê'),
(N'Gia vị', N'Nước chấm, nước mắm, gia vị nấu ăn');

------------------------------------------------------------
-- EMPLOYEES: Nhân viên
------------------------------------------------------------
INSERT INTO EMPLOYEES (LAST_NAME, FIRST_NAME, TITLE, TITLE_OF_COURTESY, BIRTH_DATE, HIRE_DATE, ADDRESS, CITY, COUNTRY, HOME_PHONE)
VALUES
(N'Nguyễn', N'Thảo', N'Nhân viên bán hàng', N'Cô', '1990-05-12', '2020-03-01', N'123 Hoàng Hoa Thám', N'Hà Nội', N'Việt Nam', '0981111111'),
(N'Trần', N'Minh', N'Quản lý bán hàng', N'Ông', '1985-08-20', '2015-06-15', N'45 Nguyễn Văn Cừ', N'Hà Nội', N'Việt Nam', '0982222222'),
(N'Lưu', N'Lan', N'Nhân viên bán hàng', N'Cô', '1993-07-10', '2022-09-01', N'56 Tây Sơn', N'Hà Nội', N'Việt Nam', '0961234567'),
(N'Hoàng', N'Nam', N'Nhân viên bán hàng', N'Ông', '1991-01-05', '2021-03-10', N'90 Bạch Đằng', N'Đà Nẵng', N'Việt Nam', '0977654321');

------------------------------------------------------------
-- SUPPLIERS: Nhà cung cấp
------------------------------------------------------------
INSERT INTO SUPPLIERS (COMPANY_NAME, CONTACT_NAME, CONTACT_TITLE, ADDRESS, CITY, COUNTRY, PHONE)
VALUES
(N'Công ty Nước giải khát Việt Nam', N'Phạm Văn D', N'Giám đốc mua hàng', N'89 Trần Hưng Đạo', N'Hà Nội', N'Việt Nam', '024-6666666'),
(N'Công ty Gia vị Hương Việt', N'Hoàng Thị E', N'Trưởng phòng kinh doanh', N'23 Lạc Long Quân', N'Hà Nội', N'Việt Nam', '024-7777777');

------------------------------------------------------------
-- SHIPPERS: Đơn vị giao hàng
------------------------------------------------------------
INSERT INTO SHIPPERS (COMPANY_NAME, PHONE)
VALUES
(N'Giao hàng nhanh', '1900-1234'),
(N'VNPost Express', '1900-4567'),
(N'Giao hàng tiết kiệm', '1900-7890');

------------------------------------------------------------
-- PRODUCTS: Sản phẩm
------------------------------------------------------------
INSERT INTO PRODUCTS (PRODUCT_NAME, SUPPLIER_ID, CATEGORY_ID, QUANTITY_PER_UNIT, UNIT_PRICE, UNITS_IN_STOCK, UNITS_ON_ORDER, REORDER_LEVEL, DISCONTINUED)
VALUES
(N'Trà xanh', 1, 1, N'20 gói/hộp', 20000, 100, 0, 10, 0),
(N'Nước cam ép', 1, 1, N'6 chai/thùng', 30000, 50, 0, 5, 0),
(N'Nước mắm Nam Ngư', 2, 2, N'12 chai/thùng', 40000, 80, 0, 8, 0),
(N'Cà phê hòa tan G7', 1, 1, N'20 gói/hộp', 50000, 200, 0, 20, 0),
(N'Bia Hà Nội', 1, 1, N'24 lon/thùng', 250000, 150, 0, 15, 0),
(N'Nước tương Maggi', 2, 2, N'12 chai/thùng', 35000, 100, 0, 10, 0);

------------------------------------------------------------
-- ORDERS: Đơn hàng
------------------------------------------------------------
INSERT INTO ORDERS (CUSTOMER_ID, EMPLOYEE_ID, ORDER_DATE, REQUIRED_DATE, SHIPPED_DATE, SHIP_VIA, FREIGHT, SHIP_NAME, SHIP_ADDRESS, SHIP_CITY, SHIP_COUNTRY)
VALUES
(1, 1, '2025-09-09', '2025-09-12', '2025-09-10', 1, 50000, N'Công ty Thực phẩm An Phát', N'12 Lý Thường Kiệt', N'Hà Nội', N'Việt Nam'),
(2, 2, '2025-09-09', '2025-09-15', '2025-09-11', 2, 30000, N'Cửa hàng Tạp hóa Bình Minh', N'34 Nguyễn Trãi', N'Hà Nội', N'Việt Nam'),
(3, 1, '2025-09-08', '2025-09-13', '2025-09-09', 3, 70000, N'Nhà hàng Quê Hương', N'56 Hai Bà Trưng', N'Hà Nội', N'Việt Nam'),
(4, 3, '2025-09-10', '2025-09-15', '2025-09-12', 1, 90000, N'Công ty Thủy sản Đại Dương', N'78 Lê Lợi', N'Hải Phòng', N'Việt Nam'),
(5, 4, '2025-09-11', '2025-09-18', '2025-09-13', 2, 60000, N'Nhà hàng Sen Tây Hồ', N'614 Lạc Long Quân', N'Hà Nội', N'Việt Nam'),
(6, 2, '2025-09-11', '2025-09-20', '2025-09-14', 3, 45000, N'Công ty Thép Việt', N'12 Điện Biên Phủ', N'Hải Phòng', N'Việt Nam');

------------------------------------------------------------
-- ORDER_DETAILS: Chi tiết đơn hàng
------------------------------------------------------------
INSERT INTO ORDER_DETAILS (ORDER_ID, PRODUCT_ID, UNIT_PRICE, QUANTITY, DISCOUNT)
VALUES
(1, 1, 20000, 5, 0),
(1, 2, 30000, 3, 0.05),
(2, 3, 40000, 2, 0),
(3, 1, 20000, 10, 0.1),
(4, 4, 50000, 4, 0),
(4, 5, 250000, 1, 0),
(5, 6, 35000, 6, 0.05),
(6, 2, 30000, 8, 0),
(6, 3, 40000, 5, 0.1);

SELECT TOP 1
    S.COMPANY_NAME AS DonViGiaoHang,
    COUNT(O.ORDER_ID) AS SoDonGiao
FROM ORDERS O
JOIN SHIPPERS S ON O.SHIP_VIA = S.SHIPPER_ID
GROUP BY S.COMPANY_NAME
ORDER BY SoDonGiao ASC;


SELECT * FROM CUSTOMERS;
SELECT * FROM EMPLOYEES;
SELECT * FROM SUPPLIERS;
SELECT * FROM SHIPPERS;
SELECT * FROM CATEGORIES;
SELECT * FROM PRODUCTS;
SELECT * FROM ORDERS;
SELECT * FROM ORDER_DETAILS;
/// lấy hóa đơn 
SELECT 
    O.ORDER_ID,
    C.COMPANY_NAME AS TenKhachHang,
    E.LAST_NAME + ' ' + E.FIRST_NAME AS NhanVienBanHang,
    S.COMPANY_NAME AS DonViGiaoHang,
    O.ORDER_DATE
FROM ORDERS O
JOIN CUSTOMERS C ON O.CUSTOMER_ID = C.CUSTOMER_ID
JOIN EMPLOYEES E ON O.EMPLOYEE_ID = E.EMPLOYEE_ID
JOIN SHIPPERS S ON O.SHIP_VIA = S.SHIPPER_ID
WHERE CAST(O.ORDER_DATE AS DATE) = '2025-09-09';
/// 
SELECT TOP 1
    C.COMPANY_NAME AS TenKhachHang,
    COUNT(O.ORDER_ID) AS SoLanMuaHang
FROM ORDERS O
JOIN CUSTOMERS C ON O.CUSTOMER_ID = C.CUSTOMER_ID
GROUP BY C.COMPANY_NAME
ORDER BY COUNT(O.ORDER_ID) DESC;
////
SELECT TOP 1
    C.COMPANY_NAME AS TenKhachHang,
    SUM(OD.UNIT_PRICE * OD.QUANTITY * (1 - OD.DISCOUNT)) AS TongTien
FROM ORDERS O
JOIN CUSTOMERS C ON O.CUSTOMER_ID = C.CUSTOMER_ID
JOIN ORDER_DETAILS OD ON O.ORDER_ID = OD.ORDER_ID
GROUP BY C.COMPANY_NAME
ORDER BY TongTien DESC;
///
SELECT TOP 1
    E.LAST_NAME + ' ' + E.FIRST_NAME AS NhanVien,
    SUM(OD.UNIT_PRICE * OD.QUANTITY * (1 - OD.DISCOUNT)) AS TongTienBan
FROM ORDERS O
JOIN EMPLOYEES E ON O.EMPLOYEE_ID = E.EMPLOYEE_ID
JOIN ORDER_DETAILS OD ON O.ORDER_ID = OD.ORDER_ID
GROUP BY E.LAST_NAME, E.FIRST_NAME
ORDER BY TongTienBan DESC;
////
SELECT TOP 1
    S.COMPANY_NAME AS DonViGiaoHang,
    COUNT(O.ORDER_ID) AS SoDonGiao
FROM ORDERS O
JOIN SHIPPERS S ON O.SHIP_VIA = S.SHIPPER_ID
GROUP BY S.COMPANY_NAME
ORDER BY SoDonGiao ASC;
////