package com.example.sqlitedemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

public class StudentInfoDialogFragment extends DialogFragment {

    private String student_id;
    private String student_name;

    public static StudentInfoDialogFragment newInstance(String type) {
        StudentInfoDialogFragment myFragment = new StudentInfoDialogFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        myFragment.setArguments(args);
        return myFragment;
    }

    public interface AddDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String id, String name);

        public void onDialogNegativeClick(DialogFragment dialog);
    }

    AddDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (AddDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String type = getArguments().getString("type");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        LinearLayout add_layout = (LinearLayout) inflater.inflate(R.layout.add_student_info_layout, null);

        final EditText id_input = add_layout.findViewById(R.id.student_id);
        final EditText name_input = add_layout.findViewById(R.id.student_name);


        builder.setView(add_layout)
                .setTitle(type)
                .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        student_id = id_input.getText().toString();
                        student_name = name_input.getText().toString();

                        mListener.onDialogPositiveClick(StudentInfoDialogFragment.this, student_id, student_name);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogNegativeClick(StudentInfoDialogFragment.this);
                    }
                });
        return builder.create();
    }
}
