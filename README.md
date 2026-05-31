# 📱 MyWallet - Ứng dụng Quản lý Chi tiêu Cá nhân

Báo cáo tiến độ, cấu trúc chức năng của ứng dụng **MyWallet** thuộc học phần **Lập trình thiết bị di động (65.CNTT-2)**.

---

## 📚 Mục lục
- [🚀 Giới thiệu & Công nghệ sử dụng](#-giới-thiệu--công-nghệ-sử-dụng)
- [✨ Các Chức năng Chính của Ứng dụng](#-các-chức-năng-chính-của-ứng-dụng)
  - [1. Chức năng Đăng ký Tài khoản](#1-chức-năng-đăng-ký-tài-khoản)
  - [2. Phân hệ Xác thực Tài khoản & Đồng bộ Hồ sơ Cá nhân](#2-phân-hệ-xác-thực-tài-khoản--đồng-bộ-hồ-sơ-cá-nhân)
  - [3. Chức năng Thêm Giao dịch & Quản lý Danh mục Linh hoạt](#3-chức-năng-thêm-giao-dịch--quản-lý-danh-mục-linh-hoạt)
  - [4. Chức năng Trang chủ & Quản lý Danh sách Giao dịch (CRUD Toàn diện)](#4-chức-năng-trang-chủ--quản-lý-danh-sách-giao-dịch-crud-toàn-diện)
  - [5. Chức năng Biểu đồ & Phân tích Tài chính Trực quan](#5-chức-năng-biểu-đồ--phân-tích-tài-chính-trực-quan)
- [🛠 Cấu trúc Database (Firebase Cloud Firestore)](#-cấu-trúc-database-firebase-cloud-firestore)

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

### 1. Chức năng Đăng ký Tài khoản
> 🔗 [Xem mã nguồn `RegisterActivity.java`](https://github.com/mlinhhihi/Expense-Tracker/blob/main/MyWallet/app/src/main/java/thick2/truongthimylinh/mywallet/RegisterActivity.java)

<p align="center">
  <img width="220" alt="Màn hình đăng ký tài khoản" src="https://github.com/user-attachments/assets/6afd5ec8-11e6-4081-bd9c-38b815b2d3fc"/>
</p>

---

### 2. Phân hệ Xác thực Tài khoản & Đồng bộ Hồ sơ Cá nhân
> 🔗 [Xem mã nguồn `LoginActivity.java`](https://github.com/mlinhhihi/Expense-Tracker/blob/main/MyWallet/app/src/main/java/thick2/truongthimylinh/mywallet/LoginActivity.java) | [Xem mã nguồn `ProfileFragment.java`](https://github.com/mlinhhihi/Expense-Tracker/blob/main/MyWallet/app/src/main/java/thick2/truongthimylinh/mywallet/ProfileFragment.java)

<p align="center">
  <img width="220" alt="Màn hình Đăng nhập" src="https://github.com/user-attachments/assets/abf877e5-96e9-4cfb-bac1-caf58998af46" style="margin-right: 20px;" />
  <img width="220" alt="Màn hình Hồ sơ" src="https://github.com/user-attachments/assets/ee168bdd-4863-4193-8e22-3a95e00a02d4" />
</p>

---

### 3. Chức năng Thêm Giao dịch & Quản lý Danh mục Linh hoạt
> 🔗 [Xem mã nguồn `AddFragment.java`](https://github.com/mlinhhihi/Expense-Tracker/blob/main/MyWallet/app/src/main/java/thick2/truongthimylinh/mywallet/AddFragment.java)


<p align="center">
  <img width="220" alt="Màn hình thêm giao dịch" src="https://github.com/user-attachments/assets/002f3323-def4-4a42-a178-a0214d7bbd55" style="margin-right: 20px;" />
  <img width="220" alt="chức năng thêm danh mục" src="https://github.com/user-attachments/assets/8cff79dd-a8c1-40a4-b001-ba472dfc5e79" />
</p>


---

### 4. Chức năng Trang chủ & Quản lý Danh sách Giao dịch (CRUD Toàn diện)
> 🔗 [Xem mã nguồn `HomeFragment.java`](https://github.com/mlinhhihi/Expense-Tracker/blob/main/MyWallet/app/src/main/java/thick2/truongthimylinh/mywallet/HomeFragment.java)

<p align="center">
  <img width="220" alt="Màn hình Trang chủ" src="https://github.com/user-attachments/assets/bd2584af-6f64-46db-ac72-1fbba70ff252" />
</p>

---

### 5. Chức năng Biểu đồ & Phân tích Tài chính Trực quan
> 🔗 [Xem mã nguồn `ChartFragment.java`](https://github.com/mlinhhihi/Expense-Tracker/blob/main/MyWallet/app/src/main/java/thick2/truongthimylinh/mywallet/ChartFragment.java)

<p align="center">
  <img width="220" alt="Biểu đồ thu chi" src="https://github.com/user-attachments/assets/7f3c7aad-a168-4de5-ba11-d5e9642d9ea0" style="margin-right: 20px;" />
  <img width="220" alt="Màn hình Hồ sơ" src="https://github.com/user-attachments/assets/7b098d7e-519c-4e88-a1c7-b2bd63911307" />
</p>

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
