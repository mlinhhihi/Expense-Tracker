# 📱 MyWallet - Ứng dụng Quản lý Chi tiêu Cá nhân

Báo cáo tiến độ và cấu trúc chức năng ứng dụng **MyWallet** thuộc học phần **Lập trình thiết bị di động (65.CNTT-2)**.

---

## 📚 Mục lục
- [🚀 Giới thiệu & Công nghệ sử dụng](#-giới-thiệu--công-nghệ-sử-dụng)
- [✨ Các Chức năng Chính của Ứng dụng](#-các-chức-năng-chính-của-ứng-dụng)
  - [1. Chức năng Đăng nhập & Tài khoản](#1-chức-năng-đăng-nhập--tài-khoản)
  - [2. Chức năng Thêm Giao dịch](#2-chức-năng-thêm-giao-dịch)
  - [3. Chức năng Trang chủ & Quản lý danh sách](#3-chức-năng-trang-chủ--quản-lý-danh-sách)
- [📝 Danh sách Bài tập & Thực hành Học phần](#-danh-sách-bài-tập--thực hành-học-phần)
  - [Bài tập Quản lý chi tiêu theo tuần](#bài-tập-quản-lý-chi-tiêu-theo-tuần)
  - [Nội dung THIGK2 (Câu 1, 2, 3)](#nội-dung-thigk2)
  - [Các bài thực hành thành phần (Bài 8 - Bài 13)](#các-bài-thực-hành-thành-phần)

---

## 🚀 Giới thiệu & Công nghệ sử dụng
**MyWallet** là ứng dụng giúp người dùng dễ dàng theo dõi, ghi chép và phân tích các khoản thu chi hàng ngày. 
* **Ngôn ngữ phát triển:** Java
* **Thành phần kiến trúc:** `Fragment` (Static/Dynamic), `RecyclerView`, `BottomNavigation`, `Custom ListView`.
* **Quản lý tài nguyên:** `Drawable Resource`, `Intent` truyền dữ liệu.

---

## ✨ Các Chức năng Chính của Ứng dụng

### 1. Chức năng Đăng nhập & Tài khoản
> 🔗 [Xem mã nguồn `LoginActivity.java`](https://github.com/mlinhhihi/Expense-Tracker/blob/main/MyWallet/app/src/main/java/thick2/truongthimylinh/mywallet/LoginActivity.java)

<p align="center">
  <img width="220" alt="Màn hình Đăng nhập" src="https://github.com/user-attachments/assets/ce707991-2c95-46ac-9785-3dfc410f498e" style="margin-right: 20px;" />
  <img width="220" alt="Màn hình Hồ sơ" src="https://github.com/user-attachments/assets/c1d5097c-d167-4658-a280-84c92542fef5" />
</p>

* **Đăng nhập hệ thống:** Người dùng nhập Email và Mật khẩu đã đăng ký để truy cập vào ứng dụng.
* **Quản lý hồ sơ cá nhân (Profile):** Sau khi đăng nhập, người dùng có thể kiểm tra và cập nhật các thông tin cá nhân (Họ tên, ngày sinh, số điện thoại,... ngoại trừ Email).
* **Bảo mật:** Ứng dụng yêu cầu trạng thái đăng nhập bắt buộc để sử dụng. Khi nhấn đăng xuất, hệ thống sẽ xóa phiên làm việc và quay về màn hình đăng nhập.

---

### 2. Chức năng Thêm Giao dịch
> 🔗 [Xem mã nguồn `AddFragment.java`](https://github.com/mlinhhihi/Expense-Tracker/blob/main/MyWallet/app/src/main/java/thick2/truongthimylinh/mywallet/AddFragment.java)

<p align="center">
  <img width="220" alt="Màn hình Thêm Giao dịch" src="https://github.com/user-attachments/assets/caf22be5-a092-4e61-a7d5-2b95da82952f" />
</p>

* Cho phép tạo mới các khoản **Thu** hoặc **Chi** một cách nhanh chóng.
* **Các trường thông tin hỗ trợ:** Danh mục (Ăn uống, mua sắm, lương...), Số tiền, Ghi chú chi tiết và Thời gian thực hiện.
* Dữ liệu sau khi lưu sẽ được đồng bộ trực tiếp phục vụ cho việc thống kê và hiển thị.

---

### 3. Chức năng Trang chủ & Quản lý danh sách
> 🔗 [Xem mã nguồn `HomeFragment.java`](https://github.com/mlinhhihi/Expense-Tracker/blob/main/MyWallet/app/src/main/java/thick2/truongthimylinh/mywallet/HomeFragment.java)

<p align="center">
  <img width="220" alt="Màn hình Trang chủ" src="https://github.com/user-attachments/assets/3102cca5-7200-41ab-9cef-382664cdcb35" />
</p>

* **Hiển thị trực quan:** Danh sách toàn bộ giao dịch được phân loại rõ ràng và bộ lọc thông minh dựa trên mốc thời gian (Tháng / Năm).
* **Tìm kiếm nâng cao:** Hỗ trợ người dùng tìm kiếm nhanh chóng các giao dịch cũ theo các từ khóa linh hoạt như: *ngày cụ thể trong tháng, nội dung ghi chú, hoặc danh mục thu chi*.

---

## 📝 Danh sách Bài tập & Thực hành Học phần

Dưới đây là các phần nội dung bài tập nền tảng được tích hợp và hoàn thiện trong quá trình xây dựng ứng dụng qua từng tuần học:

### Bài tập Quản lý chi tiêu theo tuần
* Triển khai nền tảng logic quản lý dòng tiền theo tiến độ tuần học.

### Nội dung THIGK2
* **Câu 1:** Thiết kế và tối ưu giao diện cơ bản.
* **Câu 2:** Xử lý logic nghiệp vụ và ràng buộc dữ liệu đầu vào.
* **Câu 3:** Kết nối cơ sở dữ liệu/Xử lý lưu trữ luồng dữ liệu chính.

### Các bài thực hành thành phần
* **Bài 8:** Tùy chỉnh ListView (`Custom ListView` áp dụng cho danh bạ/món ăn).
* **Bài 9:** Sử dụng `RecyclerView` tối ưu hiển thị danh sách đối tượng lớn.
* **Sử dụng Drawable Resource:** Thiết kế UI components, custom góc bo, gradient màu sắc cho button và background.
* **Bài 10:** Tương tác chuyển đổi giữa các Activity thông qua `Intent` đơn giản.
* **Bài 11 & 12:** Xây dựng bố cục linh hoạt với `Fragment tĩnh` và `Fragment động`.
* **Bài 13 & Bottom Navigation:** Xử lý chuyển đổi qua lại giữa các Fragment chính (Home, Add, Profile) mượt mà bằng thanh điều hướng phía dưới màn hình.
