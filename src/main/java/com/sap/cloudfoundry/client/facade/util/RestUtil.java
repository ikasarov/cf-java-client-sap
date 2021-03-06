package com.sap.cloudfoundry.client.facade.util;

import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLException;
import javax.net.ssl.X509TrustManager;

import org.cloudfoundry.reactor.ConnectionContext;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import com.sap.cloudfoundry.client.facade.oauth2.OAuthClient;
import com.sap.cloudfoundry.client.facade.oauth2.OAuthClientWithLoginHint;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import reactor.netty.http.client.HttpClient;

/**
 * Some helper utilities for creating classes used for the REST support.
 *
 */
public class RestUtil {

    public OAuthClient createOAuthClient(URL authorizationUrl, boolean trustSelfSignedCerts) {
        return new OAuthClient(authorizationUrl, createWebClient(trustSelfSignedCerts));
    }

    public OAuthClient createOAuthClient(URL authorizationUrl, boolean trustSelfSignedCerts, ConnectionContext connectionContext,
                                         String origin) {
        return new OAuthClientWithLoginHint(authorizationUrl, createWebClient(trustSelfSignedCerts), connectionContext, origin);
    }

    public WebClient createWebClient(boolean trustSelfSignedCerts) {
        return WebClient.builder()
                        .clientConnector(buildClientConnector(trustSelfSignedCerts))
                        .build();
    }

    private ClientHttpConnector buildClientConnector(boolean trustSelfSignedCerts) {
        HttpClient httpClient = HttpClient.create();
        if (trustSelfSignedCerts) {
            httpClient.secure(sslContextSpec -> sslContextSpec.sslContext(buildSslContext()));
        } else {
            httpClient.secure();
        }
        return new ReactorClientHttpConnector(httpClient);
    }

    private SslContext buildSslContext() {
        try {
            return SslContextBuilder.forClient()
                                    .trustManager(createDummyTrustManager())
                                    .build();
        } catch (SSLException e) {
            throw new RuntimeException("An error occurred setting up the SSLContext", e);
        }
    }

    private X509TrustManager createDummyTrustManager() {
        return new X509TrustManager() {

            @Override
            public void checkClientTrusted(X509Certificate[] xcs, String string) {
                // NOSONAR
            }

            @Override
            public void checkServerTrusted(X509Certificate[] xcs, String string) {
                // NOSONAR
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }

        };
    }
}
