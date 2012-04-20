/**
 * Created by drogoul, 5 févr. 2012
 * 
 */
package msi.gama.lang.gaml.ui.hover;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.documentation.IEObjectDocumentationProvider;

public class GamlDocumentationProvider implements IEObjectDocumentationProvider {

	@Override
	public String getDocumentation(final EObject o) {
		StringBuilder sb = new StringBuilder();
		sb.append("This object is an instance of ").append(o.getClass().getSimpleName());
		if ( o.eContainer() != null ) {
			sb.append("<br>").append("Contained in an instance of ")
				.append(o.eContainer().getClass().getSimpleName());
		}
		sb.append("<br>");

		// if ( o instanceof Statement ) {
		// sb.append("Facets allowed: " +
		// DescriptionFactory.getModelFactory()
		// .getMetaDescriptionFor(null, ((Statement) o).getKey()).getPossibleFacets()
		// .keySet());
		// }
		return sb.toString();
	}
}