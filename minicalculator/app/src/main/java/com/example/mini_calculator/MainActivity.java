package com.example.mini_calculator;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etNumber1, etNumber2;
    private TextView tvResult, tvHistory, tvExpression;
    private List<String> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo danh sách lịch sử
        historyList = new ArrayList<>();

        // Khởi tạo các views
        etNumber1 = findViewById(R.id.etNumber1);
        etNumber2 = findViewById(R.id.etNumber2);
        tvResult = findViewById(R.id.tvResult);
        tvHistory = findViewById(R.id.tvHistory);
        tvExpression = findViewById(R.id.tvExpression);

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnSub = findViewById(R.id.btnSub);
        Button btnMul = findViewById(R.id.btnMul);
        Button btnDiv = findViewById(R.id.btnDiv);
        Button btnClear = findViewById(R.id.btnClear);
        Button btnClearHistory = findViewById(R.id.btnClearHistory);

        // Xử lý sự kiện click cho các nút phép toán
        btnAdd.setOnClickListener(v -> performOperation('+'));
        btnSub.setOnClickListener(v -> performOperation('-'));
        btnMul.setOnClickListener(v -> performOperation('*'));
        btnDiv.setOnClickListener(v -> performOperation('/'));

        // Xử lý nút Clear
        btnClear.setOnClickListener(v -> {
            etNumber1.setText("");
            etNumber2.setText("");
            tvResult.setText("0");
            tvExpression.setText("");
            Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
        });

        // Xử lý nút Clear History
        btnClearHistory.setOnClickListener(v -> {
            historyList.clear();
            tvHistory.setText("Chưa có phép tính nào");
            Toast.makeText(this, "Đã xóa lịch sử", Toast.LENGTH_SHORT).show();
        });
    }

    private void performOperation(char operator) {
        String num1Str = etNumber1.getText().toString().trim();
        String num2Str = etNumber2.getText().toString().trim();

        if (TextUtils.isEmpty(num1Str) || TextUtils.isEmpty(num2Str)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ hai số!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double num1 = Double.parseDouble(num1Str);
            double num2 = Double.parseDouble(num2Str);
            double result = 0;
            String operatorSymbol = "";

            // Thực hiện phép tính
            switch (operator) {
                case '+':
                    result = num1 + num2;
                    operatorSymbol = "+";
                    break;
                case '-':
                    result = num1 - num2;
                    operatorSymbol = "-";
                    break;
                case '*':
                    result = num1 * num2;
                    operatorSymbol = "×";
                    break;
                case '/':
                    // Kiểm tra chia cho 0
                    if (num2 == 0) {
                        Toast.makeText(this, "Lỗi: Không thể chia cho 0!", Toast.LENGTH_LONG).show();
                        tvExpression.setText(formatNumber(num1) + " ÷ " + formatNumber(num2) + " = ERROR");
                        return;
                    }
                    result = num1 / num2;
                    operatorSymbol = "÷";
                    break;
            }

            // Format kết quả
            String resultStr = formatNumber(result);
            String expression = formatNumber(num1) + " " + operatorSymbol + " " + formatNumber(num2) + " = " + resultStr;

            // Hiển thị biểu thức hiện tại
            tvExpression.setText(expression);

            // Hiển thị kết quả
            tvResult.setText(resultStr);

            // Thêm vào lịch sử
            addToHistory(expression);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }

    // Thêm phép tính vào lịch sử
    private void addToHistory(String expression) {
        historyList.add(expression);

        // Giới hạn lịch sử tối đa 50 phép tính
        if (historyList.size() > 50) {
            historyList.remove(0);
        }

        // Cập nhật hiển thị lịch sử
        updateHistoryDisplay();
    }

    // Cập nhật hiển thị lịch sử
    private void updateHistoryDisplay() {
        if (historyList.isEmpty()) {
            tvHistory.setText("Chưa có phép tính nào");
        } else {
            StringBuilder historyText = new StringBuilder();
            // Hiển thị từ mới nhất đến cũ nhất
            for (int i = historyList.size() - 1; i >= 0; i--) {
                historyText.append(historyList.get(i));
                if (i > 0) {
                    historyText.append("\n");
                }
            }
            tvHistory.setText(historyText.toString());
        }
    }

    // Format số để hiển thị đẹp hơn
    private String formatNumber(double number) {
        if (number == (long) number) {
            return String.valueOf((long) number);
        } else {
            // Loại bỏ số 0 thừa ở cuối
            String formatted = String.format(Locale.getDefault(), "%.10f", number);
            formatted = formatted.replaceAll("0*$", "").replaceAll("\\.$", "");
            return formatted;
        }
    }
}