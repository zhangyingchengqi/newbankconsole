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
	 * ��ʾ�����棬���� account: ��ǰ��¼���˻�
	 */
	public void showMain(Map<String, String> account) {
		System.out.println("��, ��ӭ��:" + account.get("BNAME"));
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��MM��dd�� HH:mm");
		System.out.println("�����Ǳ���ʱ��:" + sdf.format(d));
		while (true) {
			System.out.println("1. ���");
			System.out.println("2. ȡ��");
			System.out.println("3. ת��");
			System.out.println("4. ��ѯ���");
			System.out.println("5. ��ѯ������ˮ");
			System.out.println("6. �˳�");
			System.out.println("����������ѡ��:");
			int choice = sc.nextInt();
			sc.nextLine();
			switch (choice) {
			case 1:
				System.out.println("������Ҫ���Ǯ��:");
				double money = sc.nextDouble(); // 30.0 �س�
				sc.nextLine();
				// ��Ҫ�Ĳ���: id, money
				// ��������: ��һ������ ����. �޸���� ��¼��ˮ
				// �����������
				boolean r = ab.save(account, money);
				if (r) {
					System.out.println("���" + money + "�ɹ������:" + account.get("BALANCE"));
				} else {
					System.out.println("���ʧ��");
					log.error("���" + money + "ʧ��");
				}
				break;
			case 2:
				System.out.println("������Ҫȡ��Ǯ��:");
				money = sc.nextDouble();
				sc.nextLine();
				boolean result = ab.withdraw(account, money);
				if (result) {
					System.out.println(account.get("BNAME") + "ȡ��ɹ�, ���Ϊ:" + account.get("BALANCE"));
				} else {
					System.out.println(account.get("BNAME") + "ȡ��ʧ��");
				}
				break;
			case 3:
				System.out.println("������Է��˺�:");
				String id = sc.nextLine();
				boolean flag = ab.checkAccount(id);
				if (flag == false) {
					System.out.println("���޴��˺ţ���ȷ�Ϻ����²���...");
					System.out.println("�����������....\n\n");
					sc.nextLine();
					break;
				}
				System.out.println("������Ҫת�˵Ľ��:");
				money = sc.nextDouble();
				sc.nextLine();
				
				flag = ab.transfer(account, id, money);
				if (flag) {
					System.out.println(account.get("BNAME") + "ת�˳ɹ�, ���Ϊ:" + account.get("BALANCE"));
				} else {
					System.out.println(account.get("BNAME") + "ת��ʧ��");
				}
				break;
			case 4:   // ��ѯ���. -> ϵͳֻ����һ���û���¼�������    
				System.out.println(account.get("BNAME")+",��,����ǰ��hu���Ϊ:"+   account.get("BALANCE"));
				System.out.println("�����������....\n\n");
				sc.nextLine();
				break;
			case 5:
				List<Map<String,String>> list= ab.findRecord(account);
				System.out.println("�˻�"+account.get("ID")+"������ˮ��¼����:");
				if(  list!=null&&list.size()>0){
					for(  Map<String,String> data: list){
						System.out.println(   data.get("ID")+"\t"+  data.get("OPTIME")+"\t"+ data.get("NUM")+"\t"+ data.get("OPTYPE") );
					}
				}
				System.out.println("�����������....\n\n");
				sc.nextLine();
				break;
			case 6:
				System.out.println("�˳���¼...");
				sc.hasNextLine();
				return;
			}
		}

	}
}
