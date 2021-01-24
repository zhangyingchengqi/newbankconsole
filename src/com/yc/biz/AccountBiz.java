package com.yc.biz;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.yc.dao.DBHelper;

/*
 * ҵ��㣺 ��һЩ����ҵ����, ����dao�㣬�������ݿ�
 */
public class AccountBiz {

	private DBHelper db = new DBHelper();

	// ȡ��Ľ��Ҫ��
	public boolean withdraw(Map<String, String> account, double money) {
//		if(   money>   Double.parseDouble(  account.get("balance")  )  ){
//			System.out.println("����...,��ȷ�����");
//			return false;
//		}
		//�Ȳ��쳣�����.    �ȼ���ˮ��  �ٸ������.
		String sql1 = "update bankaccount set balance=balance-? where id=?";
		String sql2 = "insert into bankrecord values(  seq_bankrecord_id.nextval,   sysdate,  ?,  ?, ?,   '')";
		List<String> sqls = new ArrayList<String>();
		sqls.add(sql2);
		sqls.add(sql1);
		
		List<Object[]> params = new ArrayList<Object[]>();
		params.add(new Object[] { OpType.ȡ, money, account.get("ID") });
		params.add(new Object[] { money, account.get("ID") });
		
		try {
			db.doUpdate(sqls, params);
			double m = Double.parseDouble(account.get("BALANCE"));
			account.put("BALANCE", m - money + "");
			return true;
		}catch( Exception ex) {
			return false;
		}
	}

	/**
	 * ��Ǯ
	 * 
	 * @param account : ��Ǯ����hu��Ϣ
	 * @param money   : Ҫ���Ǯ��
	 * @return Map: ����Ǯ�����hu��Ϣ
	 */
	public boolean save(Map<String, String> account, double money) {
		String sql1 = "update bankaccount set balance=balance+? where id=?";
		// String sql2="insert into bankrecord "
		// + "values( seq_bankrecord_id.nextval, sysdate, '��', ?, ?, '')"; //��1����� ?2:�˺�
		// TODO: ��ö�������� �̶��ĳ���ֵ
		String sql2 = "insert into bankrecord " + "values(  seq_bankrecord_id.nextval,   sysdate,   ?,  ?, ?,   '')"; // OpType.��;
		// TODO:����һ�������γ����� ��Ǯ����
		// db.doUpdate(sql1, money, account.get("ID"));
		// db.doUpdate(sql2, OpType.��, money,account.get("ID"));
		List<String> sqls = new ArrayList<String>();
		sqls.add(sql1);
		sqls.add(sql2);

		List<Object[]> params = new ArrayList<Object[]>();
		params.add(new Object[] { money, account.get("ID") });
		params.add(new Object[] { OpType.��, money, account.get("ID") });

		try {
			db.doUpdate(sqls, params);
			// �޸�ԭ��balance�����
			double m = Double.parseDouble(account.get("BALANCE")) + money;
			account.put("BALANCE", m + "");
			return true;
		}catch( Exception ex) {
			return false;
		}
	}
	
	//��ѯ�˺��Ƿ����
	public boolean checkAccount(  String id){
		String sql="select * from bankaccount where id=?";       //   id Ҫ����������������
		List<Map<String,String>> list=db.doSelect(sql, id);
		if(   list!=null&&list.size()>0){
			return true;
		}else{
			return false;
		}
	}
	
	//ת��
		public boolean transfer(Map<String, String> account, String id, double money) {
//			Scanner sc=new Scanner(System.in);
//			if (money > Double.parseDouble(account.get("BALANCE"))) {
//				System.out.println("����....");
//				System.out.println("�����������....\n\n");
//				sc.nextLine();
//				return false;
//			}
			//    �Լ�����hu��  money
			//     �Է�����hu ��money
			//     ��¼�Լ�    "ת��"
			//     ��¼�Է�  "ת��"
			String sql1="update bankaccount set balance=balance-? where id=?";
			String sql2="update bankaccount set balance=balance+? where id=?";
			
			// ����    nextval      curval                             nextval,currval
			String sql3="insert into bankrecord values(  seq_bankrecord_id.nextval,   sysdate,  ?,  ?, ?,   seq_bankrecord_brno.nextval)";
			String sql4="insert into bankrecord values(  seq_bankrecord_id.nextval,   sysdate,  ?,  ?, ?,   seq_bankrecord_brno.currval)";
			
			List<String> sqls=new ArrayList<String>();
			sqls.add( sql1  );
			sqls.add( sql2  );
			sqls.add( sql3  );
			sqls.add( sql4 );
			
			List<    Object[]   > params=new ArrayList<Object[]>();
			params.add(  new Object[] { money,  account.get("ID")    }     );
			params.add(  new Object[] { money,  id    }  );
			params.add(  new Object[] { OpType.ת��, money,  account.get("ID")    }  );
			params.add(  new Object[] { OpType.ת��,money,  id   }  );
			
			try {
				db.doUpdate(sqls, params);   
				double m=Double.parseDouble(    account.get("BALANCE")   );
				account.put("BALANCE", m-money   +"");
				return true;
			} catch (Exception e) {
				//e.printStackTrace();
				return false;
			}
		}
		
		//���һ���£�ĳ���û�����ˮ������
		public List<Map<String,String>> findRecord(   Map<String,String> account){
			String sql="select * from view_bankrecord_aweek where baid=?   ";
			List<Map<String,String>> list=   db.doSelect(sql,  account.get("ID")      );
			return list;
		}

}
