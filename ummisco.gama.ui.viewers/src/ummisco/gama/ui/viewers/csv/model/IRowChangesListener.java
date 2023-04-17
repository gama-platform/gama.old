/*******************************************************************************************************
 *
 * IRowChangesListener.java, in ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.viewers.csv.model;

/**
 * The listener interface for receiving IRowChanges events.
 * The class that is interested in processing a IRowChanges
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addIRowChangesListener<code> method. When
 * the IRowChanges event occurs, that object's appropriate
 * method is invoked.
 *
 * @see IRowChangesEvent
 */
public interface IRowChangesListener
{
    /**
     * Element at the given index position has changes in
     * @param row the {@link CSVRow} which changed
     * @param index the index position
     */
    void rowChanged(CSVRow row, int index);
}

