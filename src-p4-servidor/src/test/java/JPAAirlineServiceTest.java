import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import paa.airline.business.AirlineServiceException;
import paa.airline.business.JPAAirlineService;
import paa.airline.model.AircraftType;
import paa.airline.model.Airport;
import paa.airline.model.Flight;
import paa.airline.model.Ticket;

//ALUMNO: PALMA PADILLA ALEXIS JAEL 
//GRUPO: J08J09
//YA IMPLEMENTÉ LA MEJORA OPCIONAL, QUE NOS DA TODOS LOS TICKETS ORDENADOS POR ASCENDENTE 

public class JPAAirlineServiceTest {
	JPAAirlineService AirlineServicio= new JPAAirlineService("paa-jpa"); //Servicio

		@DisplayName("Probando que se creen las Entidades de forma correcta: AircraftType, Airport, Flight")
		@Test
		void testCreateAircraftTypeAirportFlight() {
			try {
				AircraftType AT1= AirlineServicio.createAircraft("Boeing Commercial Airplanes", "Boeing 737", 20, 4);
				AircraftType AT2= AirlineServicio.createAircraft("Boeing Commercial Airplanes", "Boeing 800", 10, 4);
				System.out.println("testCreateAircraftTypeAirportFlight");
				System.out.println("AIRCRAFTTYPE CREADOS:");
				System.out.println(AT1);
				System.out.println(AT2);
				
				Airport A1= AirlineServicio.createAirport("MAD", "MADRID", "Barajas International Airport", -3.566764, 40.493556);
				Airport A2= AirlineServicio.createAirport("CDG", "PARIS", "Paris Charles de Gaulle Airport (Roissy Airport)", 2.55, 49.012779);
				System.out.println("AEREOPUERTOS CREADOS:");
				System.out.println(A1);
				System.out.println(A2);
				
				Flight F1= AirlineServicio.createFlight(A1.getIataCode(), A2.getIataCode(), AT1.getId());
				System.out.println("VUELO CREADO:");
				System.out.println(F1);
			} catch (AirlineServiceException e) {
				e.printStackTrace();
			}
		}
		
		@DisplayName("Probando que sirva el comprar ticket y el availableSeats")
		@Test
		void testPurcharseTicket() {
			try {
				AircraftType AT1= AirlineServicio.createAircraft("Boeing Commercial Airplanes", "Boeing 737", 20, 4);
				Airport A1= AirlineServicio.createAirport("MAD", "MADRID", "Barajas International Airport", -3.566764, 40.493556);
				Airport A2= AirlineServicio.createAirport("CDG", "PARIS", "Paris Charles de Gaulle Airport (Roissy Airport)", 2.55, 49.012779);
				Flight F1= AirlineServicio.createFlight(A1.getIataCode(), A2.getIataCode(), AT1.getId());
				System.out.println("testPurcharseTicket");
				System.out.println("TICKETS DISPONIBLES:"+AirlineServicio.availableSeats(F1.getFlightNumber(), LocalDate.of(2023, 03, 30))); //Asientos sin compra
				
				
				AirlineServicio.purchaseTicket("MIGUEL", "MARTINEZ", F1.getFlightNumber(), LocalDate.of(2023, 03, 10), LocalDate.of(2023, 03, 30)); //Un ticket comprado
				System.err.println("1 TICKET COMPRADO");
				System.out.println("TICKETS DISPONIBLES:"+AirlineServicio.availableSeats(F1.getFlightNumber(), LocalDate.of(2023, 03, 30))); //Asientos con 1 compra
		} catch (AirlineServiceException e) {
				e.printStackTrace();
			}
		}
		
