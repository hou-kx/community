package com.nowcoder.community;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Test implements Runnable{
    public static void main(String[] argv) {
        Test b = new Test();
        b.run();
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Value of i = " + i);
        }
    }
}
