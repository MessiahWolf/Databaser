/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaser;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.RemoteDevice;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author rcher
 */
public class DatabaseJFrame extends javax.swing.JFrame {

    // Variable Declaration
    // Java Native Classes
    private ImageIcon iconBluetoothOff;
    private ImageIcon iconBluetoothOn;
    private ImageIcon iconConnected;
    private ImageIcon iconDatabase;
    private ImageIcon iconDisconnected;
    private ImageIcon iconSend;
    private ImageIcon iconAdd;
    private ImageIcon iconRemove;
    // Project Classes
    private DatabaseConnection databaseConnection;
    // Data Types
    private String loadedHost;
    private String loadedUser;
    private String loadedSchema;
    private String loadedTable;
    private long loadedPort;
    // End of Variable Delcaration

    public static void main(String[] args) {

        // Typical creating the frame
        final DatabaseJFrame frame = new DatabaseJFrame();

        // Setting it viisible
        frame.setVisible(true);
    }

    public DatabaseJFrame() {

        // Attempt to set the look and feel of the application
        try {

            // Set to native look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException cnfe) {
            System.err.println(cnfe);
        }

        //
        initComponents();
        init();
    }

    private void init() {

        // Load the property file, or create if it doesn't exist.
        loadPropertyFile();

        //
        portJFormField.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new java.text.DecimalFormat("#0"))));

        //
        final Toolkit kit = Toolkit.getDefaultToolkit();
        final Class closs = getClass();

        //
        try {

            //
            iconDatabase = new ImageIcon(ImageIO.read(closs.getResource("/icons/icon-database16.png")));
            iconSend = new ImageIcon(ImageIO.read(closs.getResource("/icons/icon-send16.png")));
            iconConnected = new ImageIcon(ImageIO.read(closs.getResource("/icons/icon-connected16.png")));
            iconDisconnected = new ImageIcon(ImageIO.read(closs.getResource("/icons/icon-disconnected16.png")));
            iconBluetoothOff = new ImageIcon(ImageIO.read(closs.getResource("/icons/icon-bluetooth-off16.png")));
            iconBluetoothOn = new ImageIcon(ImageIO.read(closs.getResource("/icons/icon-bluetooth-on16.png")));
            iconAdd = new ImageIcon(ImageIO.read(closs.getResource("/icons/icon-add16.png")));
            iconRemove = new ImageIcon(ImageIO.read(closs.getResource("/icons/icon-remove16.png")));
        } catch (IOException ioe) {

            //
            Logger.getLogger(DatabaseJFrame.class.getName()).log(Level.SEVERE, ioe.getMessage());
        }

        //
        addRowJButton.setEnabled(false);
        addRowJButton.setIcon(iconAdd);
        removeRowJButton.setIcon(iconRemove);
        removeRowJButton.setEnabled(false);
        rebaseJButton.setIcon(iconDatabase);
        rebaseJButton.setEnabled(false);
        rebaseJButton.setToolTipText("Fill Table (" + "SELECT * FROM " + loadedTable + ")");
        statementJButton.setIcon(iconSend);
        statementJButton.setEnabled(false);
        sendJButton.setIcon(iconSend);
        sendJButton.setEnabled(false);
        connectJButton.setIcon(iconDisconnected);
        deviceJButton.setIcon(iconBluetoothOff);
