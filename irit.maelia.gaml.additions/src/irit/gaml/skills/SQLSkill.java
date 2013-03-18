package irit.gaml.skills;

import java.sql.Connection;
import msi.gama.common.util.GuiUtils;
import msi.gama.database.SqlConnection;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.matrix.*;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

@skill(name = "SQLSKILL")
public class SQLSkill extends Skill {

	private static final boolean DEBUG = false; // Change DEBUG = false for release version

	/*
	 * for test only
	 */
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
	 * "dbtype":"SQLSERVER", //MySQL/sqlserver/sqlite
	 * "url":"host address",
	 * "port":"port number",
	 * "database":"database name",
	 * "user": "user name",
	 * "passwd": "password"
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
			if ( DEBUG ) {
				GuiUtils.debug("testConnection.Database:" + database);
				GuiUtils.debug("testConnection.DBRelativeLocation:" + DBRelativeLocation);
			}
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
	 * Make a connection to BDMS and execute the update statement (update/insert/delete/create/drop)
	 * 
	 * @syntax: do action: executeUpdate {
	 * arg params value:[
	 * "dbtype":"MSSQL",
	 * "url":"host address",
	 * "port":"port number",
	 * "database":"database name",
	 * "user": "user name",
	 * "passwd": "password",
	 * ],
	 * arg updateComm value: "update string"
	 * }
	 */
	// @action(name="executeUpdate",
	// args = {
	// @arg(name = "params", type = IType.MAP_STR, optional = false, doc =
	// @doc("Connection parameters")),
	// @arg(name = "updateComm", type = IType.STRING_STR, optional = false, doc =
	// @doc("SQL commands such as Create, Update, Delete, Drop"))
	// })
	//
	// public int executeUpdate(final IScope scope) throws GamaRuntimeException {
	// java.util.Map params = (java.util.Map) scope.getArg("params", IType.MAP);
	// String updateComm = (String) scope.getArg("updateComm", IType.STRING);
	// String dbtype = (String) params.get("dbtype");
	// String host = (String)params.get("host");
	// String port = (String)params.get("port");
	// String database = (String) params.get("database");
	// String user = (String) params.get("user");
	// String passwd = (String)params.get("passwd");
	// SqlConnection sqlConn;
	// // create connection
	// if (dbtype.equalsIgnoreCase(SqlConnection.SQLITE)){
	// String DBRelativeLocation =
	// scope.getSimulationScope().getModel().getRelativeFilePath(database, true);
	// System.out.println("database sqlite:"+DBRelativeLocation);
	// //sqlConn=new SqlConnection(dbtype,database);
	// sqlConn=new SqlConnection(dbtype,DBRelativeLocation);
	// }else{
	// sqlConn=new SqlConnection(dbtype,host,port,database,user,passwd);
	// }
	// int n = 0;
	// try {
	// n = sqlConn.executeUpdateDB(updateComm);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// throw new GamaRuntimeException("SQLSkill.exexuteUpdateDB:" + e.toString());
	// }
	//
	// if ( DEBUG ) {
	// GuiUtils.informConsole(updateComm + " was run");
	// }
	//
	// return n;
	//
	// }

