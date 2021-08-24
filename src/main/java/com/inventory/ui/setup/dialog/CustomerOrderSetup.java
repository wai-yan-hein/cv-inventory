/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup.dialog;

import com.inventory.common.Global;
import com.inventory.common.Util1;
import com.inventory.editor.RegionAutoCompleter;
import com.inventory.model.Trader;
import com.inventory.ui.setup.dialog.common.CustomerGridTabelModel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class CustomerOrderSetup extends javax.swing.JDialog implements KeyListener {

    private static final Logger log = LoggerFactory.getLogger(CustomerOrderSetup.class);
    private Trader trader;

    @Autowired
    private CustomerGridTabelModel gridTabelModel;
    private RegionAutoCompleter regionAutoCompleter;

    /**
     * Creates new form CustomerOrderSetup
     */
    public CustomerOrderSetup() {
        super(Global.parentForm, true);
        initComponents();
        initKeyListener();
    }

    private void initKeyListener() {
        txtAccount.addKeyListener(this);
        txtAddress.addKeyListener(this);
        txtName.addKeyListener(this);
        txtPhone.addKeyListener(this);
        txtRegion.addKeyListener(this);
        btnSave.addKeyListener(this);
    }

    public void initAutoCompleter() {
        regionAutoCompleter = new RegionAutoCompleter(txtRegion, Global.listRegion, null);
        regionAutoCompleter.setRegion(null);
    }

    private void save() {
        if (isValidEntry()) {

        }
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (txtName.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Name");
            txtName.requestFocus();
        }
        if (txtPhone.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Phone");
            txtPhone.requestFocus();
        }
        if (txtAddress.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Address");
            txtAddress.requestFocus();
        }
        if (regionAutoCompleter.getRegion() == null) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Region");
            txtAddress.requestFocus();
        }
        if (status) {
            trader = new Trader();
            trader.setUserCode(txtUserCode.getText());
            trader.setTraderName(txtName.getText());
            trader.setPhone(txtPhone.getText());
            trader.setAddress(txtAddress.getText());
            trader.setEmail(txtAccount.getText());
            trader.setCompCode(Global.compCode);
            trader.setActive(Boolean.TRUE);
            trader.setCreatedBy(Global.loginUser);
            trader.setCreatedDate(Util1.getTodayDate());
            trader.setMacId(Global.machineId);
            trader.setCompCode(Global.compCode);
        }
        return status;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtPhone = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtAccount = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtRegion = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtUserCode = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Name");

        txtName.setFont(Global.textFont);
        txtName.setName("txtName"); // NOI18N
        txtName.setNextFocusableComponent(txtPhone);
        txtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNameFocusLost(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Phone No");

        txtPhone.setFont(Global.textFont);
        txtPhone.setName("txtPhone"); // NOI18N
        txtPhone.setNextFocusableComponent(txtAccount);
        txtPhone.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPhoneFocusGained(evt);
            }
        });
        txtPhone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhoneActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Account");

        txtAccount.setFont(Global.textFont);
        txtAccount.setName("txtAccount"); // NOI18N
        txtAccount.setNextFocusableComponent(txtRegion);
        txtAccount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAccountFocusGained(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Address");

        txtAddress.setFont(Global.textFont);
        txtAddress.setName("txtAddress"); // NOI18N
        txtAddress.setNextFocusableComponent(btnSave);
        txtAddress.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAddressFocusGained(evt);
            }
        });

        btnSave.setFont(Global.lableFont);
        btnSave.setText("Save");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Region");

        txtRegion.setFont(Global.textFont);
        txtRegion.setName("txtRegion"); // NOI18N
        txtRegion.setNextFocusableComponent(txtAddress);
        txtRegion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRegionFocusGained(evt);
            }
        });

        jLabel6.setFont(Global.textFont);
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("New Trader");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Code");

        txtUserCode.setFont(Global.textFont);
        txtUserCode.setName("txtName"); // NOI18N
        txtUserCode.setNextFocusableComponent(txtName);
        txtUserCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUserCodeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtUserCodeFocusLost(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnSave)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRegion, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtAccount, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtPhone)
                            .addComponent(txtAddress)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtUserCode, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtUserCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtRegion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(btnSave)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtPhoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPhoneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhoneActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void txtNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNameFocusLost

    private void txtNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusGained
        // TODO add your handling code here:
        txtName.selectAll();
    }//GEN-LAST:event_txtNameFocusGained

    private void txtPhoneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPhoneFocusGained
        // TODO add your handling code here:
        txtPhone.selectAll();
    }//GEN-LAST:event_txtPhoneFocusGained

    private void txtAccountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAccountFocusGained
        // TODO add your handling code here:
        txtAccount.selectAll();
    }//GEN-LAST:event_txtAccountFocusGained

    private void txtRegionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRegionFocusGained
        // TODO add your handling code here:
        regionAutoCompleter.showPopup();
    }//GEN-LAST:event_txtRegionFocusGained

    private void txtAddressFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAddressFocusGained
        // TODO add your handling code here:
        txtAddress.selectAll();
    }//GEN-LAST:event_txtAddressFocusGained

    private void txtUserCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUserCodeFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUserCodeFocusGained

    private void txtUserCodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUserCodeFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUserCodeFocusLost

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txtAccount;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtRegion;
    private javax.swing.JTextField txtUserCode;
    // End of variables declaration//GEN-END:variables

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        String ctrlName = "-";
        Object source = e.getSource();
        if (source instanceof JTextField) {
            ctrlName = ((JTextField) source).getName();
        } else if (source instanceof JButton) {
            ctrlName = ((JButton) source).getName();
        }
        switch (ctrlName) {
            case "txtName":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtPhone.requestFocus();
                        break;
                }
                break;
            case "txtPhone":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtAccount.requestFocus();
                        break;
                }
                break;
            case "txtAccount":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtRegion.requestFocus();
                        break;
                }
                break;
            case "txtRegion":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtAddress.requestFocus();
                        break;
                }
                break;
            case "txtAddress":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        btnSave.requestFocus();
                        break;
                }
                break;
        }
    }
}