package com.xxxx.seckill.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.vo.RespBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成用户工具类
 * Author: asus
 * Date: 2022/11/23 16:15
 */
public class UserUtils {

    public static void main(String[] args) throws Exception {

        List<User> users = createUser(5000);
        // saveUser(users);
        String urlString = "http://localhost:8080/login/doLogin";
        File file = new File("C:\\Users\\asus\\Downloads\\秒杀项目\\config.txt");
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        for (int i = 0; i < users.size(); i++) {
            Map<String, String> param = new HashMap<>();
            param.put("mobile", users.get(i).getId().toString());
            param.put("password", MD5Util.inpuPassToFromPass("123456"));
            String response = doRequest(urlString, param);
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String userTicket = (String) respBean.getObj();
            // System.out.println("create userTicket:" + userTicket);
            String row = users.get(i).getId() + "," + userTicket;
            System.out.println(row);
            writeToFile(raf, row);
            // System.out.println("write to file:" + users.get(i).getId());
        }
        raf.close();
        System.out.println("over");
    }

    public static void writeToFile(RandomAccessFile raf, String row) throws IOException {
        raf.seek(raf.length());
        raf.write(row.getBytes());
        raf.write("\r\n".getBytes());
    }

    public static void saveUser(List<User> users) throws Exception {
        Connection conn = getConn();
        String sql = "insert into t_user ( id, nickname, slat, password, regist_date, login_count) value (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            preparedStatement.setLong(1, user.getId());
            preparedStatement.setString(2, user.getNickname());
            preparedStatement.setString(3, user.getSlat());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setTimestamp(5, new Timestamp(user.getRegistDate().getTime()));
            preparedStatement.setInt(6, user.getLoginCount());
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
        preparedStatement.clearParameters();
        preparedStatement.close();
        System.out.println("insert to db");
    }


    private static Connection getConn() throws Exception {
        String url = "jdbc:mysql://localhost:3306/seckill?allowMultiQueries=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai";
        String userName = "root";
        String password = "cape";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url, userName, password);
    }

    public static List<User> createUser(int count) {
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
        return users;
    }

    public static String doRequest(String urlPath, Map<String, String> param) throws IOException {
        URL url = null;
        HttpURLConnection conn = null;
        BufferedReader in = null;
        String responseData = "";
        PrintWriter printWriter = null;
        try {
            url = new URL(urlPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);// Set enable output
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Charset", "utf-8");

            OutputStream os = conn.getOutputStream();
            printWriter = new PrintWriter(os);

            // traversing map(param) stitching parameters
            String content = "";
            for (String key : param.keySet()) {
                content += "&";
                content += key;
                content += "=";
                content += param.get(key);
            }

            // System.out.println("the param ergodic result content:" + content);
            printWriter.write(content);
            printWriter.flush();

            int code = conn.getResponseCode();
            if (code == 200) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                String retData = null;
                while ((retData = in.readLine()) != null) {
                    responseData += retData;
                }
            } else {
                responseData = code + "";
            }
            return responseData;
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
            if (in != null) {
                in.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
