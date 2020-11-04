package com.linhcn.exampleripplelayout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.linhcn.ripplelayout.RippleLayout;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RippleLayout rippleLayout = findViewById(R.id.ripple_bg);
        ImageView imageView = findViewById(R.id.centerImage);
        rippleLayout.startRippleAnimation();
        imageView.setOnClickListener(view -> {
            if (rippleLayout.isRippleAnimationRunning())
                rippleLayout.stopRippleAnimation();
            else
                rippleLayout.startRippleAnimation();
        });

        Button button = findViewById(R.id.btn_speed);
        button.setOnClickListener(view -> {
            rippleLayout.setSpeed(rippleLayout.getDurationTime() - 500);
        });
    }
}