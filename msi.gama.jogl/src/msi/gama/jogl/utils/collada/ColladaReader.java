package msi.gama.jogl.utils.collada;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import msi.gama.jogl.utils.GraphicDataType.MyJTSGeometry;

import com.jmex.model.collada.ColladaImporter;
 
public class ColladaReader {
 
	public ColladaReader(){
		//ColladaImporter myColImporter = ColladaImporter.getInstance();
		
		InputStream input = null;
		try {
			input = new FileInputStream("/Users/Arno/Projects/Gama/Sources/GAMA_CURRENT/msi.gama.jogl/src/collada/cube_triangulate.dae");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String name= "za";
		ColladaImporter.load(input,name);
		
		
		ArrayList<String> geometryNames = ColladaImporter.getGeometryNames();
		
		Iterator<String> it = geometryNames.iterator();
		while (it.hasNext()) {
		System.out.println( it);
		}
	}
}
	