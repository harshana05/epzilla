package org.epzilla.clusterNode.nodeControler;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA.
 * User: Chathura
 * Date: Jun 10, 2010
 * Time: 7:48:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class WakeNode {
    
    public static void nodeInit(String ipAdrs, String macAdrs, int port) {
        try {
            byte[] bytes = getMacByBytes(macAdrs);
            byte[] byteArray = new byte[6 + 16 * bytes.length];
            for (int i = 0; i < 6; i++) {
                byteArray[i] = (byte) 0xff;
            }
            for (int i = 6; i < byteArray.length; i += bytes.length) {
                System.arraycopy(bytes, 0, byteArray, i, bytes.length);
            }

            InetAddress adrs = InetAddress.getByName(ipAdrs);
            DatagramPacket packet = new DatagramPacket(byteArray, byteArray.length, adrs, port);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();

            System.out.println("Wake node on LAN call");
        }
        catch (Exception e) {
            System.out.println("Failed to send wake the Node: " + e);
            System.exit(1);
        }
    }

    private static byte[] getMacByBytes(String macAdrs) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hexValue = macAdrs.split("(\\:|\\-)");
        if (hexValue.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hexValue[i], 16);
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex value");
        }
        return bytes;
    }
}
