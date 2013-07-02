package msi.gama.database.mdx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import msi.gama.runtime.exceptions.GamaRuntimeException;

import org.olap4j.OlapConnection;
import org.olap4j.OlapWrapper;

public class MSASConnection extends MdxConnection {
	static final String DRIVER = new String("org.olap4j.driver.xmla.XmlaOlap4jDriver");

	public MSASConnection()
	{
		super();
	}
	
	public MSASConnection(String vender)
	{
		super(vender);
	}
	public MSASConnection(String venderName,String database)
	{
		super(venderName,database);
	}

	public MSASConnection(String venderName,String url,String port,
			String dbName, String userName,String password)  
	{
		super(venderName,url,port,dbName,userName,password);	
	}
	

	@Override
	public Connection connectMDB() throws GamaRuntimeException 
	{
		OlapWrapper wrapper;
		Connection conn;
		try {
			if ( vender.equalsIgnoreCase(MSAS) ) {
				Class.forName(DRIVER);
				conn =
				    DriverManager.getConnection(
				    		//"jdbc:xmla:Server=http://localhost/xmla/msxisapi.dll");
				    		"jdbc:xmla:Server=http://"+ url + ":" + port + "/" + dbName+ "/msmdpump.dll;",userName,password);
				 wrapper = (OlapWrapper) conn;
				 olapConnection = wrapper.unwrap(OlapConnection.class);	
			} else {
				throw GamaRuntimeException.error("MSASConnection.connectMDB: The " 
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
