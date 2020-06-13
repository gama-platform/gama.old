/*********************************************************************************************
 *
 *
 * 'SQLSkill.java', in plugin 'irit.gaml.extensions.database', is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package irit.gaml.extensions.database.skills;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import msi.gama.database.sql.SqlConnection;
import msi.gama.database.sql.SqlUtils;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;
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
 * insert action as key of arg "Param" 01-Aug-2014: Add date time functions: getCurrentDateTime: get system datetime
 * getDateOffset: get (datetime + offsettime) Last Modified: 01-Aug-2014
 */
@skill (
		name = "SQLSKILL",
		concept = { IConcept.DATABASE, IConcept.SKILL })
@SuppressWarnings ({ "rawtypes", "unchecked" })
@doc ("This skill allows agents to be provided with actions and attributes in order to connect to SQL databases")
public class SQLSkill extends Skill {

	// Get current time of system
	// added from MaeliaSkill
	@action (
			name = "timeStamp")
	public Long timeStamp(final IScope scope) throws GamaRuntimeException {
		final Long timeStamp = System.currentTimeMillis();
		return timeStamp;
	}

	// Get current time of system
	@action (
			name = "getCurrentDateTime",
			args = { @arg (
					name = "dateFormat",
					type = IType.STRING,
					optional = false,
					doc = @doc ("date format examples: 'yyyy-MM-dd' , 'yyyy-MM-dd HH:mm:ss' ")) })
	public String getCurrentDateTime(final IScope scope) throws GamaRuntimeException {
		final String dateFormat = (String) scope.getArg("dateFormat", IType.STRING);
		final DateFormat datef = new SimpleDateFormat(dateFormat);
		final Calendar c = Calendar.getInstance();
		return datef.format(c.getTime());
	}

