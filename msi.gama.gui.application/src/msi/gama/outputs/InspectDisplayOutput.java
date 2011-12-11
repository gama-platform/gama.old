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
package msi.gama.outputs;

import java.util.*;
import msi.gama.factories.DescriptionFactory;
import msi.gama.gui.application.views.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.GamaList;

/**
 * The Class AbstractInspectOutput.
 * 
 * @author drogoul
 */
@SuppressWarnings("unchecked")
@symbol(name = ISymbol.INSPECT, kind = ISymbolKind.OUTPUT)
@inside(symbols = ISymbol.OUTPUT)
@facets(value = {
	@facet(name = ISymbol.NAME, type = IType.LABEL, optional = false),
	@facet(name = ISymbol.REFRESH_EVERY, type = IType.INT_STR, optional = true),
	@facet(name = ISymbol.VALUE, type = IType.NONE_STR, optional = true),
	@facet(name = ISymbol.TYPE, type = IType.ID, values = { ISymbol.AGENT, ISymbol.SPECIES,
		ISymbol.DYNAMIC }, optional = false) })
public class InspectDisplayOutput extends MonitorOutput {

	public static final short INSPECT_AGENT = 0;
	public static final short INSPECT_DYNAMIC = 2;
	public static final short INSPECT_SPECIES = 1;
	static int count = 0;

	// TODO Add the dynamic inspector to the outputs (for the moment, only computed by the view
	// directly)
	// The agents inspected should also be known at runtime (instead of being kept by the view).
	// This in order to compute the new values of their attributes.

	static final List<String> types = Arrays
		.asList(ISymbol.AGENT, ISymbol.SPECIES, ISymbol.DYNAMIC);

	int target;

	public InspectDisplayOutput(/* final ISymbol context, */final IDescription desc) {
		super(desc);
		String type = getLiteral(ISymbol.TYPE);
		if ( value != null ) {
			target = INSPECT_DYNAMIC;
		} else {
			target = types.indexOf(type);
		}
	}

	public InspectDisplayOutput(final String name, final short type) throws GamlException {
		// Opens directly an inspector
		super(DescriptionFactory.createDescription(ISymbol.INSPECT, ISymbol.NAME, name +
			(type != INSPECT_SPECIES ? count++ : ""), ISymbol.TYPE, types.get(type)));
		target = type;
	}

	public void launch() throws GamaRuntimeException {
		prepare(GAMA.getFrontmostSimulation());
		outputManager.addOutput(this);
		schedule();
		open();
		update();
	}

	@Override
	public boolean isUnique() {
		return target != INSPECT_DYNAMIC;
	}

	@Override
	public String getViewId() {
		switch (target) {
			case INSPECT_DYNAMIC:
				return DynamicAgentInspectView.ID;
			case INSPECT_AGENT:
				return AgentInspectView.ID;
			case INSPECT_SPECIES:
				return SpeciesInspectView.ID;
			default:
				return AgentInspectView.ID;
		}
	}

	@Override
	public void setType(final String t) {
		target = types.indexOf(t);
	}

	@Override
	public List<IAgent> getLastValue() {
		if ( lastValue instanceof IAgent ) { return GamaList.with(lastValue); }
		if ( lastValue instanceof List ) {
			for ( Object o : (List) lastValue ) {
				if ( !(o instanceof IAgent) ) { return null; }
			}
			return (List) lastValue;
		}
		return null;
	}

}
