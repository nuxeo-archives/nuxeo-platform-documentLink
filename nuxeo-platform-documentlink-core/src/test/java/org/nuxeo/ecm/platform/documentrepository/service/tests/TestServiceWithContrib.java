package org.nuxeo.ecm.platform.documentrepository.service.tests;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.repository.jcr.testing.RepositoryOSGITestCase;
import org.nuxeo.ecm.platform.documentrepository.api.DocRepository;
import org.nuxeo.ecm.platform.documentrepository.service.api.DocumentRepositoryManager;
import org.nuxeo.runtime.api.Framework;

public class TestServiceWithContrib extends RepositoryOSGITestCase {


    @Override
    public void setUp() throws Exception {
        super.setUp();

        deployBundle("org.nuxeo.ecm.platform.content.template");
        deployBundle("org.nuxeo.ecm.platform.types.api");
        //deployBundle("org.nuxeo.ecm.platform.documentlink.api");
        deployBundle("org.nuxeo.ecm.platform.dublincore");
        //deployContrib("org.nuxeo.ecm.platform.documentlink.api","OSGI-INF/documentlink-adapter-contrib.xml");
        deployContrib("org.nuxeo.ecm.platform.documentlink.api","OSGI-INF/repository-adapter-contrib.xml");
        //deployBundle("org.nuxeo.ecm.platform.documentlink.types");
        deployContrib("org.nuxeo.ecm.platform.documentlink.types","OSGI-INF/documentlink-types-contrib.xml");
        deployContrib("org.nuxeo.ecm.platform.documentlink.core","OSGI-INF/documentrepository-framework.xml");
        openRepository();
    }

    public void testDocumentCreationWithClassContrib() throws Exception
    {

        deployContrib("org.nuxeo.ecm.platform.documentlink.core", "OSGI-INF/test-repoplugin-class-contrib.xml");

        DocumentRepositoryManager service =  Framework.getService(DocumentRepositoryManager.class);
        assertNotNull(service);

        DocumentModel domain = coreSession.getChild(coreSession.getRootDocument().getRef(), "default-domain");

        DocumentModel doc = coreSession.createDocumentModel("/", "testFile","File");

        DocRepository repo = service.getDocumentRepository(coreSession, domain);

        assertNotNull(repo);

        assertTrue(repo.getRepoDoc().getPathAsString().contains(domain.getPathAsString()));

        doc = service.createDocumentInRepository(coreSession,repo.getRepoDoc(), doc);

        assertNotNull(doc);

        coreSession.save();

        assertTrue(doc.getPathAsString().contains(repo.getRepoDoc().getPathAsString()));

        String path = doc.getPathAsString();

        String[] subPathParts = path.split("/");

        assertEquals(3 +  7, subPathParts.length);

    }

}
