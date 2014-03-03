package msi.gama.jogl.utils.collada;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
 
public class ColladaReaderDom {
 
	public static void main(String argv[]) {
 
	  try {
 
		//File fXmlFile = new File("/Users/Arno/Projects/Gama/Sources/GAMA_CURRENT/msi.gama.jogl/src/test.dae");
		File fXmlFile = new File("/Users/Arno/Projects/Gama/Sources/GAMA_CURRENT/msi.gama.jogl/src/collada/cube_triangulate.dae");
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
 
		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("library_geometries");
		System.out.println("-----------------------");
 
		for (int temp = 0; temp < nList.getLength(); temp++) {
 
		   Node nNode = nList.item(temp);
		   if (nNode.getNodeType() == Node.ELEMENT_NODE) {
 
		      Element eElement = (Element) nNode;
 
		      //System.out.println("Geometry : " + getTagValue("geometry", eElement));
		      
		      
		      String id ="";
		      id = ((Element)eElement).getAttribute(id);
		      System.out.println("id="+id);

		      //System.out.println("Last Name : " + getTagValue("lastname", eElement));
	            //  System.out.println("Nick Name : " + getTagValue("nickname", eElement));
		      //System.out.println("Salary : " + getTagValue("salary", eElement));
 
		   }
		}
	  } catch (Exception e) {
		e.printStackTrace();
	  }
  }
 
  private static String getTagValue(String sTag, Element eElement) {
	NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
 
        Node nValue = (Node) nlList.item(0);
 
	return nValue.getNodeValue();
  }
 
}