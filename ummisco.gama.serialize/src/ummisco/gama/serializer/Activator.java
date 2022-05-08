package ummisco.gama.serializer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import msi.gama.util.file.json.Jsoner;
import ummisco.gama.serializer.inject.ConverterJSON;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		Jsoner.streamConverter = new ConverterJSON();		
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
	}
}
