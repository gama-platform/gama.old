package collada;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import msi.gama.jogl.utils.GraphicDataType.MyJTSGeometry;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Files;


public class Output3D {
	public final static String FILE3DFOLDER = "file3D";
	
	public static void to3DGLGEModel(ArrayList<MyJTSGeometry> myJTSGeometries) {
//		Geometry geom;
//		for(MyJTSGeometry myGeom : myJTSGeometries){
//			geom = myGeom.geometry;
//			double z = myGeom.z_layer;
//			double zAlt = myGeom.altitude;
//			
//			System.out.println(myGeom.agent.getName());
//			System.out.println(geom);
//			System.out.println(geom.getGeometryType() + " zLayer " + z + " alt "+zAlt + " height "+myGeom.height);
//			
//			if(myGeom.height>0){
//				Coordinate vertex;
//				// -2 because the last one = he first one
//				for(int i=0;i<geom.getCoordinates().length-2;i++){
//					vertex = geom.getCoordinates()[i];
//					System.out.println("     "+vertex.x+" "+vertex.y+" "+vertex.z);
//				}
//				for(int i=0;i<geom.getCoordinates().length-1;i++){
//					vertex = geom.getCoordinates()[i];
//					System.out.println("     "+vertex.x+" "+vertex.y+" "+myGeom.height);
//				}
//			}
//			else {
//				for(Coordinate vertex : geom.getCoordinates()){
//					System.out.println("     "+vertex.x+" "+vertex.y+" "+vertex.z);
//				}				
//			}
//			
//			if(myGeom.geometry.getGeometryType() == "Polygon");
//			if(myGeom.geometry.getGeometryType() == "MultiPolygon");
//		}
		
		Output3D.createXML(myJTSGeometries);
	}

	public static void createXML(ArrayList<MyJTSGeometry> myJTSGeometries){     
   
		Document doc = Output3D.createXML();
	
		Element root = doc.createElement("glge");
		
		Element sceneElt = doc.createElement("scene");
		sceneElt.setAttribute("id", "mainscene");
		sceneElt.setAttribute("camera", "#maincamera");
		sceneElt.setAttribute("ambient_color", "#666");
		sceneElt.setAttribute("fog_type", "FOG_NONE");

		Geometry geom;
		for(MyJTSGeometry myGeom : myJTSGeometries){
			// <mesh id="cube">
			Element meshElt = doc.createElement("mesh");
			meshElt.setAttribute( "id", myGeom.agent.getName() );
			Element positionsElt = doc.createElement( "positions" );
			Element normalsElt = doc.createElement("normals");
			Element uv1Elt = doc.createElement("uv1");
			Element facesElt = doc.createElement("faces");
			// object element in the scene
			Element objectElt = doc.createElement("object");
//			<object id="wallobject" mesh="#cube" scale_x="10" scale_y="10" scale_z="10" material="#wallmaterial" />
			objectElt.setAttribute("id", myGeom.agent.getName());
			objectElt.setAttribute("mesh","#"+myGeom.agent.getName());
			objectElt.setAttribute("scale_x", "1");
			objectElt.setAttribute("scale_y", "1");
			objectElt.setAttribute("scale_z", "1");
			objectElt.setAttribute("material", "#"+myGeom.agent.getName()+"material");
			sceneElt.appendChild(objectElt);
			
			geom = myGeom.geometry;
			String positionsText = "";
			String facesText = "";
			if(myGeom.height>0){
				Coordinate vertex;
				// -2 because the last one = he first one
				for(int i=0;i<geom.getCoordinates().length-2;i++){
					vertex = geom.getCoordinates()[i];
					positionsText += " "+vertex.x+","+vertex.y+","+vertex.z + ",\n";
				}
				for(int i=0;i<geom.getCoordinates().length-1;i++){
					vertex = geom.getCoordinates()[i];
					positionsText += ""+vertex.x+","+vertex.y+","+myGeom.height+ ",\n";
				}
			}
			else {
				for(Coordinate vertex : geom.getCoordinates()){
					positionsText += ""+vertex.x+","+vertex.y+","+vertex.z+ ",\n";
				}				
				facesText = Output3D.facesFromVertices(geom.getCoordinates().length);
			}
			// remove the ,\n at the end of the last Coordinate
			positionsText = positionsText.substring(0, positionsText.length()-2); 			
			positionsElt.appendChild( doc.createTextNode(positionsText) );
			facesElt.appendChild(doc.createTextNode(facesText));
			
			meshElt.appendChild(positionsElt);
			meshElt.appendChild(normalsElt);
			meshElt.appendChild(uv1Elt);
			meshElt.appendChild(facesElt);
			root.appendChild(meshElt);
		}
//		defining the camera
		Element cameraElt = doc.createElement("camera");
		cameraElt.setAttribute("id", "maincamera");
		cameraElt.setAttribute("loc_x", "1");		
		cameraElt.setAttribute("loc_y", "20");	
		cameraElt.setAttribute("loc_z", "8");	
		cameraElt.setAttribute("rot_order", "ROT_XZY");	
		cameraElt.setAttribute("xtype", "C_ORTHO");	
		cameraElt.setAttribute("rot_x", "1.56");		
		cameraElt.setAttribute("rot_y", "3.141");	
		cameraElt.setAttribute("rot_z", "0");			
		root.appendChild(cameraElt);

		Element lightElt = doc.createElement("light");
		lightElt.setAttribute("id", "mainlight");
		lightElt.setAttribute("loc_x","0");
		lightElt.setAttribute("loc_y","15");
		lightElt.setAttribute("loc_z","10");
		lightElt.setAttribute("rot_x","-1.3");
		lightElt.setAttribute("attenuation_constant","0.5");
		lightElt.setAttribute("type","L_POINT");	
		sceneElt.appendChild(lightElt);
		
		root.appendChild(sceneElt);
		
		doc.appendChild(root);
		Output3D.writeXML(doc);
	}
	
	public static String facesFromVertices(int nbVertices){
		String res = "";
		for(int i = 2; i<nbVertices;i++){
			res+= "1,"+i+","+(i+1)+",";
		}
		res = res.substring(0, res.length()-1); 			
		return res;
	}
	
		public static Document createXML() {
			DocumentBuilder docBuilder = null;

			try {
				docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();			
			} catch (ParserConfigurationException e) {
				System.err.println("Impossible to create a DocumentBuilder.");
				System.exit(1);
			}
			Document doc = docBuilder.newDocument();
			
			return doc;
		}

		public static void writeXML(Document doc) 	{
			DOMSource source = new DOMSource(doc);
			IScope scope = GAMA.getDefaultScope();
			try {
				Files.newFolder(scope, FILE3DFOLDER);
			} catch (GamaRuntimeException e1) {
				e1.addContext("Impossible to create folder " + FILE3DFOLDER);
				GAMA.reportError(e1);
				e1.printStackTrace();
				return;
			}
			String file3DFile =
				scope.getSimulationScope().getModel()
					.getRelativeFilePath(FILE3DFOLDER + "/" + "level.xml", false);
			
			try{
				FileWriter out = new FileWriter(file3DFile);
				
				StringWriter writer = new StringWriter();
				StreamResult result = new StreamResult(writer);
	
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer;
				transformer = tf.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");	
				transformer.transform(source, result);
				String stringResult = writer.toString();		
				final PrintWriter docWriterXML = new PrintWriter(out);
				docWriterXML.append(stringResult).println("");
				docWriterXML.close();
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Finished!!");
		}	  
	  
}
