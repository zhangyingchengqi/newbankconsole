package com.yc.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *  �Ե��� ģʽ��ȡdb.properties�ļ��е����ݿ���������.
 *
 */
public class MyProperties extends Properties{
	
	private static final long serialVersionUID = 1L;
	
	//����ģʽ
	private  static MyProperties instance; 

	//���췽��˽�л�
	private MyProperties() {
		//�����ù��췽������ȡ db.properties�ļ�
		InputStream iis=this.getClass().getClassLoader().getResourceAsStream("db.properties");
		try {
			this.load(   iis );
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if( iis!=null) {
				try {
					iis.close();   // close����������ͷ�db.properties�ļ�.
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//TODO:  ֻ���ǵ��̳߳���.    ����ȥѧϰ��  ���߳�    double check    ( synchronized, ...�� 
	public static MyProperties getInstance() {
		if(  instance==null ) {
			instance=new MyProperties();
		}
		return instance;
	}
}
