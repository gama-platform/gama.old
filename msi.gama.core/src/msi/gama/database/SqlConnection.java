package msi.gama.database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//import org.geotools.data.teradata.WKBAttributeIO;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKTReader;

import msi.gama.common.util.GisUtils;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaPair;
import msi.gama.util.matrix.GamaObjectMatrix;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.IType;

/*
 * @Author  
 *     TRUONG Minh Thai
 *     Fredric AMBLARD
 *     Benoit GAUDOU
 *     Christophe Sibertin-BLANC
 * 
 * 
 * SQLConnection:   supports the method
 * - connectDB: make a connection to DBMS.
 * - selectDB: connect to DBMS and run executeQuery to select data from DBMS.
 * - executeUpdateDB: connect to DBMS and run executeUpdate to update/insert/delete/drop/create data
 * on DBMS.
 * 
 * Created date: 17-Feb-2012
 * Modified:
 *     20-Jun-2012: Change the Skill class to class library for Skill and Agent class
 *     19-Sep-2012: Change const MSSQL to SQLSERVER for SQL Server 
 *     20-Sep-2012: Add SQLITE to all methods
 *     24-Sep-2012: Add methods
 *                    - SqlConnection()
 *                    - select(Connection conn, String select)
 *                    - executeUpdate(Connection conn, String updateConn)
 *    25-Sep-2012:  
 *        Add case: geometry of SQLITE into methods:
 *    			- List<Integer> getGeometryColumns(ResultSetMetaData rsmd)
 *              - GamaList<Object> getColumnTypeName(ResultSetMetaData rsmd)       
 *              
 *   18-Nov-2012:
 *      add PostgresSQL case
 *      
 *   08-Jan-2013:
 *      modify geometry type for postgresSQL data
 *        
 *   18-Feb-2013:
 *      Add InsertDB(Connection conn, String table_name, GamaList<Object> cols, GammaList<Object> values)
 *      Add InsertDB(String table_name, GamaList<Object> cols, GammaList<Object> values)
 *        
 *      Modify selectDB(String selectComm)
 *  19-Feb-2013
 *      Add public int executeUpdateDB(Connection conn,String queryStr, GamaList<Object> condition_values)
 *      Add public int executeUpdateDB(String queryStr, GamaList<Object> condition_values)    
 *  20-Feb-2013:  
 *    	Add InsertDB(Connection conn, String table_name, GammaList<Object> values)
 *    	Add InsertDB(String table_name, GammaList<Object> values)
 *    	Add public GamaList<Object> executeQueryDB(Connection conn,String queryStr, GamaList<Object> condition_values)
 *      Add public GamaList<Object> executeQueryDB(String queryStr, GamaList<Object> condition_values)
 *      
 *   10-Mar-2013:
 *   	Modify select methods: Add transform parameter  
 *      Modify insert methods: Add transform parameter 
 *      Add fromAbsoluteToGis methods
 *      Add fromGisToAbsolute methods
 * Last Modified: 10-Mar-2013
 */
public class SqlConnection {
		private static final boolean DEBUG = false; // Change DEBUG = false for release version
		public static final String MYSQL ="mysql";
		public static final String POSTGRES="postgres";
		public static final String POSTGIS="postgis";
		public static final String MSSQL ="sqlserver";
		public static final String SQLITE="sqlite";
		public static final String GEOMETRYTYPE="GEOMETRY";
		public static final String CHAR="CHAR";
		public static final String VARCHAR="VARCHAR";
		public static final String NVARCHAR="NVARCHAR";
		public static final String TEXT="TEXT";

