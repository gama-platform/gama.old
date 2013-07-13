package ummisco.miro.extension.transportation.dikjstra;

import java.util.HashMap;

import ummisco.miro.extension.transportation.dikjstra.WeightedGraph.Transit;
import ummisco.miro.extension.transportation.graph.BusStation;

public class Dikjstra {
		      public static TransportationResult dijkstra (WeightedGraph G, BusStation start , BusStation end, long timeStamp) {
		      final int s = G.getBusStationId(start);
		      final long [] dist = new long [G.size()];  // shortest known distance from "s"
		      final int [] pred = new int [G.size()];  // preceeding node in path
		      final boolean [] visited = new boolean [G.size()]; // all false initially
		      for (int i=0; i<dist.length; i++) {
		           dist[i] = Long.MAX_VALUE;
		           pred[i] = -1;
		        }
		      dist[s] = timeStamp;
		      for (int i=0; i<dist.length; i++) 
		      {
		         final int next = minVertex (dist, visited);
		         if(next == -1)
		        	 break;
		           System.out.println("sommet choisi "+ next);
		           visited[next] = true;
		           final Transit [] n = G.neighbors(next, dist[next]);
		           
		           for (int j=0; j<n.length; j++) {
		              final int v = n[j].stationID;
		              final long d = n[j].timeStamp;//dist[next] + G.getWeight(next,v);
		             System.out.print("Attention a ID; "+v);
		              if (dist[v] > d) {
		                 dist[v] = d;
		                 pred[v] = next;
		                 System.out.println("affectation "+ G.getStation(next).getName() + "->"+  G.getStation(v).getName() + " "+ dist[v]);
		              }
		           }
		        }
		        
		        BusStation [] res = new BusStation[pred.length];
		       // HashMap<BusStation, BusStation> predStation = new HashMap<BusStation, BusStation>();
		       
		        for( int i = 0; i<pred.length; i++)
		        {
		        	res[i]= G.getStation(pred[i]);
		        	if(res[i] != null)
		        		System.out.println(G.getStation(pred[i]).getName()+ " -> "+ G.getStation(i).getName());
		        	else
		        		System.out.println("Pas de prédécesseur");
		        		
		        }
		 /*       for( int i = 0; i<dist.length; i++)
		        {
		        	System.out.println(G.getStation(i).getName()+ " -> "+ dist[i]);
		        }
		   */
		        
		        System.out.println("out " + dist[G.getBusStationId(end)]);
		        
		        return new TransportationResult(dist[G.getBusStationId(end)], pred);  // (ignore pred[s]==0!)
		     }
		  
		     private static int minVertex (long [] dist, boolean [] v) {
		        long x = Long.MAX_VALUE;
		        int y = -1;   // graph not connected, or no unvisited vertices
		        for (int i=0; i<dist.length; i++) {
		           if (!v[i] && dist[i]<x ) 
		           {
		        	   y=i; 
		        	   x=dist[i];
		           }
		        }
		        return y;
		     }
}
