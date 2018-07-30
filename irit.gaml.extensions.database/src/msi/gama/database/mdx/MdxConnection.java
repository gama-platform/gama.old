/*********************************************************************************************
 *
 *
 * 'MdxConnection.java', in plugin 'msi.gama.core', is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.database.mdx;

import java.sql.SQLException;
import java.util.List;

import org.olap4j.Axis;
import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.CellSetAxisMetaData;
import org.olap4j.CellSetMetaData;
import org.olap4j.OlapConnection;
import org.olap4j.OlapException;
import org.olap4j.OlapStatement;
import org.olap4j.Position;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Member;
import org.olap4j.metadata.NamedList;
import org.olap4j.metadata.Property;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Strings;

/*
 * @Author TRUONG Minh Thai Fredric AMBLARD Benoit GAUDOU Christophe Sibertin-BLANC
 *
 *
 * SQLConnection: supports the method - connectDB: make a connection to DBMS. - selectDB: connect to DBMS and run
 * executeQuery to select data from DBMS. - executeUpdateDB: connect to DBMS and run executeUpdate to
 * update/insert/delete/drop/create data on DBMS.
 *
 * Created date: 18-Jan-2013 Modified: 03-05-2013: add selectMDB methods
 *
 * Last Modified: 02-07-2013
 */
@SuppressWarnings ({ "unchecked" })
public abstract class MdxConnection {

	// private static final boolean DEBUG = false; // Change DEBUG = false for
	// release version
	protected static final String MONDRIAN = "mondrian";
	protected static final String MONDRIANXMLA = "mondrian/xmla";
	protected static final String MSAS = "ssas/xmla"; // Micrsoft SQL Server
														// Analysis Services
	protected static final String MYSQL = "mysql";
	protected static final String POSTGRES = "postgres";
	protected static final String POSTGIS = "postgis";
	protected static final String MSSQL = "sqlserver";
	protected static final String SQLITE = "sqlite";

	protected static final String GEOMETRYTYPE = "GEOMETRY";
	protected static final String MYSQLDriver = new String("com.mysql.jdbc.Driver");
	// static final String MSSQLDriver = new
	// String("com.microsoft.sqlserver.jdbc.SQLServerDriver");
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
	public abstract OlapConnection connectMDB(IScope scope) throws GamaRuntimeException;

	/*
	 * Make a connection to Multidimensional Database Server
	 */
	public abstract OlapConnection connectMDB(IScope scope, String dbName) throws GamaRuntimeException;

	/*
	 * Make a connection to Multidimensional Database Server
	 */
	public abstract OlapConnection connectMDB(IScope scope, String dbName, String catalog) throws GamaRuntimeException;

	public void setConnection(final IScope scope) {
		this.olapConnection = this.connectMDB(scope);

	}

	public void setConnection(final OlapConnection oConn) {
		this.olapConnection = oConn;

	}

	public OlapConnection getConnection() {
		return this.olapConnection;
	}

	public boolean isConnected() {
		if (this.olapConnection != null) {
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

	public CellSet select(final IScope scope, final String selectComm) {
		CellSet resultCellSet = null;
		OlapConnection oConn = null;
		try {
			oConn = connectMDB(scope);
			resultCellSet = select(scope, oConn, selectComm);
			oConn.close();
		} catch (final SQLException e) {

		}
		return resultCellSet;
	}

	public CellSet select(final IScope scope, final String selectComm, final IList<Object> condition_values) {
		CellSet resultCellSet = null;
		OlapConnection oConn = null;
		try {
			// Connection conn = connectMDB();
			final String mdxStr = parseMdx(selectComm, condition_values);
			oConn = connectMDB(scope);
			resultCellSet = select(scope, oConn, mdxStr);
			oConn.close();
		} catch (final SQLException e) {

		}
		return resultCellSet;
	}

	public CellSet select(final IScope scope, final OlapConnection connection, final String selectComm)
			throws GamaRuntimeException {
		CellSet resultCellSet = null;
		OlapStatement statement;
		try {
			statement = connection.createStatement();
			resultCellSet = statement.executeOlapQuery(selectComm);

			statement.close();
			// connection.close();
		} catch (final OlapException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString(), scope);
		} catch (final SQLException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString(), scope);
		}
		return resultCellSet;
	}

	/*
	 * Select data source with connection was established
	 */
	public IList<Object> selectMDB(final IScope scope, final String selectComm, final IList<Object> condition_values) {
		final CellSet cellSet = select(scope, selectComm, condition_values);
		return cellSet2List(cellSet);
	}

	public IList<Object> selectMDB(final IScope scope, final String selectComm) {
		final CellSet cellSet = select(scope, selectComm);
		return cellSet2List(cellSet);
	}

	public IList<Object> selectMDB(final IScope scope, final OlapConnection connection, final String selectComm) {
		final CellSet cellSet = select(scope, connection, selectComm);
		return cellSet2List(cellSet);
	}

	public IList<Object> selectMDB(final IScope scope, final String onColumns, final String onRows, final String from) {
		final String mdxStr = "SELECT " + onColumns + " ON COLUMNS, " + onRows + " ON ROWS " + " FROM " + from;
		return selectMDB(scope, mdxStr);
	}

	public IList<Object> selectMDB(final IScope scope, final OlapConnection connection, final String onColumns,
			final String onRows, final String from) {
		final String mdxStr = "SELECT " + onColumns + " ON COLUMNS, " + onRows + " ON ROWS " + " FROM " + from;
		return selectMDB(scope, connection, mdxStr);
	}

