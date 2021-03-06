/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.report;

import com.acc.common.CrAmtTableModel;
import com.acc.common.DrAmtTableModel;
import com.acc.model.ReportFilter;
import com.acc.model.TmpOpening;
import com.acc.model.VGl;
import com.common.Global;
import com.common.ProUtil;
import com.common.SelectionObserver;
import com.common.TableCellRender;
import com.common.Util1;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Slf4j
public class TrialBalanceDetailDialog extends javax.swing.JDialog implements SelectionObserver {

    private final CrAmtTableModel crAmtTableModel = new CrAmtTableModel();
    private final DrAmtTableModel drAmtTableModel = new DrAmtTableModel();
    private JTextField txtDep;
    private JTextField txtDate;
    private TableRowSorter<TableModel> sorter;
    private String desp;
    private Double openingAmt = 0.0;
    private String coaCode;
    private String traderCode;
    private String stDate;
    private String endDate;
    private String curCode;
    private List<String> department;
    private WebClient accountApi;

    public WebClient getAccountApi() {
        return accountApi;
    }

    public void setAccountApi(WebClient accountApi) {
        this.accountApi = accountApi;
    }

    public JTextField getTxtDate() {
        return txtDate;
    }

    public void setTxtDate(JTextField txtDate) {
        this.txtDate = txtDate;
    }

    public JTextField getTxtDep() {
        return txtDep;
    }

    public void setTxtDep(JTextField txtDep) {
        this.txtDep = txtDep;
    }

    public List<String> getDepartment() {
        return department;
    }

    public void setDepartment(List<String> department) {
        this.department = department;
    }

    public String getStDate() {
        return stDate;
    }

