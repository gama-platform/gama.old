package msi.gama.database.mdx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.olap4j.OlapConnection;
import org.olap4j.OlapWrapper;

import msi.gama.common.util.GuiUtils;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class MondrianConnection extends MdxConnection {
	private static final boolean DEBUG = true; // Change DEBUG = false for release version
	static final String DRIVER = new String("mondrian.olap4j.MondrianOlap4jDriver");	
	private static java.util.HashMap<String, String> driverMap = new java.util.HashMap<String, String>();
	private static java.util.HashMap<String, String> jdbcMap = new java.util.HashMap<String, String>();

	public MondrianConnection()
	{
		super();
		init();
	}
	
	public MondrianConnection(String vender)
	{
		super(vender);
		init();
	}
	public MondrianConnection(String venderName,String database)
	{
		super(venderName,database);
		init();
	}

	public MondrianConnection(String venderName,String url,String port,
			String dbName, String userName,String password)  
	{
		super(venderName,url,port,dbName,userName,password);	
		init();
	}
	
	public MondrianConnection(String venderName,String url,String port,
			String dbName, String catalog, String userName,String password)  
	{
		super(venderName,url,port,dbName,catalog, userName,password);
		init();
	}

	public MondrianConnection(String venderName, String dbtype, String url,String port,
			String dbName, String catalog, String userName,String password)  
	{
		super(venderName,dbtype, url,port,dbName,catalog,userName,password);
		init();
	}
	
	private void init(){
		driverMap.put(MYSQL, MYSQLDriver);
		driverMap.put(POSTGRES, POSTGRESDriver);
		driverMap.put(POSTGIS, POSTGRESDriver);
		driverMap.put(MSSQL, MSSQLDriver);
		driverMap.put(SQLITE, SQLITEDriver);		
		jdbcMap.put(MYSQL, "jdbc:mysql://");
		jdbcMap.put(POSTGRES,"jdbc:postgresql://");
		jdbcMap.put(POSTGIS, "jdbc:postgresql://");
		jdbcMap.put(MSSQL, "jdbc:jtds:sqlserver://");
		jdbcMap.put(SQLITE, "jdbc:sqlite:");		

	}

	@Override
	public Connection connectMDB() throws GamaRuntimeException 
	{
		OlapWrapper wrapper; 
		Connection conn;
		if (DEBUG){
			GuiUtils.debug("dbtype:"+ dbtype);
			GuiUtils.debug("driver:"+ driverMap.toString());
			GuiUtils.debug("jdbc:"+ jdbcMap.toString());
			GuiUtils.debug("MondrianConnection.connectMDB:"+vender+" - "+dbtype+" - "+" - "+url+" - "
					+ port+" - "+dbName+" - "+catalog+" - "+userName+" - "+password);

		}
		try {
			if ( vender.equalsIgnoreCase(MONDRIAN) ) {
				Class.forName(DRIVER);
				conn =
				DriverManager.getConnection(
						"jdbc:mondrian:"
						+"JdbcDrivers=" + driverMap.get(dbtype.toLowerCase()) +";"
						+"Jdbc="+ jdbcMap.get(dbtype.toLowerCase())+ url+":"+port+"/"+dbName+"?user="+userName+"&"+"password="+password+";"
//						+"Catalog=file:C:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\webapps\\mondrian\\WEB-INF\\queries\\FoodMart.xml;" 
	                    +"Catalog=file:"+catalog +";"
				);
	
				wrapper = (OlapWrapper) conn;
				 olapConnection = wrapper.unwrap(OlapConnection.class);
			} else {
				throw GamaRuntimeException.error("MondrianConnection.connectMDB: The " 
			                                       + vender
			                                       + " is not supported!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString());
		}
		return conn;
	}

	@Override
	public Connection connectMDB(String dbName) throws GamaRuntimeException {
		OlapWrapper wrapper; 
		Connection conn;
		System.out.println("dbtype:"+ dbtype);
		System.out.println("driver:"+ driverMap.toString());
		System.out.println("jdbc:"+ jdbcMap.toString());
		try {
			if ( vender.equalsIgnoreCase(MONDRIAN) ) {
				Class.forName(DRIVER);
				conn =
				DriverManager.getConnection(
						"jdbc:mondrian:"
						+"JdbcDrivers=" + driverMap.get(dbtype.toLowerCase()) +";"
						+"Jdbc="+ jdbcMap.get(dbtype.toLowerCase())+ url+":"+port+"/"+dbName+"?user="+userName+"&"+"password="+password+";"
	                    +"Catalog=file:"+catalog +";"
				);
	
				wrapper = (OlapWrapper) conn;
				 olapConnection = wrapper.unwrap(OlapConnection.class);
			} else {
				throw GamaRuntimeException.error("MondrianConnection.connectMDB: The " 
			                                       + vender
			                                       + " is not supported!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString());
		}
		return conn;
	}

	@Override
	public Connection connectMDB(String dbName, String catalog)
			throws GamaRuntimeException {
		OlapWrapper wrapper; 
		Connection conn;
		System.out.println("dbtype:"+ dbtype);
		System.out.println("driver:"+ driverMap.toString());
		System.out.println("jdbc:"+ jdbcMap.toString());
		try {
			if ( vender.equalsIgnoreCase(MONDRIAN) ) {
				Class.forName(DRIVER);
				conn =
				DriverManager.getConnection(
						"jdbc:mondrian:"
						+"JdbcDrivers=" + driverMap.get(dbtype.toLowerCase()) +";"
						+"Jdbc="+ jdbcMap.get(dbtype.toLowerCase())+ url+":"+port+"/"+dbName+"?user="+userName+"&"+"password="+password+";"
	                    +"Catalog=file:"+catalog +";"
				);
	
				wrapper = (OlapWrapper) conn;
				 olapConnection = wrapper.unwrap(OlapConnection.class);
			} else {
				throw GamaRuntimeException.error("MondrianConnection.connectMDB: The " 
			                                       + vender
			                                       + " is not supported!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString());
		}
		return conn;
	}

}
