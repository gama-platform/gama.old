package idees.gaml.operators;

import org.opengis.referencing.FactoryException;

import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class Projection {
	
	@operator(value = {  "CRS_transform" }, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(usages = { @usage(value = "returns the geometry corresponding to the transfomartion of the given geometry by the current CRS (Coordinate Reference System), the one corresponding to the world's agent one",
			examples = { @example(value = "CRS_transform(shape)",
				equals = "a geometry corresponding to the agent geometry transformed into the current CRS",
				test = false) }) })
		public static
			IShape transform_CRS(final IScope scope, final IShape g) {
			IProjection gis = scope.getSimulationScope().getProjectionFactory().getWorld();
			if (gis == null) {
				return g.copy(scope);
			}
			IShape s = new GamaShape(gis.inverseTransform(g.getInnerGeometry()));
			if (g instanceof ILocation)
				return s.getLocation();
			return s;
		}
	
	@operator(value = {  "to_GAMA_CRS" }, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(usages = { @usage(value = "returns the geometry corresponding to the transfomartion of the given geometry to the GAMA CRS (Coordinate Reference System) assuming the given geometry is referenced by the current CRS, the one corresponding to the world's agent one",
			examples = { @example(value = "to_GAMA_CRS({121,14})",
				equals = "a geometry corresponding to the agent geometry transformed into the GAMA CRS",
				test = false) }) })
		public static
			IShape to_GAMA_CRS(final IScope scope, final IShape g ) {
			IProjection gis = scope.getSimulationScope().getProjectionFactory().getWorld();
			if (gis == null) {
				return g.copy(scope);
			}
			IShape s = new GamaShape(gis.transform(g.getInnerGeometry()));
			if (g instanceof ILocation)
				return s.getLocation();
			return s;
		}
	
	@operator(value = {  "to_GAMA_CRS" }, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(usages = { @usage(value = "returns the geometry corresponding to the transfomartion of the given geometry to the GAMA CRS (Coordinate Reference System) assuming the given geometry is referenced by given CRS",
			examples = { @example(value = "to_GAMA_CRS({121,14}, \"EPSG:4326\")",
				equals = "a geometry corresponding to the agent geometry transformed into the GAMA CRS",
				test = false) }) })
		public static
			IShape to_GAMA_CRS(final IScope scope, final IShape g, final String code ) {
			IProjection gis;
			try {
				gis = scope.getSimulationScope().getProjectionFactory().forSavingWith(code);
			} catch (FactoryException e) {
				throw GamaRuntimeException.error("The code " + code +
						" does not correspond to a known EPSG code", scope);
			}
			if (gis == null) {
				return g.copy(scope);
			}
			IShape s = new GamaShape(gis.transform(g.getInnerGeometry()));
			if (g instanceof ILocation)
				return s.getLocation();
			return s;
		}
	
	@operator(value = {  "CRS_transform" }, category = { IOperatorCategory.SPATIAL,
			IOperatorCategory.SP_TRANSFORMATIONS })
		@doc(usages = { @usage(value = "returns the geometry corresponding to the transfomartion of the given geometry by the left operand CRS (Coordinate Reference System)",
			examples = { @example(value = "shape CRS_transform(\"EPSG:4326\")",
				equals = "a geometry corresponding to the agent geometry transformed into the EPSG:4326 CRS",
				test = false) }) })
		public static
			IShape transform_CRS(final IScope scope, final IShape g, final String code) {
			IProjection gis;
			try {
				gis = scope.getSimulationScope().getProjectionFactory().forSavingWith(code);
			} catch (FactoryException e) {
				throw GamaRuntimeException.error("The code " + code +
						" does not correspond to a known EPSG code", scope);
			}
			if (gis == null) {
				return g.copy(scope);
			}
			IShape s = new GamaShape(gis.inverseTransform(g.getInnerGeometry()));
			if (g instanceof ILocation)
				return s.getLocation();
			return s;
		}
}
