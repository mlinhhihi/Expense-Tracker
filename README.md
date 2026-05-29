# 📱 MyWallet - Ứng dụng Quản lý Chi tiêu Cá nhân

Báo cáo tiến độ, cấu trúc chức năng và tài liệu hướng dẫn kỹ thuật của ứng dụng **MyWallet** thuộc học phần **Lập trình thiết bị di động (65.CNTT-2)**.

---

## 📚 Mục lục
- [🚀 Giới thiệu & Công nghệ sử dụng](#-giới-thiệu--công-nghệ-sử-dụng)
- [✨ Các Chức năng Chính của Ứng dụng](#-các-chức-năng-chính-của-ứng-dụng)
  - [1. Chức năng đăng ký tài khoản](#2-chức-năng-đăng-ký-tài-khoản)
  - [2. Chức năng Đăng nhập & Tài khoản](#1-chức-năng-đăng-nhập--tài-khoản)
  - [3. Chức năng Thêm Giao dịch](#3-chức-năng-thêm-giao-dịch)
  - [4. Chức năng Trang chủ & Quản lý danh sách](#4-chức-năng-trang-chủ--quản-lý-danh-sách)
  - [5. Chức năng Biểu đồ & Phân tích (Thống kê)](#5-chức-năng-biểu-đồ--phân-tích-thống-kê)
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
  <img width="220" alt="Màn hình đăng ký tài khoản" src="https://github.com/user-attachments/assets/54831966-e4e8-41ac-a430-d943cebfa3ff"/>
</p>


**Khởi tạo:** Cho phép người dùng mới nhanh chóng thiết lập tài khoản truy cập ứng dụng bằng cách cung cấp thông tin bảo mật gồm Email và Mật khẩu.
* **Cơ chế kiểm soát dữ liệu đầu vào (Validation):** Hệ thống tự động kiểm tra nghiêm ngặt, chặn các hành vi bỏ trống trường nhập liệu, đồng thời bắt buộc độ dài mật khẩu phải đạt từ 6 ký tự trở lên để đảm bảo quy chuẩn an toàn trước khi gửi yêu cầu lên máy chủ.
* **Xác thực trực tiếp qua Firebase:** Sử dụng API `createUserWithEmailAndPassword` để xử lý đăng ký bất đồng bộ. Ngay sau khi Firebase Authentication cấp tài khoản thành công, ứng dụng sẽ thông báo và tự động giải phóng màn hình (`finish()`) để đưa người dùng quay lại giao diện chính.

---

### 2. Chức năng Đăng nhập & Tài khoản
> 🔗 [Xem mã nguồn `LoginActivity.java`](https://github.com/mlinhhihi/Expense-Tracker/blob/main/MyWallet/app/src/main/java/thick2/truongthimylinh/mywallet/LoginActivity.java)

<p align="center">
  <img width="220" alt="Màn hình Đăng nhập" src="https://github.com/user-attachments/assets/ce707991-2c95-46ac-9785-3dfc410f498e" style="margin-right: 20px;" />
  <img width="220" alt="Màn hình Hồ sơ" src="https://github.com/user-attachments/assets/c1d5097c-d167-4658-a280-84c92542fef5" />
</p>

* **Xác thực phiên làm việc (Authentication):** Người dùng nhập Email và Mật khẩu tại `LoginActivity`. Hệ thống gọi API `signInWithEmailAndPassword` để xác thực bất đồng bộ với hệ thống đám mây Firebase. Khi thành công, một mã định danh duy nhất (`uid`) sẽ được cấp phát để ghim giữ phiên làm việc.
* **Liên kết dữ liệu & Tự động hiển thị (Firestore Sync):** Khi người dùng chuyển sang `ProfileFragment`, ứng dụng sẽ tự động lấy mã `uid` hiện tại của phiên đăng nhập để làm "khóa chính", truy vấn trực tiếp vào Collection `users` trên `Cloud Firestore`. Toàn bộ thông tin được lưu trữ từ trước (Họ tên, ngày sinh, chuỗi ảnh đại diện Base64) sẽ lập tức được tải xuống và đổ ra các thành phần giao diện theo thời gian thực.
* **Cơ chế chuyển đổi trạng thái động (Dynamic Layout Toggle):** Ngay tại màn hình Hồ sơ, người dùng có thể linh hoạt chuyển đổi giữa chế độ Xem và Chỉnh sửa thông tin bằng cách đóng/mở thuộc tính ẩn hiện (`View.VISIBLE` / `View.GONE`) của các lớp `ConstraintLayout` con, giúp tiết kiệm tối đa tài nguyên phần cứng.
* **Tích hợp Khôi phục Mật khẩu & Đăng xuất an toàn:** * Cho phép người dùng gửi nhanh liên kết đặt lại mật khẩu qua hàm `sendPasswordResetEmail`.
  * Nút Đăng xuất (`auth.signOut()`) sử dụng cờ hiệu `Intent.FLAG_ACTIVITY_CLEAR_TASK` xóa sạch lịch sử các màn hình trước, cam kết bảo mật tuyệt đối khi thoát ứng dụng.

---

### 3. Chức năng Thêm Giao dịch
> 🔗 [Xem mã nguồn `AddFragment.java`](https://github.com/mlinhhihi/Expense-Tracker/blob/main/MyWallet/app/src/main/java/thick2/truongthimylinh/mywallet/AddFragment.java)

<p align="center">
  <img width="220" alt="Màn hình Thêm Giao dịch" src="https://github.com/user-attachments/assets/caf22be5-a092-4e61-a7d5-2b95da82952f" />
</p>

**Tự động định dạng tiền tệ thời gian thực (Real-time Currency Masking):** Tích hợp bộ lắng nghe `TextWatcher` kết hợp giải thuật định dạng `DecimalFormat("#,###")`. Khi người dùng nhập số tiền, hệ thống tự động bóc tách và chèn các dấu chấm phân cách hàng nghìn (`100.000`, `2.5000.000`...) theo thời gian thực mà không làm nghẽn luồng xử lý chính, mang lại trải nghiệm UI/UX mượt mà.
**Cơ chế ẩn/hiện giao diện thông minh (Conditional UI Toggle):** Tránh hiện tượng phân tán sự chú ý bằng cách ẩn toàn bộ các trường nhập liệu ban đầu. Chỉ khi người dùng chọn một danh mục cụ thể hoặc kích hoạt tính năng tạo danh mục mới, các ô nhập liệu liên quan (`Amount`, `Note`, `SaveButton`) mới thay đổi trạng thái hiển thị (`View.VISIBLE`), giúp giao diện luôn sạch sẽ, tinh gọn.
**Tích hợp lịch chọn thời gian kép (Nested Picker Dialogs):** Cho phép ghi nhận mốc thời gian giao dịch chính xác bằng cách lồng ghép liên tiếp hai hộp thoại: `DatePickerDialog` (Chọn ngày) nối tiếp `TimePickerDialog` (Chọn giờ), dữ liệu sau đó được chuẩn hóa về định dạng `dd/MM/yyyy HH:mm`.
**Cơ chế lưu trữ danh mục động (Dynamic Category System):** * Cung cấp sẵn các danh mục mặc định cho hai phân hệ dòng tiền: **INCOME** (*Lương, Thưởng, Bán đồ*) và **EXPENSE** (*Ăn uống, Mua sắm, Đi lại*).
   Hỗ trợ tùy chọn đặc biệt `+ Thêm danh mục`, cho phép người dùng tự tạo nhãn mới. Dữ liệu này được lưu riêng lẻ vào một Sub-collection nằm sâu trong cấu trúc tài khoản người dùng (`users/{uid}/categories`) để tái sử dụng lâu dài, đảm bảo tính cá nhân hóa tối đa.
**Đồng bộ thực thể giao dịch lên Firestore:** Gom toàn bộ dữ liệu gồm Số tiền (sau khi đã chuẩn hóa loại bỏ dấu chấm), Ghi chú, Danh mục, Phân loại, Mã người dùng (`uid`) và định dạng thời gian Firebase `Timestamp` để đẩy lên Collection `transactions` toàn cục, làm cơ sở dữ liệu cho việc tính toán biểu đồ và bộ lọc lịch sử.

---

### 4. Chức năng Trang chủ & Quản lý danh sách
> 🔗 [Xem mã nguồn `HomeFragment.java`](https://github.com/mlinhhihi/Expense-Tracker/blob/main/MyWallet/app/src/main/java/thick2/truongthimylinh/mywallet/HomeFragment.java)

<p align="center">
  <img width="220" alt="Màn hình Trang chủ" src="https://github.com/user-attachments/assets/3102cca5-7200-41ab-9cef-382664cdcb35" />
</p>

**Tính toán cán cân tài chính tự động (Real-time Balance Calculator):** Hệ thống quét toàn bộ danh sách giao dịch trả về từ Cloud Firestore để phân tách dòng tiền. Sử dụng thuật toán tính toán lũy kế bất đồng bộ để xuất ra ba chỉ số tài chính: **Tổng thu nhập (`INCOME`)**, **Tổng chi tiêu (`EXPENSE`)**, và **Số dư khả dụng (`Balance`)**. Dữ liệu được định dạng bằng `DecimalFormat("#,###")` và tự động cập nhật ngay khi có biến động dữ liệu.
**Bộ lọc thời gian thông minh (Calendar-driven Query):** Tích hợp hai nút chuyển đổi tháng (`btnPrevMonth`/`btnNextMonth`) kết hợp nhân bản đối tượng `Calendar.clone()`. Hệ thống tự động xác định chính xác ranh giới mili-giây đầu tiên và cuối cùng của tháng được chọn để lọc dữ liệu trực quan mà không bị lệch múi giờ.
**Tìm kiếm đa điều kiện nâng cao (Multi-Criteria Real-time Search):** Sử dụng `TextWatcher` để bắt chuỗi ký tự người dùng gõ vào ô tìm kiếm. Thuật toán tự động chuyển đổi định dạng mốc thời gian `Timestamp` thành chuỗi văn bản (`dd/MM/yyyy HH:mm`) để tra cứu song song và quét đồng thời trên 4 trường thuộc tính: *Tên danh mục, Phân loại dòng tiền, Nội dung ghi chú, và Ngày giờ thực hiện*, giúp trả về kết quả chính xác tuyệt đối.
**Chu trình quản lý dữ liệu khép kín (Full CRUD Workflow via Dialog):** * **Đọc (Read):** Tối ưu hóa hiển thị bằng `RecyclerView` kết hợp sắp xếp danh sách theo thứ tự thời gian mới nhất (`Collections.sort`).
  **Xóa (Delete):** Cho phép xóa thực thể giao dịch trực tiếp khỏi Firestore bằng Document ID, tự động trừ tiền trong danh sách cục bộ (`list.remove`) và cập nhật lại giao diện ngay lập tức.
  **Sửa (Update):** Khởi tạo hộp thoại tùy chọn lồng cấu trúc `LayoutInflater` để nạp layout `fragment_edit` động. Hộp thoại tự ghim lại toàn bộ dữ liệu cũ (Số tiền, ghi chú, loại giao dịch, ngày giờ, danh mục tương ứng) và tích hợp sẵn `TextWatcher` định dạng dấu chấm tiền tệ thời gian thực, cho phép người dùng thay đổi mọi thông số và đồng bộ ngược lên cơ sở dữ liệu qua lệnh `db.collection(...).update()`.

---

### 5. Chức năng Biểu đồ & Phân tích (Thống kê)
> 🔗 [Xem mã nguồn `ChartFragment.java`](https://github.com/mlinhhihi/Expense-Tracker/blob/main/MyWallet/app/src/main/java/thick2/truongthimylinh/mywallet/ChartFragment.java)
<p align="center">
  <img width="220" alt="Màn hình Đăng nhập" src="https://github.com/user-attachments/assets/7bf514a5-c7ea-4c6a-bbfd-6a4db2514241" style="margin-right: 20px;" />
  <img width="220" alt="Màn hình Hồ sơ" src="https://github.com/user-attachments/assets/38a425f4-adad-48c0-83e6-a8f56e8bcc28" />
</p>

**Xử lý đa luồng dữ liệu (Dual-Mode Data Analytics):** Thiết kế cấu trúc chuyển đổi linh hoạt thông qua `RadioGroup` đáp ứng 2 chế độ phân tích sâu:
  **Biểu đồ Tổng quan Thu/Chi (Overview Mode):** Tính toán cán cân dòng tiền tổng thể bằng cách gom toàn bộ giá trị của `INCOME` (Màu xanh lam tươi mát `#81C784`) đối chiếu trực tiếp với `EXPENSE` (Màu đỏ cảnh báo `#E57373`).
  **Biểu đồ Cơ cấu Chi tiêu (Expense Detail Mode):** Sử dụng cấu trúc dữ liệu `Map<String, Double>` để tự động gộp dữ liệu tiền tệ theo từng từ khóa nhãn danh mục chi tiết (Ví dụ: *Ăn uống, Mua sắm, Đi lại*). Các danh mục không có tên sẽ được gom tự động vào nhóm dữ liệu phân loại `Khác`.
**Thuật toán bóc tách và phân lọc mốc thời gian (Time-Parsing Algorithm):** Hệ thống quét toàn bộ danh sách, ép kiểu thực thể linh hoạt (hỗ trợ cả định dạng dữ liệu kiểu `Number` hoặc chuỗi `String` từ database) và dùng đối tượng `Calendar` để đồng bộ. Bộ lọc hỗ trợ bóc tách mốc thời gian theo năm hiện tại, kết hợp tùy chọn xem chi tiết theo từng tháng cô lập hoặc gộp dữ liệu "Cả năm" để đánh giá xu hướng dài hạn.
**Tối ưu hóa UI/UX Biểu đồ Cao cấp (MPAndroidChart Customization):** * Định dạng hiển thị dữ liệu thuần ở dạng tỷ lệ phần trăm (`setUsePercentValues`) kết hợp hiệu ứng hoạt họa quét góc `animateY(1000)`.
   Tận dụng kỹ thuật dịch chuyển nhãn thông minh (`ValuePosition.OUTSIDE_SLICE`), vẽ đường dẫn chỉ định cấu trúc (`setValueLinePart1Length`) giúp các nhãn tỷ lệ phần trăm luôn hiển thị ngay ngắn ở viền ngoài biểu đồ, tránh hiện tượng chồng chéo chữ khi có quá nhiều danh mục chi tiêu nhỏ lẻ.

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

