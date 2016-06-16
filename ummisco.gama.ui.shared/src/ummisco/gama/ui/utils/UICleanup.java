package ummisco.gama.ui.utils;

public class UICleanup {

	public static void run() {
		RemoveUnwantedWizards.run();
		RemoveUnwantedActionSets.run();
		RearrangeMenus.run();
	}

}
