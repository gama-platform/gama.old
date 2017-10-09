/*********************************************************************************************
 *
 * 'GamlLabelProvider.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.labeling;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
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

/**
 * Provides labels for a EObjects.
 *
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
@Singleton

public class GamlLabelProvider extends DefaultEObjectLabelProvider implements IGamlLabelProvider {

	@Inject private IQualifiedNameProvider nameProvider;

	@Inject
	public GamlLabelProvider(final AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}

	@Override
	public Image convertToImage(final Object imageDescription) {
		return super.convertToImage(imageDescription);
	}

	String image(final Model ele) {
		return "_model.png";
	}

	// Import
	String text(final Import ele) {
		String display = ele.getImportURI();
		final int index = display.lastIndexOf('/');
		if (index >= 0) {
			display = display.substring(index + 1);
		}
		return "import " + display;
	}

	String text(final HeadlessExperiment e) {
		return "Experiment " + e.getName();
	}

	String text(final EObject ele) {
		String text;
		String key = EGaml.getKeyOf(ele);
		if (key == null) {
			key = "";
		}
		text = key;
		key = null;
		if (ele instanceof Statement) {
			if (text.equals(IKeyword.PARAMETER)) { return parameterText((Statement) ele); }
			if (GamlOutlineTreeProvider.isAttribute((Statement) ele)) { return attributeText((S_Definition) ele); }
			if (GamlOutlineTreeProvider.isAction((Statement) ele)) { return actionText((Statement) ele); }
			String name = EGaml.getNameOf((Statement) ele);
			if (name == null) {
				final Expression expr = ((Statement) ele).getExpr();
				if (expr != null) {
					name = EGaml.getKeyOf(expr);
				}
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
		String type = EGaml.getKeyOf(ele);
		String key = type.equals(IKeyword.CONST) ? type : null;
		final Map<String, Facet> map = EGaml.getFacetsMapOf(ele);
		if (ele.getBlock() != null && ele.getBlock().getFunction() != null) {
			key = "function";
		} else {
			if (map.containsKey(IKeyword.FUNCTION) || map.containsKey("->")) {
				type = "function";
			}
		}
		if (type.equals(IKeyword.VAR) || type.equals(IKeyword.CONST)) {
			final Facet f = map.get(IKeyword.TYPE);
			if (f != null) {
				type = EGaml.getKeyOf(f.getExpr());
			}
		}
		String name = EGaml.getNameOf(ele);
		if (name == null) {
			final Expression expr = ((Statement) ele).getExpr();
			if (expr != null) {
				name = EGaml.getKeyOf(expr);
			}
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
		return "Attribute " + (name == null ? "" : name)
				+ (type == null ? "" : " (" + type + ") " + (key == null ? "" : "(" + key + ") "));

	}

	/**
	 * @param ele
	 * @return
	 */
	private String actionText(final Statement ele) {
		final String type = EGaml.getKeyOf(ele);
		final String name = EGaml.getNameOf(ele);
		return "Action " + name + " " + (type.equals(IKeyword.ACTION) ? "" : " (" + type + ")");
	}

	String text(final Model obj) {
		return "Model " + obj.getName();
	}

	protected String parameterText(final Statement p) {
		String type = null;
		String var = null;
		final Map<String, Facet> map = EGaml.getFacetsMapOf(p);
		Facet f = map.get(IKeyword.VAR);
		if (f != null) {
			final Expression vr = f.getExpr();
			if (vr instanceof VariableRef) {
				final VarDefinition vd = ((VariableRef) vr).getRef();
				if (vd instanceof S_Declaration) {
					type = EGaml.getKeyOf(vd);
					var = EGaml.getNameOf((S_Declaration) vd);
				}
			}
		}
		// if ( type == null ) {
		// type = "parameter";
		// }
		String name = null;
		f = map.get(IKeyword.NAME);
		if (f == null) {
			final Expression e = p.getExpr();
			if (e instanceof StringLiteral) {
				name = e.getOp();
			}
		} else {
			final Expression e = f.getExpr();
			if (e instanceof StringLiteral) {
				name = e.getOp();
			}
		}
		return "Parameter " + "\"" + name + "\""
				+ (var == null ? "" : " (" + var + ")" + (type == null ? "" : " (" + type + ")"));
	}

	String image(final Import ele) {
		return "_include.png";
	}

	String image(final S_Experiment ele) {
		final List<Facet> facets = EGaml.getFacetsOf(ele);
		Facet type = null;
		for (final Facet f : facets) {
			if (f.getKey().startsWith(IKeyword.TYPE)) {
				type = f;
				break;
			}
		}
		if (type == null) { return "_gui.png"; }
		return typeImage(EGaml.toString(type.getExpr()));
	}

	String image(final HeadlessExperiment ele) {
		final List<Facet> facets = EGaml.getFacetsOf(ele);
		Facet type = null;
		for (final Facet f : facets) {
			if (f.getKey().startsWith(IKeyword.TYPE)) {
				type = f;
				break;
			}
		}
		if (type == null) { return "_batch.png"; }
		return typeImage(EGaml.toString(type.getExpr()));
	}

	// Statement : keyword.value
	public String image(final Statement ele) {
		final String kw = EGaml.getKeyOf(ele);
		if (kw == null)
			return null;
		if (kw.equals(IKeyword.PARAMETER)) { return parameterImage(ele); }
		if (kw.equals(IKeyword.VAR) || kw.equals(IKeyword.CONST)) {
			for (final Facet f : EGaml.getFacetsOf(ele)) {
				if (EGaml.getKeyOf(f).startsWith(IKeyword.TYPE)) { return typeImage(EGaml.getKeyOf(f.getExpr())); }
			}
		}
		return typeImage(kw);
	}

	protected String parameterImage(final Statement p) {
		if (IKeyword.PARAMETER.equals(p.getKey())) {
			String var = null;
			final Facet f = EGaml.getFacetsMapOf(p).get(IKeyword.VAR);
			if (f != null) {
				final Expression vr = f.getExpr();
				if (vr instanceof VariableRef) {
					final VarDefinition vd = ((VariableRef) vr).getRef();
					if (vd instanceof S_Declaration) {
						var = EGaml.getKeyOf(vd);
					}
				}
			}
			if (var == null) { return "_parameter.png"; }
			return "_" + var + ".png";
		} else {
			return "_parameter.png";
		}
	}

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
	public Object getImage(final ISyntacticElement element) {
		return this.getImage(element.getElement());
	}

}
