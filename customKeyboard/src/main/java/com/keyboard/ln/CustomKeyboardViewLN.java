package com.keyboard.ln;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import com.keyboard.IBasicKBType;
import com.keyboard.custom.R;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 自定义键盘: 包括字母键盘和数字键盘
 * 1.字母键盘和数字键盘内包含一个切换按钮，可以在字母键盘和数字键盘之间进行切换
 * 2.数字键盘只包含数字
 * 3.字母键盘只包含字母
 */
public class CustomKeyboardViewLN extends KeyboardView {
    private Context context;

    private int keyboardType = -1;
    private boolean isUpper = false;        // 大小写标志
    /*********************************************************************/
    public CustomKeyboardViewLN(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomKeyboardViewLN(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    private void init(AttributeSet attrs) {
        // 数组必须升序
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                new int[] {android.R.attr.textColor, android.R.attr.background});
        int textColorId = typedArray.getResourceId(0, -1);
        int bgId = typedArray.getResourceId(1, -1);
        typedArray.recycle();
    }

    /** /
     @Override
     public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
     super.onMeasure(widthMeasureSpec, heightMeasureSpec);

     // 获取view宽的SpecSize和SpecMode
     int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
     int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);

     // 获取view高的SpecSize和SpecMode
     int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
     int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);


     if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST){
     // 当view的宽和高都设置为wrap_content时，调用setMeasuredDimension(measuredWidth,measureHeight)方法设置view的宽/高为400px
     setMeasuredDimension(200, 150);
     }else if (widthSpecMode == MeasureSpec.AT_MOST){
     // 当view的宽设置为wrap_content时，设置View的宽为你想要设置的大小（这里我设置400px）,高就采用系统获取的heightSpecSize
     setMeasuredDimension(200, heightSpecSize);
     }else if (heightSpecMode == MeasureSpec.AT_MOST){
     // 当view的高设置为wrap_content时，设置View的高为你想要设置的大小（这里我设置400px）,宽就采用系统获取的widthSpecSize
     setMeasuredDimension(widthSpecSize, 150);
     }
     }
     /**/

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        try {
            List<Keyboard.Key> keys = getKeyboard().getKeys();
            for (Keyboard.Key key : keys) {
                switch (key.codes[0]) {
                    case Keyboard.KEYCODE_DELETE:           // 删除
                        drawKeyBackground(R.drawable.kbd_bg_highlight, canvas, key);
                        drawKeyIcon(R.mipmap.kbi_delete, canvas, key);
                        break;
                    case Keyboard.KEYCODE_SHIFT:            // 大小写切换
                        drawKeyBackground(R.drawable.kbd_bg_highlight, canvas, key);
                        if (isUpper) {
                            drawKeyIcon(R.mipmap.kbi_uppercase, canvas, key);
                        } else {
                            drawKeyIcon(R.mipmap.kbi_lowercase, canvas, key);
                        }
                        break;
                    //                case Keyboard.KEYCODE_MODE_CHANGE:      // 字母键盘与数字键盘切换
                    case 90001:      // 字母键盘与数字键盘切换
                    case 90002:      // 字母键盘与数字键盘切换
                        drawKeyBackground(R.drawable.kbd_bg_highlight, canvas, key);
                        drawText(canvas, key);
                        break;
                    default:
                        //                    drawKeyBackground(R.drawable.kbd_bg_highlight, canvas, key);
                        //                    drawText(canvas, key);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawText(Canvas canvas, Keyboard.Key key) {
        try {
            Rect bounds = new Rect();
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);

            if (key.label != null) {
                String label = key.label.toString();
                Field field;

                if (label.length() > 1 && key.codes.length < 2) {
                    int labelTextSize = 0;
                    try {
                        field = KeyboardView.class.getDeclaredField("mLabelTextSize");
                        field.setAccessible(true);
                        labelTextSize = (int) field.get(this);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    paint.setTextSize(labelTextSize);
                    paint.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    int keyTextSize = 0;
                    try {
                        field = KeyboardView.class.getDeclaredField("mLabelTextSize");
                        field.setAccessible(true);
                        keyTextSize = (int) field.get(this);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    paint.setTextSize(keyTextSize);
                    paint.setTypeface(Typeface.DEFAULT);
                }

                paint.getTextBounds(key.label.toString(), 0, key.label.toString().length(), bounds);
                canvas.drawText(key.label.toString(), key.x + (key.width / 2),
                        (key.y + key.height / 2) + bounds.height() / 2, paint);
            } else if (key.icon != null) {
                key.icon.setBounds(key.x + (key.width - key.icon.getIntrinsicWidth()) / 2, key.y + (key.height - key.icon.getIntrinsicHeight()) / 2,
                        key.x + (key.width - key.icon.getIntrinsicWidth()) / 2 + key.icon.getIntrinsicWidth(), key.y + (key.height - key.icon.getIntrinsicHeight()) / 2 + key.icon.getIntrinsicHeight());
                key.icon.draw(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 绘制键盘key的图标
     *
     * @param drawableId 将要绘制上去的图标
     * @param canvas
     * @param key        需要绘制的键
     */
    private void drawKeyIcon(int drawableId, Canvas canvas, Keyboard.Key key) {
        Drawable npd = (Drawable) context.getResources().getDrawable(drawableId);
        int[] drawableState = key.getCurrentDrawableState();
        if (key.codes[0] != 0) {
            npd.setState(drawableState);
        }
        int width = npd.getIntrinsicWidth();
        int height = npd.getIntrinsicHeight();
        int startX = key.x + (key.width - width)/2;
        int startY = key.y + (key.height - height)/2;
        npd.setBounds(startX, startY, startX + width, startY + height);
        npd.draw(canvas);
    }

    /**
     * 绘制键盘key的背景色
     *
     * @param drawableId 背景色Drawable
     * @param canvas
     * @param key        需要绘制的键
     */
    private void drawKeyBackground(int drawableId, Canvas canvas, Keyboard.Key key) {
        Drawable dr = (Drawable) context.getResources().getDrawable(drawableId);
        int[] drawableState = key.getCurrentDrawableState();    // 获取key的按压状态
        dr.setState(drawableState);                             // 跟据key的状态设置背景颜色
        dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
        dr.draw(canvas);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
    /*********************************************************************/
    /**
     * 设置当前键盘标识 0：数字键盘；1：英文键盘
     *
     * @param keyboardType
     */
    public void setCurrentKeyboard(int keyboardType) {
        this.keyboardType = keyboardType;
        invalidate();
    }
    /**
     * 设置当前键盘大小写标志 true：字母大小；false：字母小写
     *
     * @param bLook
     */
    public void setCapsLook(boolean bLook){
        isUpper = bLook;
    }
    /*********************************************************************/
}

