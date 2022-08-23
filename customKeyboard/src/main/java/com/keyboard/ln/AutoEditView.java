package com.keyboard.ln;


import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.keyboard.IBasicKBType;
import com.keyboard.IKeyboardType;
import com.keyboard.custom.R;

import java.util.Arrays;
import java.util.List;

/**
 * 自定义键盘，在输入密码等安全性要求较高的时候使用此编辑框
 */

public class AutoEditView extends androidx.appcompat.widget.AppCompatEditText implements CustomKeyboardViewLN.OnKeyboardActionListener {
    private Context context;

    private ViewGroup viewGroup;
    private Keyboard keyboardLetter;                // 字母键盘
    private Keyboard keyboardNumber;                // 数字键盘
    private Keyboard keyboardNumberPoint;           // 数字键盘-带点
    private Keyboard keyboardNumberX;               // 数字键盘-带X(身份证键盘)
    private CustomKeyboardViewLN keyboardView;

    private int nDefaultShowKB = 0;                 // 默认显示的键盘：0 数字键盘；1 字母键盘
    private boolean isUpper = false;              // 是否是大写字母(标识英文键盘大小写切换)
    private boolean isPreview = false;              // 是否显示预览KEY

    //点击【完成】、键盘隐藏、键盘显示时的回调
    private OnKeyboardListener onKeyboardListener;

    private final int NO_KEY_SPACE = -1001; // 占位，特殊处理不显示，不输入

    /*********************************************************************/
    public AutoEditView(Context context) {
        this(context, null);
    }

    public AutoEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    /**
     * 初始化自定义键盘
     */
    private void initKeyboardView(int type) {
        switch (type) {
            case IKeyboardType.KBT_NUMBER_SHIFT:
                nDefaultShowKB = IBasicKBType.BASE_NUMBER;
                keyboardNumber = new Keyboard(context, R.xml.keyboard_ln_numbers);
                keyboardLetter = new Keyboard(context, R.xml.keyboard_ln_letters);
                keyboardView.setKeyboard(keyboardNumber);
                break;
            case IKeyboardType.KBT_LETTER_SHIFT:
                nDefaultShowKB = IBasicKBType.BASE_LETTER;
                keyboardNumber = new Keyboard(context, R.xml.keyboard_ln_numbers);
                keyboardLetter = new Keyboard(context, R.xml.keyboard_ln_letters);
                keyboardView.setKeyboard(keyboardLetter);
                break;
            default:
                break;
        }
    }

