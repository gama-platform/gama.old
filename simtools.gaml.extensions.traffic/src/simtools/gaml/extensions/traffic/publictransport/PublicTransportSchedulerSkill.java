package simtools.gaml.extensions.traffic.publictransport;

import java.util.ArrayList;
import java.util.List;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

@vars({
	@variable(
		name = PublicTransportSchedulerSkill.SCHEDULE,
		type = IType.MATRIX,
		doc = @doc ("?")
		/*
		 * I suppose it is a 2D matrix arbitrary formated :
		 * first column stop name,
		 * then second column first bus time schedule,
		 * until last column is the last bus time schedule of ... the day
		 */
	),
	@variable(
		name = PublicTransportSchedulerSkill.STOPS,
		type = IType.LIST,
		doc = @doc ("?") // I suppose it is the list of stops name
	),
	@variable(
		name = PublicTransportSchedulerSkill.NEXT_DEPARTURE,
		type = IType.INT,
		doc = @doc("?")
	),
	@variable(
		name = PublicTransportSchedulerSkill.NEXT_DEPARTURE_CYCLE,
		type = IType.INT,
		doc = @doc("?")
	),
	@variable(
		name = PublicTransportSchedulerSkill.TRANSPORT_LINE_NAME,
		type = IType.STRING,
		doc = @doc ("The name of the bus line")
	),
	@variable(
		name = "start_time_hour",
		type = IType.INT,
		init = "0",
		doc = @doc ("The name of the bus line")
	),
	@variable(
		name = "start_time_minute",
		type = IType.INT,
		init = "0",
		doc = @doc ("The name of the bus line")
	),
	@variable(
		name = "start_time_second",
		type = IType.INT,
		init = "0",
		doc = @doc ("The name of the bus line")
	),
})
@skill(
	name = PublicTransportSchedulerSkill.PUBLIC_TRANSPORT_SCHEDULER,
	concept = { IConcept.TRANSPORT, IConcept.SKILL },
	doc = @doc ("A skill that provides schedule management for public transports")
)
public class PublicTransportSchedulerSkill extends Skill {
	int startTimeHour;
	int startTimeMinute;
	int startTimeSecond;
	int midnightCycle;
	
	public final static String PUBLIC_TRANSPORT_SCHEDULER = "public_transport_scheduler";

	public final static String START_TIME_HOUR = "start_time_hour";
	public final static String START_TIME_MINUTE = "start_time_minute";
	public final static String START_TIME_SECOND = "start_time_second";

	public final static String DEFINE_SCHEDULE = "define_schedule";
	public final static String SCHEDULE = "schedule";
	public final static String STOPS = "stops";
	public final static String TRANSPORT_LINE_NAME = "transport_line";

	public final static String CHECK_NEXT_DEPARTURE = "check_next_departure";
	public final static String NEXT_DEPARTURE = "next_departure";
	public final static String NEXT_DEPARTURE_CYCLE = "next_departure_cycle";

	public final static String CHECK_DEPARTURE = "check_departure";

	public final static String PT_NA = "|";
	public final static String TS_SPLIT = ":";

	private void init(final IAgent agent) {
		startTimeHour = (int) agent.getAttribute(START_TIME_HOUR);
		startTimeMinute = (int) agent.getAttribute(START_TIME_MINUTE);
		startTimeSecond = (int) agent.getAttribute(START_TIME_SECOND);
		if(startTimeHour == 0 && startTimeMinute == 0 && startTimeSecond ==  0) {
			startTimeHour = agent.getScope().getClock().getStartingDate().getHour();
			startTimeMinute = agent.getScope().getClock().getStartingDate().getMinute();
			startTimeSecond = agent.getScope().getClock().getStartingDate().getSecond();
		}
		midnightCycle = ((23 - startTimeHour) * 3600) + ((59 - startTimeMinute) * 60) + (60 - startTimeSecond);
	}

	private int readTime (final IAgent agent, String timeString, int currentCycle) {
		String[] timeList = timeString.split(TS_SPLIT);

		int val = ((Integer.parseInt(timeList[0]) - startTimeHour) * 3600) + ((Integer.parseInt(timeList[1]) - startTimeMinute) * 60) - startTimeSecond;
		if(currentCycle == 0) {
			return val;
		}
		else {
			if(val < 0) {
				val = (Integer.parseInt(timeList[0]) * 3600) + (Integer.parseInt(timeList[1]) * 60);
				return (midnightCycle + val);
			}
			else {
				return val;
			}
		}
	}


