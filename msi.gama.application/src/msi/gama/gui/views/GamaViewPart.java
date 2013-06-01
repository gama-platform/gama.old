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
package msi.gama.gui.views;

import msi.gama.common.interfaces.IGamaView;
import msi.gama.gui.views.actions.*;
import msi.gama.outputs.*;
import msi.gama.runtime.GAMA;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

/**
 * @author drogoul
 */
public abstract class GamaViewPart extends ViewPart implements IGamaView, IGamaViewActions {

	protected IDisplayOutput output = null;
	protected Composite parent;

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		final String s_id = site.getSecondaryId();
		final String id = site.getId() + (s_id == null ? "" :  s_id);
		if ( GAMA.getExperiment() != null ) {
			final IOutputManager manager = GAMA.getExperiment().getOutputManager();
			if ( manager != null ) {
				final IDisplayOutput out = (IDisplayOutput) manager.getOutput(id);
				setOutput(out);
			}
		}
		GamaToolbarFactory.buildToolbar(this, getToolbarActionsId());
	}

	/**
	 * @return
	 */
	protected abstract Integer[] getToolbarActionsId();

	@Override
	public final void createPartControl(final Composite parent) {
		this.parent = parent;
		ownCreatePartControl(parent);
		activateContext();
	}

	public abstract void ownCreatePartControl(Composite parent);

	public void activateContext() {
		final IContextService contextService = (IContextService) getSite().getService(IContextService.class);
		contextService.activateContext("msi.gama.application.simulation.context");
	}

	@Override
	public void setRefreshRate(final int rate) {
		if ( rate > 0 ) {
			setPartName(getOutput().getName() + " [refresh every " + String.valueOf(rate) +
				(rate == 1 ? " cycle]" : " cycles]"));
		}
	}

	@Override
	public void update(final IDisplayOutput output) {}

	@Override
	public IDisplayOutput getOutput() {
		return output;
	}

	@Override
	public void setOutput(final IDisplayOutput out) {
		output = out;
	}

	@Override
	public void setFocus() {}

}