    /**
     * 设置键盘
     *
     * @param viewGroup
     * @param keyboardView
     * @param numberType   0:表示默认数字键盘，1：表示默认带X的数字键盘，2：表示带小数点的数字键盘
     *                     3:表示字母键盘
     */
    public void setEditView(ViewGroup viewGroup, CustomKeyboardViewLN keyboardView, int numberType) {
        this.viewGroup = viewGroup;
        this.keyboardView = keyboardView;
        initKeyboardView(numberType);

        keyboardView.setCurrentKeyboard(nDefaultShowKB);
        keyboardView.setEnabled(true);
        keyboardView.setOnKeyboardActionListener(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        closeKeyboard(this);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        closeKeyboard(this);
        keyboardView = null;
        viewGroup = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        requestFocus();
        requestFocusFromTouch();
        closeKeyboard(this);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!isShow()) {
                show();
            }
        }
        return true;
    }

    @Override
    public void onPress(int primaryCode) {
        if (onKeyboardListener != null) {
            onKeyboardListener.onPress(primaryCode);
        }

        if(isPreview) {
            setPreview(primaryCode);
        }
    }

    @Override
    public void onRelease(int primaryCode) {
        switch (primaryCode) {
            case Keyboard.KEYCODE_DONE:// 完成-4
                hide(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onKey(int primaryCode, int[] ints) {
        Editable editable = this.getText();
        int start = this.getSelectionStart();      // 光标位置
        switch (primaryCode) {
            case 90001:    // 数字键盘切换为字母键盘 90001
                keyboardView.setKeyboard(keyboardLetter);
                keyboardView.setCurrentKeyboard(1);
                break;
            case 90002:    // 英文键盘切换为数字键盘 90002
                keyboardView.setKeyboard(keyboardNumber);
                keyboardView.setCurrentKeyboard(0);
                break;
            case Keyboard.KEYCODE_DELETE:// 回退-5
                if (editable != null && editable.length() > 0 && start > 0) {
                    editable.delete(start - 1, start);
                }
                break;
            case Keyboard.KEYCODE_SHIFT:// 英文大小写切换-1
                shiftEnglish();
                keyboardView.setKeyboard(keyboardLetter);
                break;
            case Keyboard.KEYCODE_DONE:// 完成-4
                break;
            case NO_KEY_SPACE: // 间隔键，不取值
                break;
            default:
                editable.insert(start, Character.toString((char) primaryCode));
                break;
        }
    }

    /**
     * 英文键盘大小写切换
     */
    private void shiftEnglish() {
        List<Keyboard.Key> keyList = keyboardLetter.getKeys();
        for (Keyboard.Key key : keyList) {
            if (key.label != null && isKey(key.label.toString())) {
                if (isUpper) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                } else {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0] - 32;
                }
            }
        }
        isUpper = !isUpper;
        keyboardView.setCapsLook(isUpper);
    }

    /**
     * 判断是否需要预览Key
     *
     * @param primaryCode keyCode
     */

    private void setPreview(int primaryCode) {
        List<Integer> list = Arrays.asList(Keyboard.KEYCODE_MODE_CHANGE, Keyboard.KEYCODE_DELETE, Keyboard.KEYCODE_SHIFT, Keyboard.KEYCODE_DONE, 32);
        if (list.contains(primaryCode)) {
            keyboardView.setPreviewEnabled(false);
        } else {
            keyboardView.setPreviewEnabled(true);
        }
    }

    /**
     * 判断此key是否正确，且存在
     *
     * @param key
     * @return
     */
    private boolean isKey(String key) {
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        if (lowercase.indexOf(key.toLowerCase()) > -1) {
            return true;
        }
        return false;
    }

    /**
     * 设置键盘隐藏
     *
     * @param isCompleted true：表示点击了【完成】
     */
    public void hide(boolean isCompleted) {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            keyboardView.setVisibility(View.INVISIBLE);
            if (viewGroup != null) {
                viewGroup.setVisibility(View.GONE);
            }
        }
        if (onKeyboardListener != null) {
            onKeyboardListener.onHide(isCompleted);
        }
    }

    /**
     * 设置键盘对话框显示，并且屏幕上移
     */
    public void show() {
        // 设置键盘显示
        int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
            if (viewGroup != null) {
                viewGroup.setVisibility(View.VISIBLE);
            }
        }
        if (onKeyboardListener != null) {
            onKeyboardListener.onShow();
        }
    }

    /**
     * 键盘状态
     *
     * @return true：表示键盘开启 false：表示键盘隐藏
     */
    public boolean isShow() {
        return keyboardView.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onText(CharSequence charSequence) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /*if (keyCode == KeyEvent.KEYCODE_BACK) {
            hide(false);
            return true;
        }*/
        return super.onKeyDown(keyCode, event);
    }

    public interface OnKeyboardListener {
        /**
         * 键盘隐藏了
         *
         * @param isCompleted true：表示点击了【完成】
         */


        void onHide(boolean isCompleted);

        /**
         * 键盘弹出了
         */


        void onShow();

        /**
         * 按下
         *
         * @param primaryCode
         */


        void onPress(int primaryCode);
    }

    /**
     * 对外开放的方法
     *
     * @param onKeyboardListener
     */
    public void setOnKeyboardListener(OnKeyboardListener onKeyboardListener) {
        this.onKeyboardListener = onKeyboardListener;
    }

    /***********************************************************/
    /**
     * 关闭软键盘
     * @param mEditText 输入框
     */
    public void closeKeyboard(EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    /***********************************************************/
}
