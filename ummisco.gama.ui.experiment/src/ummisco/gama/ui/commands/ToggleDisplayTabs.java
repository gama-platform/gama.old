package ummisco.gama.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class ToggleDisplayTabs extends AbstractHandler {

	// NOT YET READY FOR PRIME TIME
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		if (false){
		ArrangeDisplayViews.keepTabs = !ArrangeDisplayViews.keepTabs;
		ArrangeDisplayViews
				.execute(new LayoutTreeConverter().convertCurrentLayout(ArrangeDisplayViews.listDisplayViews()));
		return this;}
		return null;
	}

}
