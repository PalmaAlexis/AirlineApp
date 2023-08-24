package paa.airline.business;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import paa.airline.model.AircraftType;
import paa.airline.model.Airport;
import paa.airline.model.Flight;
import paa.airline.model.Ticket;
import paa.airline.persistence.AircraftTypeJPADAO;
import paa.airline.persistence.AirportJPADAO;
import paa.airline.persistence.FlightJPADAO;
import paa.airline.persistence.TicketJPADAO;
import paa.airline.util.AirportQuery;

public class JPAAirlineService implements AirlineService {
	private String persistenceUnit;
	private EntityManagerFactory emf;
	private EntityManager em;
	private String path=null;
	
	public JPAAirlineService(String persistenceUnit) {
		this.persistenceUnit = persistenceUnit;
	}
	
	public JPAAirlineService(String persistenceUnit,String path) {
		this.persistenceUnit = persistenceUnit;
		this.path=path;
	}
	
	//ASEGURA DE QUE HAY UN EMF O EM ACTIVOS
	private void ensureActiveManager() { 
		if(this.emf==null || !this.emf.isOpen()) { 
			if (null==path) {
				this.emf = Persistence.createEntityManagerFactory(persistenceUnit);
			} else {
				Map<String, String> properties = new HashMap<String, String>();
				properties.put("javax.persistence.jdbc.url", "jdbc:derby:" + path + ";create=true");
				this.emf = Persistence.createEntityManagerFactory(persistenceUnit, properties);
			}
		}
		if(this.em==null || !this.em.isOpen()) {
			this.em=this.emf.createEntityManager(); //se crea un manager
		}
	}
	//listo
	@Override
	public Airport createAirport(String iataCode, String cityName, String airportName, double longitude,
			double latitude) throws AirlineServiceException {
		if(null==iataCode) {
			throw new AirlineServiceException("iataCode: es NULL");
		}
		if(iataCode.length() !=3  || !iataCode.toUpperCase().equals(iataCode)) { //metodo ve si es mayuscula, y con ! vemos lo contrario
			throw new AirlineServiceException("iataCode: no debe ser diferente de 3 digitos, y no debe contener minusculas");
		}
		for(int i=0;i<iataCode.length();i++) {
			if(!( (iataCode.charAt(i)>='a' && iataCode.charAt(i)<='z') || (iataCode.charAt(i)>='A' && iataCode.charAt(i)<='Z') )) {
				throw new AirlineServiceException("iataCode: solo debe contener letras");
			}
		}
		if(cityName==null || cityName.isBlank()) {
			throw new AirlineServiceException("cityName: no debe ser nulo, o ser un Blank");
		}
		if(airportName==null || airportName.isBlank() ) {
			throw new AirlineServiceException("airportName: no debe ser nulo o ser un Blank");
		}
		if(longitude<-180 || longitude>180) {
			throw new AirlineServiceException("Longitude: debe estar en el intervalo [-180,180]");
		}
		if(latitude<-90 || latitude>90) {
			throw new AirlineServiceException("Latitude: debe estar en el intervalo [-90,90]");
		}
		ensureActiveManager();
		AirportJPADAO dao= new AirportJPADAO(em);
		
		Airport a= new Airport();
		a.setIataCode(iataCode);
		a.setCityName(cityName);
		a.setAirportName(airportName);
		a.setLongitude(longitude);
		a.setLatitude(latitude);
		
		EntityTransaction et= em.getTransaction();
		
		try {
			et.begin();
			a= dao.create(a);
			et.commit();
			em.refresh(a);
			return a;
		}catch (Exception e){
			if(et.isActive()) {
				et.rollback();
			}
			throw new AirlineServiceException("Error: no se pudo crear un Aiport");
		}
	}
    //listo
	@Override
	public List<Airport> listAirports() {
		ensureActiveManager();
		AirportJPADAO dao= new AirportJPADAO(em);
		return dao.findAll();
	}
	//listo
	@Override
	public AircraftType createAircraft(String manufacturer, String model, int seatRows, int seatColumns) 
			throws AirlineServiceException {
		if(manufacturer==null || manufacturer.isBlank()) {
			throw new AirlineServiceException("Manufacturer: no puede ser null o blank");
		}
		if(model==null || model.isBlank()) {
			throw new AirlineServiceException("Model: no puede ser null o blank");
		}
		if(seatRows<0 || seatColumns>seatRows) {
			throw new AirlineServiceException("SeatRows: no puede ser negativo y no puede ser menor a SeatColumns");
		}
		if(seatColumns<0 || seatColumns>seatRows) {
			throw new AirlineServiceException("SeatRows: no puede ser negativo y no puede ser mayor a SeatRows");
		}
		if(seatColumns==seatRows) {
			throw new AirlineServiceException("No se puede crear un aircraft con el mismo numero de SeatRows y SeatColumns");
		}
		if(0==seatRows || 0==seatColumns) {
			throw new AirlineServiceException("seatRows,seatColumns: no pueden ser 0 ");
		}
		//AircraftType nuevo= new AircraftType(manufacturer,model,seatRows,seatColumns);
		ensureActiveManager();
		AircraftTypeJPADAO dao= new AircraftTypeJPADAO(em); //nuevo objeto
		AircraftType a= new AircraftType(); //nuevo objeto
		a.setManufacturer(manufacturer); //aplicando valores
		a.setModel(model);
		a.setSeatRows(seatRows);
		a.setSeatColumns(seatColumns);
		
		EntityTransaction et= this.em.getTransaction(); //creando entidad para hacer la transaccion
		
		try {//empieza transaccion
			et.begin(); 
			a= dao.create(a); //guardamos en a, el objeto que crea dao *crea el objeto a en la base de datos*, guardamos el mismo objeto pero ahora ya instanciado en jpa
			et.commit();
			//termina transaccion
			em.refresh(a);
			return a; //regresamos el objeto ya metido en la base de datos
		}catch(Exception e){
			if(et.isActive()) {
				et.rollback();
			}
			throw new AirlineServiceException("Error: no se puede crear un Aircraft");
		}
	}
    //listo
	@Override
	public List<AircraftType> listAircraftTypes() {
		ensureActiveManager();
		AircraftTypeJPADAO dao= new AircraftTypeJPADAO(em);
		return dao.findAll();
	}
    //listo
	@Override
	public Flight createFlight(String originAirportCode, String destinationAirportCode, Long aircraftTypeCode)
			throws AirlineServiceException {
		if(null==originAirportCode) {
			throw new AirlineServiceException("originAirportCode: es NULL");
		}
		if(null==destinationAirportCode) {
			throw new AirlineServiceException("destinationAirportCode: es NULL");
		}
		if(null==aircraftTypeCode) {
			throw new AirlineServiceException("aircraftTypeCode: es NULL");
		}
		
		AirportJPADAO daoA= new AirportJPADAO(em);
		Airport origen= daoA.find(originAirportCode);
		Airport destino= daoA.find(destinationAirportCode);
		
		
		if(null==origen) {
			throw new AirlineServiceException("originAirportCode: No existe");
		}
		if(null==destino) {
			throw new AirlineServiceException("destinationAirportCode: No existe");
		}
		if(origen==destino) {
			throw new AirlineServiceException("originAirportCode y destinationAirportCode: Son iguales");
		}
		
		
		AircraftTypeJPADAO daoAT= new AircraftTypeJPADAO(em);
		AircraftType tipo= daoAT.find(aircraftTypeCode);
		if(null==tipo) {
			throw new AirlineServiceException("aircraftTypeCode: No existe");
		}
		
		FlightJPADAO daoF= new FlightJPADAO(em);
		Flight creado= new Flight();
		creado.setOrigin(origen);
		creado.setDestination(destino);
		creado.setAircraft(tipo);
		
		ensureActiveManager();
		EntityTransaction et= em.getTransaction();
		try {
			et.begin();
			creado=daoF.create(creado);
			et.commit();
			em.refresh(creado);
			return creado;
		}catch(Exception e){
			if(et.isActive() ) {
				et.rollback();
			}
			throw new AirlineServiceException("createFlight: No se pudo crear vuelo");
		}
	}
    //listo
	@Override
	public Flight findFlight(Long flightNumber) {
		ensureActiveManager();
		FlightJPADAO dao= new FlightJPADAO(em);
		try {
			return dao.find(flightNumber);
		}catch(Exception e) {
			return null; //retornamos null si no existe 
		}
		
	}
    //listo
	@Override
	public List<Flight> listFlights() {
		ensureActiveManager();
		FlightJPADAO dao= new FlightJPADAO(em);
		return dao.findAll(); //devolvemos la lista, misma que es buscada por el entitityManager
	}
	//lista
	@Override
	public Ticket purchaseTicket(String firstName, String lastName, Long flightNumber, LocalDate purchaseDate,
			LocalDate flightDate) throws AirlineServiceException {
		ensureActiveManager();
		if(firstName==null || firstName.isBlank() ) {
			throw new AirlineServiceException("firstName: No puede ser nulo o tipo Blank");
		}
		if(lastName==null || lastName.isBlank() ) {
			throw new AirlineServiceException("lastName: No puede ser nulo o tipo Blank");
		}
		if(flightNumber==null) {
			throw new AirlineServiceException("flightNumber: es NULL");
		}
		FlightJPADAO dao= new FlightJPADAO(em); //dao em1
		Flight f= dao.find(flightNumber);
		if(null==f) {
			throw new AirlineServiceException("flightNumber: Debe existir en la base de datos");
			}
		if(null==flightDate) {
			throw new AirlineServiceException("flightDate: No puede ser null");
		}
		if(purchaseDate==null  || purchaseDate.isAfter(flightDate)) {
			throw new AirlineServiceException("purchaseDate: No puede ser despues de flightDate o null");
		}
		if(availableSeats(flightNumber, flightDate)<=0) {
			System.out.println("0");
			throw new AirlineServiceException("Todos los asientos ya vendidos");
		}
		
		
		
		//tMax ya declarada: lo maximo que puede alcanzar la funcion
		//Ttotal no declarada: dependencia del precio del ticket con respecto al tiempo
		//tRush ya declarada: numero de dias antes de que le precio del vuelo empiece a subir
		//t no declarada: diferencia de dias entre diaCompra y diaVuelo
		long t=java.time.temporal.ChronoUnit.DAYS.between(purchaseDate, flightDate);
		double Ttotal;
		Ttotal= (((tMax-1)*7*t*Math.exp((1/2)-(49*t*t/8*tRush*tRush)))/(2*tRush))+1;
		
		//n declarada: numero de boletos vendidos para el vuelo
		//nMax declarada: numero totales de boletos
		//Omax declarada: lo maximo que puede alcanzar la funcion
		double Ntotal;
		int nMax=(   f.getAircraft().getSeatColumns()  *  f.getAircraft().getSeatRows()  )-1; //numero de asientos totales COLUMNA*FILA -1
		int n=dao.find(flightNumber) .getTickets() .size( );          //la consulta que devuelve los vendidos y .size(), getTickets regresa los tickets, y solo se crea un ticket cuando ya se vendio, entonces seria una lista de los tickets vendidos
		Ntotal=( ((1-oMax)*Math.cos((n*Math.PI)/nMax)) +oMax+1 )/2;
		
		
		//d: distancia en kilometros del aerepuertoOrigen a aereopuertoDestino
		double Btotal;
		//f.getOrigin().getIataCode(); retornan el objeto Aereopuerto origen, y ahi mismo llamamos al getter de iataCode
		double dca=AirportQuery.geodesicDistance(f.getOrigin().getIataCode(), f.getDestination().getIataCode()); //como es estatica, no se tiene que instanciar un objeto 
		Btotal=1000+(10*dca);
		
		int PrecioTotal= (int) Math.round(Ttotal*Ntotal*Btotal);
		
		
		
		int NumeroAsiento= dao.find(flightNumber).getTickets().size(); //numero de asiento= le vamos a asginar desde el 0, entonces seria el mismo numero que los tickets vendidos, si se vende un ticket, sube el numero de asiento +1
		int numeroColumna= (int)NumeroAsiento%f.getAircraft().getSeatColumns();
		int numeroFila=(int)(NumeroAsiento-1)/f.getAircraft().getSeatColumns();
		
		
		
		Ticket creado= new Ticket();
		creado.setPassengerFirstName(firstName);
		creado.setPassengerLastName(lastName);
		creado.setSeatRow(numeroFila+1);
		creado.setSeatColumn(numeroColumna+1);
		creado.setFlightDate(flightDate);
		creado.setPricePaid(PrecioTotal);
		creado.setFlight(f);
		

		TicketJPADAO daoT= new TicketJPADAO(em);
		EntityTransaction et= em.getTransaction();
		try {
			et.begin();
			creado=daoT.create(creado);
			em.refresh(creado.getFlight());
			et.commit();
			return creado;
		}catch (Exception e) {
			if(et.isActive() ) {
				et.rollback();
			}
			throw new AirlineServiceException("purchaseTicket: No se pudo crear el ticket");
		}
	}
	//lista
	@Override
	public int availableSeats(Long flightNumber, LocalDate flightDate) throws AirlineServiceException { //busca con una consulta la fecha que le damos y nos da los asientos especificos de esa fecha y del vuelo en si 
		if(flightNumber==null) {
			throw new AirlineServiceException("Numero de vuelo: No puede ser nulo");
		}
		if(flightDate==null) {
			throw new AirlineServiceException("Fecha de vuelo: No puede ser nulo");
		}
		ensureActiveManager();
		FlightJPADAO daoF= new FlightJPADAO(em);
		TicketJPADAO daoT= new TicketJPADAO(em);
		
		Flight vuelo=daoF.find(flightNumber); 
		if(vuelo==null) {
			throw new AirlineServiceException("Error: vuelo no encontrado");
		}

		int nMax=(   vuelo.getAircraft().getSeatColumns()  *  vuelo.getAircraft().getSeatRows()  ); //numero de asientos totales COLUMNA*FILA -1
		int n= daoT.TicketsVendidosFecha(flightNumber,flightDate).size();//metodo que busca los tickets 
		return nMax-n; 
	}
	//lista
	@Override
	public void cancelTicket(Long ticketNumber, LocalDate cancelDate) throws AirlineServiceException {
		if(ticketNumber==null) {
			throw new AirlineServiceException("ticketNumber: no debe ser ser null");
		}
		if(cancelDate==null) {
			throw new AirlineServiceException("cancelDate: no debe de ser null");
		}
		ensureActiveManager();
		TicketJPADAO daoT= new TicketJPADAO(em);
		Ticket eliminado=daoT.find(ticketNumber);
		if(null==eliminado) {
			throw new AirlineServiceException("ticket: No existe en la base de datos el ticket con ese numero");
		}
		if(eliminado.getFlightDate().isBefore(cancelDate)) { //nos dara un error si la fecha de cancelacion es despues a la fecha del vuelo 
			throw new AirlineServiceException("cancelDate: debe ser antes que la fecha del vuelo");
		}
		if(eliminado.getFlightDate().equals(cancelDate)) {
			throw new AirlineServiceException("cancelDate: no debe ser igual que la fecha del vuelo");
		}
		
		EntityTransaction et= em.getTransaction();
		try {
			et.begin();
			daoT.delete(eliminado);
			et.commit();
			em.refresh(eliminado.getFlight()); //refresca la base de datos, especificamente al valor del objeto Ticket con el getter Flight, nos retorna el vuelo a cancelar
		}catch(Exception e) {
			if(et.isActive()) {
				et.rollback();
			}
			throw new AirlineServiceException("Error: no se puede cancelar el ticket"); 
		}
	}
	//lista
	public int TicketsVendidos(Long flightNumber) throws AirlineServiceException {
		if(flightNumber==null) {
			throw new AirlineServiceException("Numero de vuelo: No puede ser nulo");
		}
		ensureActiveManager();//prevencion de que esté abierto
		FlightJPADAO daoF= new FlightJPADAO(em);
		Flight vuelo=daoF.find(flightNumber); //vuelo a buscar
		if(vuelo==null) {
			throw new AirlineServiceException("Error: vuelo no encontrado");
		}
		int n= vuelo.getTickets().size();//metodo que busca los tickets  //n<- este es el problema
		return n; 
	}	
	
	//creando auxiliar
	public List<Ticket> TicketsVendidosFechaConsulta(Long flightNumber,LocalDate flightDate){
		ensureActiveManager();
		TicketJPADAO daoT= new TicketJPADAO(em);
		return daoT.TicketsVendidosFecha(flightNumber,flightDate);
	}

	//creando auxiliar MEJORA OPCIONAL PROPUESTA
	public List<Ticket> findAllTicketsSortedByDate(){
		ensureActiveManager();
		TicketJPADAO daoT= new TicketJPADAO(em);
		return daoT.findAllTicketsSortedByDate1();
	}
	//LISTO
	public void close() {
		if (null != this.emf) {
			this.emf.close();
			this.emf=null;
		}
		if (null != this.em) {
			this.em.close();
			this.em=null;
		}
	}

}
