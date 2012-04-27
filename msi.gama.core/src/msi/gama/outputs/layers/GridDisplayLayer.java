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
package msi.gama.outputs.layers;

import java.awt.Color;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
@symbol(name = IKeyword.GRID, kind = ISymbolKind.LAYER)
@inside(symbols = IKeyword.DISPLAY)
@facets(value = { @facet(name = IKeyword.POSITION, type = IType.POINT_STR, optional = true),
	@facet(name = IKeyword.SIZE, type = IType.POINT_STR, optional = true),
	@facet(name = IKeyword.TRANSPARENCY, type = IType.FLOAT_STR, optional = true),
	@facet(name = IKeyword.NAME, type = IType.ID, optional = false),
	@facet(name = IKeyword.LINES, type = IType.COLOR_STR, optional = true) }, omissible = IKeyword.NAME)
public class GridDisplayLayer extends AbstractDisplayLayer {

	public GridDisplayLayer(/* final ISymbol context, */final IDescription desc)
		throws GamaRuntimeException {
		super(desc);
	}

	GamaSpatialMatrix grid;
	IExpression lineColor;
	GamaColor currentColor, constantColor;

	// BufferedImage supportImage;

	@Override
	public void prepare(final IDisplayOutput out, final IScope sim) throws GamaRuntimeException {
		super.prepare(out, sim);

		lineColor = getFacet(IKeyword.LINES);
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
		// if ( supportImage != null ) {
		// supportImage.flush();
		// }
		// supportImage = ImageUtils.createCompatibleImage(grid.numCols, grid.numRows);
	}

	public GamaSpatialMatrix getEnvironment() {
		return grid;
	}

	@Override
	public short getType() {
		return IDisplayLayer.GRID;
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

		if ( lineColor == null || constantColor != null ) { return; }
		currentColor = Cast.asColor(sim, lineColor.value(sim));
	}

}
