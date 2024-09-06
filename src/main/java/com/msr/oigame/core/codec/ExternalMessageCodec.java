package com.msr.oigame.core.codec;

import com.msr.oigame.common.exception.WriteEmptyDataException;
import com.msr.oigame.core.json.JsonSerializer;
import com.msr.oigame.core.json.JsonSerializerFactory;
import com.msr.oigame.core.protocol.BaseMessage;
import com.msr.oigame.core.protocol.DataType;
import com.msr.oigame.core.protocol.ExternalMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ExternalMessageCodec implements MessageCodec {

    private static boolean useProtobuf;
    private static Class<?> messageListClass;
    private static Method protobufToByteArray;

    private static final JsonSerializer jsonSerializer;

    static {
        try {
            messageListClass = Class.forName("com.google.protobuf.MessageLite");
            protobufToByteArray = messageListClass.getMethod("toByteArray");
            useProtobuf = true;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            useProtobuf = false;
        }

        jsonSerializer = JsonSerializerFactory.getJsonSerializer();
    }

    /**
     * 将数据对象编码成字节数组
     *
     * @param data 数据对象
     * @return bytes
     */
    @Override
    public ByteBuf encode(Object data) {
        if (data.getClass().isArray()) {
            Object[] array = (Object[]) data;
            int cmd = (int) array[0];
            if (array.length == 1) {
                return encodeMsg(cmd).getData();
            }
            Object[] dataList = new Object[array.length - 1];
            System.arraycopy(array, 1, dataList, 0, dataList.length);
            return encodeMsg(cmd, dataList).getData();
        } else if (data instanceof Collection<?> collection) {
            Iterator<?> iterator = collection.iterator();
            int cmd = (int) iterator.next();
            if (iterator.hasNext()) {
                Object[] dataList = new Object[collection.size()];
                System.arraycopy(collection.toArray(), 1, dataList, 0, dataList.length);
                return encodeMsg(cmd, dataList).getData();
            } else {
                return encodeMsg(cmd).getData();
            }
        } else if (data instanceof Iterable<?> iterable) {
            Iterator<?> iterator = iterable.iterator();
            int cmd = (int) iterator.next();
            if (iterator.hasNext()) {
                List<Object> dataList = new ArrayList<>();
                while (iterator.hasNext()) {
                    dataList.add(iterator.next());
                }
                return encodeMsg(cmd, dataList.toArray()).getData();
            } else {
                return encodeMsg(cmd).getData();
            }
        }
        throw new UnsupportedOperationException();
    }

    public ExternalMessage encodeMsg(int cmd, Object... data) {
        // 创建一个堆缓冲区的byteBuf
        ByteBuf byteBuf = Unpooled.buffer(4);
        // 写入指令
        byteBuf.writeInt(cmd);
        // 写入数据
        encode(byteBuf, data);
        return new ExternalMessage(byteBuf);
    }

    /**
     * 将数据按一定格式写入byteBuf
     * 具体格式参照文档 message.md
     *
     * @param byteBuf 写入目标byteBuf
     * @param data    待写入的数据
     * @return 写入的数据长度
     */
    private static int encode(ByteBuf byteBuf, Object[] data) {
        // 写入开始位置，用于计算总写入长度
        int startIndex = byteBuf.writerIndex();
        for (Object item : data) {
            if (item == null) {
                throw new WriteEmptyDataException("构建消息时待写入数据为空");
            }

            if (item instanceof Integer) {
                byteBuf.writeByte(DataType.INT);
                byteBuf.writeInt((int) item);
            } else if (item instanceof Boolean) {
                byteBuf.writeByte(DataType.BOOL);
                byteBuf.writeBoolean((boolean) item);
            } else if (item instanceof Collection) {
                byteBuf.writeByte(DataType.DYNAMIC_ARRAY);
                int _startIndex = byteBuf.writerIndex();
                byteBuf.writerIndex(_startIndex + 4);
                int _length = encode(byteBuf, ((Collection<?>) item).toArray());
                byteBuf.setInt(_startIndex, _length);
            } else if (item.getClass().isArray()) {
                boolean success = encodeArray(byteBuf, item);
                if (!success) {
                    byteBuf.writeByte(DataType.DYNAMIC_ARRAY);
                    int _startIndex = byteBuf.writerIndex();
                    byteBuf.writerIndex(_startIndex + 4);
                    int _length = encode(byteBuf, (Object[]) item);
                    byteBuf.setInt(_startIndex, _length);
                }
            } else if (item instanceof CharSequence) {
                byteBuf.writeByte(DataType.VARCHAR);
                int _startIndex = byteBuf.writerIndex();
                byteBuf.writerIndex(_startIndex + 4);
                int _length = byteBuf.writeCharSequence((CharSequence) item, StandardCharsets.UTF_8);
                byteBuf.setInt(_startIndex, _length);
            } else if (useProtobuf && messageListClass.isInstance(item)) {
                byteBuf.writeByte(DataType.ARRAY);
                byteBuf.writeByte(DataType.BYTE);
                byte[] protoBufBytes;
                try {
                    protoBufBytes = (byte[]) protobufToByteArray.invoke(item);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                byteBuf.writeInt(protoBufBytes.length);
                byteBuf.writeBytes(protoBufBytes);
            } else if (item instanceof Double) {
                byteBuf.writeByte(DataType.DOUBLE);
                byteBuf.writeDouble((double) item);
            } else if (item instanceof Byte) {
                byteBuf.writeByte(DataType.BYTE);
                byteBuf.writeByte((byte) item);
            } else if (item instanceof Long) {
                byteBuf.writeByte(DataType.LONG);
                byteBuf.writeLong((long) item);
            } else if (item instanceof Float) {
                byteBuf.writeByte(DataType.FLOAT);
                byteBuf.writeFloat((float) item);
            } else if (item instanceof Short) {
                byteBuf.writeByte(DataType.SHORT);
                byteBuf.writeShort((short) item);
            } else if (item instanceof Character) {
                byteBuf.writeByte(DataType.CHAR);
                byteBuf.writeChar((char) item);
            } else if (item instanceof Map) {
                byteBuf.writeByte(DataType.VARCHAR);
                int _startIndex = byteBuf.writerIndex();
                byteBuf.writerIndex(_startIndex + 2);
                int _length = byteBuf.writeCharSequence(jsonSerializer.serialize(item), StandardCharsets.UTF_8);

                byteBuf.setInt(_startIndex, _length);
            } else {
                throw new UnsupportedMessageTypeException(item);
            }
        }
        return byteBuf.writerIndex() - startIndex;
    }

    /**
     * 编码数据
     *
     * @param byteBuf 写入目标byteBuf
     * @param data    待写入的数组
     * @return 是否写入成功
     */
    private static boolean encodeArray(ByteBuf byteBuf, Object data) {
        // 基本类型数组
        switch (data) {
            case byte[] bytes -> {
                byteBuf.writeByte(DataType.ARRAY);
                byteBuf.writeByte(DataType.BYTE);
                byteBuf.writeInt(bytes.length);
                byteBuf.writeBytes(bytes);
            }
            case boolean[] booleans -> {
                byteBuf.writeByte(DataType.ARRAY);
                byteBuf.writeByte(DataType.BOOL);
                byteBuf.writeInt(booleans.length);
                for (boolean b : booleans) {
                    byteBuf.writeBoolean(b);
                }
            }
            case short[] shorts -> {
                byteBuf.writeByte(DataType.ARRAY);
                byteBuf.writeByte(DataType.SHORT);
                byteBuf.writeInt(shorts.length * 2);
                for (short s : shorts) {
                    byteBuf.writeShort(s);
                }
            }
            case char[] characters -> {
                byteBuf.writeByte(DataType.ARRAY);
                byteBuf.writeByte(DataType.CHAR);
                byteBuf.writeInt(characters.length * 2);
                for (char c : characters) {
                    byteBuf.writeChar(c);
                }
            }
            case int[] integers -> {
                byteBuf.writeByte(DataType.ARRAY);
                byteBuf.writeByte(DataType.INT);
                byteBuf.writeInt(integers.length * 4);
                for (int i : integers) {
                    byteBuf.writeInt(i);
                }
            }
            case float[] floats -> {
                byteBuf.writeByte(DataType.ARRAY);
                byteBuf.writeByte(DataType.FLOAT);
                byteBuf.writeInt(floats.length * 4);
                for (float f : floats) {
                    byteBuf.writeFloat(f);
                }
            }
            case double[] doubles -> {
                byteBuf.writeByte(DataType.ARRAY);
                byteBuf.writeByte(DataType.DOUBLE);
                byteBuf.writeInt(doubles.length * 8);
                for (double d : doubles) {
                    byteBuf.writeDouble(d);
                }
            }
            case long[] longs -> {
                byteBuf.writeByte(DataType.ARRAY);
                byteBuf.writeByte(DataType.LONG);
                byteBuf.writeInt(longs.length * 8);
                for (long l : longs) {
                    byteBuf.writeLong(l);
                }
            }
            case null, default -> {
                return false;
            }
        }
        return true;
    }

    /**
     * 将netty字节缓冲解码成消息对象
     * @param data netty字节缓冲
     * @return 消息对象
     */
    @Override
    public BaseMessage decode(ByteBuf data) {
        return new ExternalMessage(data);
    }
}
