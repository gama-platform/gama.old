package ummisco.gama.ui.modeling.editbox;


public class TextBoxBuilder extends BoxBuilderImpl {

	protected void addbox0(int start, int end, int offset) {

		if (offset == currentbox.offset) {
			if (currentbox.hasChildren || (emptyPrevLine && currentbox.parent != null)) {
				currentbox = newbox(start, end, offset, currentbox.parent);
				updateParentEnds(currentbox);
			} else if (end > currentbox.end) {
				currentbox.end = end;
				if (currentbox.tabsStart < 0 && lineHasStartTab)
					currentbox.tabsStart = start;
				updateMaxEndOffset(start, currentbox);
				updateParentEnds(currentbox);
			}
		} else if (offset > currentbox.offset) {
			currentbox = newbox(start, end, offset, currentbox);
			updateParentEnds(currentbox);
		} else if (currentbox.parent != null) {
			currentbox = currentbox.parent;
			addbox0(start, end, offset);
		}

	}
}
