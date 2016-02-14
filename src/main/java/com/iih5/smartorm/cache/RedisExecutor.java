package com.iih5.smartorm.cache;/*
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

import com.iih5.smartorm.kit.SpringKit;
import redis.clients.jedis.*;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;

import java.util.*;

public class RedisExecutor {
    private static Map<String, RedisExecutor> map = new HashMap<String, RedisExecutor>();
    private static String defaultJedisPool = null;
    public JedisPool pool = null;
    /**
     * 选择使用数据库（默认选中第一个）
     * @param jedisPool 在spring.xml里配置的jdbc jedisPool
     * @return RedisExecutor
     */
    public static RedisExecutor use(String jedisPool) {
        RedisExecutor executor =map.get(jedisPool);
        if (executor==null){
            executor=new RedisExecutor();
            executor.pool= SpringKit.getJedisPool(jedisPool);
            map.put(jedisPool,executor);
        }
        return executor;
    }
    /**
     * 默认第一个jedisPool
     * @return RedisExecutor
     */
    public static RedisExecutor use() {
        if (defaultJedisPool==null){
            String[] dbs = SpringKit.getApplicationContext().getBeanNamesForType(redis.clients.jedis.JedisPool.class);
            defaultJedisPool = dbs[0];
        }
        RedisExecutor executor =map.get(defaultJedisPool);
        if (executor==null){
            executor =new RedisExecutor();
            executor.pool= SpringKit.getJedisPool(defaultJedisPool);
            map.put(defaultJedisPool,executor);
        }
        return  executor;
    }

    public int getNumActivePool() {
        return pool.getNumActive();
    }

    public int getNumIdlePool() {
      return pool.getNumIdle();
    }

    public int getNumWaitersPool() {
      return pool.getNumWaiters();
    }

    public String set(String key, String value) {
        Jedis jedis=pool.getResource();
        String d=jedis.set(key,value);
        jedis.close();
        return d;
    }
    public String set(String key, String value, String nxxx, String expx, long time) {
        Jedis jedis=pool.getResource();
        String d=jedis.set(key,value,nxxx,expx,time);
        jedis.close();
        return d;
    }

    public String set(String key, String value, String nxxx) {
        Jedis jedis=pool.getResource();
        String d=jedis.set(key,value,nxxx);
        jedis.close();
        return d;
    }

    public String get(String key) {
        Jedis jedis=pool.getResource();
        String d=jedis.get(key);
        jedis.close();
        return d;
    }

    public Boolean exists(String key) {
        Jedis jedis=pool.getResource();
        Boolean d=jedis.exists(key);
        jedis.close();
        return d;
    }

    public Long persist(String key) {
        Jedis jedis=pool.getResource();
        Long d=jedis.persist(key);
        jedis.close();
        return d;
    }

    public String type(String key) {
        Jedis jedis=pool.getResource();
        String d=jedis.type(key);
        jedis.close();
        return d;
    }

    public Long expire(String key, int seconds) {
        Jedis jedis=pool.getResource();
        Long d=jedis.expire(key,seconds);
        jedis.close();
        return d;
    }

    public Long pexpire(String key, long milliseconds) {
        Jedis jedis=pool.getResource();
        Long d=jedis.pexpire(key,milliseconds);
        jedis.close();
        return d;
    }

    public Long expireAt(String key, long unixTime) {
        Jedis jedis=pool.getResource();
        Long d=jedis.expireAt(key,unixTime);
        jedis.close();
        return d;
    }

    public Long pexpireAt(String key, long millisecondsTimestamp) {
        Jedis jedis=pool.getResource();
        Long d=jedis.pexpireAt(key,millisecondsTimestamp);
        jedis.close();
        return d;
    }

    public Long ttl(String key) {
        Jedis jedis=pool.getResource();
        Long d=jedis.ttl(key);
        jedis.close();
        return d;
    }

    public Long pttl(String key) {
        Jedis jedis=pool.getResource();
        Long d=jedis.pttl(key);
        jedis.close();
        return d;
    }

    public Boolean setbit(String key, long offset, boolean value) {
        Jedis jedis=pool.getResource();
        Boolean d=jedis.setbit(key,offset,value);
        jedis.close();
        return d;
    }

    public Boolean setbit(String key, long offset, String value) {
        Jedis jedis=pool.getResource();
        Boolean d=jedis.setbit(key,offset,value);
        jedis.close();
        return d;
    }

    public Boolean getbit(String key, long offset) {
        Jedis jedis=pool.getResource();
        Boolean d=jedis.getbit(key,offset);
        jedis.close();
        return d;
    }

    public Long setrange(String key, long offset, String value) {
        Jedis jedis=pool.getResource();
        Long d=jedis.setrange(key,offset,value);
        jedis.close();
        return d;
    }

    public String getrange(String key, long startOffset, long endOffset) {
        Jedis jedis=pool.getResource();
        String d=jedis.getrange(key,startOffset,endOffset);
        jedis.close();
        return d;
    }

    public String getSet(String key, String value) {
        Jedis jedis=pool.getResource();
        String d=jedis.getSet(key,value);
        jedis.close();
        return d;
    }

    public Long setnx(String key, String value) {
        Jedis jedis=pool.getResource();
        Long d=jedis.setnx(key,value);
        jedis.close();
        return d;
    }

    public String setex(String key, int seconds, String value) {
        Jedis jedis=pool.getResource();
        String d=jedis.setex(key,seconds,value);
        jedis.close();
        return d;
    }

    public String psetex(String key, long milliseconds, String value) {
        Jedis jedis=pool.getResource();
        String d=jedis.psetex(key,milliseconds,value);
        jedis.close();
        return d;
    }

    public Long decrBy(String key, long integer) {
        Jedis jedis=pool.getResource();
        Long d=jedis.decrBy(key,integer);
        jedis.close();
        return d;
    }

    public Long decr(String key) {
        Jedis jedis=pool.getResource();
        Long d=jedis.decr(key);
        jedis.close();
        return d;
    }

    public Long incrBy(String key, long integer) {
        Jedis jedis=pool.getResource();
        Long d=jedis.incrBy(key,integer);
        jedis.close();
        return d;
    }

    public Double incrByFloat(String key, double value) {
        Jedis jedis=pool.getResource();
        Double d=jedis.incrByFloat(key,value);
        jedis.close();
        return d;
    }

    public Long incr(String key) {
        Jedis jedis=pool.getResource();
        Long d=jedis.incr(key);
        jedis.close();
        return d;
    }

    public Long append(String key, String value) {
        Jedis jedis=pool.getResource();
        Long d=jedis.append(key,value);
        jedis.close();
        return d;
    }

    public String substr(String key, int start, int end) {
        Jedis jedis=pool.getResource();
        String d=jedis.substr(key,start,end);
        jedis.close();
        return d;
    }

    public Long hset(String key, String field, String value) {
        Jedis jedis=pool.getResource();
        Long d=jedis.hset(key,field,value);
        jedis.close();
        return d;
    }

    public String hget(String key, String field) {
        Jedis jedis=pool.getResource();
        String d=jedis.hget(key,field);
        jedis.close();
        return d;
    }

    public Long hsetnx(String key, String field, String value) {
        Jedis jedis=pool.getResource();
        Long d=jedis.hsetnx(key,field,value);
        jedis.close();
        return d;
    }

    public String hmset(String key, Map<String, String> hash) {
        Jedis jedis=pool.getResource();
        String d=jedis.hmset(key,hash);
        jedis.close();
        return d;
    }

    public List<String> hmget(String key, String... fields) {
        Jedis jedis=pool.getResource();
        List<String> d=jedis.hmget(key,fields);
        jedis.close();
        return d;
    }

    public Long hincrBy(String key, String field, long value) {
        Jedis jedis=pool.getResource();
        Long d=jedis.hincrBy(key,field,value);
        jedis.close();
        return d;
    }

    public Double hincrByFloat(String key, String field, double value) {
        Jedis jedis=pool.getResource();
        Double d=jedis.hincrByFloat(key,field,value);
        jedis.close();
        return d;
    }

    public Boolean hexists(String key, String field) {
        Jedis jedis=pool.getResource();
        Boolean d=jedis.hexists(key,field);
        jedis.close();
        return d;
    }

    public Long hdel(String key, String... field) {
        Jedis jedis=pool.getResource();
        Long d=jedis.hdel(key,field);
        jedis.close();
        return d;
    }

    public Long hlen(String key) {
        Jedis jedis=pool.getResource();
        Long d=jedis.hlen(key);
        jedis.close();
        return d;
    }

    public Set<String> hkeys(String key) {
        Jedis jedis=pool.getResource();
        Set<String> d=jedis.hkeys(key);
        jedis.close();
        return d;
    }

    public List<String> hvals(String key) {
        Jedis jedis=pool.getResource();
        List<String> d=jedis.hvals(key);
        jedis.close();
        return d;
    }

    public Map<String, String> hgetAll(String key) {
        Jedis jedis=pool.getResource();
        Map<String, String> d=jedis.hgetAll(key);
        jedis.close();
        return d;
    }

    public Long rpush(String key, String... string) {
        Jedis jedis=pool.getResource();
        Long d=jedis.rpush(key,string);
        jedis.close();
        return d;
    }

    public Long lpush(String key, String... string) {
        Jedis jedis=pool.getResource();
        Long d=jedis.lpush(key,string);
        jedis.close();
        return d;
    }

    public Long llen(String key) {
        Jedis jedis=pool.getResource();
        Long d=jedis.llen(key);
        jedis.close();
        return d;
    }

    public List<String> lrange(String key, long start, long end) {
        Jedis jedis=pool.getResource();
        List<String> d=jedis.lrange(key,start,end);
        jedis.close();
        return d;
    }

    public String ltrim(String key, long start, long end) {
        Jedis jedis=pool.getResource();
        String d=jedis.ltrim(key,start,end);
        jedis.close();
        return d;
    }

    public String lindex(String key, long index) {
        Jedis jedis=pool.getResource();
        String d=jedis.lindex(key,index);
        jedis.close();
        return d;
    }

    public String lset(String key, long index, String value) {
        Jedis jedis=pool.getResource();
        String d=jedis.lset(key,index,value);
        jedis.close();
        return d;
    }

    public Long lrem(String key, long count, String value) {
        Jedis jedis=pool.getResource();
        Long d=jedis.lrem(key,count,value);
        jedis.close();
        return d;
    }

    public String lpop(String key) {
        Jedis jedis=pool.getResource();
        String d=jedis.lpop(key);
        jedis.close();
        return d;
    }

    public String rpop(String key) {
        Jedis jedis=pool.getResource();
        String d=jedis.rpop(key);
        jedis.close();
        return d;
    }

    public Long sadd(String key, String... member) {
        Jedis jedis=pool.getResource();
        Long d=jedis.sadd(key,member);
        jedis.close();
        return d;
    }

    public Set<String> smembers(String key) {
        Jedis jedis=pool.getResource();
        Set<String> d=jedis.smembers(key);
        jedis.close();
        return d;
    }

    public Long srem(String key, String... member) {
        Jedis jedis=pool.getResource();
        Long d=jedis.srem(key,member);
        jedis.close();
        return d;
    }

    public String spop(String key) {
        Jedis jedis=pool.getResource();
        String d=jedis.spop(key);
        jedis.close();
        return d;
    }

    public Set<String> spop(String key, long count) {
        Jedis jedis=pool.getResource();
        Set<String> d=jedis.spop(key,count);
        jedis.close();
        return d;
    }

    public Long scard(String key) {
        Jedis jedis=pool.getResource();
        Long d=jedis.scard(key);
        jedis.close();
        return d;
    }

    public Boolean sismember(String key, String member) {
        Jedis jedis=pool.getResource();
        Boolean d=jedis.sismember(key,member);
        jedis.close();
        return d;
    }

    public String srandmember(String key) {
        Jedis jedis=pool.getResource();
        String d=jedis.srandmember(key);
        jedis.close();
        return d;
    }

    public List<String> srandmember(String key, int count) {
        Jedis jedis=pool.getResource();
        List<String> d=jedis.srandmember(key,count);
        jedis.close();
        return d;
    }

    public Long strlen(String key) {
        Jedis jedis=pool.getResource();
        Long d=jedis.strlen(key);
        jedis.close();
        return d;
    }

    public Long zadd(String key, double score, String member) {
        Jedis jedis=pool.getResource();
        Long d=jedis.zadd(key,score,member);
        jedis.close();
        return d;
    }

    public Long zadd(String key, double score, String member, ZAddParams params) {
        Jedis jedis=pool.getResource();
        Long d=jedis.zadd(key,score,member,params);
        jedis.close();
        return d;
    }

    public Long zadd(String key, Map<String, Double> scoreMembers) {
        Jedis jedis=pool.getResource();
        Long d=jedis.zadd(key,scoreMembers);
        jedis.close();
        return d;
    }

    public Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params) {
        Jedis jedis=pool.getResource();
        Long d=jedis.zadd(key,scoreMembers,params);
        jedis.close();
        return d;
    }

    public Set<String> zrange(String key, long start, long end) {
        Jedis jedis=pool.getResource();
        Set<String> d=jedis.zrange(key,start,end);
        jedis.close();
        return d;
    }

    public Long zrem(String key, String... member) {
        Jedis jedis=pool.getResource();
        Long d=jedis.zrem(key,member);
        jedis.close();
        return d;
    }

    public Double zincrby(String key, double score, String member) {
        Jedis jedis=pool.getResource();
        Double d=jedis.zincrby(key,score,member);
        jedis.close();
        return d;
    }

    public Double zincrby(String key, double score, String member, ZIncrByParams params) {
        Jedis jedis=pool.getResource();
        Double d=jedis.zincrby(key,score,member,params);
        jedis.close();
        return d;
    }

    public Long zrank(String key, String member) {
        Jedis jedis=pool.getResource();
        Long d=jedis.zrank(key,member);
        jedis.close();
        return d;
    }

    public Long zrevrank(String key, String member) {
        Jedis jedis=pool.getResource();
        Long d=jedis.zrevrank(key,member);
        jedis.close();
        return d;
    }

    public Set<String> zrevrange(String key, long start, long end) {
        Jedis jedis=pool.getResource();
        Set<String> d=jedis.zrevrange(key,start,end);
        jedis.close();
        return d;
    }

    public Set<Tuple> zrangeWithScores(String key, long start, long end) {
        Jedis jedis=pool.getResource();
        Set<Tuple> d=jedis.zrangeWithScores(key,start,end);
        jedis.close();
        return d;
    }

    public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
        Jedis jedis=pool.getResource();
        Set<Tuple> d=jedis.zrevrangeWithScores(key,start,end);
        jedis.close();
        return d;
    }

    public Long zcard(String key) {
        Jedis jedis=pool.getResource();
        Long d=jedis.zcard(key);
        jedis.close();
        return d;
    }

    public Double zscore(String key, String member) {
        Jedis jedis=pool.getResource();
        Double d=jedis.zscore(key,member);
        jedis.close();
        return d;
    }

    public List<String> sort(String key) {
        Jedis jedis=pool.getResource();
        List<String> d=jedis.sort(key);
        jedis.close();
        return d;
    }

    public List<String> sort(String key, SortingParams sortingParameters) {
        Jedis jedis=pool.getResource();
        List<String> d=jedis.sort(key,sortingParameters);
        jedis.close();
        return d;
    }

    public Long zcount(String key, double min, double max) {
        Jedis jedis=pool.getResource();
        Long d=jedis.zcount(key,min,max);
        jedis.close();
        return d;
    }

    public Long zcount(String key, String min, String max) {
        Jedis jedis=pool.getResource();
        Long d=jedis.zcount(key,min,max);
        jedis.close();
        return d;
    }

    public Set<String> zrangeByScore(String key, double min, double max) {
        Jedis jedis=pool.getResource();
        Set<String> d=jedis.zrangeByScore(key,min,max);
        jedis.close();
        return d;
    }

    public Set<String> zrangeByScore(String key, String min, String max) {
        Jedis jedis=pool.getResource();
        Set<String> d=jedis.zrangeByScore(key,min,max);
        jedis.close();
        return d;
    }

    public Set<String> zrevrangeByScore(String key, double max, double min) {
        Jedis jedis=pool.getResource();
        Set<String> d=jedis.zrevrangeByScore(key,min,max);
        jedis.close();
        return d;
    }

    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        Jedis jedis=pool.getResource();
        Set<String> d=jedis.zrangeByScore(key,min,max);
        jedis.close();
        return d;
    }

    public Set<String> zrevrangeByScore(String key, String max, String min) {
        Jedis jedis=pool.getResource();
        Set<String> d=jedis.zrevrangeByScore(key,min,max);
        jedis.close();
        return d;
    }

    public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
        Jedis jedis=pool.getResource();
        Set<String> d=jedis.zrangeByScore(key,min,max,offset,count);
        jedis.close();
        return d;
    }

    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        Jedis jedis=pool.getResource();
        Set<String> d=jedis.zrevrangeByScore(key,min,max,offset,count);
        jedis.close();
        return d;
    }

    public Long zremrangeByRank(String key, long start, long end) {
        Jedis jedis=pool.getResource();
        Long d=jedis.zremrangeByRank(key,start,end);
        jedis.close();
        return d;
    }

    public Long zremrangeByScore(String key, double start, double end) {
        Jedis jedis=pool.getResource();
        Long d=jedis.zremrangeByScore(key,start,end);
        jedis.close();
        return d;
    }

    public Long zremrangeByScore(String key, String start, String end) {
        Jedis jedis=pool.getResource();
        Long d=jedis.zremrangeByScore(key,start,end);
        jedis.close();
        return d;
    }

    public Long zlexcount(String key, String min, String max) {
        Jedis jedis=pool.getResource();
        Long d=jedis.zlexcount(key,min,max);
        jedis.close();
        return d;
    }

    public Set<String> zrangeByLex(String key, String min, String max) {
        Jedis jedis=pool.getResource();
        Set<String> d=jedis.zrangeByLex(key,min,max);
        jedis.close();
        return d;
    }

    public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
        Jedis jedis=pool.getResource();
        Set<String> d=jedis.zrangeByLex(key,min,max,offset,count);
        jedis.close();
        return d;
    }

    public Set<String> zrevrangeByLex(String key, String max, String min) {
        Jedis jedis=pool.getResource();
        Set<String> d=jedis.zrevrangeByLex(key,min,max);
        jedis.close();
        return d;
    }

    public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
        Jedis jedis=pool.getResource();
        Set<String> d=jedis.zrevrangeByLex(key,min,max,offset,count);
        jedis.close();
        return d;
    }

    public Long zremrangeByLex(String key, String min, String max) {
        Jedis jedis=pool.getResource();
        Long d=jedis.zremrangeByLex(key,min,max);
        jedis.close();
        return d;
    }

    public Long linsert(String key, BinaryClient.LIST_POSITION where, String pivot, String value) {
        Jedis jedis=pool.getResource();
        Long d=jedis.linsert(key,where,pivot,value);
        jedis.close();
        return d;
    }

    public Long lpushx(String key, String... string) {
        Jedis jedis=pool.getResource();
        Long d=jedis.lpushx(key,string);
        jedis.close();
        return d;
    }

    public Long rpushx(String key, String... string) {
        Jedis jedis=pool.getResource();
        Long d=jedis.rpushx(key,string);
        jedis.close();
        return d;
    }

    public List<String> blpop(int timeout, String key) {
        Jedis jedis=pool.getResource();
        List<String> d=jedis.blpop(timeout,key);
        jedis.close();
        return d;
    }


    public List<String> brpop(int timeout, String key) {
        Jedis jedis=pool.getResource();
        List<String> d=jedis.brpop(timeout,key);
        jedis.close();
        return d;
    }

    public Long del(String key) {
        Jedis jedis=pool.getResource();
        Long d=jedis.del(key);
        jedis.close();
        return d;
    }

    public String echo(String string) {
        Jedis jedis=pool.getResource();
        String d=jedis.echo(string);
        jedis.close();
        return d;
    }

    public Long move(String key, int dbIndex) {
        Jedis jedis=pool.getResource();
        Long d=jedis.move(key,dbIndex);
        jedis.close();
        return d;
    }

    public Long bitcount(String key) {
        Jedis jedis=pool.getResource();
        Long d=jedis.bitcount(key);
        jedis.close();
        return d;
    }

    public Long bitcount(String key, long start, long end) {
        Jedis jedis=pool.getResource();
        Long d=jedis.bitcount(key,start,end);
        jedis.close();
        return d;
    }

    public Long bitpos(String key, boolean value) {
        Jedis jedis=pool.getResource();
        Long d=jedis.bitpos(key,value);
        jedis.close();
        return d;
    }

    public Long bitpos(String key, boolean value, BitPosParams params) {
        Jedis jedis=pool.getResource();
        Long d=jedis.bitpos(key,value,params);
        jedis.close();
        return d;
    }

    public Long geoadd(String key, double longitude, double latitude, String member) {
        Jedis jedis=pool.getResource();
        Long d=jedis.geoadd(key,longitude,latitude,member);
        jedis.close();
        return d;
    }

    public Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap) {
        Jedis jedis=pool.getResource();
        Long d=jedis.geoadd(key,memberCoordinateMap);
        jedis.close();
        return d;
    }

    public Double geodist(String key, String member1, String member2) {
        Jedis jedis=pool.getResource();
        Double d=jedis.geodist(key,member1,member2);
        jedis.close();
        return d;
    }

    public Double geodist(String key, String member1, String member2, GeoUnit unit) {
        Jedis jedis=pool.getResource();
        Double d=jedis.geodist(key,member1,member2,unit);
        jedis.close();
        return d;
    }

    public List<String> geohash(String key, String... members) {
        Jedis jedis=pool.getResource();
        List<String> d=jedis.geohash(key,members);
        jedis.close();
        return d;
    }

    public List<GeoCoordinate> geopos(String key, String... members) {
        Jedis jedis=pool.getResource();
        List<GeoCoordinate> d=jedis.geopos(key,members);
        jedis.close();
        return d;
    }

    public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit) {
        Jedis jedis=pool.getResource();
        List<GeoRadiusResponse> d=jedis.georadius(key,longitude,latitude,radius,unit);
        jedis.close();
        return d;
    }

    public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param) {
        Jedis jedis=pool.getResource();
        List<GeoRadiusResponse> d=jedis.georadius(key,longitude,latitude,radius,unit,param);
        jedis.close();
        return d;
    }

    public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit) {
        Jedis jedis=pool.getResource();
        List<GeoRadiusResponse> d=jedis.georadiusByMember(key,member,radius,unit);
        jedis.close();
        return d;
    }

    public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit, GeoRadiusParam param) {
        Jedis jedis=pool.getResource();
        List<GeoRadiusResponse> d=jedis.georadiusByMember(key,member,radius,unit,param);
        jedis.close();
        return d;
    }

    public Long del(String... keys) {
        Jedis jedis=pool.getResource();
        Long d=jedis.del(keys);
        jedis.close();
        return d;
    }

    public Long exists(String... keys) {
        Jedis jedis=pool.getResource();
        Long d=jedis.exists(keys);
        jedis.close();
        return d;
    }

    public List<String> blpop(int timeout, String... keys) {
        Jedis jedis=pool.getResource();
        List<String> d=jedis.blpop(timeout,keys);
        jedis.close();
        return d;
    }

    public List<String> brpop(int timeout, String... keys) {
        Jedis jedis=pool.getResource();
        List<String> d=jedis.brpop(timeout,keys);
        jedis.close();
        return d;
    }

    public List<String> blpop(String... args) {
        Jedis jedis=pool.getResource();
        List<String> d=jedis.blpop(args);
        jedis.close();
        return d;
    }

    public List<String> brpop(String... args) {
        Jedis jedis=pool.getResource();
        List<String> d=jedis.brpop(args);
        jedis.close();
        return d;
    }

    public Set<String> keys(String pattern) {
        Jedis jedis=pool.getResource();
        Set<String> d=jedis.keys(pattern);
        jedis.close();
        return d;
    }

    public List<String> mget(String... keys) {
        Jedis jedis=pool.getResource();
        List<String> d=jedis.mget(keys);
        jedis.close();
        return d;
    }

    public String mset(String... keysvalues) {
        Jedis jedis=pool.getResource();
        String d=jedis.mset(keysvalues);
        jedis.close();
        return d;
    }

    public Long msetnx(String... keysvalues) {
        Jedis jedis=pool.getResource();
        Long d=jedis.msetnx(keysvalues);
        jedis.close();
        return d;
    }

    public String rename(String oldkey, String newkey) {
        Jedis jedis=pool.getResource();
        String d=jedis.rename(oldkey,newkey);
        jedis.close();
        return d;
    }

    public Long renamenx(String oldkey, String newkey) {
        Jedis jedis=pool.getResource();
        Long d=jedis.renamenx(oldkey,newkey);
        jedis.close();
        return d;
    }

    public String rpoplpush(String srckey, String dstkey) {
        Jedis jedis=pool.getResource();
        String d=jedis.rpoplpush(srckey,dstkey);
        jedis.close();
        return d;
    }

    public Set<String> sdiff(String... keys) {
        Jedis jedis=pool.getResource();
        Set<String>  d=jedis.sdiff(keys);
        jedis.close();
        return d;
    }

    public Long sdiffstore(String dstkey, String... keys) {
        Jedis jedis=pool.getResource();
        Long d=jedis.sdiffstore(dstkey,keys);
        jedis.close();
        return d;
    }

    public Set<String> sinter(String... keys) {
        Jedis jedis=pool.getResource();
        Set<String> d=jedis.sinter(keys);
        jedis.close();
        return d;
    }

    public Long sinterstore(String dstkey, String... keys) {
        Jedis jedis=pool.getResource();
        Long d=jedis.sinterstore(dstkey,keys);
        jedis.close();
        return d;
    }

    public Long smove(String srckey, String dstkey, String member) {
        Jedis jedis=pool.getResource();
        Long d=jedis.smove(dstkey,dstkey,member);
        jedis.close();
        return d;
    }

    public Long sort(String key, SortingParams sortingParameters, String dstkey) {
        Jedis jedis=pool.getResource();
        Long d=jedis.sort(key,sortingParameters,dstkey);
        jedis.close();
        return d;
    }

    public Long sort(String key, String dstkey) {
        Jedis jedis=pool.getResource();
        Long d=jedis.sort(key,dstkey);
        jedis.close();
        return d;
    }

    public Set<String> sunion(String... keys) {
        Jedis jedis=pool.getResource();
        Set<String> d=jedis.sunion(keys);
        jedis.close();
        return d;
    }

    public Long sunionstore(String dstkey, String... keys) {
        Jedis jedis=pool.getResource();
        Long d=jedis.sunionstore(dstkey,keys);
        jedis.close();
        return d;
    }

    public String watch(String... keys) {
        Jedis jedis=pool.getResource();
        String d=jedis.watch(keys);
        jedis.close();
        return d;
    }

    public String unwatch() {
        Jedis jedis=pool.getResource();
        String d=jedis.unwatch();
        jedis.close();
        return d;
    }

    public Long zinterstore(String dstkey, String... sets) {
        Jedis jedis=pool.getResource();
        Long d=jedis.zinterstore(dstkey, sets);
        jedis.close();
        return d;
    }

    public Long zinterstore(String dstkey, ZParams params, String... sets) {
        Jedis jedis=pool.getResource();
        Long d=jedis.zinterstore(dstkey, params, sets);
        jedis.close();
        return d;
    }

    public Long zunionstore(String dstkey, String... sets) {
        Jedis jedis=pool.getResource();
        Long d=jedis.zunionstore(dstkey, sets);
        jedis.close();
        return d;
    }

    public Long zunionstore(String dstkey, ZParams params, String... sets) {
        Jedis jedis=pool.getResource();
        Long d=jedis.zunionstore(dstkey,params, sets);
        jedis.close();
        return d;
    }

    public String brpoplpush(String source, String destination, int timeout) {
        Jedis jedis=pool.getResource();
        String d=jedis.brpoplpush(source, destination, timeout);
        jedis.close();
        return d;
    }

    public Long publish(String channel, String message) {
        Jedis jedis=pool.getResource();
        Long d=jedis.publish(channel, message);
        jedis.close();
        return d;
    }

    public void subscribe(JedisPubSub jedisPubSub, String... channels) {
        Jedis jedis=pool.getResource();
        jedis.subscribe(jedisPubSub, channels);
        jedis.close();
        return ;
    }

    public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
        Jedis jedis=pool.getResource();
        jedis.psubscribe(jedisPubSub, patterns);
        jedis.close();
        return ;
    }

    public String randomKey() {
        Jedis jedis=pool.getResource();
        String d=jedis.randomKey();
        jedis.close();
        return d;
    }

    public Long bitop(BitOP op, String destKey, String... srcKeys) {
        Jedis jedis=pool.getResource();
        Long d=jedis.bitop(op, destKey, srcKeys);
        jedis.close();
        return d;
    }


}
