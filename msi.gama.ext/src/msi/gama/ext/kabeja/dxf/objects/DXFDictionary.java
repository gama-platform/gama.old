/*******************************************************************************************************
 *
 * DXFDictionary.java, in msi.gama.ext, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.ext.kabeja.dxf.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import msi.gama.ext.kabeja.dxf.DXFConstants;

/**
 * The Class DXFDictionary.
 */
public class DXFDictionary extends DXFObject {
	
	/** The records. */
	protected ArrayList<DXFDictionaryRecord> records = new ArrayList<>();

	@Override
	public String getObjectType() { return DXFConstants.OBJECT_TYPE_DICTIONARY; }

	/**
	 * Checks for DXF object by ID.
	 *
	 * @param id the id
	 * @return true, if successful
	 */
	public boolean hasDXFObjectByID(final String id) {
		return findByID(id) != null;
	}

	/**
	 * Gets the name for DXF object ID.
	 *
	 * @param id the id
	 * @return the name for DXF object ID
	 */
	public String getNameForDXFObjectID(final String id) {
		return findByID(id).getName();
	}

	/**
	 * Gets the
	 *
	 * @see DXFObject with the specified ID.
	 * @param id
	 * @return the DXFObject or null if there is no such DXFObject
	 */
	public DXFObject getDXFObjectByID(final String id) {
		// search for child dictionaries
		DXFDictionary dic = this.getDXFDictionaryForID(id);

		if (dic != null) {
			DXFDictionaryRecord dicRecord = dic.findByID(id);

			if (dicRecord != null) return dicRecord.getDXFObject();
		}

		return null;
	}

	/**
	 * Gets the DXF object by name.
	 *
	 * @param name the name
	 * @return the DXF object by name
	 */
	public DXFObject getDXFObjectByName(final String name) {
		DXFDictionaryRecord record = findByName(name);

		if (record != null) return record.getDXFObject();

		return null;
	}

	/**
	 * Put DXF object.
	 *
	 * @param obj the obj
	 */
	public void putDXFObject(final DXFObject obj) {
		findByID(obj.getID()).setDXFObject(obj);
	}

	/**
	 * Put DXF object relation.
	 *
	 * @param name the name
	 * @param id the id
	 */
	public void putDXFObjectRelation(final String name, final String id) {
		DXFDictionaryRecord record = null;

		if ((record = findByName(name)) != null) {
			record.setID(id);
		} else {
			record = new DXFDictionaryRecord(name, id);
			this.records.add(record);
		}
	}

	/**
	 * Find by name.
	 *
	 * @param name the name
	 * @return the DXF dictionary record
	 */
	protected DXFDictionaryRecord findByName(final String name) {
		for (int i = 0; i < this.records.size(); i++) {
			DXFDictionaryRecord record = records.get(i);

			if (record.getName().equals(name)) return record;
		}

		return null;
	}

	/**
	 * Find by ID.
	 *
	 * @param id the id
	 * @return the DXF dictionary record
	 */
	protected DXFDictionaryRecord findByID(final String id) {
		for (int i = 0; i < this.records.size(); i++) {
			DXFDictionaryRecord record = records.get(i);

			if (record.getID().equals(id)) return record;
		}

		return null;
	}

	/**
	 * Searches recursive for the dictionary which holds the ID
	 *
	 * @param id
	 * @return the dictionary or null
	 */
	public DXFDictionary getDXFDictionaryForID(final String id) {
		Set<DXFObject> dictionaries = new HashSet<>();
		DXFObject obj = null;

		for (int i = 0; i < this.records.size(); i++) {
			DXFDictionaryRecord record = records.get(i);

			if (record.getID().equals(id)) return this;
			if ((obj = record.getDXFObject()) != null
					&& DXFConstants.OBJECT_TYPE_DICTIONARY.equals(obj.getObjectType())) {
				dictionaries.add(obj);
			}
		}

		Iterator<DXFObject> ie = dictionaries.iterator();

		while (ie.hasNext()) {
			DXFDictionary dic = (DXFDictionary) ie.next();
			DXFDictionary d = dic.getDXFDictionaryForID(id);

			if (d != null) return d;
		}

		return null;
	}

	/**
	 *
	 * @return iterator over all DXFObjects in this dictionary
	 */
	public Iterator<?> getDXFObjectIterator() {
		return new Iterator<Object>() {
			int count = 0;

			@Override
			public boolean hasNext() {
				return count < records.size();
			}

			@Override
			public Object next() {
				return records.get(count++).getDXFObject();
			}

			@Override
			public void remove() {
				records.remove(count - 1);
			}
		};
	}

	/**
	 * The Class DXFDictionaryRecord.
	 */
	private static class DXFDictionaryRecord {
		
		/** The id. */
		private String id;
		
		/** The name. */
		private final String name;
		
		/** The obj. */
		private DXFObject obj;

		/**
		 * Instantiates a new DXF dictionary record.
		 *
		 * @param name the name
		 * @param id the id
		 */
		public DXFDictionaryRecord(final String name, final String id) {
			this.id = id;
			this.name = name;
		}

		/**
		 * Gets the name.
		 *
		 * @return the name
		 */
		public String getName() { return this.name; }

		/**
		 * Gets the id.
		 *
		 * @return the id
		 */
		public String getID() { return this.id; }

		/**
		 * Sets the id.
		 *
		 * @param id the new id
		 */
		public void setID(final String id) { this.id = id; }

		/**
		 * Sets the DXF object.
		 *
		 * @param obj the new DXF object
		 */
		public void setDXFObject(final DXFObject obj) { this.obj = obj; }

		/**
		 * Gets the DXF object.
		 *
		 * @return the DXF object
		 */
		public DXFObject getDXFObject() { return this.obj; }
	}
}
