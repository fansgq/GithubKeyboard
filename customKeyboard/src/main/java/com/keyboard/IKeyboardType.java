package com.keyboard;

/**
 * 定义模块所支持的各种键盘的类型标识，每种键盘在布局上均有不同
 */
public interface IKeyboardType {
    /** 数字键盘 */
    int KBT_NUMBER = 0;         // 键盘类型：数字键盘-只有数字
    int KBT_NUMBER_P = 1;       // 键盘类型：数字键盘-带小数点
    int KBT_NUMBER_X = 2;       // 键盘类型：数字键盘-带X(身份证键盘)
    int KBT_NUMBER_SHIFT = 3;   // 键盘类型：数字键盘-带字母切换键
    /** 字母键盘 */
    int KBT_LETTER = 4;         // 键盘类型：字母键盘-只有字母
    int KBT_LETTER_SHIFT = 5;   // 键盘类型：字母键盘-只带数字切换键
    /** 符号键盘 */
    int KBT_SYMBOL = 6;         // 键盘类型：符号键盘-只有符号
    int KBT_SYMBOL_SHIFT = 7;   // 键盘类型：符号键盘-只带字母切换键
    /** 数字字母合一的键盘 */
    int KBT_LN = 8;             // 键盘类型：数字+字母合一键盘
    int KBT_LN_SHIFT = 9;       // 键盘类型：数字+字母合一键盘-带符号切换键
    /** 通用的键盘 */
    int KBT_NUMBER_G = 10;      // 键盘类型：通用数字键盘-带字母切换键
    int KBT_LETTER_G = 11;      // 键盘类型：通用字母键盘-带数字切换键和符号切换键
    int KBT_SYMBOL_G = 12;      // 键盘类型：通用符号键盘-带数字切换键和字母切换键
}

