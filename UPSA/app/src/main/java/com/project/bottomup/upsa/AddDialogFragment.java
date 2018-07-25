package com.project.bottomup.upsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class AddDialogFragment extends DialogFragment{
    public interface OnCompleteListener{
        void onInputedData(String admit);
    }

    private OnCompleteListener mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnCompleteListener) activity;
        }
        catch (ClassCastException e) {
            Log.d("AddDialogFragment", "Activity doesn't implement the OnCompleteListener interface");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_dialog, null);
        builder.setView(view);
        final Button OK = (Button) view.findViewById(R.id.OKbutton);
        final Button NO = (Button) view.findViewById(R.id.NObutton);

        OK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
                mCallback.onInputedData("OK");
            }
        });
        NO.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
                mCallback.onInputedData("NO");
            }
        });
        return builder.create();
    }
}