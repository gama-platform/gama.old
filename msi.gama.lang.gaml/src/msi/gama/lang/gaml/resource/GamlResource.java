/**
 * Created by drogoul, 24 avr. 2012
 * 
 */
package msi.gama.lang.gaml.resource;

import static msi.gama.common.interfaces.IKeyword.*;
import java.util.*;
import msi.gama.common.interfaces.ISyntacticElement;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.gaml.validation.*;
import msi.gama.lang.utils.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.descriptions.*;
import msi.gaml.factories.DescriptionFactory;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import com.google.inject.Inject;

/**
 * The class GamlResource.
 * 
 * @author drogoul
 * @since 24 avr. 2012
 * 
 */
public class GamlResource extends LazyLinkingResource {

	static int counter = 0;
	static final List<Integer> STATEMENTS_WITH_ATTRIBUTES = Arrays.asList(ISymbolKind.SPECIES,
		ISymbolKind.EXPERIMENT, ISymbolKind.OUTPUT);

	private ISyntacticElement syntacticContents;
	private IGamlBuilderListener listener;

	@Inject
	GamlJavaValidator validator;

	@Override
	public String toString() {
		return "resource" + "[" + getURI() + "]";
	}

	public void setModelDescription(final boolean withErrors, final ModelDescription model) {
		if ( listener != null ) {
			Set<String> exp = model == null ? Collections.EMPTY_SET : model.getExperimentNames();
			listener.validationEnded(exp, withErrors);
		}
	}

	public IModel doBuild() {
		return GamlBuilder.INSTANCE.build(this);
	}

	public ModelDescription doValidate() {
		long begin = System.currentTimeMillis();
		ModelDescription md = GamlBuilder.INSTANCE.validate(this);
		long end = System.currentTimeMillis();
		GuiUtils.debug("Validation of " + md + " took " + (end - begin) + " milliseconds");
		return md;
	}

	public void setListener(final IGamlBuilderListener listener) {
		this.listener = listener;
	}

	public void removeListener() {
		listener = null;
	}

	public IGamlBuilderListener getListener() {
		return listener;
	}

	public void error(final String message, final ISyntacticElement elt, final boolean warning) {
		validator.add(new GamlCompilationError(message, elt, warning));
	}

	@Override
	protected void unload(final EObject oldRootObject) {
		if ( oldRootObject != null ) {
			EList<Adapter> list = new BasicEList<Adapter>(oldRootObject.eAdapters());
			for ( Adapter adapter : list ) {
				if ( adapter instanceof ModelDescription ) {
					((ModelDescription) adapter).dispose();
				}
			}
		}
		super.unload(oldRootObject);
	}

	public ISyntacticElement getSyntacticContents() {
		if ( syntacticContents == null ) {
			buildSyntacticContents();
		}
		return syntacticContents;
	}

	public void eraseSyntacticContents() {
		if ( syntacticContents != null ) {
			syntacticContents.dispose();
		}
		syntacticContents = null;
	}

	public void buildSyntacticContents() {
		Model m = (Model) getContents().get(0);
		if ( m == null ) { throw new NullPointerException("The model of " + this +
			" appears to be null. Please debug to understand the cause."); }
		syntacticContents = new SyntacticStatement(MODEL, m);
		syntacticContents.setFacet(NAME, new LabelExpressionDescription(m.getName()));
		for ( final Import i : m.getImports() ) {
			final ISyntacticElement include = new SyntacticStatement(INCLUDE, i);
			include.setFacet(FILE, new LabelExpressionDescription(i.getImportURI()));
			syntacticContents.addChild(include);
		}
		convStatements(syntacticContents, m.getStatements());
	}

	private final void convStatements(final ISyntacticElement elt, final EList<Statement> ss) {
		for ( final Statement stm : ss ) {
			elt.addChild(convStatement(elt.getKeyword(), stm));
		}
	}

	private void addFacet(final EObject stm, final ISyntacticElement e, final String key,
		final IExpressionDescription expr) {
		if ( e.getFacet(key) != null ) {
			error("Double definition of facet " + key + ". Only the last one will be considered",
				e, true);
		}
		e.setFacet(key, expr);
	}

