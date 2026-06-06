package com.bodymatch.nutrition.client;

import com.bodymatch.nutrition.shared.UserId;
import feign.FeignException;
import org.springframework.stereotype.Service;

@Service
public class IamGateway {
    private final IamClient iamClient;

    public IamGateway(IamClient iamClient) {
        this.iamClient = iamClient;
    }

    public boolean existsUser(UserId userId) {
        try {
            iamClient.getUserById(userId.userId());
            return true;
        } catch (FeignException.NotFound e) {
            return false;
        }
    }
}
