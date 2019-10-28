package com.example.virtualcloset;

import android.os.Bundle;
import android.widget.ImageView;

import in.goodiebag.carouselpicker.CarouselPicker;
import java.util.List;
import java.util.ArrayList;
import androidx.viewpager.widget.ViewPager;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private int mCurrentPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        CarouselPicker carouselPicker = (CarouselPicker) findViewById(R.id.carousel);
        CarouselPicker carouselPicker2 =  (CarouselPicker) findViewById(R.id.carousel2);

// Case 1 : To populate the picker with images
        List<CarouselPicker.PickerItem> shirts = new ArrayList<>();
        shirts.add(new CarouselPicker.DrawableItem(R.drawable.striped_guess));
        shirts.add(new CarouselPicker.DrawableItem(R.drawable.carhart_shirt));
        shirts.add(new CarouselPicker.DrawableItem(R.drawable.stussy_crewneck));

        List<CarouselPicker.PickerItem> bottoms = new ArrayList<>();
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
}
