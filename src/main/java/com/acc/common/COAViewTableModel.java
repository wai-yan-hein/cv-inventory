/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.common;

import com.acc.model.VCOALv3;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author MyoGyi
 */
public class COAViewTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(COAViewTableModel.class);
    private List<VCOALv3> listCOA = new ArrayList();
    private final String[] columnNames = {"System Code", "User Code", "COA Name",
        "Group System Code", "Group User Code", "Group COA Name", "Head System Code", "Head User Code", "Head COA Name"};
    private JTable parent;

    @Override
    public int getRowCount() {
        return listCOA == null ? 0 : listCOA.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            VCOALv3 coa = listCOA.get(row);
            return switch (column) {
                case 0 ->
                    coa.getCoaCode();
                case 1 ->
                    coa.getCoaUsrCode();
                case 2 ->
                    coa.getCoaNameEng();
                case 3 ->
                    coa.getCoaCodeParent2();
                case 4 ->
                    coa.getCoaUsrCodeParent2();
                case 5 ->
                    coa.getCoaNameEngParent2();
                case 6 ->
                    coa.getCoaCodeParent3();
                case 7 ->
                    coa.getCoaUsrCodeParent3();
                case 8 ->
                    coa.getCoaNameEngParent3();
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
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;

    }

    public VCOALv3
            getChartOfAccount(int row) {
        return listCOA.get(row);
    }

    public void addCoa(VCOALv3 coa) {
        listCOA.add(coa);
        fireTableRowsInserted(listCOA.size() - 1, listCOA.size() - 1);
    }

    public void setCoaGroup(int row, VCOALv3 coa) {
        if (!listCOA.isEmpty()) {
            listCOA.set(row, coa);
            fireTableRowsUpdated(row, row);
        }
    }

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public List<VCOALv3> getListCOA() {
        return listCOA;
    }

    public void setListCOA(List<VCOALv3> listCOA) {
        this.listCOA = listCOA;
        fireTableDataChanged();
    }

    public void clear() {
        if (listCOA != null) {
            listCOA.clear();
            fireTableDataChanged();
        }
    }
}
