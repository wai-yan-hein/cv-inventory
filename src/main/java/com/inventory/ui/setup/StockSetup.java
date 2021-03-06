/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.ui.setup;

import com.common.Global;
import com.common.PanelControl;
import com.common.SelectionObserver;
import com.common.StartWithRowFilter;
import com.common.TableCellRender;
import com.common.Util1;
import com.inventory.editor.BrandAutoCompleter;
import com.inventory.editor.CategoryAutoCompleter;
import com.inventory.editor.StockTypeAutoCompleter;
import com.inventory.editor.UnitAutoCompleter;
import com.inventory.editor.UnitRelationAutoCompleter;
import com.inventory.model.Category;
import com.inventory.model.Stock;
import com.inventory.model.StockBrand;
import com.inventory.model.StockType;
import com.inventory.model.StockUnit;
import com.inventory.model.UnitRelation;
import com.inventory.ui.common.InventoryRepo;
import com.inventory.ui.setup.common.StockTableModel;
import com.inventory.ui.setup.dialog.CategorySetupDialog;
import com.inventory.ui.setup.dialog.RelationSetupDialog;
import com.inventory.ui.setup.dialog.StockBrandSetupDialog;
import com.inventory.ui.setup.dialog.StockImportDialog;
import com.inventory.ui.setup.dialog.StockTypeSetupDialog;
import com.inventory.ui.setup.dialog.StockUnitSetupDailog;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 *
 * @author Lenovo
 */
@Component
public class StockSetup extends javax.swing.JPanel implements KeyListener, PanelControl {

    private int selectRow = -1;
    @Autowired
    private StockTypeSetupDialog itemTypeSetupDialog;
    @Autowired
    private CategorySetupDialog categorySetupDailog;
    @Autowired
    private StockBrandSetupDialog itemBrandDailog;
    @Autowired
    private StockUnitSetupDailog itemUnitSetupDailog;
    private final StockTableModel stockTableModel = new StockTableModel();
    @Autowired
    private WebClient inventoryApi;
    @Autowired
    private InventoryRepo inventoryRepo;
    private StockTypeAutoCompleter typeAutoCompleter;
    private CategoryAutoCompleter categoryAutoCompleter;
    private BrandAutoCompleter brandAutoCompleter;
    private UnitAutoCompleter purUnitCompleter;
    private UnitAutoCompleter saleUnitCompleter;
    private UnitRelationAutoCompleter relationAutoCompleter;
    private Stock stock = new Stock();
    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter swrf;
    private SelectionObserver observer;
    private JProgressBar progress;
    private List<StockUnit> listStockUnit = new ArrayList<>();
    private List<Category> listCategory = new ArrayList<>();
    private List<StockBrand> listStockBrand = new ArrayList<>();
    private List<StockType> listStockType = new ArrayList<>();
    private List<UnitRelation> listRelation = new ArrayList<>();
    private final Image icon = new ImageIcon(getClass().getResource("/images/setting.png")).getImage();

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public JProgressBar getProgress() {
        return progress;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }

    /**
     * Creates new form StockSetup
     */
    public StockSetup() {
        initComponents();
        initKeyListener();
    }

    public void initMain() {
        initData();
        initCombo();
        initTable();
        searchStock();
        clear();
    }

    private void initData() {
        progress.setIndeterminate(true);
        listStockUnit = inventoryRepo.getStockUnit();
        listStockType = inventoryRepo.getStockType();
        listCategory = inventoryRepo.getCategory();
        listStockBrand = inventoryRepo.getStockBrand();
        listRelation = inventoryRepo.getUnitRelation();
        searchStock();
        progress.setIndeterminate(false);
    }

