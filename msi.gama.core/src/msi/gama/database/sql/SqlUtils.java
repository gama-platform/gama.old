package msi.gama.database.sql;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import msi.gama.common.util.GuiUtils;
import msi.gama.database.sql.SqlConnection;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.types.IType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
/*
 * @Author  
 *     TRUONG Minh Thai
 *     Fredric AMBLARD
 *     Benoit GAUDOU
 *     Christophe Sibertin-BLANC
 * Created date: 19-Apr-2013
 * Modified:  
 *    18-July-2013:  
 *      Add load extension library for SQLITE case.
 * Last Modified: 18-July-2013
*/
public class SqlUtils {
	private static boolean DEBUG =false;
	
	public static SqlConnection createConnectionObject(final IScope scope,final Map<String, Object> params) throws GamaRuntimeException 
	{
		String dbtype = (String) params.get("dbtype");
		String host = (String) params.get("host");
		String port = (String) params.get("port");
		String database = (String) params.get("database");
		String user = (String) params.get("user");
		String passwd = (String) params.get("passwd");
		String extension = (String) params.get("extension");
		
		if (DEBUG){
			GuiUtils.debug("SqlUtils.createConnection:"+dbtype+" - "+host+" - "+ port+" - "+database+" - ");
		}
		SqlConnection sqlConn;
		// create connection
		if ( dbtype.equalsIgnoreCase(SqlConnection.SQLITE) ) {
			String DBRelativeLocation = scope.getSimulationScope().getModel().getRelativeFilePath(database, true);
			if (extension!=null){
				String EXTRelativeLocation = scope.getSimulationScope().getModel().getRelativeFilePath(extension, true);
				sqlConn = new SqliteConnection(dbtype, DBRelativeLocation,EXTRelativeLocation);
				
			}else{
				sqlConn = new SqliteConnection(dbtype, DBRelativeLocation);
			}
		} else if ( dbtype.equalsIgnoreCase(SqlConnection.MSSQL) ) {
			sqlConn = new MSSQLConnection(dbtype, host, port, database, user, passwd);
		}else if ( dbtype.equalsIgnoreCase(SqlConnection.MYSQL) ) {
			sqlConn = new MySqlConnection(dbtype, host, port, database, user, passwd);
		}else if ( dbtype.equalsIgnoreCase(SqlConnection.POSTGRES) ||
				   dbtype.equalsIgnoreCase(SqlConnection.POSTGIS)){
			sqlConn = new PostgresConnection(dbtype, host, port, database, user, passwd);
		}else {
			throw GamaRuntimeException.error("GAMA does not support: " + dbtype);
		}
		if (DEBUG){
			GuiUtils.debug("SqlUtils.createConnection:"+sqlConn.toString());
		}

		return sqlConn;
	}
	
