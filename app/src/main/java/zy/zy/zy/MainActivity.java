package zy.zy.zy;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_AUDIO = 1;
    private TextView selectedTimeText;
    private TextView selectedAudioText;
    private final Handler handler = new Handler();
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
        myButton.setOnClickListener(v -> showCustomDialog());
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
        selectTimeButton.setOnClickListener(v -> showTimePickerDialog());

        // 设置选择音频按钮的点击事件
        selectAudioButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(intent, REQUEST_CODE_SELECT_AUDIO);
        });

        // 设置对话框的确定和取消按钮
        builder.setPositiveButton("确定", (dialog, which) -> {
            Toast.makeText(MainActivity.this, "设置已保存", Toast.LENGTH_SHORT).show();
            // 通过 ID 获取 TextView 的引用
            TextView gl_selectedTimeText = findViewById(R.id.selected_time_gl);
            TextView gl_selectedAudioText = findViewById(R.id.selected_audio_gl);
            // 判断是否超出边界
            String Time_sampleText=checkTextOverflow(selectedTimeText);
            String Audio_sampleText=checkTextOverflow(selectedAudioText);
            // 更新
            gl_selectedTimeText.setText(Time_sampleText);
            gl_selectedAudioText.setText(Audio_sampleText);
            startTimeChecker(); // 开始检查时间
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    // 检查文本是否超出边界并处理换行
    private String checkTextOverflow(TextView selectedText) {
        int screenWidth = getScreenWidth(this);

        String Audio_text=selectedText.getText().toString();
        StringBuilder sampleText= new StringBuilder();
        // 在遇到需要添加换行符之前的字符的总宽度
        float s_head_len=0;
        // 从 '/' 或者 '\' 开始的长度
        int s_final_count=0;
        // 与边界保持一点距离
        float Boundary_distance;

        for (int i=0;i<Audio_text.length();i++) {

            if (Audio_text.charAt(i) == '/' || Audio_text.charAt(i) == '\\' || i==Audio_text.length()-1) {
                float total_char_len = 0;

                // 计算当前字符之前的所有字符宽度
                for (int j = s_final_count; j >= 0; j--) {
                    total_char_len += TextUtils.getCharWidthInPx(this, Audio_text.charAt(i - j));
                }

                Boundary_distance = TextUtils.getCharWidthInPx(this, Audio_text.charAt(i));

                // 判断是否超出边界
                if (total_char_len + s_head_len + Boundary_distance >= screenWidth) {
                    sampleText.append('\n'); // 添加换行
                    s_head_len = 0; // 重置宽度
                } else {
                    s_head_len += total_char_len; // 累加宽度
                }

                // 添加字符到 sampleText
                for (int j = s_final_count; j >= 0; j--) {
                    sampleText.append(Audio_text.charAt(i - j));
                }

                s_final_count = 0; // 重置计数器
            } else {
                s_final_count++; // 继续计数
            }

        }
        return sampleText.toString();
    }
    // 获取屏幕宽度的方法
    private int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels; // 返回屏幕宽度（像素）
    }
    // 显示 TimePickerDialog
    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    // 格式化时间为 "HH:mm"
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    selectedTimeText.setText("选择的时间: " + formattedTime); // 将选择的时间显示在 TextView 中
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
