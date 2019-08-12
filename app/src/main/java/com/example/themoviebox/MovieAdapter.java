package com.example.themoviebox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.themoviebox.Model.MovieResult;

import java.util.ArrayList;
import java.util.List;

import static com.example.themoviebox.MovieDBApi.BASE_URL;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    List<MovieResult> movieResultArrayList;
    Context context;

    public MovieAdapter(List<MovieResult> movieResultArrayList, Context context) {
        this.movieResultArrayList = movieResultArrayList;
        this.context = context;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title, userrating;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.title);
            userrating = itemView.findViewById(R.id.userrating);

            Glide.with(context)
                    .asGif()
                    .load(R.raw.loading)
                    .into(thumbnail);
        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_card, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {

        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + movieResultArrayList.get(position).getPosterPath())
                .centerCrop()
                .into(holder.thumbnail);
        holder.title.setText(movieResultArrayList.get(position).getTitle());
        holder.userrating.setText(movieResultArrayList.get(position).getVoteAverage() + "");
    }

    @Override
    public int getItemCount() {
        return movieResultArrayList.size();
    }

}
