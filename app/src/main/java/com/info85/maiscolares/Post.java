package com.info85.maiscolares;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class Post extends AppCompatActivity {

    private TextView titleTextView;
    private TextView contentTextView;
    private TextView localTextView;
    private ImageView imageView;
    private ImageButton comoChegarButton;

    private Animation shakeAnimation;
    private Handler handler;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        titleTextView = findViewById(R.id.titleTextView);
        contentTextView = findViewById(R.id.contentTextView);
        localTextView = findViewById(R.id.localTextView);
        imageView = findViewById(R.id.imageView);
        comoChegarButton = findViewById(R.id.buttonComoChegar);

        // Receive the title, content, image link, and local from the Intent
        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        String imageLink = getIntent().getStringExtra("image");
        String local = getIntent().getStringExtra("local");

        // Display the title and content in the TextViews
        titleTextView.setText(title);

        // Replace '\n' with new lines in the content
        content = content.replace("<br>", System.getProperty("line.separator"));
        contentTextView.setText(content);

        // Replace '\n' with new lines in the content
        contentTextView.setText(content);

        // Set the local to the TextView
        localTextView.setText(local);

        // Create the complete image link by appending the base URL
        String completeImageLink = "https://maiscolares.info85.com.br/" + imageLink;

        // Load the image using Glide
        Glide.with(this)
                .load(completeImageLink)
                .placeholder(R.drawable.appmaiscolaresnobg) // Placeholder image while loading
                .error(R.drawable.erro_icon) // Error image if the link is invalid
                .into(imageView);

        // Set click listener for "Como Chegar" button
        comoChegarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMaps(localTextView.getText().toString(), titleTextView.getText().toString());
            }
        });

        // Initialize the shake animation
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_animation);
        handler = new Handler();

        // Start the animation with a delay of 10 seconds
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startShakeAnimation();
                handler.postDelayed(this, 10000); // Repeat every 10 seconds
            }
        }, 5000);
    }

    private void openMaps(String local, String title) {
        String uri = "geo:0,0?q=" + Uri.encode(local + " (" + title + ")");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    public void startShakeAnimation() {
        comoChegarButton.startAnimation(shakeAnimation);
    }
}
