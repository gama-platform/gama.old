
package msi.gama.ext.kml;

import java.util.List;
import msi.gama.ext.kml.atom.Author;
import msi.gama.ext.kml.gx.AnimatedUpdate;
import msi.gama.ext.kml.gx.FlyTo;
import msi.gama.ext.kml.gx.LatLonQuad;
import msi.gama.ext.kml.gx.MultiTrack;
import msi.gama.ext.kml.gx.Option;
import msi.gama.ext.kml.gx.Playlist;
import msi.gama.ext.kml.gx.SimpleArrayData;
import msi.gama.ext.kml.gx.SimpleArrayField;
import msi.gama.ext.kml.gx.SoundCue;
import msi.gama.ext.kml.gx.Tour;
import msi.gama.ext.kml.gx.TourControl;
import msi.gama.ext.kml.gx.Track;
import msi.gama.ext.kml.gx.ViewerOptions;
import msi.gama.ext.kml.gx.Wait;
import msi.gama.ext.kml.xal.AddressDetails;
import msi.gama.ext.kml.xal.AddressLine;
import msi.gama.ext.kml.xal.AddressLines;
import msi.gama.ext.kml.xal.AdministrativeArea;
import msi.gama.ext.kml.xal.BuildingName;
import msi.gama.ext.kml.xal.CountryName;
import msi.gama.ext.kml.xal.Department;
import msi.gama.ext.kml.xal.DependentLocality;
import msi.gama.ext.kml.xal.Firm;
import msi.gama.ext.kml.xal.LargeMailUser;
import msi.gama.ext.kml.xal.Locality;
import msi.gama.ext.kml.xal.MailStop;
import msi.gama.ext.kml.xal.PostBox;
import msi.gama.ext.kml.xal.PostOffice;
import msi.gama.ext.kml.xal.PostalCode;
import msi.gama.ext.kml.xal.PostalRoute;
import msi.gama.ext.kml.xal.Premise;
import msi.gama.ext.kml.xal.PremiseNumber;
import msi.gama.ext.kml.xal.PremiseNumberPrefix;
import msi.gama.ext.kml.xal.PremiseNumberSuffix;
import msi.gama.ext.kml.xal.SubPremise;
import msi.gama.ext.kml.xal.Thoroughfare;
import msi.gama.ext.kml.xal.ThoroughfareLeadingType;
import msi.gama.ext.kml.xal.ThoroughfareName;
import msi.gama.ext.kml.xal.ThoroughfareNumber;
import msi.gama.ext.kml.xal.ThoroughfareNumberPrefix;
import msi.gama.ext.kml.xal.ThoroughfareNumberSuffix;
import msi.gama.ext.kml.xal.ThoroughfarePostDirection;
import msi.gama.ext.kml.xal.ThoroughfarePreDirection;
import msi.gama.ext.kml.xal.ThoroughfareTrailingType;
import msi.gama.ext.kml.xal.XAL;


/**
 * Factory functions to create all KML complex elements.
 * 
 */
public final class KmlFactory {


    /**
     * Create an instance of {@link Alias}
     * 
     */
    public static Alias createAlias() {
        return new Alias();
    }

    /**
     * Create an instance of {@link BalloonStyle}
     * 
     */
    public static BalloonStyle createBalloonStyle() {
        return new BalloonStyle();
    }

    /**
     * Create an instance of {@link BasicLink}
     * 
     */
    public static BasicLink createBasicLink() {
        return new BasicLink();
    }

    /**
     * Create an instance of {@link Boundary}
     * 
     */
    public static Boundary createBoundary() {
        return new Boundary();
    }

    /**
     * Create an instance of {@link Camera}
     * 
     */
    public static Camera createCamera() {
        return new Camera();
    }

    /**
     * Create an instance of {@link Change}
     * 
     */
    public static Change createChange() {
        return new Change();
    }

