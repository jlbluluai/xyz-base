package com.xyz.bu.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 重试工具类
 *
 * @author xyz
 * @date 2020/9/22
 */
public class RetryUtils {

    private RetryUtils() {
    }

    /**
     * function not need input arg
     *
     * @param supplier      function to execute
     * @param times         the times of retry
     * @param intervalMills thr interval mills between two retry, is invalid if interval <= 0
     * @return true/false
     */
    public static boolean retry(@NonNull Supplier<Boolean> supplier, int times, long intervalMills) {
        return new AbstractRetry() {
            @Override
            public boolean handle() {
                return supplier.get();
            }
        }.retry(times, intervalMills);
    }

    /**
     * function need one input arg
     *
     * @param param         input param
     * @param predicate     function to execute
     * @param times         the times of retry
     * @param intervalMills thr interval mills between two retry, is invalid if interval <= 0
     * @return true/false
     */
    public static <T> boolean retry(@Nullable T param, @NonNull Predicate<T> predicate, int times, long intervalMills) {
        return new AbstractRetry() {
            @Override
            public boolean handle() {
                return predicate.test(param);
            }
        }.retry(times, intervalMills);
    }

    /**
     * function need two input arg
     *
     * @param param1        input param 1
     * @param param2        input param 2
     * @param predicate     function to execute
     * @param times         the times of retry
     * @param intervalMills thr interval mills between two retry, is invalid if interval <= 0
     * @return true/false
     */
    public static <T, U> boolean retry(@Nullable T param1, @Nullable U param2, @NonNull BiPredicate<T, U> predicate, int times, long intervalMills) {
        return new AbstractRetry() {
            @Override
            public boolean handle() {
                return predicate.test(param1, param2);
            }
        }.retry(times, intervalMills);
    }

    /**
     * 重试的模板
     */
    public static abstract class AbstractRetry {

        private static final Logger logger = LoggerFactory.getLogger(AbstractRetry.class);

        /**
         * 重试
         *
         * @param times    重试次数
         * @param interval 重试间隔毫秒数 若为<=0 不sleep
         * @return 执行结果
         */
        public boolean retry(int times, long interval) {
            for (int i = 0; i < times; i++) {
                if (handle()) {
                    return true;
                }
                logger.info("retry times={}", i + 1);
                if (interval > 0) {
                    try {
                        Thread.sleep(interval);
                    } catch (Exception e) {
                        logger.error("retry sleep error", e);
                    }
                }
            }
            logger.info("retry end but result is false");
            return false;
        }

        /**
         * 处理
         *
         * @return true/false
         */
        public abstract boolean handle();
    }

}
