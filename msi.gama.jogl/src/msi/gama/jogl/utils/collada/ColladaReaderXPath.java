package msi.gama.jogl.utils.collada;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import com.jogamp.common.nio.Buffers;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

public class ColladaReaderXPath {
	
	private static XPath xpath;
	private static Document doc;

	public static void main(String[] args) throws ParserConfigurationException,
			SAXException, IOException, XPathExpressionException {

		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!

		DocumentBuilder builder = domFactory.newDocumentBuilder();

		Document doc = builder
				.parse("/Users/Arno/Projects/Gama/Sources/GAMA_CURRENT/msi.gama.jogl/src/collada/cube_triangulate.dae");

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();

		XPathExpression expr = xpath
				.compile("//library_geometries//geometry//mesh//source");

		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;

		for (int i = 0; i < nodes.getLength(); i++) {

			
			String name = getNodeAttr("name", nodes.item(i));
			System.out.println(nodes.item(i).getNodeName() + " " + name);
			if(name.equalsIgnoreCase("position")){
				GetFloatArrayPosition(nodes.item(i));	
			}
			/*else if(name.equalsIgnoreCase("normal")){
				GetFloatArrayNormal(nodes.item(i));
			}*/
		}
	}
	
	public ColladaReaderXPath(String filename){
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!

		DocumentBuilder builder = null;
		try {
			builder = domFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			doc = builder.parse(filename);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		XPathFactory factory = XPathFactory.newInstance();
		xpath = factory.newXPath();
		
	}
	
	
	public static float[] GetObjectVertex() throws XPathExpressionException{
		
		XPathExpression expr = xpath
				.compile("//library_geometries//geometry//mesh//source");
		
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;

		for (int i = 0; i < nodes.getLength(); i++) {
			String name = getNodeAttr("name", nodes.item(i));
			System.out.println(nodes.item(i).getNodeName() + " " + name);
			if(name.equalsIgnoreCase("position")){
				return GetFloatArrayPosition(nodes.item(i));	
			}
		}
		return null;
	}
	
	
public static float[] ProcessMesh() throws XPathExpressionException{
		
		XPathExpression expr = xpath
				.compile("//library_geometries//geometry//mesh");
		
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;

		//for each mesh
		for (int i = 0; i < nodes.getLength(); i++) {

			
			String name = getNodeAttr("source", nodes.item(i));
			System.out.println(nodes.item(i).getNodeName() + " " + name);
			if(name.equalsIgnoreCase("position")){
				return GetFloatArrayPosition(nodes.item(i));	
			}
		}
		return null;
	}

	public static float[] GetFloatArrayPosition(Node source ) {		
		Node float_array_node= getNode("float_array", source.getChildNodes());	
		String[] flostr = getNodeValue(float_array_node).split(" ");
		float[] floats = new float[flostr.length];

		for(int i= 0; i<flostr.length;i++){
			floats[i]=Float.valueOf(flostr[i]);	
		}
		return floats;
	}
	
	
	public static FloatBuffer GetFloatArrayNormal(Node source ) {

		
		Node float_array_node= getNode("float_array", source.getChildNodes());
		String nb_coords = getNodeAttr("count", float_array_node);
		
		String float_array = getNodeValue(float_array_node);
		
		System.out.println(nb_coords + " coords: " + float_array);

		FloatBuffer position_buffer = Buffers.newDirectFloatBuffer(1);
		return position_buffer;

	}
	
	
	
	
	
	
	
	
	
	
	//////////////////////////////////////////////
	
	protected static Node getNode(String tagName, NodeList nodes) {
	    for ( int x = 0; x < nodes.getLength(); x++ ) {
	        Node node = nodes.item(x);
	        if (node.getNodeName().equalsIgnoreCase(tagName)) {
	            return node;
	        }
	    }
	 
	    return null;
	}
	 
	protected static String getNodeValue( Node node ) {
	    NodeList childNodes = node.getChildNodes();
	    for (int x = 0; x < childNodes.getLength(); x++ ) {
	        Node data = childNodes.item(x);
	        if ( data.getNodeType() == Node.TEXT_NODE )
	            return data.getNodeValue();
	    }
	    return "";
	}
	 
	protected static String getNodeValue(String tagName, NodeList nodes ) {
	    for ( int x = 0; x < nodes.getLength(); x++ ) {
	        Node node = nodes.item(x);
	        if (node.getNodeName().equalsIgnoreCase(tagName)) {
	            NodeList childNodes = node.getChildNodes();
	            for (int y = 0; y < childNodes.getLength(); y++ ) {
	                Node data = childNodes.item(y);
	                if ( data.getNodeType() == Node.TEXT_NODE )
	                    return data.getNodeValue();
	            }
	        }
	    }
	    return "";
	}
	 
	protected static String getNodeAttr(String attrName, Node node ) {
	    NamedNodeMap attrs = node.getAttributes();
	    for (int y = 0; y < attrs.getLength(); y++ ) {
	        Node attr = attrs.item(y);
	        if (attr.getNodeName().equalsIgnoreCase(attrName)) {
	            return attr.getNodeValue();
	        }
	    }
	    return "";
	}
	 
	protected String getNodeAttr(String tagName, String attrName, NodeList nodes ) {
	    for ( int x = 0; x < nodes.getLength(); x++ ) {
	        Node node = nodes.item(x);
	        if (node.getNodeName().equalsIgnoreCase(tagName)) {
	            NodeList childNodes = node.getChildNodes();
	            for (int y = 0; y < childNodes.getLength(); y++ ) {
	                Node data = childNodes.item(y);
	                if ( data.getNodeType() == Node.ATTRIBUTE_NODE ) {
	                    if ( data.getNodeName().equalsIgnoreCase(attrName) )
	                        return data.getNodeValue();
	                }
	            }
	        }
	    }
	 
	    return "";
	}

}