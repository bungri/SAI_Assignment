package broker.gui;

import broker.gateway.BankApplicationGateway;
import broker.gateway.ClientApplicationGateway;
import broker.model.BankInterestReply;
import broker.model.LoanRequest;

import javax.jms.Destination;
import javax.jms.Message;
import java.util.ArrayList;

public class BrokerController {
    protected ArrayList<String> list_msgId = new ArrayList<String>();

    private BrokerWindow brokerWindow;
    private BankApplicationGateway bankGateway;
    private ClientApplicationGateway clientGateway;

    public BrokerController() {
        brokerWindow = null;
        bankGateway = null;
        clientGateway = null;
    }

    public void setBrokerWindow(BrokerWindow _brokerWindow) {
        this.brokerWindow = _brokerWindow;
    }

    public void setBankGateway(BankApplicationGateway _bankGateway) {
        this.bankGateway = _bankGateway;
    }

    public void setClientGateway(ClientApplicationGateway _clientGateway) {
        this.clientGateway = _clientGateway;
    }

    public void relayLoanRequest(LoanRequest request, Destination senderDestination, String msgId) throws Exception {
        if (bankGateway != null) {
            System.out.println("request relay");
            bankGateway.send(request, senderDestination, msgId);
        }
    }

    public void relayBankInterestReply(BankInterestReply reply, Destination senderDestination, String msgId) throws Exception {
        if (clientGateway != null) {
            clientGateway.send(reply, senderDestination, msgId);
        }
    }

    public void onReceiveLoanRequest(Message message) throws Exception {
        int ssn = message.getIntProperty("ssn");
        int amount = message.getIntProperty("amount");
        int time = message.getIntProperty("time");
        String msgId = message.getJMSCorrelationID();
        list_msgId.add(msgId);

        LoanRequest request = new LoanRequest(ssn, amount, time);

        System.out.println(request);
        brokerWindow.onReceiveRequest(request);

        relayLoanRequest(request, message.getJMSReplyTo(), msgId);
    }

    public void onReceiveBankInterestReply(Message message) throws Exception {
        double interest = message.getDoubleProperty("interest");
        String bankId = message.getStringProperty("bankId");
        String msgId = message.getJMSCorrelationID();

        BankInterestReply reply = new BankInterestReply(interest, bankId);

        System.out.println(reply);
        brokerWindow.onReceiveReply(reply, list_msgId.indexOf(msgId));

        relayBankInterestReply(reply, message.getJMSReplyTo(), msgId);
    }

}
