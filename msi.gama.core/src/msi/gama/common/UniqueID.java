package msi.gama.common;

/**
 * Class that hold the rank where has been created an agent and his id
 * This class is only use for the purpose of helping the distribution of gama models
 * 
 * @author Lucas Grosjean
 *
 */
public class UniqueID {
	
	int initialRank;
	int id;
	
	public UniqueID(int initRank, int uID)
	{
		System.out.println("created new uniqueID struct for rank " + initRank + " : " + uID);
		this.initialRank = initRank;
		this.id = uID;
	}
	
	public int getID()
	{
		return id;
	}
	
	public void setID(int id)
	{
		this.id = id;
	}
	
	public int getInitialMpiRank()
	{
		return initialRank;
	}
	
	public String toString(){//overriding the toString() method
		return "UNIQUEID (initRank :: id) = " + initialRank + " :: " + id ;
	}
}
