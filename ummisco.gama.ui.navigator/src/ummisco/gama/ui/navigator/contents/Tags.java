/*******************************************************************************************************
 *
 * Tags.java, in ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.navigator.contents;

import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import one.util.streamex.StreamEx;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaIcons;

/**
 * Class ImportFolder.
 *
 * @author drogoul
 * @since 5 févr. 2015
 *
 */
public class Tags extends VirtualContent<WrappedFile> {

	/** The tags. */
	final Map<String, String> tags;

	/** The search. */
	final boolean search;

	/**
	 * @param root
	 * @param name
	 */
	public Tags(final WrappedFile root, final Map<String, String> object, final String name,
			final boolean doubleClickForSearching) {
		super(root, name);
		tags = object;
		search = doubleClickForSearching;
	}

	/**
	 * Method hasChildren()
	 *
	 * @see ummisco.gama.ui.navigator.contents.VirtualContent#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		return !tags.isEmpty();
	}

	/**
	 * Method getNavigatorChildren()
	 *
	 * @see ummisco.gama.ui.navigator.contents.VirtualContent#getNavigatorChildren()
	 */
	@Override
	public Object[] getNavigatorChildren() {
		if (tags.isEmpty()) return EMPTY;
		return StreamEx.ofKeys(tags).map(each -> new Tag(this, each, tags.get(each), search)).toArray();
	}

	/**
	 * Method getImage()
	 *
	 * @see ummisco.gama.ui.navigator.contents.VirtualContent#getImage()
	 */
	@Override
	public Image getImage() { return GamaIcons.create(IGamaIcons.ATTRIBUTES).image(); }

	/**
	 * Method getColor()
	 *
	 * @see ummisco.gama.ui.navigator.contents.VirtualContent#getColor()
	 */
	@Override
	public Color getColor() {
		for (String s : tags.values()) {
			if (s.contains("built-in attribute")) return GamaColors.system(SWT.COLOR_DARK_RED);
		}
		return null;
	}

	@Override
	public void getSuffix(final StringBuilder sb) {}

	@Override
	public int findMaxProblemSeverity() {
		return 0;
	}

	@Override
	public ImageDescriptor getOverlay() { return null; }

	@Override
	public VirtualContentType getType() { return VirtualContentType.CATEGORY; }

	@Override
	public String getStatusMessage() { return "Tags"; }

}
