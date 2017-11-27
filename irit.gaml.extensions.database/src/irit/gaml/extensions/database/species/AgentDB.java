/*********************************************************************************************
 *
 *
 * 'AgentDB.java', in plugin 'irit.gaml.extensions.database', is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package irit.gaml.extensions.database.species;

import java.sql.Connection;
import java.sql.SQLException;

import msi.gama.database.sql.SqlConnection;
import msi.gama.database.sql.SqlUtils;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.types.IType;

/*
 * @Author TRUONG Minh Thai
 *
 * @Supervisors: Christophe Sibertin-BLANC Fredric AMBLARD Benoit GAUDOU
 *
 * species: The AgentDB is defined in this class. AgentDB supports the action - isConnected: returns true/false -
 * testConnection: tests the connection - close: closes the current connection - connect: makes a connection to DBMS. -
 * select: executeQuery to select data from DBMS via current connection. - executeUpdate: runs executeUpdate to
 * update/insert/delete/drop/create data on DBMS via current connection.
 *
 * created date: 22-Feb-2012 Modified: 24-Sep-2012: Add methods: - boolean isconnected() - select(String select) -
 * executeUpdate(String updateComm) - getParameter: return connection Parameter; Delete method: selectDB,
 * executeUpdateDB 25-Sep-2012: Add methods: timeStamp, helloWorld 18-Feb-2013: Add public int insert(final IScope
 * scope) throws GamaRuntimeException 21-Feb-2013: Modify public GamaList<Object> select(final IScope scope) throws
 * GamaRuntimeException Modify public int executeUpdate(final IScope scope) throws GamaRuntimeException Modify public
 * int insert(final IScope scope) throws GamaRuntimeException 10-Mar-2013: Modify select method: Add transform parameter
 * Modify insert method: Add transform parameter 29-Apr-2013: Remove import msi.gama.database.SqlConnection; Add import
 * msi.gama.database.sql.SqlConnection; Change all method appropriately 07-Jan-2014: Move arg "transform" of select and
 * insert action to key of arg "Params" Last Modified: 07-Jan-2014
 */
