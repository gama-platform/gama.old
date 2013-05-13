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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.descriptions.IDescription;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.types.IType;

/**
 * The Class AbstractInspectOutput.
 * 
 * @author drogoul
 */
@SuppressWarnings("unchecked")
@symbol(name = IKeyword.INSPECT, kind = ISymbolKind.OUTPUT, with_sequence = false)
@inside(symbols = IKeyword.OUTPUT)
@facets(value = {
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.REFRESH_EVERY, type = IType.INT, optional = true),
	@facet(name = IKeyword.VALUE, type = IType.NONE, optional = true),
	@facet(name = IKeyword.TYPE, type = IType.ID, values = { IKeyword.AGENT, IKeyword.SPECIES, IKeyword.DYNAMIC }, optional = false) }, omissible = IKeyword.NAME)
public class InspectDisplayOutput extends MonitorOutput {

	public static final short INSPECT_AGENT = 0;
	public static final short INSPECT_DYNAMIC = 2;
	public static final short INSPECT_SPECIES = 1;

	static int count = 0;

	// TODO Add the dynamic inspector to the outputs (for the moment, only computed by the view
	// directly)
	// The agents inspected should also be known at runtime (instead of being kept by the view).
	// This in order to compute the new values of their attributes.

	static final List<String> types = Arrays.asList(IKeyword.AGENT, IKeyword.SPECIES, IKeyword.DYNAMIC);

	int target;

	public InspectDisplayOutput(final IDescription desc) {
		super(desc);
		String type = getLiteral(IKeyword.TYPE);
		if ( value != null ) {
			target = INSPECT_DYNAMIC;
		} else {
			target = types.indexOf(type);
		}
	}

	public InspectDisplayOutput(final String name, final short type) {
		// Opens directly an inspector
		super(DescriptionFactory.create(IKeyword.INSPECT, IKeyword.NAME, name +
			(type != INSPECT_SPECIES ? count++ : ""), IKeyword.TYPE, types.get(type)));
		target = type;
	}

	public void launch() throws GamaRuntimeException {
		GAMA.run(new InScope.Void() {

			@Override
			public void process(IScope scope) {
				init(scope);
				outputManager.addOutput(InspectDisplayOutput.this);
				schedule();
				open();
				update();
			}
		});

	}

	@Override
	public boolean isUnique() {
		return target != INSPECT_DYNAMIC;
	}

	@Override
	public String getViewId() {
		switch (target) {
			case INSPECT_DYNAMIC:
				return GuiUtils.DYNAMIC_VIEW_ID;
			case INSPECT_AGENT:
				return GuiUtils.AGENT_VIEW_ID;
			case INSPECT_SPECIES:
				return GuiUtils.SPECIES_VIEW_ID;
			default:
				return GuiUtils.AGENT_VIEW_ID;
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
