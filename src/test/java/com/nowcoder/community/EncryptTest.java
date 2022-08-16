package com.nowcoder.community;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

//@RunWith(SpringRunner.class)
@SpringBootTest
// @ContextConfiguration(classes = CommunityApplication.class) //Test方式也使用main中的配置类
public class EncryptTest {


    private static final String ALGORITHM_INFO = "PBEWithMD5AndDES";
    private static final String PASSWORD_INFO = "salt@123";

    private static final String dbUsername = "root";
    private static final String dbPassword = "Nihao@123";

    @Test
    public void main() {

        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        //加密所需的salt
        textEncryptor.setPassword(PASSWORD_INFO);
        //要加密的数据（数据库的用户名或密码）
        String username = textEncryptor.encrypt(dbUsername);
        String password = textEncryptor.encrypt(dbPassword);
        System.out.println("username:"+username);
        System.out.println("password:"+password);
    }


    @Test
    public void encryptPwd() {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        //配置文件中配置如下的算法
        standardPBEStringEncryptor.setAlgorithm(ALGORITHM_INFO);
        //配置文件中配置的password
        standardPBEStringEncryptor.setPassword(PASSWORD_INFO);
        //要加密的文本
        String name = standardPBEStringEncryptor.encrypt(dbUsername);
        String password = standardPBEStringEncryptor.encrypt(dbPassword);
        String redisPassword =  standardPBEStringEncryptor.encrypt("123456");
        //将加密的文本写到配置文件中
        System.out.println("name=" + name);
        System.out.println("password=" + password);
        System.out.println("redisPassword=" + redisPassword);

        //要解密的文本
        String name2 = standardPBEStringEncryptor.decrypt(name);
        String password2 = standardPBEStringEncryptor.decrypt(password);
        String redisPassword2 = standardPBEStringEncryptor.decrypt(redisPassword);
        //解密后的文本
        System.out.println("name2=" + name2);
        System.out.println("password2=" + password2);
        System.out.println("redisPassword2=" + redisPassword2);
    }


    @Test
    public void jackSonTest() {
        try {
            System.out.println( new ObjectMapper().writeValueAsString(Arrays.asList("adsad", "asda", "asdasd")));
                    // <String>{"asdas","asdas"}));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void testTest(){
        OtherTest test = new OtherTest();
    }
}
