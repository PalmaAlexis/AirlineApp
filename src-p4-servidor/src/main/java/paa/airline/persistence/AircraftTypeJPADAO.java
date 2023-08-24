package paa.airline.persistence;
import javax.persistence.EntityManager;
import paa.airline.model.AircraftType;

public class AircraftTypeJPADAO extends JPADAO<AircraftType, Long>{ //T clase K id

	public AircraftTypeJPADAO(EntityManager em) {
		super(em, AircraftType.class); //ponemos directamente la clase, para ya no establecerla en un futuro 
	}
	

}
