package com.nowcoder.community;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) //Test方式也使用main中的配置类
//class CommunityApplicationTests {
class CommunityApplicationTests implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override	//注解表示重写父类的方法
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Test
	public void testApplicationContext() {
		System.out.println(this.applicationContext);
//		System.out.print("\n\n");
		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
		System.out.println(alphaDao.select());

		alphaDao = applicationContext.getBean("alphaHibernate", AlphaDao.class);
		System.out.println(alphaDao.select()); 	//这里直接根据别名调用hibernate 这个Bean 并且转换为 AlphaDao的类型
	}

	@Test
	public void testBeanManagement(){
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);

		// 在AlphaService 设置@Scope prototype 每次访问都生成一个新的实例
		//		for(int i = 0; i< 3; i++){
		//			alphaService = applicationContext.getBean(AlphaService.class);
		//			System.out.println(alphaService);
		//		}
	}

	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date())); //格式化一个当前时间的对象
	}
	//以上方式都是主动获取而不是依赖注入

//	//自动注入的方式
//	@Autowired //自动注入的注解
//	private AlphaDao alphaDao;

	@Autowired
	@Qualifier("alphaHibernate")	//指定名称的Bean进行依赖注入
	private AlphaDao alphaDao;

	@Autowired
	private AlphaService alphaService;

	@Autowired
	private SimpleDateFormat simpleDateFormat;

	@Test
	public void testDI(){	//
		System.out.println(alphaDao);
		System.out.println(alphaService);
		System.out.println(simpleDateFormat);
	}

	//依赖注入，也可以放在构造方法、Set方法之前也可以，可以阅读手册
}
