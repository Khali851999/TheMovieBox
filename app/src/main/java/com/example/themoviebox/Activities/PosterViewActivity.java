package com.example.themoviebox.Activities;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.themoviebox.R;

public class PosterViewActivity extends AppCompatActivity {

    ImageView imageView, backButton;
    String ImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_poster_view);

        ImageUrl = getIntent().getStringExtra("ImageUrl");

        imageView = findViewById(R.id.imageView);
        backButton = findViewById(R.id.backButton);
        Glide.with(PosterViewActivity.this)
                .load(ImageUrl)
                .into(imageView);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PosterViewActivity.super.onBackPressed();
            }
        });


    }

}
