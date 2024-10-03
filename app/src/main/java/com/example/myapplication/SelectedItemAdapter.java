//package com.example.myapplication;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.Switch;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//public class SelectedItemAdapter extends RecyclerView.Adapter<SelectedItemAdapter.ViewHolder> {
//
//    private List<SelectedItem> items;
//    private OnItemDeleteListener deleteListener;
//
//    public SelectedItemAdapter(List<SelectedItem> items, OnItemDeleteListener deleteListener) {
//        this.items = items;
//        this.deleteListener = deleteListener;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        SelectedItem item = items.get(position);
//        holder.textTime.setText(item.getTime());
//        holder.textAudio.setText(item.getAudioFile());
//        holder.switchEnabled.setChecked(item.isEnabled());
//
//        holder.switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            item.setEnabled(isChecked);
//        });
//
//        // 设置删除按钮的点击事件
//        holder.deleteButton.setOnClickListener(v -> {
//            if (deleteListener != null) {
//                deleteListener.onItemDelete(position);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return items.size();
//    }
//
//    static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView textTime;
//        TextView textAudio;
//        Switch switchEnabled;
//        Button deleteButton; // 添加删除按钮
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            textTime = itemView.findViewById(R.id.text_time);
//            textAudio = itemView.findViewById(R.id.text_audio);
//            switchEnabled = itemView.findViewById(R.id.switch_enabled);
//            deleteButton = itemView.findViewById(R.id.delete_button); // 初始化删除按钮
//        }
//    }
//
//    // 接口用于删除任务
//    public interface OnItemDeleteListener {
//        void onItemDelete(int position);
//    }
//
//    public void updateList(List<SelectedItem> items) {
//        this.items = items;
//        notifyDataSetChanged();
//    }
//}
