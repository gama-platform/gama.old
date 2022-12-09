/*******************************************************************************************************
 *
 * IDocManager.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import org.eclipse.emf.ecore.EObject;

import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;

/**
 * The Interface IDocManager.
 */
// Internal interface instantiated by XText
public interface IDocManager {

	/**
	 * The Class NullImpl.
	 */
	public static class NullImpl implements IDocManager {

		@Override
		public void document(final IDescription description) {}

		@Override
		public IGamlDescription getGamlDocumentation(final EObject o) {
			return null;
		}

		@Override
		public IGamlDescription getGamlDocumentation(final IGamlDescription o) {
			return null;
		}

		@Override
		public void addCleanupTask(final ModelDescription model) {}

		@Override
		public void setGamlDocumentation(final EObject object, final IGamlDescription description,
				final boolean replace, final boolean force) {}

	}

	/**
	 * Document.
	 *
	 * @param description the description
	 */
	void document(IDescription description);

	/**
	 * Gets the gaml documentation.
	 *
	 * @param o the o
	 * @return the gaml documentation
	 */
	IGamlDescription getGamlDocumentation(EObject o);

	/**
	 * Gets the gaml documentation.
	 *
	 * @param o the o
	 * @return the gaml documentation
	 */
	IGamlDescription getGamlDocumentation(IGamlDescription o);

	/**
	 * Sets the gaml documentation.
	 *
	 * @param object the object
	 * @param description the description
	 * @param replace the replace
	 * @param force the force
	 */
	void setGamlDocumentation(final EObject object, final IGamlDescription description, boolean replace, boolean force);

	/**
	 * Sets the gaml documentation.
	 *
	 * @param object the object
	 * @param description the description
	 * @param replace the replace
	 */
	default void setGamlDocumentation(final EObject object, final IGamlDescription description, final boolean replace) {
		setGamlDocumentation(object, description, replace, false);
	}

	/**
	 * Adds the cleanup task.
	 *
	 * @param model the model
	 */
	void addCleanupTask(ModelDescription model);

	/** The null. */
	IDocManager NULL = new NullImpl();

}