		static final String MYSQLDriver = new String("com.mysql.jdbc.Driver");
		static final String MSSQLDriver = new String("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		static final String SQLITEDriver = new String("org.sqlite.JDBC");
		static final String POSTGRESDriver= new String("org.postgresql.Driver");
		
		static String vender="";
		static String dbtype="";
		static String url="";
		static String port="";
		static String dbName="";
		static String userName="";
		static String password="";
		static Boolean transformed=false;
		
		private static java.util.HashMap<String, String> tmap = new java.util.HashMap<String, String>();
		public SqlConnection(String dbName)
		{
			this.dbName=dbName;
			transformInit();
		}
		public SqlConnection(String venderName,String database)
		{
			this.vender=venderName;
			this.dbName=database;
			transformInit();
		}
		public SqlConnection(String venderName,String database, Boolean transformed)
		{
			this.vender=venderName;
			this.dbName=database;
			this.transformed = transformed;
			transformInit();
		}

		public SqlConnection()
		{
		}

		public SqlConnection(String venderName,String url,String port,
				String dbName, String userName,String password)
		{
			this.vender=venderName;
			this.url=url;
			this.port=port;
			this.dbName=dbName;
			this.userName=userName;
			this.password=password;	
			this.dbtype=venderName;
			transformInit();
			//conn=connectDB();
		}
		public SqlConnection(String venderName,String url,String port,
				String dbName, String userName,String password, Boolean transformed)
		{
			this.vender=venderName;
			this.url=url;
			this.port=port;
			this.dbName=dbName;
			this.userName=userName;
			this.password=password;	
			this.dbtype=venderName;
			this.transformed = transformed;
			transformInit();
			//conn=connectDB();
		}
		/*
		 * Tranform function base on DBMS
		 */
		private void transformInit(){
			tmap.put(MYSQL, "GeomFromText");
			tmap.put(POSTGRES, "ST_GeomFromText");
			tmap.put(POSTGIS, "ST_GeomFromText");
			tmap.put(MSSQL, "geometry::STGeomFromText");
			tmap.put(SQLITE, "GeomFromText");	
		}
		/*
		 * Make a connection to BDMS
		 * 
		 */
		public Connection connectDB()
				throws ClassNotFoundException, InstantiationException, SQLException, IllegalAccessException 
		{
				Connection conn = null;
				try {
					if ( vender.equalsIgnoreCase(MYSQL) ) {
						Class.forName(MYSQLDriver).newInstance();
						conn =
							DriverManager.getConnection("jdbc:mysql://" + url + ":" + port + "/" + dbName,
								userName, password);
					} else if ( vender.equalsIgnoreCase(MSSQL) ) {
						Class.forName(MSSQLDriver).newInstance();
						conn =
							DriverManager.getConnection("jdbc:sqlserver://" + url + ":" + port +
								";databaseName=" + dbName + ";user=" + userName + ";password=" + password +
								";");
					} else if ( vender.equalsIgnoreCase(SQLITE) ) {
						Class.forName(SQLITEDriver).newInstance();
						conn =
							DriverManager.getConnection("jdbc:sqlite:" + dbName);
					} else if ( vender.equalsIgnoreCase(POSTGRES) || vender.equalsIgnoreCase(POSTGIS) ) {
						Class.forName(POSTGRESDriver).newInstance();
						conn =
							DriverManager.getConnection("jdbc:postgresql://"+url+":"+port+"/"+ dbName + "?user="+ userName + "&password=" + password);
					}  else {
						throw new ClassNotFoundException("SQLConnection.connectSQL: The " 
					                                       + vender
					                                       + " is not supported!");
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
		 * Make a connection to BDMS and execute the select statement
		 * 
		 * @return GamaList<GamaList<Object>>
		 */
		//public GamaList<GamaList<Object>> selectDB(String selectComm) 
		public GamaList<Object> selectDB(String selectComm) 
		{
			GamaList<Object> result=new GamaList<Object>();
			Connection conn=null;
			try {
				conn=connectDB();				
				result=selectDB(conn,selectComm);
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.selectDB: " + e.toString());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.selectDB: " + e.toString());
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.selectDB: " + e.toString());
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.selectDB: " + e.toString());
			}

			if ( DEBUG ) {
				GuiUtils.informConsole(selectComm + " was run");
			}
			//return repRequest;
			return result;
		}
		/*
		 * Make a connection to BDMS and execute the select statement
		 * 
		 * @return GamaList<GamaList<Object>>
		 */
		//public GamaList<GamaList<Object>> selectDB(String selectComm) 
		public GamaList<Object> selectDB(Connection conn, String selectComm) 
		{
			ResultSet rs;
			GamaList<Object> result=new GamaList<Object>();
			//GamaList<Object> rowList = new GamaList<Object>();
			GamaList<GamaList<Object>> repRequest = new GamaList<GamaList<Object>>();
			try {
				Statement st = conn.createStatement(); 
				rs = st.executeQuery(selectComm);
				ResultSetMetaData rsmd = rs.getMetaData();
				if (DEBUG){
					GuiUtils.debug("MetaData:"+rsmd.toString());
				}
				//repRequest=resultSet2GamaList(rs);
				
				//result.add(rsmd);
				result.add(getColumnName(rsmd));
				result.add(getColumnTypeName(rsmd));
				
				repRequest=resultSet2GamaList(rs);
				result.add(repRequest);
				
				if (DEBUG){
					GuiUtils.debug("list of column name:" + result.get(0) );
					GuiUtils.debug("list of column type:" + result.get(1) );
					GuiUtils.debug("list of data:" + result.get(2) );
				}
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.selectDB: " + e.toString());
			}
			//return repRequest;
			return result;
		}

		/*
		 * Make a connection to BDMS and execute the update statement (update/insert/delete/create/drop)
		 * 
		 */

		public int executeUpdateDB(String updateComm) throws GamaRuntimeException {
			Connection conn = null;
			int n = 0;
			try {
				conn = connectDB();
				if (DEBUG) {
					GuiUtils.debug("Update Command:" +updateComm);
				}
				Statement st = conn.createStatement(); 
				n = st.executeUpdate(updateComm);
				if (DEBUG) {
					GuiUtils.debug("Updated records :" +n);
				}

				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.executeUpdateDB: " + e.toString());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.executeUpdateDB: " + e.toString());
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.executeUpdateDB: " + e.toString());
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.executeUpdateDB: " + e.toString());
			}

			if ( DEBUG ) {
				GuiUtils.informConsole(updateComm + " was run");
			}

			return n;

		}

		/*
		 *  execute the update statement with current connection(update/insert/delete/create/drop)
		 * 
		 */

		public int executeUpdateDB(Connection conn, String updateComm) throws GamaRuntimeException {
			int n = 0;
			try {
				if (DEBUG) {
					GuiUtils.debug("Update Command:" +updateComm);
				}
				Statement st = conn.createStatement(); 
				n = st.executeUpdate(updateComm);
				if (DEBUG) {
					GuiUtils.debug("Updated records :" +n);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.executeUpdateDB: " + e.toString());
			}
			return n;
		}
		
		
		
		GamaList<GamaList<Object>> resultSet2GamaList(ResultSet rs) throws SQLException
		{
			ResultSetMetaData rsmd=rs.getMetaData();
			return resultSet2GamaList(rsmd,rs);
		}
		
		/*
		 * @Method:resultSet2GamaList(ResultSetMetaData rsmd, ResultSet rs)
	     * @Description: Convert RecordSet to GamaList
	     * 
	     * @param ResultSetMetaData,ResultSet
	     * 
	     * @return GamaList<GamaList<Object>>
	     * 
	 	 */
		GamaList<GamaList<Object>> resultSet2GamaList(ResultSetMetaData rsmd, ResultSet rs)
			{
				// convert Geometry in SQL to Geometry type in GeoTool
				
				GamaList<GamaList<Object>> repRequest = new GamaList<GamaList<Object>>();
				try{
					List<Integer>  geoColumn = getGeometryColumns(rsmd);
					int nbCol = rsmd.getColumnCount();
					int i=1;
					if (DEBUG) GuiUtils.debug("Number of col:"+ nbCol);
					if (DEBUG) GuiUtils.debug("Number of row:"+ rs.getFetchSize());
					while (rs.next())
					{
						//InputStream inputStream = rs.getBinaryStream(i);
						if (DEBUG) GuiUtils.debug("processing at row:"+ i);

						GamaList<Object> rowList = new GamaList<Object>();
						for ( int j = 1; j <= nbCol; j++ ) 
						{
							// check column is geometry column?
							if (DEBUG) GuiUtils.debug("col "+j+": " +rs.getObject(j));
							if (geoColumn.contains(j)){
								if (DEBUG) GuiUtils.debug("convert at ["+ i+","+j+"]: ");
								//rowList.add(Binary2Geometry(rs.getBytes(j)));
								//rowList.add(InputStream2Geometry(rs.getBinaryStream(j)));
								//rowList.add(read((byte[])rs.getObject(j)));
								//rowList.add(read(rs.getBytes(j)));
								//rowList.add(read(rs,j));
								if ( vender.equalsIgnoreCase(MYSQL))
										// ||vender.equalsIgnoreCase(POSTGRES))
								{
									rowList.add(InputStream2Geometry(rs.getBinaryStream(j)));
								}
								else //for (vender.equalsIgnoreCase(MSSQL)/(vender.equalsIgnoreCase(SQLITE))
									rowList.add(read(rs.getBytes(j)));
								    //rowList.add(InputStream2Geometry( (InputStream) rs.getObject(j)));
							}
							else
								rowList.add(rs.getObject(j));
						}
						repRequest.add(rowList);
						i++;
					}
					if (DEBUG) GuiUtils.debug("Number of row:"+ i);
				}
				catch (Exception e)
				{
					
				}
				return repRequest;
							
			}
			
	

			/*
		     * @Meththod: getGeometryColumns(ResultSetMetaData rsmd)
		     * @Description: Get columns id of field with geometry type
		     * 
		     * @param ResultSetMetaData
		     * 
		     * @return List<Integer>
		     * 
		     * @throws SQLException
		 	 */
			List<Integer> getGeometryColumns(ResultSetMetaData rsmd)
			throws SQLException
			{
				 int numberOfColumns = rsmd.getColumnCount();
				 List<Integer>  geoColumn = new ArrayList<Integer>();
				 for (int i=1; i<=numberOfColumns; i++){
					 
					 if (DEBUG){
						 GuiUtils.debug("col "+ i + ": "+ rsmd.getColumnName(i));
						 GuiUtils.debug("   - Type: "+ rsmd.getColumnType(i));
						 GuiUtils.debug("   - TypeName: "+ rsmd.getColumnTypeName(i));
						 GuiUtils.debug("  - size: "+ rsmd.getColumnDisplaySize(i));
						 
					 }
					
					 /* for Geometry 
					 - in MySQL Type: -2/-4   - TypeName: UNKNOWN   - size: 2147483647
					 - In MSSQL Type: -3   - TypeName: geometry     - size: 2147483647
					 - In SQLITE Type: 2004   - TypeName: BLOB      - size: 2147483647
					 - In PostGIS/PostGresSQL Type: 1111   - TypeName: geometry  - size: 2147483647
					          st_asbinary(geom):   - Type: -2   - TypeName: bytea  - size: 2147483647
					 */
					 // Search column with Geometry type
					 if ((vender.equalsIgnoreCase(MYSQL) & rsmd.getColumnType(i)==-4 ) || 
							 (vender.equalsIgnoreCase(MYSQL) & rsmd.getColumnType(i)==-2 ) || 
							 (vender.equalsIgnoreCase(MSSQL) & rsmd.getColumnType(i)==-3 )||
							 (vender.equalsIgnoreCase(SQLITE) & rsmd.getColumnType(i)==2004)||
							 //add:03-Jan-2013
							 (vender.equalsIgnoreCase(POSTGRES) & rsmd.getColumnType(i)==1111)||
							 (vender.equalsIgnoreCase(POSTGRES) & rsmd.getColumnType(i)==-2) ||
							 (vender.equalsIgnoreCase(POSTGIS) & rsmd.getColumnType(i)==1111)||
							 (vender.equalsIgnoreCase(POSTGIS) & rsmd.getColumnType(i)==-2)) 
							 
						 geoColumn.add(i);
				}		
				 return geoColumn;
			}

			/*
		     * @Method: getColumnName
		     * @Description: Get columns name
		     * 
		     * @param ResultSetMetaData
		     * 
		     * @return GamaList<String>
		     * 
		     * @throws SQLException
		 	 */
			
			GamaList<Object> getColumnName(ResultSetMetaData rsmd) throws SQLException
			{
				 int numberOfColumns = rsmd.getColumnCount();
				 GamaList<Object>  columnType = new GamaList<Object>();
				 for (int i=1; i<=numberOfColumns; i++){
						 columnType.add(rsmd.getColumnName(i).toUpperCase());
				}		
				 return columnType;				
			}

			/*
		     * @Method: getColumnTypeName
		     * @Description: Get columns type name
		     * 
		     * @param ResultSetMetaData
		     * 
		     * @return GamaList<String>
		     * 
		     * @throws SQLException
		 	 */
			
			GamaList<Object> getColumnTypeName(ResultSetMetaData rsmd) throws SQLException
			{
				 int numberOfColumns = rsmd.getColumnCount();
				 GamaList<Object>  columnType = new GamaList<Object>();
				 for (int i=1; i<=numberOfColumns; i++){
					 /* for Geometry 
					 - in MySQL Type: -2/-4   - TypeName: UNKNOWN   - size: 2147483647
					 - In MSSQL Type: -3   - TypeName: geometry     - size: 2147483647
					 - In SQLITE Type: 2004   - TypeName: BLOB      - size: 2147483647
					 - In PostGIS/PostGresSQL Type: 1111   - TypeName: geometry  - size: 2147483647
					 */
				     // Search column with Geometry type
					 if ((vender.equalsIgnoreCase(MYSQL) & rsmd.getColumnType(i)==-2) || 
							 (vender.equalsIgnoreCase(MSSQL) & rsmd.getColumnType(i)==-3)||
							 (vender.equalsIgnoreCase(SQLITE) & rsmd.getColumnType(i)==2004)||
							 //add: 03-Jan-2013
							 (vender.equalsIgnoreCase(POSTGRES) & rsmd.getColumnType(i)==1111)||
							 (vender.equalsIgnoreCase(POSTGRES) & rsmd.getColumnType(i)==-2) ||
							 (vender.equalsIgnoreCase(POSTGIS) & rsmd.getColumnType(i)==1111)||
							 (vender.equalsIgnoreCase(POSTGIS) & rsmd.getColumnType(i)==-2)) 
						 columnType.add(GEOMETRYTYPE);
					 else
						 columnType.add(rsmd.getColumnTypeName(i).toUpperCase());
				}		
				 return columnType;				
			}
			
			/*
		     * @Method: read(byte [] b)
		     * @Description: Convert Binary to Geometry (sqlserver case)
		     * 
		     * @param byte [] b
		     * 
		     * @return Geometry
		     * 
		     * @throws IOException, ParseException
		 	 */
			static Geometry read(byte [] b) throws IOException, ParseException
			{
				//WKBAttributeIO wkb=new WKBAttributeIO();
				WKBReader wkb= new WKBReader();
				Geometry geom=wkb.read(b);
				return geom;
				//return GisUtils.fromGISToAbsolute(geom);
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
			static Geometry Binary2Geometry(byte [] geometryAsBytes ) throws ParseException
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
			
			public static Geometry InputStream2Geometry(InputStream inputStream) throws Exception {
				 
				 
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
			 
			     return dbGeometry;			 }
			
			
			public String getURL(){ return url; }
			public String getVendor(){ return vender; }
			public String getUser(){ return userName;}
			
		/*
		 * @Method: getBounds( GamaList<Object> gamaList)
		 * @Description: Get Envelope of a set of geometry
		 * 
		 * @param GamaList<Object> gamaList: gamalist is a set of geometry type
		 * 
		 * @return Envelope: Envelope/boundary of the geometry set.
		 * 
		 * @throws Exception
		 */

		public static Envelope getBounds( IScope scope, GamaList<Object> gamaList)throws IOException {
				   	Envelope envelope;
					//get Column name
					GamaList<Object> colNames=(GamaList<Object>) gamaList.get(0);
					//get Column type
					GamaList<Object> colTypes=(GamaList<Object>) gamaList.get(1);
					int index=colTypes.indexOf("GEOMETRY");
					if (index<0) return null;
					else {
						//Get ResultSet 
						GamaList<GamaList<Object>> initValue = (GamaList<GamaList<Object>>) gamaList.get(2);
						int n=initValue.length(scope);
						//int max = number == null ? Integer.MAX_VALUE : numberOfAgents;
			            if (n<0) return null;
			            else {
			            	GamaList<Object> rowList = initValue.get(0);
			            	Geometry geo= (Geometry) rowList.get(index);
			            	envelope=geo.getEnvelopeInternal();
			            	double maxX=envelope.getMaxX();
			            	double maxY=envelope.getMaxY();
			            	double minX=envelope.getMinX();
			            	double minY=envelope.getMinY();
			            	

							for (int i=1; i<n && i<Integer.MAX_VALUE; i++)
							{
								
								rowList = initValue.get(i);
								geo= (Geometry) rowList.get(index);
								envelope=geo.getEnvelopeInternal();
				            	double maxX1=envelope.getMaxX();
				            	double maxY1=envelope.getMaxY();
				            	double minX1=envelope.getMinX();
				            	double minY1=envelope.getMinY();

								maxX= maxX>maxX1 ? maxX : maxX1 ;
								maxY= maxY>maxY1 ? maxY : maxY1 ;
								minX= minX<minX1 ? minX : minX1 ;
								minY= minY<minY1 ? minY : minY1 ;
								envelope.init(minX,maxX, minY, maxY);
								
							}
							return envelope;
			            }
					}

			   }
		
//		/*
//		 * Make Insert a reccord into table
//		 * 
//		 */
//		public int insertDB(Connection conn, String table_name, GamaList<Object> cols,GamaList<Object> values ) 
//				throws GamaRuntimeException
//		{
//			PreparedStatement pstmt = null;
//			//int col_no=cols.length(scope);
//			int col_no=cols.size();
//			int rec_no=0;
//			String insertStr= "INSERT INTO ";
//			String col_names="";
//			String valueStr="";
//			//Check size of parameters
//			if (values.size()!=col_no) {
//				throw new IndexOutOfBoundsException("Size of columns list and values list are not equal");
//			}
//			//Get column name
//			for (int i=0; i<col_no;i++){
//				if (i==col_no-1){
//					col_names= col_names+ (String) (cols.get(i));
//					valueStr=valueStr + "?";
//				}else{
//					col_names= col_names+ (String) (cols.get(i))+",";
//					valueStr=valueStr + "?"+",";
//				}
//				if (DEBUG){
//					GuiUtils.debug("col"+(String) (cols.get(i)));
//				}
//					
//			}
//			// create INSERT statement string
//			insertStr= insertStr + table_name +"("+col_names+") " +"VALUES("+valueStr+")";
//
//			if (DEBUG){
//				GuiUtils.debug("Insert command:"+insertStr);
//			}
//				
//		
//			try{
//				//Insert command
//				
//				pstmt=conn.prepareStatement(insertStr);
//				//set parameter value
//				for (int i=0; i<col_no;i++){
//						pstmt.setObject(i+1, values.get(i));
//				}
//				rec_no=pstmt.executeUpdate();				
//				if (DEBUG){
//					GuiUtils.debug("rec count:" + rec_no);
//				}
//				
//			}catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				throw new GamaRuntimeException("SQLConnection.insertBD " + e.toString());
//			}
//			
//			return rec_no;
//		}
		/*-------------------------------------------------------------------------------------------------------------------------
		 * Make Insert a reccord into table
		 * 
		 */
		public int insertDB(Connection conn, String table_name, GamaList<Object> cols,GamaList<Object> values ) 
				throws GamaRuntimeException
		{
			int rec_no=-1;
			if (values.size()!=cols.size()) {
				throw new IndexOutOfBoundsException("Size of columns list and values list are not equal");
			}		
			try{
				//Get Insert command
				Statement st=conn.createStatement();
				rec_no=st.executeUpdate(getInsertString(conn,table_name,cols,values));
				if (DEBUG){
					GuiUtils.debug("SQLConnection.insertBD.rec_no:" + rec_no);
				}
				
			}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.insertBD " + e.toString());
			}
			
			return rec_no;
		}
		
		/*-------------------------------------------------------------------------------------------------------------------------
		 * Make Insert a reccord into table
		 * 
		 */
		public int insertDB(Connection conn, String table_name, GamaList<Object> cols,GamaList<Object> values, Boolean transformed ) 
				throws GamaRuntimeException
		{
			this.transformed=transformed;
			int rec_no=-1;
			//Get Insert command
			rec_no=insertDB(conn,table_name,cols,values);
			return rec_no;
		}

		/*-------------------------------------------------------------------------------------------------------------------------
		 * Make Insert a reccord into table
		 * 
		 */
		public int insertDB(String table_name, GamaList<Object> cols,GamaList<Object> values ) 
				throws GamaRuntimeException {
			Connection conn;
			int rec_no=-1;
			try{
				conn=connectDB();
				rec_no=insertDB(conn,table_name,cols,values);
				conn.close();
			}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.insertBD " + e.toString());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.insertDB: " + e.toString());
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.insertBD: " + e.toString());
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.insertBD: " + e.toString());
			}
			return rec_no;
		}
		/*
		 * Make Insert a reccord into table
		 * 
		 */
		public int insertDB(String table_name, GamaList<Object> cols,GamaList<Object> values, Boolean transformed ) 
				throws GamaRuntimeException {
			this.transformed=transformed;
			int rec_no=-1;
			rec_no=insertDB(table_name,cols,values);
			return rec_no;
		}
	
//		/*
//		 * Make Insert a reccord into table
//		 * 
//		 */
//		public int insertDB(Connection conn, String table_name,GamaList<Object> values ) 
//				throws GamaRuntimeException
//		{
//			//Connection conn=null;
//			PreparedStatement pstmt = null;
//			//int col_no=cols.length(scope);
//			int col_no=values.size();
//			int rec_no=0;
//			String insertStr= "INSERT INTO ";
//			String valueStr="";
//			//Create question mark list
//			for (int i=0; i<col_no;i++){
//				if (i==col_no-1){
//					valueStr=valueStr + "?";
//				}else{
//					valueStr=valueStr + "?"+",";
//				}					
//			}
//			// create INSERT statement string
//			insertStr= insertStr + table_name +" VALUES("+valueStr+")";				
//		
//			try{				
//				pstmt=conn.prepareStatement(insertStr);
//				//set parameter value
//				for (int i=0; i<col_no;i++){
//					pstmt.setObject(i+1, values.get(i));
//				}	
//				rec_no=pstmt.executeUpdate();
//				if (DEBUG){
//					GuiUtils.debug("rec count:" + rec_no);
//				}
//				
//			}catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				throw new GamaRuntimeException("SQLConnection.insertBD " + e.toString());
//			}
//			return rec_no;
//		}

		
		/*
		 * Make Insert a reccord into table
		 * 
		 */
		public int insertDB(Connection conn, String table_name,GamaList<Object> values ) 
				throws GamaRuntimeException
		{
			int rec_no=-1;
			try{				
				//Get Insert command
				Statement st=conn.createStatement();
				rec_no=st.executeUpdate(getInsertString(conn,table_name,values));

				if (DEBUG){
					GuiUtils.debug("SQLConnection.insertBD.rec_no:" + rec_no);
				}
		
			}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.insertBD " + e.toString());
			}
			return rec_no;
		}
		
