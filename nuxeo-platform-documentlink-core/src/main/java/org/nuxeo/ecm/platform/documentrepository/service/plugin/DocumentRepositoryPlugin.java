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

package org.nuxeo.ecm.platform.documentrepository.service.plugin;

import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.documentrepository.api.DocRepository;

/**
 *
 * Interface of the Plugin of the DocumentRepositoryService
 *
 * @author <a href="mailto:td@nuxeo.com">Thierry Delprat</a>
 *
 */
public interface DocumentRepositoryPlugin {

    DocRepository getDocumentRepository(CoreSession coreSession, DocumentModel context) throws ClientException;

    DocumentModel createDocument(CoreSession clientSession, DocumentModel repo, String typeName, String title) throws ClientException;

    DocumentModel createDocument(CoreSession clientSession,DocumentModel repo, DocumentModel doc) throws ClientException;

    void init(Map<String,String> params);


}