@species (
		name = "AgentDB",
		doc = @doc ("An abstract species that can be extended to provide agents with capabilities to access databases"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class AgentDB extends GamlAgent {

	private Connection conn = null;
	private SqlConnection sqlConn = null;
	private boolean isConnection = false;
	private java.util.Map<String, String> params = null;
	static final boolean DEBUG = false; // Change DEBUG = false for release
										// version

	public AgentDB(final IPopulation s) throws GamaRuntimeException {
		super(s);
	}

	@action (
			name = "isConnected")
	public boolean isConnected(final IScope scope) throws GamaRuntimeException {
		return isConnection;
	}

	@action (
			name = "close")
	public Object close(final IScope scope) throws GamaRuntimeException {
		try {
			conn.close();
			isConnection = false;
		} catch (final SQLException e) {
			// e.printStackTrace();
			throw GamaRuntimeException.error("AgentDB.close error:" + e.toString(), scope);
		}
		return null;

	}

	// @action(name = "helloWorld")
	// public Object helloWorld(final IScope scope) throws GamaRuntimeException
	// {
	// scope.getGui().informConsole("Hello World");
	// return null;
	// }

	// Get current time of system
	// added from MaeliaSkill
	@action (
			name = "timeStamp")
	public Long timeStamp(final IScope scope) throws GamaRuntimeException {
		final Long timeStamp = System.currentTimeMillis();
		return timeStamp;
	}

	/*
	 * Make a connection to BDMS
	 *
	 * @syntax: do action: connectDB { arg params value:[ "dbtype":"SQLSERVER", "url":"host address", "port":
	 * "port number", "database":"database name", "user": "user name", "passwd": "password" ]; }
	 */
	@action (
			name = "connect",
			args = { @arg (
					name = "params",
					type = IType.MAP,
					optional = false,
					doc = @doc ("Connection parameters")) })
	public Object connectDB(final IScope scope) throws GamaRuntimeException {

		params = (java.util.Map<String, String>) scope.getArg("params", IType.MAP);

		final String dbtype = params.get("dbtype");

		// SqlConnection sqlConn;
		if (dbtype.equalsIgnoreCase(SqlConnection.SQLITE)) { throw GamaRuntimeException.error(
				"AgentDB.connection to SQLite error: an AgentDB agent cannot connect to SQLite DBMS (cf. documentation for further info).",
				scope); }
		if (isConnection) { throw GamaRuntimeException.error("AgentDB.connection error: a connection is already opened",
				scope); }
		try {
			sqlConn = SqlUtils.createConnectionObject(scope);
			conn = sqlConn.connectDB();
			isConnection = true;
		} catch (final Exception e) {
			throw GamaRuntimeException.error("AgentDB.connect:" + e.toString(), scope);
		}
		return null;
		// ----------------------------------------------------------------------------------------------------------
	}

	/*
	 * Make a connection to BDMS
	 *
	 * @syntax: do action: connectDB { arg params value:[ "dbtype":"SQLSERVER", "url":"host address", "port":
	 * "port number", "database":"database name", "user": "user name", "passwd": "password", ]; }
	 */
	@action (
			name = "testConnection",
			args = { @arg (
					name = "params",
					type = IType.MAP,
					optional = false,
					doc = @doc ("Connection parameters")) })
	public boolean testConnection(final IScope scope) throws GamaRuntimeException {
		try {
			SqlConnection sqlConn;
			sqlConn = SqlUtils.createConnectionObject(scope);
			final Connection conn = sqlConn.connectDB();
			conn.close();
		} catch (final Exception e) {
			return false;
		}
		return true;
		// ---------------------------------------------------------------------------------------
	}

	/*
	 * Make a connection to BDMS and execute the select statement
	 *
	 * @syntax do action: select { arg select value: "select string with question marks"; arg values value [List of
	 * values that are used to replace question marks] }
	 *
	 * @return GamaList<GamaList<Object>>
	 */
	@action (
			name = "select",
			args = { @arg (
					name = "select",
					type = IType.STRING,
					optional = false,
					doc = @doc ("select string")),
					@arg (
							name = "values",
							type = IType.LIST,
							optional = true,
							doc = @doc ("List of values that are used to replace question marks"))
			// , @arg(name = "transform", type = IType.BOOL, optional = true,
			// doc =
			// @doc("if transform = true then geometry will be tranformed from
			// absolute to gis otherways it will be not transformed. Default
			// value is false "))
			})
	public IList select(final IScope scope) throws GamaRuntimeException {

		if (!isConnection) { throw GamaRuntimeException.error("AgentDB.select: Connection was not established ",
				scope); }
		final String selectComm = (String) scope.getArg("select", IType.STRING);
		final IList<Object> values = (IList<Object>) scope.getArg("values", IType.LIST);
		// Boolean transform = scope.hasArg("transform") ? (Boolean)
		// scope.getArg("transform", IType.BOOL) : false;
		IList<? super IList<? super IList>> repRequest = GamaListFactory.create(msi.gaml.types.Types.LIST);
		// get data
		try {
			if (values.size() > 0) {
				repRequest = sqlConn.executeQueryDB(scope, conn, selectComm, values);
			} else {
				repRequest = sqlConn.selectDB(scope, conn, selectComm);
			}
			// if ( transform ) { return sqlConn.fromGisToAbsolute(gis,
			// repRequest); }
			return repRequest;
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("AgentDB.select: " + e.toString(), scope);
		}
		// --------------------------------------------------------------------------------------------------

	}

	/*
	 * - Make a connection to BDMS - Executes the SQL statement in this PreparedStatement object, which must be an SQL
	 * INSERT, UPDATE or DELETE statement; or an SQL statement that returns nothing, such as a DDL statement.
	 *
	 * @syntax: do action: executeUpdate { arg updateComm value: " SQL statement string with question marks" arg values
	 * value [List of values that are used to replace question marks] }
	 */
	@action (
			name = "executeUpdate",
			args = { @arg (
					name = "updateComm",
					type = IType.STRING,
					optional = false,
					doc = @doc ("SQL commands such as Create, Update, Delete, Drop with question mark")),
					@arg (
							name = "values",
							type = IType.LIST,
							optional = true,
							doc = @doc ("List of values that are used to replace question mark"))
			// , @arg(name = "transform", type = IType.BOOL, optional = true,
			// doc =
			// @doc("if transform = true then geometry will be tranformed from
			// absolute to gis otherways it will be not transformed. Default
			// value is false "))
			})
	public int executeUpdate(final IScope scope) throws GamaRuntimeException {

		if (!isConnection) { throw GamaRuntimeException.error("AgentDB.select: Connection was not established ",
				scope); }
		final String updateComm = (String) scope.getArg("updateComm", IType.STRING);
		final GamaList<Object> values = (GamaList<Object>) scope.getArg("values", IType.LIST);

		int row_count = -1;
		// get data
		try {
			if (values.size() > 0) {
				row_count = sqlConn.executeUpdateDB(scope, conn, updateComm, values);
			} else {
				row_count = sqlConn.executeUpdateDB(scope, conn, updateComm);
			}

		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("AgentDB.executeUpdate: " + e.toString(), scope);
		}
		if (DEBUG) {
			scope.getGui().getConsole(scope).informConsole(updateComm + " was run", scope.getRoot());
		}

		return row_count;
		// ----------------------------------------------------------------------------------------------------
	}

	@action (
			name = "getParameter",
			args = {})
	public Object getParamater(final IScope scope) throws GamaRuntimeException {
		return params;
	}

	@action (
			name = "setParameter",
			args = { @arg (
					name = "params",
					type = IType.MAP,
					optional = false,
					doc = @doc ("Connection parameters")) })
	public Object setParameter(final IScope scope) throws GamaRuntimeException {
		params = (java.util.Map<String, String>) scope.getArg("params", IType.MAP);

		if (isConnection) {
			try {
				conn.close();
				isConnection = false;
			} catch (final SQLException e) {
				// e.printStackTrace();
				throw GamaRuntimeException.error("AgentDB.close error:" + e.toString(), scope);
			}
		}
		return null;
	}

	/*
	 * Make a connection to BDMS and execute the insert statement
	 *
	 * @syntax do insert with: [into:: table_name, columns:column_list, values:value_list];
	 *
	 * @return an integer
	 */
	@action (
			name = "insert",
			args = { @arg (
					name = "into",
					type = IType.STRING,
					optional = false,
					doc = @doc ("Table name")),
					@arg (
							name = "columns",
							type = IType.LIST,
							optional = true,
							doc = @doc ("List of column name of table")),
					@arg (
							name = "values",
							type = IType.LIST,
							optional = false,
							doc = @doc ("List of values that are used to insert into table. Columns and values must have same size"))
			// ,@arg(name = "transform", type = IType.BOOL, optional = true, doc
			// =
			// @doc("if transform = true then geometry will be tranformed from
			// absolute to gis otherways it will be not transformed. Default
			// value is false "))
			})
	public int insert(final IScope scope) throws GamaRuntimeException {

		if (!isConnection) { throw GamaRuntimeException.error("AgentDB.select: Connection was not established ",
				scope); }
		final String table_name = (String) scope.getArg("into", IType.STRING);
		final GamaList<Object> cols = (GamaList<Object>) scope.getArg("columns", IType.LIST);
		final GamaList<Object> values = (GamaList<Object>) scope.getArg("values", IType.LIST);
		// thai.truongminh@gmail.com
		// Move transform arg of select to a key in params
		// boolean transform = scope.hasArg("transform") ? (Boolean)
		// scope.getArg("transform", IType.BOOL) : true;
		// boolean transform = params.containsKey("transform") ? (Boolean)
		// params.get("transform") : true;
		int rec_no = -1;

		try {
			if (cols.size() > 0) {
				// rec_no = sqlConn.insertDB(scope, conn, table_name, cols,
				// values, transform);
				rec_no = sqlConn.insertDB(scope, conn, table_name, cols, values);
			} else {
				// rec_no = sqlConn.insertDB(scope, table_name, values,
				// transform);
				rec_no = sqlConn.insertDB(scope, conn, table_name, values);
			}
		} catch (final Exception e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("AgentDB.insert: " + e.toString(), scope);
		}
		if (DEBUG) {
			scope.getGui().getConsole(scope).informConsole("Insert into " + " was run", scope.getRoot());
		}

		return rec_no;
	}
	// -----------------------------------------------------------------------------------------------------
}
