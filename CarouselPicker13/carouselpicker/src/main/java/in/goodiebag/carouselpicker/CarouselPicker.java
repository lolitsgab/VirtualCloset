package in.goodiebag.carouselpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pavan on 25/04/17.
 */

public class CarouselPicker extends ViewPager {
    private int itemsVisible = 3;
    private float divisor;

    public CarouselPicker(Context context) {
        this(context, null);
    }

    public CarouselPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
        init();
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        if (attrs != null) {
            final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CarouselPicker);
            itemsVisible = array.getInteger(R.styleable.CarouselPicker_items_visible, itemsVisible);
            switch (itemsVisible) {
                case 3:
                    TypedValue threeValue = new TypedValue();
                    getResources().getValue(R.dimen.three_items, threeValue, true);
                    divisor = threeValue.getFloat();
                    break;
                case 5:
                    TypedValue fiveValue = new TypedValue();
                    getResources().getValue(R.dimen.five_items, fiveValue, true);
                    divisor = fiveValue.getFloat();
                    break;
                case 7:
                    TypedValue sevenValue = new TypedValue();
                    getResources().getValue(R.dimen.seven_items, sevenValue, true);
                    divisor = sevenValue.getFloat();
                    break;
                default:
                    divisor = 3;
                    break;
            }
            array.recycle();
        }
    }

    private void init() {
        this.setPageTransformer(false, (PageTransformer) new CustomPageTransformer(getContext()));
        this.setClipChildren(false);
        this.setFadingEdgeLength(0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth();

        // Add 250 pixels for padding between images
        setPageMargin((int) (-w / divisor) + 250);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        this.setOffscreenPageLimit(adapter.getCount());
    }

    public static class CarouselViewAdapter extends PagerAdapter {

        List<PickerItem> items = new ArrayList<>();
        public List<String> names = new ArrayList<>();
        Context context;
        ImageView imageView;
        int drawable;
        int textColor = 0;

        public CarouselViewAdapter(Context context, List<PickerItem> items, List<String> names, int drawable) {
            this.context = context;
            this.items = items;
            this.names = names;
            this.imageView = imageView;
            if (this.drawable == 0) {
                this.drawable = R.layout.page;
            }
        }


        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(context).inflate(this.drawable, null);
            ImageView iv = (ImageView) view.findViewById(R.id.iv);
            PickerItem pickerItem = items.get(position);
            if (pickerItem.hasDrawable()) {
                iv.setVisibility(VISIBLE);
                iv.setImageResource(pickerItem.getDrawable());
            }
            if(pickerItem != null) {
                iv.setVisibility(VISIBLE);
                iv.setImageBitmap(pickerItem.getBitmap());
            }
            else {
                if (pickerItem.getText() != null) {
                    iv.setVisibility(GONE);
                }
            }
            view.setTag(position);
            container.addView(view);
            return view;
        }

        public int getTextColor() {
            return textColor;
        }

        public void setTextColor(@ColorInt int textColor) {
            this.textColor = textColor;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        private int dpToPx(int dp) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        }
    }

    /**
     * An interface which should be implemented by all the Item classes.
     * The picker only accepts items in the form of PickerItem.
     */
    public interface PickerItem {
        String getText();

        Bitmap getBitmap();

        @DrawableRes
        int getDrawable();

        boolean hasDrawable();
    }

    /**
     * A PickerItem which supports text.
     */
    public static class TextItem implements PickerItem {
        private String text;
        private int textSize;

        public TextItem(String text, int textSize) {
            this.text = text;
            this.textSize = textSize;
        }

        public String getText() {
            return text;
        }

        @Override
        public int getDrawable() {
            return 0;
        }

        @Override
        public Bitmap getBitmap() {
            return null;
        }

        @Override
        public boolean hasDrawable() {
            return false;
        }

        public int getTextSize() {
            return textSize;
        }

        public void setTextSize(int textSize) {
            this.textSize = textSize;
        }
    }

    /**
     * A PickerItem which supports drawables.
     */
    public static class BitmapItem implements PickerItem {
        private Bitmap bit;

        public BitmapItem(Bitmap bit) {
            this.bit = bit;
        }

        @Override
        public String getText() {
            return null;
        }

        public Bitmap getBitmap() {
            return bit;
        }

        @DrawableRes
        public int getDrawable() {
            return 0;
        }

        @Override
        public boolean hasDrawable() {
            return true;
        }
    }
    public static class DrawableItem implements PickerItem {
        @DrawableRes
        private int drawable;

        public DrawableItem(@DrawableRes int drawable) {
            this.drawable = drawable;
        }

        @Override
        public String getText() {
            return null;
        }

        @DrawableRes
        public int getDrawable() {
            return drawable;
        }

        @Override
        public boolean hasDrawable() {
            return true;
        }

        public Bitmap getBitmap() {
            return null;
        }
    }


}
