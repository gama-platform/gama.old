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
import org.w3c.dom.NodeList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.jogl.utils.GraphicDataType.MyJTSGeometry;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Files;


public class Output3D {
	public final static String FILE3DFOLDER = "file3D";
	Document doc;
	// int lastCycleStored; 
	int nbCycle;
	
//	public Output3D() {
//		doc = Output3D.createXML();
//	}

	public Output3D(ILocation nbCycles,
							ArrayList<MyJTSGeometry> myJTSGeometries, JOGLAWTGLRenderer openGLGraphicsGLRender) {
		doc = Output3D.createXML();
		nbCycle = (int)nbCycles.getY();
		//initGLGEModel(myJTSGeometries,openGLGraphicsGLRender);
		//writeXML();

		System.out.println("NbCycles to store: "+ nbCycle);
	}
	
	public void updateOutput3D(ArrayList<MyJTSGeometry> myJTSGeometries, JOGLAWTGLRenderer openGLGraphicsGLRender){
		// TODO
		int currentClock = GAMA.getDefaultScope().getClock().getCycle();
		System.out.println("Nb cycle " + GAMA.getDefaultScope().getClock().getCycle());
		if(currentClock == 0){
			initGLGEModel(myJTSGeometries,openGLGraphicsGLRender);
		}
		
		// Update of the animations
		for(MyJTSGeometry myGeom : myJTSGeometries){
			String animID = myGeom.agent.getName()+"anim";
			// Find the Element with id = animID
			// add a position
			// Animations
			Element animVec = doc.getElementById(animID);
			System.out.println(animID + "  " + animVec);
			NodeList animCurveList = animVec.getElementsByTagName("animation_curve");
			for(int i = 0 ; i < animCurveList.getLength();i++){
				Element animCurveElt = (Element) animCurveList.item(i);
				System.out.println(animCurveElt);
				Element linPtElt = doc.createElement("linear_point");
				linPtElt.setAttribute("x", ""+currentClock);
		
				if("LocX".equals(animCurveElt.getAttribute("channel"))){
					linPtElt.setAttribute("y", "" + myGeom.agent.getLocation().getX()); 
				} else if("LocY".equals(animCurveElt.getAttribute("channel"))) {
					linPtElt.setAttribute("y", "" + myGeom.agent.getLocation().getY()); 
				} else if("LocZ".equals(animCurveElt.getAttribute("channel"))) {
					linPtElt.setAttribute("y", "" + myGeom.agent.getLocation().getZ()); 
				}
				animCurveElt.appendChild(linPtElt);
			}
		}	
		
		if(currentClock == nbCycle){
			writeXML();
		}
	}
	
	public void initGLGEModel(ArrayList<MyJTSGeometry> myJTSGeometries, JOGLAWTGLRenderer openGLGraphicsGLRender){     
	
		Element root = doc.createElement("glge");
		
		Element sceneElt = doc.createElement("scene");
		sceneElt.setAttribute("id", "mainscene");
		sceneElt.setAttribute("camera", "#maincamera");
		sceneElt.setAttribute("ambient_color", "#666");
		sceneElt.setAttribute("fog_type", "FOG_NONE");

		Element groupElt = doc.createElement("group");
		groupElt.setAttribute("id", "objects");
		for(MyJTSGeometry myGeom : myJTSGeometries){
			// For each geometry, we write a mesh, a material, animations and an object in the scene
			String materialID = myGeom.agent.getName()+"material";
			//String animationID = myGeom.agent.getName()+"animation";
			Element meshElt = createMeshElement(myGeom);
			root.appendChild(meshElt);
			
			// object element in the scene
			Element objectElt = doc.createElement("object");
			objectElt.setAttribute("id", myGeom.agent.getName());
			objectElt.setAttribute("name", myGeom.agent.getName());
			objectElt.setAttribute("mesh","#"+myGeom.agent.getName());
			objectElt.setAttribute("scale_x", "1");
			objectElt.setAttribute("scale_y", "1");
			objectElt.setAttribute("scale_z", "1");
			objectElt.setAttribute("material", "#"+materialID);
			objectElt.setAttribute("animation", "#"+myGeom.agent.getName() + "anim");
			groupElt.appendChild(objectElt);
			
			// the material
			Element materialElt = doc.createElement("material");
			materialElt.setAttribute("id", materialID);
			materialElt.setAttribute("name", materialID);
			materialElt.setAttribute("specular", "0.5");
			materialElt.setAttribute("colorR", ""+(myGeom.color.getRed()/255));
			materialElt.setAttribute("colorG", ""+(myGeom.color.getGreen()/255));			
			materialElt.setAttribute("colorB", ""+(myGeom.color.getBlue()/255));
			// materialElt.setAttribute("animation", "#"+animationID);
			root.appendChild(materialElt);
			
			// Animations
			Element animVec = doc.createElement("animation_vector");
			animVec.setAttribute("id", myGeom.agent.getName() + "anim");
			animVec.setIdAttribute("id",true);
			animVec.setAttribute("frames",""+nbCycle);

			String[] tabCoord = {"X","Y","Z"};
			for(String coord : tabCoord){
				Element animCurveElt = doc.createElement("animation_curve");
				animCurveElt.setAttribute("channel", "Loc"+coord);
//				Element linPtElt = doc.createElement("linear_point");
//				linPtElt.setAttribute("x", "0");
//				linPtElt.setAttribute("y", "" + ("X".equals(coord)?myGeom.agent.getLocation().getX():
//												 "Y".equals(coord)?myGeom.agent.getLocation().getY():
//													 myGeom.agent.getLocation().getZ()));
//				animCurveElt.appendChild(linPtElt);
				animVec.appendChild(animCurveElt);
			}
			sceneElt.appendChild(animVec);
		}
		sceneElt.appendChild(groupElt);
		
//		defining the camera
		Element cameraGroupElt = doc.createElement("group");
		cameraGroupElt.setAttribute("id", "cameraOffset");
		Element cameraElt = doc.createElement("camera");
		cameraElt.setAttribute("id", "maincamera");
		cameraElt.setAttribute("loc_x", ""+openGLGraphicsGLRender.camera.getxPos());		
		cameraElt.setAttribute("loc_y", ""+openGLGraphicsGLRender.camera.getyPos());	
		cameraElt.setAttribute("loc_z", ""+openGLGraphicsGLRender.camera.getzPos());
		cameraElt.setAttribute("rot_order", "ROT_XZY");	
		cameraElt.setAttribute("xtype", "C_ORTHO");	
		cameraElt.setAttribute("rot_x", "0");		
		cameraElt.setAttribute("rot_y", "0");	
		cameraElt.setAttribute("rot_z", "0");			
		cameraGroupElt.appendChild(cameraElt);
		sceneElt.appendChild(cameraGroupElt);

		// A remettre avec les normals
//		Element lightElt = doc.createElement("light");
//		lightElt.setAttribute("id", "mainlight");
//		lightElt.setAttribute("loc_x","0");
//		lightElt.setAttribute("loc_y","15");
//		lightElt.setAttribute("loc_z","10");
//		lightElt.setAttribute("rot_x","-1.3");
//		lightElt.setAttribute("attenuation_constant","0.5");
//		lightElt.setAttribute("type","L_POINT");	
//		sceneElt.appendChild(lightElt);
		
		root.appendChild(sceneElt);
		
		doc.appendChild(root);
	}
	