	/*
	 * - Make a connection to BDMS
	 * - Executes the SQL statement in this PreparedStatement object, which must be an SQL INSERT,
	 * UPDATE or DELETE statement; or an SQL statement that returns nothing, such as a DDL
	 * statement.
	 * 
	 * @syntax: do action: executeUpdate {
	 * arg params value:[
	 * "dbtype":"MSSQL",
	 * "url":"host address",
	 * "port":"port number",
	 * "database":"database name",
	 * "user": "user name",
	 * "passwd": "password",
	 * ],
	 * arg updateComm value: " SQL statement string with question marks"
	 * arg values value [List of values that are used to replace question marks]
	 * }
	 */
	@action(name = "executeUpdate", args = {
		@arg(name = "params", type = IType.MAP_STR, optional = false, doc = @doc("Connection parameters")),
		@arg(name = "updateComm", type = IType.STRING_STR, optional = false, doc = @doc("SQL commands such as Create, Update, Delete, Drop with question mark")),
		@arg(name = "values", type = IType.LIST_STR, optional = true, doc = @doc("List of values that are used to replace question mark")) })
	public int executeUpdate_QM(final IScope scope) throws GamaRuntimeException {
		java.util.Map params = (java.util.Map) scope.getArg("params", IType.MAP);
		String updateComm = (String) scope.getArg("updateComm", IType.STRING);
		GamaList<Object> values = (GamaList<Object>) scope.getArg("values", IType.LIST);
		String dbtype = (String) params.get("dbtype");
		String host = (String) params.get("host");
		String port = (String) params.get("port");
		String database = (String) params.get("database");
		String user = (String) params.get("user");
		String passwd = (String) params.get("passwd");
		SqlConnection sqlConn;
		int row_count = -1;
		// create connection
		if ( dbtype.equalsIgnoreCase(SqlConnection.SQLITE) ) {
			String DBRelativeLocation =
				scope.getSimulationScope().getModel().getRelativeFilePath(database, true);
			sqlConn = new SqlConnection(dbtype, DBRelativeLocation);
		} else {
			sqlConn = new SqlConnection(dbtype, host, port, database, user, passwd);
		}

		// get data
		try {
			if ( values.size() > 0 ) {
				row_count = sqlConn.executeUpdateDB(updateComm, values);
			} else {
				row_count = sqlConn.executeUpdateDB(updateComm);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GamaRuntimeException("SQLSkill.executeUpdateDB: " + e.toString());
		}
		if ( DEBUG ) {
			GuiUtils.debug(updateComm + " was run");
		}

		return row_count;

	}

	/*
	 * Make a connection to BDMS and execute the insert statement
	 * 
	 * @syntax do insert with: [into:: table_name, columns:column_list, values:value_list];
	 * 
	 * @return an integer
	 */
	@action(name = "insert", args = {
		@arg(name = "params", type = IType.MAP_STR, optional = false, doc = @doc("Connection parameters")),
		@arg(name = "into", type = IType.STRING_STR, optional = false, doc = @doc("Table name")),
		@arg(name = "columns", type = IType.LIST_STR, optional = true, doc = @doc("List of column name of table")),
		@arg(name = "values", type = IType.LIST_STR, optional = false, doc = @doc("List of values that are used to insert into table. Columns and values must have same size")),
		@arg(name = "transform", type = IType.BOOL_STR, optional = true, doc = @doc("if transform = true then geometry will be tranformed from absolute to gis otherways it will be not transformed. Default value is false ")) })
	public int insert(final IScope scope) throws GamaRuntimeException {
		java.util.Map params = (java.util.Map) scope.getArg("params", IType.MAP);
		String table_name = (String) scope.getArg("into", IType.STRING);
		GamaList<Object> cols = (GamaList<Object>) scope.getArg("columns", IType.LIST);
		GamaList<Object> values = (GamaList<Object>) scope.getArg("values", IType.LIST);
		// String tranformStr = (String) scope.getArg("transform", IType.BOOL);
		// Boolean transform = ( (tranformStr != null) ? Boolean.parseBoolean(tranformStr) : false);
		Boolean transform =
			scope.hasArg("transform") ? (Boolean) scope.getArg("transform", IType.BOOL) : false;
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
			if ( DEBUG ) {
				GuiUtils.debug("database sqlite:" + DBRelativeLocation);
			}
			sqlConn = new SqlConnection(dbtype, DBRelativeLocation, transform);
		} else {
			sqlConn = new SqlConnection(dbtype, host, port, database, user, passwd, transform);
		}

		int rec_no = -1;

		try {
			// Connection conn=sqlConn.connectDB();
			if ( cols.size() > 0 ) {
				rec_no = sqlConn.insertDB(scope, table_name, cols, values);
			} else {
				rec_no = sqlConn.insertDB(scope, table_name, values);
			}
			// conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new GamaRuntimeException("SQLSkill.insert: " + e.toString());
		}
		if ( DEBUG ) {
			GuiUtils.debug("Insert into " + " was run");
		}

		return rec_no;
	}

