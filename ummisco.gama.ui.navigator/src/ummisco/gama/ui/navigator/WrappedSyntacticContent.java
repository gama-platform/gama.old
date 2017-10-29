/*********************************************************************************************
 *
 * 'WrappedSyntacticContent.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling
 * and simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import gnu.trove.map.hash.TObjectIntHashMap;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.ast.ISyntacticElement;
import ummisco.gama.ui.resources.IGamaColors;

public class WrappedSyntacticContent extends VirtualContent implements Comparable<WrappedSyntacticContent> {

	public static class WrappedModelContent extends WrappedSyntacticContent {

		TObjectIntHashMap<String> uriProblems;

		public WrappedModelContent(final IFile file, final ISyntacticElement e) {
			super(file, e, "Contents");
			try {
				final IMarker[] markers = file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
				for (final IMarker marker : markers) {
					final String s = marker.getAttribute("URI_KEY", null);
					final int severity = marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
					if (uriProblems == null)
						uriProblems = new TObjectIntHashMap<String>();
					uriProblems.put(s, severity);
				}
			} catch (final CoreException ce) {}
		}

		@Override
		public int getURIProblem(final String fragment) {
			// final IFile file = (IFile) getParent();
			if (uriProblems == null)
				return -1;
			final int[] severity = new int[] { -1 };
			uriProblems.forEachEntry((s, arg1) -> {
				if (s != null && s.startsWith(fragment)) {
					severity[0] = arg1;
					return false;
				}
				return true;
			});
			return severity[0];

		}

		@Override
		public boolean hasChildren() {
			return true;
		}

	}

	public static class WrappedExperimentContent extends WrappedSyntacticContent {

		TObjectIntHashMap<String> uriProblems;

		public WrappedExperimentContent(final IFile file, final ISyntacticElement e) {
			super(file, e, GAMA.getGui().getGamlLabelProvider().getText(e));
			try {
				final IMarker[] markers = file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
				for (final IMarker marker : markers) {
					final String s = marker.getAttribute("URI_KEY", null);
					final int severity = marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
					if (uriProblems == null)
						uriProblems = new TObjectIntHashMap<String>();
					uriProblems.put(s, severity);
				}
			} catch (final CoreException ce) {}
		}

		@Override
		public int getURIProblem(final String fragment) {
			if (fragment == null)
				return -1;
			// final IFile file = (IFile) getParent();
			if (uriProblems == null)
				return -1;
			final int[] severity = new int[] { -1 };
			uriProblems.forEachEntry((s, arg1) -> {
				if (s.startsWith(fragment)) {
					severity[0] = arg1;
					return false;
				}
				return true;
			});
			return severity[0];

		}

		@Override
		public boolean hasChildren() {
			return true;
		}

	}

	final ISyntacticElement element;
	final String uri;

	WrappedSyntacticContent(final WrappedSyntacticContent parent, final ISyntacticElement e) {
		this(parent, e, GAMA.getGui().getGamlLabelProvider().getText(e));
	}

	public WrappedSyntacticContent(final Object root, final ISyntacticElement e, final String name) {
		super(root, name == null ? GAMA.getGui().getGamlLabelProvider().getText(e) : name);
		element = e;
		uri = element == null || element.getElement() == null ? null
				: EcoreUtil.getURI(element.getElement()).toString();
	}

	@Override
	public boolean hasChildren() {
		if (!element.hasChildren())
			return false;
		if (element.isSpecies() || element.isExperiment())
			return true;
		return false;
	}

	@Override
	public Object[] getNavigatorChildren() {
		if (!hasChildren())
			return null;
		final List<WrappedSyntacticContent> children = new ArrayList<>();
		element.visitAllChildren(
				element -> children.add(new WrappedSyntacticContent(WrappedSyntacticContent.this, element)));
		return children.toArray();
	}

	@Override
	public Image getImage() {
		return (Image) GAMA.getGui().getGamlLabelProvider().getImage(element);
	}

	@Override
	public Color getColor() {
		return IGamaColors.BLACK.inactive();
	}

	@Override
	public boolean handleDoubleClick() {
		if (element.isExperiment()) {
			GAMA.getGui().runModel(getParent(), element.getName());
		} else
			GAMA.getGui().editModel(null, element.getElement());
		return true;
	}

	@Override
	public int compareTo(final WrappedSyntacticContent o) {
		final ISyntacticElement e = o.element;
		if (element.isSpecies()) {
			if (e.isSpecies())
				return getName().compareTo(o.getName());
			if (element.getKeyword().equals(IKeyword.GRID))
				return 1;
			return 1;
		} else if (e.isSpecies()) {
			return -1;
		} else
			return getName().compareTo(o.getName());

	}

	@Override
	public boolean canBeDecorated() {
		return decorationSeverity() > 0;
	}

	public int decorationSeverity() {
		return getURIProblem(uri);
	}

	public int getURIProblem(final String fragment) {
		return ((WrappedSyntacticContent) getParent()).getURIProblem(fragment);
	}

	@Override
	public int findMaxProblemSeverity() {
		return IMarker.SEVERITY_ERROR;
	}

}