	private final ISyntacticElement convStatement(final String upper, final Statement stm) {
		// If the initial statement is null, we return null
		if ( stm == null ) { throw new NullPointerException(
			"Trying to convert a null statement. Please debug to understand the cause."); }
		// We catch its keyword
		String keyword = EGaml.getKey.caseStatement(stm);
		if ( keyword == null ) {
			throw new NullPointerException(
				"Trying to convert a statement with a null keyword. Please debug to understand the cause.");
		} else {
			keyword = convertKeyword(keyword, upper);
		}
		final ISyntacticElement elt = new SyntacticStatement(keyword, stm);;
		Expression expr = EGaml.getExprOf(stm);
		/**
		 * Some syntactic rewritings to remove ambiguities inherent to the grammar of GAML and
		 * translate the new compact syntax into the legacy facet-based one
		 */

		if ( keyword.equals("<-") ) {
			// Translation of "var := value" or "var <- value" to "set var value: value"
			elt.setKeyword(SET);
			IExpressionDescription value = convExpr(EGaml.getValueOf(stm));
			addFacet(stm, elt, VALUE, value);
			keyword = SET;
		} else if ( keyword.equals("<<") || keyword.equals("+=") || keyword.equals("++") ) {
			// Translation of "container << item" or "container ++ item" to
			// "add item: item to: container"
			elt.setKeyword(ADD);
			addFacet(stm, elt, TO, convExpr(expr));
			addFacet(stm, elt, ITEM, convExpr(EGaml.getValueOf(stm)));
			keyword = ADD;
		} else if ( keyword.equals("-=") || keyword.equals(">>") || keyword.equals("--") ) {
			// Translation of "container >> item" or "container -- item" to
			// "remove item: item from: container"
			elt.setKeyword(REMOVE);
			addFacet(stm, elt, FROM, convExpr(expr));
			addFacet(stm, elt, ITEM, convExpr(EGaml.getValueOf(stm)));
			keyword = REMOVE;
		} else if ( keyword.equals(EQUATION_OP) ) {
			// Translation of equations "diff(x, t) = ax + b" to "= left: diff(x, t) right: ax+b"
			addFacet(stm, elt, EQUATION_LEFT, convExpr(EGaml.getFunctionOf(stm)));
			addFacet(stm, elt, EQUATION_RIGHT, convExpr(expr));
		}
		// Translation of "container[index] <- value" to
		// "put item: value in: container at: index"
		if ( keyword.equals(SET) && expr instanceof Access ) {
			elt.setKeyword(PUT);
			addFacet(stm, elt, ITEM, elt.getFacets().remove(VALUE));
			addFacet(stm, elt, IN, convExpr(expr.getLeft()));
			List<Expression> args = EGaml.getExprsOf(((Access) expr).getArgs());
			int size = args.size();
			if ( size == 1 ) { // Integer index
				addFacet(stm, elt, AT, convExpr(args.get(0)));
			} else if ( size > 1 ) { // Point index
				Point p = EGaml.getFactory().createPoint();
				// Copy should be necessary to avoid any side-effect on the model
				// Copier copier = new Copier(false, true);
				p.setLeft(args.get(0));
				// Trick as setLeft() removes the first element of args
				p.setRight(args.get(0));
				// copier.copyReferences();
				Facet f = EGaml.getFactory().createFacet();
				f.setKey(AT);
				f.setExpr(p);
				stm.getFacets().add(f);
				// addFacet(stm, elt, AT, convExpr(p));
			} else {// size = 0 ? maybe "all: true" by default
				addFacet(stm, elt, ALL, convExpr(EGaml.createTerminal(true)));
			}
			keyword = PUT;
		}

		// If we define a variable with this statement
		if ( !SymbolProto.nonTypeStatements.contains(keyword) ) {
			int kind = DescriptionFactory.getProto(upper).getKind();
			if ( !STATEMENTS_WITH_ATTRIBUTES.contains(kind) ) {
				// Translation of "type var ..." to "let var type: type ..." if we are not in a
				// top-level statement (i.e. not in the declaration of a species or an experiment)
				elt.setKeyword(LET);
				addFacet(stm, elt, TYPE, new StringBasedExpressionDescription(keyword));
				keyword = LET;
			} else if ( stm.getBlock() != null && EGaml.getFunctionOf(stm) == null ) {
				// Translation of "type1 ID1 (type2 ID2, type3 ID3) {...}" to
				// "action ID1 type: type1 { arg ID2 type: type2; arg ID3 type: type3; ...}"
				elt.setKeyword(ACTION);
				addFacet(stm, elt, TYPE, new StringBasedExpressionDescription(keyword));
				keyword = ACTION;
			}
		}
		// Translation of "stm ID (ID1: V1, ID2:V2)" to "stm ID with:(ID1: V1, ID2:V2)"
		Parameters p = EGaml.getParamsOf(stm);
		if ( p != null ) {
			addFacet(stm, elt, WITH, convExpr(p));
		}
		// Translation of "type<contents> ..." to "type of: contents..."
		Contents of = stm.getOf();
		if ( of != null ) {
			String type = of.getType();
			if ( type != null ) {
				addFacet(stm, elt, OF, new StringBasedExpressionDescription(type));
			}
		}

		// We apply some conversions to the facets expressed in the statement
		convertFacets(stm, keyword, elt);

		// If the statement is "if", we convert its potential "else" part and put it as a child of
		// the syntactic element (as GAML expects it)
		if ( keyword.equals(IF) ) {
			convElse(stm, elt);
		}
		// Conversion of "action ID (type1 ID1 <- V1, type2 ID2)" to
		// "action ID {arg ID1 type: type1 default: V1; arg ID2 type: type2}"
		if ( keyword.equals(ACTION) ) {
			convertArgs(stm, elt);

		}
		// We add the dependencies (only for variable declarations)
		assignDependencies(stm, keyword, elt);

		// We add the "default" (or omissible) facet to the syntactic element
		String def = DescriptionFactory.getOmissibleFacetForSymbol(keyword);
		if ( def != null && !def.isEmpty() && elt.getFacet(def) == null ) {
			IExpressionDescription ed = findExpr(stm);
			if ( ed != null ) {
				elt.setFacet(def, ed);
			}
		}

		// We modify the names of experiments so as not to confuse them with species
		if ( keyword.equals(EXPERIMENT) ) {
			String name = elt.getLabel(NAME);
			elt.setFacet(NAME, new LabelExpressionDescription("Experiment " + name));
		}

		// We modify the "var", "const" and "experiment" declarations in order to replace the
		// keyword by the type
		if ( keyword.equals(VAR) || keyword.equals(CONST) || keyword.equals(EXPERIMENT) ) {
			String type = elt.getLabel(TYPE);
			if ( type == null ) {
				error("Facet 'type' is missing", elt, false);
				return elt;
			} else {
				elt.setKeyword(type);
			}
			if ( keyword.equals(CONST) ) {
				String constant = elt.getLabel(CONST);
				if ( constant != null && constant.equals(FALSE) ) {
					error("Is this variable constant or not ?", elt, true);
				}
				elt.setFacet(CONST, convExpr(EGaml.createTerminal(true)));
			}
		}
		// We apply the same conversion for methods (to get the name instead of the "method"
		// keyword)
		else if ( keyword.equals(METHOD) ) {
			String type = elt.getLabel(NAME);
			if ( type != null ) {
				elt.setKeyword(type);
			}
		}

		// We convert the block of the statement, if any
		Block block = stm.getBlock();
		if ( block != null ) {
			Expression function = EGaml.getFunctionOf(stm);
			if ( function != null ) {
				// If it is a function (and not a regular block), we add it as a facet
				addFacet(stm, elt, FUNCTION, convExpr(function));
			} else {
				convStatements(elt, block.getStatements());
			}
		}
		return elt;
	}

