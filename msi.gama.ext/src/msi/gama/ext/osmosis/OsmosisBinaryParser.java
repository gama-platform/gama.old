// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.ext.osmosis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import msi.gama.ext.osmosis.Osmformat.DenseInfo;

/** Class that reads and parses binary files and sends the contained entities to the sink. */
public class OsmosisBinaryParser extends BinaryParser {

	@Override
	public void complete() {
		sink.complete();
	}

	/**
	 * Get the osmosis object representing a the user in a given Info protobuf.
	 *
	 * @param info
	 *            The info protobuf.
	 * @return The OsmUser object
	 */
	OsmUser getUser(final Osmformat.Info info) {
		// System.out.println(info);
		if (info.hasUid() && info.hasUserSid()) {
			if (info.getUid() < 0) { return OsmUser.NONE; }
			return new OsmUser(info.getUid(), getStringById(info.getUserSid()));
		} else {
			return OsmUser.NONE;
		}
	}

	/** The magic number used to indicate no version number metadata for this entity. */
	static final int NOVERSION = -1;
	/** The magic number used to indicate no changeset metadata for this entity. */
	static final int NOCHANGESET = -1;

	@Override
	protected void parseNodes(final List<Osmformat.Node> nodes) {
		for (final Osmformat.Node i : nodes) {
			final List<Tag> tags = new ArrayList<>();
			for (int j = 0; j < i.getKeysCount(); j++) {
				tags.add(new Tag(getStringById(i.getKeys(j)), getStringById(i.getVals(j))));
			}
			// long id, int version, Date timestamp, OsmUser user,
			// long changesetId, Collection<Tag> tags,
			// double latitude, double longitude
			Node tmp;
			final long id = i.getId();
			final double latf = parseLat(i.getLat()), lonf = parseLon(i.getLon());

			if (i.hasInfo()) {
				final Osmformat.Info info = i.getInfo();
				tmp = new Node(new CommonEntityData(id, info.getVersion(), getDate(info), getUser(info),
						info.getChangeset(), tags), latf, lonf);
			} else {
				tmp = new Node(new CommonEntityData(id, NOVERSION, NODATE, OsmUser.NONE, NOCHANGESET, tags), latf,
						lonf);
			}
			sink.process(new NodeContainer(tmp));

		}
	}

	@Override
	protected void parseDense(final Osmformat.DenseNodes nodes) {
		long lastId = 0, lastLat = 0, lastLon = 0;

		int j = 0; // Index into the keysvals array.

		// Stuff for dense info
		long lasttimestamp = 0, lastchangeset = 0;
		int lastuserSid = 0, lastuid = 0;
		DenseInfo di = null;
		if (nodes.hasDenseinfo()) {
			di = nodes.getDenseinfo();
		}
		for (int i = 0; i < nodes.getIdCount(); i++) {
			Node tmp;
			final List<Tag> tags = new ArrayList<>(0);
			final long lat = nodes.getLat(i) + lastLat;
			lastLat = lat;
			final long lon = nodes.getLon(i) + lastLon;
			lastLon = lon;
			final long id = nodes.getId(i) + lastId;
			lastId = id;
			final double latf = parseLat(lat), lonf = parseLon(lon);
			// If empty, assume that nothing here has keys or vals.
			if (nodes.getKeysValsCount() > 0) {
				while (nodes.getKeysVals(j) != 0) {
					final int keyid = nodes.getKeysVals(j++);
					final int valid = nodes.getKeysVals(j++);
					tags.add(new Tag(getStringById(keyid), getStringById(valid)));
				}
				j++; // Skip over the '0' delimiter.
			}
			// Handle dense info.
			if (di != null) {
				final int uid = di.getUid(i) + lastuid;
				lastuid = uid;
				final int userSid = di.getUserSid(i) + lastuserSid;
				lastuserSid = userSid;
				final long timestamp = di.getTimestamp(i) + lasttimestamp;
				lasttimestamp = timestamp;
				final int version = di.getVersion(i);
				final long changeset = di.getChangeset(i) + lastchangeset;
				lastchangeset = changeset;

				final Date date = new Date(date_granularity * timestamp);

				OsmUser user;
				if (uid < 0) {
					user = OsmUser.NONE;
				} else {
					user = new OsmUser(uid, getStringById(userSid));
				}
				tmp = new Node(new CommonEntityData(id, version, date, user, changeset, tags), latf, lonf);
			} else {
				tmp = new Node(new CommonEntityData(id, NOVERSION, NODATE, OsmUser.NONE, NOCHANGESET, tags), latf,
						lonf);
			}
			sink.process(new NodeContainer(tmp));
		}
	}

