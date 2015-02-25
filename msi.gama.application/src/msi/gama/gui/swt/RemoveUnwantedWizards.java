/**
 * Created by drogoul, 21 nov. 2014
 * 
 */
package msi.gama.gui.swt;

import java.util.*;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.WorkbenchWizardElement;
import org.eclipse.ui.internal.wizards.AbstractExtensionWizardRegistry;
import org.eclipse.ui.wizards.*;

/**
 * Class RemoveUnwantedWizards.
 * 
 * @author drogoul
 * @since 21 nov. 2014
 * 
 */
public class RemoveUnwantedWizards {

	private static List<String> CATEGORIES_TO_REMOVE = Arrays.asList(new String[] { "org.eclipse.jdt.debug.ui.java",
		"org.eclipse.jdt.junit", "org.eclipse.pde.PDE", /* "org.eclipse.ui.Basic", */
		"org.eclipse.emf.codegen.ecore.ui.wizardCategory", "org.eclipse.jdt.ui.java" });

	static void run() {
		AbstractExtensionWizardRegistry r =
			(AbstractExtensionWizardRegistry) PlatformUI.getWorkbench().getNewWizardRegistry();
		IWizardCategory[] categories = r.getRootCategory().getCategories();
		for ( final IWizardDescriptor wizard : getAllWizards(categories) ) {
			final String id = wizard.getCategory().getId();
			// System.out.println("Wizard " + wizard.getId());
			if ( CATEGORIES_TO_REMOVE.contains(id) ) {
				System.out.println("Removing 'new' wizard " + wizard.getId() + " in category " + id);
				final WorkbenchWizardElement element = (WorkbenchWizardElement) wizard;
				r.removeExtension(element.getConfigurationElement().getDeclaringExtension(), new Object[] { element });
			}
		}
		r = (AbstractExtensionWizardRegistry) PlatformUI.getWorkbench().getImportWizardRegistry();
		categories = r.getRootCategory().getCategories();
		for ( final IWizardDescriptor wizard : getAllWizards(categories) ) {
			final String id = wizard.getCategory().getId();
			// System.out.println("Wizard " + wizard.getId());
			if ( CATEGORIES_TO_REMOVE.contains(id) ) {
				System.out.println("Removing 'import' wizard " + wizard.getId() + " in category " + id);
				final WorkbenchWizardElement element = (WorkbenchWizardElement) wizard;
				r.removeExtension(element.getConfigurationElement().getDeclaringExtension(), new Object[] { element });
			}
		}

	}

	static private IWizardDescriptor[] getAllWizards(final IWizardCategory[] categories) {
		final List<IWizardDescriptor> results = new ArrayList<IWizardDescriptor>();
		for ( final IWizardCategory wizardCategory : categories ) {

			results.addAll(Arrays.asList(wizardCategory.getWizards()));
			results.addAll(Arrays.asList(getAllWizards(wizardCategory.getCategories())));
		}
		return results.toArray(new IWizardDescriptor[0]);
	}

}
