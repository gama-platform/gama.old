/*******************************************************************************************************
 *
 * SQLSkill.java, in irit.gaml.extensions.database, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package irit.gaml.extensions.database.gaml.skills;

import java.sql.Connection;

import irit.gaml.extensions.database.utils.sql.SqlConnection;
import irit.gaml.extensions.database.utils.sql.SqlUtils;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.example;

import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gama.util.matrix.GamaObjectMatrix;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class SQLSkill.
 */
/*
 * @Author TRUONG Minh Thai
 *
 * @Supervisors: Christophe Sibertin-BLANC Fredric AMBLARD Benoit GAUDOU
 *
 *
 * created date: 22-Feb-2012 Modified: 24-Sep-2012: Add methods: - boolean isconnected() - select(String select) -
 * executeUpdate(String updateComm) - getParameter: return connection Parameter; Delete method: selectDB,
 * executeUpdateDB 25-Sep-2012: Add methods: timeStamp, helloWorld 18-Feb-2013: Add public int insert(final IScope
 * scope) throws GamaRuntimeException 21-Feb-2013: Modify public IList<Object> select(final IScope scope) throws
 * GamaRuntimeException Modify public int executeUpdate(final IScope scope) throws GamaRuntimeException Modify public
 * int insert(final IScope scope) throws GamaRuntimeException 10-Mar-2013: Modify select method: Add transform parameter
 * Modify insert method: Add transform parameter 29-Apr-2013 Remove import msi.gama.database.SqlConnection; Add import
 * msi.gama.database.sql.SqlConnection; Change all method appropriately 07-Jan-2014: Move arg "transform" of select and
 * insert action as key of arg "Param" 
 * 01-Aug-2014: Add date time functions: getCurrentDateTime: get system datetime
 * getDateOffset: get (datetime + offsettime) 
 * 21-Sept-2022 : remove getCurrentDateTime, timeStamp and getDateOffset as they are irrelevant to databases
 * Last Modified: 01-Aug-2014
 */
@skill (
		name = "SQLSKILL",
		concept = { IConcept.DATABASE, IConcept.SKILL })
@SuppressWarnings ({ "rawtypes", "unchecked" })
@doc ("This skill allows agents to be provided with actions and attributes in order to connect to SQL databases")
public class SQLSkill extends Skill {

