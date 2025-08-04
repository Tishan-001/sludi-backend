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

        String multiaddr = "/ip4/127.0.0.1/tcp/5001";
        this.ipfs = new IPFS(multiaddr);
    }

    public IPFS getIpfs() {
        return ipfs;
    }
}
