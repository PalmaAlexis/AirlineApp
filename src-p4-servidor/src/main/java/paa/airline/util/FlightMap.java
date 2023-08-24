package paa.airline.util;

import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.*;
import paa.airline.business.AirlineService;
import paa.airline.util.impl.AirlinePainter;

import java.awt.*;
import java.time.LocalDate;

public class FlightMap extends JXMapKit {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	AirlinePainter flightPainter;
    public FlightMap(int preferredWidth, int preferredHeight, AirlineService service) {
        super();
        this.setDefaultProvider(DefaultProviders.OpenStreetMaps);

        TileFactoryInfo info = new OSMTileFactoryInfo();
        TileFactory tf = new DefaultTileFactory(info);
        this.setTileFactory(tf);
        //this.setZoom(7);
        //this.setAddressLocation(new GeoPosition(40.438889, -3.691944)); // Madrid
        this.setZoom(14);
        //this.setAddressLocation(new GeoPosition(48.305833, 14.286389)); // Linz, Austria
        this.setAddressLocation(new GeoPosition(48.305833, 10)); // Un poco pa' la izquierda
        this.getMainMap().setRestrictOutsidePanning(true);
        this.getMainMap().setHorizontalWrapped(false);

//        this.waypointPainter = new WaypointPainter<LockerWaypoint>();
//        waypointPainter.setRenderer(new LockerWaypointRenderer());

        this.flightPainter = new AirlinePainter(service);
//        this.circlePainter = new GreatCirclePainter();
//        java.util.List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
//        painters.add(this.circlePainter);
//        painters.add(this.waypointPainter);
//        CompoundPainter<JXMapViewer> compoundPainter = new CompoundPainter<>(painters);

        this.getMainMap().setOverlayPainter(this.flightPainter);
        //this.getMainMap().setOverlayPainter(this.waypointPainter);
//        this.getMainMap().setOverlayPainter(compoundPainter);
//        this.waypoints = new HashSet<LockerWaypoint>();


        ((DefaultTileFactory) this.getMainMap().getTileFactory()).setThreadPoolSize(8);
        this.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
    }

    public void updateMap(LocalDate date, Long selectedFlightNumber) {
        this.flightPainter.updateFlights(date, selectedFlightNumber);
        this.repaint();
    }
}
