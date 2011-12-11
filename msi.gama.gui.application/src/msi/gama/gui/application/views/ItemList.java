/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application.views;

import java.util.List;

/**
 * Written by drogoul Modified on 13 mai 2011
 * 
 * @todo Description
 * 
 */
public interface ItemList<T> {

	public static final Character ERROR_CODE = 'Û';
	public static final Character INFO_CODE = '¤';
	public static final Character WARNING_CODE = '`';
	public static final Character SEPARATION_CODE = '£';

	boolean addItem(T obj);

	void removeItem(T obj);

	void pauseItem(T obj);

	void resumeItem(T obj);

	void focusItem(T obj);

	public List<T> getItems();

	String getItemDisplayName(T obj, String previousName);

	void updateItemValues();

}
