package com.msr.oigame.core.protocol;

import com.msr.oigame.common.exception.MessageException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息主体
 *
 * @author morley
 * 2021/12/18 15:56
 */
@Getter
public class ExternalMessage implements BaseMessage {
    private final int cmd;
    private final ByteBuf data;

    public ExternalMessage(ByteBuf data) {
        this.cmd = data.readInt();
        this.data = data;
    }

    public static ExternalMessage emptyMsg() {
        return new ExternalMessage(Unpooled.buffer());
    }

    /**
     * 获取字节位置
     *
     * @param order 顺序
     * @return 索引
     */
    private int getIndex(int order) {
        // 前4位是cmd数据，不需要读取
        int _index = 4;
        for (int i = 0; i < order; i++) {
            byte type = data.getByte(_index);
            _index += 1;
            switch (type) {
                case DataType.BYTE, DataType.BOOL -> _index += 1;
                case DataType.SHORT, DataType.CHAR -> _index += 2;
                case DataType.INT, DataType.FLOAT -> _index += 4;
                case DataType.DOUBLE, DataType.LONG -> _index += 8;
                case DataType.VARCHAR, DataType.DYNAMIC_ARRAY -> _index += data.getShort(_index) + 4;
                case DataType.ARRAY -> _index += data.getShort(_index + 1) + 5;
            }
        }
        return _index + 1;
    }

    public boolean hasRead() {
        return data.readableBytes() >= 2;
    }

    /**
     * 读取一条数据
     * 会将底层的ByteBuf的readerIndex增加
     * @return 读取的数据
     */
    public Object read() {
        if (!data.isReadable()) {
            return null;
        }
        byte type = data.readByte();
        switch (type) {
            case DataType.BYTE:
                return data.readByte();
            case DataType.BOOL:
                return data.readBoolean();
            case DataType.SHORT:
                return data.readShort();
            case DataType.CHAR:
                return data.readChar();
            case DataType.INT:
                return data.readInt();
            case DataType.FLOAT:
                return data.readFloat();
            case DataType.DOUBLE:
                return data.readDouble();
            case DataType.LONG:
                return data.readLong();
            case DataType.VARCHAR: {
                int length = data.readInt();
                return data.readCharSequence(length, StandardCharsets.UTF_8);
            }
            case DataType.ARRAY: {
                byte type2 = data.readByte();
                switch (type2) {
                    case DataType.BYTE -> {
                        byte[] bytes = new byte[data.readInt()];
                        data.readBytes(bytes);
                        return bytes;
                    }
                    case DataType.BOOL -> {
                        boolean[] booleans = new boolean[data.readInt()];
                        for (int i = 0; i < booleans.length; i++) {
                            booleans[i] = data.readBoolean();
                        }
                        return booleans;
                    }
                    case DataType.SHORT -> {
                        short[] shorts = new short[data.readInt() / 2];
                        for (int i = 0; i < shorts.length; i++) {
                            shorts[i] = data.readShort();
                        }
                        return shorts;
                    }
                    case DataType.CHAR -> {
                        char[] characters = new char[data.readInt() / 2];
                        for (int i = 0; i < characters.length; i++) {
                            characters[i] = data.readChar();
                        }
                        return characters;
                    }
                    case DataType.INT -> {
                        int[] integers = new int[data.readInt() / 4];
                        for (int i = 0; i < integers.length; i++) {
                            integers[i] = data.readInt();
                        }
                        return integers;
                    }
                    case DataType.FLOAT -> {
                        float[] floats = new float[data.readInt() / 4];
                        for (int i = 0; i < floats.length; i++) {
                            floats[i] = data.readFloat();
                        }
                        return floats;
                    }
                    case DataType.DOUBLE -> {
                        double[] doubles = new double[data.readInt() / 8];
                        for (int i = 0; i < doubles.length; i++) {
                            doubles[i] = data.readDouble();
                        }
                        return doubles;
                    }
                    case DataType.LONG -> {
                        long[] longs = new long[data.readInt() / 8];
                        for (int i = 0; i < longs.length; i++) {
                            longs[i] = data.readLong();
                        }
                        return longs;
                    }
                }
            }
            case DataType.DYNAMIC_ARRAY: {
                List<Object> result = new ArrayList<>();
                long endIndex = data.readerIndex() + data.readInt();
                while (data.readerIndex() <= endIndex) {
                    result.add(read());
                }
                return result;
            }
            default:
                throw new MessageException("错误的消息类型");
        }
    }

