/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.CameraDefinition.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.outputs.LayeredDisplayOutput;
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
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

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
				doc = @doc ("The name of the camera")),
				@facet (
						name = IKeyword.LOCATION,
						type = IType.POINT,
						optional = true,
						doc = @doc ("The location of the camera in the world")),
				@facet (
						name = IKeyword.LOOK_AT,
						type = IType.POINT,
						optional = true,
						doc = @doc ("The location that the camera is looking")),
				@facet (
						name = IKeyword.UP_VECTOR,
						type = IType.POINT,
						optional = true,
						doc = @doc ("The up-vector of the camera.")) },
		omissible = IKeyword.NAME)
@doc (
		value = "`" + IKeyword.CAMERA
				+ "` allows the modeler to define a camera. The display will then be able to choose among the camera defined (either within this statement or globally in GAMA) in a dynamic way. ",
		see = { IKeyword.DISPLAY, IKeyword.AGENTS, IKeyword.CHART, IKeyword.EVENT, "graphics", IKeyword.GRID_POPULATION,
				IKeyword.IMAGE, IKeyword.POPULATION, })
public class CameraDefinition extends AbstractLayerStatement {

	final IExpression locationExpr, lookAtExpr, upVectorExpr;
	ILocation location, lookAt, upVector;

	public CameraDefinition(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		locationExpr = getFacet(IKeyword.LOCATION);
		lookAtExpr = getFacet(IKeyword.LOOK_AT);
		upVectorExpr = getFacet(IKeyword.UP_VECTOR);
	}

	@Override
	public LayerType getType(final LayeredDisplayOutput output) {
		return LayerType.OVERLAY;
	}

	@Override
	protected boolean _init(final IScope scope) {
		return true;
	}

	@Override
	protected boolean _step(final IScope scope) {
		location = locationExpr == null ? null : Cast.asPoint(scope, locationExpr.value(scope));
		lookAt = lookAtExpr == null ? null : Cast.asPoint(scope, lookAtExpr.value(scope));
		upVector = upVectorExpr == null ? null : Cast.asPoint(scope, upVectorExpr.value(scope));
		return true;
	}

}
