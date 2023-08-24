package paa.airline.presentation;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import paa.airline.business.AirlineService;
import paa.airline.business.AirlineServiceException;
import paa.airline.model.AircraftType;
import paa.airline.model.Airport;
import paa.airline.model.Flight;

public class NewFlightVentana extends JDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AirlineService servicio;
	private Flight f;
	private boolean flag=false;
	private JComboBox<Airport> desplegableAereopuertos;
	private JComboBox<Airport> desplegableAereopuertos1;
	private JComboBox<AircraftType> desplegableAircrafts;
	private JFrame owner;

	
	public NewFlightVentana(JFrame owner, AirlineService servicio) {
		super(owner,"Create Flight",true);
		this.servicio=servicio;
		this.desplegableAereopuertos= new JComboBox<Airport>(new Vector<Airport>(servicio.listAirports()));
		this.desplegableAereopuertos1= new JComboBox<Airport>(new Vector<Airport>(servicio.listAirports()));
		this.desplegableAircrafts= new JComboBox<AircraftType>(new Vector<AircraftType>(servicio.listAircraftTypes() ) );

		this.owner=owner;
		
		JPanel panel= new JPanel();
		
		JLabel OriginLabel= new JLabel("Origin:");                  
		JLabel DestinationLabel= new JLabel("Destination:");      
		JLabel AircraftLabel= new JLabel("Aircraft type:");      
		JButton NewAirportButton= new JButton("New Airport");
		NewAirportButton.addActionListener(new botonCrearAirport());
		JButton NewAirportButton2= new JButton("New Airport");
		NewAirportButton2.addActionListener(new botonCrearAirport());
		JButton NewAircraft= new JButton("New Aircraft");
		NewAircraft.addActionListener(new botonCrearAircraft());
		
		
		
		
		panel.setLayout(new GridLayout(3, 3));
		panel.add(OriginLabel);       panel.add(desplegableAereopuertos);           panel.add(NewAirportButton);
		panel.add(DestinationLabel);  panel.add(desplegableAereopuertos1);          panel.add(NewAirportButton2);
		panel.add(AircraftLabel);     panel.add(desplegableAircrafts);              panel.add(NewAircraft);
		 
		JPanel panelBotones= new JPanel();
		JButton botonOK= new JButton("OK");
		botonOK.addActionListener(new botonOK());
		JButton botonCancel= new JButton("Cancel");
		botonCancel.addActionListener(new botonCancel());
		panelBotones.setLayout(new GridLayout(1, 2));
		panelBotones.add(botonOK);   panelBotones.add(botonCancel);
		
		
		
		this.setLayout(new BorderLayout());
		this.add(panel, BorderLayout.NORTH);
		this.add(panelBotones, BorderLayout.SOUTH);
		
		this.pack();
		this.setLocationRelativeTo(null);
		
	}
	
	class botonOK implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
			    f=servicio.createFlight( ((Airport) desplegableAereopuertos.getSelectedItem()).getIataCode(), ((Airport) desplegableAereopuertos1.getSelectedItem()).getIataCode(), ((AircraftType)desplegableAircrafts.getSelectedItem()).getId()    )  ;   //JCOMB
				NewFlightVentana.this.setVisible(false);
				flag=true;
			} catch (AirlineServiceException e1) {
				JOptionPane.showConfirmDialog(NewFlightVentana.this, e1.getMessage(), "Error al crear Flight", JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
	class botonCancel implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			NewFlightVentana.this.dispose();
		}
		
	}
	public Flight getF() {
		return f;
	}
	public boolean estaCompleto() {

		return flag;
	}
    class botonCrearAirport implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			NewAirportVentana dialog= new NewAirportVentana(owner, servicio);
			dialog.setVisible(true);
			if(dialog.estaCompletado()==true) {
				desplegableAereopuertos.addItem(dialog.getA());
				desplegableAereopuertos1.addItem(dialog.getA());
			}
		}
    }
    class botonCrearAircraft implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			NewAircraftVentana ventana= new NewAircraftVentana(owner, servicio);
			ventana.setVisible(true);
			if(ventana.isCompletado()==true) {
				desplegableAircrafts.addItem(ventana.getAt());
		    }
			
		}
    	
    }


}
