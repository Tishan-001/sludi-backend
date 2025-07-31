package com.sludi.sludi.config;

import io.ipfs.api.IPFS;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class IPFSConfig {
    private final IPFS ipfs;

    public IPFSConfig() {
        String projectId = "";
        String projectSecret = "";
        String multiaddr = "/dns/ipfs.infura.io/tcp/5001/https";
        this.ipfs = new IPFS(multiaddr);
        this.ipfs.updateHeaders.set("Authorization",
                "Basic " + java.util.Base64.getEncoder()
                        .encodeToString((projectId + ":" + projectSecret).getBytes())
        );

    }

    public IPFS getIpfs() {
        return ipfs;
    }
}
