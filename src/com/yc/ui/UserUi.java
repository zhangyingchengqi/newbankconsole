package com.yc.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.yc.biz.AccountBiz;
import com.yc.dao.DBHelper;

public class UserUi {
	private Scanner sc = new Scanner(System.in);
	private AccountBiz ab = new AccountBiz();

	protected final static Logger log = Logger.getLogger(UserUi.class);

	/*
	 * 显示主界面，操作 account: 当前登录的账户
	 */
	public void showMain(Map<String, String> account) {
		System.out.println("亲, 欢迎您:" + account.get("BNAME"));
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		System.out.println("现在是北京时间:" + sdf.format(d));
		while (true) {
			System.out.println("1. 存款");
			System.out.println("2. 取款");
			System.out.println("3. 转账");
			System.out.println("4. 查询余额");
			System.out.println("5. 查询操作流水");
			System.out.println("6. 退出");
			System.out.println("请输入您的选项:");
			int choice = sc.nextInt();
			sc.nextLine();
			switch (choice) {
			case 1:
				System.out.println("请输入要存的钱数:");
				double money = sc.nextDouble(); // 30.0 回车
				sc.nextLine();
				// 需要的参数: id, money
				// 两步操作: 是一个完整 事务. 修改余额 记录流水
				// 返回最新余额
				boolean r = ab.save(account, money);
				if (r) {
					System.out.println("存款" + money + "成功，余额:" + account.get("BALANCE"));
				} else {
					System.out.println("存款失败");
					log.error("存款" + money + "失败");
				}
				break;
			case 2:
				System.out.println("请输入要取的钱数:");
				money = sc.nextDouble();
				sc.nextLine();
				boolean result = ab.withdraw(account, money);
				if (result) {
					System.out.println(account.get("BNAME") + "取款成功, 余额为:" + account.get("BALANCE"));
				} else {
					System.out.println(account.get("BNAME") + "取款失败");
				}
				break;
			case 3:
				System.out.println("请输入对方账号:");
				String id = sc.nextLine();
				boolean flag = ab.checkAccount(id);
				if (flag == false) {
					System.out.println("查无此账号，请确认后重新操作...");
					System.out.println("按任意键继续....\n\n");
					sc.nextLine();
					break;
				}
				System.out.println("请输入要转账的金额:");
				money = sc.nextDouble();
				sc.nextLine();
				
				flag = ab.transfer(account, id, money);
				if (flag) {
					System.out.println(account.get("BNAME") + "转账成功, 余额为:" + account.get("BALANCE"));
				} else {
					System.out.println(account.get("BNAME") + "转账失败");
				}
				break;
			case 4:   // 查询余额. -> 系统只允许一个用户登录的情况。    
				System.out.println(account.get("BNAME")+",亲,您当前账hu余额为:"+   account.get("BALANCE"));
				System.out.println("按任意键继续....\n\n");
				sc.nextLine();
				break;
			case 5:
				List<Map<String,String>> list= ab.findRecord(account);
				System.out.println("账户"+account.get("ID")+"近期流水记录如下:");
				if(  list!=null&&list.size()>0){
					for(  Map<String,String> data: list){
						System.out.println(   data.get("ID")+"\t"+  data.get("OPTIME")+"\t"+ data.get("NUM")+"\t"+ data.get("OPTYPE") );
					}
				}
				System.out.println("按任意键继续....\n\n");
				sc.nextLine();
				break;
			case 6:
				System.out.println("退出登录...");
				sc.hasNextLine();
				return;
			}
		}

	}
}
