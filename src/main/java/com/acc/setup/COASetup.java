/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.setup;

import com.acc.common.AccountRepo;
import com.acc.common.COAGroupChildTableModel;
import com.acc.common.COAGroupTableModel;
import com.acc.common.COAHeadTableModel;
import com.acc.model.ChartOfAccount;
import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.inventory.ui.setup.dialog.common.AutoClearEditor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author MyoGyi
 */
@Component
public class COASetup extends javax.swing.JPanel implements KeyListener, PanelControl {

    private int selectRow = -1;
    private final COAHeadTableModel coaHeadTableModel = new COAHeadTableModel();
    private final COAGroupTableModel coaGroupTableModel = new COAGroupTableModel();
    private final COAGroupChildTableModel cOAGroupChildTableModel = new COAGroupChildTableModel();
    private TableFilterHeader filterHeader;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private WebClient accountApi;
    private JProgressBar progress;
    private SelectionObserver observer;

    public JProgressBar getProgress() {
        return progress;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    /**
     * Creates new form CoaSetup
     */
    public COASetup() {
        initComponents();
        //ActionMapping();

    }

    public void initMain() {
        initKeyListener();
        initTable();
    }

    private void initTable() {
        tblCOAHead();
        tblCOAGroup();
        tblCOA();
    }

    private void tblCOAHead() {
        tblCoaHead.setModel(coaHeadTableModel);
        tblCoaHead.getTableHeader().setFont(Global.tblHeaderFont);
        tblCoaHead.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCoaHead.getColumnModel().getColumn(0).setPreferredWidth(20);// Code
        tblCoaHead.getColumnModel().getColumn(1).setPreferredWidth(400);// Name
        tblCoaHead.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        tblCoaHead.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblCoaHead.setDefaultRenderer(Object.class, new TableCellRender());
        tblCoaHead.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        filterHeader = new TableFilterHeader(tblCoaHead, AutoChoices.ENABLED);
        filterHeader.setPosition(TableFilterHeader.Position.TOP);
        filterHeader.setFont(Global.textFont);
        filterHeader.setVisible(false);
        searchHead();
    }

    private void searchHead() {
        Mono<ResponseEntity<List<ChartOfAccount>>> result = accountApi.get().uri((builder) -> builder.path("/account/get-coa-child")
                .queryParam("parentCode", "#")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .toEntityList(ChartOfAccount.class);
        result.subscribe((t) -> {
            coaHeadTableModel.setlistCoaHead(t.getBody());
            tblCoaHead.requestFocus();
            progress.setIndeterminate(false);
        }, (e) -> {
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(this, e.getMessage());
        });
    }

    private void tblCOAGroup() {
        tblCoaGroup.setCellSelectionEnabled(true);
        tblCoaGroup.getTableHeader().setFont(Global.tblHeaderFont);
        tblCoaGroup.setModel(coaGroupTableModel);
        coaGroupTableModel.setParent(tblCoaGroup);
        coaGroupTableModel.setParetnDesp(lblCoaGroup);
        coaGroupTableModel.setAccountRepo(accountRepo);
        tblCoaGroup.getColumnModel().getColumn(0).setPreferredWidth(10);// Sys Code
        tblCoaGroup.getColumnModel().getColumn(1).setPreferredWidth(20);// Usr Code
        tblCoaGroup.getColumnModel().getColumn(2).setPreferredWidth(400);// Name
        tblCoaGroup.getColumnModel().getColumn(3).setPreferredWidth(10);// Active
        tblCoaGroup.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblCoaGroup.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblCoaGroup.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblCoaGroup.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblCoaGroup.setDefaultRenderer(Object.class, new TableCellRender());
        tblCoaGroup.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        filterHeader = new TableFilterHeader(tblCoaGroup, AutoChoices.ENABLED);
        filterHeader.setPosition(TableFilterHeader.Position.TOP);
        filterHeader.setFont(Global.textFont);
        filterHeader.setVisible(false);

    }

    private void tblCOA() {
        tblCOAGroupChild.setCellSelectionEnabled(true);
        tblCOAGroupChild.getTableHeader().setFont(Global.tblHeaderFont);
        tblCOAGroupChild.setModel(cOAGroupChildTableModel);
        cOAGroupChildTableModel.setParent(tblCOAGroupChild);
        cOAGroupChildTableModel.setAccountRepo(accountRepo);
        tblCOAGroupChild.getColumnModel().getColumn(0).setPreferredWidth(10);// Sys Code
        tblCOAGroupChild.getColumnModel().getColumn(1).setPreferredWidth(20);// Usr Code
        tblCOAGroupChild.getColumnModel().getColumn(2).setPreferredWidth(400);// Name
        tblCOAGroupChild.getColumnModel().getColumn(3).setPreferredWidth(10);// Active
        tblCOAGroupChild.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        tblCOAGroupChild.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblCOAGroupChild.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblCOAGroupChild.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCOAGroupChild.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblCOAGroupChild.setDefaultRenderer(Object.class, new TableCellRender());
        tblCOAGroupChild.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        filterHeader = new TableFilterHeader(tblCOAGroupChild, AutoChoices.ENABLED);
        filterHeader.setPosition(TableFilterHeader.Position.TOP);
        filterHeader.setFont(Global.textFont);
        filterHeader.setVisible(false);

    }

    private void getCOAGroup(int row) {
        clear();
        ChartOfAccount c = coaHeadTableModel.getChartOfAccount(row);
        if (c.getCoaCode() != null) {
            progress.setIndeterminate(true);
            Mono<ResponseEntity<List<ChartOfAccount>>> result = accountApi.get().uri((builder) -> builder.path("/account/get-coa-child")
                    .queryParam("parentCode", c.getCoaCode())
                    .queryParam("compCode", Global.compCode)
                    .build())
                    .retrieve()
                    .toEntityList(ChartOfAccount.class);
            result.subscribe((t) -> {
                coaGroupTableModel.setCoaHeadCode(c.getCoaCode());
                coaGroupTableModel.setListCOA(t.getBody());
                coaGroupTableModel.addEmptyRow();
                lblCoaGroup.setText(c.getCoaNameEng());
                reqCoaGroup();
                progress.setIndeterminate(false);
            }, (e) -> {
                progress.setIndeterminate(false);
                JOptionPane.showMessageDialog(this, e.getMessage());
            });
        }
    }

    private void reqCoaGroup() {
        int row = tblCoaGroup.getRowCount();
        if (row > 0) {
            tblCoaGroup.setRowSelectionInterval(row - 1, row - 1);
            tblCoaGroup.setColumnSelectionInterval(1, 1);
            tblCoaGroup.requestFocus();
        } else {
            tblCoaGroup.setRowSelectionInterval(row, row);
            tblCoaGroup.setColumnSelectionInterval(1, 1);
            tblCoaGroup.requestFocus();
        }
    }

    private void getCOAGroupChild(int row) {
        cOAGroupChildTableModel.clear();
        ChartOfAccount c = coaGroupTableModel.getChartOfAccount(row);
        if (c.getCoaCode() != null) {
            progress.setIndeterminate(true);
            Mono<ResponseEntity<List<ChartOfAccount>>> result = accountApi.get().uri((builder) -> builder.path("/account/get-coa-child")
                    .queryParam("parentCode", c.getCoaCode())
                    .queryParam("compCode", Global.compCode)
                    .build())
                    .retrieve()
                    .toEntityList(ChartOfAccount.class);
            result.subscribe((t) -> {
                cOAGroupChildTableModel.setCoaGroupCode(c.getCoaCode());
                cOAGroupChildTableModel.setListCOA(t.getBody());
                cOAGroupChildTableModel.addEmptyRow();
                lblCoaChild.setText(c.getCoaNameEng());
                reqCOAGroupChild();
                progress.setIndeterminate(false);
            }, (e) -> {
                progress.setIndeterminate(false);
                JOptionPane.showMessageDialog(this, e.getMessage());
            });
        }
    }

    private void reqCOAGroupChild() {
        int row = tblCOAGroupChild.getRowCount();
        if (row > 0) {
            tblCOAGroupChild.setRowSelectionInterval(row - 1, row - 1);
            tblCOAGroupChild.setColumnSelectionInterval(1, 1);
            tblCOAGroupChild.requestFocus();
        } else {
            tblCOAGroupChild.setRowSelectionInterval(0, 0);
            tblCOAGroupChild.setColumnSelectionInterval(1, 1);
            tblCOAGroupChild.requestFocus();
        }
    }

    private void clear() {
        coaGroupTableModel.clear();
        cOAGroupChildTableModel.clear();
        lblCoaChild.setText("...");
        lblCoaGroup.setText("...");
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
        tblCoaHead = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCoaGroup = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblCOAGroupChild = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        lblCoaChild = new javax.swing.JLabel();
        lblCoaGroup = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblCoaHead.setFont(Global.textFont);
        tblCoaHead.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCoaHead.setName("tblCoaHead"); // NOI18N
        tblCoaHead.setRowHeight(Global.tblRowHeight);
        tblCoaHead.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCoaHeadMouseClicked(evt);
            }
        });
        tblCoaHead.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblCoaHeadKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblCoaHead);

        tblCoaGroup.setFont(Global.textFont);
        tblCoaGroup.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCoaGroup.setName("tblCoaGroup"); // NOI18N
        tblCoaGroup.setRowHeight(Global.tblRowHeight);
        tblCoaGroup.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCoaGroupMouseClicked(evt);
            }
        });
        tblCoaGroup.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblCoaGroupKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblCoaGroupKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tblCoaGroup);

        tblCOAGroupChild.setFont(Global.textFont);
        tblCOAGroupChild.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCOAGroupChild.setName("tblCOAGroupChild"); // NOI18N
        tblCOAGroupChild.setRowHeight(Global.tblRowHeight);
        tblCOAGroupChild.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblCOAGroupChildKeyReleased(evt);
            }
        });
        jScrollPane3.setViewportView(tblCOAGroupChild);

        jLabel1.setFont(Global.menuFont);
        jLabel1.setText("Account Head");

        lblCoaChild.setFont(Global.menuFont);
        lblCoaChild.setText("...");

        lblCoaGroup.setFont(Global.menuFont);
        lblCoaGroup.setText("...");

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                    .addComponent(jSeparator4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(lblCoaGroup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCoaChild, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(lblCoaGroup))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblCoaChild, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)))))
                .addGap(10, 10, 10))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, lblCoaChild, lblCoaGroup});

    }// </editor-fold>//GEN-END:initComponents

    private void tblCoaGroupKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblCoaGroupKeyPressed

    }//GEN-LAST:event_tblCoaGroupKeyPressed

    private void tblCoaHeadKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblCoaHeadKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            /* showCoaGroup(tblCoaHead.convertRowIndexToModel(tblCoaHead.getSelectedRow()));
            tblCoaGroup.requestFocus();
            tblCoaGroup.setRowSelectionInterval(0, 0);*/
        }
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            /*showCoaGroup(tblCoaHead.convertRowIndexToModel(tblCoaHead.getSelectedRow()));*/

        }
    }//GEN-LAST:event_tblCoaHeadKeyReleased

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:

    }//GEN-LAST:event_formComponentShown

    private void tblCOAGroupChildKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblCOAGroupChildKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_tblCOAGroupChildKeyReleased

    private void tblCoaGroupKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblCoaGroupKeyReleased
        // TODO add your handling code here:??
    }//GEN-LAST:event_tblCoaGroupKeyReleased

    private void tblCoaGroupMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCoaGroupMouseClicked
        // TODO add your handling code here:
        if (tblCoaGroup.getSelectedRow() >= 0) {
            selectRow = tblCoaGroup.convertRowIndexToModel(tblCoaGroup.getSelectedRow());
            getCOAGroupChild(selectRow);
        }
    }//GEN-LAST:event_tblCoaGroupMouseClicked

    private void tblCoaHeadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCoaHeadMouseClicked
        // TODO add your handling code here:
        if (tblCoaHead.getSelectedRow() >= 0) {
            selectRow = tblCoaHead.convertRowIndexToModel(tblCoaHead.getSelectedRow());
            getCOAGroup(selectRow);
        }
    }//GEN-LAST:event_tblCoaHeadMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel lblCoaChild;
    private javax.swing.JLabel lblCoaGroup;
    private javax.swing.JTable tblCOAGroupChild;
    private javax.swing.JTable tblCoaGroup;
    private javax.swing.JTable tblCoaHead;
    // End of variables declaration//GEN-END:variables

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private void initKeyListener() {
        tblCoaHead.addKeyListener(this);
        tblCoaGroup.addKeyListener(this);
        tblCOAGroupChild.addKeyListener(this);
    }

    @Override
    public void save() {
    }

    @Override
    public void delete() {
    }

    @Override
    public void newForm() {
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }

    @Override
    public void refresh() {
        searchHead();
    }

    @Override
    public void filter() {
    }

    @Override
    public String panelName() {
        return this.getName();
    }
}
