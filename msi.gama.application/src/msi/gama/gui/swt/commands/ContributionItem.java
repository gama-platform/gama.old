/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.swt.commands;

import java.util.*;
import msi.gama.gui.swt.SwtGui;
import msi.gama.outputs.IDisplayOutput;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.*;

public class ContributionItem extends CompoundContributionItem {

	public static final String ID_PARAMETER = "msi.gama.application.commands.showGrids.parameter";

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
					"msi.gama.application.commands.ShowGrids", params,
					SwtGui.getImageDescriptor("icons/view_layers.png"), null, // disabled icon
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
