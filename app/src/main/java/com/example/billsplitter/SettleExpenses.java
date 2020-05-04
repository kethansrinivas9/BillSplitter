package com.example.billsplitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//class for settling up the expenses
public class SettleExpenses extends AppCompatActivity {

    private TextView userName;
    private TextView userEmail;
    private TextView oweMoney;
    private Button settleButton;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_expenses);

        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        oweMoney = findViewById(R.id.oweMoney);
        settleButton = findViewById(R.id.settlebtn);


        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        final String user_email = sp.getString("UserEmail", null);
        final String my_id = sp.getString("UserId", null);
        final String name  = getIntent().getStringExtra("UserName");
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
                        for(DataSnapshot indi : unit.child("individual_expenses").getChildren()){
                            if(indi.getKey().equals(friend_id)){
                                amt = amt + Double.parseDouble(indi.getValue().toString());
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

        //onClick Listener for settle button
        settleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference = FirebaseDatabase.getInstance().getReference("expenses_data");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot unit : dataSnapshot.getChildren()){
                            if(unit.getKey().equals(my_id)){
                                for(DataSnapshot indi : unit.child("individual_expenses").getChildren()){
                                    if(indi.getKey().equals(friend_id)){
                                       // amt = amt + Double.parseDouble(indi.getValue().toString());
                                        databaseReference.child(unit.getKey()).child("individual_expenses").child(indi.getKey()).setValue("0.0");
                                    }
                                }
                            }
                        }
                        for (DataSnapshot unit : dataSnapshot.getChildren()){
                            if(unit.getKey().equals(friend_id)){
                                for(DataSnapshot indi : unit.child("individual_expenses").getChildren()){
                                    if(indi.getKey().equals(my_id)){
                                       // amt = amt + Double.parseDouble(indi.getValue().toString());
                                        databaseReference.child(unit.getKey()).child("individual_expenses").child(indi.getKey()).setValue("0.0");
                                    }
                                }
                            }
                        }

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
