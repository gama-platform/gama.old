package ummisco.gama.ui.commands;

import static ummisco.gama.ui.utils.SwtGui.allDisplaySurfaces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import msi.gama.util.tree.GamaTree;
import msi.gama.util.tree.GamaTreeNode;
import msi.gaml.operators.IUnits;
import one.util.streamex.StreamEx;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class LayoutTreeConverter {

	public GamaTree<String> convert(final int layout) {
		final int[] indices = StreamEx.of(allDisplaySurfaces()).mapToInt((s) -> s.getOutput().getIndex()).toArray();
		if (indices.length <= 1) { return null; }
		Arrays.sort(indices);
		switch (layout) {
			case IUnits.stack:
				return buildStackTree(indices);
			case IUnits.split:
				return buildGridTree(indices);
			case IUnits.horizontal:
			case IUnits.vertical:
				return buildHorizontalOrVerticalTree(indices, layout == IUnits.horizontal);
			case IUnits.none:
				return null;
		}
		return null;
	}

	static GamaTree<String> newLayoutTree() {
		return new GamaTree<>("layout");
	}

	GamaTree<String> buildStackTree(final int[] indices) {
		final GamaTree<String> result = newLayoutTree();
		final GamaTreeNode<String> root = result.getRoot().addChild("stack");
		for (final int i : indices) {
			root.addChild(String.valueOf(i), 5000d);
		}
		return result;
	}

	GamaTree<String> buildGridTree(final int[] indices) {
		final GamaTree<String> result = newLayoutTree();

		final GamaTreeNode<String> initialSash = result.getRoot().addChild("horizontal");
		final List<GamaTreeNode<String>> placeholders = new ArrayList<>();
		buildPlaceholders(initialSash, placeholders, indices.length);
		int i = 0;
		for (final GamaTreeNode<String> node : placeholders) {
			node.setData(String.valueOf(indices[i++]));
		}
		return result;
	}

	void buildPlaceholders(final GamaTreeNode<String> root, final List<GamaTreeNode<String>> list, final int size) {
		if (size == 0) {
			return;
		} else if (size == 1) {
			root.setData("placeholder");
			list.add(root);
		} else {
			final int half = size / 2;
			final String orientation = root.getData().equals("horizontal") ? "vertical" : "horizontal";
			buildPlaceholders(root.addChild(orientation, 5000d), list, half);
			buildPlaceholders(root.addChild(orientation, 5000d), list, size - half);
		}
	}

	GamaTree<String> buildHorizontalOrVerticalTree(final int[] indices, final boolean horizontal) {
		final GamaTree<String> result = newLayoutTree();
		final GamaTreeNode<String> sashNode = result.getRoot().addChild(horizontal ? "horizontal" : "vertical");
		for (final int i : indices) {
			sashNode.addChild(String.valueOf(i), 5000d);
		}
		return result;
	}

	public GamaTree<String> convertCurrentLayout(final Map<String, MPlaceholder> holders) {
		final MApplication application = WorkbenchHelper.getService(MApplication.class);
		final IEclipseContext context = WorkbenchHelper.getService(IEclipseContext.class);
		final EModelService modelService = context.get(EModelService.class);
		final List<MPartStack> stacks = modelService.findElements(application, MPartStack.class,
				EModelService.IN_ACTIVE_PERSPECTIVE, element -> "displays".equals(element.getElementId()));
		final MPartStack displayStack = stacks.isEmpty() ? null : stacks.get(0);
		if (displayStack == null) { return null; }
		final GamaTree<String> tree = newLayoutTree();
		final MElementContainer rootSash = displayStack.getParent();
		save(rootSash, holders, tree.getRoot(), null);
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

	private void save(final MUIElement element, final Map<String, MPlaceholder> holders,
			final GamaTreeNode<String> parent, final String weight) {
		String data = weight;
		if (data == null) {
			data = getWeight(element);
		}
		if (element instanceof MPlaceholder && holders.containsValue(element)) {
			parent.addChild(String.valueOf(holders.get(element)), Double.parseDouble(data));
		} else if (element instanceof MElementContainer) {
			final MElementContainer container = (MElementContainer) element;
			final List<MUIElement> children = getNonEmptyChildren(container, holders);
			if (children.size() == 0) { return; }
			if (children.size() == 1) {
				save(children.get(0), holders, parent, data);
				return;
			}
			final GamaTreeNode<String> node = parent.addChild(prefix(container), Double.parseDouble(data));
			for (final MUIElement e : children) {
				save(e, holders, node, null);
			}
		}
	}

	String prefix(final MElementContainer<?> container) {
		if (container instanceof MPartStack) { return "stack"; }
		if (container instanceof MPartSashContainer) { return ((MPartSashContainer) container).isHorizontal()
				? "horizontal" : "vertical"; }
		return "";
	}

	private boolean isEmpty(final MUIElement element, final Map<String, MPlaceholder> holders) {
		if (element instanceof MElementContainer) { return isEmpty((MElementContainer<?>) element, holders); }
		if (element instanceof MPlaceholder && holders.containsValue(element)) { return false; }
		return true;
	}

	private boolean isEmpty(final MElementContainer<? extends MUIElement> container,
			final Map<String, MPlaceholder> holders) {
		for (final MUIElement element : container.getChildren()) {
			if (!isEmpty(element, holders)) { return false; }
		}
		return true;
	}

	List<MUIElement> getNonEmptyChildren(final MElementContainer<? extends MUIElement> container,
			final Map<String, MPlaceholder> holders) {
		final List<MUIElement> children = new ArrayList<>();
		for (final MUIElement element : container.getChildren()) {
			if (!isEmpty(element, holders)) {
				children.add(element);
			}
		}
		return children;
	}

}
