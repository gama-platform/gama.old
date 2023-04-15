/*******************************************************************************************************
 *
 * GamlSemanticHighlightingCalculator.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling
 * and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.highlight;

import static msi.gama.lang.gaml.ui.highlight.DelegateHighlightingConfiguration.ASSIGN_ID;
import static msi.gama.lang.gaml.ui.highlight.DelegateHighlightingConfiguration.FACET_ID;
import static msi.gama.lang.gaml.ui.highlight.DelegateHighlightingConfiguration.KEYWORD_ID;
import static msi.gama.lang.gaml.ui.highlight.DelegateHighlightingConfiguration.NUMBER_ID;
import static msi.gama.lang.gaml.ui.highlight.DelegateHighlightingConfiguration.OPERATOR_ID;
import static msi.gama.lang.gaml.ui.highlight.DelegateHighlightingConfiguration.PRAGMA_ID;
import static msi.gama.lang.gaml.ui.highlight.DelegateHighlightingConfiguration.RESERVED_ID;
import static msi.gama.lang.gaml.ui.highlight.DelegateHighlightingConfiguration.TASK_ID;
import static msi.gama.lang.gaml.ui.highlight.DelegateHighlightingConfiguration.TYPE_ID;
import static msi.gama.lang.gaml.ui.highlight.DelegateHighlightingConfiguration.UNIT_ID;
import static msi.gama.lang.gaml.ui.highlight.DelegateHighlightingConfiguration.VARDEF_ID;
import static msi.gama.lang.gaml.ui.highlight.DelegateHighlightingConfiguration.VARIABLE_ID;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ide.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.tasks.ITaskFinder;
import org.eclipse.xtext.tasks.Task;
import org.eclipse.xtext.util.CancelIndicator;

import com.google.inject.Inject;

import msi.gama.common.util.StringUtils;
import msi.gama.lang.gaml.EGaml;
import msi.gama.lang.gaml.gaml.ArgumentDefinition;
import msi.gama.lang.gaml.gaml.ArgumentPair;
import msi.gama.lang.gaml.gaml.Facet;
import msi.gama.lang.gaml.gaml.GamlDefinition;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.HeadlessExperiment;
import msi.gama.lang.gaml.gaml.Parameter;
import msi.gama.lang.gaml.gaml.Pragma;
import msi.gama.lang.gaml.gaml.S_Assignment;
import msi.gama.lang.gaml.gaml.S_Definition;
import msi.gama.lang.gaml.gaml.S_Display;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.gaml.StringLiteral;

/**
 *
 * @author Pierrick cf. http://www.eclipse.org/Xtext/documentation/latest/xtext.html# highlighting
 *
 */
public class GamlSemanticHighlightingCalculator implements ISemanticHighlightingCalculator {

	/** The task finder. */
	@Inject private ITaskFinder taskFinder;

	/** The assignments. */
	private static Set<String> ASSIGNMENTS =
			new HashSet<>(Arrays.asList("<-", "<<", ">>", "->", "<+", ">-", "<<+", ">>-", "+<-"));

	/** The acceptor. */
	private IHighlightedPositionAcceptor acceptor;

	/** The done. */
	Set<INode> done = new HashSet<>();

	@Override
	public void provideHighlightingFor(final XtextResource resource, final IHighlightedPositionAcceptor arg1,
			final CancelIndicator arg2) {
		if (resource == null) return;
		acceptor = arg1;
		final var root = resource.getAllContents();
		while (root.hasNext()) { process(root.next()); }
		done.clear();
		highlightTasks(resource, acceptor);
	}

	/**
	 * Highlight tasks.
	 *
	 * @param resource
	 *            the resource
	 * @param acceptor
	 *            the acceptor
	 */
	protected void highlightTasks(final XtextResource resource, final IHighlightedPositionAcceptor acceptor) {
		final var tasks = taskFinder.findTasks(resource);
		for (final Task task : tasks) { acceptor.addPosition(task.getOffset(), task.getTagLength(), TASK_ID); }
	}

	/**
	 * Process.
	 *
	 * @param object
	 *            the object
	 */
	void process(final EObject object) {
		if (object == null) return;
		process(object, object.eClass());
	}

