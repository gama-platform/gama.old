package msi.gama.util.file;

import org.geotools.util.SimpleInternationalString;
import org.opengis.util.InternationalString;
import org.opengis.util.ProgressListener;

import msi.gama.common.interfaces.IStatusDisplayer;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

class ProgressCounter implements ProgressListener {

	final IScope scope;
	final String name;
	float progress;

	ProgressCounter(final IScope scope, final String name) {
		this.scope = scope;
		this.name = name;
	}

	IStatusDisplayer getDisplayer() {
		return scope.getGui().getStatus(scope);
	}

	@Override
	public void complete() {
		getDisplayer().setSubStatusCompletion(1d);
	}

	@Override
	public void dispose() {
		getDisplayer().endSubStatus(name.toString());
	}

	@Override
	public void exceptionOccurred(final Throwable arg0) {
		GAMA.reportAndThrowIfNeeded(scope, GamaRuntimeException.create(arg0, scope), true);
	}

	@Override
	public float getProgress() {
		return progress;
	}

	@Override
	public InternationalString getTask() {
		return new SimpleInternationalString(name);
	}

	@Override
	public boolean isCanceled() {
		return scope.interrupted();
	}

	@Override
	public void progress(final float p) {
		progress = p;
		getDisplayer().setSubStatusCompletion(progress);
	}

	@Override
	public void setCanceled(final boolean cancel) {
		getDisplayer().endSubStatus(name.toString());
	}

	@Override
	public void setTask(final InternationalString n) {}

	@Override
	public void started() {
		getDisplayer().beginSubStatus(name.toString());
	}

	@Override
	public void warningOccurred(final String source, final String location, final String warning) {
		GAMA.reportAndThrowIfNeeded(scope, GamaRuntimeException.warning(warning, scope), false);
	}

}