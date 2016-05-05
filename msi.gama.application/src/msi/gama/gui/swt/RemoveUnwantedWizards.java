/**
 * Created by drogoul, 21 nov. 2014
 * 
 */
package msi.gama.gui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.WorkbenchWizardElement;
import org.eclipse.ui.internal.wizards.AbstractExtensionWizardRegistry;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;

/**
 * Class RemoveUnwantedWizards.
 * 
 * @author drogoul
 * @since 21 nov. 2014
 * 
 */
public class RemoveUnwantedWizards {

	private static Set<String> CATEGORIES_TO_REMOVE = new HashSet(
		Arrays.asList(new String[] { "org.eclipse.pde.PDE", "org.eclipse.emf.codegen.ecore.ui.wizardCategory" }));

	private static Set<String> IDS_TO_REMOVE = new HashSet(
		Arrays.asList(new String[] { "org.eclipse.ui.wizards.new.project", "org.eclipse.equinox.p2.replication.import",
			"org.eclipse.equinox.p2.replication.importfrominstallation", "org.eclipse.team.ui.ProjectSetImportWizard",
			"org.eclipse.equinox.p2.replication.export", "org.eclipse.team.ui.ProjectSetExportWizard" }));

	static void run() {
		final List<IWizardCategory> cats = new ArrayList();
		AbstractExtensionWizardRegistry r =
			(AbstractExtensionWizardRegistry) PlatformUI.getWorkbench().getNewWizardRegistry();
		cats.addAll(Arrays.asList(r.getRootCategory().getCategories()));
		r = (AbstractExtensionWizardRegistry) PlatformUI.getWorkbench().getImportWizardRegistry();
		cats.addAll(Arrays.asList(r.getRootCategory().getCategories()));
		r = (AbstractExtensionWizardRegistry) PlatformUI.getWorkbench().getExportWizardRegistry();
		cats.addAll(Arrays.asList(r.getRootCategory().getCategories()));
		for ( final IWizardDescriptor wizard : getAllWizards(cats.toArray(new IWizardCategory[0])) ) {
			final String id = wizard.getCategory().getId();
			if ( CATEGORIES_TO_REMOVE.contains(id) || IDS_TO_REMOVE.contains(wizard.getId()) ) {
				System.out.println("Removing wizard " + wizard.getId() + " in category " + id);
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
