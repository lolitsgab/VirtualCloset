
package com.example.virtualcloset;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.navigation.NavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import androidx.core.view.GravityCompat;

import java.util.List;
import in.goodiebag.carouselpicker.CarouselPicker;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;

    private ActionBarDrawerToggle drawerToggle;

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

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //change later to hamburger (three lines icon)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawer = findViewById(R.id.drawer_layout);
        nvDrawer = findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);


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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
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













