package com.tuna.uploadfirebase.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tuna.uploadfirebase.R;
import com.tuna.uploadfirebase.model.UpLoad;

import java.util.List;

public class UpLoadAdapter extends RecyclerView.Adapter<UpLoadAdapter.ViewHolder> {
    private Context context;
    private List<UpLoad> upLoadList;
    private AdapterListener adapterListener;

    public UpLoadAdapter(Context context, List<UpLoad> upLoadList) {
        this.context = context;
        this.upLoadList = upLoadList;
    }

    public UpLoadAdapter(Context context, List<UpLoad> upLoadList, AdapterListener adapterListener) {
        this.context = context;
        this.upLoadList = upLoadList;
        this.adapterListener = adapterListener;
    }

    public interface AdapterListener{
        void OnClick(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        UpLoad upLoad = upLoadList.get(position);
        holder.tvAddress.setText(upLoad.getTitle());
        holder.tvArea.setText(upLoad.getArearoom().toString());
        Glide.with(context).load(upLoad.getImages()).into(holder.imgHomestays);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterListener.OnClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return upLoadList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgHomestays;
        TextView tvAddress,tvArea;
        CardView cardView;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            imgHomestays = itemView.findViewById(R.id.imgHomestays);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvArea = itemView.findViewById(R.id.tvArea);
        }
    }
}
