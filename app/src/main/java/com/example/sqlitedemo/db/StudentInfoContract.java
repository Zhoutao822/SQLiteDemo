package com.example.sqlitedemo.db;

import android.provider.BaseColumns;

public final class StudentInfoContract {
    private StudentInfoContract() {
    }

    public static class StudentEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_STUDENT_NAME = "name";
        public static final String COLUMN_NAME_STUDENT_ID = "id";
    }
}
