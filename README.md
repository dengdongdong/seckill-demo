# seckill-demo
电商秒杀项目：springboot+redis+rabbitMQ+异常处理+压测
```java
class aaa{
    public static void main(){
    
    }
}
```
```text
这是一个text
```
####分布式锁：用lua脚本确保原子性操作。
##安全优化
###（1）接口防刷
    每个时间允许一定数量的请求。
###（2）接口

###接口限流算法-->保护系统
    （1）令牌
    （2）计数器
    （3）漏洞