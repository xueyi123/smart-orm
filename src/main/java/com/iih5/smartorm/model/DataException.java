/**
 * ---------------------------------------------------------------------------
 * 类名称   ：DataException
 * 类描述   ：
 * 创建人   ： xue.yi
 * 创建时间： 2016/10/13 15:54
 * 版权拥有：星电商科技
 * ---------------------------------------------------------------------------
 */
package com.iih5.smartorm.model;

public class DataException extends RuntimeException{
    public DataException(String msg){
        super(msg);
    }
}