	@Override
	protected void parseWays(final List<Osmformat.Way> ways) {
		for (final Osmformat.Way i : ways) {
			final List<Tag> tags = new ArrayList<>();
			for (int j = 0; j < i.getKeysCount(); j++) {
				tags.add(new Tag(getStringById(i.getKeys(j)), getStringById(i.getVals(j))));
			}
			long lastId = 0;
			long lastLat = 0;
			long lastLon = 0;
			final List<WayNode> nodes = new ArrayList<>();
			for (int index = 0; index < i.getRefsCount(); index++) {
				final long identifier = lastId + i.getRefs(index);
				WayNode node;
				if (index < i.getLatCount() && index < i.getLonCount()) {
					final long lat = lastLat + i.getLat(index);
					final long lon = lastLon + i.getLon(index);
					node = new WayNode(identifier, parseLat(lat), parseLon(lon));
					lastLat = lat;
					lastLon = lon;
				} else {
					node = new WayNode(identifier);
				}
				nodes.add(node);
				lastId = identifier;
			}

			final long id = i.getId();

			// long id, int version, Date timestamp, OsmUser user,
			// long changesetId, Collection<Tag> tags,
			// List<WayNode> wayNodes
			Way tmp;
			if (i.hasInfo()) {
				final Osmformat.Info info = i.getInfo();
				tmp = new Way(new CommonEntityData(id, info.getVersion(), getDate(info), getUser(info),
						info.getChangeset(), tags), nodes);
			} else {
				tmp = new Way(new CommonEntityData(id, NOVERSION, NODATE, OsmUser.NONE, NOCHANGESET, tags), nodes);
			}
			sink.process(new WayContainer(tmp));
		}
	}

	@Override
	protected void parseRelations(final List<Osmformat.Relation> rels) {
		for (final Osmformat.Relation i : rels) {
			final List<Tag> tags = new ArrayList<>();
			for (int j = 0; j < i.getKeysCount(); j++) {
				tags.add(new Tag(getStringById(i.getKeys(j)), getStringById(i.getVals(j))));
			}

			final long id = i.getId();

			long lastMid = 0;
			final List<RelationMember> nodes = new ArrayList<>();
			for (int j = 0; j < i.getMemidsCount(); j++) {
				final long mid = lastMid + i.getMemids(j);
				lastMid = mid;
				final String role = getStringById(i.getRolesSid(j));
				EntityType etype = null;

				if (i.getTypes(j) == Osmformat.Relation.MemberType.NODE) {
					etype = EntityType.Node;
				} else if (i.getTypes(j) == Osmformat.Relation.MemberType.WAY) {
					etype = EntityType.Way;
				} else if (i.getTypes(j) == Osmformat.Relation.MemberType.RELATION) {
					etype = EntityType.Relation;
				} else {
					assert false; // TODO; Illegal file?
				}

				nodes.add(new RelationMember(mid, etype, role));
			}
			// long id, int version, TimestampContainer timestampContainer,
			// OsmUser user,
			// long changesetId, Collection<Tag> tags,
			// List<RelationMember> members
			Relation tmp;
			if (i.hasInfo()) {
				final Osmformat.Info info = i.getInfo();
				tmp = new Relation(new CommonEntityData(id, info.getVersion(), getDate(info), getUser(info),
						info.getChangeset(), tags), nodes);
			} else {
				tmp = new Relation(new CommonEntityData(id, NOVERSION, NODATE, OsmUser.NONE, NOCHANGESET, tags), nodes);
			}
			sink.process(new RelationContainer(tmp));
		}
	}

	@Override
	public void parse(final Osmformat.HeaderBlock block) {
		for (final String s : block.getRequiredFeaturesList()) {
			if (s.equals("OsmSchema-V0.6")) {
				continue; // We can parse this.
			}
			if (s.equals("DenseNodes")) {
				continue; // We can parse this.
			}
			throw new OsmosisRuntimeException("File requires unknown feature: " + s);
		}

		if (block.hasBbox()) {
			String source = "no-version-specified";
			if (block.hasSource()) {
				source = block.getSource();
			}

			final double multiplier = .000000001;
			final double rightf = block.getBbox().getRight() * multiplier;
			final double leftf = block.getBbox().getLeft() * multiplier;
			final double topf = block.getBbox().getTop() * multiplier;
			final double bottomf = block.getBbox().getBottom() * multiplier;

			final Bound bounds = new Bound(rightf, leftf, topf, bottomf, source);
			sink.process(new BoundContainer(bounds));
		}
	}

	/**
	 * Sets the osm sink to send data to.
	 *
	 * @param sink
	 *            The sink for receiving all produced data.
	 */
	public void setSink(final Sink sink) {
		this.sink = sink;
	}

	private Sink sink;
}
