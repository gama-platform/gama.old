/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs;

import java.io.PrintWriter;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.experiment.IExperimentSpecies;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;

/**
 * The Class OutputManager.
 * 
 * @author Alexis Drogoul modified by Romain Lavaud 05.07.2010
 */
@symbol(name = IKeyword.OUTPUT, kind = ISymbolKind.OUTPUT, with_sequence = true)
@inside(kinds = { ISymbolKind.MODEL, ISymbolKind.EXPERIMENT })
public class OutputManager extends Symbol implements IOutputManager {

	private GuiOutputManager displays;
	private final Map<String, IOutput> outputs = new HashMap<String, IOutput>();
	private final Set<IOutput> outputsToUnschedule = new HashSet<IOutput>();
	private final Set<IOutput> scheduledOutputs = new HashSet<IOutput>();
	private final Set<IOutput> outputsToUpdateNow = new HashSet();

	public OutputManager(final IDescription desc) {
		super(desc);
	}

	@Override
	public IOutput getOutput(final String id) {
		return outputs.get(id);
	}

	public IOutput getOutputWithName(final String name) {
		for ( final IOutput output : outputs.values() ) {
			if ( output.getName().equals(name) ) { return output; }
		}
		return null;
	}

	@Override
	public void addOutput(final IOutput output) {
		if ( output == null || outputs.containsValue(output) ) { return; }
		outputs.put(output.getId(), output);
		if ( displays != null && output instanceof IDisplayOutput ) {
			displays.addDisplayOutput(output);
		}
	}

	public void buildOutputs(final IExperimentSpecies exp) {
		if ( displays != null ) {
			displays.fireSelectionChanged(null);
		}
		if ( exp.isGui() ) {
			displays = new GuiOutputManager(this);
		} else if ( GuiUtils.isInHeadLessMode() ) {
			displays = new HeadlessOutputManager(this);
		}

		for ( final IOutput output : outputs.values() ) {
			if ( output instanceof IDisplayOutput ) {
				displays.addDisplayOutput(output);
			}
		}
		displays.buildOutputs(exp);

	}

	@Override
	public void init(final IScope scope) {
		GuiUtils.run(new Runnable() {

			@Override
			public void run() {
				for ( final IOutput output : outputs.values() ) {
					if ( !output.isPermanent() ) {
						try {
							// GuiUtils.debug("Preparing output " + output.getName());
							if ( !scope.init(output) ) { return; }
						} catch (final GamaRuntimeException e) {
							e.addContext("in preparing output " + output.getName());
							e.addContext("output " + output.getName() + " has not been opened");
							GAMA.reportError(e);
							continue;
						} catch (final Exception e) {
							e.printStackTrace();
						}
						try {
							// GuiUtils.debug("Scheduling and opening output " + output.getName());
							output.schedule();
							output.open();
							outputsToUpdateNow.add(output);
						} catch (final GamaRuntimeException e) {
							e.addContext("in opening output " + output.getName());
							e.addContext("output " + output.getName() + " has not been opened");
							GAMA.reportError(e);
							continue;
						} catch (final Exception e) {
							e.printStackTrace();
						}

					}
				}

			}
		});

		OutputSynchronizer.waitForViewsToBeInitialized();
		GuiUtils.informStatus("Experiment ready");
		scope.step(this);

	}

	public synchronized void dispose(final boolean includingBatch) {
		try {
			// GuiUtils.debug("Disposing the outputs");
			outputsToUpdateNow.clear();
			if ( displays != null ) {
				displays.dispose();
			}
			outputsToUnschedule.clear();
			scheduledOutputs.clear();
			for ( final IOutput output : outputs.values() ) {
				if ( includingBatch || !output.isPermanent() ) {
					// GuiUtils.debug("Disposing of output " + output.getName());
					output.dispose();
				}
			}
			outputs.clear();
			// GuiUtils.debug("Ouputs disposed");
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<MonitorOutput> getMonitors() {
		final List<MonitorOutput> result = new GamaList();
		for ( final IOutput out : outputs.values() ) {
			if ( out instanceof MonitorOutput ) {
				result.add((MonitorOutput) out);
			}
		}
		return result;
	}

	@Override
	public void scheduleOutput(final IOutput output) {
		scheduledOutputs.add(output);
	}

	@Override
	public void unscheduleOutput(final IOutput o) {
		if ( scheduledOutputs.contains(o) ) {
			outputsToUnschedule.add(o);
		}
		if ( displays != null ) {
			displays.removeDisplayOutput(o);
		}
	}

	@Override
	public void step(final IScope scope) {
		final int cycle = scope.getClock().getCycle();
		scheduledOutputs.removeAll(outputsToUnschedule);
		outputsToUnschedule.clear();
		for ( final IOutput o : new HashSet<IOutput>(scheduledOutputs) ) {
			if ( !o.isPaused() && !o.isClosed() ) {
				final long ii = o.getNextTime();
				// GUI.debug("At cycle " + cycle + ", next update time for " + o.getName() + ": " +
				// ii);
				if ( cycle >= ii ) {
					try {
						try {
							// ? o.hasBeenComputed(false);
							if ( !scope.step(o) ) { return; }
							o.update();
							// outputsToUpdateNow.add(o);
							// ? o.hasBeenComputed(true);
						} catch (final GamaRuntimeException e) {
							throw e;
						} catch (final Exception e) {
							throw GamaRuntimeException.create(e);
						}
					} catch (final GamaRuntimeException e) {
						e.addContext("in computing output " + o.getName());
						e.addContext("output " + o.getName() + " has not been computed at cycle " + cycle);
						GAMA.reportError(e);
						continue;
					}
					o.setNextTime(cycle + o.getRefreshRate());
				}
			}
		}
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		for ( final ISymbol s : commands ) {
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

	@Override
	public IDisplaySurface getDisplaySurfaceFor(final String keyword, final IDisplayOutput layerDisplayOutput,
		final double w, final double h, final Object ... args) {
		if ( displays != null ) { return GuiUtils.getDisplaySurfaceFor(keyword, layerDisplayOutput, w, h, args); }
		return null;
		// return new ImageDisplaySurface(w, h);
	}

	@Override
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

	/**
	 * 
	 */
	public void desynchronizeOutputs() {
		displays.desynchronizeOutputs();
	}

	@Override
	public void forceUpdateOutputs() {
		displays.forceUpdateOutputs();
	}

	public Map<String, IOutput> getOutputs() {
		return outputs;
	}

}
