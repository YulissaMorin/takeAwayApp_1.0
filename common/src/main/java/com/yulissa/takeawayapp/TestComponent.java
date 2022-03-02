package com.yulissa.takeawayapp;

import com.yulissa.takeawayapp.aopannotation.MethodTimer;
import org.springframework.stereotype.Component;

/**
 * @author yulissa
 */
@Component
public class TestComponent {
    @MethodTimer
    public void print() {
        System.out.println("测试");
    }
}
