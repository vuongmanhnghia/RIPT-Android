package com.example.photo_gallary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Photo> photos;
    private OnFavoriteClickListener favoriteClickListener;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(int position, Photo photo);
    }

    public ImageAdapter(Context context, ArrayList<Photo> photos) {
        this.context = context;
        this.photos = photos;
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.favoriteClickListener = listener;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_item, parent, false);

            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.gridImageView);
            holder.nameTextView = convertView.findViewById(R.id.gridImageName);
            holder.favoriteButton = convertView.findViewById(R.id.btnFavorite);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Photo photo = photos.get(position);

        // Hiển thị ảnh
        if (photo.isFromGallery()) {
            holder.imageView.setImageURI(photo.getImageUri());
        } else {
            holder.imageView.setImageResource(photo.getDrawableId());
        }

        // Hiển thị tên
        holder.nameTextView.setText(photo.getName());

        // Hiển thị trạng thái yêu thích
        if (photo.isFavorite()) {
            holder.favoriteButton.setImageResource(android.R.drawable.star_big_on);
        } else {
            holder.favoriteButton.setImageResource(android.R.drawable.star_big_off);
        }

        // Xử lý click vào nút yêu thích
        holder.favoriteButton.setOnClickListener(v -> {
            if (favoriteClickListener != null) {
                favoriteClickListener.onFavoriteClick(position, photo);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        ImageView favoriteButton;
    }

    public void updateData(ArrayList<Photo> newPhotos) {
        this.photos = newPhotos;
        notifyDataSetChanged();
    }
}
