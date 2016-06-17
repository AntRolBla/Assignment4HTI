package a2id40.thermostatapp.fragments.Utils;

import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import a2id40.thermostatapp.R;

/**
 * Created by IsabelGomes on 17/06/16.
 */
public class SnackBarHelper {

    public static void showErrorSnackBar(View view){
        Snackbar mSnackbar = Snackbar.make(view, "There has been an error while accessing the server. Please try again.", Snackbar.LENGTH_LONG);
        mSnackbar.getView().setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.lightRed));
        mSnackbar.show();
    }

    public static void showErrorOnAddingTimeslot(View view){
        Snackbar mSnackbar = Snackbar.make(view, "There has been an error while accessing the server. Please try adding your timeslot again.", Snackbar.LENGTH_LONG);
        mSnackbar.getView().setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.lightRed));
        mSnackbar.show();
    }

    public static void showErrorOnRemovingTimeslot(View view){
        Snackbar mSnackbar = Snackbar.make(view, "There has been an error while accessing the server. Please try removing your timeslot again.", Snackbar.LENGTH_LONG);
        mSnackbar.getView().setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.lightRed));
        mSnackbar.show();
    }

}
