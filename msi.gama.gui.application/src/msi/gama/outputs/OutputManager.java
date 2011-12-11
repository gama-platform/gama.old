/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs;

import java.io.PrintWriter;
import java.util.*;
import msi.gama.gui.application.GUI;
import msi.gama.gui.graphics.*;
import msi.gama.gui.util.events.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.*;
import msi.gama.kernel.exceptions.*;
import msi.gama.kernel.experiment.IExperiment;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.with_sequence;
import msi.gama.precompiler.*;
import msi.gama.util.GamaList;

/**
 * The Class OutputManager.
 * 
 * 
 * 
 * 
 * FAIRE UN GUI-LESS OUTPUTMANAGER, qui n'ait aucune référence a Eclipse. Et une sous-classe qui
 * redéfinit les méthodes faisant appel au GUI
 * 
 * 
 * 
 * 
 * @author Alexis Drogoul modified by Romain Lavaud 05.07.2010
 */
@symbol(name = ISymbol.OUTPUT, kind = ISymbolKind.OUTPUT)
@inside(kinds = { ISymbolKind.MODEL, ISymbolKind.EXPERIMENT })
@with_sequence
public class OutputManager extends Symbol implements GamaSelectionProvider, GamaSelectionListener {

	private GuiOutputManager displays;
	private final Map<String, IOutput> outputs = new HashMap<String, IOutput>();
	private final Set<IOutput> outputsToUnschedule = new HashSet<IOutput>();
	private final Set<IOutput> scheduledOutputs = new HashSet<IOutput>();
	public static Thread OUTPUT_THREAD = new Thread(null, new Runnable() {

		@Override
		public void run() {
			boolean cond = false;
			while (cond) {
				try {
					GAMA.OUTPUT_AUTHORIZATION.acquire();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				IExperiment exp = GAMA.getExperiment();
				if ( exp != null && exp.isRunning() ) {
					try {
						GAMA.OUTPUT_FINISHED.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					try {
						exp.getOutputManager().updateOutputs();
					} finally {
						GAMA.OUTPUT_FINISHED.release();
					}
				}

			}
		}
	}, "Output thread");

	static {
		OUTPUT_THREAD.start();
	}

	public OutputManager(final IDescription desc) {
		super(desc);
	}

	public IOutput getOutput(final String id) {
		return outputs.get(id);
	}

	public void addOutput(final IOutput output) {
		if ( output == null || outputs.containsValue(output) ) { return; }
		outputs.put(output.getId(), output);
		if ( displays != null && output instanceof IDisplayOutput ) {
			displays.addDisplayOutput(output);
		}
	}

	public void buildOutputs(final IExperiment exp) {
		if ( exp.isGui() ) {
			// GUI.debug("Building the GUI output manager");
			if ( displays != null ) {
				// GUI.debug("Cancelling any previous selection");
				displays.fireSelectionChanged(null);
			}
			displays = new GuiOutputManager(this);
			for ( IOutput output : outputs.values() ) {
				if ( output instanceof IDisplayOutput ) {
					displays.addDisplayOutput(output);
				}
			}
			displays.buildOutputs(exp);

		}
		GUI.run(new Runnable() {

			@Override
			public void run() {
				for ( final IOutput output : outputs.values() ) {
					if ( !output.isPermanent() ) {
						try {
							// GUI.debug("Preparing output " + output.getName());
							output.prepare(exp.getCurrentSimulation());
						} catch (GamaRuntimeException e) {
							e.addContext("in preparing output " + output.getName());
							e.addContext("output " + output.getName() + " has not been opened");
							GAMA.reportError(e);
							continue;
						} catch (GamlException e) {
							e.printStackTrace();
						}
						try {
							// GUI.debug("Scheduling and opening output " + output.getName());
							output.schedule();
							output.open();
							output.update();
						} catch (GamaRuntimeException e) {
							e.addContext("in opening output " + output.getName());
							e.addContext("output " + output.getName() + " has not been opened");
							GAMA.reportError(e);
							continue;
						}

					}
				}
			}
		});
	}

	public synchronized void dispose(final boolean includingBatch) {
		// GUI.debug("Disposing the outputs");
		outputsToUpdateNow.clear();
		if ( displays != null ) {
			displays.dispose();
		}
		outputsToUnschedule.clear();
		scheduledOutputs.clear();
		for ( final IOutput output : outputs.values() ) {
			if ( includingBatch || !output.isPermanent() ) {
				// GUI.debug("Disposing of output " + output.getName());
				output.dispose();
			}
		}
		outputs.clear();
		// GUI.debug("Ouputs disposed");
	}

	public List<MonitorOutput> getMonitors() {
		List<MonitorOutput> result = new GamaList();
		for ( final IOutput out : outputs.values() ) {
			if ( out instanceof MonitorOutput ) {
				result.add((MonitorOutput) out);
			}
		}
		return result;
	}

	public void scheduleOutput(final IOutput output) {
		scheduledOutputs.add(output);
	}

	private final Set<IOutput> outputsToUpdateNow = new HashSet();

	public void updateOutputs() {
		try {
			for ( IOutput o : outputsToUpdateNow ) {
				try {
					try {
						o.update();
					} catch (Exception e) {
						throw new GamaRuntimeException(e);
					}
				} catch (GamaRuntimeException e) {
					e.addContext("in updating output " + o.getName());
					e.addContext("output " + o.getName() + " has not been updated ");
					GAMA.reportError(e);
					continue;
				}
			}
		} finally {
			outputsToUpdateNow.clear();
		}
	}

	public void unscheduleOutput(final IOutput o) {
		if ( scheduledOutputs.contains(o) ) {
			outputsToUnschedule.add(o);
		}
	}

	public void step(final IScope scope, final long cycle) {
		scheduledOutputs.removeAll(outputsToUnschedule);
		outputsToUnschedule.clear();
		for ( IOutput o : scheduledOutputs ) {
			if ( !o.isPaused() && !o.isClosed() ) {
				long ii = o.getNextTime();
				// GUI.debug("At cycle " + cycle + ", next update time for " + o.getName() + ": " +
				// ii);
				if ( cycle >= ii ) {
					try {
						try {
							// ? o.hasBeenComputed(false);
							o.compute(scope, cycle);
							outputsToUpdateNow.add(o);
							// ? o.hasBeenComputed(true);
						} catch (GamaRuntimeException e) {
							throw e;
						} catch (Exception e) {
							throw new GamaRuntimeException(e);
						}
					} catch (GamaRuntimeException e) {
						e.addContext("in computing output " + o.getName());
						e.addContext("output " + o.getName() + " has not been computed at cycle " +
							cycle);
						GAMA.reportError(e);
						continue;
					}
					o.setNextTime(cycle + o.getRefreshRate());
				}
			}
		}
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) throws GamlException {
		for ( ISymbol s : commands ) {
			if ( s instanceof IOutput ) {
				addOutput((IOutput) s);
			}
		}
	}

	public List<? extends ISymbol> getChildren() {
		return new GamaList(outputs.values());
	}

	@Override
	public void selectionChanged(final Object entity) {
		if ( displays != null ) {
			displays.selectionChanged(entity);
		}
	}

	@Override
	public void addGamaSelectionListener(final GamaSelectionListener listener) {
		if ( displays != null ) {
			displays.addGamaSelectionListener(listener);
		}

	}

	@Override
	public void removeGamaSelectionListener(final GamaSelectionListener listener) {
		if ( displays != null ) {
			displays.removeGamaSelectionListener(listener);
		}

	}

	@Override
	public void fireSelectionChanged(final Object entity) {
		if ( displays != null ) {
			displays.fireSelectionChanged(entity);
		}

	}

	public IDisplaySurface getDisplaySurfaceFor(final LayerDisplayOutput layerDisplayOutput,
		final double w, final double h) {
		if ( displays != null ) { return displays.getDisplaySurfaceFor(layerDisplayOutput, w, h); }
		return new ImageDisplaySurface(w, h);
	}

	public void exportOutputsOn(final PrintWriter pw) {
		pw.println("<output>");
		for ( final IOutput output : outputs.values() ) {
			if ( output.isUserCreated() ) {
				final String s = output.toGaml();
				if ( s != null ) {
					pw.println(s);
				}
			}
		}
		pw.println("</output>");
	}

	public GuiOutputManager getDisplayOutputManager() {
		return displays;
	}
}
