package com.info85.maiscolares;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ContactActivity extends AppCompatActivity {

    private EditText nameEditText, suggestionEditText, phoneEditText, captchaEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        nameEditText = findViewById(R.id.NameEditText);
        suggestionEditText = findViewById(R.id.SuggestionEditText);
        phoneEditText = findViewById(R.id.PhoneEditText);
        captchaEditText = findViewById(R.id.CaptchaEditText);

        Button submitButton = findViewById(R.id.SubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    private void submitForm() {
        String name = nameEditText.getText().toString().trim();
        String suggestion = suggestionEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String captcha = captchaEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(suggestion) ||
                TextUtils.isEmpty(phone) || TextUtils.isEmpty(captcha)) {
            // Exibir mensagem de erro se algum campo estiver vazio
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!captcha.equals("10")) {
            // Exibir mensagem de erro se o CAPTCHA estiver incorreto
            Toast.makeText(ContactActivity.this, "CAPTCHA incorreto", Toast.LENGTH_SHORT).show();
            return;
        }

        // Enviar os dados para o servidor PHP
        sendToServer(name, suggestion, phone, captcha);
    }

    private void sendToServer(String name, String suggestion, String phone, String captcha) {
        // URL do servidor PHP para inserção de dados
        String serverUrl = "https://maiscolares.info85.com.br/connmsg.php";

        // Parâmetros a serem enviados para o servidor
        Map<String, String> params = new HashMap<>();
        params.put("nome", name);
        params.put("sugestao", suggestion);
        params.put("telefone", phone);
        params.put("captcha", captcha);

        // Executar a tarefa assíncrona para enviar dados para o servidor
        new SendDataToServerTask().execute(serverUrl, params);
    }

    private class SendDataToServerTask extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... params) {
            String serverUrl = (String) params[0];
            Map<String, String> postData = (Map<String, String>) params[1];

            try {
                URL url = new URL(serverUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                StringBuilder postDataString = new StringBuilder();
                for (Map.Entry<String, String> entry : postData.entrySet()) {
                    postDataString.append(entry.getKey()).append("=")
                            .append(entry.getValue()).append("&");
                }
                os.write(postDataString.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Dados enviados com sucesso
                    return "Sugestão enviada com sucesso";
                } else {
                    return "Erro ao enviar sugestão para o servidor";
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Erro de conexão com o servidor";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Exibir AlertDialog quando os dados forem enviados com sucesso
            showSuccessDialog(result);
        }
    }

    private void showSuccessDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sucesso");
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Voltar para a MainActivity
            onBackPressed();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
