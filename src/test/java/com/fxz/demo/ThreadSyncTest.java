package com.fxz.demo;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSyncTest {


}

/**
 * 方式1: 继承Thread类
 */
class ExtendThread extends Thread {

    private int i;

    public static void main(String[] args) {
        //调用Thread类的currentThread()方法获取当前线程

        //创建并启动第一个线程
        new ExtendThread().start();

        //创建并启动第二个线程
        new ExtendThread().start();

    }

    public void run() {
        for(;i < 10;) {
            i++;
            //当通过继承Thread类的方式实现多线程时，可以直接使用this获取当前执行的线程
            System.out.println(this.getName() + " "  + i);
        }
    }
}

/**
 * 方式2: 实现Runnable类
 */
class ImpRunnable implements Runnable {

    private int i;

    @Override
    public  void run() {
      for (;i < 10;) {
          //当线程类实现Runnable接口时，要获取当前线程对象只有通过Thread.currentThread()获取
          System.out.println(Thread.currentThread()+ "_" +i);
          i++;
      }
    }

    public static void main(String[] args) {
        ImpRunnable thread_target = new ImpRunnable();
        //通过new Thread(target,name)的方式创建线程
        new Thread(thread_target,"线程1").start();
        new Thread(thread_target,"线程2").start();
    }

}

/**
 * 方式3: 通过Callable和Future接口创建线程
 */
class ThirdThreadImp {
    public static void main(String[] args) {
        //这里call()方法的重写是采用lambda表达式，没有新建一个Callable接口的实现类
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int i = 0;
                for(;i < 50;i++) {
                    System.out.println(Thread.currentThread().getName() +
                            "  的线程执行体内的循环变量i的值为：" + i);
                }
                //call()方法的返回值
                return i;
            }
        };

        FutureTask<Integer> task1 =  new FutureTask<Integer>(callable);
        FutureTask<Integer> task2 =  new FutureTask<Integer>(callable);

        new Thread(task1,"有返回值的线程1").start();
        new Thread(task2,"有返回值的线程2").start();


        try {
            System.out.println("子线程的返回值：" + task1.get());
            System.out.println("子线程的返回值：" + task2.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("所有线程执行完毕");
    }

}


/**
 * 同步方法
 */
class test01 {
    public static void main(String[] args) throws InterruptedException {
        // 并发：多线程操作同一个资源类,
        Ticket ticket = new Ticket();
        // @FunctionalInterface 函数式接口
        new Thread(() -> {
            saleI(ticket);
        }, "A").start();
        new Thread(() -> {
            saleI(ticket);
        }, "B").start();
        new Thread(() -> {
            saleI(ticket);
        }, "C").start();
    }

    public static void saleI(Ticket ticket) {
        for (int i = 1; i < 20; i++) {
            ticket.sale1();
        }
    }
}


class Ticket {
    //共卖40张票
    private int number = 30;

    /**
     * 同步方法
     */
    // 卖票的方式
    public synchronized void sale() {
        if (number > 0) {
            System.out.println(Thread.currentThread().getName() + "卖出了第" + (number--) + "票,剩余：" + number);
        }
    }

    /**
     * 同步块
     */
    public void sale1() {
        synchronized(this){
            if (number > 0) {
                System.out.println(Thread.currentThread().getName() + "卖出了第" + (number--) + "票,剩余：" + number);
            }
        }
    }
}


/**
 * volatile关键字 -- 保证在每个线程中可见性,不能保证原子性,不能保证线程安全,常用于线程状态标识位
 */
class VolatileTest extends Thread {

    //不加volatile关键字,程序不会正常退出
    boolean flag = false;
    int i = 0;

    public void run() {
        while (!flag) {
            i++;
        }
    }

    public static void main(String[] args) throws Exception {
        VolatileTest vt = new VolatileTest();
        vt.start();
        Thread.sleep(2000);
        vt.flag = true;
        System.out.println("stope" + vt.i);
    }
}



/**
 * 重入锁
 */
class Bank {
    private int account = 100;
    //需要声明这个锁
    private Lock lock = new ReentrantLock();

    public int getAccount() {
        return account;
    }


    public void save(int money) {
        lock.lock();
        try {
            account += money;
        } finally {
            lock.unlock();
        }
    }
}

/**
 * 使用局部变量ThreadLocal,解决变量访问冲突问题,每个线程只访问修改变量的副本,互不影响
 */
class ThreadLocalTest {

    static ThreadLocal<String> localVar = new ThreadLocal<>();

    static void print(String str) {
        //打印当前线程中本地内存中本地变量的值
        System.out.println(str + " :" + localVar.get());
        //清除本地内存中的本地变量
        localVar.remove();
    }

    public static void main(String[] args) {
        Thread t1  = new Thread(new Runnable() {
            @Override
            public void run() {
                //设置线程1中本地变量的值
                localVar.set("localVar1");
                //调用打印方法
                print("thread1");
                //打印本地变量
                System.out.println("after remove : " + localVar.get());
            }
        });

        Thread t2  = new Thread(new Runnable() {
            @Override
            public void run() {
                //设置线程1中本地变量的值
                localVar.set("localVar2");
                //调用打印方法
                print("thread2");
                //打印本地变量
                System.out.println("after remove : " + localVar.get());
            }
        });

        t1.start();
        t2.start();
    }
}




/**
 * 使用原子类保证线程同步 ---原子操作就是指将读取变量值、修改变量值、保存变量值看成一个整体来操作即-这几种行为要么同时完成，要么都不完成
 */
class Bank2 {
    private AtomicInteger account = new AtomicInteger(100);
    public AtomicInteger getAccount() {
        return account;
    }
    public void save(int money) {
        account.addAndGet(money);
        System.out.println("current_Money:" + account.get());
    }
}


class BankRunnable {

    public static void main(String[] args) {
        Bank2 bank2 = new Bank2();
        new Thread(()->{
            saveMoney(bank2);
        }).start();
        new Thread(()->{
            saveMoney(bank2);
        }).start();
    }

    public static void saveMoney(Bank2 bank2) {
        for (int i = 0;i < 20;i++) {
            bank2.save(1);
        }
    }
}