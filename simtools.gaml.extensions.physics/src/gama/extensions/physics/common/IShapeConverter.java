package gama.extensions.physics.common;

import gama.extensions.physics.gaml.PhysicalSimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gaml.skills.GridSkill.IGridAgent;

public interface IShapeConverter<ShapeType, VectorType> extends IPhysicalConstants {

	default float computeDepth(final IAgent agent) {
		// Special case for grids, where the grid_value is used as the elevation
		float result = 0f;
		if (agent.getSpecies().isGrid()) {
			result = (float) ((IGridAgent) agent).getValue();
		} else {
			Double d = agent.getDepth();
			result = d == null ? 0f : d.floatValue();
		}
		// Depth cannot be negative as it is used for the half-extents of shapes
		return result < 0 ? 0f : result;
	}

	default IShape.Type computeType(final IAgent agent) {
		if (agent.getSpecies().isGrid()) return IShape.Type.BOX;
		return agent.getGeometricalType();
	}

	default ShapeType convertAndTranslate(final IAgent agent, final VectorType aabbTranslation,
			final VectorType visualTranslation) {
		IShape.Type type = computeType(agent);
		float depth = computeDepth(agent);
		computeTranslation(agent, type, depth, aabbTranslation, visualTranslation);
		if (agent instanceof PhysicalSimulationAgent) {
			GamaFloatMatrix terrain = ((PhysicalSimulationAgent) agent).getTerrain();
			if (terrain != null)
				return convertTerrain(agent.getScope(), terrain, agent.getWidth(), agent.getHeight(), depth);
		}
		return convertShape(agent.getGeometry(), type, depth);

	}

	void computeTranslation(final IAgent agent, final IShape.Type type, final float depth,
			final VectorType aabbTranslation, final VectorType visualTranslation);

	ShapeType convertShape(final IShape shape, final IShape.Type type, final float depth);

	ShapeType convertTerrain(final IScope scope, final GamaFloatMatrix field, final Double width, final Double height,
			final float depth);

	VectorType toVector(final GamaPoint v);

}
