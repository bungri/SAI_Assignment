package client.gui;

import client.gateway.LoanBrokerApplicationGateway;
import client.model.LoanReply;
import client.model.LoanRequest;

import javax.jms.Message;

public class ClientController {
    protected int __msgId_list;

    private ClientWindow clientWindow;
    private LoanBrokerApplicationGateway brokerGateway;

    public ClientController() {
        clientWindow = null;
        brokerGateway = null;

        __msgId_list = 0;
    }

    public void setClientWindow(ClientWindow _bankWindow) {
        this.clientWindow = _bankWindow;
    }

    public void setBrokerGateway(LoanBrokerApplicationGateway _brokerGateway) {
        this.brokerGateway = _brokerGateway;
    }

    public void send(LoanRequest request) throws Exception{
        if(brokerGateway!= null) {
            brokerGateway.send(request, __msgId_list++);
        }
    }

    public void onReceiveBankInterestReply(Message message) throws Exception {
        double interest = message.getDoubleProperty("interest");
        String bankId = message.getStringProperty("bankId");
        String msgId = message.getJMSCorrelationID();

        LoanReply reply = new LoanReply(interest, bankId);

        System.out.println(reply);
        clientWindow.onReceiveReply(reply, Integer.parseInt(msgId.substring(msgId.indexOf("-")+1, msgId.length())));
    }

}
