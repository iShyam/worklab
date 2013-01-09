package com.garage.ejb;

/**
 * The SimpleProducer class consists only of a main method,
 * which sends several messages to a queue or topic.
 *
 * Run this program in conjunction with SimpleSynchConsumer or
  * SimpleAsynchConsumer. Specify a queue or topic name on the
 * command line when you run the program.  By default, the
 * program sends one message.  Specify a number after the
 * destination name to send that number of messages.
  */
import java.util.Properties;

import javax.jms.*;
import javax.naming.*;

public class SimpleProducer {
    /**
     * Main method.
     *
     * @param args     the destination used by the example
      *                 and, optionally, the number of
     *                 messages to send
     */
    public static void main(String[] args) {
        final int NUM_MSGS;

        if ((args.length < 1) || (args.length > 2)) {
             System.out.println("Program takes one or two arguments: " +
                "<dest_name> [<number-of-messages>]");
            System.exit(1);
        }

        String destName = new String(args[0]);
         System.out.println("Destination name is " + destName);

        if (args.length == 2) {
            NUM_MSGS = (new Integer(args[1])).intValue();
        } else {
            NUM_MSGS = 1;
         }

        /*
         * Create a JNDI API InitialContext object if none exists
         * yet.
         */
        Context jndiContext = null;

        try {
         Properties env = new Properties( );
          env.put(Context.SECURITY_PRINCIPAL, "weblogic");  
         env.put(Context.SECURITY_CREDENTIALS, "weblogic1");
         env.put(Context.INITIAL_CONTEXT_FACTORY, 
            "weblogic.jndi.WLInitialContextFactory");
          env.put(Context.PROVIDER_URL, 
            "t3://localhost:7001");                 
         
            jndiContext = new InitialContext(env);
        } catch (NamingException e) {
            System.out.println("Could not create JNDI API context: " +
                 e.toString());
            System.exit(1);
        }

        /*
         * Look up connection factory and destination.  If either
         * does not exist, exit.  If you look up a
         * TopicConnectionFactory or a QueueConnectionFactory,
          * program behavior is the same.
         */
        ConnectionFactory connectionFactory = null;
        Destination dest = null;

        try {
            connectionFactory = (ConnectionFactory) jndiContext.lookup(
                     "jms/myTopicConnectionFactory");
            dest = (Destination) jndiContext.lookup(destName);
        } catch (Exception e) {
            System.out.println("JNDI API lookup failed: " + e.toString());
             e.printStackTrace();
            System.exit(1);
        }

        /*
         * Create connection.
         * Create session from connection; false means session is
         * not transacted.
          * Create producer and text message.
         * Send messages, varying text slightly.
         * Send end-of-messages message.
         * Finally, close connection.
         */
        Connection connection = null;
         MessageProducer producer = null;

        try {
            connection = connectionFactory.createConnection();

            Session session =
                connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
             producer = session.createProducer(dest);

            TextMessage message = session.createTextMessage();

            for (int i = 0; i < NUM_MSGS; i++) {
                message.setText("This is message " + (i + 1));
                 System.out.println("Sending message: " + message.getText());
                producer.send(message);
            }

            /*
             * Send a non-text control message indicating end of
              * messages.
             */
            producer.send(session.createMessage());
        } catch (JMSException e) {
            System.out.println("Exception occurred: " + e.toString());
         } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }
     }
} 
