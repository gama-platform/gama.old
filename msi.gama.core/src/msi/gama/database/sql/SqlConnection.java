package msi.gama.database.sql;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import msi.gama.common.util.GuiUtils;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import com.vividsolutions.jts.geom.*;

/*
 * @Author  
 *     TRUONG Minh Thai
 *     Fredric AMBLARD
 *     Benoit GAUDOU
 *     Christophe Sibertin-BLANC
 * Created date: 19-Apr-2013
 * Modified:  
 *    26-Apr-2013:  
 *      Remove driver msi.gama.ext/sqljdbc4.jar
 *      add driver msi.gama.ext/jtds-1.2.6.jar
 *      Change driver name for MSSQL from com.microsoft.sqlserver.jdbc.SQLServerDriver to net.sourceforge.jtds.jdbc.Driver
 *    18-July-2013:  
 *      Add load extension library for SQLITE case.
 *       
 * Last Modified: 18-July-2013
*/
public abstract class SqlConnection {

	protected static final boolean DEBUG = false; // Change DEBUG = false for release version
	public static final String MYSQL = "mysql";
	public static final String POSTGRES = "postgres";
	public static final String POSTGIS = "postgis";
	public static final String MSSQL = "sqlserver";
	public static final String SQLITE = "sqlite";
	public static final String GEOMETRYTYPE = "GEOMETRY";
	public static final String CHAR = "CHAR";
	public static final String VARCHAR = "VARCHAR";
	public static final String NVARCHAR = "NVARCHAR";
	public static final String TEXT = "TEXT";
	public static final String BLOB ="BLOB";

	static final String MYSQLDriver = new String("com.mysql.jdbc.Driver");
	//static final String MSSQLDriver = new String("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	static final String MSSQLDriver = new String("net.sourceforge.jtds.jdbc.Driver");
	static final String SQLITEDriver = new String("org.sqlite.JDBC");
	static final String POSTGRESDriver = new String("org.postgresql.Driver");


	protected String vender = "";
	protected String dbtype = "";
	protected String url = "";
	protected String port = "";
	protected String dbName = "";
	protected String userName = "";
	protected String password = "";
	protected static Boolean transformed = false;
	protected String extension=null;
	protected boolean loadExt=false;
	public SqlConnection(String dbName) {
		this.dbName = dbName;
	}

	public SqlConnection(String venderName, String database) {
		this.vender = venderName;
		this.dbName = database;
		this.dbtype = venderName;
	}

	public SqlConnection(String venderName, String database, Boolean transformed) {
		this.vender = venderName;
		this.dbName = database;
		this.transformed = transformed;
		this.dbtype = venderName;
	}

	public SqlConnection() {}

	public SqlConnection(String venderName, String url, String port, String dbName, String userName, String password) {
		this.vender = venderName;
		this.url = url;
		this.port = port;
		this.dbName = dbName;
		this.userName = userName;
		this.password = password;
		this.dbtype = venderName;
	}

	public SqlConnection(String venderName, String url, String port, String dbName, String userName, String password,
		Boolean transformed) {
		this.vender = venderName;
		this.url = url;
		this.port = port;
		this.dbName = dbName;
		this.userName = userName;
		this.password = password;
		this.dbtype = venderName;
		this.transformed = transformed;

	}

	/*
	 * Make a connection to BDMS
	 */
	public abstract Connection connectDB() throws ClassNotFoundException, InstantiationException, SQLException,
		IllegalAccessException;

	/*
	 * @Method:resultSet2GamaList(ResultSetMetaData rsmd, ResultSet rs)
	 * 
	 * @Description: Convert RecordSet to GamaList
	 * 
	 * @param ResultSetMetaData,ResultSet
	 * 
	 * @return GamaList<GamaList<Object>>
	 */
	protected abstract GamaList<GamaList<Object>> resultSet2GamaList(ResultSetMetaData rsmd, ResultSet rs);

	/*
	 * @Meththod: getGeometryColumns(ResultSetMetaData rsmd)
	 * 
	 * @Description: Get columns id of field with geometry type
	 * 
	 * @param ResultSetMetaData
	 * 
	 * @return List<Integer>
	 * 
	 * @throws SQLException
	 */
	protected abstract List<Integer> getGeometryColumns(ResultSetMetaData rsmd) throws SQLException;

