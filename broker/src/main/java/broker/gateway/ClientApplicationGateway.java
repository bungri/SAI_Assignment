package broker.gateway;

import broker.gui.BrokerController;
import broker.model.BankInterestReply;
import org.apache.activemq.command.ActiveMQMessage;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Properties;

public class ClientApplicationGateway {
    private BrokerController brokerController;
    private requestListener msgListener;

    private Session session; // session for creating messages, producers and
    private MessageProducer producer; // for sending messages
    private Connection connection; // to connect to the ActiveMQ

    private Destination receiveDestination; //reference to a queue/topic destination
    private MessageConsumer consumer; // for receiving messages

    public ClientApplicationGateway() throws Exception {
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

        // connect to the my receiveQ
        props.put(("queue.broker_client"), "broker_client");

        Context jndiContext = new InitialContext(props);
        ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");

        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // define message producer
        producer = session.createProducer(null);

        // connect to the receiveQ
        receiveDestination = (Destination) jndiContext.lookup("broker_client");
        consumer = session.createConsumer(receiveDestination);

        msgListener = new requestListener();
        consumer.setMessageListener(msgListener);
        connection.start(); // this is needed to start receiving messages
    }

    public void setBrokerController(BrokerController _brokerController) {
        this.brokerController = _brokerController;
    }

    public void send(BankInterestReply reply, Destination senderDestination, String msgId) throws Exception{
        Message msgReply = session.createMessage();
        msgReply.setDoubleProperty("interest", reply.getInterest());
        msgReply.setStringProperty("bankId", reply.getQuoteId());
        msgReply.setJMSCorrelationID(msgId);

        producer.send(senderDestination, msgReply);
        System.out.println(msgReply);
    }

    public class requestListener implements MessageListener {
        @Override
        public void onMessage(Message message) {
            ActiveMQMessage msg = (ActiveMQMessage) message;
            if ("jms/message".equals(msg.getJMSXMimeType())) {
                Message _message = (Message) message;
                if (_message != null) {
                    try {
                        brokerController.onReceiveLoanRequest(_message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
