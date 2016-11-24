/*********************************************************************************************
 *
 * 'ViewsHelper.java, in plugin ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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
