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

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View viewAdd = inflater.inflate(R.layout.fragment_add, container, false);

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

        uid = user.getUid();
        txtTime.setText(sdf.format(selectedCalendar.getTime()));


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

}