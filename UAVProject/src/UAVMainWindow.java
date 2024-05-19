
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.print.attribute.AttributeSet;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.Timestamp;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;

public class UAVMainWindow {

	private JFrame frame;
	private JLabel topLabel;
	private JLabel lowLabel;

	private JTextField uavNameTextField;
	private JTextField uavBatteryTextField;
	private JTextField uavLattitudeTextField;
	private JTextField uavLongtitudeTextField;
	private JTextField uavAltitudeTextField;
	private JTextField uavSpeedTextField;

	private UavController uavController;

	private final String[] columnNames = { "UavId", "UavCode", "UavBatteryPercentage", "UavGeoPosition", "UavSpeed",
			"UavTotalFlightTime" };

	private DefaultTableModel uiTableModel = new DefaultTableModel();
	private JTable uiTable = new JTable();

	JPopupMenu popupMenu = new JPopupMenu();
	JMenuItem menuItemDelete = new JMenuItem("Delete");
	JMenuItem menuItemUpdate = new JMenuItem("Update");

	private Timer uiUpdateTimer = new Timer();

	JFrame newUpdateFrame = new JFrame("UAV Update Screen");

	JLabel newUpdateLabel = new JLabel();
	JLabel uavNameUptLabel = new JLabel("UAV Name:");

	JLabel uavBatteryPercentageUptLabel = new JLabel("UAV Battery Percentage:");
	JLabel uavLatitudeUptLabel = new JLabel("UAV Latitude:");
	JLabel uavLongtitudeUptLabel = new JLabel("UAV Longtitude:");
	JLabel uavAltitudeUptLabel = new JLabel("UAV Altitude:");
	JLabel uavSpeedUptLabel = new JLabel("UAV Speed:");

	JTextField uavNameUptTextField = new JTextField();
	JTextField uavBatteryUptTextField = new JTextField();
	JTextField uavLattitudeUptTextField = new JTextField();
	JTextField uavLongtitudeUptTextField = new JTextField();
	JTextField uavAltitudeUptTextField = new JTextField();
	JTextField uavSpeedTextUptField = new JTextField();

	JButton updateButton = new JButton("UPDATE");
	JButton cancelButton = new JButton("CANCEL");
	boolean ascending = true;
	int sortColumnIndex;
	private JScrollPane scrollPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UAVMainWindow window = new UAVMainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public UAVMainWindow() {
		uavController = new UavController();
		initialize();
		uiTimerTask();
		tableMouseHeaderListener();

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1024, 800);
		frame.setTitle("UAV Tracking System");
		frame.setResizable(false);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		createUILabel();
		createUITextField();
		createTable();

		addUIButton();

		popUpMenuProcess();

