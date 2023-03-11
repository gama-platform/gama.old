/*******************************************************************************************************
 *
 * LayoutTreeConverter.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.commands;

import static java.lang.String.valueOf;
import static msi.gama.common.interfaces.IKeyword.LAYOUT;
import static msi.gama.util.tree.GamaTree.withRoot;
import static msi.gaml.constants.GamlCoreConstants.horizontal;
import static msi.gaml.constants.GamlCoreConstants.none;
import static msi.gaml.constants.GamlCoreConstants.split;
import static msi.gaml.constants.GamlCoreConstants.stack;
import static msi.gaml.constants.GamlCoreConstants.vertical;
import static msi.gaml.operators.Displays.HORIZONTAL;
import static msi.gaml.operators.Displays.STACK;
import static msi.gaml.operators.Displays.VERTICAL;
import static one.util.streamex.StreamEx.of;
import static ummisco.gama.ui.commands.ArrangeDisplayViews.DISPLAY_INDEX_KEY;
import static ummisco.gama.ui.commands.ArrangeDisplayViews.collectAndPrepareDisplayViews;
import static ummisco.gama.ui.commands.ArrangeDisplayViews.getDisplaysPlaceholder;
import static ummisco.gama.ui.utils.ViewsHelper.getDisplayViews;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;

import msi.gama.common.interfaces.IGamaView.Display;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.util.tree.GamaNode;
import msi.gama.util.tree.GamaTree;
import one.util.streamex.IntStreamEx;

/**
 * The Class LayoutTreeConverter.
 */
public class LayoutTreeConverter {

	/**
	 * Convert.
	 *
	 * @param layout
	 *            the layout
	 * @return the gama tree
	 */
	public static GamaTree<String> convert(final int layout) {
		if (layout < 0 || layout >= GamaPreferences.Displays.LAYOUTS.size()) return null;
		collectAndPrepareDisplayViews();
		final int[] indices = of(getDisplayViews(null)).mapToInt(Display::getIndex).toArray();
		// Issue #2740 -- proceed anyway with only 1 display
		// if (indices.length <= 1) { return null; }
		Arrays.sort(indices);
		final GamaTree<String> result = newLayoutTree();
		switch (layout) {
			case none:
			case stack:
				return buildStackTree(result, indices);
			case split:
				return buildGridTree(result, indices);
			case horizontal:
			case vertical:
				return buildHorizontalOrVerticalTree(result, indices, layout == horizontal);
			// Issue #3313. Forcing a layout seems to be the solution to the sizing problem of Java2D displays
			// case none:
			// return null;
		}
		return null;
	}

	/**
	 * New layout tree.
	 *
	 * @return the gama tree
	 */
	static GamaTree<String> newLayoutTree() {
		return withRoot(LAYOUT);
	}

	/**
	 * Builds the stack tree.
	 *
	 * @param result
	 *            the result
	 * @param indices
	 *            the indices
	 * @return the gama tree
	 */
	static GamaTree<String> buildStackTree(final GamaTree<String> result, final int[] indices) {
		if (indices.length == 0) return result;
		final GamaNode<String> root = result.getRoot().addChild(STACK);
		IntStreamEx.of(indices).forEach(i -> root.addChild(valueOf(i), 5000));
		return result;
	}

	/**
	 * Builds the grid tree.
	 *
	 * @param result
	 *            the result
	 * @param indices
	 *            the indices
	 * @return the gama tree
	 */
	static GamaTree<String> buildGridTree(final GamaTree<String> result, final int[] indices) {
		if (indices.length == 0) return result;
		final GamaNode<String> initialSash = result.getRoot().addChild(HORIZONTAL);
		final List<GamaNode<String>> placeholders = new ArrayList<>();
		buildPlaceholders(initialSash, placeholders, indices.length);
		int i = 0;
		for (final GamaNode<String> node : placeholders) { node.setData(valueOf(indices[i++])); }
		return result;
	}

