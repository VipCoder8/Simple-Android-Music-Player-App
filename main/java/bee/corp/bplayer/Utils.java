package bee.corp.bplayer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Utils {
    LayoutInflater layoutInflater;
    public Utils(Activity a) {
        layoutInflater = a.getLayoutInflater();
    }
    public View getLayout(int layout, ViewGroup mainLayout) {
        return layoutInflater.inflate(layout, mainLayout);
    }
}
