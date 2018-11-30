package client.gui;

import client.gateway.LoanBrokerApplicationGateway;

public class ClientMain {

    private static LoanBrokerApplicationGateway brokerGateway;
    private static ClientController clientController;
    private static ClientWindow clientWindow;

    public static void main(String[] args) {
        try {
            brokerGateway = new LoanBrokerApplicationGateway();
        } catch (Exception e) {
            e.printStackTrace();
        }
        clientController = new ClientController();

        clientWindow = new ClientWindow(clientController);

        clientController.setClientWindow(clientWindow);
        clientController.setBrokerGateway(brokerGateway);

        brokerGateway.setClientController(clientController);
    }

}
