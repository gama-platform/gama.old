/*********************************************************************************************
 *
 *
 * 'ContentAssistContextFactory.java', in plugin 'msi.gama.lang.gaml.ui', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.contentassist;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.ui.editor.contentassist.antlr.FollowElement;
import org.eclipse.xtext.ui.editor.contentassist.antlr.ParserBasedContentAssistContextFactory.FollowElementCalculator;
import org.eclipse.xtext.ui.editor.contentassist.antlr.ParserBasedContentAssistContextFactory.PartialStatefulFactory;

import com.google.common.collect.Multimap;

import msi.gama.runtime.GAMA;

/**
 * The class ContentAssistContextFactory.
 *
 * @author drogoul
 * @since 9 avr. 2013
 *
 */
public class ContentAssistContextFactory extends PartialStatefulFactory {

	Map<AbstractElement, Integer> recurse = new LinkedHashMap<>();
	boolean stop = false;

	/**
	 * AD 08/13 : Workaround for a bug manifesting itself as an infinite
	 * recursion over an AlternativesImpl element. The choice here is to allow
	 * for 10 occurrences of the element to be computed and then fall back to
	 * the caller.
	 */
	@Override
	protected void computeFollowElements(final FollowElementCalculator calculator, final FollowElement element,
			final Multimap<Integer, List<AbstractElement>> visited) {
		if (stop) {
			return;
		}
		final AbstractElement e = element.getGrammarElement();
		if (!recurse.containsKey(e)) {
			recurse.put(e, 1);
		} else {
			recurse.put(e, recurse.get(e) + 1);
		}
		if (recurse.get(e) > 3) {
			GAMA.getGui().debug("Infinite recursion detected in completion proposal for " + e);
			stop = true;
			recurse.clear();
			return;
		}

		// scope.getGui().debug(" Computing FollowElement -- + visited : " +
		// element +
		// " ; number of times : " + recurse.get(e));
		super.computeFollowElements(calculator, element, visited);
	}

}
