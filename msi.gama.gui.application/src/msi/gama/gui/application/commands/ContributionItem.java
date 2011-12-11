/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application.commands;

import java.util.*;
import msi.gama.gui.application.Activator;
import msi.gama.interfaces.IDisplayOutput;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.*;

public class ContributionItem extends CompoundContributionItem {

	public static final String ID_PARAMETER =
		"msi.gama.gui.application.commands.showGrids.parameter";

	static Map<String, IDisplayOutput> listView = new HashMap<String, IDisplayOutput>();

	// public ContributionItem(IDisplayOutput output, String idView) {
	// super();
	// this.output = output;
	// this.idView = idView;
	// }

	@Override
	protected IContributionItem[] getContributionItems() {
		Map<String, String> params;
		LinkedList<IContributionItem> list = new LinkedList<IContributionItem>();

		// /* Get the icons */
		// AbstractUIPlugin plugin = Activator.getDefault();
		// ImageRegistry imageRegistry = plugin.getImageRegistry();
		// Image image = imageRegistry.get(Activator.image_layers);

		if ( listView == null ) { return null; }

		for ( Map.Entry<String, IDisplayOutput> entry : listView.entrySet() ) {
			String idView = entry.getKey();

			params = new HashMap<String, String>();
			params.put(ID_PARAMETER, idView);

			CommandContributionItemParameter para =
				new CommandContributionItemParameter(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow(), ID_PARAMETER,
					"msi.gama.gui.application.commands.ShowGrids", params,
					Activator.getImageDescriptor("icons/view_layers.png"), null, // disabled icon
					null, // hover icon
					entry.getValue().getName(), // label
					null, // mnemonic
					null, // tooltip
					CommandContributionItem.STYLE_PUSH, null, // help context id
					true // visibleEnable
				);

			list.add(new CommandContributionItem(para));
		}
		return list.toArray(new IContributionItem[list.size()]);
	}

	public static void addViewToDynamicDisplayList(final IDisplayOutput out, final String idView) {
		if ( !listView.containsKey(idView) ) {
			listView.put(idView, out);
		}
	}

	public static void clearDynamicDisplayList() {
		listView.clear();
	}

}
