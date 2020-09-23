package app.calcounterapplication.com.tcc.helper;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

//CLASSE OBSOLETA

public class DataFragment extends Fragment
{
    private Button selecionarData;

    public void displaydate(int year, int monthOfYear, int dayOfMonth) {

        String date = String.format("%02d", dayOfMonth) + "/" + String.format("%02d", monthOfYear + 1) + "/" + String.format("%02d", year);
        Log.d("DTAG", "date: "+date);
    }
    //..code oncreateview

    public void showDatePickerDialog(View v)
    {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");

    }

}