    private void initTable() {
        stockTableModel.setInventoryRepo(inventoryRepo);
        tblStock.setModel(stockTableModel);
        tblStock.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblStock.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblStock.getColumnModel().getColumn(2).setPreferredWidth(10);
        tblStock.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblStock.getTableHeader().setFont(Global.tblHeaderFont);
        tblStock.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblStock.setDefaultRenderer(Object.class, new TableCellRender());
        tblStock.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblStock.getSelectedRow() >= 0) {
                    selectRow = tblStock.convertRowIndexToModel(tblStock.getSelectedRow());
                    setStock(selectRow);
                }
            }
        });
        sorter = new TableRowSorter<>(tblStock.getModel());
        tblStock.setRowSorter(sorter);
        swrf = new StartWithRowFilter(txtFilter);
    }

    private void setStock(int row) {
        stock = stockTableModel.getStock(row);
        txtStockCode.setText(stock.getStockCode());
        txtBarCode.setText(stock.getBarcode());
        txtUserCode.setText(stock.getUserCode());
        txtStockName.setText(stock.getStockName());
        chkActive.setSelected(stock.isActive());
        brandAutoCompleter.setBrand(stock.getBrand());
        categoryAutoCompleter.setCategory(stock.getCategory());
        saleUnitCompleter.setStockUnit(stock.getSaleUnit());
        typeAutoCompleter.setStockType(stock.getStockType());
        purUnitCompleter.setStockUnit(stock.getPurUnit());
        relationAutoCompleter.setRelation(stock.getUnitRelation());
        txtPurWt.setText(Util1.getString(stock.getPurWeight()));
        txtSaleWt.setText(Util1.getString(stock.getSaleWeight()));
        txtPurPrice.setText(Util1.getString(stock.getPurPrice()));
        txtSalePrice.setText(Util1.getString(stock.getSalePriceN()));
        txtSalePriceA.setText(Util1.getString(stock.getSalePriceA()));
        txtSalePriceB.setText((Util1.getString(stock.getSalePriceB())));
        txtSalePriceC.setText(Util1.getString(stock.getSalePriceC()));
        txtSalePriceD.setText(Util1.getString(stock.getSalePriceD()));
        txtSalePriceE.setText(Util1.getString(stock.getSalePriceE()));
        chkCal.setSelected(stock.isCalculate());
        lblStatus.setText("EDIT");
    }

    private void searchStock() {
        progress.setIndeterminate(true);
        stockTableModel.setListStock(inventoryRepo.getStock(false));
        progress.setIndeterminate(false);
    }

    private void initCombo() {
        typeAutoCompleter = new StockTypeAutoCompleter(txtType, listStockType, null, false, false);
        typeAutoCompleter.setStockType(null);
        categoryAutoCompleter = new CategoryAutoCompleter(txtCat, listCategory, null, false, false);
        categoryAutoCompleter.setCategory(null);
        brandAutoCompleter = new BrandAutoCompleter(txtBrand, listStockBrand, null, false, false);
        brandAutoCompleter.setBrand(null);
        purUnitCompleter = new UnitAutoCompleter(txtPurUnit, listStockUnit, null);
        purUnitCompleter.setStockUnit(null);
        saleUnitCompleter = new UnitAutoCompleter(txtSaleUnit, listStockUnit, null);
        saleUnitCompleter.setStockUnit(null);
        relationAutoCompleter = new UnitRelationAutoCompleter(txtRelation, listRelation, null, false, false);
        relationAutoCompleter.setRelation(null);

    }

    private boolean isValidEntry() {
        boolean status = true;
        if (txtStockName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Stock Name must not be blank.",
                    "Stock name.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtStockName.requestFocus();
        } else if (typeAutoCompleter.getStockType() == null) {
            JOptionPane.showMessageDialog(this, "You must choose stock type.",
                    "Stock Type.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtType.requestFocus();
        } else if (purUnitCompleter.getStockUnit() == null) {
            JOptionPane.showMessageDialog(this, "Purchase Unit  cannot be blank.",
                    "Stock Unit Pattern.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtPurUnit.requestFocus();
        } else if (relationAutoCompleter.getRelation() == null) {
            JOptionPane.showMessageDialog(this, "Relation Unit  cannot be blank.",
                    "Stock Relation.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtRelation.requestFocus();
        } else {
            stock.setStockCode(txtStockCode.getText());
            stock.setUserCode(txtUserCode.getText().trim());
            stock.setStockType(typeAutoCompleter.getStockType());
            stock.setStockName(txtStockName.getText().trim());
            stock.setCategory(categoryAutoCompleter.getCategory());
            stock.setBrand(brandAutoCompleter.getBrand());
            stock.setActive(chkActive.isSelected());
            stock.setBarcode(txtBarCode.getText().trim());
            stock.setPurPrice(Util1.getFloat(txtPurPrice.getText()));
            stock.setPurWeight(Util1.gerFloatOne(txtPurWt.getText()));
            stock.setPurUnit(purUnitCompleter.getStockUnit());
            stock.setSaleUnit(saleUnitCompleter.getStockUnit());
            stock.setUnitRelation(relationAutoCompleter.getRelation());
            stock.setSaleWeight(Util1.gerFloatOne(txtSaleWt.getText()));
            stock.setSalePriceN(Util1.getFloat(txtSalePrice.getText()));
            stock.setSalePriceA(Util1.getFloat(txtSalePriceA.getText()));
            stock.setSalePriceB(Util1.getFloat(txtSalePriceB.getText()));
            stock.setSalePriceC(Util1.getFloat(txtSalePriceC.getText()));
            stock.setSalePriceD(Util1.getFloat(txtSalePriceD.getText()));
            stock.setSalePriceE(Util1.getFloat(txtSalePriceE.getText()));
            stock.setCalculate(chkCal.isSelected());
            if (lblStatus.getText().equals("NEW")) {
                stock.setMacId(Global.macId);
                stock.setCompCode(Global.compCode);
                stock.setCreatedDate(Util1.getTodayDate());
                stock.setCreatedBy(Global.loginUser.getUserCode());
            } else {
                stock.setUpdatedBy(Global.loginUser.getUserCode());
            }
        }

        return status;
    }

    private void saveStock() {
        if (isValidEntry()) {
            stock = inventoryRepo.saveStock(stock);
            if (stock.getStockCode() != null) {
                if (lblStatus.getText().equals("NEW")) {
                    stockTableModel.addStock(stock);
                } else {
                    stockTableModel.setStock(selectRow, stock);
                }
                clear();
            }
        }
    }

    public void clear() {
        relationAutoCompleter.setRelation(null);
        brandAutoCompleter.setBrand(null);
        categoryAutoCompleter.setCategory(null);
        purUnitCompleter.setStockUnit(null);
        saleUnitCompleter.setStockUnit(null);
        typeAutoCompleter.setStockType(null);
        txtStockCode.setText(null);
        txtBarCode.setText(null);
        txtUserCode.setText(null);
        txtStockName.setText(null);
        chkActive.setSelected(true);
        chkCal.setSelected(true);
        txtPurPrice.setText(null);
        txtSalePriceE.setText(null);
        txtSalePrice.setText(null);
        txtSalePriceA.setText(null);
        txtSalePriceB.setText(null);
        txtSalePriceC.setText(null);
        txtSalePriceD.setText(null);
        txtPurWt.setText(null);
        txtSaleWt.setText(null);
        lblStatus.setText("NEW");
        txtUserCode.setEnabled(true);
        txtUserCode.requestFocus();
        stock = new Stock();
        lblRecord.setText(stockTableModel.getListStock().size() + "");

    }

    private void initKeyListener() {
        txtBarCode.addKeyListener(this);
        txtSalePriceE.addKeyListener(this);
        txtSalePrice.addKeyListener(this);
        txtSalePriceA.addKeyListener(this);
        txtSalePriceB.addKeyListener(this);
        txtSalePriceC.addKeyListener(this);
        txtSalePriceD.addKeyListener(this);
        txtPurPrice.addKeyListener(this);
        txtUserCode.addKeyListener(this);
        txtStockName.addKeyListener(this);
        lblRecord.addKeyListener(this);
        txtType.addKeyListener(this);
        txtCat.addKeyListener(this);
        txtBrand.addKeyListener(this);
        chkActive.addKeyListener(this);
        btnAddBrand.addKeyListener(this);
        btnAddCategory.addKeyListener(this);
        btnAddItemType.addKeyListener(this);
        btnUnit.addKeyListener(this);
        tblStock.addKeyListener(this);
        txtPurUnit.addKeyListener(this);
        txtSaleUnit.addKeyListener(this);
    }

    private void relationSetup() {
        RelationSetupDialog relationSetupDialog = new RelationSetupDialog();
        relationSetupDialog.setIconImage(icon);
        relationSetupDialog.setInventoryRepo(inventoryRepo);
        relationSetupDialog.setListUnitRelation(listRelation);
        relationSetupDialog.initMain();
        relationSetupDialog.setSize(Global.width / 2, Global.height / 2);
        relationSetupDialog.setLocationRelativeTo(null);
        relationSetupDialog.setVisible(true);
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
        tblStock = new javax.swing.JTable();
        lblRecord = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtFilter = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtUserCode = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnAddItemType = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtStockName = new javax.swing.JTextField();
        btnAddCategory = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnAddBrand = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtBarCode = new javax.swing.JTextField();
        chkActive = new javax.swing.JCheckBox();
        lblStatus = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtSalePrice = new javax.swing.JTextField();
        txtSalePriceA = new javax.swing.JTextField();
        txtSalePriceB = new javax.swing.JTextField();
        txtSalePriceC = new javax.swing.JTextField();
        txtSalePriceD = new javax.swing.JTextField();
        txtSalePriceE = new javax.swing.JTextField();
        txtSaleUnit = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtSaleWt = new javax.swing.JTextField();
        btnAddItemType1 = new javax.swing.JButton();
        txtType = new javax.swing.JTextField();
        txtCat = new javax.swing.JTextField();
        txtBrand = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txtPurPrice = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtPurUnit = new javax.swing.JTextField();
        btnUnit = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        txtPurWt = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtStockCode = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtRelation = new javax.swing.JTextField();
        btnAddRelation = new javax.swing.JButton();
        chkCal = new javax.swing.JCheckBox();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblStock.setFont(Global.textFont);
        tblStock.setModel(new javax.swing.table.DefaultTableModel(
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
        tblStock.setName("tblStock"); // NOI18N
        tblStock.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblStock);

        lblRecord.setEditable(false);
        lblRecord.setFont(Global.lableFont);
        lblRecord.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        lblRecord.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jLabel19.setFont(Global.lableFont);
        jLabel19.setText("Record :");

        txtFilter.setFont(Global.textFont);
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("User Code");

        txtUserCode.setFont(Global.textFont);
        txtUserCode.setName("txtUserCode"); // NOI18N
        txtUserCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUserCodeFocusGained(evt);
            }
        });
        txtUserCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUserCodeActionPerformed(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Stock Type");

        btnAddItemType.setBackground(Global.selectionColor);
        btnAddItemType.setFont(Global.lableFont);
        btnAddItemType.setForeground(new java.awt.Color(255, 255, 255));
        btnAddItemType.setText("...");
        btnAddItemType.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddItemType.setName("btnAddItemType"); // NOI18N
        btnAddItemType.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddItemType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddItemTypeActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Stock Name");

        txtStockName.setFont(Global.textFont);
        txtStockName.setName("txtStockName"); // NOI18N
        txtStockName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtStockNameFocusGained(evt);
            }
        });

        btnAddCategory.setBackground(Global.selectionColor);
        btnAddCategory.setFont(Global.lableFont);
        btnAddCategory.setForeground(new java.awt.Color(255, 255, 255));
        btnAddCategory.setText("...");
        btnAddCategory.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddCategory.setName("btnAddCategory"); // NOI18N
        btnAddCategory.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCategoryActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Category");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Brand");

        btnAddBrand.setBackground(Global.selectionColor);
        btnAddBrand.setFont(Global.lableFont);
        btnAddBrand.setForeground(new java.awt.Color(255, 255, 255));
        btnAddBrand.setText("...");
        btnAddBrand.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddBrand.setName("btnAddBrand"); // NOI18N
        btnAddBrand.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddBrand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddBrandActionPerformed(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Bar Code");

        txtBarCode.setFont(Global.textFont);
        txtBarCode.setName("txtBarCode"); // NOI18N
        txtBarCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBarCodeFocusGained(evt);
            }
        });

        chkActive.setFont(Global.lableFont);
        chkActive.setSelected(true);
        chkActive.setText("Active");
        chkActive.setName("chkActive"); // NOI18N

        lblStatus.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lblStatus.setText("NEW");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Sale Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

        jLabel11.setFont(Global.lableFont);
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("Sale Price N");

        jLabel14.setFont(Global.lableFont);
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("Sale Price A");

        jLabel15.setFont(Global.lableFont);
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("Sale Price B");

        jLabel16.setFont(Global.lableFont);
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Sale Price C");

        jLabel17.setFont(Global.lableFont);
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Sale Price D");

        jLabel18.setFont(Global.lableFont);
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Sale Price E");

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Sale Unit");

        txtSalePrice.setFont(Global.textFont);
        txtSalePrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSalePrice.setName("txtSalePrice"); // NOI18N
        txtSalePrice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalePriceFocusGained(evt);
            }
        });
        txtSalePrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSalePriceActionPerformed(evt);
            }
        });

        txtSalePriceA.setFont(Global.textFont);
        txtSalePriceA.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSalePriceA.setName("txtSalePriceA"); // NOI18N
        txtSalePriceA.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalePriceAFocusGained(evt);
            }
        });

        txtSalePriceB.setFont(Global.textFont);
        txtSalePriceB.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSalePriceB.setName("txtSalePriceB"); // NOI18N
        txtSalePriceB.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalePriceBFocusGained(evt);
            }
        });
        txtSalePriceB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSalePriceBActionPerformed(evt);
            }
        });

        txtSalePriceC.setFont(Global.textFont);
        txtSalePriceC.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSalePriceC.setName("txtSalePriceC"); // NOI18N
        txtSalePriceC.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalePriceCFocusGained(evt);
            }
        });

        txtSalePriceD.setFont(Global.textFont);
        txtSalePriceD.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSalePriceD.setName("txtSalePriceD"); // NOI18N
        txtSalePriceD.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalePriceDFocusGained(evt);
            }
        });

        txtSalePriceE.setFont(Global.textFont);
        txtSalePriceE.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSalePriceE.setName("txtSalePriceE"); // NOI18N
        txtSalePriceE.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalePriceEFocusGained(evt);
            }
        });

        txtSaleUnit.setFont(Global.textFont);
        txtSaleUnit.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSaleUnit.setName("txtSaleUnit"); // NOI18N
        txtSaleUnit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSaleUnitFocusGained(evt);
            }
        });
        txtSaleUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSaleUnitActionPerformed(evt);
            }
        });

        jLabel21.setFont(Global.lableFont);
        jLabel21.setText("Sale Weight");

        txtSaleWt.setFont(Global.textFont);
        txtSaleWt.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSaleWt.setName("txtSaleUnit"); // NOI18N
        txtSaleWt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSaleWtFocusGained(evt);
            }
        });
        txtSaleWt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSaleWtActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(txtSalePrice))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtSalePriceB, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                                    .addComponent(txtSalePriceA))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtSalePriceC, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtSalePriceD)
                                    .addComponent(txtSalePriceE)))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSaleWt)
                            .addComponent(txtSaleUnit))))
                .addGap(4, 4, 4))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel11, jLabel12, jLabel14, jLabel15, jLabel21});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtSaleUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(txtSaleWt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtSalePrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(txtSalePriceC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtSalePriceA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(txtSalePriceD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtSalePriceB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(txtSalePriceE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnAddItemType1.setFont(Global.lableFont);
        btnAddItemType1.setText("Import");
        btnAddItemType1.setName("btnAddItemType"); // NOI18N
        btnAddItemType1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnAddItemType1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddItemType1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddItemType1ActionPerformed(evt);
            }
        });

        txtType.setFont(Global.textFont);
        txtType.setName("txtType"); // NOI18N

        txtCat.setFont(Global.textFont);
        txtCat.setName("txtCat"); // NOI18N

        txtBrand.setFont(Global.textFont);
        txtBrand.setName("txtBrand"); // NOI18N
        txtBrand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBrandActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Purchase Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Purchase Unit");

        txtPurPrice.setFont(Global.textFont);
        txtPurPrice.setToolTipText("Purchase Price");
        txtPurPrice.setName("txtPurPrice"); // NOI18N
        txtPurPrice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPurPriceFocusGained(evt);
            }
        });

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Purchase Price");

        txtPurUnit.setFont(Global.textFont);
        txtPurUnit.setToolTipText("Purchase Price");
        txtPurUnit.setName("txtPurUnit"); // NOI18N
        txtPurUnit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPurUnitFocusGained(evt);
            }
        });

        btnUnit.setBackground(Global.selectionColor);
        btnUnit.setFont(Global.lableFont);
        btnUnit.setForeground(new java.awt.Color(255, 255, 255));
        btnUnit.setText("...");
        btnUnit.setName("btnUnit"); // NOI18N
        btnUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnitActionPerformed(evt);
            }
        });

        jLabel20.setFont(Global.lableFont);
        jLabel20.setText("Purchase Weight");

        txtPurWt.setFont(Global.textFont);
        txtPurWt.setToolTipText("Purchase Price");
        txtPurWt.setName("txtPurUnit"); // NOI18N
        txtPurWt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPurWtFocusGained(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtPurUnit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtPurPrice)
                    .addComponent(txtPurWt))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtPurUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUnit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(txtPurWt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPurPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("System Code");

        txtStockCode.setEditable(false);
        txtStockCode.setFont(Global.textFont);
        txtStockCode.setName("txtStockCode"); // NOI18N
        txtStockCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtStockCodeFocusGained(evt);
            }
        });
        txtStockCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtStockCodeActionPerformed(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Unit Relation");

        txtRelation.setFont(Global.textFont);
        txtRelation.setName("txtBarCode"); // NOI18N
        txtRelation.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRelationFocusGained(evt);
            }
        });

        btnAddRelation.setBackground(Global.selectionColor);
        btnAddRelation.setFont(Global.lableFont);
        btnAddRelation.setForeground(new java.awt.Color(255, 255, 255));
        btnAddRelation.setText("...");
        btnAddRelation.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddRelation.setName("btnAddBrand"); // NOI18N
        btnAddRelation.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddRelation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddRelationActionPerformed(evt);
            }
        });

        chkCal.setFont(Global.lableFont);
        chkCal.setSelected(true);
        chkCal.setText("Calulate Stock");
        chkCal.setName("chkActive"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtBarCode)
                            .addComponent(txtStockName)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCat)
                                    .addComponent(txtBrand))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnAddCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnAddBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtRelation)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAddRelation, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel2)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtType)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAddItemType, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtUserCode)
                            .addComponent(txtStockCode)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chkCal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkActive)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnAddItemType1)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtStockCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtUserCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtStockName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addComponent(btnAddItemType))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtCat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnAddCategory))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(txtBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtBarCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7)))
                            .addComponent(btnAddBrand))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtRelation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)))
                    .addComponent(btnAddRelation))
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAddItemType1)
                    .addComponent(chkActive)
                    .addComponent(lblStatus)
                    .addComponent(chkCal))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane2.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addGap(18, 18, 18)
                        .addComponent(lblRecord))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE)
                    .addComponent(txtFilter))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19))))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddItemTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddItemTypeActionPerformed
        // TODO add your handling code here:
        itemTypeSetupDialog.setListStockType(listStockType);
        itemTypeSetupDialog.setIconImage(icon);
        itemTypeSetupDialog.initMain();
        itemTypeSetupDialog.setSize(Global.width / 2, Global.height / 2);
        itemTypeSetupDialog.setLocationRelativeTo(null);
        itemTypeSetupDialog.setVisible(true);
    }//GEN-LAST:event_btnAddItemTypeActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        observer.selected("control", this);
    }//GEN-LAST:event_formComponentShown

    private void btnAddCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCategoryActionPerformed
        // TODO add your handling code here:
        categorySetupDailog.setListCategory(listCategory);
        categorySetupDailog.setIconImage(icon);
        categorySetupDailog.initMain();
        categorySetupDailog.setSize(Global.width / 2, Global.height / 2);
        categorySetupDailog.setLocationRelativeTo(null);
        categorySetupDailog.setVisible(true);
    }//GEN-LAST:event_btnAddCategoryActionPerformed

    private void btnAddBrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBrandActionPerformed
        // TODO add your handling code here:
        itemBrandDailog.setListStockBrand(listStockBrand);
        itemBrandDailog.setIconImage(icon);
        itemBrandDailog.initMain();
        itemBrandDailog.setSize(Global.width / 2, Global.height / 2);
        itemBrandDailog.setLocationRelativeTo(null);
        itemBrandDailog.setVisible(true);
    }//GEN-LAST:event_btnAddBrandActionPerformed

    private void btnUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnitActionPerformed
        // TODO add your handling code here:
        itemUnitSetupDailog.setListStockUnit(listStockUnit);
        itemUnitSetupDailog.setIconImage(icon);
        itemUnitSetupDailog.initMain();
        itemUnitSetupDailog.setSize(Global.width / 2, Global.height / 2);
        itemUnitSetupDailog.setLocationRelativeTo(null);
        itemUnitSetupDailog.setVisible(true);
    }//GEN-LAST:event_btnUnitActionPerformed

    private void txtStockNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStockNameFocusGained
        // TODO add your handling code here:
        txtStockName.selectAll();
    }//GEN-LAST:event_txtStockNameFocusGained

    private void txtBarCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBarCodeFocusGained
        // TODO add your handling code here:
        txtBarCode.selectAll();
    }//GEN-LAST:event_txtBarCodeFocusGained

    private void txtPurPriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPurPriceFocusGained
        // TODO add your handling code here:
        txtPurPrice.selectAll();
    }//GEN-LAST:event_txtPurPriceFocusGained

    private void txtSalePriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceFocusGained
        // TODO add your handling code here:
        txtSalePrice.selectAll();
    }//GEN-LAST:event_txtSalePriceFocusGained

    private void txtSalePriceAFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceAFocusGained
        // TODO add your handling code here:
        txtSalePriceA.selectAll();
    }//GEN-LAST:event_txtSalePriceAFocusGained

    private void txtSalePriceBFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceBFocusGained
        // TODO add your handling code here:
        txtSalePriceB.selectAll();
    }//GEN-LAST:event_txtSalePriceBFocusGained

    private void txtSalePriceCFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceCFocusGained
        // TODO add your handling code here:
        txtSalePriceC.selectAll();
    }//GEN-LAST:event_txtSalePriceCFocusGained

    private void txtSalePriceDFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceDFocusGained
        // TODO add your handling code here:
        txtSalePriceD.selectAll();
    }//GEN-LAST:event_txtSalePriceDFocusGained

    private void txtSalePriceEFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceEFocusGained
        // TODO add your handling code here:
        txtSalePriceE.selectAll();
    }//GEN-LAST:event_txtSalePriceEFocusGained

    private void btnAddItemType1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddItemType1ActionPerformed
        // TODO add your handling code here:
        StockImportDialog dialog = new StockImportDialog(Global.parentForm);
        dialog.setInventoryRepo(inventoryRepo);
        dialog.setWebClient(inventoryApi);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);


    }//GEN-LAST:event_btnAddItemType1ActionPerformed

    private void txtSalePriceBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSalePriceBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSalePriceBActionPerformed

    private void txtSalePriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSalePriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSalePriceActionPerformed

    private void txtPurUnitFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPurUnitFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPurUnitFocusGained

    private void txtUserCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUserCodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUserCodeActionPerformed

    private void txtBrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBrandActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBrandActionPerformed

    private void txtSaleUnitFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSaleUnitFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaleUnitFocusGained

    private void txtSaleUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSaleUnitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaleUnitActionPerformed

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        // TODO add your handling code here:
        if (txtFilter.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(swrf);
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    private void txtUserCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUserCodeFocusGained
        // TODO add your handling code here:
        txtUserCode.requestFocus();
    }//GEN-LAST:event_txtUserCodeFocusGained

    private void txtPurWtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPurWtFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPurWtFocusGained

    private void txtSaleWtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSaleWtFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaleWtFocusGained

    private void txtSaleWtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSaleWtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaleWtActionPerformed

    private void txtStockCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStockCodeFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockCodeFocusGained

    private void txtStockCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStockCodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockCodeActionPerformed

    private void txtRelationFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRelationFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRelationFocusGained

    private void btnAddRelationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRelationActionPerformed
        // TODO add your handling code here:
        relationSetup();
    }//GEN-LAST:event_btnAddRelationActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddBrand;
    private javax.swing.JButton btnAddCategory;
    private javax.swing.JButton btnAddItemType;
    private javax.swing.JButton btnAddItemType1;
    private javax.swing.JButton btnAddRelation;
    private javax.swing.JButton btnUnit;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JCheckBox chkCal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField lblRecord;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblStock;
    private javax.swing.JTextField txtBarCode;
    private javax.swing.JTextField txtBrand;
    private javax.swing.JTextField txtCat;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtPurPrice;
    private javax.swing.JTextField txtPurUnit;
    private javax.swing.JTextField txtPurWt;
    private javax.swing.JTextField txtRelation;
    private javax.swing.JTextField txtSalePrice;
    private javax.swing.JTextField txtSalePriceA;
    private javax.swing.JTextField txtSalePriceB;
    private javax.swing.JTextField txtSalePriceC;
    private javax.swing.JTextField txtSalePriceD;
    private javax.swing.JTextField txtSalePriceE;
    private javax.swing.JTextField txtSaleUnit;
    private javax.swing.JTextField txtSaleWt;
    private javax.swing.JTextField txtStockCode;
    private javax.swing.JTextField txtStockName;
    private javax.swing.JTextField txtType;
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
        Object sourceObj = e.getSource();
        String ctrlName = "-";

        if (sourceObj instanceof JTable jTable) {
            ctrlName = jTable.getName();
        } else if (sourceObj instanceof JTextField jTextField) {
            ctrlName = jTextField.getName();
        } else if (sourceObj instanceof JButton jButton) {
            ctrlName = jButton.getName();
        } else if (sourceObj instanceof JCheckBox jCheckBox) {
            ctrlName = jCheckBox.getName();
        } else if (sourceObj instanceof JFormattedTextField jFormattedTextField) {
            ctrlName = jFormattedTextField.getName();
        } else if (sourceObj instanceof JTextComponent jTextComponent) {
            ctrlName = jTextComponent.getName();
        } else if (sourceObj instanceof JTextFieldDateEditor jTextFieldDateEditor) {
            ctrlName = jTextFieldDateEditor.getName();
        }
        switch (ctrlName) {
            case "txtUserCode" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtStockName.requestFocus();
                    case KeyEvent.VK_DOWN ->
                        txtStockName.requestFocus();
                    case KeyEvent.VK_UP -> {
                    }
                    case KeyEvent.VK_RIGHT ->
                        txtType.requestFocus();
                    case KeyEvent.VK_LEFT -> {
                    }
                }
            }
            case "txtType" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtCat.requestFocus();
                    case KeyEvent.VK_UP ->
                        txtUserCode.requestFocus();
                    case KeyEvent.VK_RIGHT ->
                        btnAddItemType.requestFocus();
                    case KeyEvent.VK_LEFT ->
                        txtUserCode.requestFocus();
                }
            }
            case "btnAddItemType" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtStockName.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtType.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtStockName.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtType.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtStockName.requestFocus();
                        break;
                }
            }
            case "txtStockName" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtType.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtType.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtType.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtType.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtCat.requestFocus();
                        break;

                }
            }
            case "txtCat" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtBrand.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtType.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        btnAddCategory.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtStockName.requestFocus();
                        break;
                }
            }
            case "btnAddCategory" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtBrand.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtCat.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtBrand.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtCat.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtBrand.requestFocus();
                        break;
                }
            }
            case "txtBrand" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtBarCode.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtCat.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        btnAddBrand.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtCat.requestFocus();
                        break;
                }
            }
            case "btnAddBrand" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        //txtRemark.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtBrand.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        //txtRemark.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtBrand.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        //txtRemark.requestFocus();
                        break;
                }
            }
            case "txtRemark" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtBarCode.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtBarCode.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtBrand.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtBarCode.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtBrand.requestFocus();
                        break;

                }
            }
            case "txtBarCode" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtPurUnit.requestFocus();
                    case KeyEvent.VK_DOWN ->
                        txtPurUnit.requestFocus();
                    case KeyEvent.VK_UP ->
                        txtBrand.requestFocus();
                    case KeyEvent.VK_RIGHT ->
                        txtPurUnit.requestFocus();
                    case KeyEvent.VK_LEFT ->
                        txtBrand.requestFocus();

                }
            }

            case "txtPurWt" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtPurUnit.requestFocus();
                    case KeyEvent.VK_DOWN ->
                        txtPurUnit.requestFocus();
                    case KeyEvent.VK_UP ->
                        txtPurUnit.requestFocus();
                }
            }
            case "btnUnit" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtPurPrice.requestFocus();
                    case KeyEvent.VK_DOWN ->
                        txtPurPrice.requestFocus();

                    case KeyEvent.VK_RIGHT ->
                        txtPurPrice.requestFocus();

                }
            }
            case "txtPurPrice" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSaleUnit.requestFocus();
                }
            }
            case "txtSaleUnit" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSalePrice.requestFocus();
                }
            }

            case "txtSaleWt" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSaleUnit.requestFocus();
                    case KeyEvent.VK_UP ->
                        chkActive.requestFocus();
                    case KeyEvent.VK_RIGHT ->
                        txtSaleUnit.requestFocus();
                    case KeyEvent.VK_LEFT ->
                        chkActive.requestFocus();
                }
            }
            case "txtPurUnit" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtPurPrice.requestFocus();
                }
            }

            case "txtSalePrice" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSalePriceA.requestFocus();
                }
            }

            case "txtSalePriceA" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSalePriceB.requestFocus();
                }
            }

            case "txtSalePriceB" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSalePriceC.requestFocus();
                }
            }

            case "txtSalePriceC" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSalePriceD.requestFocus();
                }
            }

            case "txtSalePriceD" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER ->
                        txtSalePriceE.requestFocus();

                }
            }

            case "txtSalePriceStd" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER -> {
                    }

                }
            }

            case "tblStock" -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN -> {
                        selectRow = tblStock.convertRowIndexToModel(tblStock.getSelectedRow());
                        setStock(selectRow);
                    }
                    case KeyEvent.VK_UP -> {
                        selectRow = tblStock.convertRowIndexToModel(tblStock.getSelectedRow());
                        setStock(selectRow);
                    }
                    case KeyEvent.VK_RIGHT -> {
                        if (e.isControlDown()) {
                            txtUserCode.requestFocus();
                        }
                    }

                }
            }

        }
    }

    @Override
    public void save() {
        saveStock();
    }

    @Override
    public void delete() {
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }

    @Override
    public void refresh() {
        searchStock();
    }

    @Override
    public void filter() {
    }

    @Override
    public String panelName() {
        return this.getName();
    }
}
