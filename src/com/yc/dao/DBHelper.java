package com.yc.dao;    // dao : data access object 数据库访问对象

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
 * TODO: 将数据库驱动更换成sql server
 * 
 * DBHelper: 数据库访问的帮助类
 * 
 * 系统属性配置的解决方案:
 *    1. 将属性写入到注册表
 *    2. 将属性写入到环境变量
 *    3. 将属性保存到程序的一个属性文件/ 文本. 
 *               java Properties可以很好读取  .properties
 *                       load(   InputStream读取了.properties );
 *               问题：如何读取这个  db.properties文件. ? 路径. 
 *                   ->  类路径加载器
 */
public class DBHelper {
	
	//静态块在jvm一加载就会运行,而且只执行一次. 这与jdbc驱动在同一个项目只需要加载一次的要求一致.
	static {
		try {
			MyProperties mp=MyProperties.getInstance();
			//方案一:    反射加载驱动
			Class.forName(   mp.getProperty("driverClass")  );    // 没有依赖.
			//TODO:  方案二:  驱动注册
			//java.sql.DriverManager.registerDriver(new OracleDriver());   //有依赖
		} catch (Exception e) {
			System.out.println("系统无有效驱动");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	//TODO：系统中用的属性的存储方案:  1. 注册表      2.系统的环境变量     *** 3. properties 文件. 
	public Connection getConnection() {
		Connection con=null;
		try {
			MyProperties mp=MyProperties.getInstance();
			//方案一:  给三个参数
			//con = DriverManager.getConnection(mp.getProperty("url"),mp.getProperty("user"),mp.getProperty("password"));
			//方案二: 利用properties类
			//如果是下面这种方案来获取联接的话，则 属性中的键名必须为  user, password.
			con=DriverManager.getConnection( mp.getProperty("url")  , mp);
		} catch (SQLException e) {
			System.out.println("数据无法访问");
			e.printStackTrace();
			System.exit(0);
		}
		return con;
	}
	
	//问题是:如何取出表中的列名,列的数据类型. 
	public List<Map<String,String>> doSelect(  String sql, Object... params){
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		Connection con=getConnection();
		//2. 创建语句对象
		PreparedStatement pstmt=null;
		// 4. 执行语句对象，获取结果集
		ResultSet rs=null;
		try {
			//语句对象
			pstmt = con.prepareStatement(sql);
			setParams(pstmt, params);   //修改成调用  setParams方法来设置占位符
			rs = pstmt.executeQuery();   //查询，得到结果集
			//****以下用来获取结果集中的列名   存入  columName[]
			ResultSetMetaData rsmd=rs.getMetaData();    //通过结果集获取列的元信息
			int columnCount=rsmd.getColumnCount();   //列的数量
			String[] columName=new String[columnCount];
			for( int i=0;i<columnCount;i++) {
				columName[i]=rsmd.getColumnLabel(i+1);
			}
			// 5. 循环结果集
			while( rs.next() ) {
				//取出  rs这一行中的各个列的值 及列名,创建一个Map，存好这些值 和列名，再将这个map存到list中
				Map<String,String> map=new HashMap<String,String>();
				//根据columName数组中保存的列名，取出 rs 当前这一行的各列的值，
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
	 * 事务的定义: 多条sql要执行，要不同时成功，要不同时失败
	 * 事务是一次数据库操作最小的单元. 
	 *    ACID:
	 *       A: 原子性
	 *       C: 一致性
	 *       I: 隔离性  
	 *       D: 持久性
	 *  commit
	 *  savepoint p1
	 *  rollback to xxx
	 *  
	 *  标准jdbc中的更新操作DML是隐式事务处理，即自动提交，所以在多条sql语句为同一事务时，要修改原有的代码，关闭隐式事务 。 
	 *  
	 *  所以要使用显式事务处理
	 */
	/**
	 * 带事务带?的修改，
	 * @param sqls
	 *            　　　要执行的sql语句集
	 * @return　成功返回true
	 * @throws SQLException 
	 */
	public void doUpdate(List<String> sqls, List<Object[]> params) throws SQLException {
		Connection con = this.getConnection();
		PreparedStatement pstmt=null;
		try {
			con.setAutoCommit(false); //   ***:  关闭自动提交( 关闭隐式事务 )
			if (sqls != null && sqls.size() > 0) {
				for (int i = 0; i < sqls.size(); i++) {
					 pstmt = con.prepareStatement(sqls.get(i));
					if(  params!=null  && params.size()>0   &&   params.get(i)!=null  ){
						this.setParams(pstmt, params.get(i));
					}
					 pstmt.executeUpdate();
				}
			}
			// 当所有语句执行完后没有出现错误，
			con.commit(); // *** 手动提交修改
		} catch (SQLException e) {   //受检/非受检异常
			try {
				con.rollback();   //  ***   自动回滚
			} catch (SQLException e1) {
				e1.printStackTrace();
			} // 说明执行过程中出错，那么就回滚数据
			
			e.printStackTrace();
			
			//不能吃掉异常
			throw e;
			
		} finally {
			try {
				con.setAutoCommit(true);    //   *** 恢复现场， 恢复默认值。
			} catch (SQLException e) {
				e.printStackTrace();
			}
			this.closeAll(con, pstmt); // 只有查询才有结果集，更新没有结果集
		}
	}
	
	
	
	
	
	/**
	 * 更新的重载方法   ，   jdbc的更新都隐务事务提交,即不需要手工  commit/rollback. 
	 * @param sql
	 * @param params  :动态数组
	 * @return
	 */
	public  int doUpdate(  String sql,Object... params  ){ //  Object[] params  
		Connection con=getConnection();
	   	//预编译的语句对象
		PreparedStatement pstmt=null;
		int result=-1;
		try {
			pstmt = con.prepareStatement(  sql  );
			setParams(pstmt, params);    //调用  setParams
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll(   con,pstmt);   //关闭
		}
		return result;
	}

	public void setParams(PreparedStatement pstmt, Object... params) throws SQLException {
		//预编译语句对象中的 占位符?
		if( params!=null&&params.length>0) {
			for( int i=0;i<params.length;i++) {
				//TODO: 可以扩展成 根据param的数据类型来判断 调用的setXxx()方法
				pstmt.setString(  i+1 ,  params[i].toString() );  
			}
		}
	}
	
	public  int doUpdate(  String sql,List<Object> params  ){   // sql中可能有0个或多个?,   params值 ：也由调用用户传进来，个数与sql中?对应
		if(   params==null || params.size()<=0 ) {        // 当params为空的边界条件.
			return doUpdate(  sql, new Object[] {} );
		}
		return doUpdate(  sql,  params.toArray())  ;
	}
	
	//closeAll的三种重载，分别对应，只关联接，只关联接，语句对象，只关联接，语句，结果集. 
	public void closeAll(   Connection con) {
		closeAll(  con,   null, null);
	}
	
	public void closeAll(   Connection con, PreparedStatement pstmt) {
		closeAll(  con, pstmt,null);
	}

	//不能一次性捕获异常，防止因为pstmt出错，而造成rs,con没有关闭，从而导致的内存溢出的问题.
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
