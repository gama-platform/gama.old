package msi.gama.common.interfaces;

/**
 * Represent GAMA stepables that generate their own scope
 *
 * @author A. Drogoul
 *
 */
public interface IScopedStepable extends IScoped, IStepable {

	default boolean step() {
		return getScope().step(this).passed();
	}

	default boolean init() {
		return getScope().init(this).passed();
	}

}
