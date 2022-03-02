package com.yulissa.takeawayapp.entity.useroauth;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author yulissa
 * @since 2022-01-29
 */
@TableName("user_security_info")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserSecurityInfo对象", description="")
public class UserSecurityInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long uid;

    @ApiModelProperty(value = "qq邮箱作登录名")
    private String username;

    private String password;

    private String authorities;

    private Boolean enabled;

    private Date created;

    private Date updated;


}
