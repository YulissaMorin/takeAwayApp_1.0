package com.yulissa.takeawayapp.util;

import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author yulissa
 */
public class RedisTemplateBitmapUtil {

    public static byte[] get(RedisTemplate redisTemplate, String key) {
        return (byte[]) redisTemplate.execute((RedisCallback) con -> con.get(key.getBytes()));
    }
    /**
     * 设置key字段第offset位bit数值
     *
     * @param key    字段
     * @param offset 位置
     * @param value  数值
     */
    public static void setBit(RedisTemplate redisTemplate, String key, long offset, boolean value) {
        redisTemplate.execute((RedisCallback) con -> con.setBit(key.getBytes(), offset, value));
    }

    /**
     * 判断该key字段offset位否为1
     *
     * @param key    字段
     * @param offset 位置
     * @return 结果
     */
    public static boolean getBit(RedisTemplate redisTemplate, String key, long offset) {
        return (boolean) redisTemplate.execute((RedisCallback) con -> con.getBit(key.getBytes(), offset));

    }

    /**
     * 统计key字段value为1的总数
     *
     * @param key 字段
     * @return 总数
     */
    public static Long bitCount(RedisTemplate redisTemplate, String key) {
        return (Long) redisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes()));
    }

    /**
     * 统计key字段value为1的总数,从start开始到end结束
     *
     * @param key   字段
     * @param start 起始
     * @param end   结束
     * @return 总数
     */
    public static Long bitCount(RedisTemplate redisTemplate, String key, Long start, Long end) {
        return (Long) redisTemplate.execute((RedisCallback) con -> con.bitCount(key.getBytes(), start, end));
    }

    /**
     * 取多个key并集并计算总数
     *
     * @param key key
     * @return 总数
     */
    public static Long OpOrCount(RedisTemplate redisTemplate, String... key) {
        byte[][] keys = new byte[key.length][];
        for (int i = 0; i < key.length; i++) {
            keys[i] = key[i].getBytes();
        }
        redisTemplate.execute((RedisCallback) con -> con.bitOp(RedisStringCommands.BitOperation.OR, (key[0] + "To" + key[key.length - 1]).getBytes(), keys));
        redisTemplate.expire(key[0] + "To" + key[key.length - 1], 10, TimeUnit.SECONDS);
        return bitCount(redisTemplate,key[0] + "To" + key[key.length - 1]);
    }

    /**
     * 取多个key的交集并计算总数
     *
     * @param key key
     * @return 总数
     */
    public static Long OpAndCount(RedisTemplate redisTemplate, String... key) {
        byte[][] keys = new byte[key.length][];
        for (int i = 0; i < key.length; i++) {
            keys[i] = key[i].getBytes();
        }
        redisTemplate.execute((RedisCallback) con -> con.bitOp(RedisStringCommands.BitOperation.AND, (key[0] + "To" + key[key.length - 1]).getBytes(), keys));
        redisTemplate.expire(key[0] + "To" + key[key.length - 1], 10, TimeUnit.SECONDS);
        return bitCount(redisTemplate,key[0] + "To" + key[key.length - 1]);
    }

    /**
     * 取多个key的补集并计算总数
     *
     * @param key key
     * @return 总数
     */
    public static Long OpXorCount(RedisTemplate redisTemplate, String... key) {
        byte[][] keys = new byte[key.length][];
        for (int i = 0; i < key.length; i++) {
            keys[i] = key[i].getBytes();
        }
        redisTemplate.execute((RedisCallback) con -> con.bitOp(RedisStringCommands.BitOperation.XOR, (key[0] + "To" + key[key.length - 1]).getBytes(), keys));
        redisTemplate.expire(key[0] + "To" + key[key.length - 1], 10, TimeUnit.SECONDS);
        return bitCount(redisTemplate,key[0] + "To" + key[key.length - 1]);
    }

    /**
     * 取多个key的否集并计算总数
     *
     * @param key key
     * @return 总数
     */
    public static Long OpNotCount(RedisTemplate redisTemplate, String... key) {
        byte[][] keys = new byte[key.length][];
        for (int i = 0; i < key.length; i++) {
            keys[i] = key[i].getBytes();
        }
        redisTemplate.execute((RedisCallback) con -> con.bitOp(RedisStringCommands.BitOperation.NOT, (key[0] + "To" + key[key.length - 1]).getBytes(), keys));
        redisTemplate.expire(key[0] + "To" + key[key.length - 1], 10, TimeUnit.SECONDS);
        return bitCount(redisTemplate,key[0] + "To" + key[key.length - 1]);
    }

}
