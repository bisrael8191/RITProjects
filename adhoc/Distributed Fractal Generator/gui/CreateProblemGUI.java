package gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
 
public class CreateProblemGUI extends JFrame implements ActionListener{

	//Parameters
	JTextField name;
	JTextField author;
	
	//Image parameters
	JTextField width;
	JTextField height;
	JTextField centerX;
	JTextField centerY;
	JTextField pixelsPerUnit;
	
	
	//Fractal Parameters specified by class file, in table
	JTable fractalParams;
	
	ParameterTableModel tableModel;
	
	//BUTTONS
	JButton createButton;
	JButton cancelButton;
	
	
	//The strings for arguments
	String[] paramTypes;
	String[] paramValues;
	
	
	//The interface to this GUI
	CreateProblemInter GUIinterface;
	
	/*
	 * Create the Problem create window
	 */
	public CreateProblemGUI(CreateProblemInter inter, String[] paramTypes){

		super("Create a new Fractal");
		
		GUIinterface = inter;
		
		this.paramTypes = paramTypes;
		
		
		Container c = getContentPane();
		
		setSize(600,400);
		setVisible(true);
		setLayout(new BorderLayout());
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.LEFT);
		JPanel row1 = new JPanel(flow);
		//row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
		JPanel row2 = new JPanel(flow);
		JPanel row3 = new JPanel(flow);
		JPanel row4 = new JPanel(flow);
		
		row1.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		row2.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		row3.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		row4.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		
		name = new JTextField("", 10);
		author = new JTextField("", 10);
		width = new JTextField("", 5);
		height = new JTextField("", 5);
		centerX = new JTextField("", 6);
		centerY = new JTextField("", 6);
		pixelsPerUnit = new JTextField("", 5);
		
		row1.add(new JLabel("Fractal Name"));
		row1.add(name);
		row1.add(new JLabel("Author"));
		row1.add(author);
		row1.add(new JPanel());
		row1.add(new JPanel());
		
		row2.add(new JLabel("Image Width"));
		row2.add(width);
		row2.add(new JLabel("Image Height"));
		row2.add(height);
		row2.add(new JPanel());
		row2.add(new JPanel());
		
		row3.add(new JLabel("Center Point (X,Y)"));
		row3.add(centerX);
		row3.add(centerY);
		row3.add(new JPanel());
		row3.add(new JPanel());
		row3.add(new JPanel());
		
		row4.add(new JLabel("Pixels per Unit"));
		row4.add(pixelsPerUnit);
		row4.add(new JPanel());
		row4.add(new JPanel());
		row4.add(new JPanel());
		row4.add(new JPanel());
		
		panel.add(row1);
		panel.add(row2);
		panel.add(row3);
		panel.add(row4);
		
		c.add(panel, BorderLayout.NORTH);
		
		tableModel = new ParameterTableModel(paramTypes.length, paramTypes);
		
		

		fractalParams = new JTable(tableModel);
		
		fractalParams.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		fractalParams.setPreferredSize(new Dimension(400,300));
		fractalParams.setCellSelectionEnabled(true);
		
		JScrollPane tableScroller = new JScrollPane(fractalParams);
		
		c.add(tableScroller, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		
		createButton = new JButton("Create Problem");
		cancelButton = new JButton("Cancel");
		
		buttonPanel.add(createButton);
		buttonPanel.add(cancelButton);
		
		createButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		
		c.add(buttonPanel, BorderLayout.SOUTH);
		
		repaint();
	
	}
	
	/*
	 * Action performed
	 */
	public void actionPerformed(ActionEvent e){
		
		if(e.getActionCommand() == "Cancel"){
			//shut this window down
			dispose();
		}
		else if(e.getActionCommand() == "Create Problem"){
			
			/**
			 * Here needs to take the input data, and create a new Problem tuple and
			 * start it working on it.
			 */
			
			fractalParams.editCellAt(0,0);
			
//			try{
//				//Check if first top parameters are valid
//				int w = Integer.parseInt(width.getText());
//				int h = Integer.parseInt(height.getText());
//				
//				double x = Double.parseDouble(centerX.getText()),
//				y = Double.parseDouble(centerY.getText()),
//				res = Double.parseDouble(pixelsPerUnit.getText());
//				
//				if(name.getText() != "" &&
//							author.getText() != "" && w > 0 && h > 0){
//					
//					//Now check if the function parameters are not empty
//					paramValues = tableModel.getParamValues();
//					boolean test = false;
//					for(int i = 0; i < paramValues.length; i++){
//						
//						if(paramValues[i] == ""){
//							test = true;
//							break;
//						}
//					
//					}
//					if(test){
//						JOptionPane.showMessageDialog(this, "Function Parameters left empty or are invalid.", "Error", JOptionPane.ERROR_MESSAGE);
//					}
//					
//					//Else you passed all your initial test, pass the problem variables on to the creator
//					else{
//						
//						GUIinterface.createProblem(name.getText(), author.getText(), h, w, x, y, res, paramValues);
//						dispose();
//					}
//					
//				}
//				else{
//					JOptionPane.showMessageDialog(this, "Normal Parameters left empty or areinvalid.", "Error", JOptionPane.ERROR_MESSAGE);
//				}
//				
//				
//				
//				
//			}catch(NumberFormatException ed){			
//				JOptionPane.showMessageDialog(this, "Number parameters not parsed as numbers, enter again.", "Error", JOptionPane.ERROR_MESSAGE);
//			}
			
			paramValues = tableModel.getParamValues();
			
			int w = Integer.parseInt(width.getText());
			int h = Integer.parseInt(height.getText());
			double x = Double.parseDouble(centerX.getText()),
			y = Double.parseDouble(centerY.getText()),
			res = Double.parseDouble(pixelsPerUnit.getText());
			GUIinterface.createProblem(name.getText(), author.getText(), h, w, x, y, res, paramValues);
			dispose();
		
			
		}
		
	}	
	
}
