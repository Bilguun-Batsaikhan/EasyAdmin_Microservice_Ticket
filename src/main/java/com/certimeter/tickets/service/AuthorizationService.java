package com.certimeter.tickets.service;

import com.certimeter.tickets.enumeration.UserRoleEnum;
import com.certimeter.tickets.requestcontext.RequestContext;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
public class AuthorizationService {
    public boolean isAuthorized(RequestContext request, EnumSet<UserRoleEnum> authorizedRoles) {
        return authorizedRoles.contains(request.getRole());
    }
}
