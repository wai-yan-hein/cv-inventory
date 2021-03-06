/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.inventory.ui.setup.dialog;

import com.common.Global;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.model.Stock;
import com.inventory.model.StockType;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.setup.dialog.common.StockImportTableModel;
import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class StockImportDialog extends javax.swing.JDialog {

    private WebClient inventoryApi;
    private final StockImportTableModel tableModel = new StockImportTableModel();
    private TaskExecutor taskExecutor;
    private InventoryRepo inventoryRepo;

    public InventoryRepo getInventoryRepo() {
        return inventoryRepo;
    }

    public void setInventoryRepo(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public WebClient getWebClient() {
        return inventoryApi;
    }

    public void setWebClient(WebClient inventoryApi) {
        this.inventoryApi = inventoryApi;
    }

    /**
     * Creates new form CustomerImportDialog
     *
     * @param parent
     */
    public StockImportDialog(JFrame parent) {
        super(parent, true);
        initComponents();
        initTable();
    }

    private void initTable() {
        tblTrader.setModel(tableModel);
        tblTrader.getTableHeader().setFont(Global.tblHeaderFont);
        tblTrader.setDefaultRenderer(Object.class, new TableCellRender());
        tblTrader.setDefaultRenderer(Float.class, new TableCellRender());
        tblTrader.setFont(Global.textFont);
    }

    private void chooseFile() {
        FileDialog dialog = new FileDialog(this, "Choose CSV File", FileDialog.LOAD);
        dialog.setDirectory("D:\\");
        dialog.setFile(".csv");
        dialog.setVisible(true);
        String directory = dialog.getFile();
        log.info("File Path :" + directory);
        if (directory != null) {
            readFile(dialog.getDirectory() + "\\" + directory);
        }
    }

    private void save() {
        List<Stock> traders = tableModel.getListStock();
        btnSave.setEnabled(false);
        lblLog.setText("Importing.");
        for (Stock stock : traders) {
            inventoryRepo.saveStock(stock);
        }
        lblLog.setText("Success.");
        dispose();
    }

    private void readFile(String path) {
        HashMap<String, StockType> hm = new HashMap<>();
        List<StockType> listST = inventoryRepo.getStockType();
        if (!listST.isEmpty()) {
            for (StockType st : listST) {
                hm.put(st.getUserCode(), st);
            }
        }
        String line;
        String splitBy = ",";
        int lineCount = 0;
        List<Stock> listStock = new ArrayList<>();
        try {
            try ( BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(path), "UTF8"))) {
                while ((line = br.readLine()) != null) //returns a Boolean value
                {
                    Stock t = new Stock();
                    String[] data = line.split(splitBy);    // use comma as separator
                    String userCode = null;
                    String stockName = null;
                    String priceA = null;
                    String priceB = null;
                    String typeCode = null;
                    lineCount++;
                    try {
                        userCode = data[0];
                        stockName = data[1];
                        priceA = data[2];
                        priceB = data[3];
                        typeCode = data[4];
                    } catch (IndexOutOfBoundsException e) {
                        //JOptionPane.showMessageDialog(Global.parentForm, "FORMAT ERROR IN LINE:" + lineCount + e.getMessage());
                    }
                    t.setUserCode(userCode);
                    t.setStockName(stockName);
                    t.setSalePriceN(Util1.getFloat(priceA));
                    t.setSalePriceA(Util1.getFloat(priceB));
                    t.setStockType(hm.get(typeCode));
                    t.setCompCode(Global.compCode);
                    t.setActive(true);
                    t.setCreatedDate(Util1.getTodayDate());
                    t.setCreatedBy(Global.loginUser.getUserCode());
                    t.setMacId(Global.macId);
                    t.setCalculate(true);
                    listStock.add(t);
                }
            }
            tableModel.setListStock(listStock);
        } catch (IOException e) {
            log.error("Read CSV File :" + e.getMessage());

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblTrader = new javax.swing.JTable();
        btnSave = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        lblLog = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tblTrader.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblTrader);

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        jButton2.setText("Choose File");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblLog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSave, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblLog, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        chooseFile();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblLog;
    private javax.swing.JTable tblTrader;
    // End of variables declaration//GEN-END:variables
}
