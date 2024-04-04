package bee.corp.bplayer;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.ArrayList;
public class MusicsTabFinder {
    public MusicsTabFinder() {}
    public void search(String toFind, ArrayList<ConstraintLayout> whereToFind, int indexOfTitleInView) {
        float y = whereToFind.get(0).getY();
        int height = 69;
        if(toFind.trim().length()>0) {
            for(int i = 0; i < whereToFind.size();i++) {
                whereToFind.get(i).setVisibility(View.INVISIBLE);
                if(((TextView)whereToFind.get(i).getChildAt(indexOfTitleInView)).getText().toString().toLowerCase().contains(toFind.toLowerCase())) {
                    whereToFind.get(i).setY(y);
                    y+=height;
                    whereToFind.get(i).setVisibility(View.VISIBLE);
                } else {
                    whereToFind.get(i).setVisibility(View.INVISIBLE);
                }
            }
        } else {
            for(int i = 0; i < whereToFind.size();i++) {
                whereToFind.get(i).setY(y);
                y+=height;
                whereToFind.get(i).setVisibility(View.VISIBLE);
            }
        }
    }
}
