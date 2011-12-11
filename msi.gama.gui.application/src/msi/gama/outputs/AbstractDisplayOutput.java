/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
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
package msi.gama.outputs;

import java.awt.Color;
import java.awt.image.BufferedImage;
import msi.gama.gui.application.GUI;
import msi.gama.gui.application.views.GamaViewPart;
import msi.gama.gui.graphics.IDisplaySurface;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;

/**
 * The Class AbstractDisplayOutput.
 * 
 * @author drogoul
 */
public abstract class AbstractDisplayOutput extends AbstractOutput implements IDisplayOutput {

	public AbstractDisplayOutput(final IDescription desc) {
		super(desc);
	}

	protected boolean disposed = false;

	@Override
	public void open() {
		super.open();
		openView();
		setRefreshRate(getRefreshRate()); // Workaround for displaying the title
	}

	protected void openView() {
		GamaViewPart view = (GamaViewPart) GUI.showView(getViewId(), isUnique() ? null : getName());
		if ( view == null ) { return; }
		view.setOutput(this);
	}

	@Override
	public void resume() {
		if ( !paused ) { return; }
		super.resume();
	}

	@Override
	public void setRefreshRate(final int refresh) {
		super.setRefreshRate(refresh);
		GUI.setViewRateOf(this, refresh);
	}

	@Override
	public void dispose() {
		if ( disposed ) { return; }
		disposed = true;
	}

	@Override
	public void close() {
		super.close();
		outputManager.unscheduleOutput(this);
	}

	@Override
	public String getViewName() {
		return getName();
	}

	@Override
	public void update() throws GamaRuntimeException {
		GUI.updateViewOf(this);
	}

	@Override
	public boolean isUnique() {
		return false;
	}

	@Override
	public abstract String getViewId();

	@Override
	public void setType(final String t) {}

	@Override
	public BufferedImage getImage() {
		return null;
	}

	@Override
	public IDisplaySurface getSurface() {
		return null;
	}

	@Override
	public Color getBackgroundColor() {
		return Color.white;
	}

	@Override
	public void setBackgroundColor(final Color background) {}

	@Override
	public String getId() {
		return isUnique() ? getViewId() : getViewId() + getName();
	}

}
