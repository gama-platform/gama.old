/*********************************************************************************************
 * 
 * 
 * 'BrowserItem.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.views.actions;

import msi.gama.gui.swt.*;
import msi.gama.gui.views.BrowserEditor;
import org.eclipse.jface.action.*;
import org.eclipse.swt.browser.Browser;

/**
 * Class BrowserItem.
 * 
 * @author drogoul
 * @since 3 avr. 2014
 * 
 */
public abstract class BrowserItem extends GamaViewItem {

	public BrowserItem() {
		super(null);
	}

	protected Browser getBrowser() {
		return ((BrowserEditor) getView()).getBrowser();
	}

	public void setView(final BrowserEditor view) {
		this.view = view;
	}

	public static class Back extends BrowserItem {

		@Override
		protected IContributionItem createItem() {
			IAction action =
				new GamaAction("Back", "Go back in history ", IAction.AS_PUSH_BUTTON,
					IGamaIcons.BROWSER_BACK.descriptor()) {

					@Override
					public void run() {
						getBrowser().back();
					}

				};
			return new ActionContributionItem(action);
		}

	}

	public static class Forward extends BrowserItem {

		@Override
		protected IContributionItem createItem() {
			IAction action =
				new GamaAction("Forward", "Go forward in history ", IAction.AS_PUSH_BUTTON,
					IGamaIcons.BROWSER_FORWARD.descriptor()) {

					@Override
					public void run() {
						getBrowser().forward();
					}

				};
			return new ActionContributionItem(action);
		}

	}

	public static class Stop extends BrowserItem {

		@Override
		protected IContributionItem createItem() {
			IAction action =
				new GamaAction("Stop", "Stop loading page", IAction.AS_PUSH_BUTTON,
					IGamaIcons.BROWSER_STOP.descriptor()) {

					@Override
					public void run() {
						getBrowser().stop();
					}

				};
			return new ActionContributionItem(action);
		}

	}

	public static class Home extends BrowserItem {

		@Override
		protected IContributionItem createItem() {
			IAction action =
				new GamaAction("Home", "Go back to the welcome page", IAction.AS_PUSH_BUTTON,
					IGamaIcons.BROWSER_HOME.descriptor()) {

					@Override
					public void run() {
						ApplicationWorkbenchWindowAdvisor.openWelcomePage(false);
					}

				};
			return new ActionContributionItem(action);
		}

	}

	public static class Refresh extends BrowserItem {

		@Override
		protected IContributionItem createItem() {
			IAction action =
				new GamaAction("Back", "Go back in history ", IAction.AS_PUSH_BUTTON,
					IGamaIcons.BROWSER_REFRESH.descriptor()) {

					@Override
					public void run() {
						getBrowser().refresh();
					}

				};
			return new ActionContributionItem(action);
		}

	}

}
