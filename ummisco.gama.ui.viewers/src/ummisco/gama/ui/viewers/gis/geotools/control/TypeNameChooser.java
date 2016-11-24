/*********************************************************************************************
 *
 * 'TypeNameChooser.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.gis.geotools.control;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.geotools.data.DataStore;

/**
 * 
 *
 * @source $URL$
 */
public class TypeNameChooser {
    
    public static String showTypeNameChooser( DataStore dataStore ){
        if( dataStore == null ){
            return null; // could not connect
        }
        String typeNames[];
        try {
            typeNames = dataStore.getTypeNames();
        } catch (IOException e) {
            return null; // could not connect
        }
        if( typeNames.length == 0 ){
            return null; // could not connect
        }
        if (typeNames.length == 1) {
            return typeNames[0]; // no need to choose only one option
        } else {
            String typeName = (String) JOptionPane
                    .showInputDialog(null, "Please select a type name.", "Type Name",
                            JOptionPane.QUESTION_MESSAGE, null, typeNames, typeNames[0]);
            return typeName;
        }
    }
}
