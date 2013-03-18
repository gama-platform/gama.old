package irit.gaml.species;

import java.sql.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.database.SqlConnection;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.types.IType;

/*
 * @Author TRUONG Minh Thai
 * 
 * @Supervisors:
 * Christophe Sibertin-BLANC
 * Fredric AMBLARD
 * Benoit GAUDOU
 * 
 * species: The AgentDB is defined in this class. AgentDB supports the action
 * - isConnected: return true/false
 * - close: close current connection
 * - connect: make a connection to DBMS.
 * - select: executeQuery to select data from DBMS via current connection.
 * - executeUpdate: run executeUpdate to update/insert/delete/drop/create data on DBMS via current
 * connection.
 * 
 * created date: 22-Feb-2012
 * Modified:
 * 24-Sep-2012:
 * Add methods:
 * - boolean isconnected()
 * - select(String select)
 * - executeUpdate(String updateComm)
 * - getParameter: return connection Parameter;
 * Delete method: selectDB, executeUpdateDB
 * 25-Sep-2012: Add methods: timeStamp, helloWorld
 * 18-Feb-2013:
 * Add public int insert(final IScope scope) throws GamaRuntimeException
 * 21-Feb-2013:
 * Modify public GamaList<Object> select(final IScope scope) throws GamaRuntimeException
 * Modify public int executeUpdate(final IScope scope) throws GamaRuntimeException
 * Modify public int insert(final IScope scope) throws GamaRuntimeException
 * 10-Mar-2013:
 * Modify select method: Add transform parameter
 * Modify insert method: Add transform parameter
 * Last Modified: 10-Mar-2013
 */
@species(name = "AgentDB")
public class AgentDB extends GamlAgent {

	private Connection conn = null;
	private boolean isConnection = false;
	private java.util.Map params = null;
	// private Statement stat;
	static final boolean DEBUG = false; // Change DEBUG = false for release version

	public AgentDB(final IPopulation s) throws GamaRuntimeException {
		super(s);
	}

	@action(name = "isConnected")
	@args(names = {})
	public boolean isConnected(final IScope scope) throws GamaRuntimeException {

		return isConnection;

	}

	@action(name = "close")
	@args(names = {})
	public Object close(final IScope scope) throws GamaRuntimeException {
		try {
			conn.close();
			isConnection = false;
		} catch (SQLException e) {
			// e.printStackTrace();
			throw new GamaRuntimeException("AgentDB.close error:" + e.toString());
		}
		return null;

	}

	@action(name = "helloWorld")
	@args(names = {})
	public Object helloWorld(final IScope scope) throws GamaRuntimeException {
		GuiUtils.informConsole("Hello World");
		return null;
	}

	// Get current time of system
	// added from MaeliaSkill
	@action(name = "timeStamp")
	@args(names = {})
	public Long timeStamp(final IScope scope) throws GamaRuntimeException {
		Long timeStamp = System.currentTimeMillis();
		return timeStamp;
	}

