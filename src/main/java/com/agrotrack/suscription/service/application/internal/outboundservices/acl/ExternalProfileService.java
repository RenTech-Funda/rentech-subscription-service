package com.agrotrack.suscription.service.application.internal.outboundservices.acl;

import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * External service to interact with Profile bounded context
 * This is part of the ACL (Anti-Corruption Layer) for Subscription context
 */
@Service
public class ExternalProfileService {
    private final ProfileContextFacade profileContextFacade;

    public ExternalProfileService(ProfileContextFacade profileContextFacade) {
        this.profileContextFacade = profileContextFacade;
    }

    /**
     * Get profile ID by user ID from Profile context
     * @param userId The user ID
     * @return The profile ID if found, empty otherwise
     */
    public Optional<Long> getProfileIdByUserId(Long userId) {
        return profileContextFacade.getProfileIdByUserId(userId);
    }
}
