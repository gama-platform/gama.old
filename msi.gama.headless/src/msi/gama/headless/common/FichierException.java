package msi.gama.headless.common;


import java.io.*;



/**
exception renvoyer lorsqu'il y a une erreur lors du chargement d'un fichier
*/
public class FichierException extends IOException
	{
		/**
		constructeur
		*/
	public	FichierException(String fileName, String msg)
		{
			super("Erreur lors du chargement du fichier " + fileName + " : " + msg);
		}
	}
