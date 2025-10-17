package com.example.photo_gallary;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ImageSwitcher imageSwitcher;
    private GridView gridView;
    private TextView tvImageName, tvGridTitle;
    private Button btnPlaySlideshow, btnStopSlideshow, btnAddPhoto, btnShowFavorites;

    private ArrayList<Photo> allPhotos;
    private ArrayList<Photo> displayedPhotos;
    private ImageAdapter adapter;
    private int currentImageIndex = 0;

    private Handler slideshowHandler;
    private Runnable slideshowRunnable;
    private boolean isSlideshowRunning = false;
    private static final int SLIDESHOW_DELAY = 3000;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "PhotoGalleryPrefs";
    private static final String KEY_RECENT_IMAGES = "recentImages";
    private static final String KEY_FAVORITES = "favorites";
    private static final String KEY_CUSTOM_PHOTOS = "customPhotos";

    private boolean showingFavoritesOnly = false;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        initViews();
        initImagePicker();
        initImageData();
        setupImageSwitcher();
        setupGridView();
        setupButtons();

        displayImage(0);
        loadRecentImages();
    }

    private void initViews() {
        imageSwitcher = findViewById(R.id.imageSwitcher);
        gridView = findViewById(R.id.gridView);
        tvImageName = findViewById(R.id.tvImageName);
        tvGridTitle = findViewById(R.id.tvGridTitle);
        btnPlaySlideshow = findViewById(R.id.btnPlaySlideshow);
        btnStopSlideshow = findViewById(R.id.btnStopSlideshow);
        btnAddPhoto = findViewById(R.id.btnAddPhoto);
        btnShowFavorites = findViewById(R.id.btnShowFavorites);

        slideshowHandler = new Handler();
    }

    private void initImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            try {
                                // Lưu quyền truy cập URI vĩnh viễn
                                getContentResolver().takePersistableUriPermission(
                                        imageUri,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                );
                                addPhotoFromGallery(imageUri);
                            } catch (SecurityException e) {
                                Log.e(TAG, "Không thể lưu quyền truy cập URI", e);
                                Toast.makeText(this, "Không thể truy cập ảnh", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );
    }

    private void initImageData() {
        allPhotos = new ArrayList<>();

        // Bỏ ảnh mẫu từ drawable - bắt đầu với thư viện trống
        // User sẽ thêm ảnh của riêng mình

        // Tải ảnh tùy chỉnh từ SharedPreferences
        loadCustomPhotos();

        // Tải trạng thái yêu thích
        loadFavorites();

        displayedPhotos = new ArrayList<>(allPhotos);
    }

    private void setupImageSwitcher() {
        imageSwitcher.setFactory(() -> {
            ImageView imageView = new ImageView(MainActivity.this);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                    ImageSwitcher.LayoutParams.MATCH_PARENT,
                    ImageSwitcher.LayoutParams.MATCH_PARENT
            ));
            return imageView;
        });

        Animation inAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation outAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        imageSwitcher.setInAnimation(inAnimation);
        imageSwitcher.setOutAnimation(outAnimation);
    }

    private void setupGridView() {
        adapter = new ImageAdapter(this, displayedPhotos);
        gridView.setAdapter(adapter);

        // Xử lý click vào ảnh
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            currentImageIndex = position;
            displayImage(position);
            saveRecentImage(position);

            if (isSlideshowRunning) {
                stopSlideshow();
            }
        });

        // Xử lý click vào nút yêu thích
        adapter.setOnFavoriteClickListener((position, photo) -> {
            photo.setFavorite(!photo.isFavorite());
            adapter.notifyDataSetChanged();
            saveFavorites();

            String message = photo.isFavorite() ?
                    "Đã thêm vào yêu thích: " + photo.getName() :
                    "Đã xóa khỏi yêu thích: " + photo.getName();
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupButtons() {
        btnPlaySlideshow.setOnClickListener(v -> startSlideshow());
        btnStopSlideshow.setOnClickListener(v -> stopSlideshow());

        btnAddPhoto.setOnClickListener(v -> {
            if (checkPermission()) {
                openGallery();
            } else {
                requestPermission();
            }
        });

        btnShowFavorites.setOnClickListener(v -> toggleFavoritesView());
    }

    private void displayImage(int position) {
        if (position >= 0 && position < displayedPhotos.size()) {
            Photo photo = displayedPhotos.get(position);

            if (photo.isFromGallery()) {
                imageSwitcher.setImageURI(photo.getImageUri());
            } else {
                imageSwitcher.setImageResource(photo.getDrawableId());
            }

            String displayName = photo.getName();
            if (photo.isFavorite()) {
                displayName = displayName + " ⭐";
            }
            tvImageName.setText(displayName);
            currentImageIndex = position;
        }
    }

    private void startSlideshow() {
        if (!isSlideshowRunning && !displayedPhotos.isEmpty()) {
            isSlideshowRunning = true;
            btnPlaySlideshow.setEnabled(false);
            btnStopSlideshow.setEnabled(true);

            Toast.makeText(this, "Slideshow bắt đầu", Toast.LENGTH_SHORT).show();

            slideshowRunnable = new Runnable() {
                @Override
                public void run() {
                    if (isSlideshowRunning) {
                        currentImageIndex = (currentImageIndex + 1) % displayedPhotos.size();
                        displayImage(currentImageIndex);
                        slideshowHandler.postDelayed(this, SLIDESHOW_DELAY);
                    }
                }
            };

            slideshowHandler.postDelayed(slideshowRunnable, SLIDESHOW_DELAY);
        }
    }

    private void stopSlideshow() {
        if (isSlideshowRunning) {
            isSlideshowRunning = false;
            btnPlaySlideshow.setEnabled(true);
            btnStopSlideshow.setEnabled(false);
            slideshowHandler.removeCallbacks(slideshowRunnable);
            Toast.makeText(this, "Slideshow đã dừng", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Cần quyền truy cập để thêm ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        pickImageLauncher.launch(intent);
    }

    private void addPhotoFromGallery(Uri imageUri) {
        String photoName = "Ảnh " + (allPhotos.size() + 1);
        Photo photo = new Photo(photoName, imageUri);
        allPhotos.add(photo);

        if (!showingFavoritesOnly) {
            displayedPhotos.add(photo);
            adapter.notifyDataSetChanged();
        }

        saveCustomPhotos();
        Toast.makeText(this, "Đã thêm ảnh mới", Toast.LENGTH_SHORT).show();

        // Hiển thị ảnh vừa thêm nếu đây là ảnh đầu tiên
        if (allPhotos.size() == 1) {
            displayImage(0);
        }
    }

    private void toggleFavoritesView() {
        showingFavoritesOnly = !showingFavoritesOnly;

        if (showingFavoritesOnly) {
            displayedPhotos.clear();
            for (Photo photo : allPhotos) {
                if (photo.isFavorite()) {
                    displayedPhotos.add(photo);
                }
            }
            btnShowFavorites.setText(getString(R.string.btn_show_all));
            tvGridTitle.setText(getString(R.string.favorite_photos_title, displayedPhotos.size()));

            if (displayedPhotos.isEmpty()) {
                Toast.makeText(this, getString(R.string.no_favorites), Toast.LENGTH_SHORT).show();
            }
        } else {
            displayedPhotos.clear();
            displayedPhotos.addAll(allPhotos);
            btnShowFavorites.setText(getString(R.string.btn_show_favorites));
            tvGridTitle.setText(getString(R.string.all_photos_title));
        }

        adapter.notifyDataSetChanged();

        if (!displayedPhotos.isEmpty()) {
            currentImageIndex = 0;
            displayImage(0);
        }
    }

    private void saveRecentImage(int position) {
        if (position < displayedPhotos.size()) {
            Set<String> recentImages = sharedPreferences.getStringSet(KEY_RECENT_IMAGES, new HashSet<>());
            Set<String> updatedRecentImages = new HashSet<>(recentImages);
            updatedRecentImages.add(displayedPhotos.get(position).getName());

            if (updatedRecentImages.size() > 10) {
                String oldest = updatedRecentImages.iterator().next();
                updatedRecentImages.remove(oldest);
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(KEY_RECENT_IMAGES, updatedRecentImages);
            editor.apply();
        }
    }

    private void loadRecentImages() {
        Set<String> recentImages = sharedPreferences.getStringSet(KEY_RECENT_IMAGES, new HashSet<>());
        if (!recentImages.isEmpty()) {
            Toast.makeText(this, "Đã xem " + recentImages.size() + " ảnh gần đây", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFavorites() {
        Set<String> favorites = new HashSet<>();
        for (int i = 0; i < allPhotos.size(); i++) {
            if (allPhotos.get(i).isFavorite()) {
                favorites.add(String.valueOf(i));
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(KEY_FAVORITES, favorites);
        editor.apply();
    }

    private void loadFavorites() {
        Set<String> favorites = sharedPreferences.getStringSet(KEY_FAVORITES, new HashSet<>());
        for (String indexStr : favorites) {
            try {
                int index = Integer.parseInt(indexStr);
                if (index < allPhotos.size()) {
                    allPhotos.get(index).setFavorite(true);
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Lỗi parse favorite index: " + indexStr, e);
            }
        }
    }

    private void saveCustomPhotos() {
        // Lưu theo format: uri1|name1;uri2|name2;...
        StringBuilder sb = new StringBuilder();
        for (Photo photo : allPhotos) {
            if (photo.isFromGallery()) {
                if (sb.length() > 0) {
                    sb.append(";");
                }
                sb.append(photo.getImageUri().toString()).append("|").append(photo.getName());
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_CUSTOM_PHOTOS, sb.toString());
        editor.apply();
    }

    private void loadCustomPhotos() {
        String data = sharedPreferences.getString(KEY_CUSTOM_PHOTOS, "");
        if (!data.isEmpty()) {
            String[] photoEntries = data.split(";");
            for (String entry : photoEntries) {
                String[] parts = entry.split("\\|");
                if (parts.length == 2) {
                    try {
                        Uri uri = Uri.parse(parts[0]);
                        String name = parts[1];
                        Photo photo = new Photo(name, uri);
                        allPhotos.add(photo);
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi parse custom photo: " + entry, e);
                    }
                }
            }
        }
    }

    private void showEmptyGalleryMessage() {
        tvImageName.setText("Chưa có ảnh nào trong thư viện\n\nNhấn nút '➕ Thêm Ảnh' để bắt đầu");
        Toast.makeText(this, "Hãy thêm ảnh đầu tiên vào thư viện!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isSlideshowRunning) {
            stopSlideshow();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSlideshowRunning) {
            slideshowHandler.removeCallbacks(slideshowRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSlideshowRunning && slideshowRunnable != null) {
            slideshowHandler.postDelayed(slideshowRunnable, SLIDESHOW_DELAY);
        }
    }
}