		@DisplayName("Probando que sirva el Cancel ticket y el availableSeats")
		@Test
		void testCancelTicket() {
			try {
				AircraftType AT1= AirlineServicio.createAircraft("Boeing Commercial Airplanes", "Boeing 737", 20, 4);
				Airport A1= AirlineServicio.createAirport("MAD", "MADRID", "Barajas International Airport", -3.566764, 40.493556);
				Airport A2= AirlineServicio.createAirport("CDG", "PARIS", "Paris Charles de Gaulle Airport (Roissy Airport)", 2.55, 49.012779);
				
				Flight F1= AirlineServicio.createFlight(A1.getIataCode(), A2.getIataCode(), AT1.getId());
				System.out.println("testCancelTicket");
				System.out.println("TICKETS DISPONIBLES:"+AirlineServicio.availableSeats(F1.getFlightNumber(), LocalDate.of(2023, 03, 30))); //Asientos sin compra
				
				Ticket T1=AirlineServicio.purchaseTicket("MIGUEL", "MARTINEZ", F1.getFlightNumber(), LocalDate.of(2023, 03, 10), LocalDate.of(2023, 03, 30)); //Un ticket comprado
				System.err.println("1 TICKET COMPRADO");
				System.out.println("TICKETS DISPONIBLES:"+AirlineServicio.availableSeats(F1.getFlightNumber(), LocalDate.of(2023, 03, 30))); //Asientos con 1 compra
				
				AirlineServicio.cancelTicket(T1.getTicketNumber(), LocalDate.of(2023, 03, 15)); //cancelando el ticket
				System.err.println("1 TICKET CANCELADO");
				System.out.println("TICKETS DISPONIBLES:"+AirlineServicio.availableSeats(F1.getFlightNumber(), LocalDate.of(2023, 03, 30))); //Asientos con 1 compra cancelada		
		} catch (AirlineServiceException e) {
				e.printStackTrace();
			}
		}
		
		@DisplayName("Probando que se listen las Entidades de forma correcta: AircraftType, Airport, Flight")
		@Test
		void testListAircraftTypeAirportFlight() {
			try {
				AircraftType AT1=AirlineServicio.createAircraft("Boeing Commercial Airplanes", "Boeing 737", 20, 4);
				AircraftType AT2=AirlineServicio.createAircraft("Boeing Commercial Airplanes", "Boeing 800", 10, 4);
				System.out.println("testListAircraftTypeAirportFlight");
				System.out.println("LISTA AIRCRAFTS EXISTENTES");
				System.out.println(AirlineServicio.listAircraftTypes());
				
				Airport A1=AirlineServicio.createAirport("MAD", "MADRID", "Barajas International Airport", -3.566764, 40.493556);
				Airport A2=AirlineServicio.createAirport("CDG", "PARIS", "Paris Charles de Gaulle Airport (Roissy Airport)", 2.55, 49.012779);
				System.out.println("LISTA AEREOPUERTOS EXISTENTES");
				System.out.println(AirlineServicio.listAirports());
				
				AirlineServicio.createFlight(A1.getIataCode(), A2.getIataCode(), AT1.getId());
				AirlineServicio.createFlight(A2.getIataCode(), A1.getIataCode(), AT2.getId());				
				System.out.println("LISTA VUELOS EXISTENTES");
				System.out.println(AirlineServicio.listFlights());
			} catch (AirlineServiceException e) {
				e.printStackTrace();
			}
		
		}
		
		@DisplayName("Probando que sirva el findFlight")
		@Test
		void testFindFlight() {
			try {
				AircraftType AT1 = AirlineServicio.createAircraft("Boeing Commercial Airplanes", "Boeing 737", 20, 4);
				AircraftType AT2=AirlineServicio.createAircraft("Boeing Commercial Airplanes", "Boeing 800", 10, 4);
				Airport A1=AirlineServicio.createAirport("MAD", "MADRID", "Barajas International Airport", -3.566764, 40.493556);
				Airport A2=AirlineServicio.createAirport("CDG", "PARIS", "Paris Charles de Gaulle Airport (Roissy Airport)", 2.55, 49.012779);
				
				Flight F1= AirlineServicio.createFlight(A1.getIataCode(), A2.getIataCode(), AT1.getId());
				Flight F2=AirlineServicio.createFlight(A2.getIataCode(), A1.getIataCode(), AT2.getId());
				
				System.out.println("testFindFlight");
				System.out.println("BUSCANDO EL VUELO 1-> "+AirlineServicio.findFlight(F1.getFlightNumber()));
				System.out.println("BUSCANDO EL VUELO 2-> "+AirlineServicio.findFlight(F2.getFlightNumber()));
			} catch (AirlineServiceException e) {
				e.printStackTrace();
			}
		}
		
