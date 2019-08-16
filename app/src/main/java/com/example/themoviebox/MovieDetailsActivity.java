package com.example.themoviebox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.themoviebox.Model.MovieResult;
import com.example.themoviebox.R;

public class MovieDetailsActivity extends AppCompatActivity {

    TextView title, userrating, releasedate, plotsynopsis, thumbnailUrll;
    RecyclerView trailer_recyclerView;
    ImageView thumbnail_image_header,backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_layout);


        title = findViewById(R.id.title);
        userrating = findViewById(R.id.userrating);
        releasedate = findViewById(R.id.releasedate);
        plotsynopsis = findViewById(R.id.plotsynopsis);
        thumbnailUrll = findViewById(R.id.thumbnailUrl);
        thumbnail_image_header = findViewById(R.id.thumbnail_image_header);
        trailer_recyclerView = findViewById(R.id.trailer_recyclerView);
        backButton = findViewById(R.id.backButton);


        MovieResult movieResult = getIntent().getExtras().getParcelable("MovieResult");

        title.setText(movieResult.getOriginalTitle());
        userrating.setText(movieResult.getVoteAverage() + "");
        releasedate.setText(movieResult.getReleaseDate());
        plotsynopsis.setText(movieResult.getOverview());
        Glide.with(MovieDetailsActivity.this)
                .load("https://image.tmdb.org/t/p/w500" + movieResult.getPosterPath())
                .centerCrop()
                .into(thumbnail_image_header);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MovieDetailsActivity.super.onBackPressed();
            }
        });

    }
}
