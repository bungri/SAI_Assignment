package bank.gui;

import bank.gateway.LoanBrokerApplicationGateway;
import bank.model.BankInterestReply;
import bank.model.BankInterestRequest;

import javax.jms.Message;
import java.util.ArrayList;

public class BankController {
    protected static ArrayList<Message> list_message = new ArrayList<Message>();

    private BankWindow bankWindow;
    private LoanBrokerApplicationGateway brokerGateway;

    public BankController() {
        bankWindow = null;
        brokerGateway = null;
    }

    public void setBankWindow(BankWindow _bankWindow) {
        this.bankWindow = _bankWindow;
    }

    public void setBrokerGateway(LoanBrokerApplicationGateway _brokerGateway) {
        this.brokerGateway = _brokerGateway;
    }

    public void send(BankInterestReply reply, int index) throws Exception{
        if(brokerGateway!= null) {
            Message tmpMsg = list_message.get(index);
            brokerGateway.send(reply, tmpMsg.getJMSReplyTo(), tmpMsg.getJMSCorrelationID());
        }
    }

    public void onReceiveBankInterestRequest(Message message) throws Exception {
        int amount = message.getIntProperty("amount");
        int time = message.getIntProperty("time");

        BankInterestRequest request = new BankInterestRequest(amount, time);
        list_message.add(message);
        bankWindow.onReceiveRequest(request);
        System.out.println(request);
    }
}
