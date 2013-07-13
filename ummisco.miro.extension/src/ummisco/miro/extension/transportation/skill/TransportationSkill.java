package ummisco.miro.extension.transportation.skill;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import ummisco.miro.extension.transportation.dikjstra.Dikjstra;
import ummisco.miro.extension.transportation.dikjstra.TransportationResult;
import ummisco.miro.extension.transportation.dikjstra.WeightedGraph;
import ummisco.miro.extension.transportation.graph.BusLine;
import ummisco.miro.extension.transportation.graph.BusStation;
import ummisco.miro.extension.transportation.graph.BusStop;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ILocated;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaPair;
import msi.gama.util.IList;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.path.GamaPath;
import msi.gama.util.path.IPath;
import msi.gaml.operators.Cast;
import msi.gaml.skills.Skill;
import msi.gaml.species.ISpecies;
import msi.gaml.types.GamaAgentType;
import msi.gaml.types.IType;


@vars({ @var(name = "filePath", type = IType.STRING, init = ""),
	@var(name = "isTemporalGraph", type = IType.BOOL, init = "true"),
	@var(name = "individualGraph", type= IType.GRAPH),
	@var(name = "stationID", type = IType.STRING, doc = @doc("DB identificator of the station"))
})
@skill(name = "busTransportation")
public class TransportationSkill extends Skill {
	
	final static String FILE_PATH = "filePath";
	final static String IS_TEMPORAL_GRAPH="isTemporalGraph";
	final static double FOOT_SPEED = 5000 / 3600;
	final static double FOOT_EXCHANGE = 5 * 60;
	final static int OD_MATRIX = 1;
	final static int BUS_GRAPH = 2;
	
	
	private HashMap<String,BusLine> buslines;
	private HashMap<String, BusStation> busStations;
	private Vector<BusStation> stations;
	private WeightedGraph graph;
	private HashMap<BusStation ,HashMap<BusStation, GamaPath>> savedPath;
	private HashMap<BusStation, IAgent> stationAgents;
	private HashMap<String, HashMap<String, Double>> ODMatrix;
	private int dataType;
	//private HashMap<IAgent,BusStation> agentsStation;
	private String dbFileLocation;
	
	
	public TransportationSkill() {
		super();
		stationAgents = new HashMap<BusStation, IAgent>();
		//agentsStation = new HashMap< IAgent,BusStation>();
		savedPath = new HashMap<BusStation, HashMap<BusStation,GamaPath>>();
		this.ODMatrix = new HashMap<String, HashMap<String,Double>>();
		this.busStations = new HashMap<String,BusStation>();
		this.stations = new Vector<BusStation>();
		
		// TODO Auto-generated constructor stub
	}

	
	@getter(FILE_PATH)
	public String getSourceFilePath(final IAgent agent) {
		return (String) agent.getAttribute(FILE_PATH);
	}

	@setter(FILE_PATH)
	public void setSourceFilePath(final IAgent agent, final String fileP) {
		agent.setAttribute(FILE_PATH, fileP);
	}
 
	@getter(IS_TEMPORAL_GRAPH)
	public boolean isTemporalGraph(final IAgent agent) {
		return (Boolean) agent.getAttribute(IS_TEMPORAL_GRAPH);
	}

	@setter(IS_TEMPORAL_GRAPH)
	public void setTemporalGraph(final IAgent agent, final boolean v) {
		agent.setAttribute(FILE_PATH, v);
	}
	
	@action(name = "loadFile", args = {
			@arg(name = "source", type = IType.STRING, optional = false, doc = @doc("Path of the source file")),
			@arg(name = "datatype", type = IType.STRING, optional = true, doc = @doc("determine file datatype: OD -> it is an Origin Destination Matrix; busline -> official timetable of the transportation service"))},
			doc = @doc(value = "moves the agent towards the target passed in the arguments.", returns = "the path followed by the agent.", examples = { "do action: goto{\n arg target value: one_of (list (species (self))); \n arg speed value: speed * 2; \n arg on value: road_network;}" }))
	public void loadFile(final IScope scope) throws GamaRuntimeException {
			final IAgent agent = getCurrentAgent(scope);
			ILocation source = agent.getLocation().copy(scope);
			String fileLocation = (String) scope.getArg("source", IType.STRING);
			String datatype = (String) scope.getArg("datatype", IType.STRING);
			
		//	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXX" + datatype);
			if(this.dbFileLocation==null|| !this.dbFileLocation.equals(fileLocation))
			{
				if(datatype.equals("OD"))
				{
					this.dbFileLocation = fileLocation;
					this.dataType = OD_MATRIX;
					loadBusMatrixDb(scope, fileLocation);
				}
				else
					{
						this.dbFileLocation = fileLocation;
						this.dataType = BUS_GRAPH;
						loadBusDb( scope,fileLocation);
					}
			}
			registerStation( getCurrentAgent(scope));
			
			
	}
	
