package eu.ramich.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import eu.ramich.popularmovies.R;

public final class PopularMoviesPreferences {

    public static void setSortOrder(Context context, int sortOrder) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        String keyForSortOrder = context.getString(R.string.sort_order_key);

        editor.putInt(keyForSortOrder, sortOrder);
        editor.apply();
    }

    public static int getSortOrder(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String keyForSortOrder = context.getString(R.string.sort_order_key);
        int defaultSortOrder = 0;

        return sp.getInt(keyForSortOrder, defaultSortOrder);
    }

    public static String getSortOrderKey(Context context) {
        int sortOrder = getSortOrder(context);

        String[] sortOrderKeys = context.getResources()
                .getStringArray(R.array.pref_sort_order_values);

        return sortOrderKeys[sortOrder];
    }

    public static String getSortOrderLabel(Context context) {
        int sortOrder = getSortOrder(context);

        String[] sortOrderLabels = context.getResources()
                .getStringArray(R.array.pref_sort_order_entries);

        return sortOrderLabels[sortOrder];
    }

    public static String[] getAllSortOrders(Context context) {
        return context.getResources().getStringArray(R.array.pref_sort_order_values);
    }

}
