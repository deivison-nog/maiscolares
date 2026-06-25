package com.info85.maiscolares;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;

public class VideoDownloader {

    public interface VideoDownloadCallback {
        void onVideoDownloaded(String videoUrl);
        void onVideoDownloadError();
    }

    public static void downloadRandomVideo(Context context, VideoDownloadCallback callback) {
        new LoadRandomVideoTask(context, callback).execute();
    }

    private static class LoadRandomVideoTask extends AsyncTask<Void, Void, String> {
        private Context context;
        private VideoDownloadCallback callback;

        public LoadRandomVideoTask(Context context, VideoDownloadCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String videoUrl = null;

            // Replace with your actual JSON URL
            String jsonUrl = "https://maiscolares.info85.com.br/videos.json";

            try {
                URL url = new URL(jsonUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    Scanner scanner = new Scanner(inputStream);
                    StringBuilder response = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        response.append(scanner.nextLine());
                    }
                    scanner.close();

                    // Parse JSON and get random video URL
                    String[] videoUrls = parseJsonResponse(response.toString());
                    if (videoUrls.length > 0) {
                        Random random = new Random();
                        int randomIndex = random.nextInt(videoUrls.length);
                        videoUrl = videoUrls[randomIndex];

                        // Download and save the video to cache
                        downloadAndSaveVideo(videoUrl);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return videoUrl;
        }

        @Override
        protected void onPostExecute(String videoUrl) {
            if (videoUrl != null) {
                // Start the InterstitialActivity with the cached video URL
                callback.onVideoDownloaded(videoUrl);
            } else {
                callback.onVideoDownloadError();
            }
        }

        private String[] parseJsonResponse(String jsonResponse) {
            String[] videoUrls = null;
            try {
                JSONArray jsonArray = new JSONArray(jsonResponse);
                if (jsonArray.length() > 0) {
                    videoUrls = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        videoUrls[i] = jsonObject.getString("url");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return videoUrls;
        }

        private void downloadAndSaveVideo(String videoUrl) {
            try {
                URL url = new URL(videoUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    File cacheDir = context.getCacheDir();
                    File videoFile = new File(cacheDir, "cached_video.mp4");
                    FileOutputStream outputStream = new FileOutputStream(videoFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.close();
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
