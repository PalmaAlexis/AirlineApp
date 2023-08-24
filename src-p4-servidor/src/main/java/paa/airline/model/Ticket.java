package paa.airline.model;

import javax.persistence.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.LocalDate;
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property = "ticketNumber")
@Entity
public class Ticket {
	@Id
	@GeneratedValue
    private Long ticketNumber;
    private String passengerFirstName;
    private String passengerLastName;
    private int seatRow;
    private int seatColumn;
    private LocalDate flightDate;
    private int pricePaid;
    @ManyToOne
    @JoinColumn(name="flightNumber")
    private Flight flight;

    public Long getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(Long ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getPassengerFirstName() {
        return passengerFirstName;
    }

    public void setPassengerFirstName(String passengerFirstName) {
        this.passengerFirstName = passengerFirstName;
    }

    public String getPassengerLastName() {
        return passengerLastName;
    }

    public void setPassengerLastName(String passengerLastName) {
        this.passengerLastName = passengerLastName;
    }

    public int getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(int seatRow) {
        this.seatRow = seatRow;
    }

    public int getSeatColumn() {
        return seatColumn;
    }

    public void setSeatColumn(int seatColumn) {
        this.seatColumn = seatColumn;
    }

    public int getPricePaid() {
        return pricePaid;
    }

    public LocalDate getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(LocalDate flightDate) {
        this.flightDate = flightDate;
    }

    public void setPricePaid(int pricePaid) {
        this.pricePaid = pricePaid;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticket)) return false;

        Ticket ticket = (Ticket) o;

        return ticketNumber.equals(ticket.ticketNumber);
    }

    @Override
    public int hashCode() {
        return ticketNumber.hashCode();
    }

    private String seatNumber() {
        char columnLetter = 'A' - 1;
        return String.format("%d%s", this.seatRow, Character.toString(columnLetter + this.seatColumn));
    }

    @Override
    public String toString() {
        return String.format("%s on %s, seat %s: %s %s", this.flight, this.flightDate, this.seatNumber(), this.passengerFirstName, this.passengerLastName);

    }

    public Ticket() {
        // Required by JPA
    }

    public Ticket(String passengerFirstName, String passengerLastName, int seatRow, int seatColumn, LocalDate flightDate, int pricePaid, Flight flight) {
        this.passengerFirstName = passengerFirstName;
        this.passengerLastName = passengerLastName;
        this.seatRow = seatRow;
        this.seatColumn = seatColumn;
        this.flightDate = flightDate;
        this.pricePaid = pricePaid;
        this.flight = flight;
    }
}
