package cn.zhao.redis.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.concurrent.TimeUnit;

/**
 * @Author zhaowl
 * @Date 2021/1/6 14:13
 * @Version 1.0
 * @Description redis工具类
 */
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置缓存失效时间
     * @param key   键
     * @param time  时间（秒）
     * @return  true 成功 false 失败
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取缓存失效时间
     * @param key   键（不能为null）
     * @return  时间（秒）   时间为0时，代表永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     * @param key   键
     * @return  true 存在 false 不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除key
     * @param key   键
     */
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * 放入操作
     * @param key   键
     * @param value 值
     * @return  true 成功 false 失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 放入操作并设置时间
     * @param key   键
     * @param value 值
     * @param time  时间（秒）   时间小于等于0时，为永久
     * @return  true 成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                this.set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取value
     * @param key   键
     * @return  值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 递增
     * @param key   键
     * @param num   增加数（大于0）
     * @return
     */
    public long increment(String key, long num) {
        if (num <= 0)
            throw new RuntimeException("递增数必须大于0");
        return redisTemplate.opsForValue().increment(key, num);
    }

    /**
     * 递减
     * @param key   键
     * @param num   减少数（大于0）
     * @return
     */
    public long decrement(String key, long num) {
        return this.increment(key, -num);
    }

}
