package com.example.midterm1_old;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.ArrayList;

public class PMTaskAdapter extends ArrayAdapter<Task> {

    Context context;
    ArrayList<Task> tasks;
    ArrayList<User> devList;
    DatabaseHelper dbHelper;

    public PMTaskAdapter(Context context, ArrayList<Task> tasks, ArrayList<User> devList, DatabaseHelper dbHelper) {
        super(context, 0, tasks);
        this.context = context;
        this.tasks = tasks;
        this.devList = devList;
        this.dbHelper = dbHelper;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Task task = tasks.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_pm_task, parent, false);
        }

        TextView tvTaskName = convertView.findViewById(R.id.tvTaskName);
        TextView tvStatus = convertView.findViewById(R.id.tvStatus);
        Spinner spinnerDev = convertView.findViewById(R.id.spinnerDev);
        Button btnAssign = convertView.findViewById(R.id.btnAssign);

        tvTaskName.setText(task.getName());
        tvStatus.setText("Trạng thái: " + task.getStatus());

        // Tạo danh sách tên developer
        ArrayList<String> devNames = new ArrayList<>();
        for (User dev : devList) {
            devNames.add(dev.getFullName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, devNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDev.setAdapter(spinnerAdapter);

        if (task.getId() != -1) {
            for (int i = 0; i < devList.size(); i++) {
                if (devList.get(i).getId() == task.getId()) {
                    spinnerDev.setSelection(i);
                    break;
                }
            }
        }

        btnAssign.setOnClickListener(v -> {
            int selectedPosition = spinnerDev.getSelectedItemPosition();
            int devId = devList.get(selectedPosition).getId();

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("UPDATE Tasks SET AssignID=? WHERE ID=?", new Object[]{devId, task.getId()});
            db.close();

            Toast.makeText(context, "Đã giao task cho " + devList.get(selectedPosition).getFullName(), Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
}