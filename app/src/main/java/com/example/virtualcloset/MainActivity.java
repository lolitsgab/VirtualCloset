package com.example.virtualcloset;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import in.goodiebag.carouselpicker.CarouselPicker;
import java.util.List;
import java.util.ArrayList;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;
import android.view.MenuItem;
import androidx.core.view.GravityCompat;



public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup toolbar to replace actionbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //change later to hamburger (three lines icon)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawer = findViewById(R.id.drawer_layout);
        nvDrawer = findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);


        final CarouselPicker carouselPicker = (CarouselPicker) findViewById(R.id.carousel);
        CarouselPicker carouselPicker2 =  (CarouselPicker) findViewById(R.id.carousel2);

// Case 1 : To populate the picker with images
        final List<CarouselPicker.PickerItem> shirts = new ArrayList<>();
        shirts.add(new CarouselPicker.DrawableItem(R.drawable.striped_guess));
        shirts.add(new CarouselPicker.DrawableItem(R.drawable.carhart_shirt));
        shirts.add(new CarouselPicker.DrawableItem(R.drawable.stussy_crewneck));

        final List<CarouselPicker.PickerItem> bottoms = new ArrayList<>();
        bottoms.add(new CarouselPicker.DrawableItem(R.drawable.jeans));
        bottoms.add(new CarouselPicker.DrawableItem(R.drawable.black_jeans));
        bottoms.add(new CarouselPicker.DrawableItem(R.drawable.striped_pants));
//Create an adapter
        CarouselPicker.CarouselViewAdapter imageAdapter = new CarouselPicker.CarouselViewAdapter(this, shirts, 0);
        CarouselPicker.CarouselViewAdapter imageAdapter2 = new CarouselPicker.CarouselViewAdapter(this, bottoms, 0);
//Set the adapter
        carouselPicker.setAdapter(imageAdapter);
        carouselPicker2.setAdapter(imageAdapter2);


        carouselPicker.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
