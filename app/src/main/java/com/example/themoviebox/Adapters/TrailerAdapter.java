package com.example.themoviebox.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.themoviebox.Model.TrailerResult;
import com.example.themoviebox.R;

import java.util.ArrayList;
import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    List<TrailerResult> trailerResultList;
    Context context;

    public TrailerAdapter(List<TrailerResult> trailerResultList, Context context) {
        this.trailerResultList = trailerResultList;
        this.context = context;
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        CardView card_view;
        ImageView imageView;

        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            card_view = itemView.findViewById(R.id.card_view);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_row, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, final int position) {
        holder.title.setText(trailerResultList.get(position).getName());
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=" + trailerResultList.get(position).getKey()));
                context.startActivity(intent);
            }
        });
        Glide.with(context)
                .load(R.drawable.youtube)
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return trailerResultList.size();
    }
}
