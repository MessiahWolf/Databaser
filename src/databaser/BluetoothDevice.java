/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

/**
 *
 * @author rcher
 */
public class BluetoothDevice {

    // Variable Declaration
    private StreamConnection connection;
    private InputStream input;
    private OutputStream output;
    // Data Types
    private String url;
    // This is the physical address of my personal hc05, yours is different use the AT commands to find it.
    private String address;
    private String name;
    // End of Variable Declaration
    
    public BluetoothDevice(String url, String address, String name) {
        this. url = url;
        this.name = name;
        this.address = address;
        
        
        //
        try {
            connection = (StreamConnection) Connector.open(url);
        } catch (IOException ioe) {
            
        }
    }

    public void closeConnection() {

        // Now attmept to open the connection to the url.
        try {

            //
            if (input != null) {
                input.close();
            }

            //
            if (output != null) {
                output.close();
            }

            //
            if (connection != null) {
                connection.close();
            }
        } catch (IOException ioe) {

            // Log the error.
            Logger.getLogger(BluetoothDeviceManager.class.getName()).log(Level.SEVERE, ioe.getMessage());
        }
    }

    public String getURL() {
        return url;
    }

    public String getAddress() {
        return address;
    }

    public String getFriendlyName() {
        return name;
    }

    public StreamConnection getConnection() {
        return connection;
    }
}
