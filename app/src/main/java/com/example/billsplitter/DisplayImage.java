package com.example.billsplitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.lang3.StringUtils;

public class DisplayImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        String image = getIntent().getStringExtra("imageID");
        String imageName = image.concat(".jpeg");
        final ImageView imgView = findViewById(R.id.downloaded_image);
        TextView imageTxtView = findViewById(R.id.no_image_text);
        System.out.println("#####################################");
        System.out.println(image);
        System.out.println("#####################################");
        if(!image.equalsIgnoreCase("")) {
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference();
            StorageReference expenseImagesRef = storageReference.child("expense_image");
            StorageReference imagePath = expenseImagesRef.child(imageName);

            final long ONE_MEGABYTE = 1024 * 1024;
            imagePath.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    Bitmap bitmapScaled = Bitmap.createScaledBitmap(bmp, 500,
                            500, true);
                    Drawable drawable = new BitmapDrawable(bitmapScaled);
                    imgView.setBackgroundDrawable(drawable);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(), "Download image Failed", Toast.LENGTH_LONG);
                }
            });
        } else {
            imageTxtView.setText("This expense has no image!");
            imageTxtView.setTextColor(Color.BLACK);
        }
    }
}
