package msi.gama.util.file.json;

/**
 * The Class HashIndexTable.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 29 oct. 2023
 */
class HashIndexTable {

	/** The hash table. */
	private final byte[] hashTable = new byte[32]; // must be a power of two

	/**
	 * Instantiates a new hash index table.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 29 oct. 2023
	 */
	HashIndexTable() {}

	/**
	 * Instantiates a new hash index table.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param original
	 *            the original
	 * @date 29 oct. 2023
	 */
	HashIndexTable(final HashIndexTable original) {
		System.arraycopy(original.hashTable, 0, hashTable, 0, hashTable.length);
	}

	/**
	 * Adds the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the name
	 * @param index
	 *            the index
	 * @date 29 oct. 2023
	 */
	void add(final String name, final int index) {
		int slot = hashSlotFor(name);
		if (index < 0xff) {
			// increment by 1, 0 stands for empty
			hashTable[slot] = (byte) (index + 1);
		} else {
			hashTable[slot] = 0;
		}
	}

	/**
	 * Removes the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param index
	 *            the index
	 * @date 29 oct. 2023
	 */
	void remove(final int index) {
		for (int i = 0; i < hashTable.length; i++) {
			if ((hashTable[i] & 0xff) == index + 1) {
				hashTable[i] = 0;
			} else if ((hashTable[i] & 0xff) > index + 1) { hashTable[i]--; }
		}
	}

	/**
	 * Gets the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the name
	 * @return the int
	 * @date 29 oct. 2023
	 */
	int get(final Object name) {
		int slot = hashSlotFor(name);
		// subtract 1, 0 stands for empty
		return (hashTable[slot] & 0xff) - 1;
	}

	/**
	 * Hash slot for.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param element
	 *            the element
	 * @return the int
	 * @date 29 oct. 2023
	 */
	private int hashSlotFor(final Object element) {
		return element.hashCode() & hashTable.length - 1;
	}

}