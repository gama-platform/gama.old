package msi.gama.database;

import java.sql.*;

import org.olap4j.*;

/*
 * @Author  
 *     TRUONG Minh Thai
 *     Fredric AMBLARD
 *     Benoit GAUDOU
 *     Christophe Sibertin-BLANC
 * 
 * 
 * SQLConnection:   supports the method
 * - connectDB: make a connection to DBMS.
 * - selectDB: connect to DBMS and run executeQuery to select data from DBMS.
 * - executeUpdateDB: connect to DBMS and run executeUpdate to update/insert/delete/drop/create data
 * on DBMS.
 * 
 * Created date: 18-Jan-2013
 * Modified:
 *     dd-mm-yyyy: description
 *        
 * Last Modified: 18-Jan-2013
 */
public class MDXConnection {
	private static final boolean DEBUG = false; // Change DEBUG = false for release version
	public static final String MONDRIAN ="MONDRIAN";
	public static final String MSAS ="MSAS"; //Micrsoft SQL Server Analysis Services
	public static final String GEOMETRYTYPE="GEOMETRY";
	static final String MONDRIANDriver = new String("mondrian.olap4j.MondrianOlap4jDriver");
	static final String MSASDriver = new String("org.olap4j.driver.xmla.XmlaOlap4jDriver");
	
	protected String vender="";
	protected String url="";
	protected String port="";
	protected String dbName="";
	protected String userName="";
	protected String password="";
	
	public MDXConnection(String vender)
	{
		this.vender=vender;
	}
	public MDXConnection(String venderName,String database)
	{
		this.vender=venderName;
		this.dbName=database;
	}
	public MDXConnection()
	{
	}

	public MDXConnection(String venderName,String url,String port,
			String dbName, String userName,String password)  
	{
		this.vender=venderName;
		this.url=url;
		this.port=port;
		this.dbName=dbName;
		this.userName=userName;
		this.password=password;	
	}
	
	/*
	 * Make a connection to Multidimensional Database Server
	 */
	public OlapConnection connectMDB()
			throws ClassNotFoundException, InstantiationException, SQLException, IllegalAccessException 
	{
			Connection conn = null;
			OlapWrapper wrapper;
			OlapConnection olapConnection;
			
			try {
				if ( vender.equalsIgnoreCase(MONDRIAN) ) {
					Class.forName(MONDRIANDriver).newInstance();
					conn =
						DriverManager.getConnection("jdbc:mondrian:Jdbc=jdbc:odbc:" + url + ":" + port + "/" + dbName,
							userName, password);
					 wrapper = (OlapWrapper) conn;
					 olapConnection = wrapper.unwrap(OlapConnection.class);
				} else if ( vender.equalsIgnoreCase(MSAS) ) {
					Class.forName("org.olap4j.driver.xmla.XmlaOlap4jDriver");
					Connection connection =
					    DriverManager.getConnection(
					    		//"jdbc:xmla:Server=http://localhost/xmla/msxisapi.dll");
					    		"jdbc:xmla:Server=http://"+ url + ":" + port + "/" + dbName+ "/msmdpump.dll;",userName,password);
					 wrapper = (OlapWrapper) connection;
					 olapConnection = wrapper.unwrap(OlapConnection.class);	
				} else {
					throw new ClassNotFoundException("MDXConnection.connectMDB: The " 
				                                       + vender
				                                       + " is not supported!");
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new ClassNotFoundException(e.toString());
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new InstantiationException(e.toString());
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new IllegalAccessException(e.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new SQLException(e.toString());
			}
			return olapConnection;
	}

	

}
