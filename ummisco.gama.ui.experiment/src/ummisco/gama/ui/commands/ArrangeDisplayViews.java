/*********************************************************************************************
 *
 * 'ArrangeDisplayViews.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.commands;

import static org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory.INSTANCE;
import static org.eclipse.e4.ui.workbench.modeling.EModelService.IN_ACTIVE_PERSPECTIVE;
import static ummisco.gama.ui.utils.WorkbenchHelper.findDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.util.tree.GamaTree;
import msi.gama.util.tree.GamaTreeNode;
import one.util.streamex.StreamEx;
import ummisco.gama.ui.utils.WorkbenchHelper;

@SuppressWarnings ({ "rawtypes" })
public class ArrangeDisplayViews extends AbstractHandler {

	public static boolean keepTabs = true;

	public static final String LAYOUT = "msi.gama.displays.layout";

	@Override
	public Object execute(final ExecutionEvent e) {
		final String layout = e.getParameter(LAYOUT);
		final int orientation = GamaPreferences.Displays.LAYOUTS.indexOf(layout);
		execute(orientation);
		return true;
	}

	public static void execute(final int layout) {
		if (layout < 0 || layout >= GamaPreferences.Displays.LAYOUTS.size()) { return; }
		//
		final GamaTree<String> tree = new LayoutTreeConverter().convert(layout);
		if (tree == null) { return; }
		//
		execute(tree);
	}

	private static EPartService getPartService() {
		return WorkbenchHelper.getService(EPartService.class);
	}

	private static MApplication getApplication() {
		return WorkbenchHelper.getService(MApplication.class);
	}

	private static EModelService getModelService() {
		return WorkbenchHelper.getService(EModelService.class);
	}

	public static void execute(final GamaTree<String> tree) {
		final Map<String, MPlaceholder> holders = listDisplayViews();
		final List<MPartStack> stacks = getModelService().findElements(getApplication(), MPartStack.class,
				IN_ACTIVE_PERSPECTIVE, element -> "displays".equals(element.getElementId()));
		final MPartStack displayStack = stacks.isEmpty() ? null : stacks.get(0);
		if (displayStack == null) { return; }
		clearDisplays(displayStack, holders);
		process(displayStack.getParent(), tree.getRoot().getChildren().get(0), holders);
		activateDisplays(holders);
	}

	private static void activateDisplays(final Map<String, MPlaceholder> holders) {
		holders.forEach((i, ph) -> getPartService().activate((MPart) ph.getRef(), false));
	}

	public static void clearDisplays(final MPartStack displayStack, final Map<String, MPlaceholder> holders) {
		final MElementContainer<MUIElement> parent = displayStack.getParent();
		for (final MPlaceholder holder : holders.values()) {
			displayStack.getChildren().add(holder);
		}
		activateDisplays(holders);
		for (final MUIElement element : new ArrayList<>(parent.getChildren())) {
			if (element.getTransientData().containsKey("Layout")) {
				element.setToBeRendered(false);
				parent.getChildren().remove(element);
			}
		}
	}

	public static void process(final MElementContainer uiRoot, final GamaTreeNode<String> treeRoot,
			final Map<String, MPlaceholder> holders) {
		final String data = treeRoot.getData();
		final String weight = String.valueOf(treeRoot.getWeight());
		final Boolean dir = !data.equals("horizontal") && !data.equals("vertical") ? null : data.equals("horizontal");
		final MPlaceholder holder = holders.get(data);
		final MElementContainer container = create(uiRoot, weight, dir);
		if (holder != null) {
			container.getChildren().add(holder);
		} else {
			for (final GamaTreeNode<String> node : treeRoot.getChildren()) {
				process(container, node, holders);
			}
		}
	}

	static final Map<String, MPlaceholder> listDisplayViews() {
		final List<MPlaceholder> holders = getModelService().findElements(getApplication(), MPlaceholder.class,
				IN_ACTIVE_PERSPECTIVE, e -> findDisplay(e.getElementId()) != null);
		return StreamEx.of(holders).toMap(e -> String.valueOf(findDisplay(e.getElementId()).getIndex()), k -> k);
	}

	static MElementContainer create(final MElementContainer root, final String weight, final Boolean dir) {
		if (dir == null && (root instanceof MPartStack || !keepTabs)) {// stack
			return root;
		}
		final MElementContainer c = dir != null ? INSTANCE.createPartSashContainer() : INSTANCE.createPartStack();
		c.getTransientData().put("Dynamic", true);
		c.getTransientData().put("Layout", true);
		c.setContainerData(weight);
		if (dir != null) {
			((MPartSashContainer) c).setHorizontal(dir);
		}
		root.getChildren().add(c);
		return c;
	}

}