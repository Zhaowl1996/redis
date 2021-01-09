package cn.zhao.redis.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Set;
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


    //  =========================== common ===========================

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


    //  =========================== String ===========================

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
     * @return  变化后的值
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
     * @return  变化后的值
     */
    public long decrement(String key, long num) {
        return this.increment(key, -num);
    }


    //  =========================== Hash ===========================

    /**
     * hash中放入数据，不存在会创建
     * @param h     表
     * @param hk    键
     * @param hv    值
     * @return  true 成功 false 失败
     */
    public boolean hashSet(String h, String hk, Object hv) {
        try {
            redisTemplate.opsForHash().put(h, hk, hv);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * hash中放入数据并设置缓存时间，不存在会创建
     * @param h     表
     * @param hk    键
     * @param hv    值
     * @param time  时间（秒）   已存在的时间会被更新
     * @return  true 成功 false 失败
     */
    public boolean hashSet(String h, String hk, Object hv, long time) {
        try {
            redisTemplate.opsForHash().put(h, hk, hv);
            if (time > 0) {
                this.expire(h, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * hash中放入多个数据
     * @param h     表
     * @param map   多个键值对
     * @return  true 成功 false 失败
     */
    public boolean hashSetAll(String h, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(h, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * hash中放入多个数据并设置缓存时间
     * @param h     表
     * @param map   多个键值对
     * @param time  缓存时间
     * @return  true 成功 false 失败
     */
    public boolean hashSetAll(String h, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(h, map);
            if (time > 0) {
                this.expire(h, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取hash中key对应的值
     * @param h     表
     * @param hk    键
     * @return  值
     */
    public Object hashGet(String h, String hk) {
        return redisTemplate.opsForHash().get(h, hk);
    }

    /**
     * 获取hash中所有键值对
     * @param h     表
     * @return  键值对
     */
    public Map<String, Object> hashGetAll(String h) {
        return redisTemplate.opsForHash().entries(h);
    }

    /**
     * 删除hash中key的值
     * @param h     表
     * @param hk    键（多个值）
     */
    public void hashDel(String h, String... hk) {
        redisTemplate.opsForHash().delete(h, hk);
    }

    /**
     * 判断hash中是否包含该key和对应的值
     * @param h     表
     * @param hk    键
     * @return true 存在 false 不存在
     */
    public boolean hasHashKey(String h, String hk) {
        return redisTemplate.opsForHash().hasKey(h, hk);
    }

    /**
     * hash递增
     * @param h     表
     * @param hk    键
     * @param num   增加数（大于0）
     * @return  变化后的值
     */
    public double hashIncrement(String h, String hk, double num) {
        return redisTemplate.opsForHash().increment(h, hk, num);
    }

    /**
     * hash递减
     * @param h     表
     * @param hk    键
     * @param num   减少数（大于0）
     * @return  变化后的值
     */
    public double hashDecrement(String h, String hk, double num) {
        return this.hashIncrement(h, hk, -num);
    }


    //  =========================== Set ===========================

    /**
     * set中放入多个数据
     * @param key       键
     * @param values    值（多个）
     * @return          成功个数，异常返回-1
     */
    public long setSetAll(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * set中放入多个数据并设置缓存时间
     * @param key       键
     * @param values    值（多个）
     * @param time      缓存时间（秒）
     * @return          成功个数，异常返回-1
     */
    public long setSetAll(String key, long time, Object... values) {
        try {
            long result = redisTemplate.opsForSet().add(key, values);
            if (time > 0)
                expire(key, time);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Set获取值
     * @param key   键
     * @return 获取值集合
     */
    public Set<Object> setGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Set删除元素
     * @param key       键
     * @param values    值（多个）
     * @return          删除个数，异常返回-1
     */
    public long setDel(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 判断Set中是否包含当前key，value
     * @param key   键
     * @param value 值
     * @return  true 存在 false 不存在
     */
    public boolean setHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取Set中key对应value的长度
     * @param key   键
     * @return  值长度，异常返回-1
     */
    public long setGetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