    /**
     * 读取一条byte数据
     */
    public byte readByte() {
        return (byte) read();
    }

    /**
     * 获取指定位置的byte数据
     * @param order 元素位置
     */
    public byte getByte(int order) {
        int index = getIndex(order);
        return data.getByte(index);
    }

    /**
     * 读取一条boolean数据
     */
    public boolean readBool() {
        return (boolean) read();
    }

    /**
     * 获取指定位置的boolean数据
     * @param order 元素位置
     */
    public boolean getBool(int order) {
        int index = getIndex(order);
        return data.getBoolean(index);
    }

    /**
     * 读取一条short数据
     */
    public short readShort() {
        return (short) read();
    }

    /**
     * 获取指定位置的short数据
     * @param order 元素位置
     */
    public short getShort(int order) {
        int index = getIndex(order);
        return data.getShort(index);
    }

    /**
     * 读取一条int数据
     */
    public int readInt() {
        return (int) read();
    }

    /**
     * 获取指定位置的boolean数据
     * @param order 元素位置
     */
    public int getInt(int order) {
        int index = getIndex(order);
        return data.getInt(index);
    }

    /**
     * 读取一条float数据
     */
    public float readFloat() {
        return (float) read();
    }

    /**
     * 获取指定位置的float数据
     * @param order 元素位置
     */
    public float getFloat(int order) {
        int index = getIndex(order);
        return data.getFloat(index);
    }

    /**
     * 读取一条double数据
     */
    public double readDouble() {
        return (double) read();
    }

    /**
     * 获取指定位置的double数据
     * @param order 元素位置
     */
    public double getDouble(int order) {
        int index = getIndex(order);
        return data.getDouble(index);
    }

    /**
     * 读取一条long数据
     */
    public long readLong() {
        return (long) read();
    }

    /**
     * 获取指定位置的long数据
     * @param order 元素位置
     */
    public long getLong(int order) {
        int index = getIndex(order);
        return data.getLong(index);
    }

    /**
     * 读取一条字符串数据
     */
    public String readStr() {
        return (String) read();
    }

    /**
     * 获取指定位置的字符串数据
     * @param order 元素位置
     */
    public String getStr(int order) {
        int index = getIndex(order);
        int length = data.getInt(index);
        return data.getCharSequence(index + 4, length, StandardCharsets.UTF_8).toString();
    }

    /**
     * 读取一条数据，并将该数据转换成接收类型
     * @param type 要读取的数据类型
     */
    public <T> T readObject(Class<T> type) {
        return (T) read();
    }

