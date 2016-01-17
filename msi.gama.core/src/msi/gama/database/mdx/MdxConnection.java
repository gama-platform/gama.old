/*********************************************************************************************
 *
 *
 * 'MdxConnection.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.database.mdx;

import java.sql.SQLException;
import java.util.List;
import org.olap4j.*;
import org.olap4j.metadata.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Strings;

/*
 * @Author
 * TRUONG Minh Thai
 * Fredric AMBLARD
 * Benoit GAUDOU
 * Christophe Sibertin-BLANC
 *
 *
 * SQLConnection: supports the method
 * - connectDB: make a connection to DBMS.
 * - selectDB: connect to DBMS and run executeQuery to select data from DBMS.
 * - executeUpdateDB: connect to DBMS and run executeUpdate to update/insert/delete/drop/create data
 * on DBMS.
 *
 * Created date: 18-Jan-2013
 * Modified:
 * 03-05-2013: add selectMDB methods
 *
 * Last Modified: 02-07-2013
 */
public abstract class MdxConnection {

	private static final boolean DEBUG = false; // Change DEBUG = false for release version
	protected static final String MONDRIAN = "mondrian";
	protected static final String MONDRIANXMLA = "mondrian/xmla";
	protected static final String MSAS = "ssas/xmla"; // Micrsoft SQL Server Analysis Services
	protected static final String MYSQL = "mysql";
	protected static final String POSTGRES = "postgres";
	protected static final String POSTGIS = "postgis";
	protected static final String MSSQL = "sqlserver";
	protected static final String SQLITE = "sqlite";

	protected static final String GEOMETRYTYPE = "GEOMETRY";
	protected static final String MYSQLDriver = new String("com.mysql.jdbc.Driver");
	// static final String MSSQLDriver = new String("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	protected static final String MSSQLDriver = new String("net.sourceforge.jtds.jdbc.Driver");
	protected static final String SQLITEDriver = new String("org.sqlite.JDBC");
	protected static final String POSTGRESDriver = new String("org.postgresql.Driver");

	protected String vender = "";
	protected String dbtype = POSTGRES;
	protected String url = "";
	protected String port = "";
	protected String dbName = "";
	protected String catalog = "";
	protected String userName = "";
	protected String password = "";

	protected OlapConnection olapConnection;

	// protected Connection connection;

	public MdxConnection() {}

	public MdxConnection(final String vender) {
		this.vender = vender;
	}

	public MdxConnection(final String venderName, final String database) {
		this.vender = venderName;
		this.dbName = database;
	}

	public MdxConnection(final String venderName, final String dbtype, final String url) {
		this.vender = venderName;
		this.dbtype = dbtype;
		this.dbName = url;
	}

	public MdxConnection(final String venderName, final String url, final String port, final String dbName,
		final String userName, final String password) {
		this.vender = venderName;
		this.url = url;
		this.port = port;
		this.dbName = dbName;
		this.userName = userName;
		this.password = password;
	}

	public MdxConnection(final String venderName, final String url, final String port, final String dbName,
		final String catalog, final String userName, final String password) {
		this.vender = venderName;
		this.url = url;
		this.port = port;
		this.dbName = dbName;
		this.catalog = catalog;
		this.userName = userName;
		this.password = password;
	}

	public MdxConnection(final String venderName, final String dbtype, final String url, final String port,
		final String dbName, final String catalog, final String userName, final String password) {
		this.vender = venderName;
		this.dbtype = dbtype;
		this.url = url;
		this.port = port;
		this.dbName = dbName;
		this.catalog = catalog;
		this.userName = userName;
		this.password = password;
	}

	/*
	 * Make a connection to Multidimensional Database Server
	 */
	public abstract OlapConnection connectMDB() throws GamaRuntimeException;

	/*
	 * Make a connection to Multidimensional Database Server
	 */
	public abstract OlapConnection connectMDB(String dbName) throws GamaRuntimeException;

	/*
	 * Make a connection to Multidimensional Database Server
	 */
	public abstract OlapConnection connectMDB(String dbName, String catalog) throws GamaRuntimeException;

	public void setConnection() {
		this.olapConnection = this.connectMDB();

	}

	public void setConnection(final OlapConnection oConn) {
		this.olapConnection = oConn;

	}

	public OlapConnection getConnection() {
		return this.olapConnection;
	}

	public boolean isConnected() {
		if ( this.olapConnection != null ) {
			return true;
		} else {
			return false;
		}
	}

	public String getVender() {
		return this.vender;
	}

	public String getdbType() {
		return this.dbtype;
	}

	public String getdbName() {
		return this.dbName;
	}

	public String getURL() {
		return this.url;
	}

	public String getport() {
		return this.port;
	}