		public int insertDB(Connection conn, String table_name,GamaList<Object> values, Boolean transformed ) 
				throws GamaRuntimeException
		{
			this.transformed=transformed;
			return insertDB(conn,table_name,values);
		}
		
		/*
		 * Make Insert a reccord into table
		 * 
		 */
		public int insertDB(String table_name, GamaList<Object> values ) 
				throws GamaRuntimeException {
			Connection conn;
			int rec_no=-1;
			try{
				conn=connectDB();
				rec_no=insertDB(conn,table_name,values);
				conn.close();
			}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.insertBD " + e.toString());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.insertDB: " + e.toString());
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.insertBD: " + e.toString());
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.insertBD: " + e.toString());
			}
			return rec_no;
		}
	
		/*
		 * Make Insert a reccord into table
		 * 
		 */
		public int insertDB(String table_name, GamaList<Object> values, Boolean transformed ) 
				throws GamaRuntimeException {
			this.transformed=transformed;
			return insertDB(table_name,values);
		}
			

		/*
		 * @Method: executeQueryDB(Connection conn,String queryStr, GamaList<Object> condition_value)
		 * @Description: Executes the SQL query in this PreparedStatement object and returns the ResultSet object generated by the query
		 * 
		 * @param queryStr: SQL query string with question mark (?).
		 * @param condition_value:List of values that are used to assign into conditions of queryStr
		 * 
		 * @return ResultSet:returns the ResultSet object generated by the query.
		 * 
		 * @throws GamaRuntimeException: if a database access error occurs or the SQL statement does not return a ResultSet object
		 */
			public GamaList<Object> executeQueryDB(Connection conn,String queryStr, GamaList<Object> condition_values)
			throws GamaRuntimeException
			{
				PreparedStatement pstmt = null;
				ResultSet rs;
				GamaList<Object> result=new GamaList<Object>();
				GamaList<GamaList<Object>> repRequest = new GamaList<GamaList<Object>>();
				int condition_count =condition_values.size();
				try {
					pstmt = conn.prepareStatement(queryStr);
					// set value for each condition
					for (int i=0; i<condition_count;i++){
						pstmt.setObject(i+1, condition_values.get(i));
					}
					rs = pstmt.executeQuery();
					ResultSetMetaData rsmd = rs.getMetaData();
					if (DEBUG){
						GuiUtils.debug("MetaData:"+rsmd.toString());
					}
					
					result.add(getColumnName(rsmd));
					result.add(getColumnTypeName(rsmd));
					
					repRequest=resultSet2GamaList(rs);
					result.add(repRequest);
					
					if (DEBUG){
						GuiUtils.debug("list of column name:" + result.get(0) );
						GuiUtils.debug("list of column type:" + result.get(1) );
						GuiUtils.debug("list of data:" + result.get(2) );
					}
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new GamaRuntimeException("SQLConnection.selectDB: " + e.toString());
				}
				//return repRequest;
				return result;
				
			}
			/*
			 * @Method: ExecuteQueryDB(Connection conn,String queryStr, GamaList<Object> condition_values)
			 * @Description: Executes the SQL query in this PreparedStatement object and returns the ResultSet object generated by the query
			 * 
			 * @param conn: MAP of Connection parameters  to RDBM
			 * @param queryStr: SQL query (select) string with question mark (?).
			 * @param condition_value:List of values that are used to assign into conditions of queryStr
			 * 
			 * @return ResultSet:returns the ResultSet object generated by the query.
			 * 
			 * @throws GamaRuntimeException: if a database access error occurs or the SQL statement does not return a ResultSet object
			 */
				public GamaList<Object> executeQueryDB(String queryStr, GamaList<Object> condition_values)
				throws GamaRuntimeException
				{				
					GamaList<Object> result=new GamaList<Object>();
					Connection conn;
					try {
						conn=connectDB();
						result=executeQueryDB(conn,queryStr,condition_values);
						conn.close();
						// set value for each condition
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new GamaRuntimeException("SQLConnection.executeQuery: " + e.toString());
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new GamaRuntimeException("SQLConnection.executeQuery: " + e.toString());
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new GamaRuntimeException("SQLConnection.executeQuery: " + e.toString());
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new GamaRuntimeException("SQLConnection.executeQuery: " + e.toString());
					}
					//return repRequest;
					return result;
					
				}
				/*
				 * @Method: executeUpdateDB(Connection conn,String queryStr, GamaList<Object> condition_value)
				 * @Description: Executes the SQL statement in this PreparedStatement object, which must be an SQL INSERT, UPDATE or DELETE statement; or an SQL statement that returns nothing, such as a DDL statement.
				 * 
				 * @param conn: MAP of Connection parameters  to RDBM
				 * @param queryStr:  an SQL INSERT, UPDATE or DELETE statement with question mark (?).
				 * @param condition_values: List of values that are used to assign into conditions of queryStr.
				 * 
				 * @return row_count:either (1) the row count for INSERT, UPDATE, or DELETE statements or (2) 0 for SQL statements that return nothing
				 * 
				 * @throws GamaRuntimeException
				 */
					public int executeUpdateDB(Connection conn,String queryStr, GamaList<Object> condition_values)
					throws GamaRuntimeException
					{
						PreparedStatement pstmt = null;
						int row_count=-1;
						int condition_count =condition_values.size();
						try {
							pstmt = conn.prepareStatement(queryStr);
							// set value for each condition
							if (DEBUG){
								GuiUtils.debug("SqlConnection.ExecuteUpdateDB.values.size:"+condition_count);
								GuiUtils.debug("SqlConnection.ExecuteUpdateDB.values.size:"+condition_values.toGaml());
							}
							
							for (int i=0; i<condition_count;i++){
								pstmt.setObject(i+1, condition_values.get(i));
							}
							row_count = pstmt.executeUpdate();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							throw new GamaRuntimeException("SQLConnection.selectDB: " + e.toString());
						}
						//return repRequest;
						return row_count;
						
					}

				/*
				 * @Method: executeUpdateDB(Connection conn,String queryStr, GamaList<Object> condition_value)
				 * @Description: Executes the SQL statement in this PreparedStatement object, which must be an SQL INSERT, UPDATE or DELETE statement; or an SQL statement that returns nothing, such as a DDL statement.
				 * 
				 * @param queryStr:  an SQL INSERT, UPDATE or DELETE statement with question mark (?).
				 * @param condition_values:
				 * 
				 * @return row_count:either (1) the row count for INSERT, UPDATE, or DELETE statements or (2) 0 for SQL statements that return nothing
				 * 
				 * @throws GamaRuntimeException
				 */
					public int executeUpdateDB(String queryStr, GamaList<Object> condition_values)
					throws GamaRuntimeException
					{				
						int row_count=-1;	
						Connection conn;
						try {
							conn=connectDB();
							row_count=executeUpdateDB(conn,queryStr,condition_values);
							conn.close();
							// set value for each condition
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							throw new GamaRuntimeException("SQLConnection.executeUpdateDB: " + e.toString());
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							throw new GamaRuntimeException("SQLConnection.executeUpdateDB: " + e.toString());
						} catch (InstantiationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							throw new GamaRuntimeException("SQLConnection.executeUpdateDB: " + e.toString());
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							throw new GamaRuntimeException("SQLConnection.executeUpdateDB: " + e.toString());
						}
						//return repRequest;
						return row_count;
						
					}
					
				    /**
				     * This method return a prepared MULTIPOINT geometry if is MULTIPOINT (We need do this
				     * because MULTIPOINT Feature format is: MULTIPOINT ((x y),(x y),(x y)) and
				     * MULTIPOINT Spatialite format is: MULTIPOINT (x y, x y, x y)
				     * @param theGeom;
				     * @return value;
				     */
				    private String prepareGeom (String theGeom){
				        String value = theGeom;
				            if (theGeom.contains("MULTIPOINT"))
				            {
				                value = value.replaceAll("\\(\\(", "*");
				                value = value.replaceAll("\\)\\)", "#");
				                value = value.replaceAll("\\(", "");
				                value = value.replaceAll("\\)", "");
				                value = value.replaceAll("\\*", "(");
				                value = value.replaceAll("\\#", ")");
				            }
				        return value;
				    }
					/*
					 * Make insert command string with columns and values
					 * 
					 */
					private String getInsertString(Connection conn, String table_name, GamaList<Object> cols,GamaList<Object> values ) 
							throws GamaRuntimeException
					{
						int col_no=cols.size();
						String insertStr= "INSERT INTO ";
						String selectStr="SELECT ";
						String colStr="";
						String valueStr="";
						//Check size of parameters
						if (values.size()!=col_no) {
							throw new IndexOutOfBoundsException("Size of columns list and values list are not equal");
						}
						//Get column name
						for (int i=0; i<col_no;i++){
							if (i==col_no-1){
								colStr= colStr+ (String) (cols.get(i));
							}else{
								colStr= colStr+ (String) (cols.get(i))+",";
							}					
						}
						// create SELECT statement string
						if (vender.equalsIgnoreCase(MSSQL)){
							selectStr=selectStr + " TOP 1 "+ colStr + " FROM " + table_name +" ;";
						}else{
							selectStr=selectStr + colStr + " FROM " + table_name +" LIMIT 1 ;";
						}
						
						if (DEBUG){
							GuiUtils.debug("SqlConnection.getInsertString.select command:"+selectStr);
						}
						
						try{
							//get column type;
							Statement st = conn.createStatement(); 
							ResultSet rs = st.executeQuery(selectStr);
							ResultSetMetaData rsmd = rs.getMetaData();
							GamaList<Object> col_Names= getColumnName(rsmd);
							GamaList<Object> col_Types=getColumnTypeName(rsmd);
							
							if (DEBUG){
								GuiUtils.debug("list of column Name:" + col_Names);
								GuiUtils.debug("list of column type:" + col_Types);
							}
							//Insert command
							//set parameter value
							valueStr="";
							for (int i=0; i<col_no;i++){
								//Value list begin-------------------------------------------
								if (((String)col_Types.get(i)).equalsIgnoreCase(GEOMETRYTYPE)){ // for GEOMETRY type
									    //Transform GAMA GIS TO NORMAL
										if (transformed){
											WKTReader wkt = new WKTReader();
											Geometry geo2 =GisUtils.fromAbsoluteToGis(wkt.read(values.get(i).toString()));
											valueStr=valueStr+tmap.get(vender.toLowerCase())+"('"+geo2.toString()+"')";
										}else{
											valueStr=valueStr+tmap.get(vender.toLowerCase())+"('"+values.get(i).toString()+"')";
										}
								}else if (((String)col_Types.get(i)).equalsIgnoreCase(CHAR)
										||((String)col_Types.get(i)).equalsIgnoreCase(VARCHAR)
										||((String)col_Types.get(i)).equalsIgnoreCase(NVARCHAR)
										||((String)col_Types.get(i)).equalsIgnoreCase(TEXT)){ // for String type
									//Correct error string
									String temp=values.get(i).toString();
									temp=temp.replaceAll("'", "''");
									//Add to value:
									valueStr=valueStr+"'"+temp+"'";
								}else { // For other type
									valueStr=valueStr+values.get(i).toString();
								}
								if (i!=col_no-1){ // Add delimiter of each value 
									valueStr=valueStr + ",";
								}
								//Value list end--------------------------------------------------------

							}									
							insertStr= insertStr + table_name +"("+colStr+") " +"VALUES("+valueStr+")";					

							if(DEBUG) 
								GuiUtils.debug("SqlConection.getInsertString:" +insertStr );
							
							
						}catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							throw new GamaRuntimeException("SQLConnection.insertBD " + e.toString());
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							throw new GamaRuntimeException("SQLConnection.insertBD " + e.toString());
						}
						
						return insertStr;
					}
					
					/*
					 * Make insert command string for all columns with values
					 * 
					 */

					private String getInsertString(Connection conn, String table_name,GamaList<Object> values ) 
							throws GamaRuntimeException
					{
						String insertStr= "INSERT INTO ";
						String selectStr="SELECT ";
						String colStr="";
						String valueStr="";

						//Get column name
						// create SELECT statement string
						if (vender.equalsIgnoreCase(MSSQL)){
							selectStr=selectStr + " TOP 1 * " + " FROM " + table_name +" ;";
						}else{
							selectStr=selectStr +" * " + " FROM " + table_name +" LIMIT 1 ;";
						}
						
						if (DEBUG)
							GuiUtils.debug("SqlConnection.getInsertString.select command:"+selectStr);
						
						try{
							//get column type;
							Statement st = conn.createStatement(); 
							ResultSet rs = st.executeQuery(selectStr);
							ResultSetMetaData rsmd = rs.getMetaData();
							GamaList<Object> col_Names= getColumnName(rsmd);
							GamaList<Object> col_Types=getColumnTypeName(rsmd);
							int col_no=col_Names.size();
							//Check size of parameters
							if (values.size()!=col_Names.size()) {
								throw new IndexOutOfBoundsException("Size of columns list and values list are not equal");
							}
					
							if (DEBUG){
								GuiUtils.debug("list of column Name:" + col_Names);
								GuiUtils.debug("list of column type:" + col_Types);
							}
							//Insert command
							//set parameter value
							colStr="";
							valueStr="";
							for (int i=0; i<col_no;i++){
								//Value list begin-------------------------------------------
								if (((String)col_Types.get(i)).equalsIgnoreCase(GEOMETRYTYPE)){ // for GEOMETRY type
									    //Transform GAMA GIS TO NORMAL
										if (transformed){
											WKTReader wkt = new WKTReader();
											Geometry geo2 =GisUtils.fromAbsoluteToGis(wkt.read(values.get(i).toString()));
											valueStr=valueStr+tmap.get(vender.toLowerCase())+"('"+geo2.toString()+"')";
										}else{ 
											valueStr=valueStr+tmap.get(vender.toLowerCase())+"('"+values.get(i).toString()+"')";
										}
								}else if (((String)col_Types.get(i)).equalsIgnoreCase(CHAR)
											||((String)col_Types.get(i)).equalsIgnoreCase(VARCHAR)
											||((String)col_Types.get(i)).equalsIgnoreCase(NVARCHAR)
											||((String)col_Types.get(i)).equalsIgnoreCase(TEXT))
								{ // for String type
										//Correct error string
										String temp=values.get(i).toString();
										temp=temp.replaceAll("'", "''");
										//Add to value:
										valueStr=valueStr+"'"+temp+"'";
								}else { // For other type
										valueStr=valueStr+values.get(i).toString();
								}
								//Value list end--------------------------------------------------------
								//column list
								colStr=colStr + col_Names.get(i).toString();

								if (i!=col_no-1){ // Add delimiter of each value 
									colStr=colStr + ",";
									valueStr=valueStr + ",";
								}
							}
									
							insertStr= insertStr + table_name +"("+colStr+") " +"VALUES("+valueStr+")";					

							if(DEBUG) 
								GuiUtils.debug("SqlConection.getInsertString:" +insertStr );
							
						}catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							throw new GamaRuntimeException("SQLConnection.insertBD " + e.toString());
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							throw new GamaRuntimeException("SQLConnection.insertBD " + e.toString());
						}
						
						return insertStr;
					}
					
					public void setTransformed(Boolean tranformed){
						this.transformed=tranformed;
					}

					public static Geometry fromGisToAbsolute(Geometry geom){
						return GisUtils.fromGISToAbsolute(geom);

					}
					
					public static Geometry fromAbsoluteToGis(Geometry geom){
						return GisUtils.fromAbsoluteToGis(geom);

					}


					/*
					 * Gis2Absolute: transform all geometry values to absolute geometry in GAMA
					 */
					public GamaList<Object> fromGisToAbsolute(GamaList<Object> dataset) throws GamaRuntimeException{
						try{
							GamaList<Object> response = new GamaList<Object>();	
							GamaList<Object> records_new = new GamaList<Object>();	
							GamaList<Object> columnNames=(GamaList<Object>) dataset.get(0);
							GamaList<Object> columnTypes=(GamaList<Object>) dataset.get(1);
							GamaList<Object> records=(GamaList<Object>) dataset.get(2);
							int columnSize=columnNames.size();
							int lineSize=records.size();
							
							response.add(columnNames);
							response.add(columnTypes);
							
							//transform
							for ( int i = 0; i < lineSize; i++ ) {
								GamaList<Object> rec_old=(GamaList<Object>) records.get(i);
								GamaList<Object> rec_new= new GamaList<Object>();
								for ( int j = 0; j < columnSize; j++ ) {
									if (((String)columnTypes.get(j)).equalsIgnoreCase(GEOMETRYTYPE)){
										//WKTReader wkt = new WKTReader();
										//Geometry geo2 =fromGisToAbsolute(wkt.read(rec_old.get(j).toString()));
										Geometry geo2 =fromGisToAbsolute((Geometry)rec_old.get(j));
										rec_new.add(geo2);
									}else{
										rec_new.add(rec_old.get(j));
									}
									
								}
								records_new.add(rec_new);
							}
							response.add(records_new);
							return response;
						}catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							throw new GamaRuntimeException("SQLConnection.Gis2Absolute: " + e.toString());
			
						}
						
					}
					/*
					 * Gis2Absolute: transform all absolute geometry values in GAMA to geometry 
					 */
					public GamaList<Object> fromAbsoluteToGIS(GamaList<Object> dataset) throws GamaRuntimeException{
						try{
							GamaList<Object> response = new GamaList<Object>();	
							GamaList<Object> records_new = new GamaList<Object>();	
							GamaList<Object> columnNames=(GamaList<Object>) dataset.get(0);
							GamaList<Object> columnTypes=(GamaList<Object>) dataset.get(1);
							GamaList<Object> records=(GamaList<Object>) dataset.get(2);
							int columnSize=columnNames.size();
							int lineSize=records.size();
							
							response.add(columnNames);
							response.add(columnTypes);
							
							//transform
							for ( int i = 0; i < lineSize; i++ ) {
								GamaList<Object> rec_old=(GamaList<Object>) records.get(i);
								GamaList<Object> rec_new= new GamaList<Object>();
								for ( int j = 0; j < columnSize; j++ ) {
									if (((String)columnTypes.get(j)).equalsIgnoreCase(GEOMETRYTYPE)){
										//WKTReader wkt = new WKTReader();
										//Geometry geo2 =fromGisToAbsolute(wkt.read(rec_old.get(j).toString()));
										Geometry geo2 =fromAbsoluteToGis((Geometry)rec_old.get(j));
										rec_new.add(geo2);
									}else{
										rec_new.add(rec_old.get(j));
									}
									
								}
								records_new.add(rec_new);
							}
							response.add(records_new);
							return response;
						}catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							throw new GamaRuntimeException("SQLConnection.Gis2Absolute: " + e.toString());
			
						}
						
					}

}
