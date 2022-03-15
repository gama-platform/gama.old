package core.metamodel.io;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.xml.crypto.dsig.TransformException;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import core.metamodel.attribute.Attribute;
import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;

public interface IGSGeofile<E extends AGeoEntity<V>, V extends IValue> {

	public enum GeoGSFileType {RASTER,VECTOR}
	
	/**
	 * Gives the geographic type of data within the file: either to be
	 * raster or vector
	 * 
	 * @see GeoGSFileType
	 * @return GeoGSFileType#RASTER or GeoGSFileType#VECTOR
	 */
	public GeoGSFileType getGeoGSFileType();
	
	/**
	 * Retrieve main spatial component of the file: the type of data implement {@link IGeoGSAttribute}.
	 * This method could leads to store huge amount of data into collection and then not be quite efficient
	 * 
	 * @return
	 * @throws TransformException 
	 */
	public Collection<E> getGeoEntity() throws IOException;
	
	/**
	 * Retrieve all possible variable within spatial component.
	 * This method could leads to store huge amount of data into collection and then not be quite efficient
	 * 
	 * @return 
	 */
	public Collection<V> getGeoValues() ;
	
	/**
	 * Retrieve all possible attribute that geo entity can embody
	 * 
	 * @return
	 */
	public Collection<Attribute<? extends V>> getGeoAttributes();


	/**
	 * Says if geographical information of the two files are congruent in term of space.
	 * That implies that, if true, the two files share at least the same projection, coordinate system
	 * and some point in space (coordinate that are present in the two files) 
	 * 
	 * @param file
	 * @return
	 * @throws FactoryException 
	 */
	public boolean isCoordinateCompliant(IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> file);
	
	/**
	 * Access to file coordinate referent system through a WKT representation.
	 * 
	 * @return
	 */
	public String getWKTCoordinateReferentSystem();
	
	/**
	 * Access to file content without memory stored collection
	 * 
	 * @return
	 */
	public Iterator<E> getGeoEntityIterator() ;
	
	/**
	 * Access to file data but limited to geo data within the given Geometry.
	 * 
	 * @param feature
	 * @return Iterator 
	 */
	public Iterator<E> getGeoEntityIteratorWithin(Geometry geom);
	
	/**
	 * Access to file data but limited to geo data within the given Geometry.
	 * 
	 * @param geom
	 * @return Collection 
	 */
	public Collection<E> getGeoEntityWithin(Geometry geom);
	
	/**
	 * Access to file data but limited to geo data intersected with the given Geometry
	 * 
	 * @param feature
	 * @return Iterator 
	 */
	public Iterator<E> getGeoEntityIteratorIntersect(Geometry geom);

	/**
	 * Access to file data but limited to geo data intersected with the given Geometry
	 * 
	 * @param geom
	 * @return Collection 
	 */
	public Collection<E> getGeoEntityIntersect(Geometry geom);
	
	/**
	 * Access to file envelope as define in JTS 
	 * 
	 * @return
	 * @throws IOException 
	 */
	public Envelope getEnvelope() throws IOException;
	
	/**
	 * Use this GIS file as a template to fill with transfer mapping in argument
	 * 
	 * WARNING: the transfer map keys {@link AGeoEntity} must be part of this GIS file 
	 * 
	 * TODO: extends transfer to be a map of <entity, Map<attribute, value>> to transfer multiple attribute value pairs 
	 * 
	 * @param transfer
	 * @return
	 * @throws org.opengis.referencing.operation.TransformException 
	 * @throws IOException 
	 * @throws IllegalArgumentException 
	 */
	public IGSGeofile<E, V> transferTo(File destinationTmp, Map<? extends AGeoEntity<? extends IValue>,Number> transfer, 
			Attribute<? extends IValue> attribute) 
					throws IllegalArgumentException, IOException;
	
}
