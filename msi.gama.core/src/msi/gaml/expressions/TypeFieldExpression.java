/*********************************************************************************************
 *
 *
 * 'TypeFieldExpression.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.GamlProperties;
import msi.gama.runtime.IScope;
import msi.gaml.descriptions.*;

public class TypeFieldExpression extends UnaryOperator {

	public TypeFieldExpression(final OperatorProto proto, final IDescription context, final IExpression ... exprs) {
		super(proto, context, exprs);
	}

	@Override
	public TypeFieldExpression resolveAgainst(final IScope scope) {
		return new TypeFieldExpression(prototype, null, child.resolveAgainst(scope));
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		StringBuilder sb = new StringBuilder();
		parenthesize(sb, child);
		sb.append(".").append(name);
		return sb.toString();
	}

	@Override
	public String toString() {
		if ( child == null ) { return prototype.signature.toString() + "." + name; }
		return child.serialize(false) + "." + name;
	}

	@Override
	public String getDocumentation() {
		StringBuilder sb = new StringBuilder(200);
		if ( child != null ) {
			sb.append("Defined on objects of type " + child.getType().getTitle());
		}
		vars annot = prototype.getSupport().getAnnotation(vars.class);
		if ( annot != null ) {
			var[] allVars = annot.value();
			for ( var v : allVars ) {
				if ( v.name().equals(getName()) ) {
					if ( v.doc().length > 0 ) {
						sb.append("<br/>");
						sb.append(v.doc()[0].value());
					}
				}
			}
		}
		return sb.toString();
	}

	@Override
	public String getTitle() {
		return "field <b>" + getName() + "</b> of type " + getType().getTitle();
	}

	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		meta.put(GamlProperties.ATTRIBUTES, name);
	}

}
