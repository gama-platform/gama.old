/*******************************************************************************************************
 *
 * GosplPopulationInDatabase.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling
 * and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;

import core.metamodel.IPopulation;
import core.metamodel.IQueryablePopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.entity.EntityUniqueId;
import core.metamodel.value.IValue;
import core.metamodel.value.binary.BinarySpace;
import core.metamodel.value.binary.BooleanValue;
import core.util.exception.GenstarException;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Stores a population in database; provides quick access.
 *
 * prepared statements for perf
 *
 * create indexes on dimensions often queried together
 *
 * @author Samuel Thiriot
 */
public class GosplPopulationInDatabase implements IQueryablePopulation<ADemoEntity, Attribute<? extends IValue>> {

	/** The Constant VARCHAR_SIZE. */
	public static final int VARCHAR_SIZE = 255;

	/** The Constant MAX_BUFFER_QRY. */
	public static final int MAX_BUFFER_QRY = 10000;

	/** The Constant DEFAULT_ENTITY_TYPE. */
	public static final String DEFAULT_ENTITY_TYPE = "unknown";

	/** The remove entities batch. */
	public static final int REMOVE_ENTITIES_BATCH = 500;

	/** The add entities batch. */
	public static final int ADD_ENTITIES_BATCH = 5000; // more !

	/** The connection. */
	private final Connection connection;

	/** The entity type 2 table name. */
	private final Map<String, String> entityType2tableName = new HashMap<>();

	/** The entity type 2 attribute 2 col name. */
	private final Map<String, Map<Attribute<? extends IValue>, String>> entityType2attribute2colName = new HashMap<>();

	/** The entity type 2 attributes. */
	private final Map<String, Set<Attribute<? extends IValue>>> entityType2attributes = new HashMap<>();

	/** The current instance count. */
	private static int currentInstanceCount = 0;

	/** The my sql D bname. */
	private static final String mySqlDBname = "IPopulation_" + (++currentInstanceCount);

	/** The table 2 created index. */
	private final Map<String, Set<String>> table2createdIndex = new HashMap<>();

	/**
	 * Instantiates a new gospl population in database.
	 *
	 * @param connection
	 *            the connection
	 * @param population
	 *            the population
	 */
	public GosplPopulationInDatabase(final Connection connection,
			final IPopulation<ADemoEntity, Attribute<? extends IValue>> population) {
		this.connection = connection;
		loadPopulationIntoDatabase(population);
	}

	/**
	 * Creates an empty population in memory
	 */
	public GosplPopulationInDatabase() {
		try {
			this.connection =
					DriverManager.getConnection("jdbc:hsqldb:mem:" + mySqlDBname + ";shutdown=true", "SA", "");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GenstarException(
					"error while trying to initialize the HDSQL database engine in memory: " + e.getMessage(), e);
		}
	}

	/**
	 * Creates a population stored in memory. Suitable as long as the population is not too big.
	 *
	 * @param population
	 * @param connection
	 */
	public GosplPopulationInDatabase(final IPopulation<ADemoEntity, Attribute<? extends IValue>> population) {
		try {
			this.connection =
					DriverManager.getConnection("jdbc:hsqldb:mem:" + mySqlDBname + ";shutdown=true", "SA", "");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GenstarException(
					"error while trying to initialize the HDSQL database engine in memory: " + e.getMessage(), e);
		}
		loadPopulationIntoDatabase(population);
	}

	/**
	 * Stores the population in a file passed as parameter.
	 *
	 * @param databaseFile
	 * @param population
	 */
	public GosplPopulationInDatabase(final File databaseFile,
			final IPopulation<ADemoEntity, Attribute<? extends IValue>> population) {

		try {
			// ;ifexists=true
			this.connection = DriverManager.getConnection(
					"jdbc:hsqldb:file:" + databaseFile.getPath() + ";create=true;shutdown=true;", "SA", "");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GenstarException("error while trying to initialize the HDSQL database engine in file "
					+ databaseFile + ": " + e.getMessage(), e);
		}
		loadPopulationIntoDatabase(population);
	}

	/**
	 * The connection might be "hsql://localhost/xdb" or in the form "http://localhost/xdb".
	 *
	 * @see http://hsqldb.org/doc/2.0/guide/running-chapt.html#N100CF
	 * @param hsqlUrlServer
	 * @param pop
	 */
	public GosplPopulationInDatabase(final URL hsqlUrlServer,
			final IPopulation<ADemoEntity, Attribute<? extends IValue>> population) {
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
		} catch (Exception e) {
			DEBUG.ERR("ERROR: failed to load HSQLDB JDBC driver.");
			e.printStackTrace();
			throw new GenstarException("error while trying to load the JDBC driver to load the HSQL database", e);
		}

