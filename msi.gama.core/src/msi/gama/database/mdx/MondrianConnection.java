package msi.gama.database.mdx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.olap4j.OlapConnection;
import org.olap4j.OlapWrapper;

import msi.gama.runtime.exceptions.GamaRuntimeException;

public class MondrianConnection extends MdxConnection {
	static final String DRIVER = new String("mondrian.olap4j.MondrianOlap4jDriver");

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
	

	@Override
	public Connection connectMDB() throws GamaRuntimeException 
	{
		OlapWrapper wrapper;
		Connection conn;
		try {
			if ( vender.equalsIgnoreCase(MONDRIAN) ) {
				Class.forName(DRIVER);
				conn =
					DriverManager.getConnection("jdbc:mondrian:Jdbc=jdbc:odbc:" + url + ":" + port + "/" + dbName,
						userName, password);
				 wrapper = (OlapWrapper) conn;
				 olapConnection = wrapper.unwrap(OlapConnection.class);
			} else {
				throw new GamaRuntimeException("MondrianConnection.connectMDB: The " 
			                                       + vender
			                                       + " is not supported!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GamaRuntimeException(e.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new GamaRuntimeException(e.toString());
		}
		return conn;
	}

}
