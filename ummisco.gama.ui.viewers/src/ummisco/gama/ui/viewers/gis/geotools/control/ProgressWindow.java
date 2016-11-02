/*********************************************************************************************
 *
 * 'ProgressWindow.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.gis.geotools.control;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;
import org.geotools.util.SimpleInternationalString;
import org.opengis.util.InternationalString;
import org.opengis.util.ProgressListener;

/**
 * Wrapper for geotools' {@link ProgressListener}.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 *
 *
 * @source $URL$
 */
public class ProgressWindow extends ProgressMonitorDialog implements ProgressListener {

    private IProgressMonitor monitor;
    private String description;
    private final int taskSize;
    private float percent;
    private float previousPercent = -1;

    public ProgressWindow( Shell parent, int taskSize ) {
        super(parent);
        this.taskSize = taskSize;
        monitor = getProgressMonitor();
    }

    public boolean isCanceled() {
        return monitor.isCanceled();
    }

    public void setCanceled( boolean value ) {
        monitor.setCanceled(value);
    }

    public void setTask( InternationalString task ) {
        setDescription(task.toString());
    }

    public InternationalString getTask() {
        return new SimpleInternationalString(getDescription());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public void started() {
        monitor.beginTask(description, taskSize);
    }

    public void progress( float percent ) {
        this.percent = percent;
        if (previousPercent == -1) {
            monitor.worked((int) percent);
        } else {
            monitor.worked((int) (percent - previousPercent));
        }
        previousPercent = percent;
    }

    public float getProgress() {
        return percent;
    }

    public void complete() {
        monitor.done();
    }

    public void dispose() {
    }

    public void warningOccurred( String source, String location, String warning ) {

    }

    public void exceptionOccurred( Throwable exception ) {

    }

}