	@action (
			name = DEFINE_SCHEDULE,
			args = { @arg (
						name = SCHEDULE,
						type = IType.MATRIX,
						optional = false,
						doc = @doc ("The stop(x)/time(y) matrix[x,y]")) },
			doc = @doc (
					value = "action to define the schedule of a bus_line",
					examples = { @example ("do define_schedule schedule: busline_schedule;") }))
	public void defineSchedule(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		init(agent);

		@SuppressWarnings("unchecked")
		final IMatrix<String> schedule = (IMatrix<String>) scope.getArg(SCHEDULE, IType.MATRIX);
		final IList<String> stops = schedule.getColumn(0);

		if(stops.size() > 1 && schedule.getRow(0).size() > 1) {
			agent.setAttribute(SCHEDULE, schedule);
			agent.setAttribute(STOPS, stops);
		}
		else {
			GamaRuntimeException.warning("Transport schedule missing information, abort", scope);
		}
	}


	@action (
			name = CHECK_NEXT_DEPARTURE,
			doc = @doc (
					value = "action to check next departure time",
					examples = { @example ("do check_next_departure;") }))
	public void checkNextDeparture(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		@SuppressWarnings("unchecked")
		final IMatrix<String> schedule = (IMatrix<String>) agent.getAttribute(SCHEDULE);
		@SuppressWarnings("unchecked")
		final IList<String> stops = (IList<String>) agent.getAttribute(STOPS);
		final int nextDeparture = (int) agent.getAttribute(NEXT_DEPARTURE);

		checkNextDeparture(scope, agent, schedule, stops, nextDeparture);
	}

	private void checkNextDeparture(final IScope scope, final IAgent agent, final IMatrix<String> schedule, final IList<String> stops, int nextDeparture) {
		if(schedule.get(scope, nextDeparture, 0) == null) {
			agent.dispose();
			return;
		}


		int nextStopIndex = 0;
		while(nextStopIndex < stops.size() && schedule.get(scope, nextDeparture, nextStopIndex).equals(PT_NA)) {
			nextStopIndex++;
		}

		if(nextStopIndex >= stops.size()) {
			nextDeparture++;
			checkNextDeparture(scope, agent, schedule, stops, nextDeparture);
		}
		else {
			final int currentCycle = scope.getClock().getCycle();
			int nextDepartureCycle = (int) agent.getAttribute(NEXT_DEPARTURE_CYCLE);
			nextDepartureCycle = readTime(agent, schedule.get(scope, nextDeparture, nextStopIndex), currentCycle);
			if(nextDepartureCycle < currentCycle) {
				nextDeparture++;
				checkNextDeparture(scope, agent, schedule, stops, nextDeparture);
			}
			else {
				agent.setAttribute(NEXT_DEPARTURE_CYCLE, nextDepartureCycle);
				agent.setAttribute(NEXT_DEPARTURE, nextDeparture);
			}
		}
	}

	@action (
			name = CHECK_DEPARTURE,
			doc = @doc (
					value = "action to check if a transport must depart",
					examples = { @example ("do check_departure;") }))
	public List<Integer> checkDeparture(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final int nextDepartureCycle = (int) agent.getAttribute(NEXT_DEPARTURE_CYCLE);
		@SuppressWarnings("unchecked")
		final IList<String> stops = (IList<String>) agent.getAttribute(STOPS);
		@SuppressWarnings("unchecked")
		final IMatrix<String> schedule = (IMatrix<String>) agent.getAttribute(SCHEDULE);
		int nextDeparture = (int) agent.getAttribute(NEXT_DEPARTURE);

		if(nextDepartureCycle == scope.getClock().getCycle()) {
			// Gather stop time for the bus to depart
			List<Integer> stopTimes = new ArrayList<>();

			for (int i = 0; i < stops.size(); i++) {
				String timeString = schedule.get(scope, nextDeparture, i);
				if(timeString.equals(PT_NA)) {
					stopTimes.add(-1);
				}
				else {
					stopTimes.add(readTime(agent, timeString, scope.getClock().getCycle()));
				}
			}

			nextDeparture++;
			checkNextDeparture(scope, agent, schedule, stops, nextDeparture);

			return stopTimes;
		}
		return null;
	}
}
