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
import msi.gama.common.interfaces.*;
import msi.gama.common.util.IErrorCollector;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.commands.Facets;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.*;
import org.eclipse.emf.common.notify.*;

/**
 * Written by drogoul Modified on 16 mars 2010
 * 
 * @todo Description
 * 
 */
public class SymbolDescription implements IDescription {

	protected final Facets facets;
	private ISyntacticElement source;
	protected IDescription enclosing;
	protected final List<IDescription> children;
	protected SymbolMetaDescription meta;
	protected String name;
	protected String keyword;

	public SymbolDescription(final String keyword, final IDescription superDesc,
		final List<IDescription> children, final ISyntacticElement source,
		final SymbolMetaDescription md) {
		this.facets = source.getFacets();
		facets.putAsLabel(IKeyword.KEYWORD, keyword);
		setSource(source);
		meta = md;
		setSuperDescription(superDesc);
		if ( meta.hasSequence() ) {
			this.children = new ArrayList();
			addChildren(children);
		} else {
			this.children = null;
		}
	}

	/**
	 * A method dedicated to initializing various structures
	 */
	protected void initialize() {

	}

	@Override
	public SymbolMetaDescription getMeta() {
		return meta;
	}

	private void flagError(final String s, final String code, final boolean warning,
		final Object facet, final String ... data) throws GamaRuntimeException {
		ISyntacticElement e =
			facet instanceof ISyntacticElement ? (ISyntacticElement) facet : getSourceInformation();
		IDescription desc = this;
		while (e == null && desc != null) {
			desc = desc.getSuperDescription();
			if ( desc != null ) {
				e = desc.getSourceInformation();
			}
		}
		// throws a runtime exception if there is no way to signal the error in the source
		// (i.e. we are probably in a runtime scenario)
		if ( e == null ) { throw new GamaRuntimeException(s, warning); }
		getErrorCollector().add(new GamlCompilationError(this, s, code, e, warning, facet, data));
	}

	@Override
	public void flagError(final String message) {
		flagError(message, IGamlIssue.GENERAL);
	}

	@Override
	public void flagError(final String message, final String code) {
		flagError(message, code, false, null, (String[]) null);
	}

	@Override
	public void flagError(final String s, final String code, final Object facet,
		final String ... data) {
		flagError(s, code, false, facet, data);
	}

	@Override
	public void flagWarning(final String message, final String code) {
		flagError(message, code, true, null, (String[]) null);
	}

	@Override
	public void flagWarning(final String s, final String code, final Object facet,
		final String ... data) {
		flagError(s, code, true, facet, data);
	}

	@Override
	public String getKeyword() {
		if ( keyword == null ) {
			keyword = facets.getLabel(IKeyword.KEYWORD);
		}
		return keyword;
	}

	@Override
	public String getName() {
		if ( name == null ) {
			name = facets.getLabel(IKeyword.NAME);
		}
		return name;
	}

	@Override
	public void dispose() {
		// facets.dispose();
		if ( children != null ) {
			for ( IDescription c : children ) {
				c.dispose();
			}
			children.clear();
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
	public ISyntacticElement getSourceInformation() {
		return source;
	}

	@Override
	public IDescription copy() {
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
	public IExpression addTemp(final String name, final IType type, final IType contentType) {
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
	public CommandDescription getAction(final String name) {
		return null;
	}

	/**
	 * @see msi.gama.common.interfaces.IDescription#getWorldSpecies()
	 */
	@Override
	public SpeciesDescription getWorldSpecies() {
		IDescription model = getModelDescription();
		if ( model == null ) { return null; }
		return model.getWorldSpecies();
	}

	protected void setSource(final ISyntacticElement source) {
		this.source = source;

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
		return (Notifier) getSourceInformation().getUnderlyingElement(null);
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

	@Override
	public IErrorCollector getErrorCollector() {
		if ( enclosing == null ) { return null; }
		return enclosing.getErrorCollector();
	}
}
