package paa.airline.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Airport {
	@Id
	//@GeneratedValue(strategy= GenerationType.IDENTITY) //indica que la clavve lo es autogenerada
    private String iataCode;
    private String cityName;
    private String airportName;
    private double longitude;
    private double latitude;

    /**
     * Default constructor, required by JPA. It does not initialize anything at all.
     */
    public Airport() {}

    /**
     * Contructs a new @code Airport with the specified parameters.
     *
     * Note that this constructor does not validate any of its parameters because it is not intended to be used
     * directly, but only through the business layer.
     *
     * @param iataCode the IATA-assigned three-letter code for this airport
     * @param cityName the name of the city this airport serves
     * @param airportName the full name of this airport
     * @param longitude the longitude coordinate (negative W, positive E)
     * @param latitude the latitude coordinate (negative S, positive N)
     */
    public Airport(String iataCode, String cityName, String airportName, double longitude, double latitude) {
        this.iataCode = iataCode;
        this.cityName = cityName;
        this.airportName = airportName;
        this.longitude = longitude;
        this.latitude = latitude;
    }


    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Airport)) return false;

        Airport otherAirport = (Airport) o;

        return Objects.equals(iataCode, otherAirport.iataCode);
    }

    @Override
    public int hashCode() {
        return iataCode != null ? iataCode.hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format("%s - %s, %s", this.iataCode, this.cityName, this.airportName);

    }
}
