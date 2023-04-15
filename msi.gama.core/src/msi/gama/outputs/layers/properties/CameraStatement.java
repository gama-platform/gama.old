/*******************************************************************************************************
 *
 * CameraStatement.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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
		name = IKeyword.CAMERA,
		kind = ISymbolKind.LAYER,
		with_sequence = false,
		unique_in_context = false,
		concept = { IConcept.CAMERA, IConcept.DISPLAY, IConcept.THREED })
@inside (
		symbols = IKeyword.DISPLAY)
@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.STRING,
				optional = false,
				doc = @doc ("The name of the camera. Will be used to populate a menu with the other camera presets. "
						+ "Can provide a value to the 'camera:' facet of the display, which specifies which camera to use."
						+ "Using the special constant #default will make it the default of the surrounding display")),

				@facet (
						name = IKeyword.DYNAMIC,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("If true, the location, distance and target are automatically recomputed every step. Default is false. When true, will also set 'locked' to true, to avoid interferences from users")),

				@facet (
						name = "distance",
						type = { IType.FLOAT },
						optional = true,
						doc = @doc ("If the 'location:' facet is not defined, defines the distance (in world units) that separates the camera from its target. "
								+ "If 'location:' is defined, especially if it is using a symbolic position, allows to specify the distance to keep from the target. "
								+ "If neither 'location:' or 'distance:' is defined, the default distance is the maximum between the width and the height of the world")),
				@facet (
						name = IKeyword.LOCATION,
						type = { IType.POINT, IType.STRING },
						optional = true,
						doc = @doc ("Allows to define the location of the camera in the world, i.e. from where it looks at its target. If 'distance:' is specified, the final location is translated on the target-camera axis to respect the distance. "
								+ "Can be a (possibly dynamically computed) point or a symbolic position (#from_above, #from_left, #from_right, #from_up_right, #from_up_left, #from_front, #from_up_front) that will be dynamically recomputed if the target moves"
								+ "If 'location:' is not defined, it will be that of the default camera (#from_top, #from_left...) defined in the preferences.")),
				@facet (
						name = IKeyword.TARGET,
						type = { IType.POINT, IType.AGENT, IType.GEOMETRY },
						optional = true,
						doc = @doc ("Allows to define the target of the camera (what does it look at). It can be a point (in world coordinates), a geometry or an agent, in which case its (possibly dynamic) location it used as the target. "
								+ "This facet can be complemented by 'distance:' and/or 'location:' to specify from where the target is looked at. If 'target:' is not defined, the default target is the centroid of the world shape. ")),
				@facet (
						name = "lens",
						type = { IType.FLOAT, IType.INT },
						optional = true,
						doc = @doc ("Allows to define the lens -- field of view in degrees -- of the camera. Between 0 and 360. Defaults to 45°")),
				@facet (
						name = "locked",
						type = IType.BOOL,
						optional = true,
						doc = @doc ("If true, the user cannot modify the camera location and target by interacting with the display. It is automatically set when the camera is dynamic, so that the display can 'follow' the coordinates; but it can also be used with fixed coordinates to 'focus' the display on a specific scene")), },
		omissible = IKeyword.NAME)
@doc (
		value = "`" + IKeyword.CAMERA
				+ "` allows the modeler to define a camera. The display will then be able to choose among the camera defined (either within this statement or globally in GAMA) in a dynamic way. "
				+ "Several preset cameras are provided and accessible in the preferences (to choose the default) or in GAML using the keywords #from_above, #from_left, #from_right, #from_up_right, #from_up_left, #from_front, #from_up_front, #isometric."
				+ "These cameras are unlocked (so that they can be manipulated by the user), look at the center of the world from a symbolic position, and the distance between this position and the target is equal to the maximum of the width and height of the world's shape. "
				+ "These preset cameras can be reused when defining new cameras, since their names can become symbolic positions for them. For instance: camera 'my_camera' location: #from_top distance: 10; will lower (or extend) the distance between the camera and the center of the world to 10. "
				+ "camera 'my_camera' locked: true location: #from_up_front target: people(0); will continuously follow the first agent of the people species from the up-front position. ",
		see = { IKeyword.DISPLAY, IKeyword.AGENTS, IKeyword.CHART, IKeyword.EVENT, "graphics", IKeyword.GRID_LAYER,
				IKeyword.IMAGE_LAYER, IKeyword.SPECIES_LAYER, })
@validator (OpenGLSpecificLayerValidator.class)
public class CameraStatement extends AbstractLayerStatement {

	/** The definition. */
	final CameraDefinition definition;

	/**
	 * Instantiates a new camera statement.
	 *
	 * @param desc
	 *            the desc
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public CameraStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		definition = new CameraDefinition(this);
	}

	@Override
	public LayerType getType(final LayeredDisplayOutput output) {
		return LayerType.CAMERA;
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
	public CameraDefinition getDefinition() { return definition; }
}
