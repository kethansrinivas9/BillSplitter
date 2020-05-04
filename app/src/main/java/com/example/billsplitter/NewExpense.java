package com.example.billsplitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.billsplitter.ui.home.FriendsTab;
import com.example.billsplitter.ui.util.GenerateUniqueId;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.seismic.ShakeDetector;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.example.billsplitter.MainActivity.checkUser;
import static com.example.billsplitter.MainActivity.getIdFromUserName;
import static com.example.billsplitter.MainActivity.getNameFromUserID;
import static com.example.billsplitter.ui.util.GenerateUniqueId.getUniqueId;

public class NewExpense extends AppCompatActivity implements ShakeDetector.Listener {

    //declaring variables
    private Button paid;
    private Button split, addItem;

    private boolean[] checkedItems;
    private String selected;
    private String friendPhoneNumber;
    private EditText etDescription;
    private EditText etAmount;
    private ImageView expenseImage;

    private ArrayList<String> userItems = new ArrayList<>();

    private ArrayList<String> PaidList;
    private boolean paidByYou = true;

    private String[] listItems;

    private DatabaseReference ExpenseTable;
    private DatabaseReference FriendsAndGroups;

    private DatabaseReference ActivityTable;
    private String UserID;
    private String you;
    private String friend;

    private ArrayAdapter<String> adapter;

    private String selectedFriendOrGroup;
    private Spinner sList;

