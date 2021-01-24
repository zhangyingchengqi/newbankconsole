package com.yc.ui;

import static com.yc.util.Encrypt.md5;
import static com.yc.util.Encrypt.sha;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import com.yc.dao.DBHelper;

//测试为什么不用 PreparedStatement的父接口Statement? 防止系统被sql注入攻击.
public class Test_sqlAttack {

	public static void main(String[] args) throws SQLException {
		Scanner sc=new Scanner(System.in);
		DBHelper db=new DBHelper();
		System.out.println("请输入您的用户名:");
		String name=sc.nextLine();
		System.out.println("周围没人的情况下请输入您的密码:");
		String pwd=sc.nextLine();
		pwd=md5(   sha(md5(   pwd )  )  );    //TODO: 静态导入 
		//String sql="select * from bankaccount where bname=? and pwd=?";
		
		Connection con=db.getConnection();
		Statement stmt=con.createStatement();   //  TODO: 非预编译语句对象，不支持  ? 占位符，所以参数值 要拼接到sql语句,这就是问题点
		
		String sql="select * from bankaccount where  pwd='"+pwd+"' and  bname='"+name+"' ";
		                                                              // pwd='+    1' or '1'='1      +'
																	  //  pwd='3e' or '1'='1'
		System.out.println(  sql );
		
		ResultSet rs=stmt.executeQuery(    sql   );    //  TODO: 非预编译语句对象是在执行时加入sql.
		if(  rs.next() ) {
			System.out.println("登录成功");
		}else {
			System.out.println("登录失败，用户名或密码错误");
		}
		
		db.closeAll(con);
		
		
	}

}