    /**
     * Create an instance of {@link Coordinate}
     * 
     * @param longitude
     *     required parameter
     * @param latitude
     *     required parameter
     */
    public static Coordinate createCoordinate(final double longitude, final double latitude) {
        return new Coordinate(longitude, latitude);
    }

    /**
     * Create an instance of {@link Coordinate}
     * 
     * @param longitude
     *     required parameter
     * @param latitude
     *     required parameter
     * @param altitude
     *     required parameter
     */
    public static Coordinate createCoordinate(final double longitude, final double latitude, final double altitude) {
        return new Coordinate(longitude, latitude, altitude);
    }

    /**
     * Create an instance of {@link Coordinate}
     * 
     * @param coordinates
     *     required parameter
     */
    public static Coordinate createCoordinate(final String coordinates) {
        return new Coordinate(coordinates);
    }

    /**
     * Create an instance of {@link Create}
     * 
     */
    public static Create createCreate() {
        return new Create();
    }

    /**
     * Create an instance of {@link Data}
     * 
     * @param value
     *     required parameter
     */
    public static Data createData(final String value) {
        return new Data(value);
    }

    /**
     * Create an instance of {@link Delete}
     * 
     */
    public static Delete createDelete() {
        return new Delete();
    }

    /**
     * Create an instance of {@link Document}
     * 
     */
    public static Document createDocument() {
        return new Document();
    }

    /**
     * Create an instance of {@link ExtendedData}
     * 
     */
    public static ExtendedData createExtendedData() {
        return new ExtendedData();
    }

    /**
     * Create an instance of {@link Folder}
     * 
     */
    public static Folder createFolder() {
        return new Folder();
    }

    /**
     * Create an instance of {@link GroundOverlay}
     * 
     */
    public static GroundOverlay createGroundOverlay() {
        return new GroundOverlay();
    }

    /**
     * Create an instance of {@link Icon}
     * 
     */
    public static Icon createIcon() {
        return new Icon();
    }

    /**
     * Create an instance of {@link IconStyle}
     * 
     */
    public static IconStyle createIconStyle() {
        return new IconStyle();
    }

    /**
     * Create an instance of {@link ImagePyramid}
     * 
     */
    public static ImagePyramid createImagePyramid() {
        return new ImagePyramid();
    }

    /**
     * Create an instance of {@link ItemIcon}
     * 
     */
    public static ItemIcon createItemIcon() {
        return new ItemIcon();
    }

    /**
     * Create an instance of {@link Kml}
     * 
     */
    public static Kml createKml() {
        return new Kml();
    }

    /**
     * Create an instance of {@link LabelStyle}
     * 
     */
    public static LabelStyle createLabelStyle() {
        return new LabelStyle();
    }

    /**
     * Create an instance of {@link LatLonAltBox}
     * 
     */
    public static LatLonAltBox createLatLonAltBox() {
        return new LatLonAltBox();
    }

    /**
     * Create an instance of {@link LatLonBox}
     * 
     */
    public static LatLonBox createLatLonBox() {
        return new LatLonBox();
    }

    /**
     * Create an instance of {@link LineString}
     * 
     */
    public static LineString createLineString() {
        return new LineString();
    }

    /**
     * Create an instance of {@link LineStyle}
     * 
     */
    public static LineStyle createLineStyle() {
        return new LineStyle();
    }

    /**
     * Create an instance of {@link LinearRing}
     * 
     */
    public static LinearRing createLinearRing() {
        return new LinearRing();
    }

    /**
     * Create an instance of {@link msi.gama.ext.kml.Link}
     * 
     */
    public static msi.gama.ext.kml.Link createLink() {
        return new msi.gama.ext.kml.Link();
    }

    /**
     * Create an instance of {@link ListStyle}
     * 
     */
    public static ListStyle createListStyle() {
        return new ListStyle();
    }

    /**
     * Create an instance of {@link Location}
     * 
     */
    public static Location createLocation() {
        return new Location();
    }

