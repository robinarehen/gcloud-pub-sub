package com.example.demo.pubsub.read;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

class DemoPubsubReadApplicationTests {

	private String hostport;
	private final String PROJECT_ID = "my-project-1";
	private final String SUBCRIPTION_ID = "my-subscription-1";

	private ManagedChannel channel;
	private TransportChannelProvider channelProvider;
	private CredentialsProvider credentialsProvider;

	@BeforeEach
	void contextLoads() {
		this.hostport = "[::1]:8085";
		assertNotNull("Environment variable: PUBSUB_EMULATOR_HOST must by set, example = [::1]:8085", this.hostport);

		this.channel = ManagedChannelBuilder.forTarget(hostport).usePlaintext().build();
		this.channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(this.channel));
		this.credentialsProvider = NoCredentialsProvider.create();
	}

	@Test
	public void subscribeAsyncExample() {
		ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(this.PROJECT_ID, this.SUBCRIPTION_ID);

		// Instantiate an asynchronous message receiver.
		MessageReceiver receiver = (PubsubMessage message, AckReplyConsumer consumer) -> {
			// Handle incoming message, then ack the received message.
			System.out.println("Id: " + message.getMessageId());
			System.out.println("Data: " + message.getData().toStringUtf8());
			consumer.ack();
		};

		Subscriber subscriber = null;

		try {

			subscriber = Subscriber.newBuilder(subscriptionName, receiver).setChannelProvider(this.channelProvider)
					.setCredentialsProvider(this.credentialsProvider).build();

			// Start the subscriber.
			subscriber.startAsync().awaitRunning();
			System.out.printf("Listening for messages on %s:\n", subscriptionName.toString());

			// Allow the subscriber to run for 30s unless an unrecoverable error occurs.
			subscriber.awaitTerminated(30, TimeUnit.MINUTES);

		} catch (TimeoutException e) {
			// Shut down the subscriber after 30s. Stop receiving messages.
			subscriber.stopAsync();
		} finally {
			this.channel.shutdown();
		}
	}

}
