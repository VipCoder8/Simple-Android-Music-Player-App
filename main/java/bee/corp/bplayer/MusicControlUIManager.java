package bee.corp.bplayer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.slider.Slider;

import java.io.File;
import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MusicControlUIManager {
    private int lastMusicTabSelectedIndex;
    private int newUsersChangedMusicPosition;
    private boolean userChangedMusicProgress = false;

    private File selectedMusicTabFile;
    private MusicLoader musicsLoader;
    private MusicsTabFinder musicsTabFinder;
    private ConstraintLayout controlPanel;
    public MusicControlUIManager(MusicLoader ml, MusicsTabFinder mtf, ConstraintLayout cl) {
        this.musicsLoader = ml;
        this.musicsTabFinder = mtf;
        this.controlPanel = cl;
    }
    public void updateListeners(Slider slider) {
        updateMusicTabControlButtons(slider);
        updateControlPanelControlButtons(slider);
    }
    public void updateSearchTextEditText(EditText searchText, ScrollView musicsTab, int indexOfTitleInView) {
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                musicsTabFinder.search(searchText.getText().toString(), musicsLoader.getMusicPanels(), indexOfTitleInView);
                musicsTab.scrollTo(0,0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void updateMusicTabControlButtons(Slider musicProgress) {
        //Play music with Music Tab's play_button ImageButton
        for(int i = 0; i < musicsLoader.getMusicPanels().size(); i++) {
            int selectedIndex = i;
            musicsLoader.getMusicPanels().get(i).getChildAt(1).setOnClickListener(v -> {
                if ((int) musicsLoader.getMusicPanels().get(selectedIndex).getChildAt(1).getTag() == R.drawable.play_icon) {
                    ((ImageButton) controlPanel.getChildAt(0)).setImageResource(R.drawable.pause_icon);
                    controlPanel.getChildAt(0).setTag(R.drawable.pause_icon);
                    ((ImageButton) musicsLoader.getMusicPanels().get(selectedIndex).getChildAt(1)).setImageResource(R.drawable.pause_icon);
                    musicsLoader.getMusicPanels().get(selectedIndex).getChildAt(1).setTag(R.drawable.pause_icon);
                    if (lastMusicTabSelectedIndex != selectedIndex) {
                        musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex).getChildAt(1).setTag(R.drawable.play_icon);
                        ((ImageButton) musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex).getChildAt(1)).setImageResource(R.drawable.play_icon);
                        MusicUtils.StopAndResetCurrentMusic();
                    }
                    lastMusicTabSelectedIndex = selectedIndex;
                    selectedMusicTabFile = new File((String) musicsLoader.getMusicPanels().get(selectedIndex).getChildAt(3).getTag());
                    try {
                        MusicUtils.PlayMusic(selectedMusicTabFile);
                    } catch (IOException e) {
                        MusicUtils.StopAndResetCurrentMusic();
                        throw new RuntimeException(e);
                    }
                    //Update and check Music Progress states
                    updateCheckMusicProgress(0, MusicUtils.GetCurrentMusicDuration(), selectedIndex, musicProgress);
                } else if ((int) musicsLoader.getMusicPanels().get(selectedIndex).getChildAt(1).getTag() == R.drawable.pause_icon) {
                    ((ImageButton) controlPanel.getChildAt(0)).setImageResource(R.drawable.play_icon);
                    controlPanel.getChildAt(0).setTag(R.drawable.play_icon);
                    ((ImageButton) musicsLoader.getMusicPanels().get(selectedIndex).getChildAt(1)).setImageResource(R.drawable.play_icon);
                    musicsLoader.getMusicPanels().get(selectedIndex).getChildAt(1).setTag(R.drawable.play_icon);
                    MusicUtils.StopAndResetCurrentMusic();
                }
            });
            //Play previous Song using Music Tab's previous_button ImageButton
            musicsLoader.getMusicPanels().get(i).getChildAt(0).setOnClickListener(v -> {
                if (lastMusicTabSelectedIndex - 1 >= 0) {
                    ((ImageButton) controlPanel.getChildAt(0)).setImageResource(R.drawable.pause_icon);
                    controlPanel.getChildAt(0).setTag(R.drawable.pause_icon);

                    lastMusicTabSelectedIndex--;
                    ((ImageButton) musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex+1).getChildAt(1)).setImageResource(R.drawable.play_icon);
                    musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex+1).getChildAt(1).setTag(R.drawable.play_icon);

                    MusicUtils.StopAndResetCurrentMusic();

                    ((ImageButton) musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex).getChildAt(1)).setImageResource(R.drawable.pause_icon);
                    musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex).getChildAt(1).setTag(R.drawable.pause_icon);

                    selectedMusicTabFile = new File((String) musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex).getChildAt(3).getTag());
                    try {
                        MusicUtils.PlayMusic(selectedMusicTabFile);
                    } catch (IOException e) {
                        MusicUtils.StopAndResetCurrentMusic();
                        throw new RuntimeException(e);
                    }
                    updateCheckMusicProgress(0, MusicUtils.GetCurrentMusicDuration(), lastMusicTabSelectedIndex, musicProgress);
                }
            });
            //Play next Song using Music Tab's previous_button ImageButton
            musicsLoader.getMusicPanels().get(i).getChildAt(2).setOnClickListener(v -> {
                if (lastMusicTabSelectedIndex + 1 <= musicsLoader.getMusicPanels().size()-1) {
                    ((ImageButton) controlPanel.getChildAt(0)).setImageResource(R.drawable.pause_icon);
                    controlPanel.getChildAt(0).setTag(R.drawable.pause_icon);

                    ((ImageButton) musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex).getChildAt(1)).setImageResource(R.drawable.play_icon);
                    musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex).getChildAt(1).setTag(R.drawable.play_icon);

                    lastMusicTabSelectedIndex++;

                    MusicUtils.StopAndResetCurrentMusic();

                    ((ImageButton) musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex).getChildAt(1)).setImageResource(R.drawable.pause_icon);
                    musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex).getChildAt(1).setTag(R.drawable.pause_icon);

                    selectedMusicTabFile = new File((String) musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex).getChildAt(3).getTag());
                    try {
                        MusicUtils.PlayMusic(selectedMusicTabFile);
                    } catch (IOException e) {
                        MusicUtils.StopAndResetCurrentMusic();
                        throw new RuntimeException(e);
                    }
                    updateCheckMusicProgress(0, MusicUtils.GetCurrentMusicDuration(), lastMusicTabSelectedIndex, musicProgress);
                }
            });
        }
    }
    private void updateControlPanelControlButtons(Slider musicProgress) {
        //Play previous music with Control Panel's control_panel_next_button ImageButton
        controlPanel.getChildAt(1).setOnClickListener(v -> {
            if (lastMusicTabSelectedIndex - 1 >= 0) {
                ((ImageButton) controlPanel.getChildAt(0)).setImageResource(R.drawable.pause_icon);
                controlPanel.getChildAt(0).setTag(R.drawable.pause_icon);

                lastMusicTabSelectedIndex--;

                ((ImageButton) musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex+1).getChildAt(1)).setImageResource(R.drawable.play_icon);
                musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex+1).getChildAt(1).setTag(R.drawable.play_icon);

                MusicUtils.StopAndResetCurrentMusic();

                ((ImageButton) musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex).getChildAt(1)).setImageResource(R.drawable.pause_icon);
                musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex).getChildAt(1).setTag(R.drawable.pause_icon);

                selectedMusicTabFile = new File((String) musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex).getChildAt(3).getTag());
                try {
                    MusicUtils.PlayMusic(selectedMusicTabFile);
                } catch (IOException e) {
                    MusicUtils.StopAndResetCurrentMusic();
                    throw new RuntimeException(e);
                }
                updateCheckMusicProgress(0, MusicUtils.GetCurrentMusicDuration(), lastMusicTabSelectedIndex, musicProgress);
            }
        });
        //Play next Song using Control Panel's control_panel_previous_button ImageButton
        controlPanel.getChildAt(2).setOnClickListener(v -> {
            if (lastMusicTabSelectedIndex + 1 <= musicsLoader.getMusicPanels().size()-1) {
                ((ImageButton) controlPanel.getChildAt(0)).setImageResource(R.drawable.pause_icon);
                controlPanel.getChildAt(0).setTag(R.drawable.pause_icon);

                lastMusicTabSelectedIndex++;

                ((ImageButton) musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex-1).getChildAt(1)).setImageResource(R.drawable.play_icon);
                musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex-1).getChildAt(1).setTag(R.drawable.play_icon);

                MusicUtils.StopAndResetCurrentMusic();

                ((ImageButton) musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex).getChildAt(1)).setImageResource(R.drawable.pause_icon);
                musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex).getChildAt(1).setTag(R.drawable.pause_icon);

                selectedMusicTabFile = new File((String) musicsLoader.getMusicPanels().get(lastMusicTabSelectedIndex).getChildAt(3).getTag());
                try {
                    MusicUtils.PlayMusic(selectedMusicTabFile);
                } catch (IOException e) {
                    MusicUtils.StopAndResetCurrentMusic();
                    throw new RuntimeException(e);
                }
                updateCheckMusicProgress(0, MusicUtils.GetCurrentMusicDuration(), lastMusicTabSelectedIndex, musicProgress);
            }
        });
        //Change Control Panel's Play/Stop ImageButton
        controlPanel.getChildAt(0).setOnClickListener(v -> {
            if((int)controlPanel.getChildAt(0).getTag()==R.drawable.play_icon) {
                if(MusicUtils.IsCurrentMusicPaused()) {
                    MusicUtils.ResumeCurrentMusic();
                    ((ImageButton)controlPanel.getChildAt(0)).setImageResource(R.drawable.pause_icon);
                    controlPanel.getChildAt(0).setTag(R.drawable.pause_icon);
                }
            } else if((int)controlPanel.getChildAt(0).getTag()==R.drawable.pause_icon) {
                if(MusicUtils.IsCurrentMusicRunning()) {
                    MusicUtils.PauseCurrentMusic();
                    ((ImageButton)controlPanel.getChildAt(0)).setImageResource(R.drawable.play_icon);
                    controlPanel.getChildAt(0).setTag(R.drawable.play_icon);
                }
            }
        });
    }
    private void updateCheckMusicProgress(int min, int max, int currentMusicPlaying, Slider musicProgress) {
        musicProgress.setValueFrom(min);
        musicProgress.setValueTo(max);
        musicProgress.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                userChangedMusicProgress = true;
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                newUsersChangedMusicPosition = (int) slider.getValue();
                musicProgress.setValue(newUsersChangedMusicPosition);
                MusicUtils.SetCurrentMusicPositionTo((int) newUsersChangedMusicPosition);
                if(!MusicUtils.IsCurrentMusicRunning()) {
                    MusicUtils.ResumeCurrentMusic();
                }
                userChangedMusicProgress = false;
            }
        });
        Disposable progressUpdaterDisposable;
        Observable<Integer> dynamicProgressObservable = Observable.create(emitter -> {
            int progress = min;
            while(true) {
                if(!userChangedMusicProgress && progress < musicProgress.getValueTo() && (int)controlPanel.getChildAt(0).getTag()==R.drawable.pause_icon) {
                    emitter.onNext(progress);
                    progress = MusicUtils.GetCurrentMusicCurrentPosition();
                    Thread.sleep(500);
                }
                if(progress >= musicProgress.getValueTo() || !MusicUtils.IsCurrentMusicRunning() && !MusicUtils.IsCurrentMusicPaused()) {
                    if(progress > musicProgress.getValueTo()) {
                        progress = (int)musicProgress.getValueTo();
                    }
                    emitter.onNext(progress);
                    ((ImageButton)controlPanel.getChildAt(0)).setImageResource(R.drawable.play_icon);
                    ((ImageButton)musicsLoader.getMusicPanels().get(currentMusicPlaying).getChildAt(1)).setImageResource(R.drawable.play_icon);
                    break;
                }
            }
        });
        progressUpdaterDisposable = dynamicProgressObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(musicProgress::setValue);
    }
}
