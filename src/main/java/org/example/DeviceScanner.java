package org.example;

import com.fazecast.jSerialComm.SerialPort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DeviceScanner {

    public static void main(String[] args) {
        SerialPort[] ports = SerialPort.getCommPorts();
        processSerialPorts(ports);

        // Выберите конкретный порт для исследования файловой структуры (здесь используется второй порт)
        if (ports.length > 1) {
            SerialPort explorePort = ports[1];
            exploreDeviceFiles(explorePort);
        } else {
            System.out.println("Not enough serial ports available for exploration.");
        }
    }

    private static void processSerialPorts(SerialPort[] ports) {
        for (SerialPort port : ports) {
            displayPortInfo(port);
            processPort(port);
            System.out.println("=================================");
        }
    }

    private static void displayPortInfo(SerialPort port) {
        System.out.println("Port Name: " + port.getSystemPortName());
        System.out.println("Description: " + port.getPortDescription());
        System.out.println("Manufacturer: " + port.getDescriptivePortName());
    }

    private static void processPort(SerialPort port) {
        port.setComPortParameters(9600, 8, 1, SerialPort.NO_PARITY);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);

        if (port.openPort()) {
            processConnectedDevice(port);
            port.closePort();
        } else {
            System.out.println("Failed to open the port.");
        }
    }

    private static void processConnectedDevice(SerialPort port) {
        if (isMobileDevice(port)) {
            String deviceName = getDeviceName(port);
            String deviceCharacteristics = getDeviceCharacteristics(port);
            System.out.println("Connected device: " + deviceName);
            System.out.println("Characteristics: " + deviceCharacteristics);
        } else {
            System.out.println("No mobile device connected to the port.");
        }
    }

    private static boolean isMobileDevice(SerialPort port) {
        return port.getPortDescription().contains("Mobile");
    }

    private static String getDeviceName(SerialPort port) {
        return port.getPortDescription();
    }

    private static String getDeviceCharacteristics(SerialPort port) {
        return port.getDescriptivePortName();
    }

    private static void exploreDeviceFiles(SerialPort port) {
        String command = "adb shell ls -R /sdcard";
        String result = executeCommand(command);
        System.out.println("File structure on device:\n" + result);
    }

    private static String executeCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
