package thick2.truongthimylinh.mywallet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class HomeFragment extends Fragment {

    TextView txtBalance, txtIncome, txtExpense, txtMonth;
    ImageView btnPrevMonth, btnNextMonth;

    EditText edtSearch;
    RecyclerView rvTransaction;

    List<TransactionModel> list = new ArrayList<>();
    TransactionAdapter adapter;

    FirebaseFirestore db;
    FirebaseUser user;
    String uid;

    Calendar selectedCalendar = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View viewHome = inflater.inflate(R.layout.fragment_home, container, false);

        // ánh xạ
        txtBalance = viewHome.findViewById(R.id.txtBalance);
        txtIncome = viewHome.findViewById(R.id.txtIncome);
        txtExpense = viewHome.findViewById(R.id.txtExpense);
        txtMonth = viewHome.findViewById(R.id.txtMonth);

        edtSearch = viewHome.findViewById(R.id.edtSearch);
        rvTransaction = viewHome.findViewById(R.id.rvTransaction);

        rvTransaction.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TransactionAdapter(list, this::showOptionDialog);
        rvTransaction.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            txtBalance.setText("Chưa đăng nhập");
            return viewHome;
        }

        uid = user.getUid();

        updateMonthText();
        loadTransactions();

        btnPrevMonth = viewHome.findViewById(R.id.btnPrevMonth);
        btnNextMonth = viewHome.findViewById(R.id.btnNextMonth);

        btnPrevMonth.setOnClickListener(v -> {
            selectedCalendar.add(Calendar.MONTH, -1);
            updateMonthText();
            loadTransactions();
        });

        btnNextMonth.setOnClickListener(v -> {
            selectedCalendar.add(Calendar.MONTH, 1);
            updateMonthText();
            loadTransactions();
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        return viewHome;
    }

    // ================= LOAD DATA =================
    private void loadTransactions() {

        Calendar start = (Calendar) selectedCalendar.clone();
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);

        Calendar end = (Calendar) start.clone();
        end.add(Calendar.MONTH, 1);

        db.collection("transactions")
                .whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener(query -> {

                    list.clear();

                    double income = 0;
                    double expense = 0;

                    for (DocumentSnapshot doc : query) {

                        TransactionModel t = doc.toObject(TransactionModel.class);
                        if (t == null || t.getTime() == null) continue;

                        t.setId(doc.getId());

                        Date date = t.getTime().toDate();

                        if (date.after(start.getTime()) && date.before(end.getTime())) {

                            list.add(t);

                            try {
                                double amount = Double.parseDouble(t.getAmount());

                                if ("INCOME".equals(t.getType())) income += amount;
                                else expense += amount;

                            } catch (Exception ignored) {}
                        }
                    }

                    DecimalFormat f = new DecimalFormat("#,###");

                    String incomeText =
                            f.format(income).replace(",", ".");

                    String expenseText =
                            f.format(expense).replace(",", ".");

                    String balanceText =
                            f.format(income - expense).replace(",", ".");

                    txtIncome.setText(incomeText + "đ");

                    txtExpense.setText(expenseText + "đ");

                    txtBalance.setText(balanceText + "đ");

                    Collections.sort(list, (a, b) -> b.getTime().compareTo(a.getTime()));

                    adapter.updateList(list);
                });
    }

    // ================= MONTH =================
    private void updateMonthText() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        txtMonth.setText("Tháng " + sdf.format(selectedCalendar.getTime()));
    }

    // ================= SEARCH =================
    private void filterList(String text) {

        List<TransactionModel> filtered = new ArrayList<>();

        for (TransactionModel t : list) {

            String cat = t.getCategory() == null ? "" : t.getCategory();
            String type = t.getType() == null ? "" : t.getType();
            String note = t.getNote() == null ? "" : t.getNote();

            if (cat.toLowerCase().contains(text.toLowerCase())
                    || type.toLowerCase().contains(text.toLowerCase())
                    || note.toLowerCase().contains(text.toLowerCase())) {
                filtered.add(t);
            }
        }

        adapter.updateList(filtered);
    }

    // ================= OPTION =================
    private void showOptionDialog(TransactionModel t) {

        String[] ops = {"Sửa", "Xóa"};

        new AlertDialog.Builder(getContext())
                .setTitle("Chọn thao tác")
                .setItems(ops, (d, which) -> {
                    if (which == 0) editTransaction(t);
                    else deleteTransaction(t);
                })
                .show();
    }

    // ================= DELETE =================
    private void deleteTransaction(TransactionModel t) {

        db.collection("transactions")
                .document(t.getId())
                .delete()
                .addOnSuccessListener(u -> {
                    list.remove(t);
                    adapter.updateList(list);
                    Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                    loadTransactions();
                });
    }


    private void editTransaction(TransactionModel transaction) {

        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.fragment_edit, null);

        EditText edtAmount = view.findViewById(R.id.edtAmount);
        edtAmount.addTextChangedListener(new TextWatcher() {

            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if (!s.toString().equals(current)) {

                    edtAmount.removeTextChangedListener(this);

                    String clean = s.toString().replace(".", "");

                    if (clean.isEmpty()) {
                        current = "";
                        edtAmount.setText("");
                        edtAmount.addTextChangedListener(this);
                        return;
                    }

                    try {
                        long value = Long.parseLong(clean);

                        java.text.DecimalFormat formatter =
                                new java.text.DecimalFormat("#,###");

                        String formatted = formatter.format(value).replace(",", ".");

                        current = formatted;
                        edtAmount.setText(formatted);
                        edtAmount.setSelection(formatted.length());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    edtAmount.addTextChangedListener(this);
                }
            }
        });
        EditText edtNote = view.findViewById(R.id.edtNote);
        TextView txtTime = view.findViewById(R.id.txtTime);
        Spinner spCategory = view.findViewById(R.id.spCategory);
        RadioGroup radioType = view.findViewById(R.id.radioType);
        RadioButton rbIncome = view.findViewById(R.id.rbIncome);
        RadioButton rbExpense = view.findViewById(R.id.rbExpense);

        // DATA CŨ
        edtAmount.setText(transaction.getAmount());
        edtNote.setText(transaction.getNote());

        boolean isIncome = "INCOME".equals(transaction.getType());
        rbIncome.setChecked(isIncome);
        rbExpense.setChecked(!isIncome);

        Calendar calendar = Calendar.getInstance();
        if (transaction.getTime() != null)
            calendar.setTime(transaction.getTime().toDate());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        txtTime.setText(sdf.format(calendar.getTime()));

        txtTime.setOnClickListener(v -> {

            DatePickerDialog dp = new DatePickerDialog(requireContext(),
                    (view1, y, m, d) -> {

                        calendar.set(Calendar.YEAR, y);
                        calendar.set(Calendar.MONTH, m);
                        calendar.set(Calendar.DAY_OF_MONTH, d);

                        TimePickerDialog tp = new TimePickerDialog(requireContext(),
                                (v2, h, min) -> {

                                    calendar.set(Calendar.HOUR_OF_DAY, h);
                                    calendar.set(Calendar.MINUTE, min);

                                    txtTime.setText(sdf.format(calendar.getTime()));

                                }, calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE), true);

                        tp.show();

                    }, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));

            dp.show();
        });

        // CATEGORY FIX (CHỈ 1 SOURCE)
        ArrayList<String> catList = new ArrayList<>();

        String type = isIncome ? "INCOME" : "EXPENSE";

        db.collection("users")
                .document(uid)
                .collection("categories")
                .whereEqualTo("type", type)
                .get()
                .addOnSuccessListener(snap -> {

                    catList.clear();

                    if (isIncome) {
                        catList.add("Lương");
                        catList.add("Thưởng");
                        catList.add("Bán đồ");
                    } else {
                        catList.add("Ăn uống");
                        catList.add("Mua sắm");
                        catList.add("Đi lại");
                    }

                    for (DocumentSnapshot doc : snap) {
                        String name = doc.getString("name");
                        if (name != null && !catList.contains(name))
                            catList.add(name);
                    }

                    ArrayAdapter<String> ad = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            catList
                    );

                    spCategory.setAdapter(ad);

                    int pos = 0;
                    for (int i = 0; i < catList.size(); i++) {
                        if (catList.get(i).equals(transaction.getCategory())) {
                            pos = i;
                            break;
                        }
                    }

                    spCategory.setSelection(pos);
                });

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Sửa giao dịch")
                .setView(view)
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {

            String amount = edtAmount.getText().toString().replace(".", "").trim();

            if (amount.isEmpty()) {
                edtAmount.setError("Nhập số tiền");
                return;
            }

            String newType = rbIncome.isChecked() ? "INCOME" : "EXPENSE";

            Map<String, Object> update = new HashMap<>();
            update.put("amount", amount);
            update.put("note", edtNote.getText().toString());
            update.put("type", newType);
            update.put("category", spCategory.getSelectedItem().toString());
            update.put("time", new Timestamp(calendar.getTime()));

            db.collection("transactions")
                    .document(transaction.getId())
                    .update(update)
                    .addOnSuccessListener(u -> {
                        Toast.makeText(getContext(), "Cập nhật OK", Toast.LENGTH_SHORT).show();
                        loadTransactions();
                        dialog.dismiss();
                    });
        });
    }
}