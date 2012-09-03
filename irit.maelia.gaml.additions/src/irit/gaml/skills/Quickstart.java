package irit.gaml.skills;

//docs start source
/*
*    GeoTools - The Open Source Java GIS Tookit
*    http://geotools.org
*
*    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
*
*    This file is hereby placed into the Public Domain. This means anyone is
*    free to do whatever they wish with this file. Use it well and enjoy!
*/


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import msi.gama.util.GamaList;

import org.geotools.data.db2.DB2WKBReader;
import org.geotools.data.teradata.WKBAttributeIO;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.InStream;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;

/**
* GeoTools Quickstart demo application. Prompts the user for a shapefile
* and displays its contents on the screen in a map frame
*
*/
public class Quickstart {
  static boolean debug=true;
  static String driver;
 /**
  * GeoTools Quickstart demo application. Prompts the user for a shapefile
  * and displays its contents on the screen in a map frame
  */    
 public static void main(String[] args) throws Exception {
     // display a data store file chooser dialog for shapefiles
    /*
	 File file = JFileDataStoreChooser.showOpenFile("shp", null);
     if (file == null) {
         return;
     }
     
*/ 
	 Quickstart qs = new Quickstart();
	 ResultSet rs;
	 //Connection conn = qs.connectMySQL("MySQL","127.0.0.1","3306","BPH","root","");
	 //rs = (ResultSet) conn.createStatement().executeQuery("select id_1,AsBinary(geometry)  from VNM_ADM1");
	 //rs = (ResultSet) conn.createStatement().executeQuery("select varname_1,AsWKB(geometry) as geo  from VNM_ADM1");
	 Connection conn = qs.connectMySQL("MSSQL","193.49.54.165","1433","BPH","sa","tmt");
	 rs = (ResultSet) conn.createStatement().executeQuery("select id_1, name_1,geom.STAsBinary()  from VNM_ADM1");
	 //printData(rs);
	 //ResultSetMetaData rsmd= rs.getMetaData();
	 //List<Integer>  geoColumn = getGeometryColumns(rsmd);

	// convert Geometry in SQL to Geometry type in GeoTool
	System.out.print("Class"+ conn.getClass())	;
	 GamaList<GamaList<Object>> repRequest = resultSet2GamaList(rs.getMetaData(),rs);	
		
		System.out.println("number of row:"+repRequest.length());
		
		
		/*
		for ( int i=0;i<repRequest.length();i++){
			//GamaList<Object> rowList = new GamaList<Object>();
			//rowList=repRequest.get(i);
			
			System.out.println("number of col:"+rowList.length());
			System.out.print("row ["+i+"]: ");
			for (int j=0 ; j<rowList.length(); j++)
				System.out.print(rowList.get(j) +" - ");
			
			System.out.println();
		}
		
		*/	
 }