    public void setStDate(String stDate) {
        this.stDate = stDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCurCode() {
        return curCode;
    }

    public void setCurCode(String curCode) {
        this.curCode = curCode;
        txtCurrency.setText(curCode);
    }

    public String getTraderCode() {
        return traderCode;
    }

    public void setTraderCode(String traderCode) {
        this.traderCode = traderCode;
    }

    public String getCoaCode() {
        return coaCode;
    }

    public void setCoaCode(String coaCode) {
        this.coaCode = coaCode;
    }

    public Double getOpeningAmt() {
        return openingAmt;
    }

    public void setOpeningAmt(Double openingAmt) {
        this.openingAmt = openingAmt;
    }

    public void setDesp(String desp) {
        this.desp = desp;
        lblName.setText(this.desp);
    }

    /**
     * Creates new form TrialBalanceDetailDialog
     */
    public TrialBalanceDetailDialog() {
        super(Global.parentForm, true);
        initComponents();
        progress.setVisible(false);
    }

    private void searchTriBalDetail() {
        progress.setIndeterminate(true);
        progress.setVisible(true);
        ReportFilter filter = new ReportFilter(Global.compCode, Global.macId);
        filter.setFromDate(stDate);
        filter.setToDate(endDate);
        filter.setDesp(desp);
        filter.setSrcAcc(coaCode);
        filter.setCurCode(curCode);
        filter.setDepartments(department);
        filter.setTraderCode(traderCode);
        Mono<ResponseEntity<List<VGl>>> result = accountApi.post()
                .uri("/account/search-gl")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .toEntityList(VGl.class);
        result.subscribe((t) -> {
            List<VGl> listVGl = t.getBody();
            swapData(listVGl, coaCode);
            calculateOpeningClosing(listVGl);
            progress.setIndeterminate(false);
        }, (e) -> {
            progress.setIndeterminate(false);
            JOptionPane.showMessageDialog(this, e.getMessage());
        });
    }

    private void swapData(List<VGl> listVGL, String targetId) {
        listVGL.forEach(vgl -> {
            String sourceAcId = Util1.isNull(vgl.getSourceAcId(), "-");
            if (!sourceAcId.equals(targetId)) {
                double tmpDrAmt = 0.0;
                if (vgl.getDrAmt() != null) {
                    tmpDrAmt = vgl.getDrAmt();
                }
                vgl.setDrAmt(Util1.getDouble(vgl.getCrAmt()));
                vgl.setCrAmt(tmpDrAmt);
                String tmpStr = vgl.getAccName();
                vgl.setAccName(vgl.getSrcAccName());
                vgl.setSrcAccName(tmpStr);
            } else {
                vgl.setCrAmt(Util1.getDouble(vgl.getCrAmt()));
                vgl.setDrAmt((Util1.getDouble(vgl.getDrAmt())));
            }
        });
    }

    private void calculateOpeningClosing(List<VGl> listVGl) {
        String opDate = Util1.toDateStrMYSQL(Global.startDate, "dd/MM/yyyy");
        String clDate = Util1.toDateStrMYSQL(stDate, "dd/MM/yyyy");
        ReportFilter filter = new ReportFilter(Global.compCode, Global.macId);
        filter.setOpeningDate(opDate);
        filter.setClosingDate(clDate);
        filter.setCurCode(curCode);
        filter.setDepartments(department);
        filter.setTraderCode(traderCode);
        filter.setCoaCode(coaCode);
        Mono<ResponseEntity<List<TmpOpening>>> result = accountApi.post()
                .uri("/account/get-coa-opening")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .toEntityList(TmpOpening.class);
        result.subscribe((t) -> {
            List<TmpOpening> tmpOpening = t.getBody();
            if (!t.getBody().isEmpty()) {
                double opAmt = tmpOpening.isEmpty() ? 0 : tmpOpening.get(0).getOpening();
                double ttlDrAmt = 0.0;
                double ttlCrAmt = 0.0;
                if (!listVGl.isEmpty()) {
                    for (VGl vgl : listVGl) {
                        if (vgl.getDrAmt() > 0) {
                            ttlDrAmt += vgl.getDrAmt();
                            drAmtTableModel.addVGl(vgl);
                        }
                        if (vgl.getCrAmt() > 0) {
                            ttlCrAmt += vgl.getCrAmt();
                            crAmtTableModel.addVGl(vgl);
                        }
                    }
                    txtDrCount.setValue(drAmtTableModel.getListVGl().size());
                    txtCrCount.setValue(crAmtTableModel.getListVGl().size());
                    txtFCrAmt.setValue(Util1.toFormatPattern(ttlCrAmt));
                    txtFDrAmt.setValue(Util1.toFormatPattern(ttlDrAmt));
                }
                txtOpening.setValue(Util1.toFormatPattern(opAmt));
                double closingAmt = opAmt + ttlDrAmt - ttlCrAmt;
                txtClosing.setValue(Util1.toFormatPattern(closingAmt));
            }
        }, (e) -> {
            JOptionPane.showMessageDialog(this, e.getMessage());
        });

    }

    public void initMain() {
        initTable();
        searchTriBalDetail();
    }

    private void initTable() {
        tblCR();
        tblDR();
        clear();
    }

    private void clear() {
        txtFDrAmt.setValue("0.0");
        txtFCrAmt.setValue("0.0");
        txtOpening.setValue("0.0");
        txtClosing.setValue("0.0");
    }

    private void tblCR() {
        crAmtTableModel.clear();
        tblCr.setModel(crAmtTableModel);
        tblCr.getTableHeader().setFont(Global.lableFont);
        tblCr.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCr.setRowHeight(Global.tblRowHeight);
        tblCr.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblCr.getColumnModel().getColumn(1).setPreferredWidth(5);
        tblCr.getColumnModel().getColumn(2).setPreferredWidth(170);
        tblCr.getColumnModel().getColumn(3).setPreferredWidth(170);
        tblCr.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblCr.getColumnModel().getColumn(5).setPreferredWidth(50);
        tblCr.setDefaultRenderer(Double.class, new TableCellRender());
        tblCr.setDefaultRenderer(Object.class, new TableCellRender());
        sorter = new TableRowSorter<>(tblCr.getModel());
        tblCr.setRowSorter(sorter);

    }

    private void tblDR() {
        drAmtTableModel.clear();
        tblDr.setModel(drAmtTableModel);
        tblDr.setRowHeight(Global.tblRowHeight);
        tblDr.getTableHeader().setFont(Global.lableFont);
        tblDr.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDr.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblDr.getColumnModel().getColumn(1).setPreferredWidth(5);
        tblDr.getColumnModel().getColumn(2).setPreferredWidth(170);
        tblDr.getColumnModel().getColumn(3).setPreferredWidth(170);
        tblDr.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblDr.getColumnModel().getColumn(5).setPreferredWidth(50);
        tblDr.setDefaultRenderer(Double.class, new TableCellRender());
        tblDr.setDefaultRenderer(Object.class, new TableCellRender());
        sorter = new TableRowSorter<>(tblDr.getModel());
        tblDr.setRowSorter(sorter);

    }

    private void print() {
        try {
            this.dispose();
            String compName = Global.companyName;
            String reportPath = ProUtil.getReportPath();
            String filePath = reportPath + File.separator + "TriBalanceDetail";
            Map<String, Object> p = new HashMap();
            p.put("p_company_name", compName);
            p.put("p_mac_id", Global.macId);
            p.put("p_comp_code", Global.compCode);
            p.put("p_report_info", lblName.getText());
            p.put("p_date", txtDate.getText());
            p.put("p_dept_name", txtDep.getText());
            p.put("p_from_date", stDate);
            p.put("p_to_date", endDate);
            p.put("p_account_code", coaCode);
            p.put("p_cur_code", curCode);
            p.put("p_trader_code", Util1.isNull(traderCode, "-"));
            p.put("p_opening", txtOpening.getValue());
            p.put("p_closing", txtClosing.getValue());
        } catch (Exception ex) {
            log.error("print: " + ex.getMessage());
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

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        progress = new javax.swing.JProgressBar();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDr = new javax.swing.JTable();
        txtClosing = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtOpening = new javax.swing.JFormattedTextField();
        txtFDrAmt = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        txtDrCount = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JFormattedTextField();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCr = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        txtFCrAmt = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtCrCount = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Trial Balance");
        setBackground(new java.awt.Color(255, 255, 255));
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                formFocusLost(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        lblName.setFont(Global.menuFont);
        lblName.setForeground(new java.awt.Color(255, 255, 255));
        lblName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblName.setText("Name");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblName, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Closing Amt");

        tblDr.setFont(Global.textFont);
        tblDr.setModel(new javax.swing.table.DefaultTableModel(
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
        tblDr.setName("Debit"); // NOI18N
        tblDr.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblDr);

        txtClosing.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClosing.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtClosing.setEnabled(false);
        txtClosing.setFont(Global.amtFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Opening Amt");

        txtOpening.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtOpening.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtOpening.setEnabled(false);
        txtOpening.setFont(Global.amtFont);
        txtOpening.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOpeningActionPerformed(evt);
            }
        });

        txtFDrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFDrAmt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtFDrAmt.setEnabled(false);
        txtFDrAmt.setFont(Global.amtFont);
        txtFDrAmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFDrAmtActionPerformed(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Total Dr-Amt");

        txtDrCount.setEditable(false);
        txtDrCount.setBorder(null);
        txtDrCount.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jLabel4.setText("Total Count : ");

        jLabel9.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jLabel9.setText("Currency : ");

        txtCurrency.setEditable(false);
        txtCurrency.setBorder(null);
        txtCurrency.setForeground(new java.awt.Color(0, 51, 255));
        txtCurrency.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 774, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtOpening))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtClosing)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(txtFDrAmt))
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDrCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel5, jLabel6});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtDrCount)
                    .addComponent(jLabel9)
                    .addComponent(txtCurrency))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtOpening, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(txtFDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtClosing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        tblCr.setFont(Global.textFont);
        tblCr.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCr.setName("Credit"); // NOI18N
        tblCr.setRowHeight(Global.tblRowHeight);
        jScrollPane2.setViewportView(tblCr);

        jButton1.setText("Print");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        txtFCrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFCrAmt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtFCrAmt.setEnabled(false);
        txtFCrAmt.setFont(Global.amtFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Total Cr-Amt");

        jLabel7.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jLabel7.setText("Total Count : ");

        txtCrCount.setEditable(false);
        txtCrCount.setBorder(null);
        txtCrCount.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N

        jLabel8.setFont(Global.lableFont);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(389, 389, 389)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtCrCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 774, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtFCrAmt)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCrCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtFCrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 722, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(2, 2, 2))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentShown

    private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_formFocusLost

    private void txtOpeningActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOpeningActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOpeningActionPerformed

    private void txtFDrAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFDrAmtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFDrAmtActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        print();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        // TODO add your handling code here:

    }//GEN-LAST:event_formKeyPressed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel lblName;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTable tblCr;
    private javax.swing.JTable tblDr;
    private javax.swing.JFormattedTextField txtClosing;
    private javax.swing.JFormattedTextField txtCrCount;
    private javax.swing.JFormattedTextField txtCurrency;
    private javax.swing.JFormattedTextField txtDrCount;
    private javax.swing.JFormattedTextField txtFCrAmt;
    private javax.swing.JFormattedTextField txtFDrAmt;
    private javax.swing.JFormattedTextField txtOpening;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {

        if (source.equals("SAVE-GL-DR")) {
            int selectRow = tblDr.convertRowIndexToModel(tblDr.getSelectedRow());
            VGl vgl = (VGl) selectObj;
            if (!vgl.getSourceAcId().equals(coaCode)) {
                vgl = swapDrCr(vgl);
            }
            drAmtTableModel.setVGl(selectRow, vgl);
        }
        if (source.equals("SAVE-GL-CR")) {
            int selectRow = tblDr.convertRowIndexToModel(tblCr.getSelectedRow());
            VGl vgl = (VGl) selectObj;
            if (!vgl.getSourceAcId().equals(coaCode)) {
                vgl = swapDrCr(vgl);
            }
            crAmtTableModel.setVGl(selectRow, vgl);
        }
    }

    private VGl swapDrCr(VGl vgl) {
        vgl.setDrAmt(Util1.getDouble(vgl.getCrAmt()));
        vgl.setCrAmt(Util1.getDouble(vgl.getDrAmt()));
        return vgl;
    }
}
