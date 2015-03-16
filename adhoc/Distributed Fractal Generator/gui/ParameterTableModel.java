/**
 * 
 */
package gui;

import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 * @author Jacob Hays
 * 
 * This class holds the data for the parameter table that the user uses to set up a fratal
 * There is a maximum of 24 parameters that the table can display.
 */
public class ParameterTableModel extends DefaultTableModel {

	
	
	private String[] paramTypes = new String[0];
	private String[] paramValue = new String[0];
	
	
	private int numValues;
	
	public ParameterTableModel(int numValues, String[] types){

		this.numValues = numValues;
		
		paramTypes = types;
		paramValue = new String[types.length];
		for(int i = 0; i < numValues; i++)
			paramValue[i] = "";
		
	}

	public int getColumnCount() {
		return 3;
	}

	/* 
	 * Return column name
	 */
	public String getColumnName(int col) {
		// TODO Auto-generated method stub
		switch(col){
		case(0): return "Param Name";
		case(1): return "Param Type";
		case(2): return "Param Value";
		default: return "";
		}
	}

	/* 
	 * return row count
	 */
	public int getRowCount() {
		return numValues;
	}

	/* 
	 * Only last column is editable
	 */
	public boolean isCellEditable(int row, int col) {
		
		if(col < 2)
			return false;
		else
			return true;
	}
	
   
    public Object getValueAt(int row, int col) {
		switch(col){
		case(0): return "arg" + row; 
		case(1): return paramTypes[row];
		case(2): return paramValue[row];
		default: return null;
		}
    }

    public String[] getParamValues(){
    	return paramValue;
    }
    
    
    public void setValueAt(Object value, int row, int col) {
		switch(col){
		case(0): break;
		case(1): break;
		case(2): paramValue[row] = (String)value;
		}
    }
}
