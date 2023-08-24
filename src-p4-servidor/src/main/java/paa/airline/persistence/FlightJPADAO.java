package paa.airline.persistence;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import paa.airline.model.Flight;
import paa.airline.model.Ticket;

public class FlightJPADAO extends JPADAO<Flight, Long>{

	public FlightJPADAO(EntityManager em) {
		super(em, Flight.class);
	}
	
	//auxiliar
	public List<Ticket> TicketsVendidos(Flight f) {
		TypedQuery<Ticket> q= em.createQuery("select t from Ticket t where t.flight= :flight and t.pricePaid!=null", Ticket.class); //query a la base de datos para los tickets en precioPagado!=null
		q.setParameter("flight", f);
		List<Ticket> vendidos= q.getResultList();
		return vendidos;
	}
	


}
