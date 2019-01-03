package hh.szu.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by intellij IDEA
 *
 * @author Raven
 * Date:2018/6/12
 * Time:15:43
 */
public class JedisUtils {
    private static JedisPool pool = null;
    private static Jedis resource = null;

    static {
        //加载配置文件
        InputStream in = JedisUtils.class.getClassLoader().getResourceAsStream("redis.properties");
        Properties prop = new Properties();
        try {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //1、创建池子的配置对象
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        //最大闲置数
        poolConfig.setMaxIdle(Integer.parseInt(prop.get("maxIdle").toString()));
        //最小闲置数
        poolConfig.setMinIdle(Integer.parseInt(prop.get("minIdle").toString()));
        //最大连接数
        poolConfig.setMaxTotal(Integer.parseInt(prop.get("maxTotal").toString()));

        pool = new JedisPool(poolConfig, prop.getProperty("host"), Integer.parseInt(prop.get("port").toString()));
    }

    public static Jedis getJedis(){
        resource = pool.getResource();
        return resource;
    }

    public static void main(String[] args) {
        Jedis redis= JedisUtils.getJedis();
        System.out.println(redis.get("name"));

    }

}
