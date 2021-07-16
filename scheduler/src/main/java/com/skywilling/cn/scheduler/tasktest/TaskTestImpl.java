package com.skywilling.cn.scheduler.tasktest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author han yao
 * @date 2021/6/16 0:15
 */
@Service
public class TaskTestImpl implements TaskTest{
    @Autowired
    private PostHTTP postHTTP;

    @Override
    public void run(){
        System.out.println("==================================");
        System.out.println("TaskTestApp run");
        System.out.println("==================================");
        Thread thread = new Thread(postHTTP);
        thread.start();
    }
}