    /**
     * Create an instance of {@link Lod}
     * 
     */
    public static Lod createLod() {
        return new Lod();
    }

    /**
     * Create an instance of {@link LookAt}
     * 
     */
    public static LookAt createLookAt() {
        return new LookAt();
    }

    /**
     * Create an instance of {@link Metadata}
     * 
     */
    public static Metadata createMetadata() {
        return new Metadata();
    }

    /**
     * Create an instance of {@link Model}
     * 
     */
    public static Model createModel() {
        return new Model();
    }

    /**
     * Create an instance of {@link MultiGeometry}
     * 
     */
    public static MultiGeometry createMultiGeometry() {
        return new MultiGeometry();
    }

    /**
     * Create an instance of {@link NetworkLink}
     * 
     */
    public static NetworkLink createNetworkLink() {
        return new NetworkLink();
    }

    /**
     * Create an instance of {@link NetworkLinkControl}
     * 
     */
    public static NetworkLinkControl createNetworkLinkControl() {
        return new NetworkLinkControl();
    }

    /**
     * Create an instance of {@link Orientation}
     * 
     */
    public static Orientation createOrientation() {
        return new Orientation();
    }

    /**
     * Create an instance of {@link Pair}
     * 
     */
    public static Pair createPair() {
        return new Pair();
    }

    /**
     * Create an instance of {@link PhotoOverlay}
     * 
     */
    public static PhotoOverlay createPhotoOverlay() {
        return new PhotoOverlay();
    }

    /**
     * Create an instance of {@link Placemark}
     * 
     */
    public static Placemark createPlacemark() {
        return new Placemark();
    }

    /**
     * Create an instance of {@link Point}
     * 
     */
    public static Point createPoint() {
        return new Point();
    }

    /**
     * Create an instance of {@link PolyStyle}
     * 
     */
    public static PolyStyle createPolyStyle() {
        return new PolyStyle();
    }

    /**
     * Create an instance of {@link Polygon}
     * 
     */
    public static Polygon createPolygon() {
        return new Polygon();
    }

    /**
     * Create an instance of {@link Region}
     * 
     */
    public static Region createRegion() {
        return new Region();
    }

    /**
     * Create an instance of {@link ResourceMap}
     * 
     */
    public static ResourceMap createResourceMap() {
        return new ResourceMap();
    }

    /**
     * Create an instance of {@link Scale}
     * 
     */
    public static Scale createScale() {
        return new Scale();
    }

    /**
     * Create an instance of {@link Schema}
     * 
     */
    public static Schema createSchema() {
        return new Schema();
    }

    /**
     * Create an instance of {@link SchemaData}
     * 
     */
    public static SchemaData createSchemaData() {
        return new SchemaData();
    }

    /**
     * Create an instance of {@link ScreenOverlay}
     * 
     */
    public static ScreenOverlay createScreenOverlay() {
        return new ScreenOverlay();
    }

    /**
     * Create an instance of {@link SimpleData}
     * 
     * @param name
     *     required parameter
     */
    public static SimpleData createSimpleData(final String name) {
        return new SimpleData(name);
    }

    /**
     * Create an instance of {@link SimpleField}
     * 
     */
    public static SimpleField createSimpleField() {
        return new SimpleField();
    }

    /**
     * Create an instance of {@link Snippet}
     * 
     */
    public static Snippet createSnippet() {
        return new Snippet();
    }

    /**
     * Create an instance of {@link Style}
     * 
     */
    public static Style createStyle() {
        return new Style();
    }

    /**
     * Create an instance of {@link StyleMap}
     * 
     */
    public static StyleMap createStyleMap() {
        return new StyleMap();
    }

    /**
     * Create an instance of {@link TimeSpan}
     * 
     */
    public static TimeSpan createTimeSpan() {
        return new TimeSpan();
    }

