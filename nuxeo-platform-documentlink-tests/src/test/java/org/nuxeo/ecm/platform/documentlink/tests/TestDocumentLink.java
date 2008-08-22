/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 *
 */

package org.nuxeo.ecm.platform.documentlink.tests;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.model.DocumentPart;
import org.nuxeo.ecm.core.repository.jcr.testing.RepositoryOSGITestCase;
import org.nuxeo.ecm.platform.documentlink.api.DocumentLinkAdapter;
import org.nuxeo.ecm.platform.documentlink.api.DocumentLinkConstants;
import org.nuxeo.ecm.platform.documentlink.api.helper.DocumentLinkHelper;
import org.nuxeo.ecm.platform.documentrepository.api.DocRepository;
import org.nuxeo.ecm.platform.documentrepository.api.helper.DocRepositoryHelper;

public class TestDocumentLink extends RepositoryOSGITestCase {

    private DocumentModel doc;

    private DocumentModel link;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle("org.nuxeo.ecm.platform.content.template");
        deployBundle("org.nuxeo.ecm.platform.types.api");
        //deployBundle("org.nuxeo.ecm.platform.documentlink.api");
        deployContrib("org.nuxeo.ecm.platform.documentlink.api","OSGI-INF/documentlink-adapter-contrib.xml");
        deployContrib("org.nuxeo.ecm.platform.documentlink.api","OSGI-INF/repository-adapter-contrib.xml");
        //deployBundle("org.nuxeo.ecm.platform.documentlink.types");
        deployContrib("org.nuxeo.ecm.platform.documentlink.types","OSGI-INF/documentlink-types-contrib.xml");
        //deployContrib("org.nuxeo.ecm.platform.documentlink.types","OSGI-INF/content-template-contrib.xml");
        //deployBundle("org.nuxeo.ecm.platform.documentlink.core");
        deployContrib("org.nuxeo.ecm.platform.documentlink.core","OSGI-INF/documentrepository-framework.xml");