		@DisplayName("Probando que busque los tickets de un vuelo por su fecha epecifica, empleando una consulta en el TicketJPADAO")
		@Test
		void testConsultaPorFecha() {
			try {
				AircraftType AT1 = AirlineServicio.createAircraft("Boeing Commercial Airplanes", "Boeing 737", 20, 4);
				Airport A1= AirlineServicio.createAirport("MAD", "MADRID", "Barajas International Airport", -3.566764, 40.493556);
				Airport A2= AirlineServicio.createAirport("CDG", "PARIS", "Paris Charles de Gaulle Airport (Roissy Airport)", 2.55, 49.012779);
				Flight F1= AirlineServicio.createFlight(A1.getIataCode(), A2.getIataCode(), AT1.getId());

				AirlineServicio.purchaseTicket("MIGUEL", "MARTINEZ", F1.getFlightNumber(), LocalDate.of(2023, 03, 10), LocalDate.of(2023, 03, 30)); //FECHA 1
				AirlineServicio.purchaseTicket("PEDRO", "HERNANDEZ", F1.getFlightNumber(), LocalDate.of(2023, 03, 10), LocalDate.of(2023, 03, 29)); //FECHA 2
				AirlineServicio.purchaseTicket("DAVID", "GONZALES", F1.getFlightNumber(), LocalDate.of(2023, 03, 10), LocalDate.of(2023, 03, 30)); //FECHA 1 
				
				System.out.println("testAvailableSeats implementando la consulta por fecha");
				System.out.println(AirlineServicio.TicketsVendidosFechaConsulta(F1.getFlightNumber(), LocalDate.of(2023, 03, 30)) ); //BOLETOS FECHA 1
			} catch (AirlineServiceException e) {
				e.printStackTrace();
			}

		}

		@DisplayName("Implementando la mejora opcional propuesta: Listar todos los tickets ordenados por fecha ascendente")
		@Test
		void testConsultaTicketsAsc() {
			try {
				AircraftType AT1 = AirlineServicio.createAircraft("Boeing Commercial Airplanes", "Boeing 737", 20, 4);
				Airport A1= AirlineServicio.createAirport("MAD", "MADRID", "Barajas International Airport", -3.566764, 40.493556);
				Airport A2= AirlineServicio.createAirport("CDG", "PARIS", "Paris Charles de Gaulle Airport (Roissy Airport)", 2.55, 49.012779);
				Flight F1= AirlineServicio.createFlight(A1.getIataCode(), A2.getIataCode(), AT1.getId());

				AirlineServicio.purchaseTicket("MIGUEL", "MARTINEZ", F1.getFlightNumber(), LocalDate.of(2023, 03, 10), LocalDate.of(2023, 03, 30)); //FECHA 1 (tercera fecha)
				AirlineServicio.purchaseTicket("PEDRO", "HERNANDEZ", F1.getFlightNumber(), LocalDate.of(2023, 03, 10), LocalDate.of(2023, 03, 10)); //FECHA 2 (primera fecha)
				AirlineServicio.purchaseTicket("DAVID", "GONZALES", F1.getFlightNumber(), LocalDate.of(2023, 03, 10), LocalDate.of(2023, 03, 20)); //FECHA 3  (segunda fecha) 
				
				System.out.println("MEJORA OPCIONAL-> ORDENAR LOS TICKETS POR FECHA ASCENDENTE");
				System.out.println(AirlineServicio.findAllTicketsSortedByDate());
			} catch (AirlineServiceException e) {
				e.printStackTrace();
			}
			
		}
}