	private String convertKeyword(final String k, final String upper) {

		String keyword = k;
		if ( upper.equals(BATCH) && keyword.equals(SAVE) ) {
			keyword = SAVE_BATCH;
		} else if ( upper.equals(OUTPUT) && keyword.equals(FILE) ) {
			keyword = OUTPUT_FILE;
		} else if ( upper.equals(DISPLAY) || upper.equals(POPULATION) ) {
			if ( keyword.equals(SPECIES) ) {
				keyword = POPULATION;
			} else if ( keyword.equals(GRID) ) {
				keyword = GRID_POPULATION;
			}
		}
		return keyword;
	}

	private void assignDependencies(final Statement stm, final String keyword,
		final ISyntacticElement elt) {
		if ( !SymbolProto.nonTypeStatements.contains(keyword) ) {
			Set<String> s = varDependenciesOf(stm);
			if ( s != null && !s.isEmpty() ) {
				elt.setFacet(DEPENDS_ON, new StringListExpressionDescription(s));
			}
			if ( !keyword.equals(VAR) && !keyword.equals(CONST) ) {
				String type = elt.getLabel(TYPE);
				if ( type != null ) {
					if ( type.equals(keyword) ) {
						error("Duplicate declaration of type", elt, true);
					} else {
						error("Conflicting declaration of type (" + type + " and " + keyword +
							"), only the last one will be considered", elt, true);
					}
				}
			}
		}
	}

