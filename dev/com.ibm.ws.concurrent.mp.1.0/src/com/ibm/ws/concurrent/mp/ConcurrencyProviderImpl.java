/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.concurrent.mp;

import org.eclipse.microprofile.concurrent.ManagedExecutorBuilder;
import org.eclipse.microprofile.concurrent.ThreadContextBuilder;
import org.eclipse.microprofile.concurrent.spi.ConcurrencyManager;
import org.eclipse.microprofile.concurrent.spi.ConcurrencyManagerBuilder;
import org.eclipse.microprofile.concurrent.spi.ConcurrencyProvider;
import org.eclipse.microprofile.concurrent.spi.ConcurrencyProviderRegistration;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.ibm.ws.concurrent.mp.context.ApplicationContextProvider;

/**
 * Registers this implementation as the provider of MicroProfile Concurrency.
 */
@Component(configurationPolicy = ConfigurationPolicy.IGNORE, immediate = true)
public class ConcurrencyProviderImpl implements ConcurrencyProvider {
    final ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider();

    private ConcurrencyProviderRegistration registration;

    @Activate
    protected void activate(ComponentContext osgiComponentContext) {
        applicationContextProvider.classloaderContextProviderRef.activate(osgiComponentContext);
        applicationContextProvider.jeeMetadataContextProviderRef.activate(osgiComponentContext);
        registration = ConcurrencyProvider.register(this);
    }

    @Deactivate
    protected void deactivate(ComponentContext osgiComponentContext) {
        registration.unregister();
        applicationContextProvider.jeeMetadataContextProviderRef.deactivate(osgiComponentContext);
        applicationContextProvider.classloaderContextProviderRef.deactivate(osgiComponentContext);
    }

    @Override
    public ConcurrencyManager getConcurrencyManager() {
        return null; // TODO
    }

    @Override
    public ConcurrencyManager getConcurrencyManager(ClassLoader classLoader) {
        return null; // TODO
    }

    @Override
    public ConcurrencyManagerBuilder getConcurrencyManagerBuilder() {
        return null; // TODO
    }

    @Override
    public ManagedExecutorBuilder newManagedExecutorBuilder() {
        return null; // TODO rely on default implementation to get from concurrency manager?
    }

    @Override
    public ThreadContextBuilder newThreadContextBuilder() {
        return new ThreadContextBuilderImpl(this); // TODO rely on default implementation to get from concurrency manager?
    }

    @Override
    public void registerConcurrencyManager(ConcurrencyManager manager, ClassLoader classLoader) {
        // TODO
    }

    @Override
    public void releaseConcurrencyManager(ConcurrencyManager manager) {
        // TODO
    }

    @Reference(service = com.ibm.wsspi.threadcontext.ThreadContextProvider.class,
               target = "(component.name=com.ibm.ws.classloader.context.provider)")
    protected void setClassloaderContextProvider(ServiceReference<com.ibm.wsspi.threadcontext.ThreadContextProvider> ref) {
        applicationContextProvider.classloaderContextProviderRef.setReference(ref);
    }

    @Reference(service = com.ibm.wsspi.threadcontext.ThreadContextProvider.class,
               target = "(component.name=com.ibm.ws.javaee.metadata.context.provider)")
    protected void setJeeMetadataContextProvider(ServiceReference<com.ibm.wsspi.threadcontext.ThreadContextProvider> ref) {
        applicationContextProvider.jeeMetadataContextProviderRef.setReference(ref);
    }

    protected void unsetClassloaderContextProvider(ServiceReference<com.ibm.wsspi.threadcontext.ThreadContextProvider> ref) {
        applicationContextProvider.classloaderContextProviderRef.unsetReference(ref);
    }

    protected void unsetJeeMetadataContextProvider(ServiceReference<com.ibm.wsspi.threadcontext.ThreadContextProvider> ref) {
        applicationContextProvider.jeeMetadataContextProviderRef.unsetReference(ref);
    }
}