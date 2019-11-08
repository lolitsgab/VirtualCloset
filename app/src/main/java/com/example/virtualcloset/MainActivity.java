package com.example.virtualcloset;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import java.util.ArrayList;
import androidx.annotation.NonNull;

import java.util.List;
import in.goodiebag.carouselpicker.CarouselPicker;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;


public class MainActivity extends AppCompatActivity {
    String  UserUID;
    Bitmap bm;
    List<CarouselPicker.PickerItem> topItems = new ArrayList<>();
    List<CarouselPicker.PickerItem> bottomItems = new ArrayList<>();
    StorageReference storageReference, pathTopReference, pathBottomReference;
    ImageView cameraActvivityButton;
    FirebaseStorage storage;
    CarouselPicker topCarousel, bottomCarousel;
    final long ONE_MEGABYTE = 1024 * 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraActvivityButton = this.findViewById(R.id.startCameraActivityButton);
        topCarousel = this.findViewById(R.id.carouselTop);
        bottomCarousel = this.findViewById(R.id.carouselBottom);

        // FIREBASE SETUP
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        UserUID = FirebaseAuth.getInstance().getUid();

        // TOP CAROUSEL


        // BOTTOM CAROUSEL


        // CAMERA ACTIVITY
        cameraActvivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        addTopCarousel();
        addBottomCarousel();
    }

    public void addTopCarousel() {
        pathTopReference = storageReference.child("users/" + UserUID +
                "/clothes/" + "top" + "/");
        pathTopReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {

                for(StorageReference filteref: listResult.getItems()) {
                    filteref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            bm = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                            topItems.add(new CarouselPicker.BitmapItem(bm));
                            CarouselPicker.CarouselViewAdapter topAdapter = new CarouselPicker.CarouselViewAdapter
                                    (getApplicationContext(), topItems, 0);
                            topCarousel.setAdapter(topAdapter);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void addBottomCarousel() {
        pathBottomReference = storageReference.child("users/" + UserUID +
                "/clothes/" + "bottom" + "/");
        pathBottomReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference filteref: listResult.getItems()) {
                    filteref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            bm = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                            bottomItems.add(new CarouselPicker.BitmapItem(bm));
                            CarouselPicker.CarouselViewAdapter bottomAdapter = new CarouselPicker.CarouselViewAdapter
                                    (getApplicationContext(), bottomItems, 0);
                            bottomCarousel.setAdapter(bottomAdapter);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}


