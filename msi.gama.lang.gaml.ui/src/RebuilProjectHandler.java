import org.eclipse.core.commands.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.IProgressConstants2;

public class RebuilProjectHandler extends AbstractHandler implements IHandler {

	public RebuilProjectHandler() {}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		Job buildJob = new Job("Cleaning and building all models") {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				monitor.beginTask("", 100);
				try {
					ResourcesPlugin.getWorkspace().build(
						IncrementalProjectBuilder.INCREMENTAL_BUILD,
						new SubProgressMonitor(monitor, 100));
				} catch (CoreException e) {
					return e.getStatus();
				} finally {
					monitor.done();
				}
				return Status.OK_STATUS;
			}

			@Override
			public boolean belongsTo(final Object family) {
				return ResourcesPlugin.FAMILY_MANUAL_BUILD == family;
			}
		};
		buildJob.setUser(true);
		buildJob.setProperty(IProgressConstants2.SHOW_IN_TASKBAR_ICON_PROPERTY, Boolean.TRUE);
		buildJob.schedule();

		return null;
	}
}
