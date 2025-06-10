package com.example.midterm1_old;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.*;
import android.widget.*;
import java.util.ArrayList;

public class TaskAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Task> tasks;
    private final DatabaseHelper dbHelper;
    private final String[] statuses = {"Chưa bắt đầu", "Đang thực hiện", "Hoàn thành"};

    public TaskAdapter(Context context, ArrayList<Task> tasks, DatabaseHelper dbHelper) {
        this.context = context;
        this.tasks = tasks;
        this.dbHelper = dbHelper;
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return tasks.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TaskAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tvTaskName);
            holder.tvDesc = convertView.findViewById(R.id.tvTaskDesc);
            holder.spStatus = convertView.findViewById(R.id.spStatus);
            convertView.setTag(holder);
        } else {
            holder = (TaskAdapter.ViewHolder) convertView.getTag();
        }

        Task task = tasks.get(position);

        holder.tvName.setText(task.getName());
        holder.tvDesc.setText(task.getDescription());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, statuses);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spStatus.setAdapter(spinnerAdapter);

        int selectedIndex = getStatusIndex(task.getStatus());
        holder.spStatus.setSelection(selectedIndex);

        holder.spStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean firstLoad = true;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (firstLoad) {
                    firstLoad = false;
                    return;
                }

                String newStatus = statuses[pos];
                if (!newStatus.equals(task.getStatus())) {
                    task.setStatus(newStatus);
                    updateTaskStatus(task.getId(), newStatus);
                    Toast.makeText(context, "Cập nhật trạng thái thành: " + newStatus, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        return convertView;
    }

    private void updateTaskStatus(int taskId, String status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE Tasks SET Status=? WHERE ID=?", new Object[]{status, taskId});
        db.close();
    }

    private int getStatusIndex(String status) {
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equalsIgnoreCase(status)) {
                return i;
            }
        }
        return 0;
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvDesc;
        Spinner spStatus;
    }
}
