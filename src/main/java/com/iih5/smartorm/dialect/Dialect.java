package com.iih5.smartorm.dialect;
/*
 * Copyright 2016 xueyi (1581249005@qq.com)
 *
 * The SmartORM Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Dialect {

    /**
     * 构建查找sql
     * @param tableName
     * @param columns
     * @param pKeys
     * @return  sql
     */
    public String forModelFindBy(String tableName, String columns, String[] pKeys);

    /**
     * 构建删除sql
     * @param tableName
     * @param conditions 比如：conditions="userId=? and name=?"
     * @return sql
     */
    public String deleteByCondition(String tableName, String conditions);
    /**
     * 构建保存sql
     * @param tableName
     * @param attrs
     * @param sql
     * @param paras
     */
    public void forModelSave(String tableName, Map<String, Object> attrs, StringBuilder sql, List<Object> paras);

    /**
     * 构建更改sql
     * @param tableName
     * @param conditions 比如：conditions="userId=? and name=?"
     * @param attrs
     * @param modifyFlag
     * @param sql
     */
    public void forModelUpdate(String tableName, String conditions, Map<String, Object> attrs, Set<String> modifyFlag, StringBuilder sql);

}






