package com.example.billsplitter.ui.home;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.billsplitter.ui.database.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.example.billsplitter.MainActivity.getNameFromUserID;

import static com.facebook.FacebookSdk.getApplicationContext;

//class for Home View Model
public class HomeViewModel extends ViewModel {

    private MutableLiveData<ArrayList<String>> mfriendsList;
    private MutableLiveData<ArrayList<String>> mfriendsAmountList;
    private MutableLiveData<ArrayList<String>> mgroupsAmountList;
    private MutableLiveData<ArrayList<String>> mGroupsList;
    private ArrayList<String> friends;
    private ArrayList<String> groups;
    private ArrayList<String> amountFriends;
    private ArrayList<String> amountGroups;
    private Context context;
    private String UserID;

    public HomeViewModel() {
        mfriendsList = new MutableLiveData<>();
        mGroupsList = new MutableLiveData<>();
        mfriendsAmountList = new MutableLiveData<>();
        mgroupsAmountList = new MutableLiveData<>();
        context = getApplicationContext();
        setFriendsList();
        setGroupList();

    }


    public LiveData<ArrayList<String>> getFriendsList(){
        return mfriendsList;
    }

    public LiveData<ArrayList<String>> getFriendsAmountList(){
        return mfriendsAmountList;
    }

    public LiveData<ArrayList<String>> getGroupsList(){
        return mGroupsList;
    }
    public LiveData<ArrayList<String>> getGroupsAmountList(){
        return mgroupsAmountList;
    }


    /*
    * This method is used to fetch the friends list from the database and set them
    * to the arraylist to use later
    * */
    private void setFriendsList(){

        friends = new ArrayList<>();
        amountFriends = new ArrayList<>();

        //adding reference to database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();


        SharedPreferences sp = context.getSharedPreferences("Login", MODE_PRIVATE);
        final String userID = sp.getString("UserId", null);
        System.out.println(userID);
        DatabaseReference ref = database.getReference("expenses_data/");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friends.clear();
                amountFriends.clear();
                for (DataSnapshot unit : dataSnapshot.getChildren()){
                    if(unit.getKey().equals(userID)){
                        System.out.println(unit.child("individual_expenses"));

                        for(DataSnapshot child : unit.child("individual_expenses").getChildren()){
                            if (!child.getKey().equals("isEmpty")){
                                friends.add(getNameFromUserID(child.getKey()));
                                amountFriends.add(child.getValue().toString());
                            }
                        }
                    }
                }
                mfriendsList.setValue(friends);
                mfriendsAmountList.setValue(amountFriends);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    //setting groups list
    public void setGroupList(){

        groups = new ArrayList<>();
        amountGroups = new ArrayList<>();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        SharedPreferences sp = context.getSharedPreferences("Login", MODE_PRIVATE);
        final String userID = sp.getString("UserId", null);
        DatabaseReference ref = database.getReference("expenses_data/");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groups.clear();
                amountGroups.clear();
                for (DataSnapshot unit : dataSnapshot.getChildren()){
                    if(unit.getKey().equals(userID)){

                        for(DataSnapshot child : unit.child("group_expenses").getChildren()){

                            if (!child.getKey().equals("isEmpty")){
                                groups.add(child.getKey());

                                Double dTotal = 0.0;

                                for (DataSnapshot members : child.getChildren()){
                                    String value = members.getValue().toString();
                                    Double dValue = Double.parseDouble(value);
                                    dTotal += dValue;
                                }
                                amountGroups.add(dTotal.toString());
                            }
                        }
                    }
                }
                mGroupsList.setValue(groups);
                mgroupsAmountList.setValue(amountGroups);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }


}

