package com.kushui.rpc.common.util;

import sun.nio.ch.ThreadPool;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtil {

    public static ThreadPoolExecutor createThreadPool(final String name, int corePoolNum, int maxPoolnum) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolNum, maxPoolnum, 60L, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "netty-rpc-" + name + "-" + r.hashCode());
                    }
                },
                new ThreadPoolExecutor.AbortPolicy()
        );

        return threadPoolExecutor;
    }
}