    /**
     * 获取指定位置的数组数据
     * @param order 元素位置
     * @param type 要读取的数据类型，必须是数组类型
     * @throws ClassCastException 如果type参数不是数组类型会抛出此异常
     */
    public <T> T getArray(int order, Class<T> type) {
        if (!type.isArray()) {
            throw new ClassCastException("类型: [" + type.getName() + "] 不是可用的数组类型");
        }
        int index = getIndex(order);
        byte _type = data.getByte(index);
        index += 1;
        Object[] arr;
        switch (_type) {
            case DataType.BYTE -> {
                int length = data.getInt(index);
                index += 4;
                arr = new Byte[length];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = data.getByte(index);
                    index += 1;
                }
            }
            case DataType.BOOL -> {
                int length = data.getInt(index);
                index += 4;
                arr = new Boolean[length];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = data.getBoolean(index);
                    index += 1;
                }
            }
            case DataType.SHORT -> {
                int length = data.getInt(index);
                index += 4;
                arr = new Short[length / 2];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = data.getShort(index);
                    index += 2;
                }
            }
            case DataType.CHAR -> {
                int length = data.getInt(index);
                index += 4;
                arr = new Character[length / 2];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = data.getChar(index);
                    index += 2;
                }
            }
            case DataType.INT -> {
                int length = data.getInt(index);
                index += 4;
                arr = new Integer[length / 4];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = data.getInt(index);
                    index += 4;
                }
            }
            case DataType.FLOAT -> {
                int length = data.getInt(index);
                index += 4;
                arr = new Float[length / 4];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = data.getFloat(index);
                    index += 4;
                }
            }
            case DataType.DOUBLE -> {
                int length = data.getInt(index);
                index += 4;
                arr = new Double[length / 8];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = data.getDouble(index);
                    index += 8;
                }
            }
            case DataType.LONG -> {
                int length = data.getInt(index);
                index += 4;
                arr = new Long[length / 8];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = data.getLong(index);
                    index += 8;
                }
            }
            default -> throw new MessageException("未知数据类型: " + type);
        }
        return (T) arr;
    }

    /**
     * 读取任意泛型类型List
     * @param <T> 元素类型
     */
    public <T> List<T> readList() {
        return (List<T>) read();
    }

    /**
     * 获取指定位置的List数据
     * @param order 元素位置
     * @return List
     */
    public List<?> getList(int order) {
        int index = getIndex(order);
        return _getList(index);
    }

    private List<?> _getList(int index) {
        int endIndex = data.getInt(index) + index;
        index += 4;
        List<Object> result = new ArrayList<>();
        while (index <= endIndex) {
            byte type = data.getByte(index);
            index += 1;
            switch (type) {
                case DataType.BYTE -> {
                    result.add(data.getByte(index));
                    index += 1;
                }
                case DataType.BOOL -> {
                    result.add(data.getBoolean(index));
                    index += 1;
                }
                case DataType.SHORT -> {
                    result.add(data.getShort(index));
                    index += 2;
                }
                case DataType.CHAR -> {
                    result.add(data.getChar(index));
                    index += 2;
                }
                case DataType.INT -> {
                    result.add(data.getInt(index));
                    index += 4;
                }
                case DataType.FLOAT -> {
                    result.add(data.getFloat(index));
                    index += 4;
                }
                case DataType.DOUBLE -> {
                    result.add(data.getDouble(index));
                    index += 8;
                }
                case DataType.LONG -> {
                    result.add(data.getLong(index));
                    index += 8;
                }
                case DataType.VARCHAR -> {
                    int length = data.getInt(index);
                    index += 4;
                    result.add(data.getCharSequence(index, length, StandardCharsets.UTF_8));
                }
                case DataType.ARRAY -> {
                    byte type2 = data.getByte(index);
                    index += 1;
                    switch (type2) {
                        case DataType.BYTE -> {
                            byte[] bytes = new byte[data.getInt(index)];
                            index += 4;
                            for (int i = 0; i < bytes.length; i++) {
                                bytes[i] = data.readByte();
                            }
                            data.getBytes(index, bytes);
                            index += bytes.length;
                            result.add(bytes);
                        }
                        case DataType.BOOL -> {
                            boolean[] booleans = new boolean[data.getInt(index)];
                            index += 4;
                            for (int i = 0; i < booleans.length; i++) {
                                booleans[i] = data.getBoolean(index);
                                index += 1;
                            }
                            result.add(booleans);
                        }
                        case DataType.SHORT -> {
                            short[] shorts = new short[data.getInt(index) / 2];
                            index += 4;
                            for (int i = 0; i < shorts.length; i++) {
                                shorts[i] = data.getShort(index);
                                index += 2;
                            }
                            result.add(shorts);
                        }
                        case DataType.CHAR -> {
                            char[] characters = new char[data.getInt(index) / 2];
                            index += 4;
                            for (int i = 0; i < characters.length; i++) {
                                characters[i] = data.getChar(index);
                                index += 2;
                            }
                            result.add(characters);
                        }
                        case DataType.INT -> {
                            int[] integers = new int[data.getInt(index) / 4];
                            index += 4;
                            for (int i = 0; i < integers.length; i++) {
                                integers[i] = data.getInt(index);
                                index += 2;
                            }
                            result.add(integers);
                        }
                        case DataType.FLOAT -> {
                            float[] floats = new float[data.getInt(index) / 4];
                            index += 4;
                            for (int i = 0; i < floats.length; i++) {
                                floats[i] = data.getFloat(index);
                                index += 4;
                            }
                            result.add(floats);
                        }
                        case DataType.DOUBLE -> {
                            double[] doubles = new double[data.getInt(index) / 8];
                            index += 4;
                            for (int i = 0; i < doubles.length; i++) {
                                doubles[i] = data.getDouble(index);
                                index += 8;
                            }
                            result.add(doubles);
                        }
                        case DataType.LONG -> {
                            long[] longs = new long[data.getInt(index) / 8];
                            index += 4;
                            for (int i = 0; i < longs.length; i++) {
                                longs[i] = data.getLong(index);
                                index += 8;
                            }
                            result.add(longs);
                        }
                    }
                }
                case DataType.DYNAMIC_ARRAY -> {
                    List<Object> _result = new ArrayList<>();
                    int _endIndex = index + data.getInt(index);
                    index += 4;
                    if (index <= _endIndex) {
                        _result.add(_getList(index));
                    }
                    result.add(_result);
                }
                default -> throw new MessageException("错误的消息类型");
            }
        }
        return result;
    }
}
