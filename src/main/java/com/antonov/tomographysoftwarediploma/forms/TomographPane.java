package com.antonov.tomographysoftwarediploma.forms;

import dblayer.DbModule;
import com.antonov.tomographysoftwarediploma.DensityAnalizator;
import com.antonov.tomographysoftwarediploma.ImageTransformator;
import com.antonov.tomographysoftwarediploma.LUTFunctions;
import com.antonov.tomographysoftwarediploma.Utils;
import com.antonov.tomographysoftwarediploma.impl.Controller;
import com.antonov.tomographysoftwarediploma.impl.HardwareModuleController;
import com.antonov.tomographysoftwarediploma.impl.ITomographView;
import com.antonov.tomographysoftwarediploma.impl.ModellingModuleController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JRadioButton;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TomographPane extends javax.swing.JFrame implements ITomographView {

    private static Logger logger = LoggerFactory.getLogger(TomographPane.class);
    ModellingModuleController modellingModuleController;
    HardwareModuleController hardwareModuleController;
    private final ResourceBundle bundle = ResourceBundle.getBundle(
            "bundle_Rus");

    public static final List<String> modelNames = new ArrayList<>(); // For modelling images names
    private BufferedImage sinogramImage;
    private BufferedImage reconstructImage;
    private BufferedImage reconstructColorImage;
    private BufferedImage scaleReconstructImage; // для DensityViewer
    private List<BufferedImage> arrayReconstructedImage = new ArrayList<>();

    ImageTransformator sinogramCreator = new ImageTransformator();
    String nameOfProjData;

    public TomographPane() {
        initComponents();
        
        this.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                    int i=JOptionPane.showConfirmDialog(null, bundle.getString("CONFIRMATION_EXIT"),"",JOptionPane.YES_NO_OPTION);
                    if(i==0)
                        logger.info("=======Stop TomographySoftware 1.0.0 application=======");
                        System.exit(0);//cierra aplicacion
                }
            });
    }

    @Override
    public void setModellingController(ModellingModuleController controller) {
        this.modellingModuleController = controller;
    }

    @Override
    public void setHardwareController(HardwareModuleController controller) {
        this.hardwareModuleController = controller;
    }

    @Override
    public void setModellingImages(Map<String, BufferedImage> imageSamplesMapWithNames) {
        fillModelNames(imageSamplesMapWithNames);
        initModelList();
    }

    private void fillModelNames(Map<String, BufferedImage> imageSamplesMapWithNames) {
        if (!imageSamplesMapWithNames.isEmpty()) {

            for (String name : imageSamplesMapWithNames.keySet()) {
                modelNames.add(name);
            }
        } else {
            logger.warn("Map of modelling images is empty ");
        }
    }

    private void initModelList() {

        modelList.setModel(new javax.swing.AbstractListModel() {

            @Override
            public int getSize() {
                return modelNames.size();
            }

            @Override
            public Object getElementAt(int i) {
                return modelNames.get(i);
            }
        });

        modelList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                if (evt.getValueIsAdjusting()) {
                    String model = (String) modelList.getSelectedValue();
                    modellingModuleController.setModelCurrentModellingImageByName(model);
                }
            }
        });
    }

    @Override
    public void setCurrentModellingImage(BufferedImage image) {
        ImageIcon icon = new ImageIcon(image);
        labelImage1.setIcon(icon);
    }

    @Override
    public void clearResultModelling() {
        labelImage2.setIcon(null);
    }

    @Override
    public void disableModellingControls() {

        buttonSaveSinogram.setEnabled(false);
        buttonSaveReconstruct.setEnabled(false);
        buttonConverse.setEnabled(true);
        buttonReconstruct.setEnabled(false);
        coloring.setEnabled(false);
        coloring.setSelected(false);
        colorPanel.setVisible(false);
        buttonDensityViewer.setEnabled(false);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        dialogProgressBar = new javax.swing.JDialog();
        jLabel11 = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        openFileChooser = new javax.swing.JFileChooser();
        filterGroup = new javax.swing.ButtonGroup();
        saveFileChooser = new javax.swing.JFileChooser();
        colorGroup = new javax.swing.ButtonGroup();
        dialogProjDataChooser = new javax.swing.JDialog();
        jLabel5 = new javax.swing.JLabel();
        buttonProjDataOk = new javax.swing.JButton();
        buttonProjDataCancel = new javax.swing.JButton();
        buttonProjDataOpenFile = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableProjData = new javax.swing.JTable();
        dialogFilterChooser = new javax.swing.JDialog();
        jLabel7 = new javax.swing.JLabel();
        filteringTomograph = new javax.swing.JCheckBox();
        filterRampTomograph = new javax.swing.JRadioButton();
        filterShepploganTomograph = new javax.swing.JRadioButton();
        filterHammingTomograph = new javax.swing.JRadioButton();
        filterHannTomograph = new javax.swing.JRadioButton();
        filterCosineTomograph = new javax.swing.JRadioButton();
        filterBlackManTomograph = new javax.swing.JRadioButton();
        buttonOkFilterTomograph = new javax.swing.JButton();
        filterGroupTomograph = new javax.swing.ButtonGroup();
        dialogNameAsker = new javax.swing.JDialog();
        jLabel8 = new javax.swing.JLabel();
        textFielsName = new javax.swing.JTextField();
        textFielsDescription = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        buttonOkSetName = new javax.swing.JButton();
        buttonCanselSetName = new javax.swing.JButton();
        densityViewer = new javax.swing.JDialog();
        jPanel3 = new javax.swing.JPanel();
        labelImageDensityViewer = new javax.swing.JLabel();
        densityGraphPane = new javax.swing.JPanel();
        densitySlider = new javax.swing.JSlider();
        colorGroupTomograph = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        Model = new javax.swing.JPanel();
        modelPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        modelList = new javax.swing.JList();
        buttonOpenFile = new javax.swing.JButton();
        image1 = new javax.swing.JScrollPane();
        labelImage1 = new javax.swing.JLabel();
        buttonConverse = new javax.swing.JButton();
        Image2 = new javax.swing.JScrollPane();
        labelImage2 = new javax.swing.JLabel();
        buttonReconstruct = new javax.swing.JButton();
        buttonSaveReconstruct = new javax.swing.JButton();
        buttonSaveSinogram = new javax.swing.JButton();
        filterActionPanel = new javax.swing.JPanel();
        filteringModel = new javax.swing.JCheckBox();
        filterPanel = new javax.swing.JPanel();
        filterRamp = new javax.swing.JRadioButton();
        filterShepplogan = new javax.swing.JRadioButton();
        filterHamming = new javax.swing.JRadioButton();
        filterHann = new javax.swing.JRadioButton();
        filterCosine = new javax.swing.JRadioButton();
        filterBlackman = new javax.swing.JRadioButton();
        coloring = new javax.swing.JCheckBox();
        colorPanel = new javax.swing.JPanel();
        color1 = new javax.swing.JRadioButton();
        color2 = new javax.swing.JRadioButton();
        color4 = new javax.swing.JRadioButton();
        color3 = new javax.swing.JRadioButton();
        buttonDensityViewer = new javax.swing.JButton();
        ParamModellingPane = new javax.swing.JPanel();
        labelDetectors = new javax.swing.JLabel();
        scansModel = new javax.swing.JTextField();
        labelStepsize = new javax.swing.JLabel();
        stepsize = new javax.swing.JTextField();
        labelReconstructSize = new javax.swing.JLabel();
        reconstructSize = new javax.swing.JTextField();
        Tomograph = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        scansTomograph = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        stepSizeTomograph = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        sliderImage = new javax.swing.JSlider();
        jScrollPane1 = new javax.swing.JScrollPane();
        labelimage3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        outputImgSizeTomograph = new javax.swing.JTextField();
        buttonSaveReconstructTomograph = new javax.swing.JButton();
        buttonOpenProjData = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        coloringTomograph = new javax.swing.JCheckBox();
        colorPanelTomograph = new javax.swing.JPanel();
        color1Tomograph = new javax.swing.JRadioButton();
        color2Tomograph = new javax.swing.JRadioButton();
        color4Tomograph = new javax.swing.JRadioButton();
        color3Tomograph = new javax.swing.JRadioButton();
        buttonDensityViewer1 = new javax.swing.JButton();
        buttonDensityViewerTomograph = new javax.swing.JButton();

        dialogProgressBar.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        dialogProgressBar.setAlwaysOnTop(true);
        dialogProgressBar.setMinimumSize(new java.awt.Dimension(409, 99));
        dialogProgressBar.setResizable(false);

        jLabel11.setText("Идет процесс вычисления, пожалуйста подождите");

        javax.swing.GroupLayout dialogProgressBarLayout = new javax.swing.GroupLayout(dialogProgressBar.getContentPane());
        dialogProgressBar.getContentPane().setLayout(dialogProgressBarLayout);
        dialogProgressBarLayout.setHorizontalGroup(
            dialogProgressBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogProgressBarLayout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addGroup(dialogProgressBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(629, Short.MAX_VALUE))
        );
        dialogProgressBarLayout.setVerticalGroup(
            dialogProgressBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogProgressBarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(588, Short.MAX_VALUE))
        );

        dialogProgressBar.setLocationRelativeTo(null);

        openFileChooser.setFileFilter(new ImageFilter());

        saveFileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        saveFileChooser.setCurrentDirectory(new java.io.File("C:\\"));
            saveFileChooser.setFileFilter(new FileNameExtensionFilter("JPEG File", "jpg"));
            saveFileChooser.setFileFilter(new FileNameExtensionFilter("PNG File", "png"));
            saveFileChooser.setFileFilter(new FileNameExtensionFilter("BMP File", "bmp"));

            dialogProjDataChooser.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            dialogProjDataChooser.setAlwaysOnTop(true);
            dialogProjDataChooser.setAutoRequestFocus(false);
            dialogProjDataChooser.setMinimumSize(new java.awt.Dimension(560, 301));
            dialogProjDataChooser.setResizable(false);

            jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
            jLabel5.setText("Выберите набор проекционных данных");

            buttonProjDataOk.setText("ОК");
            buttonProjDataOk.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonProjDataOkActionPerformed(evt);
                }
            });

            buttonProjDataCancel.setText("Отмена");
            buttonProjDataCancel.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonProjDataCancelActionPerformed(evt);
                }
            });

            buttonProjDataOpenFile.setText("Открыть из файла");
            buttonProjDataOpenFile.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonProjDataOpenFileActionPerformed(evt);
                }
            });

            tableProjData.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                    {null, null, null},
                    {null, null, null},
                    {null, null, null},
                    {null, null, null}
                },
                new String [] {
                    "Дата", "Название", "Дополнительно"
                }
            ) {
                Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class, java.lang.String.class
                };
                boolean[] canEdit = new boolean [] {
                    false, false, false
                };

                public Class getColumnClass(int columnIndex) {
                    return types [columnIndex];
                }

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit [columnIndex];
                }
            });
            tableProjData.setDragEnabled(true);
            tableProjData.setFocusable(false);
            jScrollPane3.setViewportView(tableProjData);

            javax.swing.GroupLayout dialogProjDataChooserLayout = new javax.swing.GroupLayout(dialogProjDataChooser.getContentPane());
            dialogProjDataChooser.getContentPane().setLayout(dialogProjDataChooserLayout);
            dialogProjDataChooserLayout.setHorizontalGroup(
                dialogProjDataChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogProjDataChooserLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(buttonProjDataOk, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(buttonProjDataCancel)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(buttonProjDataOpenFile)
                    .addGap(129, 129, 129))
                .addGroup(dialogProjDataChooserLayout.createSequentialGroup()
                    .addGap(143, 143, 143)
                    .addComponent(jLabel5)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogProjDataChooserLayout.createSequentialGroup()
                    .addContainerGap(35, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 494, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(31, 31, 31))
            );
            dialogProjDataChooserLayout.setVerticalGroup(
                dialogProjDataChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(dialogProjDataChooserLayout.createSequentialGroup()
                    .addGap(27, 27, 27)
                    .addComponent(jLabel5)
                    .addGap(18, 18, 18)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                    .addGroup(dialogProjDataChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttonProjDataOk)
                        .addComponent(buttonProjDataCancel)
                        .addComponent(buttonProjDataOpenFile))
                    .addGap(24, 24, 24))
            );

            dialogProjDataChooser.setLocationRelativeTo(null);

            dialogFilterChooser.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            dialogFilterChooser.setAlwaysOnTop(true);
            dialogFilterChooser.setMinimumSize(new java.awt.Dimension(296, 297));
            dialogFilterChooser.setModal(true);
            dialogFilterChooser.setResizable(false);

            jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
            jLabel7.setText("Выберите фильтр реконструкции");

            filteringTomograph.setText("Без фильтра");
            filteringTomograph.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    filteringTomographActionPerformed(evt);
                }
            });

            filterGroupTomograph.add(filterRampTomograph);
            filterRampTomograph.setText("ramp");

            filterGroupTomograph.add(filterShepploganTomograph);
            filterShepploganTomograph.setText("shepplogan");

            filterGroupTomograph.add(filterHammingTomograph);
            filterHammingTomograph.setText("hamming");

            filterGroupTomograph.add(filterHannTomograph);
            filterHannTomograph.setText("hann");

            filterGroupTomograph.add(filterCosineTomograph);
            filterCosineTomograph.setText("cosine");

            filterGroupTomograph.add(filterBlackManTomograph);
            filterBlackManTomograph.setText("blackman");

            buttonOkFilterTomograph.setText("OK");
            buttonOkFilterTomograph.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonOkFilterTomographActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout dialogFilterChooserLayout = new javax.swing.GroupLayout(dialogFilterChooser.getContentPane());
            dialogFilterChooser.getContentPane().setLayout(dialogFilterChooserLayout);
            dialogFilterChooserLayout.setHorizontalGroup(
                dialogFilterChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(dialogFilterChooserLayout.createSequentialGroup()
                    .addGroup(dialogFilterChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(dialogFilterChooserLayout.createSequentialGroup()
                            .addGap(38, 38, 38)
                            .addComponent(jLabel7))
                        .addGroup(dialogFilterChooserLayout.createSequentialGroup()
                            .addGap(101, 101, 101)
                            .addGroup(dialogFilterChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(filterRampTomograph)
                                .addComponent(filteringTomograph)
                                .addComponent(filterHammingTomograph)
                                .addComponent(filterHannTomograph)
                                .addComponent(filterCosineTomograph)
                                .addComponent(filterBlackManTomograph)
                                .addComponent(filterShepploganTomograph)
                                .addComponent(buttonOkFilterTomograph, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addContainerGap(301, Short.MAX_VALUE))
            );
            dialogFilterChooserLayout.setVerticalGroup(
                dialogFilterChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(dialogFilterChooserLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel7)
                    .addGap(18, 18, 18)
                    .addComponent(filteringTomograph)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(filterRampTomograph)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(filterShepploganTomograph)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(filterHammingTomograph)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(filterHannTomograph)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(filterCosineTomograph)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(filterBlackManTomograph)
                    .addGap(18, 18, 18)
                    .addComponent(buttonOkFilterTomograph)
                    .addContainerGap(41, Short.MAX_VALUE))
            );

            dialogFilterChooser.setLocationRelativeTo(null);

            dialogNameAsker.setMinimumSize(new java.awt.Dimension(427, 245));
            dialogNameAsker.setResizable(false);

            jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
            jLabel8.setText("Введите данные для нового файла проекционных данных");

            jLabel9.setText("Описание");

            jLabel10.setText("Название");

            buttonOkSetName.setText("OK");
            buttonOkSetName.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonOkSetNameActionPerformed(evt);
                }
            });

            buttonCanselSetName.setText("Отмена");
            buttonCanselSetName.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonCanselSetNameActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout dialogNameAskerLayout = new javax.swing.GroupLayout(dialogNameAsker.getContentPane());
            dialogNameAsker.getContentPane().setLayout(dialogNameAskerLayout);
            dialogNameAskerLayout.setHorizontalGroup(
                dialogNameAskerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogNameAskerLayout.createSequentialGroup()
                    .addGap(128, 128, 128)
                    .addComponent(buttonOkSetName, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(buttonCanselSetName)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogNameAskerLayout.createSequentialGroup()
                    .addContainerGap(30, Short.MAX_VALUE)
                    .addGroup(dialogNameAskerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogNameAskerLayout.createSequentialGroup()
                            .addGroup(dialogNameAskerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel9)
                                .addComponent(jLabel10)
                                .addGroup(dialogNameAskerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(textFielsDescription, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(textFielsName, javax.swing.GroupLayout.Alignment.LEADING)))
                            .addGap(28, 28, 28))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogNameAskerLayout.createSequentialGroup()
                            .addComponent(jLabel8)
                            .addGap(19, 19, 19))))
            );
            dialogNameAskerLayout.setVerticalGroup(
                dialogNameAskerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(dialogNameAskerLayout.createSequentialGroup()
                    .addGap(22, 22, 22)
                    .addComponent(jLabel8)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel10)
                    .addGap(8, 8, 8)
                    .addComponent(textFielsName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel9)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(textFielsDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(26, 26, 26)
                    .addGroup(dialogNameAskerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttonOkSetName)
                        .addComponent(buttonCanselSetName))
                    .addContainerGap(48, Short.MAX_VALUE))
            );

            dialogNameAsker.setLocationRelativeTo(null);

            densityViewer.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            densityViewer.setTitle("Анализатор плотности");
            densityViewer.setAlwaysOnTop(true);
            densityViewer.setLocationByPlatform(true);
            densityViewer.setMinimumSize(new java.awt.Dimension(950, 550));
            densityViewer.setResizable(false);

            jPanel3.setMaximumSize(new java.awt.Dimension(475, 404));
            jPanel3.setMinimumSize(new java.awt.Dimension(475, 404));
            jPanel3.setPreferredSize(new java.awt.Dimension(475, 404));

            labelImageDensityViewer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            labelImageDensityViewer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

            javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
            jPanel3.setLayout(jPanel3Layout);
            jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(51, 51, 51)
                    .addComponent(labelImageDensityViewer)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(66, 66, 66)
                    .addComponent(labelImageDensityViewer)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            densityGraphPane.setMaximumSize(new java.awt.Dimension(475, 404));
            densityGraphPane.setMinimumSize(new java.awt.Dimension(475, 404));
            densityGraphPane.setPreferredSize(new java.awt.Dimension(475, 404));

            javax.swing.GroupLayout densityGraphPaneLayout = new javax.swing.GroupLayout(densityGraphPane);
            densityGraphPane.setLayout(densityGraphPaneLayout);
            densityGraphPaneLayout.setHorizontalGroup(
                densityGraphPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 0, Short.MAX_VALUE)
            );
            densityGraphPaneLayout.setVerticalGroup(
                densityGraphPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 404, Short.MAX_VALUE)
            );

            densitySlider.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
            densitySlider.setValue(0);
            densitySlider.setFocusable(false);
            densitySlider.setMaximumSize(new java.awt.Dimension(475, 40));
            densitySlider.setMinimumSize(new java.awt.Dimension(475, 23));
            densitySlider.setPreferredSize(new java.awt.Dimension(475, 23));
            densitySlider.setRequestFocusEnabled(false);
            densitySlider.setVerifyInputWhenFocusTarget(false);
            densitySlider.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    densitySliderStateChanged(evt);
                }
            });

            javax.swing.GroupLayout densityViewerLayout = new javax.swing.GroupLayout(densityViewer.getContentPane());
            densityViewer.getContentPane().setLayout(densityViewerLayout);
            densityViewerLayout.setHorizontalGroup(
                densityViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(densityViewerLayout.createSequentialGroup()
                    .addGap(26, 26, 26)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                    .addGroup(densityViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(densityGraphPane, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(densitySlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(19, 19, 19))
            );
            densityViewerLayout.setVerticalGroup(
                densityViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(densityViewerLayout.createSequentialGroup()
                    .addGap(27, 27, 27)
                    .addGroup(densityViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(densityGraphPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(densitySlider, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 11, Short.MAX_VALUE))
            );

            densityViewer.setLocationRelativeTo(null);

            setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
            setTitle("Томографический комплекс 1.0 НИЯУ МИФИ");
            setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            setMinimumSize(new java.awt.Dimension(1250, 700));

            jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
            jTabbedPane1.setToolTipText("");
            jTabbedPane1.setDoubleBuffered(true);

            Model.setMinimumSize(new java.awt.Dimension(800, 600));
            Model.setLayout(new java.awt.GridBagLayout());

            java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bundle_Rus"); // NOI18N
            modelPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("LIST_MODEL_TITLE"))); // NOI18N

            modelList.setFocusable(false);
            jScrollPane4.setViewportView(modelList);

            javax.swing.GroupLayout modelPanelLayout = new javax.swing.GroupLayout(modelPanel);
            modelPanel.setLayout(modelPanelLayout);
            modelPanelLayout.setHorizontalGroup(
                modelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(modelPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                    .addContainerGap())
            );
            modelPanelLayout.setVerticalGroup(
                modelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, modelPanelLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            );

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            Model.add(modelPanel, gridBagConstraints);

            buttonOpenFile.setText("Открыть файл");
            buttonOpenFile.setFocusPainted(false);
            buttonOpenFile.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonOpenFileActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            Model.add(buttonOpenFile, gridBagConstraints);

            labelImage1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            labelImage1.setAutoscrolls(true);
            labelImage1.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    labelImage1MouseClicked(evt);
                }
            });
            image1.setViewportView(labelImage1);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 13;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 6;
            gridBagConstraints.gridheight = 10;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.ipadx = 477;
            gridBagConstraints.ipady = 517;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(11, 18, 0, 0);
            Model.add(image1, gridBagConstraints);

            buttonConverse.setText("Синограмма");
            buttonConverse.setDefaultCapable(false);
            buttonConverse.setEnabled(false);
            buttonConverse.setFocusPainted(false);
            buttonConverse.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonConverseActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 13;
            gridBagConstraints.gridy = 17;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(18, 18, 0, 0);
            Model.add(buttonConverse, gridBagConstraints);

            labelImage2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            labelImage2.setAutoscrolls(true);
            labelImage2.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    labelImage2MouseClicked(evt);
                }
            });
            Image2.setViewportView(labelImage2);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 19;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.gridheight = 10;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.ipadx = 477;
            gridBagConstraints.ipady = 517;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(11, 18, 0, 37);
            Model.add(Image2, gridBagConstraints);

            buttonReconstruct.setText("Реконструкция");
            buttonReconstruct.setDefaultCapable(false);
            buttonReconstruct.setEnabled(false);
            buttonReconstruct.setFocusPainted(false);
            buttonReconstruct.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonReconstructActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 14;
            gridBagConstraints.gridy = 17;
            gridBagConstraints.gridwidth = 3;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(18, 10, 0, 0);
            Model.add(buttonReconstruct, gridBagConstraints);

            buttonSaveReconstruct.setText("<html> Сохранить<br> реконструкцию");
            buttonSaveReconstruct.setActionCommand("Сохранить<br> реконструкцию");
            buttonSaveReconstruct.setEnabled(false);
            buttonSaveReconstruct.setFocusPainted(false);
            buttonSaveReconstruct.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonSaveReconstructActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            Model.add(buttonSaveReconstruct, gridBagConstraints);

            buttonSaveSinogram.setText("<html> Сохранить<br> синограмму");
            buttonSaveSinogram.setActionCommand("Сохранить<br> реконструкцию");
            buttonSaveSinogram.setEnabled(false);
            buttonSaveSinogram.setFocusPainted(false);
            buttonSaveSinogram.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonSaveSinogramActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            Model.add(buttonSaveSinogram, gridBagConstraints);

            filteringModel.setText("Фильтрация");
            filteringModel.setFocusPainted(false);
            filteringModel.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    filteringModelActionPerformed(evt);
                }
            });

            filterGroup.add(filterRamp);
            filterRamp.setText("ramp");

            filterGroup.add(filterShepplogan);
            filterShepplogan.setText("shepplogan");

            filterGroup.add(filterHamming);
            filterHamming.setText("hamming");

            filterGroup.add(filterHann);
            filterHann.setText("hann");

            filterGroup.add(filterCosine);
            filterCosine.setText("cosine");

            filterGroup.add(filterBlackman);
            filterBlackman.setText("blackman");

            javax.swing.GroupLayout filterPanelLayout = new javax.swing.GroupLayout(filterPanel);
            filterPanel.setLayout(filterPanelLayout);
            filterPanelLayout.setHorizontalGroup(
                filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(filterPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(filterHann)
                        .addComponent(filterRamp)
                        .addComponent(filterShepplogan)
                        .addComponent(filterHamming)
                        .addComponent(filterBlackman)
                        .addComponent(filterCosine))
                    .addContainerGap(95, Short.MAX_VALUE))
            );
            filterPanelLayout.setVerticalGroup(
                filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, filterPanelLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(filterRamp)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(filterShepplogan)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(filterHamming)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(filterHann)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(filterCosine)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(filterBlackman)
                    .addContainerGap())
            );

            javax.swing.GroupLayout filterActionPanelLayout = new javax.swing.GroupLayout(filterActionPanel);
            filterActionPanel.setLayout(filterActionPanelLayout);
            filterActionPanelLayout.setHorizontalGroup(
                filterActionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(filterActionPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(filteringModel)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(filterActionPanelLayout.createSequentialGroup()
                    .addComponent(filterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE))
            );
            filterActionPanelLayout.setVerticalGroup(
                filterActionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(filterActionPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(filteringModel)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(filterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            filterPanel.setVisible(false);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 9;
            gridBagConstraints.gridwidth = 9;
            gridBagConstraints.gridheight = 11;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(18, 10, 0, 0);
            Model.add(filterActionPanel, gridBagConstraints);
            filterActionPanel.setVisible(false);

            coloring.setText("Цветовой фильтр");
            coloring.setEnabled(false);
            coloring.setFocusPainted(false);
            coloring.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    coloringActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 13;
            gridBagConstraints.gridy = 19;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.gridheight = 12;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(2, 18, 7, 0);
            Model.add(coloring, gridBagConstraints);

            colorPanel.setMaximumSize(new java.awt.Dimension(236, 25));
            colorPanel.setMinimumSize(new java.awt.Dimension(236, 23));
            colorPanel.setPreferredSize(new java.awt.Dimension(236, 23));

            colorGroup.add(color1);
            color1.setText("color1");
            color1.setFocusPainted(false);
            color1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    color1ActionPerformed(evt);
                }
            });

            colorGroup.add(color2);
            color2.setText("color2");
            color2.setFocusPainted(false);
            color2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    color2ActionPerformed(evt);
                }
            });

            colorGroup.add(color4);
            color4.setText("color4");
            color4.setFocusable(false);
            color4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    color4ActionPerformed(evt);
                }
            });

            colorGroup.add(color3);
            color3.setText("color3");
            color3.setFocusPainted(false);
            color3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    color3ActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout colorPanelLayout = new javax.swing.GroupLayout(colorPanel);
            colorPanel.setLayout(colorPanelLayout);
            colorPanelLayout.setHorizontalGroup(
                colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(colorPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(color1)
                    .addGap(2, 2, 2)
                    .addComponent(color2)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(color3)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(color4)
                    .addContainerGap(69, Short.MAX_VALUE))
            );
            colorPanelLayout.setVerticalGroup(
                colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(colorPanelLayout.createSequentialGroup()
                    .addGroup(colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(color1)
                        .addComponent(color2)
                        .addComponent(color3)
                        .addComponent(color4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGap(0, 0, Short.MAX_VALUE))
            );

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 19;
            gridBagConstraints.gridy = 17;
            gridBagConstraints.gridheight = 2;
            gridBagConstraints.ipadx = 60;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(18, 18, 0, 0);
            Model.add(colorPanel, gridBagConstraints);
            colorPanel.setVisible(false);

            buttonDensityViewer.setText("Анализатор плотности");
            buttonDensityViewer.setEnabled(false);
            buttonDensityViewer.setFocusPainted(false);
            buttonDensityViewer.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonDensityViewerActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 17;
            gridBagConstraints.gridy = 17;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(18, 10, 0, 0);
            Model.add(buttonDensityViewer, gridBagConstraints);

            ParamModellingPane.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("PANE_PARAM_MODELLING"))); // NOI18N
            ParamModellingPane.setLayout(new java.awt.GridBagLayout());

            labelDetectors.setText("Число сканирований");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            ParamModellingPane.add(labelDetectors, gridBagConstraints);

            scansModel.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            scansModel.setText("400");
            scansModel.setToolTipText("");
            scansModel.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyTyped(java.awt.event.KeyEvent evt) {
                    scansModelKeyTyped(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridheight = 2;
            gridBagConstraints.ipadx = 55;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
            ParamModellingPane.add(scansModel, gridBagConstraints);

            labelStepsize.setText("<html>Шаг сканирования,<br>        град");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.gridheight = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            ParamModellingPane.add(labelStepsize, gridBagConstraints);

            stepsize.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            stepsize.setText("1");
            stepsize.setToolTipText("");
            stepsize.setAutoscrolls(false);
            stepsize.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyTyped(java.awt.event.KeyEvent evt) {
                    stepsizeKeyTyped(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.gridheight = 2;
            gridBagConstraints.ipadx = 55;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
            ParamModellingPane.add(stepsize, gridBagConstraints);

            labelReconstructSize.setText("Размер реконструкции");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 4;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
            ParamModellingPane.add(labelReconstructSize, gridBagConstraints);

            reconstructSize.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            reconstructSize.setText("400");
            reconstructSize.setToolTipText("");
            reconstructSize.setAutoscrolls(false);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 4;
            gridBagConstraints.ipadx = 55;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
            ParamModellingPane.add(reconstructSize, gridBagConstraints);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 5;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            Model.add(ParamModellingPane, gridBagConstraints);

            jTabbedPane1.addTab("Модель", Model);

            jLabel1.setText("Количество сканирований");

            scansTomograph.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            scansTomograph.setText("700");

            jLabel2.setText("Шаг поворота, град");

            stepSizeTomograph.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            stepSizeTomograph.setText("1");

            jLabel3.setText("Шаг протяжки, мм");

            jTextField3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            jTextField3.setText("5");

            jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
            jLabel4.setText("Начальные условия");

            sliderImage.setValue(0);
            sliderImage.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    sliderImageStateChanged(evt);
                }
            });

            labelimage3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            labelimage3.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    labelimage3MouseClicked(evt);
                }
            });
            jScrollPane1.setViewportView(labelimage3);

            jLabel6.setText("Размер реконструкции");

            outputImgSizeTomograph.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            outputImgSizeTomograph.setText("500");

            buttonSaveReconstructTomograph.setText("<html> Сохранить<br> реконструкцию");
            buttonSaveReconstructTomograph.setActionCommand("Сохранить<br> реконструкцию");
            buttonSaveReconstructTomograph.setEnabled(false);
            buttonSaveReconstructTomograph.setFocusPainted(false);
            buttonSaveReconstructTomograph.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonSaveReconstructTomographActionPerformed(evt);
                }
            });

            buttonOpenProjData.setText("Реконструкция");
            buttonOpenProjData.setFocusPainted(false);
            buttonOpenProjData.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            buttonOpenProjData.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonOpenProjDataActionPerformed(evt);
                }
            });

            buttonStart.setBackground(new java.awt.Color(0, 102, 0));
            buttonStart.setText("СТАРТ");
            buttonStart.setFocusPainted(false);
            buttonStart.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonStartActionPerformed(evt);
                }
            });

            jScrollPane2.setViewportView(jTextPane1);

            coloringTomograph.setText("Цветовой фильтр");
            coloringTomograph.setEnabled(false);
            coloringTomograph.setFocusPainted(false);
            coloringTomograph.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    coloringTomographActionPerformed(evt);
                }
            });

            colorPanelTomograph.setEnabled(false);
            colorPanelTomograph.setMaximumSize(new java.awt.Dimension(236, 25));
            colorPanelTomograph.setMinimumSize(new java.awt.Dimension(236, 23));

            colorGroupTomograph.add(color1Tomograph);
            color1Tomograph.setText("color1");
            color1Tomograph.setFocusPainted(false);
            color1Tomograph.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    color1TomographActionPerformed(evt);
                }
            });

            colorGroupTomograph.add(color2Tomograph);
            color2Tomograph.setText("color2");
            color2Tomograph.setFocusPainted(false);
            color2Tomograph.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    color2TomographActionPerformed(evt);
                }
            });

            colorGroupTomograph.add(color4Tomograph);
            color4Tomograph.setText("color4");
            color4Tomograph.setFocusable(false);
            color4Tomograph.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    color4TomographActionPerformed(evt);
                }
            });

            colorGroupTomograph.add(color3Tomograph);
            color3Tomograph.setText("color3");
            color3Tomograph.setFocusPainted(false);
            color3Tomograph.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    color3TomographActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout colorPanelTomographLayout = new javax.swing.GroupLayout(colorPanelTomograph);
            colorPanelTomograph.setLayout(colorPanelTomographLayout);
            colorPanelTomographLayout.setHorizontalGroup(
                colorPanelTomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(colorPanelTomographLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(colorPanelTomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(color1Tomograph)
                        .addComponent(color2Tomograph)
                        .addComponent(color3Tomograph)
                        .addComponent(color4Tomograph))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            colorPanelTomographLayout.setVerticalGroup(
                colorPanelTomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(colorPanelTomographLayout.createSequentialGroup()
                    .addComponent(color1Tomograph)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(color2Tomograph)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(color3Tomograph)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(color4Tomograph, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                    .addGap(100, 100, 100))
            );

            buttonDensityViewer1.setText("Анализатор плотности");
            buttonDensityViewer1.setEnabled(false);
            buttonDensityViewer1.setFocusPainted(false);
            buttonDensityViewer1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonDensityViewer1ActionPerformed(evt);
                }
            });

            buttonDensityViewerTomograph.setText("Анализатор плотности");
            buttonDensityViewerTomograph.setEnabled(false);
            buttonDensityViewerTomograph.setFocusPainted(false);
            buttonDensityViewerTomograph.setHideActionText(true);
            buttonDensityViewerTomograph.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonDensityViewerTomographActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout TomographLayout = new javax.swing.GroupLayout(Tomograph);
            Tomograph.setLayout(TomographLayout);
            TomographLayout.setHorizontalGroup(
                TomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(TomographLayout.createSequentialGroup()
                    .addGroup(TomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(TomographLayout.createSequentialGroup()
                            .addGap(28, 28, 28)
                            .addGroup(TomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(TomographLayout.createSequentialGroup()
                                    .addGroup(TomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel6))
                                    .addGap(32, 32, 32)
                                    .addGroup(TomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(outputImgSizeTomograph, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(stepSizeTomograph, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(scansTomograph, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addComponent(jLabel1)
                                .addComponent(jLabel2)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(TomographLayout.createSequentialGroup()
                                    .addGap(34, 34, 34)
                                    .addGroup(TomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(buttonSaveReconstructTomograph, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(buttonStart, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(buttonOpenProjData, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGroup(TomographLayout.createSequentialGroup()
                            .addGap(38, 38, 38)
                            .addComponent(jLabel4)))
                    .addGap(100, 100, 100)
                    .addGroup(TomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 621, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(sliderImage, javax.swing.GroupLayout.PREFERRED_SIZE, 621, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(49, 49, 49)
                    .addGroup(TomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(coloringTomograph)
                        .addComponent(buttonDensityViewerTomograph)
                        .addComponent(colorPanelTomograph, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(69, Short.MAX_VALUE))
                .addGroup(TomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(TomographLayout.createSequentialGroup()
                        .addGap(562, 562, 562)
                        .addComponent(buttonDensityViewer1)
                        .addContainerGap(563, Short.MAX_VALUE)))
            );
            TomographLayout.setVerticalGroup(
                TomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, TomographLayout.createSequentialGroup()
                    .addGroup(TomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(TomographLayout.createSequentialGroup()
                            .addGap(27, 27, 27)
                            .addComponent(buttonDensityViewerTomograph)
                            .addGap(33, 33, 33)
                            .addComponent(coloringTomograph)
                            .addGap(18, 18, 18)
                            .addComponent(colorPanelTomograph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, TomographLayout.createSequentialGroup()
                            .addGroup(TomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(TomographLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jScrollPane1))
                                .addGroup(TomographLayout.createSequentialGroup()
                                    .addGap(27, 27, 27)
                                    .addComponent(jLabel4)
                                    .addGap(18, 18, 18)
                                    .addGroup(TomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(scansTomograph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(18, 18, 18)
                                    .addGroup(TomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(stepSizeTomograph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(18, 18, 18)
                                    .addGroup(TomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(18, 18, 18)
                                    .addGroup(TomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel6)
                                        .addComponent(outputImgSizeTomograph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(18, 18, 18)
                                    .addComponent(buttonOpenProjData, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(buttonSaveReconstructTomograph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(buttonStart, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(21, 21, 21)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                    .addComponent(sliderImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(12, 12, 12))
                .addGroup(TomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(TomographLayout.createSequentialGroup()
                        .addGap(300, 300, 300)
                        .addComponent(buttonDensityViewer1)
                        .addContainerGap(301, Short.MAX_VALUE)))
            );

            colorPanelTomograph.setVisible(false);

            jTabbedPane1.addTab("Томограф", Tomograph);

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jTabbedPane1)
                    .addContainerGap())
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 651, Short.MAX_VALUE)
                    .addContainerGap())
            );

            jTabbedPane1.getAccessibleContext().setAccessibleName("Модель");

            pack();
        }// </editor-fold>//GEN-END:initComponents

    private void buttonSaveReconstructTomographActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveReconstructTomographActionPerformed
        // TODO add your handling code here:
        if (saveFileChooser.showSaveDialog(this) == saveFileChooser.APPROVE_OPTION) {
            ImageIcon icon = (ImageIcon) labelimage3.getIcon();
            BufferedImage bi = (BufferedImage) ((Image) icon.getImage());
            // BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_BYTE_ARGB);

            String name = saveFileChooser.getSelectedFile().getAbsolutePath();
            String filterImageDesc = saveFileChooser.getFileFilter().getDescription();

            saveFile(bi, name, filterImageDesc);

        }
    }//GEN-LAST:event_buttonSaveReconstructTomographActionPerformed

    private void buttonOpenProjDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOpenProjDataActionPerformed
        // TODO add your handling code here:
        dialogProjDataChooser.setVisible(true);
        try {
            buttonSaveReconstructTomograph.setEnabled(false);

            Class.forName("com.mysql.jdbc.Driver");
            while (tableProjData.getRowCount() > 0) {
                ((DefaultTableModel) tableProjData.getModel()).removeRow(0);
            }

            DefaultTableModel model = (DefaultTableModel) tableProjData.getModel();
            Properties props = new Properties();
            props.put("useUnicode", "true");
            props.put("characterEncoding", "UTF-8");

            Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/testing?"
                    + "user=root&password=ProL1ant", props);

            Statement statement = connect.createStatement();

            String SQL = "select * from project_data";
            ResultSet rs = statement.executeQuery(SQL);
            String n = "", e = "", k1 = "";
            while (rs.next()) {

                Object[] data = {rs.getString("DATE"), rs.getString("NAME"), rs.getString("DESCRIPTION")};
                model.addRow(data);
            }
        } catch (SQLException ex) {
//            Logger.getLogger(TomographPane.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(TomographPane.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_buttonOpenProjDataActionPerformed

    private void buttonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartActionPerformed
        // TODO add your handling code here:
        buttonSaveReconstructTomograph.setEnabled(false);
        labelimage3.setIcon(null);
        dialogNameAsker.setVisible(true);

    }//GEN-LAST:event_buttonStartActionPerformed

    private void coloringActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_coloringActionPerformed
        // TODO add your handling code here:
        if (coloring.isSelected()) {
            colorPanel.setVisible(true);
        } else {
            colorPanel.setVisible(false);
            ImageIcon icon2 = new ImageIcon(reconstructImage);
            labelImage2.setIcon(icon2);
        }
    }//GEN-LAST:event_coloringActionPerformed

    private void filteringModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filteringModelActionPerformed
        // TODO add your handling code here:
        if (filteringModel.isSelected()) {
            filterPanel.setVisible(true);
        } else {
            filterPanel.setVisible(false);
        }
    }//GEN-LAST:event_filteringModelActionPerformed

    private void buttonSaveSinogramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveSinogramActionPerformed
        // TODO add your handling code here:
        if (saveFileChooser.showSaveDialog(this) == saveFileChooser.APPROVE_OPTION) {
            String name = saveFileChooser.getSelectedFile().getAbsolutePath();
            String filterImageDesc = saveFileChooser.getFileFilter().getDescription();
            saveFile(sinogramImage, name, filterImageDesc);

        }
    }//GEN-LAST:event_buttonSaveSinogramActionPerformed

    private void buttonSaveReconstructActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveReconstructActionPerformed

        if (saveFileChooser.showSaveDialog(this) == saveFileChooser.APPROVE_OPTION) {
            ImageIcon icon = (ImageIcon) labelImage2.getIcon();
            BufferedImage bi = (BufferedImage) ((Image) icon.getImage());

            String name = saveFileChooser.getSelectedFile().getAbsolutePath();
            String filterImageDesc = saveFileChooser.getFileFilter().getDescription();
            saveFile(bi, name, filterImageDesc);

        }

    }//GEN-LAST:event_buttonSaveReconstructActionPerformed

    private void buttonReconstructActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonReconstructActionPerformed

        dialogProgressBar.setVisible(true);
        progressBar.setIndeterminate(true);

        Thread threadReconstruct = new Thread(new Runnable() {
            public void run() //Этот метод будет выполняться в побочном потоке
            {
                if (filteringModel.isSelected()) {

                    String filterName = "shepplogan";  // for durak

                    Enumeration<AbstractButton> allRadioButton = filterGroup.getElements();
                    while (allRadioButton.hasMoreElements()) {
                        JRadioButton temp = (JRadioButton) allRadioButton.nextElement();
                        if (temp.isSelected()) {
                            filterName = temp.getText();
                            break;
                        }
                    }
                    sinogramCreator.setReconstructParameters(Integer.parseInt(reconstructSize.getText()), true, filterName);
                } else {
                    sinogramCreator.setReconstructParameters(Integer.parseInt(reconstructSize.getText()));
                }
                reconstructImage = sinogramCreator.createReconstructedImage();
                ImageIcon icon2 = new ImageIcon(reconstructImage);
                labelImage2.setIcon(icon2);
                buttonSaveReconstruct.setEnabled(true);
                coloring.setEnabled(true);
                progressBar.setIndeterminate(false);
                dialogProgressBar.setVisible(false);
                buttonDensityViewer.setEnabled(true);
            }
        });
        threadReconstruct.start();	//Запуск потока


    }//GEN-LAST:event_buttonReconstructActionPerformed

    private void stepsizeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_stepsizeKeyTyped
        // TODO add your handling code here:
        buttonSaveReconstruct.setEnabled(false);
        buttonReconstruct.setEnabled(false);
    }//GEN-LAST:event_stepsizeKeyTyped

    private void scansModelKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_scansModelKeyTyped
        // TODO add your handling code here:
        buttonSaveReconstruct.setEnabled(false);
        buttonReconstruct.setEnabled(false);
    }//GEN-LAST:event_scansModelKeyTyped

    private void labelImage2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelImage2MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2 && labelImage2.getIcon() != null) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    ImageViewerPane viewer = new ImageViewerPane();
                    viewer.setVisible(true);
                    viewer.image.setIcon(labelImage2.getIcon());
                }
            });

        }
    }//GEN-LAST:event_labelImage2MouseClicked

    private void buttonConverseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonConverseActionPerformed

        // TODO add your handling code here:
        //        int height = imgBuf.getHeight();
        //        int width = imgBuf.getWidth();
        //
        //        BufferedImage imgNew = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        //
        //        TextArea.append("Width new "+imgNew.getWidth()+"\n"+"Height new "+imgNew.getHeight()+"\n"+"MinX new "+imgNew.getMinX()+"\n"+"Min y new "+imgNew.getMinY()+"\n");
        if (checkScanParameters()) {

            dialogProgressBar.setVisible(true);
            progressBar.setIndeterminate(true);

            Thread threadCreateSinogram = new Thread(new Runnable() {
                public void run() //Этот метод будет выполняться в побочном потоке
                {
                    sinogramCreator.setScanParameters(Integer.parseInt(scansModel.getText()), Integer.parseInt(stepsize.getText()));
//                    sinogramCreator.setImage(imgBuf);
                    sinogramImage = sinogramCreator.createSinogram();
                    ImageIcon icon2 = new ImageIcon(sinogramImage);
                    labelImage2.setIcon(icon2);
                    buttonReconstruct.setEnabled(true);
                    buttonSaveSinogram.setEnabled(true);
                    filterActionPanel.setVisible(true);
                    progressBar.setIndeterminate(false);
                    dialogProgressBar.setVisible(false);
                    buttonDensityViewer.setEnabled(false);
                }
            });
            threadCreateSinogram.start();	//Запуск потока

        } else {
            JOptionPane.showMessageDialog(this, "Введены некорректные данные", "Ошибка", 0);
        }
    }//GEN-LAST:event_buttonConverseActionPerformed

    private void labelImage1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelImage1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2 && labelImage1.getIcon() != null) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    ImageViewerPane viewer = new ImageViewerPane();
                    viewer.setVisible(true);
                    viewer.image.setIcon(labelImage1.getIcon());

                }
            });

        }
    }//GEN-LAST:event_labelImage1MouseClicked

    private void buttonOpenFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOpenFileActionPerformed

        int returnValue = openFileChooser.showOpenDialog(this);
        if (returnValue == 0) {
            File file = openFileChooser.getSelectedFile();

//            try {
                //                    BufferedImage sinoimg;

//                imgBuf = ImageIO.read(file);
//                Image gray = new BufferedImage(imgBuf.getWidth(), imgBuf.getHeight(),
//                        BufferedImage.TYPE_BYTE_GRAY);
//
//                if (imgBuf.getType() != 13) {
//                    ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
//                    ColorConvertOp op = new ColorConvertOp(cs, null);
//                    imgBuf = op.filter(imgBuf, null);
//                }
//
//                //                        File outputfile = new File("saved.png");
//                //                        ImageIO.write(windowedImage, "png", outputfile);
//                ImageIcon icon = new ImageIcon(imgBuf);
//                labelImage1.setIcon(icon);
//                displayImageDetails(imgBuf);
                buttonSaveSinogram.setEnabled(false);
                buttonSaveReconstruct.setEnabled(false);
                buttonConverse.setEnabled(true);
                buttonReconstruct.setEnabled(false);
                labelImage2.setIcon(null);
                coloring.setEnabled(false);
                coloring.setSelected(false);
                colorPanel.setVisible(false);
                buttonDensityViewer.setEnabled(false);
//            } catch (IOException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//            }
        }

    }//GEN-LAST:event_buttonOpenFileActionPerformed

    private void buttonProjDataOpenFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonProjDataOpenFileActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buttonProjDataOpenFileActionPerformed

    private void buttonProjDataCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonProjDataCancelActionPerformed
        // TODO add your handling code here:
        dialogProjDataChooser.setVisible(false);
    }//GEN-LAST:event_buttonProjDataCancelActionPerformed

    private void buttonProjDataOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonProjDataOkActionPerformed
        // TODO add your handling code here:
        if (tableProjData.getSelectedRow() > -1) {

            nameOfProjData = tableProjData.getValueAt(tableProjData.getSelectedRow(), 1).toString();
            dialogProjDataChooser.setVisible(false);
            dialogFilterChooser.setVisible(true);

        } else {
            JOptionPane.showMessageDialog(this, "Не выбран набор проеционных данных", "Ошибка", 0);
        }

    }//GEN-LAST:event_buttonProjDataOkActionPerformed

    private void sliderImageStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderImageStateChanged
        // TODO add your handling code here:

        int value = sliderImage.getValue();
        ImageIcon icon = new ImageIcon(arrayReconstructedImage.get(value));
        labelimage3.setIcon(icon);

    }//GEN-LAST:event_sliderImageStateChanged

    private void filteringTomographActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filteringTomographActionPerformed
        // TODO add your handling code here:
        if (filteringTomograph.isSelected()) {
            filterGroupTomograph.clearSelection();
            filterRampTomograph.setEnabled(false);
            filterShepploganTomograph.setEnabled(false);
            filterHammingTomograph.setEnabled(false);
            filterHannTomograph.setEnabled(false);
            filterCosineTomograph.setEnabled(false);
            filterBlackManTomograph.setEnabled(false);
        } else {
            filterRampTomograph.setEnabled(true);
            filterShepploganTomograph.setEnabled(true);
            filterHammingTomograph.setEnabled(true);
            filterHannTomograph.setEnabled(true);
            filterCosineTomograph.setEnabled(true);
            filterBlackManTomograph.setEnabled(true);
        }
    }//GEN-LAST:event_filteringTomographActionPerformed

    private void buttonOkFilterTomographActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkFilterTomographActionPerformed
        // TODO add your handling code here:
        String filterName = "";
        boolean filteringTomographBool = false;

        //-------------------Check data ReconstuctionImgSize 
        boolean correctData = false;
        try {
            Integer.parseInt(outputImgSizeTomograph.getText());
            correctData = true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Некорректные данные размера рекострукции. Введите целое числовое значение", "Ошибка", 0);
            dialogFilterChooser.setVisible(false);
        }

        if (correctData) {
            //---------------------Check enable any filter options
            boolean isFilterSelected = false;

            for (Enumeration<AbstractButton> buttons = filterGroupTomograph.getElements(); buttons.hasMoreElements();) {
                AbstractButton button = buttons.nextElement();
                if (button.isSelected()) {
                    isFilterSelected = true;
                    filterName = button.getText();
                    filteringTomographBool = true;
                }
            }

            if (isFilterSelected || filteringTomograph.isSelected()) {
                //   java.awt.Toolkit.getDefaultToolkit().beep();
                dialogFilterChooser.setVisible(false);

                dialogProgressBar.setVisible(true);
                progressBar.setIndeterminate(true);

                final String filterNameThread = filterName;
                final boolean filteringTomographBoolThread = filteringTomographBool;

                Thread threadReconstructTomograph = new Thread(new Runnable() {
                    public void run() //Этот метод будет выполняться в побочном потоке
                    {

                        arrayReconstructedImage = ImageTransformator.createArrayReconstructedImage(nameOfProjData, Integer.parseInt(outputImgSizeTomograph.getText()), filteringTomographBoolThread, filterNameThread, Integer.parseInt(scansTomograph.getText()), Integer.parseInt(stepSizeTomograph.getText()));
                        ImageIcon icon = new ImageIcon(arrayReconstructedImage.get(0));
                        labelimage3.setIcon(icon);

                        //------------Dealing with Slider
                        sliderImage.setMaximum(arrayReconstructedImage.size() - 1);
                        sliderImage.setMinorTickSpacing(1);
                        sliderImage.setPaintTicks(true);
                        buttonSaveReconstructTomograph.setEnabled(true);

                        progressBar.setIndeterminate(false);
                        dialogProgressBar.setVisible(false);

                    }
                });
                threadReconstructTomograph.start();	//Запуск потока

                coloringTomograph.setEnabled(true);
                buttonDensityViewerTomograph.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Установите параметры фильтрации", "Внимание", 1);
            }

        }
    }//GEN-LAST:event_buttonOkFilterTomographActionPerformed

    private void buttonOkSetNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkSetNameActionPerformed
        // TODO add your handling code here:
        //-------------------Check data ReconstuctionImgSize 
        boolean correctData = false;
        try {
            Integer.parseInt(scansTomograph.getText());
            Integer.parseInt(stepSizeTomograph.getText());
            if (180 % Integer.parseInt(stepSizeTomograph.getText()) == 0) {
                correctData = true;
            } else {
                JOptionPane.showMessageDialog(this, "Некорректные параметры сканирования. Введите шаг поворота - делитель 180", "Ошибка", 0);
                dialogNameAsker.setVisible(false);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Некорректные параметры сканирования. Введите целое числовое значение", "Ошибка", 0);
            dialogFilterChooser.setVisible(false);
        }

        if (correctData) {
            if (textFielsName.getText().length() > 0 && textFielsDescription.getText().length() > 0) {

                dialogProgressBar.setVisible(true);
                progressBar.setIndeterminate(true);

                Thread threadCreateProjDataSet = new Thread(new Runnable() {
                    public void run() //Этот метод будет выполняться в побочном потоке
                    {
                        DbModule.setProjDataSet(textFielsName.getText(), textFielsDescription.getText(), Integer.parseInt(scansTomograph.getText()), Integer.parseInt(stepSizeTomograph.getText()));
                        dialogNameAsker.setVisible(false);

                        progressBar.setIndeterminate(false);
                        dialogProgressBar.setVisible(false);
                    }
                });
                threadCreateProjDataSet.start();	//Запуск потока
            } else {
                JOptionPane.showMessageDialog(this, "Введите название и описание файла проекционных данных", "Внимание", 1);
            }
        }
    }//GEN-LAST:event_buttonOkSetNameActionPerformed

    private void buttonCanselSetNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCanselSetNameActionPerformed
        // TODO add your handling code here:
        dialogNameAsker.setVisible(false);
    }//GEN-LAST:event_buttonCanselSetNameActionPerformed

    private void labelimage3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelimage3MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2 && labelimage3.getIcon() != null) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    ImageViewerPane viewer = new ImageViewerPane();
                    viewer.setVisible(true);
                    viewer.image.setIcon(labelimage3.getIcon());
                }
            });

        }
    }//GEN-LAST:event_labelimage3MouseClicked

    private void buttonDensityViewerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDensityViewerActionPerformed
