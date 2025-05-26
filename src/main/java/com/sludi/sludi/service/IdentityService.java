package com.sludi.sludi.service;

import com.sludi.sludi.domain.Identity;
import com.sludi.sludi.util.JsonUtil;
import org.hyperledger.fabric.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class IdentityService {

    private final Contract contract;

    @Autowired
    public IdentityService(Contract contract) {
        this.contract = contract;
    }

    public void initLedger() throws EndorseException, SubmitException, CommitStatusException, CommitException {
        contract.submitTransaction("InitLedger");
    }

    public String getAllIdentities() throws GatewayException {
        var result = contract.evaluateTransaction("GetAllIdentities");
        return JsonUtil.prettyJson(result);
    }

    public void createIdentity(Identity identity)
            throws EndorseException, SubmitException, CommitStatusException, CommitException {
        contract.submitTransaction("CreateIdentity", identity.getNic(), identity.getFullName(), identity.getDateOfBirth(),
                identity.getGender(), identity.getAddress(), identity.getPhoneNumber(), identity.getEmail(),
                identity.getIssuedDate(), identity.getIssuedBy(), identity.getBiometricHash(), identity.getStatus());
    }

    public String readIdentity(String nic) throws GatewayException {
        var result = contract.evaluateTransaction("ReadIdentity", nic);
        return JsonUtil.prettyJson(result);
    }

    public void updateIdentity(String nic, Identity identity)
            throws EndorseException, SubmitException, CommitStatusException, CommitException {
        contract.submitTransaction("UpdateIdentity", nic, identity.getFullName(), identity.getDateOfBirth(),
                identity.getGender(), identity.getAddress(), identity.getPhoneNumber(), identity.getEmail(),
                identity.getIssuedDate(), identity.getIssuedBy(), identity.getBiometricHash(), identity.getStatus());
    }

    public void deleteIdentity(String nic) throws EndorseException, SubmitException, CommitStatusException, CommitException {
        contract.submitTransaction("DeleteIdentity", nic);
    }

    public boolean identityExists(String nic) throws GatewayException {
        var result = contract.evaluateTransaction("IdentityExists", nic);
        return Boolean.parseBoolean(new String(result, StandardCharsets.UTF_8));
    }
}
