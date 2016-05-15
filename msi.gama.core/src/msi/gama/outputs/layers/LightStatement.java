package msi.gama.outputs.layers;

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.AbstractDisplayOutput;
import msi.gama.precompiler.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AspectStatement;
import msi.gaml.types.IType;

@symbol(name = "light", kind = ISymbolKind.LAYER, with_sequence = true, concept = { IConcept.LIGHT, IConcept.THREED })
@inside(symbols = IKeyword.DISPLAY)
@facets(
	value = { 
		@facet(name = IKeyword.ID, type = IType.INT, optional = false,
		doc = @doc("a number from 1 to 7 to specify which light we are using") ),
		@facet(name = IKeyword.POSITION, type = IType.POINT, optional = true,
		doc = @doc("the position of the light (only for point and spot light). (default value : {0,0,1})") ),
		@facet(name = IKeyword.TYPE, type = IType.LABEL, optional = true,
		doc = @doc("the type of light to create. A value among {point, direction, spot}. (default value : direction)") ),
		@facet(name = IKeyword.DIRECTION, type = IType.POINT, optional = true,
		doc = @doc("the direction of the light (only for direction and spot light). (default value : {0,0,-1})") ),
		@facet(name = IKeyword.SPOT_ANGLE, type = IType.FLOAT, optional = true,
		doc = @doc("the angle of the spot light in degree (only for spot light). (default value : 45)") ),
		@facet(name = IKeyword.LINEAR_ATTENUATION, type = IType.FLOAT, optional = true,
		doc = @doc("the linear attenuation of the positionnal light. (default vlaue : 0)") ),
		@facet(name = IKeyword.QUADRATIC_ATTENUATION, type = IType.FLOAT, optional = true,
		doc = @doc("the linear attenuation of the positionnal light. (default value : 0)") ),
		@facet(name = "active",	type = IType.BOOL, optional = true,
		doc = @doc("a boolean expression telling if you want this light to be switch on or not. (default value : true)") ),
		@facet(name = IKeyword.COLOR,
		type = { IType.INT, IType.COLOR }, optional = true,
		doc = @doc("an int / rgb / rgba value to specify the color and the intensity of the light. (default value : (255,255,255,255) ).") ),
		@facet(name = IKeyword.SPECULAR,
		type = { IType.INT, IType.COLOR }, optional = true,
		doc = @doc("an int / rgb / rgba value to specify the color and the intensity of the specular light. (default value : (255,255,255,255) ).") ) })
//@doc(
//	value = "`graphics` allows the modeler to freely draw shapes/geometries/texts without having to define a species. It works exactly like a species [Aspect161 aspect]: the draw statement can be used in the same way.",
//	usages = { @usage(value = "The general syntax is:",
//	examples = { @example(value = "display my_display {", isExecutable = false),
//		@example(value = "   graphics \"my new layer\" {", isExecutable = false),
//		@example(value = "      draw circle(5) at: {10,10} color: #red;", isExecutable = false),
//		@example(value = "      draw \"test\" at: {10,10} size: 20 color: #black;", isExecutable = false),
//		@example(value = "   }", isExecutable = false), @example(value = "}", isExecutable = false) }) },
//	see = { IKeyword.DISPLAY, IKeyword.AGENTS, IKeyword.CHART, IKeyword.EVENT, "graphics", IKeyword.GRID_POPULATION,
//		IKeyword.IMAGE, IKeyword.OVERLAY, IKeyword.POPULATION })
public class LightStatement extends AbstractLayerStatement {

	AspectStatement aspect;
	static int i;

	public LightStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		layerToCreate = false;
		IDescription d = DescriptionFactory.create(IKeyword.ASPECT, desc, IKeyword.NAME, "graphic_aspect" + i++);
		aspect = new AspectStatement(d);
	}

	public AspectStatement getAspect() {
		return aspect;
	}

	@Override
	protected boolean _init(IScope scope) {
		// TODO Auto-generated method stub
		int lightId = Cast.asInt(scope, getFacetValue(scope, IKeyword.ID));
		
		if (getFacetValue(scope, "active") != null) {
			getLayeredDisplayData().setLightActive(lightId,Cast.asBool(scope, getFacetValue(scope, "active")));
		}
		if (getFacetValue(scope, IKeyword.POSITION) != null) {
			getLayeredDisplayData().setLightPosition(lightId,(GamaPoint)Cast.asPoint(scope, getFacetValue(scope, IKeyword.POSITION)));
		}
		if (getFacetValue(scope, IKeyword.DIRECTION) != null) {
			getLayeredDisplayData().setLightDirection(lightId,(GamaPoint)Cast.asPoint(scope, getFacetValue(scope, IKeyword.DIRECTION)));
		}
		if (getFacetValue(scope, IKeyword.COLOR) != null) {
			getLayeredDisplayData().setDiffuseLightColor(lightId,Cast.asColor(scope, getFacetValue(scope, IKeyword.COLOR)));
		}
		if (getFacetValue(scope, IKeyword.SPECULAR) != null) {
			getLayeredDisplayData().setSpecularLightColor(lightId,Cast.asColor(scope, getFacetValue(scope, IKeyword.SPECULAR)));
		}
		if (getFacetValue(scope, IKeyword.LINEAR_ATTENUATION) != null) {
			getLayeredDisplayData().setLinearAttenuation(lightId,(float)(double)Cast.asFloat(scope, getFacetValue(scope, IKeyword.LINEAR_ATTENUATION)));
		}
		if (getFacetValue(scope, IKeyword.QUADRATIC_ATTENUATION) != null) {
			getLayeredDisplayData().setQuadraticAttenuation(lightId,(float)(double)Cast.asFloat(scope, getFacetValue(scope, IKeyword.QUADRATIC_ATTENUATION)));
		}
		if (getFacetValue(scope, IKeyword.SPOT_ANGLE) != null) {
			getLayeredDisplayData().setSpotAngle(lightId,(float)(double)Cast.asFloat(scope, getFacetValue(scope, IKeyword.SPOT_ANGLE)));
		}
		if (getFacetValue(scope, IKeyword.DRAW_LIGHT) != null) {
			getLayeredDisplayData().setDrawLight(lightId,Cast.asBool(scope, getFacetValue(scope, IKeyword.DRAW_LIGHT)));
		}
		return true;
	}

	@Override
	public short getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected boolean _step(IScope scope) {
		// TODO Auto-generated method stub
		return false;
	}

}
