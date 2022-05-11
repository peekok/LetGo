package com.abdulrahman.letgo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.abdulrahman.letgo.ui.ThreeTwoImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Random;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.ButterKnife;

public class DetailActivity extends Activity {
    public static final String EXTRA_AUTHOR = "EXTRA_AUTHOR";
    public int userMoney = 0;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.photo) ThreeTwoImageView imageView;
    @Bind(R.id.author) TextView author;
    @Bind(R.id.price) TextView price;
    @BindInt(R.integer.detail_desc_slide_duration) int slideDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        imageView = findViewById(R.id.photo);
        author = findViewById(R.id.author);
        toolbar = findViewById(R.id.toolbar);
        price = findViewById(R.id.price);
        ButterKnife.bind(this);
        Random random = new Random();
        int randomPrice = random.nextInt(900) + 100;
        price.setText(randomPrice+"");
        System.out.println(getIntent().getStringExtra(EXTRA_AUTHOR));
        Picasso.with(this)
                .load(getIntent().getData())
                .placeholder(R.color.placeholder)
                .into(imageView);
        author.setText("—" + getIntent().getStringExtra(EXTRA_AUTHOR));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide(Gravity.BOTTOM);
            slide.addTarget(R.id.description);
            slide.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator
                    .linear_out_slow_in));
            slide.setDuration(slideDuration);
            getWindow().setEnterTransition(slide);
        }
    }
    public void setUserMoney(int userMoney) {
        this.userMoney = userMoney;
    }
    public int getUserMoney() {
        return userMoney;
    }

    public void buy(View view) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String uid = mAuth.getCurrentUser().getUid().toString();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.getReference("Users/" + uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        System.out.println("I am here");
                        setUserMoney(Integer.parseInt(task.getResult().child("userMoney").getValue().toString()));
                        Log.d("firebase", "Data Imported");
                        System.out.println(getUserMoney());
                        if (getUserMoney() < Integer.parseInt(price.getText().toString())) {
                            Toast.makeText(DetailActivity.this, R.string.notEnoughMoney, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(DetailActivity.this, TopUpPage.class));
                            return;
                        } else {
                            int currentMoney = getUserMoney() - Integer.parseInt(price.getText().toString());
                            database.getReference("Users/" + uid)
                                    .child("userMoney").setValue(currentMoney);
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference(uid);
                            imageView = findViewById(R.id.photo);
                            imageView.setDrawingCacheEnabled(true);
                            imageView.buildDrawingCache();
                            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();
                            Random random = new Random();
                            storageRef.child(uid + random.nextInt(900) + 100).putBytes(data);
                            Toast.makeText(DetailActivity.this, "This photo is added to your library", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }
            });
        } else {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }
    }
}
