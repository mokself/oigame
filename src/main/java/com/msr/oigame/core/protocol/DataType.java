package com.msr.oigame.core.protocol;

/**
 * 消息数据类型常量
 *
 * @author morley
 * 2022/1/22 18:03
 */
public interface DataType {
    /**
     * byte类型 1字节
     */
    byte BYTE = 0;

    /**
     * boolean类型 1字节
     */
    byte BOOL = 1;

    /**
     * short类型 2字节
     */
    byte SHORT = 2;

    /**
     * char类型 2字节
     */
    byte CHAR = 3;

    /**
     * int类型 4字节
     */
    byte INT = 4;

    /**
     * float类型 4字节
     */
    byte FLOAT = 5;

    /**
     * double类型 8字节
     */
    byte DOUBLE = 6;

    /**
     * long类型 8字节
     */
    byte LONG = 7;

    /**
     * String类型
     */
    byte VARCHAR = 8;

    /**
     * 固定元素类型数组
     */
    byte ARRAY = 9;

    /**
     * 任意元素类型数组
     */
    byte DYNAMIC_ARRAY = 10;
}
