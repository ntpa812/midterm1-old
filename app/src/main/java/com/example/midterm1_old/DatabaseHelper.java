package com.example.midterm1_old;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "midterm1_old.db";
    private static final int DATABASE_VERSION = 6;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Users (" +
                "ID INTEGER PRIMARY KEY," +
                "FullName TEXT," +
                "Email TEXT," +
                "Role INT," +
                "Password TEXT)");

        db.execSQL("CREATE TABLE Projects (" +
                "ID INTEGER PRIMARY KEY," +
                "ProjectName TEXT," +
                "Description TEXT," +
                "ManagerID INT)");

        db.execSQL("CREATE TABLE Tasks (" +
                "ID INTEGER PRIMARY KEY," +
                "TaskName TEXT," +
                "Description TEXT," +
                "Status TEXT, " +
                "AssignID INT, " +
                "ProjectID INT)");

        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO Users (ID, FullName, Email, Role, Password) VALUES " +
                "(1, 'Nguyễn Văn A', 'pmanager@company.com', '1', '123456'), " +
                "(2, 'Trần Văn B', 'dev1@company.com', '2', '123456'), " +
                "(3, 'Lê Thị C', 'dev2@company.com', '2', '123456'), " +
                "(4, 'Phạm Văn D', 'tester@company.com', '3', '123456')");

        db.execSQL("INSERT INTO Projects (ID, ProjectName, Description, ManagerID) VALUES " +
                "(1, 'App Bán Hàng Online', 'Xây dựng ứng dụng bán hàng', 1), " +
                "(2, 'Hệ thống e-Learning', 'Website học trực tuyến', 1)");

        db.execSQL("INSERT INTO Tasks (ID, TaskName, Description, Status, AssignID, ProjectID) VALUES " +
                "(1, 'Thiết kế giao diện', 'Thiết kế màn hình chính', 'Chưa bắt đầu', 2, 1), " +
                "(2, 'Code API đăng nhập', 'Xử lý login/register', 'Đang thực hiện', 3, 1), " +
                "(3, 'Viết test đăng ký', 'Kiểm thử form đăng ký', 'Hoàn thành', 4, 1), " +
                "(4, 'Xây dựng giao diện khóa học', 'Màn hình danh sách khóa học', 'Chưa bắt đầu', 2, 2)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Tasks");
        db.execSQL("DROP TABLE IF EXISTS Projects");
        db.execSQL("DROP TABLE IF EXISTS Users");
        onCreate(db);
    }
}

