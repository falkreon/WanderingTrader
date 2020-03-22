package blue.endless.wtrader.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class ListTableModel<T> implements TableModel {
	private List<T> data = new ArrayList<>();
	
	private List<String> columnNames = new ArrayList<>();
	private List<Function<T, Object>> extractors = new ArrayList<>();
	
	private List<TableModelListener> listeners = new ArrayList<>();
	
	public void addColumn(String name, Function<T, Object> extractor) {
		this.columnNames.add(name);
		this.extractors.add(extractor);
	}
	
	@Override
	public void addTableModelListener(TableModelListener listener) {
		listeners.add(listener);
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return Object.class;
	}

	@Override
	public int getColumnCount() {
		return extractors.size();
	}

	@Override
	public String getColumnName(int column) {
		if (column<0||column>=columnNames.size()) return "";
		return columnNames.get(column);
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		//System.out.println("get row: "+row+" col: "+col);
		if (row<0 || row>=data.size()) return null;
		if (col<0 || col>=extractors.size()) return null;
		
		T dataRow = data.get(row);
		return extractors.get(col).apply(dataRow);
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		//This isn't supported.
	}

	public void addRow(T t) {
		data.add(t);
		for(TableModelListener listener : listeners) {
			System.out.println("Notifying "+listener);
			listener.tableChanged(new TableModelEvent(this));
		}
	}

}
