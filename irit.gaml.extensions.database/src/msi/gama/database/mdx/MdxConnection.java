/*******************************************************************************************************
 *
 * MdxConnection.java, in irit.gaml.extensions.database, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
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

/**
 * The Class MdxConnection.
 */
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

	/** The Constant MONDRIAN. */
	protected static final String MONDRIAN = "mondrian";
	
	/** The Constant MONDRIANXMLA. */
	protected static final String MONDRIANXMLA = "mondrian/xmla";
	
	/** The Constant MSAS. */
	protected static final String MSAS = "ssas/xmla"; // Micrsoft SQL Server
														
														/** The Constant MYSQL. */
														// Analysis Services
	protected static final String MYSQL = "mysql";
	
	/** The Constant POSTGRES. */
	protected static final String POSTGRES = "postgres";
	
	/** The Constant POSTGIS. */
	protected static final String POSTGIS = "postgis";
	
	/** The Constant MSSQL. */
	protected static final String MSSQL = "sqlserver";
	
	/** The Constant SQLITE. */
	protected static final String SQLITE = "sqlite";

	/** The Constant MYSQLDriver. */
	protected static final String MYSQLDriver = new String("com.mysql.jdbc.Driver");
	
	/** The Constant MSSQLDriver. */
	protected static final String MSSQLDriver = new String("net.sourceforge.jtds.jdbc.Driver");
	
	/** The Constant SQLITEDriver. */
	protected static final String SQLITEDriver = new String("org.sqlite.JDBC");
	
	/** The Constant POSTGRESDriver. */
	protected static final String POSTGRESDriver = new String("org.postgresql.Driver");

	/** The vender. */
	protected String vender = "";
	
	/** The dbtype. */
	protected String dbtype = POSTGRES;
	
	/** The url. */
	protected String url = "";
	
	/** The port. */
	protected String port = "";
	
	/** The db name. */
	protected String dbName = "";
	
	/** The catalog. */
	protected String catalog = "";
	
	/** The user name. */
	protected String userName = "";
	
	/** The password. */
	protected String password = "";

	/** The olap connection. */
	protected OlapConnection olapConnection;

	/**
	 * Instantiates a new mdx connection.
	 *
	 * @param vender the vender
	 */
	MdxConnection(final String vender) {
		this.vender = vender;
	}

	/**
	 * Instantiates a new mdx connection.
	 *
	 * @param venderName the vender name
	 * @param database the database
	 */
	MdxConnection(final String venderName, final String database) {
		this.vender = venderName;
		this.dbName = database;
	}

	/**
	 * Instantiates a new mdx connection.
	 *
	 * @param venderName the vender name
	 * @param url the url
	 * @param port the port
	 * @param dbName the db name
	 * @param userName the user name
	 * @param password the password
	 */
	MdxConnection(final String venderName, final String url, final String port, final String dbName,
			final String userName, final String password) {
		this.vender = venderName;
		this.url = url;
		this.port = port;
		this.dbName = dbName;
		this.userName = userName;
		this.password = password;
	}

	/**
	 * Instantiates a new mdx connection.
	 *
	 * @param venderName the vender name
	 * @param url the url
	 * @param port the port
	 * @param dbName the db name
	 * @param catalog the catalog
	 * @param userName the user name
	 * @param password the password
	 */
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

	/**
	 * Instantiates a new mdx connection.
	 *
	 * @param venderName the vender name
	 * @param dbtype the dbtype
	 * @param url the url
	 * @param port the port
	 * @param dbName the db name
	 * @param catalog the catalog
	 * @param userName the user name
	 * @param password the password
	 */
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

	/**
	 * Connect MDB.
	 *
	 * @param scope the scope
	 * @return the olap connection
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	/*
	 * Make a connection to Multidimensional Database Server
	 */
	public abstract OlapConnection connectMDB(IScope scope) throws GamaRuntimeException;

	/**
	 * Connect MDB.
	 *
	 * @param scope the scope
	 * @param dbName the db name
	 * @return the olap connection
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	/*
	 * Make a connection to Multidimensional Database Server
	 */
	public abstract OlapConnection connectMDB(IScope scope, String dbName) throws GamaRuntimeException;

	/**
	 * Connect MDB.
	 *
	 * @param scope the scope
	 * @param dbName the db name
	 * @param catalog the catalog
	 * @return the olap connection
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	/*
	 * Make a connection to Multidimensional Database Server
	 */
	public abstract OlapConnection connectMDB(IScope scope, String dbName, String catalog) throws GamaRuntimeException;

	/**
	 * Sets the connection.
	 *
	 * @param scope the new connection
	 */
	public void setConnection(final IScope scope) {
		this.olapConnection = this.connectMDB(scope);

	}

	/**
	 * Sets the connection.
	 *
	 * @param oConn the new connection
	 */
	public void setConnection(final OlapConnection oConn) {
		this.olapConnection = oConn;

	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	public OlapConnection getConnection() {
		return this.olapConnection;
	}

	/**
	 * Checks if is connected.
	 *
	 * @return true, if is connected
	 */
	public boolean isConnected() {
		if (this.olapConnection != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets the vender.
	 *
	 * @return the vender
	 */
	public String getVender() {
		return this.vender;
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getURL() {
		return this.url;
	}

	/**
	 * Gets the catalog.
	 *
	 * @return the catalog
	 */
	public String getCatalog() {
		return this.catalog;
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public String getUser() {
		return this.userName;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Sets the vender.
	 *
	 * @param vender the new vender
	 */
	public void setVender(final String vender) {
		this.vender = vender;
	}

	/**
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setURL(final String url) {
		this.url = url;
	}

	/**
	 * Sets the catalog.
	 *
	 * @param catalog the new catalog
	 */
	public void setCatalog(final String catalog) {
		this.catalog = catalog;
	}

	/**
	 * Sets the user.
	 *
	 * @param userName the new user
	 */
	public void setUser(final String userName) {
		this.userName = userName;
	}

	/*
	 * Select data source with connection was established
	 */

	/**
	 * Select.
	 *
	 * @param scope the scope
	 * @param selectComm the select comm
	 * @return the cell set
	 */
	private CellSet select(final IScope scope, final String selectComm) {
		CellSet resultCellSet = null;
		try (OlapConnection oConn = connectMDB(scope);) {
			resultCellSet = select(scope, oConn, selectComm);
		} catch (final SQLException e) {

		}
		return resultCellSet;
	}

	/**
	 * Select.
	 *
	 * @param scope the scope
	 * @param selectComm the select comm
	 * @param condition_values the condition values
	 * @return the cell set
	 */
	private CellSet select(final IScope scope, final String selectComm, final IList<Object> condition_values) {
		CellSet resultCellSet = null;
		try (OlapConnection oConn = connectMDB(scope);) {
			final String mdxStr = parseMdx(selectComm, condition_values);
			resultCellSet = select(scope, oConn, mdxStr);
		} catch (final SQLException e) {

		}
		return resultCellSet;
	}

	/**
	 * Select.
	 *
	 * @param scope the scope
	 * @param connection the connection
	 * @param selectComm the select comm
	 * @return the cell set
	 * @throws GamaRuntimeException the gama runtime exception
	 */
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

	/**
	 * Select MDB.
	 *
	 * @param scope the scope
	 * @param selectComm the select comm
	 * @param condition_values the condition values
	 * @return the i list
	 */
	/*
	 * Select data source with connection was established
	 */
	public IList<Object> selectMDB(final IScope scope, final String selectComm, final IList<Object> condition_values) {
		final CellSet cellSet = select(scope, selectComm, condition_values);
		return cellSet2List(cellSet);
	}

	/**
	 * Select MDB.
	 *
	 * @param scope the scope
	 * @param selectComm the select comm
	 * @return the i list
	 */
	public IList<Object> selectMDB(final IScope scope, final String selectComm) {
		final CellSet cellSet = select(scope, selectComm);
		return cellSet2List(cellSet);
	}

	/**
	 * Cell set 2 list.
	 *
	 * @param cellSet the cell set
	 * @return the i list
	 */
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

	/**
	 * Gets the columns name.
	 *
	 * @param cellSet the cell set
	 * @return the columns name
	 */
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

	/**
	 * Gets the rows data.
	 *
	 * @param cellSet the cell set
	 * @return the rows data
	 */
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

	/**
	 * Parses the mdx.
	 *
	 * @param str the str
	 * @param condition_values the condition values
	 * @return the string
	 * @throws GamaRuntimeException the gama runtime exception
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