    private String PaidBy;
    private String amountFinal;
    private final int CAMERA_SELECTED = 0, GALLERY_SELECTED = 1;
    private boolean isImageSet = false;
    private String uniqueId;
    BottomSheetDialog mBottomSheetDialog;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    StorageReference expenseImagesRef = storageReference.child("expense_image");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_expense);

        //shake detector for sensor
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector shakeDetector = new ShakeDetector(NewExpense.this);
        shakeDetector.start(sensorManager);

        //initializing variables
        etAmount = findViewById(R.id.amount);
        etDescription = findViewById(R.id.describeitem);
        paid = findViewById(R.id.paidByButton);
        addItem = findViewById(R.id.additem);
        expenseImage = findViewById(R.id.expense_image);

        Button imageUpload = findViewById(R.id.upload_image);

        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        UserID = sp.getString("UserId", null);

        ArrayList<String> f= getIntent().getStringArrayListExtra("friends");
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        FriendsAndGroups = database.getReference("/expenses_data");

        ArrayList<String> friendsAndGroups = new ArrayList<>();
        for (int i=0;i<f.size();i++)
        {
            if (getNameFromUserID(f.get(i)).equals(""))
            {
                if (!f.get(i).equals("isEmpty"))
                    friendsAndGroups.add(StringUtils.abbreviate(f.get(i),25));
            }
            else {
                if (!f.get(i).equals("isEmpty"))
                    friendsAndGroups.add(StringUtils.abbreviate(getNameFromUserID(f.get(i)), 25));
            }
        }
        PaidList = new ArrayList<>();
        PaidBy = "You";
        adapter = new ArrayAdapter<>(
                NewExpense.this, android.R.layout.simple_list_item_1, friendsAndGroups);


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sList =  findViewById(R.id.list_friends);
        sList.setAdapter(adapter);


        sList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFriendOrGroup = sList.getSelectedItem().toString();
                PaidList.clear();
                PaidList.add("You");
                if (!checkUser(selectedFriendOrGroup))
                {
                    FriendsAndGroups.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot unit : dataSnapshot.child(UserID).child("group_expenses").getChildren()) {
                                if (unit.getKey().equals(selectedFriendOrGroup)){
                                    for (DataSnapshot members : unit.getChildren()){

                                        if (!members.getKey().equals(UserID)){
                                            PaidList.add(getNameFromUserID(members.getKey()));
                                        }

                                        System.out.println("**************" + members.getKey());
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
                else
                {
                    PaidList.add(selectedFriendOrGroup);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

      //OnClick Listener for paid by
        paid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedFriendOrGroup = sList.getSelectedItem().toString();


                System.out.println(UserID);
                System.out.println(sList.getSelectedItem());


                listItems = new String[PaidList.size()];
                for(int i=0;i<PaidList.size();i++){
                    listItems[i] = PaidList.get(i);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(NewExpense.this);
                builder.setTitle("Paid by");
                builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected = listItems[which];
                    }
                });
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        /*String people = "";
                        for (int j = 0; j < userItems.size(); j++){
                            people = people + userItems.get(j);
                        }

                        System.out.println(selected);
                        if (selected.equals("Friend")){
                            paidByYou = false;
                            paid.setText(R.string.paid_by_friend);
                        }
                        else{
                            paid.setText(R.string.paid_by_you);
                        }*/
                    }
                });
                AlertDialog mDialog = builder.create();
                mDialog.show();
            }
        });

        //OnClick Listener for adding item
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String desc = etDescription.getText().toString();
                String amount = etAmount.getText().toString();


                if (desc.isEmpty() || amount.isEmpty()){
                    if (desc.isEmpty()){
                        etDescription.setError("Please fill the bill description");
                    }
                    if (amount.isEmpty()){
                        etAmount.setError("Please enter the amount");
                    }
                    return;
                }

                uniqueId = "";
                if (isImageSet == true) {
                    uniqueId = new GenerateUniqueId().getUniqueId();
                }

                if (checkUser(selectedFriendOrGroup)){
                    addExpenseToDb(desc, amount, getIdFromUserName(selectedFriendOrGroup), uniqueId);
                }
                else {
                    addExpenseToDb(desc, amount,"", uniqueId);
                }

                if (isImageSet == true) {
                    uploadImageToFirebase();
                }
                //String friendUserName = etUserName.getText().toString();
                //addExpenseToDb(desc, amount, friendUserName);
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                
            }
        });

        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheet();
            }
        });
    }

   //adding expense to database
    private void addExpenseToDb(String desc, String amount, final String friendID, String uniqueId){
        Double splitAmount;
        int number = 0;

        final String groupName = sList.getSelectedItem().toString();
        final DecimalFormat df = new DecimalFormat("#.##");

        if (selected != null)
            PaidBy = selected;

        if (PaidBy.equals("You")){
            PaidBy = UserID;
        }
        else {
            PaidBy = getIdFromUserName(selected);
        }
        if (!PaidBy.equals(UserID)){
            PaidList.add(getNameFromUserID(UserID));
            number--;
        }

        boolean groupExpense = true;
        if (friendID.equals("")){
            number += PaidList.size();
            splitAmount = Double.parseDouble(amount)/number;
            amountFinal = df.format(splitAmount);
        }
        else {
            groupExpense = false;
            number = 2;
            splitAmount = Double.parseDouble(amount)/number;

            amountFinal = df.format(splitAmount);

            if (PaidBy.equals(UserID)){
                you = amountFinal;
                friend = "-" + amountFinal;
            }
            else {
                friend = amountFinal;
                you = "-" + amountFinal;
            }
        }
        addExpenseToActivity(desc, PaidBy, amountFinal, amount, uniqueId);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        ExpenseTable = database.getReference("expenses_data/");

        if (groupExpense){
            ExpenseTable.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot unit : dataSnapshot.getChildren()) {

                        try{
                            if (unit.getKey().equals(PaidBy)){

                                for (DataSnapshot group : unit.child("group_expenses").child(groupName).getChildren())
                                {

                                    String preValue = String.valueOf(group.getValue());
                                    double value = Double.parseDouble(preValue);
                                    value = value + Double.parseDouble(amountFinal);

                                    String write = df.format(value);

                                    ExpenseTable.child(unit.getKey()).child("group_expenses").child(groupName).child(group.getKey()).setValue(write);
                                }
                            }
                            else if (PaidList.contains(getNameFromUserID(unit.getKey()))) {

                                for (DataSnapshot group : unit.child("group_expenses").child(groupName).getChildren())
                                {
                                    if (group.getKey().equals(PaidBy)){

                                        String preValue = String.valueOf(group.getValue());
                                        double value = Double.parseDouble(preValue);
                                        value = value - Double.parseDouble(amountFinal);
                                        String write = df.format(value);

                                        ExpenseTable.child(unit.getKey()).child("group_expenses").child(groupName).child(group.getKey()).setValue(write);
                                    }

                                }

                            }
                        }
                        catch (Exception e) {
                            System.out.println(e.getMessage());
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });


        }
        else {
            ExpenseTable.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot unit : dataSnapshot.getChildren()) {

                        if (unit.getKey().equals(UserID)){

                                String preValue = String.valueOf(unit.child("individual_expenses").child(friendID).getValue());
                                double value = Double.parseDouble(preValue);
                                if (unit.getKey().equals(PaidBy)){
                                    value = value + Double.parseDouble(amountFinal);
                                }
                                else {
                                    value = value - Double.parseDouble(amountFinal);
                                }

                                String write = df.format(value);

                                ExpenseTable.child(unit.getKey()).child("individual_expenses").child(friendID).setValue(write);

                        }
                        else if (unit.getKey().equals(friendID)){
                                String preValue = String.valueOf(unit.child("individual_expenses").child(UserID).getValue());
                                double value = Double.parseDouble(preValue);
                                if (unit.getKey().equals(PaidBy)){
                                    value = value + Double.parseDouble(amountFinal);
                                }
                                else {
                                    value = value - Double.parseDouble(amountFinal);
                                }

                                String write = df.format(value);
                                ExpenseTable.child(unit.getKey()).child("individual_expenses").child(UserID).setValue(write);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    //method for adding expense to activity
    private void addExpenseToActivity(final String desc, final String paidBy, final String amountSplit, final String amountFull, final String uniqueId){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final String expenseID = getUniqueId();

        ActivityTable = database.getReference("activity/");
        ActivityTable.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot unit : dataSnapshot.getChildren()){
                    if (unit.getKey().equals(UserID)){
                        ActivityTable.child(unit.getKey()).child(expenseID).child("desc").setValue(desc);
                        if (paidBy.equals(UserID)){
                            ActivityTable.child(unit.getKey()).child(expenseID).child("amount").setValue(amountFull);
                        }
                        else{
                            ActivityTable.child(unit.getKey()).child(expenseID).child("amount").setValue(amountSplit);
                        }
                        ActivityTable.child(unit.getKey()).child(expenseID).child("paidBy").setValue(paidBy);
                        ActivityTable.child(unit.getKey()).child(expenseID).child("imageId").setValue(uniqueId);
                    }
                    else if (PaidList.contains(getNameFromUserID(unit.getKey()))){
                        ActivityTable.child(unit.getKey()).child(expenseID).child("desc").setValue(desc);
                        if (paidBy.equals(unit.getKey())){
                            ActivityTable.child(unit.getKey()).child(expenseID).child("amount").setValue(amountFull);
                        }
                        else{
                            ActivityTable.child(unit.getKey()).child(expenseID).child("amount").setValue(amountSplit);
                        }
                        ActivityTable.child(unit.getKey()).child(expenseID).child("paidBy").setValue(paidBy);
                        ActivityTable.child(unit.getKey()).child(expenseID).child("imageId").setValue(uniqueId);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    public void hearShake() {
        Intent intent = new Intent(NewExpense.this, HomeActivity.class);
        startActivity(intent);
    }

    //method for uploading an image either from camera or gallery
    public void openBottomSheet(){
        mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = this.getLayoutInflater().inflate(R.layout.select_expense_image_bottom_layout, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();

        LinearLayout camera = sheetView.findViewById(R.id.camera_linear_layout);
        LinearLayout gallery = sheetView.findViewById(R.id.gallery_linear_layout);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
                mBottomSheetDialog.dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
                mBottomSheetDialog.dismiss();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case CAMERA_SELECTED:
                if(resultCode == RESULT_OK){
                    isImageSet = true;
                    Bitmap bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    expenseImage.setImageBitmap(bitmap);
                }
                break;

            case GALLERY_SELECTED:
                if(resultCode == RESULT_OK){
                    isImageSet = true;
                    Uri selectedImage = imageReturnedIntent.getData();
                    expenseImage.setImageURI(selectedImage);
                }
                break;
        }
    }

    //method for uploading image to firebase
    private void uploadImageToFirebase(){
        String imageName = StringUtils.join(uniqueId, ".jpeg");
        StorageReference newImageRef = expenseImagesRef.child(imageName);
        expenseImage.setDrawingCacheEnabled(true);
        expenseImage.buildDrawingCache();

        Bitmap bitmap = ((BitmapDrawable) expenseImage.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        UploadTask uploadTask = newImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Image upload failed!", Toast.LENGTH_LONG);
            }
        });
    }
}