package com.fxz.demo;

import org.springframework.util.StopWatch;

public class StopWatchTest {

    public static void main(String[] args) throws InterruptedException {
        StopWatch sw = new StopWatch("test");
        sw.start("task1");
        Thread.sleep(200);
        sw.stop();
        sw.start("task2");
        Thread.sleep(200);
        sw.stop();
        System.out.println(sw.prettyPrint());
    }
}
