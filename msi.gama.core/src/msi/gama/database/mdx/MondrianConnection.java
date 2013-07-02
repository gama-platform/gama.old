package msi.gama.database.mdx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.olap4j.OlapConnection;
import org.olap4j.OlapWrapper;

import msi.gama.runtime.exceptions.GamaRuntimeException;

public class MondrianConnection extends MdxConnection {
	static final String DRIVER = new String("mondrian.olap4j.MondrianOlap4jDriver");	
	protected String catalog=new String("");
	protected String dbServer=MYSQL;
	public MondrianConnection()
	{
		super();
	}
	
	public MondrianConnection(String vender)
	{
		super(vender);
	}
	public MondrianConnection(String venderName,String database)
	{
		super(venderName,database);
	}

	public MondrianConnection(String venderName,String url,String port,
			String dbName, String userName,String password)  
	{
		super(venderName,url,port,dbName,userName,password);	
	}
	
	public MondrianConnection(String venderName,String url,String port,
			String dbName, String userName,String password,String catalog)  
	{
		super(venderName,url,port,dbName,userName,password);
		this.catalog=catalog;
	}

	public MondrianConnection(String venderName, String dbServer, String url,String port,
			String dbName, String userName,String password,String catalog)  
	{
		super(venderName,url,port,dbName,userName,password);
		this.catalog=catalog;
		this.dbServer=dbServer;
	}

	@Override
	public Connection connectMDB() throws GamaRuntimeException 
	{
		OlapWrapper wrapper;
		Connection conn;
		try {
			if ( vender.equalsIgnoreCase(MONDRIAN) ) {
				Class.forName(DRIVER);
				conn =
				DriverManager.getConnection(
						"jdbc:mondrian:"
						+"JdbcDrivers=com.mysql.jdbc.Driver;"
						+"Jdbc=jdbc:mysql://localhost:"+port+"/"+dbName+"?user="+userName+"&"+"password="+password+";"
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

}
