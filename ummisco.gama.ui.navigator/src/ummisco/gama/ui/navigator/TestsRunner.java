package ummisco.gama.ui.navigator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import msi.gama.common.interfaces.IGui;
import msi.gama.runtime.GAMA;
import msi.gaml.statements.test.TestStatement.TestSummary;
import ummisco.gama.ui.utils.SwtGui;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class TestsRunner {

	public static void start() {
		SwtGui.PERSISTENT_TEST_VIEW = true;
		// final StringBuilder sb = new StringBuilder();
		List<IFile> testFiles = null;
		try {
			testFiles = findTestModels();
		} catch (final CoreException e) {}
		if (testFiles != null) {
			final IGui gui = GAMA.getRegularGui();
			gui.openTestView(GAMA.getRuntimeScope(), true);
			for (final IFile file : testFiles) {
				final List<TestSummary> summaries = gui.runHeadlessTests(file);
				for (final TestSummary summary : summaries) {
					gui.displayTestsResults(GAMA.getRuntimeScope(), summary);
				}
				// if (summary != null) {
				// sb.append(summary).append(Strings.LN);
				// }
			}
		}

		// GAMA.getGui().getConsole(GAMA.getRuntimeScope()).showConsoleView(GAMA.agent);
		// GAMA.getGui().getConsole(GAMA.getRuntimeScope()).informConsole(sb.toString(), GAMA.agent);
		SwtGui.PERSISTENT_TEST_VIEW = false;
	}

	private static List<IFile> findTestModels() throws CoreException {
		final List<IFile> result = new ArrayList<>();
		final IWorkspaceRoot w = ResourcesPlugin.getWorkspace().getRoot();
		for (final IProject p : w.getProjects()) {
			if (p == null || !p.exists() || !p.isAccessible())
				continue;
			if (p.getDescription().hasNature(WorkbenchHelper.TEST_NATURE)) {
				result.addAll(ModelsFinder.getAllGamaFilesInProject(p));
			}
		}
		return result;

	}

}
