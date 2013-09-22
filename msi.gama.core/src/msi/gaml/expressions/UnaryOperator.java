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
package msi.gaml.expressions;

import static msi.gama.precompiler.ITypeProvider.*;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.*;

/**
 * The Class UnaryOpCustomExpr.
 */
public class UnaryOperator extends AbstractExpression implements IOperator {

	protected IExpression child;
	protected final GamaHelper helper;
	private final boolean canBeConst;
	protected final int typeProvider, contentTypeProvider, keyTypeProvider;
	private final int[] expectedContentType;
	protected GamlElementDocumentation doc;
	private final boolean lazy;
	protected Signature signature;

	@Override
	public boolean isConst() {
		return canBeConst && child.isConst();
	}

	public UnaryOperator(final IType rt, final GamaHelper exec, final boolean canBeConst, final int tProv,
		final int ctProv, final int iProv, final int[] expectedContentType, final boolean lazy,
		final Signature signature) {
		type = rt;
		helper = exec;
		this.canBeConst = canBeConst;
		this.lazy = lazy;
		typeProvider = tProv;
		contentTypeProvider = ctProv;
		keyTypeProvider = iProv;
		this.expectedContentType = expectedContentType;
		this.signature = signature;

	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {

		final Object childValue = lazy ? child : child.value(scope);
		try {
			return helper.run(scope, childValue);
		} catch (final GamaRuntimeException e1) {
			e1.addContext("when applying the " + literalValue() + " operator on " + childValue);
			throw e1;

		} catch (final Exception e) {
			final GamaRuntimeException ee = GamaRuntimeException.create(e);
			ee.addContext("when applying the " + literalValue() + " operator on " + childValue);
			throw ee;
		}
	}

	@Override
	public UnaryOperator copy() {
		final UnaryOperator copy =
			new UnaryOperator(type, helper, canBeConst, typeProvider, contentTypeProvider, keyTypeProvider,
				expectedContentType, lazy, signature);
		copy.setName(getName());
		// FIXME: Why contentType is initialized ?
		// copy.contentType = contentType;
		copy.doc = doc;
		return copy;
	}

	@Override
	public String toGaml() {
		return literalValue() + " (" + child.toGaml() + ")";
	}

	@Override
	public String toString() {
		return literalValue() + "(" + child + ")";
	}

	@Override
	public String getTitle() {
		final StringBuilder sb = new StringBuilder(50);
		sb.append("operator <b>").append(getName()).append("</b> (");
		sb.append(child == null ? signature : child.getType());
		sb.append(") returns ");
		sb.append(typeToString());
		return sb.toString();
	}

	@Override
	public String getDocumentation() {
		final StringBuilder sb = new StringBuilder(200);
		sb.append(doc.getMain());
		return sb.toString();
	}

	private IType computeType(final int t, final IType def) {
		if ( t == NONE ) { return def; }
		if ( t == FIRST_ELEMENT_CONTENT_TYPE ) {
			if ( child instanceof ListExpression ) {
				final IExpression[] array = ((ListExpression) child).elements;
				if ( array.length == 0 ) { return Types.NO_TYPE; }
				return array[0].getContentType();
			} else if ( child instanceof MapExpression ) {
				final IExpression[] array = ((MapExpression) child).valuesArray();
				if ( array.length == 0 ) { return Types.NO_TYPE; }
				return array[0].getContentType();
			}
			return def;
		} else if ( t == FIRST_CONTENT_TYPE_OR_TYPE ) {
			final IType t2 = child.getContentType();
			if ( t2 == Types.NO_TYPE ) { return child.getType(); }
			return t2;
		}
		return t == FIRST_TYPE ? child.getType() : t == FIRST_CONTENT_TYPE ? child.getContentType()
			: t == FIRST_KEY_TYPE ? child.getKeyType() : t >= 0 ? Types.get(t) : def;
	}

	public void computeType() {
		type = computeType(typeProvider, type);
	}

	public void computeContentType() {
		contentType = computeType(contentTypeProvider, type.defaultContentType());
	}

	public void computeKeyType() {
		keyType = computeType(keyTypeProvider, type.defaultKeyType());
	}

	@Override
	public UnaryOperator init(final String name, final IDescription context, final IExpression ... args) {
		setName(name);
		setChild(context, args[0]);
		computeType();
		computeContentType();
		computeKeyType();
		return this;
	}

	private void setChild(final IDescription context, final IExpression c) {
		child = c;
		if ( expectedContentType.length == 0 ) { return; }
		final IType ct = c.getContentType();
		for ( int i = 0; i < expectedContentType.length; i++ ) {
			if ( ct.isTranslatableInto(Types.get(expectedContentType[i])) ) { return; }
		}
		context.error("The " + getName() + " operator cannot operate on elements of type " + ct.toString(),
			IGamlIssue.WRONG_TYPE);
	}

	public boolean hasChildren() {
		return true;
	}

	@Override
	public IOperator resolveAgainst(final IScope scope) {
		final UnaryOperator copy = copy();
		copy.child = child.resolveAgainst(scope);
		return copy;
	}

	@Override
	public IExpression arg(final int i) {
		return i == 0 ? child : null;
	}

	// FIXME: need to create sometime an operator prototype from which to derive operators instead
	// of copying them
	@Override
	public void setDoc(final GamlElementDocumentation doc) {
		this.doc = doc;
	}

	@Override
	public IType getElementsContentType() {
		if ( contentType.hasContents() ) { return child.getContentType(); }
		return contentType.defaultContentType();
	}

	@Override
	public IType getElementsKeyType() {
		if ( contentType.hasContents() ) { return child.getKeyType(); }
		return contentType.defaultKeyType();
	}

}
