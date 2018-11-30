package client.gui;

import client.model.LoanReply;
import client.model.LoanRequest;

import javax.jms.Message;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ClientWindow extends JFrame {
    private ClientController clientController;

    private JLabel _text_ssn = new JLabel("ssn");
    private JLabel _text_amout = new JLabel("amount");
    private JLabel _text_time = new JLabel("time");

    private JTextField _tf_ssn = new JTextField();
    private JTextField _tf_amount = new JTextField();
    private JTextField _tf_time = new JTextField();

    private JButton _btn_send = new JButton("send loan request");

    protected static DefaultListModel __listmodel = new DefaultListModel();
    protected static JList __lv_msg = new JList(__listmodel);

    //Define GUI Layout & initiate GUI
    public ClientWindow(ClientController _clientController) {
        super("Loan Client");

        this.clientController = _clientController;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(600, 400);
        setResizable(false);

        this.setLayout(null);

        Container pane = this.getContentPane();

        _text_ssn.setBounds(15, 5, 50, 30);
        _text_amout.setBounds(15, 40, 50, 30);
        _text_time.setBounds(15, 75, 50, 30);

        _tf_ssn.setBounds(70, 5, 150, 30);
        _tf_amount.setBounds(70, 40, 150, 30);
        _tf_time.setBounds(70, 75, 150, 30);

        _btn_send.setBounds(230, 75, 150, 30);

        __lv_msg.setBounds(15, 120, 570, 245);

        pane.add(_text_ssn);
        pane.add(_text_amout);
        pane.add(_text_time);

        pane.add(_tf_ssn);
        pane.add(_tf_amount);
        pane.add(_tf_time);

        pane.add(_btn_send);

        pane.add(__lv_msg);

        _btn_send.addActionListener(this::actionPerformed);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (!e.getSource().equals(_btn_send)) {
            return;
        } else {
            if((_tf_ssn.getText() != "") && (_tf_amount.getText() != "") && (_tf_time.getText() != "")) {
                LoanRequest request = new LoanRequest();
                request.setSsn(Integer.parseInt(_tf_ssn.getText()));
                request.setAmount(Integer.parseInt(_tf_amount.getText()));
                request.setTime(Integer.parseInt(_tf_time.getText()));

                System.out.println("clintWindow:"+request);

                try {
                    clientController.send(request);
                    __listmodel.addElement(request.toString() + " || " + "waiting...");

                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            } else {
                JOptionPane.showMessageDialog(this, "Please enter all input forms...");
            }
        }
    }

    public void onReceiveReply(LoanReply reply, int index) {
        String archive = (String) __listmodel.getElementAt(index);
        String originalMsg = archive.substring(0, archive.indexOf(" || "));

        __listmodel.setElementAt(originalMsg + " || " + reply.toString(), index);
    }

}
