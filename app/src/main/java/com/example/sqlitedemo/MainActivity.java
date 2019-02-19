package com.example.sqlitedemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private RecyclerView recyclerView;
    private MyListAdapter myListAdapter;
    private RecyclerView queryView;
    private MyListAdapter queryAdapter;
    private List<StudentInfo> allList;
    private List<StudentInfo> resultList;


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
        allList = new ArrayList<>();
        getAllData();

        myListAdapter = new MyListAdapter(allList);
        recyclerView = findViewById(R.id.my_recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myListAdapter);

        resultList = new ArrayList<>();
        queryAdapter = new MyListAdapter(resultList);
        queryView = findViewById(R.id.query_view);
        queryView.setLayoutManager(new LinearLayoutManager(this));
        queryView.setAdapter(queryAdapter);
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

            getAllData();
            myListAdapter.notifyDataSetChanged();
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
            int resultId = db.delete(StudentInfoContract.StudentEntry.TABLE_NAME, nameSelection, nameSelectionArgs);
            if (resultId <= 0){
                Toast.makeText(getApplicationContext(), "Can't find data:" + " name is "
                        + name, Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getApplicationContext(), "Delete data success", Toast.LENGTH_SHORT).show();
                getAllData();
                myListAdapter.notifyDataSetChanged();
            }
        } else if (name.isEmpty()) {
            int resultId = db.delete(StudentInfoContract.StudentEntry.TABLE_NAME, idSelection, idSelectionArgs);
            if (resultId <= 0){
                Toast.makeText(getApplicationContext(), "Can't find data:" + " id is "
                        + id, Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getApplicationContext(), "Delete data success", Toast.LENGTH_SHORT).show();
                getAllData();
                myListAdapter.notifyDataSetChanged();
            }
        } else {
            int resultId = db.delete(StudentInfoContract.StudentEntry.TABLE_NAME,
                    idSelection + " and " + nameSelection, new String[]{id, name});
            if (resultId <= 0){
                Toast.makeText(getApplicationContext(), "Can't find data:" + " id is " + id
                        + " name is " + name, Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getApplicationContext(), "Delete data success", Toast.LENGTH_SHORT).show();
                getAllData();
                myListAdapter.notifyDataSetChanged();
            }
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
            getAllData();
            myListAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getApplicationContext(), "Data not exist", Toast.LENGTH_LONG).show();
            dialog.getDialog().cancel();
        }
    }

    private void queryStudentInfo(DialogFragment dialog, String id, String name) {
        resultList.clear();
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
                resultList.add(new StudentInfo(index, id, name));
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
                resultList.add(new StudentInfo(index, id, name));
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
                resultList.add(new StudentInfo(index, id, name));
                Log.i(TAG, "query data: " + index + " - " + name + " - " + id);
            }
            cursor.close();
        }
        if (resultList.isEmpty()){
            Toast.makeText(getApplicationContext(), "Can't find data", Toast.LENGTH_SHORT).show();
        }else {
            queryAdapter.notifyDataSetChanged();
        }
    }

    private void getAllData() {
        allList.clear();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String name;
        String id;
        Cursor cursor = db.query(
                StudentInfoContract.StudentEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                BaseColumns._ID + " ASC");
        while (cursor.moveToNext()) {
            long index = cursor.getLong(
                    cursor.getColumnIndexOrThrow(StudentInfoContract.StudentEntry._ID));
            name = cursor.getString(cursor.getColumnIndex(StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_NAME));
            id = cursor.getString(cursor.getColumnIndex(StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_ID));
            allList.add(new StudentInfo(index, id, name));
            Log.i(TAG, "all data: " + index + " - " + name + " - " + id);
        }
        cursor.close();
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