    /**
     * Create an instance of {@link TimeStamp}
     * 
     */
    public static TimeStamp createTimeStamp() {
        return new TimeStamp();
    }

    /**
     * Create an instance of {@link Update}
     * 
     * @param createOrDeleteOrChange
     *     required parameter
     * @param targetHref
     *     required parameter
     */
    public static Update createUpdate(final String targetHref, final List<Object> createOrDeleteOrChange) {
        return new Update(targetHref, createOrDeleteOrChange);
    }

    /**
     * Create an instance of {@link Vec2}
     * 
     */
    public static Vec2 createVec2() {
        return new Vec2();
    }

    /**
     * Create an instance of {@link ViewVolume}
     * 
     */
    public static ViewVolume createViewVolume() {
        return new ViewVolume();
    }

    /**
     * Create an instance of {@link Author}
     * 
     */
    public static Author createAtomAuthor() {
        return new Author();
    }

    /**
     * Create an instance of {@link msi.gama.ext.kml.atom.Link}
     * 
     * @param href
     *     required parameter
     */
    public static msi.gama.ext.kml.atom.Link createAtomLink(final String href) {
        return new msi.gama.ext.kml.atom.Link(href);
    }

    /**
     * Create an instance of {@link AnimatedUpdate}
     * 
     */
    public static AnimatedUpdate createGxAnimatedUpdate() {
        return new AnimatedUpdate();
    }

    /**
     * Create an instance of {@link FlyTo}
     * 
     */
    public static FlyTo createGxFlyTo() {
        return new FlyTo();
    }

    /**
     * Create an instance of {@link LatLonQuad}
     * 
     */
    public static LatLonQuad createGxLatLonQuad() {
        return new LatLonQuad();
    }

    /**
     * Create an instance of {@link MultiTrack}
     * 
     */
    public static MultiTrack createGxMultiTrack() {
        return new MultiTrack();
    }

    /**
     * Create an instance of {@link Option}
     * 
     */
    public static Option createGxOption() {
        return new Option();
    }

    /**
     * Create an instance of {@link Playlist}
     * 
     */
    public static Playlist createGxPlaylist() {
        return new Playlist();
    }

    /**
     * Create an instance of {@link SimpleArrayData}
     * 
     */
    public static SimpleArrayData createGxSimpleArrayData() {
        return new SimpleArrayData();
    }

    /**
     * Create an instance of {@link SimpleArrayField}
     * 
     */
    public static SimpleArrayField createGxSimpleArrayField() {
        return new SimpleArrayField();
    }

    /**
     * Create an instance of {@link SoundCue}
     * 
     */
    public static SoundCue createGxSoundCue() {
        return new SoundCue();
    }

    /**
     * Create an instance of {@link Tour}
     * 
     */
    public static Tour createGxTour() {
        return new Tour();
    }

    /**
     * Create an instance of {@link TourControl}
     * 
     */
    public static TourControl createGxTourControl() {
        return new TourControl();
    }

    /**
     * Create an instance of {@link Track}
     * 
     */
    public static Track createGxTrack() {
        return new Track();
    }

    /**
     * Create an instance of {@link ViewerOptions}
     * 
     * @param option
     *     required parameter
     */
    public static ViewerOptions createGxViewerOptions(final List<Option> option) {
        return new ViewerOptions(option);
    }

    /**
     * Create an instance of {@link Wait}
     * 
     */
    public static Wait createGxWait() {
        return new Wait();
    }

    /**
     * Create an instance of {@link AddressDetails}
     * 
     * @param xalAddress
     *     required parameter
     * @param addressLines
     *     required parameter
     * @param country
     *     required parameter
     * @param locality
     *     required parameter
     * @param administrativeArea
     *     required parameter
     * @param thoroughfare
     *     required parameter
     */
    public static AddressDetails createXalAddressDetails(final AddressDetails.Address xalAddress, final AddressLines addressLines, final AddressDetails.Country country, final AdministrativeArea administrativeArea, final Locality locality, final Thoroughfare thoroughfare) {
        return new AddressDetails(xalAddress, addressLines, country, administrativeArea, locality, thoroughfare);
    }

