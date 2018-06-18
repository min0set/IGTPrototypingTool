package userinterface;

import algorithm.*;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.LayoutManager;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;//import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.net.*;
import inputOutput.*; 


public class Gui extends JFrame implements ActionListener{
	
	private JButton start = new JButton("Starte Messung");
	private JButton finish = new JButton("Beende Messung");
	private JButton start2 = new JButton("Starte 2.Messung");
	private JButton finish2 = new JButton("Ende 2.Messung"); 
	private JButton loadData = new JButton("Lade Datei");
	private JButton calculate = new JButton("Berechne");
	private JTextField xEbene = new JTextField(25);
	private JTextField yEbene = new JTextField(25);
	private JTextField zEbene = new JTextField(25);
	private JTextField adresse= new JTextField(25); 
	private String[] messungen = {"Rauschen", "Korrektheit"}; 
	private JComboBox messarten = new JComboBox(messungen);
	private JCheckBox cBJitter = new JCheckBox("Jitter", false);
	private JCheckBox cBCorrectness = new JCheckBox("Korrektheit", false);
	private JCheckBox cBAccuracy = new JCheckBox("Genauigkeit", false);
	JPanel pCenter = new JPanel();
	JLabel xAxis = new JLabel(); JLabel zAxis = new JLabel();JLabel yAxis = new JLabel();
	JMenuBar bar; JMenu menu ; 
	JMenuItem openItem; JMenuItem closeItem;
	JLabel lValue = new JLabel();
	JLabel lCalcJ = new JLabel();  
	JLabel lCalcC = new JLabel();  
	JLabel lCalcA = new JLabel();  
	File f; 
	DataService dataS = new DataService();
	
	
	public Gui(){
		//allow window
		enableEvents(AWTEvent.WINDOW_EVENT_MASK); 
		init();	
	}
	
