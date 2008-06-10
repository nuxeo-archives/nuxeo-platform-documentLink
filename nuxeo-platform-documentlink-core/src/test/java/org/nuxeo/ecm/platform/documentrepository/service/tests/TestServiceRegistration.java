package org.nuxeo.ecm.platform.documentrepository.service.tests;

import org.nuxeo.ecm.platform.documentrepository.service.DocumentRepositoryConfigurationService;
import org.nuxeo.ecm.platform.documentrepository.service.api.DocumentRepositoryManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.NXRuntimeTestCase;

public class TestServiceRegistration extends NXRuntimeTestCase {


    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployContrib("org.nuxeo.ecm.platform.documentlink.core","OSGI-INF/documentrepository-framework.xml");
    }


    public void testRuntimeComponent()
    {

        DocumentRepositoryConfigurationService component = (DocumentRepositoryConfigurationService) Framework.getRuntime().getComponent(DocumentRepositoryConfigurationService.NAME);
        assertNotNull(component);
    }

    public void testService() throws Exception
    {

        DocumentRepositoryManager service =  Framework.getService(DocumentRepositoryManager.class);
        assertNotNull(service);
    }


}
