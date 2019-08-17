package com.example.themoviebox.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.themoviebox.Model.ReviewResult;
import com.example.themoviebox.R;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    List<ReviewResult> reviewResultList;
    Context context;

    public ReviewAdapter(List<ReviewResult> reviewResultList, Context context) {
        this.reviewResultList = reviewResultList;
        this.context = context;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        CardView reviewCardview;
        TextView nameTextView, contentTextView, urlDescriptionTextView, urlTextView;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewCardview = itemView.findViewById(R.id.reviewCardview);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            urlDescriptionTextView = itemView.findViewById(R.id.urlDescriptionTextView);
            urlTextView = itemView.findViewById(R.id.urlTextView);

        }
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_card, null, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, final int position) {
        holder.reviewCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse(reviewResultList.get(position).getUrl()));
                context.startActivity(intent);
            }
        });
        holder.nameTextView.setText(reviewResultList.get(position).getAuthor());
        holder.contentTextView.setText(reviewResultList.get(position).getContent());
        holder.urlDescriptionTextView.setText("Read "+reviewResultList.get(position).getAuthor()+" full article: ");
        holder.urlTextView.setText(reviewResultList.get(position).getUrl());
    }

    @Override
    public int getItemCount() {
        return reviewResultList.size();
    }
}
