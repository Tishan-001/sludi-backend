package com.sludi.sludi.service;

import com.sludi.sludi.config.IPFSConfig;
import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class IpfsService {

    private final IPFS ipfs;

    @Autowired
    public IpfsService(IPFSConfig ipfsConfig) {
        this.ipfs = ipfsConfig.getIpfs();
    }
    public String uploadFile(byte[] fileBytes, String fileName) throws IOException {
        NamedStreamable file = new NamedStreamable.ByteArrayWrapper(fileName,fileBytes);
        MerkleNode result = ipfs.add(file).getFirst();
        return result.hash.toString();
    }

    public byte[] loadFile(String hash) throws IOException{
        Multihash filePointer = Multihash.fromBase58(hash);
        byte[] fileContents = ipfs.cat(filePointer);
        return ipfs.cat(filePointer);
    }


}
