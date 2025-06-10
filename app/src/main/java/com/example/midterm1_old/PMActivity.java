package com.example.midterm1_old;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class PMActivity extends AppCompatActivity {

    TextView tvWelcome;
    ListView lvProjects, lvTasks;
    DatabaseHelper dbHelper;
    ArrayList<Project> projectList = new ArrayList<>();
    ArrayList<Task> taskList = new ArrayList<>();
    ArrayList<User> devList = new ArrayList<>();
    ProjectAdapter projectAdapter;
    PMTaskAdapter taskAdapter;
    int selectedProjectId=1;
    String fullName, email;
    int role, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pm);

        tvWelcome = findViewById(R.id.tvWelcome);
        lvProjects = findViewById(R.id.lvProjects);
        lvTasks = findViewById(R.id.lvTasks);
        dbHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        fullName = intent.getStringExtra("FullName");
        email = intent.getStringExtra("Email");
        role = intent.getIntExtra("Role", 0);
        tvWelcome.setText("Xin chào: " + fullName + " - PM");

        userId = getUserIdByEmail(email);

        loadDevelopers();
        loadProjects();

        projectAdapter = new ProjectAdapter(this, projectList);
        lvProjects.setAdapter(projectAdapter);

        lvProjects.setOnItemClickListener((parent, view, position, id) -> {
            selectedProjectId = projectList.get(position).getId();
            loadTasksByProject(selectedProjectId);
            taskAdapter = new PMTaskAdapter(PMActivity.this, taskList, devList, dbHelper);
            lvTasks.setAdapter(taskAdapter);
        });

        loadTasksByProject(projectList.get(0).getId());

        Button btnAddProject = findViewById(R.id.btnAddProject);
        btnAddProject.setOnClickListener(v -> showProjectDialog(null, email));

        Button btnAddTask = findViewById(R.id.btnAddTask);
        btnAddTask.setOnClickListener(v -> showTaskDialog(null, selectedProjectId));

        lvTasks.setOnItemClickListener((parent, view, position, id) -> {
            Task selectedTask = taskList.get(position);
            showTaskDialog(selectedTask, selectedProjectId);
        });

        Button btnLogout = findViewById(R.id.btn_logOut);
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(PMActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
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


    private void loadDevelopers() {
        devList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE Role IN (2, 3)", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("FullName"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("Email"));
            devList.add(new User(id, name, email, 2));
        }
        cursor.close();
        db.close();
    }

    private void loadProjects() {
        projectList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Projects", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("ProjectName"));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow("Description"));
            projectList.add(new Project(id, name, desc));
        }
        cursor.close();
        db.close();
    }

    private void loadTasksByProject(int projectId) {
        taskList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Tasks WHERE ProjectID=?", new String[]{String.valueOf(projectId)});
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("TaskName"));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow("Description"));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("Status"));
            int assignId = cursor.getInt(cursor.getColumnIndexOrThrow("AssignID"));
            taskList.add(new Task(id, name, desc, status, assignId, projectId));
        }
        cursor.close();
        db.close();
    }

    private void showProjectDialog(Project projectToEdit, String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(projectToEdit == null ? "Thêm Project" : "Sửa Project");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_project, null);
        EditText etProjectName = view.findViewById(R.id.etProjectName);
        EditText etProjectDesc = view.findViewById(R.id.etProjectDesc);

        if (projectToEdit != null) {
            etProjectName.setText(projectToEdit.getName());
            etProjectDesc.setText(projectToEdit.getDescription());
        }

        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String name = etProjectName.getText().toString().trim();
            String desc = etProjectDesc.getText().toString().trim();
            int userId = getUserIdByEmail(email);

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (projectToEdit == null) {
                db.execSQL("INSERT INTO Projects (ProjectName, Description, ManagerID) VALUES (?, ?, ?)",
                        new Object[]{name, desc, userId});
            } else {
                db.execSQL("UPDATE Projects SET ProjectName=?, Description=? WHERE ID=?",
                        new Object[]{name, desc, projectToEdit.getId()});
            }
            db.close();
            loadProjects();
            projectAdapter.notifyDataSetChanged();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }


    private void showTaskDialog(Task taskToEdit, int selectedProjectId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(taskToEdit == null ? "Thêm Task" : "Chỉnh sửa Task");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_task, null);
        EditText etName = view.findViewById(R.id.etTaskName);
        EditText etDesc = view.findViewById(R.id.etTaskDesc);

        if (taskToEdit != null) {
            etName.setText(taskToEdit.getName());
            etDesc.setText(taskToEdit.getDescription());
        }

        builder.setView(view);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (taskToEdit == null) {
                db.execSQL("INSERT INTO Tasks (TaskName, Description, Status, AssignID, ProjectID) VALUES (?, ?, 'Pending', -1, ?)",
                        new Object[]{name, desc, selectedProjectId});
            } else {
                db.execSQL("UPDATE Tasks SET TaskName=?, Description=? WHERE ID=?",
                        new Object[]{name, desc, taskToEdit.getId()});
            }
            db.close();

            loadTasksByProject(selectedProjectId);
        });

        builder.setNegativeButton("Hủy", null);

        if (taskToEdit != null) {
            builder.setNeutralButton("Xóa", (dialog, which) -> {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("DELETE FROM Tasks WHERE ID=?", new Object[]{taskToEdit.getId()});
                db.close();
                loadTasksByProject(selectedProjectId);
            });
        }
        builder.show();
    }


}
