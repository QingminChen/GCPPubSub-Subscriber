package com.qingmin.testProject;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SubscriberExample {
  // use the default project id
  // private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();
  private static final String PROJECT_ID ="PubSub-Test-Project-16951";

  static class MessageReceiverExample implements MessageReceiver {


    public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
      System.out.println(
          "Message Id: " + message.getMessageId() + ", Recieved TimeStamp: "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date())+", Data: " + message.getData().toStringUtf8());
      // Ack only after all work for the message is complete.
      consumer.ack();
    }
  }

  /** Receive messages over a subscription. */
  public static void main(String... args) throws Exception {
    // set subscriber id, eg. my-sub
    String subscriptionId = args[0];
    ProjectSubscriptionName subscriptionName =
        ProjectSubscriptionName.of(PROJECT_ID, subscriptionId);
    Subscriber subscriber = null;
    try {
      // create a subscriber bound to the asynchronous message receiver
      //String jsonPath ="C:\\Codes\\IntelliJ_IDEA_WORKSPACE\\GCPPubTest\\src\\main\\resources\\keyNew.json";
      String jsonPath ="C:\\Codes\\IntelliJ_IDEA_WORKSPACE\\GCPSubTest\\src\\main\\resources\\AllServicesKey.json";
      CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(ServiceAccountCredentials.fromStream(new FileInputStream(jsonPath)));
      subscriber = Subscriber.newBuilder(subscriptionName, new MessageReceiverExample()).setCredentialsProvider(credentialsProvider).build();
      subscriber.startAsync().awaitRunning();
      // Allow the subscriber to run indefinitely unless an unrecoverable error occurs.
      subscriber.awaitTerminated();
    } catch (IllegalStateException e) {
      System.out.println("Subscriber unexpectedly stopped: " + e);
    }
  }
}
