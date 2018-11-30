package broker.gui;

import broker.gateway.BankApplicationGateway;
import broker.gateway.ClientApplicationGateway;

public class BrokerMain {

    private static BankApplicationGateway bankGateway;
    private static ClientApplicationGateway clientGateway;
    private static BrokerController brokerController;
    private static BrokerWindow brokerWindow;

    public static void main(String[] args) {
        try {
            bankGateway = new BankApplicationGateway();
            clientGateway = new ClientApplicationGateway();
        } catch (Exception e) {
            e.printStackTrace();
        }
        brokerController = new BrokerController();

        brokerWindow = new BrokerWindow(brokerController);

        brokerController.setBrokerWindow(brokerWindow);

        brokerController.setBankGateway(bankGateway);
        brokerController.setClientGateway(clientGateway);

        bankGateway.setBrokerController(brokerController);
        clientGateway.setBrokerController(brokerController);
    }
}
