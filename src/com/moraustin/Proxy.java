package com.moraustin;

import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;

public class Proxy extends DefaultRemoteProxy {

    public Proxy(RegistrationRequest request, Registry registry) {
        super(request, registry);
    }
}
