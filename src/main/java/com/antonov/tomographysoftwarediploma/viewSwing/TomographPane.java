package com.antonov.tomographysoftwarediploma.viewSwing;

import com.antonov.tomographysoftwarediploma.dblayer.DbModule;
import com.antonov.tomographysoftwarediploma.impl.imageprocessing.ImageTransformator;
import com.antonov.tomographysoftwarediploma.impl.imageprocessing.LUTFunctions;
import com.antonov.tomographysoftwarediploma.impl.imageprocessing.Utils;
import com.antonov.tomographysoftwarediploma.controllers.HardwareModuleController;
import com.antonov.tomographysoftwarediploma.impl.ITomographView;
import com.antonov.tomographysoftwarediploma.controllers.ModellingModuleController;
import com.antonov.tomographysoftwarediploma.impl.imageprocessing.ColorFunctionNamesEnum;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
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
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.MouseInputListener;
import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TomographPane extends javax.swing.JFrame implements ITomographView {

    private static final Logger logger = LoggerFactory.getLogger(TomographPane.class);
    ModellingModuleController modellingModuleController;
    HardwareModuleController hardwareModuleController;
    private final ResourceBundle bundle = ResourceBundle.getBundle(
            "bundle_Rus");

    public static final List<String> modelNames = new ArrayList<>(); // For modelling images names

    private BufferedImage reconstructImage;
    private BufferedImage reconstructColorImage;
    private BufferedImage scaleReconstructImage; // для DensityViewer
    private List<BufferedImage> arrayReconstructedImage = new ArrayList<>();

    ImageTransformator sinogramCreator = new ImageTransformator();
    String nameOfProjData;

    public TomographPane() {

        initComponents();
        initClosingOperations();
        initButtons();
        initToolBars();
        initTextFields();
        initComboBoxes();
        initImageAreas();
    }

    @Override
    public void initClosingOperations() {

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int i = JOptionPane.showConfirmDialog(null, bundle.getString("CONFIRMATION_EXIT"), "", JOptionPane.YES_NO_OPTION);
                if (i == 0) {
                    modellingModuleController.exitApplication();
                }
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

    @Override
    public void fillModelNames(Map<String, BufferedImage> imageSamplesMapWithNames) {

        if (!imageSamplesMapWithNames.isEmpty()) {

            for (String name : imageSamplesMapWithNames.keySet()) {
                modelNames.add(name);
            }
        } else {
            logger.warn("Map of modelling images is empty ");
        }
    }

    @Override
    public void initModelList() {

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
    public void clearResultModelling() {

        labelImage2.setIcon(null);
    }

    public void clearResultReconstruction() {
        labelReconstruction.setIcon(null);
    }

    @Override
    public void disableModellingControls() {

        buttonSaveSinogram.setEnabled(false);
        buttonSaveReconstructModelling.setEnabled(false);
        buttonSinogram.setEnabled(true);

        disableReconControls();
        disableAfterReconstrucionControls();
    }

    public void disableReconControls() {

        buttonReconstruct.setEnabled(false);
    }

    @Override
    public void enableReconControls() {

        buttonReconstruct.setEnabled(true);
        buttonSaveSinogram.setEnabled(true);
    }

    public void enableAfterReconstructControls() {
        buttonDensityViewer.setEnabled(true);
        cbColoringModel.setEnabled(true);
        buttonSaveReconstructModelling.setEnabled(true);
    }

    public void disableAfterReconstrucionControls() {

        buttonDensityViewer.setEnabled(false);
        cbColoringModel.setEnabled(false);
        buttonSaveReconstructModelling.setEnabled(false);
    }

    @Override
    public void setCurrentModellingImage(BufferedImage image) {

        ImageIcon icon = new ImageIcon(image);
        labelImage1.setIcon(icon);
    }

    @Override
    public void setSinogramImage(BufferedImage image) {

        ImageIcon icon = new ImageIcon(image);
        labelImage2.setIcon(icon);

    }

    private void setReconstructionImage(BufferedImage image) {

        ImageIcon icon = new ImageIcon(image);
        labelReconstruction.setIcon(icon);
    }

    @Override
    public void initListeners() {
        PropertyChangeListener errorListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case "INTERNAL_ERROR":
                        showInternalErrorMessage((String) evt.getNewValue());
                        break;
                    case "PARAMETER_VALUE_WARNING":
                        showWarningMessage((String) evt.getNewValue());
                        break;
                    case "ERROR":
                        showErrorMessage((String) evt.getNewValue());
                        break;
                }
            }

        };

        PropertyChangeListener imagesListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case "currentImageModelling":
                        setCurrentModellingImage((BufferedImage) evt.getNewValue());
                        break;
                    case "clearResultModelling":
                        clearResultModelling();
                        break;
                    case "setSinogramImage":
                        setSinogramImage((BufferedImage) evt.getNewValue());
                        break;
                    case "setReconstructionOfSinogramImage":
                        setReconstructionImage((BufferedImage) evt.getNewValue());
                        break;
                    case "clearResultReconstruction":
                        clearResultReconstruction();
                        break;
                }
            }
        };

        PropertyChangeListener paramsModellingListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case "scans":
                        edScansModel.setText(((Integer) evt.getNewValue()).toString());
                        break;
                    case "stepsize":
                        edStepsizeModel.setText(((Integer) evt.getNewValue()).toString());
                        break;
                    case "regimeInterpolationModel":
                        setCbInterpolation((Set) evt.getNewValue());
                        break;
                    case "regimeSinogramInterpolation":
                        cbSinogramInterpolation.setSelectedItem(evt.getNewValue());
                        break;
                    case "setModellingImages":
                        setModellingImages((Map<String, BufferedImage>) evt.getNewValue());
                        break;
                    case "sizeReconstruction":
                        edSizeReconstruction.setText(((Integer) evt.getNewValue()).toString());
                        break;
                    case "filterSet":
                        setCbFilteringModel((Set) evt.getNewValue());
                        break;
                    case "filterModel":
                        cbFilteringModel.setSelectedItem(evt.getNewValue());
                        break;
                    case "regimeReconstructionInterpolation":
                        cbReconstructionInterpolation.setSelectedItem(evt.getNewValue());
                        break;
                    case "colorModelModelling":
                        setCbColoring();
                        break;
                    case "colorImageModelling":
                        setReconstructionImage((BufferedImage) evt.getNewValue());
                        break;
                    case "currentColorModelModelling":
                        cbColoringModel.setSelectedItem(evt.getNewValue());
                        break;
                }
            }

        };

        PropertyChangeListener otherStuffListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case "disableModellingControls":
                        disableModellingControls();
                        break;
                    case "enableReconControls":
                        enableReconControls();
                        break;
                    case "disableReconControls":
                        disableReconControls();
                        break;
                    case "enableAfterReconstructControls":
                        enableAfterReconstructControls();
                        break;
                    case "disableAfterReconstrucionControls":
                        disableAfterReconstrucionControls();
                        break;
                    case "startSinogramm":
                        startCalculating();
                        break;
                    case "stopSinogramm":
                        stopCalculating();
                        break;
                    case "startReconstructionSinogram":
                        startCalculating();
                        break;
                    case "stopReconstructionSinogram":
                        stopCalculating();
                        break;
                    case "showSinogram":
                        startViewer((BufferedImage) evt.getNewValue());
                        break;
                    case "showReconstructionModelling":
                        startViewer((BufferedImage) evt.getNewValue());
                        break;
                }
            }
        };

        modellingModuleController.addPropertyChangeListenerToModel(paramsModellingListener);
        modellingModuleController.addPropertyChangeListenerToModel(imagesListener);
        modellingModuleController.addPropertyChangeListenerToModel(otherStuffListener);
        modellingModuleController.addPropertyChangeListenerToModel(errorListener);

        modellingModuleController.addPropertyChangeListener(errorListener);
        modellingModuleController.addPropertyChangeListener(paramsModellingListener);
    }

    @Override
    public void startCalculating() {

        dialogProgressBar.setVisible(true);
        progressBar.setIndeterminate(true);

    }

    @Override
    public void stopCalculating() {
        progressBar.setIndeterminate(false);
        dialogProgressBar.setVisible(false);
    }

    private void initToolBars() {
        buttonOpenFile.setToolTipText(bundle.getString("TIP_OPEN_MODELLING_IMAGE"));
        buttonOpenFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (openFileChooser.showOpenDialog(TomographPane.this) == 0) {
                    File file = openFileChooser.getSelectedFile();
                    modellingModuleController.getAndSetFileModellingImage(file);
                }
            }
        });

        buttonSaveSinogram.setToolTipText(bundle.getString("TIP_SAVE_SINOGRAM_IMAGE"));
        buttonSaveSinogram.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (saveFileChooser.showSaveDialog(TomographPane.this) == JFileChooser.APPROVE_OPTION) {
                    File file = saveFileChooser.getSelectedFile();
                    String desc = saveFileChooser.getFileFilter().getDescription();
                    modellingModuleController.saveModellingSinogram(file, desc);
                }
            }
        });

        buttonSaveReconstructModelling.setToolTipText(bundle.getString("TIP_SAVE_RECONSTRUCT_IMAGE"));
        buttonSaveReconstructModelling.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (saveFileChooser.showSaveDialog(TomographPane.this) == JFileChooser.APPROVE_OPTION) {
                    File file = saveFileChooser.getSelectedFile();
                    String desc = saveFileChooser.getFileFilter().getDescription();
                    modellingModuleController.saveModellingReconstruction(file, desc);
                }
            }
        });
    }

    private void initButtons() {

        buttonSinogram.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        modellingModuleController.createSinogram();
                    }
                }).start();
            }
        });

        buttonReconstruct.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        modellingModuleController.reconstructModellingSinogram();
                    }
                }).start();
            }
        });
    }

    private void initTextFields() {

        edScansModel.addFocusListener(new FocusLostListener() {

            @Override
            public void focusLost(FocusEvent e) {
                modellingModuleController.setScans(edScansModel.getText(), TomographPane.this);
            }
        });

        edStepsizeModel.addFocusListener(new FocusLostListener() {

            @Override
            public void focusLost(FocusEvent e) {
                modellingModuleController.setStepSize(edStepsizeModel.getText());
            }
        });

        edSizeReconstruction.addFocusListener(new FocusLostListener() {

            @Override
            public void focusLost(FocusEvent e) {
                modellingModuleController.setSizeReconstruction(edSizeReconstruction.getText());
            }
        });
    }

    private void setCbInterpolation(Set setInterpolation) {

        cbSinogramInterpolation.setModel(new DefaultComboBoxModel(setInterpolation.toArray()));
        cbReconstructionInterpolation.setModel(new DefaultComboBoxModel(setInterpolation.toArray()));
    }

    private void setCbFilteringModel(Set setFilter) {
        cbFilteringModel.setModel(new DefaultComboBoxModel(setFilter.toArray()));
    }

    private void setCbColoring() {

        cbColoringModel.setModel(new DefaultComboBoxModel(ColorFunctionNamesEnum.values()));
    }

    @Override
    public void showInternalErrorMessage(String messageError) {

        stopCalculating();
        JOptionPane.showMessageDialog(this, bundle.getString("INTERNAL_ERROR") + ". " + messageError, bundle.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showWarningMessage(String messageWarning) {

        JOptionPane.showMessageDialog(this, messageWarning + ". ", bundle.getString("WARNING"), JOptionPane.WARNING_MESSAGE);
    }

    private void showErrorMessage(String messageError) {

        stopCalculating();
        JOptionPane.showMessageDialog(this, messageError, bundle.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
    }

    private void initComboBoxes() {

        cbSinogramInterpolation.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                modellingModuleController.setSinogramInterpolation(cbSinogramInterpolation.getSelectedItem());
            }
        });

        cbReconstructionInterpolation.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                modellingModuleController.setReconstructionInterpolation(cbReconstructionInterpolation.getSelectedItem());
            }
        });

        cbFilteringModel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                modellingModuleController.setFilterModel(cbFilteringModel.getSelectedItem());
            }
        });

        cbColoringModel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                modellingModuleController.setColoringName((ColorFunctionNamesEnum) cbColoringModel.getSelectedItem());
            }
        });
    }

    private void initImageAreas() {

        labelImage2.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    modellingModuleController.showSinogram();
                }
            }
        });

        labelReconstruction.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    modellingModuleController.showReconstructionModelling();
                }
            }
        });
    }

    public void startViewer(final BufferedImage image) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ImageViewerPane viewer = new ImageViewerPane(image);
                viewer.setVisible(true);
            }
        });
    }

    @Override
    public boolean isSinogramImageEmpty() {
        return labelImage2.getIcon() == null;
    }

    @Override
    public boolean isReconstructionModellingEmpty() {
        return labelReconstruction.getIcon() == null;
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        dialogProgressBar = new javax.swing.JDialog();
        jLabel11 = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        openFileChooser = new javax.swing.JFileChooser();
        saveFileChooser = new javax.swing.JFileChooser();
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
        paneControl = new javax.swing.JPanel();
        modelPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        modelList = new javax.swing.JList();
        paneParamModelling = new javax.swing.JPanel();
        labelDetectors = new javax.swing.JLabel();
        edScansModel = new javax.swing.JTextField();
        labelStepsize = new javax.swing.JLabel();
        edStepsizeModel = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        cbSinogramInterpolation = new javax.swing.JComboBox();
        buttonSinogram = new javax.swing.JButton();
        buttonDensityViewer = new javax.swing.JButton();
        buttonReconstruct = new javax.swing.JButton();
        paneParamReconstruct = new javax.swing.JPanel();
        labelReconstructSize = new javax.swing.JLabel();
        edSizeReconstruction = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        cbFilteringModel = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        cbReconstructionInterpolation = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        cbColoringModel = new javax.swing.JComboBox();
        jSplitPane1 = new javax.swing.JSplitPane();
        paneSourceImage = new javax.swing.JPanel();
        toolbarSourceImage = new javax.swing.JToolBar();
        buttonOpenFile = new javax.swing.JButton();
        image1 = new javax.swing.JScrollPane();
        labelImage1 = new javax.swing.JLabel();
        paneResultModelling = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        paneSinogram = new javax.swing.JPanel();
        Image2 = new javax.swing.JScrollPane();
        labelImage2 = new javax.swing.JLabel();
        toolbarModellingImage = new javax.swing.JToolBar();
        buttonSaveSinogram = new javax.swing.JButton();
        buttonSaveReconstructModelling = new javax.swing.JButton();
        paneReconstruct = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        labelReconstruction = new javax.swing.JLabel();
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
        labelImage3 = new javax.swing.JLabel();
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

            jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
            jTabbedPane1.setToolTipText("");
            jTabbedPane1.setDoubleBuffered(true);

            Model.setLayout(new java.awt.GridBagLayout());

            paneControl.setLayout(new java.awt.GridBagLayout());

            java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bundle_Rus"); // NOI18N
            modelPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("LIST_MODEL_TITLE"))); // NOI18N
            modelPanel.setLayout(new java.awt.BorderLayout());

            jScrollPane4.setMinimumSize(new java.awt.Dimension(100, 70));
            jScrollPane4.setPreferredSize(new java.awt.Dimension(100, 70));

            modelList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            modelList.setFocusable(false);
            modelList.setMaximumSize(new java.awt.Dimension(50, 0));
            modelList.setPreferredSize(new java.awt.Dimension(50, 0));
            jScrollPane4.setViewportView(modelList);

            modelPanel.add(jScrollPane4, java.awt.BorderLayout.CENTER);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
            paneControl.add(modelPanel, gridBagConstraints);

            paneParamModelling.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("PANE_PARAM_MODELLING"))); // NOI18N
            paneParamModelling.setLayout(new java.awt.GridBagLayout());

            labelDetectors.setText(bundle.getString("LABEL_SCANS")); // NOI18N
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            paneParamModelling.add(labelDetectors, gridBagConstraints);

            edScansModel.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            edScansModel.setToolTipText("");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.ipadx = 30;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
            paneParamModelling.add(edScansModel, gridBagConstraints);

            labelStepsize.setText(bundle.getString("LABEL_STEPSIZE")); // NOI18N
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            paneParamModelling.add(labelStepsize, gridBagConstraints);

            edStepsizeModel.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            edStepsizeModel.setToolTipText("");
            edStepsizeModel.setAutoscrolls(false);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
            paneParamModelling.add(edStepsizeModel, gridBagConstraints);

            jLabel13.setText(bundle.getString("LABEL_TYPE_INTERPOLATION")); // NOI18N
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            paneParamModelling.add(jLabel13, gridBagConstraints);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
            paneParamModelling.add(cbSinogramInterpolation, gridBagConstraints);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
            paneControl.add(paneParamModelling, gridBagConstraints);

            buttonSinogram.setText("Синограмма");
            buttonSinogram.setDefaultCapable(false);
            buttonSinogram.setEnabled(false);
            buttonSinogram.setFocusPainted(false);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            paneControl.add(buttonSinogram, gridBagConstraints);

            buttonDensityViewer.setText(bundle.getString("LABEL_DENSANALYSE")); // NOI18N
            buttonDensityViewer.setEnabled(false);
            buttonDensityViewer.setFocusPainted(false);
            buttonDensityViewer.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonDensityViewerActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 4;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            paneControl.add(buttonDensityViewer, gridBagConstraints);

            buttonReconstruct.setText("Реконструкция");
            buttonReconstruct.setDefaultCapable(false);
            buttonReconstruct.setEnabled(false);
            buttonReconstruct.setFocusPainted(false);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            paneControl.add(buttonReconstruct, gridBagConstraints);

            paneParamReconstruct.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("PANE_PARAM_RECON"))); // NOI18N
            paneParamReconstruct.setLayout(new java.awt.GridBagLayout());

            labelReconstructSize.setText(bundle.getString("LABEL_SIZE_RECON")); // NOI18N
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
            paneParamReconstruct.add(labelReconstructSize, gridBagConstraints);

            edSizeReconstruction.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            edSizeReconstruction.setToolTipText("");
            edSizeReconstruction.setAutoscrolls(false);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.ipadx = 30;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
            paneParamReconstruct.add(edSizeReconstruction, gridBagConstraints);

            jLabel12.setText(bundle.getString("LABEL_FILTERING")); // NOI18N
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            paneParamReconstruct.add(jLabel12, gridBagConstraints);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
            paneParamReconstruct.add(cbFilteringModel, gridBagConstraints);

            jLabel14.setText(bundle.getString("LABEL_TYPE_INTERPOLATION")); // NOI18N
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            paneParamReconstruct.add(jLabel14, gridBagConstraints);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 4;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
            paneParamReconstruct.add(cbReconstructionInterpolation, gridBagConstraints);

            jLabel15.setText(bundle.getString("LABAL_COLORING")); // NOI18N
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 5;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
            paneParamReconstruct.add(jLabel15, gridBagConstraints);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 6;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
            paneParamReconstruct.add(cbColoringModel, gridBagConstraints);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
            paneControl.add(paneParamReconstruct, gridBagConstraints);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridheight = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
            Model.add(paneControl, gridBagConstraints);

            jSplitPane1.setResizeWeight(0.5);
            jSplitPane1.setToolTipText("");

            paneSourceImage.setLayout(new java.awt.GridBagLayout());

            toolbarSourceImage.setRollover(true);

            buttonOpenFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open.png"))); // NOI18N
            buttonOpenFile.setFocusPainted(false);
            buttonOpenFile.setFocusable(false);
            buttonOpenFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            buttonOpenFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            toolbarSourceImage.add(buttonOpenFile);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
            paneSourceImage.add(toolbarSourceImage, gridBagConstraints);

            labelImage1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            labelImage1.setAutoscrolls(true);
            labelImage1.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    labelImage1MouseClicked(evt);
                }
            });
            image1.setViewportView(labelImage1);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
            paneSourceImage.add(image1, gridBagConstraints);

            jSplitPane1.setLeftComponent(paneSourceImage);

            paneResultModelling.setLayout(new java.awt.GridBagLayout());

            jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
            jSplitPane2.setResizeWeight(0.34);
            jSplitPane2.setToolTipText("");

            paneSinogram.setLayout(new java.awt.GridBagLayout());

            labelImage2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            labelImage2.setAutoscrolls(true);
            Image2.setViewportView(labelImage2);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
            paneSinogram.add(Image2, gridBagConstraints);

            toolbarModellingImage.setRollover(true);

            buttonSaveSinogram.setIcon(new javax.swing.ImageIcon(getClass().getResource("/save.png"))); // NOI18N
            buttonSaveSinogram.setActionCommand("Сохранить<br> реконструкцию");
            buttonSaveSinogram.setEnabled(false);
            buttonSaveSinogram.setFocusPainted(false);
            buttonSaveSinogram.setFocusable(false);
            buttonSaveSinogram.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            buttonSaveSinogram.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            toolbarModellingImage.add(buttonSaveSinogram);

            buttonSaveReconstructModelling.setIcon(new javax.swing.ImageIcon(getClass().getResource("/save.png"))); // NOI18N
            buttonSaveReconstructModelling.setActionCommand("Сохранить<br> реконструкцию");
            buttonSaveReconstructModelling.setEnabled(false);
            buttonSaveReconstructModelling.setFocusPainted(false);
            buttonSaveReconstructModelling.setFocusable(false);
            buttonSaveReconstructModelling.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            buttonSaveReconstructModelling.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            toolbarModellingImage.add(buttonSaveReconstructModelling);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
            paneSinogram.add(toolbarModellingImage, gridBagConstraints);

            jSplitPane2.setTopComponent(paneSinogram);

            paneReconstruct.setLayout(new java.awt.GridBagLayout());

            labelReconstruction.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jScrollPane5.setViewportView(labelReconstruction);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
            paneReconstruct.add(jScrollPane5, gridBagConstraints);

            jSplitPane2.setBottomComponent(paneReconstruct);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
            paneResultModelling.add(jSplitPane2, gridBagConstraints);

            jSplitPane1.setRightComponent(paneResultModelling);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
            Model.add(jSplitPane1, gridBagConstraints);

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

            labelImage3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            labelImage3.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    labelImage3MouseClicked(evt);
                }
            });
            jScrollPane1.setViewportView(labelImage3);

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
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(TomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(TomographLayout.createSequentialGroup()
                        .addGap(562, 562, 562)
                        .addComponent(buttonDensityViewer1)
                        .addContainerGap(504, Short.MAX_VALUE)))
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
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                    .addComponent(sliderImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(12, 12, 12))
                .addGroup(TomographLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(TomographLayout.createSequentialGroup()
                        .addGap(300, 300, 300)
                        .addComponent(buttonDensityViewer1)
                        .addContainerGap(292, Short.MAX_VALUE)))
            );

            colorPanelTomograph.setVisible(false);

            jTabbedPane1.addTab("Томограф", Tomograph);

            getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);
            jTabbedPane1.getAccessibleContext().setAccessibleName("Модель");

            pack();
        }// </editor-fold>//GEN-END:initComponents

    private void buttonSaveReconstructTomographActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveReconstructTomographActionPerformed
        // TODO add your handling code here:
