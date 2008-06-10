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

public class SampleDocumentRepositoryPlugin extends DefaultDocumentRepositoryPlugin {

    @Override
    public DocRepository getDocumentRepository(CoreSession coreSession,
            DocumentModel context) throws ClientException {

        DocumentModel repo = null;

        String startPath=null;
        if (context!=null && ! "/".equals(context.getPathAsString()))
        {
            startPath = "/" + context.getPath().segment(0).toString();
        }
        else
        {
            startPath=coreSession.getRootDocument().getPathAsString();
        }

        DocumentRef repoRef = new PathRef(startPath + "/" + CENTRAL_REPOSITORY_ID);

        if (!coreSession.exists(repoRef))
        {
            repoRef = super.createRepository(coreSession.getRepositoryName(), startPath, CENTRAL_REPOSITORY_ID);
        }
        repo = coreSession.getDocument(repoRef);
        return repo.getAdapter(DocRepository.class);
    }

}
