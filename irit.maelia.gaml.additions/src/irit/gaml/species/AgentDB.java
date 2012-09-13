package irit.gaml.species;

import java.sql.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.simulation.ISimulation;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.types.IType;

/*
 * species: The AgentDB is defined in this class. AgentDB supports the action
 * - connectDB: make a connection to DBMS.
 * - selectDB: executeQuery to select data from DBMS via current connection.
 * - executeUpdateDB: run executeUpdate to update/insert/delete/drop/create data on DBMS via current
 * connection.
 * 
 * @author TRUONG Minh Thai 22-Feb-2012
 * Last Modified: 05-Mar-2012
 */
@species(name = "AgentDB")
public class AgentDB extends GamlAgent {

	private Connection conn = null;
	// private Statement stat;
	static final boolean DEBUG = true; // Change DEBUG = false for release version

	public AgentDB(final ISimulation sim, final IPopulation s) throws GamaRuntimeException {
		super(sim, s);
	}

	/*
	 * Make a connection to BDMS
	 * 
	 * @syntax: do action: connectDB {
	 * arg dbtype value: vendorName; //MySQL/MSSQL
	 * arg url value: urlvalue;
	 * arg port value: portvaluse;
	 * arg database value: dbnamevalue;
	 * arg user value: usrnamevalue;
	 * arg passwd value: pwvaluse;
	 * }
	 */
	@action(name="connectDB")
	@args(names = { "dbtype", "url", "port", "database", "user", "passwd" })
	public Object connectDB(final IScope scope) throws GamaRuntimeException {
		String vendorName = (String) scope.getArg("dbtype", IType.STRING);
		String url = (String) scope.getArg("url", IType.STRING);
		String port = (String) scope.getArg("port", IType.STRING);
		String dbName = (String) scope.getArg("database", IType.STRING);
		String usrName = (String) scope.getArg("user", IType.STRING);
		String password = (String) scope.getArg("passwd", IType.STRING);
		String mySQLDriver = new String("com.mysql.jdbc.Driver");
		String msSQLDriver = new String("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		try {
			if ( vendorName.equalsIgnoreCase("MySQL") ) {
				Class.forName(mySQLDriver).newInstance();
				conn =
					DriverManager.getConnection("jdbc:mysql://" + url + ":" + port + "/" + dbName,
						usrName, password);
				// stat = conn.createStatement();
			} else if ( vendorName.equalsIgnoreCase("MSSQL") ) {
				Class.forName(msSQLDriver).newInstance();
				conn =
					DriverManager.getConnection("jdbc:sqlserver://" + url + ":" + port +
						";databaseName=" + dbName + ";user=" + usrName + ";password=" + password +
						";");
				// stat = conn.createStatement();
			} else {
				throw new GamaRuntimeException("SQLConnection.connectSQL: The " + vendorName +
					" is not supported!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GamaRuntimeException("SQLConnection.connectDB:" + e.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new GamaRuntimeException("SQLConnection.connectDB:" + e.toString());
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new GamaRuntimeException("SQLConnection.connectDB:" + e.toString());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new GamaRuntimeException("SQLConnection.connectDB:" + e.toString());
		}

		if ( DEBUG ) {
			GuiUtils.informConsole(vendorName + " Server at address " + url + " is connected");
		}
		return null;

	}

	/*
	 * Close the current connection
	 * 
	 * @syntax: do action: closeDB;
	 */
	@action(name="closeDB")
	@args(names = {})
	public Object closeDB(final IScope scope) throws GamaRuntimeException {

		try {
			// stat.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GamaRuntimeException("SQLConnection.connectDB:" + e.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GamaRuntimeException("SQLConnection.selectDB:" + e.toString());
		}
		if ( DEBUG ) {
			GuiUtils.informConsole("The connection to DBMS is close");
		}
		return null;

	}

	/*
	 * Execute the select statement
	 * 
	 * @syntax do action: selectDB {
	 * arg selectComm value: selectStatement;
	 * }
	 * 
	 * @return GamaList<GamaList<Object>>
	 */

	@action(name="selectDB")
	@args(names = { "select" })
	public GamaList<GamaList<Object>> selectDB(final IScope scope) throws GamaRuntimeException {
		String selectComm = (String) scope.getArg("select", IType.STRING);

		ResultSet rs;
		GamaList<Object> rowList = new GamaList<Object>();
		GamaList<GamaList<Object>> repRequest = new GamaList<GamaList<Object>>();

		try {
			rs = conn.createStatement().executeQuery(selectComm);
			ResultSetMetaData rsmd = rs.getMetaData();
			int nbCol = rsmd.getColumnCount();

			while (rs.next()) {
				rowList = new GamaList<Object>();
				for ( int i = 1; i <= nbCol; i++ ) {
					rowList.add(rs.getObject(i));
				}
				repRequest.add(rowList);
			}
			rs.close();
			// conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GamaRuntimeException("SQLConnection.selectDB:" + e.toString());
		}

		if ( DEBUG ) {
			GuiUtils.informConsole(selectComm + " was run");
		}
		return repRequest;

	}

	/*
	 * executeUpdate executes the statements (update/insert/delete/create/drop)
	 * 
	 * @syntax: do action: executeUpdateDB {
	 * arg updateComm value: updateStatement;
	 * }
	 */

	@action(name="executeUpdateDB")
	@args(names = { "updateComm" })
	public int executeUpdateDB(final IScope scope) throws GamaRuntimeException {
		String updateComm = (String) scope.getArg("updateComm", IType.STRING);
		int n = 0;
		try {
			// conn = connectMySQL(vendorName,url,port,dbName,usrName,password);
			n = conn.createStatement().executeUpdate(updateComm);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GamaRuntimeException("SQLConnection.selectDB:" + e.toString());
		}

		if ( DEBUG ) {
			GuiUtils.informConsole(updateComm + " was run");
		}

		return n;

	}

}
