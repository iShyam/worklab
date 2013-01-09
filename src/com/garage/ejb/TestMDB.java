package com.garage.ejb;

import javax.annotation.Resources;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
 import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.*;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
 import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Message-Driven Bean implementation class for: TestMDB
a WebLogic MDB that uses a durable subscription to a JMS topic (in WebLogic Server 10.3.4 or later), 
 transactionally processes the messages, and forwards the messages to a target destination. 
 */
@MessageDriven(
  name="TestMDB",
  mappedName = "jms/myTopic",
  activationConfig = {
       @ActivationConfigProperty(propertyName  = "destinationType", 
                                propertyValue = "javax.jms.Topic"),
                                
          @ActivationConfigProperty(propertyName  = "subscriptionDurability",
                   propertyValue = "Durable"),

      @ActivationConfigProperty(propertyName  = "connectionFactoryJndiName",
                                propertyValue = "jms/myTopicConnectionFactory"), // External JNDI Name
    
      @ActivationConfigProperty(propertyName  = "destinationJndiName",
                                propertyValue = "jms/myTopic") // Ext. JNDI Name
    
  })
  
    @Resources ({
        @Resource(name="targetCFRef",        
                 mappedName="jms/myTopicConnectionFactory",   // External JNDI name 
                 type=javax.jms.ConnectionFactory.class),
      
        @Resource(name="targetDestRef", 
                 mappedName="jms/myTopic", // External JNDI name
                 type=javax.jms.Destination.class)
     })  
     
public class TestMDB implements MessageListener {
    // inject a reference to the MDB context

   @Resource
   private MessageDrivenContext mdctx;  

   // cache targetCF and targetDest for re-use (performance) 

   private ConnectionFactory targetCF;
    private Destination targetDest;
   
    /**
     * Default constructor. 
     */
    public TestMDB() {
        // TODO Auto-generated constructor stub
     System.out.println("MDB Created");
     }
 

  
 /**
     * @see MessageListener#onMessage(Message)
     */     
    public void onMessage(Message message) {
        System.out.println("My MDB got message: " + message);

        // Forward the message to "MyTargetDest" using "MyTargetCF"

        Connection jmsConnection = null;

        try {
          if (targetCF == null) 
            targetCF = (javax.jms.ConnectionFactory)mdctx.lookup("targetCFRef");

          if (targetDest == null)
            targetDest = (javax.jms.Destination)mdctx.lookup("targetDestRef");

          jmsConnection = targetCF.createConnection();
          Session s = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
           MessageProducer mp = s.createProducer(null);

          mp.send(targetDest, message);

        } catch (JMSException e) {

          System.out.println("Forcing rollback due to exception " + e);
           e.printStackTrace();
          mdctx.setRollbackOnly();

        } finally {

          // Closing a connection automatically returns the connection and
          // its session plus producer to the resource reference pool.

          try { if (jmsConnection != null) jmsConnection.close(); }
          catch (JMSException ignored) {};
        }
     
        // emulate 1 second of "think" time
     
        try { Thread.currentThread().sleep(1000); }
         catch (InterruptedException ie) {
          Thread.currentThread().interrupt(); // Restore the interrupted status
        }
      }
 }

