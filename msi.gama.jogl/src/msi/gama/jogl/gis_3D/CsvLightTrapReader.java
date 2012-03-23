package msi.gama.jogl.gis_3D;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.geotools.swing.data.JFileDataStoreChooser;

/**
 * This example reads data for point locations and associated attributes from a
 * comma separated text (CSV) file and exports them as a new shapefile. It
 * illustrates how to build a feature type.
 * <p>
 * Note: to keep things simple in the code below the input file should not have
 * additional spaces or tabs between fields.
 */
public class CsvLightTrapReader {

	int nbLightTraps = 36;
	int nbDays = 30;
	public float lightTrapsDatas[][] = new float[nbLightTraps][nbDays];
	
	public CsvLightTrapReader(){
		
	}

	public void ReadLightTrapsDatas() throws Exception {

		File file = JFileDataStoreChooser.showOpenFile("csv", null);
		if (file == null) {
			return;
		}

		BufferedReader reader = new BufferedReader(new FileReader(file));
		try {
			/* First line of the data file is the header */
			String line = reader.readLine();
			System.out.println("Header: " + line);
			int curLine = 0;
			
			for (line = reader.readLine(); line != null; line = reader
					.readLine()) {
				System.out.println("curLine" + curLine);
				if (line.trim().length() > 0) { // skip blank lines
					String tokens[] = line.split("\\,");

					double latitude = Double.parseDouble(tokens[0]);
					double longitude = Double.parseDouble(tokens[1]);
					System.out.println("curline" + curLine);
					System.out.println(latitude);
					System.out.println(longitude);
					
					//String name = tokens[3].trim();
					//System.out.println("name" + name);

					for (int i = 0; i < 30; i++) {
						System.out.println(tokens[i+5].trim());
						lightTrapsDatas[curLine][i]=Float.parseFloat(tokens[i+5]);
					}

					
				}
				curLine++;
				
			}
		} finally {
			reader.close();
		}

	}

}