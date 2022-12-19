package com.xxxx.seckill.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.vo.RespBean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 生成用户工具类
 *  请求接口有问题
 * Author: asus
 * Date: 2022/11/18 18:06
 */
public class UserUtil {
    private static void createUser(int count) throws Exception {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId(13000000000L + i);
            user.setLoginCount(1);
            user.setNickname("user" + i);
            user.setRegistDate(new Date());
            user.setSlat("1a2b3c4d");
            user.setPassword(MD5Util.inputPassToDBPass("123456", user.getSlat()));


            users.add(user);
        }
        System.out.println("create user");
        //插入数据库
        // Connection conn = getConn();
        // String sql = "insert into t_user ( id, nickname, slat, password, regist_date, login_count) value (?, ?, ?, ?, ?, ?)";
        // PreparedStatement preparedStatement = conn.prepareStatement(sql);
        // for (int i = 0; i < users.size(); i++) {
        //     User user = users.get(i);
        //     preparedStatement.setLong(1, user.getId());
        //     preparedStatement.setString(2, user.getNickname());
        //     preparedStatement.setString(3, user.getSlat());
        //     preparedStatement.setString(4, user.getPassword());
        //     preparedStatement.setTimestamp(5, new Timestamp(user.getRegistDate().getTime()));
        //     preparedStatement.setInt(6, user.getLoginCount());
        //     preparedStatement.addBatch();
        // }
        // preparedStatement.executeBatch();
        // preparedStatement.clearParameters();
        // preparedStatement.close();
        // System.out.println("insert to db");
        // 登录
        String urlString = "http://localhost:8080/login/doLogin";
        File file = new File("C:\\Users\\asus\\Downloads\\秒杀项目\\config.txt");
        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        file.createNewFile();
        raf.seek(0);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestProperty("test-header", "post-header-value");
            co.setRequestMethod("POST");
            // 设置是否向httpUrlConnection输出，由于这个是post请求，参数要放在http正文内，所以须要设为true, 默认状况下是false;
            co.setDoOutput(true);
            // 设置是否从httpUrlConnection读入，默认状况下是true;
            co.setDoInput(true);
            // Post 请求不能使用缓存
            // co.setUseCaches(false);
            co.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            co.connect();
            co.setConnectTimeout(20*1000); //设置连接主机超时（单位：毫秒）
            co.setReadTimeout(20*1000); //设置从主机读取数据超时（单位：毫秒）
            OutputStream out = co.getOutputStream();
            String params = "&mobile=" + user.getId() + "&password =" + MD5Util.inpuPassToFromPass("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) >= 0) {
                bout.write(buff, 0, len);
            }
            inputStream.close();
            bout.flush();
            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String userTicket = (String) respBean.getObj();
            System.out.println("create userTicket:" + user.getId());
            String row = user.getId() + "," + userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file:" + user.getId());
        }
        raf.close();
        System.out.println("over");
    }

    private static Connection getConn() throws Exception {
        String url = "jdbc:mysql://localhost:3306/seckill?allowMultiQueries=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai";
        String userName = "root";
        String password = "cape";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url, userName, password);
    }

    public static void main(String[] args) throws Exception {
        createUser(5000);
    }
}
