
package org.wso2.cep.uima.demo.Util;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

import static java.lang.System.exit;

/**
 * Created by Vidura on 1/23/15.
 */

public class TwitterConfigurationBuilder {

    private static String PARAM_CONFIGURATION_FILE = "twitterConfig.xml";
    private static Logger logger = Logger.getLogger(TwitterConfigurationBuilder.class);


    private TwitterConfigurationBuilder(){

    }

    /***
     * Method to parse and return the twitter keys configuration file
     * @return TwitterConfiguration twitter configuration object with relevant parameters set
     */
    public static TwitterConfiguration getTwitterConfiguration(){

        TwitterConfiguration twitterConfiguration = new TwitterConfiguration();
        File xmlFile = new File(PARAM_CONFIGURATION_FILE);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error("Error When Configuring the parser for "+TwitterConfigurationBuilder.class.getName(),e);
            exit(1);
        }
        Document doc = null;

        try {
            doc = dBuilder.parse(xmlFile);
        } catch (IOException e) {
            logger.error("Error Reading the Twitter Configuration File : twitterConfig.xml ",e);
            exit(1);
        } catch (SAXException e) {
            logger.error("Error Parsing Twitter Configuration File : twitterConfig.xml ",e);
            exit(1);
        }

        doc.getDocumentElement().normalize();

        //get API keys
        NodeList nodeList = doc.getElementsByTagName("TwitterAPIKeys");
        Node apiKeysNode = nodeList.item(0);
        Element element = (Element) apiKeysNode;

        twitterConfiguration.setConsumerKey(element.getElementsByTagName("consumerKey").item(0).getTextContent());
        twitterConfiguration.setConsumerSecret(element.getElementsByTagName("consumerSecret").item(0).getTextContent());
        twitterConfiguration.setAccessToken(element.getElementsByTagName("accessToken").item(0).getTextContent());
        twitterConfiguration.setAccessTokenSecret(element.getElementsByTagName("accessTokenSecret").item(0).getTextContent());

        //get user to search for
        NodeList nodeList2 = doc.getElementsByTagName("SearchConfiguration");
        Node searchConfigNode = nodeList2.item(0);

        element = (Element)searchConfigNode;

        twitterConfiguration.setUserToSearch(element.getElementsByTagName("userToSearch").item(0).getTextContent());

        int maxTweetCount = Integer.parseInt(element.getElementsByTagName("maxTweetCount").item(0).getTextContent());
        twitterConfiguration.setMaxTweets(maxTweetCount);

        NodeList nodeList3 = doc.getElementsByTagName("StreamingConfiguration");
        Node followersNode = nodeList3.item(0);
        Element followersElement = (Element) followersNode;
        String strFollowers = followersElement.getElementsByTagName("followers").item(0).getTextContent();
        twitterConfiguration.setFollowers(getFollowers(strFollowers));

        return twitterConfiguration;
    }


    /***
     * Utility Method to get followers IDs as an Array from a comma separated String
     * @param followers String - comma separated String of IDs to follow in the stream
     * @return long[] Long array of IDs of users to follow
     */
    private static long[] getFollowers(String followers){

        String[] split = followers.split(",");
        long[] followerId = new long[split.length];

        for (int i = 0; i < split.length; i++){
            followerId[i] = Long.parseLong(split[i].trim());
        }
        return followerId;
    }
    

}


