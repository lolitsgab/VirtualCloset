package com.example.virtualcloset;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import java.util.ArrayList;
import androidx.annotation.NonNull;

import java.util.List;
import java.util.UUID;
import in.goodiebag.carouselpicker.CarouselPicker;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;


public class MainActivity extends AppCompatActivity {
    String  UserUID, uniqueImageName, top;
    Bitmap bm;
    ImageView imageView;

    StorageReference storageReference, pathTopReference;
    ImageView cameraActvivityButton;
    FirebaseStorage storage;
    CarouselPicker topCarousel, bottomCarousel;
    Context ctx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraActvivityButton = this.findViewById(R.id.startCameraActivityButton);
        topCarousel = this.findViewById(R.id.carouselTop);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        UserUID = FirebaseAuth.getInstance().getUid();
        pathTopReference = storageReference.child("users/" + "hsC42FZunCgmKEhZNeRKEATks6e2" +
                "/clothes/" + "top" + "/" + "021e9412-c89c-4e08-967e-78216ff8f8d5");

        //System.out.println("path:" + pathTopReference);
        // listener needs to be inside view, but values cannot be
        // iterated inside the view

        cameraActvivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });
        final long ONE_MEGABYTE = 1024 * 1024;
        pathTopReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bm = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                List<CarouselPicker.PickerItem> bitItems = new ArrayList<>();
                System.out.println(bm);
                bitItems.add(new CarouselPicker.BitmapItem(bm));
                bitItems.add(new CarouselPicker.BitmapItem(bm));
                bitItems.add(new CarouselPicker.BitmapItem(bm));
                CarouselPicker.CarouselViewAdapter bitAdapter = new CarouselPicker.CarouselViewAdapter(getApplicationContext(), bitItems, 0);
                topCarousel.setAdapter(bitAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }
}


