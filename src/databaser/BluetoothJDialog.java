/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaser;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.RemoteDevice;
import javax.imageio.ImageIO;
import javax.microedition.io.StreamConnection;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;

/**
 *
 * @author rcher
 */
public class BluetoothJDialog extends javax.swing.JDialog implements WindowListener {

    // Variable Declaration
    // Project Classes
    private BluetoothDeviceManager deviceManager;
    private BluetoothDevice bluetoothDevice;
    private final DatabaseJFrame frame;
    // Java Native Classes
    private ArrayList<String> urlList;
    final DateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    private ImageIcon iconBluetoothOff;
    private ImageIcon iconBluetoothOn;
    private ImageIcon iconBluetoothSearchOff;
    private ImageIcon iconBluetoothSearchOn;
    private ImageIcon iconBluetoothSearchFinished;
    private ImageIcon iconClear;
    private ImageIcon iconLog;
    private ImageIcon iconSend;
    private StreamConnection streamConnection;
    private Thread searchThread;
    private InputStream input;
    private OutputStream output;
    // Data Types
    private boolean ready = false;
    private String database;
    private String table;
    private ArrayList<String> reportDump;
    // <- End of Variable Declaration

    public BluetoothJDialog(DatabaseJFrame frame, boolean modal) {

        //
        super(frame, modal);
        initComponents();

        //
        this.frame = frame;

        //
        init();
    }

    private void init() {

        //
        reportDump = new ArrayList();

        //
        addWindowListener(this);

        //
        final Class closs = getClass();

        try {

            //
            iconBluetoothOff = new ImageIcon(ImageIO.read(closs.getResource("/icons/icon-bluetooth-off16.png")));
            iconBluetoothOn = new ImageIcon(ImageIO.read((closs.getResource("/icons/icon-bluetooth-on16.png"))));
            iconBluetoothSearchOn = new ImageIcon(ImageIO.read((closs.getResource("/icons/icon-bluetooth-locate-on16.png"))));
            iconBluetoothSearchOff = new ImageIcon(ImageIO.read((closs.getResource("/icons/icon-bluetooth-locate-off16.png"))));
            iconBluetoothSearchFinished = new ImageIcon(ImageIO.read((closs.getResource("/icons/icon-bluetooth-locate-finished16.png"))));
            iconClear = new ImageIcon(ImageIO.read(closs.getResource("/icons/icon-clear16.png")));
            iconLog = new ImageIcon(ImageIO.read(closs.getResource("/icons/icon-log16.png")));
            iconSend = new ImageIcon(ImageIO.read(closs.getResource("/icons/icon-send16.png")));

            //
            deviceJButton.setIcon(iconBluetoothOff);
            statementJButton.setIcon(iconSend);
            logJButton.setIcon(iconLog);
            clearJButton.setIcon(iconClear);

            // Change the icon back
            searchJButton.setIcon(iconBluetoothSearchOff);

            // Enable the button
            statementJButton.setEnabled(false);
            logJButton.setEnabled(false);
            clearJButton.setEnabled(false);

            //
            deviceJComboBox.setEnabled(false);
            deviceJButton.setEnabled(false);
        } catch (IOException ioe) {

            //
            Logger.getLogger(BluetoothJDialog.class.getName()).log(Level.SEVERE, ioe.getMessage());
        }

        //
        final DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("No Devices");

        //
        deviceJComboBox.setModel(model);

        // Creating the device manager
        deviceManager = new BluetoothDeviceManager();

        //
        urlJField.setText("Not connected");

        //
        setTitle("Terminal");
    }

    private void reportToDatabase(String report) {

        // Grab the connection to the database
        final DatabaseConnection databaseConnection = frame.getDatabaseConnection();
        final String[] args = report.split(":");

        // Make sure we're validated
        if (ready) {

            // You must have an active connection to the databse.
            if (databaseConnection != null) {

                // We're only worried about GR_AUTO and GR_RA
                if (args[0].equals("GR_AUTO") || args[0].equals("GR_RA")) {

                    //
                    try {

                        // Then only in these two cases are we writing to the database, everything else is just echoing to the user.
                        final String query = "INSERT INTO " + database + "." + table + " (celTemp, humidity) VALUES (" + args[1] + "," + args[2] + ");";

                        // Execute the query.
                        final int result = databaseConnection.feedUpdate(query);

                        // Then rebase
                        frame.rebase();
                    } catch (SQLException sqle) {

                        //
                        Logger.getLogger(BluetoothJDialog.class.getName()).log(Level.SEVERE, sqle.getMessage());
                    }
                }
            }
        } else {

            // Otherwise add to the report dump.
            reportDump.add(report);
        }
    }

