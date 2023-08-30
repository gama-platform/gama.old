package MPICommunication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ummisco.gama.dev.utils.DEBUG;

public class MPIRequest implements Serializable
{	
	static
	{
		DEBUG.OFF();
	}
	
	enum RequestType
	{
		OLZ_AGENT
	}
	private static final long serialVersionUID = 1L;
	
	RequestType requestType;
	int MPIRankSource;
	
	MPIRequest(RequestType requestType, int mpiRankSource)
	{
		this.requestType = requestType;
		this.MPIRankSource = mpiRankSource;
	}
	
	MPIRequest(int requestTypeOrdinal, int mpiRankSource)
	{
		DEBUG.OUT("requestTypeOrdinal = " + requestTypeOrdinal);
		DEBUG.OUT("MPIRankSource = " + MPIRankSource);
		
		this.requestType = RequestType.values()[requestTypeOrdinal];
		this.MPIRankSource = mpiRankSource;
	}
	
	protected RequestType getRequestType() {
		return requestType;
	}

	protected void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}

	protected int getMPIRankSource() {
		return MPIRankSource;
	}

	protected void setMPIRankSource(int mPIRankSource) {
		MPIRankSource = mPIRankSource;
	}
	
	public void writeObject(ObjectOutputStream oos) throws IOException 
	{
		DEBUG.OUT("writeObject begin");
		oos.defaultWriteObject();
		DEBUG.OUT("requestType write");
        oos.writeInt(requestType.ordinal());
        DEBUG.OUT("source write");
        oos.writeInt(MPIRankSource);
        oos.flush();
    }

	public void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException
    {
		DEBUG.OUT("readObject begin");
		ois.defaultReadObject();
		DEBUG.OUT("requestType read");
    	Integer requestType = ois.readInt(); // requestType
    	DEBUG.OUT("requestType read");
    	Integer source = ois.readInt(); // source

    	DEBUG.OUT("requestType creating");
        this.requestType = RequestType.values()[requestType];
        DEBUG.OUT("requestType creating");
        this.MPIRankSource = source;
        
        DEBUG.OUT("Request received = "+this);    
    }

    @Override
	public String toString(){
    	return "source: " + this.MPIRankSource + "   ////    RequestType : " + requestType.name();
    }	
}
