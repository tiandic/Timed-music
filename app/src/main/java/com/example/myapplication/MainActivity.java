package com.example.myapplication;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;  // 确保导入此类
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_AUDIO = 1;
    private TextView selectedTimeText;
    private TextView selectedAudioText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f); // 加载布局 f.xml

        // 通过 ID 获取到 Button
        Button myButton = findViewById(R.id.button);

        // 设置点击事件监听器
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });
    }

    // 显示自定义对话框
    private void showCustomDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("设置时间和选择音频");

        // 获取选择按钮和显示文本的 TextView
        selectedTimeText = dialogView.findViewById(R.id.selected_time_text);
        selectedAudioText = dialogView.findViewById(R.id.selected_audio_text);
        Button selectAudioButton = dialogView.findViewById(R.id.select_audio_button);
        Button selectTimeButton = dialogView.findViewById(R.id.select_time_button);

        // 设置时间选择按钮点击事件
        selectTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        // 设置选择音频按钮的点击事件
        selectAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(intent, REQUEST_CODE_SELECT_AUDIO);
            }
        });

        // 设置对话框的确定和取消按钮
        builder.setPositiveButton("确定", (dialog, which) -> {
            Toast.makeText(MainActivity.this, "设置已保存", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 显示 TimePickerDialog
    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // 格式化时间为 "HH:mm"
                        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        selectedTimeText.setText("选择的时间: " + formattedTime); // 将选择的时间显示在 TextView 中
                    }
                }, 13, 0, true); // 默认时间设置为13:00
        timePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_AUDIO && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                String audioPath = data.getData().toString();
                // 显示选择的音频文件名
                selectedAudioText.setText("选择的音频: " + audioPath);
                Toast.makeText(this, "选择的音频文件: " + audioPath, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
