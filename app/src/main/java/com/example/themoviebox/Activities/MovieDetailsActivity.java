package com.example.themoviebox.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.themoviebox.API.MovieDBApi;
import com.example.themoviebox.Adapters.MovieAdapter;
import com.example.themoviebox.Adapters.TrailerAdapter;
import com.example.themoviebox.BuildConfig;
import com.example.themoviebox.Model.Movie;
import com.example.themoviebox.Model.MovieResult;
import com.example.themoviebox.Model.Trailer;
import com.example.themoviebox.Model.TrailerResult;
import com.example.themoviebox.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    TextView title, userrating, releasedate, plotsynopsis, thumbnailUrll;
    RecyclerView trailer_recyclerView, similarmovies_recyclerView;
    ImageView thumbnail_image_header, backButton, shareButton;
    MovieResult movieResult;
    List<TrailerResult> trailerResultList = new ArrayList<>();
    List<MovieResult> movieResultList = new ArrayList<>();
    private static final String TAG = "MovieDetailsActivity";

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
        shareButton = findViewById(R.id.shareButton);
        similarmovies_recyclerView = findViewById(R.id.similarmovies_recyclerView);


        movieResult = getIntent().getExtras().getParcelable("MovieResult");

        title.setText(movieResult.getOriginalTitle());
        userrating.setText(movieResult.getVoteAverage() + "");
        releasedate.setText(movieResult.getReleaseDate());
        plotsynopsis.setText(movieResult.getOverview());
        Glide.with(MovieDetailsActivity.this)
                .load("https://image.tmdb.org/t/p/w500/" + movieResult.getPosterPath())
                .centerCrop()
                .into(thumbnail_image_header);
        thumbnail_image_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MovieDetailsActivity.this, PosterViewActivity.class);
                intent.putExtra("ImageUrl", "https://image.tmdb.org/t/p/w500/" + movieResult.getPosterPath());
                startActivity(intent);
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MovieDetailsActivity.super.onBackPressed();
            }
        });

        shareButton.setVisibility(View.VISIBLE);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareButton.setVisibility(View.GONE);

                try {

                    // create bitmap screen capture

                    thumbnail_image_header = findViewById(R.id.thumbnail_image_header);
                    thumbnail_image_header.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(thumbnail_image_header.getDrawingCache());
                    thumbnail_image_header.setDrawingCacheEnabled(false);

                    String mPath = Environment.getExternalStorageDirectory().toString() + "/" + "MoviePoster" + ".jpg";

                    File imageFile = new File(mPath);
                    FileOutputStream outputStream = new FileOutputStream(imageFile);
                    int quality = 100;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/*");
                    Uri uri = FileProvider.getUriForFile(MovieDetailsActivity.this, BuildConfig.APPLICATION_ID + ".provider", imageFile);
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    share.putExtra(Intent.EXTRA_TEXT, movieResult.getOriginalTitle() + "\n" +
                            "Rating: " + movieResult.getVoteAverage() + "\n" +
                            "Plot: " + movieResult.getOverview());
                    startActivity(Intent.createChooser(share, "Share via"));

                } catch (Throwable e) {
                    e.printStackTrace();
                }


            }
        });

        LoadTrailers();
        LoadSimilarMovies(movieResult.getId());

    }

    public void LoadTrailers() {

        trailerResultList.clear();
        Call<Trailer> trailerResultCall = MovieDBApi.getMovieService().getTrailerDetails(movieResult.getId());
        trailerResultCall.enqueue(new Callback<Trailer>() {
            @Override
            public void onResponse(Call<Trailer> call, Response<Trailer> response) {
                Trailer trailer = response.body();
                Log.e(TAG, "onResponse: " + trailer.getId());
                trailerResultList = trailer.getResults();
                Log.e(TAG, "onResponse: " + trailerResultList.get(0).getName() + "\n" + trailerResultList.get(0).getSite());
                TrailerAdapter trailerAdapter = new TrailerAdapter(trailerResultList, MovieDetailsActivity.this);
                trailer_recyclerView.setAdapter(trailerAdapter);
                trailer_recyclerView.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this));
                trailer_recyclerView.setItemAnimator(new DefaultItemAnimator());
            }

            @Override
            public void onFailure(Call<Trailer> call, Throwable t) {

            }
        });
    }

    public void LoadSimilarMovies(int movie_id) {
        Call<Movie> movieResultCall = MovieDBApi.getMovieService().getSimilarMovies(movie_id);
        movieResultCall.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                Movie movie = response.body();
                movieResultList = movie.getMovieResults();
                MovieAdapter adapter = new MovieAdapter(movieResultList, MovieDetailsActivity.this);
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MovieDetailsActivity.this, 1, RecyclerView.HORIZONTAL, true);
//                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MovieDetailsActivity.this, RecyclerView.HORIZONTAL, true);
                similarmovies_recyclerView.setAdapter(adapter);
                similarmovies_recyclerView.setLayoutManager(layoutManager);
                similarmovies_recyclerView.setItemAnimator(new DefaultItemAnimator());

            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {

            }
        });
    }
}