	@action(name="loadVehicleGraph", args = {
		@arg(name = "source", type = IType.GRAPH)	
	})
	public void loadVehicleGraph(final IScope scope)
	{
		final IAgent agent = getCurrentAgent(scope);
		agent.setAttribute("individualGraph", scope.getArg("source", IType.GRAPH)) ;
	}
	
	private void registerStation(final IAgent agt)
	{
		String stationID = ((String)((IAgent)agt).getAttribute("stationID"));
		if(!this.busStations.containsKey(stationID))
		{
			BusStation bst = new BusStation(stationID);
			busStations.put(stationID, bst);
			stations.add(bst);
			this.stationAgents.put(bst,agt);
		}
		BusStation bst = this.busStations.get(stationID);
		if(!this.stationAgents.containsKey(bst))
		{
			this.stationAgents.put(bst,agt);
		}
		if(this.dataType!=OD_MATRIX)
		{
			loadFootConnection(agt,bst);
		}
	}
	
	private void loadBusMatrixDb(final IScope scope, final String fileLocation)
	{
		String DBRelativeLocation =
				scope.getSimulationScope().getModel().getRelativeFilePath(fileLocation, true);
		File inputFile = new File(DBRelativeLocation);
		
		try {
			loadMatrix(inputFile);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("bus loading db completed");
		
	}
	
	private void loadBusDb(final IScope scope, final String fileLocation)
	{
		String DBRelativeLocation =
				scope.getSimulationScope().getModel().getRelativeFilePath(fileLocation, true);
		File inputFile = new File(DBRelativeLocation);
		try {
			loadTemporalGraphFile(inputFile);
			
			graph = new WeightedGraph(stations);
			System.out.println("bus loading db completed");
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
			throw new GamaRuntimeException(new Exception("Bus db loading error"));
		}
	}
	
	private void loadMatrix(File in) throws NumberFormatException, IOException
	{
		BufferedReader stream = new BufferedReader(new FileReader(in));
		
		while(stream.ready())
		{
			String data = stream.readLine();
			String[] spl= data.split(";");
			String OriStation = spl[0];
			String DestStation = spl[1];
			Double delay = new Double(spl[2]);
			
			if(this.ODMatrix.containsKey(OriStation) == false)
			{
				System.out.println("O:" + OriStation+ " D:"+ DestStation+" delay:"+delay);
				this.ODMatrix.put(OriStation, new HashMap<String, Double>());
			}
			HashMap<String, Double> dest = this.ODMatrix.get(OriStation);
			dest.put(DestStation, delay);
			System.out.println("O:" + OriStation+ " D:"+ DestStation+" delay:"+delay);
			
		}
		System.out.println("bus loading db completed");

	}

	
	
	@action(name = "travel_arrival", args = {
			@arg(name = "from", type = IType.STRING, optional = false, doc = @doc("departure station ID")),
			@arg(name = "to", type = IType.STRING, optional = false, doc = @doc("arrival Station ID")),
			@arg(name = "on", type = { IType.LIST, IType.AGENT, IType.GRAPH, IType.GEOMETRY }, optional = true, doc = @doc("list, agent, graph, geometry that restrains this move (the agent moves inside this geometry)")),
			@arg(name = "departureDate", type = IType.INT, optional = false, doc = @doc("date of the departure"))},
			doc = @doc(value = "moves the agent towards the target passed in the arguments.", returns = "the path followed by the agent.", examples = { "do action: goto{\n arg target value: one_of (list (species (self))); \n arg speed value: speed * 2; \n arg on value: road_network;}" }))
	public GamaMap computTravel(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		ILocation source = agent.getLocation().copy(scope);
		String departureID = (String) scope.getArg("from", IType.STRING);
		String arrivalID = (String) scope.getArg("to", IType.STRING);
		long departure_date = ((Integer) scope.getArg("departureDate", IType.INT)).longValue();
		if( this.dataType==BUS_GRAPH)
			return computeTravelTimeTable(scope, agent, source,departureID,arrivalID ,departure_date) ;
		else
			return computeTravelOD(scope, agent, source,departureID,arrivalID ,departure_date) ;
		}

	
	private GamaMap computeTravelTimeTable(final IScope scope, IAgent agent, ILocation source,String departureID,String arrivalID ,long departure_date) throws GamaRuntimeException {
		if(!this.busStations.containsKey(departureID)||!this.busStations.containsKey(arrivalID))
		{
			GamaMap<String, Object> result = new GamaMap<String, Object>();
			result.put("duration", new Integer(-1));
			return result; // new Integer(-1);
		}
		TransportationResult tmp = Dikjstra.dijkstra(graph,this.busStations.get(departureID),busStations.get(arrivalID) ,departure_date);
		//Long  tmp = new Long((long)Dikjstra.dijkstra(graph, this.busStations.get(departureID),busStations.get(arrivalID) ,departure_date).duration);
		if(tmp.duration== Long.MAX_VALUE)
		{
			System.out.println("erreur => non trouv�...");
			GamaMap<String, Object> result = new GamaMap<String, Object>();
			result.put("duration", new Integer(-2));
			return result; // new Integer(-1);
		}
		
		GamaList<IAgent> edges = computeTransportOnTraffic(scope,tmp, departureID, arrivalID );
				
		GamaMap<String, Object> result = new GamaMap<String, Object>();
		result.put("duration", new Integer((int)tmp.duration));
		result.put("edges", edges);
		return result;
	}
	
	
	
	private GamaMap computeTravelOD(final IScope scope, IAgent agent, ILocation source,String departureID,String arrivalID ,long departure_date) throws GamaRuntimeException 
	{
		
		HashMap<String, Double> mat = this.ODMatrix.get(departureID);
		if(mat == null)
		{
			System.out.println("error departure" + departureID );
			GamaMap<String, Object> result = new GamaMap<String, Object>();
			result.put("duration", new Integer(-2));
			return result;
		}
		GamaList<IAgent> res = new GamaList<IAgent>();
		
		Double duration = mat.get(arrivalID);
		
		if(duration == null)
		{
			System.out.println("error arrival" + arrivalID + " (departure:"+departureID+")");
			GamaMap<String, Object> result = new GamaMap<String, Object>();
			result.put("duration", new Integer(-2));
			return result;
		}
		BusStation dep = busStations.get(departureID);
		BusStation arr = busStations.get(arrivalID);
			
		GamaPath tmpPath = null;
		if( this.stationAgents.containsKey(dep) && this.stationAgents.containsKey(arr))
				tmpPath= computePath(scope, this.stationAgents.get(dep),this.stationAgents.get(arr));
			else
				tmpPath= null;
		
		if(tmpPath != null)
		{
			IList<IShape> edges = tmpPath.getEdgeGeometry();
			for(IShape edge:edges)
			{
				IAgent lineAg = (IAgent)tmpPath.getRealObject(edge);
				res.add(lineAg);
			}
			
		}
		GamaMap<String, Object> result = new GamaMap<String, Object>();
		result.put("duration", new Long((int)duration.doubleValue() + departure_date));
		result.put("edges", res);
		return result;

	}
		
	protected GamaList<IAgent> computeTransportOnTraffic(IScope scope, TransportationResult transportationPath,  String  departureID, String arrivalID)
	{
		int idEnd = graph.getBusStationId(busStations.get(arrivalID));
		int idDeparture = graph.getBusStationId(busStations.get(departureID));
		int currentStation = idEnd;
		GamaList<IAgent> res = new GamaList<IAgent>();
		while(currentStation != -1 && currentStation != idDeparture)
		{
			BusStation currentB =  graph.getStation(currentStation);
			BusStation previousB =  null;
			int currentStationBB = currentStation;
			do
			{
				previousB = graph.getStation(transportationPath.pred[currentStationBB]);
				if(this.stationAgents.get(previousB) == null)
				{
					currentStationBB = transportationPath.pred[currentStationBB];
				}
			} while(currentStationBB >0 && this.stationAgents.get(previousB) == null);
			HashMap<BusStation, GamaPath> nexts = null;
			if(!savedPath.containsKey(previousB))
				nexts = savedPath.put(previousB, new HashMap<BusStation, GamaPath>());
			
			nexts = savedPath.get(previousB);
			GamaPath tmpPath = null;
			if(nexts.containsKey(currentB))
			{
				 tmpPath = nexts.get(currentB);
			}
			else
			{
				if( this.stationAgents.get(previousB) != null && this.stationAgents.get(currentB) != null)
					tmpPath= computePath(scope, this.stationAgents.get(previousB),this.stationAgents.get(currentB));
				else
					tmpPath= null;
			}
			if(tmpPath != null)
			{
				IList<IShape> edges = tmpPath.getEdgeGeometry();
				for(IShape edge:edges)
				{
					IAgent lineAg = (IAgent)tmpPath.getRealObject(edge);
					res.add(lineAg);
				}
				
			}
			currentStation = transportationPath.pred[currentStation];
				
		}
		return res;
	}
	
	
	protected ILocation computeTarget(final IScope scope, final IAgent agent)
			throws GamaRuntimeException {
			final Object target = scope.getArg("target", IType.NONE);
			ILocation result = null;
			if ( target != null && target instanceof ILocated ) {
				result = ((ILocated) target).getLocation();
			}
			if ( result == null ) {
				//scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			return result;
		}

		protected ITopology computeTopology(final IScope scope)
			throws GamaRuntimeException {
			final Object on = scope.getArg("on", IType.NONE);
			ITopology topo = Cast.asTopology(scope, on);
			if ( topo == null ) { return scope.getTopology(); }
			return topo;
		}
		
		protected GamaPath computePath(IScope scope,IAgent from, IAgent to)
		{
			final ILocation source =((ILocated) from).getLocation();
			if ( source == null ) {
				//scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			final ILocation goal =((ILocated) to).getLocation();
			if ( goal == null ) {
				//scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			final ITopology topo = computeTopology(scope);
			if ( topo == null ) {
				//scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			GamaPath path = topo.pathBetween(scope, source, goal);
			return path;
		}
	
//	final ILocation goal = computeTarget(scope, agent);
//	if ( goal == null ) {
//		scope.setStatus(ExecutionStatus.failure);
//		return null;
//	}
//	final ITopology topo = computeTopology(scope, agent);
//	if ( topo == null ) {
//		scope.setStatus(ExecutionStatus.failure);
//		return null;
//	}
//	IPath path = (GamaPath) agent.getAttribute("current_path");
//	if ( path == null || !path.getTopology().equals(topo) ||
//		!path.getEndVertex().equals(goal) || !path.getStartVertex().equals(source) ) {
//		path = topo.pathBetween(scope, source, goal);
//	} else {

	//private Integer computeTheoricalBusTravel
	
	private void loadTemporalGraphFile(File in) throws IOException
	{
		BufferedReader stream = new BufferedReader(new FileReader(in));
		this.readBusLines(stream.readLine());
		this.readBusStations(stream.readLine());
		this.readBusStops(stream);
	}
	
	private void loadFootConnection(IAgent current, BusStation bst)
	{
		System.out.println("coucou loadFootConnection" + stationAgents.size());

		for(BusStation sta:stationAgents.keySet())
		{
			IAgent dest = stationAgents.get(sta);
			double distance = dest.getLocation().euclidianDistanceTo(current.getLocation());
			if(distance <= FOOT_EXCHANGE * FOOT_SPEED)
			{
//				BusStation ori = this.busStations.current,
				graph.addFootConnection(bst, sta, distance * FOOT_SPEED);
			}
		}
	}
	
	private void readBusLines(String fileLine)
	{
		this.buslines = new HashMap<String,BusLine>();
		String[] lns = fileLine.split("\t");
		for(int i = 0 ; i<lns.length; i++)
		{
			buslines.put(lns[i].trim(),new BusLine(lns[i].trim()));
		}
	}
	
	private void readBusStations(String fileLine)
	{
		this.busStations = new HashMap<String,BusStation>();
		this.stations = new Vector<BusStation>();
		String[] lns = fileLine.split("\t");
		for(int i = 0 ; i<lns.length; i++)
		 {
			BusStation st = new BusStation(lns[i].trim());
			this.busStations.put(lns[i].trim(),st);
			this.stations.add(st);
		 } 
	}
	
	private void readBusStops(BufferedReader in) throws IOException
	{
		while(in.ready())
		{
			readCurrentBusStops(in.readLine());
		}
	}

	private void readCurrentBusStops(String in)
	{
		String[] data = in.split("\t");
		//0 2	01	MEL	333  1
		String lineID = data[2];
		String serviceID = data[1];
		String stationID = data[3];
		long date = Long.valueOf(data[4]).longValue();
		buslines.get(lineID).addStop(serviceID, this.busStations.get(stationID), date);
		
	}
}
