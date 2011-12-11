package msi.gama.lang.gaml.descript;

import org.eclipse.emf.ecore.resource.Resource;

public class GamlDescriptIO {

	private IUpdateOnChange		callback;
	private volatile boolean	canRun;
	private volatile boolean	isRunning;

	/**
	 * The solution of Bill Pugh
	 * see http://en.wikipedia.org/wiki/Singleton_pattern#The_solution_of_Bill_Pugh
	 * 
	 * @return GamlDescriptIO unique instance of singleton
	 */
	public final synchronized static GamlDescriptIO getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {

		private static final GamlDescriptIO	INSTANCE	= new GamlDescriptIO();
	}

	// Private constructor prevents instantiation from other classes
	private GamlDescriptIO() {
		canRun = true;
		isRunning = false;
	}

	/**
	 * @see IUpdateOnChange#update()
	 * @param c the callback that implements IUpdateOnChange
	 */
	public void setCallback(final IUpdateOnChange c) {
		this.callback = c;
	}

	public void process(final Resource r) throws Exception {
		if ( canRun ) {
			isRunning = true;
			try {
				callback.update(r);
			} finally {
				isRunning = false;
			}
		}
	}

	public void canRun(final boolean b) {
		canRun = b;
	}

	public boolean isBuilding() {
		return isRunning;
	}

	public IUpdateOnChange getCallback() {
		return callback;
	}
}
