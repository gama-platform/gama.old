// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.util.file.osmosis_copy;

/**
 * Provides the definition of a class receiving relation members.
 *
 * @author Brett Henderson
 */
public interface RelationMemberListener {
	/**
	 * Processes the relation member.
	 *
	 * @param relationMember
	 *            The relation member.
	 */
	void processRelationMember(RelationMember relationMember);
}
