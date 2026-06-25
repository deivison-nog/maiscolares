package com.info85.maiscolares;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class InterstitialActivity extends AppCompatActivity {

    private VideoView videoView;
    private ProgressBar progressBar;
    private Handler handler;
    private int progress = 0;

    private Runnable updateProgressBarTask = new Runnable() {
        @Override
        public void run() {
            if (progress < 100) {
                progress++;
                progressBar.setProgress(progress);
                handler.postDelayed(this, 100); // Delayed task to update every 100 milliseconds
            } else {
                // When progress reaches 100, close the activity
                finish();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);
        videoView = findViewById(R.id.videoView);
        progressBar = findViewById(R.id.progressBar);
        handler = new Handler();

        // Get the cached video file
        File cacheDir = getCacheDir();
        File videoFile = new File(cacheDir, "cached_video.mp4");

        if (videoFile.exists()) {
            Uri videoUri = Uri.fromFile(videoFile);
            videoView.setVideoURI(videoUri);
            videoView.setOnPreparedListener(mp -> {
                // Start video playback
                videoView.start();
                videoView.setOnCompletionListener(completionListener);
                handler.post(updateProgressBarTask);
            });
        }
    }

    private MediaPlayer.OnCompletionListener completionListener = mp -> {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        handler.post(updateProgressBarTask);
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(InterstitialActivity.this,"Por favor, aguarde o fim do anúncio.", Toast.LENGTH_SHORT).show();
    }
}
