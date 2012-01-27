package irit.gaml.skills;
/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Benoit Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import msi.gama.common.util.GuiUtils;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.compilation.GamlException;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;


/** 
 * MAELIASkill: This class is intended to define additional actions for MAELIA dedicated agents.
 *
 * @author Benoit Gaudou 17 jan. 12
 */

@skill({ "MAELIA" })
public class MAELIASkill extends Skill {

	@action("maeliaWrite")
	@args({})	
	public Object maelwrite(final IScope scope) throws GamaRuntimeException {
		GuiUtils.informConsole("HelloWorld");
		return null;
	}

	
	@action("maeliaInterrogateDB") 
	@args({ "request", "DBName" })
	public GamaList<GamaList<Object>> interrogateDB(final IScope scope) throws GamaRuntimeException {
	
		String DBName = (String) scope.getArg("DBName", IType.STRING);
		String request = (String) scope.getArg("request", IType.STRING);
		
		String DBRelativeLocation = 
				scope.getSimulationScope().getModel().getRelativeFilePath(DBName, true);
		
		String subprotocol = new String("sqlite");
		String urlConnection = new String("jdbc:"+subprotocol+":"+DBRelativeLocation);
		
		GamaList<GamaList<Object>> repRequest = new GamaList<GamaList<Object>>();
		GamaList<Object> rowList = new GamaList<Object>();
		
		Connection conn;
		Statement stat;
		ResultSet rs;		
		
		try {
			Class.forName("org.sqlite.JDBC");

			conn = DriverManager.getConnection(urlConnection);
			
			stat = conn.createStatement();
			rs = stat.executeQuery(request);	
			
			ResultSetMetaData rsmd = rs.getMetaData();
			int nbCol = rsmd.getColumnCount();
		    			
			while (rs.next()) {
				rowList = new GamaList<Object>();
				for(int i = 1;i<=nbCol;i++){
					rowList.add(rs.getObject(i));
				}
				repRequest.add(rowList);
			}
			
			rs.close();
			stat.close();
			conn.close();				

			} catch (ClassNotFoundException e) {
				System.out.println("maeliaInterrogateDB : JDBC connector not found");
				e.printStackTrace();
				throw new GamaRuntimeException("JDBC connector not found");
			} catch (SQLException e) {
				e.printStackTrace();
				throw new GamaRuntimeException("SQL error: check the database name or the SQL request");
			}			
		return repRequest;
	}

//	@action("createDB")
//	@args({ "DBName", "csv", "" })
//	public void createDB(final IScope scope) throws GamaRuntimeException, ClassNotFoundException, SQLException {
//
//		String DBName = (String) scope.getArg("DBName", IType.STRING);
//		String csvFile = (String) scope.getArg("csv", IType.STRING);
//		
//		Connection conn;
//		
//		Class.forName("org.sqlite.JDBC");
//		conn = DriverManager.getConnection("jdbc:sqlite:meteo.db");
//
//		
//		Statement stat = conn.createStatement();
//		stat.executeUpdate("drop table if exists points;");
//		stat.executeUpdate("create table points (id_point, month, day, temp_min);");
//		PreparedStatement prep = conn.prepareStatement(
//		"insert into points values (?, ?, ?, ?);");
//
//		BufferedReader buffer;
//		buffer = new BufferedReader(new FileReader("meteo2009.csv"));
//
//		//On saute la premi�?re ligne
//		buffer.readLine();
//		//On traite les autres
//		String line = null;
//		while ((line = buffer.readLine()) != null) {
//
//			/*
//			 * Extraction des valeurs des champs
//			 */
//			String[] fields = line.split(";");
//			String id = fields[0];
//			String sdate = fields[1];
//			String[] tab_sdate = sdate.split("/");
//			String day = tab_sdate[0];
//			String month = tab_sdate[1];
//			String temp = fields[3];
//
//			/*
//			 * PrŽparation du nouvel enregistrement
//			 */
//			prep.setString(1, id);
//			prep.setString(2, month);
//			prep.setString(3, day);
//			prep.setString(4, temp);
//			prep.addBatch();
//		}
//		
//		buffer.close();
//
//		/*
//		 * Remplissage de la BD en mode batch
//		 */
//		conn.setAutoCommit(false);
//		prep.executeBatch();
//		conn.setAutoCommit(true);
//		
//		throw new GamaRuntimeException("Pas encore implémenté : interrogateDB");
//		// return null;
//	}
	
}

