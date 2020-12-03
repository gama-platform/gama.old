package msi.gama.application.workbench;

import static msi.gama.common.preferences.GamaPreferences.create;
import static msi.gama.common.preferences.GamaPreferences.Interface.APPEARANCE;
import static msi.gama.common.preferences.GamaPreferences.Interface.NAME;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.css.swt.internal.theme.ThemeEngine;
import org.eclipse.e4.ui.css.swt.theme.ITheme;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.e4.ui.internal.workbench.swt.E4Application;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.event.EventHandler;
import msi.gama.common.preferences.Pref;
import msi.gaml.types.IType;

public class ThemeHelper {

	public static final String E4_DARK_THEME_ID = "org.eclipse.e4.ui.css.theme.e4_dark";
	public static final String E4_LIGHT_THEME_ID = "org.eclipse.e4.ui.css.theme.e4_default";
	public static final String E4_CLASSIC_THEME_ID = "org.eclipse.e4.ui.css.theme.e4_classic";
	public static final String THEME_ID_PREFERENCE = "themeid";

	public static final Pref<Boolean> CORE_THEME_FOLLOW =
		create("pref_theme_follow", "Follow OS theme", true, IType.BOOL, false).in(NAME, APPEARANCE)
			.deactivates("pref_theme_light").onChange(yes -> {
				chooseThemeBasedOnPreferences();
			});

	public static final Pref<Boolean> CORE_THEME_LIGHT =
		create("pref_theme_light", "Light theme", true, IType.BOOL, false).in(NAME, APPEARANCE).restartRequired()
			.onChange(v -> {
				chooseThemeBasedOnPreferences();
			});

	public static void chooseThemeBasedOnPreferences() {
		final var isGamaAlreadyDark = isDark();
		if ( CORE_THEME_FOLLOW.getValue() ) {
			final var isOSDark = Display.isSystemDarkTheme();
			if ( isGamaAlreadyDark && isOSDark )
				return;
			if ( isGamaAlreadyDark && !isOSDark ) {
				changeToLight();
				return;
			}
			if ( !isGamaAlreadyDark && isOSDark ) {
				changeToDark();
				return;
			}
			if ( !isGamaAlreadyDark && !isOSDark )
				return;
		} else {
			if ( CORE_THEME_LIGHT.getValue() )
				changeToLight();
			else changeToDark();
		}
	}

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
		chooseThemeBasedOnPreferences();
	}

	public static void changeToLight() {
		changeTo(E4_LIGHT_THEME_ID);
	}

	public static void changeToDark() {
		changeTo(E4_DARK_THEME_ID);
	}

	private static void changeTo(String id) {
		final var themeEngine = getContext().get(IThemeEngine.class);
		if ( themeEngine == null ) {
			// early in the cycle
			getContext().set(E4Application.THEME_ID, id);
			getPreferences().put(THEME_ID_PREFERENCE, id);
			return;
		}
		final var theme = (themeEngine.getActiveTheme());
		if ( theme != null ) {
			if ( theme.getId().startsWith(id) )
				return;
		}
		getPreferences().put(THEME_ID_PREFERENCE, id);
		themeEngine.setTheme(id, true);
	}

	public static void addListener(IThemeListener l) {
		if ( !listeners.contains(l) )
			listeners.add(l);
	}

	public static void removeListener(IThemeListener l) {
		listeners.remove(l);
	}

	private static String getEclipsePreference() {
		return getPreferences().get(THEME_ID_PREFERENCE, null);
	}

	private static IEclipsePreferences getPreferences() {
		return InstanceScope.INSTANCE.getNode(FrameworkUtil.getBundle(ThemeEngine.class).getSymbolicName());
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
		if ( themeEngine == null ) {
			final var id = (String) getContext().get(E4Application.THEME_ID);
			return (id != null) && (id.startsWith(E4_DARK_THEME_ID));
		}
		final var theme = (themeEngine.getActiveTheme());

		final var result = (theme != null) && (theme.getId().startsWith(E4_DARK_THEME_ID));
		return result;
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