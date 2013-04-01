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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.descriptions;

import java.util.*;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.util.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.*;
import msi.gaml.statements.Facets;
import msi.gaml.types.*;
import org.eclipse.emf.common.notify.*;
import org.eclipse.emf.ecore.EObject;

/**
 * Written by drogoul Modified on 16 mars 2010
 * 
 * @todo Description
 * 
 */
public class SymbolDescription implements IDescription {

	protected Facets facets;
	protected final EObject element;
	protected IDescription enclosing;
	protected String originName;
	protected final List<IDescription> children;
	protected SymbolProto meta;
	protected String keyword;

	public SymbolDescription(final String keyword, final IDescription superDesc,
		final IChildrenProvider cp, final EObject source, final Facets facets) {
		this.facets = facets;
		facets.putAsLabel(KEYWORD, keyword);
		this.keyword = keyword;
		element = source;
		if ( superDesc != null ) {
			originName = superDesc.getName();
		}
		meta = DescriptionFactory.getProto(keyword);
		setSuperDescription(superDesc);
		if ( meta.hasSequence() ) {
			this.children = new ArrayList();
			addChildren(cp.getChildren());
		} else {
			this.children = null;
		}
	}

	@Override
	public int getKind() {
		return meta.getKind();
	}

	@Override
	public SymbolProto getMeta() {
		return meta;
	}

	private void flagError(final String s, final String code, final boolean warning,
		final boolean info, final EObject source, final String ... data)
		throws GamaRuntimeException {

		IDescription desc = this;
		EObject e = source;
		while (e == null && desc != null) {
			desc = desc.getSuperDescription();
			if ( desc != null ) {
				e = desc.getUnderlyingElement(null);
			}
		}
		if ( !warning && !info ) {
			String resource = e == null ? "(no file)" : e.eResource().getURI().lastSegment();
			GuiUtils.debug("COMPILATION ERROR in " + this.toString() + ": " + s + "; source: " +
				resource);
		}
		// throws a runtime exception if there is no way to signal the error in the source
		// (i.e. we are probably in a runtime scenario)
		if ( e == null ) { throw new GamaRuntimeException(s, warning); }
		IErrorCollector c = getErrorCollector();
		if ( c == null ) {
			System.out.println((warning ? "Warning" : "Error") + ": " + s);
			return;
		}
		c.add(new GamlCompilationError(s, code, e, warning, info, data));
	}

	@Override
	public void error(final String message) {
		error(message, IGamlIssue.GENERAL);
	}

	@Override
	public void error(final String message, final String code) {
		flagError(message, code, false, false, getUnderlyingElement(null), (String[]) null);
	}

	@Override
	public void error(final String s, final String code, final EObject facet, final String ... data) {
		flagError(s, code, false, false, facet, data);
	}

	@Override
	public void error(final String s, final String code, final String facet, final String ... data) {
		flagError(s, code, false, false, this.getUnderlyingElement(facet), data);
	}

	@Override
	public void info(final String message, final String code) {
		flagError(message, code, true, true, getUnderlyingElement(null), (String[]) null);
	}

	@Override
	public void info(final String s, final String code, final EObject facet, final String ... data) {
		flagError(s, code, true, true, facet, data);
	}

	@Override
	public void info(final String s, final String code, final String facet, final String ... data) {
		flagError(s, code, true, true, this.getUnderlyingElement(facet), data);
	}

	@Override
	public void warning(final String message, final String code) {
		flagError(message, code, true, false, null, (String[]) null);
	}

	@Override
	public void warning(final String s, final String code, final EObject object,
		final String ... data) {
		flagError(s, code, true, false, object, data);
	}

	@Override
	public void warning(final String s, final String code, final String facet,
		final String ... data) {
		flagError(s, code, true, false, this.getUnderlyingElement(facet), data);
	}

	@Override
	public String getKeyword() {
		return keyword;
	}

	@Override
	public String getName() {
		return facets.getLabel(NAME);
	}

	@Override
	public void dispose() {
		if ( isBuiltIn() ) { return; }
		facets.dispose();
		facets.clear();
		if ( children != null ) {
			for ( IDescription c : children ) {
				c.dispose();
			}
			children.clear();
		}
		enclosing = null;
		if ( element != null ) {
			DescriptionFactory.unsetGamlDescription(element, this);
		}
	}

	@Override
	public ModelDescription getModelDescription() {
		if ( enclosing == null ) { return null; }
		return enclosing.getModelDescription();
	}

	// To add children from outside
	@Override
	public final void addChildren(final List<IDescription> originalChildren) {
		for ( IDescription c : originalChildren ) {
			addChild(c);
		}
	}

	@Override
	public IDescription addChild(final IDescription child) {
		if ( child == null ) { return null; }
		child.setSuperDescription(this);
		children.add(child);
		return child;
	}

	@Override
	public void setSuperDescription(final IDescription desc) {
		enclosing = desc;
	}

