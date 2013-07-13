package ummisco.miro.extension.transportation.dikjstra;

import java.util.HashMap;
import java.util.Vector;

import msi.gama.metamodel.agent.IAgent;

import ummisco.miro.extension.transportation.graph.BusStation;
import ummisco.miro.extension.transportation.graph.BusStop;

public class WeightedGraph {
	Vector<BusStation> stations;
	HashMap<BusStation, HashMap<BusStation, Float>> footConnection;
	long currentTime;
	
	class Transit
	{
		public int stationID;
		public long timeStamp;
		Transit(int s, long d)
		{
			stationID = s;
			timeStamp =  d;
		}
	}
	
	public void addFootConnection(BusStation st1, BusStation st2, double duration)
	{
		if(footConnection.containsKey(st1) == false)
		{
			footConnection.put(st1,new HashMap<BusStation, Float>());
		}
		if(footConnection.containsKey(st2) == false)
		{
			footConnection.put(st2,new HashMap<BusStation, Float>());
		}

		this.footConnection.get(st1).put(st2, new Float(duration));
		this.footConnection.get(st2).put(st1, new Float(duration));
		
		
		
	}
	
	public int getBusStationId(BusStation m)
	{
		return stations.indexOf(m);
	}
	
	public BusStation getStation(int s)
	{
		if(s < 0 || s >= stations.size())
			return null;
		return stations.get(s);
	}
	
	public WeightedGraph(Vector<BusStation> statio )
	{
		this.stations=statio;
		this.footConnection = new HashMap<BusStation, HashMap<BusStation,Float>>();
	}
	
	public int size()
	{
		return stations.size();
	}
	
	public Transit [] neighbors(int id, long timeDate)
	{
		System.out.println("time " + timeDate + " "+ timeDate/ 60);
		BusStation st = this.stations.get(id);
		Vector<BusStop> tmp=st.nextDeparture(timeDate);
		Vector<Transit> ttp = new Vector<WeightedGraph.Transit>();
		
		BusStop tmpstop = null;
		System.out.println("test" + tmp.size());
		for(int i = 0 ; i<tmp.size();i++)
		{
			tmpstop = tmp.get(i);
			int tmpid =  stations.indexOf(tmpstop.getStation());
			if(tmpstop.getNextStop()!=null)
			{
				Transit x = new Transit(stations.indexOf(tmpstop.getNextStop().getStation()),tmpstop.getNextStop().getTimeStamp()/*tmpstop.getTimeStamp()*/);
				ttp.add(x);
			}
				
		}
		HashMap<BusStation, Float> durs= footConnection.get(st);
		System.out.println("coucou" + durs + "  "+ footConnection.size());
		if(durs!= null)
			for(BusStation stF:durs.keySet())
			{
				//durs.get(duration)
				Transit x = new Transit(stations.indexOf(stF),(long)durs.get(stF).floatValue()+timeDate /*tmpstop.getTimeStamp()*/);
				System.out.println("FOOT TRANSITIONNNNNNNNNN from "+st.getName()+" to " + stF.getName()+ " "+ ((long)durs.get(stF).floatValue()+timeDate));
				
				ttp.add(x);
			}
		
		Transit [] res = new Transit[ttp.size()];
		for(int i=0;i<res.length;i++)
			res[i] = ttp.get(i);
		
//		System.out.println("nb suivant " + st.getName()+ " "+res.length);
		return res;
	}
}
