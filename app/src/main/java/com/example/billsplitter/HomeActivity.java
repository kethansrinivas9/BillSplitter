package com.example.billsplitter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.billsplitter.MainActivity.getNameFromUserID;

//class for Home Activity
public class HomeActivity extends AppCompatActivity{

    //declaring variables
    private AppBarConfiguration mAppBarConfiguration;
    private DatabaseReference UserTable;
    private DatabaseReference ExpensesTable;

    private String userID;

    private ArrayList<String> friendUserIDList;
    private DatabaseReference FriendsAndGroups;
    private ArrayList<String> friendsAndGroupsList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        //fetching friends and group
        friendsAndGroupsList = fetchFriendsAndGroup();


        //System.out.println("asdalkshd" + friendsAndGroupsList.size());
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                 R.id.nav_home, R.id.nav_activity, R.id.nav_contact_us)
                .setDrawerLayout(drawer)
                .build();

        //navigation controller for UI
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        NavigationUI.setupWithNavController(navigationView, navController);

        View headerLayout = navigationView.getHeaderView(0);

        TextView txt_email =  headerLayout.findViewById(R.id.user_email);
        TextView txt_username =  headerLayout.findViewById(R.id.user_name);


        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);

        String user_name = sp.getString("UserName", null);
        String user_email = sp.getString("UserEmail", null);
        userID = sp.getString("UserId", null);


        txt_email.setText(user_email);
        txt_username.setText(userID);
        Button logOut = findViewById(R.id.logout);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), NewExpense.class);
                intent.putStringArrayListExtra("friends", friendsAndGroupsList);
                startActivity(intent);
            }
        });

        //setting OnClick Listener for logout button
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance()
                        .signOut(getApplicationContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // user is now signed out
                                SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                                SharedPreferences.Editor ed = sp.edit();
                                ed.putString("UserName", "");
                                ed.putString("UserEmail", "");
                                ed.putString("UserId", "");
                                ed.apply();

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        });
                Toast.makeText(getApplicationContext(), "Log out", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        fetchFriends();
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_new_group:
            {
                Intent intent = new Intent(this, NewGroup.class);
                intent.putStringArrayListExtra("friends", friendUserIDList);
                startActivity(intent);
                return true;

            }
            case R.id.add_new_friend:
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Enter the username");
                final Context context = builder.getContext();
                final LayoutInflater inflater = LayoutInflater.from(context);
                final View view = inflater.inflate(R.layout.add_new_friend, null, false);
//                LayoutInflater inflater = (HomeActivity.this).getLayoutInflater();
//                View view = inflater.inflate(R.layout.add_new_friend, null);
                final EditText editText = view.findViewById(R.id.newFriend);
                //builder.setView(edittext);

                builder.setPositiveButton("Add Friend", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String name = editText.getText().toString();

                        if (getNameFromUserID(name).equals("")){
                            Toast.makeText(getApplicationContext(), "Please add a valid username", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            addFriendToDb(name);
                            addFriendExpensestoDb(name);
                            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                builder.setView(view);
                AlertDialog mDialog = builder.create();
                mDialog.show();
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //method for adding friend to database
    private boolean addFriendToDb(final String friendID) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        UserTable = database.getReference("/users");

        UserTable.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot unit : dataSnapshot.getChildren()) {
                    if (unit.getKey().equals(userID)) {
                        UserTable.child(userID).child("Friends").child(friendID).setValue("true");
                    } else if (unit.getKey().equals(friendID)) {
                        UserTable.child(friendID).child("Friends").child(userID).setValue("true");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return true;
    }


    //method for adding friend expenses to database
    private boolean addFriendExpensestoDb(final String friendID) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ExpensesTable = database.getReference("/expenses_data");

        ExpensesTable.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot unit : dataSnapshot.getChildren()) {
                    if (unit.getKey().equals(userID)) {
                        ExpensesTable.child(userID).child("individual_expenses").child(friendID).setValue("0.0");
                    } else if (unit.getKey().equals(friendID)) {
                        ExpensesTable.child(friendID).child("individual_expenses").child(userID).setValue("0.0");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return true;
    }

    //method for fetching friends
    private void fetchFriends() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        UserTable = database.getReference("/users");
        friendUserIDList = new ArrayList<>();

        UserTable.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot unit : dataSnapshot.getChildren()) {
                    if (unit.getKey().equals(userID)){
                        for (DataSnapshot friend : unit.child("Friends").getChildren()){
                            friendUserIDList.add(friend.getKey());

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //method for fetching friends and group
    private ArrayList<String> fetchFriendsAndGroup() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        FriendsAndGroups = database.getReference("/expenses_data");
        final ArrayList<String> friendsAndGroupsList = new ArrayList<>();

        System.out.println(userID);

        FriendsAndGroups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot unit : dataSnapshot.getChildren()) {
                    if (unit.getKey().equals(userID)){
                        for (DataSnapshot group : unit.child("group_expenses").getChildren()){
                            friendsAndGroupsList.add(group.getKey());
                            System.out.println(group.getKey());
                        }
                        for (DataSnapshot friend : unit.child("individual_expenses").getChildren()){
                            friendsAndGroupsList.add(friend.getKey());
                            System.out.println(friend.getKey());
                            System.out.println("*******");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return friendsAndGroupsList;
    }





}
