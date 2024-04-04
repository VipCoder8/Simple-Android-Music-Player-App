package bee.corp.bplayer;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.File;
import java.util.ArrayList;

public class MusicLoader {
    private float panelsLayoutHeight = 0;
    private RelativeLayout panelsLayout;
    private ArrayList<ConstraintLayout> musicTabs;
    private Utils utils;
    public MusicLoader(ScrollView musicsTab, Activity ac, Context c, int startY) {
        utils = new Utils(ac);
        panelsLayout = new RelativeLayout(c);
        panelsLayout.setLayoutParams(new RelativeLayout.LayoutParams(musicsTab.getWidth(), (int)panelsLayoutHeight));
        panelsLayout.setVisibility(View.VISIBLE);
        musicTabs = new ArrayList<>();
        loadMusic(musicsTab, c, startY);
    }
    private void loadMusic(ScrollView mt, Context c, int startY) {
        ContentResolver contentResolver = c.getContentResolver();
        String[] projection = {
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION};
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, projection,null,null,null);
        int titleIndex = cursor.getColumnIndex(projection[2]);
        int artistIndex = cursor.getColumnIndex(projection[3]);
        while(cursor.moveToNext()) {
            String path = MusicUtils.getMusicFilePath(c, cursor.getString(titleIndex));
            if(new File(path).exists()) {
                String title = cursor.getString(titleIndex);
                String artist = cursor.getString(artistIndex);
                ConstraintLayout musicTab = (ConstraintLayout) utils.getLayout(R.layout.music_tab, null);
                musicTab.findViewById(R.id.play_button).setTag(R.drawable.play_icon);
                ((TextView)musicTab.findViewById(R.id.title_text)).setText(title);
                musicTab.findViewById(R.id.title_text).setTag(path);
                if(((TextView)musicTab.findViewById(R.id.title_text)).getText().length()>26) {
                    ((TextView)musicTab.findViewById(R.id.title_text)).setText(title.substring(0, 26) + "...");
                }
                if(artist.trim().length()==0) {
                    ((TextView)musicTab.findViewById(R.id.title_text)).setText(((TextView)musicTab.findViewById(R.id.title_text)).getText() + " - " + "<unknown>");
                } else {
                    ((TextView)musicTab.findViewById(R.id.title_text)).setText(((TextView)musicTab.findViewById(R.id.title_text)).getText() + " - " + artist);
                }
                musicTab.setY(startY);
                this.panelsLayout.addView(musicTab);
                startY+=69f;
                panelsLayoutHeight += 69.5f;
                musicTabs.add(musicTab);
            }
        }
        this.panelsLayout.setLayoutParams(new RelativeLayout.LayoutParams(mt.getWidth(), (int)panelsLayoutHeight));
        mt.addView(panelsLayout);
    }
    public ArrayList<ConstraintLayout> getMusicPanels() {return this.musicTabs;}
}