    private void createLogFile() {

        //
        String dir = "logs";

        // Create a directory
        if (new File(dir).exists()) {

            //
            try {

                //
                final String date = format.format(new Date()).replaceAll("/", "_").replaceAll(":", "_");

                //
                final File file = new File(dir + "/" + date + ".txt");

                // Delete the file if it exists.
                if (file.exists()) {

                    // We failed to delete the previous file.
                    if (!file.delete()) {
                        return;
                    }
                }

                // If we were successful in creating the file.
                if (file.createNewFile()) {

                    //
                    try (PrintStream printer = new PrintStream(file)) {

                        //
                        for (char c : mainJTextArea.getText().toCharArray()) {

                            // Println on \n
                            if (c == 10) {
                                printer.println();
                            } else {
                                printer.print(c);
                            }
                        }
                    }
                }
            } catch (IOException ioe) {

                //
                Logger.getLogger(BluetoothJDialog.class.getName()).log(Level.SEVERE, ioe.getMessage());
            }
        } else {

            //
            final File directory = new File(dir);

            // We were successful in creating the directory so retry
            if (directory.mkdir()) {
                createLogFile();
            }
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

        jLabel1 = new javax.swing.JLabel();
        serviceJComboBox = new javax.swing.JComboBox<>();
        mainJScrollPane = new javax.swing.JScrollPane();
        mainJTextArea = new javax.swing.JTextArea();
        buttonLowerJPanel = new javax.swing.JPanel();
        clearJButton = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        statementJField = new javax.swing.JTextField();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        statementJButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        deviceJButton = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        jSeparator1 = new javax.swing.JSeparator();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        logJButton = new javax.swing.JButton();
        buttonUpperJPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        urlJField = new javax.swing.JTextField();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        jLabel2 = new javax.swing.JLabel();
        deviceJComboBox = new javax.swing.JComboBox<>();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        searchJButton = new javax.swing.JButton();

        jLabel1.setText("Service:");
        jLabel1.setMaximumSize(new java.awt.Dimension(48, 24));
        jLabel1.setMinimumSize(new java.awt.Dimension(48, 24));
        jLabel1.setPreferredSize(new java.awt.Dimension(48, 24));

        serviceJComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        serviceJComboBox.setMaximumSize(new java.awt.Dimension(88, 24));
        serviceJComboBox.setMinimumSize(new java.awt.Dimension(88, 24));
        serviceJComboBox.setName(""); // NOI18N
        serviceJComboBox.setPreferredSize(new java.awt.Dimension(88, 24));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(404, 268));

        mainJTextArea.setEditable(false);
        mainJTextArea.setColumns(20);
        mainJTextArea.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        mainJTextArea.setLineWrap(true);
        mainJTextArea.setRows(5);
        mainJScrollPane.setViewportView(mainJTextArea);

        buttonLowerJPanel.setLayout(new javax.swing.BoxLayout(buttonLowerJPanel, javax.swing.BoxLayout.LINE_AXIS));

        clearJButton.setToolTipText("Clear the activity window");
        clearJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        clearJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        clearJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        clearJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearJButtonActionPerformed(evt);
            }
        });
        buttonLowerJPanel.add(clearJButton);
        buttonLowerJPanel.add(filler3);

        statementJField.setMinimumSize(new java.awt.Dimension(6, 24));
        statementJField.setPreferredSize(new java.awt.Dimension(6, 24));
        statementJField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statementJFieldActionPerformed(evt);
            }
        });
        buttonLowerJPanel.add(statementJField);
        buttonLowerJPanel.add(filler1);

        statementJButton.setToolTipText("Send command to device");
        statementJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        statementJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        statementJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        statementJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statementJButtonActionPerformed(evt);
            }
        });
        buttonLowerJPanel.add(statementJButton);
        buttonLowerJPanel.add(filler2);

        deviceJButton.setToolTipText("Connect or reconnect to device");
        deviceJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        deviceJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        deviceJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        deviceJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deviceJButtonActionPerformed(evt);
            }
        });
        buttonLowerJPanel.add(deviceJButton);
        buttonLowerJPanel.add(filler5);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        buttonLowerJPanel.add(jSeparator1);
        buttonLowerJPanel.add(filler4);

        logJButton.setToolTipText("Create a log of the current activity");
        logJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        logJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        logJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        logJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logJButtonActionPerformed(evt);
            }
        });
        buttonLowerJPanel.add(logJButton);

        buttonUpperJPanel.setLayout(new javax.swing.BoxLayout(buttonUpperJPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setText("URL:");
        jLabel3.setEnabled(false);
        jLabel3.setMaximumSize(new java.awt.Dimension(24, 24));
        jLabel3.setMinimumSize(new java.awt.Dimension(24, 24));
        jLabel3.setPreferredSize(new java.awt.Dimension(24, 24));
        buttonUpperJPanel.add(jLabel3);
        buttonUpperJPanel.add(filler9);

        urlJField.setEditable(false);
        urlJField.setMaximumSize(new java.awt.Dimension(32767, 24));
        urlJField.setMinimumSize(new java.awt.Dimension(88, 24));
        urlJField.setPreferredSize(new java.awt.Dimension(32767, 24));
        buttonUpperJPanel.add(urlJField);
        buttonUpperJPanel.add(filler6);
        buttonUpperJPanel.add(filler8);

        jLabel2.setText("Device:");
        jLabel2.setMaximumSize(new java.awt.Dimension(48, 24));
        jLabel2.setMinimumSize(new java.awt.Dimension(48, 24));
        jLabel2.setPreferredSize(new java.awt.Dimension(48, 24));
        buttonUpperJPanel.add(jLabel2);

        deviceJComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        deviceJComboBox.setMaximumSize(new java.awt.Dimension(128, 24));
        deviceJComboBox.setMinimumSize(new java.awt.Dimension(128, 24));
        deviceJComboBox.setPreferredSize(new java.awt.Dimension(128, 24));
        deviceJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deviceJComboBoxActionPerformed(evt);
            }
        });
        buttonUpperJPanel.add(deviceJComboBox);
        buttonUpperJPanel.add(filler7);

        searchJButton.setToolTipText("Search for nearby devices");
        searchJButton.setFocusPainted(false);
        searchJButton.setMaximumSize(new java.awt.Dimension(24, 24));
        searchJButton.setMinimumSize(new java.awt.Dimension(24, 24));
        searchJButton.setPreferredSize(new java.awt.Dimension(24, 24));
        searchJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchJButtonActionPerformed(evt);
            }
        });
        buttonUpperJPanel.add(searchJButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainJScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
                    .addComponent(buttonLowerJPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonUpperJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonUpperJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mainJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonLowerJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void deviceJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deviceJButtonActionPerformed

        // Setting up our timestamps for the text area.
        final StringBuilder builder = new StringBuilder();

        // Change the icon back to the off version
        deviceJButton.setIcon(iconBluetoothOff);

        //-------------------------------------------------------------- Close previous interactions if any--------------------------------------------------\\
        if (bluetoothDevice != null) {

            // Attempt to close the previous connection if you clicked while another connection was active.
            try {

                // Closing them up.
                input.close();
                output.close();
                streamConnection.close();
            } catch (IOException ioe) {
                Logger.getLogger(BluetoothJDialog.class.getName()).log(Level.SEVERE, ioe.getMessage());
            }

            // Close the connection if there is an active on.
            bluetoothDevice.closeConnection();
            bluetoothDevice = null;

            // Kick out.
            return;
        }

        //
        final String selectedDevice = String.valueOf(deviceJComboBox.getSelectedItem());

        // If the value is a url address then get the stream connection directly.
//        if (selectedDevice.startsWith("btspp")) {
//
//            // Parse the address from the url
//            final int index1 = selectedDevice.indexOf("://") + 3;
//            final int index2 = selectedDevice.indexOf(";", index1);
//
//            //
//            bluetoothDevice = new BluetoothDevice(selectedDevice, selectedDevice.substring(index1, index2), "unknown");
//        } else {

            //-------------------------------------------------------- Setting up prior to interaction with device --------------------------------------------------\\
            // Will take a while
            bluetoothDevice = deviceManager.connectToDevice(selectedDevice);
        //}

        //
        // The device should then supply a stream connection.
        streamConnection = bluetoothDevice.getConnection();

        // Show message in text area
        if (streamConnection == null) {

            //
            mainJTextArea.append("Failed to connect to device.\n");

            // Kick out
            return;
        }

        //
        try {

            // Open up input and output streams from the connection
            input = streamConnection.openInputStream();
            output = streamConnection.openOutputStream();

            // If we failed to open either show a message and kick out.
            if (input == null || output == null) {

                //
                mainJTextArea.append("Failed to open input / output streams with service.\n");

                // Kick out
                return;
            }

            // Send output to the text area
            mainJTextArea.append("Stream connection opened with device: " + bluetoothDevice.getAddress() + "\n");

            // Change the icon
            deviceJButton.setIcon(iconBluetoothOn);
            urlJField.setText(bluetoothDevice.getURL());
            urlJField.setToolTipText(urlJField.getText());

            // Special for my HC05
            if (bluetoothDevice.getAddress().contains("98D331FB726A")) {

                // Ask the BluetoothDeviceManager to INIT. We're not in AT Mode we're using the GardenReporter sketch.
                final String initString = "GR_INIT:" + format.format(new Date()).replaceAll("/", ":");
                output.write(initString.getBytes());

                // Show the user we sent a command to the device.
                mainJTextArea.append("-> " + initString + " \n");
            }

            // Change the title to the address of the device
            setTitle("Terminal connected to: " + bluetoothDevice.getAddress() + " (" + bluetoothDevice.getFriendlyName() + ")");

            // Create a thread to handle the server connection because it could cause a long hold on other operations
            final Thread thread;

            //
            thread = new Thread() {
                @Override
                public void run() {

                    // The character as a byte read in from the stream
                    char read;

                    // We need a separate try-catch for IOException inside the thread.
                    try {

                        // While the input stream has bytes to send
                        while ((read = (char) input.read()) != -1) {

                            // Add on to the String builder string the numerical ascii code byte sent over.
                            builder.append(read);

                            // Our Input from GardenReporter ends with '\n' (ASCII:10) and the hc05 will automatically append the \n
                            if (read == 10) {

                                //
                                final String report = builder.toString();

                                // Grab a timestamp.
                                final Date date = new Date();
                                final String timestamp = format.format(date).replaceAll("/", ":");

                                // We've validated the database and table we're sending data to from the device
                                if (ready) {

                                    // If we're ready to receive data then report to the text area.
                                    mainJTextArea.append("[" + timestamp + "]: " + report);

                                    //  Depends if it's either of these commands.
                                    if (report.equals("GR_RA\r\n") || report.startsWith("GR_AUTO")) {

                                        // Report to the database
                                        reportToDatabase(report);
                                    }
                                } else if (report.startsWith("GR_INIT")) {

                                    // We sent a GR_INIT command and the BluetoothDeviceManager will echo the command with the schema and table it wants.
                                    try {

                                        // Question the report string to make sure its GR_INIT:<database>:<table>
                                        final String[] split = report.split(":");

                                        // Info we're looking for.
                                        database = split[1];
                                        table = split[2];

                                        // We've been validated.
                                        ready = true;

                                        // Display how many reports we dumped
                                        mainJTextArea.append("[" + timestamp + "]: Dumped " + reportDump.size() + " reports.\n");
                                    } catch (ArrayIndexOutOfBoundsException aioobe) {

                                        //
                                        ready = false;
                                        Logger.getLogger(BluetoothJDialog.class.getName()).log(Level.SEVERE, aioobe.getMessage());
                                    }
                                } else if (report.startsWith("GR_AUTO")) {

                                    // If we're not ready and we get a GR_AUTO commmand then send it to the report dump
                                    reportDump.add(report);
                                } else {

                                    // Anything else is syntax error info or command replies.
                                    mainJTextArea.append("[" + timestamp + "]: " + report);
                                }

                                // Reset the string builder.
                                builder.setLength(0);
                            }
                        }
                    } catch (IOException innerIOE) {

                        // 
                        mainJTextArea.append("Failed to connect to device.\n");

                        //
                        Logger.getLogger(BluetoothJDialog.class.getName()).log(Level.SEVERE, innerIOE.getMessage());
                    }
                }
            };

            // Start that puppy up. Not that a puppy is an object that can be started; they are kinda just alive and stuff.
            thread.start();
        } catch (IOException ioe) {

            // Log dem errors baby boy.
            Logger.getLogger(BluetoothJDialog.class.getName()).log(Level.SEVERE, ioe.getMessage());
        }
    }//GEN-LAST:event_deviceJButtonActionPerformed

    private void statementJFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statementJFieldActionPerformed
        // TODO add your handling code here:
        try {

            // Write to the output stream
            if (output != null) {

                // Write to the device and the response will appear in the mainJTextArea from the thread.
                output.write(statementJField.getText().getBytes());

                //
                mainJTextArea.append("-> " + statementJField.getText() + "\n");

                // Now clear it
                statementJField.setText("");
            }
        } catch (IOException ioe) {

            //
            Logger.getLogger(BluetoothJDialog.class.getName()).log(Level.SEVERE, ioe.getMessage());
        }
    }//GEN-LAST:event_statementJFieldActionPerformed

    private void statementJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statementJButtonActionPerformed

        //
        if (statementJField.getText() == null || statementJField.getText().equals("")) {
            return;
        }

        // TODO add your handling code here:
        try {

            // Write to the output stream
            if (output != null) {

                //
                output.write(statementJField.getText().getBytes());

                //
                mainJTextArea.append(statementJField.getText() + "\n");

                // Now clear it
                statementJField.setText("");
            }
        } catch (IOException ioe) {

            //
            Logger.getLogger(BluetoothJDialog.class.getName()).log(Level.SEVERE, ioe.getMessage());
        }
    }//GEN-LAST:event_statementJButtonActionPerformed

    private void logJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logJButtonActionPerformed

        // TODO add your handling code here:
        createLogFile();
    }//GEN-LAST:event_logJButtonActionPerformed

    private void clearJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearJButtonActionPerformed

        // TODO add your handling code here:
        mainJTextArea.setText("");
    }//GEN-LAST:event_clearJButtonActionPerformed

    private void deviceJComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deviceJComboBoxActionPerformed

        //
        if (bluetoothDevice != null) {
            urlJField.setText(bluetoothDevice.getURL());
        }
    }//GEN-LAST:event_deviceJComboBoxActionPerformed

    private void searchJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchJButtonActionPerformed

        //
        if (searchThread != null) {

            // Allow an interruption
            if (searchThread.isAlive()) {

                //
                searchThread.interrupt();

                // Change the icon back
                searchJButton.setIcon(iconBluetoothSearchOff);

                // Kick out
                return;
            }
        }

        // Otherwise the icon should be blue
        searchJButton.setIcon(iconBluetoothSearchOn);

        // Let's do this in a thread so we're not hung up
        searchThread = new Thread() {
            @Override
            public void run() {

                // Display that we're invoking a long operation
                mainJTextArea.append("-> Locating nearby devices. This may take a while.\n");

                //
                deviceManager.locateDevices();

                // Setting up the combo boxes
                final DefaultComboBoxModel model = new DefaultComboBoxModel();

                //
                for (RemoteDevice device : deviceManager.getFoundDevices()) {

                    //
                    model.addElement(device.getBluetoothAddress());
                }

                //
                deviceJComboBox.setModel(model);

                //
                searchJButton.setIcon(iconBluetoothSearchFinished);
                mainJTextArea.append("-> Device location process finished.\n");

                // Enable the button
                deviceJButton.setEnabled(true);
                statementJButton.setEnabled(true);
                logJButton.setEnabled(true);
                clearJButton.setEnabled(true);
                deviceJComboBox.setEnabled(true);
            }
        };

        // Start the process.
        searchThread.start();
    }//GEN-LAST:event_searchJButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonLowerJPanel;
    private javax.swing.JPanel buttonUpperJPanel;
    private javax.swing.JButton clearJButton;
    private javax.swing.JButton deviceJButton;
    private javax.swing.JComboBox<String> deviceJComboBox;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton logJButton;
    private javax.swing.JScrollPane mainJScrollPane;
    private javax.swing.JTextArea mainJTextArea;
    private javax.swing.JButton searchJButton;
    private javax.swing.JComboBox<String> serviceJComboBox;
    private javax.swing.JButton statementJButton;
    private javax.swing.JTextField statementJField;
    private javax.swing.JTextField urlJField;
    // End of variables declaration//GEN-END:variables

    @Override
    public void windowOpened(WindowEvent e) {
        //
    }

    @Override
    public void windowClosing(WindowEvent e) {
        //
    }

    @Override
    public void windowClosed(WindowEvent e) {

        // The stream connection must exist
        if (streamConnection != null) {

            //
            try {

                // Close those inputs.
                input.close();
                output.close();
                streamConnection.close();
            } catch (IOException ioe) {

                //
                Logger.getLogger(BluetoothJDialog.class.getName()).log(Level.SEVERE, ioe.getMessage());
            }
        }

        //
        //
        frame.setDeviceIconOff();
    }

    @Override
    public void windowIconified(WindowEvent e) {
        //
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        //
    }

    @Override
    public void windowActivated(WindowEvent e) {
        //
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        //
    }
}
