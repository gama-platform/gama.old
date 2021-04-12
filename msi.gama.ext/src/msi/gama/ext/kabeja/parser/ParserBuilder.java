/*
   Copyright 2005 Simon Mieth

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package msi.gama.ext.kabeja.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import msi.gama.ext.kabeja.parser.entities.DXF3DFaceHandler;
import msi.gama.ext.kabeja.parser.entities.DXF3DSolidHandler;
import msi.gama.ext.kabeja.parser.entities.DXFArcHandler;
import msi.gama.ext.kabeja.parser.entities.DXFBodyHandler;
import msi.gama.ext.kabeja.parser.entities.DXFCircleHandler;
import msi.gama.ext.kabeja.parser.entities.DXFDimensionHandler;
import msi.gama.ext.kabeja.parser.entities.DXFEllipseHandler;
import msi.gama.ext.kabeja.parser.entities.DXFHatchHandler;
import msi.gama.ext.kabeja.parser.entities.DXFImageHandler;
import msi.gama.ext.kabeja.parser.entities.DXFInsertHandler;
import msi.gama.ext.kabeja.parser.entities.DXFLWPolylineHandler;
import msi.gama.ext.kabeja.parser.entities.DXFLeaderHandler;
import msi.gama.ext.kabeja.parser.entities.DXFLineHandler;
import msi.gama.ext.kabeja.parser.entities.DXFMLineHandler;
import msi.gama.ext.kabeja.parser.entities.DXFMTextHandler;
import msi.gama.ext.kabeja.parser.entities.DXFPolylineHandler;
import msi.gama.ext.kabeja.parser.entities.DXFRayHandler;
import msi.gama.ext.kabeja.parser.entities.DXFRegionHandler;
import msi.gama.ext.kabeja.parser.entities.DXFSolidHandler;
import msi.gama.ext.kabeja.parser.entities.DXFSplineHandler;
import msi.gama.ext.kabeja.parser.entities.DXFTextHandler;
import msi.gama.ext.kabeja.parser.entities.DXFToleranceHandler;
import msi.gama.ext.kabeja.parser.entities.DXFTraceHandler;
import msi.gama.ext.kabeja.parser.entities.DXFViewportHandler;
import msi.gama.ext.kabeja.parser.entities.DXFXLineHandler;
import msi.gama.ext.kabeja.parser.objects.DXFDictionaryHandler;
import msi.gama.ext.kabeja.parser.objects.DXFImageDefHandler;
import msi.gama.ext.kabeja.parser.objects.DXFLayoutHandler;
import msi.gama.ext.kabeja.parser.objects.DXFMLineStyleHandler;
import msi.gama.ext.kabeja.parser.objects.DXFPlotsettingsHandler;
import msi.gama.ext.kabeja.parser.table.DXFDimensionStyleTableHandler;
import msi.gama.ext.kabeja.parser.table.DXFLayerTableHandler;
import msi.gama.ext.kabeja.parser.table.DXFLineTypeTableHandler;
import msi.gama.ext.kabeja.parser.table.DXFStyleTableHandler;
import msi.gama.ext.kabeja.parser.table.DXFVPortTableHandler;
import msi.gama.ext.kabeja.parser.table.DXFViewTableHandler;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 *
 *
 */
