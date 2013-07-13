package ummisco.miro.extension.transportation.graph;

import java.util.HashMap;
import java.util.Vector;

import ummisco.miro.extension.transportation.dikjstra.Dikjstra;
import ummisco.miro.extension.transportation.dikjstra.WeightedGraph;

public class BusLine {
	private String name;
	HashMap<String,Vector<BusStop>> stops;
	
	public BusLine(String name)
	{
		this.name=name;
		stops = new HashMap<String,Vector<BusStop>>();
	}
	
	public BusStop addStop(String service, BusStation station, long cdate)
	{
		if(!stops.containsKey(service))
			stops.put(service, new Vector<BusStop>());
		BusStop stp = new BusStop(this,station,cdate);
		Vector<BusStop> stopList = stops.get(service);
		if(!stopList.isEmpty())
			stopList.lastElement().setNext(stp);
		stops.get(service).add(stp);
		return stp;
	}
	
	public static void main(String [] arg)
	{
		BusLine bl = new BusLine("test1");
		BusLine b2 = new BusLine("test2");
		BusStation s1 = new BusStation("bus Station 1");
		BusStation s2 = new BusStation("bus Station 2");
		BusStation s3 = new BusStation("bus Station 3");
		BusStation s4 = new BusStation("bus Station 4");
		BusStation s5 = new BusStation("bus Station 5");
		BusStation s6 = new BusStation("bus Station 6");
		
		bl.addStop("service 1", s1, 11);
		bl.addStop("service 1", s2, 13);
		bl.addStop("service 1", s3, 21);
		bl.addStop("service 1", s4, 34);
		bl.addStop("service 2", s1, 15);
		bl.addStop("service 2", s2, 18);
		bl.addStop("service 2", s3, 25);
		bl.addStop("service 2", s4, 36);
	
		b2.addStop("Service 2", s5, 5);
		b2.addStop("Service 2", s3, 45);
		b2.addStop("Service 2", s6, 65);
		Vector<BusStation> bb = new Vector<BusStation>();
		bb.add(s1);
		bb.add(s2);
		bb.add(s3);
		bb.add(s4);
		bb.add(s5);
		bb.add(s6);
		s2.displayStop();
		s3.displayStop();
		WeightedGraph ww = new WeightedGraph(bb);
		//BusStation [] tmm = Dikjstra.dijkstra(ww, 0,16);
	
	}

}
