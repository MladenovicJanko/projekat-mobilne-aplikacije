package com.example.seminarskirad; // promeni u tvoj paket

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecordingsViewerActivity extends AppCompatActivity {

    //Podaci na View-u.
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<File> recordingFiles = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private File currentlyPlayingFile = null;
    private Button btnBack;

    //OnCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recordings_view);

        //Preuzimanje dugmića i liste sa View-a.
        btnBack = findViewById(R.id.btnBack);
        listView = findViewById(R.id.listViewRecordings);

        //Preuzimanje svih .3gp fajlova iz foldera aplikacije.
        loadRecordingFiles();

        //Dodavanje naziva fajlova u listu na View-u.
        List<String> fileNames = new ArrayList<>();
        for (File file : recordingFiles) {
            fileNames.add(file.getName());
        }

        //Inicijalizacija za reprodukciju zvuka.
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileNames);
        listView.setAdapter(adapter);

        //Dodavanje akcije listi tako da reprodukuje zvuk na tap item-a.
        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            File selectedFile = recordingFiles.get(position);
            handlePlayback(selectedFile);
        });

        //Dodavanje akcije za povratak na main page.
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    //Metoda za preuzimanje svih .3gp fajlova iz foldera aplikacije.
    private void loadRecordingFiles() {
        File recordingsDir = getExternalFilesDir(null);
        if (recordingsDir != null) {
            File[] files = recordingsDir.listFiles((dir, name) -> name.endsWith(".3gp"));
            if (files != null) {
                for (File file : files) {
                    recordingFiles.add(file);
                }
            }
        }
    }

    //Metoda koja reprodukuje ili pauzira snimljeni sadržaj prikazan u listi na View-u.
    private void handlePlayback(File file) {
        try {
            //Ako je player već pokrenut onda se reprodukcija pauzira.
            if (mediaPlayer != null && isPlaying && file.equals(currentlyPlayingFile)) {
                mediaPlayer.pause();
                isPlaying = false;
                Toast.makeText(this, "Snimak pauziran", Toast.LENGTH_SHORT).show();
            } else {
                //Ako player nije pokrenut onda se reprodukuje izabrani snimak iz liste.
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(file.getAbsolutePath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                currentlyPlayingFile = file;
                isPlaying = true;

                Toast.makeText(this, "Reprodukcija: " + file.getName(), Toast.LENGTH_SHORT).show();

                //Kada se reprodukcija završi flag-ovi se vrate na default i može se reprodukovati sledeći snimak.
                mediaPlayer.setOnCompletionListener(mp -> {
                    isPlaying = false;
                    currentlyPlayingFile = null;
                    Toast.makeText(this, "Reprodukcija završena", Toast.LENGTH_SHORT).show();
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Greška pri reprodukciji!", Toast.LENGTH_SHORT).show();
        }
    }

    //Po izlasku iz aktivnosti čisti se memorija i uništava mediaPlayer.
    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
