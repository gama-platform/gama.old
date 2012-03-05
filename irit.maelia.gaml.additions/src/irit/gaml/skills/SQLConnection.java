package irit.gaml.skills;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import msi.gama.common.util.GuiUtils;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

/*
 * SQLConnection: The MAELIADBMS skill is defined in this class. MAELIADBMS supports the action
 * - helloWorld: print "Hello world on output screen as a test of skill.
 * - connectTest: make a connection to DBMS as test connection.
 * - connectDB:   make a connection to DBMS.
 * - selectDB: connect to DBMS and run executeQuery to select data from DBMS.
 * - executeUpdateDB: connect to DBMS and run executeUpdate to update/insert/delete/drop/create data on DBMS.
 * 
 * @author  TRUONG Minh Thai  17-Feb-2012
 * Last Modified: 05-Mar-2012
 */
@skill({"MAELIADBMS"})
public class SQLConnection extends Skill{
	static final boolean DEBUG = true; // Change DEBUG = false for release version
	/*
	 * for test only
	 */
	@action("helloWorld")
	@args({})	
	public Object helloWorld(final IScope scope) throws GamaRuntimeException {
		GuiUtils.informConsole("Hello World");
		return null;
	}	
	/*
	 * Make a connection to BDMS
	 * @syntax: do action: connectTest {
	 * 									arg url value: urlvalue; 
	 *                                  arg port value: portvaluse;
	 *                                  arg dbName value: dbnamevalue;
	 *                                  arg usrName value: usrnamevalue;
	 *                                  arg password value: pwvaluse;
	 *                                  arg driver value: drivervalue;
	 *                                 }
	 *                                     	 
	 */

	@action("connectTest")
	@args({"url", "port", "dbName", "usrName", "password", "driver"})
	public  Object  connectTest(final IScope scope) throws GamaRuntimeException
	{
		Connection conn = null;
		String url = (String) scope.getArg("url", IType.STRING);
		String port = (String) scope.getArg("port", IType.STRING);
		String dbName = (String) scope.getArg("dbName", IType.STRING);
		String usrName = (String) scope.getArg("usrName", IType.STRING);
		String password = (String) scope.getArg("password", IType.STRING);
		String driver = (String) scope.getArg("driver", IType.STRING);
		
		try{
			Class.forName(driver).newInstance();
			GuiUtils.informConsole("jdbc:mysql://"+url+":"+ port +"/"+dbName +";"+usrName +";"+ password);
			conn = DriverManager.getConnection("jdbc:mysql://"+url+":"+ port +"/"+dbName, usrName, password);
			GuiUtils.informConsole(url + "is connected");
			conn.close();
			return null;
		} catch (ClassNotFoundException e) {
			System.out.println("irit.sql.gaml.SQLConnection.connectDB : JDBC driver not found");
			e.printStackTrace();
			throw new GamaRuntimeException(driver+" not found");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GamaRuntimeException("SQL error: check the database name or the SQL request");
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			throw new GamaRuntimeException(driver+" not found");
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GamaRuntimeException(driver+" not found");
		}	
		
	}

