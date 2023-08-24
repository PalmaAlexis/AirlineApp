import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import paa.airline.business.AirlineServiceException;
import paa.airline.business.JPAAirlineService;
import paa.airline.model.AircraftType;
import paa.airline.model.Airport;
import paa.airline.model.Flight;
import paa.airline.model.Ticket;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;



import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class AirlineServiceTest {
    private static final String persistenceUnitName = "paa-jpa";
    private JPAAirlineService service = new JPAAirlineService(persistenceUnitName);

    @BeforeEach
    void wipeDatabase() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);
        EntityManager em = emf.createEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        for (Ticket t: em.createQuery("select t from Ticket t", Ticket.class).getResultList()) {
            t = em.merge(t);
            em.remove(t);
        }
        for (Flight t: em.createQuery("select t from Flight t", Flight.class).getResultList()) {
            t = em.merge(t);
            em.remove(t);
        }
        for (AircraftType t: em.createQuery("select t from AircraftType t", AircraftType.class).getResultList()) {
            t = em.merge(t);
            em.remove(t);
        }
        for (Airport t: em.createQuery("select t from Airport t", Airport.class).getResultList()) {
            t = em.merge(t);
            em.remove(t);
        }
        et.commit();
    }

    @Test
    @DisplayName("Try to create airport with null IATA code")
    void tryCreateAirportNullCode() {
        String iataCode = null;
        String cityName = "Madrid";
        String airportName = "Madrid-Barajas Adolfo Suárez";
        double longitude = -3.566764;
        double latitude = 40.493556;
        try {
            Airport a = service.createAirport(iataCode, cityName, airportName, longitude, latitude);
            Assertions.fail("Must not create airport with null IATA code");
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create airport with a too long an IATA code")
    void tryCreateAirportTooLongCode() {
        String iataCode = "ABCD";
        String cityName = "Madrid";
        String airportName = "Madrid-Barajas Adolfo Suárez";
        double longitude = -3.566764;
        double latitude = 40.493556;
        try {
            Airport a = service.createAirport(iataCode, cityName, airportName, longitude, latitude);
            Assertions.fail(String.format("Must not create airport with a IATA code longer than three uppercase letters (provided: '%s')", iataCode));
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create airport with an invalid IATA code that features non-letter characters")
    void tryCreateAirportNotLettersCode() {
        String iataCode = "AB2";
        String cityName = "Madrid";
        String airportName = "Madrid-Barajas Adolfo Suárez";
        double longitude = -3.566764;
        double latitude = 40.493556;
        try {
            Airport a = service.createAirport(iataCode, cityName, airportName, longitude, latitude);
            Assertions.fail(String.format("Must not create airport with a IATA code other than three uppercase letters (provided: '%s')", iataCode));
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create airport with an invalid IATA code that is not all uppercase letters")
    void tryCreateAirportNotUppercaseCode() {
        String iataCode = "AbC";
        String cityName = "Madrid";
        String airportName = "Madrid-Barajas Adolfo Suárez";
        double longitude = -3.566764;
        double latitude = 40.493556;
        try {
            Airport a = service.createAirport(iataCode, cityName, airportName, longitude, latitude);
            Assertions.fail(String.format("Must not create airport with a IATA code other than three uppercase letters (provided: '%s')", iataCode));
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create airport with a null city name")
    void tryCreateAirportNullCityName() {
        String iataCode = "MAD";
        String cityName = null;
        String airportName = "Madrid-Barajas Adolfo Suárez";
        double longitude = -3.566764;
        double latitude = 40.493556;
        try {
            Airport a = service.createAirport(iataCode, cityName, airportName, longitude, latitude);
            Assertions.fail(String.format("Must not create airport with a null city name"));
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create airport with an empty city name")
    void tryCreateAirportEmptyCityName() {
        String iataCode = "MAD";
        String cityName = "";
        String airportName = "Madrid-Barajas Adolfo Suárez";
        double longitude = -3.566764;
        double latitude = 40.493556;
        try {
            Airport a = service.createAirport(iataCode, cityName, airportName, longitude, latitude);
            Assertions.fail(String.format("Must not create airport with an empty city name (provided: '%s')", cityName));
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create airport with a blank (whitespace only) city name")
    void tryCreateAirportBlankCityName() {
        String iataCode = "MAD";
        String cityName = "    ";
        String airportName = "Madrid-Barajas Adolfo Suárez";
        double longitude = -3.566764;
        double latitude = 40.493556;
        try {
            Airport a = service.createAirport(iataCode, cityName, airportName, longitude, latitude);
            Assertions.fail(String.format("Must not create airport with a blank city name (provided: '%s')", cityName));
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create airport with a null name")
    void tryCreateAirportNullName() {
        String iataCode = "MAD";
        String cityName = "Barajas";
        String airportName = null;
        double longitude = -3.566764;
        double latitude = 40.493556;
        try {
            Airport a = service.createAirport(iataCode, cityName, airportName, longitude, latitude);
            Assertions.fail(String.format("Must not create airport with a null name"));
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create airport with an empty name")
    void tryCreateAirportEmptyName() {
        String iataCode = "MAD";
        String cityName = "Barajas";
        String airportName = "";
        double longitude = -3.566764;
        double latitude = 40.493556;
        try {
            Airport a = service.createAirport(iataCode, cityName, airportName, longitude, latitude);
            Assertions.fail(String.format("Must not create airport with an empty name (provided: '%s'", airportName));
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create airport with a blank (whitespace only) name")
    void tryCreateAirportBlankName() {
        String iataCode = "MAD";
        String cityName = "Barajas";
        String airportName = "    ";
        double longitude = -3.566764;
        double latitude = 40.493556;
        try {
            Airport a = service.createAirport(iataCode, cityName, airportName, longitude, latitude);
            Assertions.fail(String.format("Must not create airport with a blank name (provided: '%s'", airportName));
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create airport with too low longitude")
    void tryCreateAirportLowLongitude() {
        String iataCode = "MAD";
        String cityName = "Barajas";
        String airportName = "Madrid-Barajas Adolfo Suárez";
        double longitude = Math.nextDown(-180.0);
        double latitude = 40.493556;
        try {
            Airport a = service.createAirport(iataCode, cityName, airportName, longitude, latitude);
            Assertions.fail(String.format("Must not create airport with a longitude < -180.0 (provided: '%f'", longitude));
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create airport with too high longitude")
    void tryCreateAirportHighLongitude() {
        String iataCode = "MAD";
        String cityName = "Barajas";
        String airportName = "Madrid-Barajas Adolfo Suárez";
        double longitude = Math.nextUp(180.0);
        double latitude = 40.493556;
        try {
            Airport a = service.createAirport(iataCode, cityName, airportName, longitude, latitude);
            Assertions.fail(String.format("Must not create airport with a longitude > 180.0 (provided: '%f'", longitude));
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create airport with too low latitude")
    void tryCreateAirportLowLatitude() {
        String iataCode = "MAD";
        String cityName = "Barajas";
        String airportName = "Madrid-Barajas Adolfo Suárez";
        double longitude = -3.566764;
        double latitude = Math.nextDown(-90.0);
        try {
            Airport a = service.createAirport(iataCode, cityName, airportName, longitude, latitude);
            Assertions.fail(String.format("Must not create airport with a longitude < -180.0 (provided: '%f'", longitude));
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create airport with too high latitude")
    void tryCreateAirportHighLatitude() {
        String iataCode = "MAD";
        String cityName = "Barajas";
        String airportName = "Madrid-Barajas Adolfo Suárez";
        double longitude = -3.566764;
        double latitude = Math.nextUp(90.0);
        try {
            Airport a = service.createAirport(iataCode, cityName, airportName, longitude, latitude);
            Assertions.fail(String.format("Must not create airport with a longitude > 180.0 (provided: '%f'", longitude));
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create airport within normal parameters")
    void tryCreateAirport() {
        String iataCode = "MAD";
        String cityName = "Madrid";
        String airportName = "Madrid-Barajas Adolfo Suárez";
        double longitude = -3.566764;
        double latitude = 40.493556;
        try {
            Airport a = service.createAirport(iataCode, cityName, airportName, longitude, latitude);
            Assertions.assertNotNull(a);
            Assertions.assertEquals(iataCode, a.getIataCode());
            Assertions.assertEquals(cityName, a.getCityName());
            Assertions.assertEquals(airportName, a.getAirportName());
            Assertions.assertEquals(longitude, longitude);
            Assertions.assertEquals(latitude, latitude);
        } catch (AirlineServiceException e) {
            Assertions.fail("There was nothing wrong with the parameters, should have created a new airport.");
        }
    }

    @Test
    @DisplayName("Try to create airport at the north pole")
    void tryCreateAirportNorthPole() {
        String iataCode = "MAD";
        String cityName = "Madrid";
        String airportName = "Madrid-Barajas Adolfo Suárez";
        double longitude = -3.566764;
        double latitude = 90.0;
        try {
            Airport a = service.createAirport(iataCode, cityName, airportName, longitude, latitude);
            Assertions.assertNotNull(a);
            Assertions.assertEquals(iataCode, a.getIataCode());
            Assertions.assertEquals(cityName, a.getCityName());
            Assertions.assertEquals(airportName, a.getAirportName());
            Assertions.assertEquals(longitude, longitude);
            Assertions.assertEquals(latitude, latitude);
        } catch (AirlineServiceException e) {
            Assertions.fail("There was nothing wrong with the parameters, should have created a new airport.");
        }
    }

    @Test
    @DisplayName("Try to create airport at the south pole")
    void tryCreateAirportSouthPole() {
        String iataCode = "MAD";
        String cityName = "Madrid";
        String airportName = "Madrid-Barajas Adolfo Suárez";
        double longitude = -3.566764;
        double latitude = -90.0;
        try {
            Airport a = service.createAirport(iataCode, cityName, airportName, longitude, latitude);
            Assertions.assertNotNull(a);
            Assertions.assertEquals(iataCode, a.getIataCode());
            Assertions.assertEquals(cityName, a.getCityName());
            Assertions.assertEquals(airportName, a.getAirportName());
            Assertions.assertEquals(longitude, longitude);
            Assertions.assertEquals(latitude, latitude);
        } catch (AirlineServiceException e) {
            Assertions.fail("There was nothing wrong with the parameters, should have created a new airport.");
        }
    }

    @Test
    @DisplayName("Try to create an airport as far west as possible")
    void tryCreateAirportFarWest() {
        String iataCode = "MAD";
        String cityName = "Madrid";
        String airportName = "Madrid-Barajas Adolfo Suárez";
        double longitude = -180.0;
        double latitude = 40.493556;
        try {
            Airport a = service.createAirport(iataCode, cityName, airportName, longitude, latitude);
            Assertions.assertNotNull(a);
            Assertions.assertEquals(iataCode, a.getIataCode());
            Assertions.assertEquals(cityName, a.getCityName());
            Assertions.assertEquals(airportName, a.getAirportName());
            Assertions.assertEquals(longitude, longitude);
            Assertions.assertEquals(latitude, latitude);
        } catch (AirlineServiceException e) {
            Assertions.fail("There was nothing wrong with the parameters, should have created a new airport.");
        }
    }

    @Test
    @DisplayName("Try to create an airport as far east as possible")
    void tryCreateAirportFarEast() {
        String iataCode = "MAD";
        String cityName = "Madrid";
        String airportName = "Madrid-Barajas Adolfo Suárez";
        double longitude = 180.0;
        double latitude = 40.493556;
        try {
            Airport a = service.createAirport(iataCode, cityName, airportName, longitude, latitude);
            Assertions.assertNotNull(a);
            Assertions.assertEquals(iataCode, a.getIataCode());
            Assertions.assertEquals(cityName, a.getCityName());
            Assertions.assertEquals(airportName, a.getAirportName());
            Assertions.assertEquals(longitude, longitude);
            Assertions.assertEquals(latitude, latitude);
        } catch (AirlineServiceException e) {
            Assertions.fail("There was nothing wrong with the parameters, should have created a new airport.");
        }
    }

    @Test
    @DisplayName("Test listAirports")
    void testListAirports() {
        final int numAirports = 5;
        Random rnd = new Random();
        Set<String> iataCodes = new HashSet<>();
        while (iataCodes.size() < numAirports) {
            String iataCode = String.valueOf((char) ('A' + rnd.nextInt('Z' - 'A' + 1)))
                    + String.valueOf((char) ('A' + rnd.nextInt('Z' - 'A' + 1)))
                    + String.valueOf((char) ('A' + rnd.nextInt('Z' - 'A' + 1)));
            iataCodes.add(iataCode);
        }
        String[] orderedIataCodes = iataCodes.toArray(new String[0]);
        for (int i = 0; i < numAirports; i++) {
            String cityName = "City" + orderedIataCodes[i];
            String airportName = "Airport" + orderedIataCodes[i];
            double longitude = rnd.nextDouble() * 360.0 - 180.0;
            double latitude = rnd.nextDouble() * 180.0 - 90.0;
            try {
                service.createAirport(orderedIataCodes[i], cityName, airportName, longitude, latitude);
            } catch (AirlineServiceException e) {
                Assertions.fail("There was nothing wrong with the parameters, should have created a new airport.");
            }
        }
        List<Airport> airports = service.listAirports();
        Assertions.assertEquals(numAirports, airports.size());
        for (Airport a: airports) {
            Assertions.assertTrue(iataCodes.contains(a.getIataCode()));
        }
    }

    @Test
    @DisplayName("Try to create aircraft within normal parameters")
    void tryCreateAircraft() {
        String manufacturer = "Airbus";
        String model = "A318";
        int rows = 22;
        int cols = 6;
        try {
            AircraftType a = service.createAircraft(manufacturer, model, rows, cols);
            Assertions.assertNotNull(a);
            Assertions.assertNotNull(a.getId());
        } catch (AirlineServiceException e) {
            Assertions.fail("There was nothing wrong with the parameters, should have created a new aircraft.");
        }
    }

    @Test
    @DisplayName("Try to create aircraft with null manufacturer")
    void tryCreateAircraftNullManufacturer() {
        String manufacturer = null;
        String model = "A318";
        int rows = 22;
        int cols = 6;
        try {
            AircraftType a = service.createAircraft(manufacturer, model, rows, cols);
            Assertions.fail("Must not create an aircraft with null manufacturer");
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create aircraft with blank manufacturer")
    void tryCreateAircraftBlankManufacturer() {
        String manufacturer = "   \t  ";
        String model = "A318";
        int rows = 22;
        int cols = 6;
        try {
            AircraftType a = service.createAircraft(manufacturer, model, rows, cols);
            Assertions.fail("Must not create an aircraft with blank manufacturer");
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create aircraft with null model")
    void tryCreateAircraftNullModel() {
        String manufacturer = "Airbus";
        String model = null;
        int rows = 22;
        int cols = 6;
        try {
            AircraftType a = service.createAircraft(manufacturer, model, rows, cols);
            Assertions.fail("Must not create an aircraft with null model");
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create aircraft with blank model")
    void tryCreateAircraftBlankModel() {
        String manufacturer = "Airbus";
        String model = "   \t  ";
        int rows = 22;
        int cols = 6;
        try {
            AircraftType a = service.createAircraft(manufacturer, model, rows, cols);
            Assertions.fail("Must not create an aircraft with blank model");
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create aircraft with more columns than rows")
    void tryCreateAircraftWithMoreColumns() {
        String manufacturer = "Airbus";
        String model = "A318";
        int rows = 22;
        int cols = 23;
        try {
            AircraftType a = service.createAircraft(manufacturer, model, rows, cols);
            Assertions.fail(String.format("Must not create an aircraft with more seat columns (%d) than rows (%d)", cols, rows));
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create aircraft with as many columns as rows")
    void tryCreateAircraftWithEqualColumns() {
        String manufacturer = "Airbus";
        String model = "A318";
        int rows = 22;
        int cols = 22;
        try {
            AircraftType a = service.createAircraft(manufacturer, model, rows, cols);
            Assertions.fail(String.format("Must not create an aircraft with as many seat columns (%d) as rows (%d)", cols, rows));
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create aircraft with zero columns")
    void tryCreateAircraftWithZeroColumns() {
        String manufacturer = "Airbus";
        String model = "A318";
        int rows = 22;
        int cols = 0;
        try {
            AircraftType a = service.createAircraft(manufacturer, model, rows, cols);
            Assertions.fail(String.format("Must not create an aircraft with zero seat columns (%d)", cols));
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create aircraft with negative columns")
    void tryCreateAircraftWithNegativeColumns() {
        String manufacturer = "Airbus";
        String model = "A318";
        int rows = 22;
        int cols = -1;
        try {
            AircraftType a = service.createAircraft(manufacturer, model, rows, cols);
            Assertions.fail(String.format("Must not create an aircraft with negative seat columns (%d)", cols));
        } catch (AirlineServiceException e) { }
    }

    // We don't test zero rows or negative rows because we have established that rows > cols and cols > 0

    @Test
    @DisplayName("Test listAircraftTypes")
    void testListAircraftTypes() {
        final int numAircrafts = 5;
        String manufacturer = "Manufacturer ";
        String model = "Model ";
        for (int i = 0; i < numAircrafts; ++i) {
            try {
                service.createAircraft(manufacturer + i, model + i, 2 + i, 1 + i);
            } catch (AirlineServiceException e) {
                Assertions.fail("There was nothing wrong with the parameters, should have created a new aircraft.");
            }
        }
        List<AircraftType> aircraftTypes = service.listAircraftTypes();
        for (AircraftType a: aircraftTypes) {
            Assertions.assertNotNull(a);
            Assertions.assertNotNull(a.getId());
            Assertions.assertTrue(a.getManufacturer().startsWith(manufacturer));
            Assertions.assertTrue(a.getModel().startsWith(model));
            Assertions.assertTrue(a.getSeatColumns() > 0);
            Assertions.assertTrue(a.getSeatColumns() < a.getSeatRows());
        }
    }

    @Test
    @DisplayName("Create flight with correct parameters")
    void testCreateFlight() {
        try {
            Airport origin = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
            Airport destination = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
            AircraftType aircraft = service.createAircraft("Airbus", "A318", 22, 6);
            Flight f = service.createFlight(origin.getIataCode(), destination.getIataCode(), aircraft.getId());
            Assertions.assertNotNull(f);
            Assertions.assertNotNull(f.getFlightNumber());
            Assertions.assertEquals(origin.getIataCode(), f.getOrigin().getIataCode());
            Assertions.assertEquals(destination.getIataCode(), f.getDestination().getIataCode());
            Assertions.assertEquals(aircraft.getId(), f.getAircraft().getId());
        } catch (AirlineServiceException e) {
            Assertions.fail("There was nothing wrong with the parameters, should have created a new flight.");
        }
    }

    @Test
    @DisplayName("Try to create flight with null origin")
    void testCreateFlightNullOrigin() {
        try {
            Airport destination = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
            AircraftType aircraft = service.createAircraft("Airbus", "A318", 22, 6);
            Flight f = service.createFlight(null, destination.getIataCode(), aircraft.getId());
            Assertions.fail("Must not create flight with null origin airport");
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create flight with null destination")
    void testCreateFlightNullDestination() {
        try {
            Airport origin = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
            AircraftType aircraft = service.createAircraft("Airbus", "A318", 22, 6);
            Flight f = service.createFlight(origin.getIataCode(), null, aircraft.getId());
            Assertions.fail("Must not create flight with null destination airport");
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create flight with null aircraft")
    void testCreateFlightNullAircraft() {
        try {
            Airport origin = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
            Airport destination = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
            Flight f = service.createFlight(origin.getIataCode(), destination.getIataCode(), null);
            Assertions.fail("Must not create flight with null aircraft");
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create flight with non-existing origin")
    void testCreateFlightNonExistingOrigin() {
        try {
            Airport destination = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
            AircraftType aircraft = service.createAircraft("Airbus", "A318", 22, 6);
            Flight f = service.createFlight("XXX", destination.getIataCode(), aircraft.getId());
            Assertions.fail("Must not create flight with non-existing origin airport");
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create flight with non-existing destination")
    void testCreateFlightNonExistingDestination() {
        try {
            Airport origin = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
            AircraftType aircraft = service.createAircraft("Airbus", "A318", 22, 6);
            Flight f = service.createFlight(origin.getIataCode(), "XXX", aircraft.getId());
            Assertions.fail("Must not create flight with non-existing destination airport");
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create flight with non-existing aircraft")
    void testCreateFlightNonExistingAircraft() {
        try {
            Airport origin = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
            Airport destination = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
            Flight f = service.createFlight(origin.getIataCode(), destination.getIataCode(), 666L);
            Assertions.fail("Must not create flight with non-existing aircraft");
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Try to create flight with same origin and destination")
    void testCreateFlightSameOriginAndDestination() {
        try {
            Airport origin = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
            Airport destination = origin;
            AircraftType aircraft = service.createAircraft("Airbus", "A318", 22, 6);
            Flight f = service.createFlight(origin.getIataCode(), destination.getIataCode(), aircraft.getId());
            Assertions.fail("Must not create flight with the same origin and destination airport");
        } catch (AirlineServiceException e) { }
    }

    @Test
    @DisplayName("Test findFlight")
    void testFindFlight() {
        try {
            Airport origin = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
            Airport destination = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
            AircraftType aircraft = service.createAircraft("Airbus", "A318", 22, 6);
            Flight f = service.createFlight(origin.getIataCode(), destination.getIataCode(), aircraft.getId());
            Flight found = service.findFlight(f.getFlightNumber());
            Assertions.assertNotNull(found);
            Assertions.assertEquals(f.getFlightNumber(), found.getFlightNumber());
            Assertions.assertEquals(f.getOrigin().getIataCode(), origin.getIataCode());
            Assertions.assertEquals(f.getDestination().getIataCode(), destination.getIataCode());
            Assertions.assertEquals(f.getAircraft().getId(), aircraft.getId());
        } catch (AirlineServiceException e) {
            Assertions.fail("There was nothing wrong with the parameters, should have created a new flight.");
        }
    }

    @Test
    @DisplayName("Test listFlights")
    void testListFlights() {
        final int numFlights = 7;
        Random rnd = new Random();
        try {
            Airport[] airports = {service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556),
                                  service.createAirport("MUC", "Munich", "Munich International Airport (Franz Josef Strauß International Airport)", 11.786086, 48.353783),
                                  service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500),
                                  service.createAirport("ORY", "Orly (near Paris)", "Paris Orly Airport", 2.359444, 48.725278),
                                  service.createAirport("JFK", "Jamaica, New York (New York City)", "John F. Kennedy International Airport (formerly Idlewild Airport)", -73.778926, 40.639751),
                                  service.createAirport("MIA", "Miami, Florida", "Miami International Airport", -80.290111, 25.795361),
                                  service.createAirport("FCO", "Rome", "Leonardo Da Vinci International Airport (Fiumicino International Airport)", 12.250797, 41.804475)};
            AircraftType[] aircrafts = {service.createAircraft("Airbus", "A318", 22, 6),
                                        service.createAircraft("Boeing", "737-100", 23, 6),
                                        service.createAircraft("ATR", "42", 12, 4)};
            for (int i = 0; i < numFlights; i++) {
                int originIndex = rnd.nextInt(airports.length);
                int destinationIndex;
                do {
                    destinationIndex = rnd.nextInt(airports.length);
                } while (destinationIndex == originIndex);
                int aircraftIndex = rnd.nextInt(aircrafts.length);
                service.createFlight(airports[originIndex].getIataCode(), airports[destinationIndex].getIataCode(), aircrafts[aircraftIndex].getId());
            }
        } catch (AirlineServiceException e) {
            Assertions.fail("There was nothing wrong with the parameters, should have created a new flight.");
        }
        List<Flight> flights = service.listFlights();
        Assertions.assertEquals(numFlights, flights.size());
    }
    
    @Test
    @DisplayName("Purchase ticket with correct parameters on an empty plane")
    void tryPurchaseTicket() {
    	Ticket t = null;
    	Flight f = null;
    	
    	LocalDate purchaseDate = LocalDate.of(2023, 4, 30);
        LocalDate flightDate = LocalDate.of(2023, 5, 1);
        String firstName = "Fulano";
        String lastName = "Fulánez";
        
		try {
			Airport origin = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
			Airport destination = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
	        AircraftType aircraft = service.createAircraft("Airbus", "A318", 22, 6);
	        f = service.createFlight(origin.getIataCode(), destination.getIataCode(), aircraft.getId());
	        
	        t = service.purchaseTicket(firstName, lastName, f.getFlightNumber(), purchaseDate, flightDate);
		} catch (AirlineServiceException e) {
			Assertions.fail("There was nothing wrong with the parameters, should have created a new ticket.");
		}
		Assertions.assertNotNull(t);
		Assertions.assertNotNull(t.getTicketNumber());
		Assertions.assertEquals(flightDate, t.getFlightDate());
		Assertions.assertEquals(firstName, t.getPassengerFirstName());
		Assertions.assertEquals(lastName, t.getPassengerLastName());
		Assertions.assertNotNull(t.getFlight());
		Assertions.assertEquals(f.getFlightNumber(), t.getFlight().getFlightNumber());
		Assertions.assertTrue(t.getSeatColumn() > 0);
		Assertions.assertTrue(t.getSeatRow() > 0);
		Assertions.assertTrue(t.getSeatColumn() <= t.getFlight().getAircraft().getSeatColumns());
		Assertions.assertTrue(t.getSeatRow() <= t.getFlight().getAircraft().getSeatRows());
		Assertions.assertEquals(1, service.findFlight(t.getFlight().getFlightNumber()).getTickets().size()); // Ensure indirect list is correctly updated
    }
    
    @Test
    @DisplayName("Purchase tickets with correct parameters until completely filling a plane")
    void tryPurchaseAllTickets() {
    	final int seatRows = 3;
    	final int seatCols = 2;
    	Ticket[] tickets = new Ticket[seatRows * seatCols];
    	Set<Pair> seats = new HashSet<AirlineServiceTest.Pair>(); // Automatically eliminates duplicates
    	Flight f1 = null;
    	Flight f2 = null;
    	
    	LocalDate purchaseDate = LocalDate.of(2023, 4, 30);
        LocalDate flightDate = LocalDate.of(2023, 5, 1);
        String firstName = "Fulano";
        String lastName = "Fulánez";
        
		try {
			Airport ap1 = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
			Airport ap2 = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
			Airport ap3 = service.createAirport("MIA", "Miami, Florida", "Miami International Airport", -80.290111, 25.795361);
	        AircraftType aircraft = service.createAircraft("PAA", "TinyPlane", seatRows, seatCols);
			AircraftType otherAircraft = service.createAircraft("Airbus", "A318", 22, 6);
	        f1 = service.createFlight(ap1.getIataCode(), ap2.getIataCode(), aircraft.getId());
	        f2 = service.createFlight(ap3.getIataCode(), ap2.getIataCode(), otherAircraft.getId());
	        
	        // First let's buy a couple of tickets on a different day but the same flight and then 
	        // on a different flight but the same date to ensure that the availability is being correctly computed
	        service.purchaseTicket(firstName, lastName, f1.getFlightNumber(), purchaseDate, flightDate.plusDays(1));
	        service.purchaseTicket(firstName, lastName, f2.getFlightNumber(), purchaseDate, flightDate);
	        for (int i = 0; i < tickets.length; i++) {
	        	Ticket t = service.purchaseTicket(firstName + i, lastName + i, f1.getFlightNumber(), purchaseDate, flightDate);
	        	tickets[i] = t;
	        	Assertions.assertTrue(t.getSeatColumn() > 0);
	    		Assertions.assertTrue(t.getSeatRow() > 0);
	    		Assertions.assertTrue(t.getSeatColumn() <= t.getFlight().getAircraft().getSeatColumns());
	    		Assertions.assertTrue(t.getSeatRow() <= t.getFlight().getAircraft().getSeatRows());
	    		seats.add(new Pair(t.getSeatRow(), t.getSeatColumn()));
	        }
		} catch (AirlineServiceException e) {
			Assertions.fail("There was nothing wrong with the parameters, should have created a new ticket.");
		}
		Assertions.assertEquals(1 + tickets.length, service.findFlight(tickets[0].getFlight().getFlightNumber()).getTickets().size()); // Ensure indirect list is correctly updated
		Assertions.assertEquals(tickets.length, seats.size()); // Every ticket has a distinct seat assignment	
    }
    
    @Test
    @DisplayName("Try to purchase one ticket too many in a flight")
    void tryPurchaseTooManyTickets() {
    	final int seatRows = 3;
    	final int seatCols = 2;
    	Flight f1 = null;
    	Flight f2 = null;
    	
    	LocalDate purchaseDate = LocalDate.of(2023, 4, 30);
        LocalDate flightDate = LocalDate.of(2023, 5, 1);
        String firstName = "Fulano";
        String lastName = "Fulánez";
        
		try {
			Airport ap1 = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
			Airport ap2 = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
			Airport ap3 = service.createAirport("MIA", "Miami, Florida", "Miami International Airport", -80.290111, 25.795361);
	        AircraftType aircraft = service.createAircraft("PAA", "TinyPlane", seatRows, seatCols);
			AircraftType otherAircraft = service.createAircraft("Airbus", "A318", 22, 6);
	        f1 = service.createFlight(ap1.getIataCode(), ap2.getIataCode(), aircraft.getId());
	        f2 = service.createFlight(ap3.getIataCode(), ap2.getIataCode(), otherAircraft.getId());
	        
	        // First let's buy a couple of tickets on a different day but the same flight and then 
	        // on a different flight but the same date to ensure that the availability is being correctly computed
	        service.purchaseTicket(firstName, lastName, f1.getFlightNumber(), purchaseDate, flightDate.plusDays(1));
	        service.purchaseTicket(firstName, lastName, f2.getFlightNumber(), purchaseDate, flightDate);
	        for (int i = 0; i < seatRows * seatCols; i++) {
	        	service.purchaseTicket(firstName + i, lastName + i, f1.getFlightNumber(), purchaseDate, flightDate);
	        }
		} catch (AirlineServiceException e) {
			Assertions.fail("There was nothing wrong with the parameters, should have created a new ticket.");
		}
		// The flight f1 now is full on flightDate, therefore the next try has to fail
		try {
			service.purchaseTicket(firstName, lastName, f1.getFlightNumber(), purchaseDate, flightDate);
			Assertions.fail("Cannot sell more tickets than there are seats on the plane!");
		} catch (AirlineServiceException e) { }
			
    }
    
    @Test
    @DisplayName("Try to purchase a ticket with null first name")
    void tryPurchaseTicketWithNullFirstName() {
    	final int seatRows = 3;
    	final int seatCols = 2;
    	Flight f1 = null;
    	
    	LocalDate purchaseDate = LocalDate.of(2023, 4, 30);
        LocalDate flightDate = LocalDate.of(2023, 5, 1);
        String firstName = null;
        String lastName = "Fulánez";
        
		try {
			Airport ap1 = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
			Airport ap2 = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
			AircraftType aircraft = service.createAircraft("PAA", "TinyPlane", seatRows, seatCols);
			f1 = service.createFlight(ap1.getIataCode(), ap2.getIataCode(), aircraft.getId());
		} catch (AirlineServiceException e) {
			Assertions.fail("There was nothing wrong with the parameters, should have created a new flight.");
		}
		try {
			service.purchaseTicket(firstName, lastName, f1.getFlightNumber(), purchaseDate, flightDate);
			Assertions.fail("Cannot sell a ticket to a passenger with null first name");
		} catch (AirlineServiceException e) { }
    }
    
    @Test
    @DisplayName("Try to purchase a ticket with blank first name")
    void tryPurchaseTicketWithBlankFirstName() {
    	final int seatRows = 3;
    	final int seatCols = 2;
    	Flight f1 = null;
    	
    	LocalDate purchaseDate = LocalDate.of(2023, 4, 30);
        LocalDate flightDate = LocalDate.of(2023, 5, 1);
        String firstName = "  \t ";
        String lastName = "Fulánez";
        
		try {
			Airport ap1 = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
			Airport ap2 = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
			AircraftType aircraft = service.createAircraft("PAA", "TinyPlane", seatRows, seatCols);
			f1 = service.createFlight(ap1.getIataCode(), ap2.getIataCode(), aircraft.getId());
		} catch (AirlineServiceException e) {
			Assertions.fail("There was nothing wrong with the parameters, should have created a new flight.");
		}
		try {
			service.purchaseTicket(firstName, lastName, f1.getFlightNumber(), purchaseDate, flightDate);
			Assertions.fail("Cannot sell a ticket to a passenger with blank first name");
		} catch (AirlineServiceException e) { }
    }
    
    @Test
    @DisplayName("Try to purchase a ticket with null last name")
    void tryPurchaseTicketWithNullLastName() {
    	final int seatRows = 3;
    	final int seatCols = 2;
    	Flight f1 = null;
    	
    	LocalDate purchaseDate = LocalDate.of(2023, 4, 30);
        LocalDate flightDate = LocalDate.of(2023, 5, 1);
        String firstName = "Fulano";
        String lastName = null;
        
		try {
			Airport ap1 = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
			Airport ap2 = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
			AircraftType aircraft = service.createAircraft("PAA", "TinyPlane", seatRows, seatCols);
			f1 = service.createFlight(ap1.getIataCode(), ap2.getIataCode(), aircraft.getId());
		} catch (AirlineServiceException e) {
			Assertions.fail("There was nothing wrong with the parameters, should have created a new flight.");
		}
		try {
			service.purchaseTicket(firstName, lastName, f1.getFlightNumber(), purchaseDate, flightDate);
			Assertions.fail("Cannot sell a ticket to a passenger with null last name");
		} catch (AirlineServiceException e) { }
    }
    
    @Test
    @DisplayName("Try to purchase a ticket with blank last name")
    void tryPurchaseTicketWithBlankLastName() {
    	final int seatRows = 3;
    	final int seatCols = 2;
    	Flight f1 = null;
    	
    	LocalDate purchaseDate = LocalDate.of(2023, 4, 30);
        LocalDate flightDate = LocalDate.of(2023, 5, 1);
        String firstName = "Fulano";
        String lastName = "\t    ";
        
		try {
			Airport ap1 = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
			Airport ap2 = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
			AircraftType aircraft = service.createAircraft("PAA", "TinyPlane", seatRows, seatCols);
			f1 = service.createFlight(ap1.getIataCode(), ap2.getIataCode(), aircraft.getId());
		} catch (AirlineServiceException e) {
			Assertions.fail("There was nothing wrong with the parameters, should have created a new flight.");
		}
		try {
			service.purchaseTicket(firstName, lastName, f1.getFlightNumber(), purchaseDate, flightDate);
			Assertions.fail("Cannot sell a ticket to a passenger with blank last name");
		} catch (AirlineServiceException e) { }
    }
    
    @Test
    @DisplayName("Try to purchase a ticket with null purchase date")
    void tryPurchaseTicketWithNullPurchaseDate() {
    	final int seatRows = 3;
    	final int seatCols = 2;
    	Flight f1 = null;
    	
    	LocalDate purchaseDate = null;
        LocalDate flightDate = LocalDate.of(2023, 5, 1);
        String firstName = "Fulano";
        String lastName = "Fulánez";
        
		try {
			Airport ap1 = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
			Airport ap2 = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
			AircraftType aircraft = service.createAircraft("PAA", "TinyPlane", seatRows, seatCols);
			f1 = service.createFlight(ap1.getIataCode(), ap2.getIataCode(), aircraft.getId());
		} catch (AirlineServiceException e) {
			Assertions.fail("There was nothing wrong with the parameters, should have created a new flight.");
		}
		try {
			service.purchaseTicket(firstName, lastName, f1.getFlightNumber(), purchaseDate, flightDate);
			Assertions.fail("Cannot sell a ticket to a passenger on a null purchase date");
		} catch (AirlineServiceException e) { }
    }
    
    @Test
    @DisplayName("Try to purchase a ticket with null flight date")
    void tryPurchaseTicketWithNullFlightDate() {
    	final int seatRows = 3;
    	final int seatCols = 2;
    	Flight f1 = null;
    	
    	LocalDate purchaseDate = LocalDate.of(2023, 4, 30);
        LocalDate flightDate = null;
        String firstName = "Fulano";
        String lastName = "Fulánez";
        
		try {
			Airport ap1 = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
			Airport ap2 = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
			AircraftType aircraft = service.createAircraft("PAA", "TinyPlane", seatRows, seatCols);
			f1 = service.createFlight(ap1.getIataCode(), ap2.getIataCode(), aircraft.getId());
		} catch (AirlineServiceException e) {
			Assertions.fail("There was nothing wrong with the parameters, should have created a new flight.");
		}
		try {
			service.purchaseTicket(firstName, lastName, f1.getFlightNumber(), purchaseDate, flightDate);
			Assertions.fail("Cannot sell a ticket to a passenger for a null flight date");
		} catch (AirlineServiceException e) { }
    }
    
    @Test
    @DisplayName("Try to purchase a ticket in the past")
    void tryPurchaseTicketInThePast() {
    	final int seatRows = 3;
    	final int seatCols = 2;
    	Flight f1 = null;
    	
    	LocalDate purchaseDate = LocalDate.of(2023, 5, 1);
        LocalDate flightDate = purchaseDate.minusDays(1);
        String firstName = "Fulano";
        String lastName = "Fulánez";
        
		try {
			Airport ap1 = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
			Airport ap2 = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
			AircraftType aircraft = service.createAircraft("PAA", "TinyPlane", seatRows, seatCols);
			f1 = service.createFlight(ap1.getIataCode(), ap2.getIataCode(), aircraft.getId());
		} catch (AirlineServiceException e) {
			Assertions.fail("There was nothing wrong with the parameters, should have created a new flight.");
		}
		try {
			service.purchaseTicket(firstName, lastName, f1.getFlightNumber(), purchaseDate, flightDate);
			Assertions.fail("Cannot sell a ticket for a past flight date");
		} catch (AirlineServiceException e) { }
    }
    
    @Test
    @DisplayName("Purchase a ticket in the date of the flight")
    void tryPurchaseLastMinuteTicket() {
    	final int seatRows = 3;
    	final int seatCols = 2;
    	Flight f1 = null;
    	
    	LocalDate purchaseDate = LocalDate.of(2023, 5, 1);
        LocalDate flightDate = purchaseDate;
        String firstName = "Fulano";
        String lastName = "Fulánez";
        
		try {
			Airport ap1 = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
			Airport ap2 = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
			AircraftType aircraft = service.createAircraft("PAA", "TinyPlane", seatRows, seatCols);
			f1 = service.createFlight(ap1.getIataCode(), ap2.getIataCode(), aircraft.getId());
		} catch (AirlineServiceException e) {
			Assertions.fail("There was nothing wrong with the parameters, should have created a new flight.");
		}
		try {
			service.purchaseTicket(firstName, lastName, f1.getFlightNumber(), purchaseDate, flightDate);
		} catch (AirlineServiceException e) { 
			Assertions.fail("There's nothing wrong with buying a ticket for the same day, should have created ticket.");
		}
    }
    
    @Test
    @DisplayName("Try to purchase a ticket on a null flight")
    void tryPurchaseTicketOnANullFlight() {
    	LocalDate purchaseDate = LocalDate.of(2023, 5, 1);
        LocalDate flightDate = purchaseDate.plusDays(1);
        String firstName = "Fulano";
        String lastName = "Fulánez";

		try {
			service.purchaseTicket(firstName, lastName, null, purchaseDate, flightDate);
			Assertions.fail("Cannot sell a ticket on a null flight");
		} catch (AirlineServiceException e) { }
    }
    
    @Test
    @DisplayName("Try to purchase a ticket on a non-existing flight")
    void tryPurchaseTicketOnANonExistingFlight() {
    	LocalDate purchaseDate = LocalDate.of(2023, 5, 1);
        LocalDate flightDate = purchaseDate.plusDays(1);
        String firstName = "Fulano";
        String lastName = "Fulánez";

		try {
			service.purchaseTicket(firstName, lastName, 666L, purchaseDate, flightDate);
			Assertions.fail("Cannot sell a ticket on a non-existing flight");
		} catch (AirlineServiceException e) { }
    }
    
    @Test
    @DisplayName("Test availableSeats")
    void testAvailableSeats() {
    	final int seatRows = 3;
    	final int seatCols = 2;
    	Ticket[] tickets = new Ticket[seatRows * seatCols];
    	Flight f1 = null;
    	Flight f2 = null;
    	
    	LocalDate purchaseDate = LocalDate.of(2023, 4, 30);
        LocalDate flightDate = LocalDate.of(2023, 5, 1);
        String firstName = "Fulano";
        String lastName = "Fulánez";
        
		try {
			Airport ap1 = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
			Airport ap2 = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
			Airport ap3 = service.createAirport("MIA", "Miami, Florida", "Miami International Airport", -80.290111, 25.795361);
	        AircraftType aircraft = service.createAircraft("PAA", "TinyPlane", seatRows, seatCols);
			AircraftType otherAircraft = service.createAircraft("Airbus", "A318", 22, 6);
	        f1 = service.createFlight(ap1.getIataCode(), ap2.getIataCode(), aircraft.getId());
	        f2 = service.createFlight(ap3.getIataCode(), ap2.getIataCode(), otherAircraft.getId());
	        
	        // First let's buy a couple of tickets on a different day but the same flight and then 
	        // on a different flight but the same date to ensure that the availability is being correctly computed
	        service.purchaseTicket(firstName, lastName, f1.getFlightNumber(), purchaseDate, flightDate.plusDays(1));
	        service.purchaseTicket(firstName, lastName, f2.getFlightNumber(), purchaseDate, flightDate);
	        for (int i = 0; i < tickets.length; i++) {
	        	Assertions.assertEquals(tickets.length - i, service.availableSeats(f1.getFlightNumber(), flightDate));
	        	Ticket t = service.purchaseTicket(firstName + i, lastName + i, f1.getFlightNumber(), purchaseDate, flightDate);
	        	tickets[i] = t;
	        }
	        Assertions.assertEquals(0, service.availableSeats(f1.getFlightNumber(), flightDate));
		} catch (AirlineServiceException e) {
			Assertions.fail("There was nothing wrong with the parameters, should have created a new ticket.");
		}	
    }

    @Test
    @DisplayName("Cancel a ticket with correct parameters")
    void tryCancelTicket() {
    	Ticket t = null;
    	Flight f = null;
    	
    	LocalDate purchaseDate = LocalDate.of(2023, 4, 30);
    	LocalDate cancelDate = purchaseDate.plusDays(1);
        LocalDate flightDate = cancelDate.plusDays(1);
        String firstName = "Fulano";
        String lastName = "Fulánez";
        
		try {
			Airport origin = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
			Airport destination = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
	        AircraftType aircraft = service.createAircraft("Airbus", "A318", 22, 6);
	        f = service.createFlight(origin.getIataCode(), destination.getIataCode(), aircraft.getId());
	        
	        t = service.purchaseTicket(firstName, lastName, f.getFlightNumber(), purchaseDate, flightDate);
		} catch (AirlineServiceException e) {
			Assertions.fail("There was nothing wrong with the parameters, should have created a new ticket.");
		}
		try {
			service.cancelTicket(t.getTicketNumber(), cancelDate);
		} catch (AirlineServiceException e) {
			Assertions.fail("Should have cancelled the ticket, all parameters were correct.");
		}	
		Assertions.assertEquals(0, service.findFlight(f.getFlightNumber()).getTickets().size()); // Ensure indirect list is correctly updated
    }
    
    @Test
    @DisplayName("Try to cancel a ticket after the flight date")
    void tryCancelTicketAfterFlight() {
    	Ticket t = null;
    	Flight f = null;
    	
    	LocalDate purchaseDate = LocalDate.of(2023, 4, 30);
    	LocalDate flightDate = purchaseDate.plusDays(1);
    	LocalDate cancelDate = flightDate.plusDays(1);
        
        String firstName = "Fulano";
        String lastName = "Fulánez";
        
		try {
			Airport origin = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
			Airport destination = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
	        AircraftType aircraft = service.createAircraft("Airbus", "A318", 22, 6);
	        f = service.createFlight(origin.getIataCode(), destination.getIataCode(), aircraft.getId());
	        
	        t = service.purchaseTicket(firstName, lastName, f.getFlightNumber(), purchaseDate, flightDate);
		} catch (AirlineServiceException e) {
			Assertions.fail("There was nothing wrong with the parameters, should have created a new ticket.");
		}
		try {
			service.cancelTicket(t.getTicketNumber(), cancelDate);
			Assertions.fail("The cancel date was after the flight, the ticket should have not been cancelled.");
		} catch (AirlineServiceException e) { }	
		Assertions.assertEquals(1, service.findFlight(f.getFlightNumber()).getTickets().size()); // Ensure the ticket is not actually deleted
    }
    
    @Test
    @DisplayName("Try to cancel a ticket on the flight date")
    void tryCancelTicketLastMinute() {
    	Ticket t = null;
    	Flight f = null;
    	
    	LocalDate purchaseDate = LocalDate.of(2023, 4, 30);
    	LocalDate flightDate = purchaseDate.plusDays(1);
    	LocalDate cancelDate = flightDate;
        
        String firstName = "Fulano";
        String lastName = "Fulánez";
        
		try {
			Airport origin = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
			Airport destination = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
	        AircraftType aircraft = service.createAircraft("Airbus", "A318", 22, 6);
	        f = service.createFlight(origin.getIataCode(), destination.getIataCode(), aircraft.getId());
	        
	        t = service.purchaseTicket(firstName, lastName, f.getFlightNumber(), purchaseDate, flightDate);
		} catch (AirlineServiceException e) {
			Assertions.fail("There was nothing wrong with the parameters, should have created a new ticket.");
		}
		try {
			service.cancelTicket(t.getTicketNumber(), cancelDate);
			Assertions.fail("The cancel date was the same as the flight, the ticket should have not been cancelled.");
		} catch (AirlineServiceException e) { }	
		Assertions.assertEquals(1, service.findFlight(f.getFlightNumber()).getTickets().size()); // Ensure the ticket is not actually deleted
    }
    
    @Test
    @DisplayName("Try to cancel a ticket with null number")
    void tryCancelTicketWithNullNumber() {
    	Ticket t = null;
    	Flight f = null;
    	
    	LocalDate purchaseDate = LocalDate.of(2023, 4, 30);
    	LocalDate cancelDate = purchaseDate.plusDays(1);
        LocalDate flightDate = cancelDate.plusDays(1);
        
        String firstName = "Fulano";
        String lastName = "Fulánez";
        
		try {
			Airport origin = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
			Airport destination = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
	        AircraftType aircraft = service.createAircraft("Airbus", "A318", 22, 6);
	        f = service.createFlight(origin.getIataCode(), destination.getIataCode(), aircraft.getId());
	        
	        t = service.purchaseTicket(firstName, lastName, f.getFlightNumber(), purchaseDate, flightDate);
		} catch (AirlineServiceException e) {
			Assertions.fail("There was nothing wrong with the parameters, should have created a new ticket.");
		}
		try {
			service.cancelTicket(null, cancelDate);
			Assertions.fail("Cannot delete a ticket with null number.");
		} catch (AirlineServiceException e) { }	
		Assertions.assertEquals(1, service.findFlight(f.getFlightNumber()).getTickets().size()); // Ensure the ticket is not actually deleted
    }
    
    @Test
    @DisplayName("Try to cancel a non-existing ticket")
    void tryCancelNonExistingTicket() {
    	Ticket t = null;
    	Flight f = null;
    	
    	LocalDate purchaseDate = LocalDate.of(2023, 4, 30);
    	LocalDate cancelDate = purchaseDate.plusDays(1);
        LocalDate flightDate = cancelDate.plusDays(1);
        
        String firstName = "Fulano";
        String lastName = "Fulánez";
        
		try {
			Airport origin = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
			Airport destination = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
	        AircraftType aircraft = service.createAircraft("Airbus", "A318", 22, 6);
	        f = service.createFlight(origin.getIataCode(), destination.getIataCode(), aircraft.getId());
	        
	        t = service.purchaseTicket(firstName, lastName, f.getFlightNumber(), purchaseDate, flightDate);
		} catch (AirlineServiceException e) {
			Assertions.fail("There was nothing wrong with the parameters, should have created a new ticket.");
		}
		try {
			service.cancelTicket(t.getTicketNumber() + 1L, cancelDate);
			Assertions.fail("The ticket does not exist and therefore cannot be cancelled.");
		} catch (AirlineServiceException e) { }
		f.getTickets().get(0); 
		
		System.out.println(service.findFlight(f.getFlightNumber()).getTickets().toString());
		Assertions.assertEquals(1, service.findFlight(f.getFlightNumber()).getTickets().size()); // Ensure the ticket is not actually deleted
    }
    
    @Test
    @DisplayName("Try to cancel a ticket on a null date")
    void tryCancelTicketNullDate() {
    	Ticket t = null;
    	Flight f = null;
    	
    	LocalDate purchaseDate = LocalDate.of(2023, 4, 30);
    	LocalDate cancelDate = purchaseDate.plusDays(1);
        LocalDate flightDate = cancelDate.plusDays(1);
        
        String firstName = "Fulano";
        String lastName = "Fulánez";
        
		try {
			Airport origin = service.createAirport("MAD", "Madrid", "Barajas International Airport", -3.566764, 40.493556);
			Airport destination = service.createAirport("LHR", "London", "London Heathrow Airport", -0.461389, 51.477500);
	        AircraftType aircraft = service.createAircraft("Airbus", "A318", 22, 6);
	        f = service.createFlight(origin.getIataCode(), destination.getIataCode(), aircraft.getId());
	        
	        t = service.purchaseTicket(firstName, lastName, f.getFlightNumber(), purchaseDate, flightDate);
		} catch (AirlineServiceException e) {
			Assertions.fail("There was nothing wrong with the parameters, should have created a new ticket.");
		}
		try {
			service.cancelTicket(t.getTicketNumber(), null);
			Assertions.fail("Cannot cancel a ticket on a null date!");
		} catch (AirlineServiceException e) { }	
		Assertions.assertEquals(1, service.findFlight(f.getFlightNumber()).getTickets().size()); // Ensure the ticket is not actually deleted
    }


    // To check seat assignations
    class Pair {
    	public int x, y;
    	public Pair(int x, int y) { this.x = x; this.y = y; }
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Objects.hash(x, y);
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pair other = (Pair) obj;
			return x == other.x && y == other.y;
		}    	
    }
}
