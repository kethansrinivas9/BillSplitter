package com.example.billsplitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//class for settle up expenses for group
public class GroupActivitySettleExpense extends AppCompatActivity {

    //declaring variables
    private TextView userName;
    private TextView userEmail;
    private TextView oweMoney;
    private Button settleButton;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settle_expense);

        userName = findViewById(R.id.groupuserName);
        userEmail = findViewById(R.id.groupuserEmail);
        oweMoney = findViewById(R.id.groupoweMoney);
        settleButton = findViewById(R.id.groupsettlebtn);
        final String group  = getIntent().getStringExtra("groupName");



        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        final String user_email = sp.getString("UserEmail", null);
        final String my_id = sp.getString("UserId", null);
        final String name  = getIntent().getStringExtra("UserName");
        final ArrayList<String> allMembers = getIntent().getStringArrayListExtra("memberID");
        final String friend_id = MainActivity.getIdFromUserName(name);
        userName.setText(name);

        //getting database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot unit:dataSnapshot.getChildren()){
                    if(unit.getKey().equals(friend_id)){
                        userEmail.setText(unit.child("email").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference("expenses_data");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double amt = 0.0;
                for (DataSnapshot unit : dataSnapshot.getChildren()){
                    if(unit.getKey().equals(my_id)){
                        for(DataSnapshot indi : unit.child("group_expenses").getChildren()){
                            if(indi.getKey().equals(group)){
                                for(DataSnapshot user:indi.getChildren()){
                                    if(user.getKey().equals(friend_id)){
                                        amt = amt + Double.parseDouble(String.valueOf(user.getValue()));
                                    }
                                }
                            }
                        }
                    }
                }
                if(amt == 0.0)
                    oweMoney.setText("You are all settled up with "+name);
                else if (amt < 0.0)
                    oweMoney.setText("You owe $"+Math.abs(amt) +" to "+name);
                else
                    oweMoney.setText("You get back $"+Math.abs(amt) +" from "+name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //OnClick Listener for settle Button
        settleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference = FirebaseDatabase.getInstance().getReference("expenses_data");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        databaseReference.child(my_id).child("group_expenses").child(group).child(friend_id).setValue("0.0");
                        databaseReference.child(friend_id).child("group_expenses").child(group).child(my_id).setValue("0.0");

                        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                        startActivity(intent);
                        databaseReference.removeEventListener(this);
                 }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });



            }

        });




    }
}
