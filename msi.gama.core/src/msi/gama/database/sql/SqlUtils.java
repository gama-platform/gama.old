package msi.gama.database.sql;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;

public class SqlUtils {
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
