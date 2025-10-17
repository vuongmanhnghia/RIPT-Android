package com.example.photo_gallary;

import android.net.Uri;

public class Photo {
    private String name;
    private Uri imageUri;
    private Integer drawableId;
    private boolean isFavorite;
    private boolean isFromGallery;

    // Constructor cho ảnh từ drawable
    public Photo(String name, Integer drawableId) {
        this.name = name;
        this.drawableId = drawableId;
        this.isFavorite = false;
        this.isFromGallery = false;
    }

    // Constructor cho ảnh từ gallery
    public Photo(String name, Uri imageUri) {
        this.name = name;
        this.imageUri = imageUri;
        this.isFavorite = false;
        this.isFromGallery = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Integer getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(Integer drawableId) {
        this.drawableId = drawableId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isFromGallery() {
        return isFromGallery;
    }

    public void setFromGallery(boolean fromGallery) {
        isFromGallery = fromGallery;
    }
}

