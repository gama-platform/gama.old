/*******************************************************************************************************
 *
 * GamlLabelProvider.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.labeling;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import msi.gama.common.interfaces.IGamlLabelProvider;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.EGaml;
import msi.gama.lang.gaml.gaml.Expression;
import msi.gama.lang.gaml.gaml.Facet;
import msi.gama.lang.gaml.gaml.HeadlessExperiment;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.S_Declaration;
import msi.gama.lang.gaml.gaml.S_Definition;
import msi.gama.lang.gaml.gaml.S_Experiment;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.gaml.StringLiteral;
import msi.gama.lang.gaml.gaml.VarDefinition;
import msi.gama.lang.gaml.gaml.VariableRef;
import msi.gama.lang.gaml.ui.outline.GamlOutlineTreeProvider;
import msi.gaml.compilation.ast.ISyntacticElement;
import msi.gaml.operators.Strings;
import msi.gaml.types.Types;

/**
 * Provides labels for a EObjects.
 *
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
@Singleton

public class GamlLabelProvider extends DefaultEObjectLabelProvider implements IGamlLabelProvider {

	/** The name provider. */
	@Inject private IQualifiedNameProvider nameProvider;

	/**
	 * Instantiates a new gaml label provider.
	 *
	 * @param delegate
	 *            the delegate
	 */
	@Inject
	public GamlLabelProvider(final AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}


	/**
	 * Image.
	 *
	 * @param ele
	 *            the ele
	 * @return the string
	 */
	String image(final Model ele) {
		return "_model.png";
	}

	/**
	 * Text.
	 *
	 * @param ele
	 *            the ele
	 * @return the string
	 */
	// Import
	String text(final Import ele) {
		String display = ele.getImportURI();
		final int index = display.lastIndexOf('/');
		if (index >= 0) { display = display.substring(index + 1); }
		return "import " + display;
	}

	/**
	 * Text.
	 *
	 * @param e
	 *            the e
	 * @return the string
	 */
	String text(final HeadlessExperiment e) {
		return "Experiment " + e.getName();
	}

	/**
	 * Text.
	 *
	 * @param ele
	 *            the ele
	 * @return the string
	 */
	String text(final EObject ele) {
		String text;
		String key = EGaml.getInstance().getKeyOf(ele);
		if (key == null) { key = ""; }
		text = key;
		key = null;
		if (ele instanceof Statement) {
			if (IKeyword.PARAMETER.equals(text)) return parameterText((Statement) ele);
			if (GamlOutlineTreeProvider.isAttribute((Statement) ele)) return attributeText((S_Definition) ele);
			if (GamlOutlineTreeProvider.isAction((Statement) ele)) return actionText((Statement) ele);
			String name = EGaml.getInstance().getNameOf(ele);
			if (name == null) {
				final Expression expr = ((Statement) ele).getExpr();
				if (expr != null) { name = EGaml.getInstance().getKeyOf(expr); }
			}
			if (name == null) {
				QualifiedName qn = null;
				try {
					qn = nameProvider.getFullyQualifiedName(ele);
					name = qn == null ? null : qn.toString();
				} catch (final IllegalArgumentException e) {
					name = null;
				}

			}
			text += " " + (name == null ? "" : name);
		}
		// } else {
		// text = key + " " + qn.toString();
		// }
		return StringUtils.capitalize(text);
	}

	/**
	 * @param ele
	 * @return
	 */
	private String attributeText(final S_Definition ele) {
		String type = EGaml.getInstance().getKeyOf(ele);
		String key = IKeyword.CONST.equals(type) ? type : null;
		final Map<String, Facet> map = EGaml.getInstance().getFacetsMapOf(ele);
		if (ele.getBlock() != null /* && ele.getBlock().getFunction() != null */) {
			key = "function";
		} else if (map.containsKey(IKeyword.FUNCTION) || map.containsKey("->")) { type = "function"; }
		if (IKeyword.VAR.equals(type) || IKeyword.CONST.equals(type)) {
			final Facet f = map.get(IKeyword.TYPE);
			if (f != null) { type = EGaml.getInstance().getKeyOf(f.getExpr()); }
		}
		String name = EGaml.getInstance().getNameOf(ele);
		if (name == null) {
			final Expression expr = ((Statement) ele).getExpr();
			if (expr != null) { name = EGaml.getInstance().getKeyOf(expr); }
		}
		if (name == null) {
			QualifiedName qn = null;
			try {
				qn = nameProvider.getFullyQualifiedName(ele);
				name = qn == null ? null : qn.toString();
			} catch (final IllegalArgumentException e) {
				name = null;
			}

		}
		String first = "Attribute ";
		if (Types.get(type) == Types.NO_TYPE) { first = Strings.capitalize(type) + " "; }

		return first + (name == null ? "" : name)
				+ (type == null ? "" : " (" + type + ") " + (key == null ? "" : "(" + key + ") "));

	}

	/**
	 * @param ele
	 * @return
	 */
	private static String actionText(final Statement ele) {
		final String type = EGaml.getInstance().getKeyOf(ele);
		final String name = EGaml.getInstance().getNameOf(ele);
		return "Action " + name + " " + (IKeyword.ACTION.equals(type) ? "" : " (" + type + ")");
	}

	/**
	 * Text.
	 *
	 * @param obj
	 *            the obj
	 * @return the string
	 */
	String text(final Model obj) {
		return "Model " + obj.getName();
	}

	/**
	 * Parameter text.
	 *
	 * @param p
	 *            the p
	 * @return the string
	 */
	protected String parameterText(final Statement p) {
		String type = null;
		String var = null;
		final Map<String, Facet> map = EGaml.getInstance().getFacetsMapOf(p);
		Facet f = map.get(IKeyword.VAR);
		if (f != null) {
			final Expression vr = f.getExpr();
			if (vr instanceof VariableRef) {
				final VarDefinition vd = ((VariableRef) vr).getRef();
				if (vd instanceof S_Declaration) {
					type = EGaml.getInstance().getKeyOf(vd);
					var = EGaml.getInstance().getNameOf(vd);
				}
			}
		}
		String name = null;
		f = map.get(IKeyword.NAME);
		if (f == null) {
			final Expression e = p.getExpr();
			if (e instanceof StringLiteral) { name = ((StringLiteral) e).getOp(); }
		} else {
			final Expression e = f.getExpr();
			if (e instanceof StringLiteral) { name = ((StringLiteral) e).getOp(); }
		}
		return "Parameter " + "\"" + name + "\""
				+ (var == null ? "" : " (" + var + ")" + (type == null ? "" : " (" + type + ")"));
	}

	/**
	 * Image.
	 *
	 * @param ele
	 *            the ele
	 * @return the string
	 */
	String image(final Import ele) {
		return "_include.png";
	}

	/**
	 * Image.
	 *
	 * @param ele
	 *            the ele
	 * @return the string
	 */
	String image(final S_Experiment ele) {
		final List<Facet> facets = EGaml.getInstance().getFacetsOf(ele);
		Facet type = null;
		for (final Facet f : facets) {
			if (f.getKey().startsWith(IKeyword.TYPE)) {
				type = f;
				break;
			}
		}
		if (type == null) return "_gui.png";
		return typeImage(EGaml.getInstance().toString(type.getExpr()));
	}

	/**
	 * Image.
	 *
	 * @param ele
	 *            the ele
	 * @return the string
	 */
	String image(final HeadlessExperiment ele) {
		final List<Facet> facets = EGaml.getInstance().getFacetsOf(ele);
		Facet type = null;
		for (final Facet f : facets) {
			if (f.getKey().startsWith(IKeyword.TYPE)) {
				type = f;
				break;
			}
		}
		if (type == null) return "_batch.png";
		return typeImage(EGaml.getInstance().toString(type.getExpr()));
	}

	/**
	 * Image.
	 *
	 * @param ele
	 *            the ele
	 * @return the string
	 */
	// Statement : keyword.value
	public String image(final Statement ele) {
		final String kw = EGaml.getInstance().getKeyOf(ele);
		if (kw == null) return null;
		if (IKeyword.PARAMETER.equals(kw)) return parameterImage(ele);
		if (IKeyword.VAR.equals(kw) || IKeyword.CONST.equals(kw)) {
			for (final Facet f : EGaml.getInstance().getFacetsOf(ele)) {
				if (EGaml.getInstance().getKeyOf(f).startsWith(IKeyword.TYPE))
					return typeImage(EGaml.getInstance().getKeyOf(f.getExpr()));
			}
		}
		return typeImage(kw);
	}

	/**
	 * Parameter image.
	 *
	 * @param p
	 *            the p
	 * @return the string
	 */
	protected String parameterImage(final Statement p) {
		if (!IKeyword.PARAMETER.equals(p.getKey())) return "_parameter.png";
		String var = null;
		final Facet f = EGaml.getInstance().getFacetsMapOf(p).get(IKeyword.VAR);
		if (f != null) {
			final Expression vr = f.getExpr();
			if (vr instanceof VariableRef) {
				final VarDefinition vd = ((VariableRef) vr).getRef();
				if (vd instanceof S_Declaration) { var = EGaml.getInstance().getKeyOf(vd); }
			}
		}
		if (var == null) return "_parameter.png";
		return "_" + var + ".png";
	}

	/**
	 * Type image.
	 *
	 * @param string
	 *            the string
	 * @return the string
	 */
	public String typeImage(final String string) {
		return "_" + string + ".png";
	}

	/**
	 * @see msi.gama.common.interfaces.IGamlLabelProvider#getText(msi.gaml.compilation.ast.ISyntacticElement)
	 */
	@Override
	public String getText(final ISyntacticElement element) {
		return this.getText(element.getElement());
	}

	/**
	 * @see msi.gama.common.interfaces.IGamlLabelProvider#getImage(msi.gaml.compilation.ast.ISyntacticElement)
	 */
	@Override
	public ImageDescriptor getImageDescriptor(final ISyntacticElement element) {
		return this.getImageDescriptor(element.getElement());
	}
	
	public ImageDescriptor convertToImageDescriptor(final Object obj) {
		return super.convertToImageDescriptor(obj);
	}

}
