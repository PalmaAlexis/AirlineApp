package paa.airline.util;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class AirportQuery {
    static ConcurrentHashMap<String, JSONObject> cache = new ConcurrentHashMap<>();

    private static JSONObject ensureCodeInCache(String iataCode) {
        if (cache.containsKey(iataCode.toUpperCase())) {
            return cache.get(iataCode.toUpperCase());
        }
        if (!iataCode.matches("[A-Za-z]{3}")) {
            return null;
        }

        try {
            Instant i = Instant.now();
            Thread.sleep(1000, 0); // very crude rate-limiting to avoid problems with the server if called several times in a row
            URLConnection conn = new URL("https://www.airport-data.com/api/ap_info.json?iata=" + iataCode.toUpperCase()).openConnection();
            JSONObject o = new JSONObject(new String(conn.getInputStream().readAllBytes()));
            if (o.isNull("location")) {
                System.err.println("Este aeropuerto no existe: " + iataCode);
                return null;
            } else {
                cache.put(iataCode.toUpperCase(), o);
                return o;
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getName(String iataCode) {
        JSONObject o = ensureCodeInCache(iataCode);
        return o == null ? null : o.getString("name");
    }

    public static String getLocation(String iataCode) {
        JSONObject o = ensureCodeInCache(iataCode);
        return o == null ? null : o.getString("location");
    }

    public static Double getLongitude(String iataCode) {
        JSONObject o = ensureCodeInCache(iataCode);
        return o == null ? null : o.getDouble("longitude");
    }

    public static Double getLatitude(String iataCode) {
        JSONObject o = ensureCodeInCache(iataCode);
        return o == null ? null : o.getDouble("latitude");
    }

    public static double geodesicDistance(String iataCodeA, String iataCodeB) {
        JSONObject airportA = ensureCodeInCache(iataCodeA);
        JSONObject airportB = ensureCodeInCache(iataCodeB);
        double lonA = Math.toRadians(airportA.getDouble("longitude"));
        double lonB = Math.toRadians(airportB.getDouble("longitude"));
        double latA = Math.toRadians(airportA.getDouble("latitude"));
        double latB = Math.toRadians(airportB.getDouble("latitude"));
        return geodesicDistance(lonA, latA, lonB, latB);
    }

    public static double geodesicDistance(double lonA, double latA, double lonB, double latB) {
        final double r = 6371.009;
        return r * Math.acos(Math.sin(latA) * Math.sin(latB) + Math.cos(latA) * Math.cos(latB) * Math.cos(lonB - lonA));
    }





    public static void main(String[] args) {
        for (String iataCode: new String[] {"mad", "lhr", "agp"}) {
            System.out.println(getName(iataCode));
            System.out.println(getLocation(iataCode));
            System.out.println(getLongitude(iataCode));
            System.out.println(getLatitude(iataCode));
        }

        System.out.println(geodesicDistance("mad", "bcn"));

    }
}
