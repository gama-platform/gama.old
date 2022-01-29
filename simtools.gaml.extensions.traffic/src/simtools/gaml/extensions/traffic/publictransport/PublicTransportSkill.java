/*******************************************************************************************************
 *
 * PublicTransportSkill.java, in simtools.gaml.extensions.traffic, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package simtools.gaml.extensions.traffic.publictransport;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

/**
 * The Class PublicTransportSkill.
 */
@vars ({ @variable (
		name = PublicTransportSchedulerSkill.TRANSPORT_LINE_NAME,
		type = IType.STRING,
		doc = @doc ("The name of the bus line")),
		@variable (
				name = PublicTransportSkill.STOPS,
				type = IType.LIST,
				doc = @doc ("The list of stops the bus have and will going through")),
		@variable (
				name = PublicTransportSkill.IS_STOPPED,
				type = IType.BOOL,
				doc = @doc ("Is the transport waiting for passengers")),
		@variable (
				name = PublicTransportSkill.NEXT_STOP,
				type = IType.AGENT,
				doc = @doc ("the next stop for the transport")),
		@variable (
				name = PublicTransportSkill.TRANSPORT_STATE,
				type = IType.STRING,
				doc = @doc ("?") // Can be 0 to 4
		), })
@skill (
		name = PublicTransportSkill.PUBLIC_TRANSPORT,
		concept = { IConcept.TRANSPORT, IConcept.SKILL },
		doc = @doc ("A skill that provides agent with the ability to pick and drop agents"))
public class PublicTransportSkill extends Skill {

	/** The Constant PUBLIC_TRANSPORT. */
	public final static String PUBLIC_TRANSPORT = "public_transport";

	/** The Constant DEFINE_ROUTE. */
	public final static String DEFINE_ROUTE = "define_route";

	/** The Constant STOPS. */
	public final static String STOPS = "stops";

	/** The Constant SCHEDULE. */
	public final static String SCHEDULE = "schedule";

	/** The Constant DEFINE_NORIA. */
	public final static String DEFINE_NORIA = "define_noria";

	/** The Constant PICKUP_POINT. */
	public final static String PICKUP_POINT = "pickup_point";

	/** The Constant EVACUATION_POINT. */
	public final static String EVACUATION_POINT = "evacuation_point";

	/** The Constant RETURN_POINT. */
	public final static String RETURN_POINT = "return_point";

	/** The Constant RETURN_TIME. */
	public final static String RETURN_TIME = "return_time";

	/** The Constant WAITING_TIME. */
	public final static String WAITING_TIME = "waiting_time";

	/** The Constant NEXT_STOP_INDEX. */
	public final static String NEXT_STOP_INDEX = "next_stop_index";

	/** The Constant NEXT_STOP_TIME. */
	public final static String NEXT_STOP_TIME = "next_stop_time";

	/** The Constant DEPARTURE_TIME. */
	public final static String DEPARTURE_TIME = "departure_time";

	/** The Constant INIT_DEPARTURE. */
	public final static String INIT_DEPARTURE = "init_departure";

	/** The Constant NEXT_STOP. */
	public final static String NEXT_STOP = "next_stop";

	/** The Constant IS_TIME_TO_GO. */
	public final static String IS_TIME_TO_GO = "is_time_to_go";

	/** The Constant DEFINE_NEXT_TARGET. */
	public final static String DEFINE_NEXT_TARGET = "define_next_target";

	/** The Constant IS_STOPPED. */
	public final static String IS_STOPPED = "is_stopped";

	/** The Constant TRANSPORT_STATE. */
	public final static String TRANSPORT_STATE = "transport_state";

	/** The Constant DEFAULT_STATE. */
	public final static int DEFAULT_STATE = 0;

	/** The Constant GO_TO_PICKUP_STATE. */
	public final static int GO_TO_PICKUP_STATE = 1;

	/** The Constant WAITING_STATE. */
	public final static int WAITING_STATE = 2;

	/** The Constant GO_TO_EVACUATION_STATE. */
	public final static int GO_TO_EVACUATION_STATE = 3;

	/** The Constant RETURN_STATE. */
	public final static int RETURN_STATE = 4;

