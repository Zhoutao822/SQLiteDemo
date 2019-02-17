package com.example.sqlitedemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sqlitedemo.db.StudentInfoContract;
import com.example.sqlitedemo.db.StudentInfoDbHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
                addStudentInfo();
                break;
            case R.id.btn_delete:
                deleteStudentInfo();
                break;
            case R.id.btn_update:
                updateStudentInfo();
                break;
            case R.id.btn_query:
                queryStudentInfo();
                break;
            default:
                break;
        }
    }

    private void addStudentInfo() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_ID, "U20191101");
        values.put(StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_NAME, "Tom");

        long newRowId = db.insert(StudentInfoContract.StudentEntry.TABLE_NAME, null, values);
        Log.i("add data", "success");
    }

    private void deleteStudentInfo() {
        StudentInfoDbHelper mDbHelper = new StudentInfoDbHelper(getApplicationContext());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
// Define 'where' part of query.
        String selection = StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_NAME + " LIKE ?";
// Specify arguments in placeholder order.
        String[] selectionArgs = { "Tom" };
// Issue SQL statement.
        int deletedRows = db.delete(StudentInfoContract.StudentEntry.TABLE_NAME, selection, selectionArgs);
    }

    private void updateStudentInfo() {
        StudentInfoDbHelper mDbHelper = new StudentInfoDbHelper(getApplicationContext());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

// New value for one column
        String title = "212121";
        ContentValues values = new ContentValues();
        values.put(StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_ID, title);

// Which row to update, based on the title
        String selection = StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_NAME + " LIKE ?";
        String[] selectionArgs = { "Tom" };

        int count = db.update(
                StudentInfoContract.StudentEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    private void queryStudentInfo() {

        StudentInfoDbHelper mDbHelper = new StudentInfoDbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_ID,
                StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_NAME
        };

// Filter results WHERE "title" = 'My Title'
        String selection = StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_NAME + " = ?";
        String[] selectionArgs = {"Tom"};

// How you want the results sorted in the resulting Cursor
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
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(StudentInfoContract.StudentEntry._ID));
            String name = cursor.getString(cursor.getColumnIndex(StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_NAME));
            String id = cursor.getString(cursor.getColumnIndex(StudentInfoContract.StudentEntry.COLUMN_NAME_STUDENT_ID));
            Log.i("read data", "db data: " + itemId + " - " + name + " - " + id);
        }
        cursor.close();
    }

}
