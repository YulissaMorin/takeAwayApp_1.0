package com.yulissa.takeawayapp.entity.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

@TableName("app_user")
@Data
public class AppUser implements Serializable {
    @TableId
    private Long uid;//假设该项目用户很多,为以后分库分表做准备uid用snowflake生成
    private String phone;//注册手机号
    private String email;//注册邮箱
    private String avatar;//用户头像
    @TableField(fill = FieldFill.INSERT)
    private java.util.Date created;//创建时间
    @TableField(fill = FieldFill.UPDATE)
    private java.util.Date updated;//修改时间
    private String sourceType;//会员来源：1:PC，2：H5，3：Android，4：IOS
    @TableField(fill = FieldFill.INSERT)
    private String nickName;//昵称
    private String name;//真实姓名
    private String qq;//QQ号码
    private Boolean isMobileCheck;//手机是否验证 （0否  1是）
    private Boolean isEmailCheck;//邮箱是否检测（0否  1是）
    private String sex;//性别，1男，0女
    private Integer userLevel;//会员等级
    private Integer points;//积分
    private Integer experienceValue;//经验值
    @TableField(fill = FieldFill.INSERT)
    private Integer money;//账户余额,单位为分
    private java.util.Date birthday;//出生年月日
    private java.util.Date lastLoginTime;//最后登录时间
}
