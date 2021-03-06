package com.sap.cloudfoundry.client.facade.adapters;

import org.cloudfoundry.client.v2.info.GetInfoResponse;
import org.immutables.value.Value;

import com.sap.cloudfoundry.client.facade.domain.CloudInfo;
import com.sap.cloudfoundry.client.facade.domain.ImmutableCloudInfo;

@Value.Immutable
public abstract class RawCloudInfo extends RawCloudEntity<CloudInfo> {

    @Value.Parameter
    public abstract GetInfoResponse getResource();

    @Override
    public CloudInfo derive() {
        GetInfoResponse resource = getResource();
        return ImmutableCloudInfo.builder()
                                 .authorizationEndpoint(resource.getAuthorizationEndpoint())
                                 .loggingEndpoint(resource.getDopplerLoggingEndpoint())
                                 .build(resource.getBuildNumber())
                                 .description(resource.getDescription())
                                 .name(resource.getName())
                                 .user(resource.getUser())
                                 .support(resource.getSupport())
                                 .version(parseVersion(resource.getVersion()))
                                 .build();
    }

    private static String parseVersion(Integer version) {
        return version == null ? null : String.valueOf(version);
    }

}
