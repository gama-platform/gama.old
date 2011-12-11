/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs.layers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import msi.gama.environment.ModelEnvironment;
import msi.gama.gui.displays.IDisplay;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.outputs.LayerDisplayOutput;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.ImageCache;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
@symbol(name = ISymbol.QUADTREE, kind = ISymbolKind.LAYER)
@inside(symbols = ISymbol.DISPLAY)
@facets({ @facet(name = ISymbol.POSITION, type = IType.POINT_STR, optional = true),
	@facet(name = ISymbol.SIZE, type = IType.POINT_STR, optional = true),
	@facet(name = ISymbol.TRANSPARENCY, type = IType.FLOAT_STR, optional = true),
	@facet(name = ISymbol.NAME, type = IType.LABEL, optional = false) })
public class QuadTreeDisplayLayer extends AbstractDisplayLayer {

	BufferedImage supportImage;
	
	private ModelEnvironment modelEnv;

	public QuadTreeDisplayLayer(/* final ISymbol context, */final IDescription desc)
		throws GamaRuntimeException {
		super(desc);
	}

	@Override
	public void prepare(final LayerDisplayOutput out, final IScope scope)
		throws GamaRuntimeException {
		super.prepare(out, scope);
		if (modelEnv == null) { modelEnv = scope.getSimulationScope().getModel().getModelEnvironment(); }
		supportImage =
			ImageCache.createCompatibleImage((int) modelEnv.getWidth(), (int) modelEnv.getHeight());
	}

	@Override
	public void compute(final IScope sim, final long cycle) throws GamaRuntimeException {
		Graphics2D g2 = (Graphics2D) supportImage.getGraphics();
		modelEnv.displaySpatialIndexOn(g2, supportImage.getWidth(), supportImage.getHeight());
		super.compute(sim, cycle);
	}

	@Override
	public short getType() {
		return IDisplay.QUADTREE;
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
