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

import com.example.themoviebox.Model.TrailerResult;
import com.example.themoviebox.R;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    ArrayList<TrailerResult> trailerResultArrayList;
    Context context;

    public TrailerAdapter(ArrayList<TrailerResult> trailerResultArrayList, Context context) {
        this.trailerResultArrayList = trailerResultArrayList;
        this.context = context;
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        CardView card_view;

        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            card_view = itemView.findViewById(R.id.card_view);
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
        holder.title.setText(trailerResultArrayList.get(position).getName());
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(trailerResultArrayList.get(position).getSite()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trailerResultArrayList.size();
    }
}
