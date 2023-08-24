package paa.airline.persistence;
import javax.persistence.EntityManager;
import paa.airline.model.Airport;

public class AirportJPADAO extends JPADAO<Airport, String>{

	public AirportJPADAO(EntityManager em) {
		super(em, Airport.class);
	}

}
