package com.example.sqlitedemo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyViewHolder> {
    private List<StudentInfo> mDataList;

    public MyListAdapter(List<StudentInfo> mDataList) {
        this.mDataList = mDataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.student_item_layout,
                viewGroup, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        StudentInfo info = mDataList.get(i);
        myViewHolder.index_tv.setText(String.valueOf(info.getIndex()));
        myViewHolder.id_tv.setText(info.getStudentId());
        myViewHolder.name_tv.setText(info.getStudentName());
    }


    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView index_tv;
        TextView id_tv;
        TextView name_tv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            index_tv = itemView.findViewById(R.id.item_index);
            id_tv = itemView.findViewById(R.id.item_id);
            name_tv = itemView.findViewById(R.id.item_name);
        }
    }

}