	private void convertFacets(final Statement stm, final String keyword,
		final ISyntacticElement elt) {
		for ( Facet f : EGaml.getFacetsOf(stm) ) {
			String fname = EGaml.getKeyOf(f);
			// We change the "<-" and "->" symbols into full names
			if ( fname.equals("<-") ) {
				fname = keyword.equals(LET) || keyword.equals(SET) ? VALUE : INIT;
			} else if ( fname.equals("->") ) {
				fname = FUNCTION;
			} else if ( fname.equals(TYPE) ) {
				// We convert type: ss<tt> to type: ss of: tt
				if ( f.getOf() != null ) {
					addFacet(stm, elt, OF,
						new StringBasedExpressionDescription(f.getOf().getType()));
				}
			}
			// We compute (and convert) the expression attached to the facet
			IExpressionDescription fexpr = convExpr(f.getExpr());
			if ( fexpr == null ) {
				fexpr = convExpr(f);
			}
			addFacet(stm, elt, fname, fexpr);
		}
	}

	private void convertArgs(final Statement stm, final ISyntacticElement elt) {
		ActionArguments args = EGaml.getArgsOf(stm);
		if ( args != null ) {
			for ( ArgumentDefinition def : args.getArgs() ) {
				ISyntacticElement arg = new SyntacticStatement(ARG, def);
				addFacet(def, arg, NAME, new StringBasedExpressionDescription(def.getName()));
				addFacet(def, arg, TYPE, new StringBasedExpressionDescription(def.getType()));
				if ( def.getOf() != null ) {
					addFacet(stm, elt, OF, new StringBasedExpressionDescription(def.getOf()
						.getType()));
				}
				if ( def.getDefault() != null ) {
					addFacet(def, arg, DEFAULT, convExpr(def.getDefault()));
				}
				elt.addChild(arg);
			}
		}
	}

	private final Set<String> varDependenciesOf(final Statement s) {
		Set<String> list = new HashSet();
		for ( Facet facet : EGaml.getFacetsOf(s) ) {
			Expression expr = facet.getExpr();
			if ( expr != null ) {
				for ( TreeIterator<EObject> tree = expr.eAllContents(); tree.hasNext(); ) {
					EObject obj = tree.next();
					if ( obj instanceof VariableRef ) {
						list.add(EGaml.getKey.caseVariableRef((VariableRef) obj));
					}
				}
			}
		}
		if ( list.isEmpty() ) { return null; }
		return list;
	}

	private final void convElse(final Statement stm, final ISyntacticElement elt) {
		EObject elseBlock = stm.getElse();
		if ( elseBlock != null ) {
			ISyntacticElement elseElt = new SyntacticStatement(ELSE, elseBlock);
			if ( elseBlock instanceof Statement ) {
				elseElt.addChild(convStatement(IF, (Statement) elseBlock));
			} else {
				convStatements(elseElt, ((Block) elseBlock).getStatements());
			}
			elt.addChild(elseElt);
		}
	}

	private final IExpressionDescription convExpr(final Expression expr) {
		if ( expr != null ) { return new EcoreBasedExpressionDescription(expr); }
		return null;
	}

	private final IExpressionDescription convExpr(final Facet expr) {
		if ( expr != null && expr.getName() != null ) { return new EcoreBasedExpressionDescription(
			expr); }
		return null;
	}

	private final IExpressionDescription findExpr(final Statement stm) {
		if ( stm == null ) { return null; }
		// The order below should be important
		if ( EGaml.getNameOf(stm) != null ) { return new EcoreBasedExpressionDescription(stm); }
		if ( EGaml.getExprOf(stm) != null ) { return convExpr(EGaml.getExprOf(stm)); } // ??

		return null;
	}

}
