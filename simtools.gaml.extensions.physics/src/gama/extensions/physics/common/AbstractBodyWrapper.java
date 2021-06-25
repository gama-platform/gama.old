package gama.extensions.physics.common;

import msi.gama.metamodel.agent.IAgent;

public abstract class AbstractBodyWrapper<WorldType, BodyType, ShapeType, VectorType>
		implements IBody<WorldType, BodyType, ShapeType, VectorType> {
	// Between GAMA coordinates and engines coordinates, some discrepancies
	// may exist (esp. on spheres, for instance)
	protected final VectorType aabbTranslation = toVector(null);
	protected final VectorType visualTranslation = toVector(null);
	public final boolean noNotification, isStatic;
	public final BodyType body;
	public final IAgent agent;

	public AbstractBodyWrapper(final IAgent agent, final IPhysicalWorld<WorldType, ShapeType, VectorType> world) {
		this.noNotification = this.noContactNotificationWanted(agent);
		this.agent = agent;
		isStatic = agent.getSpecies().implementsSkill(STATIC_BODY);
		ShapeType shape = world.getShapeConverter().convertAndTranslate(agent, aabbTranslation, visualTranslation);
		body = createAndInitializeBody(shape, world.getWorld());
		agent.setAttribute(BODY, this);
	}

	@Override
	public final boolean isNoNotification() {
		return noNotification;
	}

	@Override
	public final IAgent getAgent() {
		return agent;
	}

	@Override
	public final BodyType getBody() {
		return body;
	}

}
