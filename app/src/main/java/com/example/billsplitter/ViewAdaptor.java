package com.example.billsplitter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//class for adaptor view which is used for recycler view
public class ViewAdaptor extends RecyclerView.Adapter<ViewAdaptor.ViewHolder>{

    private ArrayList<Card> cardArrayList;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    //binding to view holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Card card = cardArrayList.get(position);

        holder.imageView.setImageResource(card.getImg());
        holder.textView.setText(card.getText());
    }

    @Override
    public int getItemCount() {
        return cardArrayList.size();
    }

    public ViewAdaptor(ArrayList<Card> cardList) {
        cardArrayList = cardList;
    }

    public static class ViewHolder  extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.icon);
            textView = itemView.findViewById(R.id.name);
        }
    }
}
