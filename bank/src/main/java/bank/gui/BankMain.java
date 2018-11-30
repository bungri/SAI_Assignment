package bank.gui;

import bank.gateway.LoanBrokerApplicationGateway;

public class BankMain {

    private static LoanBrokerApplicationGateway brokerGateway;
    private static BankController bankController;
    private static BankWindow bankWindow;

    public static void main(String[] args) {
        try {
            brokerGateway = new LoanBrokerApplicationGateway();
        } catch (Exception e) {
            e.printStackTrace();
        }
        bankController = new BankController();

        bankWindow = new BankWindow(bankController);

        bankController.setBankWindow(bankWindow);
        bankController.setBrokerGateway(brokerGateway);

        brokerGateway.setBankController(bankController);
    }
}
