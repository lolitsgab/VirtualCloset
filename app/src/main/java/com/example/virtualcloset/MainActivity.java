package com.example.virtualcloset;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Color;
import android.media.Image;

import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.navigation.NavigationView;
import android.widget.Toast;
import androidx.core.view.GravityCompat;

import java.util.List;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import in.goodiebag.carouselpicker.CarouselPicker;


public class MainActivity extends AppCompatActivity {
    // DEFINED FOR CAROUSEL
    private String  UserUID;
    private Bitmap bm;
    private List<CarouselPicker.PickerItem> topItems = new ArrayList<>();
    private List<CarouselPicker.PickerItem> bottomItems = new ArrayList<>();
    private StorageReference storageReference, pathTopReference, pathBottomReference;
    private FirebaseStorage storage;
    private CarouselPicker topCarousel, bottomCarousel;
    private final long ONE_MEGABYTE = 1024 * 1024;
    //DEFINED FOR CAMERA
    private ImageView cameraActvivityButton;
    //DEFINED FOR MENU DRAWER
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    private List<String> shirtNames;
    private List<String> pantsNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraActvivityButton = this.findViewById(R.id.startCameraActivityButton);
        topCarousel = this.findViewById(R.id.carouselTop);
        bottomCarousel = this.findViewById(R.id.carouselBottom);

        //STAR FUNCTION
        shirtNames = new ArrayList<>();
        pantsNames = new ArrayList<>();
        ImageView starButton = findViewById(R.id.star);
        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str = shirtNames.get(topCarousel.getCurrentItem());
                Toast.makeText(MainActivity.this, "Name of shirt: " + str, Toast.LENGTH_SHORT).show();

            }
        });

        // FIREBASE SETUP
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        UserUID = FirebaseAuth.getInstance().getUid();

        // MENU SETUP
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //change later to hamburger (three lines icon)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawer = findViewById(R.id.drawer_layout);
        nvDrawer = findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);

        // ADD CAROUSEL
        pathTopReference = storageReference.child("users/" + UserUID +
                "/clothes/" + "top" + "/");
        pathBottomReference = storageReference.child("users/" + UserUID +
                "/clothes/" + "bottom" + "/");
        addCarousel(topItems, shirtNames, pathTopReference, topCarousel);
        addCarousel(bottomItems, pantsNames, pathBottomReference, bottomCarousel);


        // CAMERA ACTIVITY
        cameraActvivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });


    }

    // ADDS TOP CLOTHING FROM USER STORAGE
    // AND DISPLAYS IN FIRST CAROUSEL
    public void addCarousel(final List<CarouselPicker.PickerItem> items, final List<String> itemNames, StorageReference ref, final CarouselPicker carousel) {
        //final List<String> names = new ArrayList<>();
        ref.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference filteref: listResult.getItems()) {
                    itemNames.add(filteref.getName());
                    filteref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            bm = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                            items.add(new CarouselPicker.BitmapItem(bm));
                            CarouselPicker.CarouselViewAdapter adapter =

                                    new CarouselPicker.CarouselViewAdapter(MainActivity.this,
                                            items, itemNames,0);
                            carousel.setAdapter(adapter);

                            carousel.setCurrentItem(adapter.getCount()/2);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "FAILED DATABASE SYNC",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "NO CLOTHING AVAILABLE TO DISPLAY",
                        Toast.LENGTH_LONG).show();
            }
        });

    }






    public void showDialog(View theView){

    }

    // MENU CONFIGURATION
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        if (item.getItemId() == android.R.id.home) {
            mDrawer.openDrawer(GravityCompat.START);
            return true;
        }else if(item.getItemId() == R.id.nav_first_fragment){
            selectDrawerItem(item);
        }else{
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NotNull MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        fragmentClass = TestFragment1.class;

        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                fragmentClass = TestFragment1.class;
                break;

            default:
                fragmentClass = TestFragment1.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }
}
