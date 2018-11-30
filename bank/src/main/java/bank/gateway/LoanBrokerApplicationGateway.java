package bank.gateway;

import bank.gui.BankController;
import bank.model.BankInterestReply;
import org.apache.activemq.command.ActiveMQMessage;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Properties;

public class LoanBrokerApplicationGateway {
    private BankController bankController;
    private requestListener msgListener;

    private Session session; // session for creating messages, producers and
    private Destination brokerDestination;
    private MessageProducer producer; // for sending messages
    private Connection connection; // to connect to the ActiveMQ

    private Destination receiveDestination; //reference to a queue/topic destination
    private MessageConsumer consumer; // for receiving messages

    public LoanBrokerApplicationGateway() throws Exception {
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

        // connect to the my receiveQ
        props.put(("queue.ABN"), "ABN");
        props.put(("queue.broker_bank"), "broker_bank");

        Context jndiContext = new InitialContext(props);
        ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");

        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // define message producer
        brokerDestination = (Destination) jndiContext.lookup("broker_bank");
        producer = session.createProducer(brokerDestination);

        // connect to the receiveQ
        receiveDestination = (Destination) jndiContext.lookup("ABN");
        consumer = session.createConsumer(receiveDestination);

        //TODO set message listener
        msgListener = new requestListener();
        consumer.setMessageListener(msgListener);
        connection.start(); // this is needed to start receiving messages
    }

    public void setBankController(BankController _bankController) {
        this.bankController = _bankController;
    }

    public void send(BankInterestReply reply, Destination destination, String correlationId) throws Exception{
        Message msgRequest = session.createMessage();
        msgRequest.setDoubleProperty("interest", reply.getInterest());
        msgRequest.setStringProperty("bankId", reply.getQuoteId());
        msgRequest.setJMSCorrelationID(correlationId);
        msgRequest.setJMSReplyTo(destination);

        producer.send(msgRequest);
        System.out.println(msgRequest);
    }

    public class requestListener implements MessageListener {
        @Override
        public void onMessage(Message message) {
            System.out.println("Received!");
            ActiveMQMessage msg = (ActiveMQMessage) message;
            if ("jms/message".equals(msg.getJMSXMimeType())) {
                Message _message = (Message) message;
                if (_message != null) {
                    try {
                        bankController.onReceiveBankInterestRequest(_message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
