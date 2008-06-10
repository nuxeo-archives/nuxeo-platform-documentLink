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

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.query.Query;
import org.nuxeo.ecm.core.repository.jcr.testing.RepositoryOSGITestCase;
import org.nuxeo.ecm.platform.documentrepository.api.DocRepository;
import org.nuxeo.ecm.platform.documentrepository.api.helper.DocRepositoryHelper;
import org.nuxeo.ecm.platform.documentrepository.service.plugin.base.DefaultDocumentRepositoryPlugin;

public class TestRepository extends RepositoryOSGITestCase {

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
        openRepository();
    }


    public void testAdapter() throws Exception {

        DocumentModel repo = coreSession.createDocumentModel("/", "repository", "Repository");

        repo= coreSession.createDocument(repo);

        coreSession.save();

        DocRepository bf = repo.getAdapter(DocRepository.class);

        assertNotNull(bf);

        DocumentModel doc = bf.createDocument("File", "TestMe");

        assertNotNull(doc);

        String path = doc.getPathAsString();

        String[] subPathParts = path.split("/");

        assertEquals(3 + DefaultDocumentRepositoryPlugin.DEFAULT_SUB_PATH_PART_NUMBER,
                subPathParts.length);
        System.out.println(path);
    }

    public void testHelper() throws Exception {



        DocRepository bf = DocRepositoryHelper.getDocumentRepository(coreSession);

        DocumentModel repo = bf.getRepoDoc();

        assertEquals(coreSession.getRootDocument().getRef(), repo.getParentRef());

        // check doc creation 1
        DocumentModel doc = DocRepositoryHelper.createDocumentInCentralRepository(
                coreSession, "File", "TestMe");

        assertNotNull(doc);

        String path = doc.getPathAsString();

        String[] subPathParts = path.split("/");

        assertEquals(3 + DefaultDocumentRepositoryPlugin.DEFAULT_SUB_PATH_PART_NUMBER,
                subPathParts.length);

        // check doc creation 2
        DocumentModel dm = coreSession.createDocumentModel("File");
        dm.setProperty("dublincore", "title", "TestMe2");
        DocumentModel doc2 = DocRepositoryHelper.createDocumentInCentralRepository(
                coreSession, dm);

        assertNotNull(doc);

        path = doc.getPathAsString();

        subPathParts = path.split("/");

        assertEquals(3 + DefaultDocumentRepositoryPlugin.DEFAULT_SUB_PATH_PART_NUMBER,
                subPathParts.length);

    }

    public void testSeach() throws Exception
    {
        DocRepository bf = DocRepositoryHelper.getDocumentRepository(coreSession);

        DocumentModel repo = bf.getRepoDoc();

        assertEquals(coreSession.getRootDocument().getRef(), repo.getParentRef());

        // check doc creation 1
        DocumentModel doc = DocRepositoryHelper.createDocumentInCentralRepository(
                coreSession, "File", "TestMe");

        assertNotNull(doc);


        coreSession.save();



        DocumentModel newDoc = coreSession.createDocumentModel("File");
        newDoc.setPathInfo(coreSession.getRootDocument().getPathAsString(), "new");
        newDoc.setProperty("dublincore", "title", "idxTest");
        newDoc = coreSession.createDocument(newDoc);
        coreSession.save();


        String title= doc.getTitle();


        //DocumentModelList docList = coreSession.query("select * from Document where lnk:linkedDocumentRef='" + doc.getRef() + "'");

        DocumentModelList  docList = coreSession.query("select * from Document where dc:title='idxTest'");

        assertNotNull(docList);


    }

}
