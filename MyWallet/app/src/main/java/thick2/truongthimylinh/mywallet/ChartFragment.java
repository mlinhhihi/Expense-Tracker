package thick2.truongthimylinh.mywallet;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChartFragment extends Fragment {

    private RadioGroup radioGroupChart;
    private RadioButton radInoutOverview, radExpenseDetail;
    private Spinner spinnerMonth, spinnerYear;
    private TextView txtChartTitle;
    private PieChart pieChart;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String uid;

    private double totalIncome = 0;
    private double totalExpense = 0;
    private final Map<String, Double> expenseByCategory = new HashMap<>();
    private final ArrayList<DocumentSnapshot> allTransactions = new ArrayList<>();

    // Biến cờ hiệu ngăn sập ứng dụng khi Spinner khởi tạo
    private boolean isDataLoaded = false;

    public ChartFragment() {
    }

    @SuppressWarnings("AndroidLintSetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);

        // Ánh xạ
        radioGroupChart = view.findViewById(R.id.radioGroupChart);
        radInoutOverview = view.findViewById(R.id.radInoutOverview);
        radExpenseDetail = view.findViewById(R.id.radExpenseDetail);
        spinnerMonth = view.findViewById(R.id.spinnerMonth);
        spinnerYear = view.findViewById(R.id.spinnerYear);
        txtChartTitle = view.findViewById(R.id.txtChartTitle);
        pieChart = view.findViewById(R.id.pieChart);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupSpinners();
        setupPieChartProperties();

        if (auth.getCurrentUser() != null) {
            uid = auth.getCurrentUser().getUid();
            fetchDataFromFirestore();
        }

        // Sự kiện đổi Tab biểu đồ
        radioGroupChart.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radInoutOverview) {
                txtChartTitle.setText("Biểu đồ tổng quan tình hình tài chính");
            } else if (checkedId == R.id.radExpenseDetail) {
                txtChartTitle.setText("Phân tích danh mục chi tiêu nhiều nhất");
            }
            if (isDataLoaded) {
                filterAndCalculateData();
            }
        });

        // Sự kiện khi bấm chọn Tháng/Năm trên Spinner
        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Chỉ tính toán khi dữ liệu từ Firebase đã sẵn sàng
                if (isDataLoaded) {
                    filterAndCalculateData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spinnerMonth.setOnItemSelectedListener(filterListener);
        spinnerYear.setOnItemSelectedListener(filterListener);

        return view;
    }

    private void setupSpinners() {
        if (getContext() == null) return;

        ArrayList<String> months = new ArrayList<>();
        months.add("Cả năm");
        for (int i = 1; i <= 12; i++) {
            months.add("Tháng " + i);
        }
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        ArrayList<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear; i >= currentYear - 5; i--) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
    }

    private void setupPieChartProperties() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(25f, 10f, 25f, 10f);
        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setHoleRadius(50f);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setWordWrapEnabled(true);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
    }

    private void fetchDataFromFirestore() {
        db.collection("transactions")
                .whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allTransactions.clear();
                    allTransactions.addAll(queryDocumentSnapshots.getDocuments());

                    // Bật cờ hiệu: Đã tải xong dữ liệu an toàn!
                    isDataLoaded = true;

                    filterAndCalculateData();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void filterAndCalculateData() {
        totalIncome = 0;
        totalExpense = 0;
        expenseByCategory.clear();

        if (spinnerMonth.getSelectedItem() == null || spinnerYear.getSelectedItem() == null) {
            return;
        }

        String selectedMonthStr = spinnerMonth.getSelectedItem().toString();
        String selectedYearStr = spinnerYear.getSelectedItem().toString();
        int targetYear = Integer.parseInt(selectedYearStr);

        for (DocumentSnapshot doc : allTransactions) {
            Timestamp timestamp = doc.getTimestamp("time");
            if (timestamp == null) continue;

            Date transactionDate = timestamp.toDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(transactionDate);

            int transMonth = cal.get(Calendar.MONTH) + 1;
            int transYear = cal.get(Calendar.YEAR);

            if (transYear != targetYear) continue;

            if (!"Cả năm".equals(selectedMonthStr)) {
                int targetMonth = Integer.parseInt(selectedMonthStr.replace("Tháng ", "").trim());
                if (transMonth != targetMonth) continue;
            }

            String type = doc.getString("type");
            String category = doc.getString("category");

            double amount = 0;
            try {
                if (doc.contains("amount")) {
                    if (doc.get("amount") instanceof Number) {
                        amount = doc.getDouble("amount");
                    } else {
                        String amountStr = doc.getString("amount");
                        if (amountStr != null) amount = Double.parseDouble(amountStr);
                    }
                }
            } catch (Exception e) {
                amount = 0;
            }

            if ("INCOME".equalsIgnoreCase(type)) {
                totalIncome += amount;
            } else if ("EXPENSE".equalsIgnoreCase(type)) {
                totalExpense += amount;

                if (category == null || category.trim().length() == 0) {
                    category = "Khác";
                }

                Double existingAmount = expenseByCategory.get(category);
                if (existingAmount != null) {
                    expenseByCategory.put(category, existingAmount + amount);
                } else {
                    expenseByCategory.put(category, amount);
                }
            }
        }

        if (radioGroupChart.getCheckedRadioButtonId() == R.id.radInoutOverview) {
            drawInOutOverviewChart();
        } else {
            drawExpenseDetailChart();
        }
    }

    private void drawInOutOverviewChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        if (totalIncome == 0 && totalExpense == 0) {
            pieChart.clear();
            return;
        }

        if (totalIncome > 0) entries.add(new PieEntry((float) totalIncome, "Tổng Thu"));
        if (totalExpense > 0) entries.add(new PieEntry((float) totalExpense, "Tổng Chi"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#81C784"));
        colors.add(Color.parseColor("#E57373"));
        dataSet.setColors(colors);

        updateChartData(dataSet);
    }

    private void drawExpenseDetailChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        if (expenseByCategory.isEmpty()) {
            pieChart.clear();
            return;
        }

        for (Map.Entry<String, Double> entry : expenseByCategory.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        updateChartData(dataSet);
    }

    private void updateChartData(PieDataSet dataSet) {
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(7f);

        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1OffsetPercentage(80f);
        dataSet.setValueLinePart1Length(0.6f);
        dataSet.setValueLinePart2Length(0.5f);
        dataSet.setValueLineColor(Color.DKGRAY);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLUE);

        pieChart.setData(data);
        pieChart.setDrawEntryLabels(false);
        pieChart.highlightValues(null);
        pieChart.invalidate();
        pieChart.animateY(1000);
    }
}