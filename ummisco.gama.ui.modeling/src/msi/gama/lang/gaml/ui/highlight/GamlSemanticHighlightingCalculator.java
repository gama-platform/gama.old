/*********************************************************************************************
 *
 * 'GamlSemanticHighlightingCalculator.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.highlight;

import static msi.gama.lang.gaml.ui.highlight.GamlHighlightingConfiguration.ASSIGN_ID;
import static msi.gama.lang.gaml.ui.highlight.GamlHighlightingConfiguration.FACET_ID;
import static msi.gama.lang.gaml.ui.highlight.GamlHighlightingConfiguration.OPERATOR_ID;
import static msi.gama.lang.gaml.ui.highlight.GamlHighlightingConfiguration.PRAGMA_ID;
import static msi.gama.lang.gaml.ui.highlight.GamlHighlightingConfiguration.RESERVED_ID;
import static msi.gama.lang.gaml.ui.highlight.GamlHighlightingConfiguration.TASK_ID;
import static msi.gama.lang.gaml.ui.highlight.GamlHighlightingConfiguration.TYPE_ID;
import static msi.gama.lang.gaml.ui.highlight.GamlHighlightingConfiguration.UNIT_ID;
import static msi.gama.lang.gaml.ui.highlight.GamlHighlightingConfiguration.VARDEF_ID;
import static msi.gama.lang.gaml.ui.highlight.GamlHighlightingConfiguration.VARIABLE_ID;
import static org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration.KEYWORD_ID;
import static org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration.NUMBER_ID;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.TreeIterator;
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
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.Parameter;
import msi.gama.lang.gaml.gaml.Pragma;
import msi.gama.lang.gaml.gaml.S_Assignment;
import msi.gama.lang.gaml.gaml.S_Definition;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.gaml.StringLiteral;

/**
 *
 * @author Pierrick cf.
 *         http://www.eclipse.org/Xtext/documentation/latest/xtext.html#
 *         highlighting
 *
 */
public class GamlSemanticHighlightingCalculator implements ISemanticHighlightingCalculator {

	@Inject private ITaskFinder taskFinder;

	private static Set<String> ASSIGNMENTS = new HashSet<>(
			Arrays.asList("<-", "<<", ">>", "->", "<+", ">-", "<<+", ">>-", "+<-"));

	private IHighlightedPositionAcceptor acceptor;
	Set<INode> done = new HashSet<>();

	@Override
	public void provideHighlightingFor(final XtextResource resource, final IHighlightedPositionAcceptor arg1,
			final CancelIndicator arg2) {
		if (resource == null) {
			return;
		}
		acceptor = arg1;
		final TreeIterator<EObject> root = resource.getAllContents();
		while (root.hasNext()) {
			process(root.next());
		}
		done.clear();
		highlightTasks(resource, acceptor);
	}

	protected void highlightTasks(final XtextResource resource, final IHighlightedPositionAcceptor acceptor) {
		final List<Task> tasks = taskFinder.findTasks(resource);
		for (final Task task : tasks) {
			acceptor.addPosition(task.getOffset(), task.getTagLength(), TASK_ID);
		}
	}

	void process(final EObject object) {
		if (object == null)
			return;
		process(object, object.eClass());
	}

	void process(final EObject object, final EClass clazz) {
		final int id = clazz.getClassifierID();

		switch (id) {
		case GamlPackage.PRAGMA:
			setStyle(object, PRAGMA_ID, ((Pragma) object).getName());
			break;
		case GamlPackage.SASSIGNMENT:
			final String s = ((S_Assignment) object).getKey();
			setStyle(object, ASSIGN_ID, s);
			break;
		case GamlPackage.FACET:
			final Facet f = (Facet) object;
			final String key = f.getKey();
			if (ASSIGNMENTS.contains(key)) {
				setStyle(object, ASSIGN_ID, 0);
			} else {
				setStyle(object, FACET_ID, 0);
				if (key.startsWith("type")) {
					setStyle(TYPE_ID, NodeModelUtils.getNode(f.getExpr()));
				} else if (f.getName() != null) {
					setStyle(object, VARDEF_ID, 1);
				}
			}
			break;
		case GamlPackage.TERMINAL_EXPRESSION:
			if (!(object instanceof StringLiteral)) {
				setStyle(object, NUMBER_ID, 0);
			}
			break;
		case GamlPackage.RESERVED_LITERAL:
			setStyle(object, RESERVED_ID, 0);
			break;
		case GamlPackage.BINARY:
		case GamlPackage.FUNCTION:
			setStyle(object, OPERATOR_ID, EGaml.getKeyOf(object));
			break;
		case GamlPackage.ARGUMENT_PAIR:
			setStyle(object, VARIABLE_ID, ((ArgumentPair) object).getOp());
			break;
		case GamlPackage.VARIABLE_REF:
			setStyle(VARIABLE_ID, NodeModelUtils.getNode(object));
			break;
		case GamlPackage.UNIT_NAME:
			setStyle(object, UNIT_ID, 0);
			break;
		case GamlPackage.TYPE_REF:
			final Statement st = EGaml.getStatement(object);
			if (st instanceof S_Definition && ((S_Definition) st).getTkey() == object) {
				setStyle(KEYWORD_ID, NodeModelUtils.findActualNodeFor(object));
			} else
				setStyle(TYPE_ID, NodeModelUtils.getNode(object));
			break;
		case GamlPackage.PARAMETER:
			setStyle(object, VARIABLE_ID, ((Parameter) object).getBuiltInFacetKey());
			break;
		case GamlPackage.ARGUMENT_DEFINITION:
			setStyle(object, VARDEF_ID, ((ArgumentDefinition) object).getName());
			break;
		case GamlPackage.STATEMENT:
			final Statement stat = (Statement) object;
			setStyle(object, VARDEF_ID, EGaml.getNameOf(stat));
			setStyle(object, KEYWORD_ID, stat.getKey());
			break;
		default:
			final List<EClass> eSuperTypes = clazz.getESuperTypes();
			if (!eSuperTypes.isEmpty())
				process(object, eSuperTypes.get(0));
		}
	}

	private final boolean setStyle(final EObject obj, final String s, final int position) {
		// position = -1 for all the node; 0 for the first leaf node, 1 for the
		// second one, etc.
		if (obj != null && s != null) {
			INode n = NodeModelUtils.getNode(obj);
			if (n == null) {
				return false;
			}
			if (position > -1) {
				int i = 0;
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

	private final boolean setStyle(final String s, final INode n) {
		if (!done.contains(n) && n != null) {
			done.add(n);
			acceptor.addPosition(n.getOffset(), n.getLength(), s);
			return true;
		}
		return false;
	}

	private final boolean setStyle(final EObject obj, final String s, final String text) {
		if (text == null) {
			return false;
		}
		if (obj != null && s != null) {
			INode n = NodeModelUtils.getNode(obj);
			if (n == null) {
				return false;
			}
			for (final ILeafNode node : n.getLeafNodes()) {
				if (!node.isHidden()) {
					final String sNode = StringUtils.toJavaString(NodeModelUtils.getTokenText(node));
					if (equalsFaceOrString(text, sNode)) {
						n = node;
						break;
					}
				}
			}
			return setStyle(s, n);
		}
		return false;
	}

	boolean equalsFaceOrString(final String text, final String s) {
		if (s.equals(text)) {
			return true;
		}
		if (s.equals(text + ":")) {
			return true;
		}
		if (s.equals("\"" + text + "\"")) {
			return true;
		}
		if (s.equals("\'" + text + "\'")) {
			return true;
		}
		return false;
	}

}