public class ParserBuilder {
    public static Parser createDefaultParser() {
        DXFParser parser = new DXFParser();

        Handler handler;

        handler = new DXFHeaderSectionHandler();

        parser.addHandler(handler);

        // the blocks handler
        HandlerManager handlerManager = new DXFBlocksSectionHandler();
        parser.addHandler(handlerManager);

        Handler h = new DXFLineHandler();
        handlerManager.addHandler(h);

        h = new DXFCircleHandler();
        handlerManager.addHandler(h);

        h = new DXFArcHandler();
        handlerManager.addHandler(h);

        h = new DXFPolylineHandler();
        handlerManager.addHandler(h);

        h = new DXFLWPolylineHandler();
        handlerManager.addHandler(h);

        h = new DXFMTextHandler();
        handlerManager.addHandler(h);

        h = new DXFTextHandler();
        handlerManager.addHandler(h);

        h = new DXFInsertHandler();
        handlerManager.addHandler(h);

        h = new DXFEllipseHandler();
        handlerManager.addHandler(h);

        h = new DXFSolidHandler();
        handlerManager.addHandler(h);

        h = new DXFTraceHandler();
        handlerManager.addHandler(h);

        h = new DXFDimensionHandler();
        handlerManager.addHandler(h);

        h = new DXFHatchHandler();
        handlerManager.addHandler(h);

        h = new DXFImageHandler();
        handlerManager.addHandler(h);

        h = new DXF3DFaceHandler();
        handlerManager.addHandler(h);

        h = new DXFRayHandler();
        handlerManager.addHandler(h);

        h = new DXFXLineHandler();
        handlerManager.addHandler(h);

        h = new DXFRegionHandler();
        handlerManager.addHandler(h);

        h = new DXFBodyHandler();
        handlerManager.addHandler(h);

        h = new DXF3DSolidHandler();
        handlerManager.addHandler(h);

        h = new DXFSplineHandler();
        handlerManager.addHandler(h);

        h = new DXFMLineHandler();
        handlerManager.addHandler(h);

        h = new DXFLeaderHandler();
        handlerManager.addHandler(h);

        h = new DXFToleranceHandler();
        handlerManager.addHandler(h);

        h = new DXFViewportHandler();
        handlerManager.addHandler(h);

        // the table handler
        handlerManager = new DXFTableSectionHandler();
        parser.addHandler(handlerManager);

        handler = new DXFLayerTableHandler();
        handlerManager.addHandler(handler);

        handler = new DXFLineTypeTableHandler();
        handlerManager.addHandler(handler);

        handler = new DXFDimensionStyleTableHandler();
        handlerManager.addHandler(handler);

        handler = new DXFStyleTableHandler();
        handlerManager.addHandler(handler);

        handler = new DXFVPortTableHandler();
        handlerManager.addHandler(handler);

        handler = new DXFViewTableHandler();
        handlerManager.addHandler(handler);

        // the entity section handler
        handlerManager = new DXFEntitiesSectionHandler();
        parser.addHandler(handlerManager);

        // the entity handlers
        h = new DXFLineHandler();
        handlerManager.addHandler(h);

        h = new DXFCircleHandler();
        handlerManager.addHandler(h);

        h = new DXFArcHandler();
        handlerManager.addHandler(h);

        h = new DXFPolylineHandler();
        handlerManager.addHandler(h);

        h = new DXFLWPolylineHandler();
        handlerManager.addHandler(h);

        h = new DXFMTextHandler();
        handlerManager.addHandler(h);

        h = new DXFTextHandler();
        handlerManager.addHandler(h);

        h = new DXFInsertHandler();
        handlerManager.addHandler(h);

        h = new DXFEllipseHandler();
        handlerManager.addHandler(h);

        h = new DXFSolidHandler();
        handlerManager.addHandler(h);

        h = new DXFTraceHandler();
        handlerManager.addHandler(h);

        h = new DXFDimensionHandler();
        handlerManager.addHandler(h);

        h = new DXFHatchHandler();
        handlerManager.addHandler(h);

        h = new DXFImageHandler();
        handlerManager.addHandler(h);

        h = new DXF3DFaceHandler();
        handlerManager.addHandler(h);

        h = new DXFRayHandler();
        handlerManager.addHandler(h);

        h = new DXFXLineHandler();
        handlerManager.addHandler(h);

        h = new DXFRegionHandler();
        handlerManager.addHandler(h);

        h = new DXFBodyHandler();
        handlerManager.addHandler(h);

        h = new DXF3DSolidHandler();
        handlerManager.addHandler(h);

        h = new DXFSplineHandler();
        handlerManager.addHandler(h);

        h = new DXFMLineHandler();
        handlerManager.addHandler(h);

        h = new DXFLeaderHandler();
        handlerManager.addHandler(h);

        h = new DXFToleranceHandler();
        handlerManager.addHandler(h);

        h = new DXFViewportHandler();
        handlerManager.addHandler(h);

        // the OBJECTS section
        handlerManager = new DXFObjectsSectionHandler();

        h = new DXFImageDefHandler();
        handlerManager.addHandler(h);

        h = new DXFDictionaryHandler();
        handlerManager.addHandler(h);

        h = new DXFPlotsettingsHandler();
        handlerManager.addHandler(h);

        h = new DXFLayoutHandler();
        handlerManager.addHandler(h);

        h = new DXFMLineStyleHandler();
        handlerManager.addHandler(h);

        //add the HandlerManager as Handler to the parser
        parser.addHandler(handlerManager);

        return parser;
    }

    /**
     * @see org.kabeja.parser.SAXParserBuilder the SAXParserBuilder for XML
     *      description
     *
     * @param file
     * @return the DXFParser build from the XML description file
     */
    public static Parser buildFromXML(String file) {
        try {
            return buildFromXML(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Parser buildFromXML(InputStream in) {
        return SAXParserBuilder.buildFromStream(in);
    }
}
