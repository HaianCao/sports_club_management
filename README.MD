# 🏆 Hệ thống Quản lý Câu lạc bộ Thể thao

Ứng dụng Java Swing với Hibernate ORM để quản lý thành viên, môn tập, lịch tập và điểm danh cho câu lạc bộ thể thao.

## 📋 Yêu cầu Hệ thống

- **Java**: 8 trở lên
- **Maven**: 3.6+
- **MySQL**: 5.7+ hoặc 8.0+
- **Hệ điều hành**: Windows, macOS, Linux

## 📦 Cài đặt Maven

Nếu chưa có Maven trên hệ thống:

1. **Tải Maven**: [Apache Maven Download](https://maven.apache.org/download.cgi)
2. **Hướng dẫn cài đặt chi tiết**: [Video Tutorial](https://www.youtube.com/watch?v=x-VtjfGWc94)
3. **Kiểm tra cài đặt**:
   ```bash
   mvn --version
   ```

## 🛠️ Triển khai Dự án

### 1. Cấu hình Kết nối Database

Chỉnh sửa file `src/main/resources/hibernate.cfg.xml`:

```xml
<property name="hibernate.connection.url">jdbc:mysql://localhost:3306/sport_club_db</property>
<property name="hibernate.connection.username">root</property>
<property name="hibernate.connection.password">your_password</property>
```

### 2. Cài đặt Dependencies

```bash
# Clone project
git clone <repository-url>
cd sports_club_management

# Tải dependencies
mvn clean install
```

## 🚀 Chạy Ứng dụng

```bash
# Chạy giao diện đồ họa
mvn compile exec:java@gui
```

## 🔧 Cấu hình Nâng cao

### Thay đổi Timezone

Mặc định: `Asia/Ho_Chi_Minh` (GMT+7)

```xml
<property name="hibernate.connection.url">
    jdbc:mysql://localhost:3306/sport_club_db?serverTimezone=Asia/Ho_Chi_Minh
</property>
```

### Logging Level

```xml
<property name="hibernate.show_sql">false</property>
<property name="hibernate.format_sql">true</property>
```

## 🎯 Tính năng Chính

- **Quản lý Thành viên**: CRUD operations với filtering
- **Quản lý Môn tập**: Thêm/sửa/xóa môn thể thao
- **Quản lý Lịch tập**: Tạo và quản lý thời gian biểu
- **Điểm danh**: Ghi nhận tham gia theo buổi học
- **Xuất CSV**: Export dữ liệu thành file CSV

## 🔄 Chuyển đổi sang SQL Server

### Yêu cầu

- **SQL Server**: 2017+ (Express/Developer/Standard)
- **SQL Server Management Studio** (SSMS) - khuyến nghị

### 1. Cập nhật Maven Dependencies

Thay thế trong `pom.xml`:

```xml
<!-- Xóa MySQL dependency -->
<!--
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
-->

<!-- Thêm SQL Server dependency -->
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>12.4.2.jre8</version>
</dependency>
```

### 2. Cập nhật Hibernate Configuration

Sửa file `src/main/resources/hibernate.cfg.xml`:

```xml
<!-- Thay đổi driver và dialect -->
<property name="hibernate.connection.driver_class">com.microsoft.sqlserver.jdbc.SQLServerDriver</property>
<property name="hibernate.connection.url">jdbc:sqlserver://localhost:1433;databaseName=sport_club_db;trustServerCertificate=true</property>
<property name="hibernate.connection.username">sa</property>
<property name="hibernate.connection.password">your_password</property>
<property name="hibernate.dialect">org.hibernate.dialect.SQLServerDialect</property>
```

### 3. Chạy ứng dụng

```bash
# Tải dependencies mới
mvn clean install

# Chạy ứng dụng
mvn compile exec:java@gui
```

### ✅ Lưu ý quan trọng

- **Code không cần sửa**: Entity models, CRUD operations, UI components
- **Hibernate tự động**: Tạo tables và map data types phù hợp với SQL Server
- **Performance**: SQL Server thường nhanh hơn MySQL cho dữ liệu lớn
- **Backup**: Sử dụng SQL Server backup tools cho production

---

**Developer**: HaianCao  
**Version**: 1.0-SNAPSHOT
