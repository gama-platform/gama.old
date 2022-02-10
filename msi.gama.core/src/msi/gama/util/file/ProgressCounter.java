/*******************************************************************************************************
 *
 * ProgressCounter.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file;

import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;

import org.geotools.util.SimpleInternationalString;
import org.opengis.util.InternationalString;
import org.opengis.util.ProgressListener;

import msi.gama.common.interfaces.IStatusDisplayer;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The Class ProgressCounter.
 */
public class ProgressCounter implements ProgressListener, IIOReadProgressListener {

	/** The scope. */
	final IScope scope;

	/** The name. */
	final String name;

	/** The progress. */
	float progress;

	/**
	 * Instantiates a new progress counter.
	 *
	 * @param scope
	 *            the scope
	 * @param name
	 *            the name
	 */
	public ProgressCounter(final IScope scope, final String name) {
		this.scope = scope;
		this.name = name;
	}

	/**
	 * Gets the displayer.
	 *
	 * @return the displayer
	 */
	IStatusDisplayer getDisplayer() { return scope.getGui().getStatus(); }

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
	public float getProgress() { return progress; }

	@Override
	public InternationalString getTask() { return new SimpleInternationalString(name); }

	@Override
	public boolean isCanceled() { return scope.interrupted(); }

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

	@Override
	public void sequenceStarted(final ImageReader source, final int minIndex) {}

	@Override
	public void sequenceComplete(final ImageReader source) {}

	@Override
	public void imageStarted(final ImageReader source, final int imageIndex) {
		getDisplayer().beginSubStatus(name.toString());
	}

	@Override
	public void imageProgress(final ImageReader source, final float percentageDone) {
		progress(percentageDone);
	}

	@Override
	public void imageComplete(final ImageReader source) {
		getDisplayer().setSubStatusCompletion(1d);
		getDisplayer().endSubStatus(name.toString());
	}

	@Override
	public void thumbnailStarted(final ImageReader source, final int imageIndex, final int thumbnailIndex) {}

	@Override
	public void thumbnailProgress(final ImageReader source, final float percentageDone) {}

	@Override
	public void thumbnailComplete(final ImageReader source) {}

	@Override
	public void readAborted(final ImageReader source) {
		getDisplayer().endSubStatus(name.toString());
	}

}