package com.example.billsplitter.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.billsplitter.DisplayImage;
import com.example.billsplitter.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

import static com.example.billsplitter.MainActivity.getNameFromUserID;

//class for Adapter of recycler view
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<String> mPaidBy;
    private ArrayList<String> mItem;
    private ArrayList<String> mAmount;
    private ArrayList<String> mImage;
    private Context mcontext;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView expenseDesc;
        public TextView expensePayment;
        public MyViewHolder(View v) {
            super(v);
            expenseDesc = v.findViewById(R.id.item);
            expensePayment = v.findViewById(R.id.paid_by);
        }
    }

    public MyAdapter(ArrayList<String> PaidBy, ArrayList<String> Item, ArrayList<String> Amount, ArrayList<String> imageid,  Context context) {
       mPaidBy = PaidBy;
       mItem = Item;
       mAmount = Amount;
       mcontext = context;
       mImage = imageid;

    }

    //creating my views
    @Override
    public  MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //create a new view
        View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    //replace the contents of a view
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        String paid = mPaidBy.get(position);
        String Item = mItem.get(position);
        String Amount = mAmount.get(position);
        final String image = mImage.get(position);
        double dAmount = Double.parseDouble(Amount);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, DisplayImage.class);
                intent.putExtra("imageID", image);
                mcontext.startActivity(intent);
            }
        });


        System.out.println("Negative sign check" + Amount);

        SharedPreferences sp = mcontext.getSharedPreferences("Login", Context.MODE_PRIVATE);

        String UserID = sp.getString("UserId", null);

        holder.expenseDesc.setText(getNameFromUserID(UserID) + " added a new expense for " + Item);

        if (paid.equals(UserID))
        {
            holder.expensePayment.setText("You paid " + dAmount);
        }
        else {

            holder.expensePayment.setText("You owe " + dAmount + " to " + getNameFromUserID(paid));
        }

     System.out.println("position = " + position);
        System.out.println(mPaidBy.size());
    }

    //returns the size of the dataset
    @Override
    public int getItemCount() {
        return mPaidBy.size();
    }
}