		try {
			this.connection = DriverManager.getConnection("jdbc:hsqldb:" + hsqlUrlServer + ";shutdown=true", "SA", "");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GenstarException("error while trying to initialize the HDSQL database engine in file "
					+ hsqlUrlServer + ": " + e.getMessage(), e);
		}
		loadPopulationIntoDatabase(population);
	}

	/**
	 * Gets the table name for entity type.
	 *
	 * @param type
	 *            the type
	 * @return the table name for entity type
	 */
	protected String getTableNameForEntityType(final String type) {
		entityType2tableName.putIfAbsent(type, "entities_" + type);
		return entityType2tableName.get(type);
	}

	/**
	 * Gets the attribute col name for type.
	 *
	 * @param type
	 *            the type
	 * @param a
	 *            the a
	 * @return the attribute col name for type
	 */
	protected String getAttributeColNameForType(final String type, final Attribute<? extends IValue> a) {
		Map<Attribute<? extends IValue>, String> a2name = entityType2attribute2colName.get(type);
		if (a2name == null) {
			a2name = new HashMap<>();
			entityType2attribute2colName.put(type, a2name);
		}
		String colName = a2name.get(a);
		if (colName == null) {
			colName = a.getAttributeName().replaceAll("(\\W|^_)*", "");
			a2name.put(a, colName);
		}
		return colName;
	}

	/**
	 * Gets the SQL type for attribute.
	 *
	 * @param a
	 *            the a
	 * @return the SQL type for attribute
	 */
	protected String getSQLTypeForAttribute(final Attribute<? extends IValue> a) {

		return switch (a.getValueSpace().getType()) {
			case Integer -> "INTEGER";
			case Continue -> "DOUBLE";
			case Nominal, Order, Range -> "VARCHAR(" + VARCHAR_SIZE + ")";
			case Boolean -> "BOOLEAN";
			default -> throw new GenstarException("this attribute type is not managed: " + a.getValueSpace().getType());
		};

		// can never reach here
		// return "HELP";
	}

	/**
	 * Creates the table for entity type.
	 *
	 * @param type
	 *            the type
	 * @throws SQLException
	 *             the SQL exception
	 */
	protected void createTableForEntityType(final String type) throws SQLException {

		// prepare the SQL query
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ") // LOCAL TEMPORARY
				.append(getTableNameForEntityType(type)).append(" (");

		sb.append("id VARCHAR(50) PRIMARY KEY"); // maybe 30 is not enough?

		for (Attribute<? extends IValue> a : entityType2attributes.get(type)) {
			sb.append(", ");

			// colname
			sb.append(getAttributeColNameForType(type, a));
			sb.append(" ");

			// type
			sb.append(getSQLTypeForAttribute(a));
			sb.append(" ");

		}
		sb.append(")");
		final String qry = sb.toString();

		// execute
		DEBUG.LOG("creating table for type {} with SQL query:  " + type + "," + qry);
		try (Statement s = connection.createStatement()) {
			s.execute(qry);
		}

		// create indexes
		Set<String> setIndex = new HashSet<>();
		table2createdIndex.put(getTableNameForEntityType(type), setIndex);

		try (Statement s2 = connection.createStatement()) {
			for (Attribute<? extends IValue> a : entityType2attributes.get(type)) {

				sb = new StringBuilder();
				sb.append("CREATE INDEX idx_").append(getTableNameForEntityType(type)).append("_")
						.append(getAttributeColNameForType(type, a));
				sb.append(" ON ");
				sb.append(getTableNameForEntityType(type));
				sb.append(" (");
				sb.append(getAttributeColNameForType(type, a));
				sb.append(")");
				s2.execute(sb.toString());

				setIndex.add(getAttributeColNameForType(type, a));
			}
		}
	}

	/**
	 * creates a different table for each entity type and defines the corresponding attributes.
	 *
	 * @throws SQLException
	 */
	protected void createInitialTables() throws SQLException {

		// create one table per type with the corresponding attributes
		for (String type : entityType2attributes.keySet()) { createTableForEntityType(type); }

		// indexes !

	}

	/**
	 * Loads the given collection: keeps in memory the attributes, create tables, and loads entities into them.
	 *
	 * @param pop
	 */
	protected void
			loadPopulationIntoDatabase(final IPopulation<? extends ADemoEntity, Attribute<? extends IValue>> pop) {
		assert this.connection != null;
		assert pop != null;

		// create the attributes
		// we don't know the entity type for this population
		String entityType = DEFAULT_ENTITY_TYPE;
		this.entityType2attributes.put(entityType, new HashSet<>(pop.getPopulationAttributes()));

		// create internal structure
		try {
			createInitialTables();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GenstarException(
					"error creating the tables to store the population in database: " + e.getMessage(), e);
		}

		try {
			storeEntities(entityType, pop);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GenstarException("error while inserting the population in database: " + e.getMessage(), e);

		}
	}

	/**
	 * Returns the SQL value for the attribute of an entity
	 *
	 * @param e
	 * @param a
	 * @return
	 */
	private String getSQLValueFor(final ADemoEntity e, final Attribute<? extends IValue> a) {

		IValue v = e.getValueForAttribute(a);

		return switch (a.getValueSpace().getType()) {
			case Continue, Integer -> v.getStringValue();
			case Nominal, Order, Range -> "'" + v.getStringValue() + "'";
			case Boolean -> ((BooleanValue) v).getActualValue() ? "TRUE" : "FALSE";
			default -> throw new GenstarException("unknown value type " + a.getValueSpace().getType());
		};

	}

	/**
	 * For a given attribute of an entity of a given type, decodes the value from a SQL resultset and returns the
	 * corresponding genstar value.
	 *
	 * @param type
	 * @param a
	 * @param r
	 * @return
	 * @throws SQLException
	 */
	protected IValue readValueForAttribute(final String type, final Attribute<? extends IValue> a, final ResultSet r)
			throws SQLException {
		final String colName = getAttributeColNameForType(type, a);
		switch (a.getValueSpace().getType()) {
			case Integer:
				int valueInd = r.getInt(colName);
				return a.getValueSpace().getValue(Integer.toString(valueInd));
			case Continue:
				double valueDouble = r.getDouble(colName);
				return a.getValueSpace().getValue(Double.toString(valueDouble));
			case Nominal, Range, Order:
				String valueStr = r.getString(colName);
				return a.getValueSpace().getValue(valueStr);
			case Boolean:
				if (r.getBoolean(colName))
					return ((BinarySpace) a.getValueSpace()).valueTrue;
				else
					return ((BinarySpace) a.getValueSpace()).valueFalse;
			default:
				throw new GenstarException("unknown entity type " + a.getValueSpace().getType());
		}
	}

	/**
	 * Store entities.
	 *
	 * @param type
	 *            the type
	 * @param entities
	 *            the entities
	 * @return the int
	 * @throws SQLException
	 *             the SQL exception
	 */
	protected int storeEntities(final String type, final Collection<? extends ADemoEntity> entities)
			throws SQLException {

		if (!entityType2tableName.containsKey(type)) { createTableForEntityType(type); }

		int added = 0;

		// name columns
		List<Attribute<? extends IValue>> attributes = new LinkedList<>(entityType2attributes.get(type));

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ").append(getTableNameForEntityType(type));
		sb.append(" m (id");
		for (Attribute<? extends IValue> a : attributes) {
			sb.append(",");
			sb.append(getAttributeColNameForType(type, a));
		}
		sb.append(") VALUES (");
		final String qryHead = sb.toString();

		// add each entity
		boolean first = true;
		for (ADemoEntity e : entities) {

			if (sb.length() >= MAX_BUFFER_QRY) {
				sb.append(")");
				final String qry = sb.toString();
				// execute the query
				DEBUG.LOG("adding entities with query " + qry);
				try (Statement st = connection.createStatement()) {
					st.executeQuery(qry);
					try (ResultSet rs = st.executeQuery("CALL DIAGNOSTICS ( ROW_COUNT )")) {
						rs.next();
						added += rs.getInt(1);
					}
				}
				// restart from scratch
				first = true;
				sb = new StringBuilder(qryHead);
			}

			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
			sb.append("('");
			sb.append(e.getEntityId());
			sb.append("'");
			for (Attribute<? extends IValue> a : attributes) {
				sb.append(",");
				sb.append(getSQLValueFor(e, a));
			}
			sb.append(")");

		}

		if (sb.length() > qryHead.length()) {
			sb.append(")");
			final String qry = sb.toString();
			// execute the query
			DEBUG.LOG("adding last entities with query " + qry);
			try (Statement st = connection.createStatement()) {
				st.executeQuery(qry);
				try (ResultSet rs = st.executeQuery("CALL DIAGNOSTICS ( ROW_COUNT )")) {
					rs.next();
					added += rs.getInt(1);
				}
			}
		}
		return added;
	}

	@Override
	public boolean add(final ADemoEntity e) {

		String type = e.getEntityType();
		if (type == null) { type = DEFAULT_ENTITY_TYPE; }

		if (!entityType2attributes.containsKey(type)) {
			entityType2attributes.put(type, new HashSet<>(e.getAttributes()));
		}

		if (!entityType2tableName.containsKey(type)) {
			try {
				createTableForEntityType(type);
			} catch (SQLException ex) {
				throw new GenstarException("error while creating table for type " + type);
			}
		}

		// name columns
		List<Attribute<? extends IValue>> attributes = new LinkedList<>(entityType2attributes.get(type));

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ").append(getTableNameForEntityType(type));
		sb.append(" (id");
		for (Attribute<? extends IValue> a : attributes) {
			sb.append(",");
			sb.append(getAttributeColNameForType(type, a));
		}
		sb.append(") VALUES");

		sb.append("('");
		sb.append(e.getEntityId());
		sb.append("'");
		for (Attribute<? extends IValue> a : attributes) {
			sb.append(",");
			sb.append(getSQLValueFor(e, a));
		}
		sb.append(")");

		try (Statement st = connection.createStatement()) {
			st.executeQuery(sb.toString());
			return true;
		} catch (SQLIntegrityConstraintViolationException e1) {
			return false;
		} catch (SQLException e1) {
			e1.printStackTrace();
			throw new GenstarException("error while adding entity " + e, e1);
		}

	}

	@Override
	public boolean addAll(final Collection<? extends ADemoEntity> c) {

		int added = 0;

		Map<String, List<ADemoEntity>> type2entities = new HashMap<>();

		try {

			for (ADemoEntity e : c) {

				String type = e.getEntityType();
				if (type == null) { type = DEFAULT_ENTITY_TYPE; }
				if (!e._hasEntityId()) { e._setEntityId(EntityUniqueId.createNextId(this, type)); }
				DEBUG.OUT("should add entity id: " + e.getEntityId());
				if (!entityType2attributes.containsKey(type)) {
					entityType2attributes.put(type, new HashSet<>(e.getAttributes()));
				}
				if (!entityType2tableName.containsKey(type)) {
					try {
						createTableForEntityType(type);
					} catch (SQLException ex) {
						throw new GenstarException("error while creating table for type " + type);
					}
				}

				List<ADemoEntity> l = type2entities.get(type);
				if (l == null) {
					l = new ArrayList<>(ADD_ENTITIES_BATCH);
					type2entities.put(type, l);
				}
				l.add(e);

				if (l.size() >= ADD_ENTITIES_BATCH) {
					try {
						added += storeEntities(type, l);
					} catch (SQLIntegrityConstraintViolationException e2) {
						// one of the entities already exist;
						// there is no easy and efficient synthax for sqldb to exclude the known ones
						// the best is to just add them one by one
						DEBUG.ERR("some of these agents already existed; switching to add 1 by 1");
						for (ADemoEntity en : l) { if (add(en)) { added++; } }
					}
					l.clear();
				}
			}

			for (String type : type2entities.keySet()) {

				try {
					added += storeEntities(type, type2entities.get(type));
				} catch (SQLIntegrityConstraintViolationException e2) {
					// one of the entities already exist;
					// there is no easy and efficient synthax for sqldb to exclude the known ones
					// the best is to just add them one by one
					DEBUG.ERR("some of these agents already existed; switching to add 1 by 1");
					for (ADemoEntity en : type2entities.get(type)) { if (add(en)) { added++; } }
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GenstarException("error while adding entities", e);
		}
		return added > 0;

	}

	@Override
	public void clear() {
		try (Statement st = connection.createStatement()) {
			for (String tablename : entityType2tableName.values()) { st.executeQuery("TRUNCATE TABLE " + tablename); }
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GenstarException("error while dropping the table containing the entities", e);
		}
	}

	@Override
	public GosplPopulationInDatabase clone() {
		throw new UnsupportedOperationException("Not yet coded");
	}

	@Override
	public boolean contains(final Object o) {
		if (o instanceof ADemoEntity e) {
			String entityType = e.getEntityType();
			if (entityType == null) { entityType = DEFAULT_ENTITY_TYPE; }
			String tableName = entityType2tableName.get(entityType);
			if (tableName == null)
				// we never saw such a type; we cannot contain it
				return false;
			try (Statement st = connection.createStatement()) {

				ResultSet set =
						st.executeQuery("SELECT COUNT(*) FROM " + tableName + " WHERE id='" + e.getEntityId() + "'");
				set.next();
				Integer count = set.getInt(1);
				return count > 0;
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new GenstarException("Unable to search for entity " + o, e1);
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(final Collection<?> c) {

		// implement that, we can do it (but its tedious and no one ever uses it !)
		throw new NotImplementedException("Not yet implemented");
	}

	@Override
	public boolean isEmpty() {

		// easy solution: no entity type means nothing was ever inserted
		if (entityType2tableName.isEmpty()) return true;

		// the hard way
		try (Statement st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

			for (String tableName : entityType2tableName.values()) {
				// ResultSet set = st.executeQuery("SELECT * FROM "+tableName+";");

				ResultSet set = st.executeQuery("SELECT * FROM " + tableName + " LIMIT 1");

				if (!set.next()) return true;

			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			throw new GenstarException("Error while checking if the table is empty", e1);
		}

		return false;
	}

	/**
	 * Iterates the entities of a given type
	 *
	 * @author Samuel Thiriot
	 */
	public class DatabaseEntitiesIterator implements Iterator<ADemoEntity> {

		/** The rs. */
		private ResultSet rs;

		/** The ps. */
		private PreparedStatement ps;

		/** The connection. */
		private final Connection connection;

		/** The sql. */
		private final String sql;

		/** The type. */
		private final String type;

		/** The attributes. */
		private final Set<Attribute<? extends IValue>> attributes;

		/**
		 * Instantiates a new database entities iterator.
		 *
		 * @param connection
		 *            the connection
		 * @param attributes
		 *            the attributes
		 * @param type
		 *            the type
		 * @param sqlWhereClause
		 *            the sql where clause
		 */
		public DatabaseEntitiesIterator(final Connection connection, final Set<Attribute<? extends IValue>> attributes,
				final String type, final String sqlWhereClause) {
			if (connection == null) throw new IllegalArgumentException("Resource is null");
			if (sqlWhereClause == null) throw new IllegalArgumentException("Clause is null");
			if (type == null) throw new IllegalArgumentException("Type is null");
			if (attributes == null) throw new IllegalArgumentException("Attributes are null");
			this.connection = connection;
			this.sql = "SELECT * FROM " + entityType2tableName.get(type) + sqlWhereClause;
			this.attributes = attributes;
			this.type = type;

		}

		/**
		 * Creates an iterator browsing all the entities of this type
		 *
		 * @param connection
		 * @param type
		 */
		public DatabaseEntitiesIterator(final Connection connection, final Set<Attribute<? extends IValue>> attributes,
				final String type) {
			this(connection, attributes, type, "");
		}

		/**
		 * Inits the.
		 */
		public void init() {
			try {
				ps = connection.prepareStatement(sql);
				rs = ps.executeQuery();
				rs.next();
			} catch (SQLException e) {
				close();
				throw new GenstarException(e);
			}
		}

		@Override
		public boolean hasNext() {
			if (ps == null) { init(); }
			try {
				boolean hasMore = !rs.isAfterLast();
				// avoid this ugly workaround ?!
				if (hasMore) {
					try {
						rs.getString("id");
					} catch (SQLException e) {
						hasMore = false;
					}
				}
				if (!hasMore) { close(); }
				return hasMore;
			} catch (SQLException e) {
				close();
				throw new GenstarException(e);
			}

		}

		/**
		 * Close.
		 */
		private void close() {
			try {
				if (rs != null) { rs.close(); }
				if (ps != null) {
					try {
						ps.close();
					} catch (SQLException e) {
						// nothing we can do here
					}
				}
			} catch (SQLException e) {
				// nothing we can do here
			}
		}

		@Override
		public ADemoEntity next() {

			if (ps == null) { init(); }

			try {
				return createEntity(rs, type, attributes);
			} catch (SQLException e) {
				throw new GenstarException(e);
			}
		}
	}

	/**
	 * Creates the entity.
	 *
	 * @param rs
	 *            the rs
	 * @param type
	 *            the type
	 * @param attributes
	 *            the attributes
	 * @return the a demo entity
	 * @throws SQLException
	 *             the SQL exception
	 */
	private ADemoEntity createEntity(final ResultSet rs, final String type,
			final Set<Attribute<? extends IValue>> attributes) throws SQLException {

		Map<Attribute<? extends IValue>, IValue> attribute2value = new HashMap<>();

		// read the attributes of the current element
		String id = rs.getString("id");

		for (Attribute<? extends IValue> a : attributes) { attribute2value.put(a, readValueForAttribute(type, a, rs)); }

		rs.next();

		// create the return result
		GosplEntity res = new GosplEntity(attribute2value);
		res._setEntityId(id);
		res.setEntityType(type);

		return res;
	}

	/**
	 * Iterates the entities of a all types
	 *
	 * @author Samuel Thiriot
	 */
	public class AllTypesIterator implements Iterator<ADemoEntity> {

		/** The connection. */
		protected Connection connection;

		/** The entity type 2 attributes. */
		protected Map<String, Set<Attribute<? extends IValue>>> entityType2attributes;

		/** The it types. */
		protected Iterator<String> itTypes = null;

		/** The it entities. */
		protected DatabaseEntitiesIterator itEntities = null;

		/** The where clause. */
		protected String whereClause = null;

		/**
		 * Instantiates a new all types iterator.
		 *
		 * @param connection
		 *            the connection
		 * @param entityType2tableName
		 *            the entity type 2 table name
		 * @param entityType2attributes
		 *            the entity type 2 attributes
		 */
		public AllTypesIterator(final Connection connection, final Map<String, String> entityType2tableName,
				final Map<String, Set<Attribute<? extends IValue>>> entityType2attributes) {

			this(connection, entityType2tableName, entityType2attributes, "");

		}

		/**
		 *
		 * @param connection
		 * @param entityType2tableName
		 * @param entityType2attributes
		 * @param whereClause
		 */
		public AllTypesIterator(final Connection connection, final Map<String, String> entityType2tableName,
				final Map<String, Set<Attribute<? extends IValue>>> entityType2attributes, final String whereClause) {

			if (connection == null) throw new IllegalArgumentException("Resource is null");
			if (whereClause == null) throw new IllegalArgumentException("Clause is null");
			if (entityType2attributes == null) throw new IllegalArgumentException("Entity types to attributes is null");

			this.connection = connection;
			this.entityType2attributes = entityType2attributes;
			this.whereClause = whereClause;

			itTypes = entityType2tableName.keySet().iterator();
		}

		/**
		 * Inits the entities iterator for type.
		 *
		 * @param currentType
		 *            the current type
		 */
		protected void initEntitiesIteratorForType(final String currentType) {
			itEntities = new DatabaseEntitiesIterator(connection, entityType2attributes.get(currentType), currentType,
					this.whereClause);
		}

		/**
		 * Inits the entities iterator.
		 */
		protected void initEntitiesIterator() {
			initEntitiesIteratorForType(itTypes.next());
		}

		@Override
		public boolean hasNext() {

			if (itEntities == null) { initEntitiesIterator(); }

			if (!itEntities.hasNext()) // DEBUG.OUT("end of the entities iterator");
				return itTypes.hasNext();
			return true;
		}

		@Override
		public ADemoEntity next() {

			/*
			 * if (itEntities == null || !itEntities.hasNext()) { currentType = itTypes.next();
			 *
			 * }
			 */
			// itTypes.hasNext()
			if (itEntities == null || !itEntities.hasNext()) { initEntitiesIterator(); }

			return itEntities.next();
		}
	}

	/**
	 * The Class AllTypesWithWhereIterator.
	 */
	public class AllTypesWithWhereIterator extends AllTypesIterator {

		/** The attribute 2 values. */
		Map<Attribute<? extends IValue>, Collection<IValue>> attribute2values;

		/**
		 * Instantiates a new all types with where iterator.
		 *
		 * @param connection
		 *            the connection
		 * @param entityType2tableName
		 *            the entity type 2 table name
		 * @param entityType2attributes
		 *            the entity type 2 attributes
		 * @param attribute2values
		 *            the attribute 2 values
		 */
		public AllTypesWithWhereIterator(final Connection connection, final Map<String, String> entityType2tableName,
				final Map<String, Set<Attribute<? extends IValue>>> entityType2attributes,
				final Map<Attribute<? extends IValue>, Collection<IValue>> attribute2values) {

			super(connection, entityType2tableName, entityType2attributes);

			this.attribute2values = attribute2values;

		}

		@Override
		protected void initEntitiesIteratorForType(final String currentType) {

			// if there is no condition, let's come back to the original version
			// which browses everything
			if (attribute2values.isEmpty()) {
				super.initEntitiesIteratorForType(currentType);
				return;
			}

			StringBuilder sb = new StringBuilder();

			addWhereClauseForAttributes(sb, currentType, attribute2values);

			itEntities = new DatabaseEntitiesIterator(connection, entityType2attributes.get(currentType), currentType,
					sb.toString());
		}
	}

	@Override
	public Iterator<ADemoEntity> iterator() {

		return new AllTypesIterator(connection, entityType2tableName, entityType2attributes);
	}

	/**
	 * Iterator.
	 *
	 * @param type
	 *            the type
	 * @return the iterator
	 */
	public Iterator<ADemoEntity> iterator(final String type) {
		return new DatabaseEntitiesIterator(connection, entityType2attributes.get(type), type);
	}

	@Override
	public boolean remove(final Object o) {
		try {
			ADemoEntity e = (ADemoEntity) o;

			if (!entityType2tableName.containsKey(e.getEntityType()) || !e._hasEntityId())
				// we never stored an agent without id
				return false;

			try (Statement st = connection.createStatement()) {

				st.executeQuery("DELETE FROM " + getTableNameForEntityType(e.getEntityType()) + " WHERE id='"
						+ e.getEntityId() + "'");

				// check if we deleted anything
				ResultSet rs = st.executeQuery("CALL DIAGNOSTICS ( ROW_COUNT )");
				rs.next();
				return rs.getInt(1) > 0;
			} catch (SQLException ex) {
				ex.printStackTrace();
				throw new GenstarException("SQL error while deleting the entity " + e, ex);
			}

		} catch (ClassCastException e1) {
			// if this is not an entity, we did not removed it
			return false;
		}
	}

	/**
	 * Creates the ids clause.
	 *
	 * @param sb
	 *            the sb
	 * @param ids
	 *            the ids
	 */
	protected void createIdsClause(final StringBuilder sb, final Collection<String> ids) {
		sb.append("IN (");
		boolean first = true;
		for (String id : ids) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
			sb.append("'").append(id).append("'");
		}
		sb.append(")");

	}

	/**
	 * Delete ids.
	 *
	 * @param type
	 *            the type
	 * @param ids
	 *            the ids
	 * @return the int
	 */
	protected int deleteIds(final String type, final Collection<String> ids) {
		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM ").append(getTableNameForEntityType(type)).append(" WHERE id ");
		createIdsClause(sb, ids);

		try (Statement st = connection.createStatement()) {
			st.executeQuery(sb.toString());
			// check if we deleted anything
			ResultSet rs = st.executeQuery("CALL DIAGNOSTICS ( ROW_COUNT )");
			rs.next();
			return rs.getInt(1);
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new GenstarException("SQL error while deleting the entities " + ex, ex);
		}
	}

	@Override
	public boolean removeAll(final Collection<?> c) {

		boolean anyChange = false;

		Map<String, List<String>> type2ids = new HashMap<>();
		for (Object o : c) {
			ADemoEntity e = null;
			try {
				e = (ADemoEntity) o;
			} catch (ClassCastException e1) {
				// skip was is not an entity
				continue;
			}
			if (!e._hasEntityId()) { continue; }

			String type = e.getEntityType();
			if (type == null) { type = DEFAULT_ENTITY_TYPE; }

			if (!entityType2tableName.containsKey(type)) {
				// we never saved it, so we will not remove it
				continue;
			}

			List<String> l = type2ids.get(type);
			if (l == null) {
				l = new ArrayList<>(REMOVE_ENTITIES_BATCH);
				type2ids.put(type, l);
			}
			l.add(e.getEntityId());

			if (l.size() >= REMOVE_ENTITIES_BATCH) {
				anyChange = deleteIds(type, l) > 0 || anyChange;
				l.clear();
			}
		}

		for (String type : type2ids.keySet()) { anyChange = deleteIds(type, type2ids.get(type)) > 0 || anyChange; }
		return anyChange;
	}

	@Override
	public boolean retainAll(final Collection<?> c) {

		throw new NotImplementedException("cannot retain all for all the types");
	}

	@Override
	public int size() {
		try (Statement st = connection.createStatement()) {
			int accumulated = 0;
			DEBUG.OUT("in size");

			for (String tableName : entityType2tableName.values()) {

				ResultSet set = st.executeQuery("SELECT COUNT(*) as TOTAL FROM " + tableName + ";");
				set.next();
				accumulated += set.getInt("TOTAL");
			}
			return accumulated;
		} catch (SQLException e1) {
			e1.printStackTrace();
			throw new GenstarException("Error while counting entities", e1);
		}
	}

	@Override
	public Object[] toArray() {
		throw new NotImplementedException("no array feature for this");
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		throw new NotImplementedException("no array feature for this");
	}

	@Override
	public Set<Attribute<? extends IValue>> getPopulationAttributes() {
		return entityType2attributes.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
	}

	@Override
	public boolean isAllPopulationOfType(final String type) {
		return entityType2tableName.size() == 1 && entityType2tableName.containsKey(type);
	}

	@Override
	public Attribute<? extends IValue> getPopulationAttributeNamed(final String name) {
		// index
		Set<Attribute<? extends IValue>> attributes = getPopulationAttributes();
		if (attributes == null) return null;
		for (Attribute<? extends IValue> a : attributes) { if (a.getAttributeName().equals(name)) return a; }
		return null;
	}

	/**
	 * At finalization time, we shutdown the database
	 */
	@Override
	protected void finalize() throws Throwable {

		if (this.connection != null) { this.connection.close(); }
		super.finalize();
	}

	@Override
	public int getCountHavingValues(final Attribute<? extends IValue> attribute, final IValue... values) {

		int total = 0;
		for (String type : entityType2tableName.keySet()) {

			// we don't even have this attribute stored, so no entity has any of these characteristics
			if (!entityType2attributes.get(type).contains(attribute)) { continue; }

			try {
				total += getEntitiesHavingValues(type, attribute, values);
			} catch (SQLException e) {
				throw new GenstarException("error while counting entities of type " + type, e);
			}
		}
		return total;
	}

	/**
	 * Adds the where clause for attribute.
	 *
	 * @param sb
	 *            the sb
	 * @param type
	 *            the type
	 * @param attribute
	 *            the attribute
	 * @param values
	 *            the values
	 */
	protected void addWhereClauseForAttribute(final StringBuilder sb, final String type,
			final Attribute<? extends IValue> attribute, final IValue... values) {

		boolean first = true;

		switch (attribute.getValueSpace().getType()) {
			case Integer:
			case Continue:
				// to optimize by creating >= <= clauses
				for (IValue v : values) {
					if (first) {
						first = false;
					} else {
						sb.append(" OR ");
					}
					sb.append(getAttributeColNameForType(type, attribute));
					sb.append("=");
					sb.append(v.getActualValue().toString());
				}
				break;
			case Range:
			case Nominal:
			case Order:
				for (IValue v : values) {
					if (first) {
						first = false;
					} else {
						sb.append(" OR ");
					}
					sb.append(getAttributeColNameForType(type, attribute));
					sb.append("='");
					sb.append(v.getActualValue().toString());
					sb.append("'");
				}
				break;
			case Boolean:
				for (IValue v : values) {
					if (first) {
						first = false;
					} else {
						sb.append(" OR ");
					}
					sb.append(getAttributeColNameForType(type, attribute));
					sb.append("=");
					sb.append(v.getStringValue());
				}
				break;
			default:
				throw new GenstarException("unknown attribute type " + attribute.getValueSpace().getType());
		}

	}

	/**
	 * Gets the entities having values.
	 *
	 * @param type
	 *            the type
	 * @param attribute
	 *            the attribute
	 * @param values
	 *            the values
	 * @return the entities having values
	 * @throws SQLException
	 *             the SQL exception
	 */
	public int getEntitiesHavingValues(final String type, final Attribute<? extends IValue> attribute,
			final IValue... values) throws SQLException {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT COUNT(*) AS TOTAL FROM ").append(getTableNameForEntityType(type));

		if (values.length > 0) {
			sb.append(" WHERE ");
			addWhereClauseForAttribute(sb, type, attribute, values);
		}

		// DEBUG.OUT(sb.toString());

		try (Statement st = connection.createStatement()) {
			ResultSet set = st.executeQuery(sb.toString());
			set.next();
			return set.getInt("TOTAL");
		}
	}

	/**
	 * Adds the where clause for attributes.
	 *
	 * @param sb
	 *            the sb
	 * @param type
	 *            the type
	 * @param attribute2values
	 *            the attribute 2 values
	 */
	/*
	 * Private inner method to setup query
	 */
	private void addWhereClauseForAttributes(final StringBuilder sb, final String type,
			final Map<Attribute<? extends IValue>, Collection<IValue>> attribute2values) {
		sb.append(" WHERE (");
		boolean first = true;

		for (Attribute<? extends IValue> attribute : attribute2values.keySet()) {
			if (first) {
				first = false;
			} else {
				sb.append(") AND (");
			}
			Collection<IValue> values = attribute2values.get(attribute);
			addWhereClauseForAttribute(sb, type, attribute, values.toArray(new IValue[values.size()]));
		}
		sb.append(")");
	}

	/**
	 * Adds the where clause for coordinate.
	 *
	 * @param sb
	 *            the sb
	 * @param type
	 *            the type
	 * @param attribute2values
	 *            the attribute 2 values
	 */
	/*
	 * Private inner method to setupe query
	 */
	private void addWhereClauseForCoordinate(final StringBuilder sb, final String type,
			final Map<Attribute<? extends IValue>, IValue> attribute2values) {
		sb.append(" WHERE (");
		boolean first = true;
		for (Attribute<? extends IValue> attribute : attribute2values.keySet()) {
			if (first) {
				first = false;
			} else {
				sb.append(") AND (");
			}
			addWhereClauseForAttribute(sb, type, attribute, attribute2values.get(attribute));
		}
		sb.append(")");
	}

	/**
	 * Gets the entities having values.
	 *
	 * @param type
	 *            the type
	 * @param attribute2values
	 *            the attribute 2 values
	 * @return the entities having values
	 * @throws SQLException
	 *             the SQL exception
	 */
	protected int getEntitiesHavingValues(final String type,
			final Map<Attribute<? extends IValue>, Collection<IValue>> attribute2values) throws SQLException {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT COUNT(*) AS TOTAL FROM ").append(getTableNameForEntityType(type));

		if (!attribute2values.isEmpty()) { addWhereClauseForAttributes(sb, type, attribute2values); }
		// DEBUG.OUT(sb.toString());

		try (Statement st = connection.createStatement()) {
			ResultSet set = st.executeQuery(sb.toString());
			set.next();
			return set.getInt("TOTAL");
		}

	}

	@Override
	public int getCountHavingValues(final Map<Attribute<? extends IValue>, Collection<IValue>> attribute2values) {

		int total = 0;
		for (String type : entityType2tableName.keySet()) {

			try {
				total += getEntitiesHavingValues(type, attribute2values);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new GenstarException("error while counting entities of type " + type, e);
			}

		}
		return total;
	}

	/**
	 * Gets the entities having coordinate.
	 *
	 * @param type
	 *            the type
	 * @param attribute2value
	 *            the attribute 2 value
	 * @return the entities having coordinate
	 * @throws SQLException
	 *             the SQL exception
	 */
	protected int getEntitiesHavingCoordinate(final String type,
			final Map<Attribute<? extends IValue>, IValue> attribute2value) throws SQLException {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT COUNT(*) AS TOTAL FROM ").append(getTableNameForEntityType(type));

		if (!attribute2value.isEmpty()) { addWhereClauseForCoordinate(sb, type, attribute2value); }
		// DEBUG.OUT(sb.toString());

		try (Statement st = connection.createStatement()) {
			ResultSet set = st.executeQuery(sb.toString());
			set.next();
			return set.getInt("TOTAL");
		}

	}

	@Override
	public int getCountHavingCoordinate(final Map<Attribute<? extends IValue>, IValue> attribute2value) {

		int total = 0;
		for (String type : entityType2tableName.keySet()) {

			try {
				total += getEntitiesHavingCoordinate(type, attribute2value);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new GenstarException("error while counting entities of type " + type, e);
			}

		}
		return total;
	}

	@Override
	public Iterator<ADemoEntity> getEntitiesHavingValues(final Attribute<? extends IValue> attribute,
			final IValue... values) {

		Map<Attribute<? extends IValue>, Collection<IValue>> a2vv = new HashMap<>();
		a2vv.put(attribute, Arrays.asList(values));

		return new AllTypesWithWhereIterator(connection, entityType2tableName, entityType2attributes, a2vv);
	}

	@Override
	public Iterator<ADemoEntity>
			getEntitiesHavingValues(final Map<Attribute<? extends IValue>, Collection<IValue>> attribute2values) {
		return new AllTypesWithWhereIterator(connection, entityType2tableName, entityType2attributes, attribute2values);
	}

	@Override
	public ADemoEntity getEntityForId(final String id) {

		try (Statement st = connection.createStatement()) {

			for (String type : entityType2tableName.keySet()) {
				try {
					ResultSet rs = st.executeQuery(
							"SELECT * FROM " + getTableNameForEntityType(type) + " WHERE id='" + id + "'");
					rs.next(); // check
					return createEntity(rs, type, entityType2attributes.get(type));
				} catch (SQLException e) {
					throw e;// maybe its not in this population
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			throw new GenstarException(e1);
		}
		return null;

	}

	@Override
	public Iterator<ADemoEntity> getEntitiesForIds(final String... ids) {
		// !!!
		throw new NotImplementedException("sorry.");
	}

}
