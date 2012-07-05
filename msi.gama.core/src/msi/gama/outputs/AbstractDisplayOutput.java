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

import java.awt.Color;
import java.awt.image.BufferedImage;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;

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
		IGamaView view = GuiUtils.showView(getViewId(), isUnique() ? null : getName());
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
		GuiUtils.setViewRateOf(this, refresh);
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
		GuiUtils.updateViewOf(this);
	}

	@Override
	public void forceUpdate() throws GamaRuntimeException {
		update();
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
