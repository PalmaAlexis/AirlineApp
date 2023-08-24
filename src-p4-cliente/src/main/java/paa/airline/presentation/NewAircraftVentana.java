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
import javax.swing.JSpinner;
import javax.swing.JTextField;

import paa.airline.business.AirlineService;
import paa.airline.business.AirlineServiceException;
import paa.airline.model.AircraftType;

public class NewAircraftVentana extends JDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AirlineService servicio;
	private AircraftType at;
	private boolean flag=false;
	private JTextField ManufacturerField;
	private JTextField ModelField;
	private JSpinner SpinnerRows;
	private JSpinner SpinnerColumns;
	
	
	public NewAircraftVentana(JFrame dueño, AirlineService servicio) {
		super(dueño, "Nuevo Aircraft", true);  //true para hacerlo modal
		this.servicio=servicio;
		
		
		JPanel panel= new JPanel();
		JLabel ManufacturerLabel= new JLabel("Manufacturer:");    this.ManufacturerField= new JTextField(20);
		JLabel ModelLabel= new JLabel("Model:");                  this.ModelField= new JTextField(20);
		JLabel SeatRowsLabel= new JLabel("Seat Rows:");           this.SpinnerRows= new JSpinner();
		JLabel SeatColumnsLabel= new JLabel("Seat Columns:");     this.SpinnerColumns= new JSpinner();
		panel.setLayout(new GridLayout(4,2)  );
		panel.add(ManufacturerLabel);     panel.add(ManufacturerField);
		panel.add(ModelLabel);            panel.add(ModelField);
		panel.add(SeatRowsLabel);         panel.add(SpinnerRows);
		panel.add(SeatColumnsLabel);      panel.add(SpinnerColumns);
		
		JPanel panelBotones= new JPanel();
		JButton OKbutton= new JButton("OK");
		OKbutton.addActionListener(new BotonOK());
		JButton CancelButton= new JButton("Cancel");
		CancelButton.addActionListener(new BotonCancel());
		panelBotones.setLayout(new GridLayout(1, 2));
		panelBotones.add(OKbutton);
		panelBotones.add(CancelButton);
		
		
		this.setLayout(new BorderLayout());
		this.add(panel,BorderLayout.NORTH);
		this.add(panelBotones, BorderLayout.SOUTH);
		
		this.pack();
		this.setLocationRelativeTo(dueño);
	}
	

	class BotonCancel implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			NewAircraftVentana.this.dispose();
		}
	}

    class BotonOK implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				at=servicio.createAircraft(ManufacturerField.getText(), ModelField.getText(), (int)SpinnerRows.getValue(),  (int)SpinnerColumns.getValue() );
				NewAircraftVentana.this.setVisible(false);
				flag=true;
			} catch (NumberFormatException nfe) {
				JOptionPane.showConfirmDialog(NewAircraftVentana.this, "Debes llenar todos los campos", "Error al crear AircraftType", JOptionPane.ERROR_MESSAGE);
			} catch (AirlineServiceException e1) {
				JOptionPane.showConfirmDialog(NewAircraftVentana.this, e1.getMessage(), "Error al crear AircraftType", JOptionPane.ERROR_MESSAGE);
			}
		}
    	
    }
    
	public boolean isCompletado() {
		return flag;
	}
	
	public AircraftType getAt() {
		return at;
	}


}