	private void init(){
		//NorthPanel
		JPanel panelNorth = new JPanel();
		//Searching for data file
		bar = new JMenuBar();
		menu = new JMenu("Dateisuche ");
		openItem = new JMenuItem("Oeffnen");
	    closeItem = new JMenuItem("Schliessen"); 
	    menu.add(openItem);
	    menu.add(closeItem);
	    bar.add(menu);
	    panelNorth.add(bar);
	    
		JLabel l0 = new JLabel(" CSV-Dateipfad:");
		panelNorth.add(l0); 
		panelNorth.add(adresse); 
		panelNorth.add(loadData);
		
		//Coordinate
		xAxis.setText("x-Achse"); 
		xAxis.setBounds(150, 130, 100, 20);
		add(xAxis);
		xEbene.setBounds(90, 150, 350, 150);
		add(xEbene);
		
		yAxis.setText("y-Achse");
		yAxis.setBounds(150, 300, 100, 20);
		add(yAxis);
		yEbene.setBounds(90, 320, 350, 150);
		add(yEbene);
		
		zAxis.setText("z-Achse");
		zAxis.setBounds(150, 490, 100, 20);
		add(zAxis);
		zEbene.setBounds(90, 510, 350, 150);
		add(zEbene);

		JLabel measuredTyp = new JLabel("Messarten"); 
		measuredTyp.setBounds(650, 80, 120, 60);
		add(measuredTyp);
		messarten.setEditable(true);
		messarten.setBounds(800, 100, 120, 20);
		add(messarten); 
		start.setBounds(650, 170, 130, 60);
		add(start);
		start.setForeground(Color.GREEN);
		finish.setBounds(800, 170, 130, 60);
		add(finish);
		finish.setForeground(Color.RED);

		cBJitter.setBounds(650, 400, 150, 30);
		cBCorrectness.setBounds(650, 420, 150, 30);
		cBAccuracy.setBounds(650, 440, 150, 30);
		add(cBJitter);
		add(cBCorrectness);
		add(cBAccuracy);
		setLayout(null);
		calculate.setBounds(800, 400, 130, 60);
		add(calculate);

		//Connection with ActionListener
		start.addActionListener(this);
		finish.addActionListener(this);
		loadData.addActionListener(this);
		calculate.addActionListener(this);
		messarten.addActionListener(this);
		start2.addActionListener(this);
		finish2.addActionListener(this);
		cBJitter.addActionListener(this);
		cBCorrectness.addActionListener(this);
		cBAccuracy.addActionListener(this);
		
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.getContentPane();
		this.add(panelNorth, BorderLayout.NORTH);
	
		openItem.addActionListener(new java.awt.event.ActionListener() {
	        //opens actionPerformed by clicking openItem
	        public void actionPerformed(java.awt.event.ActionEvent e) {
	      
	            FileFilter filter = new FileNameExtensionFilter("Testreihe", "csv");
			JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			fc.addChoosableFileFilter(filter);
			int returnValue = fc.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION){
			    File selctedFile = fc.getSelectedFile();
			    String path2 = selctedFile.getAbsolutePath();
			    CSVFileReader.setPath(path2);     
			}

	        }});}

	
		public void actionPerformed(ActionEvent e) {
				Object src = e.getSource();
				FileFilter filter; 
				String path, text;
				double correctness, accuracy;
				AverageMeasurement aM2; 
				Diagramm Diag = new Diagramm();
				DataProcessor dP = new DataProcessor(); 
				
				try{ 
					if(src == loadData){ 
						f = new File(adresse.getText());
						path = f.getAbsolutePath();
						if( f.exists()== true && path.endsWith(".csv")){
							CSVFileReader.setPath(path);
							
						}else{
							JOptionPane.showMessageDialog(null, "Ungueltiger Dateityp", 
									"Warnung", JOptionPane.WARNING_MESSAGE);
						}
						
					}else if(src == start || src == start2  ){
							
							JOptionPane.showMessageDialog(null, "Jetzt das Geraet ruhig liegen lassen", "Warnung", JOptionPane.WARNING_MESSAGE);
							
							//accuracy= dataS.getAccuracy(expectedDistance, firstAverangeMeasurement, secondAverangeMeasurement);  
							
					}else if(src == finish || src == finish2 ){
						//Diag.start(stage);
						//Coordinatensystem with x,y,z-value
							
					}else if(src == messarten){
						String selected = (String) messarten.getSelectedItem();
						if("Korrektheit".equals(selected)){
							start2.setBounds(650, 280, 130, 60);
							add(start2);
							start2.setForeground(Color.GREEN);
							start2.setEnabled(true);
							finish2.setBounds(800, 280, 130, 60);
							add(finish2);
							finish2.setForeground(Color.RED);
							finish2.setEnabled(true);
						}if("Rauschen".equals(selected)){							
							start2.setEnabled(false);
							finish2.setEnabled(false);
						}
 
					}else if(src == calculate ){
						int countToGetNext= 5; 
						List<ToolMeasure> toolMeas = dataS.loadNextData(countToGetNext);
						double jitter = 0; 
						for(int i =0 ; i< toolMeas.size(); i++){
							ToolMeasure tool = toolMeas.get(i); 
							tool.getMeasurement(); 
							AverageMeasurement aM = tool.getAverageMeasurement(); 
							jitter = aM.getError();
						//	boxplot = aM.getBoxPlot(); 
							//firstMeas = aM.g
						}
						
						lValue.setText("Errechneter Wert");
						lValue.setBounds(650, 510, 130, 30);
						add(lValue);
						lValue.setForeground(Color.BLUE);
						//validate();
						lCalcJ.setBounds(800, 510, 200, 30);
						add(lCalcJ);
						lCalcC.setBounds(800, 540, 200, 30);
						add(lCalcC);
						lCalcA.setBounds(800, 570, 200, 40);
						add(lCalcA);
						if(cBJitter.isSelected()){
							lCalcJ.setText(" Jitter = " + jitter);  
						}if(cBCorrectness.isSelected()){
							lCalcC.setText(" Korrektheit  = ");  
						}
					} 	
				}catch(Exception ep){
					if(f.exists()==false){
						JOptionPane.showMessageDialog(null,"Dateipfad existiert nicht",
								"Fenstertitel",JOptionPane.ERROR_MESSAGE);
					}else{
						//ExceptionReporting.registerExceptionReporter();
						JOptionPane.showMessageDialog(null,"Fehlermeldung","Fenstertitel",JOptionPane.ERROR_MESSAGE);
					} 

				}					
			};	
	
		//close window
				protected void processWindowsEvent(WindowEvent e){
				super.processWindowEvent(e);
					if (e.getID() == WindowEvent.WINDOW_CLOSING) {
						System.exit(0); //Prgm wird beendet
					}
				}

		
}