	/**
	 * Test connection.
	 *
	 * @param scope the scope
	 * @return true, if successful
	 */
	/*
	 * Make a connection to BDMS
	 *
	 * @syntax: do action: connectDB { arg params value:[ "dbtype":"SQLSERVER", //MySQL/sqlserver/sqlite
	 * "url":"host address", "port":"port number", "database":"database name", "user": "user name", "passwd": "password"
	 * ]; }
	 */
	@action (
			name = "testConnection",
			args = { @arg (
					name = "params",
					type = IType.MAP,
					optional = false,
					doc = @doc ("Connection parameters")) },
			doc = @doc(
					value ="Action used to test the connection to a database",
					examples = {
						@example("if (!first(DB_Accessor).testConnection(PARAMS)) {\r\n"
								+ "			write \"Connection impossible\";\r\n"
								+ "			do pause;\r\n"
								+ "		}\r\n"
								+ "")})			)
	public boolean testConnection(final IScope scope) {

		try (final Connection conn = SqlUtils.createConnectionObject(scope).connectDB()) {			
		} catch (final Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Execute update QM.
	 *
	 * @param scope the scope
	 * @return the int
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	/*
	 * - Make a connection to BDMS - Executes the SQL statement in this PreparedStatement object, which must be an SQL
	 * INSERT, UPDATE or DELETE statement; or an SQL statement that returns nothing, such as a DDL statement.
	 *
	 * @syntax: do action: executeUpdate { arg params value:[ "dbtype":"MSSQL", "url":"host address",
	 * "port":"port number", "database":"database name", "user": "user name", "passwd": "password", ], arg updateComm
	 * value: " SQL statement string with question marks" arg values value [List of values that are used to replace
	 * question marks] }
	 */
	@action (
			name = "executeUpdate",
			args = { @arg (
					name = "params",
					type = IType.MAP,
					optional = false,
					doc = @doc ("Connection parameters")),
					@arg (
							name = "updateComm",
							type = IType.STRING,
							optional = false,
							doc = @doc ("SQL commands such as Create, Update, Delete, Drop with question mark")),
					@arg (
							name = "values",
							type = IType.LIST,
							optional = true,
							doc = @doc ("List of values that are used to replace question mark")) }, 			
			doc = @doc(
					value ="Action used to execute any update query (CREATE, DROP, INSERT...) to the database (query written in SQL).",
					examples = {
						@example("do executeUpdate params: PARAMS updateComm: \"DROP TABLE IF EXISTS registration\";"),
						@example("do executeUpdate params: PARAMS updateComm: \"INSERT INTO registration \" + \"VALUES(100, 'Zara', 'Ali', 18);\";"),
						@example("do executeUpdate params: PARAMS updateComm: \"INSERT INTO registration \" + \"VALUES(?, ?, ?, ?);\" values: [101, 'Mr', 'Mme', 45];")}))
	public int executeUpdate_QM(final IScope scope) throws GamaRuntimeException {

		final String updateComm = (String) scope.getArg("updateComm", IType.STRING);
		final IList<Object> values = (IList<Object>) scope.getArg("values", IType.LIST);
		int row_count = -1;
		try (final SqlConnection sqlConn = SqlUtils.createConnectionObject(scope); ) {
			if (values.size() > 0) {
				row_count = sqlConn.executeUpdateDB(scope, updateComm, values);
			} else {
				row_count = sqlConn.executeUpdateDB(scope, updateComm);
			}

		} catch (final Exception e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLSkill.executeUpdateDB: " + e.toString(), scope);
		}
		DEBUG.OUT(updateComm + " was run");

		return row_count;
		// ------------------------------------------------------------------------------------------

	}

	/**
	 * Insert.
	 *
	 * @param scope the scope
	 * @return the int
	 * @throws GamaRuntimeException the gama runtime exception
	 */
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
					name = "params",
					type = IType.MAP,
					optional = false,
					doc = @doc ("Connection parameters")),
					@arg (
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
			},
			doc = @doc(
					value ="Action used to insert new data in a database",
					examples = {
						@example("do insert params: PARAMS into: \"registration\" values: [102, 'Mahnaz', 'Fatma', 25];"),
						@example("do insert params: PARAMS into: \"registration\" columns: [\"id\", \"first\", \"last\"] values: [103, 'Zaid tim', 'Kha'];")}))
	public int insert(final IScope scope) throws GamaRuntimeException {

		final String table_name = (String) scope.getArg("into", IType.STRING);
		final IList<Object> cols = (IList<Object>) scope.getArg("columns", IType.LIST);
		final IList<Object> values = (IList<Object>) scope.getArg("values", IType.LIST);
		int rec_no = -1;
		try (final SqlConnection sqlConn = SqlUtils.createConnectionObject(scope); ) {
			if (cols.size() > 0) {
				rec_no = sqlConn.insertDB(scope, table_name, cols, values);
			} else {
				rec_no = sqlConn.insertDB(scope, table_name, values);
			}
		} catch (final Exception e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLSkill.insert: " + e.toString(), scope);
		}
		DEBUG.OUT("Insert into " + " was run");

		return rec_no;
		// ------------------------------------------------------------------------------------------
	}

