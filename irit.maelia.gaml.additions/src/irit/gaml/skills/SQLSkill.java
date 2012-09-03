package irit.gaml.skills;

import irit.gaml.SqlConnection;

import java.sql.Connection;
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
* SQLSkill: The MAELIASQL skill is defined in this class. MAELIASQL supports the action
* - helloWorld: print "Hello world on output screen as a test of skill.
* - connectTest: make a connection to DBMS as test connection.
* - connectDB: make a connection to DBMS.
* - selectDB: connect to DBMS and run executeQuery to select data from DBMS.
* - executeUpdateDB: connect to DBMS and run executeUpdate to update/insert/delete/drop/create data
* on DBMS.
* 
* @author TRUONG Minh Thai 20-Jun-2012
* Modified:
*     
* Last Modified: 20-Jun-2012
*/
@skill(name = "MAELIASQL")
public class SQLSkill extends Skill {
	static final boolean DEBUG = false; // Change DEBUG = false for release version

	/*
	 * for test only
	 */
	@action(name="helloWorld")
	@args(names = {})
	public Object helloWorld(final IScope scope) throws GamaRuntimeException {
		GuiUtils.informConsole("Hello World");
		return null;
	}

	/*
	 * Make a connection to BDMS
	 * 
	 * @syntax: do action: connectDB {
	 * arg vendorName value: vendorName; //MySQL/MSSQL
	 * arg url value: urlvalue;
	 * arg port value: portvaluse;
	 * arg dbName value: dbnamevalue;
	 * arg usrName value: usrnamevalue;
	 * arg password value: pwvaluse;
	 * }
	 */
	@action(name="connectDB")
	@args(names = { "vendorName", "url", "port", "dbName", "usrName", "password" })
	public  Object connectDB(final IScope scope) throws GamaRuntimeException
	{
		Connection conn;
		SqlConnection sqlConn=new SqlConnection(
				(String) scope.getArg("vendorName", IType.STRING),
				(String) scope.getArg("url", IType.STRING),
				(String) scope.getArg("port", IType.STRING),
				(String) scope.getArg("dbName", IType.STRING),
				(String) scope.getArg("usrName", IType.STRING),
				(String) scope.getArg("password", IType.STRING));

		try {	
			conn = sqlConn.connectDB();
		} catch (Exception e) {
			e.printStackTrace();
			throw new GamaRuntimeException("SQLSkill.connectDB:" + e.toString());
		} 
		if ( DEBUG ) {
			GuiUtils.informConsole(sqlConn.getVendor() + " Server at address " + sqlConn.getURL() + " is connected");
		}
		return conn;

	}
	
	/*
	 * Make a connection to BDMS and execute the select statement
	 * 
	 * @syntax do action: selectDB {
	 * arg url value: urlvalue;
	 * arg port value: portvaluse;
	 * arg dbName value: dbnamevalue;
	 * arg usrName value: usrnamevalue;
	 * arg password value: pwvaluse;
	 * arg selectComm value: selectStatement;
	 * }
	 * 
	 * @return GamaList<GamaList<Object>>
	 */
	@action(name="selectDB")
	@args(names = { "vendorName", "url", "port", "dbName", "usrName", "password", "selectComm" })
	//public GamaList<GamaList<Object>> selectDB(final IScope scope) throws GamaRuntimeException
	public GamaList<Object> selectDB(final IScope scope) throws GamaRuntimeException
	{
		String selectComm=(String) scope.getArg("selectComm", IType.STRING);
		SqlConnection sqlConn=new SqlConnection(
				(String) scope.getArg("vendorName", IType.STRING),
				(String) scope.getArg("url", IType.STRING),
				(String) scope.getArg("port", IType.STRING),
				(String) scope.getArg("dbName", IType.STRING),
				(String) scope.getArg("usrName", IType.STRING),
				(String) scope.getArg("password", IType.STRING));
		
		//GamaList<GamaList<Object>> repRequest = new GamaList<GamaList<Object>>();
		GamaList<Object> repRequest = new GamaList<Object>();
		try{
			repRequest = sqlConn.selectDB(selectComm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GamaRuntimeException("SQLSkill.selectDB: " + e.toString());
		}
		if ( DEBUG ) {
			GuiUtils.informConsole(selectComm + " was run");
		}

		return repRequest;

	}

	/*
	 * Make a connection to BDMS and execute the update statement (update/insert/delete/create/drop)
	 * 
	 * @syntax: do action: connectDB {
	 * arg vendorName value: vendorvalue;
	 * arg url value: urlvalue;
	 * arg port value: portvalue;
	 * arg dbName value: dbnamevalue;
	 * arg usrName value: usrnamevalue;
	 * arg password value: pwvaluse;
	 * arg updateComm value: updateStatement;
	 * }
	 */
	@action(name="executeUpdateDB")
	@args(names = { "vendorName", "url", "port", "dbName", "usrName", "password", "updateComm" })
	public int executeUpdateDB(final IScope scope) throws GamaRuntimeException {
		SqlConnection sqlConn=new SqlConnection(
				(String) scope.getArg("vendorName", IType.STRING),
				(String) scope.getArg("url", IType.STRING),
				(String) scope.getArg("port", IType.STRING),
				(String) scope.getArg("dbName", IType.STRING),
				(String) scope.getArg("usrName", IType.STRING),
				(String) scope.getArg("password", IType.STRING));
		String updateComm = (String) scope.getArg("updateComm", IType.STRING);
		int n = 0;
		try {
			n = sqlConn.executeUpdateDB(updateComm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GamaRuntimeException("SQLSkill.exexuteUpdateDB:" + e.toString());
		}

		if ( DEBUG ) {
			GuiUtils.informConsole(updateComm + " was run");
		}

		return n;

	}

}
