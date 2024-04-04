package bee.corp.bplayer;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

public class MusicUtils {
    private static MediaPlayer player = new MediaPlayer();
    private static boolean IsPaused = false;
    private static boolean IsPrepared = false;
    @SuppressLint("Range")
    public static String getMusicFilePath(Context context, String musicTitle) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Media.DATA
        };

        String selection = MediaStore.Audio.Media.TITLE + "=?";
        String[] selectionArgs = new String[]{musicTitle};

        Cursor cursor = contentResolver.query(
                uri,
                projection,
                selection,
                selectionArgs,
                null
        );

        String filePath = null;
        if (cursor != null && cursor.moveToFirst()) {
            filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            cursor.close();
        }

        return filePath;
    }
    public static int GetCurrentMusicDuration() {
        if(player != null) {
            return player.getDuration();
        }
        return 0;
    }
    public static boolean IsCurrentMusicRunning() {
        if(player != null) {
            if(IsPrepared) {
                return player.isPlaying();
            }
        }
        return false;
    }
    public static void PlayMusic(File musicFile) throws IOException {
        FileInputStream fis = new FileInputStream(musicFile);
        FileDescriptor fd = fis.getFD();
        player.setDataSource(fd);
        player.prepare();
        IsPrepared = true;
        player.start();
        fis.close();
    }
    public static int GetCurrentMusicCurrentPosition() {
        if(player != null) {
            if(IsPrepared) {
                return player.getCurrentPosition();
            }
        }
        return 0;
    }
    public static void SetCurrentMusicPositionTo(int position) {
        if(player != null) {
            if(player.isPlaying()) {
                player.seekTo(position);
            }
        }
    }
    public static void ResumeCurrentMusic() {
        if(player != null) {
            if(IsPrepared) {
                player.start();
            }
        }
    }
    public static boolean IsCurrentMusicPaused() {
        return IsPaused;
    }
    public static void PauseCurrentMusic() {
        if(player != null) {
            if(player.isPlaying()) {
                player.pause();
                IsPaused = true;
            }
        }
    }
    public static void StopCurrentMusic() {
        if(player!=null) {
            if(player.isPlaying()) {
                player.stop();
            }
        }
    }
    public static void StopAndResetCurrentMusic() {
        if(player != null) {
            player.stop();
            player.reset();
            IsPrepared = false;
        } else {
            throw new NullPointerException("Current Player is null!");
        }
    }
}
