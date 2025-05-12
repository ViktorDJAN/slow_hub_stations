package ru.promelectronika.ocpp_charge_point.configuration;

import eu.chargetime.ocpp.IClientAPI;
import eu.chargetime.ocpp.JSONClient;
import eu.chargetime.ocpp.feature.profile.ClientCoreProfile;
import eu.chargetime.ocpp.wss.BaseWssSocketBuilder;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;

public class JsonClientBuilder {

    public static IClientAPI build(String serverAddress, ClientCoreProfile clientCoreProfile) {
        IClientAPI client = null;
        try {
            BaseWssSocketBuilder wssBuilder = BaseWssSocketBuilder.builder(); // Secure Sockets Layer is a protocol that encrypts and protects data as it travels over the Internet.
            SSLContext context = SSLContext.getDefault();
            wssBuilder.sslSocketFactory(context.getSocketFactory());
            wssBuilder.uri(URI.create(serverAddress));
            wssBuilder.build();
            client = new JSONClient(clientCoreProfile, null, wssBuilder);
        } catch (NoSuchAlgorithmException | IOException e) {
            System.out.println("Problem building client: " + e.getMessage());
        }

        return client;
    }

}
