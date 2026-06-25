package com.info85.maiscolares;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Titles extends AppCompatActivity {

    private ListView listViewTitles;
    private List<PostTitle> posts;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private InterstitialAd mInterstitialAd;

    private List<String> titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_titles);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                // AdMob initialization completed.
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-4063386848597338/1584033690", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e("TAG", "onAdFailedToLoad");
                        mInterstitialAd = null;
                    }
                });

        listViewTitles = findViewById(R.id.listViewTitles);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String category = getIntent().getStringExtra("category");

        posts = new ArrayList<>();

        fetchDataFromServer(category);
    }

    private void fetchDataFromServer(final String category) {
        String url = "https://maiscolares.info85.com.br/conn.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        showData(response, category);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error as per your requirement
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void showData(JSONArray jsonArray, String category) {
        try {
            posts.clear();
            titles = new ArrayList<>(); // Adicione esta linha para criar a nova lista de títulos
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String postCategory = jsonObject.getString("category");
                if (postCategory.equals(category)) {
                    String postTitle = jsonObject.getString("title");
                    String postContent = jsonObject.getString("content");
                    String imageLink = jsonObject.getString("image");
                    String postLocal = jsonObject.getString("local");

                    PostTitle post = new PostTitle(postTitle, postContent, imageLink, postLocal);
                    posts.add(post);
                    titles.add(postTitle); // Adicione o título à nova lista de títulos
                }
            }

//            Collections.sort(posts, new Comparator<PostTitle>() {
//                @Override
//                public int compare(PostTitle post1, PostTitle post2) {
//                    return post1.getTitle().compareTo(post2.getTitle());
//                }
//            });

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_title, R.id.textViewTitle, titles); // Atualize aqui para usar a nova lista de títulos
            listViewTitles.setAdapter(adapter);

            listViewTitles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedTitle = posts.get(position).getTitle();
                    String selectedContent = posts.get(position).getContent();
                    String selectedImageLink = posts.get(position).getImageLink();
                    String selectedLocal = posts.get(position).getLocal();

                    Intent intent = new Intent(Titles.this, Post.class);
                    intent.putExtra("title", selectedTitle);
                    intent.putExtra("content", selectedContent);
                    intent.putExtra("image", selectedImageLink);
                    intent.putExtra("local", selectedLocal);
                    startActivity(intent);

                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(Titles.this);
                    } else {
                        Log.d("TAG", "O anúncio intersticial ainda não estava pronto.");
                    }
                }
            });

            progressBar.setVisibility(View.GONE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
