/*******************************************************************************************************
 *
 * msi.gama.common.interfaces.IDocManager.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import org.eclipse.emf.ecore.EObject;

import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;

// Internal interface instantiated by XText
public interface IDocManager {

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

	void document(IDescription description);

	IGamlDescription getGamlDocumentation(EObject o);

	IGamlDescription getGamlDocumentation(IGamlDescription o);

	void setGamlDocumentation(final EObject object, final IGamlDescription description, boolean replace, boolean force);

	default void setGamlDocumentation(final EObject object, final IGamlDescription description, final boolean replace) {
		setGamlDocumentation(object, description, replace, false);
	}

	void addCleanupTask(ModelDescription model);

	IDocManager NULL = new NullImpl();

}