	@Override
	public EObject getUnderlyingElement(Object facet) {
		if ( facet == null ) { return element; }
		if ( facet instanceof EObject ) { return (EObject) facet; }
		IExpressionDescription f =
			facet instanceof IExpressionDescription ? (IExpressionDescription) facet : facets
				.get(facet);
		if ( f != null && f.getTarget() != null && f.getTarget().eContainer() != null ) { return f
			.getTarget(); }
		return element;

	}

	@Override
	public IDescription copy(final IDescription into) {
		return this;
	}

	@Override
	public Facets getFacets() {
		return facets;
	}

	@Override
	public List<IDescription> getChildren() {
		return children == null ? Collections.EMPTY_LIST : children;
	}

	@Override
	public IDescription getSuperDescription() {
		return enclosing;
	}

	protected boolean hasVar(final String name) {
		return false;
	}

	protected boolean hasAction(final String name) {
		return false;
	}

	protected boolean hasAspect(final String name) {
		return false;
	}

	@Override
	public IDescription getDescriptionDeclaringVar(final String name) {
		return hasVar(name) ? this : enclosing == null ? null : enclosing
			.getDescriptionDeclaringVar(name);
	}

	@Override
	public IDescription getDescriptionDeclaringAction(final String name) {
		return hasAction(name) ? this : enclosing == null ? null : enclosing
			.getDescriptionDeclaringAction(name);
	}

	@Override
	public IExpression getVarExpr(final String name) {
		return null;
	}

	@Override
	public IExpression addTemp(final String name, final IType type, final IType contentType,
		IType keyType) {
		return null;
	}

	@Override
	public void copyTempsAbove() {
		// Nothing to do
	}

	@Override
	public IType getTypeNamed(final String s) {
		ModelDescription m = getModelDescription();
		if ( m == null ) { return Types.get(s); }
		return m.getTypeNamed(s);
	}

	@Override
	public IType getType() {
		return Types.get(IType.NONE);
	}

	@Override
	public IType getContentType() {
		return Types.get(IType.NONE);
	}

	@Override
	public IType getKeyType() {
		return Types.get(IType.NONE);
	}

	@Override
	public SpeciesDescription getSpeciesContext() {
		if ( enclosing == null ) { return null; }
		return enclosing.getSpeciesContext();
	}

	/**
	 * @see msi.gama.common.interfaces.IDescription#getSpeciesDescription(java.lang.String)
	 */
	@Override
	public SpeciesDescription getSpeciesDescription(final String actualSpecies) {
		ModelDescription model = getModelDescription();
		if ( model == null ) { return null; }
		return model.getSpeciesDescription(actualSpecies);
	}

	/**
	 * @see msi.gama.common.interfaces.IDescription#getAction(java.lang.String)
	 */
	@Override
	public StatementDescription getAction(final String name) {
		return null;
	}

	/**
	 * @see msi.gama.common.interfaces.IDescription#getWorldSpecies()
	 */
	@Override
	public TypeDescription getWorldSpecies() {
		IDescription model = getModelDescription();
		if ( model == null ) { return null; }
		return model.getWorldSpecies();
	}

	/**
	 * @see org.eclipse.emf.common.notify.Adapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
	 */
	@Override
	public void notifyChanged(final Notification notification) {
		// Nothing to do yet
	}

	/**
	 * @see org.eclipse.emf.common.notify.Adapter#getTarget()
	 */
	@Override
	public Notifier getTarget() {
		return element;
	}

	/**
	 * @see org.eclipse.emf.common.notify.Adapter#setTarget(org.eclipse.emf.common.notify.Notifier)
	 */
	@Override
	public void setTarget(final Notifier newTarget) {}

	/**
	 * @see org.eclipse.emf.common.notify.Adapter#isAdapterForType(java.lang.Object)
	 */
	@Override
	public boolean isAdapterForType(final Object type) {
		return false;
	}

	@Override
	public void unsetTarget(final Notifier object) {

	}

	@Override
	public String getTitle() {
		return "Statement <b>" + getKeyword() + "</b> ";
	}

	@Override
	public String getDocumentation() {
		return meta.getDocumentation();
	}

	@Override
	public List<GamlCompilationError> getErrors() {
		IErrorCollector c = getErrorCollector();
		if ( c == null ) { return Collections.EMPTY_LIST; }
		return c.getErrors();
	}

	@Override
	public List<GamlCompilationError> getWarnings() {
		IErrorCollector c = getErrorCollector();
		if ( c == null ) { return Collections.EMPTY_LIST; }
		return c.getWarnings();
	}

	public List<GamlCompilationError> getInfos() {
		IErrorCollector c = getErrorCollector();
		if ( c == null ) { return Collections.EMPTY_LIST; }
		return c.getInfos();
	}

	@Override
	public IErrorCollector getErrorCollector() {
		if ( enclosing == null ) { return null; }
		return enclosing.getErrorCollector();
	}

	@Override
	public boolean isBuiltIn() {
		return element == null;
	}

	@Override
	public String getOriginName() {
		return originName;
	}

	@Override
	public void setOriginName(String name) {
		if ( originName == null ) {
			originName = name;
		}
	}

}
