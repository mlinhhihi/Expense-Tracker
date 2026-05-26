package thick2.truongthimylinh.mywallet;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfileFragment extends Fragment {

    // Thành phần điều khiển ẩn hiện giao diện ConstraintLayout
    androidx.constraintlayout.widget.ConstraintLayout layoutViewProfile, layoutEditProfile;

    // Thành phần Header
    TextView txtHeaderName, btnViewInfo;
    ImageView imgAvatar;
    CardView cardAvatarClick;

    // Thành phần chế độ Xem thông tin
    TextView txtViewEmail, txtViewName, txtViewBirth;
    Button btnGoToEdit;

    // Thành phần chế độ Sửa thông tin
    EditText edtEmailProfile, edtFullName, txtBirthDate;
    Button btnUpdateProfile, btnBackToView;

    // Nút đăng xuất nằm ngay dưới Header
    Button btnLogout;

    // Quản lý dữ liệu tài khoản
    FirebaseAuth auth;
    FirebaseFirestore db;
    String uid;
    String encodedImage = ""; // Chuỗi mã hóa Base64 của ảnh đại diện

    String currentName = "Người dùng";
    String currentBirth = "Chưa cập nhật";

    Calendar birthCalendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    // Bộ lắng nghe kết quả chọn ảnh từ thư viện máy điện thoại
    // Thay thế toàn bộ bộ chọn ảnh cũ bằng bộ chọn ảnh hiện đại này
    private final ActivityResultLauncher<androidx.activity.result.PickVisualMediaRequest> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    try {
                        InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        imgAvatar.setImageTintList(null);

                        // Hiển thị ảnh lên khung tròn
                        imgAvatar.setPadding(0, 0, 0, 0);
                        imgAvatar.setImageBitmap(bitmap);

                        // Mã hóa ảnh sang chuỗi Base64 để lưu Firestore
                        encodedImage = encodeImageToBase64(bitmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Không thể tải ảnh này", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Bạn chưa chọn ảnh nào", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 1. Ánh xạ các khung Layout
        layoutViewProfile = view.findViewById(R.id.layoutViewProfile);
        layoutEditProfile = view.findViewById(R.id.layoutEditProfile);

        // 2. Ánh xạ thành phần Header
        txtHeaderName = view.findViewById(R.id.txtHeaderName);
        btnViewInfo = view.findViewById(R.id.btnViewInfo);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        cardAvatarClick = view.findViewById(R.id.cardAvatarClick);

        // 3. Ánh xạ thành phần giao diện Xem
        txtViewEmail = view.findViewById(R.id.txtViewEmail);
        txtViewName = view.findViewById(R.id.txtViewName);
        txtViewBirth = view.findViewById(R.id.txtViewBirth);
        btnGoToEdit = view.findViewById(R.id.btnGoToEdit);

        // 4. Ánh xạ thành phần giao diện Sửa
        edtEmailProfile = view.findViewById(R.id.edtEmailProfile);
        edtFullName = view.findViewById(R.id.edtFullName);
        txtBirthDate = view.findViewById(R.id.txtBirthDate);
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        btnBackToView = view.findViewById(R.id.btnBackToView);

        // 5. Ánh xạ nút Đăng xuất dưới Header
        btnLogout = view.findViewById(R.id.btnLogout);

        // Khởi tạo Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            uid = user.getUid();
            edtEmailProfile.setText(user.getEmail());
            txtViewEmail.setText("Email: " + user.getEmail());
            loadUserProfile(); // Tải dữ liệu từ Firestore lên app
        }

        // Bấm dòng chữ "Thông tin cá nhân >" trên Header để hiện màn hình Xem
        btnViewInfo.setOnClickListener(v -> {
            layoutViewProfile.setVisibility(View.VISIBLE);
            layoutEditProfile.setVisibility(View.GONE);
            cardAvatarClick.setClickable(false); // Khóa tính năng đổi ảnh khi đang xem
        });

        // Bấm nút "Chỉnh sửa thông tin" -> Hiện form sửa và GIỮ NGUYÊN DỮ LIỆU CŨ
        btnGoToEdit.setOnClickListener(v -> {
            edtFullName.setText(currentName.equals("Người dùng") ? "" : currentName);
            txtBirthDate.setText(currentBirth.equals("Chưa cập nhật") ? "Chọn ngày sinh" : currentBirth);

            layoutViewProfile.setVisibility(View.GONE);
            layoutEditProfile.setVisibility(View.VISIBLE);
            cardAvatarClick.setClickable(true); // Mở khóa cho phép bấm đổi ảnh đại diện
        });

        // Bấm vào hình đại diện để mở thư viện ảnh theo cách mới
        cardAvatarClick.setOnClickListener(v -> {
            pickImageLauncher.launch(new androidx.activity.result.PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
        // Bấm nút "Quay lại" từ form sửa -> Quay về form xem và hủy thay đổi (nạp lại data cũ)
        btnBackToView.setOnClickListener(v -> {
            layoutViewProfile.setVisibility(View.VISIBLE);
            layoutEditProfile.setVisibility(View.GONE);
            cardAvatarClick.setClickable(false);
            imgAvatar.setImageTintList(null);
            loadUserProfile();
        });

        // Bấm nút "Lưu thay đổi" -> Đẩy dữ liệu lên Firestore và làm mới chế độ Xem thông tin
        btnUpdateProfile.setOnClickListener(v -> {
            String fullName = edtFullName.getText().toString().trim();
            String birthDate = txtBirthDate.getText().toString().trim();

            if (fullName.isEmpty()) {
                edtFullName.setError("Vui lòng nhập họ và tên");
                return;
            }

            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("fullName", fullName);
            userProfile.put("birthDate", birthDate);
            userProfile.put("avatar", encodedImage); // Đẩy chuỗi ảnh Base64 lên database

            db.collection("users").document(uid)
                    .set(userProfile, com.google.firebase.firestore.SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();

                        // Cập nhật biến dữ liệu hiện tại
                        currentName = fullName;
                        currentBirth = birthDate;

                        // Cập nhật text hiển thị trực tiếp ra màn hình
                        txtHeaderName.setText(fullName);
                        txtViewName.setText("Họ và tên: " + fullName);
                        txtViewBirth.setText("Ngày sinh: " + birthDate);

                        // Đưa người dùng quay lại giao diện xem thông tin cá nhân
                        layoutViewProfile.setVisibility(View.VISIBLE);
                        layoutEditProfile.setVisibility(View.GONE);
                        cardAvatarClick.setClickable(false);
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        // Bấm nút Đăng xuất (Nằm ngay sát dưới Header)
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    // Hàm lấy dữ liệu từ Firestore đổ về giao diện khi mở màn hình hồ sơ
    private void loadUserProfile() {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("fullName");
                        String birth = documentSnapshot.getString("birthDate");
                        String avatarStr = documentSnapshot.getString("avatar");

                        if (name != null && !name.isEmpty()) {
                            currentName = name;
                            txtHeaderName.setText(name);
                            txtViewName.setText("Họ và tên: " + name);
                        }
                        if (birth != null && !birth.isEmpty()) {
                            currentBirth = birth;
                            txtViewBirth.setText("Ngày sinh: " + birth);
                            try {
                                birthCalendar.setTime(sdf.parse(birth));
                            } catch (Exception ignored) {}
                        }
                        // Nếu tài khoản có ảnh cũ trên Firestore -> Giải mã chuỗi Base64 thành ảnh để hiển thị
                        if (avatarStr != null && !avatarStr.isEmpty()) {
                            encodedImage = avatarStr;
                            byte[] decodedBytes = Base64.decode(avatarStr, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                            imgAvatar.setImageTintList(null);
                            imgAvatar.setPadding(0, 0, 0, 0); // Xóa khoảng trống icon mặc định
                            imgAvatar.setImageBitmap(bitmap);
                        }
                    }
                });
    }

    // Hàm chuyển đổi ảnh Bitmap thành chuỗi văn bản Base64 để lưu vào Firestore gọn nhẹ
    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream); // Nén 50% tránh quá tải dung lượng dòng lệnh
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}