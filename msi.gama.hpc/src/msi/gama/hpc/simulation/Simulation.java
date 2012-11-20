package msi.gama.hpc.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import msi.gama.hpc.simulation.Result;

public class Simulation {

	/**
	 * Variable listeners
	 */

	public ArrayList<Result> result;

	/**
	 * current step
	 */
	private int step;

	/**
	 * id of current experiment
	 */
	private int experimentID;
	public int maxStep;

	public void addResult(Result p) {
		 this.result.add(p);
	}

	public Simulation(int expId) {
		this.experimentID = expId;
		result = new ArrayList<Result>();
		initialize();
	}

	public void setup() {
		this.step = 0;
		// this.model.setup();
	}

	public int getExperimentID() {
		return experimentID;
	}

	public void setExperimentID(int experimentID) {
		this.experimentID = experimentID;
	}

	public void initialize() {
		// results = new Vector<Result>();

	}

}
