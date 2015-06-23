package irit.gaml.extensions.database.skills;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import msi.gama.database.SqlConnection;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;

import org.geotools.factory.GeoTools;


import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class MyAPP {
	
	   public MyAPP(){
	        System.out.println( "Hello GeoTools:" + GeoTools.getVersion() );
	        SqlConnection sqlcon=new SqlConnection("SQLSERVER","tmthai","1433","BPH","sa","tmt");
	        //GamaList<Object> gamaList= sqlcon.selectDB("select id_1, name_1,geom.STAsBinary()  from VNM_ADM4");
	        GamaList<Object> gamaList= sqlcon.selectDB("select id_1, name_1,geom.STAsBinary()  from FRA_ADM2");
			//get Column name
			GamaList<Object> colNames=(GamaList<Object>) gamaList.get(0);
			//get Column type
			GamaList<Object> colTypes=(GamaList<Object>) gamaList.get(1);
			int index=colTypes.indexOf("GEOMETRY");
			System.out.println("index="+colTypes.indexOf("GEOMETRIY"));
			//Get ResultSet 
			GamaList<GamaList<Object>> initValue = (GamaList<GamaList<Object>>) gamaList.get(2);
			//set initialValues to generate species
			final List<Map<String, Object>> initialValues = new GamaList();
			int n=initValue.length(null);
			//int max = number == null ? Integer.MAX_VALUE : numberOfAgents;
            Envelope en0;
            if (n>0){
            	GamaList<Object> rowList = initValue.get(0);
            	Geometry geo= (Geometry) rowList.get(index);
            	en0=geo.getEnvelopeInternal();
            	System.out.println("Envolope["+0+"]"+geo.getEnvelopeInternal());
            	double maxX=en0.getMaxX();
            	double maxY=en0.getMaxY();
            	double minX=en0.getMinX();
            	double minY=en0.getMinY();
            	

				for (int i=1; i<n && i<Integer.MAX_VALUE; i++)
				{
					
					rowList = initValue.get(i);
					geo= (Geometry) rowList.get(index);
					en0=geo.getEnvelopeInternal();
					maxX= Math.max(maxX,en0.getMaxX());
					maxY= Math.max(maxY,en0.getMaxY());
					minX= Math.min(minX,en0.getMinX());
					minY= Math.min(minY,en0.getMinY());
					
					System.out.println("Envolope["+i+"]"+geo.getEnvelopeInternal());
					en0.init(maxX, minX, maxY, minY);
					System.out.println("Envolope["+0+"]"+en0);
				}
            }
            try {
				en0=getBounds(gamaList);
				System.out.println("Envolope="+en0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            
            
            com.vividsolutions.jts.geom.Envelope envelope = new Envelope(0, 10, 0, 20);
            double xMin = envelope.getMinX();
            double yMin = envelope.getMinY();
            
            double xMax = envelope.getMaxX();
            double yMax = envelope.getMaxY();
            
            double width = envelope.getWidth(); // assuming axis 0 is easting
            double height = envelope.getHeight(); // assuming axis 1 is nothing
            System.out.println("xMin="+xMin+" xMax=" + xMax+" yMin="+ yMin+"yMax"+ yMax);
            envelope.init(30.0, 20, 40, 12);
             xMin = envelope.getMinX();
             yMin = envelope.getMinY();
            
             xMax = envelope.getMaxX();
             yMax = envelope.getMaxY();
            
             width = envelope.getWidth(); // assuming axis 0 is easting
             height = envelope.getHeight(); // assuming axis 1 is nothing
            System.out.println("xMin="+xMin+" xMax=" + xMax+" yMin="+ yMin+"yMax"+ yMax);
	    }
	   
	   public static Envelope getBounds( GamaList<Object> gamaList)throws IOException {
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
				int n=initValue.length(null);
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
	   public static void main( String[] args ) throws IOException
	    {
		   MyAPP myapp=new MyAPP();
	    }

}
