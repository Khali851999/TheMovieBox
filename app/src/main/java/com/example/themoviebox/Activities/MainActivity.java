package com.example.themoviebox.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.themoviebox.API.MovieDBApi;
import com.example.themoviebox.Adapters.MovieAdapter;
import com.example.themoviebox.Model.Movie;
import com.example.themoviebox.Model.MovieResult;
import com.example.themoviebox.R;

import net.vrgsoft.layoutmanager.RollingLayoutManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.themoviebox.API.MovieDBApi.API_KEY;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView.LayoutManager layoutManager;
    private static final String TAG = "MainActivity";
    ProgressBar progressBar;
    List<MovieResult> movieResultArrayList = new ArrayList<>();
    int page_no;
    boolean isScrolling = false;
    int current_item, total_items, scrolledout_items;
    MovieAdapter movieAdapter;
    Button scrollToTop;
    ImageView loading_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        page_no = 2;

        progressBar = findViewById(R.id.progressbar);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_orange_light);
        scrollToTop = findViewById(R.id.scrollToTop);
        loading_image = findViewById(R.id.loading_image);

        Glide.with(MainActivity.this)
                .asGif()
                .load(R.raw.loading)
                .into(loading_image);

        scrollToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
                scrollToTop.setVisibility(View.GONE);
            }
        });

        if (MainActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//            layoutManager = new GridLayoutManager(MainActivity.this, 2);
            recyclerView.setLayoutManager(layoutManager);

        } else {
//            layoutManager = new GridLayoutManager(MainActivity.this, 4);
            recyclerView.setLayoutManager(layoutManager);

        }

        layoutManager = new RollingLayoutManager(MainActivity.this);


    }

    private void getData() {

        movieResultArrayList.clear();
        Call<Movie> movieDetails = MovieDBApi.getMovieService().getMovieDetails();
        movieDetails.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                Movie movie = response.body();
                if (movie == null) {
                    try {
                        Log.e(TAG, "onResponse:getData: " + response.errorBody().string());
                        Toast.makeText(MainActivity.this, "ErrorOccured", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    movieResultArrayList = movie.getMovieResults();
                    Log.e(TAG, "onResponse: " + movieResultArrayList.size());
                    movieAdapter = new MovieAdapter(movieResultArrayList, MainActivity.this);
                    recyclerView.setAdapter(movieAdapter);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    loading_image.setVisibility(View.GONE);

                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            movieAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(movieAdapter);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.smoothScrollToPosition(0);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error occurred !!!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });


    }

    public void LoadMoreData(int page_no) {
        Log.e(TAG, "143LoadMoreData: " + page_no);

        Call<Movie> movieDetails = MovieDBApi.getMovieService().getMoreMovieDetails(API_KEY, page_no);
        movieDetails.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                Movie movie = response.body();

                if (movie == null) {
                    Log.e(TAG, "onResponse:LoadMoreData: " + response.errorBody().toString());
                    Toast.makeText(MainActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();

                } else {
                    movieResultArrayList.addAll(movie.getMovieResults());
                    movieAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.INVISIBLE);

                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error occurred !!!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                isScrolling = true;

                if (page_no >= 2) {
                    scrollToTop.setVisibility(View.VISIBLE);
                } else {
                    scrollToTop.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                total_items = layoutManager.getItemCount();
                current_item = layoutManager.getChildCount();
                scrolledout_items = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                if (isScrolling && (current_item + scrolledout_items == total_items)) {


                    progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            page_no += 1;
                            LoadMoreData(page_no);
                            Log.e(TAG, "91run: " + page_no);
                        }
                    }, 1000);
                }
            }
        });

        getData();
    }
}
