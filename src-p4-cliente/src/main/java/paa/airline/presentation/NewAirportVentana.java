package paa.airline.presentation;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import paa.airline.business.AirlineService;
import paa.airline.business.AirlineServiceException;
import paa.airline.model.Airport;
import paa.airline.util.AirportQuery;

public class NewAirportVentana extends JDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AirlineService servicio;
	private Airport a;
	private boolean flag=false;
	private JTextField iataCodeField;
	private JTextField NameField;
	private JTextField CityCodeField;
	private JTextField LongitudeField; //agregar this. cuando vayamos a usar un private
	private JTextField LatitudeField;   //solo cuando vamos a especificar que son de esta clase
	
	
	public NewAirportVentana(JFrame dueño, AirlineService servicio) {
		super(dueño, "Nuevo Aereopuerto", true);
		this.servicio=servicio;
		
		
		JPanel panel= new JPanel();
		this.iataCodeField= new JTextField(20);   JLabel iataCodeLabel= new JLabel("IATA Code:");
		this.NameField= new JTextField(20);       JLabel NameLabel= new JLabel("Name:");
		this.CityCodeField= new JTextField(20);   JLabel CityCodeLabel= new JLabel("CityCode:");
		this.LongitudeField= new JTextField(20);  JLabel LongitudeLabel= new JLabel("Longitude:");
		this.LatitudeField= new JTextField(20);   JLabel LatitudeLabel= new JLabel("Latitude:");
		panel.setLayout(new GridLayout(5, 2));
		panel.add(iataCodeLabel);   panel.add(iataCodeField); 
		panel.add(NameLabel);       panel.add(NameField);
		panel.add(CityCodeLabel);   panel.add(CityCodeField);
		panel.add(LongitudeLabel);  panel.add(LongitudeField);
		panel.add(LatitudeLabel);   panel.add(LatitudeField);  
		
		JPanel panelBotones= new JPanel();
		JButton OKButton= new JButton("OK");
		OKButton.addActionListener(new BotonOK());
		JButton CancelButton= new JButton("Cancel");
		CancelButton.addActionListener(new BotonCancel());
		JButton AutofillButton= new JButton("Autofill");
		AutofillButton.addActionListener(new BotonAutofill());
		panelBotones.setLayout(new GridLayout(1, 3));
		panelBotones.add(OKButton);   panelBotones.add(CancelButton);   panelBotones.add(AutofillButton);
		
		this.setLayout(new BorderLayout());
		this.add(panel,BorderLayout.NORTH);
		this.add(panelBotones, BorderLayout.SOUTH);
		
		this.pack();
		this.setLocationRelativeTo(dueño);
	}
	
	class BotonOK implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				try {
					a=servicio.createAirport(iataCodeField.getText(), CityCodeField.getText(), NameField.getText(), Double.parseDouble(LongitudeField.getText()), Double.parseDouble(LatitudeField.getText() )   );
					setVisible(false); //escondemos ventana porque ya se creo
					flag=true;     //indicador que se completo
				} catch (AirlineServiceException e1) {
					JOptionPane.showMessageDialog(NewAirportVentana.this, e1.getMessage(), "Error al crear Aereopuerto", JOptionPane.ERROR_MESSAGE);
					}  //   .this cuando hablamos de la clase padre
				}catch (NumberFormatException nfe) {
					JOptionPane.showConfirmDialog(NewAirportVentana.this, "Llena todos los campos", "Error al crear Aereopuerto", JOptionPane.ERROR_MESSAGE);
				    }
			 
		}
		
	}
		
	class BotonCancel implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			NewAirportVentana.this.dispose();     //.this cuando se trata del JFrame padre
		}
	}
	
	class BotonAutofill implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			NameField.setText(AirportQuery.getName( iataCodeField.getText() ) );
			CityCodeField.setText(  AirportQuery.getLocation(iataCodeField.getText()));
			LongitudeField.setText(Double.toString(AirportQuery.getLongitude(iataCodeField.getText() )  )  );
			LatitudeField.setText( Double.toString(AirportQuery.getLatitude( iataCodeField.getText() )  )  );
		}
		
	}

	public boolean estaCompletado() {
		return this.flag;  //solo se usa this. cuando se va a interactuar con otras clases
	}


	public Airport getA() {
		return a;
	}
	



    
}
