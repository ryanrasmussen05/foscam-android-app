package com.rhino.foscam.dialog;

import com.rhino.foscam.R;

import android.app.*;
import android.os.Bundle;
import android.view.LayoutInflater;

public class ConnectDialog extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.connect_dialog, null));
        return builder.create();
    }

}