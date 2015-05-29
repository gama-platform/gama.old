package ummisco.gama.opengl.files;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class MtlLoader {
	
	public ArrayList Materials = new ArrayList();
	
	public class mtl {
		public String name;
		public int mtlnum;
		public float d = 1f;
		public float[] Ka = new float[3];
		public float[] Kd = new float[3];
		public float[] Ks = new float[3];
	}
	
	public MtlLoader(BufferedReader ref, String pathtoimages) {
		loadobject(ref, pathtoimages);
		cleanup();
	}
	
	private void cleanup() {
	}
	
	public int getSize() {
		return Materials.size();
	}
	
	public float getd(String namepass) {
		float returnfloat = 1f;
		for (int i=0; i < Materials.size(); i++) {
			mtl tempmtl = (mtl)Materials.get(i);
			if (tempmtl.name.matches(namepass)) {
				returnfloat = tempmtl.d;
			}
		}
		return returnfloat;
	}

	public float[] getKa(String namepass) {
		float[] returnfloat = new float[3];
		for (int i=0; i < Materials.size(); i++) {
			mtl tempmtl = (mtl)Materials.get(i);
			if (tempmtl.name.matches(namepass)) {
				returnfloat = tempmtl.Ka;
			}
		}
		return returnfloat;
	}
	
	public float[] getKd(String namepass) {
		float[] returnfloat = new float[3];
		for (int i=0; i < Materials.size(); i++) {
			mtl tempmtl = (mtl)Materials.get(i);
			if (tempmtl.name.matches(namepass)) {
				returnfloat = tempmtl.Kd;
			}
		}
		return returnfloat;
	}
	
	public float[] getKs(String namepass) {
		float[] returnfloat = new float[3];
		for (int i=0; i < Materials.size(); i++) {
			mtl tempmtl = (mtl)Materials.get(i);
			if (tempmtl.name.matches(namepass)) {
				returnfloat = tempmtl.Ks;
			}
		}
		return returnfloat;
	}
	

	private void loadobject(BufferedReader br, String pathtoimages) {
		int linecounter = 0;
		try {
			
			String newline;
			boolean firstpass = true;
			mtl matset = new mtl();
			int mtlcounter = 0;
			
			while (((newline = br.readLine()) != null)) {
				linecounter++;
				newline = newline.trim();
				if (newline.length() > 0) {
					if (newline.charAt(0) == 'n' && newline.charAt(1) == 'e' && newline.charAt(2) == 'w') {
						if (firstpass) {
							firstpass = false;
						} else {
							Materials.add(matset);
							matset = new mtl();
						}
						String[] coordstext = new String[2];
						coordstext = newline.split("\\s+");
						matset.name = coordstext[1];
						matset.mtlnum = mtlcounter;
						mtlcounter++;
					}
					if (newline.charAt(0) == 'K' && newline.charAt(1) == 'a') {
						float[] coords = new float[3];
						String[] coordstext = new String[4];
						coordstext = newline.split("\\s+");
						for (int i = 1;i < coordstext.length;i++) {
							coords[i-1] = Float.valueOf(coordstext[i]).floatValue();
						}
						matset.Ka = coords;
					}
					if (newline.charAt(0) == 'K' && newline.charAt(1) == 'd') {
						float[] coords = new float[3];
						String[] coordstext = new String[4];
						coordstext = newline.split("\\s+");
						for (int i = 1;i < coordstext.length;i++) {
							coords[i-1] = Float.valueOf(coordstext[i]).floatValue();
						}
						matset.Kd = coords;
					}
					if (newline.charAt(0) == 'K' && newline.charAt(1) == 's') {
						float[] coords = new float[3];
						String[] coordstext = new String[4];
						coordstext = newline.split("\\s+");
						for (int i = 1;i < coordstext.length;i++) {
							coords[i-1] = Float.valueOf(coordstext[i]).floatValue();
						}
						matset.Ks = coords;
					}
					if (newline.charAt(0) == 'd') {
						String[] coordstext = newline.split("\\s+");
						matset.d = Float.valueOf(coordstext[1]).floatValue();
					}
				}
			}
			Materials.add(matset);
			
		}
		catch (IOException e) {
			System.out.println("Failed to read file: " + br.toString());
			e.printStackTrace();		
		}
		catch (NumberFormatException e) {
			System.out.println("Malformed MTL (on line " + linecounter + "): " + br.toString() + "\r \r" + e.getMessage());
		}
		catch (StringIndexOutOfBoundsException e) {
			System.out.println("Malformed MTL (on line " + linecounter + "): " + br.toString() + "\r \r" + e.getMessage());
		}
	}
}
