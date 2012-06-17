import org.eclipse.core.commands.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

public class DisableAutomaticBuildHandler extends AbstractHandler implements IHandler {

	public DisableAutomaticBuildHandler() {}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		IWorkspaceDescription wd = ResourcesPlugin.getWorkspace().getDescription();
		wd.setAutoBuilding(false);
		try {
			ResourcesPlugin.getWorkspace().setDescription(wd);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}

		return null;
	}
}
