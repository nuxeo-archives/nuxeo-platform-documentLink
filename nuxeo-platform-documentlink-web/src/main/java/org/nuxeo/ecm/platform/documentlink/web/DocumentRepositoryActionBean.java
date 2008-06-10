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

package org.nuxeo.ecm.platform.documentlink.web;

import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.ScopeType.EVENT;

import java.io.Serializable;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.documentrepository.api.DocRepository;
import org.nuxeo.ecm.platform.documentrepository.api.helper.DocRepositoryHelper;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.base.InputController;

@Name("documentRepositoryActions")
@Scope(CONVERSATION)
public class DocumentRepositoryActionBean extends InputController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    @In(create = true)
    private transient NavigationContext navigationContext;

    @In(create = true, required = false)
    private transient CoreSession documentManager;

    @Factory(value = "currentDocumentRepository", scope = EVENT)
    public DocRepository getCurrentDocumentRepository() throws ClientException{
        DocumentModel currentDocument = navigationContext.getCurrentDocument();

        return DocRepositoryHelper.getDocumentRepository(documentManager,currentDocument);
    }




}
