package com.nowcoder.community;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.net.*;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

@SpringBootTest
public class OtherTest implements Runnable {

    @Value("E:/work/Java/community/resource/uploadFile/pic")
    private String uploadPath;

    public static void main(String[] argv) {
        OtherTest b = new OtherTest();
        b.run();
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Value of i = " + i);
        }
    }

    private List<String> getIpAddress() throws SocketException {
        List<String> list = new LinkedList<>();
        Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
        while (enumeration.hasMoreElements()) {
            NetworkInterface network = (NetworkInterface) enumeration.nextElement();
            if (network.isVirtual() || !network.isUp()) {
                continue;
            } else {
                Enumeration addresses = network.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = (InetAddress) addresses.nextElement();
                    if (address != null && (address instanceof Inet4Address || address instanceof Inet6Address)) {
                        list.add(address.getHostAddress());
                    }
                }
            }
        }
        return list;
    }

    @Test
    public void tsetTest() {
        try {
            System.out.println(getIpAddress());
            System.out.println(InetAddress.getLocalHost().getHostAddress());

            System.out.println(Inet4Address.getLocalHost().getHostAddress());
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void testIpSout() {
        try {
            //用 getLocalHost() 方法创建的InetAddress的对象
            InetAddress address = InetAddress.getLocalHost();
            System.out.println(address.getHostName());//主机名
            System.out.println(address.getCanonicalHostName());//主机别名
            System.out.println(address.getHostAddress());//获取IP地址
            System.out.println("===============");

            //用域名创建 InetAddress对象
            InetAddress address1 = InetAddress.getByName("www.wodexiangce.cn");
            //获取的是该网站的ip地址，如果我们所有的请求都通过nginx的，所以这里获取到的其实是nginx服务器的IP地址
            System.out.println(address1.getHostName());//www.wodexiangce.cn
            System.out.println(address1.getCanonicalHostName());//124.237.121.122
            System.out.println(address1.getHostAddress());//124.237.121.122
            System.out.println("===============");

            //用IP地址创建InetAddress对象
            InetAddress address2 = InetAddress.getByName("220.181.111.188");
            System.out.println(address2.getHostName());//220.181.111.188
            System.out.println(address2.getCanonicalHostName());//220.181.111.188
            System.out.println(address2.getHostAddress());//220.181.111.188
            System.out.println("===============");

            //根据主机名返回其可能的所有InetAddress对象
            InetAddress[] addresses = InetAddress.getAllByName("www.baidu.com");
            for (InetAddress addr : addresses) {
                System.out.println(addr);
                //www.baidu.com/220.181.111.188
                //www.baidu.com/220.181.112.244
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void stringUtilTest(){
        String str = null;
        System.out.println(StringUtils.isBlank(str));
    }

    @Test
    public void dateTest(){
        Date num = new Date(System.currentTimeMillis());

        Date num1 = new Date(System.currentTimeMillis() + 3600 * 12 * 1000);

        System.out.println(num + " \t" + num1);
        System.out.println( new Date(System.currentTimeMillis() + 3600 * 12 * 1000L));
        System.out.println( new Date(System.currentTimeMillis() + 3600 * 12 * 1000));
        System.out.println( new Date(System.currentTimeMillis() + 3600 * 12 * 2000L));
        System.out.println( new Date(System.currentTimeMillis() + 3600 * 12 * 2000));
    }



}