//        try {
        densityViewer.setVisible(true);

        scaleReconstructImage = DensityAnalizator.scaleImage(reconstructImage, 300, 300, Color.white);
        BufferedImage scaleReconstructImageLine = DensityAnalizator.generateLineonImage(scaleReconstructImage, 0);
        ImageIcon icon = new ImageIcon(scaleReconstructImageLine);
        labelImageDensityViewer.setIcon(icon);

        double[][] densitySourseArray = Utils.getDoubleArrayPixelsFromBufImg(reconstructImage);
        int initialLineSlise = densitySourseArray.length / 2;
        densitySlider.setMaximum(densitySourseArray.length - 1);
        densitySlider.setMajorTickSpacing(densitySourseArray.length / 10);
        densitySlider.setPaintLabels(true);
        densitySlider.setPaintTicks(true);

        densityGraphPane.setLayout(new java.awt.BorderLayout());
        densityGraphPane.add(DensityAnalizator.generateDensityGraph(reconstructImage, initialLineSlise), BorderLayout.CENTER);
        densityGraphPane.validate();

    }//GEN-LAST:event_buttonDensityViewerActionPerformed

    private void densitySliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_densitySliderStateChanged
        // TODO add your handling code here:

        int lineSlise = densitySlider.getValue();
        int maxSlider = densitySlider.getMaximum();

        if (jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("Томограф")) {
            int scaleLineSlise = (int) ((300 * lineSlise) / maxSlider);
            scaleReconstructImage = DensityAnalizator.scaleImage(arrayReconstructedImage.get(sliderImage.getValue()), 300, 300, Color.white);
            BufferedImage scaleReconstructImageLine = DensityAnalizator.generateLineonImage(scaleReconstructImage, scaleLineSlise);
            ImageIcon icon = new ImageIcon(scaleReconstructImageLine);
            labelImageDensityViewer.setIcon(null);
            labelImageDensityViewer.setIcon(icon);
            densityGraphPane.removeAll();
            densityGraphPane.add(DensityAnalizator.generateDensityGraph(arrayReconstructedImage.get(sliderImage.getValue()), lineSlise), BorderLayout.CENTER);
            densityGraphPane.validate();
        } else {
            int scaleLineSlise = (int) ((300 * lineSlise) / maxSlider);
            scaleReconstructImage = DensityAnalizator.scaleImage(reconstructImage, 300, 300, Color.white);
            BufferedImage scaleReconstructImageLine = DensityAnalizator.generateLineonImage(scaleReconstructImage, scaleLineSlise);
            ImageIcon icon = new ImageIcon(scaleReconstructImageLine);
            labelImageDensityViewer.setIcon(null);
            labelImageDensityViewer.setIcon(icon);
            densityGraphPane.removeAll();
            densityGraphPane.add(DensityAnalizator.generateDensityGraph(reconstructImage, lineSlise), BorderLayout.CENTER);
            densityGraphPane.validate();
        }

