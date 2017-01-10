/**
 * ---------------------------------------------------------------------------
 * 类名称 ：Param
 * 类描述 ：
 * 创建人 ： king.xue
 * 创建时间： 2017/1/3 13:53
 * 版权拥有：宇龙计算机通信科技.JV团队
 * ---------------------------------------------------------------------------
 */

package com.iih5.smartorm.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public  @interface Param {
    String value();
}
