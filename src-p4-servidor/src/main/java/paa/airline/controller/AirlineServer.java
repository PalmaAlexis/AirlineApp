package paa.airline.controller;

import java.io.DataOutput;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import paa.airline.business.AirlineServiceException;
import paa.airline.business.JPAAirlineService;
import paa.airline.model.AircraftType;
import paa.airline.model.Airport;
import paa.airline.model.Flight;
import paa.airline.model.Ticket;

@WebServlet("/AirlineServer")
public class AirlineServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private JPAAirlineService servicio;
	ObjectMapper mapper;
       
    //LISTO    
    public AirlineServer() {
        super();
        mapper= new ObjectMapper();   //OBJETO SERIALIZADOR 
        mapper.registerModule(new JavaTimeModule());  //Esto solo se hace cuando se va a serializar fechas
    }
    //LISTO    
    public void init(ServletConfig config) throws ServletException {
		super.init();
		try {
			servicio= new JPAAirlineService("paa-jpa", "bdatos");    
			System.out.println("Se ha cargado el servlet");
		}catch (Exception e) {
			System.out.println("Error al cargar el servlet");
			throw new ServletException(e);
		}
	}
    //LISTO
	@Override
	public void destroy() {
		servicio.close();
		servicio=null;
		super.destroy();
		System.out.println("Se ha destruido el servlet");

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    //LISTO	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String accion= request.getParameter("action");
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");             //NORMAS PRINCIPALES PARA QUE RECONOZCA EL TIPADO 
		
		if(null!=accion) {
			//SWITCH PARA LAS ACCIONES 
			switch (accion) {
			case "createAirport":
				createAirport(request,response);
				break;
			case "listAirports":
				listAirports(response);
				break;
			case "createAircraft":
				createAircraft(request,response);
				break;
			case "listAircraftTypes":
				listAircraftTypes(response);
				break;
			case "createFlight":
				createFlight(request,response);
				break;
			case "findFlight":
				findFlight(request,response);
				break;
			case "listFlights":
				listFlights(response);
				break;
			case "purchaseTicket":
				purchaseTicket(request,response);
				break;
			case "availableSeats":
				availableSeats(request,response);
				break;
			case "cancelTicket":
				cancelTicket(request,response);
				break;
			default:
				response.sendError(400, "Action not found");
				break;
			}		
		}else {
			response.sendError(400, "Invalid URL: Missing parameter 'action'");
		}
	}
	//LISTO
	protected void createAirport(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String iataCode= request.getParameter("iataCode");
		String cityName= request.getParameter("cityName");
		String airportName= request.getParameter("airportName");
		String longitudeString= request.getParameter("longitude");
		String latitudeString= request.getParameter("latitude");
		
		if(null==iataCode) { 
			response.sendError(400, "Invalid URL: missing parameter 'iataCode'");
			return;     //MANDA EL ERROR Y CORTA LA FUNCION CON EL RETURN (EL RETURN NO RETORNA NADA PORQUE LA FUNCION ES VOID)
		}
		if(null==cityName) {
			response.sendError(400, "Invalid URL: missing parameter 'cityName'");
			return;
		}
		if(null==airportName) {
			response.sendError(400, "Invalid URL: missing parameter 'airportName'");
			return;
		}
		if(null==longitudeString) {
			response.sendError(400, "Invalid URL: missing parameter 'longitude'");
			return;
		}
		if(null==latitudeString) {
			response.sendError(400, "Invalid URL: missing parameter 'latitude'");
			return;
		}
		
		Double longitude;
		try {
			longitude=Double.parseDouble(longitudeString);
		}catch (NumberFormatException e) {
			response.sendError(400, "Invalid URL: longitude NOT valida");
			return;
		}
		
		Double latitude;
		try {
			latitude= Double.parseDouble(latitudeString);
		}catch (NumberFormatException e) {
			response.sendError(400, "Invalid URL: latitude NOT valid");
			return;
		}
	
		try {
			Airport a = servicio.createAirport(iataCode, cityName, airportName, longitude, latitude);
			mapper.writeValue(response.getWriter(),a);     //  (WRITER, VALUE)
		} catch (AirlineServiceException e) {
			response.getWriter().println(e.getMessage());     //Aqui ya no se lanza ningun error, PORQUE YA NO ES ERROR DEL LINK. SI NO DEL SERVICIO.
		}
	}
    //LISTO
	protected void listAirports(HttpServletResponse response) throws IOException {
    	mapper.writeValue(response.getWriter(), servicio.listAirports());
	}
	//LISTO
    protected void createAircraft(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	String manufacturer= request.getParameter("manufacturer");
    	String model= request.getParameter("model");
    	String seatRowsString= request.getParameter("seatRows");
    	String seatColumnsString= request.getParameter("seatColumns");
    	
    	if(null==manufacturer) {
    		response.sendError(400, "Invalid URL: missing parameter 'manufacturer'");
    		return;
    	}
    	if(null==model) {
    		response.sendError(400, "Invalid URL: missing parameter 'model'");
    		return;
    	}
    	if(null==seatRowsString) {
    		response.sendError(400, "Invalid URL: missing parameter 'seatRows'");
    		return;
    	}
    	if(null==seatColumnsString) {
    		response.sendError(400, "Invalid URL: missing parameter 'seatColumns'");
    		return;
    	}
    	
    	int seatRows;
    	try {
    		seatRows= Integer.parseInt(seatRowsString);
    	}catch (NumberFormatException e) {
    		response.sendError(400, "Invalid URL: 'seatRows' NOT valid");
    		return;
		}
    	
    	int seatColumns;
    	try {
    		seatColumns= Integer.parseInt(seatColumnsString);
    	}catch (NumberFormatException e) {
    		response.sendError(400, "Invalid URL: 'seatColumns' NOT valid");
    		return;
		}
    	
    	try {
    		AircraftType at= servicio.createAircraft(manufacturer, model, seatRows, seatColumns);
    		mapper.writeValue(response.getWriter(), at);//al final este solo sirve para convertir a JSON, no guarda ni nada, solo convierte a JSON y ya
    	}catch (AirlineServiceException e) {
    		response.getWriter().println(e.getMessage());
		}
    	
    }
    //LISTO      
    protected void listAircraftTypes(HttpServletResponse response) throws IOException{
    	mapper.writeValue(response.getWriter(), servicio.listAircraftTypes());
    }
    //LISTO 
    protected void createFlight(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String originAirportCode=request.getParameter("originAirportCode");
		String destinationAirportCode=request.getParameter("destinationAirportCode");
		String aircraftTypeCodeString = request.getParameter("aircraftTypeCode");
		
		if(null==originAirportCode) {
			response.sendError(400, "Invalid URL: missing parameter 'originAirportCode'");
			return;
		}
		
		if(null==destinationAirportCode) {
			response.sendError(400, "Invalid URL: missing parameter 'destinationAirportCode'");
			return;
		}
		
		if(null==aircraftTypeCodeString) {
			response.sendError(400, "Invalid URL: missing parameter 'aircraftTypeCode'");
			return;
		}
		
		Long aircraftTypeCode;
		try {
			aircraftTypeCode=Long.parseLong(aircraftTypeCodeString);
		}catch (NumberFormatException e) {
			response.sendError(400, "Invalid URL: 'aircraftTypeCode' NOT valid");
			return;
		}
		
		try {
			Flight f= servicio.createFlight(originAirportCode, destinationAirportCode, aircraftTypeCode);
			mapper.writeValue(response.getWriter(), f);
		}catch (AirlineServiceException e) {
			response.getWriter().println(e.getMessage());
		}
		
		
	}
    //LISTO
    protected void findFlight(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	String flightNumberString= request.getParameter("flightNumber");
    	
    	if(null==flightNumberString) {
    		response.sendError(400, "Invalid URL: missing parameter 'flightNumber'");
    		return;
    	}
    	
    	Long flightNumber;
    	try {
    		flightNumber= Long.parseLong(flightNumberString);
    	}catch (NumberFormatException e) {
    		response.sendError(400, "Invalid URL: 'flightNumber' NOT valid");
    		return;
		}
    	
    	Flight f= servicio.findFlight(flightNumber);
		
		if(null==f) {
			response.getWriter().println("Flight with number ["+flightNumber+"] does NOT exist");
		}else {
			mapper.writeValue(response.getWriter(), f);
		}
    	
    }
    //LISTO
    protected void listFlights(HttpServletResponse response) throws IOException{
    	mapper.writeValue(response.getWriter(), servicio.listFlights());
    	}
    //LISTO
    protected void purchaseTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	String firstName= request.getParameter("firstName");
    	String lastName= request.getParameter("lastName");
    	String flightNumberString= request.getParameter("flightNumber");
    	String purchaseDateString= request.getParameter("purchaseDate");
    	String flightDateString= request.getParameter("flightDate");
    	
    	if(null==firstName) {
    		response.sendError(400, "Invalid URL: missing parameter 'firstName'");
    		return;
    	}
    	if(null==lastName) {
    		response.sendError(400, "Invalid URL: missing parameter 'lastName'");
    		return;
    	}
    	if(null==flightNumberString) {
    		response.sendError(400, "Invalid URL: missing parameter 'flightNumber'");
    		return;
    	}
    	if(null==purchaseDateString) {
    		response.sendError(400, "Invalid URL: missing parameter 'purchaseDate'");
    		return;
    	}
    	if(null==flightDateString) {
    		response.sendError(400, "Invalid URL: missing parameter 'flightDate'");
    		return;
    	}
    	
    	Long flightNumber;
    	try {
    		flightNumber= Long.parseLong(flightNumberString);
    	}catch (NumberFormatException e) {
    		response.sendError(400, "Invalid URL: parameter 'flightNumber' NOT valid");
    		return;
		}
    	
    	LocalDate purchaseDate;
    	try {
    		purchaseDate=LocalDate.parse(purchaseDateString);
    	}catch (DateTimeParseException e) {
    		response.sendError(400, "Invalid URL: parameter 'purchaseDate' NOT valid");
    		return;
		}
    	
    	LocalDate flightDate;
    	try {
    		flightDate= LocalDate.parse(flightDateString);
    	}catch (DateTimeParseException e) {
    		response.sendError(400, "Invalid URL: parameter 'flightDate' NOT valid");
    		return;
		}
    	
    	try {
    		Ticket t= servicio.purchaseTicket(firstName, lastName, flightNumber, purchaseDate, flightDate);
    		mapper.writeValue(response.getWriter(), t);
    	}catch (AirlineServiceException e) {
    		response.getWriter().println(e.getMessage());
		}
    	
    	
    }
    //LISTO
    protected void availableSeats(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	String flightNumberString= request.getParameter("flightNumber");
    	String flightDateString= request.getParameter("flightDate");
    	
    	if(null==flightNumberString) {
    		response.sendError(400, "Invalid URL: missing parameter 'flightNumber'");
    		return;
    	}
    	if(null==flightDateString) {
    		response.sendError(400, "Invalid URL: missing parameter 'flightDate'");
    		return;
    	}
    	
    	Long flightNumber;
    	try {
    		flightNumber= Long.parseLong(flightNumberString);
    	}catch (NumberFormatException e) {
    		response.sendError(400, "Invalid URL: parameter 'flightNumber' NOT valid");
    		return;
		}
    	
    	LocalDate flightDate;
    	try {
    		flightDate= LocalDate.parse(flightDateString);
    	}catch (DateTimeParseException e) {
    		response.sendError(400, "Invalid URL: parameter 'flightDate' NOT valid");
    		return;
		}
    	
    	try {
    		mapper.writeValue(response.getWriter(), servicio.availableSeats(flightNumber, flightDate));
    	}catch (AirlineServiceException e) {
    		response.getWriter().println(e.getMessage());
		}
    	
    	
    	
    	
    }
    //LISTO
    protected void cancelTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	String ticketNumberString= request.getParameter("ticketNumber");
    	String cancelDateString= request.getParameter("cancelDate");
    	
    	if(null==ticketNumberString) {
    		response.sendError(400, "Invalid URL: missing parameter 'ticketNumber'");
    		return;
    	}
    	if(null==cancelDateString) {
    		response.sendError(400, "Invalid URL: missing parameter 'cancelDate'");
    		return;
    	}
    	
    	Long ticketNumber;
    	try {
    		ticketNumber= Long.parseLong(ticketNumberString);
    		mapper.writeValue(response.getWriter(), null);
    	}catch (NumberFormatException e) {
    		response.sendError(400, "Invalid URL: parameter 'ticketNumer' NOT valid");
    		return;
		}
    	
    	LocalDate cancelDate;
    	try {
    		cancelDate=LocalDate.parse(cancelDateString);
    	}catch (DateTimeParseException e) {
    		response.sendError(400, "Invalid URL: parameter 'cancelDate' NOT valid");
    		return;
		}
    	
    	
    	try {
    		servicio.cancelTicket(ticketNumber, cancelDate);
    	}catch (AirlineServiceException e) {
    		response.getWriter().println(e.getMessage());
		}
    	
    }
