package com.mobile.future;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws SQLException {
        //第一步：加载MySQL的JDBC的驱动
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //取得连接的url,能访问MySQL数据库的用户名,密码；studentinfo：数据库名
        String url = "jdbc:mysql://localhost:3307/sq_wismall";
        String username = "root";
        String password = "123456";

        //第二步：创建与MySQL数据库的连接类的实例
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Statement sql_statement = con.createStatement();
        System.out.println("asdfasdf");
    }




}