	public Element createMeshElement(MyJTSGeometry myGeom) {
		Geometry geom;
		
		// <mesh id="cube">
		Element meshElt = doc.createElement("mesh");
		meshElt.setAttribute( "id", myGeom.agent.getName() );
		
		Element positionsElt = doc.createElement( "positions" );
		// Element normalsElt = doc.createElement("normals");
		// Element uv1Elt = doc.createElement("uv1");
		Element facesElt = doc.createElement("faces");
		
		geom = myGeom.geometry;
		String positionsText = "";
		String facesText = "";
		if(myGeom.height>0){
			Coordinate vertex;
			// -1 because the last one = he first one
			for(int i=0;i<geom.getCoordinates().length-1;i++){
				vertex = geom.getCoordinates()[i];
				positionsText += ""+ (((Double)Double.NaN).equals(vertex.x)?0.0:vertex.x)+
								 ","+(((Double)Double.NaN).equals(vertex.y)?0.0:vertex.y)+
								 ","+(((Double)Double.NaN).equals(vertex.z)?0.0:vertex.z)+ ",\n";				
			}
			for(int i=0;i<geom.getCoordinates().length-1;i++){
				vertex = geom.getCoordinates()[i];
				positionsText += ""+ (((Double)Double.NaN).equals(vertex.x)?0.0:vertex.x)+
						 		 ","+(((Double)Double.NaN).equals(vertex.y)?0.0:vertex.y)+
						 		 ","+myGeom.height+ ",\n";
			}
			facesText = Output3D.facesFromVertices3D(geom.getCoordinates().length-1);			
		}
		else {
			Coordinate vertex;
			// -1 because the last one = he first one
			for(int i=0;i<geom.getCoordinates().length-1;i++){	
				vertex = geom.getCoordinates()[i];				
				positionsText += ""+ (((Double)Double.NaN).equals(vertex.x)?0.0:vertex.x)+
								 ","+(((Double)Double.NaN).equals(vertex.y)?0.0:vertex.y)+
								 ","+(((Double)Double.NaN).equals(vertex.z)?0.0:vertex.z)+ ",\n";
			}				
			facesText = Output3D.facesFromVertices(geom.getCoordinates().length - 1);
		}
		// remove the ,\n at the end of the last Coordinate
		positionsText = positionsText.substring(0, positionsText.length()-2); 			
		positionsElt.appendChild( doc.createTextNode(positionsText) );
		
		facesElt.appendChild(doc.createTextNode(facesText));
		
		meshElt.appendChild(positionsElt);
		// meshElt.appendChild(normalsElt);
		// meshElt.appendChild(uv1Elt);
		meshElt.appendChild(facesElt);		
		
		return meshElt;
	}

	// nbVertrices is the number of vertices of both top and bottom faces
	public static String facesFromVertices3D(int nbVertices){
		String res = "";
		res += facesFromVertices(0,nbVertices-1) + ",";
		res += facesFromVertices(nbVertices, 2*nbVertices-1) + ",";
		for(int i = 0; i < nbVertices - 2; i++){
			res += i + "," + (i+1) + "," + (i+nbVertices) + ",";
			res += (i+1) + "," + (i+nbVertices) + "," + (i+nbVertices + 1) + ",";
		}
		// the last face: hand-made
		res += ","+(nbVertices -1) + ",0," + (2*nbVertices - 1) + ",";
		res += "0," + (nbVertices) + "," + (2*nbVertices - 1);		
		return res;
	}	
	
	public static String facesFromVertices(int nbVertices){
		return facesFromVertices(0,nbVertices-1);
	}
	
	public static String facesFromVertices(int firstVertex, int lastVertex){
		String res = "";
		for(int i = (firstVertex + 1); i<lastVertex;i++){
			res+= firstVertex+","+i+","+(i+1)+",";
		}
		// To remove the last ","
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

	public void writeXML() 	{
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
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Finished!!");
	}	  
	  
}
