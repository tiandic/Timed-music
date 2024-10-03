package com.example.myapplication;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_AUDIO = 1;
    private TextView selectedTimeText;
    private TextView selectedAudioText;
    private Handler handler = new Handler();
    private Runnable timeCheckerRunnable;
    private Uri audioUri; // 用于存储选择的音频 URI
    private MediaPlayer mediaPlayer; // 用于播放音频


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
            // 通过 ID 获取 TextView 的引用
            TextView gl_selectedTimeText = findViewById(R.id.selected_time_gl);
            TextView gl_selectedAudioText = findViewById(R.id.selected_audio_gl);
            // 更新
            gl_selectedTimeText.setText("选择的时间: "+selectedTimeText.getText().toString());
            gl_selectedAudioText.setText("选择的音频: "+selectedAudioText.getText().toString());
            startTimeChecker(); // 开始检查时间
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
                }, 5, 30, true); // 默认时间设置为5:30
        timePickerDialog.show();
    }

    // 开始定时检查时间
    private void startTimeChecker() {
        timeCheckerRunnable = new Runnable() {
            @Override
            public void run() {
                // 获取当前时间
                Calendar calendar = Calendar.getInstance();
                String currentTime = String.format(Locale.getDefault(), "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

                // 获取选中的时间
                String selectedTime = selectedTimeText.getText().toString().replace("选择的时间: ", "").trim();

                // 比较当前时间与选中的时间
                if (currentTime.equals(selectedTime)) {
                    play(); // 执行播放方法
                }

                // 继续检查
                handler.postDelayed(this, 1000); // 每秒检查一次
            }
        };
        handler.post(timeCheckerRunnable); // 开始执行
    }

    // 播放音频的方法
    private void play() {
        if (audioUri != null) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this, audioUri);
            }
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                Toast.makeText(this, "开始播放音频", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "未选择音频文件", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_AUDIO && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                audioUri = data.getData(); // 获取选择的音频文件 URI
                // 显示选择的音频文件名
                selectedAudioText.setText("选择的音频: " + audioUri.getPath());
                Toast.makeText(this, "选择的音频文件: " + audioUri.getPath(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止检查时间
        if (timeCheckerRunnable != null) {
            handler.removeCallbacks(timeCheckerRunnable);
        }
        // 释放 MediaPlayer 资源
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
