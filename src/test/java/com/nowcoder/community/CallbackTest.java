package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) //Test方式也使用main中的配置类
public class CallbackTest {
    @Test
    public void callbackTest(){
        Student student = new Quincy();
        Teacher teacher = new Teacher(student);
        teacher.askQuestion();
    }
}

interface callbackTeacher {
    void tellAnswer(int answer);
}

class Teacher implements callbackTeacher{

    private Student student;

    public Teacher(Student student){
        this.student = student;
    }

    public void askQuestion(){
        student.resolveQuestion(this);
    }

    @Override
    public void tellAnswer(int answer) {
        System.out.println("你的答案是"+answer);
    }
}

interface Student{
    void resolveQuestion(callbackTeacher callback);
}

class Quincy implements Student{

    @Override
    public void resolveQuestion(callbackTeacher callback) {

        // 模拟解决问题
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ignored) {

        }

        // 回调，告诉老师作业写了多久
        callback.tellAnswer(3);
    }
}
