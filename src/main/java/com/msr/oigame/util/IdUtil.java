package com.msr.oigame.util;

import io.netty.util.NetUtil;
import org.springframework.util.Assert;
import org.springframework.util.function.SingletonSupplier;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

public class IdUtil {
    private static final SingletonSupplier<Snowflake> snowflakeSingleton = SingletonSupplier.of(Snowflake::new);

    /**
     * 获取单例的Twitter的Snowflake 算法生成器对象<br>
     * 分布式系统中，有一些需要使用全局唯一ID的场景，有些时候我们希望能使用一种简单一些的ID，并且希望ID能够按照时间有序生成。
     *
     * <p>
     * snowflake的结构如下(每部分用-分开):<br>
     *
     * <pre>
     * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
     * </pre>
     * <p>
     * 第一位为未使用，接下来的41位为毫秒级时间(41位的长度可以使用69年)<br>
     * 然后是5位datacenterId和5位workerId(10位的长度最多支持部署1024个节点）<br>
     * 最后12位是毫秒内的计数（12位的计数顺序号支持每个节点每毫秒产生4096个ID序号）
     *
     * <p>
     * 参考：http://www.cnblogs.com/relucent/p/4955340.html
     *
     * @return {@link Snowflake}
     * @since 5.7.3
     */
    public static Snowflake getSnowflake() {
        return snowflakeSingleton.get();
    }

    /**
     * 获取当前进程ID，首先获取进程名称，读取@前的ID值，如果不存在，则读取进程名的hash值
     *
     * @return 进程ID
     */
    private static int getPid() {
        final String processName = ManagementFactory.getRuntimeMXBean().getName();
        if (StrUtil.isBlank(processName)) {
            throw new RuntimeException("Process name is blank!");
        }
        final int atIndex = processName.indexOf('@');
        if (atIndex > 0) {
            return Integer.parseInt(processName.substring(0, atIndex));
        } else {
            return processName.hashCode();
        }
    }

    /**
     * 获取机器ID，使用进程ID配合数据中心ID生成<br>
     * 机器依赖于本进程ID或进程名的Hash值。
     *
     * <p>
     * 此算法来自于mybatis-plus#Sequence
     * </p>
     *
     * @param datacenterId 数据中心ID
     * @param maxWorkerId  最大的机器节点ID
     * @return ID
     * @since 5.7.3
     */
    public static long getWorkerId(long datacenterId, long maxWorkerId) {
        final StringBuilder mpid = new StringBuilder();
        mpid.append(datacenterId);
        try {
            mpid.append(getPid());
        } catch (RuntimeException ignored) {
            //ignored
        }
        /*
         * MAC + PID 的 hashcode 获取16个低位
         */
        return (mpid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    /**
     * 获取数据中心ID<br>
     * 数据中心ID依赖于本地网卡MAC地址。
     * <p>
     * 此算法来自于mybatis-plus#Sequence
     * </p>
     *
     * @param maxDatacenterId 最大的中心ID
     * @return 数据中心ID
     * @since 5.7.3
     */
    public static long getDataCenterId(long maxDatacenterId) {
        Assert.isTrue(maxDatacenterId > 0, "maxDatacenterId must be between 0");
        if (maxDatacenterId == Long.MAX_VALUE) {
            maxDatacenterId -= 1;
        }
        long id = 1L;
        byte[] mac = null;
        try{
            InetAddress localhost = NetUtil.LOCALHOST;
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localhost);
            if (networkInterface != null) {
                mac = networkInterface.getHardwareAddress();
            }
        } catch (SocketException ignored) {
            // ignored
        }
        if (null != mac) {
            id = ((0x000000FF & (long) mac[mac.length - 2])
                    | (0x0000FF00 & (((long) mac[mac.length - 1]) << 8))) >> 6;
            id = id % (maxDatacenterId + 1);
        }

        return id;
    }

    /**
     * 简单获取Snowflake 的 nextId
     * 终端ID 数据中心ID 默认为1
     *
     * @return nextId
     * @since 5.7.18
     */
    public static long getSnowflakeNextId() {
        return getSnowflake().nextId();
    }
}
