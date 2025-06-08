package com.example.seminarskirad;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CatFactViewerActivity extends AppCompatActivity {

    //Dugmiči za učitavanje nove činjenice o mačkama i povrata na main page.
    private Button btnLoad, btnBack;

    //OnCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cat_facts_view);

        //Preuzimanje dugmića sa forme.
        btnLoad = findViewById(R.id.btnLoadImage);
        btnBack = findViewById(R.id.btnBack);

        //Postavljanje akcija dugmićima sa forme.
        btnLoad.setOnClickListener(v -> loadRandomCatFactInTextView());
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    //Metoda za preuzimanje nove činjenice preko interneta.
    private void loadRandomCatFactInTextView() {

        new Thread(() -> {  //Pokreće se u nezavisnoj niti kao poseban proces.
            try {
                //Definisanje osnovnih parametara za konekciju ka API-ju.
                URL url = new URL("https://catfact.ninja/fact");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                //Preuzimanje sadržaja sa API-ja.
                InputStream in = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                //Izvlačenje tekstualnog sadržaja iz odgovora API-ja.
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                connection.disconnect();    //Završetak konekcije

                //Pretvaranje odgovora u JSON objekat.
                JSONObject jsonObject = new JSONObject(response.toString());

                //Izvlačenje fact sadržaja iz objekta odgovora.
                String fact = jsonObject.getString("fact");

                //U glavnoj niti izvršenja programa postavljano tekst odgovora.
                runOnUiThread(() -> {
                    TextView textView = findViewById(R.id.catFactTextView);
                    textView.setText(fact);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Greška pri preuzimanju ili parsiranju!", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