	/**
	 * Process.
	 *
	 * @param object
	 *            the object
	 * @param clazz
	 *            the clazz
	 */
	void process(final EObject object, final EClass clazz) {
		final var id = clazz.getClassifierID();

		switch (id) {
			case GamlPackage.PRAGMA:
				setStyle(object, PRAGMA_ID, ((Pragma) object).getName(), false);
				break;
			case GamlPackage.SASSIGNMENT:
				final var s = ((S_Assignment) object).getKey();
				setStyle(object, ASSIGN_ID, s, false);
				break;
			case GamlPackage.FACET:
				final var f = (Facet) object;
				final var key = f.getKey();
				if (ASSIGNMENTS.contains(key)) {
					setStyle(object, ASSIGN_ID, 0);
				} else {
					setStyle(object, FACET_ID, 0);
					if (key.startsWith("type")) {
						setStyle(TYPE_ID, NodeModelUtils.getNode(f.getExpr()));
					} else if (f.getName() != null) { setStyle(object, VARDEF_ID, 1); }
				}
				break;
			case GamlPackage.TERMINAL_EXPRESSION:
				if (!(object instanceof StringLiteral)) { setStyle(object, NUMBER_ID, 0); }
				break;
			case GamlPackage.RESERVED_LITERAL:
				setStyle(object, RESERVED_ID, 0);
				break;
			case GamlPackage.BINARY_OPERATOR:
			case GamlPackage.FUNCTION:
				setStyle(object, OPERATOR_ID, EGaml.getInstance().getKeyOf(object), true);
				break;
			case GamlPackage.ARGUMENT_PAIR:
				setStyle(object, VARIABLE_ID, ((ArgumentPair) object).getOp(), false);
				break;
			case GamlPackage.VARIABLE_REF:
				setStyle(VARIABLE_ID, NodeModelUtils.getNode(object));
				break;
			case GamlPackage.UNIT_NAME:
				setStyle(object, UNIT_ID, 0);
				break;
			case GamlPackage.TYPE_REF:
				final var st = EGaml.getInstance().getStatement(object);
				if (st instanceof S_Definition && ((S_Definition) st).getTkey() == object) {
					setStyle(KEYWORD_ID, NodeModelUtils.findActualNodeFor(object));
				} else {
					setStyle(TYPE_ID, NodeModelUtils.getNode(object));
				}
				break;
			case GamlPackage.PARAMETER:
				setStyle(object, VARIABLE_ID, ((Parameter) object).getBuiltInFacetKey(), false);
				break;
			case GamlPackage.ARGUMENT_DEFINITION:
				setStyle(object, VARDEF_ID, ((ArgumentDefinition) object).getName(), false);
				break;
			case GamlPackage.STATEMENT:
				Statement stat = (Statement) object;
				String name = findNameOf(stat);
				if (name != null) { setStyle(stat, VARDEF_ID, name, false); }
				setStyle(stat, KEYWORD_ID, stat.getKey(), false);
				break;
			default:
				final List<EClass> eSuperTypes = clazz.getESuperTypes();
				if (!eSuperTypes.isEmpty()) { process(object, eSuperTypes.get(0)); }
		}
	}

	/**
	 * Find name of.
	 *
	 * @param o
	 *            the o
	 * @return the string
	 */
	private String findNameOf(final EObject o) {
		if (o instanceof GamlDefinition) return ((GamlDefinition) o).getName();
		if (o instanceof S_Display) return ((S_Display) o).getName();
		if (o instanceof HeadlessExperiment) return ((HeadlessExperiment) o).getName();

		return null;
	}

	/**
	 * Sets the style.
	 *
	 * @param obj
	 *            the obj
	 * @param s
	 *            the s
	 * @param position
	 *            the position
	 * @return true, if successful
	 */
	private final boolean setStyle(final EObject obj, final String s, final int position) {
		// position = -1 for all the node; 0 for the first leaf node, 1 for the
		// second one, etc.
		if (obj != null && s != null) {
			INode n = NodeModelUtils.getNode(obj);
			if (n == null) return false;
			if (position > -1) {
				var i = 0;
				for (final ILeafNode node : n.getLeafNodes()) {
					if (!node.isHidden()) {
						if (position == i) {
							n = node;
							break;
						}
						i++;
					}
				}
			}
			return setStyle(s, n);
		}
		return false;
	}

	/**
	 * Sets the style.
	 *
	 * @param s
	 *            the s
	 * @param n
	 *            the n
	 * @return true, if successful
	 */
	private final boolean setStyle(final String s, final INode n) {
		if (!done.contains(n) && n != null) {
			done.add(n);
			acceptor.addPosition(n.getOffset(), n.getLength(), s);
			return true;
		}
		return false;
	}

	/**
	 * Sets the style.
	 *
	 * @param obj
	 *            the obj
	 * @param s
	 *            the s
	 * @param text
	 *            the text
	 * @param all
	 *            the all
	 * @return true, if successful
	 */
	private final boolean setStyle(final EObject obj, final String s, final String text, final boolean all) {
		if (text == null) return false;
		if (obj != null && s != null) {
			INode n = NodeModelUtils.getNode(obj);
			if (n == null) return false;
			for (final ILeafNode node : n.getLeafNodes()) {
				if (!node.isHidden()) {
					final var sNode = StringUtils.toJavaString(NodeModelUtils.getTokenText(node));
					if (equalsFacetOrString(text, sNode)) {
						n = node;
						if (!all) { break; }
					}
				}
			}
			return setStyle(s, n);
		}
		return false;
	}

	/**
	 * Equals facet or string.
	 *
	 * @param text
	 *            the text
	 * @param s
	 *            the s
	 * @return true, if successful
	 */
	boolean equalsFacetOrString(final String text, final String s) {
		if (s == null || text == null) return false;
		if (s.equals(text)) return true;
		final var length = s.length();
		if (length == 0) return false;
		final var last = s.charAt(length - 1);
		switch (last) {
			case ':':
				return text.equals(s.substring(0, length - 1));
			case '\"':
			case '\'':
				return s.charAt(0) == last && length > 1 && text.equals(s.substring(1, length - 1));

		}
		return false;
	}

}
