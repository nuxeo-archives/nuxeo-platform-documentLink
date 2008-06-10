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

package org.nuxeo.ecm.platform.documentrepository.service.api;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.documentrepository.api.DocRepository;

/**
 *
 * Interface of the Service dedicated to managing DocReposyories
 *
 * @author <a href="mailto:td@nuxeo.com">Thierry Delprat</a>
 *
 */
public interface DocumentRepositoryManager {

    /**
     * returns the DocRepository in the context of the provided DocumentModel
     *
     * @param coreSession
     * @param context
     * @return
     * @throws ClientException
     */
    DocRepository getDocumentRepository(CoreSession coreSession, DocumentModel context) throws ClientException;

    /**
     *
     * Create a DocumentModel in the provided DocRepository
     *
     * @param clientSession
     * @param repo
     * @param typeName
     * @param title
     * @return
     * @throws ClientException
     */
    DocumentModel createDocumentInRepository(CoreSession clientSession,DocumentModel repo, String typeName, String title) throws ClientException;

    /**
     * Store a DocumentModel into the provided DocRepository
     *
     * @param clientSession
     * @param repo
     * @param doc
     * @return
     * @throws ClientException
     */
    DocumentModel createDocumentInRepository(CoreSession clientSession,DocumentModel repo, DocumentModel doc) throws ClientException;

}