	/**
	 * Select QM.
	 *
	 * @param scope the scope
	 * @return the i list
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	/*
	 * Make a connection to BDMS and execute the select statement
	 *
	 * @syntax do action: select { arg params value:[ "dbtype":"SQLSERVER", "url":"host address", "port":"port number",
	 * "database":"database name", "user": "user name", "passwd": "password" ]; arg select value:
	 * "select string with question marks"; arg values value [List of values that are used to replace question marks] }
	 *
	 * @return IList<IList<Object>>
	 */
	@action (
			name = "select",
			args = { @arg ( 
						name = "params", type = IType.MAP, optional = false,
						doc = @doc ("Connection parameters")),
					@arg (
						name = "select", type = IType.STRING, optional = false,
						doc = @doc ("select string with question marks")),
					@arg (
						name = "values", type = IType.LIST, optional = true,
						doc = @doc ("List of values that are used to replace question marks"))},
			doc = @doc(
					value ="Action used to restrieve data from a database",
					examples = {
						@example("list<list> t <- list<list> (select(PARAMS, \"SELECT * FROM registration\"));")}))
	public IList select_QM(final IScope scope) throws GamaRuntimeException {

		final String selectComm = (String) scope.getArg("select", IType.STRING);
		final IList<Object> values = (IList<Object>) scope.getArg("values", IType.LIST);

		IList<? super IList<Object>> repRequest;
		try (final SqlConnection sqlConn = SqlUtils.createConnectionObject(scope); ) {
			if (values.size() > 0) {
				repRequest = sqlConn.executeQueryDB(scope, selectComm, values);
			} else {
				repRequest = sqlConn.selectDB(scope, selectComm);
			}
			return repRequest;
		} catch (final Exception e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLSkill.select_QM: " + e.toString(), scope);
		}

		// ------------------------------------------------------------------------------------------

	}

	/**
	 * List 2 matrix.
	 *
	 * @param scope the scope
	 * @return the i matrix
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	@action (
			name = "list2Matrix",
			args = { @arg (
					name = "param",
					type = IType.LIST,
					optional = false,
					doc = @doc (
							value = "Param: a list of records and metadata")),
					@arg (
							name = "getName",
							type = IType.BOOL,
							optional = true,
							doc = @doc (
									value = "getType: a boolean value, optional parameter",
									comment = "if it is true then the action will return columnNames and data. default is true")),
					@arg (
							name = "getType",
							type = IType.BOOL,
							optional = true,
							doc = @doc (
									value = "getType: a boolean value, optional parameter",
									comment = "if it is true then the action will return columnTypes and data. default is false"))},
			doc = @doc(
				value = "Action that transforms the list of list of data and metadata (resulting from a query) into a matrix.",
				examples = {@example("list<list> t <- list<list> (select(PARAMS, \"SELECT * FROM registration\"));\r\n"
									+ "write list2Matrix(t, true, true);")}))
	public IMatrix List2matrix(final IScope scope) throws GamaRuntimeException {
		try {
			final boolean getName = scope.hasArg("getName") ? (Boolean) scope.getArg("getName", IType.BOOL) : true;
			final boolean getType = scope.hasArg("getType") ? (Boolean) scope.getArg("getType", IType.BOOL) : false;
			final IList<Object> value = (IList<Object>) scope.getArg("param", IType.LIST);
			final IList<Object> columnNames = (IList<Object>) value.get(0);
			final IList<Object> columnTypes = (IList<Object>) value.get(1);
			final IList<Object> records = (IList<Object>) value.get(2);
			final int columnSize = columnNames.size();
			final int lineSize = records.size();

			final IMatrix matrix =
					new GamaObjectMatrix(columnSize, lineSize + (getType ? 1 : 0) + (getName ? 1 : 0), Types.NO_TYPE);
			// Add ColumnNames to Matrix
			if (getName == true) {
				for (int j = 0; j < columnSize; j++) {
					matrix.set(scope, j, 0, columnNames.get(j));
				}
			}
			// Add Columntype to Matrix
			if (getType == true) {
				for (int j = 0; j < columnSize; j++) {
					matrix.set(scope, j, 0 + (getName ? 1 : 0), columnTypes.get(j));
				}
			}
			// Add Records to Matrix
			for (int i = 0; i < lineSize; i++) {
				final IList<Object> record = (IList<Object>) records.get(i);
				for (int j = 0; j < columnSize; j++) {
					matrix.set(scope, j, i + (getType ? 1 : 0) + (getName ? 1 : 0), record.get(j));
				}
			}
			return matrix;
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
