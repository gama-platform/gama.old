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

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.ast.ISyntacticElement;
import msi.gaml.compilation.ast.ISyntacticElement.SyntacticVisitor;
import msi.gaml.compilation.ast.SyntacticModelElement;
import ummisco.gama.ui.resources.IGamaColors;

public class WrappedSyntacticContent extends VirtualContent implements Comparable<WrappedSyntacticContent> {

	final ISyntacticElement element;
	final String uriFragment;

	public WrappedSyntacticContent(final Object root, final ISyntacticElement e) {
		super(root, e instanceof SyntacticModelElement ? "Contents" : GAMA.getGui().getGamlLabelProvider().getText(e));
		element = e;
		uriFragment = element == null ? null : EcoreUtil.getURI(element.getElement()).toString();
	}

	@Override
	public boolean hasChildren() {
		if (!element.hasChildren())
			return false;
		if (element.isSpecies() || element.isExperiment() || element instanceof SyntacticModelElement)
			return true;
		return false;
	}

	@Override
	public Object[] getNavigatorChildren() {
		if (!hasChildren())
			return null;
		final List<WrappedSyntacticContent> children = new ArrayList();
		element.visitAllChildren(new SyntacticVisitor() {

			@Override
			public void visit(final ISyntacticElement element) {
				children.add(new WrappedSyntacticContent(WrappedSyntacticContent.this, element));

			}
		});
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
			GAMA.getGui().editModel(element.getElement());
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
		return isURIAProblem(uriFragment);
	}

	public boolean isURIAProblem(final String fragment) {
		if (getParent() instanceof WrappedSyntacticContent)
			return ((WrappedSyntacticContent) getParent()).isURIAProblem(fragment);
		else {
			final IFile file = (IFile) getParent();
			try {
				final IMarker[] markers = file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
				if (markers.length == 0)
					return false;
				for (final IMarker marker : markers) {
					final String s = marker.getAttribute("URI_KEY", null);
					if (s == null)
						return false;
					if (s.startsWith(fragment))
						return true;
					return false;
				}
			} catch (final CoreException e) {
				return false;
			}
		}
		return false;
	}

	@Override
	public int findMaxProblemSeverity() {
		return IMarker.SEVERITY_ERROR;
	}

}
