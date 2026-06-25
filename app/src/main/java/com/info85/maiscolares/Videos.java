package com.info85.maiscolares;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Videos extends AppCompatActivity {

    private VideoAdapter videoAdapter;
    private List<VideoItem> videoItemList;
    private List<VideoItem> searchResultsList;


    private RequestQueue requestQueue;
    private static final String API_KEY = "AIzaSyC16XtXI8JJRMCAXAtcEL6oPsK2WrwNSns";
    private static final String PLAYLIST_ID = "PLonoUb-D5Qo4cJYwpqMrk3WddQvbSu6AS";
    private static final String BASE_URL = "https://www.googleapis.com/youtube/v3/";
    private ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        progressBar = findViewById(R.id.progress_bar);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        videoItemList = new ArrayList<>();
        videoAdapter = new VideoAdapter(videoItemList);
        recyclerView.setAdapter(videoAdapter);

        searchResultsList = new ArrayList<>();

        requestQueue = Volley.newRequestQueue(this);

        EditText editTextSearch = findViewById(R.id.edit_text_search);
        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String searchText = editTextSearch.getText().toString();
                    if (!searchText.isEmpty()) {
                        performSearch(searchText);
                    }

                    // Esconder o teclado
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });


        Button buttonSearch = findViewById(R.id.button_search);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = editTextSearch.getText().toString();
                if (!searchText.isEmpty()) {
                    performSearch(searchText);
                }
            }
        });


        fetchVideoData();
    }

    private void performSearch(String searchText) {
        // Limpa a lista de resultados da última busca
        searchResultsList.clear();

        // Recarrega a lista de vídeos
        fetchVideoData();

        // Após recarregar, faça a busca
        for (VideoItem video : videoItemList) {
            if (video.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                searchResultsList.add(video);
            }
        }

        if (searchResultsList.isEmpty()) {
            Toast.makeText(this, "Nenhum vídeo encontrado", Toast.LENGTH_SHORT).show();
        } else {
            videoAdapter.setData(searchResultsList);
        }
    }

    private void fetchVideoData() {
        String url = BASE_URL + "playlistItems" +
                "?part=snippet" +
                "&playlistId=" + PLAYLIST_ID +
                "&key=" + API_KEY +
                "&maxResults=100";

        @SuppressLint("NotifyDataSetChanged") JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray items = response.getJSONArray("items");
                        List<VideoItem> fetchedVideoItems = new ArrayList<>();

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject videoItem = items.getJSONObject(i);
                            JSONObject snippet = videoItem.getJSONObject("snippet");
                            String videoTitle = snippet.getString("title");
                            String videoId = snippet.getJSONObject("resourceId").getString("videoId");
                            String thumbnailUrl = snippet.getJSONObject("thumbnails")
                                    .getJSONObject("medium").getString("url");

                            VideoItem item = new VideoItem(videoId, videoTitle, thumbnailUrl);
                            fetchedVideoItems.add(item);
                        }

                        // Shuffle the list before adding it to the main list
                        videoItemList.addAll(shuffleList(fetchedVideoItems));

                        videoAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Videos.this, "Erro ao carregar os vídeos", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(request);

    }

    private List<VideoItem> shuffleList(List<VideoItem> list) {
        List<VideoItem> shuffledList = new ArrayList<>(list);
        Collections.shuffle(shuffledList);
        return shuffledList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearShuffledList();
    }


    @SuppressLint("NotifyDataSetChanged")
    private void clearShuffledList() {
        if (videoItemList != null) {
            videoItemList.clear();
            videoAdapter.notifyDataSetChanged();
        }
        if (searchResultsList != null) {
            searchResultsList.clear();
        }
    }

    public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

        private final List<VideoItem> videoItems;

        public VideoAdapter(List<VideoItem> videoItems) {
            this.videoItems = videoItems;
        }

        @NonNull
        @Override
        public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
            return new VideoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
            VideoItem videoItem = videoItems.get(position);
            holder.videoTitle.setText(videoItem.getTitle());

            Glide.with(holder.itemView.getContext())
                    .load(videoItem.getThumbnailUrl())
                    .centerCrop()
                    .placeholder(R.drawable.appmaiscolaresnobg)
                    .into(holder.videoThumbnail);

            holder.videoThumbnail.setOnClickListener(view -> {
                // Get the video ID from the thumbnail
                String videoId = videoItem.getVideoId();

                // Open the video in the YouTube app
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoId));
                view.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return videoItems.size();
        }

        public class VideoViewHolder extends RecyclerView.ViewHolder {
            ImageView videoThumbnail;
            TextView videoTitle;

            public VideoViewHolder(@NonNull View itemView) {
                super(itemView);
                videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
                videoTitle = itemView.findViewById(R.id.video_title);
            }
        }

        public void setData(List<VideoItem> videoItems) {
            this.videoItems.clear();
            this.videoItems.addAll(videoItems);
            notifyDataSetChanged();
        }

    }

    public static class VideoItem {
        private final String videoId;
        private final String title;
        private final String thumbnailUrl;

        public VideoItem(String videoId, String title, String thumbnailUrl) {
            this.videoId = videoId;
            this.title = title;
            this.thumbnailUrl = thumbnailUrl;
        }

        public String getVideoId() {
            return videoId;
        }

        public String getTitle() {
            return title;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }
    }
}
