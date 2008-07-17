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

package org.nuxeo.ecm.platform.documentlink.api.helper;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.documentlink.api.DocumentLinkAdapter;
import org.nuxeo.ecm.platform.documentrepository.api.helper.DocRepositoryHelper;

/**
 *
 * Helper class used to manipulate DocumentLinkAdpater objects.
 *
 * @author <a href="mailto:td@nuxeo.com">Thierry Delprat</a>
 *
 */
public class DocumentLinkHelper {

    /**
     * Creates a DocumentLink documentModel pointing to the target DocumentModel.
     *
     * @param documentManager client CoreSession
     * @param target DocumentModel that must be pointed by the DocumentLink
     * @param path path where the new DocumentLink DocumentModel must be created
     * @return
     * @throws ClientException
     */
    public static DocumentLinkAdapter createDocumentLink(
            CoreSession documentManager, DocumentModel target, String path)
            throws ClientException {
        return createDocumentLink(documentManager, target, path, null);
    }

    /**
     * Creates a DocumentLink documentModel pointing to the target DocumentModel.
     * This methods fetched the CoreSession from the target DocumentModel.
     *
     * @param target DocumentModel that must be pointed by the DocumentLink
     * @param path path where the new DocumentLink DocumentModel must be created
     * @return
     * @throws ClientException
     */
    public static DocumentLinkAdapter createDocumentLink(DocumentModel target,
            String path) throws ClientException {
        return createDocumentLink(null, target, path, null);
    }


    public static DocumentLinkAdapter createDocumentLink(
            CoreSession documentManager, DocumentModel target, String path,
            String DocumentLinkType) throws ClientException {

        return createDocumentLink(documentManager,target,path,DocumentLinkType, true);
    }



    /**
     * Creates a DocumentLink documentModel pointing to the target DocumentModel.
     *
     * @param documentManager client CoreSession
     * @param target DocumentModel that must be pointed by the DocumentLink
     * @param path path where the new DocumentLink DocumentModel must be created
     * @param DocumentLinkType  type of the DocumentModel that must be created to represent the DocumentLink
     * @return
     * @throws ClientException
     */
    public static DocumentLinkAdapter createDocumentLink(
            CoreSession documentManager, DocumentModel target, String path,
            String DocumentLinkType, Boolean saveTarget) throws ClientException {

        if (documentManager == null) {
            documentManager = CoreInstance.getInstance().getSession(
                    target.getSessionId());
        }

        if (DocumentLinkType == null)
            DocumentLinkType = "DocumentLink";

        String link_id = target.getName() + "_lnk";

        DocumentModel link = documentManager.createDocumentModel(path, link_id,
                DocumentLinkType);

        link = documentManager.createDocument(link);

        DocumentLinkAdapter docLink = link.getAdapter(DocumentLinkAdapter.class);

        if (docLink == null)
            return null;

        docLink.setTargetDocument(target);

        docLink.setProperty("dublincore", "title", target.getTitle() + "(Link)");

        return docLink.save(!saveTarget);
    }

    /**
     * Stores the supplied DocumentModel in the central repository and creates a DocumentLink pointing to it.
     *
     * @param documentManager client CoreSession
     * @param doc the DocumentModel that contains the data to be stored
     * @param linkPath path where the new DocumentLink must be created
     * @param DocumentLinkType type of the DocumentModel that must be created to represent the DocumentLink
     * @return
     * @throws ClientException
     */
    public static DocumentLinkAdapter createDocumentInCentralRepository(
            CoreSession documentManager, DocumentModel doc, String linkPath,
            String DocumentLinkType) throws ClientException {
        if (documentManager == null) {
            documentManager = CoreInstance.getInstance().getSession(
                    doc.getSessionId());
        }

        if (DocumentLinkType == null)
            DocumentLinkType = "DocumentLink";

        DocumentModel target = DocRepositoryHelper.createDocumentInCentralRepository(
                documentManager, doc);

        return createDocumentLink(documentManager, target, linkPath,DocumentLinkType,true);
    }
}