	public IList<Object> selectMDB(final IScope scope, final String onColumns, final String onRows, final String from,
			final String where) {
		final String mdxStr =
				"SELECT " + onColumns + " ON COLUMNS, " + onRows + " ON ROWS " + " FROM " + from + " WHERE " + where;
		return selectMDB(scope, mdxStr);
	}

	public IList<Object> selectMDB(final IScope scope, final OlapConnection connection, final String onColumns,
			final String onRows, final String from, final String where) {
		final String mdxStr =
				"SELECT " + onColumns + " ON COLUMNS, " + onRows + " ON ROWS " + " FROM " + from + " WHERE " + where;
		return selectMDB(scope, connection, mdxStr);
	}

	/*
	 * Format of Olap query result (GamaList<Object>: Result of OLAP query is transformed to Gamalist<Object> with
	 * order: (0): GamaList<String>: List of column names. (1): GamaList<Object>: Row data. it contains List of list and
	 * it look like a matrix with structure: (0): the first row data (1): the second row data ... Each row data contains
	 * two element: (0): rowMembers (GamaList<String>: this is a list of members in the row. (1): cellValues
	 * (Gamalist<Object>): This is a list of values in cell column or (we can call measures)
	 */
	public IList<Object> cellSet2List(final CellSet cellSet) {
		final IList<Object> olapResult = GamaListFactory.create();
		olapResult.add(this.getColumnsName(cellSet));
		olapResult.add(this.getRowsData(cellSet));
		return olapResult;
	}

	protected IList<Object> getColumnsName(final CellSet cellSet) {
		final IList<Object> columnsName = GamaListFactory.create();
		final List<CellSetAxis> cellSetAxes = cellSet.getAxes();
		// get headings.
		final CellSetAxis columnsAxis = cellSetAxes.get(Axis.COLUMNS.axisOrdinal());
		for (final Position position : columnsAxis.getPositions()) {
			final Member measure = position.getMembers().get(0);
			columnsName.add(measure.getName());
		}
		return columnsName;

	}

	protected IList<Object> getRowsData(final CellSet cellSet) {
		final IList<Object> rowsData = GamaListFactory.create();

		final List<CellSetAxis> cellSetAxes = cellSet.getAxes();
		final CellSetAxis columnsAxis = cellSetAxes.get(Axis.COLUMNS.axisOrdinal());
		final CellSetAxis rowsAxis = cellSetAxes.get(Axis.ROWS.axisOrdinal());
		int cellOrdinal = 0;

		for (final Position rowPosition : rowsAxis.getPositions()) {
			final IList<Object> row = GamaListFactory.create();
			final IList<Object> rowMembers = GamaListFactory.create();
			// get member on each row
			for (final Member member : rowPosition.getMembers()) {
				rowMembers.add(member.getName());
			}
			// get value of the cell in each column.
			final IList<Object> cellValues = GamaListFactory.create();
			for (final Position columnPosition : columnsAxis.getPositions()) {
				// Access the cell via its ordinal. The ordinal is kept in step
				// because we increment the ordinal once for each row and
				// column.
				final Cell cell = cellSet.getCell(cellOrdinal);
				// Just for kicks, convert the ordinal to a list of coordinates.
				// The list matches the row and column positions.
				final List<Integer> coordList = cellSet.ordinalToCoordinates(cellOrdinal);
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

		} catch (final OlapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cubes;
	}

	public void getCellSetMetaData(final CellSet cellSet) {
		CellSetMetaData cellSetMD = null;
		NamedList<CellSetAxisMetaData> cellSetAxisMD = null;
		// NamedList<Property> properties = null;
		// Cube cube = null;
		// final CellSetAxisMetaData filterAxisMD = null;
		try {
			cellSetMD = cellSet.getMetaData();
			cellSetAxisMD = cellSetMD.getAxesMetaData(); // MAP<K,V>
			// properties = cellSetMD.getCellProperties();
			// cube = cellSetMD.getCube();
			// print
			// System.out.println("CellSetAxis Meta Data");
			final int m = cellSetAxisMD.size();
			for (int i = 0; i < m; i++) {
				final CellSetAxisMetaData cellMD = cellSetAxisMD.get(i);
				final List<Hierarchy> hierarchy = cellMD.getHierarchies();
				final List<Property> property = cellMD.getProperties();
				// System.out.println("Hierarchy");
				int n = hierarchy.size();
				for (int j = 0; j < n; ++j) {
					System.out.print(hierarchy.get(j).getName() + Strings.TAB);
				}
				// System.out.println("\n Properties");
				n = property.size();
				for (int j = 0; j < n; ++j) {
					System.out.print(property.get(j).getName() + Strings.TAB);
				}

			}
			// System.out.println("\n End Cell Set Meta Data ------------");
			// System.out.println("Cell Set Axis Meta Data:" +
			// cellSetAxisMD.iterator().toString());
			// System.out.println("propertis Meta Data:" +
			// properties.iterator().toString());
			// System.out.println("cubes Meta Data:" + cube.toString());
		} catch (final OlapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * print all column names
	 */

	public String parseMdx(String queryStr, final IList<Object> condition_values) throws GamaRuntimeException {

		final int condition_count = condition_values.size();
		// set value for each condition
		for (int i = 0; i < condition_count; i++) {

			queryStr = queryStr.replaceFirst("\\?", condition_values.get(i).toString());
		}

		// if ( DEBUG ) {
		// scope.getGui().debug("Parsed Mdx:" + queryStr);
		// }
		return queryStr;

	}

}// end class