	/*
	 * Make a connection to BDMS
	 * @syntax: do action: connectDB {
	 * 									arg vendorName value: vendorName; //MySQL/MSSQL
	 * 									arg url value: urlvalue; 
	 *                                  arg port value: portvaluse;
	 *                                  arg dbName value: dbnamevalue;
	 *                                  arg usrName value: usrnamevalue;
	 *                                  arg password value: pwvaluse;
	 *                                 }
	 *                                     	 
	 */	
	@action("connectDB")
	@args({"vendorName","url", "port", "dbName", "usrName", "password"})	
	public  Object  connectDB(final IScope scope) throws GamaRuntimeException
	{
		Connection conn = null;
		String vendorName = (String) scope.getArg("vendorName", IType.STRING);
		String url = (String) scope.getArg("url", IType.STRING);
		String port = (String) scope.getArg("port", IType.STRING);
		String dbName = (String) scope.getArg("dbName", IType.STRING);
		String usrName = (String) scope.getArg("usrName", IType.STRING);
		String password = (String) scope.getArg("password", IType.STRING);
		try{
			conn = connectMySQL(vendorName,url,port,dbName,usrName,password);			
			conn.close();
		}	
		catch (SQLException e) {
			e.printStackTrace();
			throw new GamaRuntimeException("SQLConnection.connectDB:"+ e.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new GamaRuntimeException("SQLConnection.connectDB:"+ e.toString());
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new GamaRuntimeException("SQLConnection.connectDB:"+ e.toString());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new GamaRuntimeException("SQLConnection.connectDB:"+ e.toString());
		}catch (Exception e) {
			e.printStackTrace();
			throw new GamaRuntimeException("SQLConnection.connectDB:"+ e.toString());
		}
		
		if (DEBUG){
			GuiUtils.informConsole(vendorName + " Server at address "+url + " is connected");
		}
		return null;
		
	}
	
	/*
	 * Make a connection to BDMS and execute the select statement
	 * @syntax do action: selectDB {
	 * 									arg url value: urlvalue; 
	 *                                  arg port value: portvaluse;
	 *                                  arg dbName value: dbnamevalue;
	 *                                  arg usrName value: usrnamevalue;
	 *                                  arg password value: pwvaluse;
	 *                                  arg selectComm value: selectStatement; 
	 *                                 }
	 *@return  GamaList<GamaList<Object>>                                     	 
	 */ 
	@action("selectDB")
	@args({"vendorName","url", "port", "dbName", "usrName", "password", "selectComm"})	
	public  GamaList<GamaList<Object>>  selectDB(final IScope scope) throws GamaRuntimeException
	{
		Connection conn = null;
		String vendorName = (String) scope.getArg("vendorName", IType.STRING);
		String url = (String) scope.getArg("url", IType.STRING);
		String port = (String) scope.getArg("port", IType.STRING);
		String dbName = (String) scope.getArg("dbName", IType.STRING);
		String usrName = (String) scope.getArg("usrName", IType.STRING);
		String password = (String) scope.getArg("password", IType.STRING);
		String selectComm = (String) scope.getArg("selectComm", IType.STRING);
		
		ResultSet rs;
		GamaList<Object> rowList = new GamaList<Object>();
		GamaList<GamaList<Object>> repRequest = new GamaList<GamaList<Object>>();
		
		try{
			conn = connectMySQL(vendorName,url,port,dbName,usrName,password);
			rs = conn.createStatement().executeQuery(selectComm);
			ResultSetMetaData rsmd = rs.getMetaData();
			int nbCol = rsmd.getColumnCount();
		    			
			while (rs.next()) {
				rowList = new GamaList<Object>();
				for(int i = 1;i<=nbCol;i++){
					rowList.add(rs.getObject(i));
				}
				repRequest.add(rowList);
			}			
			rs.close();
			conn.close();
		}	
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GamaRuntimeException("SQLConnection.selectDB:"+ e.toString());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GamaRuntimeException("SQLConnection.selectDB:"+ e.toString());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GamaRuntimeException("SQLConnection.selectDB:"+ e.toString());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GamaRuntimeException("SQLConnection.selectDB:"+ e.toString());
		}
		
		if (DEBUG){
			GuiUtils.informConsole(selectComm + " was run");
		}
		
		return repRequest;
		
	}	

	/*
	 * Make a connection to BDMS and execute the update statement (update/insert/delete/create/drop)
	 * @syntax: do action: connectDB {
	 * 									arg vendorName value: vendorvalue;
	 * 									arg url value: urlvalue; 
	 *                                  arg port value: portvalue;
	 *                                  arg dbName value: dbnamevalue;
	 *                                  arg usrName value: usrnamevalue;
	 *                                  arg password value: pwvaluse;
	 *                                  arg updateComm value: updateStatement; 
	 *                                 }
	 *                                     	 
	 */	
	@action("executeUpdateDB")
	@args({"vendorName","url", "port", "dbName", "usrName", "password", "updateComm"})	
	public  int  executeUpdateDB(final IScope scope) throws GamaRuntimeException
	{
		Connection conn = null;
		String vendorName = (String) scope.getArg("vendorName", IType.STRING);
		String url = (String) scope.getArg("url", IType.STRING);
		String port = (String) scope.getArg("port", IType.STRING);
		String dbName = (String) scope.getArg("dbName", IType.STRING);
		String usrName = (String) scope.getArg("usrName", IType.STRING);
		String password = (String) scope.getArg("password", IType.STRING);
		String updateComm = (String) scope.getArg("updateComm", IType.STRING);
		int n=0;
		try{
			conn = connectMySQL(vendorName,url,port,dbName,usrName,password);
			n = conn.createStatement().executeUpdate(updateComm);
			conn.close();
		}	
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GamaRuntimeException("SQLConnection.selectDB:"+ e.toString());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GamaRuntimeException("SQLConnection.selectDB:"+ e.toString());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GamaRuntimeException("SQLConnection.selectDB:"+ e.toString());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GamaRuntimeException("SQLConnection.selectDB:"+ e.toString());
		}
		
		if (DEBUG){
			GuiUtils.informConsole(updateComm + " was run");
		}
		
		return n;
		
	}	
	
	
	public Connection connectMySQL(String vendorName, String url, String port, String dbName, String usrName, String password)
	throws ClassNotFoundException, InstantiationException, SQLException, IllegalAccessException
	{	
		Connection conn=null;
		String mySQLDriver = new String("com.mysql.jdbc.Driver");
		String msSQLDriver = new String("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		try{
			if (vendorName.equalsIgnoreCase("MySQL")){
				Class.forName(mySQLDriver).newInstance();
				conn = DriverManager.getConnection("jdbc:mysql://"+url+":"+ port +"/"+dbName, usrName, password);				
			}else if (vendorName.equalsIgnoreCase("MSSQL")){
				Class.forName(msSQLDriver).newInstance();
				conn = DriverManager.getConnection("jdbc:sqlserver://"+url+":"+ port +";databaseName="+dbName +";user="+ usrName+";password="+ password+";");
			}else {
				throw new ClassNotFoundException("SQLConnection.connectSQL: The "+vendorName +"is not supported!");
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
		return conn;			
	}


}
