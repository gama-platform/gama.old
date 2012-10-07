package java2.testLibs;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.htmlparser.jericho.*;

public class testJericho {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String ligne = "";
		String fichier = "src/doc.wiki";

		BufferedReader ficTexte;
		try {
			ficTexte = new BufferedReader(new FileReader(new File(fichier)));
			if (ficTexte == null) {
				throw new FileNotFoundException("Fichier non trouv�: "+ fichier);
			}
			do {
				ligne = ficTexte.readLine();
				if (ligne != null) {
					System.out.println(ligne);
					System.out.println("J: "+ toJericho(ligne));
				}
			} while (ficTexte != null);
			ficTexte.close();
			System.out.println("\n");
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public static String toJericho(String line){
	    Source htmlSrc = new Source(line);
	    Segment htmlSeg = new Segment(htmlSrc, 0, htmlSrc.length());
	    Renderer htmlRend = new Renderer(htmlSeg);
	    return htmlRend.toString();
	}
}