	/**
	 * Define route.
	 *
	 * @param scope
	 *            the scope
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = DEFINE_ROUTE,
			args = { @arg (
					name = STOPS,
					type = IType.LIST,
					optional = false,
					doc = @doc ("The stops' list to go by")),
					@arg (
							name = SCHEDULE,
							type = IType.LIST,
							optional = false,
							doc = @doc ("The times' list for each stop")) },
			doc = @doc (
					value = "action to define the route of a bus",
					examples = { @example ("do define_route stops: bus_stops schedule: bus_schedule;") }))
	public boolean defineRoute(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);

		@SuppressWarnings ("unchecked") final IList<IAgent> stops = (IList<IAgent>) scope.getArg(STOPS, IType.LIST);

		if (stops.size() > 1) {
			agent.setAttribute(STOPS, stops);
			@SuppressWarnings ("unchecked") final IList<Integer> schedule =
					(IList<Integer>) scope.getArg(SCHEDULE, IType.LIST);
			agent.setAttribute(SCHEDULE, schedule);
			agent.setAttribute(TRANSPORT_STATE, DEFAULT_STATE);
			return true;
		}
		GamaRuntimeException.warning("Transport line have 1 or less stop, abort", scope);
		return false;
	}

	/**
	 * Define noria.
	 *
	 * @param scope
	 *            the scope
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = DEFINE_NORIA,
			args = { @arg (
					name = PICKUP_POINT,
					type = IType.AGENT,
					optional = false,
					doc = @doc ("The pickup point where passengers are taken")),
					@arg (
							name = EVACUATION_POINT,
							type = IType.AGENT,
							optional = false,
							doc = @doc ("The evacuation exit")),
					@arg (
							name = RETURN_POINT,
							type = IType.AGENT,
							optional = false,
							doc = @doc ("The bus re-entry on the graph")),
					@arg (
							name = WAITING_TIME,
							type = IType.INT,
							optional = false,
							doc = @doc ("waiting time at pickup point in second (can be ignored if transport is full)")),
					@arg (
							name = RETURN_TIME,
							type = IType.INT,
							optional = true,
							doc = @doc ("time before the re-entry on the graph in second")) },
			doc = @doc (
					value = "action to define a bus noria",
					examples = {
							@example ("do define_noria pickup_point: bus_pickup evacuation_point: exit_point return_point: exit_point waiting_time: 300 return_time: 600;") }))
	public boolean defineNoria(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);

		final IAgent pickupPoint = (IAgent) scope.getArg(PICKUP_POINT, IType.AGENT);
		final IAgent evacuationPoint = (IAgent) scope.getArg(EVACUATION_POINT, IType.AGENT);
		final IAgent returnPoint = (IAgent) scope.getArg(RETURN_POINT, IType.AGENT);
		final int waitingTime = (int) scope.getArg(WAITING_TIME, IType.INT);
		final int returnTime = (int) scope.getArg(RETURN_TIME, IType.INT);

		agent.setAttribute(PICKUP_POINT, pickupPoint);
		agent.setAttribute(EVACUATION_POINT, evacuationPoint);
		agent.setAttribute(RETURN_POINT, returnPoint);
		agent.setAttribute(WAITING_TIME, waitingTime);
		agent.setAttribute(RETURN_TIME, returnTime);

		agent.setAttribute(TRANSPORT_STATE, GO_TO_PICKUP_STATE);
		return true;
	}

	/**
	 * Inits the departure.
	 *
	 * @param scope
	 *            the scope
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = INIT_DEPARTURE,
			doc = @doc (
					value = "initialise the vehicle",
					examples = { @example ("do init_departure;") }))
	public boolean initDeparture(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		int state = (int) agent.getAttribute(TRANSPORT_STATE);

		if (state == DEFAULT_STATE) return initDepartureRoute(agent);
		return initDepartureNoria(agent);
	}

	/**
	 * Inits the departure route.
	 *
	 * @param agent
	 *            the agent
	 */
	public boolean initDepartureRoute(final IAgent agent) {
		@SuppressWarnings ("unchecked") final IList<IAgent> stops = (IList<IAgent>) agent.getAttribute(STOPS);
		@SuppressWarnings ("unchecked") final IList<Integer> schedule = (IList<Integer>) agent.getAttribute(SCHEDULE);

		int start_index = 0;
		while (schedule.size() > start_index && schedule.get(start_index) == -1) { start_index++; }

		int next_stop_index = start_index;
		do { next_stop_index++; } while (schedule.size() > next_stop_index && schedule.get(next_stop_index) == -1);

		if (schedule.size() <= next_stop_index) {
			agent.setAttribute(NEXT_STOP, null);
			return false;
		}
		agent.setLocation(stops.get(start_index).getLocation());
		agent.setAttribute(NEXT_STOP_INDEX, next_stop_index);
		agent.setAttribute(NEXT_STOP, stops.get(next_stop_index));
		agent.setAttribute(DEPARTURE_TIME, schedule.get(next_stop_index));
		agent.setAttribute(IS_STOPPED, false);
		return true;
	}

