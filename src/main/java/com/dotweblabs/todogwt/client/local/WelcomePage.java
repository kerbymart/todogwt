package com.dotweblabs.todogwt.client.local;

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.nav.client.local.DefaultPage;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import javax.enterprise.context.Dependent;


/**
 * Main page
 *
 * @author kerbymart
 * @version 0-SNAPSHOT
 * @since 0-SNAPSHOT
 */
@Dependent
@Templated
@Page(role = DefaultPage.class, path = "/")
public class WelcomePage extends Composite {

}
