/*******************************************************************************************************
 *
 * WrappedSyntacticContent.java, in ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.navigator.contents;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import msi.gama.common.interfaces.IGamlLabelProvider;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.ast.ISyntacticElement;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class WrappedSyntacticContent.
 */
public class WrappedSyntacticContent extends VirtualContent<VirtualContent<?>>
		implements Comparable<WrappedSyntacticContent> {

	/** The element. */
	public final ISyntacticElement element;

	/** The uri. */
	final URI uri;

	/**
	 * Instantiates a new wrapped syntactic content.
	 *
	 * @param parent
	 *            the parent
	 * @param e
	 *            the e
	 */
	private WrappedSyntacticContent(final WrappedSyntacticContent parent, final ISyntacticElement e) {
		this(parent, e, WorkbenchHelper.getService(IGamlLabelProvider.class).getText(e));
	}

	/**
	 * Instantiates a new wrapped syntactic content.
	 *
	 * @param root
	 *            the root
	 * @param e
	 *            the e
	 * @param name
	 *            the name
	 */
	public WrappedSyntacticContent(final VirtualContent<?> root, final ISyntacticElement e, final String name) {
		super(root, name == null ? WorkbenchHelper.getService(IGamlLabelProvider.class).getText(e) : name);
		element = e;
		uri = element == null || element.getElement() == null ? null : EcoreUtil.getURI(element.getElement());
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public WrappedGamaFile getFile() { return ((WrappedSyntacticContent) getParent()).getFile(); }

	@Override
	public boolean hasChildren() {
		if (!element.hasChildren()) return false;
		if (element.isSpecies()) return true;
		return false;
	}

	@Override
	public Object[] getNavigatorChildren() {
		if (!hasChildren()) return null;
		final List<WrappedSyntacticContent> children = new ArrayList<>();
		element.visitAllChildren(elt -> children.add(new WrappedSyntacticContent(WrappedSyntacticContent.this, elt)));
		return children.toArray();
	}

	@Override
	public Image getImage() { return (Image) WorkbenchHelper.getService(IGamlLabelProvider.class).getImage(element); }

	// @Override
	// public Color getColor() {
	// return ThemeHelper.isDark() ? IGamaColors.VERY_LIGHT_GRAY.color() : IGamaColors.BLACK.inactive();
	// }

	@Override
	public boolean handleDoubleClick() {
		GAMA.getGui().editModel(null, element.getElement());
		return true;
	}

	@Override
	public boolean handleSingleClick() {

		GAMA.getGui().editModel(null, element.getElement());
		return true;
	}

	/**
	 * Gets the element.
	 *
	 * @return the element
	 */
	public ISyntacticElement getElement() { return element; }

	@Override
	public int compareTo(final WrappedSyntacticContent o) {
		final var e = o.element;
		if (element.isSpecies()) {
			if (e.isSpecies()) return getName().compareTo(o.getName());
			// if (IKeyword.GRID.equals(element.getKeyword())) {}
			return 1;
		}
		if (e.isSpecies()) return -1;
		return getName().compareTo(o.getName());

	}

	/**
	 * Gets the URI problem.
	 *
	 * @param fragment
	 *            the fragment
	 * @return the URI problem
	 */
	public int getURIProblem(final URI fragment) {
		return getFile().getURIProblem(fragment);
	}

	@Override
	public int findMaxProblemSeverity() {
		return getURIProblem(uri);
	}

	@Override
	public void getSuffix(final StringBuilder sb) {}

	@Override
	public ImageDescriptor getOverlay() {
		final var severity = getURIProblem(uri);
		if (severity != -1) return DESCRIPTORS.get(severity);
		return null;
	}

	@Override
	public VirtualContentType getType() { return VirtualContentType.GAML_ELEMENT; }

	@Override
	public String getStatusMessage() { return getName(); }

	@Override
	public GamaUIColor getStatusColor() { return IGamaColors.BLACK; }

	@Override
	public Image getStatusImage() { return getImage(); }

}
