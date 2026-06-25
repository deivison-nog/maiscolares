package com.info85.maiscolares;

import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;

public class Game extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        webView = findViewById(R.id.gameView);

        // Habilita JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Habilita Armazenamento Local
        webSettings.setDomStorageEnabled(true);

        // Habilita Cookies
        CookieManager.getInstance().setAcceptCookie(true);

        // Habilita Cache do WebView
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        // Habilita Armazenamento de Banco de Dados
        webSettings.setDatabaseEnabled(true);

        // Habilita a Execução Remota de JavaScript
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        // Habilita Zoom na Página
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        // Define um WebChromeClient para suportar funcionalidades do Chrome, como o Geolocalizador, por exemplo.
        webView.setWebChromeClient(new WebChromeClient());

        // Carrega a URL
        webView.loadUrl("https://www.bkbet.com/?id=11771641");
    }
}
