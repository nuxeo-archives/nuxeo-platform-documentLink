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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

/**
 * Factory for DocumentLinkAdapter.
 * Returns an adapter on any DocumentModel that has the documentLink schema.
 *
 * @author <a href="mailto:td@nuxeo.com">Thierry Delprat</a>
 */
public class DocumentLinkAdapterFactory implements DocumentAdapterFactory {

    private static final Log log = LogFactory.getLog(DocumentLinkAdapterFactory.class);

    public Object getAdapter(DocumentModel doc, Class itf) {

        if (doc.hasSchema(DocumentLinkConstants.DOCUMENT_LINK_SCHEMA_NAME)) {
            try {
                return new DocumentLinkAdapterImpl(doc);
            } catch (DocumentLinkException e) {
                log.error("Error while creating DocumentLink adapter", e);
                return null;
            } catch (ClientException e) {
                log.error("Error while creating DocumentLink adapter", e);
                return null;
            }
        } else {
            return null;
        }
    }

}
