package msi.gama.common.interfaces;

import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.util.GamaColor;

public interface IConsoleDisplayer {

	void debugConsole(int cycle, String s, ITopLevelAgent root, GamaColor color);

	void debugConsole(int cycle, String s, ITopLevelAgent root);

	void informConsole(String s, ITopLevelAgent root, GamaColor color);

	void informConsole(String s, ITopLevelAgent root);

	void showConsoleView(ITopLevelAgent agent);

	public void eraseConsole(final boolean setToNull);
}
