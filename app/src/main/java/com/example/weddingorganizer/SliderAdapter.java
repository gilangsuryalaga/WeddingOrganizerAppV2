package com.example.weddingorganizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    public int[] slide_images = {
            R.drawable.loveletter,
            R.drawable.weddingcouple,
            R.drawable.weddingring

    };
    public String[] slide_heading = {
            "Test Heading 1",
            "Test Heading 2",
            "Test Heading 3"
    };

    public String[] slide_desc = {
            "Lorem ipsum dolor sit amet, consectur adipiscing elit, sed do eusmod tempor incididunt ut labore et dolore magna",
            "Lorem ipsum dolor sit amet, consectur adipiscing elit, sed do eusmod tempor incididunt ut labore et dolore magna",
            "Lorem ipsum dolor sit amet, consectur adipiscing elit, sed do eusmod tempor incididunt ut labore et dolore magna"
    };

    @Override
    public int getCount() {
        return slide_heading.length;

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_item, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_images);
        TextView slideHeadings = (TextView) view.findViewById(R.id.slide_headings);
        TextView slideDesc = (TextView) view.findViewById(R.id.slide_descs);

        slideImageView.setImageResource(slide_images[position]);
        slideHeadings.setText(slide_heading[position]);
        slideDesc.setText(slide_desc[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);

    }
}