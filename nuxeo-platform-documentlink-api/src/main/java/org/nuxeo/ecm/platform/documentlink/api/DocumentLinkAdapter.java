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
package org.nuxeo.ecm.platform.documentlink.api;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Adapter interface for application level proxies.
 *
 * @author <a href="mailto:td@nuxeo.com">Thierry Delprat</a>
 */
public interface DocumentLinkAdapter extends DocumentModel {

    /**
     * Gets the link document : ie the proxy behind the adapter
     *
     * @return the link document
     */
    DocumentModel getLinkDocument();

    /**
     * Gets the targeted document behind the proxy.
     * May be null if the DocumentLink has not be set.
     *
     * @return the target document
     */
    DocumentModel getTargetDocument();

    /**
     * Sets the target document.
     *
     * @param target the new target document
     */
    void setTargetDocument(DocumentModel target) throws ClientException;

    /**
     * Checks if the link is broken.
     * Returns true is the target document is not set, or if the target document does not exists.
     *
     * @return true, if is broken
     */
    boolean isBroken();


    /**
     * Saves modifications on the underlying DocumentModel.
     * Because DocumentLinkAdapter provides access transparently to two DocumentModels (the target and the link),
     * this method is usefull to provide a unique save method.
     *
     * @return the document link adapter
     *
     * @throws ClientException the client exception
     */
    DocumentLinkAdapter save() throws ClientException;


    /**
     * Saves modifications on the underlying DocumentModel.
     * Because DocumentLinkAdapter provides access transparently to two DocumentModels (the target and the link),
     * this method is usefull to provide a unique save method.
     *
     * @param skipTarget : indicates if target document must be saved or not
     *
     * @return the document link adapter
     *
     * @throws ClientException the client exception
     */
    DocumentLinkAdapter save(Boolean skipTarget) throws ClientException;

    /**
     * Returns the list of schemas that are never taken from the link document but directly from the target document.     *
     *
     * @return the pass thought schemas
     */
    List<String> getPassThoughtSchemas() throws ClientException;

    /**
     * Return the list of schemas that are implemented by the target document but not by the link document.
     *
     * @return the unmasked schemas
     */
    List<String> getUnmaskedSchemas() throws ClientException;

    /**
     * Check if user has write access on the target document
     *
     * @return true or false
     *
     * @throws ClientException the client exception
     */
    boolean getCanWriteOnTargetDocument() throws ClientException;


    /**
     * reconnect the adapter to a specific core session
     *
     * @param sid
     * @throws DocumentLinkException
     */
    public void reconnect(String sid) throws ClientException;

}
