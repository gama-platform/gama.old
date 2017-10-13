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
import msi.gama.runtime.IScope;
import msi.gaml.statements.test.TestStatement.TestSummary;
import ummisco.gama.ui.utils.SwtGui;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class TestsRunner {

	public static void start() {
		if (SwtGui.ALL_TESTS_RUNNING)
			return;
		final IGui gui = GAMA.getRegularGui();
		final IScope scope = GAMA.getRuntimeScope();
		try {
			SwtGui.ALL_TESTS_RUNNING = true;

			List<IFile> testFiles = null;
			try {
				testFiles = findTestModels();
				if (testFiles != null) {
					gui.openTestView(scope, true);
					gui.displayTestsResults(scope, TestSummary.BEGINNING);
					for (final IFile file : testFiles) {
						final List<TestSummary> summaries = gui.runHeadlessTests(file);
						for (final TestSummary summary : summaries) {
							gui.displayTestsResults(scope, summary);
						}
					}
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		} finally {
			SwtGui.ALL_TESTS_RUNNING = false;
			gui.displayTestsResults(scope, TestSummary.FINISHED);
		}
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
