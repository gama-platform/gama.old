package ummisco.gama.ui.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.impl.PartImpl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IGui;
import msi.gaml.operators.IUnits;
import ummisco.gama.ui.utils.WorkbenchHelper;

@SuppressWarnings({ "rawtypes" })
public class ArrangeDisplayViews extends AbstractHandler {

	public static EPartService partService;
	public static EModelService modelService;
	public static MApplication application;
	public static boolean keepTabs = true;

	public static final String LAYOUT = "msi.gama.displays.layout";

	@Override
	public Object execute(final ExecutionEvent e) {
		final String layout = e.getParameter(LAYOUT);
		final int orientation = GamaPreferences.LAYOUTS.indexOf(layout);
		execute(orientation);
		return true;
	}

	public static void execute(final int layout) {
		if (modelService == null) {
			final IEclipseContext context = WorkbenchHelper.getWindow().getService(IEclipseContext.class);
			modelService = context.get(EModelService.class);
			partService = context.get(EPartService.class);
			application = WorkbenchHelper.getPage().getWorkbenchWindow().getService(MApplication.class);
		}
		if (layout < 0 || layout >= GamaPreferences.LAYOUTS.size())
			return;
		// System.out.println("Executing layout " +
		// GamaPreferences.LAYOUTS.get(layout));
		final List<MPlaceholder> holders = modelService.findElements(application, MPlaceholder.class,
				EModelService.IN_ACTIVE_PERSPECTIVE, element -> {
					final MPlaceholder holder = (MPlaceholder) element;
					if (!(holder.getRef() instanceof PartImpl))
						return false;
					final PartImpl pi = (PartImpl) holder.getRef();
					final String s = holder.getElementId();
					return pi.getObject() != null && s != null && !s.contains("*")
							&& (s.contains(IGui.GL_LAYER_VIEW_ID) || s.contains(IGui.LAYER_VIEW_ID));

				});
		// System.out.println("Found " + holders.size() + " place holders to
		// rearrange");
		if (holders.size() == 1)
			return;
		final List<MPartStack> stacks = modelService.findElements(application, MPartStack.class,
				EModelService.IN_ACTIVE_PERSPECTIVE, element -> "displays".equals(element.getElementId()));
		final MPartStack displayStack = stacks.isEmpty() ? null : stacks.get(0);
		if (displayStack == null)
			return;
		switch (layout) {
		case IUnits.stack:
			stack(displayStack, holders);
			break;
		case IUnits.split:
			grid(displayStack, holders);
			break;
		case IUnits.horizontal:
		case IUnits.vertical:
			horizontalOrVertical(displayStack, holders, layout == IUnits.horizontal);
			break;
		case IUnits.none:
			// none(displayStack, holders);

		}

	}

	static void stack(final MPartStack displayStack, final List<MPlaceholder> holders) {
		for (int i = 0; i < holders.size(); i++) {
			associate(displayStack, holders.get(i), false);
		}
	}

	static void none(final MPartStack displayStack, final List<MPlaceholder> holders) {
		for (int i = 0; i < holders.size(); i++) {
			associate(displayStack, holders.get(i), true);
		}
	}

	static void grid(final MPartStack displayStack, final List<MPlaceholder> holders) {
		final MElementContainer currentSash = displayStack.getParent();
		final int size = holders.size();
		final List<MElementContainer> containers = new ArrayList<>();
		createContainers(currentSash, containers, size, true);
		for (int i = 0; i < holders.size(); i++) {
			associate(containers.get(i), holders.get(i), false);
		}
	}

	static void horizontalOrVertical(final MPartStack displayStack, final List<MPlaceholder> holders,
			final boolean horizontal) {
		final MElementContainer rootSash = displayStack.getParent();
		((MPartSashContainer) rootSash).setHorizontal(horizontal);
		for (int i = 0; i < holders.size(); i++) {
			associate(createContainer(rootSash), holders.get(i), false);
		}
	}

	private static void createContainers(final MElementContainer root, final List<MElementContainer> containers,
			final int size, final boolean horizontal) {
		((MPartSashContainer) root).setHorizontal(horizontal);
		if (size == 0)
			return;
		else if (size == 1) {
			final MElementContainer container = createContainer(root);
			containers.add(container);
		} else {
			final int half = size / 2;
			createContainers(createSash(root), containers, half, !horizontal);
			createContainers(createSash(root), containers, size - half, !horizontal);
		}
	}

	static MPartSashContainer createSash(final MElementContainer root) {
		final MPartSashContainer sash = modelService.createModelElement(MPartSashContainer.class);
		sash.getTransientData().put("Dynamic", true);
		sash.setContainerData("5000");
		root.getChildren().add(sash);
		return sash;
	}

	static MPartStack createStack(final MElementContainer root) {
		final MPartStack stack = modelService.createModelElement(MPartStack.class);
		stack.getTransientData().put("Dynamic", true);
		stack.setContainerData("5000");
		root.getChildren().add(stack);
		return stack;
	}

	static MElementContainer createContainer(final MElementContainer root) {
		if (keepTabs && !(root instanceof MPartStack)) {
			return createStack(root);
		}
		return root;
	}

	static void associate(final MElementContainer container, final MPlaceholder holder, final boolean removeFirst) {
		if (removeFirst)
			container.getChildren().remove(holder);
		container.getChildren().add(holder);
		partService.activate((MPart) holder.getRef());
	}

}