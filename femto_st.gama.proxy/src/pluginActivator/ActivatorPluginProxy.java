package pluginActivator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class Activator for femto_st.gama.proxy plugin
 */
public class ActivatorPluginProxy implements BundleActivator {
	
	static {
		DEBUG.OFF();
	}
	
	@Override
	public void start(final BundleContext context) throws Exception 
	{	
		DEBUG.OUT("PROXY PLUGIN ACTIVATED");
	}

	@Override
	public void stop(final BundleContext context) throws Exception 
	{
		DEBUG.OUT("PROXY PLUGIN DESACTIVATED");
	}

}