package com.xyz.bu.cache;

import com.xyz.bu.utils.CastUtils;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.lang.NonNull;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xyz
 * @date 2019-06-25
 */
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

    public void setex(String key, long seconds, String val) {
        stringRedisTemplate.opsForValue().set(key, val, seconds, TimeUnit.SECONDS);
    }

    public void setex(String key, long seconds, String val, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, val, seconds, timeUnit);
    }

    public boolean setexnx(String key, String val, long seconds) {
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(key, val, seconds, TimeUnit.SECONDS);
        return Objects.nonNull(result) ? result : false;
    }

    public boolean setexnx(String key, String val, long seconds, TimeUnit timeUnit) {
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(key, val, seconds, timeUnit);
        return Objects.nonNull(result) ? result : false;
    }

    public void mset(Map<String, String> hash) {
        stringRedisTemplate.opsForValue().multiSet(hash);
    }

    public void msetex(Map<String, String> hash, int seconds) {
        stringRedisTemplate.opsForValue().multiSet(hash);
        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            connection.openPipeline();
            hash.forEach((key, value) -> connection.expire(key.getBytes(), seconds));
            connection.close();
            return null;
        });
    }

    public List<String> mget(List<String> keys) {
        List<String> list = stringRedisTemplate.opsForValue().multiGet(keys);
        return list != null ? list.stream().filter(Objects::nonNull).collect(Collectors.toList()) :
                Collections.emptyList();
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

    public Long sadd(String key, String[] vals) {
        return stringRedisTemplate.opsForSet().add(key, vals);
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
        // todo
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

    public String hget(String key, String field) {
        return (String) stringRedisTemplate.opsForHash().get(key, field);
    }

    public Map<String, String> hmget(String key) {
        Map<Object, Object> data = stringRedisTemplate.opsForHash().entries(key);
        Map<String, String> result = new HashMap<>(data.size());
        data.forEach((k, v) -> {
            result.put((String) k, (String) v);
        });
        return result;
    }

    public void hmdel(String key, String... hashKeys) {
        stringRedisTemplate.opsForHash().delete(key, (Object[]) hashKeys);
    }

    public long hsize(String key) {
        return stringRedisTemplate.opsForHash().size(key);
    }

    public void hscan(String key) {
        // todo
        ScanOptions scanOptions = ScanOptions.scanOptions().count(1).match("0").build();
        stringRedisTemplate.opsForHash().scan(key, scanOptions);
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
     * 大小
     *
     * @param key 键
     * @return 大小
     */
    public long zsize(String key) {
        Long size = stringRedisTemplate.opsForZSet().size(key);
        return Objects.nonNull(size) ? size : 0;
    }

    /**
     * 正序 按索引区间查出值
     *
     * @param key   键
     * @param start 索引开始位
     * @param end   索引结束位
     * @return 值的Set
     */
    public Set<String> zrange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 正序 按分数区间查出所有值
     *
     * @param key 键
     * @param min 分数min
     * @param max 分数max
     * @return 值的Set
     */
    public Set<String> zrangeByScore(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 正序 按分数区间分页查询
     *
     * @param key    键
     * @param min    分数min
     * @param max    分数max
     * @param offset 开始点 (页数-1)*count
     * @param count  每页数量
     * @return 值的Set
     */
    public Set<String> zrangeByScore(String key, double min, double max, long offset, long count) {
        return stringRedisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
    }

    /**
     * 逆序 按索引区间查出值
     *
     * @param key   键
     * @param start 索引开始位
     * @param end   索引结束位
     * @return 值的Set
     */
    public Set<String> zreserveRange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 逆序 按分数区间查出所有值
     *
     * @param key 键
     * @param min 分数min
     * @param max 分数max
     * @return 值的Set
     */
    public Set<String> zreverseRangeByScore(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }

    /**
     * 逆序 按分数区间分页查询
     *
     * @param key    键
     * @param min    分数min
     * @param max    分数max
     * @param offset 开始点 (页数-1)*count
     * @param count  每页数量
     * @return 值的Set
     */
    public Set<String> zreverseRangeByScore(String key, double min, double max, long offset, long count) {
        return stringRedisTemplate.opsForZSet().reverseRangeByScore(key, min, max, offset, count);
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

    @NonNull
    public List<String> lpopBatch(String key, int size) {
        List<Object> resultList = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            connection.openPipeline();
            byte[] keyByte = key.getBytes();
            // 取出
            connection.lRange(keyByte, 0, size - 1);
            // 截取
            connection.lTrim(keyByte, size, -1);
            connection.close();
            return null;
        });

        return CastUtils.castList(resultList.get(0), String.class);
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
     * 脚本系列
     */
    /**
     * 执行脚本
     *
     * @param scripts    例：return redis.call('GET',KEYS[1])
     * @param resultType 返回值类型的class
     * @param keys       键列表 没有值也不得传null
     * @param args       值数组 没有值也不得传null
     * @param <T>        指定类型
     * @return 指定类型的返回值
     */
    public <T> T eval(@NonNull String scripts, @NonNull Class<T> resultType, @NonNull List<String> keys, @NonNull Object[] args) {
        DefaultRedisScript<T> redisScript = new DefaultRedisScript<>(scripts, resultType);
        return stringRedisTemplate.execute(redisScript, keys, args);
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
