package femto_st.gama.mpi;
import java.util.HashMap;


public class LocationManager 
{
	HashMap<Integer, Integer> agentsLocation;
	
	LocationManager()
	{
		this.agentsLocation = new HashMap<Integer, Integer>();
	}
	
	void updateAgentPosition(int uniqueID, int newPosition)
	{
		this.agentsLocation.put(uniqueID, newPosition);
	}
	
	int getAgentPosition(int uniqueID)
	{
		return this.agentsLocation.get(uniqueID);
	}
}
