/*******************************************************************************************************
 *
 * ArrangeDisplayViews.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.commands;

import static msi.gaml.operators.Displays.HORIZONTAL;
import static msi.gaml.operators.Displays.VERTICAL;
import static org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory.INSTANCE;
import static org.eclipse.e4.ui.workbench.modeling.EModelService.IN_ACTIVE_PERSPECTIVE;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.ui.IWorkbenchPart;

import com.google.common.collect.Iterables;

import msi.gama.application.workbench.PerspectiveHelper;
import msi.gama.application.workbench.ThemeHelper;
import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.util.tree.GamaNode;
import msi.gama.util.tree.GamaTree;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.utils.ViewsHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class ArrangeDisplayViews.
 */

/**
 * The Class ArrangeDisplayViews.
 */
@SuppressWarnings ({ "rawtypes" })
public class ArrangeDisplayViews extends AbstractHandler {

	/**
	 * Gets the part service.
	 *
	 * @return the part service
	 */
	private static EPartService getPartService() { return WorkbenchHelper.getService(EPartService.class); }

	/**
	 * Gets the application.
	 *
	 * @return the application
	 */
	private static MApplication getApplication() { return WorkbenchHelper.getService(MApplication.class); }

	/**
	 * Gets the model service.
	 *
	 * @return the model service
	 */
	private static EModelService getModelService() { return WorkbenchHelper.getService(EModelService.class); }

	static {
		DEBUG.ON();
	}

	/** The Constant LAYOUT_KEY. */
	public static final String LAYOUT_KEY = "msi.gama.displays.layout";

	/** The Constant DISPLAY_INDEX_KEY. */
	static final String DISPLAY_INDEX_KEY = "GamaIndex";

	@Override
	public Object execute(final ExecutionEvent e) {
		execute(GamaPreferences.Displays.LAYOUTS.indexOf(e.getParameter(LAYOUT_KEY)));
		return true;
	}

	/**
	 * Execute.
	 *
	 * @param layout
	 *            the layout
	 */
	@SuppressWarnings ("unchecked")
	public static void execute(final Object layout) {
		// collectAndPrepareDisplayViews();
		if (layout instanceof Integer i) {
			execute(LayoutTreeConverter.convert(i));
		} else if (layout instanceof GamaTree t) {
			execute(t);
		} else if (layout instanceof GamaNode n) {
			final GamaTree<String> tree = LayoutTreeConverter.newLayoutTree();
			n.attachTo(tree.getRoot());
			execute(tree);
		}
	}

	/**
	 * Execute.
	 *
	 * @param tree
	 *            the tree
	 */
	public static void execute(final GamaTree<String> tree) {
		try {
			final List<MPlaceholder> holders = collectAndPrepareDisplayViews();
			if (tree != null && tree.getRoot().hasChildren()) {
				GamaNode<String> child = tree.getRoot().getChildren().get(0);
				// DEBUG.LOG("Tree root = " + child.getData() + " weight " + child.getWeight());
				if (child.getWeight() == null) { child.setWeight(5000); }
				final MPartStack displayStack = getDisplaysPlaceholder();
				if (displayStack == null) return;
				displayStack.setToBeRendered(true);
				final MElementContainer<?> root = displayStack.getParent();

				displayStack.getChildren().addAll(holders);
				process(root, child, holders);
				showDisplays(root, holders);
			}
			decorateDisplays();

		} catch (Exception e) {
			DEBUG.ERR(e);
		}
	}

	/**
	 * Gets the displays placeholder.
	 *
	 * @return the displays placeholder
	 */
	public static MPartStack getDisplaysPlaceholder() {
		final Object displayStack = getModelService().find("displays", getApplication());
		// DEBUG.OUT("Element displays found : " + displayStack);
		return displayStack instanceof MPartStack ? (MPartStack) displayStack : null;
	}

	/**
	 * Show displays.
	 *
	 * @param root
	 *            the root
	 * @param holders
	 *            the holders
	 */
	private static void showDisplays(final MElementContainer<?> root, final List<MPlaceholder> holders) {
		root.setVisible(true);
		// DEBUG.OUT("Holders to show " +
		// DEBUG.TO_STRING(StreamEx.of(holders).map(MPlaceholder::getElementId).toArray()));
		holders.forEach(ph -> {
			if (ph.getRef() instanceof MPart part) {

				// Necessary as otherwise the Java2D display does not show up if it is alone
				ph.setToBeRendered(true);
				ph.setVisible(true);
				getPartService().showPart(part, PartState.VISIBLE);
				// getPartService().showPart(part, PartState.ACTIVATE);
				// getPartService().activate(part, true);
				// getPartService().bringToTop(part);

			}

		});

	}

