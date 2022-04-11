package com.nowcoder.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary    //添加装载优先级 要不然会出错expected single matching bean but found 2: alphaDaoMybatisImpl,alphaDaohibernatelmpl
@Repository
public class AlphaDaoMybatisImpl implements AlphaDao{
    @Override
    public String select() {
        return "MyBatis";
    }
}
