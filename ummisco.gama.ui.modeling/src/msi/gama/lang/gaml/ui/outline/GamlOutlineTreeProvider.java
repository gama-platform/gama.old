/*********************************************************************************************
 *
 *
 * 'GamlOutlineTreeProvider.java', in plugin 'msi.gama.lang.gaml.ui', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.outline;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.AbstractOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider;

import com.google.inject.Inject;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.EGaml;
import msi.gama.lang.gaml.gaml.Block;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.S_Action;
import msi.gama.lang.gaml.gaml.S_Definition;
import msi.gama.lang.gaml.gaml.S_Experiment;
import msi.gama.lang.gaml.gaml.S_Global;
import msi.gama.lang.gaml.gaml.S_Species;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.ui.labeling.GamlLabelProvider;
import msi.gama.precompiler.ISymbolKind;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.factories.DescriptionFactory;

/**
 * customization of the default outline structure
 *
 */
public class GamlOutlineTreeProvider extends DefaultOutlineTreeProvider {

	// @Inject
	// private IImageHelper imageHelper;

	@Inject
	private GamlLabelProvider provider;

	// private int speciesCount = 0;
	// private int experimentCount = 0;

	@Override
	protected void _createChildren(final IOutlineNode parentNode, final EObject stm) {

	}

	protected void _createChildren(final IOutlineNode parentNode, final Model stm) {
		// speciesCount = 0;
		// experimentCount = 0;
		Block block = stm.getBlock();
		if (block != null) {
			for (final Statement s : EGaml.getStatementsOf(block)) {
				if (s instanceof S_Global /*
											 * || s instanceof S_Environment ||
											 * s instanceof S_Entities
											 */ ) {
					block = s.getBlock();
					if (block != null) {
						ownCreateChildren(parentNode, s);
					}
				} else {
					createNode(parentNode, s);
				}
			}
		}
	}

	// protected void _createChildren(final IOutlineNode parentNode, final
	// S_Global stm) {
	// Block block = stm.getBlock();
	// if ( block != null ) {
	// for ( Statement substm : EGaml.getStatementsOf(block) ) {
	// createNode(parentNode, substm);
	// }
	// }
	// }

	protected void _createChildren(final IOutlineNode parentNode, final S_Experiment stm) {
		ownCreateChildren(parentNode, stm);
	}

	protected void _createChildren(final IOutlineNode parentNode, final S_Species stm) {
		ownCreateChildren(parentNode, stm);
	}

	protected void ownCreateChildren(final IOutlineNode parentNode, final Statement stm) {

		final Block block = stm.getBlock();
		IOutlineNode attributesNode = null;
		IOutlineNode parametersNode = null;
		IOutlineNode actionsNode = null;
		if (block != null) {
			for (final Statement s : EGaml.getStatementsOf(block)) {
				if (isAttribute(s)) {
					if (attributesNode == null) {
						attributesNode = new AbstractOutlineNode(parentNode, provider.convertToImage("_attributes.png"),
								"Attributes", false) {
						};
					}
					createNode(attributesNode, s);
				} else if (IKeyword.PARAMETER.equals(s.getKey())) {
					if (parametersNode == null) {
						parametersNode = new AbstractOutlineNode(parentNode, provider.convertToImage("_parameter.png"),
								"Parameters", false) {
						};
					}
					createNode(parametersNode, s);
				} else if (isAction(s)) {
					if (actionsNode == null) {
						actionsNode = new AbstractOutlineNode(parentNode, provider.convertToImage("_action.png"),
								"Actions", false) {
						};
					}
					createNode(actionsNode, s);

				}

				else {
					createNode(parentNode, s);
				}
			}
		}
	}

	/**
	 * @param s
	 * @return
	 */
	public static boolean isAttribute(final Statement s) {
		if (!(s instanceof S_Definition)) {
			return false;
		}
		final String key = EGaml.getKeyOf(s);
		if (IKeyword.ACTION.equals(key)) {
			return false;
		}
		if (s.getBlock() != null && s.getBlock().getFunction() == null) {
			return false;
		}
		// if ( s instanceof S_Definition ) {
		// if ( ((S_Definition) s).getArgs() != null ) { return false; }
		// }
		final SymbolProto p = DescriptionFactory.getStatementProto(key);
		if (p != null && p.getKind() == ISymbolKind.BATCH_METHOD) {
			return false;
		}
		return true;
	}

	public static boolean isAction(final Statement s) {
		if (!(s instanceof S_Definition)) {
			return false;
		}
		if (s instanceof S_Action) {
			return true;
		}
		final String key = EGaml.getKeyOf(s);
		final SymbolProto p = DescriptionFactory.getStatementProto(key);
		if (p != null && p.isTopLevel()) {
			return false;
		}
		if (s.getKey() == null) {
			return true;
		}
		return false;
	}

	// protected void _createNode(final IOutlineNode parentNode, final Parameter
	// stm) {
	//
	// }

	protected void _createNode(final IOutlineNode parentNode, final S_Global stm) {
		//
	}
	//
	// protected void _createNode(final IOutlineNode parentNode, final
	// S_Entities stm) {
	// //
	// }
	//
	// protected void _createNode(final IOutlineNode parentNode, final
	// S_Environment stm) {
	// //
	// }

	protected boolean _isLeaf(final S_Experiment s) {
		// use eIsSet !
		return s.getBlock() == null || s.getBlock().getStatements().isEmpty();
	}

	protected boolean _isLeaf(final S_Species s) {
		return s.getBlock() == null || s.getBlock().getStatements().isEmpty();
	}

	protected boolean _isLeaf(final Model s) {
		return s.getBlock() == null;
	}

	@Override
	protected boolean _isLeaf(final EObject s) {
		return true;
	}
}