 static private void printData(ResultSet rs) throws SQLException, IOException
 {
	 int index=0;
	 while (rs.next()){
		 index++;
		 Geometry geo=(new WKBAttributeIO()).read(rs, 2);
		 /*
		 Geometry geo;
		try {
			geo = getGeometryFromInputStream(rs.getBinaryStream(1));
			System.out.println("geometry value["+index+"]: "+geo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		 
	 }
 }
 
static private Geometry getGeometryFromInputStream(InputStream inputStream) throws Exception {
	 
     Geometry dbGeometry = null;
 
     if (inputStream != null) {
 
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
 
	public Connection connectMySQL(final String vendorName, final String url, final String port,
			final String dbName, final String usrName, final String password)
			throws ClassNotFoundException, InstantiationException, SQLException, IllegalAccessException {
			Connection conn = null;
			String mySQLDriver = new String("com.mysql.jdbc.Driver");
			String msSQLDriver = new String("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			try {
				if ( vendorName.equalsIgnoreCase("MySQL") ) {
					Class.forName(mySQLDriver).newInstance();
					conn =
						(Connection) DriverManager.getConnection("jdbc:mysql://" + url + ":" + port + "/" + dbName,
							usrName, password);
				} else if ( vendorName.equalsIgnoreCase("MSSQL") ) {
					Class.forName(msSQLDriver).newInstance();
					conn =
						(Connection) DriverManager.getConnection("jdbc:sqlserver://" + url + ":" + port +
							";databaseName=" + dbName + ";user=" + usrName + ";password=" + password +
							";");
				} else {
					throw new ClassNotFoundException("SQLConnection.connectSQL: The " + vendorName +
						"is not supported!");
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new ClassNotFoundException(e.toString());
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new InstantiationException(e.toString());
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new IllegalAccessException(e.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new SQLException(e.toString());
			}
			return conn;
	}
	
	
	/*
     * Convert RecordSet to GamaList
     * 
     * @param byte []
     * 
     * @return Geometry
     * 
     * @throws ParseException
 	 */
	static GamaList<GamaList<Object>> resultSet2GamaList(ResultSetMetaData rsmd, ResultSet rs)
	{
		// convert Geometry in SQL to Geometry type in GeoTool
		
		GamaList<GamaList<Object>> repRequest = new GamaList<GamaList<Object>>();
		try{
			List<Integer>  geoColumn = getGeometryColumns(rsmd);
			int nbCol = rsmd.getColumnCount();
			int i=1;
			while (rs.next())
			{
				//InputStream inputStream = rs.getBinaryStream(i);
				GamaList<Object> rowList = new GamaList<Object>();
				for ( int j = 1; j <= nbCol; j++ ) 
				{
					// check column is geometry column?
					if (geoColumn.contains(j)){
						if (debug) System.out.println("convert at ["+ i+","+j+"]: ");
						//rowList.add(Binary2Geometry(rs.getBytes(j)));
						//rowList.add(read(rs,j));
						//rowList.add(getGeometryFromInputStream(rs.getBinaryStream(j)));
						rowList.add(read((byte[]) rs.getObject(j)));
						System.out.println("geometry:"+getGeometryFromInputStream(rs.getBinaryStream(j)) );
					}
					else
						rowList.add(rs.getObject(j));
				}
				repRequest.add(rowList);
				i++;
			}
		}
		catch (Exception e)
		{
			
		}
		return repRequest;
					
	}
	static Geometry read(byte [] b) throws IOException, ParseException
	{
		//WKBAttributeIO wkb=new WKBAttributeIO();
		WKBReader wkb= new WKBReader();
		Geometry geom=wkb.read(b);
		return geom;
	}
	/*
     * Convert binary to Geometry
     * 
     * @param byte []
     * 
     * @return Geometry
     * 
     * @throws ParseException
 	 */
	static Geometry Binary2Geometry(byte [] geometryAsBytes ) throws ParseException
	{
		 byte[] wkb = new byte[geometryAsBytes.length - 4];
		 System.arraycopy(geometryAsBytes, 4, wkb, 0, wkb.length);
		 WKBReader wkbReader = new WKBReader();
		 Geometry geo=wkbReader.read(wkb);	
		 return geo;
	}
	/*
     * Get columns id of field with geometry type
     * 
     * @param ResultSetMetaData
     * 
     * @return List<Integer>
     * 
     * @throws SQLException
 	 */
	static List<Integer> getGeometryColumns(ResultSetMetaData rsmd)
	throws SQLException
	{
		 int numberOfColumns = rsmd.getColumnCount();
		 List<Integer>  geoColumn = new ArrayList<Integer>();
		 for (int i=1; i<=numberOfColumns; i++){
			 
			 if (debug){
				 System.out.print("col "+ i + ": "+ rsmd.getColumnName(i));
				 System.out.print("   - Type: "+ rsmd.getColumnType(i));
				 System.out.print("   - TypeName: "+ rsmd.getColumnTypeName(i));
				 System.out.println("  - size: "+ rsmd.getColumnDisplaySize(i));
				 
			 }
			
			 /* for Geometry
			 - in MySQL Type: -2/-4   - TypeName: UNKNOWN   - size: 2147483647
			 - In MSSQL Type: -3   - TypeName: geometry  - size: 2147483647
			 */
			 // Search column with Geometry type
			 if (rsmd.getColumnType(i)==-4 ||rsmd.getColumnType(i)==-3 ||rsmd.getColumnType(i)==-2){
				 int j=1;
				 geoColumn.add(i);
			 }
		 }		
		 return geoColumn;
	}
}