package com.info85.maiscolares;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;

import java.io.File;

public class MainActivity extends AppCompatActivity implements WeatherFetchTask.WeatherDataCallback {

    private TextView tempTextView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private static final String LAST_CACHE_CLEAR_KEY = "last_cache_clear";
    private AdView mAdView;
    private NativeAd nativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this);
        AdLoader adLoader = new AdLoader.Builder(this, "ca-app-pub-4063386848597338/4843350336")
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        NativeTemplateStyle styles = new
                                NativeTemplateStyle.Builder().withMainBackgroundColor(null).build();
                        TemplateView template = findViewById(R.id.my_template);
                        template.setStyles(styles);
                        template.setNativeAd(nativeAd);
                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        clearCacheIfNeeded();

        FirebaseApp.initializeApp(this);

        WeatherFetchTask task = new WeatherFetchTask(this);
        task.execute();

        ImageButton act01 = findViewById(R.id.bt01);
        act01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Titles.class);
                intent.putExtra("category", "Centro");
                startActivity(intent);
            }
        });

        ImageButton act02 = findViewById(R.id.bt02);
        act02.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Titles.class);
            intent.putExtra("category", "Rural");
            startActivity(intent);
        });

        ImageButton act03 = findViewById(R.id.bt03);
        act03.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Titles.class);
            intent.putExtra("category", "Praia");
            startActivity(intent);
        });

        ImageButton act04 = findViewById(R.id.bt04);
        act04.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Titles.class);
            intent.putExtra("category", "Loja");
            startActivity(intent);
        });

        ImageButton act05 = findViewById(R.id.bt05);
        act05.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Titles.class);
            intent.putExtra("category", "Bar");
            startActivity(intent);
        });

        ImageButton act06 = findViewById(R.id.bt06);
        act06.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Titles.class);
            intent.putExtra("category", "Hotel");
            startActivity(intent);
        });

        ImageButton act07 = findViewById(R.id.bt07);
        act07.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Titles.class);
            intent.putExtra("category", "Transporte");
            startActivity(intent);
        });

        ImageButton act08 = findViewById(R.id.bt08);
        act08.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Titles.class);
            intent.putExtra("category", "Educação");
            startActivity(intent);
        });

        ImageButton act09 = findViewById(R.id.bt09);
        act09.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Titles.class);
            intent.putExtra("category", "Serviços");
            startActivity(intent);
        });

        ImageButton act10 = findViewById(R.id.bt10);
        act10.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Videos.class);
            startActivity(intent);
        });

        ImageButton act11 = findViewById(R.id.bt11);
        act11.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Titles.class);
            intent.putExtra("category", "Outros");
            startActivity(intent);
        });

        ImageButton act12 = findViewById(R.id.bt12);
        act12.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Game.class);
            startActivity(intent);
        });

        ImageButton act13 = findViewById(R.id.bt13);
        act13.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Titles.class);
            intent.putExtra("category", "Lanches");
            startActivity(intent);
        });

        ImageButton act14 = findViewById(R.id.bt14);
        act14.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Titles.class);
            intent.putExtra("category", "Cultural");
            startActivity(intent);
        });

        ImageButton act15 = findViewById(R.id.bt15);
        act15.setOnClickListener(v -> {
//            Intent intent = new Intent(v.getContext(), Game.class);
//            startActivity(intent);
        });


        VideoDownloader.downloadRandomVideo(this, new VideoDownloader.VideoDownloadCallback() {
            @Override
            public void onVideoDownloaded(String videoUrl) {
                if (videoUrl != null) {
                    startInterstitialActivity(videoUrl);
                } else {
                    Toast.makeText(MainActivity.this,"Erro no download do vídeo.",Toast.LENGTH_SHORT).show();
                    // Trate o caso de erro no download do vídeo
                }
            }

            @Override
            public void onVideoDownloadError() {
                Toast.makeText(MainActivity.this,"Erro no download do vídeo.",Toast.LENGTH_SHORT).show();
                // Trate o caso de erro no download do vídeo
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationview);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_1:
                        Intent intentLite = new Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/profile.php?id=61550938262072"));
                        intentLite.setPackage("com.facebook.lite");
                        if (intentLite.resolveActivity(getPackageManager()) != null) {
                            startActivity(intentLite);
                        } else {
                            Intent intentFacebook = new Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/profile.php?id=61550938262072"));
                            intentFacebook.setPackage("com.facebook.katana");
                            if (intentFacebook.resolveActivity(getPackageManager()) != null) {
                                startActivity(intentFacebook);
                            } else {
                                Intent intentBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/profile.php?id=61550938262072"));
                                startActivity(intentBrowser);
                            }
                        }
                        break;

                    case R.id.menu_item_2:
                        String instagramProfileUrl = "https://instagram.com/mais.colares";
                        try {
                            Intent intentinsta = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/mais.colares"));
                            intentinsta.setPackage("com.instagram.android");
                            startActivity(intentinsta);
                        } catch (Exception e) {
                            Intent intentin = new Intent(Intent.ACTION_VIEW, Uri.parse(instagramProfileUrl));
                            startActivity(intentin);
                        }
                        break;

                    case R.id.menu_item_3:
                        Intent intentcontact = new Intent(MainActivity.this, ContactActivity.class);
                        startActivity(intentcontact);
                        break;

                    case R.id.menu_item_4:
                        Intent intentabout = new Intent(MainActivity.this, About.class);
                        startActivity(intentabout);
                        break;

                    case R.id.menu_item_5:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom);
                        builder.setMessage("Deseja mesmo sair?")
                                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                        System.exit(0);
                                    }
                                })
                                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        break;
                }
                drawerLayout.closeDrawer(Gravity.LEFT);
                return true;
            }
        });

        loadDrawerContent();
    }

    private void loadDrawerContent() {
        NavigationView navigationView = findViewById(R.id.navigationview);
        View headerView = navigationView.getHeaderView(0);
        TableLayout mareTableLayout = headerView.findViewById(R.id.mareTableLayout);

        Mares mares = new Mares(mareTableLayout);
        mares.fetchData();
    }

    private void clearCacheIfNeeded() {
        SharedPreferences sharedPreferences = getSharedPreferences("cache_prefs", MODE_PRIVATE);
        long lastCacheClearTime = sharedPreferences.getLong(LAST_CACHE_CLEAR_KEY, 0);
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCacheClearTime > 24 * 60 * 60 * 1000) {
            clearCache();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(LAST_CACHE_CLEAR_KEY, currentTime);
            editor.apply();
        }
    }

    private void clearCache() {
        try {
            File cacheDir = getCacheDir();
            if (cacheDir != null && cacheDir.isDirectory()) {
                File[] cachedFiles = cacheDir.listFiles();
                if (cachedFiles != null) {
                    for (File file : cachedFiles) {
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWeatherDataReceived(double temperature, int weatherCondition) {
        NavigationView navigationView = findViewById(R.id.navigationview);
        View headerView = navigationView.getHeaderView(0);
        TextView temptitle = headerView.findViewById(R.id.temptitle);
        temptitle.setText("Temperatura: " + String.valueOf(Math.round(temperature)) + "º");
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startInterstitialActivity(String videoUrl) {
        Intent intent = new Intent(this, InterstitialActivity.class);
        intent.putExtra("videoUrl", videoUrl);
        startActivity(intent);
    }

}
