// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.ext.osmosis;

import org.xml.sax.Attributes;

/**
 * Provides an element processor implementation for a relation member.
 *
 * @author Brett Henderson
 */
public class RelationMemberElementProcessor extends BaseElementProcessor {
	private static final String ATTRIBUTE_NAME_ID = "ref";
	private static final String ATTRIBUTE_NAME_TYPE = "type";
	private static final String ATTRIBUTE_NAME_ROLE = "role";

	private final RelationMemberListener relationMemberListener;
	private RelationMember relationMember;
	private final MemberTypeParser memberTypeParser;

	/**
	 * Creates a new instance.
	 *
	 * @param parentProcessor
	 *            The parent element processor.
	 * @param relationMemberListener
	 *            The relation member listener for receiving created tags.
	 */
	public RelationMemberElementProcessor(final BaseElementProcessor parentProcessor,
			final RelationMemberListener relationMemberListener) {
		super(parentProcessor, true);

		this.relationMemberListener = relationMemberListener;

		memberTypeParser = new MemberTypeParser();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void begin(final Attributes attributes) {
		long id;
		EntityType type;
		String role;

		id = Long.parseLong(attributes.getValue(ATTRIBUTE_NAME_ID));
		type = memberTypeParser.parse(attributes.getValue(ATTRIBUTE_NAME_TYPE));
		role = attributes.getValue(ATTRIBUTE_NAME_ROLE);
		if (role == null) {
			role = ""; // this may actually happen
		}

		relationMember = new RelationMember(id, type, role);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void end() {
		relationMemberListener.processRelationMember(relationMember);
		relationMember = null;
	}
}
