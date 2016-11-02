/*********************************************************************************************
 *
 * 'PartialGamlContentAssistParser.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.contentassist.antlr;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.ui.codetemplates.ui.partialEditing.IPartialContentAssistParser;
import org.eclipse.xtext.ui.editor.contentassist.antlr.FollowElement;
import org.eclipse.xtext.ui.editor.contentassist.antlr.internal.AbstractInternalContentAssistParser;
import org.eclipse.xtext.util.PolymorphicDispatcher;

/*
 * Template CodetemplatesGeneratorFragment.xpt
 */
public class PartialGamlContentAssistParser extends GamlParser implements IPartialContentAssistParser {

	private AbstractRule rule;

	@Override
	public void initializeFor(AbstractRule rule) {
		this.rule = rule;
	}
	
	@Override
	protected Collection<FollowElement> getFollowElements(AbstractInternalContentAssistParser parser) {
		if (rule == null || rule.eIsProxy())
			return Collections.emptyList();
		String methodName = "entryRule" + rule.getName();
		PolymorphicDispatcher<Collection<FollowElement>> dispatcher = 
			new PolymorphicDispatcher<Collection<FollowElement>>(methodName, 0, 0, Collections.singletonList(parser));
		dispatcher.invoke();
		return parser.getFollowElements();
	}

}
