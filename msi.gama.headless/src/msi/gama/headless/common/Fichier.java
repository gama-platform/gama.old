package msi.gama.headless.common;

import java.io.*;
import java.util.*;

public class Fichier
 {
 	public final static int IN=1;
 	public final static int OUT=2;
 	public final static int INOUT=3;

 	
 	/**
	Flux de lecture
	*/	
	private BufferedReader lecture;
 	/**
 	Flux d'�criture dans le fichier
 	*/
 	private BufferedWriter ecriture; 	
 	/**
	fichier ouvert
	*/
	private File fich;
 	
	
	private int getExtentionSeparator()
	{
		String name=fich.getName();
		for(int i=name.length()-1;i>=0;i--)
			if(name.charAt(i)=='.')
				return i;
		return -1;
	}
	
	public String nom()
	{
		return fich.getName();
	}
	
	public String getPath()
	{
		return fich.getParent();
	}
	
	public String nomAbrege()
	{
		return nom().substring(0,getExtentionSeparator());
	}
	
	public String extention()
	{
		int extPos=getExtentionSeparator();
		return nom().substring(extPos+1,nom().length()-extPos-1);
	}

	
	/**
	contructeur
	@param nom nom du fichier
	@throws FichierException erreur lors de l'ouverture
	*/
 	public Fichier(String nom) throws  FichierException
 	 {
 	 	fich=new File(nom);
 	 	try
 	 	{
		 lecture=new BufferedReader(new FileReader(fich));
 	 	}
		catch(FileNotFoundException e)
		{
			throw(new FichierException(nom, "le fichier n'a pas pu être trouvé.")); 
		}
 	 }
 	 
	/**
	contructeur
	@param nom nom du fichier
	@param mode mode d'ouverture du fichier : IN:Lecture <br> OUT: ecriture <br> INOUT: Lecture Ecriture
	@throws FichierException erreur lors de l'ouverture
	*/
 	public Fichier(String nom,int mode) throws  FichierException
 	 {
		 	fich=new File(nom);
 	 	try
 	 	{
		 switch(mode)
 	 		{
 	 			case  IN  : lecture=new BufferedReader(new FileReader(fich)); break; 
 	 			case	OUT : ecriture= new BufferedWriter(new FileWriter(fich)); break;
 	 			case  INOUT : lecture=new BufferedReader(new FileReader(fich));
 	 										ecriture= new BufferedWriter(new FileWriter(fich));
 	 										break;
 	 			default : ;
 	 		}
 	 		 	 			
 	 	}
		catch(FileNotFoundException e)
		{
			throw(new FichierException(nom, "le fichier n'a pas pu être trouvé.")); 
		}
		catch(IOException e)
		{
			throw(new FichierException(nom, "erreur d'ouverture"));
		}
		
 	 }

	 
 	/**
	ferme physiquement le fichier. il peut etre alors lu par un autre programme
	*/
	public void dispose()
 	{
		lecture=null;
		ecriture=null;
		fich=null;
 	}
	
	/**
	fonction effectuant la lecture du fichier
	@return vecteur contenant toute les lignes du fichier d�coup� en string
	*/
 	public Vector lire() throws IOException
 	 {
 	 	Vector temp=new Vector();
 	 	while(lecture.ready())
 	 	 {
 	 	 	temp.addElement(lecture.readLine());
 	 	 }
 	 	return temp;
 	 }
 	 
 	 /**
 	 fonction d'ecriture d'une chaine de caract�re au d�but d'un fichier
 	 @param chaine chaine de caract�re � �crire dans le fichier
 	 */
 public void ecrire(String chaine) throws IOException
 	{
 		ecriture.write(chaine,0,chaine.length());
 		ecriture.flush();
 		
 	}
 } 
 
 
