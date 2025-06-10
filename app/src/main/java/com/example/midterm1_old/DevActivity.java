package com.example.midterm1_old;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class DevActivity extends AppCompatActivity {

    TextView tvWelcome;
    ListView lvTasks;
    DatabaseHelper dbHelper;
    TaskAdapter adapter;
    ArrayList<Task> taskList = new ArrayList<>();

    String fullName, email;
    int role;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev);

        tvWelcome = findViewById(R.id.tvWelcome);
        lvTasks = findViewById(R.id.lvTasks);
        dbHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        fullName = intent.getStringExtra("FullName");
        role = intent.getIntExtra("Role",0);
        email = intent.getStringExtra("Email");
        String RoleName="";
        if (role==2) RoleName="Dev";
        else if (role==3) RoleName="Tester";
        tvWelcome.setText("Xin chào: " + fullName + " - " + RoleName );

        userId = getUserIdByEmail(email);
        loadUserTasks();

        adapter = new TaskAdapter(this, taskList, dbHelper);
        lvTasks.setAdapter(adapter);

        Button btnLogout = findViewById(R.id.btn_logOut);

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

            Intent intent1 = new Intent(DevActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent1);
            finish();
        });
    }

    private int getUserIdByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID FROM Users WHERE Email=?", new String[]{email});
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
        }
        cursor.close();
        db.close();
        return id;
    }

    private void loadUserTasks() {
        taskList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Tasks WHERE AssignID=?", new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("TaskName"));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow("Description"));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("Status"));
            int projectId = cursor.getInt(cursor.getColumnIndexOrThrow("ProjectID"));

            taskList.add(new Task(id, name, desc, status, userId, projectId));
        }

        cursor.close();
        db.close();
    }
}
