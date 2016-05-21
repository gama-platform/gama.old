/**
 * Created by hqngh, May 22, 2016
 * 
 */
package msi.gama.lang.gaml.ui.editor;
/*
 * This file is part of OpenModelica.
 *
 * Copyright (c) 1998-CurrentYear, Open Source Modelica Consortium (OSMC),
 * c/o Linköpings universitet, Department of Computer and Information Science,
 * SE-58183 Linköping, Sweden.
 *
 * All rights reserved.
 *
 * THIS PROGRAM IS PROVIDED UNDER THE TERMS OF GPL VERSION 3 LICENSE OR 
 * THIS OSMC PUBLIC LICENSE (OSMC-PL). 
 * ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS PROGRAM CONSTITUTES RECIPIENT'S ACCEPTANCE
 * OF THE OSMC PUBLIC LICENSE OR THE GPL VERSION 3, ACCORDING TO RECIPIENTS CHOICE. 
 *
 * The OpenModelica software and the Open Source Modelica
 * Consortium (OSMC) Public License (OSMC-PL) are obtained
 * from OSMC, either from the above address,
 * from the URLs: http://www.ida.liu.se/projects/OpenModelica or  
 * http://www.openmodelica.org, and in the OpenModelica distribution. 
 * GNU version 3 is obtained from: http://www.gnu.org/copyleft/gpl.html.
 *
 * This program is distributed WITHOUT ANY WARRANTY; without
 * even the implied warranty of  MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE, EXCEPT AS EXPRESSLY SET FORTH
 * IN THE BY RECIPIENT SELECTED SUBSIDIARY LICENSE CONDITIONS OF OSMC-PL.
 *
 * See the full OSMC Public License conditions for more details.
 *
 * Main author: Wladimir Schamai, EADS Innovation Works / Linköping University, 2009-now
 *
 * Contributors: 
 *   Uwe Pohlmann, University of Paderborn 2009-2010, contribution to the Modelica code generation for state machine behavior, contribution to Papyrus GUI adoptations
 *   Parham Vasaiely, EADS Innovation Works / Hamburg University of Applied Sciences 2009-2011, implementation of simulation plugins
 */
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.ToolItem;

import msi.gama.gui.swt.GamaKeyBindings;
import msi.gama.gui.swt.controls.FlatButton;
import msi.gama.gui.swt.controls.GamaToolbarSimple;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving propertiesSectionXtextEditorKey events.
 * The class that is interested in processing a propertiesSectionXtextEditorKey
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addPropertiesSectionXtextEditorKeyListener<code> method. When
 * the propertiesSectionXtextEditorKey event occurs, that object's appropriate
 * method is invoked.
 *
 * @author koehnlein
 */
public class GamlEditorKeyListener extends KeyAdapter implements VerifyKeyListener {

	/**
	 * This element comes from the XText/GMF integration example, and was not originally documented.
	 *
	 * @param popupXtextEditorHelper the popup xtext editor helper
	 * @param contentAssistant the content assistant
	 */
	final GamlEditor editor;
	public GamlEditorKeyListener(final GamlEditor ed) {
		editor = ed;
	}

	/**
	 * Key pressed.
	 *
	 * @param e the e
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (!GamaKeyBindings.ctrl(e))
			return;
		switch (e.keyCode) {
		// Handles Launch the first experiment button
		case '1':
			if (GamaKeyBindings.shift(e)) {
				GamaToolbarSimple gts= editor.toolbar.getToolbar(SWT.LEFT);
				ToolItem ti=gts.getItem(0);
				if(ti.getControl() instanceof FlatButton){
					System.out.println("launch the first experiment");
					((FlatButton)ti.getControl()).fireButtonClick();
				}
			}
			break;
		}
	}

	/**
	 * Verify key.
	 *
	 * @param e the e
	 */
	public void verifyKey(VerifyEvent e) {
	}

}