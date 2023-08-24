package paa.airline.persistence;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import paa.airline.model.Ticket;

public class TicketJPADAO extends JPADAO<Ticket, Long>{

	public TicketJPADAO(EntityManager em) {
		super(em, Ticket.class);
	}
	
	public List<Ticket> TicketsVendidosFecha(Long flightNumber,LocalDate fecha) {
		TypedQuery<Ticket> q= em.createQuery("SELECT t FROM Ticket t JOIN t.flight f WHERE t.flightDate = :fecha AND f.flightNumber = :flightNumber", Ticket.class); //query a la base de datos para los tickets en precioPagado!=null
		q.setParameter("fecha", fecha);
		q.setParameter("flightNumber", flightNumber);
		List<Ticket> vendidosDia= q.getResultList();
		return vendidosDia;
	}
	
	public List<Ticket> findAllTicketsSortedByDate1() {
		TypedQuery<Ticket> q= em.createQuery("select t from Ticket t order by t.flightDate asc", Ticket.class); //query a la base de datos para los tickets en precioPagado!=null
		List<Ticket> ticketsPorDia= q.getResultList();
		return ticketsPorDia;
	}
	
}
