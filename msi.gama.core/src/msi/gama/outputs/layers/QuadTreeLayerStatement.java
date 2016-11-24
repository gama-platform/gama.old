/*********************************************************************************************
 *
 * 'QuadTreeLayerStatement.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 9 nov. 2009
 *
 * @todo Description
 *
 */
@symbol(name = IKeyword.QUADTREE, kind = ISymbolKind.LAYER, with_sequence = false, concept = { IConcept.DISPLAY })
@inside(symbols = IKeyword.DISPLAY)
@facets(
	value = {
		@facet(name = IKeyword.POSITION,
			type = IType.POINT,
			optional = true,
			doc = @doc("position of the upper-left corner of the layer. Note that if coordinates are in [0,1[, the position is relative to the size of the environment (e.g. {0.5,0.5} refers to the middle of the display) whereas it is absolute when coordinates are greater than 1. The position can only be a 3D point {0.5, 0.5, 0.5}, the last coordinate specifying the elevation of the layer.")),
		@facet(name = IKeyword.SIZE,
		type = IType.POINT,
		optional = true,
		doc = @doc("the layer resize factor: {1,1} refers to the original size whereas {0.5,0.5} divides by 2 the height and the width of the layer. In case of a 3D layer, a 3D point can be used (note that {1,1} is equivalent to {1,1,0}, so a resize of a layer containing 3D objects with a 2D points will remove the elevation)")),
		@facet(name = IKeyword.TRANSPARENCY,
		type = IType.FLOAT,
		optional = true,
		doc = @doc("the transparency rate of the agents (between 0 and 1, 1 means no transparency)")),
		@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false, doc = @doc("identifier of the layer")),
		@facet(name = IKeyword.REFRESH,
		type = IType.BOOL,
		optional = true,
		doc = @doc("(openGL only) specify whether the layer is refreshed. (true by default, usefull in case of agents that do not move)")) },
	omissible = IKeyword.NAME)
@doc(deprecated = "The `quadtree`layer will be removed soon from the definition of displays",
value = "`" + IKeyword.QUADTREE + "` allows the modeler to display the quadtree.",
usages = { @usage(value = "The general syntax is:",
examples = { @example(value = "display my_display {", isExecutable = false),
	@example(value = "   quadtree 'qt' position: { 0, 0.5 } size: quadrant_size;", isExecutable = false),
	@example(value = "}", isExecutable = false) }) },
see = { IKeyword.DISPLAY, IKeyword.AGENTS, IKeyword.CHART, IKeyword.EVENT, "graphics", IKeyword.GRID_POPULATION,
	IKeyword.IMAGE, IKeyword.OVERLAY, IKeyword.POPULATION })
@Deprecated
public class QuadTreeLayerStatement extends AbstractLayerStatement {

	// BufferedImage supportImage;

	// private IEnvironment modelEnv;

	public QuadTreeLayerStatement(/* final ISymbol context, */final IDescription desc) throws GamaRuntimeException {
		super(desc);
	}

	@Override
	public boolean _init(final IScope scope) throws GamaRuntimeException {
		// if ( scope.getSimulationScope() == null ) { return false; }
		// Envelope env = scope.getSimulationScope().getEnvelope();
		// supportImage = ImageUtils.createCompatibleImage((int) env.getWidth(), (int) env.getHeight());
		return true;
	}

	@Override
	public boolean _step(final IScope scope) throws GamaRuntimeException {
		// if ( supportImage == null ) { return false; }
		// IGraphics g = scope.getGraphics();
		// if ( g != null ) {
		// if ( supportImage.getWidth() != g.getDisplayWidthInPixels() ||
		// supportImage.getHeight() != g.getDisplayHeightInPixels() ) {
		// supportImage.flush();
		// supportImage =
		// ImageUtils.createCompatibleImage(g.getDisplayWidthInPixels(), g.getDisplayHeightInPixels());
		// }
		// }
		// Graphics2D g2 = (Graphics2D) supportImage.getGraphics();
		// ITopology t = scope.getTopology();
		// if ( t != null ) {
		// t.displaySpatialIndexOn(g2, supportImage.getWidth(), supportImage.getHeight());
		// }
		return true;
	}

	@Override
	public short getType() {
		return ILayerStatement.QUADTREE;
	}

	@Override
	public void dispose() {
		// supportImage.flush();
		// supportImage = null;
		super.dispose();
	}

	// public BufferedImage getSupportImage() {
	//
	// // return supportImage;
	// }
}
