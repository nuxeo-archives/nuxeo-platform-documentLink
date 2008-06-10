package org.nuxeo.ecm.platform.documentlink.tests.corelistener;

import org.nuxeo.ecm.core.repository.jcr.testing.RepositoryOSGITestCase;

public class TestCoreListener extends RepositoryOSGITestCase {


    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle("org.nuxeo.ecm.platform.content.template");
        deployBundle("org.nuxeo.ecm.platform.types.api");
        deployBundle("org.nuxeo.ecm.platform.documentlink.api");
        //deployContrib("org.nuxeo.ecm.platform.documentlink.api","OSGI-INF/documentlink-adapter-contrib.xml");
        //deployContrib("org.nuxeo.ecm.platform.documentlink.api","OSGI-INF/repository-adapter-contrib.xml");
        deployBundle("org.nuxeo.ecm.platform.documentlink.types");
        //deployContrib("org.nuxeo.ecm.platform.documentlink.types","OSGI-INF/documentlink-types-contrib.xml");
        //deployContrib("org.nuxeo.ecm.platform.documentlink.types","OSGI-INF/content-template-contrib.xml");
        deployBundle("org.nuxeo.ecm.platform.documentlink.core");
        //deployContrib("org.nuxeo.ecm.platform.documentlink.core","OSGI-INF/documentrepository-framework.xml");
        deployContrib("org.nuxeo.ecm.platform.documentlink.tests", "OSGI-INF/test-corelistener-contrib.xml");
        openRepository();
    }


    /*
    public void testCoreListener() throws Exception
    {
        DocumentModel file = coreSession.createDocumentModel("/", "myFile", "File");

        file.getContextData().put("AutoCreateLink", true);
        file= coreSession.createDocument(file);

        coreSession.save();

        assertEquals("DocumentLink", file.getType());

        DocumentModel repo = DocRepositoryHelper.getDocumentRepository(coreSession, file).getRepoDoc();

    }*/

}
