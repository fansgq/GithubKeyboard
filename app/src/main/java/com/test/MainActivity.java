package com.test;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.keyboard.IBasicKBType;
import com.keyboard.IKeyboardType;
import com.keyboard.ln.AutoEditView;
import com.keyboard.ln.CustomKeyboardViewLN;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 创建并显示嵌入式的数字-字母键盘
        showEmbedKeyboardLN();
    }

    private void showEmbedKeyboardLN(){
        AutoEditView inputEditText = (AutoEditView) findViewById(R.id.gas_input_edit);
        CustomKeyboardViewLN kln = (CustomKeyboardViewLN)findViewById(R.id.keyboard_view_ln);
        kln.setCurrentKeyboard(IBasicKBType.BASE_LETTER);

        LinearLayout llKeyboard = (LinearLayout) findViewById(R.id.ll_keyboard);
        inputEditText.setEditView(llKeyboard, kln, IKeyboardType.KBT_LETTER_SHIFT);
        inputEditText.setFocusable(true);
        inputEditText.setFocusableInTouchMode(true);
        inputEditText.requestFocus();
    }

}