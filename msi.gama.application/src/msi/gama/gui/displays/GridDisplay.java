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
package msi.gama.gui.displays;

import java.awt.Color;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.ImageUtils;
import msi.gama.gui.parameters.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import org.eclipse.swt.widgets.Composite;

public class GridDisplay extends ImageDisplay {

	private boolean turnGridOn;
	private Color lineColor;

	public GridDisplay(final double env_width, final double env_height, final IDisplayLayer model,
		final IGraphics dg) {
		super(env_width, env_height, model, dg);
		turnGridOn = ((GridDisplayLayer) model).drawLines();
	}

	@Override
	public void updateEnvDimensions(final double env_width, final double env_height) {
		super.updateEnvDimensions(env_width, env_height);
		if ( image != null ) {
			image.flush();
			image = null;
		}
	}

	@Override
	public void fillComposite(final Composite compo, final IDisplaySurface container) {
		super.fillComposite(compo, container);
		EditorFactory.create(compo, "Draw grid:", turnGridOn, new EditorListener<Boolean>() {

			@Override
			public void valueModified(final Boolean newValue) throws GamaRuntimeException {
				turnGridOn = newValue;
				container.updateDisplay();
			}
		});
	}

	@Override
	protected void buildImage() {
		GridDisplayLayer g = (GridDisplayLayer) model;
		GamaSpatialMatrix m = g.getEnvironment();
		if ( image == null ) {
			image = ImageUtils.createCompatibleImage(m.numCols, m.numRows);
		}
		image.setRGB(0, 0, m.numCols, m.numRows, m.getDisplayData(), 0, m.numCols);
	}

	@Override
	public void privateDrawDisplay(final IGraphics dg) {
		buildImage();
		if ( image == null ) { return; }
		dg.drawImage(image, null, false,"GridDisplay");
		if ( turnGridOn ) {
			lineColor = ((GridDisplayLayer) model).getLineColor();
			if ( lineColor == null ) {
				lineColor = Color.black;
			}
			dg.drawGrid(image,lineColor,size);		
		}
	}

	private IAgent getPlaceAt(final GamaPoint loc) {
		return ((GridDisplayLayer) model).getEnvironment().getPlaceAt(loc).getAgent();
	}

	@Override
	public Set<IAgent> collectAgentsAt(final int x, final int y) {
		Set<IAgent> result = new HashSet();
		result.add(getPlaceAt(this.getModelCoordinatesFrom(x, y)));
		return result;
	}

	@Override
	protected String getType() {
		return "Grid layer";
	}

}
