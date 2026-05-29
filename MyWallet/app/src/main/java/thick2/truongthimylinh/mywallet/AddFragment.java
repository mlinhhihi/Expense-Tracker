package thick2.truongthimylinh.mywallet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import android.text.TextWatcher;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddFragment extends Fragment {

    EditText edtAmount, edtNote, edtNewCategory;
    RadioGroup radioType;
    Spinner spCategory;
    Button btnSave, btnAddCategory;
    TextView txtTime;
    ProgressBar progressBarScan;
    FirebaseFirestore db;
    FirebaseUser user;
    String uid = "";

    ArrayAdapter<String> adapter;
    Calendar selectedCalendar = Calendar.getInstance();

    private Button btnScanInvoice;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private com.google.mlkit.vision.text.TextRecognizer textRecognizer;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textRecognizer = com.google.mlkit.vision.text.TextRecognition.getClient(
                com.google.mlkit.vision.text.latin.TextRecognizerOptions.DEFAULT_OPTIONS);

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Bạn cần cấp quyền Camera để quét hóa đơn!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );

        // ĐÓN DỮ LIỆU THUMBNAIL: Chụp trực tiếp không qua FileProvider tránh lỗi Rom máy ảo sập ngầm
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        Context context = getContext();
                        try {
                            Bundle extras = result.getData().getExtras();
                            if (extras != null) {
                                Bitmap imageBitmap = (Bitmap) extras.get("data");
                                if (imageBitmap != null) {
                                    runTextRecognition(imageBitmap);
                                } else {
                                    if (context != null) Toast.makeText(context, "Không lấy được dữ liệu ảnh", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (context != null) Toast.makeText(context, "Lỗi phân tích bitmap: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View viewAdd = inflater.inflate(R.layout.fragment_add, container, false);

        btnScanInvoice = viewAdd.findViewById(R.id.btnScanInvoice);
        edtAmount = viewAdd.findViewById(R.id.edtAmount);
        edtNote = viewAdd.findViewById(R.id.edtNote);
        edtNewCategory = viewAdd.findViewById(R.id.edtNewCategory);
        radioType = viewAdd.findViewById(R.id.radioType);
        spCategory = viewAdd.findViewById(R.id.spCategory);
        btnSave = viewAdd.findViewById(R.id.btnSave);
        btnAddCategory = viewAdd.findViewById(R.id.btnAddCategory);
        txtTime = viewAdd.findViewById(R.id.txtDate);
        progressBarScan = viewAdd.findViewById(R.id.progressBarScan);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            txtTime.setText(sdf.format(selectedCalendar.getTime()));
            btnScanInvoice.setEnabled(false);
            Toast.makeText(getContext(), "Cảnh báo: Bạn chưa đăng nhập tài khoản!", Toast.LENGTH_LONG).show();
            return viewAdd;
        }

        uid = user.getUid();
        txtTime.setText(sdf.format(selectedCalendar.getTime()));

        btnScanInvoice.setOnClickListener(v -> {
            Context context = getContext();
            if (context != null) {
                if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
                        == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    requestPermissionLauncher.launch(android.Manifest.permission.CAMERA);
                }
            }
        });

        edtAmount.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;
                isUpdating = true;
                try {
                    String clean = s.toString().replace(".", "").replace(",", "");
                    if (clean.isEmpty()) {
                        edtAmount.setText("");
                        isUpdating = false;
                        return;
                    }
                    long value = Long.parseLong(clean);
                    java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###");
                    String formatted = formatter.format(value).replace(",", ".");
                    edtAmount.setText(formatted);
                    edtAmount.setSelection(formatted.length());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isUpdating = false;
            }
        });

        txtTime.setOnClickListener(v -> {
            if (getContext() == null) return;
            DatePickerDialog datePicker = new DatePickerDialog(
                    getContext(),
                    (view, year, month, dayOfMonth) -> {
                        selectedCalendar.set(Calendar.YEAR, year);
                        selectedCalendar.set(Calendar.MONTH, month);
                        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        TimePickerDialog timePicker = new TimePickerDialog(
                                getContext(),
                                (view1, hour, minute) -> {
                                    selectedCalendar.set(Calendar.HOUR_OF_DAY, hour);
                                    selectedCalendar.set(Calendar.MINUTE, minute);
                                    txtTime.setText(sdf.format(selectedCalendar.getTime()));
                                },
                                selectedCalendar.get(Calendar.HOUR_OF_DAY),
                                selectedCalendar.get(Calendar.MINUTE),
                                true
                        );
                        timePicker.show();
                    },
                    selectedCalendar.get(Calendar.YEAR),
                    selectedCalendar.get(Calendar.MONTH),
                    selectedCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePicker.show();
        });

        loadCategories(true);

        radioType.setOnCheckedChangeListener((group, checkedId) -> {
            loadCategories(checkedId == R.id.rbIncome);
            edtAmount.setVisibility(View.GONE);
            edtNote.setVisibility(View.GONE);
            btnSave.setVisibility(View.GONE);
            edtNewCategory.setVisibility(View.GONE);
            btnAddCategory.setVisibility(View.GONE);
        });

        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spCategory.getSelectedItem() == null) return;
                String selected = spCategory.getSelectedItem().toString();

                if (selected.equals("+ Thêm danh mục")) {
                    edtNewCategory.setVisibility(View.VISIBLE);
                    btnAddCategory.setVisibility(View.VISIBLE);
                    edtAmount.setVisibility(View.GONE);
                    edtNote.setVisibility(View.GONE);
                    btnSave.setVisibility(View.GONE);
                } else {
                    edtNewCategory.setVisibility(View.GONE);
                    btnAddCategory.setVisibility(View.GONE);
                    edtAmount.setVisibility(View.VISIBLE);
                    edtNote.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnAddCategory.setOnClickListener(v -> {
            String newCategory = edtNewCategory.getText().toString().trim();
            if (newCategory.isEmpty()) {
                edtNewCategory.setError("Nhập danh mục");
                return;
            }

            String type = (radioType.getCheckedRadioButtonId() == R.id.rbIncome) ? "INCOME" : "EXPENSE";
            HashMap<String, Object> data = new HashMap<>();
            data.put("name", newCategory);
            data.put("type", type);

            if (!uid.isEmpty()) {
                db.collection("users").document(uid).collection("categories").add(data)
                        .addOnSuccessListener(r -> {
                            Toast.makeText(getContext(), "Đã thêm danh mục", Toast.LENGTH_SHORT).show();
                            edtNewCategory.setText("");
                            loadCategories(type.equals("INCOME"));
                        });
            }
        });

        btnSave.setOnClickListener(v -> {
            String amount = edtAmount.getText().toString().replace(".", "").trim();
            String note = edtNote.getText().toString().trim();

            if (amount.isEmpty()) {
                edtAmount.setError("Nhập số tiền");
                return;
            }

            String type = (radioType.getCheckedRadioButtonId() == R.id.rbIncome) ? "INCOME" : "EXPENSE";
            String category = spCategory.getSelectedItem() != null ? spCategory.getSelectedItem().toString() : "";

            HashMap<String, Object> data = new HashMap<>();
            data.put("amount", amount);
            data.put("note", note);
            data.put("type", type);
            data.put("category", category);
            data.put("uid", uid);
            data.put("time", new Timestamp(selectedCalendar.getTime()));

            db.collection("transactions").add(data)
                    .addOnSuccessListener(r -> {
                        Toast.makeText(getContext(), "Lưu thành công", Toast.LENGTH_SHORT).show();
                        edtAmount.setText("");
                        edtNote.setText("");
                        selectedCalendar = Calendar.getInstance();
                        txtTime.setText(sdf.format(selectedCalendar.getTime()));
                    });
        });

        return viewAdd;
    }

    private void runTextRecognition(Bitmap bitmap) {
        // 1. HIỆN PROGRESSBAR KHI BẮT ĐẦU
        progressBarScan.setVisibility(View.VISIBLE);

        com.google.mlkit.vision.common.InputImage image = com.google.mlkit.vision.common.InputImage.fromBitmap(bitmap, 0);

        textRecognizer.process(image)
                .addOnSuccessListener(text -> {
                    String rawText = text.getText();
                    if (!rawText.isEmpty()) {
                        sendToGeminiAI(rawText);
                    } else {
                        progressBarScan.setVisibility(View.GONE); // 2. ẨN NẾU KHÔNG CÓ CHỮ
                        if (getContext() != null) Toast.makeText(getContext(), "Không tìm thấy chữ", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBarScan.setVisibility(View.GONE); // 3. ẨN NẾU LỖI ML KIT
                    if (getContext() != null) Toast.makeText(getContext(), "Lỗi quét: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sendToGeminiAI(String rawText) {
        OkHttpClient client = new OkHttpClient();

        String prompt = "Đọc đoạn văn bản quét từ hóa đơn sau đây. Hãy trích xuất ra: Tổng số tiền (chỉ lấy số nguyên), Ngày tháng lập hóa đơn (định dạng dd/MM/yyyy HH:mm), và Tên cửa hàng hoặc nội dung mua sắm ngắn gọn làm ghi chú. Trả về kết quả DUY NHẤT dưới dạng chuỗi JSON sạch sẽ như sau, không kèm giải thích hay ký tự markdown:\n"
                + "{\n"
                + "  \"amount\": \"số_tiền\",\n"
                + "  \"date\": \"ngày_tháng\",\n"
                + "  \"note\": \"ghi_chú\"\n"
                + "}\n"
                + "Văn bản hóa đơn thô:\n" + rawText;
        String apiKey = BuildConfig.GEMINI_API_KEY;
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        String jsonPayload = "{\"contents\":[{\"parts\":[{\"text\":\"" + prompt.replace("\"", "\\\"").replace("\n", "\\n") + "\"}]}]}";

        RequestBody body = RequestBody.create(jsonPayload, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        progressBarScan.setVisibility(View.GONE); // ĐÃ THÊM: Tắt ProgressBar khi lỗi mạng
                        Toast.makeText(getContext(), "Lỗi kết nối AI: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseString = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        String aiResponse = jsonObject.getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text").trim();

                        if (aiResponse.contains("{")) {
                            aiResponse = aiResponse.substring(aiResponse.indexOf("{"), aiResponse.lastIndexOf("}") + 1);
                        }

                        JSONObject dataJson = new JSONObject(aiResponse);
                        String amount = dataJson.optString("amount", "");
                        String date = dataJson.optString("date", "");
                        String note = dataJson.optString("note", "");

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                radioType.check(R.id.rbExpense);

                                if (spCategory.getAdapter() != null && spCategory.getAdapter().getCount() > 0) {
                                    spCategory.setSelection(0);
                                }

                                // Sau khi AI quét xong, hiển thị các ô lên giao diện
                                edtAmount.setVisibility(View.VISIBLE);
                                edtNote.setVisibility(View.VISIBLE);
                                btnSave.setVisibility(View.VISIBLE);

                                if (!amount.isEmpty()) edtAmount.setText(amount);
                                if (!note.isEmpty()) edtNote.setText("Hóa đơn: " + note);
                                if (!date.isEmpty()) {
                                    try {
                                        java.util.Date parsedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse(date);
                                        if (parsedDate != null) {
                                            selectedCalendar.setTime(parsedDate);
                                            txtTime.setText(sdf.format(selectedCalendar.getTime()));
                                        }
                                    } catch (Exception ignored) {}
                                }
                                if (getContext() != null) Toast.makeText(getContext(), "AI đã phân tích xong hóa đơn!", Toast.LENGTH_SHORT).show();
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void loadCategories(boolean isIncome) {
        if (uid == null || uid.isEmpty()) return;
        ArrayList<String> list = new ArrayList<>();

        if (isIncome) {
            list.add("Lương");
            list.add("Thưởng");
            list.add("Bán đồ");
        } else {
            list.add("Ăn uống");
            list.add("Mua sắm");
            list.add("Đi lại");
        }

        db.collection("users")
                .document(uid)
                .collection("categories")
                .whereEqualTo("type", isIncome ? "INCOME" : "EXPENSE")
                .get()
                .addOnSuccessListener(snap -> {
                    if (snap != null) {
                        for (QueryDocumentSnapshot doc : snap) {
                            String name = doc.getString("name");
                            if (name != null && !list.contains(name)) {
                                list.add(name);
                            }
                        }
                    }
                    list.add("+ Thêm danh mục");

                    if (getContext() != null) {
                        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, list);
                        spCategory.setAdapter(adapter);
                    }
                });
    }

    private void openCamera() {
        try {
            if (getContext() != null && androidx.core.content.ContextCompat.checkSelfPermission(
                    getContext(), android.Manifest.permission.CAMERA) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA);
                return;
            }

            // Gọi Intent Camera tiêu chuẩn
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            try {
                // Thay vì dùng resolveActivity, ta khởi chạy trực tiếp bên trong khối try-catch
                cameraLauncher.launch(takePictureIntent);
            } catch (android.content.ActivityNotFoundException e) {
                // Khối này sẽ chạy nếu thiết bị thực sự không có bất kỳ app Camera nào
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Không tìm thấy ứng dụng hệ thống hỗ trợ Camera!", Toast.LENGTH_LONG).show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (getContext() != null) {
                Toast.makeText(getContext(), "Lỗi mở camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}