//        if (saveFileChooser.showSaveDialog(this) == saveFileChooser.APPROVE_OPTION) {
//            ImageIcon icon = (ImageIcon) labelimage3.getIcon();
//            BufferedImage bi = (BufferedImage) ((Image) icon.getImage());
//            // BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_BYTE_ARGB);
//
//            String name = saveFileChooser.getSelectedFile().getAbsolutePath();
//            String filterImageDesc = saveFileChooser.getFileFilter().getDescription();
//
//            saveFile(bi, name, filterImageDesc);
//
//        }
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
        labelImage3.setIcon(null);
        dialogNameAsker.setVisible(true);

    }//GEN-LAST:event_buttonStartActionPerformed

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
        labelImage3.setIcon(icon);

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
                        labelImage3.setIcon(icon);

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

    private void labelImage3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelImage3MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2 && labelImage3.getIcon() != null) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    ImageViewerPane viewer = new ImageViewerPane();
                    viewer.setVisible(true);
                    viewer.image.setIcon(labelImage3.getIcon());
                }
            });

        }
    }//GEN-LAST:event_labelImage3MouseClicked

    private void buttonDensityViewerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDensityViewerActionPerformed
//        try {
        densityViewer.setVisible(true);

        scaleReconstructImage = DensityAnalizator.scaleImage(reconstructImage, 300, 300, Color.white);
        BufferedImage scaleReconstructImageLine = DensityAnalizator.generateLineonImage(scaleReconstructImage, 0);
        ImageIcon icon = new ImageIcon(scaleReconstructImageLine);
        labelImageDensityViewer.setIcon(icon);

        double[][] densitySourseArray = Utils.getDoubleRevertedArrayPixelsFromBufImg(reconstructImage);
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

    private void coloringTomographActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_coloringTomographActionPerformed
        // TODO add your handling code here:
        if (coloringTomograph.isSelected()) {
            colorPanelTomograph.setVisible(true);
        } else {
            colorPanelTomograph.setVisible(false);
            colorGroupTomograph.clearSelection();
            ImageIcon icon2 = new ImageIcon(arrayReconstructedImage.get(sliderImage.getValue()));
            labelImage3.setIcon(icon2);
        }
    }//GEN-LAST:event_coloringTomographActionPerformed

    private void color1TomographActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_color1TomographActionPerformed
        // TODO add your handling code here:
        if (color1Tomograph.isSelected()) {
            reconstructColorImage = ImageTransformator.getColorLutImage(arrayReconstructedImage.get(sliderImage.getValue()), LUTFunctions.sin_rbg());
            ImageIcon icon2 = new ImageIcon(reconstructColorImage);
            labelImage3.setIcon(icon2);
        }
    }//GEN-LAST:event_color1TomographActionPerformed

    private void color2TomographActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_color2TomographActionPerformed
        // TODO add your handling code here:
        if (color2Tomograph.isSelected()) {
            reconstructColorImage = ImageTransformator.getColorLutImage(arrayReconstructedImage.get(sliderImage.getValue()), LUTFunctions.green_blue_saw_2());
            ImageIcon icon2 = new ImageIcon(reconstructColorImage);
            labelImage3.setIcon(icon2);
        }
    }//GEN-LAST:event_color2TomographActionPerformed

    private void color4TomographActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_color4TomographActionPerformed
        // TODO add your handling code here:
        if (color4Tomograph.isSelected()) {
            reconstructColorImage = ImageTransformator.getColorLutImage(arrayReconstructedImage.get(sliderImage.getValue()), LUTFunctions.invGray());
            ImageIcon icon2 = new ImageIcon(reconstructColorImage);
            labelImage3.setIcon(icon2);
        }
    }//GEN-LAST:event_color4TomographActionPerformed

    private void color3TomographActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_color3TomographActionPerformed
        // TODO add your handling code here:
        if (color3Tomograph.isSelected()) {
            reconstructColorImage = ImageTransformator.getColorLutImage(arrayReconstructedImage.get(sliderImage.getValue()), LUTFunctions.red_blue_saw_2());
            ImageIcon icon2 = new ImageIcon(reconstructColorImage);
            labelImage3.setIcon(icon2);
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

        double[][] densitySourseArray = Utils.getDoubleRevertedArrayPixelsFromBufImg(arrayReconstructedImage.get(sliderImage.getValue()));
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
    private javax.swing.JPanel Tomograph;
    private javax.swing.JButton buttonCanselSetName;
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
    private javax.swing.JButton buttonSaveReconstructModelling;
    private javax.swing.JButton buttonSaveReconstructTomograph;
    private javax.swing.JButton buttonSaveSinogram;
    private javax.swing.JButton buttonSinogram;
    private javax.swing.JButton buttonStart;
    private javax.swing.JComboBox cbColoringModel;
    private javax.swing.JComboBox cbFilteringModel;
    private javax.swing.JComboBox cbReconstructionInterpolation;
    private javax.swing.JComboBox cbSinogramInterpolation;
    private javax.swing.JRadioButton color1Tomograph;
    private javax.swing.JRadioButton color2Tomograph;
    private javax.swing.JRadioButton color3Tomograph;
    private javax.swing.JRadioButton color4Tomograph;
    private javax.swing.ButtonGroup colorGroupTomograph;
    private javax.swing.JPanel colorPanelTomograph;
    private javax.swing.JCheckBox coloringTomograph;
    private javax.swing.JPanel densityGraphPane;
    private javax.swing.JSlider densitySlider;
    private javax.swing.JDialog densityViewer;
    private javax.swing.JDialog dialogFilterChooser;
    private javax.swing.JDialog dialogNameAsker;
    private javax.swing.JDialog dialogProgressBar;
    private javax.swing.JDialog dialogProjDataChooser;
    private javax.swing.JTextField edScansModel;
    private javax.swing.JTextField edSizeReconstruction;
    private javax.swing.JTextField edStepsizeModel;
    private javax.swing.JRadioButton filterBlackManTomograph;
    private javax.swing.JRadioButton filterCosineTomograph;
    private javax.swing.ButtonGroup filterGroupTomograph;
    private javax.swing.JRadioButton filterHammingTomograph;
    private javax.swing.JRadioButton filterHannTomograph;
    private javax.swing.JRadioButton filterRampTomograph;
    private javax.swing.JRadioButton filterShepploganTomograph;
    private javax.swing.JCheckBox filteringTomograph;
    private javax.swing.JScrollPane image1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
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
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JLabel labelDetectors;
    private javax.swing.JLabel labelImage1;
    private javax.swing.JLabel labelImage2;
    private javax.swing.JLabel labelImage3;
    private javax.swing.JLabel labelImageDensityViewer;
    private javax.swing.JLabel labelReconstructSize;
    private javax.swing.JLabel labelReconstruction;
    private javax.swing.JLabel labelStepsize;
    private javax.swing.JList modelList;
    private javax.swing.JPanel modelPanel;
    private javax.swing.JFileChooser openFileChooser;
    private javax.swing.JTextField outputImgSizeTomograph;
    private javax.swing.JPanel paneControl;
    private javax.swing.JPanel paneParamModelling;
    private javax.swing.JPanel paneParamReconstruct;
    private javax.swing.JPanel paneReconstruct;
    private javax.swing.JPanel paneResultModelling;
    private javax.swing.JPanel paneSinogram;
    private javax.swing.JPanel paneSourceImage;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JFileChooser saveFileChooser;
    private javax.swing.JTextField scansTomograph;
    private javax.swing.JSlider sliderImage;
    private javax.swing.JTextField stepSizeTomograph;
    private javax.swing.JTable tableProjData;
    private javax.swing.JTextField textFielsDescription;
    private javax.swing.JTextField textFielsName;
    private javax.swing.JToolBar toolbarModellingImage;
    private javax.swing.JToolBar toolbarSourceImage;
    // End of variables declaration//GEN-END:variables

}
