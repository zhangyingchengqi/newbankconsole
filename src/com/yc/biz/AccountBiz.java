package com.yc.biz;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.yc.dao.DBHelper;

/*
 * 业务层： 做一些复杂业务处理, 访问dao层，操作数据库
 */
public class AccountBiz {

	private DBHelper db = new DBHelper();

	// 取款的金额要够
	public boolean withdraw(Map<String, String> account, double money) {
//		if(   money>   Double.parseDouble(  account.get("balance")  )  ){
//			System.out.println("余额不足...,请确认余额");
//			return false;
//		}
		//先测异常的情况.    先记流水，  再更新余额.
		String sql1 = "update bankaccount set balance=balance-? where id=?";
		String sql2 = "insert into bankrecord values(  seq_bankrecord_id.nextval,   sysdate,  ?,  ?, ?,   '')";
		List<String> sqls = new ArrayList<String>();
		sqls.add(sql2);
		sqls.add(sql1);
		
		List<Object[]> params = new ArrayList<Object[]>();
		params.add(new Object[] { OpType.取, money, account.get("ID") });
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
	 * 存钱
	 * 
	 * @param account : 存钱的账hu信息
	 * @param money   : 要存的钱数
	 * @return Map: 存完钱后的账hu信息
	 */
	public boolean save(Map<String, String> account, double money) {
		String sql1 = "update bankaccount set balance=balance+? where id=?";
		// String sql2="insert into bankrecord "
		// + "values( seq_bankrecord_id.nextval, sysdate, '存', ?, ?, '')"; //？1：金额 ?2:账号
		// TODO: 用枚举来代替 固定的常量值
		String sql2 = "insert into bankrecord " + "values(  seq_bankrecord_id.nextval,   sysdate,   ?,  ?, ?,   '')"; // OpType.存;
		// TODO:方案一：不能形成完整 存钱事务
		// db.doUpdate(sql1, money, account.get("ID"));
		// db.doUpdate(sql2, OpType.存, money,account.get("ID"));
		List<String> sqls = new ArrayList<String>();
		sqls.add(sql1);
		sqls.add(sql2);

		List<Object[]> params = new ArrayList<Object[]>();
		params.add(new Object[] { money, account.get("ID") });
		params.add(new Object[] { OpType.存, money, account.get("ID") });

		try {
			db.doUpdate(sqls, params);
			// 修改原来balance的余额
			double m = Double.parseDouble(account.get("BALANCE")) + money;
			account.put("BALANCE", m + "");
			return true;
		}catch( Exception ex) {
			return false;
		}
	}
	
	//查询账号是否存在
	public boolean checkAccount(  String id){
		String sql="select * from bankaccount where id=?";       //   id 要做索引，主键索引
		List<Map<String,String>> list=db.doSelect(sql, id);
		if(   list!=null&&list.size()>0){
			return true;
		}else{
			return false;
		}
	}
	
	//转账
		public boolean transfer(Map<String, String> account, String id, double money) {
//			Scanner sc=new Scanner(System.in);
//			if (money > Double.parseDouble(account.get("BALANCE"))) {
//				System.out.println("余额不够....");
//				System.out.println("按任意键继续....\n\n");
//				sc.nextLine();
//				return false;
//			}
			//    自己的账hu减  money
			//     对方的账hu 加money
			//     记录自己    "转出"
			//     记录对方  "转入"
			String sql1="update bankaccount set balance=balance-? where id=?";
			String sql2="update bankaccount set balance=balance+? where id=?";
			
			// 序列    nextval      curval                             nextval,currval
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
			params.add(  new Object[] { OpType.转出, money,  account.get("ID")    }  );
			params.add(  new Object[] { OpType.转入,money,  id   }  );
			
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
		
		//最近一个月，某个用户的流水，排序
		public List<Map<String,String>> findRecord(   Map<String,String> account){
			String sql="select * from view_bankrecord_aweek where baid=?   ";
			List<Map<String,String>> list=   db.doSelect(sql,  account.get("ID")      );
			return list;
		}

}
