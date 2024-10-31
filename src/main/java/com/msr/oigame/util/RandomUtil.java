package com.msr.oigame.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机工具类
 */
public class RandomUtil {
    /**
     * 用于随机选的数字
     */
    public static final String BASE_NUMBER = "0123456789";
    /**
     * 用于随机选的字符
     */
    public static final String BASE_CHAR = "abcdefghijklmnopqrstuvwxyz";
    /**
     * 用于随机选的字符和数字（小写）
     */
    public static final String BASE_CHAR_NUMBER_LOWER = BASE_CHAR + BASE_NUMBER;
    /**
     * 用于随机选的字符和数字（包括大写和小写字母）
     */
    public static final String BASE_CHAR_NUMBER = BASE_CHAR.toUpperCase() + BASE_CHAR_NUMBER_LOWER;


    /**
     * 获取随机数生成器对象<br>
     * ThreadLocalRandom是JDK 7之后提供并发产生随机数，能够解决多个线程发生的竞争争夺。
     *
     * <p>
     * 注意：此方法返回的{@link ThreadLocalRandom}不可以在多线程环境下共享对象，否则有重复随机数问题。
     * 见：https://www.jianshu.com/p/89dfe990295c
     * </p>
     *
     * @return {@link ThreadLocalRandom}
     * @since 3.1.2
     */
    public static ThreadLocalRandom getRandom() {
        return ThreadLocalRandom.current();
    }


    /**
     * 获得随机数int值
     *
     * @return 随机数
     * @see Random#nextInt()
     */
    public static int randomInt() {
        return getRandom().nextInt();
    }

    /**
     * 获得指定范围内的随机数 [0,limit)
     *
     * @param limitExclude 限制随机数的范围，不包括这个数
     * @return 随机数
     * @see Random#nextInt(int)
     */
    public static int randomInt(final int limitExclude) {
        return getRandom().nextInt(limitExclude);
    }

    /**
     * 获得指定范围内的随机数
     *
     * @param minInclude 最小数（包含）
     * @param maxExclude 最大数（不包含）
     * @return 随机数
     */
    public static int randomInt(final int minInclude, final int maxExclude) {
        return getRandom().nextInt(minInclude, maxExclude);
    }


    /**
     * 获得指定范围内的随机数 [0,limit)
     *
     * @param limitExclude 限制随机数的范围，不包括这个数
     * @return 随机数
     * @see ThreadLocalRandom#nextLong(long)
     */
    public static long randomLong(final long limitExclude) {
        return getRandom().nextLong(limitExclude);
    }
}
