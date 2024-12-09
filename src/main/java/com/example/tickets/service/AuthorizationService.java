package com.example.tickets.service;

import com.example.tickets.enumeration.UserRoleEnum;
import com.example.tickets.requestcontext.RequestContext;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
public class AuthorizationService {
    public boolean isAuthorized(RequestContext request, EnumSet<UserRoleEnum> authorizedRoles) {
        return authorizedRoles.contains(request.getRole());
    }
}
