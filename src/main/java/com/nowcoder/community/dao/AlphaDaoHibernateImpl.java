package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

//@Repository //存储注解 只有带有注解的Bean才能扫描装配到容器中
@Repository("alphaHibernate")  //给这Bean 命名一个别名，可以在特别的地方进行指定获取
public class AlphaDaoHibernateImpl implements AlphaDao{

    @Override
    public String select() {
        return "Hibernate";
    }
}
