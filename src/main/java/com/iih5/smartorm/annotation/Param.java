
package com.iih5.smartorm.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public  @interface Param {
    String value();
}
