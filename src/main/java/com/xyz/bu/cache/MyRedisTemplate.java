package com.xyz.bu.cache;

import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author xyz
 * @date 2019-06-25
 */
@Repository
public class MyRedisTemplate {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取原始模板
     *
     * @return
     */
    public StringRedisTemplate originalTemplate() {
        return stringRedisTemplate;
    }

    /**
     * 通用
     */
    public boolean expire(String key, int seconds) {
        Boolean isSuccess = stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
        return Objects.nonNull(isSuccess) ? isSuccess : false;
    }

    public int ttl(String key) {
        Long seconds = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return Objects.nonNull(seconds) ? seconds.intValue() : -2;
    }

    public boolean del(String key) {
        Boolean isSuccess = stringRedisTemplate.delete(key);
        return Objects.nonNull(isSuccess) ? isSuccess : false;
    }

    /**
     * string系列
     */
    public void set(String key, String val) {
        stringRedisTemplate.opsForValue().set(key, val);
    }

    public void setex(String key, int seconds, String val) {
        stringRedisTemplate.opsForValue().set(key, val, seconds, TimeUnit.SECONDS);
    }

    public void mset(Map<String, String> hash) {
        stringRedisTemplate.opsForValue().multiSet(hash);
    }

    public void msetex(Map<String, String> hash, int seconds) {
        stringRedisTemplate.opsForValue().multiSet(hash);
        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            connection.openPipeline();
            hash.forEach((key, value) -> connection.expire(key.getBytes(), seconds));
            return null;
        });
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public long incr(String key) {
        Long value = stringRedisTemplate.opsForValue().increment(key);
        return Objects.nonNull(value) ? value : 0;
    }

    public long decr(String key) {
        Long value = stringRedisTemplate.opsForValue().decrement(key);
        return Objects.nonNull(value) ? value : 0;
    }

    /**
     * set系列
     */
    public Long sadd(String key, String val) {
        return stringRedisTemplate.opsForSet().add(key, val);
    }

    public Set<String> smembers(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    public Long srem(String key, String val) {
        return stringRedisTemplate.opsForSet().remove(key, val);
    }

    public void scan(String key) {
        stringRedisTemplate.opsForSet().scan(key, ScanOptions.NONE);
    }

    public void sscan(String key) {
        stringRedisTemplate.opsForSet().scan(key, ScanOptions.scanOptions().count(10).match("0").build());
    }

    /**
     * hash系列
     */
    public void hset(String key, String field, String val) {
        stringRedisTemplate.opsForHash().put(key, field, val);
    }

    public void hmset(String key, Map<String, String> hash) {
        stringRedisTemplate.opsForHash().putAll(key, hash);
    }

    /**
     * zset系列
     */
    public void zadd(String key, String value, double score) {
        stringRedisTemplate.opsForZSet().add(key, value, score);
    }

    public void mzadd(String key, Set<ZSetOperations.TypedTuple<String>> values) {
        stringRedisTemplate.opsForZSet().add(key, values);
    }

    /**
     * list系列
     */
    public void lpush(String key, String value) {
        stringRedisTemplate.opsForList().leftPush(key, value);
    }

    public void lpushBatch(String key, String[] values) {
        stringRedisTemplate.opsForList().leftPushAll(key, values);
    }

    public void lpushBatch(String key, Collection<String> collection) {
        stringRedisTemplate.opsForList().leftPushAll(key, collection);
    }

    public String lpop(String key) {
        return stringRedisTemplate.opsForList().leftPop(key);
    }

    public List<String> lpopBatch(String key, int size) {
        List<String> result = stringRedisTemplate.opsForList().range(key, 0, size - 1);
        if (result == null) {
            return Collections.emptyList();
        }

        stringRedisTemplate.opsForList().trim(key, size, -1);
        return result;
    }

    public void rpush(String key, String value) {
        stringRedisTemplate.opsForList().rightPush(key, value);
    }

    public void rpushBatch(String key, String[] values) {
        stringRedisTemplate.opsForList().rightPushAll(key, values);
    }

    public void rpushBatch(String key, Collection<String> collection) {
        stringRedisTemplate.opsForList().rightPushAll(key, collection);
    }

    public String rpop(String key) {
        return stringRedisTemplate.opsForList().rightPop(key);
    }

    public long lsize(String key) {
        Long size = stringRedisTemplate.opsForList().size(key);
        return size != null ? size : 0;
    }

    /**
     * right push and left trim we can confirm ,
     * but left push and right trim perhaps lose data
     */
    @Deprecated
    public List<String> rpopBatch(String key, int size) {
        return null;
    }


    /**
     * 管道例子
     */
    public void pipeline() {
        //        1.executePipelined 重写 入参 RedisCallback 的doInRedis方法
        List<Object> resultList = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
//                2.connection 打开管道
            connection.openPipeline();

//                3.connection 给本次管道内添加 要一次性执行的多条命令

//                3.1 一个set操作
            byte[] key1 = "mykey1".getBytes();
            byte[] value1 = "字符串value".getBytes();
            connection.set(key1, value1);

//                3.2一个批量mset操作
            Map<byte[], byte[]> tuple = new HashMap<>();
            tuple.put("m_mykey1".getBytes(), "m_value1".getBytes());
            tuple.put("m_mykey2".getBytes(), "m_value2".getBytes());
            tuple.put("m_mykey3".getBytes(), "m_value3".getBytes());
            connection.mSet(tuple);

//                 3.3一个get操作
            connection.get("m_mykey2".getBytes());

//                4.关闭管道 不需要close 否则拿不到返回值
//                connection.closePipeline();

//                这里一定要返回null，最终pipeline的执行结果，才会返回给最外层
            return null;
        });

//        5.最后对redis pipeline管道操作返回结果进行判断和业务补偿
        for (Object str : resultList) {
            System.out.println(str);
        }
    }

}
