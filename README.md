1. 项目分层: 
    界面 ->  service业务层  -> dao层( DBHelper) -> database
2. DBHelper封装 
    static 静态块加载驱动 
	关闭
    更新:   动态数组. 类型... 数组名
    
	事务处理:   ACID
	try{
	    con.setAutoCommit(false);
		多条SQL操作
		con.commit();
	}catch( Exception ex){
		con.rollback();  
	}finally{
		关闭
	}
	
	查询: List<Map<String,String>>
	
3. 属性文件: 
     1) 注册表       ***
	 2) 环境变量
	 3) 系统的属性文件    *****
	 4) 分布式的配置中心
	 
	 Properties. 单例
	 
	 
4. 加密: 
    MD5, SHA
	指纹   ->    去重
	彩虹表
	加盐
5. 枚举  enum :   系统中出现的一些常量值 ,
6. 日志:
   1. 级别: level
        重要性    ALL< DEBUG < INFO < WARN < ERROR < FATAL
	2. 方向: appender
      ConsoleAppender
      FileAppender
      DailyRollingFileAppender
	  RollingFileAppender
        保存到哪里    
           Target: System.out.println()           
	3. 格式:  layout
     PatternLayout 
     %d{yyyy-MM-dd HH:mm:ss}  %m%n
                                %n  -> \n
      %d{yyyy-MM-dd HH:mm:ss}   %m  -> message  信息
      
      
   %m 输出代码中指定的消息
　　%p 输出优先级，即DEBUG，INFO，WARN，ERROR，FATAL 
　　%r 输出自应用启动到输出该log信息耗费的毫秒数 
　　%c 输出所属的类目，通常就是所在类的全名 
　　%t 输出产生该日志事件的线程名 
　　%n 输出一个回车换行符，Windows平台为“rn”，Unix平台为“n” 
　　%d 输出日志时间点的日期或时间，默认格式为ISO8601，也可以在其后指定格式，比如：%d{yyyy MMM dd HH:mm:ss,SSS}，输出类似：2002年10月18日 22：10：28，921 
　　%l 输出日志事件的发生位置，包括类目名、发生的线程，以及在代码中的行数。
	
    4. 日志对象	
	protected final static  Logger log = Logger.getLogger(   TestMain.class   ); 
	log.info()   debug()  error()  warn()  fatal()
	
7. sql注入攻击: 
     jdbc的代码中用了Statement对象结合sql语句拼装. 
	 
	 1) 了解你的系统:   先让你的程序 出错,读报错信息. 
	      防: 不要让异常信息全部显示前台. 应该用日志. 
	 2) 预编译语句对象，支持占位符. 
	 
8. oracle 中的索引 与视图. 
   索引: B+树索引 
   视图. 
   
9. 加载资源文件
     ///  类的字节码  获取类加载器 ( eclipse 类路径 bin |  IDEA target) 
    this.getClass().getClassLoader().getResourceAsStream("db.properties");
	 

