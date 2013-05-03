package msi.gama.database.mdx;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import msi.gama.database.MDXConnection;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;

import org.olap4j.Axis;
import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.CellSetMetaData;
import org.olap4j.OlapConnection;
import org.olap4j.OlapException;
import org.olap4j.OlapStatement;
import org.olap4j.Position;
import org.olap4j.layout.RectangularCellSetFormatter;
import org.olap4j.metadata.Member;

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
public abstract class MdxConnection {
	private static final boolean DEBUG = false; // Change DEBUG = false for release version
	public static final String MONDRIAN ="MONDRIAN";
	public static final String MSAS ="MSAS"; //Micrsoft SQL Server Analysis Services
	public static final String GEOMETRYTYPE="GEOMETRY";
		
	protected String vender="";
	protected String url="";
	protected String port="";
	protected String dbName="";
	protected String userName="";
	protected String password="";
	
	protected OlapConnection olapConnection;
	protected Connection connection;
	
	public MdxConnection(String vender)
	{
		this.vender=vender;
	}
	public MdxConnection(String venderName,String database)
	{
		this.vender=venderName;
		this.dbName=database;
	}
	public MdxConnection()
	{
	}

	public MdxConnection(String venderName,String url,String port,
			String dbName, String userName,String password)  
	{
		this.vender=venderName;
		this.url=url;
		this.port=port;
		this.dbName=dbName;
		this.userName=userName;
		this.password=password;	
	}
	
	public void setConnection(){
		this.connection=connectMDB();
		
	}
	public Connection getConnection(){
		return this.connection;
	}
	public boolean isConnected(){
		if (this.connection!=null){
			return true;
		}else {
			return false;
		}
	}
	
	/*
	 * Make a connection to Multidimensional Database Server
	 */
	public abstract Connection connectMDB() throws GamaRuntimeException ;

	/*
	 * Select data source with connection was established
	 */
	
	public CellSet selectMDB(String selectComm)
	{
		CellSet resultCellSet=null;
		Connection conn=null;
		try {
			conn = connectMDB();
			resultCellSet = selectMDB(conn, selectComm);
			conn.close();
		} catch (SQLException e) {

		}
		return resultCellSet;
	}
	
	/*
	 * Select data source with connection was established
	 */
	public CellSet selectMDB(Connection connection, String selectComm) throws GamaRuntimeException 
	{
		 CellSet resultCellSet=null;
		 OlapStatement statement;
		try {
			statement = (OlapStatement) connection.createStatement();
			resultCellSet=statement.executeOlapQuery(selectComm);
	        statement.close();
	        connection.close();
		}catch (OlapException e){
			e.printStackTrace();
			throw new GamaRuntimeException(e.toString());
		}catch (SQLException e) {
			e.printStackTrace();
			throw new GamaRuntimeException(e.toString());
		}
		 return resultCellSet;
	}
	
	public void cellSet2List(CellSet cellset){
		
	}
}
