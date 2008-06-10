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

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;

/**
 *
 * Interface for the adapted Application Level Document Repository
 *
 *  @author <a href="mailto:td@nuxeo.com">Thierry Delprat</a>
 *
 */
public interface DocRepository {

    static final String CENTRAL_REPOSITORY_ID="repository";

    /**
     *
     * Creates a DocumentModel in the repository given it's type and it's title
     *
     * @param type
     * @param title
     * @return
     * @throws ClientException
     */
    DocumentModel createDocument(String type, String title) throws ClientException;

    /**
     * Stores a DocumentModel inside the repository
     *
     * @param doc
     * @return
     * @throws ClientException
     */
    DocumentModel createDocument(DocumentModel doc) throws ClientException;

    /**
     * get a DocumentModel from the repository
     *
     * @param docRef
     * @return
     * @throws ClientException
     */
    DocumentModel getDocument(DocumentRef docRef) throws ClientException;

    /**
     *
     * Removes a documentModel from the repository
     *
     * @param docRef
     * @throws ClientException
     */
    void removeDocument(DocumentRef docRef) throws ClientException;

    /**
     *
     * Returns the DocumentModel behind the adapter
     *
     * @return
     */
    DocumentModel getRepoDoc();

    /**
     * returns all proxies pointing to a document in the repository
     *
     * @param docref
     * @return
     */
    DocumentModelList getProxiesForDocument(DocumentRef docref) throws ClientException;
}