	/**
	 * Inits the departure noria.
	 *
	 * @param agent
	 *            the agent
	 */
	public boolean initDepartureNoria(final IAgent agent) {
		IAgent returnPoint = (IAgent) agent.getAttribute(RETURN_POINT);
		IAgent pickupPoint = (IAgent) agent.getAttribute(PICKUP_POINT);

		agent.setLocation(returnPoint.getLocation());
		agent.setAttribute(NEXT_STOP, pickupPoint);
		agent.setAttribute(IS_STOPPED, false);
		return true;
	}

	/**
	 * Checks if is time to go.
	 *
	 * @param scope
	 *            the scope
	 * @return true, if is time to go
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = IS_TIME_TO_GO,
			doc = @doc (
					value = "test the departure time",
					returns = "returns true if it's time to go, false otherwise",
					examples = { @example ("if(is_time_to_go())...") }))
	public boolean isTimeToGo(final IScope scope) throws GamaRuntimeException {
		int current_cycle = scope.getClock().getCycle();
		final IAgent agent = getCurrentAgent(scope);
		final int departure_time = (int) agent.getAttribute(DEPARTURE_TIME);

		if (current_cycle >= departure_time) {
			agent.setAttribute(IS_STOPPED, false);
			return true;
		}
		return false;
	}

	/**
	 * Define next target.
	 *
	 * @param scope
	 *            the scope
	 * @return true, if successful
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = DEFINE_NEXT_TARGET,
			doc = @doc (
					value = "set up next target",
					examples = { @example ("do define_next_target;") }))
	public boolean defineNextTarget(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		int state = (int) agent.getAttribute(TRANSPORT_STATE);

		if (state == DEFAULT_STATE) {
			defineNextTargetRoute(agent);
		} else {
			defineNextTargetNoria(agent, state, scope.getClock().getCycle());
		}

		agent.setAttribute(IS_STOPPED, true);
		return true;
	}

	/**
	 * PLZ Provide some doc !!!
	 *
	 * @param agent
	 */
	public void defineNextTargetRoute(final IAgent agent) {
		@SuppressWarnings ("unchecked") final IList<IAgent> stops = (IList<IAgent>) agent.getAttribute(STOPS);
		@SuppressWarnings ("unchecked") final IList<Integer> schedule = (IList<Integer>) agent.getAttribute(SCHEDULE);
		int next_stop_index = (int) agent.getAttribute(NEXT_STOP_INDEX);

		do { next_stop_index++; } while (schedule.size() > next_stop_index && schedule.get(next_stop_index) == -1);

		agent.setAttribute(NEXT_STOP_INDEX, next_stop_index);

		if (schedule.size() <= next_stop_index) {
			agent.setAttribute(NEXT_STOP, null);
		} else {
			agent.setAttribute(NEXT_STOP, stops.get(next_stop_index));
			agent.setAttribute(DEPARTURE_TIME, schedule.get(next_stop_index));
		}
	}

	/**
	 * PLZ provides some doc !!!
	 *
	 * @param agent
	 * @param state
	 * @param cycle
	 */
	public void defineNextTargetNoria(final IAgent agent, final int state, final int cycle) {
		int departure_time = cycle;
		if (state == GO_TO_PICKUP_STATE) {
			agent.setAttribute(TRANSPORT_STATE, GO_TO_EVACUATION_STATE);
			agent.setAttribute(NEXT_STOP, agent.getAttribute(EVACUATION_POINT));
			departure_time = cycle + (int) agent.getAttribute(WAITING_TIME);
			agent.setAttribute(DEPARTURE_TIME, departure_time);
		} else if (state == GO_TO_EVACUATION_STATE) {
			agent.setAttribute(TRANSPORT_STATE, GO_TO_PICKUP_STATE);
			agent.setAttribute(NEXT_STOP, agent.getAttribute(PICKUP_POINT));
			departure_time = cycle + (int) agent.getAttribute(RETURN_TIME);
			agent.setAttribute(DEPARTURE_TIME, departure_time);
			agent.setLocation(((IAgent) agent.getAttribute(RETURN_POINT)).getLocation());
		} else {
			System.err.println("Error while defining target : state unknown");
		}
	}

	/**
	 * Checks if is stopped.
	 *
	 * @param agent
	 *            the agent
	 * @return true, if is stopped
	 */
	@getter (IS_STOPPED)
	public boolean isStopped(final IAgent agent) {
		return (boolean) agent.getAttribute(IS_STOPPED);
	}

	/**
	 * Gets the next stop.
	 *
	 * @param agent
	 *            the agent
	 * @return the next stop
	 */
	@getter (NEXT_STOP)
	public IShape getNextStop(final IAgent agent) {
		return (IShape) agent.getAttribute(NEXT_STOP);
	}
}
