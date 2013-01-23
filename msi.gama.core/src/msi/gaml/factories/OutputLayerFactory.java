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
package msi.gaml.factories;

import java.awt.Color;
import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.outputs.layers.*;
import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gama.precompiler.*;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.species.ISpecies;
import msi.gaml.types.*;

/**
 * The Class OutputFactory.
 * 
 * @author drogoul
 */
@factory(handles = { ISymbolKind.LAYER }, uses = { ISymbolKind.SINGLE_STATEMENT,
	ISymbolKind.SEQUENCE_STATEMENT })
public class OutputLayerFactory extends StatementFactory {

	static public IDisplayLayerBox largeBox = new LayerBox(1d, 0d, 0d, 1d, 1d, 0d, true);

	public OutputLayerFactory(final List<Integer> handles, final List<Integer> uses) {
		super(handles, uses);
	}

	@Override
	protected void privateValidate(final IDescription desc) {
		super.privateValidate(desc);
	}

	@Override
	protected void compileFacet(final String tag, final IDescription sd) {
		// Special case for the compilation of the "species species: ..." layer, which expects an
		// expression, contrary to the "species" statement, which expects an ID. The same for
		// "grid".
		if ( tag.equals(SPECIES) ) {
			IExpressionDescription ed = sd.getFacets().get(tag);
			ed.compileAsLabel();
			String s = sd.getFacets().getLabel(tag);
			SpeciesDescription target = sd.getSpeciesDescription(s);
			if ( target == null ) {
				sd.flagError(s + " is not the name of a species", IGamlIssue.WRONG_TYPE, ed);
			} else {
				if ( sd.getKeyword().equals(GRID_POPULATION) && !target.isGrid() ) {
					sd.flagError(s + " is not a grid", IGamlIssue.WRONG_TYPE, ed);
				} else {
					IExpression expr =
						GAMA.getExpressionFactory().createConst(s, Types.get(IType.SPECIES_STR));
					ed.setExpression(expr);
				}
			}
		} else if ( tag.equals(ASPECT) ) {
			IExpressionDescription ed = sd.getFacets().get(ASPECT);
			String s = sd.getFacets().getLabel(SPECIES);
			String a = sd.getFacets().getLabel(ASPECT);
			SpeciesDescription species = sd.getSpeciesDescription(s);
			if ( species != null ) {
				if ( species.getAspect(a) != null ) {
					ed.compileAsLabel();
				} else {
					sd.flagError(a + " is not the name of an aspect of " + s, IGamlIssue.GENERAL,
						ed);
				}
			}
		} else {
			super.compileFacet(tag, sd);
		}
	}

	public static ILayerStatement createChartLayer() {
		return null;
	}

	public static ILayerStatement createGridLayer() {
		return null;
	}

	public static ILayerStatement createImageLayer() {
		return null;
	}

	public static ILayerStatement createAgentsLayer(final String title,
		final IExpression listOfAgents, final String aspect, final IDisplayLayerBox box)
		throws GamaRuntimeException {
		IDescription desc;
		desc = DescriptionFactory.createOutputDescription(IKeyword.AGENTS, IKeyword.NAME, title);
		AgentLayerStatement l = new AgentLayerStatement(desc);
		l.setAspect(aspect);
		l.setBox(box);
		l.setAgentsExpr(listOfAgents);
		return l;
	}

	public static ILayerStatement createAgentsLayer(final String title,
		final IExpression listOfAgents, final String aspect) throws GamaRuntimeException {
		return createAgentsLayer(title, listOfAgents, aspect, largeBox);
	}

	public static ILayerStatement createAgentsLayer(final String title,
		final IExpression listOfAgents) throws GamaRuntimeException {
		return createAgentsLayer(title, listOfAgents, IKeyword.DEFAULT);
	}

	public static ILayerStatement createSpeciesLayer(final String title, final ISpecies species,
		final String aspect, final IDisplayLayerBox box) throws GamaRuntimeException {
		IDescription desc;
		desc =
			DescriptionFactory.createOutputDescription(IKeyword.SPECIES, IKeyword.NAME,
				species.getName());
		SpeciesLayerStatement l = new SpeciesLayerStatement(desc);
		l.setAspect(aspect);
		l.setBox(box);
		return l;
	}

	public static ILayerStatement createSpeciesLayer(final String title, final ISpecies species,
		final String aspect) throws GamaRuntimeException {
		return createSpeciesLayer(title, species, aspect, largeBox);
	}

	public static ILayerStatement createSpeciesLayer(final String title, final ISpecies species)
		throws GamaRuntimeException {
		return createSpeciesLayer(title, species, IKeyword.DEFAULT);
	}

	public static ILayerStatement createSpeciesLayer(final ISpecies species)
		throws GamaRuntimeException {
		return createSpeciesLayer(species.getName(), species);
	}

	public static ILayerStatement createTextLayer(final String title, final IExpression text,
		final Color color, final IDisplayLayerBox box) throws GamaRuntimeException {
		IDescription desc;
		desc = DescriptionFactory.createOutputDescription(IKeyword.TEXT, IKeyword.NAME, title);
		TextLayerStatement l = new TextLayerStatement(desc);
		l.setTextExpr(text);
		l.setBox(box);
		l.setColor(color);
		return l;
	}

	public static ILayerStatement createTextLayer(final String title, final String text,
		final Color color, final IDisplayLayerBox box) throws GamaRuntimeException {
		return createTextLayer(title, new JavaConstExpression(text), color, box);
	}

	public static ILayerStatement createTextLayer(final String text, final Color color,
		final IDisplayLayerBox box) throws GamaRuntimeException {
		return createTextLayer(text, text, color, box);
	}

	public static ILayerStatement createTextLayer(final String text, final IDisplayLayerBox box)
		throws GamaRuntimeException {
		return createTextLayer(text, text, Color.black, box);
	}

	public static ILayerStatement createTextLayer(final String text) throws GamaRuntimeException {
		return createTextLayer(text, largeBox);
	}

}
