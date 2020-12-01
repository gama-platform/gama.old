package msi.gama.application.workbench;

import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.e4.core.internal.contexts.EclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.EventHandler;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.ui.internal.themes.ThemeRegistry;
import org.eclipse.ui.internal.themes.WorkbenchThemeManager;
import org.eclipse.ui.internal.themes.WorkbenchThemeManager.Events;
import org.eclipse.ui.themes.IThemeManager;

public class ApplicationWorkbenchThemeManager {

	public static void install() {
		if ( !PlatformUI.isWorkbenchRunning() ) { return; }
		IEventBroker eventBroker = Workbench.getInstance().getService(IEventBroker.class);
		WorkbenchThemeChangedHandler themeChangedHandler = new WorkbenchThemeChangedHandler();
		if ( eventBroker != null ) {
			eventBroker.subscribe(UIEvents.UILifeCycle.THEME_CHANGED, themeChangedHandler);
			eventBroker.subscribe(IThemeEngine.Events.THEME_CHANGED, themeChangedHandler);
		}
	}

	public static class WorkbenchThemeChangedHandler implements EventHandler {

		@Override
		public void handleEvent(org.osgi.service.event.Event event) {
			System.out.println("NEW THEME= " + getTheme(event).getLabel());
		}

		private IEclipseContext getContext() {
			return Workbench.getInstance().getContext();
		}

		protected org.eclipse.e4.ui.css.swt.theme.ITheme getTheme(org.osgi.service.event.Event event) {
			org.eclipse.e4.ui.css.swt.theme.ITheme theme =
				(org.eclipse.e4.ui.css.swt.theme.ITheme) event.getProperty(IThemeEngine.Events.THEME);
			if ( theme == null ) {
				IThemeEngine themeEngine = getContext().get(IThemeEngine.class);
				theme = themeEngine != null ? themeEngine.getActiveTheme() : null;
			}
			return theme;
		}
	}
}

// Bundle b = FrameworkUtil.getBundle(getClass());
// BundleContext context = b.getBundleContext();
// ServiceReference serviceRef = context
// .getServiceReference(IThemeManager.class.getName());
// IThemeManager themeManager = (IThemeManager) context
// .getService(serviceRef);
// final IThemeEngine engine = themeManager.getEngineForDisplay(Display
// .getCurrent());
// engine.setTheme("org.eclipse.e4.ui.examples.css.rcp", true);
// if (serviceRef != null) {
// serviceRef = null;
// }
// themeManager = null;