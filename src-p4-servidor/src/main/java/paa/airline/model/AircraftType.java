package paa.airline.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class AircraftType {
	@Id
	@GeneratedValue
    private Long id;
    private String manufacturer;
    private String model;
    private int seatRows;
    private int seatColumns;

    /**
     * Default constructor, required by JPA. It does not initialize anything at all.
     */
    public AircraftType() { }

    public AircraftType(String manufacturer, String model, int seatRows, int seatColumns) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.seatRows = seatRows;
        this.seatColumns = seatColumns;
    }

    public Long getId() {
        return id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getSeatRows() {
        return seatRows;
    }

    public void setSeatRows(int rows) {
        this.seatRows = rows;
    }

    public int getSeatColumns() {
        return seatColumns;
    }

    public void setSeatColumns(int columns) {
        this.seatColumns = columns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AircraftType)) return false;
        AircraftType otherAircraft = (AircraftType) o;
        return Objects.equals(id, otherAircraft.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s %s", this.manufacturer, this.model);

    }
}
