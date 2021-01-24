package com.yc.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *  以单例 模式读取db.properties文件中的数据库联接配置.
 *
 */
public class MyProperties extends Properties{
	
	private static final long serialVersionUID = 1L;
	
	//尔汗模式
	private  static MyProperties instance; 

	//构造方法私有化
	private MyProperties() {
		//当调用构造方法，读取 db.properties文件
		InputStream iis=this.getClass().getClassLoader().getResourceAsStream("db.properties");
		try {
			this.load(   iis );
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if( iis!=null) {
				try {
					iis.close();   // close这个流，以释放db.properties文件.
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//TODO:  只考虑单线程场景.    可以去学习下  多线程    double check    ( synchronized, ...） 
	public static MyProperties getInstance() {
		if(  instance==null ) {
			instance=new MyProperties();
		}
		return instance;
	}
}
