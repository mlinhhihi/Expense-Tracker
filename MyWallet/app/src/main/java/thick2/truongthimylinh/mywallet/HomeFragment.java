package thick2.truongthimylinh.mywallet;


import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View viewHome = inflater.inflate(R.layout.fragment_home, container, false);
        // ánh xạ
        txtBalance =
                viewHome.findViewById(R.id.txtBalance);

        txtIncome =
                viewHome.findViewById(R.id.txtIncome);

        txtExpense =
                viewHome.findViewById(R.id.txtExpense);

        txtMonth =
                viewHome.findViewById(R.id.txtMonth);

        edtSearch =
                viewHome.findViewById(R.id.edtSearch);

        rvTransaction =
                viewHome.findViewById(R.id.rvTransaction);

        // RecyclerView

        rvTransaction.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        adapter = new TransactionAdapter(list, transaction -> {

            showOptionDialog(transaction);

        });

        rvTransaction.setAdapter(adapter);

        // Firebase

        db = FirebaseFirestore.getInstance();

        user = FirebaseAuth
                .getInstance()
                .getCurrentUser();

        if (user == null) {

            txtBalance.setText("Chưa đăng nhập");

            return viewHome;
        }

        uid = user.getUid();

        // hiện tháng hiện tại

        updateMonthText();

        // load dữ liệu

        loadTransactions();

        // chọn tháng

        btnPrevMonth =
                viewHome.findViewById(R.id.btnPrevMonth);

        btnNextMonth =
                viewHome.findViewById(R.id.btnNextMonth);
        btnPrevMonth.setOnClickListener(v -> {

            selectedCalendar.add(Calendar.MONTH, -1);

            updateMonthText();

            loadTransactions();
        });

        btnNextMonth.setOnClickListener(v -> {

            Calendar current = Calendar.getInstance();

            int currentMonth =
                    current.get(Calendar.MONTH);

            int currentYear =
                    current.get(Calendar.YEAR);

            int selectedMonth =
                    selectedCalendar.get(Calendar.MONTH);

            int selectedYear =
                    selectedCalendar.get(Calendar.YEAR);

            selectedCalendar.add(Calendar.MONTH, 1);

            updateMonthText();

            loadTransactions();
        });

        // search realtime

        edtSearch.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {

                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        filterList(s.toString());
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {

                    }
                });

        return viewHome;
    }

    // load transaction

    private void loadTransactions() {

        Calendar start =
                (Calendar) selectedCalendar.clone();

        start.set(Calendar.DAY_OF_MONTH, 1);

        start.set(Calendar.HOUR_OF_DAY, 0);

        start.set(Calendar.MINUTE, 0);

        start.set(Calendar.SECOND, 0);

        start.set(Calendar.MILLISECOND, 0);

        Calendar end =
                (Calendar) start.clone();

        end.add(Calendar.MONTH, 1);

        db.collection("transactions")
                .whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    list.clear();

                    double income = 0;

                    double expense = 0;

                    for (DocumentSnapshot doc
                            : queryDocumentSnapshots) {

                        TransactionModel t =
                                doc.toObject(
                                        TransactionModel.class
                                );
                        t.setId(doc.getId());

                        if (t == null
                                || t.getTime() == null)
                            continue;


                        Date transactionDate =
                                t.getTime().toDate();

                        // lọc theo tháng
                        if (transactionDate.after(start.getTime())
                                && transactionDate.before(end.getTime())) {

                            list.add(t);

                            try {

                                double amount =
                                        Double.parseDouble(
                                                t.getAmount()
                                        );

                                if ("INCOME".equals(
                                        t.getType()
                                )) {

                                    income += amount;

                                } else {

                                    expense += amount;
                                }

                            } catch (Exception ignored) {
                            }
                        }
                    }

                    double balance =
                            income - expense;

                    DecimalFormat format =
                            new DecimalFormat("#,###");

                    String incomeText =
                            format.format(income)
                                    .replace(",", ".");

                    String expenseText =
                            format.format(expense)
                                    .replace(",", ".");

                    String balanceText =
                            format.format(balance)
                                    .replace(",", ".");

                    txtIncome.setText(
                            incomeText + "đ"
                    );

                    txtExpense.setText(
                            expenseText + "đ"
                    );

                    txtBalance.setText(
                            balanceText + "đ"
                    );

                });
    }

    // update tháng

    private void updateMonthText() {

        SimpleDateFormat sdf =
                new SimpleDateFormat(
                        "MM/yyyy",
                        Locale.getDefault()
                );

        txtMonth.setText(
                "Tháng " +
                        sdf.format(
                                selectedCalendar.getTime()
                        )
        );
    }

    // search

    private void filterList(String text) {

        List<TransactionModel> filteredList =
                new ArrayList<>();

        for (TransactionModel item : list) {

            String category =
                    item.getCategory() == null
                            ? ""
                            : item.getCategory();

            String type =
                    item.getType() == null
                            ? ""
                            : item.getType();

            String note =
                    item.getNote() == null
                            ? ""
                            : item.getNote();

            if (category.toLowerCase()
                    .contains(text.toLowerCase())

                    ||

                    type.toLowerCase()
                            .contains(text.toLowerCase())

                    ||

                    note.toLowerCase()
                            .contains(text.toLowerCase())) {

                filteredList.add(item);
            }
        }

        adapter = new TransactionAdapter(
                filteredList,
                transaction -> {

                    showOptionDialog(transaction);

                }
        );

        rvTransaction.setAdapter(adapter);
    }
    private void showOptionDialog(TransactionModel transaction) {

        String[] options = {"Sửa", "Xóa"};

        new AlertDialog.Builder(getContext())
                .setTitle("Chọn thao tác")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {

                        editTransaction(transaction);

                    } else {

                        deleteTransaction(transaction);
                    }

                })
                .show();
    }

    private void deleteTransaction(TransactionModel transaction) {

        db.collection("transactions")
                .document(transaction.getId())
                .delete()

                .addOnSuccessListener(unused -> {

                    // xóa khỏi list hiện tại
                    list.remove(transaction);

                    // cập nhật RecyclerView
                    adapter.notifyDataSetChanged();

                    Toast.makeText(
                            getContext(),
                            "Đã xóa giao dịch",
                            Toast.LENGTH_SHORT
                    ).show();

                    // cập nhật lại tổng thu chi
                    loadTransactions();

                });
    }
    private void editTransaction(TransactionModel transaction) {

        Toast.makeText(
                getContext(),
                "Chức năng sửa đang phát triển",
                Toast.LENGTH_SHORT
        ).show();
    }
}