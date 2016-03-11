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

import redis.clients.jedis.*;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Redis {
    private static RedisExecutor defaultExecutor = null;
    static {
        defaultExecutor = RedisExecutor.use();
    }
     public static RedisExecutor use(String jedisPool){
        return RedisExecutor.use(jedisPool);
    }

    public static int getNumActivePool() {
        return defaultExecutor.getNumActivePool();
    }

    public static int getNumIdlePool() {
        return defaultExecutor.getNumIdlePool();
    }

    public static int getNumWaitersPool() {
        return defaultExecutor.getNumWaitersPool();
    }
    public static String set(String key, String value) {
        return defaultExecutor.set(key, value);
    }

     public static String set(String key, String value, String nxxx, String expx, long time) {
        return defaultExecutor.set(key, value, nxxx, expx, time);
    }

     public static String set(String key, String value, String nxxx) {
        return defaultExecutor.set(key, value, nxxx);
    }

     public static String get(String key) {
        return defaultExecutor.get(key);
    }

     public static Boolean exists(String key) {
        return defaultExecutor.exists(key);
    }

     public static Long persist(String key) {
        return   defaultExecutor.persist(key);
    }

     public static String type(String key) {
        return  defaultExecutor.type(key);
    }

     public static Long expire(String key, int seconds) {
        return  defaultExecutor.expire(key, seconds);
    }

     public static Long pexpire(String key, long milliseconds) {
        return  defaultExecutor.pexpire(key, milliseconds);
    }

     public static Long expireAt(String key, long unixTime) {
        return  defaultExecutor.expireAt(key, unixTime);
    }

     public static Long pexpireAt(String key, long millisecondsTimestamp) {
        return defaultExecutor.pexpireAt(key,millisecondsTimestamp);
    }

     public static Long ttl(String key) {
        return defaultExecutor.ttl(key);
    }

     public static Long pttl(String key) {
        return defaultExecutor.pttl(key);
    }

     public static Boolean setbit(String key, long offset, boolean value) {
        return defaultExecutor.setbit(key,offset,value);
    }

     public static Boolean setbit(String key, long offset, String value) {
        return defaultExecutor.setbit(key, offset, value);
    }

     public static Boolean getbit(String key, long offset) {
        return defaultExecutor.getbit(key, offset);
    }

     public static Long setrange(String key, long offset, String value) {
        return defaultExecutor.setrange(key, offset, value);
    }

     public static String getrange(String key, long startOffset, long endOffset) {
        return defaultExecutor.getrange(key, startOffset, endOffset);
    }

     public static String getSet(String key, String value) {
        return defaultExecutor.getSet(key,value);
    }

     public static Long setnx(String key, String value) {
        return defaultExecutor.setnx(key,value);
    }

     public static String setex(String key, int seconds, String value) {
        return defaultExecutor.setex(key, seconds, value);
    }

     public static String psetex(String key, long milliseconds, String value) {
        return defaultExecutor.psetex(key,milliseconds,value);
    }

     public static Long decrBy(String key, long integer) {
        return defaultExecutor.decrBy(key, integer);
    }

     public static Long decr(String key) {
        return defaultExecutor.decr(key);
    }

     public static Long incrBy(String key, long integer) {
        return defaultExecutor.incrBy(key, integer);
    }

     public static Double incrByFloat(String key, double value) {
        return defaultExecutor.incrByFloat(key, value);
    }

     public static Long incr(String key) {
        return defaultExecutor.incr(key);
    }

     public static Long append(String key, String value) {
        return defaultExecutor.append(key, value);
    }

     public static String substr(String key, int start, int end) {
        return defaultExecutor.substr(key,start,end);
    }

     public static Long hset(String key, String field, String value) {
        return defaultExecutor.hset(key,field,value);
    }

     public static String hget(String key, String field) {
        return defaultExecutor.hget(key, field);
    }

     public static Long hsetnx(String key, String field, String value) {
        return defaultExecutor.hsetnx(key, field, value);
    }

     public static String hmset(String key, Map<String, String> hash) {
        return defaultExecutor.hmset(key, hash);
    }

     public static List<String> hmget(String key, String... fields) {
        return defaultExecutor.hmget(key, fields);
    }

     public static Long hincrBy(String key, String field, long value) {
        return defaultExecutor.hincrBy(key, field, value);
    }

     public static Double hincrByFloat(String key, String field, double value) {
        return defaultExecutor.hincrByFloat(key, field, value);
    }

     public static Boolean hexists(String key, String field) {
        return defaultExecutor.hexists(key,field);
    }

     public static Long hdel(String key, String... field) {
        return defaultExecutor.hdel(key,field);
    }

     public static Long hlen(String key) {
        return defaultExecutor.hlen(key);
    }

     public static Set<String> hkeys(String key) {
        return defaultExecutor.hkeys(key);
    }

     public static List<String> hvals(String key) {
        return defaultExecutor.hvals(key);
    }

     public static Map<String, String> hgetAll(String key) {
        return defaultExecutor.hgetAll(key);
    }

     public static Long rpush(String key, String... string) {
        return defaultExecutor.rpush(key, string);
    }

     public static Long lpush(String key, String... string) {
        return defaultExecutor.lpush(key, string);
    }

     public static Long llen(String key) {
        return defaultExecutor.llen(key);
    }

     public static List<String> lrange(String key, long start, long end) {
        return defaultExecutor.lrange(key, start, end);
    }

     public static String ltrim(String key, long start, long end) {
        return defaultExecutor.ltrim(key, start, end);
    }

     public static String lindex(String key, long index) {
        return defaultExecutor.lindex(key, index);
    }

     public static String lset(String key, long index, String value) {
        return defaultExecutor.lset(key, index, value);
    }

     public static Long lrem(String key, long count, String value) {
        return defaultExecutor.lrem(key, count, value);
    }

     public static String lpop(String key) {
        return defaultExecutor.lpop(key);
    }

     public static String rpop(String key) {
        return defaultExecutor.rpop(key);
    }

     public static Long sadd(String key, String... member) {
        return defaultExecutor.sadd(key, member);
    }

     public static Set<String> smembers(String key) {
        return defaultExecutor.smembers(key);
    }

     public static Long srem(String key, String... member) {
        return defaultExecutor.srem(key, member);
    }

     public static String spop(String key) {
        return defaultExecutor.spop(key);
    }

     public static Set<String> spop(String key, long count) {
        return defaultExecutor.spop(key, count);
    }

     public static Long scard(String key) {
        return defaultExecutor.scard(key);
    }

     public static Boolean sismember(String key, String member) {
        return defaultExecutor.sismember(key, member);
    }

     public static String srandmember(String key) {
        return defaultExecutor.srandmember(key);
    }

     public static List<String> srandmember(String key, int count) {
        return defaultExecutor.srandmember(key, count);
    }

     public static Long strlen(String key) {
        return defaultExecutor.strlen(key);
    }

     public static Long zadd(String key, double score, String member) {
        return defaultExecutor.zadd(key, score, member);
    }

     public static Long zadd(String key, double score, String member, ZAddParams params) {
        return defaultExecutor.zadd(key, score, member, params);
    }

     public static Long zadd(String key, Map<String, Double> scoreMembers) {
        return defaultExecutor.zadd(key, scoreMembers);
    }

     public static Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params) {
        return defaultExecutor.zadd(key, scoreMembers, params);
    }

     public static Set<String> zrange(String key, long start, long end) {
        return defaultExecutor.zrange(key, start, end);
    }

     public static Long zrem(String key, String... member) {
        return defaultExecutor.zrem(key, member);
    }

     public static Double zincrby(String key, double score, String member) {
        return defaultExecutor.zincrby(key, score, member);
    }

     public static Double zincrby(String key, double score, String member, ZIncrByParams params) {
        return defaultExecutor.zincrby(key, score, member, params);
    }

     public static Long zrank(String key, String member) {
        return defaultExecutor.zrank(key, member);
    }

     public static Long zrevrank(String key, String member) {
        return defaultExecutor.zrevrank(key, member);
    }

     public static Set<String> zrevrange(String key, long start, long end) {
        return defaultExecutor.zrevrange(key, start, end);
    }

     public static Set<Tuple> zrangeWithScores(String key, long start, long end) {
        return defaultExecutor.zrangeWithScores(key, start, end);
    }

     public static Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
        return defaultExecutor.zrevrangeWithScores(key, start, end);
    }

     public static Long zcard(String key) {
        return defaultExecutor.zcard(key);
    }

     public static Double zscore(String key, String member) {
        return defaultExecutor.zscore(key, member);
    }

     public static List<String> sort(String key) {
        return defaultExecutor.sort(key);
    }

     public static List<String> sort(String key, SortingParams sortingParameters) {
        return defaultExecutor.sort(key, sortingParameters);
    }

     public static Long zcount(String key, double min, double max) {
        return defaultExecutor.zcount(key, min, max);
    }

     public static Long zcount(String key, String min, String max) {
        return defaultExecutor.zcount(key, min, max);
    }

     public static Set<String> zrangeByScore(String key, double min, double max) {
        return defaultExecutor.zrangeByScore(key, min, max);
    }

     public static Set<String> zrangeByScore(String key, String min, String max) {
        return defaultExecutor.zrangeByScore(key, min, max);
    }

     public static Set<String> zrevrangeByScore(String key, double max, double min) {
        return defaultExecutor.zrevrangeByScore(key, max, min);
    }

     public static Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        return defaultExecutor.zrangeByScore(key, min, max, offset, count);
    }

     public static Set<String> zrevrangeByScore(String key, String max, String min) {
        return defaultExecutor.zrevrangeByScore(key, max, min);
    }

     public static Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
        return defaultExecutor.zrangeByScore(key, min, max, offset, count);
    }

     public static Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        return defaultExecutor.zrevrangeByScore(key, max, min, offset, count);
    }


     public static Long zremrangeByScore(String key, double start, double end) {
        return defaultExecutor.zremrangeByScore(key, start, end);
    }

     public static Long zremrangeByScore(String key, String start, String end) {
        return defaultExecutor.zremrangeByScore(key, start, end);
    }

     public static Long zlexcount(String key, String min, String max) {
        return defaultExecutor.zlexcount(key, min, max);
    }

     public static Set<String> zrangeByLex(String key, String min, String max) {
        return defaultExecutor.zrangeByLex(key, min, max);
    }

     public static Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
        return defaultExecutor.zrangeByLex(key, min, max, offset, count);
    }

     public static Set<String> zrevrangeByLex(String key, String max, String min) {
        return defaultExecutor.zrevrangeByLex(key, max, min);
    }

     public static Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
        return defaultExecutor.zrevrangeByLex(key, max, min, offset, count);
    }

     public static Long zremrangeByLex(String key, String min, String max) {
        return defaultExecutor.zremrangeByLex(key, min, max);
    }

     public static Long linsert(String key, BinaryClient.LIST_POSITION where, String pivot, String value) {
        return defaultExecutor.linsert(key, where, pivot, value);
    }

     public static Long lpushx(String key, String... string) {
        return defaultExecutor.lpushx(key, string);
    }

     public static Long rpushx(String key, String... string) {
        return defaultExecutor.rpushx(key, string);
    }

     public static List<String> blpop(int timeout, String key) {
        return defaultExecutor.blpop(timeout, key);
    }


     public static List<String> brpop(int timeout, String key) {
        return defaultExecutor.brpop(timeout,key);
    }

     public static Long del(String key) {
        return defaultExecutor.del(key);
    }

     public static String echo(String string) {
        return defaultExecutor.echo(string);
    }

     public static Long move(String key, int dbIndex) {
        return defaultExecutor.move(key, dbIndex);
    }

     public static Long bitcount(String key) {
        return defaultExecutor.bitcount(key);
    }

     public static Long bitcount(String key, long start, long end) {
        return defaultExecutor.bitcount(key, start, end);
    }

     public static Long bitpos(String key, boolean value) {
        return defaultExecutor.bitpos(key, value);
    }

     public static Long bitpos(String key, boolean value, BitPosParams params) {
        return defaultExecutor.bitpos(key, value, params);
    }

     public static Long geoadd(String key, double longitude, double latitude, String member) {
        return defaultExecutor.geoadd(key, longitude, latitude, member);
    }

     public static Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap) {
        return defaultExecutor.geoadd(key, memberCoordinateMap);
    }

     public static Double geodist(String key, String member1, String member2) {
        return defaultExecutor.geodist(key, member1, member2);
    }

     public static Double geodist(String key, String member1, String member2, GeoUnit unit) {
        return defaultExecutor.geodist(key, member1, member2, unit);
    }

     public static List<String> geohash(String key, String... members) {
        return defaultExecutor.geohash(key, members);
    }

     public static List<GeoCoordinate> geopos(String key, String... members) {
        return defaultExecutor.geopos(key, members);
    }

     public static List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit) {
        return defaultExecutor.georadius(key, longitude, latitude, radius, unit);
    }

     public static List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param) {
        return defaultExecutor.georadius(key, longitude, latitude, radius, unit, param);
    }

     public static List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit) {
        return defaultExecutor.georadiusByMember(key, member, radius, unit);
    }

     public static List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit, GeoRadiusParam param) {
        return defaultExecutor.georadiusByMember(key, member, radius, unit, param);
    }

     public static Long del(String... keys) {
        return defaultExecutor.del(keys);
    }

     public static Long exists(String... keys) {
        return defaultExecutor.exists(keys);
    }

     public static List<String> blpop(int timeout, String... keys) {
        return defaultExecutor.blpop(timeout, keys);
    }

     public static List<String> brpop(int timeout, String... keys) {
        return defaultExecutor.brpop(timeout, keys);
    }

     public static List<String> blpop(String... args) {
        return defaultExecutor.blpop(args);
    }

     public static List<String> brpop(String... args) {
        return defaultExecutor.brpop(args);
    }

     public static Set<String> keys(String pattern) {
        return defaultExecutor.keys(pattern);
    }

     public static List<String> mget(String... keys) {
        return defaultExecutor.mget(keys);
    }

     public static String mset(String... keysvalues) {
        return defaultExecutor.mset(keysvalues);
    }

     public static Long msetnx(String... keysvalues) {
        return defaultExecutor.msetnx(keysvalues);
    }

     public static String rename(String oldkey, String newkey) {
        return defaultExecutor.rename(oldkey, newkey);
    }

     public static Long renamenx(String oldkey, String newkey) {
        return defaultExecutor.renamenx(oldkey, newkey);
    }

     public static String rpoplpush(String srckey, String dstkey) {
        return defaultExecutor.rpoplpush(srckey, dstkey);
    }

     public static Set<String> sdiff(String... keys) {
        return defaultExecutor.sdiff(keys);
    }

     public static Long sdiffstore(String dstkey, String... keys) {
        return defaultExecutor.sdiffstore(dstkey, keys);
    }

     public static Set<String> sinter(String... keys) {
        return defaultExecutor.sinter(keys);
    }

     public static Long sinterstore(String dstkey, String... keys) {
        return defaultExecutor.sinterstore(dstkey, keys);
    }

     public static Long smove(String srckey, String dstkey, String member) {
        return defaultExecutor.smove(srckey, dstkey, member);
    }

     public static Long sort(String key, SortingParams sortingParameters, String dstkey) {
        return defaultExecutor.sort(key, dstkey);
    }

     public static Long sort(String key, String dstkey) {
        return defaultExecutor.sort(key, dstkey);
    }

     public static Set<String> sunion(String... keys) {
        return defaultExecutor.sunion(keys);
    }

     public static Long sunionstore(String dstkey, String... keys) {
        return defaultExecutor.sunionstore(dstkey, keys);
    }

     public static String watch(String... keys) {
        return defaultExecutor.watch(keys);
    }

     public static String unwatch() {
        return defaultExecutor.unwatch();
    }

     public static Long zinterstore(String dstkey, String... sets) {
        return defaultExecutor.zinterstore(dstkey, sets);
    }

     public static Long zinterstore(String dstkey, ZParams params, String... sets) {
        return defaultExecutor.zinterstore(dstkey, params, sets);
    }

     public static Long zunionstore(String dstkey, String... sets) {
        return defaultExecutor.zunionstore(dstkey, sets);
    }

     public static Long zunionstore(String dstkey, ZParams params, String... sets) {
        return defaultExecutor.zunionstore(dstkey, params, sets);
    }

     public static String brpoplpush(String source, String destination, int timeout) {
        return defaultExecutor.brpoplpush(source, destination, timeout);
    }

     public static Long publish(String channel, String message) {
        return defaultExecutor.publish(channel, message);
    }

     public static void subscribe(final JedisPubSub jedisPubSub, final String... channels) {
         Thread thread=new Thread(new Runnable() {
             public void run() {
                 while (true) {
                     try {
                         defaultExecutor.subscribe(jedisPubSub, channels);
                     } catch (Exception e) {
                         try {
                             Thread.sleep(1000);
                         } catch (InterruptedException e1) {
                             e1.printStackTrace();
                         }
                     }
                 }
             }
         });
         thread.start();
    }

     public static void psubscribe(final JedisPubSub jedisPubSub, final String... patterns) {

         Thread thread=new Thread(new Runnable() {
             public void run() {
                 while (true) {
                     try {
                         defaultExecutor.psubscribe(jedisPubSub, patterns);
                     } catch (Exception e) {
                         try {
                             Thread.sleep(1200);
                         } catch (InterruptedException e1) {
                             e1.printStackTrace();
                         }
                     }
                 }
             }
         });
         thread.start();
    }

     public static String randomKey() {
        return defaultExecutor.randomKey();
    }

     public static Long bitop(BitOP op, String destKey, String... srcKeys) {
        return defaultExecutor.bitop(op, destKey, srcKeys);
    }


    //-----------------------------二进制----------------------------------------
    public String set(byte[] key, byte[] value) {
        return defaultExecutor.set(key, value);
    }

    public String set(byte[] key, byte[] value, byte[] nxxx, byte[] expx, long time) {
        return defaultExecutor.set(key, value, nxxx, expx, time);
    }

    public byte[] get(byte[] key) {
        return defaultExecutor.get(key);
    }

    public Long exists(byte[]... keys) {
        return defaultExecutor.exists(keys);
    }

    public Boolean exists(byte[] key) {
        return defaultExecutor.exists(key);
    }

    public Long del(byte[]... keys) {
        return defaultExecutor.del(keys);
    }

    public Long del(byte[] key) {
        return defaultExecutor.del(key);
    }

    public Set<byte[]> keys(byte[] pattern) {
        return defaultExecutor.keys(pattern);
    }

    public Long expire(byte[] key, int seconds) {
        return defaultExecutor.expire(key, seconds);
    }

    public Long ttl(byte[] key) {
        return defaultExecutor.ttl(key);
    }

    public byte[] getSet(byte[] key, byte[] value) {
        return defaultExecutor.getSet(key, value);
    }

    public List<byte[]> mget(byte[]... keys) {
        return defaultExecutor.mget(keys);
    }

    public Long append(byte[] key, byte[] value) {
        return defaultExecutor.append(key, value);
    }

    public byte[] substr(byte[] key, int start, int end) {
        return defaultExecutor.substr(key, start, end);
    }

    public Long hset(byte[] key, byte[] field, byte[] value) {
        return defaultExecutor.hset(key, field, value);
    }

    public byte[] hget(byte[] key, byte[] field) {
        return defaultExecutor.hget(key, field);
    }
    public Boolean hexists(byte[] key, byte[] field) {
        return defaultExecutor.hexists(key, field);
    }

    public Long hdel(byte[] key, byte[]... fields) {
        return defaultExecutor.hdel(key, fields);
    }

    public Long hlen(byte[] key) {
        return defaultExecutor.hlen(key);
    }

    public Set<byte[]> hkeys(byte[] key) {
        return defaultExecutor.hkeys(key);
    }

    public List<byte[]> hvals(byte[] key) {
        return defaultExecutor.hvals(key);
    }

    public Map<byte[], byte[]> hgetAll(byte[] key) {
        return defaultExecutor.hgetAll(key);
    }

}