        openRepository();

    }

    private void createDocuments() throws Exception {
        DocumentModel wsRoot = coreSession.getDocument(new PathRef(
                "default-domain/workspaces"));

        DocumentModel ws = coreSession.createDocumentModel(
                wsRoot.getPathAsString(), "ws1", "Workspace");
        ws.setProperty("dublincore", "title", "test WS");
        ws = coreSession.createDocument(ws);

        doc = coreSession.createDocumentModel(ws.getPathAsString(), "file",
                "File");
        doc.setProperty("dublincore", "title", "MyDoc");
        doc.setProperty("dublincore", "coverage", "MyDocCoverage");
        doc = coreSession.createDocument(doc);

        link = coreSession.createDocumentModel(ws.getPathAsString(), "link",
                "DocumentLink");
        link.setProperty("dublincore", "title", "MyLinkToDoc");
        link = coreSession.createDocument(link);
    }


    public void testAdapter() throws Exception {
        createDocuments();

        DocumentLinkAdapter adaptedLink = link.getAdapter(DocumentLinkAdapter.class);
        assertNotNull(adaptedLink);

        adaptedLink.setTargetDocument(doc);
        assertNotNull(adaptedLink.getTargetDocument());

        // check link setup
        String linkRef = (String) link.getProperty(
                DocumentLinkConstants.DOCUMENT_LINK_SCHEMA_NAME,
                DocumentLinkConstants.DOCUMENT_LINK_FIELD_NAME);
        assertEquals(doc.getRef().toString(), linkRef);

        String linkRef2 = (String) adaptedLink.getProperty(
                DocumentLinkConstants.DOCUMENT_LINK_SCHEMA_NAME,
                DocumentLinkConstants.DOCUMENT_LINK_FIELD_NAME);
        assertEquals(doc.getRef().toString(), linkRef2);

        // check property accessor pass-throught
        String cover1 = (String) link.getProperty("dublincore", "coverage");
        assertNull(cover1);
        String cover2 = (String) adaptedLink.getProperty("dublincore",
                "coverage");
        assertNotNull(cover2);
        assertEquals("MyDocCoverage", cover2);

        // check property accessor masking
        String title0 = (String) doc.getProperty("dublincore", "title");
        String title1 = (String) link.getProperty("dublincore", "title");
        String title2 = (String) adaptedLink.getProperty("dublincore", "title");
        assertEquals(title1, title2);
        assertFalse(title1.equals(title0));
    }

    public void testAdapterPassThrought() throws Exception {
        createDocuments();

        String[] ptschemas = new String[] { "dublincore" };



        DocumentLinkAdapter adaptedLink = link.getAdapter(DocumentLinkAdapter.class);
        assertNotNull(adaptedLink);

        adaptedLink.setTargetDocument(doc);
        assertNotNull(adaptedLink.getTargetDocument());

        assertFalse(adaptedLink.getUnmaskedSchemas().contains("dublincore"));

        DocumentPart dcPart = adaptedLink.getPart("dublincore");
        String localCoverage = (String) dcPart.get("coverage").getValue();
        assertNull(localCoverage);

        link.setProperty(DocumentLinkConstants.DOCUMENT_LINK_SCHEMA_NAME,
                "passthroughtSchemas", ptschemas);
        String[] ptschemas2 = (String[])link.getProperty(DocumentLinkConstants.DOCUMENT_LINK_SCHEMA_NAME, "passthroughtSchemas");

        assertEquals(ptschemas, ptschemas2);

        assertTrue(adaptedLink.getUnmaskedSchemas().contains("dublincore"));

        dcPart = adaptedLink.getPart("dublincore");
        String targetCoverage = (String) dcPart.get("coverage").getValue();
        assertNotNull(targetCoverage);

    }

    public void testHelper() throws Exception
    {
        createDocuments();

        DocumentLinkAdapter adaptedLink = DocumentLinkHelper.createDocumentLink(doc, "/");
        assertNotNull(adaptedLink);


        String linkRef = (String) adaptedLink.getLinkDocument().getProperty(
                DocumentLinkConstants.DOCUMENT_LINK_SCHEMA_NAME,
                DocumentLinkConstants.DOCUMENT_LINK_FIELD_NAME);
        assertEquals(doc.getRef().toString(), linkRef);

        String linkRef2 = (String) adaptedLink.getProperty(
                DocumentLinkConstants.DOCUMENT_LINK_SCHEMA_NAME,
                DocumentLinkConstants.DOCUMENT_LINK_FIELD_NAME);
        assertEquals(doc.getRef().toString(), linkRef2);

        assertEquals("/"+ adaptedLink.getName(), adaptedLink.getPathAsString());


    }


    public void testCreateInRepository() throws Exception
    {

        DocumentModel dm = coreSession.createDocumentModel("File");
        dm.setProperty("dublincore", "title", "testme");

        DocumentLinkAdapter adaptedLink =DocumentLinkHelper.createDocumentInCentralRepository(coreSession, dm, "/", "DocumentLink");

        assertNotNull(adaptedLink);

        DocRepository repo = DocRepositoryHelper.getDocumentRepository(coreSession);
        assertNotNull(repo);

        DocumentModel repoDoc = repo.getRepoDoc();
        assertNotNull(repoDoc);

        DocumentModel targetDoc = adaptedLink.getTargetDocument();

        assertEquals("testme", targetDoc.getTitle());

        assertTrue(targetDoc.getPathAsString().contains(repoDoc.getPathAsString()));

        assertFalse(adaptedLink.isBroken());

        repo.removeDocument(targetDoc.getRef());

        assertTrue(adaptedLink.isBroken());

    }

    public void testDoubleLinkAndProxiesListing() throws Exception
    {
        DocumentModel dm = coreSession.createDocumentModel("File");
        dm.setProperty("dublincore", "title", "testme");

        DocumentLinkAdapter adaptedLink =DocumentLinkHelper.createDocumentInCentralRepository(coreSession, dm, "/", "DocumentLink");

        assertNotNull(adaptedLink);

        DocRepository repo = DocRepositoryHelper.getDocumentRepository(coreSession);
        assertNotNull(repo);

        DocumentModel repoDoc = repo.getRepoDoc();
        assertNotNull(repoDoc);

        DocumentModel targetDoc = adaptedLink.getTargetDocument();

        assertEquals("testme", targetDoc.getTitle());

        assertTrue(targetDoc.getPathAsString().contains(repoDoc.getPathAsString()));

        assertFalse(adaptedLink.isBroken());

        coreSession.save();

        DocumentModelList proxies = repo.getProxiesForDocument(targetDoc.getRef());
        assertTrue(proxies.size()==1);

        DocumentLinkAdapter secondLink=DocumentLinkHelper.createDocumentLink(targetDoc, "/");
        assertNotNull(secondLink);
        assertFalse(secondLink.isBroken());
        assertTrue(secondLink.getTargetDocument().getRef().equals(targetDoc.getRef()));

        coreSession.save();

        proxies = repo.getProxiesForDocument(targetDoc.getRef());
        assertTrue(proxies.size()==2);

    }



}
