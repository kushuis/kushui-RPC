package com.kushui.rpc.client.handler;

import com.kushui.rpc.common.codec.RpcRequest;
import com.kushui.rpc.common.codec.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

public class RpcFuture implements Future<Object> {
    private static final Logger logger = LoggerFactory.getLogger(RpcFuture.class);
    private Sync sync;
    private RpcRequest request;
    private RpcResponse response;
    private long startTime;
    private long responseTimeThreshold = 5000;


    //自定义同步器，继承AQS
    static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 1L;

        //future status
        private final int done = 1;
        private final int pending = 0;
        //状态为done时说明可以获取结果
        @Override
        protected boolean tryAcquire(int arg) {
            return getState() == done;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == pending) {
                if (compareAndSetState(pending, done)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
        protected boolean isDone() {
            return getState() == done;
        }
    }

    public RpcFuture(RpcRequest request) {
        this.sync = new Sync();
        this.request = request;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public Object get()  {
        //会调用tryAcquire()返回true就继续执行，返回false会进入阻塞队列直到被唤醒
        sync.acquire(1);
        if (this.response != null) {
            return this.response.getResult();
        } else {
            return null;
        }
    }
    //线程执行future.get()实际是调用tryAcquire方法然后返回false
    //当前线程被放入阻塞队列等待，当另外一个线程执行tryRelease()时，将state状态修改为1，然后唤醒第一个线程让他重新tryAcquire
    public void done(RpcResponse reponse) {
        this.response = reponse;
        sync.release(1);

        // 判断有没有超时
        long responseTime = System.currentTimeMillis() - startTime;
        if (responseTime > this.responseTimeThreshold) {
            logger.warn("Service response time is too slow. Request id = " + reponse.getRequestId() + ". Response Time = " + responseTime + "ms");
        }
    }
    //取消任务
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }
    //是否成功取消任务
    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }
    //判断这个异步任务是否完成
    @Override
    public boolean isDone() {
        return sync.isDone();
    }


    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException {
        return null;
    }
}
