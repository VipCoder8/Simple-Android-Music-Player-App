package bee.corp.bplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.content.pm.PackageManager;

import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;

import com.google.android.material.slider.Slider;

import java.io.File;
import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    ScrollView musicsTab;
    Slider musicProgress;
    EditText searchText;
    ConstraintLayout controlPanel;

    MusicLoader musicsLoader;
    MusicsTabFinder musicsTabFinder;
    MusicControlUIManager musicControlUIManager;
    int indexOfTitleInView = 3;
    int startYForMusicTabs = 25;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
    }

    private void setupViews() {
        musicsTab = findViewById(R.id.musics_tab);
        musicsTab.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE);
            updateMusicPanelsListeners();
        });
        musicProgress = findViewById(R.id.music_progress);
        searchText = findViewById(R.id.search_field);
        controlPanel = findViewById(R.id.control_panel);
    }

    private void setupUtils() {
        if(musicsLoader == null) {
            musicsLoader = new MusicLoader(musicsTab, this, getApplicationContext(),startYForMusicTabs);
        }
        if(musicsTabFinder == null) {
            musicsTabFinder = new MusicsTabFinder();
        }
        if(musicControlUIManager == null) {
            musicControlUIManager = new MusicControlUIManager(musicsLoader, musicsTabFinder, controlPanel);
        }
    }

    private void updateMusicPanelsListeners() {
        musicControlUIManager.updateListeners(musicProgress);
        musicControlUIManager.updateSearchTextEditText(searchText, musicsTab, indexOfTitleInView);
    }

    private void requestPermissions(String perms) {
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), perms) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{perms},0);
        } else {
            setupUtils();
        }
    }

}