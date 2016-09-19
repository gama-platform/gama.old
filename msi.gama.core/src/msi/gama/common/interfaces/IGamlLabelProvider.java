package msi.gama.common.interfaces;

import msi.gaml.compilation.ISyntacticElement;

public interface IGamlLabelProvider {

	String getText(ISyntacticElement element);

	Object getImage(ISyntacticElement element);

}
