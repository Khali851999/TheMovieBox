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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.themoviebox.API.MovieDBApi;
import com.example.themoviebox.Adapters.MovieAdapter;
import com.example.themoviebox.Adapters.ReviewAdapter;
import com.example.themoviebox.Adapters.TrailerAdapter;
import com.example.themoviebox.BuildConfig;
import com.example.themoviebox.Model.Movie;
import com.example.themoviebox.Model.MovieResult;
import com.example.themoviebox.Model.Review;
import com.example.themoviebox.Model.ReviewResult;
import com.example.themoviebox.Model.Trailer;
import com.example.themoviebox.Model.TrailerResult;
import com.example.themoviebox.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    TextView title, userrating, releasedate, plotsynopsis, thumbnailUrll, reviewTextView, trailerTextView, similarmovieTextview;
    RecyclerView trailer_recyclerView, similarmovies_recyclerView, moviereview_recyclerView;
    ImageView thumbnail_image_header, backButton, shareButton;
    MovieResult movieResult;
    List<TrailerResult> trailerResultList = new ArrayList<>();
    List<MovieResult> movieResultList = new ArrayList<>();
    List<ReviewResult> reviewResultList = new ArrayList<>();
    private static final String TAG = "MovieDetailsActivity";
    Date date;
    String dateString;

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
        moviereview_recyclerView = findViewById(R.id.moviereview_recyclerView);
        reviewTextView = findViewById(R.id.reviewTextView);
        trailerTextView = findViewById(R.id.trailerTextView);
        similarmovieTextview = findViewById(R.id.similarmovieTextvIEW);


        movieResult = getIntent().getExtras().getParcelable("MovieResult");

        title.setText(movieResult.getOriginalTitle());
        userrating.setText(movieResult.getVoteAverage() + "");

        //todo: ++++++++++++++++
        try {
            date = new SimpleDateFormat("yyyy-mm-dd").parse(movieResult.getReleaseDate());
            dateString = new SimpleDateFormat("dd-MMM-yyyy").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        releasedate.setText("Release Date: " + dateString + ":" + movieResult.getReleaseDate());
        plotsynopsis.setText(movieResult.getOverview());
        Glide.with(MovieDetailsActivity.this)
                .load("https://image.tmdb.org/t/p/w500/" + movieResult.getPosterPath())
                .fitCenter()
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


    }

    public void LoadTrailers() {

        trailerResultList.clear();
        Call<Trailer> trailerResultCall = MovieDBApi.getMovieService().getTrailerDetails(movieResult.getId());
        trailerResultCall.enqueue(new Callback<Trailer>() {
            @Override
            public void onResponse(Call<Trailer> call, Response<Trailer> response) {
                Trailer trailer = response.body();
                if (trailer == null) {
                    try {
                        Log.e(TAG, "onResponse: " + response.errorBody().string());
                        Toast.makeText(MovieDetailsActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    trailerResultList = trailer.getResults();
                    if (trailerResultList.isEmpty()) {
                        trailer_recyclerView.setVisibility(View.GONE);
                        trailerTextView.setVisibility(View.GONE);
                    } else {
                        TrailerAdapter trailerAdapter = new TrailerAdapter(trailerResultList, MovieDetailsActivity.this);
                        trailer_recyclerView.setAdapter(trailerAdapter);
                        trailer_recyclerView.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this));
                        trailer_recyclerView.setItemAnimator(new DefaultItemAnimator());
                    }
                }
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
                if (movie == null) {
                    try {
                        Log.e(TAG, "onResponse: " + response.errorBody().string());
                        Toast.makeText(MovieDetailsActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    movieResultList = movie.getMovieResults();
                    if (movieResultList.isEmpty()) {
                        similarmovies_recyclerView.setVisibility(View.GONE);
                        trailerTextView.setVisibility(View.GONE);
                    } else {
                        MovieAdapter adapter = new MovieAdapter(movieResultList, MovieDetailsActivity.this);
                        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MovieDetailsActivity.this, 1, RecyclerView.HORIZONTAL, true);
//                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MovieDetailsActivity.this, RecyclerView.HORIZONTAL, true);
                        similarmovies_recyclerView.setAdapter(adapter);
                        similarmovies_recyclerView.setLayoutManager(layoutManager);
                        similarmovies_recyclerView.setItemAnimator(new DefaultItemAnimator());

                    }
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {

                Log.e(TAG, "onFailure: " + t.getMessage() + "\n" + t.getCause());
                Toast.makeText(MovieDetailsActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void LoadMovieReviews(int movie_id) {
        final Call<Review> reviewCall = MovieDBApi.getMovieService().getReviews(movie_id);
        reviewCall.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                Review review = response.body();
                if (review == null) {
                    try {
                        Log.e(TAG, "onResponse: " + response.errorBody().string());

                        moviereview_recyclerView.setVisibility(View.GONE);
                        reviewTextView.setVisibility(View.GONE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    reviewResultList = review.getResults();
                    if (reviewResultList.isEmpty()) {
                        moviereview_recyclerView.setVisibility(View.GONE);
                        reviewTextView.setVisibility(View.GONE);
                    } else {
                        ReviewAdapter reviewAdapter = new ReviewAdapter(reviewResultList, MovieDetailsActivity.this);
                        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MovieDetailsActivity.this, 1, RecyclerView.HORIZONTAL, true);
                        moviereview_recyclerView.setLayoutManager(layoutManager);
                        moviereview_recyclerView.setAdapter(reviewAdapter);
                        moviereview_recyclerView.setItemAnimator(new DefaultItemAnimator());

                    }
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {

                Log.e(TAG, "onFailure: " + t.getMessage() + "\n" + t.getCause());
                Toast.makeText(MovieDetailsActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        LoadTrailers();
        LoadSimilarMovies(movieResult.getId());
        LoadMovieReviews(movieResult.getId());
    }
}
