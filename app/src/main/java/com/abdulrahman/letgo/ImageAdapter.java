package com.abdulrahman.letgo;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;

    public ImageAdapter(Context c) {
        loadPhotos();
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public String getItem(int position) {
        return mThumbIds[position];
    }
    public void setItem(int position, String uri) {
        this.mThumbIds[position] = uri;
    }
    public long getItemId(int position) {
        return 0;
    }
    public void loadPhotos() {
        Log.d("loadPhotos[ImageAdapter]", "Hello Thereeee.");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        storageReference.listAll().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d("FirebaseStorage[ImageAdapter]", "There was an error");
            } else {
                int position = 0;
                for (StorageReference item : task.getResult().getItems()) {
                    int finalPosition = position;
                    item.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            setItem(finalPosition, task.getResult().toString());
                        }
                    });
                    position++;
                }
            }
        });
    }
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        String url = getItem(position);
        Picasso.with(mContext)
                .load(url)
                .placeholder(R.drawable.ic_load)
                .fit()
                .centerCrop().into(imageView);
        return imageView;
    }
    private String[] mThumbIds = new String[99];
}
