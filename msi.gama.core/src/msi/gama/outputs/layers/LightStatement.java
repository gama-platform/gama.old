/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.LightStatement.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.layers.LightStatement.LightStatementValidator;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AspectStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@symbol (
		name = "light",
		kind = ISymbolKind.LAYER,
		with_sequence = true,
		concept = { IConcept.LIGHT, IConcept.THREED })
@inside (
		symbols = IKeyword.DISPLAY)
@validator (LightStatementValidator.class)
@facets (
		omissible = IKeyword.ID,
		value = { @facet (
				name = IKeyword.ID,
				type = IType.INT,
				optional = false,
				doc = @doc ("a number from 1 to 7 to specify which light we are using")),
				@facet (
						name = IKeyword.POSITION,
						type = IType.POINT,
						optional = true,
						doc = @doc ("the position of the light (only for point and spot light). (default value : {0,0,1})")),
				@facet (
						name = IKeyword.TYPE,
						type = IType.LABEL,
						optional = true,
						doc = @doc ("the type of light to create. A value among {point, direction, spot}. (default value : direction)")),
				@facet (
						name = IKeyword.DIRECTION,
						type = IType.POINT,
						optional = true,
						doc = @doc ("the direction of the light (only for direction and spot light). (default value : {0.5,0.5,-1})")),
				@facet (
						name = IKeyword.SPOT_ANGLE,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the angle of the spot light in degree (only for spot light). (default value : 45)")),
				@facet (
						name = IKeyword.LINEAR_ATTENUATION,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the linear attenuation of the positionnal light. (default value : 0)")),
				@facet (
						name = IKeyword.QUADRATIC_ATTENUATION,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the linear attenuation of the positionnal light. (default value : 0)")),
				@facet (
						name = "active",
						type = IType.BOOL,
						optional = true,
						doc = @doc ("a boolean expression telling if you want this light to be switch on or not. (default value : true)")),
				@facet (
						name = IKeyword.COLOR,
						type = { IType.INT, IType.COLOR },
						optional = true,
						doc = @doc ("an int / rgb / rgba value to specify the color and the intensity of the light. (default value : (127,127,127,255) ).")),
				@facet (
						name = IKeyword.DRAW_LIGHT,
						type = { IType.BOOL },
						optional = true,
						doc = @doc ("draw or not the light. (default value : false).")),
				@facet (
						name = IKeyword.UPDATE,
						type = { IType.BOOL },
						optional = true,
						doc = @doc ("specify if the light has to be updated. (default value : true).")) })
@doc (
		value = "`light` allows to define diffusion lights in your 3D display.",
		usages = { @usage (
				value = "The general syntax is:",
				examples = { @example (
						value = "light 1 type:point position:{20,20,20} color:255, linear_attenuation:0.01 quadratic_attenuation:0.0001 draw_light:true update:false",
						isExecutable = false),
						@example (
								value = "light 2 type:spot position:{20,20,20} direction:{0,0,-1} color:255 spot_angle:25 linear_attenuation:0.01 quadratic_attenuation:0.0001 draw_light:true update:false",
								isExecutable = false),
						@example (
								value = "light 3 type:point direction:{1,1,-1} color:255 draw_light:true update:false",
								isExecutable = false) }) },
		see = { IKeyword.DISPLAY })
public class LightStatement extends AbstractLayerStatement {

	public static class LightStatementValidator implements IDescriptionValidator<IDescription> {

		/**
		 * Method validate()
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription desc) {

			final IExpressionDescription position = desc.getFacet(IKeyword.POSITION);
			final IExpressionDescription direction = desc.getFacet(IKeyword.DIRECTION);
			final IExpressionDescription spotAngle = desc.getFacet(IKeyword.SPOT_ANGLE);
			final IExpressionDescription linearAttenuation = desc.getFacet(IKeyword.LINEAR_ATTENUATION);
			final IExpressionDescription quadraticAttenuation = desc.getFacet(IKeyword.QUADRATIC_ATTENUATION);

			final IExpression idExp = desc.getFacetExpr(IKeyword.ID);
			if (idExp != null && idExp.isConst()) {
				final int id = Cast.asInt(null, idExp.literalValue());
				if (id <= 0 || id > 7) {
					desc.error(
							"'id' facet accept values between 1 and 7. (the light \"0\" is only used for the ambient light, which can be changed through the \"ambient_light\" display facet)",
							IGamlIssue.GENERAL);
				}
			}

			final IExpression spec = desc.getFacetExpr(IKeyword.TYPE);
			if (spec != null && spec.isConst()) {
				final String typeString = spec.literalValue();
				// light type direction
				if (typeString.compareTo("direction") == 0) {
					if (position != null) {
						desc.error("a direction light has no position (only a direction)", IGamlIssue.GENERAL);
					}
					if (linearAttenuation != null) {
						desc.error("a direction light has no attenuation", IGamlIssue.GENERAL);
					}
					if (quadraticAttenuation != null) {
						desc.error("a direction light has no attenuation", IGamlIssue.GENERAL);
					}
				}
				// light type spot
				else if (typeString.compareTo("spot") == 0) {}
				// light type point
				else {
					if (direction != null) {
						desc.error("a point light has no direction (only a position)", IGamlIssue.GENERAL);
					}
					if (spotAngle != null) {
						desc.error("a point light has no spot angle (only a spot light does !)", IGamlIssue.GENERAL);
					}
				}
			}
		}

	}

	AspectStatement aspect;
	static int i;
	boolean update = true;

	public LightStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		layerToCreate = false;
		final IDescription d = DescriptionFactory.create(IKeyword.ASPECT, desc, IKeyword.NAME, "graphic_aspect" + i++);
		aspect = new AspectStatement(d);
	}

	public AspectStatement getAspect() {
		return aspect;
	}

	@Override
	protected boolean _init(final IScope scope) {
		if (getFacetValue(scope, IKeyword.UPDATE) != null) {
			update = Cast.asBool(scope, getFacetValue(scope, IKeyword.UPDATE));
		}
		setLightProperties(scope);
		return true;
	}

	@Override
	public LayerType getType(final LayeredDisplayOutput output) {
		return LayerType.LIGHT;
	}

	@Override
	protected boolean _step(final IScope scope) {
		if (update) {
			setLightProperties(scope);
		}
		// TODO Auto-generated method stub
		return true;
	}

	private void setLightProperties(final IScope scope) {
		final int lightId = Cast.asInt(scope, getFacetValue(scope, IKeyword.ID));

		if (getFacetValue(scope, "active") != null) {
			getLayeredDisplayData().setLightActive(lightId, Cast.asBool(scope, getFacetValue(scope, "active")));
		} else {
			getLayeredDisplayData().setLightActive(lightId, true);
		}
		if (getFacetValue(scope, IKeyword.TYPE) != null) {
			getLayeredDisplayData().setLightType(lightId, Cast.asString(scope, getFacetValue(scope, IKeyword.TYPE)));
		}
		if (getFacetValue(scope, IKeyword.POSITION) != null) {
			getLayeredDisplayData().setLightPosition(lightId,
					(GamaPoint) Cast.asPoint(scope, getFacetValue(scope, IKeyword.POSITION)));
		}
		if (getFacetValue(scope, IKeyword.DIRECTION) != null) {
			getLayeredDisplayData().setLightDirection(lightId,
					(GamaPoint) Cast.asPoint(scope, getFacetValue(scope, IKeyword.DIRECTION)));
		}
		final IExpression expr = getFacet(IKeyword.COLOR);
		if (expr != null) {
			if (expr.getGamlType().equals(Types.COLOR)) {
				getLayeredDisplayData().setDiffuseLightColor(lightId, Cast.asColor(scope, expr.value(scope)));
			} else {
				final int meanValue = Cast.asInt(scope, expr.value(scope));
				final GamaColor color = new GamaColor(meanValue, meanValue, meanValue, 255);
				getLayeredDisplayData().setDiffuseLightColor(lightId, color);
			}
		}
		if (getFacetValue(scope, IKeyword.LINEAR_ATTENUATION) != null) {
			getLayeredDisplayData().setLinearAttenuation(lightId,
					(float) (double) Cast.asFloat(scope, getFacetValue(scope, IKeyword.LINEAR_ATTENUATION)));
		}
		if (getFacetValue(scope, IKeyword.QUADRATIC_ATTENUATION) != null) {
			getLayeredDisplayData().setQuadraticAttenuation(lightId,
					(float) (double) Cast.asFloat(scope, getFacetValue(scope, IKeyword.QUADRATIC_ATTENUATION)));
		}
		if (getFacetValue(scope, IKeyword.SPOT_ANGLE) != null) {
			getLayeredDisplayData().setSpotAngle(lightId,
					(float) (double) Cast.asFloat(scope, getFacetValue(scope, IKeyword.SPOT_ANGLE)));
		}
		if (getFacetValue(scope, IKeyword.DRAW_LIGHT) != null) {
			getLayeredDisplayData().setDrawLight(lightId,
					Cast.asBool(scope, getFacetValue(scope, IKeyword.DRAW_LIGHT)));
		}
	}

}
