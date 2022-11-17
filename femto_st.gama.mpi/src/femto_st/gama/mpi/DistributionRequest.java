package femto_st.gama.mpi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

enum RequestType
{
	GET_ALL_AGENT,
	GET_AGENT_IN_INNER_OLZ,
	GET_AGENT_IN_OUTER_OLZ,
	GET_AGENT_IN_MAIN_AREA,
	
	GATHER_ATTRIBUTE_FROM_EACH_PROCESS
}

public class DistributionRequest implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	RequestType requestType;
	int source;
	
	DistributionRequest(RequestType requestType, int source)
	{
		this.requestType = requestType;
		this.source = source;
	}
	
	DistributionRequest(int requestTypeOrdinal, int source)
	{
		System.out.println("requestTypeOrdinal = "+requestTypeOrdinal);
		this.requestType = RequestType.values()[requestTypeOrdinal];
		this.source = source;
	}
	
	
	DistributionRequest()
	{
	}
	
	public void writeObject(ObjectOutputStream oos) throws IOException 
	{
		System.out.println("writeObject begin");
		//oos.defaultWriteObject();
		System.out.println("requestType write");
        oos.writeInt(requestType.ordinal());
		System.out.println("source write");
        oos.writeInt(source);
        //oos.flush();
    }

	public void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException
    {
		System.out.println("readObject begin");
		//ois.defaultReadObject();
		System.out.println("requestType read");
    	Integer requestType = ois.readInt(); // requestType
		System.out.println("requestType read");
    	Integer source = ois.readInt(); // source

		System.out.println("requestType creating");
        this.requestType = RequestType.values()[requestType];
		System.out.println("requestType creating");
        this.source = source;
        
	    System.out.println("Request received = "+this);    
    }

    public String toString(){
    	return "source: " + this.source + "   ////    RequestType : "+requestType.name();
    }
}
