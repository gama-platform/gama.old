/*******************************************************************************************************
 *
 * LightStatement.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers.properties;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.layers.AbstractLayerStatement;
import msi.gama.outputs.layers.properties.LightStatement.LightStatementValidator;
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
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

/**
 * The Class LightStatement.
 */
@symbol (
		name = "light",
		kind = ISymbolKind.LAYER,
		with_sequence = true,
		concept = { IConcept.LIGHT, IConcept.THREED })
@inside (
		symbols = IKeyword.DISPLAY)
@validator (LightStatementValidator.class)
@facets (
		omissible = IKeyword.NAME,
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.STRING,
				optional = false,
				doc = @doc ("The name of the light source, must be unique (otherwise the last definition prevails). "
						+ "Will be used to populate a menu where light sources can be easily turned on and off. Special names can be used:"
						+ "Using the special constant #ambient will allow to redefine or control the ambient light intensity and presence"
						+ "Using the special constant #default will replace the default directional light of the surrounding display")),
				@facet (
						name = IKeyword.POSITION,
						type = IType.POINT,
						optional = true,
						doc = @doc (
								deprecated = "Use 'location:' instead",
								value = "")),
				@facet (
						name = IKeyword.LOCATION,
						type = IType.POINT,
						optional = true,
						doc = @doc ("the location of the light (only for point and spot light) in model coordinates. Default is {0,0,20}")),
				@facet (
						name = IKeyword.TYPE,
						type = IType.STRING,
						optional = true,
						doc = @doc ("the type of light to create. A value among {#point, #direction, #spot}")),
				@facet (
						name = IKeyword.DIRECTION,
						type = IType.POINT,
						optional = true,
						doc = @doc ("the direction of the light (only for direction and spot light). (default value : {0.5,0.5,-1})")),
				@facet (
						name = "spot_angle",
						type = IType.FLOAT,
						optional = true,
						doc = @doc (
								deprecated = "use 'angle:' instead",
								value = "")),
				@facet (
						name = IKeyword.COLOR,
						type = { IType.INT, IType.COLOR },
						optional = true,
						doc = @doc (
								deprecated = "use 'intensity:' instead",
								value = "")),
				@facet (
						name = "draw_light",
						type = { IType.BOOL },
						optional = true,
						doc = @doc (
								deprecated = "use 'show:' instead",
								value = "")),
				@facet (
						name = IKeyword.UPDATE,
						type = { IType.BOOL },
						optional = true,
						doc = @doc (
								deprecated = "use 'dynamic:' instead",
								value = "")),
				@facet (
						name = "angle",
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
						doc = @doc ("the quadratic attenuation of the positionnal light. (default value : 0)")),
				@facet (
						name = "active",
						type = IType.BOOL,
						optional = true,
						doc = @doc ("a boolean expression telling if the light is on or off. (default value if not specified : true)")),
				@facet (
						name = "intensity",
						type = { IType.INT, IType.COLOR },
						optional = true,
						doc = @doc ("an int / rgb / rgba value to specify either the color+intensity of the light or simply its intensity. (default value if not specified : (127,127,127,255) ).")),
				@facet (
						name = "show",
						type = { IType.BOOL },
						optional = true,
						doc = @doc ("If true, draws the light source. (default value if not specified : false).")),
				@facet (
						name = IKeyword.DYNAMIC,
						type = { IType.BOOL },
						optional = true,
						doc = @doc ("specify if the parameters of the light need to be updated every cycle or treated as constants. (default value : true).")) })