	public static SqlConnection createConnectionObject(final IScope scope) throws GamaRuntimeException 
	{
		java.util.Map params = (java.util.Map) scope.getArg("params", IType.MAP);
		boolean transform = scope.hasArg("transform") ? (Boolean) scope.getArg("transform", IType.BOOL) : false;
		String dbtype = (String) params.get("dbtype");
		String host = (String) params.get("host");
		String port = (String) params.get("port");
		String database = (String) params.get("database");
		String user = (String) params.get("user");
		String passwd = (String) params.get("passwd");
		String extension = (String) params.get("extension");

		if (DEBUG){
			GuiUtils.debug("SqlUtils.createConnection:"+dbtype+" - "+host+" - "+ port+" - "+database+" - ");
		}
		SqlConnection sqlConn;
		// create connection
		if ( dbtype.equalsIgnoreCase(SqlConnection.SQLITE) ) {
			String DBRelativeLocation = scope.getSimulationScope().getModel().getRelativeFilePath(database, true);
			if (extension!=null){
				String EXTRelativeLocation = scope.getSimulationScope().getModel().getRelativeFilePath(extension, true);
				sqlConn = new SqliteConnection(dbtype, DBRelativeLocation,EXTRelativeLocation,transform);
				
			}else{
				sqlConn = new SqliteConnection(dbtype, DBRelativeLocation,transform);
			}
		} else if ( dbtype.equalsIgnoreCase(SqlConnection.MSSQL) ) {
			
			sqlConn = new MSSQLConnection(dbtype, host, port, database, user, passwd,transform);
		}else if ( dbtype.equalsIgnoreCase(SqlConnection.MYSQL) ) {
			
			sqlConn = new MySqlConnection(dbtype, host, port, database, user, passwd,transform);
		}else if ( dbtype.equalsIgnoreCase(SqlConnection.POSTGRES) ||
				   dbtype.equalsIgnoreCase(SqlConnection.POSTGIS)) {
			sqlConn = new PostgresConnection(dbtype, host, port, database, user, passwd,transform);
		}else {
			throw GamaRuntimeException.error("GAMA does not support: " + dbtype);
		}
		if (DEBUG){
			GuiUtils.debug("SqlUtils.createConnection:"+sqlConn.toString());
		}

		return sqlConn;
	}


	
	/*
	 * @Method: read(byte [] b)
	 * @Description: Convert Binary to Geometry (MSSQL,Sqlite, Postgres cases)
	 * 
	 * @param byte [] b
	 * 
	 * @return Geometry
	 * 
	 * @throws IOException, ParseException
	 */
	public static Geometry read(byte [] b) throws IOException, ParseException
	{
		WKBReader wkb= new WKBReader();
		Geometry geom=wkb.read(b);
		return geom;
	}
	/*
	 * @Method: Binary2Geometry(byte [] geometryAsBytes )
	 * @description: Convert binary to Geometry 
	 * @param byte []
	 * 
	 * @return Geometry
	 * 
	 * @throws ParseException
	 */
	public static Geometry Binary2Geometry(byte [] geometryAsBytes ) throws ParseException
	{
		byte[] wkb = new byte[geometryAsBytes.length - 4];
		System.arraycopy(geometryAsBytes, 4, wkb, 0, wkb.length);
		WKBReader wkbReader = new WKBReader();
		Geometry geom=wkbReader.read(wkb);		
		return geom;
	}
	/*
	 * @Method: InputStream2Geometry(InputStream inputStream)
	 * @Description: Convert Binary to Geometry (MySQL case)
	 * 
	 * @param InputStream inputStream
	 * 
	 * @return Geometry
	 * 
	 * @throws Exception
	 */	
	public static Geometry InputStream2Geometry(InputStream inputStream) throws Exception 
	{		 
		     Geometry dbGeometry = null;
		     if (inputStream != null) 
		     {		 
		         //convert the stream to a byte[] array
		         //so it can be passed to the WKBReader
		         byte[] buffer = new byte[255];
		         int bytesRead = 0;
		         ByteArrayOutputStream baos = new ByteArrayOutputStream();
		         while ((bytesRead = inputStream.read(buffer)) != -1) {
		             baos.write(buffer, 0, bytesRead);
		         }
		 
		         byte[] geometryAsBytes = baos.toByteArray();
		 
		         if (geometryAsBytes.length < 5) {
		             throw new Exception("Invalid geometry inputStream - less than five bytes");
		         }
		 
		         //first four bytes of the geometry are the SRID,
		         //followed by the actual WKB.  Determine the SRID
		         //here
		         byte[] sridBytes = new byte[4];
		         System.arraycopy(geometryAsBytes, 0, sridBytes, 0, 4);
		         boolean bigEndian = (geometryAsBytes[4] == 0x00);
		 
		         int srid = 0;
		         if (bigEndian) {
		            for (int i = 0; i < sridBytes.length; i++) {
		               srid = (srid << 8) + (sridBytes[i] & 0xff);
		            }
		         } else {
		            for (int i = 0; i < sridBytes.length; i++) {
		              srid += (sridBytes[i] & 0xff) << (8 * i);
		            }
		         }
		 
		         //use the JTS WKBReader for WKB parsing
		         WKBReader wkbReader = new WKBReader();
		 
		         //copy the byte array, removing the first four
		         //SRID bytes
		         byte[] wkb = new byte[geometryAsBytes.length - 4];
		         System.arraycopy(geometryAsBytes, 4, wkb, 0, wkb.length);
		         dbGeometry = wkbReader.read(wkb);
		         dbGeometry.setSRID(srid);
		     }
		 
		     return dbGeometry;			 
	}

}
