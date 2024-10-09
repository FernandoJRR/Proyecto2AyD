package com.university.models.noBD;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String hostFront1;
    private String hostFront2;
    private String hostBackend;

    public String getHostFront1() {
        return hostFront1;
    }

    public void setHostFront1(String hostFront1) {
        this.hostFront1 = hostFront1;
    }

    public String getHostFront2() {
        return hostFront2;
    }

    public void setHostFront2(String hostFront2) {
        this.hostFront2 = hostFront2;
    }

    public String getHostBackend() {
        return hostBackend;
    }

    public void setHostBackend(String hostBackend) {
        this.hostBackend = hostBackend;
    }
}