	public String getCatalog() {
		return this.catalog;
	}

	public String getUser() {
		return this.userName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setVender(final String vender) {
		this.vender = vender;
	}

	public void setdbType(final String dbType) {
		this.dbtype = dbType;
	}

	public void setdbName(final String dbName) {
		this.dbName = dbName;
	}

	public void setURL(final String url) {
		this.url = url;
	}

	public void setport(final String port) {
		this.port = port;
	}

	public void setCatalog(final String catalog) {
		this.catalog = catalog;
	}

	public void setUser(final String userName) {
		this.userName = userName;
	}

	public void getPassword(final String password) {
		this.password = password;
	}

	// public String getDatabase() throws GamaRuntimeException {
	// try {
	// return olapConnection.getDatabase();
	// } catch (OlapException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// throw GamaRuntimeException.error(e.toString());
	// }
	// }

	// public OlapDatabaseMetaData getMetaData() throws GamaRuntimeException {
	// try {
	// return olapConnection.getMetaData();
	// } catch (OlapException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// throw GamaRuntimeException.error(e.toString());
	// }
	// }

	/*
	 * Select data source with connection was established
	 */

	public CellSet select(final String selectComm) {
		CellSet resultCellSet = null;
		OlapConnection oConn = null;
		try {
			oConn = connectMDB();
			resultCellSet = select(oConn, selectComm);
			oConn.close();
		} catch (SQLException e) {

		}
		return resultCellSet;
	}

	public CellSet select(final String selectComm, final IList<Object> condition_values) {
		CellSet resultCellSet = null;
		OlapConnection oConn = null;
		try {
			// Connection conn = connectMDB();
			String mdxStr = parseMdx(selectComm, condition_values);
			oConn = connectMDB();
			resultCellSet = select(oConn, mdxStr);
			oConn.close();
		} catch (SQLException e) {

		}
		return resultCellSet;
	}

	public CellSet select(final OlapConnection connection, final String selectComm) throws GamaRuntimeException {
		CellSet resultCellSet = null;
		OlapStatement statement;
		try {
			statement = connection.createStatement();
			resultCellSet = statement.executeOlapQuery(selectComm);

			statement.close();
			// connection.close();
		} catch (OlapException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString());
		} catch (SQLException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString());
		}
		return resultCellSet;
	}

	/*
	 * Select data source with connection was established
	 */
	public IList<Object> selectMDB(final String selectComm, final IList<Object> condition_values) {
		CellSet cellSet = select(selectComm, condition_values);
		return cellSet2List(cellSet);
	}

	public IList<Object> selectMDB(final String selectComm) {
		CellSet cellSet = select(selectComm);
		return cellSet2List(cellSet);
	}

	public IList<Object> selectMDB(final OlapConnection connection, final String selectComm) {
		CellSet cellSet = select(connection, selectComm);
		return cellSet2List(cellSet);
	}

	public IList<Object> selectMDB(final String onColumns, final String onRows, final String from) {
		String mdxStr = "SELECT " + onColumns + " ON COLUMNS, " + onRows + " ON ROWS " + " FROM " + from;
		return selectMDB(mdxStr);
	}

	public IList<Object> selectMDB(final OlapConnection connection, final String onColumns, final String onRows,
		final String from) {
		String mdxStr = "SELECT " + onColumns + " ON COLUMNS, " + onRows + " ON ROWS " + " FROM " + from;
		return selectMDB(connection, mdxStr);
	}

	public IList<Object> selectMDB(final String onColumns, final String onRows, final String from, final String where) {
		String mdxStr =
			"SELECT " + onColumns + " ON COLUMNS, " + onRows + " ON ROWS " + " FROM " + from + " WHERE " + where;
		return selectMDB(mdxStr);
	}

	public IList<Object> selectMDB(final OlapConnection connection, final String onColumns, final String onRows,
		final String from, final String where) {
		String mdxStr =
			"SELECT " + onColumns + " ON COLUMNS, " + onRows + " ON ROWS " + " FROM " + from + " WHERE " + where;
		return selectMDB(connection, mdxStr);
	}

	/*
	 * Format of Olap query result (GamaList<Object>:
	 * Result of OLAP query is transformed to Gamalist<Object> with order:
	 * (0): GamaList<String>: List of column names.
	 * (1): GamaList<Object>: Row data. it contains List of list and it look like a matrix with structure:
	 * (0): the first row data
	 * (1): the second row data
	 * ...
	 * Each row data contains two element:
	 * (0): rowMembers (GamaList<String>: this is a list of members in the row.
	 * (1): cellValues (Gamalist<Object>): This is a list of values in cell column or (we can call measures)
	 */
	public IList<Object> cellSet2List(final CellSet cellSet) {
		IList<Object> olapResult = GamaListFactory.create();
		olapResult.add(this.getColumnsName(cellSet));
		olapResult.add(this.getRowsData(cellSet));
		return olapResult;
	}

	protected IList<Object> getColumnsName(final CellSet cellSet) {
		IList<Object> columnsName = GamaListFactory.create();
		List<CellSetAxis> cellSetAxes = cellSet.getAxes();
		// get headings.
		CellSetAxis columnsAxis = cellSetAxes.get(Axis.COLUMNS.axisOrdinal());
		for ( Position position : columnsAxis.getPositions() ) {
			Member measure = position.getMembers().get(0);
			columnsName.add(measure.getName());
		}
		return columnsName;

	}

	protected IList<Object> getRowsData(final CellSet cellSet) {
		IList<Object> rowsData = GamaListFactory.create();

		List<CellSetAxis> cellSetAxes = cellSet.getAxes();
		CellSetAxis columnsAxis = cellSetAxes.get(Axis.COLUMNS.axisOrdinal());
		CellSetAxis rowsAxis = cellSetAxes.get(Axis.ROWS.axisOrdinal());
		int cellOrdinal = 0;
		// if ( DEBUG ) {
		// List<Hierarchy> h = rowsAxis.getAxisMetaData().getHierarchies();
		// int n = h.size();
		// for ( int i = 0; i < n; ++i ) {
		// // scope.getGui().debug("MdxConnection.getRowsData.getCaption:" + h.get(i).getCaption());
		// }
		//
		// }

		for ( Position rowPosition : rowsAxis.getPositions() ) {
			IList<Object> row = GamaListFactory.create();
			IList<Object> rowMembers = GamaListFactory.create();
			// get member on each row
			for ( Member member : rowPosition.getMembers() ) {
				rowMembers.add(member.getName());
			}
			// get value of the cell in each column.
			IList<Object> cellValues = GamaListFactory.create();
			for ( Position columnPosition : columnsAxis.getPositions() ) {
				// Access the cell via its ordinal. The ordinal is kept in step
				// because we increment the ordinal once for each row and
				// column.
				Cell cell = cellSet.getCell(cellOrdinal);
				// Just for kicks, convert the ordinal to a list of coordinates.
				// The list matches the row and column positions.
				List<Integer> coordList = cellSet.ordinalToCoordinates(cellOrdinal);
				assert coordList.get(0) == rowPosition.getOrdinal();
				assert coordList.get(1) == columnPosition.getOrdinal();

				++cellOrdinal;
				cellValues.add(cell.getFormattedValue());
			}
			// Add member and value to row
			row.add(rowMembers);
			row.add(cellValues);
			// Add row to rowsData
			rowsData.add(row);
		}
		return rowsData;

	}

	/*
	 * Get all column names of OLAP query
	 */
	public IList<Object> getAllColummsName(final IList<Object> olapResult) {
		return (GamaList<Object>) olapResult.get(0);
	}

	/*
	 * Get all column names of OLAP query
	 */
	public Object getColummNameAt(final IList<Object> olapResult, final int cIndex) {
		return this.getAllColummsName(olapResult).get(cIndex);
	}

	/*
	 * Get all rows data
	 */
	public IList<Object> getAllRowsData(final IList<Object> olapResult) {
		return (GamaList<Object>) olapResult.get(1);
	}

	/*
	 * Get row data (row members + cell values) at row index(rIndex)
	 */
	public IList<Object> getRowDataAt(final IList<Object> olapResult, final int rIndex) {
		return (GamaList<Object>) getAllRowsData(olapResult).get(rIndex);
	}

	/*
	 * Get all row members at row(index)
	 */
	public IList<Object> getAllMembersAt(final IList<Object> olapResult, final int rIndex) {
		return (GamaList<Object>) getRowDataAt(olapResult, rIndex).get(0);
	}

	/*
	 * Get row member at row index:rIndex ,member index:mIndex)
	 */
	public Object getRowMemberAt(final IList<Object> olapResult, final int rIndex, final int mIndex) {
		return getAllMembersAt(olapResult, rIndex).get(mIndex);
	}

	/*
	 * Get all cell values at index row
	 */
	public IList<Object> getAllCellValuesAt(final IList<Object> olapResult, final int rIndex) {
		return (GamaList<Object>) getRowDataAt(olapResult, rIndex).get(1);
	}

	/*
	 * Get cell value at row index:rIndex ,cell index:cIndex)
	 */
	public Object getCellValueAt(final IList<Object> olapResult, final int rIndex, final int cIndex) {
		return getAllCellValuesAt(olapResult, rIndex).get(cIndex);
	}

	/*
	 * Get cubes of OlapConnection
	 */
	public NamedList<Cube> getCubes(final OlapConnection connection) {
		NamedList<Cube> cubes = null;
		try {
			cubes = connection.getOlapSchema().getCubes();

		} catch (OlapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cubes;
	}

	public void getCellSetMetaData(final CellSet cellSet) {
		CellSetMetaData cellSetMD = null;
		NamedList<CellSetAxisMetaData> cellSetAxisMD = null;
		NamedList<Property> properties = null;
		Cube cube = null;
		CellSetAxisMetaData filterAxisMD = null;
		try {
			cellSetMD = cellSet.getMetaData();
			cellSetAxisMD = cellSetMD.getAxesMetaData(); // MAP<K,V>
			properties = cellSetMD.getCellProperties();
			cube = cellSetMD.getCube();
			// print
			System.out.println("CellSetAxis Meta Data");
			int m = cellSetAxisMD.size();
			for ( int i = 0; i < m; i++ ) {
				CellSetAxisMetaData cellMD = cellSetAxisMD.get(i);
				List<Hierarchy> hierarchy = cellMD.getHierarchies();
				List<Property> property = cellMD.getProperties();
				System.out.println("Hierarchy");
				int n = hierarchy.size();
				for ( int j = 0; j < n; ++j ) {
					System.out.print(hierarchy.get(j).getName() + Strings.TAB);
				}
				System.out.println("\n Properties");
				n = property.size();
				for ( int j = 0; j < n; ++j ) {
					System.out.print(property.get(j).getName() + Strings.TAB);
				}

			}
			System.out.println("\n End Cell Set Meta Data ------------");
			System.out.println("Cell Set Axis Meta Data:" + cellSetAxisMD.iterator().toString());
			System.out.println("propertis Meta Data:" + properties.iterator().toString());
			System.out.println("cubes Meta Data:" + cube.toString());
		} catch (OlapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * print all row data
	 */
	// public void printOlapResul(GamaList<Object> rowsData){
	// int m=rowsData.size();
	// for (int i=0; i<m;++i){
	// GamaList<Object> row= (GamaList<Object>) rowsData.get(i);
	// GamaList<Object> members= (GamaList<Object>) row.get(0);
	// GamaList<Object> values= (GamaList<Object>) row.get(1);
	// // print member
	// int k = members.size();
	// for (int j=0;j<k;j++){
	// System.out.print(members.get(j).toString()+Strings.TAB);
	// }
	// //print value
	// int l = values.size();
	// for (int j=0;j<l;j++){
	// System.out.print(values.get(j).toString()+Strings.TAB);
	// }
	// System.out.println();
	// }
	// }
	//
	/*
	 * print all row data
	 */
	public void printRowsData(final GamaList<Object> olapResult) {

		int m = this.getAllRowsData(olapResult).size();
		for ( int rIndex = 0; rIndex < m; ++rIndex ) {
			System.out.print("row" + rIndex + ":" + Strings.TAB);
			// print member
			int k = this.getAllMembersAt(olapResult, rIndex).size();
			for ( int mIndex = 0; mIndex < k; mIndex++ ) {
				System.out.print(this.getRowMemberAt(olapResult, rIndex, mIndex).toString() + Strings.TAB);
			}
			// print value
			int l = this.getAllCellValuesAt(olapResult, rIndex).size();
			for ( int cIndex = 0; cIndex < l; ++cIndex ) {
				System.out.print(this.getCellValueAt(olapResult, rIndex, cIndex).toString() + Strings.TAB);
			}
			System.out.println();
		}
	}

	/*
	 * print all column names
	 */

	public void prinColumnsName(final IList<Object> olapResult) {
		int m = this.getAllColummsName(olapResult).size();
		for ( int cIndex = 0; cIndex < m; ++cIndex ) {
			System.out.print(this.getColummNameAt(olapResult, cIndex).toString() + Strings.TAB);
		}

	}

	public void prinCubesName(final NamedList<Cube> cubes) {
		int m = cubes.size();
		for ( int i = 0; i < m; ++i ) {
			List<Cube> cube = (List<Cube>) cubes.get(i);
			int n = cube.size();
			System.out.print(cube.get(i).toString() + Strings.TAB);
		}

	}

	public String parseMdx(String queryStr, final IList<Object> condition_values) throws GamaRuntimeException {

		int condition_count = condition_values.size();
		// set value for each condition
		for ( int i = 0; i < condition_count; i++ ) {

			queryStr = queryStr.replaceFirst("\\?", condition_values.get(i).toString());
		}

		// if ( DEBUG ) {
		// scope.getGui().debug("Parsed Mdx:" + queryStr);
		// }
		return queryStr;

	}

}// end class
