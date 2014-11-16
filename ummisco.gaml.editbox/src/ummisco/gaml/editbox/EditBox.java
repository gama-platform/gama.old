package ummisco.gaml.editbox;

import org.eclipse.core.runtime.*;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import ummisco.gaml.editbox.impl.BoxProviderRegistry;

public class EditBox extends AbstractUIPlugin {

	private static final String ENABLED = "ENABLED";

	public static final String PLUGIN_ID = "ummisco.gaml.editbox";

	private static EditBox plugin;

	private BoxProviderRegistry registry;

	public EditBox() {}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static EditBox getDefault() {
		return plugin;
	}

	public IBoxProvider getGamlProvider() {
		return getProviderRegistry().providerForName("GAML");
	}

	public BoxProviderRegistry getProviderRegistry() {
		return registry == null ? (registry = new BoxProviderRegistry()) : registry;
	}

	public boolean isEnabled() {
		if ( getPreferenceStore().contains(ENABLED) ) { return getPreferenceStore().getBoolean(ENABLED); }
		return false;
	}

	public static void logError(final Object source, final String msg, final Throwable error) {
		String src = "";
		if ( source instanceof Class ) {
			src = ((Class) source).getName();
		} else if ( source != null ) {
			src = source.getClass().getName();
		}
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, 1, "[" + src + "] " + msg, error));
	}

}
