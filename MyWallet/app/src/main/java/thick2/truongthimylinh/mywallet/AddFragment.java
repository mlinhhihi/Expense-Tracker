package thick2.truongthimylinh.mywallet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment {

    EditText edtAmount;
    EditText edtNote;
    EditText edtNewCategory;

    RadioGroup radioType;

    Spinner spCategory;

    Button btnSave;
    Button btnAddCategory;

    FirebaseFirestore db;

    FirebaseUser user;

    String uid;

    ArrayAdapter<String> adapter;


    // TODO: Rename and change types and number of parameters
    public static AddFragment newInstance(String param1, String param2) {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewAdd = inflater.inflate(R.layout.fragment_add, container, false);

        // ánh xạ view
        edtAmount =
                viewAdd.findViewById(R.id.edtAmount);

        edtNote =
                viewAdd.findViewById(R.id.edtNote);

        edtNewCategory =
                viewAdd.findViewById(R.id.edtNewCategory);

        radioType =
                viewAdd.findViewById(R.id.radioType);

        spCategory =
                viewAdd.findViewById(R.id.spCategory);

        btnSave =
                viewAdd.findViewById(R.id.btnSave);

        btnAddCategory =
                viewAdd.findViewById(R.id.btnAddCategory);

        // Firebase
        db = FirebaseFirestore.getInstance();

        user = FirebaseAuth
                .getInstance()
                .getCurrentUser();

        if (user != null) {

            uid = user.getUid();
        }

        // ẩn ban đầu
        edtAmount.setVisibility(View.GONE);
        edtNote.setVisibility(View.GONE);
        btnSave.setVisibility(View.GONE);

        edtNewCategory.setVisibility(View.GONE);
        btnAddCategory.setVisibility(View.GONE);

        // load category mặc định
        loadCategories(true);

        // đổi THU / CHI
        radioType.setOnCheckedChangeListener(
                (group, checkedId) -> {

                    if (checkedId == R.id.rbIncome) {

                        loadCategories(true);

                    } else {

                        loadCategories(false);
                    }

                    edtAmount.setVisibility(View.GONE);
                    edtNote.setVisibility(View.GONE);
                    btnSave.setVisibility(View.GONE);

                    edtNewCategory.setVisibility(View.GONE);
                    btnAddCategory.setVisibility(View.GONE);
                });

        // chọn category
        spCategory.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        String selected =
                                spCategory
                                        .getSelectedItem()
                                        .toString();

                        // nếu chọn thêm category
                        if (selected.equals("+ Thêm danh mục")) {

                            edtNewCategory
                                    .setVisibility(View.VISIBLE);

                            btnAddCategory
                                    .setVisibility(View.VISIBLE);

                            edtAmount
                                    .setVisibility(View.GONE);

                            edtNote
                                    .setVisibility(View.GONE);

                            btnSave
                                    .setVisibility(View.GONE);

                        } else {

                            edtNewCategory
                                    .setVisibility(View.GONE);

                            btnAddCategory
                                    .setVisibility(View.GONE);

                            edtAmount
                                    .setVisibility(View.VISIBLE);

                            edtNote
                                    .setVisibility(View.VISIBLE);

                            btnSave
                                    .setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {

                    }
                });

        // thêm category mới
        btnAddCategory.setOnClickListener(v -> {

            String newCategory =
                    edtNewCategory
                            .getText()
                            .toString()
                            .trim();

            if (newCategory.isEmpty()) {

                edtNewCategory
                        .setError("Nhập danh mục");

                return;
            }

            String type =
                    (radioType.getCheckedRadioButtonId()
                            == R.id.rbIncome)
                            ? "INCOME"
                            : "EXPENSE";

            HashMap<String, Object> categoryData =
                    new HashMap<>();

            categoryData.put("name", newCategory);

            categoryData.put("type", type);

            db.collection("users")
                    .document(uid)
                    .collection("categories")
                    .add(categoryData)
                    .addOnSuccessListener(documentReference -> {

                        Toast.makeText(
                                getContext(),
                                "Đã thêm danh mục",
                                Toast.LENGTH_SHORT
                        ).show();

                        edtNewCategory.setText("");

                        loadCategories(
                                type.equals("INCOME")
                        );
                    });
        });

        // lưu transaction
        btnSave.setOnClickListener(v -> {

            String amount =
                    edtAmount
                            .getText()
                            .toString()
                            .trim();

            String note =
                    edtNote
                            .getText()
                            .toString()
                            .trim();

            if (amount.isEmpty()) {

                edtAmount.setError("Nhập số tiền");

                return;
            }

            String type =
                    (radioType.getCheckedRadioButtonId()
                            == R.id.rbIncome)
                            ? "INCOME"
                            : "EXPENSE";

            String category =
                    spCategory
                            .getSelectedItem()
                            .toString();

            HashMap<String, Object> data =
                    new HashMap<>();

            data.put("amount", amount);

            data.put("note", note);

            data.put("type", type);

            data.put("category", category);

            data.put("uid", uid);

            data.put("time", com.google.firebase.Timestamp.now());

            db.collection("transactions")
                    .add(data)
                    .addOnSuccessListener(documentReference -> {

                        Toast.makeText(
                                getContext(),
                                "Lưu thành công",
                                Toast.LENGTH_SHORT
                        ).show();

                        edtAmount.setText("");

                        edtNote.setText("");

                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(
                                getContext(),
                                "Lỗi: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();

                    });
        });

        return viewAdd;
    }

    // load category từ Firestore
    private void loadCategories(boolean isIncome) {

        ArrayList<String> tempList =
                new ArrayList<>();

        // CATEGORY MẶC ĐỊNH
        if (isIncome) {

            tempList.add("Lương");
            tempList.add("Gia đình cho");
            tempList.add("Thưởng");
            tempList.add("Bán đồ");

        } else {

            tempList.add("Ăn uống");
            tempList.add("Mua sắm");
            tempList.add("Đi lại");
            tempList.add("Học tập");
            tempList.add("Giải trí");
        }

        // load category user thêm
        db.collection("users")
                .document(uid)
                .collection("categories")
                .whereEqualTo(
                        "type",
                        isIncome
                                ? "INCOME"
                                : "EXPENSE"
                )
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (QueryDocumentSnapshot doc
                            : queryDocumentSnapshots) {

                        String name =
                                doc.getString("name");

                        // tránh trùng
                        if (!tempList.contains(name)) {

                            tempList.add(name);
                        }
                    }

                    tempList.add("+ Thêm danh mục");

                    adapter =
                            new ArrayAdapter<>(
                                    getContext(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    tempList
                            );

                    spCategory.setAdapter(adapter);
                });
    }
}