//        densityGraphPane.setLayout(new java.awt.BorderLayout());

    }//GEN-LAST:event_densitySliderStateChanged

    private void color3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_color3ActionPerformed
        // TODO add your handling code here:
        if (color3.isSelected()) {
            reconstructColorImage = ImageTransformator.getColorLutImage(reconstructImage, LUTFunctions.red_blue_saw_2());
            ImageIcon icon2 = new ImageIcon(reconstructColorImage);
            labelImage2.setIcon(icon2);
        }
    }//GEN-LAST:event_color3ActionPerformed

    private void color4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_color4ActionPerformed
        // TODO add your handling code here:
        if (color4.isSelected()) {
            reconstructColorImage = ImageTransformator.getColorLutImage(reconstructImage, LUTFunctions.invGray());
            ImageIcon icon2 = new ImageIcon(reconstructColorImage);
            labelImage2.setIcon(icon2);
        }
    }//GEN-LAST:event_color4ActionPerformed

    private void color2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_color2ActionPerformed
        // TODO add your handling code here:
        if (color2.isSelected()) {
            reconstructColorImage = ImageTransformator.getColorLutImage(reconstructImage, LUTFunctions.green_blue_saw_2());
            ImageIcon icon2 = new ImageIcon(reconstructColorImage);
            labelImage2.setIcon(icon2);
        }
    }//GEN-LAST:event_color2ActionPerformed

    private void color1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_color1ActionPerformed
        // TODO add your handling code here:
        if (color1.isSelected()) {
            reconstructColorImage = ImageTransformator.getColorLutImage(reconstructImage, LUTFunctions.sin_rbg());
            ImageIcon icon2 = new ImageIcon(reconstructColorImage);
            labelImage2.setIcon(icon2);
        }
    }//GEN-LAST:event_color1ActionPerformed

    private void coloringTomographActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_coloringTomographActionPerformed
        // TODO add your handling code here:
        if (coloringTomograph.isSelected()) {
            colorPanelTomograph.setVisible(true);
        } else {
            colorPanelTomograph.setVisible(false);
            colorGroupTomograph.clearSelection();
            ImageIcon icon2 = new ImageIcon(arrayReconstructedImage.get(sliderImage.getValue()));
            labelimage3.setIcon(icon2);
        }
    }//GEN-LAST:event_coloringTomographActionPerformed

    private void color1TomographActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_color1TomographActionPerformed
        // TODO add your handling code here:
        if (color1Tomograph.isSelected()) {
            reconstructColorImage = ImageTransformator.getColorLutImage(arrayReconstructedImage.get(sliderImage.getValue()), LUTFunctions.sin_rbg());
            ImageIcon icon2 = new ImageIcon(reconstructColorImage);
            labelimage3.setIcon(icon2);
        }
    }//GEN-LAST:event_color1TomographActionPerformed

    private void color2TomographActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_color2TomographActionPerformed
        // TODO add your handling code here:
        if (color2Tomograph.isSelected()) {
            reconstructColorImage = ImageTransformator.getColorLutImage(arrayReconstructedImage.get(sliderImage.getValue()), LUTFunctions.green_blue_saw_2());
            ImageIcon icon2 = new ImageIcon(reconstructColorImage);
            labelimage3.setIcon(icon2);
        }
    }//GEN-LAST:event_color2TomographActionPerformed

    private void color4TomographActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_color4TomographActionPerformed
        // TODO add your handling code here:
        if (color4Tomograph.isSelected()) {
            reconstructColorImage = ImageTransformator.getColorLutImage(arrayReconstructedImage.get(sliderImage.getValue()), LUTFunctions.invGray());
            ImageIcon icon2 = new ImageIcon(reconstructColorImage);
            labelimage3.setIcon(icon2);
        }
    }//GEN-LAST:event_color4TomographActionPerformed

    private void color3TomographActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_color3TomographActionPerformed
        // TODO add your handling code here:
        if (color3Tomograph.isSelected()) {
            reconstructColorImage = ImageTransformator.getColorLutImage(arrayReconstructedImage.get(sliderImage.getValue()), LUTFunctions.red_blue_saw_2());
            ImageIcon icon2 = new ImageIcon(reconstructColorImage);
            labelimage3.setIcon(icon2);
    }//GEN-LAST:event_color3TomographActionPerformed
    }
    private void buttonDensityViewer1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDensityViewer1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buttonDensityViewer1ActionPerformed

    private void buttonDensityViewerTomographActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDensityViewerTomographActionPerformed
        // TODO add your handling code here:
        densityViewer.setVisible(true);

        scaleReconstructImage = DensityAnalizator.scaleImage(arrayReconstructedImage.get(sliderImage.getValue()), 300, 300, Color.white);
        BufferedImage scaleReconstructImageLine = DensityAnalizator.generateLineonImage(scaleReconstructImage, 0);
        ImageIcon icon = new ImageIcon(scaleReconstructImageLine);
        labelImageDensityViewer.setIcon(icon);

        double[][] densitySourseArray = Utils.getDoubleArrayPixelsFromBufImg(arrayReconstructedImage.get(sliderImage.getValue()));
        int initialLineSlise = densitySourseArray.length / 2;
        densitySlider.setMaximum(densitySourseArray.length - 1);
        densitySlider.setMajorTickSpacing(densitySourseArray.length / 10);
        densitySlider.setPaintLabels(true);
        densitySlider.setPaintTicks(true);

        densityGraphPane.setLayout(new java.awt.BorderLayout());
        densityGraphPane.add(DensityAnalizator.generateDensityGraph(arrayReconstructedImage.get(sliderImage.getValue()), initialLineSlise), BorderLayout.CENTER);
        densityGraphPane.validate();
    }//GEN-LAST:event_buttonDensityViewerTomographActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane Image2;
    private javax.swing.JPanel Model;
    private javax.swing.JPanel ParamModellingPane;
    private javax.swing.JPanel Tomograph;
    private javax.swing.JButton buttonCanselSetName;
    private javax.swing.JButton buttonConverse;
    private javax.swing.JButton buttonDensityViewer;
    private javax.swing.JButton buttonDensityViewer1;
    private javax.swing.JButton buttonDensityViewerTomograph;
    private javax.swing.JButton buttonOkFilterTomograph;
    private javax.swing.JButton buttonOkSetName;
    private javax.swing.JButton buttonOpenFile;
    private javax.swing.JButton buttonOpenProjData;
    private javax.swing.JButton buttonProjDataCancel;
    private javax.swing.JButton buttonProjDataOk;
    private javax.swing.JButton buttonProjDataOpenFile;
    private javax.swing.JButton buttonReconstruct;
    private javax.swing.JButton buttonSaveReconstruct;
    private javax.swing.JButton buttonSaveReconstructTomograph;
    private javax.swing.JButton buttonSaveSinogram;
    private javax.swing.JButton buttonStart;
    private javax.swing.JRadioButton color1;
    private javax.swing.JRadioButton color1Tomograph;
    private javax.swing.JRadioButton color2;
    private javax.swing.JRadioButton color2Tomograph;
    private javax.swing.JRadioButton color3;
    private javax.swing.JRadioButton color3Tomograph;
    private javax.swing.JRadioButton color4;
    private javax.swing.JRadioButton color4Tomograph;
    private javax.swing.ButtonGroup colorGroup;
    private javax.swing.ButtonGroup colorGroupTomograph;
    private javax.swing.JPanel colorPanel;
    private javax.swing.JPanel colorPanelTomograph;
    private javax.swing.JCheckBox coloring;
    private javax.swing.JCheckBox coloringTomograph;
    private javax.swing.JPanel densityGraphPane;
    private javax.swing.JSlider densitySlider;
    private javax.swing.JDialog densityViewer;
    private javax.swing.JDialog dialogFilterChooser;
    private javax.swing.JDialog dialogNameAsker;
    private javax.swing.JDialog dialogProgressBar;
    private javax.swing.JDialog dialogProjDataChooser;
    private javax.swing.JPanel filterActionPanel;
    private javax.swing.JRadioButton filterBlackManTomograph;
    private javax.swing.JRadioButton filterBlackman;
    private javax.swing.JRadioButton filterCosine;
    private javax.swing.JRadioButton filterCosineTomograph;
    private javax.swing.ButtonGroup filterGroup;
    private javax.swing.ButtonGroup filterGroupTomograph;
    private javax.swing.JRadioButton filterHamming;
    private javax.swing.JRadioButton filterHammingTomograph;
    private javax.swing.JRadioButton filterHann;
    private javax.swing.JRadioButton filterHannTomograph;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JRadioButton filterRamp;
    private javax.swing.JRadioButton filterRampTomograph;
    private javax.swing.JRadioButton filterShepplogan;
    private javax.swing.JRadioButton filterShepploganTomograph;
    private javax.swing.JCheckBox filteringModel;
    private javax.swing.JCheckBox filteringTomograph;
    private javax.swing.JScrollPane image1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JLabel labelDetectors;
    private javax.swing.JLabel labelImage1;
    private javax.swing.JLabel labelImage2;
    private javax.swing.JLabel labelImageDensityViewer;
    private javax.swing.JLabel labelReconstructSize;
    private javax.swing.JLabel labelStepsize;
    private javax.swing.JLabel labelimage3;
    private javax.swing.JList modelList;
    private javax.swing.JPanel modelPanel;
    private javax.swing.JFileChooser openFileChooser;
    private javax.swing.JTextField outputImgSizeTomograph;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextField reconstructSize;
    private javax.swing.JFileChooser saveFileChooser;
    private javax.swing.JTextField scansModel;
    private javax.swing.JTextField scansTomograph;
    private javax.swing.JSlider sliderImage;
    private javax.swing.JTextField stepSizeTomograph;
    private javax.swing.JTextField stepsize;
    private javax.swing.JTable tableProjData;
    private javax.swing.JTextField textFielsDescription;
    private javax.swing.JTextField textFielsName;
    // End of variables declaration//GEN-END:variables

    class ImageFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            String name = f.getName();
            if (name.matches(".*((.jpg)|(.gif)|(.png)|(.bmp))")) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String getDescription() {
            return "Image files (*.jpg, *.gif, *.png, *.bmp)";
        }
    }

    class FileNameExtensionFilter extends javax.swing.filechooser.FileFilter {

        private String extenName;
        private String exten;

        FileNameExtensionFilter(String extenName, String exten) {
            this.extenName = extenName;
            this.exten = exten;
        }

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            String name = f.getName();
            if (name.matches(".*(" + exten + ")")) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String getDescription() {
            return extenName;
        }
    }

    public boolean checkScanParameters() {
        boolean checkflag = false;

        try {

            Integer.parseInt(scansModel.getText());
            Integer.parseInt(stepsize.getText());
        } catch (NumberFormatException e) {
            return checkflag;
        }
        checkflag = true;
        return checkflag;
    }

    public void saveFile(BufferedImage image, String name, String filterImageDesc) {

        String format = "";
        if (filterImageDesc.equals("JPEG File")) {
            String ext = ".jpeg";
            name = name + ext;
            format = "jpeg";
        } else if (filterImageDesc.equals("PNG File")) {
            String ext = ".png";
            name = name + ext;
            format = "PNG";
        } else if (filterImageDesc.equals("BMP File")) {
            String ext = ".bmp";
            name = name + ext;
            format = "BMP";
        } else if (filterImageDesc.equals("All Files")) {
            format = "";
        }
        File file = new File(name);
        try {
            ImageIO.write(image, format, file);
        } catch (IOException ex) {
//            Logger.getLogger(TomographPaneetName()).log(Level.SEVERE, null, ex);
        }
    }

    public void displayImageDetails(BufferedImage img) {
        jLabel1.setText("Размер изображения " + img.getWidth() + " * " + img.getHeight());
    }
}
