package com.yc.ui;

import static com.yc.util.Encrypt.md5;
import static com.yc.util.Encrypt.sha;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import com.yc.dao.DBHelper;

//����Ϊʲô���� PreparedStatement�ĸ��ӿ�Statement? ��ֹϵͳ��sqlע�빥��.
public class Test_sqlAttack {

	public static void main(String[] args) throws SQLException {
		Scanner sc=new Scanner(System.in);
		DBHelper db=new DBHelper();
		System.out.println("�����������û���:");
		String name=sc.nextLine();
		System.out.println("��Χû�˵��������������������:");
		String pwd=sc.nextLine();
		pwd=md5(   sha(md5(   pwd )  )  );    //TODO: ��̬���� 
		//String sql="select * from bankaccount where bname=? and pwd=?";
		
		Connection con=db.getConnection();
		Statement stmt=con.createStatement();   //  TODO: ��Ԥ���������󣬲�֧��  ? ռλ�������Բ���ֵ Ҫƴ�ӵ�sql���,����������
		
		String sql="select * from bankaccount where  pwd='"+pwd+"' and  bname='"+name+"' ";
		                                                              // pwd='+    1' or '1'='1      +'
																	  //  pwd='3e' or '1'='1'
		System.out.println(  sql );
		
		ResultSet rs=stmt.executeQuery(    sql   );    //  TODO: ��Ԥ��������������ִ��ʱ����sql.
		if(  rs.next() ) {
			System.out.println("��¼�ɹ�");
		}else {
			System.out.println("��¼ʧ�ܣ��û������������");
		}
		
		db.closeAll(con);
		
		
	}

}
