/*******************************************************************************************************
 *
 * ICsvFileModelListener.java, in ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.viewers.csv.model;

/**
 *
 * @author fhenri
 *
 */
public interface ICsvFileModelListener {

    /**
     * Entry changed.
     *
     * @param row the row
     * @param rowIndex the row index
     */
    void entryChanged(CSVRow row, int rowIndex);

}
