package com.yulissa.takeawayapp.handler.user;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author yulissa
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("created", new Date(), metaObject);
        this.setFieldValByName("updated", new Date(), metaObject);
        this.setFieldValByName("isEmailCheck", true, metaObject);
        this.setFieldValByName("money", 50000, metaObject);
        this.setFieldValByName("nickName", "无名氏", metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {

        this.setFieldValByName("updated", new Date(), metaObject);
    }
}
