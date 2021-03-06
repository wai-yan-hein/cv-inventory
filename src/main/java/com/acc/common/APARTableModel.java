/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.VApar;
import com.common.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author winswe
 */
@Slf4j
public class APARTableModel extends AbstractTableModel {

    private List<VApar> listAPAR = new ArrayList();
    private String[] columnNames = {"Code", "Trader Name", "Currency", "Dr-Amt", "Cr-Amt"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        return switch (column) {
            case 3 ->
                Double.class;
            case 4 ->
                Double.class;
            case 5 ->
                Double.class;
            default ->
                String.class;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {

        try {
            VApar apar = listAPAR.get(row);

            return switch (column) {
                case 0 ->
                    apar.getUserCode();
                case 1 ->
                    apar.getTraderName();
                case 2 ->
                    apar.getKey().getCurCode();
                case 3 ->
                    apar.getDrAmt();
                case 4 ->
                    apar.getCrAmt();
                default ->
                    null;
            };
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    @Override
    public int getRowCount() {
        if (listAPAR == null) {
            return 0;
        }
        return listAPAR.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public List<VApar> getListAPAR() {
        return listAPAR;
    }

    public void setListAPAR(List<VApar> listAPAR) {
        this.listAPAR = listAPAR;
        fireTableDataChanged();
    }

    public VApar getAPAR(int row) {
        return listAPAR.get(row);
    }

    public void deleteAPAR(int row) {
        if (!listAPAR.isEmpty()) {
            listAPAR.remove(row);
            fireTableRowsDeleted(0, listAPAR.size());
        }

    }

    public void addAPAR(VApar apar) {
        listAPAR.add(apar);
        fireTableRowsInserted(listAPAR.size() - 1, listAPAR.size() - 1);
    }

    public void setAPAR(int row, VApar apar) {
        if (!listAPAR.isEmpty()) {
            listAPAR.set(row, apar);
            fireTableRowsUpdated(row, row);
        }
    }

    public void clear() {
        listAPAR.clear();
        fireTableDataChanged();
    }

}
