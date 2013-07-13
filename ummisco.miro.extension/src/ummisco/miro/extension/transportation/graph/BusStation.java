package ummisco.miro.extension.transportation.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

public class BusStation {
	private String name;
	private TreeSet<BusStop> stops;
	private Vector<BusLine> lines;
	
	public BusStation(String name) {
		super();
		this.name = name;
		this.stops = new TreeSet<BusStop>();
		this.lines = new Vector<BusLine>();
	}
	
	protected void addStop(BusStop s)
	{
		this.stops.add(s);
		if(!this.lines.contains(s.getLine()))
		{
			this.lines.add(s.getLine());
		}
	}
	public Vector<BusStop> nextDeparture(long timeStamp)
	{
		int index=0;
		Vector<BusStop> res = new Vector<BusStop>();
		Iterator<BusStop> it = stops.iterator();
		BusStop stop = null;
		Vector<BusLine> lin = new Vector<BusLine>();
	
		while(it.hasNext())
		{
			stop = it.next();
			if(!lin.contains(stop.getLine()) && timeStamp <=  stop.getTimeStamp())
			{
				lin.add(stop.getLine());
				res.add(stop);
				System.out.println("Pouet " + timeStamp+ "-> "+stop.getTimeStamp());
				
			}
		}
		
		
		
	/*	while(it.hasNext()&& timeStamp > (stop = it.next()).getTimeStamp());
		do
		{
			if(!lin.contains(stop.getLine())&&timeStamp > )
			{
				lin.add(stop.getLine());
				res.add(stop);
			}
		} while(lin.size()<lines.size() && it.hasNext()&&  (stop = it.next())!= null );
		*/
		return res;
	}
	public String getName()
	{
		return this.name;
	}
	public void displayStop()
	{
		Iterator<BusStop> it = stops.iterator();
		while(it.hasNext())
		{
			BusStop st = it.next();
			System.out.println(st.getNextStop().getStation().getName()+ " "+ st.getTimeStamp());
		}
	}
}
