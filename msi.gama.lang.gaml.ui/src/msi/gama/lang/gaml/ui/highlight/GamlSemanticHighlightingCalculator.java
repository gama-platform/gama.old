/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.lang.gaml.ui.highlight;

import static msi.gama.lang.gaml.ui.highlight.GamlHighlightingConfiguration.*;
import static org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration.*;
import java.util.*;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.gaml.gaml.util.GamlSwitch;
import msi.gama.lang.utils.EGaml;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.*;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.*;

/**
 * 
 * @author Pierrick
 *         cf. http://www.eclipse.org/Xtext/documentation/latest/xtext.html#highlighting
 * 
 */
public class GamlSemanticHighlightingCalculator extends GamlSwitch implements ISemanticHighlightingCalculator {

	private static Set<String> ASSIGNMENTS = new HashSet(Arrays.asList("<-", "<<", ">>", "->"));

	private IHighlightedPositionAcceptor acceptor;
	Set<INode> done = new HashSet();

	@Override
	public void provideHighlightingFor(final XtextResource resource, final IHighlightedPositionAcceptor a) {
		if ( resource == null ) { return; }
		acceptor = a;
		TreeIterator<EObject> root = resource.getAllContents();
		while (root.hasNext()) {
			doSwitch(root.next());
		}
		done.clear();
	}

	@Override
	public Object caseStatement(final Statement object) {
		setStyle(object, VARDEF_ID, EGaml.getNameOf(object));
		setStyle(object, FACET_ID, object.getFirstFacet());
		return setStyle(object, KEYWORD_ID, object.getKey());
	}

	@Override
	public Object caseS_DirectAssignment(final S_DirectAssignment obj) {
		return setStyle(obj, ASSIGN_ID, obj.getKey());
	}

	@Override
	public Object caseS_Assignment(final S_Assignment obj) {
		String s = obj.getKey();
		if ( "=".equals(s) ) { return setStyle(obj, ASSIGN_ID, s); }
		return false;
	}

	@Override
	public Object caseFacet(final Facet object) {
		String key = object.getKey();
		if ( ASSIGNMENTS.contains(key) ) {
			setStyle(object, ASSIGN_ID, 0);
		} else {
			setStyle(object, FACET_ID, 0);
			if ( key.startsWith("type") ) {
				setStyle(TYPE_ID, NodeModelUtils.getNode(object.getExpr()));
			} else if ( object.getName() != null ) {
				setStyle(object, VARDEF_ID, 1);
			}
		}
		return true;
	}

	@Override
	public Object caseTerminalExpression(final TerminalExpression object) {
		if ( !(object instanceof StringLiteral) ) {
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
		return setStyle(TYPE_ID, NodeModelUtils.getNode(object));
	}

	@Override
	public Object caseParameter(final Parameter object) {
		return setStyle(object, VARIABLE_ID, object.getBuiltInFacetKey());
	}

	@Override
	public Object caseArgumentDefinition(final ArgumentDefinition object) {
		return setStyle(object, VARDEF_ID, object.getName());
	}

	private final boolean setStyle(final EObject obj, final String s, final int position) {
		// position = -1 for all the node; 0 for the first leaf node, 1 for the second one, etc.
		if ( obj != null && s != null ) {
			INode n = NodeModelUtils.getNode(obj);
			if ( n == null ) { return false; }
			if ( position > -1 ) {
				int i = 0;
				for ( ILeafNode node : n.getLeafNodes() ) {
					if ( !node.isHidden() ) {
						if ( position == i ) {
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
		if ( !done.contains(n) && n != null ) {
			done.add(n);
			acceptor.addPosition(n.getOffset(), n.getLength(), s);
			return true;
		}
		return false;
	}

	private final boolean setStyle(final EObject obj, final String s, final String text) {
		if ( text == null ) { return false; }
		if ( obj != null && s != null ) {
			INode n = NodeModelUtils.getNode(obj);
			if ( n == null ) { return false; }
			for ( ILeafNode node : n.getLeafNodes() ) {
				if ( !node.isHidden() ) {
					if ( NodeModelUtils.getTokenText(node).equals(text) ) {
						n = node;
						break;
					}
				}
			}
			return setStyle(s, n);
		}
		return false;
	}

}
