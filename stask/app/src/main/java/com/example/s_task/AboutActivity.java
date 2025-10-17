package com.example.s_task;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setTitle("Giới Thiệu");

        TextView textViewInfo = findViewById(R.id.textViewInfo);

        String info = "S-TASK - Quản Lý Công Việc Cá Nhân\n\n" +
                "Phiên bản: 1.0\n\n" +
                "Mô tả:\n" +
                "Ứng dụng giúp bạn quản lý công việc cá nhân hiệu quả với các tính năng:\n" +
                "• Thêm, sửa, xóa công việc\n" +
                "• Đánh dấu hoàn thành\n" +
                "• Nhắc việc đúng hạn\n" +
                "• Lưu trữ dữ liệu tự động\n\n" +
                "Người thực hiện:\n" +
                "Sinh viên PTIT\n" +
                "Bài thực hành số 4\n\n" +
                "© 2025 S-Task. All rights reserved.";

        textViewInfo.setText(info);
    }
}

