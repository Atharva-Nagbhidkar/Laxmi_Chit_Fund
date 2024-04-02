package com.example.bankappp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView=findViewById(R.id.image9);
        imageView.setImageResource(R.drawable.laxmichitfund);

        new Handler().postDelayed(new Runnable() {
            @Override public void run() {

                startActivity(new Intent(getApplicationContext(), LoginAcitviy.class));
                finish();
            }
        }, 2000);
    }
}