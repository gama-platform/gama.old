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
package msi.gama.outputs.layers;

import java.awt.Color;
import java.awt.image.BufferedImage;
import msi.gama.gui.displays.IDisplay;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.outputs.LayerDisplayOutput;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.*;
import msi.gama.util.matrix.GamaSpatialMatrix;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
@symbol(name = ISymbol.GRID, kind = ISymbolKind.LAYER)
@inside(symbols = ISymbol.DISPLAY)
@facets({ @facet(name = ISymbol.POSITION, type = IType.POINT_STR, optional = true),
	@facet(name = ISymbol.SIZE, type = IType.POINT_STR, optional = true),
	@facet(name = ISymbol.TRANSPARENCY, type = IType.FLOAT_STR, optional = true),
	@facet(name = ISymbol.NAME, type = IType.ID, optional = false),
	@facet(name = ISymbol.LINES, type = IType.COLOR_STR, optional = true) })
public class GridDisplayLayer extends AbstractDisplayLayer {

	public GridDisplayLayer(/* final ISymbol context, */final IDescription desc)
		throws GamaRuntimeException {
		super(desc);
	}

	GamaSpatialMatrix grid;
	IExpression lineColor;
	GamaColor currentColor, constantColor;
	BufferedImage supportImage;

	@Override
	public void prepare(final LayerDisplayOutput out, final IScope sim) throws GamaRuntimeException {
		super.prepare(out, sim);
		lineColor = getFacet(ISymbol.LINES);
		if ( lineColor != null ) {
			constantColor = Cast.asColor(sim, lineColor.value(sim));
			currentColor = constantColor;
		}
		final IPopulation gridPop = sim.getAgentScope().getPopulationFor(getName());
		if ( gridPop == null ) {
			throw new GamaRuntimeException("missing environment for output " + getName());
		} else if ( !gridPop.isGrid() ) { throw new GamaRuntimeException(
			"not a grid environment for: " + getName()); }

		grid = (GamaSpatialMatrix) gridPop.getTopology().getPlaces();
		if ( supportImage != null ) {
			supportImage.flush();
		}
		supportImage = ImageCache.createCompatibleImage(grid.numCols, grid.numRows);
	}

	public BufferedImage getSupportImage() {
		return supportImage;
	}

	public GamaSpatialMatrix getEnvironment() {
		return grid;
	}

	@Override
	public short getType() {
		return IDisplay.GRID;
	}

	public Color getLineColor() {
		return currentColor;
	}

	public boolean drawLines() {
		return currentColor != null;
	}

	@Override
	public void compute(final IScope sim, final long cycle) throws GamaRuntimeException {
		super.compute(sim, cycle);
		supportImage.setRGB(0, 0, grid.numCols, grid.numRows, grid.getDisplayData(), 0,
			grid.numCols);
		if ( lineColor == null || constantColor != null ) { return; }
		currentColor = Cast.asColor(sim, lineColor.value(sim));
	}

}
