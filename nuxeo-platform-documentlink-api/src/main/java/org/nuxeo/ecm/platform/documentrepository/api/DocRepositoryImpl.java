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

package org.nuxeo.ecm.platform.documentrepository.api;

import java.util.Random;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.platform.documentrepository.service.api.DocumentRepositoryManager;
import org.nuxeo.runtime.api.Framework;

/**
 * Implementation of the DocRepository interface.
 * Delegates real implementation to the DocumentRepositoryManager service for pluggability reasons.
 *
 * @author <a href="mailto:td@nuxeo.com">Thierry Delprat</a>
 *
 */
public class DocRepositoryImpl implements DocRepository {

    protected DocumentModel repo;

    protected transient CoreSession documentManager = null;

    protected static Random randomGen = new Random(System.currentTimeMillis());

    private static String drmSync = "";

    protected static DocumentRepositoryManager drm = null;

    public DocRepositoryImpl(DocumentModel repo) {
        this.repo = repo;
    }

    /**
     * @see DocRepository.getRepoDoc
     */
    public DocumentModel getRepoDoc() {
        return repo;
    }

    private DocumentRepositoryManager getDRM() throws ClientException {
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

    protected CoreSession getDocumentManager() {
        if (documentManager == null) {
            if (repo == null)
                return null;

            String sid = repo.getSessionId();
            if (sid == null)
                return null;
            documentManager = CoreInstance.getInstance().getSession(sid);
        }
        return documentManager;
    }

    /**
     * @see DocRepository.createDocument
     */
    public DocumentModel createDocument(String typeName, String title)
            throws ClientException {
        return getDRM().createDocumentInRepository(getDocumentManager(), repo,
                typeName, title);
    }

    /**
     * @see DocRepository.createDocument
     */
    public DocumentModel createDocument(DocumentModel doc)
            throws ClientException {
        return getDRM().createDocumentInRepository(getDocumentManager(), repo,
                doc);
    }

    /**
     * @see DocRepository.getDocument
     */
    public DocumentModel getDocument(DocumentRef docRef) throws ClientException {
        return getDocumentManager().getDocument(docRef);
    }

    /**
     * @see DocRepository.removeDocument
     */
    public void removeDocument(DocumentRef docRef) throws ClientException {
        // XXX : add check on existing proxies
        getDocumentManager().removeDocument(docRef);
    }

    public DocumentModelList getProxiesForDocument(DocumentRef docRef) throws ClientException {

        DocumentModelList proxyList = getDocumentManager().query("select * from Document where lnk:linkedDocumentRef='" + docRef.toString() + "'");
        return proxyList;
    }
}
