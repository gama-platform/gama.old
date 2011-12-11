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

import java.awt.Color;
import msi.gama.factories.*;
import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.java.JavaConstExpression;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.*;

/**
 * The Class OutputFactory.
 * 
 * @author drogoul
 */
@handles({ ISymbolKind.LAYER })
public class OutputLayerFactory extends SymbolFactory {

	public static AbstractDisplayLayer createChartLayer() {
		return null;
	}

	public static AbstractDisplayLayer createGridLayer() {
		return null;
	}

	public static AbstractDisplayLayer createImageLayer() {
		return null;
	}

	public static AbstractDisplayLayer createAgentsLayer(final String title,
		final IExpression listOfAgents, final String aspect, final LayerBox box)
		throws GamaRuntimeException {
		IDescription desc;
		try {
			desc = DescriptionFactory.createOutputDescription(ISymbol.AGENTS, ISymbol.NAME, title);
		} catch (GamlException e) {
			throw new GamaRuntimeException(e);
		}
		AgentDisplayLayer l = new AgentDisplayLayer(desc);
		l.setAspect(aspect);
		l.setBox(box);
		l.setAgentsExpr(listOfAgents);
		return l;
	}

	public static AbstractDisplayLayer createAgentsLayer(final String title,
		final IExpression listOfAgents, final String aspect) throws GamaRuntimeException {
		return createAgentsLayer(title, listOfAgents, aspect, OutputFactory.largeBox);
	}

	public static AbstractDisplayLayer createAgentsLayer(final String title,
		final IExpression listOfAgents) throws GamaRuntimeException {
		return createAgentsLayer(title, listOfAgents, ISymbol.DEFAULT);
	}

	public static AbstractDisplayLayer createSpeciesLayer(final String title,
		final ISpecies species, final String aspect, final LayerBox box)
		throws GamaRuntimeException {
		IDescription desc;
		try {
			desc =
				DescriptionFactory.createOutputDescription(ISymbol.SPECIES, ISymbol.NAME,
					species.getName());
		} catch (GamlException e) {
			throw new GamaRuntimeException(e);
		}
		SpeciesDisplayLayer l = new SpeciesDisplayLayer(desc);
		l.setAspect(aspect);
		l.setBox(box);
		return l;
	}

	public static AbstractDisplayLayer createSpeciesLayer(final String title,
		final ISpecies species, final String aspect) throws GamaRuntimeException {
		return createSpeciesLayer(title, species, aspect, OutputFactory.largeBox);
	}

	public static AbstractDisplayLayer createSpeciesLayer(final String title, final ISpecies species)
		throws GamaRuntimeException {
		return createSpeciesLayer(title, species, ISymbol.DEFAULT);
	}

	public static AbstractDisplayLayer createSpeciesLayer(final ISpecies species)
		throws GamaRuntimeException {
		return createSpeciesLayer(species.getName(), species);
	}

	public static AbstractDisplayLayer createTextLayer(final String title, final IExpression text,
		final Color color, final LayerBox box) throws GamaRuntimeException {
		IDescription desc;
		try {
			desc = DescriptionFactory.createOutputDescription(ISymbol.TEXT, ISymbol.NAME, title);
		} catch (GamlException e) {
			throw new GamaRuntimeException(e);
		}
		TextDisplayLayer l = new TextDisplayLayer(desc);
		l.setTextExpr(text);
		l.setBox(box);
		l.setColor(color);
		return l;
	}

	public static AbstractDisplayLayer createTextLayer(final String title, final String text,
		final Color color, final LayerBox box) throws GamaRuntimeException {
		return createTextLayer(title, new JavaConstExpression(text), color, box);
	}

	public static AbstractDisplayLayer createTextLayer(final String text, final Color color,
		final LayerBox box) throws GamaRuntimeException {
		return createTextLayer(text, text, color, box);
	}

	public static AbstractDisplayLayer createTextLayer(final String text, final LayerBox box)
		throws GamaRuntimeException {
		return createTextLayer(text, text, Color.black, box);
	}

	public static AbstractDisplayLayer createTextLayer(final String text)
		throws GamaRuntimeException {
		return createTextLayer(text, OutputFactory.largeBox);
	}

}
