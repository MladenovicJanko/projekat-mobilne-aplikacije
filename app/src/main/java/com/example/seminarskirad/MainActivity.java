package com.example.seminarskirad;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaRecorder;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    //Svojstva za postavljanje režima pregleda i njegovo čuvanje u SharedPreferences.
    private static final String PREFS_NAME = "settings_prefs";
    private static final String KEY_DARK_MODE = "dark_mode_enabled";

    //Svojstva za preuzimanje dozvola od korisnika prilikom pokretanja aplikacije.
    private static final int REQUEST_PERMISSION_CODE = 1000;
    private final String[] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_MEDIA_AUDIO,
    };

    //Svojstva za prikaz menija, navigaciju i toolbar.
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    //Svojstva za kontrolu snimanja i naziva fajla.
    private boolean isRecording = false;
    private MediaRecorder recorder;
    private File audioFile;
    private String fileName;

    //Dugme i textview na View-u koji prikazuju akciju i status snimanja.
    private FloatingActionButton fab;
    private TextView txtStatus;

    //OnCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Pozivanje metoda za prihvatanje dozvola prilikom pokretanja aplikacije.
        if (!checkPermissions()) {
            requestPermissions();
        }

        //Učitavanje režima prikaza iz SharedPreferences aplikacije.
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean darkMode = prefs.getBoolean(KEY_DARK_MODE, false);

        //Postavka režima prikaza.
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        //Inicijalizacija i preuzimanje sadržaja na View-u
        fab = findViewById(R.id.fabRecord);
        txtStatus = findViewById(R.id.txtStatus);
        txtStatus.setText("Započni snimanje pritiskom na dugme");

        //Postavka menija, toolbar-a i navigacije.
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Hamburger meni sa akcijama.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Izvršavanje akcije nakon korisničkog odabira stavke menija.
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_view_recordings) {
                startActivity(new Intent(this, RecordingsViewerActivity.class));
            }
            else if (id == R.id.nav_cat_facts) {
                startActivity(new Intent(this, CatFactViewerActivity.class));
            }
            else if (id == R.id.nav_toggle_theme) {
                toggleTheme();
            } else if (id == R.id.nav_exit) {
                finishAffinity(); // Zatvara sve aktivnosti
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        //Početak snimanja ili zahtevanje dozvola klikom na dugme za snimanje.
        fab.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else {
                if (checkPermissions()) {
                    startRecording();
                } else {
                    requestPermissions();
                }
            }
        });
    }

    //Metoda koja proverava da li su dozvolje prihvaćene od strane korisnika aplikacije.
    private boolean checkPermissions() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    //Metoda koja zahteva prihvatanje dozvola od korisnika prilikom pokretanja aplikacije.
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE);
    }

    //Override metode za traženje dozvola od korisnika.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                Toast.makeText(this, "Dozvole su uspešno odobrene!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Dozvole su neophodne za rad aplikacije!", Toast.LENGTH_LONG).show();
            }
        }
    }

    //Metoda koja menja režim prikaza i upisuje ga u SharedPreferences. (Prvo je light mode pa dark mode)
    private void toggleTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;

        SharedPreferences.Editor editor = prefs.edit();
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor.putBoolean(KEY_DARK_MODE, false);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor.putBoolean(KEY_DARK_MODE, true);
        }
        editor.apply();

        recreate(); // Restartuje UI da se primeni tema.
    }

    //Metoda koja započinje snimanje zvuka
    private void startRecording() {

        //Provera dozvola za korisćenje aplikacije.
        if (!checkPermissions()) {
            requestPermissions();
            return;
        }

        try {
            //Definisanje naziva snimka.
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            fileName = "SNIMAK_" + timeStamp + ".3gp";

            //Definisanje putanje snimanja fajla.
            File outputDir = getExternalFilesDir(null);
            audioFile = new File(outputDir, fileName);

            //Definisanje MediaRecorder-a i njegovih parametara.
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(audioFile.getAbsolutePath());
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            //Startovanje snimananja.
            recorder.prepare();
            recorder.start();

            //Kontrola omogućenosti akcija.
            isRecording = true;
            txtStatus.setText("Snimanje u toku...");
            fab.setImageResource(android.R.drawable.ic_media_pause);

            Toast.makeText(this, "Snimanje u toku...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Greška pri početku snimanju", Toast.LENGTH_SHORT).show();
        }
    }

    //Metoda za zaustavljanje snimanja i čuvanje snimka na izabranoj lokaciji.
    private void stopRecording() {
        try {
            //Zaustaljanje snimanja.
            recorder.stop();
            recorder.release();

            //Oslobađanje memorije za recorder.
            recorder = null;

            //Kontrola omogućenosti akcija.
            isRecording = false;
            txtStatus.setText("Snimljeno: " + audioFile.getName() + "\nZapočni novo snimanje pritiskom na dugme");
            fab.setImageResource(android.R.drawable.ic_btn_speak_now);

            Toast.makeText(this, "Snimljeno: " + fileName, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Greška pri zaustavljanju", Toast.LENGTH_SHORT).show();
        }
    }
}