//LISTAR AIRPORTS
//http://localhost:8080/p4-servidor/AirlineServer?action=listAirports
//CREAR AIRPORT
//http://localhost:8080/p4-servidor/AirlineServer?action=createAirport&iataCode=BCN&cityName=Barcelona&airportName=El+Prat+Airport&longitude=2.0746423&latitude=41.289182
//CREAR AIRCRAFT
//http://localhost:8080/p4-servidor/AirlineServer?action=createAircraft&manufacturer=Airbus&model=A320&seatRows=6&seatColumns=4
//LISTAR AIRCRAFTS
//http://localhost:8080/p4-servidor/AirlineServer?action=listAircraftTypes
//CREAR FLIGHT
//http://localhost:8080/p4-servidor/AirlineServer?action=createFlight&originAirportCode=MAD&destinationAirportCode=BCN&aircraftTypeCode=1
//FIND FLIGHT
//http://localhost:8080/p4-servidor/AirlineServer?action=findFlight&flightNumber=2
//LISTAR FLIGHTS
//http://localhost:8080/p4-servidor/AirlineServer?action=listFlights
//COMPRAR TICKET
//http://localhost:8080/p4-servidor/AirlineServer?action=purchaseTicket&firstName=Luis&lastName=Lopez+Perez&flightNumber=2&purchaseDate=2023-04-19&flightDate=2023-04-21
//ASIENTOS DISPONIBLES
//http://localhost:8080/p4-servidor/AirlineServer?action=availableSeats&flightNumber=2&flightDate=2023-04-21
//CANCELAR TICKET
//http://localhost:8080/p4-servidor/AirlineServer?action=cancelTicket&ticketNumber=201&cancelDate=2023-04-20
    
    
    
    
    //AL FINAL EL SERVIDOR P4_CLIENTE NO ES PARA REEMPLAZAR A AIRLINE SERVER. MAS BIEN LO IMPLEMENTA Y AL QUE REEMPLZA ES A JPASERVER, SIN CREAR LOS METODOS, MAS BIEN SOLO BAJANDOLOS DE LOS RESPECTIVOS URL.     ASI JPA SOLO SE QUEDA EN EL SERVIDOR MAIN Y LOS CLIENTES PUEDEN HACER LAS MISMAS MODIFICACIONES!!!!!!!
}
