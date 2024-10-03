package zy.zy.zy;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.util.TypedValue;

public class TextUtils {

    /**
     * 计算字符在用户指定字体大小下的宽度（以像素为单位）
     *
     * @param context 上下文
     * @param ch      要测量的字符
     * @return 字符所占的宽度（单位：像素）
     */
    public static float getCharWidthInPx(Context context, char ch) {
        // 获取用户的字体大小设置
        float textSizeSp = getUserFontSize(context);

        // 创建 Paint 对象并设置字体大小
        Paint paint = new Paint();
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSizeSp, context.getResources().getDisplayMetrics()));

        // 测量字符的宽度（以像素为单位）
        return paint.measureText(String.valueOf(ch)); // 直接返回宽度（像素）
    }

    /**
     * 获取用户的字体大小设置
     *
     * @param context 上下文
     * @return 用户设置的字体大小（默认16sp）
     */
    private static float getUserFontSize(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        float fontScale = configuration.fontScale; // 获取当前字体缩放比例
        return 16f * fontScale; // 以16sp为基准计算用户字体大小
    }
}
