package msi.gama.common.interfaces;

import msi.gaml.compilation.ast.ISyntacticElement;

public interface IGamlLabelProvider {

	String getText(ISyntacticElement element);

	Object getImage(ISyntacticElement element);

}
