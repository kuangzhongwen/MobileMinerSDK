package waterhole.commonlibs.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextUtils;

import waterhole.commonlibs.ContextWrapper;
import waterhole.commonlibs.utils.MathUtils;
import waterhole.commonlibs.utils.ScreenUtils;

/**
 * 水印处理.
 *
 * @author kzw on 2017/07/14.
 */
public final class WaterMarkProcessor {

    // 水印中文字大小
    private static final int WATERMARK_TEXT_SIZE = 13;
    // 水印中文字颜色
    private static final int WATERMARK_TEXT_COLOR = Color.WHITE;
    // 水印文字透明度
    private static final int WATERMARK_TEXT_ALPHA = 100;
    // 水印文字行距
    private static final int WATERMARK_LINE_SPACE = 150;
    // 水印的背景颜色
    private static final int WATERMARK_BG_COLOR = Color.DKGRAY;
    // 水印内容对背景底部的margin
    private static final int WATERMARK_MARGIN_BOTTOM = 5;
    // 水印内容对背景头部的margin
    private static final int WATERMARK_MARGIN_TOP = 5;

    public WaterMarkProcessor() {
        throw new RuntimeException("WatermarkProcessor stub!");
    }

    /**
     * 添加水印文字到图片，生成位图
     *
     * @param source   原始位图
     * @param markText 水印文字
     * @return 带水印的位图
     */
    public static Bitmap addMarkTextToBitmap(Bitmap source, String markText) {
        if (source == null || TextUtils.isEmpty(markText)) {
            return source;
        }
        Paint paint = getDrawMarkTextPaint();
        int width = source.getWidth();
        int height = source.getHeight();
        double diagonalLen = MathUtils.getDiagonalLength(width, height);
        int singleHeight = getSingleMarkTextHeight();
        Bitmap result = Bitmap.createBitmap(width, height, source.getConfig());
        Canvas canvas = initMarkCanvas(source, result);
        String textToDraw = getStringToDraw(markText, (int) (diagonalLen / countTextWidth(markText, paint)) + 2);
        int startPoint = getStartPoint(width, (int) diagonalLen);
        int endPoint = getEndPoint(width, (int) diagonalLen);

        canvas.rotate(-45, width / 2, height / 2);
        for (int i = -2, len = height / singleHeight + 2; i < len; i++) {
            int y = i * singleHeight;
            canvas.drawRect(initMarkTextBGRect(paint, textToDraw, startPoint, endPoint, y),
                    getDrawMarkBGPaint());
            canvas.drawTextOnPath(textToDraw, getDrawMarkTextPath(y, startPoint, endPoint), 0, 0,
                    paint);
        }
        canvas.rotate(45, width / 2, height / 2);
        return result;
    }

    private static Rect initMarkTextBGRect(Paint paint, String textToDraw, int startPoint,
                                           int endPoint, int y) {
        Rect textRect = new Rect();
        paint.getTextBounds(textToDraw, 0, textToDraw.length() - 1, textRect);
        // 因为getTextBounds 默认文字是在(0,0)出 故要加上实际偏移量 5 是上下margin
        Context context = ContextWrapper.getInstance().obtainContext();
        textRect.bottom += y + ScreenUtils.dip2px(context, WATERMARK_MARGIN_BOTTOM);
        textRect.top += y - ScreenUtils.dip2px(context, WATERMARK_MARGIN_TOP);
        textRect.left = startPoint;
        textRect.right = endPoint;
        return textRect;
    }

    private static Canvas initMarkCanvas(Bitmap sourceBitmap, Bitmap resultBitmap) {
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(sourceBitmap, 0, 0, null);
        return canvas;
    }

    private static Path getDrawMarkTextPath(int y, int startPoint, int endPoint) {
        Path path = new Path();
        path.moveTo(startPoint, y);
        path.lineTo(endPoint, y);
        return path;
    }

    private static int getSingleMarkTextHeight() {
        return WATERMARK_TEXT_SIZE + WATERMARK_LINE_SPACE;
    }

    private static Paint getDrawMarkTextPaint() {
        Paint paint = new Paint();
        paint.setColor(WATERMARK_TEXT_COLOR);
        paint.setAlpha(WATERMARK_TEXT_ALPHA);
        paint.setAntiAlias(true);
        paint.setTextSize(WATERMARK_TEXT_SIZE);
        return paint;
    }

    private static Paint getDrawMarkBGPaint() {
        Paint bgPaint = new Paint();
        bgPaint.setColor(WATERMARK_BG_COLOR);
        bgPaint.setAlpha(WATERMARK_TEXT_ALPHA);
        bgPaint.setStyle(Paint.Style.FILL);
        return bgPaint;
    }

    private static String getStringToDraw(String markText, int loopTime) {
        StringBuilder stringToDraw = new StringBuilder();
        for (int i = 0; i < loopTime; i++) {
            stringToDraw.append(markText);
        }
        return stringToDraw.toString();
    }

    private static int getStartPoint(int width, int diagonalLen) {
        return (width - diagonalLen) / 2;
    }

    private static int getEndPoint(int width, int diagonalLen) {
        return width + ((diagonalLen - width) / 2);
    }

    private static float countTextWidth(String text, Paint paint) {
        if (TextUtils.isEmpty(text) || paint == null) {
            return 0;
        }
        float[] widths = new float[text.length()];
        paint.getTextWidths(text, widths);
        float totalWidth = 0;
        for (float width : widths) {
            totalWidth += width;
        }
        return totalWidth;
    }
}
