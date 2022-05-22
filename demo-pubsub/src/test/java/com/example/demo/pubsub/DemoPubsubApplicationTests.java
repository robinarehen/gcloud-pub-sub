package com.example.demo.pubsub;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.NotFoundException;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.SubscriptionName;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

class DemoPubsubApplicationTests {

	private String hostport;
	private final String PROJECT_ID = "my-project-1";
	private final String TOPIC_ID = "my-topic-1";
	private final String SUBCRIPTION_ID = "my-subscription-1";

	private ManagedChannel channel;
	private TransportChannelProvider channelProvider;
	private CredentialsProvider credentialsProvider;
	private TopicName topicName;

	@BeforeEach
	void contextLoads() {
		// this.hostport = System.getenv("PUBSUB_EMULATOR_HOST");
		this.hostport = "[::1]:8085";
		assertNotNull("Environment variable: PUBSUB_EMULATOR_HOST must by set, example = [::1]:8085", this.hostport);

		this.channel = ManagedChannelBuilder.forTarget(this.hostport).usePlaintext().build();
		this.channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(this.channel));
		this.credentialsProvider = NoCredentialsProvider.create();
		this.topicName = TopicName.of(this.PROJECT_ID, this.TOPIC_ID);
	}

	/**
	 * https://cloud.google.com/pubsub/docs/emulator
	 */
	@Test
	public void createTopic() {
		try {

			TopicAdminClient topicClient = TopicAdminClient
					.create(TopicAdminSettings.newBuilder().setTransportChannelProvider(this.channelProvider)
							.setCredentialsProvider(this.credentialsProvider).build());

			assertNotNull(topicClient);

			Topic response = null;

			try {
				response = topicClient.getTopic(this.topicName);
			} catch (NotFoundException e) {
				response = topicClient.createTopic(this.topicName);
			}

			assertNotNull(response);

			topicClient.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			this.createPullSubscriptionExample();
		}
	}

	/**
	 * https://cloud.google.com/pubsub/docs/create-subscription#create_subscriptions
	 */
	public void createPullSubscriptionExample() {
		try {
			SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient
					.create(SubscriptionAdminSettings.newBuilder().setTransportChannelProvider(this.channelProvider)
							.setCredentialsProvider(this.credentialsProvider).build());

			SubscriptionName subscriptionName = SubscriptionName.of(this.PROJECT_ID, this.SUBCRIPTION_ID);

			Subscription subscription = subscriptionAdminClient.createSubscription(subscriptionName, this.topicName,
					PushConfig.getDefaultInstance(), 10);

			assertNotNull(subscription);
			// System.out.println("Created pull subscription: " + subscription.getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.channel.shutdown();
		}
	}

	/**
	 * https://cloud.google.com/pubsub/docs/publisher#java
	 */
	@Test
	public void publishWithErrorHandlerExample() {
		try {

			Publisher publisher = Publisher.newBuilder(this.topicName).setChannelProvider(this.channelProvider)
					.setCredentialsProvider(this.credentialsProvider).build();

			String ms1 = "{\"index\":\"value\"}";
			String ms2 = "Gcloud PubSub Mensaje 1";
			Arrays.asList(ms1, ms2).forEach(message -> {

				ByteString data = ByteString.copyFromUtf8(message);

				PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

				// Once published,
				// returns a server-assigned message id (unique within the topic)
				ApiFuture<String> future = publisher.publish(pubsubMessage);

				// Add an asynchronous callback to handle success / failure
				ApiFutures.addCallback(future, this.apiFutureCallback(message), MoreExecutors.directExecutor());
			});

			if (publisher != null) {
				// When finished with the publisher, shutdown to free up resources.
				publisher.shutdown();
				publisher.awaitTermination(1, TimeUnit.MINUTES);
			}
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.channel.shutdown();
		}

	}

	public ApiFutureCallback<String> apiFutureCallback(final String message) {
		return new ApiFutureCallback<String>() {

			@Override
			public void onFailure(Throwable throwable) {
				if (throwable instanceof ApiException) {
					ApiException apiException = ((ApiException) throwable);
					// details on the API exception
					System.out.println(apiException.getStatusCode().getCode());
					System.out.println(apiException.isRetryable());
				}
				System.out.println("Error publishing message : " + message);
			}

			@Override
			public void onSuccess(String messageId) {
				// Once published, returns server-assigned message ids (unique within the topic)
				// System.out.println("Published message ID: " + messageId);
			}
		};
	}

	@Disabled
	public void deleteTopic() {
		try {

			TopicAdminClient topicClient = TopicAdminClient.create(TopicAdminSettings.newBuilder()
					.setTransportChannelProvider(channelProvider).setCredentialsProvider(credentialsProvider).build());

			assertNotNull(topicClient);

			try {
				topicClient.getTopic(topicName);

				topicClient.deleteTopic(topicName);
			} catch (NotFoundException e) {
			}

			topicClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			this.channel.shutdown();
		}
	}

}
