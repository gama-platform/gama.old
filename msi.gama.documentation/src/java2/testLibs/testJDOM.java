package java2.testLibs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class testJDOM {

	   //Nous allons commencer notre arborescence en créant la racine XML
	   //qui sera ici "personnes".
	   static Element racine = new Element("personnes");

	   //On crée un nouveau Document JDOM basé sur la racine que l'on vient de créer
	   static org.jdom2.Document document = new Document(racine);

	   public static void main(String[] args) throws JDOMException, IOException
	   {
		  // cree();
		   file2DOM();
	   }
	   
	   static void file2DOM() throws JDOMException, IOException{
           	File baseDir = new File("files");
           	File xmlFile = new File(baseDir, "HTMLGamaDoc/batch15.html");
           	SAXBuilder builder = new SAXBuilder();
           	
           	Document doc;
			doc = (Document) builder.build(xmlFile);
			
			Element chapElt = new Element("chapter");
			chapElt.setAttribute(new Attribute("name", "chap1"));
			chapElt.setAttribute(new Attribute("fileName", "fileChap1"));
			
			Element bodyElement = doc.getRootElement().getChild("body");
			System.out.println("BODY Node: " + bodyElement.getName());
			
			Element divMainEltElement = null;
			for(Element elt : bodyElement.getChildren()) {
				if("main".equals(elt.getAttributeValue("id"))) {
					divMainEltElement = elt; 
				}
			}
			System.out.println("DIV Node: " + divMainEltElement.getName());

			List<Element> lElt = divMainEltElement.getChildren();
			for(Element e : lElt) {
				chapElt.addContent(e.clone());
			}			
			bodyElement.removeContent();
			
			bodyElement.addContent(chapElt);
			
			/////////////////////////////////////////////////////////////////////////////
			// Ajout Second fichier !! 
           	File xmlFile2 = new File(baseDir, "HTMLGamaDoc/Types15.html");
            Document doc2 = (Document) builder.build(xmlFile2);			
            
			Element chapElt2 = new Element("chapter");
			chapElt2.setAttribute(new Attribute("name", "chap2"));
			chapElt2.setAttribute(new Attribute("fileName", "fileChap2"));

			Element bodyElement2 = doc2.getRootElement().getChild("body");
			System.out.println("BODY Node: " + bodyElement2.getName());
			
			Element divMainEltElement2 = null;
			for(Element elt : bodyElement2.getChildren()) {
				if("main".equals(elt.getAttributeValue("id"))) {
					divMainEltElement2 = elt; 
				}
			}
			System.out.println("DIV Node: " + divMainEltElement2.getName());

			for(Element e : divMainEltElement2.getChildren()) {
				chapElt2.addContent(e.clone());
			}			
			
			bodyElement.addContent(chapElt2);
			
			
			
         //  	File xmlFile2 = new File(baseDir, "HTMLGamaDoc/Types15.html");
         //  	Document doc2 = (Document) builder.build(xmlFile2);
   			
//           	Element rootNode = document.getRootElement();
//           	List list = rootNode.getChildren();

	         XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());

	         sortie.output(doc, System.out);
	         sortie.output(doc, new FileOutputStream("Exercice2.xml"));
	      //   sortie.output(doc2, System.out);
	         
	   }
	   
	   static void cree(){
		      //On crée un nouvel Element etudiant et on l'ajoute
		      //en tant qu'Element de racine
		      Element etudiant = new Element("etudiant");
		      racine.addContent(etudiant);

		      //On crée un nouvel Attribut classe et on l'ajoute à etudiant
		     //grâce à la méthode setAttribute
		      Attribute classe = new Attribute("classe","P2");
		      etudiant.setAttribute(classe);

		      //On crée un nouvel Element nom, on lui assigne du texte
		      //et on l'ajoute en tant qu'Element de etudiant
		      Element nom = new Element("nom");
		      nom.setText("CynO");
		      etudiant.addContent(nom);

		      //Les deux méthodes qui suivent seront définies plus loin dans l'article
		      affiche();
		      enregistre("Exercice1.xml");
	   }
	   
	 //Ajouter ces deux méthodes à notre classe JDOM1
	   static void affiche()
	   {
	      try
	      {
	         //On utilise ici un affichage classique avec getPrettyFormat()
	         XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
	         sortie.output(document, System.out);
	      }
	      catch (java.io.IOException e){}
	   }

	   static void enregistre(String fichier)
	   {
	      try
	      {
	         //On utilise ici un affichage classique avec getPrettyFormat()
	         XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
	         //Remarquez qu'il suffit simplement de créer une instance de FileOutputStream
	         //avec en argument le nom du fichier pour effectuer la sérialisation.
	         sortie.output(document, new FileOutputStream(fichier));
	      }
	      catch (java.io.IOException e){}
	   }
}