	/*
	 * Make a connection to BDMS and execute the insert statement
	 * 
	 * @syntax do insert with: [into:: table_name, values:value_list];
	 * 
	 * @return an integer
	 */
	// @action(name="insert_All",
	// args = {
	// @arg(name = "params", type = IType.MAP_STR, optional = false, doc =
	// @doc("Connection parameters")),
	// @arg(name = "into", type = IType.STRING_STR, optional = false, doc = @doc("Table name")),
	// @arg(name = "values", type = IType.LIST_STR, optional = false, doc =
	// @doc("List of values that are used to insert into table. Columns and values must have same size"))
	// })
	// public int insert_All(final IScope scope) throws GamaRuntimeException
	// {
	// java.util.Map params = (java.util.Map) scope.getArg("params", IType.MAP);
	// String table_name = (String) scope.getArg("into", IType.STRING);
	// GamaList<Object> values =(GamaList<Object>) scope.getArg("values", IType.LIST);
	// String dbtype = (String) params.get("dbtype");
	// String host = (String)params.get("host");
	// String port = (String)params.get("port");
	// String database = (String) params.get("database");
	// String user = (String) params.get("user");
	// String passwd = (String)params.get("passwd");
	// SqlConnection sqlConn;
	// // create connection
	// if (dbtype.equalsIgnoreCase(SqlConnection.SQLITE)){
	// String DBRelativeLocation =
	// scope.getSimulationScope().getModel().getRelativeFilePath(database, true);
	// System.out.println("database sqlite:"+DBRelativeLocation);
	// //sqlConn=new SqlConnection(dbtype,database);
	// sqlConn=new SqlConnection(dbtype,DBRelativeLocation);
	// }else{
	// sqlConn=new SqlConnection(dbtype,host,port,database,user,passwd);
	// }
	//
	// int rec_no=-1;
	//
	// try{
	// //Connection conn=sqlConn.connectDB();
	// rec_no = sqlConn.insertDB(table_name,values);
	// //conn.close();
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new GamaRuntimeException("SQLSkill.insert: " + e.toString());
	// }
	// if ( DEBUG ) {
	// GuiUtils.informConsole("Insert into " + " was run");
	// }
	//
	// return rec_no;
	//
	// }

	/*
	 * Make a connection to BDMS and execute the select statement
	 * 
	 * @syntax do action:
	 * select {
	 * arg params value:[
	 * "dbtype":"SQLSERVER",
	 * "url":"host address",
	 * "port":"port number",
	 * "database":"database name",
	 * "user": "user name",
	 * "passwd": "password",
	 * ];
	 * arg select value: "select string"
	 * }
	 * 
	 * @return GamaList<GamaList<Object>>
	 */
	// @action(name="select",
	// args = {
	// @arg(name = "params", type = IType.MAP_STR, optional = false, doc =
	// @doc("Connection parameters")),
	// @arg(name = "select", type = IType.STRING_STR, optional = false, doc =
	// @doc("select command"))
	// })
	// public GamaList<Object> select(final IScope scope) throws GamaRuntimeException
	// {
	// java.util.Map params = (java.util.Map) scope.getArg("params", IType.MAP);
	// String selectComm = (String) scope.getArg("select", IType.STRING);
	// String dbtype = (String) params.get("dbtype");
	// String host = (String)params.get("host");
	// String port = (String)params.get("port");
	// String database = (String) params.get("database");
	// String user = (String) params.get("user");
	// String passwd = (String)params.get("passwd");
	// SqlConnection sqlConn;
	// GamaList<Object> repRequest = new GamaList<Object>();
	// // create connection
	// if (dbtype.equalsIgnoreCase(SqlConnection.SQLITE)){
	// String DBRelativeLocation =
	// scope.getSimulationScope().getModel().getRelativeFilePath(database, true);
	//
	// //sqlConn=new SqlConnection(dbtype,database);
	// sqlConn=new SqlConnection(dbtype,DBRelativeLocation);
	// }else{
	// sqlConn=new SqlConnection(dbtype,host,port,database,user,passwd);
	// }
	//
	// // get data
	// try{
	// //repRequest= sqlcon.selectDB((String) params.get("select"));
	// repRequest = sqlConn.selectDB(selectComm);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// throw new GamaRuntimeException("SQLSkill.select: " + e.toString());
	// }
	// if ( DEBUG ) {
	// GuiUtils.informConsole(selectComm + " was run");
	// }
	//
	// return repRequest;
	//
	// }

