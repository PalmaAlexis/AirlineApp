package paa.airline.util.impl;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;
import paa.airline.business.AirlineService;
import paa.airline.business.AirlineServiceException;
import paa.airline.model.Airport;
import paa.airline.model.Flight;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AirlinePainter implements Painter<JXMapViewer> {
    AirlineService service;
    List<Flight> otherFlights;
    Flight selectedFlight;
    LocalDate selectedDate;
    int occupiedSeats; // Only to be used when a flight is selected
    final static int pickerWidth = 10;
    final static int pickerHeight = 10;
    final static int padding = 5;

    public AirlinePainter(AirlineService service) {
        this.service = service;
        this.selectedFlight = null;
        this.otherFlights = new ArrayList<>();
    }

    private static Color availabilityColor(int available, int total) {
        Color c;
        final float availabilityRatio = (float) available / (float) total;
        if (0.5 < availabilityRatio) {
            c = Color.GREEN;
        } else if (0.2 < availabilityRatio) {
            c = Color.ORANGE;
        } else {
            c = Color.RED;
        }
        return c;
    }

    private static String colorToHTML(Color c) {
        return String.format("#%06X", 0xFFFFFF & c.getRGB());
    }

    private static JLabel makeAirportLabel(Airport w) {
        String labelText = String.format("<html><table cols=1 width=150><td><font size=+2>%s</font><br><font size=-2>%s</font><br><i>%s</i></td></table></html>", w.getIataCode(), w.getAirportName(), w.getCityName());

        JLabel label = new JLabel(labelText);
        label.setSize(label.getPreferredSize());

        return label;
    }

    private static JLabel makeFlightLabel(Flight f, LocalDate date, int occupiedSeats) {
        String labelText = String.format("<html>Flight %d: %s-%s<br><font size=-2>%s %s</font><br><i>Occupancy on %s: %d/%d</i></html>",
                f.getFlightNumber(), f.getOrigin().getIataCode(), f.getDestination().getIataCode(),
                f.getAircraft().getManufacturer(), f.getAircraft().getModel(),
                date.toString(), occupiedSeats, f.getAircraft().getSeatRows() * f.getAircraft().getSeatColumns());

        JLabel label = new JLabel(labelText);

        label.setSize(label.getPreferredSize());

        return label;
    }

    private static Image makeLabelImage(JLabel label) {
        BufferedImage bi = new BufferedImage(
                label.getWidth(),
                label.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        label.paint(bi.createGraphics());
        return bi;
    }

    private static RoundRectangle2D makeRectangle(JLabel label, Point2D locationPixel) {
        RoundRectangle2D rr = new RoundRectangle2D.Float();
        int x = (int) locationPixel.getX();
        int y = (int) locationPixel.getY();
        rr.setRoundRect(x - label.getWidth() / 2 - padding, y - (label.getHeight() + 2 * padding) - pickerHeight, label.getWidth() + 2 * padding, label.getHeight() + 2 * padding, 2 * padding, 2 * padding);
        return rr;
    }

    private static Polygon makePicker(Point2D locationPixel) {
        Polygon picker = new Polygon();
        int x = (int) locationPixel.getX();
        int y = (int) locationPixel.getY();
        picker.addPoint(x, y);
        picker.addPoint(x + pickerWidth / 2, y - pickerHeight);
        picker.addPoint(x - pickerWidth / 2, y - pickerHeight);
        return picker;
    }

    public void paintAirport(Graphics2D g, JXMapViewer viewer, Airport w) {
        g = (Graphics2D) g.create();
        GeoPosition pos = new GeoPosition(w.getLatitude(), w.getLongitude());
        Point2D point = viewer.getTileFactory().geoToPixel(pos, viewer.getZoom());

        int x = (int) point.getX();
        int y = (int) point.getY();

        JLabel label = makeAirportLabel(w);

        RoundRectangle2D rectangle = makeRectangle(label, point);
        g.setColor(Color.WHITE);
        g.fill(rectangle);
        g.setColor(Color.BLACK);
        g.draw(rectangle);

        g.drawImage(makeLabelImage(label), x - label.getWidth() / 2, y - label.getHeight() - pickerHeight - padding, null);

        Polygon picker = makePicker(point);
        g.setBackground(Color.BLACK);
        //g.drawPolygon(picker);
        g.fillPolygon(picker);

        g.dispose();
    }

    public void paintFlight(Graphics2D g, JXMapViewer viewer, Flight w, int occupiedSeats) {
        g = (Graphics2D) g.create();
        Vec3D originVec = Vec3D.fromSpherical(w.getOrigin().getLongitude(), w.getOrigin().getLatitude(), 1);
        Vec3D destinationVec = Vec3D.fromSpherical(w.getDestination().getLongitude(), w.getDestination().getLatitude(), 1);
        Vec3D midPoint = Vec3D.slerp(originVec, destinationVec, 0.5);

        GeoPosition pos = new GeoPosition(midPoint.getLatitude(), midPoint.getLongitude());
        Point2D point = viewer.getTileFactory().geoToPixel(pos, viewer.getZoom());

        int x = (int) point.getX();
        int y = (int) point.getY();

        JLabel label = makeFlightLabel(w, this.selectedDate, occupiedSeats);

        RoundRectangle2D rectangle = makeRectangle(label, point);
        g.setColor(Color.WHITE);
        g.fill(rectangle);
        g.setColor(Color.BLACK);
        g.draw(rectangle);

        g.drawImage(makeLabelImage(label), x - label.getWidth() / 2, y - label.getHeight() - pickerHeight - padding, null);

        Polygon picker = makePicker(point);
        g.setBackground(Color.BLACK);
        //g.drawPolygon(picker);
        g.fillPolygon(picker);

        g.dispose();
    }

    public void updateFlights(LocalDate date, Long selectedFlightNumber) {
        this.otherFlights.clear();
        this.selectedDate = date;
        for (Flight f: this.service.listFlights()) {
            if (f.getFlightNumber().equals(selectedFlightNumber)) {
                this.selectedFlight = f;
                try {
                    int availableSeats = this.service.availableSeats(f.getFlightNumber(), date);
                    int totalSeats = f.getAircraft().getSeatColumns() * f.getAircraft().getSeatRows();
                    this.occupiedSeats = totalSeats - availableSeats;
                } catch (AirlineServiceException e) {
                    // Should never happen at this point, will do nothing about it
                    throw new RuntimeException(e);
                }
            } else {
                this.otherFlights.add(f);
            }
        }
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int width, int height) {
        g = (Graphics2D) g.create();

        // convert from viewport to world bitmap
        Rectangle rect = map.getViewportBounds();
        //System.err.println(rect);
        g.translate(-rect.x, -rect.y);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // do the drawing
        g.setColor(Color.GRAY);
        g.setStroke(new BasicStroke(1));
        for (Flight f: this.otherFlights) {
            paintFlight(g, map, f);
        }
        if (this.selectedFlight != null) {
            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(3));
            paintFlight(g, map, this.selectedFlight);
            paintAirport(g, map, this.selectedFlight.getOrigin());
            paintAirport(g, map, this.selectedFlight.getDestination());
            paintFlight(g, map, this.selectedFlight, this.occupiedSeats);
        }
    }

    private static void paintFlight(Graphics2D g, JXMapViewer map, Flight f) {
        GeoPosition origin = new GeoPosition(f.getOrigin().getLatitude(), f.getOrigin().getLongitude());
        GeoPosition destination = new GeoPosition(f.getDestination().getLatitude(), f.getDestination().getLongitude());
        paintGreatCircleRoute(g, map, origin, destination);
    }

    private static void paintGreatCircleRoute(Graphics2D g, JXMapViewer map, GeoPosition start, GeoPosition end) {
        Vec3D vecA = Vec3D.fromSpherical(start.getLongitude(), start.getLatitude(), 1.0);
        Vec3D vecB = Vec3D.fromSpherical(end.getLongitude(), end.getLatitude(), 1.0);
        int numpoints = 100;
        List<GeoPosition> positions = new ArrayList<>(numpoints);
        for (double fraction: linspace(0.0, 1.0, numpoints)) {
            Vec3D currentVector = Vec3D.slerp(vecA, vecB, fraction);
            GeoPosition currentPosition = new GeoPosition(currentVector.getLatitude(), currentVector.getLongitude());
            positions.add(currentPosition);
        }
        int airportRadius = 5;
        Point2D startPoint = map.getTileFactory().geoToPixel(start, map.getZoom());
        Point2D endPoint = map.getTileFactory().geoToPixel(end, map.getZoom());
        g.fillOval((int) (startPoint.getX() - airportRadius), (int) (startPoint.getY() - airportRadius), 1 + 2 * airportRadius, 1 + 2 * airportRadius);
        g.fillOval((int) (endPoint.getX() - airportRadius), (int) (endPoint.getY() - airportRadius), 1 + 2 * airportRadius, 1 + 2 * airportRadius);
        for (int i = 1; i < numpoints; ++i) {
            GeoPosition positionA = positions.get(i - 1);
            GeoPosition positionB = positions.get(i);
            Point2D pointA = map.getTileFactory().geoToPixel(positionA, map.getZoom());
            Point2D pointB = map.getTileFactory().geoToPixel(positionB, map.getZoom());



            if (180 < Math.abs(positionA.getLongitude() - positionB.getLongitude())) {
                continue; // Wraps around date change line, we don't want to draw this segment
            }
            Rectangle rect = map.getViewportBounds();
            if (pointA.getX() < rect.getX() && pointB.getX() < rect.getX()) {
                continue;
            }
            if (pointA.getX() > rect.getX() + rect.getWidth() && pointB.getX() > rect.getX() + rect.getWidth()) {
                continue;
            }
            if (pointA.getY() < rect.getY() && pointB.getY() < rect.getY()) {
                continue;
            }
            if (pointA.getY() > rect.getY() + rect.getHeight() && pointB.getY() > rect.getY() + rect.getHeight()) {
                continue;
            }
            g.drawLine((int) (0.5 + pointA.getX()), (int) (0.5 + pointA.getY()), (int) (0.5 + pointB.getX()), (int) (0.5 + pointB.getY()));
        }
    }

    private static List<Double> linspace(double from, double to, int numElements) {
        ArrayList<Double> space = new ArrayList<>();

        if (numElements <= 0) {
            // Let's not hard fail, just return the empty list
        } else if (numElements == 1) {
            space.add(to); // This is consistent with Matlab's linspace
        } else {
            space.ensureCapacity(numElements);
            for (int i = 0; i < numElements; ++i) {
                space.add(from + (to - from) * ((double) i / (double) (numElements - 1)));
            }
        }
        return space;
    }

}