		frame.setVisible(true);

	}

	public void addUIButton() {
		/* ADD BUTTON */
		var addButton = new JButton("ADD");

		addButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				var model = uavController.addUavModel(controlInputName(uavNameTextField.getText()),
						controlInputBattery(uavBatteryTextField.getText()),
						controlInputLattitude(uavLattitudeTextField.getText()),
						controlInputLongtitude(uavLongtitudeTextField.getText()),
						controlInputAltitude(uavAltitudeTextField.getText()),
						controlInputSpeed(uavSpeedTextField.getText()));
				
					uavNameTextField.setText("");
					uavBatteryTextField.setText("");
					uavLattitudeTextField.setText("");
					uavLongtitudeTextField.setText("");
					uavAltitudeTextField.setText("");
					uavSpeedTextField.setText("");

			}
		});
		addButton.setBounds(851, 80, 89, 23);
		topLabel.add(addButton);

	}

	public void createUILabel() {

		topLabel = new JLabel("");
		topLabel.setBounds(0, 0, 998, 103);
		frame.getContentPane().add(topLabel);

		lowLabel = new JLabel("");
		lowLabel.setBounds(0, 103, 998, 798);
		frame.getContentPane().add(lowLabel);

		/* UAV NAME */
		JLabel uavNameLabel = new JLabel("AUV Name:");
		uavNameLabel.setBounds(20, 11, 123, 19);
		topLabel.add(uavNameLabel);

		/* Battery */
		JLabel uavBatteryPercentageLabel = new JLabel("UAV Battery Percentage:");
		uavBatteryPercentageLabel.setBounds(20, 44, 142, 17);
		topLabel.add(uavBatteryPercentageLabel);

		/* UAV LAT */
		JLabel uavLatitudeLabel = new JLabel("Latitude:");
		uavLatitudeLabel.setBounds(370, 12, 80, 17);
		topLabel.add(uavLatitudeLabel);

		/* UAV LONGT */
		JLabel uavLongtitudeLabel = new JLabel("Longtitude:");
		uavLongtitudeLabel.setBounds(370, 44, 80, 17);
		topLabel.add(uavLongtitudeLabel);

		/* UAV ALTITUDE */
		JLabel lblNewLabel_2_1 = new JLabel("Altitude:");
		lblNewLabel_2_1.setBounds(686, 13, 80, 17);
		topLabel.add(lblNewLabel_2_1);

		/* UAV SPEED */
		JLabel lblNewLabel_2_2 = new JLabel("Speed:");
		lblNewLabel_2_2.setBounds(686, 45, 80, 17);
		topLabel.add(lblNewLabel_2_2);
	}

	public void createUITextField() {
		
		/* UAV NAME TEXT FIELD */
		uavNameTextField = new JTextField();
		uavNameTextField.setText("");
		uavNameTextField.setBounds(173, 10, 154, 20);
		topLabel.add(uavNameTextField);
		uavNameTextField.setColumns(10);

		/* UAV BATTERY TEXT FIELD */
		uavBatteryTextField = new JTextField();
		uavBatteryTextField.setText("");
		uavBatteryTextField.setBounds(173, 42, 154, 20);
		topLabel.add(uavBatteryTextField);
		uavBatteryTextField.setColumns(10);

		/* UAV LATTITUDE TEXT FIELD */
		uavLattitudeTextField = new JTextField();
		uavLattitudeTextField.setText("");
		uavLattitudeTextField.setColumns(10);
		uavLattitudeTextField.setBounds(452, 10, 154, 20);
		topLabel.add(uavLattitudeTextField);

		/* UAV LONGTITUDE TEXT FIELD */
		uavLongtitudeTextField = new JTextField();
		uavLongtitudeTextField.setText("");
		uavLongtitudeTextField.setColumns(10);
		uavLongtitudeTextField.setBounds(452, 42, 154, 20);
		topLabel.add(uavLongtitudeTextField);

		/* UAV ALTITUDE TEXT FIELD */
		uavAltitudeTextField = new JTextField();
		uavAltitudeTextField.setText("");
		uavAltitudeTextField.setColumns(10);
		uavAltitudeTextField.setBounds(786, 10, 154, 20);
		topLabel.add(uavAltitudeTextField);

		/* UAV SPEED TEXT FIELD */
		uavSpeedTextField = new JTextField();
		uavSpeedTextField.setText("");
		uavSpeedTextField.setColumns(10);
		uavSpeedTextField.setBounds(786, 42, 154, 20);
		topLabel.add(uavSpeedTextField);
	}
	
	public String controlInputName(String name)
	{
		if (name.length() > 32) {
            return name.substring(0, 32);
        }
		if(name.isEmpty())
		{
			name = "EMPTY";
		}
        return name;
	}
	
	public String controlInputBattery(String battery)
	{
		String numbersOnly = battery.replaceAll("[^\\d.]", "");
		float batteryLevel;
		
		if(numbersOnly.isEmpty())
		{
			numbersOnly = "100";
		}
		
		try {
            batteryLevel = Float.parseFloat(numbersOnly);

            if (batteryLevel < 0) {
                batteryLevel = 0;
            } else if (batteryLevel > 100) {
                batteryLevel = 100;
            }
        } catch (NumberFormatException e) {
            batteryLevel = -1; // Hatalı giriş durumunda geçersiz bir değer ata
            System.out.println("Girilen değer geçerli bir sayı değil.");
        }
		return String.valueOf(batteryLevel);
	}
	
	public String controlInputLattitude(String lattitude)
	{
		String numbersOnly = lattitude.replaceAll("[^\\d.]", "");
		float min = (float) 10.00;
		float max = (float) 45.00;
		
		if(numbersOnly.isEmpty())
		{
			numbersOnly = "10.00";
		}
		
		if (Float.parseFloat(numbersOnly) < -90.0) {
            return String.valueOf(min);
        } else if (Float.parseFloat(numbersOnly) > 90.0) {
            return String.valueOf(max);
        }
        return numbersOnly;
	}
	
	public String controlInputLongtitude(String lattitude)
	{
		String numbersOnly = lattitude.replaceAll("[^\\d.]", "");
		float min = (float) 12.00;
		float max = (float) 65.32;
		
		if(numbersOnly.isEmpty())
		{
			numbersOnly = "12.00";
		}
		
		if (Float.parseFloat(numbersOnly) < -180.0) {
            return String.valueOf(min);
        } else if (Float.parseFloat(numbersOnly) > 180.0) {
            return String.valueOf(max);
        }
        return numbersOnly;
    }
		
	
	public String controlInputAltitude(String altitude)
	{
		String numbersOnly = altitude.replaceAll("[^\\d.]", "");
		if(numbersOnly.isEmpty())
		{
			numbersOnly = "0";
		}
		return numbersOnly;
	}
	
	public String controlInputSpeed(String speed)
	{
		String numbersOnly = speed.replaceAll("[^\\d.]", "");
		
		if(numbersOnly.isEmpty())
		{
			numbersOnly = "0.0";
		}
		
		float floatSpeed = Float.parseFloat(numbersOnly);
		
		
		if(floatSpeed >= 0 && floatSpeed <= 5)
		{
			return numbersOnly;
		}
		else
		{
			return "1.0";
		}
	}

	private void uiTimerTask() {
		TimerTask updateTask = new TimerTask() {
			@Override
			public void run() {
				var dtos = uavController.createDtosForUiUpdate();
				uavController.fillDtosSpecificArea(dtos, sortColumnIndex, ascending);

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						updateUiTable(dtos);
					}
				});
			}
		};

		uiUpdateTimer.schedule(updateTask, 0, 100);

	}

	public void updateUiTable(Vector<UavModelDto> dtos) {
		var currRowCount = uiTableModel.getRowCount();
		var deltaRowCount = currRowCount - dtos.size();

		if (deltaRowCount < 0) {
			for (var i = 0; i < -deltaRowCount; i++) {
				var data = new Object[6];
				uiTableModel.addRow(data);
			}
		} else {
			for (var i = 0; i < deltaRowCount; i++) {
				uiTableModel.removeRow(currRowCount - 1);
			}
		}


		for (var dto : dtos) {
			uiTableModel.setValueAt(dto.uavId, dto.rowIndex, 0);
			uiTableModel.setValueAt(dto.uavCode, dto.rowIndex, 1);
			uiTableModel.setValueAt(dto.uavBatteryPercentage, dto.rowIndex, 2);
			uiTableModel.setValueAt(dto.uavGeoPosition, dto.rowIndex, 3);
			uiTableModel.setValueAt(dto.uavSpeed, dto.rowIndex, 4);
			uiTableModel.setValueAt(dto.aliveTimeStr, dto.rowIndex, 5);
		}
		
		var cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            	for (var dto : dtos) {
        			if(dto.rowIndex == row)
        			{
        				if(dto.orangeBackground == true)
        					setBackground(new Color(255, 165, 0));
        				else
        					setBackground(null);
        			}
        		}
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        
        for (int i = 0; i < uiTable.getColumnCount(); i++) {
        	uiTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
	}

	public void createTable() 
	{
		uiTableModel.setColumnIdentifiers(columnNames);

		uiTable.setModel(uiTableModel);

		popupMenu.add(menuItemDelete);
		popupMenu.add(menuItemUpdate);

		uiTable.getTableHeader().setBounds(5, 0, 997, 25);
		uiTable.setBounds(5, 25, 997, 798);
		uiTable.setComponentPopupMenu(popupMenu);
		uiTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		
		lowLabel.add(uiTable.getTableHeader());
		lowLabel.add(uiTable);
		

		tableMouseListener();
	}

	public void tableMouseListener() {
		uiTable.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 1) 
				{
					uiTable.setEnabled(false);

					int row = uiTable.rowAtPoint(e.getPoint());
					int column = uiTable.columnAtPoint(e.getPoint());

					if (!uiTable.isRowSelected(row)) {
						uiTable.changeSelection(row, column, true, false);
					}
				}
			}
		});
	}

	public void popUpMenuProcess() {
		menuItemDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (uiTable.getSelectedRowCount() == 1) 
				{
					int uavId = Integer.parseInt(uiTable.getModel().getValueAt(uiTable.getSelectedRow(), 0).toString());

					uavController.deleteUavModel(uavId);
				}
			}
		});

		menuItemUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (uiTable.getSelectedRowCount() == 1) 
				{
					int uavId = Integer.parseInt(uiTable.getModel().getValueAt(uiTable.getSelectedRow(), 0).toString());
					openUpdateWindow(uavId);
				}
			}
		});
	}

	public void openUpdateWindow(int uavId) {
		String[] geoPosition;
		String latitude;
		String longtitude;
		String altitude;

		newUpdateLabel.setBounds(0, 0, 100, 50);
		newUpdateLabel.setFont(new Font(null, Font.PLAIN, 25));
		newUpdateLabel.setSize(350, 290);

		uavNameUptLabel.setBounds(20, 10, 100, 20);
		newUpdateLabel.add(uavNameUptLabel);
		uavNameUptTextField.setBounds(180, 10, 150, 20);
		newUpdateLabel.add(uavNameUptTextField);

		uavBatteryPercentageUptLabel.setBounds(20, 40, 150, 20);
		newUpdateLabel.add(uavBatteryPercentageUptLabel);
		uavBatteryUptTextField.setBounds(180, 40, 150, 20);
		newUpdateLabel.add(uavBatteryUptTextField);

		uavLatitudeUptLabel.setBounds(20, 70, 150, 20);
		newUpdateLabel.add(uavLatitudeUptLabel);
		uavLattitudeUptTextField.setBounds(180, 70, 150, 20);
		newUpdateLabel.add(uavLattitudeUptTextField);

		uavLongtitudeUptLabel.setBounds(20, 100, 150, 20);
		newUpdateLabel.add(uavLongtitudeUptLabel);
		uavLongtitudeUptTextField.setBounds(180, 100, 150, 20);
		newUpdateLabel.add(uavLongtitudeUptTextField);

		uavAltitudeUptLabel.setBounds(20, 130, 150, 20);
		newUpdateLabel.add(uavAltitudeUptLabel);
		uavAltitudeUptTextField.setBounds(180, 130, 150, 20);
		newUpdateLabel.add(uavAltitudeUptTextField);

		uavSpeedUptLabel.setBounds(20, 160, 150, 20);
		newUpdateLabel.add(uavSpeedUptLabel);
		uavSpeedTextUptField.setBounds(180, 160, 150, 20);
		newUpdateLabel.add(uavSpeedTextUptField);

		updateButton.setBounds(250, 190, 80, 50);
		newUpdateLabel.add(updateButton);

		cancelButton.setBounds(20, 190, 80, 50);
		newUpdateLabel.add(cancelButton);

		newUpdateFrame.getContentPane().add(newUpdateLabel);
		newUpdateFrame.getContentPane().add(uavNameUptTextField);

		var dtos = uavController.createDtosForUiUpdate();

		for (var dto : dtos) {
			if (dto.uavId == uavId) {
				geoPosition = dto.uavGeoPosition.split("/");
				uavNameUptTextField.setText(dto.uavCode);
				uavBatteryUptTextField.setText(String.valueOf(dto.uavBatteryPercentage));
				uavLattitudeUptTextField.setText(geoPosition[0]);
				uavLongtitudeUptTextField.setText(geoPosition[1]);
				uavAltitudeUptTextField.setText(geoPosition[2]);
				uavSpeedTextUptField.setText(String.valueOf(dto.uavSpeed));

				break;
			}
		}

		updateUIButton(uavId);

		newUpdateFrame.setSize(360, 300);
		newUpdateFrame.getContentPane().setLayout(null);
		newUpdateFrame.setVisible(true);

	}

	public void updateUIButton(int uavId) {
		updateButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				uavController.updateUavModel(uavId,
						uavNameUptTextField.getText(),
						uavBatteryUptTextField.getText(),
						uavLattitudeUptTextField.getText(),
						uavLongtitudeUptTextField.getText(),
						uavAltitudeUptTextField.getText(),
						uavSpeedTextUptField.getText());
				
				closeSmallScreen();
	        }
		});

		cancelButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				uavNameUptTextField.setText(null);
				uavBatteryUptTextField.setText(null);
				uavLattitudeUptTextField.setText(null);
				uavLongtitudeUptTextField.setText(null);
				uavAltitudeUptTextField.setText(null);
				uavSpeedTextUptField.setText(null);

				closeSmallScreen();
			}
		});
	}

	public void tableMouseHeaderListener() {
		uiTable.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				var columnIndex = uiTable.columnAtPoint(e.getPoint());
				if (columnIndex == -1)
					return;

				sortColumnIndex = columnIndex;
				ascending = !ascending;
			}
		});
	}
	
	public void closeSmallScreen()
	{
		newUpdateFrame.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	            newUpdateFrame.dispose();
	        }
	    });
	    newUpdateFrame.dispatchEvent(new WindowEvent(newUpdateFrame, WindowEvent.WINDOW_CLOSING));
	}
}
