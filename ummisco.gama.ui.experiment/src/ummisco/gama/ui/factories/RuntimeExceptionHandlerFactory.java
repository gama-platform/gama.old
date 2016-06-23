package ummisco.gama.ui.factories;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import msi.gama.common.interfaces.IRuntimeExceptionHandler;
import ummisco.gama.ui.commands.RuntimeExceptionHandler;

public class RuntimeExceptionHandlerFactory extends AbstractServiceFactory {

	IRuntimeExceptionHandler handler;

	public IRuntimeExceptionHandler getHandler() {
		if (handler == null) {
			handler = new RuntimeExceptionHandler();
		}
		if (!handler.isRunning())
			handler.start();
		return handler;
	}

	@Override
	public Object create(final Class serviceInterface, final IServiceLocator parentLocator,
			final IServiceLocator locator) {
		return getHandler();
	}

}