	/**
	 * Decorate displays.
	 */
	public static void decorateDisplays() {
		List<IGamaView.Display> displays = ViewsHelper.getDisplayViews(null);
		// DEBUG.OUT("Displays to decorate "
		// + DEBUG.TO_STRING(StreamEx.of(displays).select(IViewPart.class).map(IViewPart::getTitle).toArray()));

		displays.forEach(v -> {
			final Boolean tb = PerspectiveHelper.keepToolbars();
			if (tb != null) { v.showToolbar(tb); }
			v.showOverlay(PerspectiveHelper.showOverlays());
		});
		displays.forEach(v -> {
			LayeredDisplayOutput output = v.getOutput();
			if (output != null && output.getData().fullScreen() > -1) {
				WorkbenchHelper.runInUI("FS", 100, m -> {
					WorkbenchHelper.getPage().bringToTop((IWorkbenchPart) v);
					v.showCanvas();
					v.focusCanvas();
					v.getOutput().update();
					//
					// // v.toggleFullScreen();
				});
			}

		});
		// displays.forEach(d -> ViewsHelper.activate((IWorkbenchPart) d));
		if (PerspectiveHelper.getBackground() != null) {
			ThemeHelper.changeSashBackground(PerspectiveHelper.getBackground());
			PerspectiveHelper.getActiveSimulationPerspective().setRestoreBackground(ThemeHelper::restoreSashBackground);
		}
		// Attempt to solve the problem expressed in #3587 by forcing the focus on the canvases at least once
		// Modified to only target 2d displays as it was creating a problem on macOS (perspective not able to go back to
		// modeling and forth)
		displays.forEach(d -> { if (d.is2D()) { d.focusCanvas(); } });

	}

	/**
	 * Process.
	 *
	 * @param uiRoot
	 *            the ui root
	 * @param treeRoot
	 *            the tree root
	 * @param holders
	 *            the holders
	 */
	public static void process(final MElementContainer uiRoot, final GamaNode<String> treeRoot,
			final List<MPlaceholder> holders) {
		final String data = treeRoot.getData();
		final String weight = String.valueOf(treeRoot.getWeight());
		// DEBUG.OUT("Processing " + data + " with weight " + weight);
		final Boolean dir = !HORIZONTAL.equals(data) && !VERTICAL.equals(data) ? null : HORIZONTAL.equals(data);

		MPlaceholder holder = Iterables.find(holders, h -> {
			Object s = h.getTransientData().get(DISPLAY_INDEX_KEY);
			return s != null && s.equals(data);
		}, null);
		final MElementContainer container = create(uiRoot, weight, dir);
		if (holder != null) {
			if (container.equals(uiRoot)) { holder.setContainerData(weight); }
			container.getChildren().add(holder);
		} else {
			for (final GamaNode<String> node : treeRoot.getChildren()) { process(container, node, holders); }
		}
	}

	/**
	 * List display views.
	 *
	 * @return the list
	 */
	static final List<MPlaceholder> collectAndPrepareDisplayViews() {
		final List<MPlaceholder> holders = getModelService().findElements(getApplication(), MPlaceholder.class,
				IN_ACTIVE_PERSPECTIVE, e -> ViewsHelper.isDisplay(e.getElementId()));
		/// Issue #2680
		int currentIndex = 0;
		for (final MPlaceholder h : holders) {
			final IGamaView.Display display = ViewsHelper.findDisplay(h.getElementId());
			if (display != null) {
				display.setIndex(currentIndex++);
				h.getTransientData().put(DISPLAY_INDEX_KEY, String.valueOf(currentIndex - 1));
			}
		}
		// DEBUG.OUT(Sets.newHashSet(Iterables.transform(holders, @Nullable MPlaceholder::getElementId)));
		return holders;
	}

	/**
	 * Creates the.
	 *
	 * @param root
	 *            the root
	 * @param weight
	 *            the weight
	 * @param dir
	 *            the dir
	 * @return the m element container
	 */
	static MElementContainer create(final MElementContainer root, final String weight, final Boolean dir) {
		if (dir == null && (root instanceof MPartStack || !PerspectiveHelper.keepTabs())) return root;
		final MElementContainer c;
		if (dir != null) {
			c = INSTANCE.createPartSashContainer();
			((MPartSashContainer) c).setHorizontal(dir);
		} else {
			c = INSTANCE.createPartStack();
		}
		c.setContainerData(weight);
		if (root != null) { root.getChildren().add(c); }
		return c;
	}

}