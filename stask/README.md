# S-TASK - Ứng Dụng Quản Lý Công Việc Cá Nhân

## Các lỗi đã khắc phục

### 1. Lỗi TaskReminderReceiver.java
- **Vấn đề**: File bị lỗi cấu trúc nghiêm trọng (code bị trùng lặp, thiếu package)
- **Giải pháp**: Tạo lại file hoàn toàn với cấu trúc đúng

### 2. Lỗi Theme
- **Vấn đề**: Theme sử dụng `NoActionBar` nhưng MainActivity cần ActionBar
- **Giải pháp**: Đổi từ `Theme.Material3.DayNight.NoActionBar` → `Theme.Material3.DayNight`

### 3. Lỗi Deprecated API
- **Vấn đề**: `startActivityForResult()` và `onActivityResult()` đã deprecated
- **Giải pháp**: Sử dụng Activity Result API mới với `ActivityResultLauncher`

## Cách chạy ứng dụng

### Build từ Terminal
```bash
cd /home/nagih/Workspaces/ptit/mobile/RIPT-Android/stask
./gradlew clean assembleDebug
```

### Cài đặt APK
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Chạy từ Android Studio
1. Mở project trong Android Studio
2. Click nút Run (▶️)
3. Chọn emulator hoặc thiết bị

## Tính năng đã triển khai

✅ **Màn hình chính**
- Hiển thị danh sách công việc (RecyclerView)
- Nút FAB (+) để thêm công việc
- Menu: Thêm, Xóa tất cả, Giới thiệu, Lọc

✅ **Thêm/Sửa công việc**
- Nhập tên, mô tả
- Chọn ngày hết hạn (DatePicker)
- Toast thông báo khi lưu thành công

✅ **Lưu trữ dữ liệu**
- SharedPreferences với JSON format
- Dữ liệu tồn tại khi mở lại app

✅ **Thông báo nhắc việc**
- AlarmManager + BroadcastReceiver
- Notification khi đến hạn (9:00 sáng)

✅ **Bộ lọc (Yêu cầu mở rộng)**
- Tất cả công việc
- Chưa hoàn thành
- Đã hoàn thành

✅ **Các tính năng khác**
- Checkbox đánh dấu hoàn thành
- Xóa từng công việc
- Xóa tất cả công việc
- Màn hình Giới thiệu

## Cấu trúc Project

```
app/src/main/
├── java/com/example/s_task/
│   ├── MainActivity.java              # Màn hình chính
│   ├── AddEditTaskActivity.java       # Thêm/Sửa công việc
│   ├── AboutActivity.java             # Giới thiệu
│   ├── Task.java                      # Model công việc
│   ├── TaskAdapter.java               # RecyclerView Adapter
│   ├── TaskStorage.java               # Lưu trữ SharedPreferences
│   └── TaskReminderReceiver.java      # BroadcastReceiver nhắc việc
├── res/
│   ├── layout/
│   │   ├── activity_main.xml
│   │   ├── activity_add_edit_task.xml
│   │   ├── activity_about.xml
│   │   └── item_task.xml
│   ├── menu/
│   │   └── main_menu.xml
│   └── values/
│       ├── strings.xml
│       └── themes.xml
└── AndroidManifest.xml
```

## Permissions

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.USE_EXACT_ALARM" />
```

## Yêu cầu hệ thống

- Android SDK: 24+ (Android 7.0+)
- Target SDK: 36
- Compile SDK: 36
- Java Version: 11

## Người thực hiện

Sinh viên PTIT - Bài thực hành số 4

© 2025 S-Task

