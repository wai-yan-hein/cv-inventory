/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.entry.dialog.common;

import com.common.Global;
import com.inventory.model.VSale;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author wai yan
 */
@Slf4j
public class SaleVouSearchTableModel extends AbstractTableModel {

    private List<VSale> listSaleHis = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Customer", "Remark", "Created By", "Paid Amt", "V-Total"};

    private JTable parent;

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        if (listSaleHis == null) {
            return 0;
        }
        return listSaleHis.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 6,5 -> {
                return Float.class;
            }
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            if (!listSaleHis.isEmpty()) {
                VSale his = listSaleHis.get(row);
                switch (column) {
                    case 0 -> {
                        //date
                        return his.getVouDate();
                    }
                    case 1 -> {
                        //vou-no
                        if (his.isDeleted()) {
                            return his.getVouNo() + "***";
                        } else {
                            return his.getVouNo();
                        }
                    }
                    case 2 -> {
                        //customer
                        return his.getTraderName();
                    }
                    case 3 -> {
                        //user
                        return his.getRemark();
                    }
                    case 4 -> {
                        //user
                        return Global.hmUser.get(his.getCreatedBy());
                    }
                    case 5 -> {
                        //paid
                        return his.getPaid();
                    }
                    case 6 -> {
                        return his.getVouTotal();
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        return null;
    }

    public List<VSale> getListSaleHis() {
        return listSaleHis;
    }

    public void setListSaleHis(List<VSale> listSaleHis) {
        this.listSaleHis = listSaleHis;
        fireTableDataChanged();
    }

    public VSale getSelectVou(int row) {
        if (listSaleHis != null) {
            if (!listSaleHis.isEmpty()) {
                return listSaleHis.get(row);
            }
        }
        return null;
    }
}
