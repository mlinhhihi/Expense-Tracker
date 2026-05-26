# 📱 MyWallet - Ứng dụng Quản lý Chi tiêu Cá nhân

Báo cáo tiến độ, cấu trúc chức năng và tài liệu hướng dẫn kỹ thuật của ứng dụng **MyWallet** thuộc học phần **Lập trình thiết bị di động (65.CNTT-2)**.

---

## 📚 Mục lục
- [🚀 Giới thiệu & Công nghệ sử dụng](#-giới-thiệu--công-nghệ-sử-dụng)
- [✨ Các Chức năng Chính của Ứng dụng](#-các-chức-năng-chính-của-ứng-dụng)
  - [1. Chức năng Đăng nhập & Tài khoản](#1-chức-năng-đăng-nhập--tài-khoản)
  - [2. Chức năng Đăng ký Tài khoản](#2-chức-năng-đăng-ký-tài-khoản)
  - [3. Chức năng Thêm Giao dịch](#3-chức-năng-thêm-giao-dịch)
  - [4. Chức năng Trang chủ & Quản lý danh sách](#4-chức-năng-trang-chủ--quản-lý-danh-sách)
  - [5. Chức năng Biểu đồ & Phân tích (Thống kê)](#5-chức-năng-biểu-đồ--phân-tích-thống-kê)
- [🛠 Cấu trúc Database (Firebase Cloud Firestore)](#-cấu-trúc-database-firebase-cloud-firestore)
- [📐 Thiết kế Giao diện & Trải nghiệm Người dùng (UI/UX)](#-thiết-kế-giao-diện--trải-nghiệm-người-dùng-uiux)

---

## 🚀 Giới thiệu & Công nghệ sử dụng
**MyWallet** là một giải pháp di động toàn diện giúp người dùng cá nhân dễ dàng theo dõi, ghi chép, phân loại và phân tích các khoản thu nhập (Income) cũng như chi tiêu (Expense) hàng ngày một cách trực quan, khoa học.

### 🛠 Hệ sinh thái công nghệ & Thư viện tích hợp:
* **Ngôn ngữ lập trình cốt lõi:** Java (Android SDK)
* **Kiến trúc giao diện:** `ConstraintLayout` tối ưu hiệu năng phần cứng, hệ thống `Fragment` động linh hoạt chuyển đổi qua `BottomNavigationView`.
* **Cơ sở dữ liệu đám mây:** `Firebase Authentication` (Quản lý bảo mật phiên làm việc) & `Google Cloud Firestore` (Đồng bộ hóa dữ liệu thời gian thực).
* **Thư viện đồ họa:** `MPAndroidChart` (Xử lý kết xuất biểu đồ tròn PieChart động nâng cao).
* **Thành phần bổ trợ:** `CardView` bo góc, `FrameLayout` custom bọc `Spinner` chống tràn nhãn, `RecyclerView` tối ưu danh bạ giao dịch.

---

## ✨ Các Chức năng Chính của Ứng dụng

### 1. Chức năng Đăng nhập & Tài khoản
> 🔗 [Xem mã nguồn `LoginActivity.java`](https://github.com/mlinhhihi/Expense-Tracker/blob/main/MyWallet/app/src/main/java/thick2/truongthimylinh/mywallet/LoginActivity.java)

<p align="center">
  <img width="220" alt="Màn hình Đăng nhập" src="https://github.com/user-attachments/assets/ce707991-2c95-46ac-9785-3dfc410f498e" style="margin-right: 20px;" />
  <img width="220" alt="Màn hình Hồ sơ" src="https://github.com/user-attachments/assets/c1d5097c-d167-4658-a280-84c92542fef5" />
</p>

* **Xác thực hệ thống:** Người dùng đăng nhập bằng tài khoản email cá nhân thông qua cơ chế bảo mật nghiêm ngặt của Firebase.
* **Quản lý hồ sơ (Profile):** Hiển thị chi tiết thông tin cá nhân. Cho phép người dùng tùy biến chỉnh sửa thông tin (Họ tên, số điện thoại, ngày sinh...) ngoại trừ khóa chính là Email.
* **Trạng thái làm việc (Session):** Hệ thống ghim phiên đăng nhập lâu dài. Khi người dùng nhấn nút "Đăng xuất", ứng dụng lập tức xóa Token, hủy trạng thái kết nối dữ liệu an toàn và chuyển hướng về màn hình đăng nhập.

---

### 2. Chức năng Đăng ký Tài khoản


---

### 3. Chức năng Thêm Giao dịch
> 🔗 [Xem mã nguồn `AddFragment.java`](https://github.com/mlinhhihi/Expense-Tracker/blob/main/MyWallet/app/src/main/java/thick2/truongthimylinh/mywallet/AddFragment.java)

<p align="center">
  <img width="220" alt="Màn hình Thêm Giao dịch" src="https://github.com/user-attachments/assets/caf22be5-a092-4e61-a7d5-2b95da82952f" />
</p>

* **Phân loại dòng tiền:** Hỗ trợ tạo lập tức thì hai phân loại giao dịch cơ bản: **Thu nhập (INCOME)** và **Chi tiêu (EXPENSE)**.
* **Cấu trúc trường nhập liệu:** * Số tiền (Hỗ trợ định dạng số linh hoạt).
  * Danh mục chi tiêu trực quan (Ăn uống, Thuê nhà/Trọ, Mua sắm, Đi lại, Lương...).
  * Ghi chú chi tiết (Ví dụ: "Giày", "Tiền phòng tháng 5"...).
  * Ngày giờ thực hiện giao dịch (Tự động ghim thời gian thực hoặc tùy chỉnh).

---

### 4. Chức năng Trang chủ & Quản lý danh sách
> 🔗 [Xem mã nguồn `HomeFragment.java`](https://github.com/mlinhhihi/Expense-Tracker/blob/main/MyWallet/app/src/main/java/thick2/truongthimylinh/mywallet/HomeFragment.java)

<p align="center">
  <img width="220" alt="Màn hình Trang chủ" src="https://github.com/user-attachments/assets/3102cca5-7200-41ab-9cef-382664cdcb35" />
</p>

* **Tổng hợp dòng tiền trực quan:** Tính toán hiển thị tổng số tiền đã thu và chi ngay đầu màn hình theo thời gian thực.
* **Bộ lọc thời gian thông minh:** Phân tách dữ liệu linh hoạt theo từng "Tháng" cụ thể hoặc tổng quan "Cả năm" kết hợp điều kiện chọn loại "Năm" biến thiên.
* **Tìm kiếm nâng cao:** Hỗ trợ thanh tìm kiếm động siêu mượt, cho phép tra cứu nhanh lịch sử giao dịch dựa trên từ khóa: tên danh mục, mốc ngày cụ thể hoặc nội dung chữ ghi chú đính kèm.

---

### 5. Chức năng Biểu đồ & Phân tích (Thống kê)
> 🔗 [Xem mã nguồn `ChartFragment.java`](https://github.com/mlinhhihi/Expense-Tracker/blob/main/MyWallet/app/src/main/java/thick2/truongthimylinh/mywallet/ChartFragment.java)
<p align="center">
  <img width="220" alt="Màn hình Đăng nhập" src="https://github.com/user-attachments/assets/7bf514a5-c7ea-4c6a-bbfd-6a4db2514241" style="margin-right: 20px;" />
  <img width="220" alt="Màn hình Hồ sơ" src="https://github.com/user-attachments/assets/38a425f4-adad-48c0-83e6-a8f56e8bcc28" />
</p>
* **Đồ thị Sliding Tab phẳng:** Sử dụng cấu trúc `RadioGroup` bo góc thẩm mỹ, hỗ trợ chuyển đổi mượt mà giữa hai góc nhìn phân tích chuyên sâu:
  * **Tổng quan Thu - Chi:** Biểu diễn tỷ trọng tương quan giữa Tổng thu nhập (`INCOME`) và Tổng chi tiêu (`EXPENSE`) bằng sắc màu trực quan (Xanh lá - Đỏ).
  * **Phân tích Khoản Chi:** Bóc tách chuyên sâu tổng lượng tiền chi ra thành các lát bánh thành phần tương ứng với từng danh mục cụ thể (Ăn uống, Trọ, Mua sắm, Đi lại...).
* **Công nghệ tối ưu UI/UX Đồ thị:**
  * Toàn bộ nhãn tên danh mục trên mặt biểu đồ tròn được **ẩn đi một cách tinh tế** (`pieChart.setDrawEntryLabels(false)`) để loại bỏ tình trạng rối mắt, chèn ép chữ khi diện tích lát cắt quá hẹp.
  * Tên danh mục được gom tập trung xuống bảng chú thích màu sắc (`Legend`) xếp gọn gàng dưới đáy màn hình.
  * Các đường chỉ dẫn giá trị phần trăm (`Value Lines`) được **kéo dài biên độ** (`0.6f` và `0.5f`) giúp đẩy các con số phần trăm độc lập ra rìa ngoài biểu đồ, ngăn chặn hoàn toàn hiện tượng chồng chéo, dính chữ lên nhau trên màn hình thiết bị di động.

---

## 🛠 Cấu trúc Database (Firebase Cloud Firestore)

Hệ thống lưu trữ cơ sở dữ liệu NoSQL được tổ chức phân cấp mạch lạc, liên kết chặt chẽ qua mã định danh người dùng (`uid`):

### 📁 Collection: `transactions`
Mỗi một tài liệu (Document) đại diện cho một giao dịch chi tiết bao gồm các trường thuộc tính chuẩn hóa:
* `amount` (String/Number): Số tiền giao dịch (Ví dụ: `"89000"`).
* `category` (String): Danh mục chi tiêu (Ví dụ: `"Mua sắm"`, `"Trọ"`, `"Ăn uống"`).
* `note` (String): Nội dung ghi chú đính kèm (Ví dụ: `"Giày"`).
* `time` (Timestamp): Mốc thời gian thực hiện lưu trữ trên Firebase.
* `type` (String): Phân hệ dòng tiền (`"INCOME"` hoặc `"EXPENSE"`).
* `uid` (String): Chuỗi định danh duy nhất của tài khoản người dùng (`FirebaseAuth.getUid()`).

---

