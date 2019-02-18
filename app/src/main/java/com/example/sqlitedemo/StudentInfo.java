package com.example.sqlitedemo;

public class StudentInfo {
    private long index;
    private String studentId;
    private String studentName;

    public StudentInfo(long index, String studentId, String studentName) {
        this.index = index;
        this.studentId = studentId;
        this.studentName = studentName;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
