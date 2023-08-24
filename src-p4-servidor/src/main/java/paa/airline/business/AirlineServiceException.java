package paa.airline.business;

public class AirlineServiceException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AirlineServiceException(String format) {
        super(format);
    }
}
