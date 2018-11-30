package client.gateway;

import client.gui.ClientController;
import client.model.LoanRequest;
import org.apache.activemq.command.ActiveMQMessage;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Properties;
import java.util.Random;

public class LoanBrokerApplicationGateway {
    private ClientController clientController;
    private replyListener msgListener;

    private Session session; // session for creating messages, producers and
    private Connection connection; // to connect to the ActiveMQ
    private Context jndiContext;

    private MessageProducer producer; // for sending messages

    private String receiveQueueName;
    private Destination receiveDestination; //reference to a queue/topic destination
    private MessageConsumer consumer; // for receiving messages


    public LoanBrokerApplicationGateway() throws Exception {

        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

        // connect to the my receiveQ
        receiveQueueName = "client_" + getRandomString(8);
        props.put(("queue."+receiveQueueName), receiveQueueName);
        props.put(("queue.broker_client"), "broker_client");

        jndiContext = new InitialContext(props);
        ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");

        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // define message producer
        Destination brokerDestination = (Destination) jndiContext.lookup("broker_client");
        producer = session.createProducer(brokerDestination);


        // connect to the receiveQ
        receiveDestination = (Destination) jndiContext.lookup(receiveQueueName);
        consumer = session.createConsumer(receiveDestination);

        msgListener = new replyListener();
        consumer.setMessageListener(msgListener);
        connection.start(); // this is needed to start receiving messages

    }

    public void setClientController(ClientController _clientController) {
        this.clientController = _clientController;
    }

    public void send(LoanRequest reply, int msgId) throws Exception{
        Message replyMsg = session.createMessage();
        replyMsg.setIntProperty("ssn", reply.getSsn());
        replyMsg.setIntProperty("amount", reply.getAmount());
        replyMsg.setIntProperty("time", reply.getTime());
        replyMsg.setJMSReplyTo(receiveDestination);
        replyMsg.setJMSCorrelationID(receiveQueueName + "-" + Integer.toString(msgId));

        producer.send(replyMsg);
        System.out.println(replyMsg);
    }

    public class replyListener implements MessageListener {
        @Override
        public void onMessage(Message message) {
            ActiveMQMessage msg = (ActiveMQMessage) message;
            if ("jms/message".equals(msg.getJMSXMimeType())) {
                Message _message = (Message) message;
                if (_message != null) {
                    try {
                        clientController.onReceiveBankInterestReply(_message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static String getRandomString(int length)
    {
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();

        String chars[] = "a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,0,1,2,3,4,5,6,7,8,9".split(",");

        for (int i=0 ; i<length ; i++)
        {
            buffer.append(chars[random.nextInt(chars.length)]);
        }
        return buffer.toString();
    }
}

