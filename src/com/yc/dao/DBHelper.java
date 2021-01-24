package com.yc.dao;    // dao : data access object ���ݿ���ʶ���

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * TODO: �����ݿ�����������sql server
 * 
 * DBHelper: ���ݿ���ʵİ�����
 * 
 * ϵͳ�������õĽ������:
 *    1. ������д�뵽ע���
 *    2. ������д�뵽��������
 *    3. �����Ա��浽�����һ�������ļ�/ �ı�. 
 *               java Properties���Ժܺö�ȡ  .properties
 *                       load(   InputStream��ȡ��.properties );
 *               ���⣺��ζ�ȡ���  db.properties�ļ�. ? ·��. 
 *                   ->  ��·��������
 */
public class DBHelper {
	
	//��̬����jvmһ���ؾͻ�����,����ִֻ��һ��. ����jdbc������ͬһ����Ŀֻ��Ҫ����һ�ε�Ҫ��һ��.
	static {
		try {
			MyProperties mp=MyProperties.getInstance();
			//����һ:    �����������
			Class.forName(   mp.getProperty("driverClass")  );    // û������.
			//TODO:  ������:  ����ע��
			//java.sql.DriverManager.registerDriver(new OracleDriver());   //������
		} catch (Exception e) {
			System.out.println("ϵͳ����Ч����");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	//TODO��ϵͳ���õ����ԵĴ洢����:  1. ע���      2.ϵͳ�Ļ�������     *** 3. properties �ļ�. 
	public Connection getConnection() {
		Connection con=null;
		try {
			MyProperties mp=MyProperties.getInstance();
			//����һ:  ����������
			//con = DriverManager.getConnection(mp.getProperty("url"),mp.getProperty("user"),mp.getProperty("password"));
			//������: ����properties��
			//������������ַ�������ȡ���ӵĻ����� �����еļ�������Ϊ  user, password.
			con=DriverManager.getConnection( mp.getProperty("url")  , mp);
		} catch (SQLException e) {
			System.out.println("�����޷�����");
			e.printStackTrace();
			System.exit(0);
		}
		return con;
	}
	
	//������:���ȡ�����е�����,�е���������. 
	public List<Map<String,String>> doSelect(  String sql, Object... params){
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		Connection con=getConnection();
		//2. ����������
		PreparedStatement pstmt=null;
		// 4. ִ�������󣬻�ȡ�����
		ResultSet rs=null;
		try {
			//������
			pstmt = con.prepareStatement(sql);
			setParams(pstmt, params);   //�޸ĳɵ���  setParams����������ռλ��
			rs = pstmt.executeQuery();   //��ѯ���õ������
			//****����������ȡ������е�����   ����  columName[]
			ResultSetMetaData rsmd=rs.getMetaData();    //ͨ���������ȡ�е�Ԫ��Ϣ
			int columnCount=rsmd.getColumnCount();   //�е�����
			String[] columName=new String[columnCount];
			for( int i=0;i<columnCount;i++) {
				columName[i]=rsmd.getColumnLabel(i+1);
			}
			// 5. ѭ�������
			while( rs.next() ) {
				//ȡ��  rs��һ���еĸ����е�ֵ ������,����һ��Map�������Щֵ ���������ٽ����map�浽list��
				Map<String,String> map=new HashMap<String,String>();
				//����columName�����б����������ȡ�� rs ��ǰ��һ�еĸ��е�ֵ��
				for( String cn:columName ) {
					String value=rs.getString(   cn   );
					map.put(    cn, value);
				}
				list.add(  map );
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			closeAll(   con,pstmt,rs);
		}
		return list;
	}
	
	
	/*
	 * ����Ķ���: ����sqlҪִ�У�Ҫ��ͬʱ�ɹ���Ҫ��ͬʱʧ��
	 * ������һ�����ݿ������С�ĵ�Ԫ. 
	 *    ACID:
	 *       A: ԭ����
	 *       C: һ����
	 *       I: ������  
	 *       D: �־���
	 *  commit
	 *  savepoint p1
	 *  rollback to xxx
	 *  
	 *  ��׼jdbc�еĸ��²���DML����ʽ���������Զ��ύ�������ڶ���sql���Ϊͬһ����ʱ��Ҫ�޸�ԭ�еĴ��룬�ر���ʽ���� �� 
	 *  
	 *  ����Ҫʹ����ʽ������
	 */
	/**
	 * �������?���޸ģ�
	 * @param sqls
	 *            ������Ҫִ�е�sql��伯
	 * @return���ɹ�����true
	 * @throws SQLException 
	 */
	public void doUpdate(List<String> sqls, List<Object[]> params) throws SQLException {
		Connection con = this.getConnection();
		PreparedStatement pstmt=null;
		try {
			con.setAutoCommit(false); //   ***:  �ر��Զ��ύ( �ر���ʽ���� )
			if (sqls != null && sqls.size() > 0) {
				for (int i = 0; i < sqls.size(); i++) {
					 pstmt = con.prepareStatement(sqls.get(i));
					if(  params!=null  && params.size()>0   &&   params.get(i)!=null  ){
						this.setParams(pstmt, params.get(i));
					}
					 pstmt.executeUpdate();
				}
			}
			// ���������ִ�����û�г��ִ���
			con.commit(); // *** �ֶ��ύ�޸�
		} catch (SQLException e) {   //�ܼ�/���ܼ��쳣
			try {
				con.rollback();   //  ***   �Զ��ع�
			} catch (SQLException e1) {
				e1.printStackTrace();
			} // ˵��ִ�й����г�����ô�ͻع�����
			
			e.printStackTrace();
			
			//���ܳԵ��쳣
			throw e;
			
		} finally {
			try {
				con.setAutoCommit(true);    //   *** �ָ��ֳ��� �ָ�Ĭ��ֵ��
			} catch (SQLException e) {
				e.printStackTrace();
			}
			this.closeAll(con, pstmt); // ֻ�в�ѯ���н����������û�н����
		}
	}
	
	
	
	
	
	/**
	 * ���µ����ط���   ��   jdbc�ĸ��¶����������ύ,������Ҫ�ֹ�  commit/rollback. 
	 * @param sql
	 * @param params  :��̬����
	 * @return
	 */
	public  int doUpdate(  String sql,Object... params  ){ //  Object[] params  
		Connection con=getConnection();
	   	//Ԥ�����������
		PreparedStatement pstmt=null;
		int result=-1;
		try {
			pstmt = con.prepareStatement(  sql  );
			setParams(pstmt, params);    //����  setParams
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll(   con,pstmt);   //�ر�
		}
		return result;
	}

	public void setParams(PreparedStatement pstmt, Object... params) throws SQLException {
		//Ԥ�����������е� ռλ��?
		if( params!=null&&params.length>0) {
			for( int i=0;i<params.length;i++) {
				//TODO: ������չ�� ����param�������������ж� ���õ�setXxx()����
				pstmt.setString(  i+1 ,  params[i].toString() );  
			}
		}
	}
	
	public  int doUpdate(  String sql,List<Object> params  ){   // sql�п�����0������?,   paramsֵ ��Ҳ�ɵ����û���������������sql��?��Ӧ
		if(   params==null || params.size()<=0 ) {        // ��paramsΪ�յı߽�����.
			return doUpdate(  sql, new Object[] {} );
		}
		return doUpdate(  sql,  params.toArray())  ;
	}
	
	//closeAll���������أ��ֱ��Ӧ��ֻ�����ӣ�ֻ�����ӣ�������ֻ�����ӣ���䣬�����. 
	public void closeAll(   Connection con) {
		closeAll(  con,   null, null);
	}
	
	public void closeAll(   Connection con, PreparedStatement pstmt) {
		closeAll(  con, pstmt,null);
	}

	//����һ���Բ����쳣����ֹ��Ϊpstmt���������rs,conû�йرգ��Ӷ����µ��ڴ����������.
	public void closeAll(   Connection con,PreparedStatement pstmt, ResultSet rs) {
		if(  pstmt!=null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(  rs!=null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if( con!=null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


}
