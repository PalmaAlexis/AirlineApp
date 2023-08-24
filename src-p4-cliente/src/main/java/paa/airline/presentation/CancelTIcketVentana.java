package paa.airline.presentation;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
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
public class CancelTIcketVentana extends JDialog{
	private AirlineService servicio;
	private JComboBox<Flight> cajaConFlights;
	private JComboBox <Ticket> cajaConTickets;
	private JTextField fechaActualTexto;
	private boolean estaCancelado=false;
	private Ticket ticket;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CancelTIcketVentana(JFrame padre, AirlineService servicio, JTextField fechaActualTexto) {
		super(padre, "Cancel Ticket", true);
		this.servicio=servicio;
		this.fechaActualTexto=fechaActualTexto;
		
		cajaConTickets= new JComboBox<Ticket>();
		
		cajaConFlights= new JComboBox<Flight>(new Vector<Flight>(servicio.listFlights()));
		ActulizarTickets();
		cajaConFlights.addActionListener(new BotonSeleccionarVueloListener());
		
		
		//cajaConTickets= new JComboBox<Ticket>(); 
		//ActulizarTickets();

		
		JPanel panel= new JPanel();
		JLabel FlightLabel= new JLabel("Flight");     /*JComboBox<Object> Flights= new JComboBox<Object>();*/      FlightLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		JLabel TicketLabel= new JLabel("Ticket");     /*JComboBox<Object> Tickets= new JComboBox<Object>();*/ 	   TicketLabel.setHorizontalAlignment(SwingConstants.RIGHT);	
		
		panel.setLayout(new GridLayout(2, 2));
		panel.add(FlightLabel);   panel.add(cajaConFlights);
		panel.add(TicketLabel);   panel.add(cajaConTickets);
		
		JPanel panelBotones= new JPanel();
		JButton botonOK= new JButton("OK");
		botonOK.addActionListener(new BotonOKListener());
		JButton botonCancel= new JButton("Cancel");
		botonCancel.addActionListener(new BotonCancelListener());
		panelBotones.setLayout(new GridLayout(1, 2));
		panelBotones.add(botonOK);        panelBotones.add(botonCancel);
		
		
		this.setLayout(new BorderLayout());
		this.add(panel,BorderLayout.NORTH);
		this.add(panelBotones, BorderLayout.SOUTH);
		
		cajaConFlights.addActionListener(new BotonSeleccionarVueloListener());
		
		this.pack();
		this.setLocationRelativeTo(padre);
		
	}
	
	class BotonCancelListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			CancelTIcketVentana.this.dispose();
		}
	}
	
	protected void ActulizarTickets(){
		Flight c= (Flight) cajaConFlights.getSelectedItem();
		if(null != c) {
			//cajaConTickets=new JComboBox<Ticket>(new Vector<Ticket>(c.getTickets()  ) );
			//cajaConTickets.setListData(new Vector<Ticket>(c.getTickets()));
			
			cajaConTickets.setModel(new DefaultComboBoxModel<Ticket>(new Vector<Ticket>(c.getTickets())  ));
			
		}
		
	}
	
	class BotonSeleccionarVueloListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			ActulizarTickets();
		}
	}
	
	class BotonOKListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				ticket=(Ticket) cajaConTickets.getSelectedItem();
				servicio.cancelTicket(((Ticket) cajaConTickets.getSelectedItem()).getTicketNumber()  , LocalDate.parse(fechaActualTexto.getText() )   );
				CancelTIcketVentana.this.setVisible(false);
				estaCancelado=true;
			} catch (AirlineServiceException e1) {
				JOptionPane.showConfirmDialog(CancelTIcketVentana.this, e1.getMessage(), "Error al cancelar ticket", JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}

	public boolean isEstaCancelado() {
		return estaCancelado;
	}

	public Ticket getTicket() {
		return ticket;
	}
	
	
	
	

}
