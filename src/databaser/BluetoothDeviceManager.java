package databaser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rcher
 */
public class BluetoothDeviceManager implements DiscoveryListener {

    // -> Variable Declaration
    // Java Native Classes
    private final ArrayList<RemoteDevice> remoteDevices;
    private final HashMap<RemoteDevice, ServiceRecord> deviceServiceMap;
    // Data Types
    private final Object lock = new Object();
    // <- End of Variable Declaration

    public BluetoothDeviceManager() {

        //
        deviceServiceMap = new HashMap();
        remoteDevices = new ArrayList();
    }

    public void locateDevices() {
        
        // Clear previous entries if any
        remoteDevices.clear();
        deviceServiceMap.clear();
        
        //
        try {

            //
            final LocalDevice localDevice = LocalDevice.getLocalDevice();
            final DiscoveryAgent agent = localDevice.getDiscoveryAgent();

            // Add pre-cached devices.
            final RemoteDevice[] cached = agent.retrieveDevices(DiscoveryAgent.PREKNOWN);

            // If we found any pre-cached devices at all.
            if (cached != null) {

                // Adding all precached devices in range.
                remoteDevices.addAll(Arrays.asList(cached));
            }

            // Once we have the local device and the agent. Search for devices and wait.
            synchronized (lock) {

                // Start the inquiry to discovery generally available devices. -> inquiryEvent will need to notify this thread to wake.
                agent.startInquiry(DiscoveryAgent.GIAC, this);

                //
                lock.wait();
            }

            // For every remote device that we found, search for services for it.
            // In the future we'll place as an array of services and make the user pick.
            for (RemoteDevice device : remoteDevices) {

                // Wait for its services to appear
                synchronized (lock) {

                    // Searching for the services as uuids 0x0003 is the RFComm service.
                    agent.searchServices(new int[]{0x0100}, new UUID[]{new UUID(0x0003)}, device, this);

                    lock.wait();
                }
            }

            // We're done we have the service records we need to open a url.
        } catch (InterruptedException | IOException io) {

            // Log that error.
            Logger.getLogger(BluetoothDeviceManager.class.getName()).log(Level.SEVERE, io.getMessage());
        }
    }

    public BluetoothDevice connectToDevice(final String address) {

        // Now go over those service records
        for (Map.Entry<RemoteDevice, ServiceRecord> recs : deviceServiceMap.entrySet()) {

            //
            RemoteDevice device = recs.getKey();

            //
            if (device.getBluetoothAddress().equals(address)) {

                // MY BluetoothDeviceManager is not using an encrpyed connection and does not need to be authenticated.
                final String url = recs.getValue().getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);

                // Now attmept to open the connection to the url.
                try {

                    //
                    return new BluetoothDevice(url, address, device.getFriendlyName(false));
                } catch (IOException ioe) {

                    // Log that error
                    Logger.getLogger(BluetoothDeviceManager.class.getName()).log(Level.SEVERE, ioe.getMessage());
                }
            }
        }

        //
        return null;
    }

    @Override
    public void deviceDiscovered(RemoteDevice foundDevice, DeviceClass dc) {

        //
        boolean found = false;

        //
        for (RemoteDevice device : remoteDevices) {

            // If the device is already listed.
            if (foundDevice.getBluetoothAddress().equalsIgnoreCase(device.getBluetoothAddress())) {
                found = true;
            }
        }

        // So the new device we discovered wasnt apart of the cached list comprised at the start so this device is brand spanking new.
        if (!found) {

            //
            remoteDevices.add(foundDevice);
        }
    }

    @Override
    public void servicesDiscovered(int i, ServiceRecord[] srs) {

        // Will report NEW services discovered by cached, preknown, or new devices. Not old services that the os is already aware of.
        for (ServiceRecord record : srs) {

            // Map the service record found to the host device.
            deviceServiceMap.put(record.getHostDevice(), record);
        }
    }

    @Override
    public void serviceSearchCompleted(int i, int i1) {

        // Apparently once we're done we just lift the lock which kinda makes sense to me
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    @Override
    public void inquiryCompleted(int i) {

        // Lift the lock so the program will continue.
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public RemoteDevice getBluetoothDevice(String address) {

        //
        for (RemoteDevice device : remoteDevices) {

            //
            if (device.getBluetoothAddress().equals(address)) {

                // Return the device found.
                return device;
            }
        }

        //
        return null;
    }

    public ArrayList<RemoteDevice> getFoundDevices() {
        return remoteDevices;
    }

    public HashMap<RemoteDevice, ServiceRecord> getServiceRecords() {
        return deviceServiceMap;
    }
}
