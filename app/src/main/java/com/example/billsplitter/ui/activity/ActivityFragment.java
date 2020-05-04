package com.example.billsplitter.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billsplitter.R;

import java.util.ArrayList;
//class for activity fragment
public class ActivityFragment extends Fragment {

    private ActivityViewModel activityViewModel;
    private RecyclerView recycle_view;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<String> PaidBy;
    private ArrayList<String> Item;
    private ArrayList<String> Amount;
    private ArrayList<String> ImageID;
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        activityViewModel =
                ViewModelProviders.of(this).get(ActivityViewModel.class);
        View root = inflater.inflate(R.layout.fragment_activity, container, false);


        recycle_view = root.findViewById(R.id.recycler_view);
        recycle_view.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recycle_view.setLayoutManager(layoutManager);

        PaidBy = new ArrayList<>();
        Item = new ArrayList<>();
        Amount = new ArrayList<>();
        ImageID = new ArrayList<>();
        context = getActivity();


        activityViewModel.getPaidByList().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                PaidBy = strings;
                setAdapter();
            }
        });
        activityViewModel.getAmountList().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                Amount = strings;
                setAdapter();
            }
        });
        activityViewModel.getItemList().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                Item = strings;
                setAdapter();
            }
        });
        activityViewModel.getImageList().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                ImageID = strings;
                setAdapter();
            }
        });
        return root;
    }
    private void setAdapter(){

        mAdapter = new MyAdapter(PaidBy, Item, Amount, ImageID, context );
        recycle_view.setAdapter(mAdapter);
    }
}