package com.dotweblabs.todogwt.client.local;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ui.nav.client.local.Navigation;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.parseplatform.client.Parse;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Application bootstrap
 * 
 * @author kerbymart
 * @version 0-SNAPSHOT
 * @since 0-SNAPSHOT
 */
@Templated
@ApplicationScoped
@EntryPoint
public class Bootstrap extends Composite {

    @Inject
    Navigation navigation;

    @PostConstruct
    public void buildUI() {
        Parse.setServerUrl("http://localhost:1337/parse");
        Parse.initialize("myAppId", "myRestAPIKey");
        Parse.initializeJavascriptKey("myJavascriptKey");
        RootPanel.get("rootPanel").add(this);
    }

}
