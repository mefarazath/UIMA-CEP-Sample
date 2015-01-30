package org.wso2.cep.uima.demo;

import org.apache.activemq.ActiveMQConnectionFactory;
import twitter4j.*;

import javax.jms.*;

/***
 *  Listener implementation for the Twitter Streamer
 */
public class StatusHandler implements StatusListener{
	
	private MessageProducer producer;
	private Session session;
	private static Logger logger = Logger.getLogger(StatusHandler.class);


	/***
	 *
	 * @param jmsURL String | URL of the JMS Broker to send the messages to
	 */
	public StatusHandler(String jmsURL, String topicName) throws JMSException {
		// create the factory for ActiveMQ connection
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(jmsURL);
		Connection connection = factory.createConnection();
		connection.start();
		logger.info("ActiveMQ connection established for StatusHandler successfully");

		// Create a non-transactional session with automatic acknowledgement
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		// Create a reference to the queue test_queue in this session.
		Topic topic = session.createTopic(topicName);

		// Create a producer for queue
		producer = session.createProducer(topic);
		logger.info("ActiveMQ producer successfully created for TwitterStreamer");
	}


	/***
	 * Method to handle arrival of a status to the stream
	 * @param status Recieved Tweet as a Status Object
	 */
	@Override
	public void onStatus(Status status) {
		Logger.getLogger(StatusHandler.class).info("Tweet Recieved : "+status.getText());
		if(producer == null){
			throw new NullPointerException("ActiveMQ producer not set for StatusHandler");
		}

		// send the tweet to the topic
		try {
			TextMessage tweetMessage = session.createTextMessage(status.getText());
			producer.send(tweetMessage);
		} catch (JMSException e) {
			logger.error("Error when sending tweet text to ActiveMQ topic ",e);
			throw new RuntimeException("Unable to send tweet to ActiveMQ topic ",e);
		}

	}


	@Override
	public void onException(Exception arg0) {
		Logger.getLogger(StatusHandler.class).error("Exception occured while streaming for tweets : "+arg0.getMessage());
		arg0.printStackTrace();
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice arg0) {

	}

	@Override
	public void onScrubGeo(long arg0, long arg1) {

	}

	@Override
	public void onStallWarning(StallWarning arg0) {

	}

	@Override
	public void onTrackLimitationNotice(int arg0) {
		Logger.getLogger(StatusHandler.class).error("Track Limitations Exceeded");
	}

}