	/*
	 * @Method: getColumnTypeName
	 * 
	 * @Description: Get columns type name
	 * 
	 * @param ResultSetMetaData
	 * 
	 * @return GamaList<String>
	 * 
	 * @throws SQLException
	 */
	protected abstract GamaList<Object> getColumnTypeName(ResultSetMetaData rsmd) throws SQLException;

	/*
	 * Make insert command string with columns and values
	 */
	protected abstract String getInsertString(IScope scope, Connection conn, String table_name, GamaList<Object> cols,
		GamaList<Object> values) throws GamaRuntimeException;

	/*
	 * Make insert command string for all columns with values
	 */
	protected abstract String getInsertString(IScope scope, Connection conn, String table_name, GamaList<Object> values)
		throws GamaRuntimeException;

	/*
	 * Make a connection to BDMS and execute the select statement
	 * 
	 * @return GamaList<GamaList<Object>>
	 */
	public GamaList<Object> selectDB(String selectComm) {
		GamaList<Object> result = new GamaList<Object>();
		Connection conn = null;
		try {
			conn = connectDB();
			result = selectDB(conn, selectComm);
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.selectDB: " + e.toString());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.selectDB: " + e.toString());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.selectDB: " + e.toString());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.selectDB: " + e.toString());
		}

		if ( DEBUG ) {
			GuiUtils.informConsole(selectComm + " was run");
		}
		// return repRequest;
		return result;
	}

	/*
	 * Make a connection to BDMS and execute the select statement
	 * 
	 * @return GamaList<GamaList<Object>>
	 */
	// public GamaList<GamaList<Object>> selectDB(String selectComm)
	public GamaList<Object> selectDB(Connection conn, String selectComm) {
		ResultSet rs;
		GamaList<Object> result = new GamaList<Object>();
		// GamaList<Object> rowList = new GamaList<Object>();
		GamaList<GamaList<Object>> repRequest = new GamaList<GamaList<Object>>();
		try {
			Statement st = conn.createStatement();
			//load extension library
//			if (this.dbtype.equalsIgnoreCase(SQLITE)){
//				if (this.extension!=null && !loadExt){
//					st = conn.createStatement();
//				    st.setQueryTimeout(30); // set timeout to 30 sec.
//					st.execute("SELECT load_extension('"+extension+"')");
//				    String sql = "SELECT InitSpatialMetadata()";
//				    st.execute(sql);
//				    loadExt=true;
//				}
//			}
			rs = st.executeQuery(selectComm);

			ResultSetMetaData rsmd = rs.getMetaData();
			if ( DEBUG ) {
				GuiUtils.debug("MetaData:" + rsmd.toString());
			}
			// repRequest=resultSet2GamaList(rs);

			// result.add(rsmd);
			result.add(getColumnName(rsmd));
			result.add(getColumnTypeName(rsmd));

			repRequest = resultSet2GamaList(rs);
			result.add(repRequest);

			if ( DEBUG ) {
				GuiUtils.debug("list of column name:" + result.get(0));
				GuiUtils.debug("list of column type:" + result.get(1));
				GuiUtils.debug("list of data:" + result.get(2));
			}
			
			st.close();
//			st=null;
//			System.gc();
			
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.selectDB: " + e.toString());
		}
		// return repRequest;
		return result;
	}

	/*
	 * Make a connection to BDMS and execute the update statement (update/insert/delete/create/drop)
	 */

	public int executeUpdateDB(String updateComm) throws GamaRuntimeException {
		Connection conn = null;
		int n = 0;
		try {
			conn = connectDB();
			if ( DEBUG ) {
				GuiUtils.debug("Update Command:" + updateComm);
			}
			Statement st = conn.createStatement();
			//load extension library
//			if (this.dbtype.equalsIgnoreCase(SQLITE)){
//				if (this.extension!=null && !loadExt){
//					st = conn.createStatement();
//				    st.setQueryTimeout(30); // set timeout to 30 sec.
//					st.execute("SELECT load_extension('"+extension+"')");
//				    String sql = "SELECT InitSpatialMetadata()";
//				    st.execute(sql);
//				    loadExt=true;
//				}
//
//			}			
			n = st.executeUpdate(updateComm);
			if ( DEBUG ) {
				GuiUtils.debug("Updated records :" + n);
			}
			
			st.close();
//			st=null;
//			System.gc();

			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.executeUpdateDB: " + e.toString());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.executeUpdateDB: " + e.toString());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.executeUpdateDB: " + e.toString());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.executeUpdateDB: " + e.toString());
		}

		if ( DEBUG ) {
			GuiUtils.informConsole(updateComm + " was run");
		}

		return n;

	}

	/*
	 * execute the update statement with current connection(update/insert/delete/create/drop)
	 */
	public int executeUpdateDB(Connection conn, String updateComm) throws GamaRuntimeException {
		int n = 0;
		try {
			if ( DEBUG ) {
				GuiUtils.debug("Update Command:" + updateComm);
			}
			Statement st = conn.createStatement();
			
			//load extension library
//			if (this.dbtype.equalsIgnoreCase(SQLITE)){
//				if (this.extension!=null && !loadExt){
//					st = conn.createStatement();
//				    st.setQueryTimeout(30); // set timeout to 30 sec.
//					st.execute("SELECT load_extension('"+extension+"')");
//				    String sql = "SELECT InitSpatialMetadata()";
//				    st.execute(sql);
//					loadExt=true;
//				}
//			}
			
			n = st.executeUpdate(updateComm);
			st.close();
//			st=null;
//			System.gc();

			if ( DEBUG ) {
				GuiUtils.debug("Updated records :" + n);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.executeUpdateDB: " + e.toString());
		}

		return n;
	}

	protected GamaList<GamaList<Object>> resultSet2GamaList(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		return resultSet2GamaList(rsmd, rs);
	}

	/*
	 * @Method: getColumnName
	 * 
	 * @Description: Get columns name
	 * 
	 * @param ResultSetMetaData
	 * 
	 * @return GamaList<String>
	 * 
	 * @throws SQLException
	 */
	protected GamaList<Object> getColumnName(ResultSetMetaData rsmd) throws SQLException {
		int numberOfColumns = rsmd.getColumnCount();
		GamaList<Object> columnType = new GamaList<Object>();
		for ( int i = 1; i <= numberOfColumns; i++ ) {
			columnType.add(rsmd.getColumnName(i).toUpperCase());
		}
		return columnType;
	}


	public String getURL() {
		return url;
	}

	public String getVendor() {
		return vender;
	}

	public String getUser() {
		return userName;
	}

	/*
	 * @Method: getBounds( GamaList<Object> gamaList)
	 * 
	 * @Description: Get Envelope of a set of geometry
	 * 
	 * @param GamaList<Object> gamaList: gamalist is a set of geometry type
	 * 
	 * @return Envelope: Envelope/boundary of the geometry set.
	 * 
	 * @throws Exception
	 */

	public static Envelope getBounds(IScope scope, GamaList<Object> gamaList) throws IOException {
		Envelope envelope;
		// get Column name
		GamaList<Object> colNames = (GamaList<Object>) gamaList.get(0);
		// get Column type
		GamaList<Object> colTypes = (GamaList<Object>) gamaList.get(1);
		int index = colTypes.indexOf("GEOMETRY");
		if ( index < 0 ) {
			return null;
		} else {
			// Get ResultSet
			GamaList<GamaList<Object>> initValue = (GamaList<GamaList<Object>>) gamaList.get(2);
			int n = initValue.length(scope);
			// int max = number == null ? Integer.MAX_VALUE : numberOfAgents;
			if ( n < 0 ) {
				return null;
			} else {
				GamaList<Object> rowList = initValue.get(0);
				Geometry geo = (Geometry) rowList.get(index);
				envelope = geo.getEnvelopeInternal();
				double maxX = envelope.getMaxX();
				double maxY = envelope.getMaxY();
				double minX = envelope.getMinX();
				double minY = envelope.getMinY();
				for ( int i = 1; i < n && i < Integer.MAX_VALUE; i++ ) {
					rowList = initValue.get(i);
					geo = (Geometry) rowList.get(index);
					envelope = geo.getEnvelopeInternal();
					double maxX1 = envelope.getMaxX();
					double maxY1 = envelope.getMaxY();
					double minX1 = envelope.getMinX();
					double minY1 = envelope.getMinY();

					maxX = maxX > maxX1 ? maxX : maxX1;
					maxY = maxY > maxY1 ? maxY : maxY1;
					minX = minX < minX1 ? minX : minX1;
					minY = minY < minY1 ? minY : minY1;
					envelope.init(minX, maxX, minY, maxY);

				}
				return envelope;
			}
		}
	}

	/*-------------------------------------------------------------------------------------------------------------------------
	 * Make Insert a reccord into table
	 * 
	 */
	public int insertDB(IScope scope, Connection conn, String table_name, GamaList<Object> cols, GamaList<Object> values)
		throws GamaRuntimeException {
		int rec_no = -1;
		if ( values.size() != cols.size() ) { throw new IndexOutOfBoundsException(
			"Size of columns list and values list are not equal"); }
		try {
			// Get Insert command
			Statement st = conn.createStatement();
			
			//load extension library
//			if (this.dbtype.equalsIgnoreCase(SQLITE)){
//				if (this.extension!=null &&!loadExt){
//					st = conn.createStatement();
//				    st.setQueryTimeout(30); // set timeout to 30 sec.
//					st.execute("SELECT load_extension('"+extension+"')");
//				    String sql = "SELECT InitSpatialMetadata()";
//				    st.execute(sql);
//				    loadExt=true;
//				}
//
//			}
			String sqlStr=getInsertString(scope, conn, table_name, cols, values);
			if ( DEBUG ) {
				GuiUtils.debug("SQLConnection.insertBD.STR:" + sqlStr);
			}

			//rec_no = st.executeUpdate(getInsertString(scope, conn, table_name, cols, values));
			rec_no = st.executeUpdate(sqlStr);
			st.close();
//			st=null;
//			System.gc();

			if ( DEBUG ) {
				GuiUtils.debug("SQLConnection.insertBD.rec_no:" + rec_no);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.insertBD " + e.toString());
		}

		return rec_no;
	}

	/*-------------------------------------------------------------------------------------------------------------------------
	 * Make Insert a reccord into table
	 * 
	 */
	public int insertDB(IScope scope, Connection conn, String table_name, GamaList<Object> cols,
		GamaList<Object> values, Boolean transformed) throws GamaRuntimeException {
		SqlConnection.transformed = transformed;
		int rec_no = -1;
		// Get Insert command
		rec_no = insertDB(scope, conn, table_name, cols, values);
		return rec_no;
	}

	/*-------------------------------------------------------------------------------------------------------------------------
	 * Make Insert a reccord into table
	 * 
	 */
	public int insertDB(IScope scope, String table_name, GamaList<Object> cols, GamaList<Object> values)
		throws GamaRuntimeException {
		Connection conn;
		int rec_no = -1;
		try {
			conn = connectDB();
			rec_no = insertDB(scope, conn, table_name, cols, values);
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.insertBD " + e.toString());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.insertDB: " + e.toString());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.insertBD: " + e.toString());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.insertBD: " + e.toString());
		}
		return rec_no;
	}

	/*
	 * Make Insert a reccord into table
	 */
	public int insertDB(IScope scope, String table_name, GamaList<Object> cols, GamaList<Object> values,
		Boolean transformed) throws GamaRuntimeException {
		SqlConnection.transformed = transformed;
		int rec_no = -1;
		rec_no = insertDB(scope, table_name, cols, values);
		return rec_no;
	}

	/*
	 * Make Insert a reccord into table
	 */
	public int insertDB(IScope scope, Connection conn, String table_name, GamaList<Object> values)
		throws GamaRuntimeException {
		int rec_no = -1;
		try {
			// Get Insert command
			Statement st = conn.createStatement();
			
			//load extension library
//			if (this.dbtype.equalsIgnoreCase(SQLITE)){
//				if (this.extension!=null &&!loadExt){
//					st = conn.createStatement();
//				    st.setQueryTimeout(30); // set timeout to 30 sec.
//					st.execute("SELECT load_extension('"+extension+"')");
//				    String sql = "SELECT InitSpatialMetadata()";
//				    st.execute(sql);
//				    loadExt=true;
//				}
//			}	
			
			rec_no = st.executeUpdate(getInsertString(scope, conn, table_name, values));
			st.close();
//			st=null;
//			System.gc();
			if ( DEBUG ) {
				GuiUtils.debug("SQLConnection.insertBD.rec_no:" + rec_no);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.insertBD " + e.toString());
		}
		return rec_no;
	}

	public int insertDB(IScope scope, Connection conn, String table_name, GamaList<Object> values, Boolean transformed)
		throws GamaRuntimeException {
		SqlConnection.transformed = transformed;
		return insertDB(scope, conn, table_name, values);
	}

	/*
	 * Make Insert a reccord into table
	 */
	public int insertDB(IScope scope, String table_name, GamaList<Object> values) throws GamaRuntimeException {
		Connection conn;
		int rec_no = -1;
		try {
			conn = connectDB();
			rec_no = insertDB(scope, conn, table_name, values);
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.insertBD " + e.toString());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.insertDB: " + e.toString());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.insertBD: " + e.toString());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.insertBD: " + e.toString());
		}
		return rec_no;
	}

	/*
	 * Make Insert a reccord into table
	 */
	public int insertDB(IScope scope, String table_name, GamaList<Object> values, Boolean transformed)
		throws GamaRuntimeException {
		SqlConnection.transformed = transformed;
		return insertDB(scope, table_name, values);
	}

	/*
	 * @Method: executeQueryDB(Connection conn,String queryStr, GamaList<Object> condition_value)
	 * 
	 * @Description: Executes the SQL query in this PreparedStatement object and returns the ResultSet object generated
	 * by the query
	 * 
	 * @param queryStr: SQL query string with question mark (?).
	 * 
	 * @param condition_value:List of values that are used to assign into conditions of queryStr
	 * 
	 * @return ResultSet:returns the ResultSet object generated by the query.
	 * 
	 * @throws GamaRuntimeException: if a database access error occurs or the SQL statement does not return a ResultSet
	 * object
	 */
	public GamaList<Object> executeQueryDB(Connection conn, String queryStr, GamaList<Object> condition_values)
		throws GamaRuntimeException {
		PreparedStatement pstmt = null;
		ResultSet rs;
		GamaList<Object> result = new GamaList<Object>();
		GamaList<GamaList<Object>> repRequest = new GamaList<GamaList<Object>>();
		int condition_count = condition_values.size();
		try {
			
			//load extension library
//			if (this.dbtype.equalsIgnoreCase(SQLITE)){
//				if (this.extension!=null && !loadExt){
//					pstmt = conn.prepareStatement("SELECT load_extension('"+extension+"')");
//					pstmt.setQueryTimeout(30); // set timeout to 30 sec.
//					pstmt.executeQuery();
//					pstmt = conn.prepareStatement("SELECT InitSpatialMetadata()");	
//					pstmt.executeQuery();	
//					loadExt=true;
//				}
//			}
			
			pstmt = conn.prepareStatement(queryStr);
			
			// set value for each condition
			for ( int i = 0; i < condition_count; i++ ) {
				pstmt.setObject(i + 1, condition_values.get(i));
			}
			rs = pstmt.executeQuery();

			ResultSetMetaData rsmd = rs.getMetaData();
			if ( DEBUG ) {
				GuiUtils.debug("MetaData:" + rsmd.toString());
			}

			result.add(getColumnName(rsmd));
			result.add(getColumnTypeName(rsmd));

			repRequest = resultSet2GamaList(rs);
			result.add(repRequest);

			if ( DEBUG ) {
				GuiUtils.debug("list of column name:" + result.get(0));
				GuiUtils.debug("list of column type:" + result.get(1));
				GuiUtils.debug("list of data:" + result.get(2));
			}
			
			pstmt.close();
//			pstmt=null;
//			System.gc();

			
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.selectDB: " + e.toString());
		}
		// return repRequest;
		return result;

	}

	/*
	 * @Method: ExecuteQueryDB(Connection conn,String queryStr, GamaList<Object> condition_values)
	 * 
	 * @Description: Executes the SQL query in this PreparedStatement object and returns the ResultSet object generated
	 * by the query
	 * 
	 * @param conn: MAP of Connection parameters to RDBM
	 * 
	 * @param queryStr: SQL query (select) string with question mark (?).
	 * 
	 * @param condition_value:List of values that are used to assign into conditions of queryStr
	 * 
	 * @return ResultSet:returns the ResultSet object generated by the query.
	 * 
	 * @throws GamaRuntimeException: if a database access error occurs or the SQL statement does not return a ResultSet
	 * object
	 */
	public GamaList<Object> executeQueryDB(String queryStr, GamaList<Object> condition_values)
		throws GamaRuntimeException {
		GamaList<Object> result = new GamaList<Object>();
		Connection conn;
		try {
			conn = connectDB();
			result = executeQueryDB(conn, queryStr, condition_values);
			conn.close();
			// set value for each condition
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.executeQuery: " + e.toString());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.executeQuery: " + e.toString());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.executeQuery: " + e.toString());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.executeQuery: " + e.toString());
		}

		return result;

	}

	/*
	 * @Method: executeUpdateDB(Connection conn,String queryStr, GamaList<Object> condition_value)
	 * 
	 * @Description: Executes the SQL statement in this PreparedStatement object, which must be an SQL INSERT, UPDATE or
	 * DELETE statement; or an SQL statement that returns nothing, such as a DDL statement.
	 * 
	 * @param conn: MAP of Connection parameters to RDBM
	 * 
	 * @param queryStr: an SQL INSERT, UPDATE or DELETE statement with question mark (?).
	 * 
	 * @param condition_values: List of values that are used to assign into conditions of queryStr.
	 * 
	 * @return row_count:either (1) the row count for INSERT, UPDATE, or DELETE statements or (2) 0 for SQL statements
	 * that return nothing
	 * 
	 * @throws GamaRuntimeException
	 */
	public int executeUpdateDB(Connection conn, String queryStr, GamaList<Object> condition_values)
		throws GamaRuntimeException {
		PreparedStatement pstmt = null;
		int row_count = -1;
		int condition_count = condition_values.size();
		try {
			//load extension library
//			if (this.dbtype.equalsIgnoreCase(SQLITE)){
//				if (this.extension!=null && !loadExt){
//					pstmt = conn.prepareStatement("SELECT load_extension('"+extension+"')");
//					pstmt.setQueryTimeout(30); // set timeout to 30 sec.
//					pstmt.executeQuery();
//					pstmt = conn.prepareStatement("SELECT InitSpatialMetadata()");	
//					pstmt.executeQuery();
//					loadExt=true;
//				}
//			}

			pstmt = conn.prepareStatement(queryStr);
			// set value for each condition
			if ( DEBUG ) {
				GuiUtils.debug("SqlConnection.ExecuteUpdateDB.values.size:" + condition_count);
				GuiUtils.debug("SqlConnection.ExecuteUpdateDB.values.size:" + condition_values.toGaml());
			}

			for ( int i = 0; i < condition_count; i++ ) {
				pstmt.setObject(i + 1, condition_values.get(i));
			}
			row_count = pstmt.executeUpdate();
			
			pstmt.close();
//			pstmt=null;
//			System.gc();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.selectDB: " + e.toString());
		}
		return row_count;
	}

	/*
	 * @Method: executeUpdateDB(Connection conn,String queryStr, GamaList<Object> condition_value)
	 * 
	 * @Description: Executes the SQL statement in this PreparedStatement object, which must be an SQL INSERT, UPDATE or
	 * DELETE statement; or an SQL statement that returns nothing, such as a DDL statement.
	 * 
	 * @param queryStr: an SQL INSERT, UPDATE or DELETE statement with question mark (?).
	 * 
	 * @param condition_values:
	 * 
	 * @return row_count:either (1) the row count for INSERT, UPDATE, or DELETE statements or (2) 0 for SQL statements
	 * that return nothing
	 * 
	 * @throws GamaRuntimeException
	 */
	public int executeUpdateDB(String queryStr, GamaList<Object> condition_values) throws GamaRuntimeException {
		int row_count = -1;
		Connection conn;
		try {
			conn = connectDB();
			row_count = executeUpdateDB(conn, queryStr, condition_values);
			conn.close();
			// set value for each condition
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.executeUpdateDB: " + e.toString());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.executeUpdateDB: " + e.toString());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.executeUpdateDB: " + e.toString());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.executeUpdateDB: " + e.toString());
		}
		return row_count;
	}

	public void setTransformed(boolean tranformed) {
		SqlConnection.transformed = tranformed;
	}

	public static Geometry fromGisToAbsolute(IScope scope, Geometry geom) {
		return scope.getTopology().getGisUtils().transform(geom);

	}

	public static Geometry fromAbsoluteToGis(IScope scope, Geometry geom) {
		return scope.getTopology().getGisUtils().inverseTransform(geom);

	}

	/*
	 * Gis2Absolute: transform all geometry values to absolute geometry in GAMA
	 */
	public GamaList<Object> fromGisToAbsolute(IScope scope, GamaList<Object> dataset) throws GamaRuntimeException {
		try {
			GamaList<Object> response = new GamaList<Object>();
			GamaList<Object> records_new = new GamaList<Object>();
			GamaList<Object> columnNames = (GamaList<Object>) dataset.get(0);
			GamaList<Object> columnTypes = (GamaList<Object>) dataset.get(1);
			GamaList<Object> records = (GamaList<Object>) dataset.get(2);
			int columnSize = columnNames.size();
			int lineSize = records.size();

			response.add(columnNames);
			response.add(columnTypes);

			// transform
			for ( int i = 0; i < lineSize; i++ ) {
				GamaList<Object> rec_old = (GamaList<Object>) records.get(i);
				GamaList<Object> rec_new = new GamaList<Object>();
				for ( int j = 0; j < columnSize; j++ ) {
					if ( ((String) columnTypes.get(j)).equalsIgnoreCase(GEOMETRYTYPE) ) {
						// WKTReader wkt = new WKTReader();
						// Geometry geo2 =fromGisToAbsolute(wkt.read(rec_old.get(j).toString()));
						Geometry geo2 = fromGisToAbsolute(scope, (Geometry) rec_old.get(j));
						rec_new.add(geo2);
					} else {
						rec_new.add(rec_old.get(j));
					}
				}
				records_new.add(rec_new);
			}
			response.add(records_new);
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.Gis2Absolute: " + e.toString());
		}
	}

	/*
	 * 
	 * Gis2Absolute: transform all absolute geometry values in GAMA to geometry
	 */
	public GamaList<Object> fromAbsoluteToGIS(IScope scope, GamaList<Object> dataset) throws GamaRuntimeException {
		try {
			GamaList<Object> response = new GamaList<Object>();
			GamaList<Object> records_new = new GamaList<Object>();
			GamaList<Object> columnNames = (GamaList<Object>) dataset.get(0);
			GamaList<Object> columnTypes = (GamaList<Object>) dataset.get(1);
			GamaList<Object> records = (GamaList<Object>) dataset.get(2);
			int columnSize = columnNames.size();
			int lineSize = records.size();

			response.add(columnNames);
			response.add(columnTypes);

			// transform
			for ( int i = 0; i < lineSize; i++ ) {
				GamaList<Object> rec_old = (GamaList<Object>) records.get(i);
				GamaList<Object> rec_new = new GamaList<Object>();
				for ( int j = 0; j < columnSize; j++ ) {
					if ( ((String) columnTypes.get(j)).equalsIgnoreCase(GEOMETRYTYPE) ) {
						// WKTReader wkt = new WKTReader();
						// Geometry geo2 =fromGisToAbsolute(wkt.read(rec_old.get(j).toString()));
						Geometry geo2 = fromAbsoluteToGis(scope, (Geometry) rec_old.get(j));
						rec_new.add(geo2);
					} else {
						rec_new.add(rec_old.get(j));
					}

				}
				records_new.add(rec_new);
			}
			response.add(records_new);
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.Gis2Absolute: " + e.toString());

		}
	}
	

}// end of class
