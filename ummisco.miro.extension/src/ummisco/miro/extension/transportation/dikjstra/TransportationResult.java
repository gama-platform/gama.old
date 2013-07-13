package ummisco.miro.extension.transportation.dikjstra;

public class TransportationResult {
	public long duration;
	public  int [] pred ;
	
	public TransportationResult(long dura, int [] pre)
	{
		this.pred=pre;
		this.duration = dura;
	}
}
