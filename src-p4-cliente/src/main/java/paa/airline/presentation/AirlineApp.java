package paa.airline.presentation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import paa.airline.business.AirlineService;
import paa.airline.business.RemoteAirlineService;
import paa.airline.model.AircraftType;
import paa.airline.model.Airport;
import paa.airline.model.Flight;
import paa.airline.model.Ticket;
import paa.airline.util.FlightMap;

public class AirlineApp extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AirlineService servicio= new RemoteAirlineService();
	private JComboBox<Airport> desplegableAereopuertos; //**
	private JComboBox<AircraftType> desplegableAircrafts;
	//private JComboBox<Flight> desplegableFlights;
	private JTextField fechaActualTexto;
	private JList<Ticket> listaTickets;
	private JComboBox<Flight> cajaConFlights;
	private FlightMap mapa;
	private JComboBox<LocalDate> fechasVuelosMapa;
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				AirlineApp app=new AirlineApp("Airline App");
				app.pack();
				app.setLocationRelativeTo(null);
				app.setVisible(true);
				app.setDefaultCloseOperation(EXIT_ON_CLOSE);
			}
		});
	}
	
	public AirlineApp(String titulo) {
		super(titulo);
		desplegableAereopuertos= new JComboBox<Airport>();
		desplegableAircrafts= new JComboBox<AircraftType>();
		//desplegableFlights= new JComboBox<Flight>();
		listaTickets= new JList<Ticket>();
		
		
		//*********************************************************MENU************************************************************
		JMenuBar menu= new JMenuBar();                   
		menu.setBackground(Color.LIGHT_GRAY);
		
		JMenu OperationsMenu= new JMenu("Operations");
		JMenuItem MenuNuevoAereopuerto= new JMenuItem("New Airport");
		MenuNuevoAereopuerto.addActionListener(new NewAirportListener());
		JMenuItem MenuNuevoAircraft= new JMenuItem("New Aircraft");
		MenuNuevoAircraft.addActionListener(new NewAircraftListener());
		JMenuItem MenuNuevoFlight= new JMenuItem("New Flight");
		MenuNuevoFlight.addActionListener(new NewFlightListener());
		JMenuItem MenuBuyTicket= new JMenuItem("Buy Ticket");
		MenuBuyTicket.addActionListener(new BuyTicketListener());
		JMenuItem MenuCancelTicket= new JMenuItem("Cancel Ticket");
		MenuCancelTicket.addActionListener(new CancelTicketListener());
		JMenuItem MenuQuit= new JMenuItem("Quit");
		MenuQuit.addActionListener(new VentanaQuit());
		
		JMenu HelpMenu= new JMenu("Help");
		JMenuItem about= new JMenuItem("About");
		about.addActionListener(new VentanaInfo());
		
		OperationsMenu.add(MenuNuevoAereopuerto);
		OperationsMenu.add(MenuNuevoAircraft);
		OperationsMenu.add(MenuNuevoFlight);
		OperationsMenu.add(MenuBuyTicket);
		OperationsMenu.add(MenuCancelTicket);
		OperationsMenu.add(MenuQuit);
		HelpMenu.add(about);

		menu.add(OperationsMenu);
		menu.add(HelpMenu);
		
		//*********************************************************PANEL ARRIBA************************************************************
		JPanel panelArriba= new JPanel();   
		panelArriba.setBackground(Color.LIGHT_GRAY);
		Border marcoAzul= BorderFactory.createLineBorder(new Color(173, 216, 230));
		
		JPanel panelBotonesIconos= new JPanel();
		panelBotonesIconos.setBackground(Color.LIGHT_GRAY);
		Icon IconoVuelo= new ImageIcon(getClass().getResource("/newFlight.png"));
		Icon IconoNuevoTicket= new ImageIcon(getClass().getResource("/buyTicket.png"));
		Icon IconoCancelTicket= new ImageIcon(getClass().getResource("/cancelTicket.png"));
		JButton botonIconoVuelo= new JButton(IconoVuelo);
		botonIconoVuelo.addActionListener(new NewFlightListener());
		JButton botonNuevoTicket= new JButton(IconoNuevoTicket);
		botonNuevoTicket.addActionListener(new BuyTicketListener());
		JButton botonCancelTicket= new JButton(IconoCancelTicket);
		botonCancelTicket.addActionListener(new CancelTicketListener());
		panelBotonesIconos.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelBotonesIconos.add(botonIconoVuelo);
		panelBotonesIconos.add(botonNuevoTicket);
		panelBotonesIconos.add(botonCancelTicket);    
		
		JPanel CurrentDatepanel= new JPanel();
		CurrentDatepanel.setBackground(Color.LIGHT_GRAY);
		JButton atrasarDate= new JButton("<");
		atrasarDate.addActionListener(new BotonAtrasarfecha());
		JButton adelantarDate= new JButton(">"); 
		adelantarDate.addActionListener(new BotonAdelantarfecha());
		LocalDate fechaActualVariable = LocalDate.now();                   fechaActualTexto= new JTextField(fechaActualVariable.toString());     fechaActualTexto.setEditable(false);
		JLabel fechaActualLabel= new JLabel("CURRENT DATE (SIMULATOR)");   JLabel labelBlanca= new JLabel(" ");
		CurrentDatepanel.setLayout(new BorderLayout());
		CurrentDatepanel.add(atrasarDate, BorderLayout.WEST);              CurrentDatepanel.add(fechaActualTexto, BorderLayout.CENTER);    
		CurrentDatepanel.add(adelantarDate, BorderLayout.EAST);            CurrentDatepanel.add(fechaActualLabel, BorderLayout.NORTH);
		CurrentDatepanel.add(labelBlanca, BorderLayout.SOUTH);
		CurrentDatepanel.setBorder(marcoAzul);                             
		
		panelArriba.setLayout(new BorderLayout());
		panelArriba.add(panelBotonesIconos, BorderLayout.WEST);
		panelArriba.add(CurrentDatepanel, BorderLayout.EAST); 
		
		
		//*********************************************************PANEL IZQUIERDO************************************************************
		JPanel panelIzquierda= new JPanel();
		panelIzquierda.setBackground(Color.WHITE);   
		cajaConFlights= new JComboBox<Flight>(new Vector<Flight>(servicio.listFlights()));
		//ActualizarVuelos();
		actualizarTickets();
		cajaConFlights.addActionListener(new BotonCambiarTickets());

		
		JScrollPane scroller= new JScrollPane(listaTickets);       scroller.setPreferredSize(new Dimension(200, 50));
		Border marcoScheduledFlights= BorderFactory.createTitledBorder(marcoAzul, "Scheduled Flights");   Border marcoTickets= BorderFactory.createTitledBorder(marcoAzul, "Tickets");
		cajaConFlights.setBorder(marcoScheduledFlights);          listaTickets.setBorder(marcoTickets);
		panelIzquierda.setLayout(new BorderLayout());
		panelIzquierda.add(cajaConFlights, BorderLayout.NORTH);  //desplegableFlights
		panelIzquierda.add(scroller, BorderLayout.CENTER);
		
		//*********************************************************PANEL DERECHO************************************************************
		JPanel panelDerecha= new JPanel();
		panelDerecha.setBackground(Color.WHITE);
		AirlineService servicio= new RemoteAirlineService();
		this.mapa= new FlightMap(800, 500, servicio);
	    Border marcoFlightMap= BorderFactory.createTitledBorder(marcoAzul, "Flight Map");
	    fechasVuelosMapa= new JComboBox<LocalDate>();
	    InsertarFechas(); 
	    fechasVuelosMapa.addActionListener(new BotonCambiarMapas());
	    actualizarMapa();
	    panelDerecha.setBorder(marcoFlightMap);
	    panelDerecha.setLayout(new BorderLayout());
	    panelDerecha.add(fechasVuelosMapa,BorderLayout.NORTH);
	    panelDerecha.add(mapa,BorderLayout.CENTER);

		
		//*********************************************************JFRAME************************************************************
		this.setJMenuBar(menu);
		this.add(panelArriba, BorderLayout.NORTH);
		this.add(panelIzquierda, BorderLayout.WEST);
		this.add(panelDerecha, BorderLayout.CENTER);
		

		
		
	}
	
	class VentanaInfo implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(null, "PROGRAMA: PROYECTO\nVERSION:1\nAUTOR: PALMA PADILLA ALEXIS JAEL","About",JOptionPane.INFORMATION_MESSAGE);	
		}
	}
	
    class VentanaQuit implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}
	
    class NewAirportListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			NewAirportVentana dialog= new NewAirportVentana(AirlineApp.this, servicio);
			dialog.setVisible(true);
			if(dialog.estaCompletado()==true) {
				desplegableAereopuertos.addItem(dialog.getA());
			}
			
		}
    	
    }
	
    class NewAircraftListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			NewAircraftVentana ventana= new NewAircraftVentana(AirlineApp.this, servicio);
			ventana.setVisible(true);
			if(ventana.isCompletado()==true) {
				desplegableAircrafts.addItem(ventana.getAt());
			}
		}
		
	}
	
	class NewFlightListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			NewFlightVentana dialog= new NewFlightVentana(AirlineApp.this, servicio);
			dialog.setVisible(true);
			if(dialog.estaCompleto()==true) {
				//desplegableFlights.addItem(dialog.getF());
				//cajaConFlights= new JComboBox<Flight>(new Vector<Flight>(servicio.listFlights()));
				//cajaConFlights.addItem(dialog.getF());
				//cajaConFlights.setModel(new DefaultComboBoxModel<Flight>(new Vector<Flight>(servicio.listFlights() ) ) );
				ActualizarVuelos();
				actualizarMapa();
			}	
		}
	}
	
	protected void ActualizarVuelos() {
		cajaConFlights.setModel(new DefaultComboBoxModel<Flight>(new Vector<Flight>(servicio.listFlights() ) ) ); //el modelo de un jcombobox con un listflights, para refresh, el listflights de arriba se usa solo para el pantallazo inicial 
	}
	
	protected void InsertarFechas() {
		fechasVuelosMapa.addItem(LocalDate.now());
		for(int i=1;i<15;i++) {                         //proximas dos semanas
			fechasVuelosMapa.addItem(LocalDate.now().plusDays(i));
		}
	}
	
    class BuyTicketListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			BuyTicketVentana dialog= new BuyTicketVentana(AirlineApp.this, servicio,fechaActualTexto);
			dialog.setVisible(true);
			if(dialog.estaHecho()==true) {
				actualizarTickets();
				actualizarMapa();
			}
		}
    }
    
    
	class BotonAtrasarfecha implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			LocalDate fechaVariable=LocalDate.parse(fechaActualTexto.getText());
			fechaVariable= fechaVariable.minusDays(1);
			fechaActualTexto.setText(fechaVariable.toString());
		}
	}
	
	class BotonAdelantarfecha implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			LocalDate fechaVariable= LocalDate.parse(fechaActualTexto.getText());
			fechaVariable= fechaVariable.plusDays(1);
			fechaActualTexto.setText(fechaVariable.toString());
		}
		
	}

	protected void actualizarTickets() {
		Flight c = (Flight) cajaConFlights.getSelectedItem();
		if (null != c) {
			listaTickets.setListData(new Vector<Ticket>(c.getTickets() ) );
		}
	}
	
	protected void actualizarMapa() {
		LocalDate date = (LocalDate) fechasVuelosMapa.getSelectedItem();
		Flight flight = (Flight) cajaConFlights.getSelectedItem();
		if (null != flight && null!=date) {
			mapa.updateMap(date, flight.getFlightNumber());
		}
	}
	
	class BotonCambiarTickets implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			actualizarTickets();
			actualizarMapa();
		}
	}
	
	class BotonCambiarMapas implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			actualizarMapa();
		}
	}
	
	class CancelTicketListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			CancelTIcketVentana dialog= new CancelTIcketVentana(AirlineApp.this, servicio,fechaActualTexto);
			dialog.setVisible(true);	
			if(dialog.isEstaCancelado()==true) {
				actualizarTickets();
				actualizarMapa();
	     	}
	    }
	}

}