	/*
	 * Make a connection to BDMS
	 * 
	 * @syntax: do action: connectDB {
	 * arg params value:[
	 * "dbtype":"SQLSERVER",
	 * "url":"host address",
	 * "port":"port number",
	 * "database":"database name",
	 * "user": "user name",
	 * "passwd": "password"
	 * ];
	 * }
	 */
	@action(name = "connect")
	@args(names = { "params" })
	public Object connectDB(final IScope scope) throws GamaRuntimeException {

		// java.util.Map params = (java.util.Map) scope.getArg("params", IType.MAP);
		params = (java.util.Map) scope.getArg("params", IType.MAP);

		String dbtype = (String) params.get("dbtype");
		String host = (String) params.get("host");
		String port = (String) params.get("port");
		String database = (String) params.get("database");
		String user = (String) params.get("user");
		String passwd = (String) params.get("passwd");
		SqlConnection sqlConn;
		if ( isConnection ) { throw new GamaRuntimeException(
			"AgentDB.connection error: a connection is already opened"); }
		// create connection
		if ( dbtype.equalsIgnoreCase(SqlConnection.SQLITE) ) {
			String DBRelativeLocation =
				scope.getSimulationScope().getModel().getRelativeFilePath(database, true);

			// sqlConn=new SqlConnection(dbtype,database);
			sqlConn = new SqlConnection(dbtype, DBRelativeLocation);
		} else {
			sqlConn = new SqlConnection(dbtype, host, port, database, user, passwd);
		}
		try {
			conn = sqlConn.connectDB();
			isConnection = true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GamaRuntimeException("AgentDB.connect:" + e.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new GamaRuntimeException("AgentDB.connect:" + e.toString());
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new GamaRuntimeException("AgentDB.connect:" + e.toString());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new GamaRuntimeException("AgentDB.connect:" + e.toString());
		}
		return null;
	}

	/*
	 * Make a connection to BDMS
	 * 
	 * @syntax: do action: connectDB {
	 * arg params value:[
	 * "dbtype":"SQLSERVER",
	 * "url":"host address",
	 * "port":"port number",
	 * "database":"database name",
	 * "user": "user name",
	 * "passwd": "password",
	 * ];
	 * }
	 */
	@action(name = "testConnection")
	@args(names = { "params" })
	public boolean testConnection(final IScope scope) throws GamaRuntimeException {
		Connection conn;
		java.util.Map params = (java.util.Map) scope.getArg("params", IType.MAP);
		String dbtype = (String) params.get("dbtype");
		String host = (String) params.get("host");
		String port = (String) params.get("port");
		String database = (String) params.get("database");
		String user = (String) params.get("user");
		String passwd = (String) params.get("passwd");
		SqlConnection sqlConn;

		// create connection
		if ( dbtype.equalsIgnoreCase(SqlConnection.SQLITE) ) {
			String DBRelativeLocation =
				scope.getSimulationScope().getModel().getRelativeFilePath(database, true);

			// sqlConn=new SqlConnection(dbtype,database);
			sqlConn = new SqlConnection(dbtype, DBRelativeLocation);
		} else {
			sqlConn = new SqlConnection(dbtype, host, port, database, user, passwd);
		}
		try {
			conn = sqlConn.connectDB();
			conn.close();
		} catch (Exception e) {
			// e.printStackTrace();
			// throw new GamaRuntimeException("SQLSkill.connectDB: " + e.toString());
			return false;
		}
		return true;
	}

	/*
	 * Make a connection to BDMS and execute the select statement
	 * 
	 * @syntax do action:
	 * select {
	 * arg select value: "select string with question marks";
	 * arg values value [List of values that are used to replace question marks]
	 * }
	 * 
	 * @return GamaList<GamaList<Object>>
	 */
	@action(name = "select", args = {
		@arg(name = "select", type = IType.STRING_STR, optional = false, doc = @doc("select string")),
		@arg(name = "values", type = IType.LIST_STR, optional = true, doc = @doc("List of values that are used to replace question marks")),
		@arg(name = "transform", type = IType.BOOL_STR, optional = true, doc = @doc("if transform = true then geometry will be tranformed from absolute to gis otherways it will be not transformed. Default value is false ")) })
	public GamaList<Object> select(final IScope scope) throws GamaRuntimeException {
		String selectComm = (String) scope.getArg("select", IType.STRING);
		GamaList<Object> values = (GamaList<Object>) scope.getArg("values", IType.LIST);
		Boolean transform =
			scope.hasArg("transform") ? (Boolean) scope.getArg("transform", IType.BOOL) : false;
		GamaList<Object> repRequest = new GamaList<Object>();
		// get data
		try {
			if ( values.size() > 0 ) {
				repRequest = new SqlConnection().executeQueryDB(conn, selectComm, values);
			} else {
				repRequest = new SqlConnection().selectDB(conn, selectComm);
			}
			if ( transform ) { return new SqlConnection().fromGisToAbsolute(scope, repRequest); }
			return repRequest;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GamaRuntimeException("AgentDB.select: " + e.toString());
		}
	}

	/*
	 * - Make a connection to BDMS
	 * - Executes the SQL statement in this PreparedStatement object, which must be an SQL INSERT,
	 * UPDATE or DELETE statement; or an SQL statement that returns nothing, such as a DDL
	 * statement.
	 * 
	 * @syntax: do action: executeUpdate {
	 * arg updateComm value: " SQL statement string with question marks"
	 * arg values value [List of values that are used to replace question marks]
	 * }
	 */
	@action(name = "executeUpdate", args = {
		@arg(name = "updateComm", type = IType.STRING_STR, optional = false, doc = @doc("SQL commands such as Create, Update, Delete, Drop with question mark")),
		@arg(name = "values", type = IType.LIST_STR, optional = true, doc = @doc("List of values that are used to replace question mark")),
		@arg(name = "transform", type = IType.BOOL_STR, optional = true, doc = @doc("if transform = true then geometry will be tranformed from absolute to gis otherways it will be not transformed. Default value is false ")) })
	public int executeUpdate(final IScope scope) throws GamaRuntimeException {
		String updateComm = (String) scope.getArg("updateComm", IType.STRING);
		GamaList<Object> values = (GamaList<Object>) scope.getArg("values", IType.LIST);
		SqlConnection sqlConn;
		int row_count = -1;
		// get data
		try {
			if ( values.size() > 0 ) {
				row_count = new SqlConnection().executeUpdateDB(conn, updateComm, values);
			} else {
				row_count = new SqlConnection().executeUpdateDB(conn, updateComm);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GamaRuntimeException("AgentDB.executeUpdate: " + e.toString());
		}
		if ( DEBUG ) {
			GuiUtils.informConsole(updateComm + " was run");
		}

		return row_count;

	}

	@action(name = "getParameter")
	@args(names = {})
	public Object getParamater(final IScope scope) throws GamaRuntimeException {
		return params;
	}

	@action(name = "setParameter")
	@args(names = { "params" })
	public Object setParameter(final IScope scope) throws GamaRuntimeException {
		Connection conn;
		params = (java.util.Map) scope.getArg("params", IType.MAP);
		return null;
	}

	/*
	 * Make a connection to BDMS and execute the insert statement
	 * 
	 * @syntax do insert with: [into:: table_name, columns:column_list, values:value_list];
	 * 
	 * @return an integer
	 */
	@action(name = "insert", args = {
		@arg(name = "into", type = IType.STRING_STR, optional = false, doc = @doc("Table name")),
		@arg(name = "columns", type = IType.LIST_STR, optional = true, doc = @doc("List of column name of table")),
		@arg(name = "values", type = IType.LIST_STR, optional = false, doc = @doc("List of values that are used to insert into table. Columns and values must have same size")),
		@arg(name = "transform", type = IType.BOOL_STR, optional = true, doc = @doc("if transform = true then geometry will be tranformed from absolute to gis otherways it will be not transformed. Default value is false ")) })
	public int insert(final IScope scope) throws GamaRuntimeException {
		String table_name = (String) scope.getArg("into", IType.STRING);
		GamaList<Object> cols = (GamaList<Object>) scope.getArg("columns", IType.LIST);
		GamaList<Object> values = (GamaList<Object>) scope.getArg("values", IType.LIST);
		Boolean transform =
			scope.hasArg("transform") ? (Boolean) scope.getArg("transform", IType.BOOL) : false;
		int rec_no = -1;

		try {
			// Connection conn=sqlConn.connectDB();
			if ( cols.size() > 0 ) {
				rec_no =
					new SqlConnection().insertDB(scope, conn, table_name, cols, values, transform);
			} else {
				rec_no = new SqlConnection().insertDB(scope, conn, table_name, values, transform);
			}
			// conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new GamaRuntimeException("AgentDB.insert: " + e.toString());
		}
		if ( DEBUG ) {
			GuiUtils.informConsole("Insert into " + " was run");
		}

		return rec_no;
	}

}
