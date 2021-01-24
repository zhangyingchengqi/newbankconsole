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
	//��ȡ��־����
	protected final static  Logger log = Logger.getLogger(   TestMain.class   );   //����ģʽ ->   TestMain.class��ȡclassLoader  

	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);
		DBHelper db=new DBHelper();
		while(  true){
			System.out.println("***************************");
			System.out.println("\t\t�й������������֧�й�ѧԺ�ֻ���ӭ��");
			System.out.println("***************************");
			System.out.println("�����������û���:");
			String name=sc.nextLine();
			System.out.println("��Χû�˵��������������������:");
			String pwd=sc.nextLine();
			
			String sql="select * from bankaccount where bname=? and pwd=?";
			pwd=md5(   sha(md5(   pwd )  )  );    //TODO: ��̬���� 
			
			List<Map<String,String>> list=db.doSelect(sql, name,pwd);
			if(  list!=null&&list.size()>0){
				System.out.println("��¼�ɹ�");
				//TODO:��ʾ����Ĳ�������
				log.info("��¼�ɹ�");
				//log.debug("");
				//log.warn("");
				//log.error("");
				//log.fatal("");
				
				UserUi uu=new UserUi();
				uu.showMain(    list.get(0)     );
				
			}else{
				System.out.println("��¼ʧ��,�û������������");
				log.error( "��¼ʧ��,�û������������");
			}
			
		}

	}

}
