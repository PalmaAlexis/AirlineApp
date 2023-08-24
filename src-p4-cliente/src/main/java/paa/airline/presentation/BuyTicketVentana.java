package paa.airline.presentation;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import paa.airline.business.AirlineService;
import paa.airline.business.AirlineServiceException;
import paa.airline.model.Flight;
import paa.airline.model.Ticket;

public class BuyTicketVentana extends JDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AirlineService servicio;
	private JComboBox<Flight> desplegablevuelos;	
	private JTextField FirstName;
	private JTextField LastName;
	private JTextField FlightDate;
	private JTextField fechaActualTexto;
	private Ticket ticket;
	private boolean Flag=false;
	
	
	public BuyTicketVentana(JFrame dueño,AirlineService servicio, JTextField fechaActualTexto) {
		super(dueño, "Buy ticket", true);
		this.servicio=servicio;
		this.fechaActualTexto=fechaActualTexto;
		
		desplegablevuelos=new JComboBox<Flight>(new Vector<Flight>(servicio.listFlights() ));
		
		JPanel panel= new JPanel();
		
		JLabel FirstNameLabel= new JLabel("Traveller first name:");         FirstName= new JTextField(20);                      FirstNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		JLabel LastNameLabel= new JLabel("Traveller last name:");           LastName= new JTextField(20);                       LastNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		JLabel FlightLabel= new JLabel("Flight:");					      /*JComboBox<Object> Flight= new JComboBox<Object>();*/FlightLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		JLabel FlightDateLabel= new JLabel("Flight date (AAAA-MM-DD):");					FlightDate= new JTextField(20);                     FlightDateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		panel.setLayout(new GridLayout(4, 2));
		panel.add(FirstNameLabel);     panel.add(FirstName);
		panel.add(LastNameLabel);      panel.add(LastName);
		panel.add(FlightLabel);        panel.add(desplegablevuelos);
		panel.add(FlightDateLabel);    panel.add(FlightDate);
		
		
		JPanel panelBotones= new JPanel();
		
		JButton OKboton= new JButton("OK");
		OKboton.addActionListener(new OKbotonListener());
		JButton Cancelboton= new JButton("Cancel");
		Cancelboton.addActionListener(new CancelbotonListener());
		
		panelBotones.setLayout(new GridLayout(1, 2));
		panelBotones.add(OKboton);       panelBotones.add(Cancelboton);
		
		
		this.setLayout(new BorderLayout());
		this.add(panel,BorderLayout.NORTH);
		this.add(panelBotones, BorderLayout.SOUTH);
		
		this.pack();
		this.setLocationRelativeTo(null);
	}
	
	class CancelbotonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			BuyTicketVentana.this.dispose();
		}	
	}
	
	class OKbotonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				ticket=servicio.purchaseTicket(FirstName.getText(), LastName.getText(), ((Flight)desplegablevuelos.getSelectedItem()).getFlightNumber(), LocalDate.parse(fechaActualTexto.getText()), LocalDate.parse(FlightDate.getText()) );
				BuyTicketVentana.this.setVisible(false);
				Flag=true;
			} catch (AirlineServiceException e1) {
				JOptionPane.showConfirmDialog(BuyTicketVentana.this, e1.getMessage(), "Error al comprar Ticket", JOptionPane.ERROR_MESSAGE);
			}catch (NumberFormatException nfe) {
				JOptionPane.showConfirmDialog(BuyTicketVentana.this, "Llena todos los campos", "Error al crear Aereopuerto", JOptionPane.ERROR_MESSAGE);
		    }
			catch(DateTimeParseException e1) {
				JOptionPane.showConfirmDialog(BuyTicketVentana.this, "Fecha introducida: no valida","Error al comprar ticket",JOptionPane.ERROR_MESSAGE);
				
			}
				
			}
		}
		
		
		
	

	
	public Ticket getTicket() {
		return ticket;
	}

	
    public boolean estaHecho() {
		return Flag;
	}
	
	
	

}
