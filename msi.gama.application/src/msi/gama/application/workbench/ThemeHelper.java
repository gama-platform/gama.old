package msi.gama.application.workbench;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.css.swt.theme.ITheme;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.osgi.service.event.EventHandler;

public class ThemeHelper {

	public static final String E4_DARK_THEME_ID = "org.eclipse.e4.ui.css.theme.e4_dark";
	public static final String E4_LIGHT_THEME_ID = "org.eclipse.e4.ui.css.theme.e4_default";
	public static final String E4_CLASSIC_THEME_ID = "org.eclipse.e4.ui.css.theme.e4_classic";

	private static final List<IThemeListener> listeners = new ArrayList<>();

	private static IEclipseContext getContext() {
		return Workbench.getInstance().getContext();
	}

	public static void install() {
		if ( !PlatformUI.isWorkbenchRunning() ) { return; }
		final var eventBroker = Workbench.getInstance().getService(IEventBroker.class);
		final var themeChangedHandler = new WorkbenchThemeChangedHandler();
		if ( eventBroker != null ) {
			eventBroker.subscribe(UIEvents.UILifeCycle.THEME_CHANGED, themeChangedHandler);
			eventBroker.subscribe(IThemeEngine.Events.THEME_CHANGED, themeChangedHandler);
		}
	}

	public static void changeToLight() {
		changeTo(E4_LIGHT_THEME_ID);
	}

	public static void changeToDark() {
		changeTo(E4_DARK_THEME_ID);
	}

	private static void changeTo(String id) {
		final var themeEngine = getContext().get(IThemeEngine.class);
		if ( themeEngine == null )
			return;
		final var theme = (themeEngine.getActiveTheme());
		if ( theme != null ) {
			if ( theme.getId().startsWith(id) )
				return;
		}
		themeEngine.setTheme(id, true);
	}

	public static void addListener(IThemeListener l) {
		if ( !listeners.contains(l) )
			listeners.add(l);
	}

	public static void removeListener(IThemeListener l) {
		listeners.remove(l);
	}

	public static class WorkbenchThemeChangedHandler implements EventHandler {

		@Override
		public void handleEvent(org.osgi.service.event.Event event) {
			final var theme = getTheme(event);
			System.out.println("THEME = " + theme);
			if ( theme == null )
				return;
			final var isDark = theme.getId().startsWith(E4_DARK_THEME_ID);
			listeners.forEach(l -> l.themeChanged(!isDark));
		}

		protected ITheme getTheme(org.osgi.service.event.Event event) {
			var theme = (ITheme) event.getProperty(IThemeEngine.Events.THEME);
			if ( theme == null ) {
				final var themeEngine = getContext().get(IThemeEngine.class);
				theme = themeEngine != null ? themeEngine.getActiveTheme() : null;
			}
			return theme;
		}
	}

	public interface IThemeListener {

		void themeChanged(boolean light);
	}

	public static boolean isDark() {
		final var themeEngine = getContext().get(IThemeEngine.class);
		if ( themeEngine == null )
			return false;
		final var theme = (themeEngine.getActiveTheme());
		return (theme != null) && (theme.getId().startsWith(E4_DARK_THEME_ID));
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