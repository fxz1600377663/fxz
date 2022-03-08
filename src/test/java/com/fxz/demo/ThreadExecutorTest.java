package com.fxz.demo;

import java.util.concurrent.*;

public class ThreadExecutorTest {


    /**
     * 　　1）ArrayBlockingQueue：基于数组的先进先出队列，此队列创建时必须指定大小；
     *
     * 　　2）LinkedBlockingQueue：基于链表的先进先出队列，如果创建时没有指定此队列大小，则默认为Integer.MAX_VALUE；
     *
     * 　　3）synchronousQueue：这个队列比较特殊，它不会保存提交的任务，而是将直接新建一个线程来执行新来的任务。
     *    使用时要求maximumPoolSize为无界(Integer.MAX_VALUE)
     */
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(5,
            10,60, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(10));

    public static void main(String[] args) {
        try {
            for (int i = 1;i < 21;i++) {
                Task task = new Task(i);
                executor.execute(task);
                System.out.println("线程池中线程数目：" + executor.getPoolSize() + "，队列中等待执行的任务数目：" +
                        executor.getQueue().size() + "，已执行完的任务数目：" + executor.getCompletedTaskCount());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

}

/**
 * 从执行结果可以看出，当线程池中线程的数目大于corePoolSize时，便将任务放入任务缓存队列里面，
 * 当任务缓存队列满了之后，便在线程池中创建新的线程。如果上面程序中，将for循环中改成执行25个任务，就会抛出任务拒绝异常了。
 *
 *
 *    Executors.newCachedThreadPool();        //创建一个缓冲池，缓冲池容量大小为Integer.MAX_VALUE
 *    Executors.newSingleThreadExecutor();   //创建容量为1的缓冲池
 *    Executors.newFixedThreadPool(int);    //创建固定容量大小的缓冲池
 *
 *
 */

class Task implements Runnable {
    private int taskNum;

    public Task(int taskNum) {
        this.taskNum = taskNum;
    }

    @Override
    public void run() {
        System.out.println("task" + taskNum + "start");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("task" + taskNum + "end");
    }
}