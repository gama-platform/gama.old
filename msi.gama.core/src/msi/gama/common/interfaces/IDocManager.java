/*******************************************************************************************************
 *
 * msi.gama.common.interfaces.IDocManager.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
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
		public void setGamlDocumentation(final EObject object, final IGamlDescription description,
				final boolean replace) {}

		@Override
		public void addCleanupTask(final ModelDescription model) {}
	}

	public static final IDocManager NULL = new NullImpl();

	public static final String KEY = "Doc";

	public void document(IDescription description);

	public IGamlDescription getGamlDocumentation(EObject o);

	public IGamlDescription getGamlDocumentation(IGamlDescription o);

	public void setGamlDocumentation(final EObject object, final IGamlDescription description, boolean replace);

	public void addCleanupTask(ModelDescription model);

}