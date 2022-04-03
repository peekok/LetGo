package com.abdulrahman.letgo;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;

import com.abdulrahman.letgo.ui.ThreeTwoImageView;
import com.squareup.picasso.Picasso;

import java.util.Random;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.ButterKnife;

public class DetailActivity extends Activity {

    public static final String EXTRA_AUTHOR = "EXTRA_AUTHOR";

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
        price.setText(randomPrice + "USD");
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
    public void buy(View view) {
        // we will see about it.
    }
}
