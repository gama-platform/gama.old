/**
 * Created by drogoul, 20 avr. 2012
 * 
 */
package msi.gama.lang.gaml.trials;

import java.util.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.resource.*;

/**
 * The class GamlDescriptionUtils.
 * 
 * @author drogoul
 * @since 20 avr. 2012
 * 
 */
@Deprecated
public class GamlDescriptionUtils extends DescriptionUtils {

	@Override
	public Set<URI> collectOutgoingReferences(final IResourceDescription description) {
		Set<URI> uris = new HashSet(super.collectOutgoingReferences(description));
		// uris.addAll(((GamlResourceDescription) description).getImports());
		// GuiUtils.debug("Outgoing references of " + description.getURI().lastSegment() + ": " + uris);
		return uris;
	}
}
