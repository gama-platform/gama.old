/*********************************************************************************************
 *
 *
 * 'GamlSemanticHighlightingCalculator.java', in plugin 'msi.gama.lang.gaml.ui', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.tasks.ITaskFinder;
import org.eclipse.xtext.tasks.Task;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;

import com.google.inject.Inject;

import msi.gama.common.util.StringUtils;
import msi.gama.lang.gaml.gaml.ArgumentDefinition;
import msi.gama.lang.gaml.gaml.ArgumentPair;
import msi.gama.lang.gaml.gaml.Binary;
import msi.gama.lang.gaml.gaml.Facet;
import msi.gama.lang.gaml.gaml.Function;
import msi.gama.lang.gaml.gaml.Parameter;
import msi.gama.lang.gaml.gaml.Pragma;
import msi.gama.lang.gaml.gaml.ReservedLiteral;
import msi.gama.lang.gaml.gaml.S_Assignment;
import msi.gama.lang.gaml.gaml.S_Definition;
import msi.gama.lang.gaml.gaml.S_DirectAssignment;
import msi.gama.lang.gaml.gaml.S_Display;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.gaml.StringLiteral;
import msi.gama.lang.gaml.gaml.TerminalExpression;
import msi.gama.lang.gaml.gaml.TypeRef;
import msi.gama.lang.gaml.gaml.UnitName;
import msi.gama.lang.gaml.gaml.VariableRef;
import msi.gama.lang.gaml.gaml.util.GamlSwitch;
import msi.gama.lang.utils.EGaml;

/**
 *
 * @author Pierrick cf.
 *         http://www.eclipse.org/Xtext/documentation/latest/xtext.html#
 *         highlighting
 *
 */
public class GamlSemanticHighlightingCalculator extends GamlSwitch implements ISemanticHighlightingCalculator {

	@Inject
	private ITaskFinder taskFinder;

	private static Set<String> ASSIGNMENTS = new HashSet(
			Arrays.asList("<-", "<<", ">>", "->", "<+", ">-", "<<+", ">>-", "+<-"));

	private IHighlightedPositionAcceptor acceptor;
	Set<INode> done = new HashSet();

	@Override
	public void provideHighlightingFor(final XtextResource resource, final IHighlightedPositionAcceptor a) {
		if (resource == null) {
			return;
		}
		acceptor = a;
		final TreeIterator<EObject> root = resource.getAllContents();
		while (root.hasNext()) {
			doSwitch(root.next());
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

	@Override
	public Object caseS_Display(final S_Display object) {
		return caseStatement(object);
	}

	@Override
	public Object caseStatement(final Statement object) {

		setStyle(object, VARDEF_ID, EGaml.getNameOf(object));

		setStyle(object, KEYWORD_ID, object.getKey());

		return setStyle(object, FACET_ID, object.getFirstFacet());
	}

	@Override
	public Object casePragma(final Pragma object) {
		return setStyle(object, PRAGMA_ID, object.getName());
	}

	@Override
	public Object caseS_DirectAssignment(final S_DirectAssignment obj) {
		return setStyle(obj, ASSIGN_ID, obj.getKey());
	}

	@Override
	public Object caseS_Assignment(final S_Assignment obj) {
		final String s = obj.getKey();
		if ("=".equals(s)) {
			return setStyle(obj, ASSIGN_ID, s);
		}
		return false;
	}

	@Override
	public Object caseFacet(final Facet object) {
		final String key = object.getKey();
		if (ASSIGNMENTS.contains(key)) {
			setStyle(object, ASSIGN_ID, 0);
		} else {
			setStyle(object, FACET_ID, 0);
			if (key.startsWith("type")) {
				setStyle(TYPE_ID, NodeModelUtils.getNode(object.getExpr()));
			} else if (object.getName() != null) {
				setStyle(object, VARDEF_ID, 1);
			}
		}
		return true;
	}

	@Override
	public Object caseTerminalExpression(final TerminalExpression object) {
		if (!(object instanceof StringLiteral)) {
			setStyle(object, NUMBER_ID, 0);
		}
		return true;
	}

	@Override
	public Object caseReservedLiteral(final ReservedLiteral object) {
		return setStyle(object, RESERVED_ID, 0);
	}

	@Override
	public Object caseBinary(final Binary object) {
		return setStyle(object, OPERATOR_ID, EGaml.getKeyOf(object));
	}

	@Override
	public Object caseFunction(final Function object) {
		return setStyle(object, OPERATOR_ID, EGaml.getKeyOf(object));
	}

	@Override
	public Object caseArgumentPair(final ArgumentPair object) {
		return setStyle(object, VARIABLE_ID, object.getOp());
	}

	@Override
	public Object caseVariableRef(final VariableRef object) {
		return setStyle(VARIABLE_ID, NodeModelUtils.getNode(object));
	}

	@Override
	public Object caseUnitName(final UnitName object) {
		return setStyle(object, UNIT_ID, 0);
	}

	@Override
	public Object caseTypeRef(final TypeRef object) {
		final Statement s = EGaml.getStatement(object);
		if (s instanceof S_Definition && ((S_Definition) s).getTkey() == object) {
			setStyle(KEYWORD_ID, NodeModelUtils.findActualNodeFor(object));
		}
		return setStyle(TYPE_ID, NodeModelUtils.getNode(object));
	}

	//
	// @Override
	// public Object caseSpeciesRef(final SpeciesRef object) {
	// return setStyle(TYPE_ID, NodeModelUtils.getNode(object));
	// }

	@Override
	public Object caseParameter(final Parameter object) {
		return setStyle(object, VARIABLE_ID, object.getBuiltInFacetKey());
	}

	@Override
	public Object caseArgumentDefinition(final ArgumentDefinition object) {
		return setStyle(object, VARDEF_ID, object.getName());
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