	/**
	 * Builds the placeholders.
	 *
	 * @param root
	 *            the root
	 * @param list
	 *            the list
	 * @param size
	 *            the size
	 */
	static void buildPlaceholders(final GamaNode<String> root, final List<GamaNode<String>> list, final int size) {
		if (size == 0) return;
		if (size == 1) {
			list.add(root);
		} else {
			final int half = size / 2;
			final String orientation = HORIZONTAL.equals(root.getData()) ? VERTICAL : HORIZONTAL;
			buildPlaceholders(root.addChild(orientation, 5000), list, half);
			buildPlaceholders(root.addChild(orientation, 5000), list, size - half);
		}
	}

	/**
	 * Builds the horizontal or vertical tree.
	 *
	 * @param result
	 *            the result
	 * @param indices
	 *            the indices
	 * @param horizon
	 *            the horizon
	 * @return the gama tree
	 */
	static GamaTree<String> buildHorizontalOrVerticalTree(final GamaTree<String> result, final int[] indices,
			final boolean horizon) {
		final GamaNode<String> sashNode = result.getRoot().addChild(horizon ? HORIZONTAL : VERTICAL);
		IntStreamEx.of(indices).forEach(i -> sashNode.addChild(valueOf(i), 5000));
		return result;
	}

	/**
	 * Convert current layout.
	 *
	 * @param holders
	 *            the holders
	 * @return the gama tree
	 */
	public static GamaTree<String> convertCurrentLayout(final List<MPlaceholder> holders) {
		final MPartStack displayStack = getDisplaysPlaceholder();
		if (displayStack == null) return null;
		final GamaTree<String> tree = newLayoutTree();
		save(displayStack.getParent(), holders, tree.getRoot(), null);
		return tree;
	}

	/**
	 * Gets the weight.
	 *
	 * @param element
	 *            the element
	 * @return the weight
	 */
	private static String getWeight(final MUIElement element) {
		String data = element.getContainerData();
		final MUIElement parent = element.getParent();
		while (data == null && parent != null) { data = parent.getContainerData(); }
		return data;
	}

	/**
	 * Save.
	 *
	 * @param element
	 *            the element
	 * @param holders
	 *            the holders
	 * @param parent
	 *            the parent
	 * @param weight
	 *            the weight
	 */
	private static void save(final MUIElement element, final List<MPlaceholder> holders, final GamaNode<String> parent,
			final String weight) {
		final String data = weight == null ? getWeight(element) : weight;
		if (element instanceof MPlaceholder && holders.contains(element)) {
			parent.addChild(valueOf(element.getTransientData().get(DISPLAY_INDEX_KEY)), parseInt(data));
		} else if (element instanceof MElementContainer) {
			final MElementContainer<?> container = (MElementContainer<?>) element;
			final List<? extends MUIElement> children = getNonEmptyChildren(container, holders);
			if (children.size() == 0) return;
			if (children.size() == 1) {
				save(children.get(0), holders, parent, data);
			} else {
				final GamaNode<String> node = parent.addChild(prefix(container), parseInt(data));
				children.forEach(e -> save(e, holders, node, null));
			}
		}
	}

	/**
	 * Parses the int.
	 *
	 * @param data
	 *            the data
	 * @return the int
	 */
	static int parseInt(final String data) {
		try {
			return data == null ? 0 : Integer.parseInt(data);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * Prefix.
	 *
	 * @param container
	 *            the container
	 * @return the string
	 */
	private static String prefix(final MElementContainer<?> container) {
		return container instanceof MPartStack ? STACK : container instanceof MPartSashContainer
				? ((MPartSashContainer) container).isHorizontal() ? HORIZONTAL : VERTICAL : "";
	}

	/**
	 * Checks if is empty.
	 *
	 * @param element
	 *            the element
	 * @param holders
	 *            the holders
	 * @return true, if is empty
	 */
	private static boolean isEmpty(final MUIElement element, final List<MPlaceholder> holders) {
		if (element instanceof MElementContainer)
			return of(((MElementContainer<?>) element).getChildren()).allMatch(e -> isEmpty(e, holders));
		return !holders.contains(element);
	}

	/**
	 * Gets the non empty children.
	 *
	 * @param container
	 *            the container
	 * @param holders
	 *            the holders
	 * @return the non empty children
	 */
	static List<? extends MUIElement> getNonEmptyChildren(final MElementContainer<? extends MUIElement> container,
			final List<MPlaceholder> holders) {
		return of(container.getChildren()).filter(e -> !isEmpty(e, holders)).toList();
	}

}
