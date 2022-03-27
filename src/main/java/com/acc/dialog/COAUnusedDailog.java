/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.dialog;

import com.acc.common.COAOptionTableModel;
import com.acc.model.ChartOfAccount;
import com.common.Global;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import java.util.List;
import javax.swing.JOptionPane;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
public class COAUnusedDailog extends javax.swing.JDialog {

    private final COAOptionTableModel model = new COAOptionTableModel();
    private WebClient inventoryApi;
    private SelectionObserver observer;
    private final String title = "COA Unused Dialog";

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public WebClient getWebClient() {
        return inventoryApi;
    }

    public void setWebClient(WebClient inventoryApi) {
        this.inventoryApi = inventoryApi;
    }

    /**
     * Creates new form COAOptionDialog
     */
    public COAUnusedDailog() {
        super(Global.parentForm, true);
        initComponents();
    }

    public void initTable() {
        tblOption.getTableHeader().setFont(Global.tblHeaderFont);
        tblOption.setDefaultRenderer(Object.class, new TableCellRender());
        tblOption.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblOption.setModel(model);
        searchHead();
    }

    private void searchHead() {
        this.setTitle("Analyzing...");
        model.clear();
        Mono<ResponseEntity<List<ChartOfAccount>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/account/get-coa")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(ChartOfAccount.class);

        result.subscribe((t) -> {
            if (!t.getBody().isEmpty()) {
                t.getBody().forEach(coa -> {
                    coa.setActive(Boolean.TRUE);
                });
                model.setListCoaHead(t.getBody());
                this.setTitle(title);
            } else {
                this.setTitle(title + "- No unused coa.");
            }
        }, (e) -> {
        });

    }

    private void delete() {
        int status = JOptionPane.showConfirmDialog(Global.parentForm, "Are you sure to delete.");
        if (status == JOptionPane.OK_OPTION) {

            this.dispose();
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
        tblOption = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select COA Group ");

        tblOption.setFont(Global.textFont);
        tblOption.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblOption.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblOption);

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Delete");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(Global.lableFont);
        jButton2.setText("Cancel");
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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(251, 251, 251)
                        .addComponent(jButton2)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        delete();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblOption;
    // End of variables declaration//GEN-END:variables
}