package com.example.midterm1_old;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.ArrayList;

public class ProjectAdapter extends BaseAdapter {

    Context context;
    ArrayList<Project> projectList;

    public ProjectAdapter(Context context, ArrayList<Project> projectList) {
        this.context = context;
        this.projectList = projectList;
    }

    @Override
    public int getCount() {
        return projectList.size();
    }

    @Override
    public Object getItem(int position) {
        return projectList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return projectList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProjectAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_project, parent, false);
            holder = new ViewHolder();
            holder.tvProjectName = convertView.findViewById(R.id.tvProjectName);
            holder.tvProjectDesc = convertView.findViewById(R.id.tvProjectDesc);
            convertView.setTag(holder);
        } else {
            holder = (ProjectAdapter.ViewHolder) convertView.getTag();
        }

        Project project = projectList.get(position);
        holder.tvProjectName.setText("Tên dự án: " + project.getName());
        holder.tvProjectDesc.setText("Mô tả: " + project.getDescription());

        return convertView;
    }

    static class ViewHolder {
        TextView tvProjectName, tvProjectDesc;
    }
}
