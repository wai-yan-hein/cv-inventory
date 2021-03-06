/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.editor;

import com.acc.model.TraderA;
import com.common.Global;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class TraderCellEditor extends AbstractCellEditor implements TableCellEditor {

    private static final Logger log = LoggerFactory.getLogger(TraderCellEditor.class);
    private JComponent component = null;
    private TraderAAutoCompleter completer;
    private final boolean filter;
    private final int max;
    private List<TraderA> listTrader;

    public List<TraderA> getListTrader() {
        return listTrader;
    }

    public void setListTrader(List<TraderA> listTrader) {
        this.listTrader = listTrader;
    }

    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
        }

        @Override
        public void focusGained(FocusEvent e) {
            JTextField jtf = (JTextField) e.getSource();
            String lastString = jtf.getText().substring(jtf.getText().length() - 1);
            jtf.setText("");
            jtf.setText(lastString);
        }

    };

    public TraderCellEditor(List<TraderA> listTrader, boolean filter, int max) {
        this.listTrader = listTrader;
        this.filter = filter;
        this.max = max;
    }

    //private List<Medicine> listTrader = new ArrayList();
    @Override
    public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
        JTextField jtf = new JTextField();
        jtf.setFont(Global.textFont);
        //List<Medicine> listTrader = dao.findAll("Medicine", "active = true");
        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                int keyCode = keyEvent.getKeyCode();

                if ((keyEvent.isControlDown() && (keyCode == KeyEvent.VK_F8))
                        || (keyEvent.isShiftDown() && (keyCode == KeyEvent.VK_F8))
                        || (keyCode == KeyEvent.VK_F5)
                        || (keyCode == KeyEvent.VK_F7)
                        || (keyCode == KeyEvent.VK_F9)
                        || (keyCode == KeyEvent.VK_F10)
                        || (keyCode == KeyEvent.VK_ESCAPE)) {
                    stopCellEditing();
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
            }

            @Override
            public void keyTyped(KeyEvent keyEvent) {
            }
        };

        jtf.addKeyListener(keyListener);
        jtf.addFocusListener(fa);
        component = jtf;
        if (value != null) {
            jtf.setText(value.toString());
            jtf.selectAll();
        }
        completer = new TraderAAutoCompleter(jtf, listTrader, this, this.filter, this.max);
        return component;
    }

    @Override
    public Object getCellEditorValue() {
        Object obj;
        TraderA trader = completer.getTrader();

        if (trader != null) {
            obj = trader;
        } else {
            obj = ((JTextField) component).getText();
        }

        return obj;

    }

    /*
     * To prevent mouse click cell editing and 
     * function key press
     */
    @Override
    public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            return false;
        } else if (anEvent instanceof KeyEvent) {
            KeyEvent ke = (KeyEvent) anEvent;

            if (ke.isActionKey()) {//Function key
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

}
