package eu.ramich.popularmovies.utilities;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PopularMoviesUtils {

    private static final String TAG = PopularMoviesUtils.class.getSimpleName();


    public static String normalizeDate(String rDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.US);
        Date date = null;
        try {
            date = simpleDateFormat.parse(rDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return DateFormat.getDateInstance(DateFormat.LONG).format(date);
    }

    public static String getPosterWidth(boolean poster) {
        int displayWidth = getDisplaySize().widthPixels;
        int displayHeight = getDisplaySize().heightPixels;

//        Log.v(TAG, "DisplaySize: " + displayHeight + "x" + displayWidth);

        if (displayWidth > displayHeight) {
            displayWidth = displayHeight;
        }

        if (poster) {
            displayWidth /= 3;
        }

        if (displayWidth <= 92) {
            return "w92";
        } else if (displayWidth <= 154) {
            return "w154";
        } else if (displayWidth <= 185) {
            return "w185";
        } else if (displayWidth <= 342) {
            return "w342";
        } else if (displayWidth <= 500) {
            return "w500";
        } else {
            return "w780";
        }
//        else {
//            return "original";
//        }

    }

    public static int getOptimalColumnCount(float posterWidth) {
        float displayWidth = getDisplaySize().widthPixels;

        return Math.max(Math.round(displayWidth / posterWidth), 1);
    }

    private static DisplayMetrics getDisplaySize() {
        return Resources.getSystem().getDisplayMetrics();
    }
}