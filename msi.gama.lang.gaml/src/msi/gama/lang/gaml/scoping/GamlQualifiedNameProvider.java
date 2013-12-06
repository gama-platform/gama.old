package msi.gama.lang.gaml.scoping;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.EGaml;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.util.*;
import com.google.inject.Inject;

/**
 * A Puppet Qualified Name provider.
 * 
 */
public class GamlQualifiedNameProvider extends DefaultDeclarativeQualifiedNameProvider {

	public static QualifiedName splice(final QualifiedName a, final QualifiedName b) {
		return a == null ? b : a.append(b);
	}

	@Inject
	private IQualifiedNameConverter converter;

	/**
	 * The fully qualified name of the closest named parent.
	 * 
	 * @param o
	 * @return
	 */
	// QualifiedName getParentsFullyQualifiedName(EObject o) {
	// for ( EObject tmp = o.eContainer(); tmp != null; tmp = tmp.eContainer() ) {
	// if ( tmp instanceof Statement &&
	// ((Statement) tmp).getKey().equals(IKeyword.ENVIRONMENT) ||
	// ((Statement) tmp).getKey().equals(IKeyword.ENTITIES) ||
	// ((Statement) tmp).getKey().equals(IKeyword.GLOBAL) ) {
	// continue;
	// }
	// QualifiedName n = getFullyQualifiedName(tmp);
	// if ( n != null ) { return n; }
	// }
	// return null;
	// }
	//
	// QualifiedName qualifiedName(Statement o) {
	// String k = EGaml.getKey.caseStatement(o);
	// String n = EGaml.getNameOf(o);
	// if ( n == null ) { return null; }
	// if ( k.equals(IKeyword.SPECIES) || k.equals(IKeyword.GRID) || k.equals(IKeyword.VAR) ||
	// !SymbolProto.nonTypeStatements.contains(k) ) { return splice(
	// getParentsFullyQualifiedName(o), converter.toQualifiedName(EGaml.getNameOf(o))); }
	// return null;
	// }

	QualifiedName qualifiedName(final Statement s) {
		String k = EGaml.getKey.caseStatement(s);
		if ( k.equals(IKeyword.SPECIES) || k.equals(IKeyword.GRID) ) {
			for ( EObject tmp = s.eContainer(); tmp != null; tmp = tmp.eContainer() ) {
				if ( tmp instanceof Statement && IKeyword.DISPLAY.equals(EGaml.getKeyOf(tmp)) ) {
					QualifiedName nn = QualifiedName.create(EGaml.getNameOf(s) + "_display");
					return nn;
				}
			}
		}

		String name = SimpleAttributeResolver.NAME_RESOLVER.apply(s);
		if ( Strings.isEmpty(name) ) { return null; }
		return converter.toQualifiedName(name);
	}

	QualifiedName qualifiedName(final S_Experiment s) {
		return converter.toQualifiedName(s.getName());
	}

	QualifiedName qualifiedName(final S_Definition s) {
		if ( IKeyword.PARAMETER.equals(EGaml.getKeyOf(s)) ) { return null; }
		return converter.toQualifiedName(s.getName());
	}

	QualifiedName qualifiedName(final ArgumentDefinition a) {
		return QualifiedName.create(a.getName());
	}

	QualifiedName qualifiedName(final Facet f) {
		String name = f.getName();
		if ( !Strings.isEmpty(name) ) { return QualifiedName.create(name); }
		return null;
	}

	QualifiedName qualifiedName(final Model o) {
		return QualifiedName.create(o.getName() + "_model");
	}

}