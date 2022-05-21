package com.example.demo.pubsub.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Configuration
public class BeanConfig {

	@Bean
	public ManagedChannel managedChannel() {
		String hostport = System.getenv("PUBSUB_EMULATOR_HOST");
		return ManagedChannelBuilder.forTarget(hostport).usePlaintext().build();
	}

	@Bean
	public TransportChannelProvider TransportChannelProvider() {
		return FixedTransportChannelProvider.create(GrpcTransportChannel.create(this.managedChannel()));
	}

	@Bean
	public SubscriptionAdminClient subscriptionAdminClient() {
		try {
			return SubscriptionAdminClient.create(
					SubscriptionAdminSettings.newBuilder().setTransportChannelProvider(this.TransportChannelProvider())
							.setCredentialsProvider(NoCredentialsProvider.create()).build());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
