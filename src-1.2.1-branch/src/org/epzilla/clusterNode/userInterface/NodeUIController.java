package org.epzilla.clusterNode.userInterface;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Dishan
 * Date: Mar 9, 2010
 * Time: 4:09:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class NodeUIController {
    private static NodeUI instance;

    public static NodeUI getInstance() {
        return instance;
    }

    public static void setInstance(NodeUI instance) {
        NodeUIController.instance = instance;
    }

    public static void InitializeUI() {
        instance = new NodeUI();
        instance.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        instance.setVisible(true);
    }

    public static void setVisible(boolean status) {
        instance.setVisible(status);
    }


    public static void appendTextToStatus(String text) {
        instance.getJTextAreaStatus().append(text + "\n");
    }

    public static void appendTextToTriggerList(String text) {
        instance.getJTextAreaTriggers().append(text + "\n");
    }

    public static void appendTextToIPList(String text) {
        instance.getJTextAreaIPList().append(text + "\n");
    }

    public static String getIpList() {
        return instance.getJTextAreaIPList().getText();
    }

    public static void appendTextToNodeList(String text) {
        instance.getJTextAreaNodeList().append(text + "\n");
    }

    public static String getNodeList() {
        return instance.getJTextAreaNodeList().getText();
    }

    public static void clearNodeList() {
        instance.getJTextAreaNodeList().setText("");
    }

    public static void appendTextToMachineInfo(String text) {
        instance.getjTextAreaMachineInfo().append(text + "\n");
    }

    public static void appendTextToPerformanceInfo(String text) {
        instance.getjTextAreaPerformance().append(text + "\n");
    }

    public static void clearPerformanceInfo() {
        instance.getjTextAreaPerformance().setText("");
    }

    public static void setLeaderStatus(String text) {
        instance.getJTextAreaLeader().setText(text);
    }

    public static void setEventCount(String text) {
        instance.getJTextAreaEventCount().setText(text);
    }
    /*
    get node controler button
     */
    public static void getAddNodeButton(){
        instance.getAddNodeButton().setVisible(false);
    }
    public static void getRemoveNodeBtn(){
        instance.getRemoveNodeButton().setVisible(false);
    }
//    public static void main(String[] args){
//        InitializeUI();
//    }
}



