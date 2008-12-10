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

package org.nuxeo.ecm.platform.documentrepository.service.plugin.base;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.documentrepository.api.DocRepository;
import org.nuxeo.ecm.platform.documentrepository.service.plugin.DocumentRepositoryPlugin;

/**
 * Default repository plugin.
 * <p>
 * Only overides the getDocumentRepository method to implement a central repository logic:
 * only one repository is available for a given core instance.
 *
 * @author <a href="mailto:td@nuxeo.com">Thierry Delprat</a>
 */
public class DefaultDocumentRepositoryPlugin extends AbstractDocumentRepositoryPlugin implements
        DocumentRepositoryPlugin {

    public static final String CENTRAL_REPOSITORY_ID = "repository";

    public DocRepository getDocumentRepository(CoreSession coreSession,
            DocumentModel context) throws ClientException {

        DocumentRef repoRef = new PathRef(
                coreSession.getRootDocument().getPathAsString() + "/" + CENTRAL_REPOSITORY_ID);

        if (!coreSession.exists(repoRef)) {
            repoRef = createRepository(
                    coreSession.getRepositoryName(), coreSession.getRootDocument().getPathAsString(), CENTRAL_REPOSITORY_ID);
        }
        DocumentModel repo = coreSession.getDocument(repoRef);
        return repo.getAdapter(DocRepository.class);
    }

}
