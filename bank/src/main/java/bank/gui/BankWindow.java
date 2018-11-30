package bank.gui;

import bank.model.BankInterestReply;
import bank.model.BankInterestRequest;

import javax.jms.Message;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class BankWindow extends JFrame {
    private BankController bankController;

    private JLabel text_reply = new JLabel("type reply");
    private JTextField tf_reply = new JTextField();
    private JButton btn_send = new JButton("send reply");

    protected static DefaultListModel listmodel = new DefaultListModel();
    protected static JList lv_msg = new JList(listmodel);

    //Define GUI Layout & initiate GUI
    public BankWindow(BankController _bankController) {
        super("JMS Bank - ABN AMRO");

        this.bankController = _bankController;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(600, 300);
        setResizable(false);
        this.setLayout(null);

        Container pane = this.getContentPane();

        lv_msg.setBounds(15, 10, 570, 225);
        text_reply.setBounds(65, 240, 75, 30);
        tf_reply.setBounds(155, 240, 225, 30);
        btn_send.setBounds(400, 240, 150, 30);

        pane.add(lv_msg);
        pane.add(text_reply);
        pane.add(tf_reply);
        pane.add(btn_send);

        btn_send.addActionListener(this::actionPerformed);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (!e.getSource().equals(btn_send)) {
            return;
        } else {
            if((tf_reply.getText() != "") && (lv_msg.getSelectedIndex() >= 0)) {
                BankInterestReply request = new BankInterestReply();
                request.setInterest(Double.parseDouble(tf_reply.getText()));
                request.setQuoteId("ABN");
                int index = lv_msg.getSelectedIndex();

                System.out.println("clintWindow:"+request);

                try {
                    bankController.send(request, index);

                    String archive = (String) listmodel.getElementAt(index);
                    String originalMsg = archive.substring(0, archive.indexOf(" || "));

                    listmodel.setElementAt(originalMsg + " || " + request.toString(), index);

                }catch (Exception e1) {
                    e1.printStackTrace();
                }

            } else {
                JOptionPane.showMessageDialog(this, "Please enter all input forms...");
            }
        }
    }


    public void onReceiveRequest(BankInterestRequest request) {
        listmodel.addElement(request.toString() + " || " + "waiting...");
    }





}
