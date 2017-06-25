package eu.ramich.popularmovies.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import eu.ramich.popularmovies.R;
import eu.ramich.popularmovies.data.PopularMoviesPreferences;

public class SortOrderFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.sort_order_label)
                .setSingleChoiceItems(R.array.pref_sort_order_entries,
                        PopularMoviesPreferences.getSortOrder(getActivity()),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PopularMoviesPreferences.setSortOrder(getActivity(), which);
                                dismiss();
                            }
                        });

        return builder.create();
    }
}
