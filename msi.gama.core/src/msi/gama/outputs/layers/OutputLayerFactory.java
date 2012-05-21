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
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.*;
import msi.gaml.factories.*;
import msi.gaml.species.ISpecies;

/**
 * The Class OutputFactory.
 * 
 * @author drogoul
 */
@handles({ ISymbolKind.LAYER })
public class OutputLayerFactory extends SymbolFactory {

	static public IDisplayLayerBox largeBox = new LayerBox(1d, 0d, 0d, 1d, 1d);

	/**
	 * @param superFactory
	 */
	public OutputLayerFactory(final ISymbolFactory superFactory) {
		super(superFactory);
	}

	public static IDisplayLayer createChartLayer() {
		return null;
	}

	public static IDisplayLayer createGridLayer() {
		return null;
	}

	public static IDisplayLayer createImageLayer() {
		return null;
	}

	public static IDisplayLayer createAgentsLayer(final String title,
		final IExpression listOfAgents, final String aspect, final IDisplayLayerBox box)
		throws GamaRuntimeException {
		IDescription desc;
		desc = DescriptionFactory.createOutputDescription(IKeyword.AGENTS, IKeyword.NAME, title);
		AgentDisplayLayer l = new AgentDisplayLayer(desc);
		l.setAspect(aspect);
		l.setBox(box);
		l.setAgentsExpr(listOfAgents);
		return l;
	}

	public static IDisplayLayer createAgentsLayer(final String title,
		final IExpression listOfAgents, final String aspect) throws GamaRuntimeException {
		return createAgentsLayer(title, listOfAgents, aspect, largeBox);
	}

	public static IDisplayLayer createAgentsLayer(final String title, final IExpression listOfAgents)
		throws GamaRuntimeException {
		return createAgentsLayer(title, listOfAgents, IKeyword.DEFAULT);
	}

	public static IDisplayLayer createSpeciesLayer(final String title, final ISpecies species,
		final String aspect, final IDisplayLayerBox box) throws GamaRuntimeException {
		IDescription desc;
		desc =
			DescriptionFactory.createOutputDescription(IKeyword.SPECIES, IKeyword.NAME,
				species.getName());
		SpeciesDisplayLayer l = new SpeciesDisplayLayer(desc);
		l.setAspect(aspect);
		l.setBox(box);
		return l;
	}

	public static IDisplayLayer createSpeciesLayer(final String title, final ISpecies species,
		final String aspect) throws GamaRuntimeException {
		return createSpeciesLayer(title, species, aspect, largeBox);
	}

	public static IDisplayLayer createSpeciesLayer(final String title, final ISpecies species)
		throws GamaRuntimeException {
		return createSpeciesLayer(title, species, IKeyword.DEFAULT);
	}

	public static IDisplayLayer createSpeciesLayer(final ISpecies species)
		throws GamaRuntimeException {
		return createSpeciesLayer(species.getName(), species);
	}

	public static IDisplayLayer createTextLayer(final String title, final IExpression text,
		final Color color, final IDisplayLayerBox box) throws GamaRuntimeException {
		IDescription desc;
		desc = DescriptionFactory.createOutputDescription(IKeyword.TEXT, IKeyword.NAME, title);
		TextDisplayLayer l = new TextDisplayLayer(desc);
		l.setTextExpr(text);
		l.setBox(box);
		l.setColor(color);
		return l;
	}

	public static IDisplayLayer createTextLayer(final String title, final String text,
		final Color color, final IDisplayLayerBox box) throws GamaRuntimeException {
		return createTextLayer(title, new JavaConstExpression(text), color, box);
	}

	public static IDisplayLayer createTextLayer(final String text, final Color color,
		final IDisplayLayerBox box) throws GamaRuntimeException {
		return createTextLayer(text, text, color, box);
	}

	public static IDisplayLayer createTextLayer(final String text, final IDisplayLayerBox box)
		throws GamaRuntimeException {
		return createTextLayer(text, text, Color.black, box);
	}

	public static IDisplayLayer createTextLayer(final String text) throws GamaRuntimeException {
		return createTextLayer(text, largeBox);
	}

}
