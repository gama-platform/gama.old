package ummisco.gama.ui.commands;

import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static msi.gama.common.interfaces.IKeyword.LAYOUT;
import static msi.gama.util.tree.GamaTree.withRoot;
import static msi.gaml.operators.Displays.HORIZONTAL;
import static msi.gaml.operators.Displays.STACK;
import static msi.gaml.operators.Displays.VERTICAL;
import static msi.gaml.operators.IUnits.horizontal;
import static msi.gaml.operators.IUnits.none;
import static msi.gaml.operators.IUnits.split;
import static msi.gaml.operators.IUnits.stack;
import static msi.gaml.operators.IUnits.vertical;
import static one.util.streamex.StreamEx.of;
import static ummisco.gama.ui.commands.ArrangeDisplayViews.DISPLAY_INDEX_KEY;
import static ummisco.gama.ui.commands.ArrangeDisplayViews.getDisplaysPlaceholder;
import static ummisco.gama.ui.utils.SwtGui.allDisplaySurfaces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.util.tree.GamaNode;
import msi.gama.util.tree.GamaTree;
import one.util.streamex.IntStreamEx;

public class LayoutTreeConverter {

	public GamaTree<String> convert(final int layout) {
		if (layout < 0 || layout >= GamaPreferences.Displays.LAYOUTS.size()) { return null; }
		final int[] indices = of(allDisplaySurfaces()).mapToInt((s) -> s.getOutput().getIndex()).toArray();
		if (indices.length <= 1) { return null; }
		Arrays.sort(indices);
		final GamaTree<String> result = newLayoutTree();
		switch (layout) {
			case stack:
				return buildStackTree(result, indices);
			case split:
				return buildGridTree(result, indices);
			case horizontal:
			case vertical:
				return buildHorizontalOrVerticalTree(result, indices, layout == horizontal);
			case none:
				return null;
		}
		return null;
	}

	static GamaTree<String> newLayoutTree() {
		return withRoot(LAYOUT);
	}

	GamaTree<String> buildStackTree(final GamaTree<String> result, final int[] indices) {
		final GamaNode<String> root = result.getRoot().addChild(STACK);
		IntStreamEx.of(indices).forEach(i -> root.addChild(valueOf(i), 5000));
		return result;
	}

	GamaTree<String> buildGridTree(final GamaTree<String> result, final int[] indices) {
		final GamaNode<String> initialSash = result.getRoot().addChild(HORIZONTAL);
		final List<GamaNode<String>> placeholders = new ArrayList<>();
		buildPlaceholders(initialSash, placeholders, indices.length);
		int i = 0;
		for (final GamaNode<String> node : placeholders) {
			node.setData(valueOf(indices[i++]));
		}
		return result;
	}

	void buildPlaceholders(final GamaNode<String> root, final List<GamaNode<String>> list, final int size) {
		if (size == 0) {
			return;
		} else if (size == 1) {
			list.add(root);
		} else {
			final int half = size / 2;
			final String orientation = root.getData().equals(HORIZONTAL) ? VERTICAL : HORIZONTAL;
			buildPlaceholders(root.addChild(orientation, 5000), list, half);
			buildPlaceholders(root.addChild(orientation, 5000), list, size - half);
		}
	}

	GamaTree<String> buildHorizontalOrVerticalTree(final GamaTree<String> result, final int[] indices,
			final boolean horizon) {
		final GamaNode<String> sashNode = result.getRoot().addChild(horizon ? HORIZONTAL : VERTICAL);
		IntStreamEx.of(indices).forEach(i -> sashNode.addChild(valueOf(i), 5000));
		return result;
	}

	public GamaTree<String> convertCurrentLayout(final List<MPlaceholder> holders) {
		final MPartStack displayStack = getDisplaysPlaceholder();
		if (displayStack == null) { return null; }
		final GamaTree<String> tree = newLayoutTree();
		save(displayStack.getParent(), holders, tree.getRoot(), null);
		return tree;
	}

	String getWeight(final MUIElement element) {
		String data = element.getContainerData();
		final MUIElement parent = element.getParent();
		while (data == null && parent != null) {
			data = parent.getContainerData();
		}
		return data;
	}

	private void save(final MUIElement element, final List<MPlaceholder> holders, final GamaNode<String> parent,
			final String weight) {
		final String data = weight == null ? getWeight(element) : weight;
		if (element instanceof MPlaceholder && holders.contains(element)) {
			parent.addChild(valueOf(element.getTransientData().get(DISPLAY_INDEX_KEY)), parseInt(data));
		} else if (element instanceof MElementContainer) {
			final MElementContainer<?> container = (MElementContainer<?>) element;
			final List<? extends MUIElement> children = getNonEmptyChildren(container, holders);
			if (children.size() == 0) { return; }
			if (children.size() == 1) {
				save(children.get(0), holders, parent, data);
			} else {
				final GamaNode<String> node = parent.addChild(prefix(container), parseInt(data));
				children.forEach(e -> save(e, holders, node, null));
			}
		}
	}

	String prefix(final MElementContainer<?> container) {
		return container instanceof MPartStack ? STACK : container instanceof MPartSashContainer
				? ((MPartSashContainer) container).isHorizontal() ? HORIZONTAL : VERTICAL : "";
	}

	private boolean isEmpty(final MUIElement element, final List<MPlaceholder> holders) {
		if (element instanceof MElementContainer) { return of(((MElementContainer<?>) element).getChildren())
				.allMatch(e -> isEmpty(e, holders)); }
		return !(holders.contains(element));
	}

	List<? extends MUIElement> getNonEmptyChildren(final MElementContainer<? extends MUIElement> container,
			final List<MPlaceholder> holders) {
		return of(container.getChildren()).filter(e -> !isEmpty(e, holders)).toList();
	}

}
