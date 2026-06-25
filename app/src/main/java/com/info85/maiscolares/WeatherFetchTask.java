package com.info85.maiscolares;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFetchTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = WeatherFetchTask.class.getSimpleName();
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather?units=metric&q=colares,br&appid=6508c80d1115b29a5bb0e65c0009916d";

    private WeatherDataCallback callback;

    public WeatherFetchTask(WeatherDataCallback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                return response.toString();
            } else {
                Log.e(TAG, "Erro na solicitação HTTP. Código de resposta: " + responseCode);
            }
        } catch (IOException e) {
            Log.e(TAG, "Exceção ao obter previsão do tempo: " + e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(String response) {
        if (response != null) {
            try {
                JSONObject json = new JSONObject(response);
                JSONArray weatherArray = json.getJSONArray("weather");
                JSONObject weatherObject = weatherArray.getJSONObject(0);
                int weatherCondition = weatherObject.getInt("id");
                double temperature = json.getJSONObject("main").getDouble("temp");

                if (callback != null) {
                    callback.onWeatherDataReceived(temperature, weatherCondition);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Exceção ao analisar resposta JSON: " + e.getMessage());
            }
        } else {
            // Lidar com erros de solicitação da API
        }
    }

    public interface WeatherDataCallback {
        void onWeatherDataReceived(double temperature, int weatherCondition);
    }

}