//        deviceJButton.setEnabled(false);

        //
        dataJTable.setModel(new DefaultTableModel());
        dataJTable.setEnabled(false);
        databaseJComboBox.setModel(new DefaultComboBoxModel());
        databaseJComboBox.setEnabled(false);
        tableJComboBox.setModel(new DefaultComboBoxModel());
        tableJComboBox.setEnabled(false);

        //
        final ArrayList<Image> frameImage = new ArrayList<>();
        frameImage.add(kit.createImage(closs.getResource("/icons/icon-frame16.png")));
        frameImage.add(kit.createImage(closs.getResource("/icons/icon-frame32.png")));
        setIconImages(frameImage);

        //
        setTitle("Databaser");

        // Set to correct position on screen
        final Dimension screen = kit.getScreenSize();

        // Set the window to the middle of the screen
        final int x = ((screen.width / 2 - getSize().width / 2));
        final int y = ((screen.height / 2 - getSize().height / 2));

        // Move the window
        setLocation(new Point(x, y));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent evt) {
                System.err.println("Window Closed event.");
                createPropertyFile();
            }

            @Override
            public void windowClosing(WindowEvent evt) {
                System.err.println("Window Closing event.");
                createPropertyFile();
            }
        });
    }

    private void loadPropertyFile() {

        // Attmept to find the property file
        final Properties propertyFile = new Properties();

        try {

            // Load the property file
            propertyFile.load(new FileInputStream("config.properties"));

            // Host key.
            if (propertyFile.containsKey("lastHost")) {
                loadedHost = propertyFile.getProperty("lastHost");
                hostJCheckbox.setSelected(true);
            }

            // Port Key
            if (propertyFile.containsKey("lastPort")) {
                loadedPort = Integer.parseInt(propertyFile.getProperty("lastPort"));
                portJCheckbox.setSelected(true);
            }

            // User key
            if (propertyFile.containsKey("lastUser")) {
                loadedUser = propertyFile.getProperty("lastUser");
                userJCheckbox.setSelected(true);
            }

            // Grab information about the last used databaseConnection.getConnection().
            if (propertyFile.containsKey("lastSchema")) {
                loadedSchema = propertyFile.getProperty("lastSchema");
                databaseJCheckbox.setSelected(true);
            }

            // Table key
            if (propertyFile.containsKey("lastTable")) {
                loadedTable = propertyFile.getProperty("lastTable");
                tableJCheckbox.setSelected(true);
            }

            //
            hostJField.setText(loadedHost);
            userJField.setText(loadedUser);

            //
            portJFormField.setValue(loadedPort);
        } catch (IOException fnfe) {

            //
            createPropertyFile();
        }
    }

    private void createPropertyFile() {

        // Attmept to find the property file
        final Properties propertyFile = new Properties();

        // Don't alter properties if you didn't make a connection.
//        if (databaseConnection == null) {
//            return;
//        }
        try {

            // Save information about the last databaseConnection.getConnection() except for the password.
            propertyFile.setProperty("\\Strings", "");

            // This all depends.
            if (hostJCheckbox.isSelected()) {
                propertyFile.setProperty("lastHost", databaseConnection == null || databaseConnection.getHost() == null ? "" : databaseConnection.getHost());
            } else {
                propertyFile.remove("lastHost");
            }

            // Ack. Depends
            if (userJCheckbox.isSelected()) {
                propertyFile.setProperty("lastUser", databaseConnection == null || databaseConnection.getUser() == null ? "" : databaseConnection.getUser());
            } else {
                propertyFile.remove("lastUser");
            }

            // Depends.
            if (databaseJCheckbox.isSelected()) {
                propertyFile.setProperty("lastSchema", databaseConnection == null || databaseConnection.getDatabase() == null ? "" : databaseConnection.getDatabase());
            } else {
                propertyFile.remove("lastSchema");
            }

            // Also depends.
            if (tableJCheckbox.isSelected()) {
                propertyFile.setProperty("lastTable", databaseConnection == null || databaseConnection.getTable() == null ? "" : databaseConnection.getTable());
            } else {
                propertyFile.remove("lastTable");
            }

            // Integers.
            if (portJCheckbox.isSelected()) {
                propertyFile.setProperty("lastPort", databaseConnection == null ? "3306" : String.valueOf(databaseConnection.getPort()));
            } else {
                propertyFile.remove("lastPort");
            }

            // Write the file.
            propertyFile.store(new FileOutputStream("config.properties"), null);
        } catch (IOException ioe) {

            //
            JOptionPane.showMessageDialog(this, ioe.getMessage(), "Error Creating Property File", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void link() {

        //
        if (databaseConnection == null) {
            return;
        }

        //
        try {
            //
            databaseJComboBox.setSelectedItem(databaseConnection.getDatabase());

            // Setting the model up after databaseConnection.getConnection() is established.
            final DefaultComboBoxModel tbModel = new DefaultComboBoxModel();

            // Grab those table names..
            final String[] tableNames = databaseConnection.prefetchTableNames();

            // Add to the model.
            for (String s : tableNames) {
                tbModel.addElement(s);
            }

            // Set that model and default index
            tableJComboBox.setModel(tbModel);

            //
            if (tbModel.getSize() > 0) {
                tableJComboBox.setSelectedIndex(0);
            }

            //
            databaseConnection.setTable(String.valueOf(tableJComboBox.getSelectedItem()));

            // Enable the disabled controls, because we have an active databaseConnection.getConnection().
            addRowJButton.setEnabled(true);
            removeRowJButton.setEnabled(true);
            rebaseJButton.setEnabled(true);
            sendJButton.setEnabled(true);
            statementJButton.setEnabled(true);
            databaseJComboBox.setEnabled(true);
            tableJComboBox.setEnabled(true);
            dataJTable.setEnabled(true);
            connectJButton.setIcon(iconConnected);

            // Simulate the rebase button
            rebaseJButton.doClick();
        } catch (SQLException sqe) {

            //
            Logger.getLogger(DatabaseJFrame.class.getName()).log(Level.SEVERE, sqe.getMessage());
        }
    }

    private void addPrefilledRow() {

        //
        try {

            //
            if (dataJTable.getModel() != null) {

                // Add that data to the model.
                DefaultTableModel model = (DefaultTableModel) dataJTable.getModel();
                model.addRow(databaseConnection.addPrefilledRow((DefaultTableModel) dataJTable.getModel()));

                // Then finally apply that data to the model.
                dataJTable.setModel(model);
                dataJTable.revalidate();
            }
        } catch (SQLException sqe) {

            //
            Logger.getLogger(DatabaseJFrame.class.getName()).log(Level.SEVERE, sqe.getMessage());
        }
    }

    private void feedStatement(String str) {

        try {

            // Feed that model to the table and revalidate it.
            dataJTable.setModel(databaseConnection.feedStatement(str));
            dataJTable.revalidate();

            // Make sure each column gets its proper renderer.
            final DefaultTableColumnModel columnModel = (DefaultTableColumnModel) dataJTable.getColumnModel();

            // Again we're seaching for the index of the primary key.
            int primaryKeyIndex;
            final String primaryKeyColumnName = databaseConnection.getPKeyColumnName();
            final String[] columnNames = databaseConnection.prefetchColumnNames();

            // Change the renderer for every column in the model
            for (int i = 0; i < columnModel.getColumnCount(); i++) {

                // When it matches let the ToolTipCellRenderer know which column is the primary Key index so we can color it a different color.
                if (columnNames[i].equalsIgnoreCase(primaryKeyColumnName)) {
                    primaryKeyIndex = i;
                } else {
                    primaryKeyIndex = -1;
                }

                //
                columnModel.getColumn(i).setCellRenderer(new ToolTipCellRenderer(primaryKeyIndex));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void rebase() {

        //
        dataJTable.setModel(new DefaultTableModel());
        dataJTable.revalidate();

        // Again, Good luck.
        feedStatement("SELECT * FROM " + tableJComboBox.getSelectedItem());
    }

    public DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }

    public JTable getDataTable() {
        return dataJTable;
    }

    public void setDeviceIconOff() {

        //
        if (iconBluetoothOff != null) {
            
            //
            deviceJButton.setIcon(iconBluetoothOff);
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
        java.awt.GridBagConstraints gridBagConstraints;

        graphJButton = new javax.swing.JButton();
        closeJButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        buttonJPanel = new javax.swing.JPanel();
        rebaseJButton = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        sendJButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        addRowJButton = new javax.swing.JButton();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        removeRowJButton = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        jSeparator1 = new javax.swing.JSeparator();
        deviceJButton = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        connectJButton = new javax.swing.JButton();
        mainJTabbedPane = new javax.swing.JTabbedPane();
        mainJScrollPane = new javax.swing.JScrollPane();
        dataJTable = new javax.swing.JTable() {
            @Override
            public boolean isCellEditable(int row, int col) {
                return customIsCellEditable(row, col);
            }
        };
        mainJPanel = new javax.swing.JPanel();
        controlJPanel = new javax.swing.JPanel();
        userJLabel = new javax.swing.JLabel();
        passwordJLabel = new javax.swing.JLabel();
        portJLabel = new javax.swing.JLabel();
        databaseJLabel = new javax.swing.JLabel();
        databaseJComboBox = new javax.swing.JComboBox<>();
        userJField = new javax.swing.JTextField();
        passwordJField = new javax.swing.JPasswordField();
        portJFormField = new javax.swing.JFormattedTextField();
        hostJField = new javax.swing.JTextField();
        userJLabel1 = new javax.swing.JLabel();
        tableJLabel = new javax.swing.JLabel();
        tableJComboBox = new javax.swing.JComboBox<>();
        tableJCheckbox = new javax.swing.JCheckBox();
        databaseJCheckbox = new javax.swing.JCheckBox();
        portJCheckbox = new javax.swing.JCheckBox();
        userJCheckbox = new javax.swing.JCheckBox();
        hostJCheckbox = new javax.swing.JCheckBox();
        passwordJCheckbox = new javax.swing.JCheckBox();
        statementJPanel = new javax.swing.JPanel();
        statementJLabel = new javax.swing.JLabel();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        statementJField = new javax.swing.JTextField();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        statementJButton = new javax.swing.JButton();
        descJLabel = new javax.swing.JLabel();

        graphJButton.setText("Graph");
        graphJButton.setEnabled(false);
        graphJButton.setPreferredSize(new java.awt.Dimension(110, 23));

        closeJButton.setText("Close");
        closeJButton.setMaximumSize(new java.awt.Dimension(68, 22));
        closeJButton.setMinimumSize(new java.awt.Dimension(68, 22));
        closeJButton.setPreferredSize(new java.awt.Dimension(68, 22));
        closeJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeJButtonActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(354, 366));
        setResizable(false);

        buttonJPanel.setLayout(new javax.swing.BoxLayout(buttonJPanel, javax.swing.BoxLayout.LINE_AXIS));

        rebaseJButton.setToolTipText("Update to values in database");
        rebaseJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        rebaseJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        rebaseJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        rebaseJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rebaseJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(rebaseJButton);
        buttonJPanel.add(filler3);

        sendJButton.setToolTipText("Send changes to database");
        sendJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        sendJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        sendJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        sendJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(sendJButton);
        buttonJPanel.add(filler1);

        addRowJButton.setToolTipText("Add a row to the active table");
        addRowJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        addRowJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        addRowJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        addRowJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRowJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(addRowJButton);
        buttonJPanel.add(filler6);

        removeRowJButton.setToolTipText("Remove a row from the active table");
        removeRowJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        removeRowJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        removeRowJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        removeRowJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeRowJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(removeRowJButton);
        buttonJPanel.add(filler5);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setMaximumSize(new java.awt.Dimension(10, 24));
        jSeparator1.setMinimumSize(new java.awt.Dimension(10, 24));
        jSeparator1.setPreferredSize(new java.awt.Dimension(10, 24));
        buttonJPanel.add(jSeparator1);

        deviceJButton.setToolTipText("Open communication with bluetooth device");
        deviceJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        deviceJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        deviceJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        deviceJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deviceJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(deviceJButton);
        buttonJPanel.add(filler4);

        connectJButton.setToolTipText("Open a connection with specified credentials");
        connectJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        connectJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        connectJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        connectJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectJButtonActionPerformed(evt);
            }
        });
        buttonJPanel.add(connectJButton);

        mainJScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Result Table"));
        mainJScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        dataJTable.setModel(new javax.swing.table.DefaultTableModel(
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
        dataJTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        dataJTable.setAutoscrolls(false);
        dataJTable.setFillsViewportHeight(true);
        dataJTable.getTableHeader().setReorderingAllowed(false);
        mainJScrollPane.setViewportView(dataJTable);

        mainJTabbedPane.addTab("Table", mainJScrollPane);

        mainJPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Connection Info."));

        controlJPanel.setMinimumSize(new java.awt.Dimension(216, 144));
        controlJPanel.setPreferredSize(new java.awt.Dimension(216, 144));
        java.awt.GridBagLayout controlJPanelLayout = new java.awt.GridBagLayout();
        controlJPanelLayout.columnWidths = new int[] {0, 10, 0, 10, 0};
        controlJPanelLayout.rowHeights = new int[] {0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0};
        controlJPanel.setLayout(controlJPanelLayout);

        userJLabel.setText("User:");
        userJLabel.setMaximumSize(new java.awt.Dimension(80, 24));
        userJLabel.setMinimumSize(new java.awt.Dimension(80, 24));
        userJLabel.setPreferredSize(new java.awt.Dimension(80, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        controlJPanel.add(userJLabel, gridBagConstraints);

        passwordJLabel.setText("Password:");
        passwordJLabel.setMaximumSize(new java.awt.Dimension(80, 24));
        passwordJLabel.setMinimumSize(new java.awt.Dimension(80, 24));
        passwordJLabel.setPreferredSize(new java.awt.Dimension(80, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        controlJPanel.add(passwordJLabel, gridBagConstraints);

        portJLabel.setText("Port:");
        portJLabel.setMaximumSize(new java.awt.Dimension(80, 24));
        portJLabel.setMinimumSize(new java.awt.Dimension(80, 24));
        portJLabel.setPreferredSize(new java.awt.Dimension(80, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        controlJPanel.add(portJLabel, gridBagConstraints);

        databaseJLabel.setText("Database: ");
        databaseJLabel.setMaximumSize(new java.awt.Dimension(80, 24));
        databaseJLabel.setMinimumSize(new java.awt.Dimension(80, 24));
        databaseJLabel.setPreferredSize(new java.awt.Dimension(80, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        controlJPanel.add(databaseJLabel, gridBagConstraints);

        databaseJComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        databaseJComboBox.setMaximumSize(new java.awt.Dimension(128, 24));
        databaseJComboBox.setMinimumSize(new java.awt.Dimension(128, 24));
        databaseJComboBox.setNextFocusableComponent(tableJComboBox);
        databaseJComboBox.setPreferredSize(new java.awt.Dimension(128, 24));
        databaseJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                databaseJComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        controlJPanel.add(databaseJComboBox, gridBagConstraints);

        userJField.setMaximumSize(new java.awt.Dimension(128, 24));
        userJField.setMinimumSize(new java.awt.Dimension(128, 24));
        userJField.setNextFocusableComponent(passwordJField);
        userJField.setPreferredSize(new java.awt.Dimension(128, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        controlJPanel.add(userJField, gridBagConstraints);

        passwordJField.setMaximumSize(new java.awt.Dimension(128, 24));
        passwordJField.setMinimumSize(new java.awt.Dimension(128, 24));
        passwordJField.setNextFocusableComponent(portJFormField);
        passwordJField.setPreferredSize(new java.awt.Dimension(128, 24));
        passwordJField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordJFieldActionPerformed(evt);
            }
        });
        passwordJField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                passwordJFieldFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        controlJPanel.add(passwordJField, gridBagConstraints);

        portJFormField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        portJFormField.setMaximumSize(new java.awt.Dimension(128, 24));
        portJFormField.setMinimumSize(new java.awt.Dimension(128, 24));
        portJFormField.setNextFocusableComponent(databaseJComboBox);
        portJFormField.setPreferredSize(new java.awt.Dimension(128, 24));
        portJFormField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                portJFormFieldActionPerformed(evt);
            }
        });
        portJFormField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                portJFormFieldFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        controlJPanel.add(portJFormField, gridBagConstraints);

        hostJField.setMaximumSize(new java.awt.Dimension(128, 24));
        hostJField.setMinimumSize(new java.awt.Dimension(128, 24));
        hostJField.setNextFocusableComponent(userJField);
        hostJField.setPreferredSize(new java.awt.Dimension(128, 24));
        hostJField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hostJFieldActionPerformed(evt);
            }
        });
        hostJField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                hostJFieldFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        controlJPanel.add(hostJField, gridBagConstraints);

        userJLabel1.setText("Host:");
        userJLabel1.setMaximumSize(new java.awt.Dimension(80, 24));
        userJLabel1.setMinimumSize(new java.awt.Dimension(80, 24));
        userJLabel1.setPreferredSize(new java.awt.Dimension(80, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        controlJPanel.add(userJLabel1, gridBagConstraints);

        tableJLabel.setText("Table:");
        tableJLabel.setMaximumSize(new java.awt.Dimension(80, 24));
        tableJLabel.setMinimumSize(new java.awt.Dimension(80, 24));
        tableJLabel.setPreferredSize(new java.awt.Dimension(80, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        controlJPanel.add(tableJLabel, gridBagConstraints);

        tableJComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        tableJComboBox.setMaximumSize(new java.awt.Dimension(128, 24));
        tableJComboBox.setMinimumSize(new java.awt.Dimension(128, 24));
        tableJComboBox.setNextFocusableComponent(hostJField);
        tableJComboBox.setPreferredSize(new java.awt.Dimension(128, 24));
        tableJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableJComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        controlJPanel.add(tableJComboBox, gridBagConstraints);

        tableJCheckbox.setText("Remember");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 10;
        controlJPanel.add(tableJCheckbox, gridBagConstraints);

        databaseJCheckbox.setText("Remember");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        controlJPanel.add(databaseJCheckbox, gridBagConstraints);

        portJCheckbox.setText("Remember");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        controlJPanel.add(portJCheckbox, gridBagConstraints);

        userJCheckbox.setText("Remember");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        controlJPanel.add(userJCheckbox, gridBagConstraints);

        hostJCheckbox.setText("Remember");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        controlJPanel.add(hostJCheckbox, gridBagConstraints);

        passwordJCheckbox.setText("Show");
        passwordJCheckbox.setMaximumSize(new java.awt.Dimension(77, 23));
        passwordJCheckbox.setMinimumSize(new java.awt.Dimension(77, 23));
        passwordJCheckbox.setPreferredSize(new java.awt.Dimension(77, 23));
        passwordJCheckbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                passwordJCheckboxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        controlJPanel.add(passwordJCheckbox, gridBagConstraints);

        javax.swing.GroupLayout mainJPanelLayout = new javax.swing.GroupLayout(mainJPanel);
        mainJPanel.setLayout(mainJPanelLayout);
        mainJPanelLayout.setHorizontalGroup(
            mainJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(controlJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainJPanelLayout.setVerticalGroup(
            mainJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(controlJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        mainJTabbedPane.addTab("Settings", mainJPanel);

        statementJPanel.setMaximumSize(new java.awt.Dimension(32985, 24));
        statementJPanel.setMinimumSize(new java.awt.Dimension(218, 24));
        statementJPanel.setPreferredSize(new java.awt.Dimension(244, 24));
        statementJPanel.setLayout(new javax.swing.BoxLayout(statementJPanel, javax.swing.BoxLayout.LINE_AXIS));

        statementJLabel.setText("Statement:");
        statementJLabel.setMaximumSize(new java.awt.Dimension(60, 24));
        statementJLabel.setMinimumSize(new java.awt.Dimension(60, 24));
        statementJLabel.setPreferredSize(new java.awt.Dimension(64, 24));
        statementJPanel.add(statementJLabel);
        statementJPanel.add(filler7);

        statementJField.setMaximumSize(new java.awt.Dimension(128, 24));
        statementJField.setMinimumSize(new java.awt.Dimension(128, 24));
        statementJField.setPreferredSize(new java.awt.Dimension(128, 24));
        statementJField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statementJFieldActionPerformed(evt);
            }
        });
        statementJPanel.add(statementJField);
        statementJPanel.add(filler8);

        statementJButton.setToolTipText("");
        statementJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        statementJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        statementJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        statementJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statementJButtonActionPerformed(evt);
            }
        });
        statementJPanel.add(statementJButton);

        descJLabel.setText("Execute Query to Database (Not an Update)");
        descJLabel.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(mainJTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(buttonJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(statementJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(descJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mainJTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(descJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(statementJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    //

    private boolean customIsCellEditable(int row, int col) {

        //
        try {

            //
            if (databaseConnection != null && databaseConnection.getConnection() != null) {

                //
                final Statement statement = databaseConnection.getConnection().createStatement();

                // First get the number of rows for the loop below
                ResultSet result = statement.executeQuery("SELECT COUNT(DATA_TYPE) FROM INFORMATION_SCHEMA.COLUMNS  WHERE TABLE_SCHEMA ='" + databaseConnection.getDatabase() + "' AND TABLE_NAME = '" + databaseConnection.getTable() + "'");
                result.first();

                // Our number of rows
                final int rowCount = result.getInt(1);
                final int primaryKeyIndex = databaseConnection.getPKeyIndex((DefaultTableModel) dataJTable.getModel());

                // Now query up the information we want which is the data type for each column
                result = statement.executeQuery("SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA ='" + databaseConnection.getDatabase() + "' AND TABLE_NAME = '" + databaseConnection.getTable() + "'");
                result.first();

                // Go over the rows derived from the query's result set.
                // which should be 1 row for every data type  in each column
                for (int i = col; i < rowCount; i++) {

                    // Disable for timestamps and the primary key -- will auto fill soon.
                    if (i == primaryKeyIndex || result.getString(1).equalsIgnoreCase("timestamp")) {
                        return false;
                    }

                    // Otherwise advance.
                    result.next();
                }

                // Remember to close the databaseConnection.getConnection()
                statement.close();
            }
        } catch (SQLException sqe) {

            //
            Logger.getLogger(DatabaseJFrame.class.getName()).log(Level.SEVERE, sqe.getMessage());
        }

        // On uncaught values, just allow.
        return true;
    }
    private void passwordJFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordJFieldActionPerformed

        // Setting the password for the databaseConnection.getConnection()
        //connPassword = new String(passwordJField.getPassword());
        // Now attempt to connect
        connectJButton.doClick();

        // Clear field
        //passwordJField.setText("");
    }//GEN-LAST:event_passwordJFieldActionPerformed

    private void portJFormFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_portJFormFieldActionPerformed

        // Update Control.
        connectJButton.setToolTipText("jdbc:mysql://" + databaseConnection.getHost() + ":" + databaseConnection.getPort() + "/" + databaseConnection.getDatabase());
    }//GEN-LAST:event_portJFormFieldActionPerformed

    private void databaseJComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_databaseJComboBoxActionPerformed

        // Setting the database to derive data from
        databaseConnection.setDatabase(String.valueOf(databaseJComboBox.getSelectedItem()));

        // Update Control.
        connectJButton.setToolTipText("jdbc:mysql://" + databaseConnection.getHost() + ":" + databaseConnection.getPort() + "/" + databaseConnection.getDatabase());

        // Attempt link
        link();

        //
        if (databaseConnection.getTable() != null && !databaseConnection.getTable().isEmpty()) {
            tableJComboBox.setSelectedItem(databaseConnection.getTable());
        } else {
            //Attempt to update the table to the data in the database for the first found table; if any.
            tableJComboBox.setSelectedItem(0);
        }
    }//GEN-LAST:event_databaseJComboBoxActionPerformed

    private void statementJFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statementJFieldActionPerformed

        // We must have an active databaseConnection.getConnection() to the databse to process any statements
        if (databaseConnection != null) {

            // Good luck.
            feedStatement(statementJField.getText());
        }
    }//GEN-LAST:event_statementJFieldActionPerformed

    private void connectJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectJButtonActionPerformed

        // Attempting our credentials on the server / driver
        try {

            // Creating a database model instead
            final DefaultComboBoxModel dbModel = new DefaultComboBoxModel();

            //
            databaseConnection = new DatabaseConnection(hostJField.getText(), (long) portJFormField.getValue(), loadedSchema, userJField.getText(), new String(passwordJField.getPassword()));

            //
            if (databaseConnection != null) {

                //
                final String[] names = databaseConnection.prefetchDatabaseNames();

                // If the names return is null kick out.
                if (names == null) {
                    return;
                }

                //
                for (String s : names) {
                    dbModel.addElement(s);
                }

                // Clear the table model.
                dataJTable.setModel(new DefaultTableModel());
                databaseJComboBox.setModel(dbModel);

                // If the database is filled in  (in the case of preloading from a property file)
                if (databaseConnection.getDatabase() != null && !databaseConnection.getDatabase().isEmpty()) {

                    // Event procs link(); by itsself in databaseJComboBoxActionPerformed();
                    databaseJComboBox.setSelectedItem(databaseConnection.getDatabase());
                } else {

                    //
                    databaseJComboBox.setSelectedIndex(0);

                    //
                    databaseConnection.setDatabase(databaseJComboBox.getItemAt(0));

                    // Attempt to link.
                    link();
                }
            }

            // Enable the device button
//            deviceJButton.setEnabled(true);
            // Recreate the property file on successful link.
            createPropertyFile();
        } catch (SQLException se) {

            // Disable the controls because the databaseConnection.getConnection() failed to link.
            addRowJButton.setEnabled(false);
            removeRowJButton.setEnabled(false);
            rebaseJButton.setEnabled(false);
            sendJButton.setEnabled(false);
            statementJButton.setEnabled(false);
            databaseJComboBox.setEnabled(false);
            tableJComboBox.setEnabled(false);
            dataJTable.setEnabled(false);
            connectJButton.setIcon(iconDisconnected);

            //
//            deviceJButton.setEnabled(false);
            // Show the user a failure message
            JOptionPane.showMessageDialog(this, se.getMessage(), "Connection Failed", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_connectJButtonActionPerformed

    private void rebaseJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rebaseJButtonActionPerformed

        //
        rebase();
    }//GEN-LAST:event_rebaseJButtonActionPerformed

    private void hostJFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hostJFieldActionPerformed

        // Update Control.
        connectJButton.setToolTipText("jdbc:mysql://" + hostJField.getText() + ":" + portJFormField.getValue() + "/" + databaseJComboBox.getSelectedItem());
    }//GEN-LAST:event_hostJFieldActionPerformed

    private void hostJFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_hostJFieldFocusLost

        // Update Control.
        connectJButton.setToolTipText("jdbc:mysql://" + hostJField.getText() + ":" + portJFormField.getValue() + "/" + databaseJComboBox.getSelectedItem());
    }//GEN-LAST:event_hostJFieldFocusLost

    private void passwordJFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passwordJFieldFocusLost

        // Clear field
        //passwordJField.setText("");
    }//GEN-LAST:event_passwordJFieldFocusLost

    private void portJFormFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_portJFormFieldFocusLost

        // Solve for letters or illegal characters in the field before processing
        try {

            // Update Control.
            connectJButton.setToolTipText("jdbc:mysql://" + hostJField.getText() + ":" + portJFormField.getText() + "/" + databaseJComboBox.getSelectedItem());
        } catch (NumberFormatException nfe) {

            // In event of failure return to deafult port of '3306'
            portJFormField.setValue(3306);
        }
    }//GEN-LAST:event_portJFormFieldFocusLost

    private void tableJComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableJComboBoxActionPerformed

        // Base Null Case.
        if (databaseConnection == null) {
            return;
        }

        // Setting the table from the database to load data from
        databaseConnection.setTable(String.valueOf(tableJComboBox.getSelectedItem()));

        // If the table exists then update the JTable to reflect the information in the database
        if (databaseConnection.getTable() != null) {
            rebaseJButton.doClick();
        }

        // Adjust to the new table.
        rebaseJButton.setToolTipText("Fill Table (" + "SELECT * FROM " + databaseConnection.getTable() + ")");
    }//GEN-LAST:event_tableJComboBoxActionPerformed

    private void statementJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statementJButtonActionPerformed

        // Base Null Case.
        if (databaseConnection == null) {
            return;
        }

        // Connection must exist to attempt statements on databaseConnection.getConnection()
        if (databaseConnection.getConnection() != null) {

            // Break a leg.
            feedStatement(statementJField.getText());
        }
    }//GEN-LAST:event_statementJButtonActionPerformed

    private void closeJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeJButtonActionPerformed

        // TODO add your handling code here:
        try {

            // Base Null Case.
            if (databaseConnection != null) {

                // Attempt to close the databaseConnection.getConnection() before the application finishes; don't thread of loop to make sure. Just get the attempt in and close.
                if (databaseConnection.getConnection() != null) {
                    databaseConnection.getConnection().close();
                }
            }
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, se.getMessage(), "Failed to close databaseConnection.getConnection()", JOptionPane.ERROR_MESSAGE);
        }

        // Close this application.
        dispose();
    }//GEN-LAST:event_closeJButtonActionPerformed

    private void sendJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendJButtonActionPerformed

        // Base Null Case.
        if (databaseConnection == null) {
            return;
        }

        // The databaseConnection.getConnection() must exist for the user to push changes onto the database
        if (databaseConnection.getConnection() != null) {

            // Model must exist because this is where we'll be pulling information from.
            if (dataJTable.getModel() != null) {

                // Then attempt to update.
                databaseConnection.sendChanges((DefaultTableModel) dataJTable.getModel());
            }
        }
    }//GEN-LAST:event_sendJButtonActionPerformed

    private void addRowJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRowJButtonActionPerformed

        // Base Null Case.
        if (databaseConnection == null) {
            return;
        }

        // TODO add your handling code here:
        if (databaseConnection.getConnection() != null) {
            addPrefilledRow();
        }
    }//GEN-LAST:event_addRowJButtonActionPerformed

    private void removeRowJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeRowJButtonActionPerformed

        // Base Null Case.
        if (databaseConnection == null) {
            return;
        }

        // No databaseConnection.getConnection(), then no button service.
        if (databaseConnection.getConnection() != null) {

            // Don't service on a null model.
            if (dataJTable.getModel() != null) {

                //
                final int row = dataJTable.getSelectedRow();
                final String primaryColumnName = databaseConnection.getPKeyColumnName();
                String primaryColumnValue = null;

                // A row must exist and be selected.
                if (row != -1) {

                    // Prompt the user if they're sure they wish to delete the row
                    if (JOptionPane.showConfirmDialog(this, "Are you sure you wish to delete data for this row (" + row + ") ?", "Confirm Delete", JOptionPane.YES_NO_OPTION) != JOptionPane.OK_OPTION) {
                        return;
                    }

                    //
                    final DefaultTableModel model = (DefaultTableModel) dataJTable.getModel();

                    // Determine location of primary key
                    for (int col = 0; col < model.getColumnCount(); col++) {

                        //
                        if (model.getColumnName(col).equalsIgnoreCase(primaryColumnName)) {
                            primaryColumnValue = String.valueOf(model.getValueAt(row, col));
                            break;
                        }
                    }

                    // First and foremost, the user must have a row highlighted, and for our purposes there must be an existing primary key in use.
                    if (primaryColumnValue != null) {

                        // Deleting is pretty simple as you only need to know the primaryColumnName, if that's what you want to use to determine what makes each row unique.
                        // You could modify this program to check for other characteristics.
                        try {

                            // Create the staement
                            final Statement statement = databaseConnection.createStatement();

                            // Delete from the table where the primary key matches the selected key in the table.
                            statement.executeUpdate("DELETE FROM " + databaseConnection.getTable() + " WHERE " + primaryColumnName + " = '" + primaryColumnValue + "' LIMIT 1;");

                            // Then remove the row from the model.
                            model.removeRow(dataJTable.getSelectedRow());
                        } catch (SQLException sqe) {
                            Logger.getLogger(DatabaseJFrame.class.getName()).log(Level.SEVERE, sqe.getMessage(), sqe);
                        }
                    }

                    // Now update the table to reflect these new values.
                    dataJTable.setModel(model);
                    dataJTable.revalidate();
                }
            }
        }
    }//GEN-LAST:event_removeRowJButtonActionPerformed

    private void deviceJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deviceJButtonActionPerformed
        // TODO add your handling code here:
        deviceJButton.setIcon(iconBluetoothOn);

        //
        final BluetoothJDialog dialog = new BluetoothJDialog(this, false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }//GEN-LAST:event_deviceJButtonActionPerformed

    private void passwordJCheckboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_passwordJCheckboxItemStateChanged
        // TODO add your handling code here:
        passwordJField.setEchoChar(passwordJCheckbox.isSelected() ? (char) 0 : '●');
    }//GEN-LAST:event_passwordJCheckboxItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addRowJButton;
    private javax.swing.JPanel buttonJPanel;
    private javax.swing.JButton closeJButton;
    private javax.swing.JButton connectJButton;
    private javax.swing.JPanel controlJPanel;
    private javax.swing.JTable dataJTable;
    private javax.swing.JCheckBox databaseJCheckbox;
    private javax.swing.JComboBox<String> databaseJComboBox;
    private javax.swing.JLabel databaseJLabel;
    private javax.swing.JLabel descJLabel;
    private javax.swing.JButton deviceJButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.JButton graphJButton;
    private javax.swing.JCheckBox hostJCheckbox;
    private javax.swing.JTextField hostJField;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel mainJPanel;
    private javax.swing.JScrollPane mainJScrollPane;
    private javax.swing.JTabbedPane mainJTabbedPane;
    private javax.swing.JCheckBox passwordJCheckbox;
    private javax.swing.JPasswordField passwordJField;
    private javax.swing.JLabel passwordJLabel;
    private javax.swing.JCheckBox portJCheckbox;
    private javax.swing.JFormattedTextField portJFormField;
    private javax.swing.JLabel portJLabel;
    private javax.swing.JButton rebaseJButton;
    private javax.swing.JButton removeRowJButton;
    private javax.swing.JButton sendJButton;
    private javax.swing.JButton statementJButton;
    private javax.swing.JTextField statementJField;
    private javax.swing.JLabel statementJLabel;
    private javax.swing.JPanel statementJPanel;
    private javax.swing.JCheckBox tableJCheckbox;
    private javax.swing.JComboBox<String> tableJComboBox;
    private javax.swing.JLabel tableJLabel;
    private javax.swing.JCheckBox userJCheckbox;
    private javax.swing.JTextField userJField;
    private javax.swing.JLabel userJLabel;
    private javax.swing.JLabel userJLabel1;
    // End of variables declaration//GEN-END:variables
}
