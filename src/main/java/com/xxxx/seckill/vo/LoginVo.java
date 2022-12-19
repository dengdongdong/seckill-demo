package com.xxxx.seckill.vo;

import com.xxxx.seckill.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * Author: asus
 * Date: 2022/10/22 16:47
 */
@Data
public class LoginVo {
    @NotNull
    @IsMobile()//默认required=true；可以省略不写
    private String mobile;
    @NotNull
    @Length(min = 32)
    private String password;
}
