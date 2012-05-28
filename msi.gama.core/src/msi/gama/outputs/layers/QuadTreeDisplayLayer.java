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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.ImageUtils;
import msi.gama.metamodel.topology.IEnvironment;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
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
@symbol(name = IKeyword.QUADTREE, kind = ISymbolKind.LAYER)
@inside(symbols = IKeyword.DISPLAY)
@facets(value = { @facet(name = IKeyword.POSITION, type = IType.POINT_STR, optional = true),
	@facet(name = IKeyword.SIZE, type = IType.POINT_STR, optional = true),
	@facet(name = IKeyword.TRANSPARENCY, type = IType.FLOAT_STR, optional = true),
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.Z, type = IType.FLOAT_STR, optional = true)}, omissible = IKeyword.NAME)
    
public class QuadTreeDisplayLayer extends AbstractDisplayLayer {

	BufferedImage supportImage;

	private IEnvironment modelEnv;

	public QuadTreeDisplayLayer(/* final ISymbol context, */final IDescription desc)
		throws GamaRuntimeException {
		super(desc);
	}

	@Override
	public void prepare(final IDisplayOutput out, final IScope scope) throws GamaRuntimeException {
		super.prepare(out, scope);
		if ( modelEnv == null ) {
			modelEnv = scope.getSimulationScope().getModel().getModelEnvironment();
		}
		supportImage =
			ImageUtils.createCompatibleImage((int) modelEnv.getWidth(), (int) modelEnv.getHeight());
	}

	@Override
	public void compute(final IScope sim, final long cycle) throws GamaRuntimeException {
		Graphics2D g2 = (Graphics2D) supportImage.getGraphics();
		modelEnv.displaySpatialIndexOn(g2, supportImage.getWidth(), supportImage.getHeight());
		super.compute(sim, cycle);
	}

	@Override
	public short getType() {
		return IDisplayLayer.QUADTREE;
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
