package com.gammery.trizzel.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Project: Trizzel
 * Author: Matías E. Vazquez (matiasevqz@gmail.com)
 * Github: https://github.com/mevqz
 */

public class MyDialogFragment extends DialogFragment {

    public static MyDialogFragment newInstance() {
        return new MyDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        View view = inflater.inflate(R.layout.info_dialog, null);
        TextView textView = (TextView) view.findViewById(R.id.soundoff_author_txt);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView = (TextView) view.findViewById(R.id.soundon_author_txt);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView = (TextView) view.findViewById(R.id.play_icon_author);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        builder.setView(view);

        final Dialog dialog = builder.create();

        return dialog;
    }
}