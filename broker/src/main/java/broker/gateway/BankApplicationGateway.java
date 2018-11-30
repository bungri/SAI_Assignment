package broker.gateway;

import broker.gui.BrokerController;
import broker.model.LoanRequest;
import org.apache.activemq.command.ActiveMQMessage;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Properties;

public class BankApplicationGateway {
    private BrokerController brokerController;
    private replyListener msgListener;

    private Session session; // session for creating messages, producers and
    private MessageProducer producer; // for sending messages
    private Connection connection; // to connect to the ActiveMQ

    private Properties props;
    private Context jndiContext;

    private Destination receiveDestination; //reference to a queue/topic destination
    private Destination bankDestination;
    private MessageConsumer consumer; // for receiving messages

    public BankApplicationGateway() throws Exception {
        props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

        // connect to the my receiveQ
        props.put(("queue.broker_bank"), "broker_bank");
        props.put(("queue.ABN"), "ABN");

        jndiContext = new InitialContext(props);
        ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");

        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // define message producer
        bankDestination = (Destination) jndiContext.lookup("ABN");
        producer = session.createProducer(bankDestination);

        // connect to the receiveQ
        receiveDestination = (Destination) jndiContext.lookup("broker_bank");
        consumer = session.createConsumer(receiveDestination);

        msgListener = new replyListener();
        consumer.setMessageListener(msgListener);
        connection.start(); // this is needed to start receiving messages
    }

    public void setBrokerController(BrokerController _brokerController) {
        this.brokerController = _brokerController;
    }

    public void send(LoanRequest request, Destination senderDestination, String msgId) throws Exception{
        Message msgRequest = session.createMessage();
        msgRequest.setIntProperty("amount", request.getAmount());
        msgRequest.setIntProperty("time", request.getTime());
        msgRequest.setJMSCorrelationID(msgId);
        msgRequest.setJMSReplyTo(senderDestination);

        producer.send(msgRequest);
        System.out.println(msgRequest);
    }

    public class replyListener implements MessageListener {
        @Override
        public void onMessage(Message message) {
            ActiveMQMessage msg = (ActiveMQMessage) message;
            if ("jms/message".equals(msg.getJMSXMimeType())) {
                Message _message = (Message) message;
                if (_message != null) {
                    try {
                        brokerController.onReceiveBankInterestReply(_message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