@doc (
		value = "`light` allows to define diffusion lights in your 3D display. They must be given a name, which will help track them in the UI. Two names have however special meanings: #ambient, "
				+ "which designates the ambient luminosity and color of the scene (with a default intensity of (127,127,127,255)) and #default, "
				+ "which designates the default directional light applied to a scene (with a default medium intensity of (127,127,127,255) in the direction given by (0.5,0.5,1)). Redefining a light named #ambient or #regular "
				+ "will then modify these default lights (for example changing their color or deactivating them). To be more precise, and given all the default values of the facets, the existence of these two lights is effectively equivalent to redefining:"
				+ "light #ambient intensity: 127; light #default type: #direction intensity: 127 direction: {0.5,0.5,-1};",
		usages = { @usage (
				value = "The general syntax is:",
				examples = { @example (
						value = "light 1 type:point position:{20,20,20} color:255, linear_attenuation:0.01 quadratic_attenuation:0.0001 draw_light:true update:false",
						isExecutable = false),
						@example (
								value = "light 'spot1' type: #spot location:{20,20,20} direction:{0,0,-1} color:255 angle:25 linear_attenuation:0.01 quadratic_attenuation:0.0001 draw:true dynamic: false",
								isExecutable = false),
						@example (
								value = "light 'point2' type: #point direction:{1,1,-1} color:255 draw:true dynamic: false",
								isExecutable = false) }) },
		see = { IKeyword.DISPLAY })
public class LightStatement extends AbstractLayerStatement {

	/**
	 * The Class LightStatementValidator.
	 */
	public static class LightStatementValidator implements IDescriptionValidator<IDescription> {

		/**
		 * Method validate()
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription desc) {

			// Taking care of deprecations
			swap(desc, IKeyword.POSITION, IKeyword.LOCATION);
			swap(desc, IKeyword.COLOR, "intensity");
			swap(desc, "spot_angle", IKeyword.ANGLE);
			swap(desc, IKeyword.UPDATE, IKeyword.DYNAMIC);
			swap(desc, "draw_light", "show");

			final IExpressionDescription position = desc.getFacet(IKeyword.LOCATION);
			final IExpressionDescription direction = desc.getFacet(IKeyword.DIRECTION);
			final IExpressionDescription spotAngle = desc.getFacet(IKeyword.ANGLE);
			final IExpressionDescription linearAttenuation = desc.getFacet(IKeyword.LINEAR_ATTENUATION);
			final IExpressionDescription quadraticAttenuation = desc.getFacet(IKeyword.QUADRATIC_ATTENUATION);

			final IExpression spec = desc.getFacetExpr(IKeyword.TYPE);
			if (spec != null && spec.isConst()) {
				final String type = spec.literalValue();
				// light type direction
				switch (type) {
					case ILightDefinition.point: {
						if (direction != null) {
							desc.warning("a point light has no direction (only a position)", GENERAL);
						}
						if (spotAngle != null) {
							desc.warning("a point light has no spot angle (only a spot light does !)", GENERAL);
						}
						break;
					}
					case ILightDefinition.spot:
						break;
					case ILightDefinition.direction: {
						if (position != null) {
							desc.warning("a direction light has no position (only a direction)", GENERAL);
						}
						if (linearAttenuation != null) { desc.error("a direction light has no attenuation", GENERAL); }
						if (quadraticAttenuation != null) {
							desc.warning("a direction light has no attenuation", GENERAL);
						}
						break;
					}
					case ILightDefinition.ambient:
						break;
				}

			}
		}

	}

	/** The definition. */
	final LightDefinition definition;

	/**
	 * Instantiates a new light statement.
	 *
	 * @param desc
	 *            the desc
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public LightStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		definition = new LightDefinition(this);
	}

	/**
	 * Inits the.
	 *
	 * @param scope
	 *            the scope
	 * @return true, if successful
	 */
	@Override
	protected boolean _init(final IScope scope) {
		definition.refresh(scope);
		return true;
	}

	/**
	 * Gets the type.
	 *
	 * @param output
	 *            the output
	 * @return the type
	 */
	@Override
	public LayerType getType(final LayeredDisplayOutput output) {
		return LayerType.LIGHT;
	}

	/**
	 * Step.
	 *
	 * @param scope
	 *            the scope
	 * @return true, if successful
	 */
	@Override
	protected boolean _step(final IScope scope) {
		definition.refresh(scope);
		return true;
	}

	/**
	 * Gets the definition.
	 *
	 * @return the definition
	 */
	public LightDefinition getDefinition() { return definition; }

}
