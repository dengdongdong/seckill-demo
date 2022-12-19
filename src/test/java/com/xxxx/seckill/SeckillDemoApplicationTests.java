package com.xxxx.seckill;

import com.sun.org.apache.xerces.internal.dom.PSVIAttrNSImpl;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.sql.Time;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class SeckillDemoApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisScript<Boolean> script;

    @Test
    public void testLock01() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Boolean aBoolean = valueOperations.setIfAbsent("k1", "v1");
        if (aBoolean) {
            valueOperations.set("name", "xxxx");
            Object name = valueOperations.get("name");
            System.out.println("name = " + name);
            //异常
            //    操作结束，删除锁
            valueOperations.decrement("k1");
        } else {
            System.out.println("有现成正在使用，请稍后再试。");
        }
    }

    @Test
    public void testLock02() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Boolean isLock = valueOperations.setIfAbsent("k1", "v1", 5, TimeUnit.SECONDS);
        if (isLock) {
            valueOperations.set("name", "xxxx");
            Object name = valueOperations.get("name");
            System.out.println("name = " + name);
            Integer.parseInt("xxxx");
            redisTemplate.delete("k1");
        } else {
            System.out.println("有线程在使用，请稍后再试。");
        }
    }

    //获取锁-比较锁-删除锁不是原子性操作。
    /*lua脚本可以在redis服务端原子性的执行多个redis命令*/
    /*
    1.可以再redis服务端提前写好lua脚本，然后再Java端调用即可，
        优点：提前配置好脚本命令，后期使用时可以方便调用；降低因系统之间调用带来的带宽消耗
        缺点：修改不方便。
    2、在Java端写好脚本，使用的时候可以调用脚本向redis服务端发命令
        优点：编写方便
        缺点：每次向redis服务器发送lua脚本，造成网络通信时间变长。
    * */
    @Test
    public void lockTest03() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String value = UUID.randomUUID().toString();
        Boolean isLock = valueOperations.setIfAbsent("k1", value, 120, TimeUnit.SECONDS);
        if (isLock) {
            valueOperations.set("name", "xxxxx");
            String name = (String) valueOperations.get("name");
            System.out.println("name = " + name);
            System.out.println(valueOperations.get("k1"));
            Boolean result = (Boolean) redisTemplate.execute(script, Collections.singletonList("k1"), value);
            System.out.println(result);
        }else {
            System.out.println("有线程正在使用，请稍后。");
        }


    }

}
