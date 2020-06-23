/*********************************************************************************************
 *
 *
 * 'MdxConnection.java', in plugin 'msi.gama.core', is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
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
import org.olap4j.OlapConnection;
import org.olap4j.OlapException;
import org.olap4j.OlapStatement;
import org.olap4j.Position;
import org.olap4j.metadata.Member;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;

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

	protected static final String MONDRIAN = "mondrian";
	protected static final String MONDRIANXMLA = "mondrian/xmla";
	protected static final String MSAS = "ssas/xmla"; // Micrsoft SQL Server
														// Analysis Services
	protected static final String MYSQL = "mysql";
	protected static final String POSTGRES = "postgres";
	protected static final String POSTGIS = "postgis";
	protected static final String MSSQL = "sqlserver";
	protected static final String SQLITE = "sqlite";

	protected static final String MYSQLDriver = new String("com.mysql.jdbc.Driver");
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

	MdxConnection(final String vender) {
		this.vender = vender;
	}

	MdxConnection(final String venderName, final String database) {
		this.vender = venderName;
		this.dbName = database;
	}

	MdxConnection(final String venderName, final String url, final String port, final String dbName,
			final String userName, final String password) {
		this.vender = venderName;
		this.url = url;
		this.port = port;
		this.dbName = dbName;
		this.userName = userName;
		this.password = password;
	}

	MdxConnection(final String venderName, final String url, final String port, final String dbName,
			final String catalog, final String userName, final String password) {
		this.vender = venderName;
		this.url = url;
		this.port = port;
		this.dbName = dbName;
		this.catalog = catalog;
		this.userName = userName;
		this.password = password;
	}

	MdxConnection(final String venderName, final String dbtype, final String url, final String port,
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

	public String getURL() {
		return this.url;
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

	public void setURL(final String url) {
		this.url = url;
	}

	public void setCatalog(final String catalog) {
		this.catalog = catalog;
	}

	public void setUser(final String userName) {
		this.userName = userName;
	}

	/*
	 * Select data source with connection was established
	 */

	private CellSet select(final IScope scope, final String selectComm) {
		CellSet resultCellSet = null;
		try (OlapConnection oConn = connectMDB(scope);) {
			resultCellSet = select(scope, oConn, selectComm);
		} catch (final SQLException e) {

		}
		return resultCellSet;
	}

	private CellSet select(final IScope scope, final String selectComm, final IList<Object> condition_values) {
		CellSet resultCellSet = null;
		try (OlapConnection oConn = connectMDB(scope);) {
			final String mdxStr = parseMdx(selectComm, condition_values);
			resultCellSet = select(scope, oConn, mdxStr);
		} catch (final SQLException e) {

		}
		return resultCellSet;
	}

	private CellSet select(final IScope scope, final OlapConnection connection, final String selectComm)
			throws GamaRuntimeException {
		CellSet resultCellSet = null;
		try (OlapStatement statement = connection.createStatement();) {
			resultCellSet = statement.executeOlapQuery(selectComm);

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

	/*
	 * Format of Olap query result (GamaList<Object>: Result of OLAP query is transformed to Gamalist<Object> with
	 * order: (0): GamaList<String>: List of column names. (1): GamaList<Object>: Row data. it contains List of list and
	 * it look like a matrix with structure: (0): the first row data (1): the second row data ... Each row data contains
	 * two element: (0): rowMembers (GamaList<String>: this is a list of members in the row. (1): cellValues
	 * (Gamalist<Object>): This is a list of values in cell column or (we can call measures)
	 */
	private IList<Object> cellSet2List(final CellSet cellSet) {
		final IList<Object> olapResult = GamaListFactory.create();
		olapResult.add(this.getColumnsName(cellSet));
		olapResult.add(this.getRowsData(cellSet));
		return olapResult;
	}

	private IList<Object> getColumnsName(final CellSet cellSet) {
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

	private IList<Object> getRowsData(final CellSet cellSet) {
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
	 * print all column names
	 */

	private String parseMdx(final String str, final IList<Object> condition_values) throws GamaRuntimeException {
		String queryStr = str;
		final int condition_count = condition_values.size();
		// set value for each condition
		for (int i = 0; i < condition_count; i++) {

			queryStr = queryStr.replaceFirst("\\?", condition_values.get(i).toString());
		}

		return queryStr;

	}

}// end class