	@action (
			name = "getDateOffset",
			args = { @arg (
					name = "dateFormat",
					type = IType.STRING,
					optional = false,
					doc = @doc ("date format examples: 'yyyy-MM-dd' , 'yyyy-MM-dd HH:mm:ss' ")),
					@arg (
							name = "dateStr",
							type = IType.STRING,
							optional = false,
							doc = @doc ("Start date")),
					@arg (
							name = "offset",
							type = IType.STRING,
							optional = false,
							doc = @doc ("number on day to increase or decrease")) })
	public String getDateOffset(final IScope scope) throws GamaRuntimeException {
		final String dateFormat = (String) scope.getArg("dateFormat", IType.STRING);
		final String dateStr = (String) scope.getArg("dateStr", IType.STRING);
		final int dateOffset = Integer.parseInt(scope.getArg("offset", IType.INT).toString());
		final DateFormat datef = new SimpleDateFormat(dateFormat);
		final Calendar c = Calendar.getInstance();
		try {
			c.setTime(datef.parse(dateStr));
		} catch (final ParseException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			throw GamaRuntimeException.error("getDate error: Date format may not correct!" + e.toString(), scope);
		}
		c.add(Calendar.DATE, dateOffset); // number of days to add
		// dt is now the new date

		return datef.format(c.getTime());
	}

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
					doc = @doc ("Connection parameters")) })
	public boolean testConnection(final IScope scope) {

		try (final Connection conn = SqlUtils.createConnectionObject(scope).connectDB()) {} catch (final Exception e) {
			// e.printStackTrace();
			// throw new GamaRuntimeException("SQLSkill.connectDB: " +
			// e.toString());
			return false;
		}
		return true;
	}

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
							doc = @doc ("List of values that are used to replace question mark")) })
	public int executeUpdate_QM(final IScope scope) throws GamaRuntimeException {

		// final java.util.Map params = (java.util.Map) scope.getArg("params", IType.MAP);
		final String updateComm = (String) scope.getArg("updateComm", IType.STRING);
		final IList<Object> values = (IList<Object>) scope.getArg("values", IType.LIST);
		int row_count = -1;
		SqlConnection sqlConn;
		try {
			sqlConn = SqlUtils.createConnectionObject(scope);
			if (values.size() > 0) {
				row_count = sqlConn.executeUpdateDB(scope, updateComm, values);
			} else {
				row_count = sqlConn.executeUpdateDB(scope, updateComm);
			}

		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLSkill.executeUpdateDB: " + e.toString(), scope);
		}
		DEBUG.OUT(updateComm + " was run");

		return row_count;
		// ------------------------------------------------------------------------------------------

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
			// ,@arg(name = "transform", type = IType.BOOL, optional = true, doc
			// =
			// @doc("if transform = true then geometry will be tranformed from
			// absolute to gis otherways it will be not transformed. Default
			// value is false "))
			})
	public int insert(final IScope scope) throws GamaRuntimeException {

		SqlConnection sqlConn;
		// final java.util.Map params = (java.util.Map) scope.getArg("params", IType.MAP);
		final String table_name = (String) scope.getArg("into", IType.STRING);
		final IList<Object> cols = (IList<Object>) scope.getArg("columns", IType.LIST);
		final IList<Object> values = (IList<Object>) scope.getArg("values", IType.LIST);
		int rec_no = -1;
		try {
			sqlConn = SqlUtils.createConnectionObject(scope);
			// Connection conn=sqlConn.connectDB();
			if (cols.size() > 0) {
				rec_no = sqlConn.insertDB(scope, table_name, cols, values);
			} else {
				rec_no = sqlConn.insertDB(scope, table_name, values);
			}
			// conn.close();
		} catch (final Exception e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLSkill.insert: " + e.toString(), scope);
		}
		DEBUG.OUT("Insert into " + " was run");

		return rec_no;
		// ------------------------------------------------------------------------------------------
	}

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
					name = "params",
					type = IType.MAP,
					optional = false,
					doc = @doc ("Connection parameters")),
					@arg (
							name = "select",
							type = IType.STRING,
							optional = false,
							doc = @doc ("select string with question marks")),
					@arg (
							name = "values",
							type = IType.LIST,
							optional = true,
							doc = @doc ("List of values that are used to replace question marks"))
			// ,@arg(name = "transform", type = IType.BOOL, optional = true, doc
			// =
			// @doc("if transform = true then geometry will be tranformed from
			// absolute to gis otherways it will be not transformed. Default
			// value is false "))

			})
	public IList select_QM(final IScope scope) throws GamaRuntimeException {

		// final java.util.Map params = (java.util.Map) scope.getArg("params", IType.MAP);Ò
		final String selectComm = (String) scope.getArg("select", IType.STRING);
		final IList<Object> values = (IList<Object>) scope.getArg("values", IType.LIST);
		// thai.truongminh@gmail.com
		// Move transform arg of select to a key in params
		// boolean transform = scope.hasArg("transform") ? (Boolean)
		// scope.getArg("transform", IType.BOOL) : true;
		// boolean transform = params.containsKey("transform") ? (Boolean)
		// params.get("transform") : true;

		SqlConnection sqlConn;
		IList<? super IList<Object>> repRequest;
		try {
			sqlConn = SqlUtils.createConnectionObject(scope);
			if (values.size() > 0) {
				repRequest = sqlConn.executeQueryDB(scope, selectComm, values);
			} else {
				repRequest = sqlConn.selectDB(scope, selectComm);
			}
			// Transform GIS to Absolute (Geometry in GAMA)
			// AD: now made directly in the select / query
			// if ( transform ) {
			// return sqlConn.fromGisToAbsolute(scope, repRequest);
			// } else {
			// return repRequest;
			// }
			return repRequest;
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLSkill.select_QM: " + e.toString(), scope);
		}

		// ------------------------------------------------------------------------------------------

	}

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
									comment = "if it is true then the action will return columnTypes and data. default is false"))

			})
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
