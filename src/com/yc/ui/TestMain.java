package com.yc.ui;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.yc.dao.DBHelper;
import com.yc.util.Encrypt;
import static com.yc.util.Encrypt.md5;
import static com.yc.util.Encrypt.sha;

public class TestMain {
	//获取日志对象
	protected final static  Logger log = Logger.getLogger(   TestMain.class   );   //工厂模式 ->   TestMain.class获取classLoader  

	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);
		DBHelper db=new DBHelper();
		while(  true){
			System.out.println("***************************");
			System.out.println("\t\t中国工商银行灵湖支行工学院分机欢迎您");
			System.out.println("***************************");
			System.out.println("请输入您的用户名:");
			String name=sc.nextLine();
			System.out.println("周围没人的情况下请输入您的密码:");
			String pwd=sc.nextLine();
			
			String sql="select * from bankaccount where bname=? and pwd=?";
			pwd=md5(   sha(md5(   pwd )  )  );    //TODO: 静态导入 
			
			List<Map<String,String>> list=db.doSelect(sql, name,pwd);
			if(  list!=null&&list.size()>0){
				System.out.println("登录成功");
				//TODO:显示后面的操作界面
				log.info("登录成功");
				//log.debug("");
				//log.warn("");
				//log.error("");
				//log.fatal("");
				
				UserUi uu=new UserUi();
				uu.showMain(    list.get(0)     );
				
			}else{
				System.out.println("登录失败,用户名或密码错误");
				log.error( "登录失败,用户名或密码错误");
			}
			
		}

	}

}
