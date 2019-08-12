package com.example.themoviebox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.themoviebox.Model.Movie;
import com.example.themoviebox.Model.MovieResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView.LayoutManager layoutManager;
    private static final String TAG = "MainActivity";
    List<MovieResult> movieResultArrayList = new ArrayList<>();
    int page_no;
    boolean isScrolling = false;
    int current_item, total_items, scrolledout_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_orange_light);


        if (MainActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(MainActivity.this, 2);
            recyclerView.setLayoutManager(layoutManager);

        } else {
            layoutManager = new GridLayoutManager(MainActivity.this, 4);
            recyclerView.setLayoutManager(layoutManager);

        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                isScrolling = true;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                total_items = layoutManager.getItemCount();
                current_item = layoutManager.getChildCount();
//                scrolledout_items = layoutMan
                if (isScrolling ){}
            }
        });

        getData(page_no);


    }

    private void getData(int page_no) {

        Call<Movie> movieDetails = MovieDBApi.getMovieService().getMovieDetails();
        movieDetails.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                Movie movie = response.body();
                Toast.makeText(MainActivity.this, "Data Loaded", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onResponse: " + movie.getTotalResults() + ":" + movie.getTotalPages());
                Log.e(TAG, "onResponse: " + response.isSuccessful());
                Log.e(TAG, "+++++++++++++++onResponse: " + movie.getMovieResults().size());

                final MovieAdapter movieAdapter = new MovieAdapter(movie.getMovieResults(), MainActivity.this);
                recyclerView.setAdapter(movieAdapter);
                recyclerView.setLayoutManager(layoutManager);

                Log.e(TAG, "++++++++++onResponse: " + layoutManager.getItemCount());

                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        movieAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(movieAdapter);
                        recyclerView.setLayoutManager(layoutManager);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error occurred !!!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });


    }
}
