/*********************************************************************************************
 * 
 *
 * 'QuadTreeLayerStatement.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.outputs.layers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.ImageUtils;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.IType;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
@symbol(name = IKeyword.QUADTREE, kind = ISymbolKind.LAYER, with_sequence = false)
@inside(symbols = IKeyword.DISPLAY)
@facets(value = { @facet(name = IKeyword.POSITION, type = IType.POINT, optional = true),
	@facet(name = IKeyword.SIZE, type = IType.POINT, optional = true),
	@facet(name = IKeyword.TRANSPARENCY, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.REFRESH, type = IType.BOOL, optional = true) }, omissible = IKeyword.NAME)
public class QuadTreeLayerStatement extends AbstractLayerStatement {

	BufferedImage supportImage;

	// private IEnvironment modelEnv;

	public QuadTreeLayerStatement(/* final ISymbol context, */final IDescription desc) throws GamaRuntimeException {
		super(desc);
	}

	@Override
	public boolean _init(final IScope scope) throws GamaRuntimeException {
		Envelope env = scope.getSimulationScope().getEnvelope();
		supportImage = ImageUtils.createCompatibleImage((int) env.getWidth(), (int) env.getHeight());
		return true;
	}

	@Override
	public boolean _step(final IScope scope) throws GamaRuntimeException {
		IGraphics g = scope.getGraphics();
		if ( g != null ) {
			if ( supportImage.getWidth() != g.getDisplayWidthInPixels() ||
				supportImage.getHeight() != g.getDisplayHeightInPixels() ) {
				supportImage.flush();
				supportImage =
					ImageUtils.createCompatibleImage(g.getDisplayWidthInPixels(), g.getDisplayHeightInPixels());
			}
		}
		Graphics2D g2 = (Graphics2D) supportImage.getGraphics();
		ITopology t = scope.getTopology();
		if ( t != null ) {
			t.displaySpatialIndexOn(g2, supportImage.getWidth(), supportImage.getHeight());
		}
		return true;
	}

	@Override
	public short getType() {
		return ILayerStatement.QUADTREE;
	}

	@Override
	public void dispose() {
		supportImage.flush();
		supportImage = null;
		super.dispose();
	}

	public BufferedImage getSupportImage() {
		return supportImage;
	}
}
