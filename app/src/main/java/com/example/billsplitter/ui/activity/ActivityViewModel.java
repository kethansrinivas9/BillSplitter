package com.example.billsplitter.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ActivityViewModel extends ViewModel {

    private MutableLiveData<ArrayList<String>> PaidByList;
    private MutableLiveData<ArrayList<String>> ItemList;
    private MutableLiveData<ArrayList<String>> AmountList;
    private MutableLiveData<ArrayList<String>> ImageList;
    private ArrayList<String> PaidBy;
    private ArrayList<String> Item;
    private ArrayList<String> Amount;
    private ArrayList<String> ImageID;
    private Context context;

    public ActivityViewModel() {
       PaidByList = new MutableLiveData<>();
        ItemList = new MutableLiveData<>();
        AmountList = new MutableLiveData<>();
        ImageList = new MutableLiveData<>();
        ImageID = new ArrayList<>();
        PaidBy = new ArrayList<>();
        Item = new ArrayList<>();
        Amount = new ArrayList<>();
        context = getApplicationContext();
        getActivity();

    }


    public LiveData<ArrayList<String>> getPaidByList() {

        return PaidByList;
    }
    public LiveData<ArrayList<String>> getItemList() {

        return ItemList;
    }
    public LiveData<ArrayList<String>> getAmountList() {

        return AmountList;
    }
    public LiveData<ArrayList<String>> getImageList() {

        return ImageList;
    }

    private void getActivity(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        SharedPreferences sp = context.getSharedPreferences("Login", MODE_PRIVATE);
        final String userID = sp.getString("UserId", null);
        DatabaseReference ref = database.getReference("activity/");

        //getting Value Event Listener
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot unit : dataSnapshot.getChildren()){
                    if (unit.getKey().equals(userID)){
                        for (DataSnapshot expenses : unit.getChildren()){
                            if (!expenses.getKey().equals("isEmpty")){
                                PaidBy.add(String.valueOf(expenses.child("paidBy").getValue()));
                                Item.add(String.valueOf(expenses.child("desc").getValue()));
                                Amount.add(String.valueOf(expenses.child("amount").getValue()));
                                ImageID.add(String.valueOf(expenses.child("imageId").getValue()));
                                System.out.print(expenses.child("imageId").getValue());
                            }
                        }
                    }


                }
                PaidByList.setValue(PaidBy);
                ItemList.setValue(Item);
                AmountList.setValue(Amount);
                ImageList.setValue(ImageID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}