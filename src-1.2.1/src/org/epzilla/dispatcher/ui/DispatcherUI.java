package org.epzilla.dispatcher.ui;

import org.epzilla.dispatcher.DispatcherRegister;
import org.epzilla.dispatcher.controlers.DispatcherUIController;
import org.epzilla.dispatcher.xml.ServerSettingsReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class DispatcherUI extends JFrame implements ActionListener {
    private JTabbedPane tabbedPane = null;
    private JTextField txtIP = null;
    private JTextField txtPort = null;
    private JTextField txtNameServer = null;
    private JLabel labelIP = null;
    private JLabel labelPort = null;
    private JLabel labelName = null;
    private JButton btnRegister = null;
    private JButton btnLoadSettings = null;
    private JLabel lblDetails = null;
    private JMenuBar menuBar = null;
    private JMenuItem about = null;
    private JMenuItem help = null;
    private JMenuItem adminSettings = null;
    private JMenuItem closetabs = null;
    private JMenuItem exit = null;
    private JMenu file = null;
    private JMenu helpmenu = null;
    private JPanel mainSettings = null;
    private JPanel summary = null;
    private JPanel clusterDe = null;
    private JTextArea txtTriggers = null;
    private JLabel lblDisp = null;
    private JLabel lblName = null;
    private JTextField txtDispSerName = null;
    private JTextArea txtStatus = null;
    private JLabel lblTriggers = null;
    private JLabel lblEvents = null;
    private JLabel lblIPs = null;
    private JTextArea txtIPs = null;
    private JScrollPane statusScrollPane = null;
    private JScrollPane triggerListScrollPane = null;
    private JScrollPane ipScrollPane = null;
    private JScrollPane resultScrollPane = null;
    private JScrollPane discoveryStatusPane = null;
    private JLabel lblInEC = null;
    private JTextField txtInEventCount = null;
    private JLabel lblStatus = null;
    private JTextArea txtResult = null;
    private boolean isRegister = false;
    private JLabel lblOutEC = null;
    private JTextField txtOutEventCount = null;
    private JLabel lblDiscoveryStatus = null;
    private JTextArea txtDiscoveryStatus = null;
    private JLabel lblClusterIPs = null;
    private JTextArea txtClusterIPs = null;

    public DispatcherUI() {
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException e) {
        }
        catch (ClassNotFoundException e) {
        }
        catch (InstantiationException e) {
        }
        catch (IllegalAccessException e) {
        }
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screen.width;
        int y = screen.height;
        this.setTitle("Dispatcher");
        this.setBackground(SystemColor.control);
        Image img = Toolkit.getDefaultToolkit().getImage("images//logo.jpg");
        this.setIconImage(img);
        this.setSize(x, y);
        this.setPreferredSize(new Dimension(1024, 768));
        this.setContentPane(getMyTabbedPane());
        this.setJMenuBar(getmyMenuBar());
        loadSettings();
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    dispose();
                    System.exit(0);
                }
            }
        });
    }

    private JTabbedPane getMyTabbedPane() {
        if (tabbedPane == null) {
            ImageIcon settingsIcon = new ImageIcon("images//settings.jpg");
            ImageIcon summaryIcon = new ImageIcon("images//summary.jpg");
            ImageIcon clusterDeIcon = new ImageIcon("images//clusterDe.jpg");

            lblDetails = new JLabel();
            lblDetails.setText("NameServer Details ");
            lblDetails.setLocation(new Point(15, 8));
            lblDetails.setFont(new Font("Dialog", Font.BOLD, 12));
            lblDetails.setSize(new Dimension(151, 25));
            labelName = new JLabel();
            labelName.setText("Service Name :");
            labelName.setSize(new Dimension(93, 25));
            labelName.setLocation(new Point(30, 92));
            labelPort = new JLabel();
            labelPort.setText("Port :");
            labelPort.setSize(new Dimension(41, 22));
            labelPort.setLocation(new Point(30, 132));
            labelIP = new JLabel();
            labelIP.setText("Server IP Address :");
            labelIP.setSize(new Dimension(121, 25));
            labelIP.setLocation(new Point(30, 49));

            tabbedPane = new JTabbedPane();
            tabbedPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            tabbedPane.addTab("Summary", summaryIcon, getSummeryTab());
            tabbedPane.addTab("Cluster Details", clusterDeIcon, getClusterDeTab());
            tabbedPane.addTab("Settings", settingsIcon, getMainSettings());
            tabbedPane.setVisible(true);
        }
        return tabbedPane;
    }

    private JPanel getMainSettings() {
        if (mainSettings == null) {

            lblStatus = new JLabel();
            lblStatus.setBounds(new Rectangle(15, 340, 71, 23));
            lblStatus.setText("Status :");
            lblName = new JLabel();
            lblName.setText("Service Name :");
            lblName.setLocation(new Point(30, 203));
            lblName.setSize(new Dimension(94, 16));
            lblDisp = new JLabel();
            lblDisp.setText("Dispatcher Details ");
            lblDisp.setLocation(new Point(15, 171));
            lblDisp.setFont(new Font("Dialog", Font.BOLD, 12));
            lblDisp.setSize(new Dimension(175, 22));
            mainSettings = new JPanel();
            mainSettings.setLayout(null);
            mainSettings.add(getTxtNSIp(), null);
            mainSettings.add(getTxtPort(), null);
            mainSettings.add(getTxtNameServer(), null);
            mainSettings.add(labelIP, null);
            mainSettings.add(labelPort, null);
            mainSettings.add(labelName, null);
            mainSettings.add(getBtnRegister(), null);
            mainSettings.add(getBtnLoadSetings(), null);
            mainSettings.add(lblDetails, null);
            mainSettings.add(lblDisp, null);
            mainSettings.add(lblName, null);
            mainSettings.add(getTbDispSerName(), null);
            mainSettings.add(lblStatus, null);
            mainSettings.add(getResultPane(), null);
        }
        return mainSettings;
    }

    private JPanel getSummeryTab() {
        if (summary == null) {
            lblOutEC = new JLabel();
            lblOutEC.setBounds(new Rectangle(220, 553, 138, 22));
            lblOutEC.setText("Outgoing Event Count :");
            lblInEC = new JLabel();
            lblInEC.setBounds(new Rectangle(15, 553, 140, 22));
            lblInEC.setText("Incoming Event Count :");
            lblIPs = new JLabel();
            lblIPs.setBounds(new Rectangle(720, 17, 38, 16));
            lblIPs.setText("IP :");
            lblEvents = new JLabel();
            lblEvents.setBounds(new Rectangle(15, 21, 117, 16));
            lblEvents.setText("Status :");
            lblTriggers = new JLabel();
            lblTriggers.setBounds(new Rectangle(15, 313, 116, 16));
            lblTriggers.setText("Trigger List :");
            summary = new JPanel();
            summary.setLayout(null);
            summary.add(getTriggerScrollPane(), null);
            summary.add(getStatusScrollPane(), null);
            summary.add(getIpScrollPane(), null);
            summary.add(lblTriggers, null);
            summary.add(lblEvents, null);
            summary.add(lblIPs, null);
            summary.add(lblInEC, null);
            summary.add(getTxtInEventCount(), null);
            summary.add(lblOutEC, null);
            summary.add(getTxtOutEventCount(), null);
        }
        return summary;
    }

    private JPanel getClusterDeTab() {
        if (clusterDe == null) {
            lblDiscoveryStatus = new JLabel();
            lblDiscoveryStatus.setBounds(new Rectangle(10, 10, 200, 25));
            lblDiscoveryStatus.setText("Dispatcher Discovery Status :");

            lblClusterIPs = new JLabel();
            lblClusterIPs.setBounds(new Rectangle(650, 10, 150, 25));
            lblClusterIPs.setText("Cluster IPs :");

            clusterDe = new JPanel();
            clusterDe.setLayout(null);
            clusterDe.add(lblDiscoveryStatus, null);
            clusterDe.add(getDiscoveryStaPane(), null);
            clusterDe.add(lblClusterIPs, null);
            clusterDe.add(getClusterIPs(), null);
        }
        return clusterDe;
    }

    private JMenuBar getmyMenuBar() {
        if (menuBar == null) {
            menuBar = new JMenuBar();
            menuBar.setBackground(SystemColor.control);
            file = new JMenu("File");
            helpmenu = new JMenu("Help");
            file.add(getAdminSettingMI());
            file.add(getCloseMI());
            file.add(getExitMI());
            helpmenu.add(getAboutMI());
            menuBar.add(file);
            menuBar.add(helpmenu);
        }
        return menuBar;
    }

    private JMenuItem getAboutMI() {
        if (about == null) {
            about = new JMenuItem();
            about.setText("About");
            about.addActionListener(this);
        }
        return about;
    }

    private JMenuItem getAdminSettingMI() {
        if (adminSettings == null) {
            ImageIcon settingsIcon = new ImageIcon("images//settings.jpg");
            adminSettings = new JMenuItem(settingsIcon);
            adminSettings.setText("Administrator Settings");
            adminSettings.addActionListener(this);
        }
        return adminSettings;
    }

    private JMenuItem getExitMI() {
        if (exit == null) {
            exit = new JMenuItem();
            exit.setText("Exit");
            exit.addActionListener(this);
        }
        return exit;
    }

    private JMenuItem getCloseMI() {
        if (closetabs == null) {
            ImageIcon closeIcon = new ImageIcon("images//close.jpg");
            closetabs = new JMenuItem(closeIcon);
            closetabs.setText("Close Tabs");
            closetabs.addActionListener(this);
        }
        return closetabs;
    }

    private JTextField getTxtNSIp() {
        if (txtIP == null) {
            txtIP = new JTextField();
            txtIP.setLocation(new Point(150, 51));
            txtIP.setSize(new Dimension(200, 20));
            txtIP.setBackground(Color.black);
            txtIP.setForeground(Color.green);
            txtIP.setEditable(false);
        }
        return txtIP;
    }

    private JTextField getTxtPort() {
        if (txtPort == null) {
            txtPort = new JTextField();
            txtPort.setLocation(new Point(150, 135));
            txtPort.setSize(new Dimension(200, 20));
            txtPort.setBackground(Color.black);
            txtPort.setForeground(Color.green);
            txtPort.setEditable(false);
        }
        return txtPort;
    }

    private JTextField getTxtNameServer() {
        if (txtNameServer == null) {
            txtNameServer = new JTextField();
            txtNameServer.setLocation(new Point(150, 95));
            txtNameServer.setSize(new Dimension(200, 20));
            txtNameServer.setBackground(Color.black);
            txtNameServer.setForeground(Color.green);
            txtNameServer.setEditable(false);
        }
        return txtNameServer;
    }

    private JButton getBtnRegister() {
        if (btnRegister == null) {
            ImageIcon registerIcon = new ImageIcon("images//register.jpg");
            btnRegister = new JButton(registerIcon);
            btnRegister.setLocation(new Point(150, 250));
            btnRegister.setText("Register");
            btnRegister.setSize(new Dimension(97, 20));
            btnRegister.addActionListener(this);
        }
        return btnRegister;
    }

    private JButton getBtnLoadSetings() {
        if (btnLoadSettings == null) {
            ImageIcon reloadIcon = new ImageIcon("images//reload.jpg");
            btnLoadSettings = new JButton(reloadIcon);
            btnLoadSettings.setLocation(255, 250);
            btnLoadSettings.setText("Reload");
            btnLoadSettings.setSize(new Dimension(97, 20));
            btnLoadSettings.addActionListener(this);
        }
        return btnLoadSettings;
    }

    private JScrollPane getTriggerScrollPane() {
        if (triggerListScrollPane == null) {
            triggerListScrollPane = new JScrollPane();
            triggerListScrollPane.setBounds(new Rectangle(13, 337, 588, 173));
            triggerListScrollPane.setViewportView(getTxtTriggers());
        }
        return triggerListScrollPane;
    }

    public JTextArea getTxtTriggers() {
        if (txtTriggers == null) {
            txtTriggers = new JTextArea();
            txtTriggers.setBounds(new Rectangle(15, 337, 588, 173));
            txtTriggers.setForeground(Color.green);
            txtTriggers.setEditable(false);
            txtTriggers.setBackground(Color.black);
        }
        return txtTriggers;
    }

    private JTextField getTbDispSerName() {
        if (txtDispSerName == null) {
            txtDispSerName = new JTextField();
            txtDispSerName.setSize(new Dimension(200, 20));
            txtDispSerName.setText("DISPATCHER_SERVICE");
            txtDispSerName.setLocation(new Point(150, 202));
            txtDispSerName.setBackground(Color.black);
            txtDispSerName.setForeground(Color.green);
            txtDispSerName.setEditable(false);
        }
        return txtDispSerName;
    }

    private JScrollPane getStatusScrollPane() {
        if (statusScrollPane == null) {
            statusScrollPane = new JScrollPane();
            statusScrollPane.setBounds(new Rectangle(15, 47, 587, 177));
            statusScrollPane.setViewportView(getTxtStatus());
        }
        return statusScrollPane;
    }

    public JTextArea getTxtStatus() {
        if (txtStatus == null) {
            txtStatus = new JTextArea();
            txtStatus.setBounds(new Rectangle(15, 47, 587, 177));
            txtStatus.setForeground(Color.green);
            txtStatus.setEditable(false);
            txtStatus.setBackground(Color.black);
        }
        return txtStatus;
    }

    private JScrollPane getIpScrollPane() {
        if (ipScrollPane == null) {
            ipScrollPane = new JScrollPane();
            ipScrollPane.setBounds(new Rectangle(720, 44, 270, 465));
            ipScrollPane.setViewportView(getTxtIPSet());
        }
        return ipScrollPane;
    }

    public JTextArea getTxtIPSet() {
        if (txtIPs == null) {
            txtIPs = new JTextArea();
            txtIPs.setBounds(new Rectangle(720, 44, 270, 400));
            txtIPs.setForeground(Color.green);
            txtIPs.setEditable(false);
            txtIPs.setBackground(Color.black);
        }
        return txtIPs;
    }

    public JTextField getTxtInEventCount() {
        if (txtInEventCount == null) {
            txtInEventCount = new JTextField();
            txtInEventCount.setBounds(new Rectangle(15, 581, 154, 30));
            txtInEventCount.setBackground(Color.black);
            txtInEventCount.setEditable(false);
            txtInEventCount.setForeground(Color.green);
        }
        return txtInEventCount;
    }

    public JTextField getTxtOutEventCount() {
        if (txtOutEventCount == null) {
            txtOutEventCount = new JTextField();
            txtOutEventCount.setBounds(new Rectangle(220, 581, 154, 30));
            txtOutEventCount.setForeground(Color.green);
            txtOutEventCount.setEditable(false);
            txtOutEventCount.setBackground(Color.black);
        }
        return txtOutEventCount;
    }

    private JScrollPane getResultPane() {
        if (resultScrollPane == null) {
            resultScrollPane = new JScrollPane();
            resultScrollPane.setBounds(new Rectangle(15, 381, 600, 250));
            resultScrollPane.setViewportView(getTxtResult());
        }
        return resultScrollPane;
    }

    public JTextArea getTxtResult() {
        if (txtResult == null) {
            txtResult = new JTextArea();
            txtResult.setBounds(new Rectangle(15, 381, 600, 250));
            txtResult.setBackground(Color.black);
            txtResult.setForeground(Color.green);
            txtResult.setEditable(false);
        }
        return txtResult;
    }

    public JTextArea getTxtDiscoveryStatus() {
        if (txtDiscoveryStatus == null) {
            txtDiscoveryStatus = new JTextArea();
            txtDiscoveryStatus.setBounds(new Rectangle(10, 35, 400, 500));
            txtDiscoveryStatus.setForeground(Color.green);
            txtDiscoveryStatus.setBackground(Color.black);
        }
        return txtDiscoveryStatus;
    }

    private JScrollPane getDiscoveryStaPane() {
        if (discoveryStatusPane == null) {
            discoveryStatusPane = new JScrollPane();
            discoveryStatusPane.setBounds(new Rectangle(10, 35, 400, 500));
            discoveryStatusPane.setViewportView(getTxtDiscoveryStatus());
        }
        return discoveryStatusPane;
    }

    public JTextArea getClusterIPs() {
        if (txtClusterIPs == null) {
            txtClusterIPs = new JTextArea();
            txtClusterIPs.setBounds(new Rectangle(650, 35, 200, 500));
            txtClusterIPs.setBackground(Color.black);
            txtClusterIPs.setForeground(Color.green);
            txtClusterIPs.setEditable(false);
        }
        return txtClusterIPs;
    }

    public void register() throws MalformedURLException, RemoteException, UnknownHostException, NotBoundException {
        String ip = txtIP.getText().toString();
        String nameService = txtNameServer.getText().toString();
        String dispatcherName = txtDispSerName.getText().toString();
        String port = txtPort.getText().toString();
        if (isValidIp(ip) == false) {
            JOptionPane.showMessageDialog(null, "Enter valid IP Address of NameServer.", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (isValidPort(port) == false) {
            JOptionPane.showMessageDialog(null, "Enter valid Port number", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (nameService.length() != 0 && dispatcherName.length() != 0) {
            DispatcherRegister.register(ip, nameService, port, dispatcherName);
            isRegister = true;
            btnRegister.setEnabled(false);
            btnLoadSettings.setEnabled(false);

        } else {
            JOptionPane.showMessageDialog(null, "Dispatcher registration fails. Enter setting details correctly.", "Message", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidPort(String port) {
        boolean returnValue = true;
        if (port.length() != 0) {
            try {
                int num = Integer.parseInt(port);
                if (num < 1) {
                    returnValue = false;
                } else if (num > 65000) {
                    returnValue = false;
                }
            } catch (NumberFormatException e) {
                returnValue = false;
            }
        } else
            returnValue = false;

        return returnValue;

    }

    private static boolean isValidIp(final String ip) {
        boolean format = ip.matches("^[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}$");
        if (format) {
            boolean validIp = true;
            String[] values = ip.split("\\.");
            for (int k = 0; k < values.length; ++k) {
                short v = Short.valueOf(values[k]).shortValue();
                if ((v < 0) || (v > 255)) {
                    validIp = false;
                    break;
                }
            }
            return validIp;
        }
        return false;
    }

    public void showAbout() {
        About abut = new About();
        abut.setVisible(true);
    }

    private void loadSettings() {
        try {
            ArrayList<String[]> data = ServerSettingsReader.getServerIPSettings("./src/settings/server_settings.xml");
            String[] ar = data.get(0);
            txtIP.setText(ar[0]);
            txtPort.setText(ar[1]);
            txtNameServer.setText(ar[2]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void systemExit() {
        int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == 0)
            System.exit(0);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == adminSettings) {
            tabbedPane.setVisible(true);
            tabbedPane.setFocusable(false);
        } else if (source == help) {

        } else if (source == exit) {
            systemExit();
        } else if (source == about) {
            showAbout();
        } else if (source == closetabs) {
            tabbedPane.setVisible(false);
        } else if (source == btnLoadSettings) {
            loadSettings();
        } else if (source == btnRegister) {
            try {
                if (isRegister == false) {
                    register();
                } else
                    JOptionPane.showMessageDialog(null, "Dispatcher already registered", "Message", JOptionPane.INFORMATION_MESSAGE);
            } catch (MalformedURLException e) {
                JOptionPane.showMessageDialog(null, e, "Message", JOptionPane.ERROR_MESSAGE);
            } catch (RemoteException e) {
                DispatcherUIController.appendResults("Name Server not working...");
            } catch (UnknownHostException e) {
                JOptionPane.showMessageDialog(null, e, "Message", JOptionPane.ERROR_MESSAGE);
            } catch (NotBoundException e) {
                JOptionPane.showMessageDialog(null, e, "Message", JOptionPane.ERROR_MESSAGE);
            }

        }
    }
}