	/*
	 * Make a connection to BDMS and execute the select statement
	 * 
	 * @syntax do action:
	 * select {
	 * arg params value:[
	 * "dbtype":"SQLSERVER",
	 * "url":"host address",
	 * "port":"port number",
	 * "database":"database name",
	 * "user": "user name",
	 * "passwd": "password"
	 * ];
	 * arg select value: "select string with question marks";
	 * arg values value [List of values that are used to replace question marks]
	 * }
	 * 
	 * @return GamaList<GamaList<Object>>
	 */
	@action(name = "select", args = {
		@arg(name = "params", type = IType.MAP_STR, optional = false, doc = @doc("Connection parameters")),
		@arg(name = "select", type = IType.STRING_STR, optional = false, doc = @doc("select string with question marks")),
		@arg(name = "values", type = IType.LIST_STR, optional = true, doc = @doc("List of values that are used to replace question marks")),
		@arg(name = "transform", type = IType.BOOL_STR, optional = true, doc = @doc("if transform = true then geometry will be tranformed from absolute to gis otherways it will be not transformed. Default value is false "))

	})
	public GamaList<Object> select_QM(final IScope scope) throws GamaRuntimeException {
		java.util.Map params = (java.util.Map) scope.getArg("params", IType.MAP);
		String selectComm = (String) scope.getArg("select", IType.STRING);
		GamaList<Object> values = (GamaList<Object>) scope.getArg("values", IType.LIST);
		Boolean transform =
			scope.hasArg("transform") ? (Boolean) scope.getArg("transform", IType.BOOL) : false;
		String dbtype = (String) params.get("dbtype");
		String host = (String) params.get("host");
		String port = (String) params.get("port");
		String database = (String) params.get("database");
		String user = (String) params.get("user");
		String passwd = (String) params.get("passwd");
		SqlConnection sqlConn;
		GamaList<Object> repRequest = new GamaList<Object>();
		// create connection
		if ( dbtype.equalsIgnoreCase(SqlConnection.SQLITE) ) {
			String DBRelativeLocation =
				scope.getSimulationScope().getModel().getRelativeFilePath(database, true);
			sqlConn = new SqlConnection(dbtype, DBRelativeLocation);
		} else {
			sqlConn = new SqlConnection(dbtype, host, port, database, user, passwd);
		}

		// get data
		try {
			if ( values.size() > 0 ) {
				repRequest = sqlConn.executeQueryDB(selectComm, values);
			} else {
				repRequest = sqlConn.selectDB(selectComm);
			}
			// Transform GIS to Absolute (Geometry in GAMA)
			if ( transform ) {
				return sqlConn.fromGisToAbsolute(scope, repRequest);
			} else {
				return repRequest;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GamaRuntimeException("SQLSkill.select_QM: " + e.toString());
		}

	}

	@action(name = "list2Matrix", args = {
		@arg(name = "param", type = IType.LIST_STR, optional = false, doc = @doc(value = "Param: a list of records and metadata")),
		@arg(name = "getName", type = IType.BOOL_STR, optional = true, doc = @doc(value = "getType: a boolean value, optional parameter", comment = "if it is true then the action will return columnNames and data. default is true")),
		@arg(name = "getType", type = IType.BOOL_STR, optional = true, doc = @doc(value = "getType: a boolean value, optional parameter", comment = "if it is true then the action will return columnTypes and data. default is false"))

	})
	public IMatrix List2matrix(final IScope scope) throws GamaRuntimeException {
		try {
			boolean getName =
				scope.hasArg("getName") ? (Boolean) scope.getArg("getName", IType.BOOL) : true;
			boolean getType =
				scope.hasArg("getType") ? (Boolean) scope.getArg("getType", IType.BOOL) : false;
			GamaList<Object> value = (GamaList<Object>) scope.getArg("param", IType.LIST);
			GamaList<Object> columnNames = (GamaList<Object>) value.get(0);
			GamaList<Object> columnTypes = (GamaList<Object>) value.get(1);
			GamaList<Object> records = (GamaList<Object>) value.get(2);
			int columnSize = columnNames.size();
			int lineSize = records.size();

			final IMatrix matrix =
				new GamaObjectMatrix(scope, columnSize, lineSize + (getType ? 1 : 0) +
					(getName ? 1 : 0));
			// Add ColumnNames to Matrix
			if ( getName == true ) {
				for ( int j = 0; j < columnSize; j++ ) {
					matrix.set(scope, j, 0, columnNames.get(j));
				}
			}
			// Add Columntype to Matrix
			if ( getType == true ) {
				for ( int j = 0; j < columnSize; j++ ) {
					matrix.set(scope, j, 0 + (getName ? 1 : 0), columnTypes.get(j));
				}
			}
			// Add Records to Matrix
			for ( int i = 0; i < lineSize; i++ ) {
				GamaList<Object> record = (GamaList<Object>) records.get(i);
				for ( int j = 0; j < columnSize; j++ ) {
					matrix.set(scope, j, i + (getType ? 1 : 0) + (getName ? 1 : 0), record.get(j));
				}
			}
			return matrix;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
