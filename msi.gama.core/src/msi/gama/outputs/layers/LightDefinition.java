package msi.gama.outputs.layers;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.IType;

@symbol(name = "light", kind = ISymbolKind.LAYER, with_sequence = false, unique_in_context = false, concept = {
		IConcept.LIGHT, IConcept.THREED })
@inside(symbols = IKeyword.DISPLAY)
@facets(omissible = IKeyword.VALUE, value = {
		@facet(name = IKeyword.VALUE, type = IType.INT, optional = false, doc = @doc("a number from 1 to 7 to specify which light we are using")),
		@facet(name = "active", type = IType.BOOL, optional = true, doc = @doc("a boolean expression telling if you want this light to be switch on or not. (default value : true)")),
		@facet(name = IKeyword.COLOR, type = { IType.INT,
				IType.COLOR }, optional = true, doc = @doc("an int / rgb / rgba value to specify the color and the intensity of the light. (default value : (255,255,255,255) ).")) })
// @doc(
// value = "`graphics` allows the modeler to freely draw shapes/geometries/texts
// without having to define a species. It works exactly like a species
// [Aspect161 aspect]: the draw statement can be used in the same way.",
// usages = { @usage(value = "The general syntax is:",
// examples = { @example(value = "display my_display {", isExecutable = false),
// @example(value = " graphics \"my new layer\" {", isExecutable = false),
// @example(value = " draw circle(5) at: {10,10} color: #red;", isExecutable =
// false),
// @example(value = " draw \"test\" at: {10,10} size: 20 color: #black;",
// isExecutable = false),
// @example(value = " }", isExecutable = false), @example(value = "}",
// isExecutable = false) }) },
// see = { IKeyword.DISPLAY, IKeyword.AGENTS, IKeyword.CHART, IKeyword.EVENT,
// "graphics", IKeyword.GRID_POPULATION,
// IKeyword.IMAGE, IKeyword.OVERLAY, IKeyword.POPULATION })
public class LightDefinition extends AbstractLayerStatement {

	static int i;

	public LightDefinition(final IDescription desc) throws GamaRuntimeException {
		super(desc);

	}

	@Override
	protected boolean _init(final IScope scope) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public short getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected boolean _step(final IScope scope) {
		// TODO Auto-generated method stub
		return false;
	}

}
