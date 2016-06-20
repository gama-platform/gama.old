package ummisco.gama.ui.utils;

import msi.gama.common.interfaces.IGamaView;

public class ViewsHelper {

	private static volatile boolean isRequesting;

	public static void requestUserAttention(final IGamaView part, final String tempMessage) {
		if (isRequesting)
			return;
		// rate at which the title will change in milliseconds
		final int rateOfChange = 200;
		final int numberOfTimes = 2;

		// flash n times and thats it
		final String orgText = part.getPartName();

		for (int x = 0; x < numberOfTimes; x++) {
			WorkbenchHelper.getDisplay().timerExec(2 * rateOfChange * x - rateOfChange, new Runnable() {

				@Override
				public void run() {
					isRequesting = true;
					part.setName(tempMessage);
				}
			});
			WorkbenchHelper.getDisplay().timerExec(2 * rateOfChange * x, new Runnable() {

				@Override
				public void run() {
					part.setName(orgText);
					isRequesting = false;
				}
			});
		}
	}

}
