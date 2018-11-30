package broker.gui;

import broker.model.BankInterestReply;
import broker.model.LoanRequest;

import javax.jms.Message;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class BrokerWindow extends JFrame{

    private BrokerController brokerController;

    protected static DefaultListModel listmodel = new DefaultListModel();
    protected static JList lv_msg = new JList(listmodel);

    //Define GUI Layout & initiate GUI
    public BrokerWindow(BrokerController _brokerController) {
        super("loan-broker");

        this.brokerController = _brokerController;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(600, 300);
        setResizable(false);
        this.setLayout(null);

        Container pane = this.getContentPane();

        lv_msg.setBounds(15, 10, 570, 260);

        pane.add(lv_msg);
        setVisible(true);
    }

    public void onReceiveRequest(LoanRequest request) {
        listmodel.addElement(request + " || " + "waiting...");
    }

    public void onReceiveReply(BankInterestReply reply, int index) {
        String archive = (String) listmodel.getElementAt(index);
        String originalMsg = archive.substring(0, archive.indexOf(" || "));

        listmodel.setElementAt(originalMsg + " || " + reply.toString(), index);
    }


}