    /**
     * Create an instance of {@link AddressLine}
     * 
     */
    public static AddressLine createXalAddressLine() {
        return new AddressLine();
    }

    /**
     * Create an instance of {@link AddressLines}
     * 
     * @param addressLine
     *     required parameter
     */
    public static AddressLines createXalAddressLines(final List<AddressLine> addressLine) {
        return new AddressLines(addressLine);
    }

    /**
     * Create an instance of {@link AdministrativeArea}
     * 
     * @param postalCode
     *     required parameter
     * @param locality
     *     required parameter
     * @param postOffice
     *     required parameter
     */
    public static AdministrativeArea createXalAdministrativeArea(final Locality locality, final PostOffice postOffice, final PostalCode postalCode) {
        return new AdministrativeArea(locality, postOffice, postalCode);
    }

    /**
     * Create an instance of {@link BuildingName}
     * 
     */
    public static BuildingName createXalBuildingName() {
        return new BuildingName();
    }

    /**
     * Create an instance of {@link CountryName}
     * 
     */
    public static CountryName createXalCountryName() {
        return new CountryName();
    }

    /**
     * Create an instance of {@link Department}
     * 
     */
    public static Department createXalDepartment() {
        return new Department();
    }

    /**
     * Create an instance of {@link DependentLocality}
     * 
     * @param postBox
     *     required parameter
     * @param postOffice
     *     required parameter
     * @param postalRoute
     *     required parameter
     * @param largeMailUser
     *     required parameter
     */
    public static DependentLocality createXalDependentLocality(final PostBox postBox, final LargeMailUser largeMailUser, final PostOffice postOffice, final PostalRoute postalRoute) {
        return new DependentLocality(postBox, largeMailUser, postOffice, postalRoute);
    }

    /**
     * Create an instance of {@link Firm}
     * 
     */
    public static Firm createXalFirm() {
        return new Firm();
    }

    /**
     * Create an instance of {@link LargeMailUser}
     * 
     */
    public static LargeMailUser createXalLargeMailUser() {
        return new LargeMailUser();
    }

    /**
     * Create an instance of {@link Locality}
     * 
     * @param postBox
     *     required parameter
     * @param postOffice
     *     required parameter
     * @param postalRoute
     *     required parameter
     * @param largeMailUser
     *     required parameter
     */
    public static Locality createXalLocality(final PostBox postBox, final LargeMailUser largeMailUser, final PostOffice postOffice, final PostalRoute postalRoute) {
        return new Locality(postBox, largeMailUser, postOffice, postalRoute);
    }

    /**
     * Create an instance of {@link MailStop}
     * 
     */
    public static MailStop createXalMailStop() {
        return new MailStop();
    }

    /**
     * Create an instance of {@link PostBox}
     * 
     * @param postBoxNumber
     *     required parameter
     */
    public static PostBox createXalPostBox(final PostBox.PostBoxNumber postBoxNumber) {
        return new PostBox(postBoxNumber);
    }

    /**
     * Create an instance of {@link PostOffice}
     * 
     */
    public static PostOffice createXalPostOffice() {
        return new PostOffice();
    }

    /**
     * Create an instance of {@link PostalCode}
     * 
     */
    public static PostalCode createXalPostalCode() {
        return new PostalCode();
    }

    /**
     * Create an instance of {@link PostalRoute}
     * 
     * @param postalRouteName
     *     required parameter
     * @param postalRouteNumber
     *     required parameter
     */
    public static PostalRoute createXalPostalRoute(final List<PostalRoute.PostalRouteName> postalRouteName, final PostalRoute.PostalRouteNumber postalRouteNumber) {
        return new PostalRoute(postalRouteName, postalRouteNumber);
    }

