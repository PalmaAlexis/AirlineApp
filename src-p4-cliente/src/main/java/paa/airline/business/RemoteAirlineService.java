package paa.airline.business;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import paa.airline.model.AircraftType;
import paa.airline.model.Airport;
import paa.airline.model.Flight;
import paa.airline.model.Ticket;

public class RemoteAirlineService  implements AirlineService{
	private final String URLbase= "http://localhost:8080/p4-servidor/AirlineServer?action=";
	private ObjectMapper mapper;
	//LISTO
	public RemoteAirlineService() {
		mapper=new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
	}
	//LISTO
	private static String askURL(String url) throws AirlineServiceException {
		System.out.println(LocalDateTime.now()+ ": "+url);	
		try {
			HttpURLConnection connection= (HttpURLConnection) new URL(url).openConnection();
			int codeConnection= connection.getResponseCode();
			
			if(200==codeConnection) {
				String resp= new String(connection.getInputStream().readAllBytes(), Charset.forName("UTF-8"));
				return resp;
			}else {
				throw new AirlineServiceException("There is a problem connecting to: "+url);
			}
		} catch (IOException e) {
			throw new AirlineServiceException("There is a problem connecting to: "+url);
		}
	}
    //LISTO
	@Override
	public Airport createAirport(String iataCode, String cityName, String airportName, double longitude,
			double latitude) throws AirlineServiceException {
		@SuppressWarnings("deprecation")
		String URL= URLbase+"createAirport&iataCode="+ iataCode +"&cityName="+ URLEncoder.encode(cityName)
		+"&airportName="+ URLEncoder.encode(airportName) +"&longitude="+ longitude +"&latitude="+ latitude;
		
		String response= askURL(URL);
		try {
			Airport a=mapper.readValue(response, Airport.class);
			return a;
		}catch (JsonParseException e) {
			throw new AirlineServiceException(response);
		}catch (JsonMappingException e) {
			throw new AirlineServiceException("Unexpected error: "+e.getMessage());
		}catch (JsonProcessingException e) {
			throw new AirlineServiceException("Unexpected error: "+e.getMessage());
		}
	}
	//LISTO
	@Override
	public List<Airport> listAirports() {
		String URL= URLbase+"listAirports";
		try {
			String response= askURL(URL);
			List<Airport> lA= mapper.readValue(response, new TypeReference< List<Airport> >() {} );
			return lA;
		} catch (Exception e) {
			return new ArrayList<Airport>();
		}
	}
	//LISTO
	@Override
	public AircraftType createAircraft(String manufacturer, String model, int seatRows, int seatColumns)
			throws AirlineServiceException {
		@SuppressWarnings("deprecation")
		String URL= URLbase+"createAircraft&manufacturer="+URLEncoder.encode(manufacturer)+"&model="+URLEncoder.encode(model)
				+"&seatRows="+seatRows+"&seatColumns="+seatColumns;
		String response= askURL(URL);
		try {
			AircraftType at= mapper.readValue(response, AircraftType.class);
			return at;
		}catch (JsonParseException e) {
			throw new AirlineServiceException(response);
		}catch (JsonMappingException e) {
			throw new AirlineServiceException("Unexpected error: "+e.getMessage());
		}catch (JsonProcessingException e) {
			throw new AirlineServiceException("Unexpected error:" +e.getMessage());
		}
	}
	//LISTO
	@Override
	public List<AircraftType> listAircraftTypes() {
		String URL= URLbase+"listAircraftTypes";
		try {
			String response= askURL(URL);
			List<AircraftType> LAt= mapper.readValue(response, new TypeReference<List<AircraftType>>(){});
			return LAt;
		} catch (Exception e) {
			return new ArrayList<AircraftType>();
		}
	}
	//LISTO
	@Override
	public Flight createFlight(String originAirportCode, String destinationAirportCode, Long aircraftTypeCode)
			throws AirlineServiceException {
		String URL= URLbase+ "createFlight&originAirportCode="+originAirportCode+"&destinationAirportCode="+destinationAirportCode
				+"&aircraftTypeCode="+aircraftTypeCode;
		String response= askURL(URL);
		try {
			Flight f = mapper.readValue(response, Flight.class);
			return f;
		}catch (JsonParseException e) {
			throw new AirlineServiceException(response);
		}catch (JsonMappingException e) {
			throw new AirlineServiceException("Unexpected error: "+e.getMessage());
		}catch (JsonProcessingException e) {
			throw new AirlineServiceException("Unexpected error: "+e.getMessage());
		}
	}
	//LISTO
	@Override
	public Flight findFlight(Long flightNumber) {
		String URL= URLbase+"findFlight&flightNumber="+flightNumber;
		try {
			String response= askURL(URL);
			Flight f= mapper.readValue(response, Flight.class);
			return f;
		} catch (Exception e) {
			return null;        
		}
	}
	//LISTO
	@Override
	public List<Flight> listFlights() {
		String URL= URLbase+"listFlights";
		try {
			String response= askURL(URL);
			List<Flight> LF= mapper.readValue(response, new TypeReference<List<Flight>>() {});
			return LF;
		} catch (Exception e) {
			return new ArrayList<Flight>();
		}
	}
	//LISTO
	@Override
	public Ticket purchaseTicket(String firstName, String lastName, Long flightNumber, LocalDate purchaseDate,
			LocalDate flightDate) throws AirlineServiceException {
		@SuppressWarnings("deprecation")
		String URL= URLbase+"purchaseTicket&firstName="+URLEncoder.encode(firstName)+"&lastName="+URLEncoder.encode(lastName)
				+"&flightNumber="+flightNumber+"&purchaseDate="+purchaseDate+"&flightDate="+flightDate;
		String response= askURL(URL);		
		try {
			Ticket t= mapper.readValue(response, Ticket.class);
			return t;
		}catch (JsonParseException e) {
			throw new AirlineServiceException(response);
		}catch (JsonMappingException e) {
			throw new AirlineServiceException("Unexpected error: "+e.getMessage());
		}catch (JsonProcessingException e) {
			throw new AirlineServiceException("Unexpected error: "+e.getMessage()); 
		}
	}
	//LISTO
	@Override
	public int availableSeats(Long flightNumber, LocalDate flightDate) throws AirlineServiceException {
		String URL= URLbase+"availableSeats&flightNumber="+flightNumber+"&flightDate="+flightDate;
		String response= askURL(URL);
		try {
			int A= mapper.readValue(response, int.class);
			return A;
		}catch (JsonParseException e) {
			throw new AirlineServiceException(response);
		}catch (JsonMappingException e) {
			throw new AirlineServiceException("Unexpected error: "+e.getMessage());
		}catch (JsonProcessingException e) {
			throw new AirlineServiceException("Unexpected error: "+e.getMessage());
		}
	}
	//LISTO
	@Override
	public void cancelTicket(Long ticketNumber, LocalDate cancelDate) throws AirlineServiceException {
		String URL= URLbase+"cancelTicket&ticketNumber="+ticketNumber+"&cancelDate="+cancelDate;
		String response= askURL(URL);	
		try {
			mapper.readValue(response, void.class);
		}catch (JsonParseException e) {
			throw new AirlineServiceException(response);
		}catch (JsonMappingException e) {
			throw new AirlineServiceException("Unexpected error: "+e.getMessage());
		} catch (JsonProcessingException e) {
			throw new AirlineServiceException("Unexpected error: "+e.getMessage());
		}
	}
}
