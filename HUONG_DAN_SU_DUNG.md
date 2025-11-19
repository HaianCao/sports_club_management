# 🏆 HỆ THỐNG QUẢN LÝ CÂU LẠC BỘ THỂ THAO

## 📋 Tổng quan
Đây là hệ thống quản lý câu lạc bộ thể thao được phát triển bằng Java với giao diện đồ họa (GUI) sử dụng Swing, cơ sở dữ liệu MySQL và Hibernate ORM. Hệ thống hỗ trợ múi giờ GMT+7 (Việt Nam).

## ⚙️ Yêu cầu hệ thống
- **Java**: JDK 8 trở lên
- **Maven**: 3.6 trở lên
- **MySQL**: 5.7 trở lên
- **RAM**: Tối thiểu 2GB
- **Hệ điều hành**: Windows/Linux/macOS

## 🚀 Hướng dẫn cài đặt

### 1. Cài đặt MySQL
```sql
-- Tạo database (tự động tạo khi chạy ứng dụng)
-- Đảm bảo MySQL server đang chạy trên localhost:3306
-- User: root, Password: (để trống)
```

### 2. Compile dự án
```bash
mvn clean compile
```

### 3. Chạy ứng dụng

#### Chạy từ file batch (Windows):
```bash
./run.bat
```

#### Chạy trực tiếp:
```bash
# Chạy console tests
mvn exec:java

# Chạy GUI
java -cp target/classes com.sportclub.GuiLauncher
```

## 🖥️ Giao diện người dùng

### 📱 Trang chủ
- **Tổng quan**: Hiển thị menu chính với các chức năng
- **Quick Access**: Truy cập nhanh đến các module quản lý
- **Thông tin**: Hiển thị timezone và thời gian hiện tại

### 👥 Quản lý Thành viên
**Chức năng:**
- ➕ Thêm thành viên mới
- ✏️ Cập nhật thông tin thành viên
- 🗑️ Xóa mềm thành viên (soft delete)
- 🔍 Tìm kiếm và lọc thành viên

**Thông tin thành viên:**
- Họ tên
- Số điện thoại
- Tài khoản đăng nhập
- Mật khẩu
- Giới tính (Male/Female/Other)
- Vai trò (Root/Admin/Manager/User)

### 🏃 Quản lý Môn tập
**Chức năng:**
- ➕ Thêm môn tập mới
- ✏️ Cập nhật mô tả môn tập
- 🗑️ Xóa mềm môn tập
- 📋 Xem danh sách môn tập

**Thông tin môn tập:**
- Tên môn tập
- Mô tả chi tiết
- Trạng thái (Hoạt động/Đã xóa)

### 📅 Quản lý Lịch tập
**Chức năng:**
- ➕ Tạo lịch tập mới
- ⏰ Thiết lập thời gian bắt đầu/kết thúc
- 🌏 Hỗ trợ múi giờ GMT+7
- 📊 Xem danh sách lịch tập

**Định dạng thời gian:**
- Ngày: `yyyy-MM-dd` (VD: 2025-01-01)
- Giờ: `HH:mm:ss` (VD: 14:30:00)

### ✅ Điểm danh
**Chức năng:**
- ✏️ Ghi nhận sự tham gia
- 💬 Thêm ghi chú
- 👤 Chọn người quản lý
- 📈 Theo dõi tỷ lệ tham gia

**Quy trình điểm danh:**
1. Chọn thành viên
2. Chọn môn tập
3. Chọn lịch tập
4. Đánh dấu tham gia/vắng mặt
5. Thêm ghi chú (nếu có)
6. Lưu điểm danh

### 📄 Báo cáo & In file
**Các loại báo cáo:**
- 👥 **Báo cáo thành viên**: Danh sách, thống kê thành viên
- 🏃 **Báo cáo môn tập**: Các môn tập và mô tả
- ✅ **Báo cáo điểm danh**: Tỷ lệ tham gia, ghi chú
- 📊 **Thống kê hoạt động**: Tổng quan hệ thống

**Chức năng xuất:**
- 🖨️ **In trực tiếp**: In báo cáo ra máy in
- 💾 **Xuất file**: Lưu báo cáo thành file .txt
- 📋 **Xem chi tiết**: Hiển thị báo cáo đầy đủ

## 🔧 Cấu hình hệ thống

### 🕐 Múi giờ
Hệ thống được cấu hình sử dụng múi giờ GMT+7 (Asia/Ho_Chi_Minh):
```java
TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
```

### 🗄️ Cơ sở dữ liệu
```properties
URL: jdbc:mysql://localhost:3306/sport_club_db
Username: root
Password: (empty)
Timezone: Asia/Ho_Chi_Minh
Character Set: UTF-8
```

### 👑 Phân quyền
- **Root (0)**: Toàn quyền hệ thống
- **Admin (1)**: Quản lý toàn bộ trừ Root
- **Manager (2)**: Quản lý trong phạm vi được giao
- **User (3)**: Chỉ xem thông tin

## 📊 Dữ liệu mẫu

### 🗃️ Cấu trúc bảng
```sql
Users (id, name, phone, account, passwd, gender, role, is_deleted)
Subjects (id, name, description, is_deleted)
Timelines (time_id, start, end, is_deleted)
Joins (u_id, t_id, subject_id, participated, comment, is_deleted, manage_id)
```

### 👤 Tài khoản Root mặc định
- **Tài khoản**: root
- **Mật khẩu**: admin
- **Tự động tạo** khi chạy lần đầu

## 🛠️ Troubleshooting

### ❌ Lỗi thường gặp

**1. Lỗi kết nối MySQL:**
```
Solution: Kiểm tra MySQL server đang chạy trên localhost:3306
```

**2. Lỗi compile:**
```bash
mvn clean compile
```

**3. Lỗi dependencies:**
```bash
mvn dependency:resolve
```

**4. Lỗi GUI không hiển thị:**
```bash
# Kiểm tra Java GUI support
java -version
# Chạy lại
java -cp target/classes com.sportclub.GuiLauncher
```

### 📝 Log files
- **Hibernate logs**: Hiển thị SQL queries
- **Application logs**: Console output
- **Error logs**: Exception stack traces

## 🔄 Workflow sử dụng

### Quy trình quản lý thành viên:
1. **Đăng ký** → Thêm thành viên mới
2. **Cập nhật** → Sửa thông tin khi cần
3. **Phân quyền** → Gán role phù hợp
4. **Deactive** → Soft delete khi nghỉ

### Quy trình tổ chức hoạt động:
1. **Tạo môn tập** → Định nghĩa hoạt động
2. **Lập lịch** → Thiết lập thời gian
3. **Đăng ký** → Thành viên tham gia
4. **Điểm danh** → Ghi nhận sự tham gia
5. **Báo cáo** → Thống kê kết quả

## 📞 Hỗ trợ

### 🔗 Liên kết hữu ích
- **Java Documentation**: [Oracle Java Docs](https://docs.oracle.com/javase/8/)
- **Maven Guide**: [Maven.apache.org](https://maven.apache.org/guides/)
- **Hibernate Documentation**: [Hibernate.org](https://hibernate.org/orm/documentation/)

### 📧 Liên hệ
- **Developer**: HaianCao
- **Repository**: sports_club_management
- **Platform**: GitHub

---

> **Lưu ý**: Hệ thống này được thiết kế cho mục đích học tập và demo. 
> Để sử dụng trong môi trường production, cần bổ sung thêm security, validation và error handling.