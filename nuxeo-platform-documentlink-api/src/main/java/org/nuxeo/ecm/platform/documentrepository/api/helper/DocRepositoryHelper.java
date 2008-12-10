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

package org.nuxeo.ecm.platform.documentrepository.api.helper;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.documentrepository.api.DocRepository;
import org.nuxeo.ecm.platform.documentrepository.service.api.DocumentRepositoryManager;
import org.nuxeo.runtime.api.Framework;

/**
 *
 * Helper class to maipulate DocRepository object.
 *
 *
 * @author <a href="mailto:td@nuxeo.com">Thierry Delprat</a>
 *
 */
public class DocRepositoryHelper {

    private static String drmSync = "";

    protected static DocumentRepositoryManager drm;

    private static DocumentRepositoryManager getDRM() throws ClientException {
        if (drm == null) {
            try {
                synchronized (drmSync) {
                    drm = Framework.getService(DocumentRepositoryManager.class);
                }
            } catch (Exception e) {
                throw new ClientException(
                        "Unable to get DocumentRepositoryManager service", e);
            }
        }
        return drm;
    }

    /**
     * Gets the DocRepository object given a CoreSession.
     *
     * @param coreSession
     * @return
     * @throws ClientException
     */
    public static DocRepository getDocumentRepository(CoreSession coreSession)
            throws ClientException {
        return getDocumentRepository(coreSession, null);
    }

    /**
     * Gets the DocRepository in the context of a given DocumentModel.
     * (because there may be several DocRepository)
     *
     * @param coreSession
     * @param context
     * @return
     * @throws ClientException
     */
    public static DocRepository getDocumentRepository(CoreSession coreSession,
            DocumentModel context) throws ClientException {
        return getDRM().getDocumentRepository(coreSession, context);
    }

    /**
     * Creates a DocumentModel in the repository.
     *
     * @param coreSession
     * @param type
     * @param title
     * @return
     * @throws ClientException
     */
    public static DocumentModel createDocumentInCentralRepository(
            CoreSession coreSession, String type, String title)
            throws ClientException {
        return createDocumentInCentralRepository(coreSession, type, title, null);
    }

    /**
     * Stores the given DocumentModel inside the repository.
     *
     * @param coreSession
     * @param dm
     * @return
     * @throws ClientException
     */
    public static DocumentModel createDocumentInCentralRepository(
            CoreSession coreSession, DocumentModel dm) throws ClientException {
        return createDocumentInCentralRepository(coreSession, dm, null);
    }

    /**
     *
     * Creates a DocumentModel in the repository that matches the context DocumentModel.
     *
     * @param coreSession
     * @param type
     * @param title
     * @param context
     * @return
     * @throws ClientException
     */
    public static DocumentModel createDocumentInCentralRepository(
            CoreSession coreSession, String type, String title,
            DocumentModel context) throws ClientException {
        DocRepository repo = getDocumentRepository(coreSession, context);
        return repo.createDocument(type, title);
    }

    /**
     * Stores the DocumentModel in the repository that macthes the context DocumentModel.
     *
     * @param coreSession
     * @param dm
     * @param context
     * @return
     * @throws ClientException
     */
    public static DocumentModel createDocumentInCentralRepository(
            CoreSession coreSession, DocumentModel dm, DocumentModel context)
            throws ClientException {
        DocRepository repo = getDocumentRepository(coreSession, context);
        return repo.createDocument(dm);
    }

}