    /**
     * Create an instance of {@link Premise}
     * 
     * @param premiseNumber
     *     required parameter
     * @param premiseNumberRange
     *     required parameter
     * @param premiseLocation
     *     required parameter
     */
    public static Premise createXalPremise(final Premise.PremiseLocation premiseLocation, final List<PremiseNumber> premiseNumber, final Premise.PremiseNumberRange premiseNumberRange) {
        return new Premise(premiseLocation, premiseNumber, premiseNumberRange);
    }

    /**
     * Create an instance of {@link PremiseNumber}
     * 
     */
    public static PremiseNumber createXalPremiseNumber() {
        return new PremiseNumber();
    }

    /**
     * Create an instance of {@link PremiseNumberPrefix}
     * 
     */
    public static PremiseNumberPrefix createXalPremiseNumberPrefix() {
        return new PremiseNumberPrefix();
    }

    /**
     * Create an instance of {@link PremiseNumberSuffix}
     * 
     */
    public static PremiseNumberSuffix createXalPremiseNumberSuffix() {
        return new PremiseNumberSuffix();
    }

    /**
     * Create an instance of {@link SubPremise}
     * 
     * @param subPremiseLocation
     *     required parameter
     */
    public static SubPremise createXalSubPremise(final SubPremise.SubPremiseLocation subPremiseLocation) {
        return new SubPremise(subPremiseLocation);
    }

    /**
     * Create an instance of {@link Thoroughfare}
     * 
     * @param postalCode
     *     required parameter
     * @param premise
     *     required parameter
     * @param firm
     *     required parameter
     * @param dependentLocality
     *     required parameter
     */
    public static Thoroughfare createXalThoroughfare(final DependentLocality dependentLocality, final Premise premise, final Firm firm, final PostalCode postalCode) {
        return new Thoroughfare(dependentLocality, premise, firm, postalCode);
    }

    /**
     * Create an instance of {@link ThoroughfareLeadingType}
     * 
     */
    public static ThoroughfareLeadingType createXalThoroughfareLeadingType() {
        return new ThoroughfareLeadingType();
    }

    /**
     * Create an instance of {@link ThoroughfareName}
     * 
     */
    public static ThoroughfareName createXalThoroughfareName() {
        return new ThoroughfareName();
    }

    /**
     * Create an instance of {@link ThoroughfareNumber}
     * 
     */
    public static ThoroughfareNumber createXalThoroughfareNumber() {
        return new ThoroughfareNumber();
    }

    /**
     * Create an instance of {@link ThoroughfareNumberPrefix}
     * 
     */
    public static ThoroughfareNumberPrefix createXalThoroughfareNumberPrefix() {
        return new ThoroughfareNumberPrefix();
    }

    /**
     * Create an instance of {@link ThoroughfareNumberSuffix}
     * 
     */
    public static ThoroughfareNumberSuffix createXalThoroughfareNumberSuffix() {
        return new ThoroughfareNumberSuffix();
    }

    /**
     * Create an instance of {@link ThoroughfarePostDirection}
     * 
     */
    public static ThoroughfarePostDirection createXalThoroughfarePostDirection() {
        return new ThoroughfarePostDirection();
    }

    /**
     * Create an instance of {@link ThoroughfarePreDirection}
     * 
     */
    public static ThoroughfarePreDirection createXalThoroughfarePreDirection() {
        return new ThoroughfarePreDirection();
    }

    /**
     * Create an instance of {@link ThoroughfareTrailingType}
     * 
     */
    public static ThoroughfareTrailingType createXalThoroughfareTrailingType() {
        return new ThoroughfareTrailingType();
    }

    /**
     * Create an instance of {@link XAL}
     * 
     * @param xalAddressDetails
     *     required parameter
     */
    public static XAL createXalXAL(final List<AddressDetails> xalAddressDetails) {
        return new XAL(xalAddressDetails);
    }

}
