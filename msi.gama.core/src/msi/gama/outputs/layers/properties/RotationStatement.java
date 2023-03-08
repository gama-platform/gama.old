/*******************************************************************************************************
 *
 * RotationStatement.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
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
import msi.gama.outputs.layers.AbstractLayerStatement.OpenGLSpecificLayerValidator;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.IType;

/**
 * The Class CameraDefinition.
 */
@symbol (
		name = IKeyword.ROTATION,
		kind = ISymbolKind.LAYER,
		with_sequence = false,
		unique_in_context = true,
		concept = { IConcept.CAMERA, IConcept.DISPLAY, IConcept.THREED })
@inside (
		symbols = IKeyword.DISPLAY)
@facets (
		value = {

				@facet (
						name = IKeyword.DYNAMIC,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("If true, the rotation is applied every step. Default is false.")),
				@facet (
						name = IKeyword.LOCATION,
						type = IType.POINT,
						optional = true,
						doc = @doc ("Allows to define the center of the rotation. Defaut value is not specified is the center of mass of the world (i.e. {width/2, height/2, max(width, height) / 2})")),
				@facet (
						name = "axis",
						type = IType.POINT,
						optional = true,
						doc = @doc ("The axis of rotation, defined by a vector. Default is {0,0,1} (rotation around the z axis)"
								+ "This facet can be complemented by 'distance:' and/or 'location:' to specify from where the target is looked at. If 'target:' is not defined, the default target is the centroid of the world shape. ")),
				@facet (
						name = "angle",
						type = { IType.FLOAT, IType.INT },
						optional = false,
						doc = @doc ("Defines the angle of rotation around the axis. No default defined. ")) },
		omissible = "angle")
@doc (
		value = "`" + IKeyword.CAMERA
				+ "` allows the modeler to define a camera. The display will then be able to choose among the camera defined (either within this statement or globally in GAMA) in a dynamic way. "
				+ "Several preset cameras are provided and accessible in the preferences (to choose the default) or in GAML using the keywords #from_above, #from_left, #from_right, #from_up_right, #from_up_left, #from_front, #from_up_front."
				+ "These cameras are unlocked (so that they can be manipulated by the user), look at the center of the world from a symbolic position, and the distance between this position and the target is equal to the maximum of the width and height of the world's shape. "
				+ "These preset cameras can be reused when defining new cameras, since their names can become symbolic positions for them. For instance: camera 'my_camera' location: #from_top distance: 10; will lower (or extend) the distance between the camera and the center of the world to 10. "
				+ "camera 'my_camera' locked: true location: #from_up_front target: people(0); will continuously follow the first agent of the people species from the up-front position. ",
		see = { IKeyword.DISPLAY, IKeyword.AGENTS, IKeyword.CHART, IKeyword.EVENT, "graphics", IKeyword.GRID_LAYER,
				IKeyword.IMAGE, IKeyword.SPECIES_LAYER, })
@validator (OpenGLSpecificLayerValidator.class)
public class RotationStatement extends AbstractLayerStatement {

	/** The definition. */
	final RotationDefinition definition;

	/**
	 * Instantiates a new camera statement.
	 *
	 * @param desc
	 *            the desc
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public RotationStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		definition = new RotationDefinition(this);
	}

	@Override
	public LayerType getType(final LayeredDisplayOutput output) {
		return LayerType.ROTATION;
	}

	@Override
	protected boolean _init(final IScope scope) {
		definition.refresh(scope);
		return true;
	}

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
	public RotationDefinition getDefinition() { return definition; }
}
