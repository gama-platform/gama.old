/**
 * Created by drogoul, 9 avr. 2013
 * 
 */
package msi.gama.lang.gaml.ui.contentassist;

import java.util.*;
import msi.gama.common.util.GuiUtils;
import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.ui.editor.contentassist.antlr.*;
import org.eclipse.xtext.ui.editor.contentassist.antlr.ParserBasedContentAssistContextFactory.FollowElementCalculator;
import org.eclipse.xtext.ui.editor.contentassist.antlr.ParserBasedContentAssistContextFactory.StatefulFactory;
import com.google.common.collect.Multimap;

/**
 * The class ContentAssistContextFactory.
 * 
 * @author drogoul
 * @since 9 avr. 2013
 * 
 */
public class ContentAssistContextFactory extends StatefulFactory {

	@Override
	protected void computeFollowElements(final Collection<FollowElement> followElements,
		final Collection<AbstractElement> result) {
		// stop = false;
		// recurse.clear();
		// GuiUtils.debug(" Computing FollowElements : " + followElements);
		super.computeFollowElements(followElements, result);
	}

	@Override
	protected void computeFollowElements(final FollowElementCalculator calculator, final FollowElement element) {
		// GuiUtils.debug(" Computing FollowElement : " + element);
		super.computeFollowElements(calculator, element);
	}

	Map<AbstractElement, Integer> recurse = new LinkedHashMap();
	boolean stop = false;

	/**
	 * Workaround for a bug manifesting itself as an infinite recursion over an AlternativesImpl element.
	 * The choice here is to allow for 10 occurences of the element to be computed and then fall back to the caller.
	 */
	@Override
	protected void computeFollowElements(final FollowElementCalculator calculator, final FollowElement element,
		final Multimap<Integer, List<AbstractElement>> visited) {
		if ( stop ) { return; }
		AbstractElement e = element.getGrammarElement();
		if ( !recurse.containsKey(e) ) {
			recurse.put(e, 1);
		} else {
			recurse.put(e, recurse.get(e) + 1);
		}
		if ( recurse.get(e) > 3 ) {
			GuiUtils.debug("Infinite recursion detected in completion proposal for " + e);
			stop = true;
			recurse.clear();
			return;
		}

		// GuiUtils.debug(" Computing FollowElement -- + visited : " + element +
		// " ; number of times : " + recurse.get(e));
		super.computeFollowElements(calculator, element, visited);
	}

}
