package com.example.sqlitedemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sqlitedemo.db.StudentInfoContract;
import com.example.sqlitedemo.db.StudentInfoDbHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, StudentInfoDialogFragment.AddDialogListener {

    private static final String TAG = "DBDEMO";

    private StudentInfoDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_add = findViewById(R.id.btn_add);
        Button btn_delete = findViewById(R.id.btn_delete);
        Button btn_update = findViewById(R.id.btn_update);
        Button btn_query = findViewById(R.id.btn_query);

        btn_add.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        btn_query.setOnClickListener(this);

        mDbHelper = new StudentInfoDbHelper(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                DialogFragment addFragment = StudentInfoDialogFragment.newInstance("Add");
                addFragment.show(getSupportFragmentManager(), "add");
                break;
            case R.id.btn_delete:
                DialogFragment deleteFragment = StudentInfoDialogFragment.newInstance("Delete");
                deleteFragment.show(getSupportFragmentManager(), "delete");
                break;
            case R.id.btn_update:
                DialogFragment updateFragment = StudentInfoDialogFragment.newInstance("Update");
                updateFragment.show(getSupportFragmentManager(), "update");
                break;
            case R.id.btn_query:
                DialogFragment queryFragment = StudentInfoDialogFragment.newInstance("Query");
                queryFragment.show(getSupportFragmentManager(), "query");
                break;
            default:
                break;
        }
    }

    private void addStudentInfo(DialogFragment dialog, String id, String name) {
        if (!id.isEmpty() && !name.isEmpty() && !checkIdExist(id)) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_ID, id);
            values.put(StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_NAME, name);

            db.insert(StudentInfoContract.StudentEntry.TABLE_NAME, null, values);
            Toast.makeText(getApplicationContext(), "Add new data: " + "id is " + id + " name is "
                    + name, Toast.LENGTH_SHORT).show();

            Log.i(TAG, "add data: " + "id is " + id + " name is " + name);
        } else {
            Toast.makeText(getApplicationContext(), "Data should not be null or id existed", Toast.LENGTH_LONG).show();
            dialog.getDialog().cancel();
        }
    }

    private void deleteStudentInfo(DialogFragment dialog, String id, String name) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String nameSelection = StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_NAME + " = ?";
        String[] nameSelectionArgs = {name};
        String idSelection = StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_ID + " = ?";
        String[] idSelectionArgs = {id};

        if (id.isEmpty() && name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Data should not be null", Toast.LENGTH_LONG).show();
            dialog.getDialog().cancel();
        } else if (id.isEmpty()) {
            db.delete(StudentInfoContract.StudentEntry.TABLE_NAME, nameSelection, nameSelectionArgs);
            Toast.makeText(getApplicationContext(), "Data delete: " + " name is "
                    + name, Toast.LENGTH_LONG).show();
        } else if (name.isEmpty()) {
            db.delete(StudentInfoContract.StudentEntry.TABLE_NAME, idSelection, idSelectionArgs);
            Toast.makeText(getApplicationContext(), "Data delete: " + "id is "
                    + id, Toast.LENGTH_LONG).show();
        } else {
            db.delete(StudentInfoContract.StudentEntry.TABLE_NAME,
                    idSelection + " and " + nameSelection, new String[]{id, name});
            Toast.makeText(getApplicationContext(), "Data delete: " + "id is " + id + " name is "
                    + name, Toast.LENGTH_LONG).show();
        }
    }

    private void updateStudentInfo(DialogFragment dialog, String id, String name) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String idSelection = StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_ID + " = ?";
        String[] idSelectionArgs = {id};
        ContentValues values = new ContentValues();
        values.put(StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_NAME, name);

        if (id.isEmpty() || name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Data should not be null", Toast.LENGTH_LONG).show();
            dialog.getDialog().cancel();
        } else if (checkIdExist(id)) {
            db.update(
                    StudentInfoContract.StudentEntry.TABLE_NAME,
                    values,
                    idSelection,
                    idSelectionArgs);
            Toast.makeText(getApplicationContext(), "Data update: " + "id is " + id + " name is "
                    + name, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Data not exist", Toast.LENGTH_LONG).show();
            dialog.getDialog().cancel();
        }
    }

    private void queryStudentInfo(DialogFragment dialog, String id, String name) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_ID,
                StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_NAME
        };
        String nameSelection = StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_NAME + " = ?";
        String[] nameSelectionArgs = {name};
        String idSelection = StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_ID + " = ?";
        String[] idSelectionArgs = {id};

        String sortOrder = BaseColumns._ID + " ASC";
        List<StudentInfo> retList = new ArrayList<>();
        if (id.isEmpty() && name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Data should not be null", Toast.LENGTH_LONG).show();
            dialog.getDialog().cancel();
        } else if (id.isEmpty()) {
            Cursor cursor = db.query(
                    StudentInfoContract.StudentEntry.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    nameSelection,              // The columns for the WHERE clause
                    nameSelectionArgs,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrder               // The sort order
            );
            while (cursor.moveToNext()) {
                long index = cursor.getLong(
                        cursor.getColumnIndexOrThrow(StudentInfoContract.StudentEntry._ID));
                name = cursor.getString(cursor.getColumnIndex(StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_NAME));
                id = cursor.getString(cursor.getColumnIndex(StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_ID));
                retList.add(new StudentInfo(index, id, name));
                Log.i(TAG, "query data: " + index + " - " + name + " - " + id);
            }
            cursor.close();
        } else if (name.isEmpty()) {
            Cursor cursor = db.query(
                    StudentInfoContract.StudentEntry.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    idSelection,              // The columns for the WHERE clause
                    idSelectionArgs,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrder               // The sort order
            );
            while (cursor.moveToNext()) {
                long index = cursor.getLong(
                        cursor.getColumnIndexOrThrow(StudentInfoContract.StudentEntry._ID));
                name = cursor.getString(cursor.getColumnIndex(StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_NAME));
                id = cursor.getString(cursor.getColumnIndex(StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_ID));
                retList.add(new StudentInfo(index, id, name));
                Log.i(TAG, "query data: " + index + " - " + name + " - " + id);
            }
            cursor.close();
        } else {
            Cursor cursor = db.query(
                    StudentInfoContract.StudentEntry.TABLE_NAME,
                    projection,
                    idSelection + " and " + nameSelection,
                    new String[]{id, name},
                    null,
                    null,
                    sortOrder
            );
            while (cursor.moveToNext()) {
                long index = cursor.getLong(
                        cursor.getColumnIndexOrThrow(StudentInfoContract.StudentEntry._ID));
                name = cursor.getString(cursor.getColumnIndex(StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_NAME));
                id = cursor.getString(cursor.getColumnIndex(StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_ID));
                retList.add(new StudentInfo(index, id, name));
                Log.i(TAG, "query data: " + index + " - " + name + " - " + id);
            }
            cursor.close();
        }
    }

    private boolean checkIdExist(String id) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_ID
        };

        String selection = StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_ID + " = ?";
        String[] selectionArgs = {id};

        String sortOrder =
                StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_ID + " DESC";

        Cursor cursor = db.query(
                StudentInfoContract.StudentEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        while (cursor.moveToNext()) {
            return true;
        }
        cursor.close();
        return false;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String id, String name) {

        assert dialog.getTag() != null;
        switch (dialog.getTag()) {
            case "add":
                addStudentInfo(dialog, id, name);
                break;
            case "delete":
                deleteStudentInfo(dialog, id, name);
                break;
            case "update":
                updateStudentInfo(dialog, id, name);
                break;
            case "query":
                queryStudentInfo(dialog, id, name);
                break;
            default:
                break;
        }

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }
}
