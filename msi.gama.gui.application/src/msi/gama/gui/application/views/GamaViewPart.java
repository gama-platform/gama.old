/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application.views;

import msi.gama.interfaces.IDisplayOutput;
import msi.gama.kernel.GAMA;
import msi.gama.outputs.OutputManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

/**
 * @author drogoul
 */
public abstract class GamaViewPart extends ViewPart implements IGamaView {

	IDisplayOutput output = null;
	Composite parent;

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		final String s_id = site.getSecondaryId();
		final String id = site.getId() + (s_id == null ? "" : s_id);
		if ( GAMA.getExperiment() != null ) {
			OutputManager manager = GAMA.getExperiment().getOutputManager();
			if ( manager != null ) {
				IDisplayOutput out = (IDisplayOutput) manager.getOutput(id);
				setOutput(out);
			}
		}
	}

	@Override
	public final void createPartControl(final Composite parent) {
		this.parent = parent;
		ownCreatePartControl(parent);
		activateContext();
	}

	public abstract void ownCreatePartControl(Composite parent);

	public void activateContext() {
		IContextService contextService =
			(IContextService) getSite().getService(IContextService.class);
		contextService.activateContext("msi.gama.gui.application.simulation.context");
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
