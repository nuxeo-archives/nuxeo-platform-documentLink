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
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.documentlink.api.DocumentLinkAdapter;
import org.nuxeo.ecm.platform.documentlink.api.helper.DocumentLinkHelper;
import org.nuxeo.ecm.platform.ejb.EJBExceptionHandler;
import org.nuxeo.ecm.platform.forms.layout.service.WebLayoutManager;
import org.nuxeo.ecm.platform.types.TypeManager;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.base.InputController;
import org.nuxeo.runtime.api.Framework;

@Name("documentLinkActions")
@Scope(CONVERSATION)
public class DocumentLinkActionBean extends InputController implements Serializable {

    private static final long serialVersionUID = 1L;

    @In(create = true)
    private transient NavigationContext navigationContext;

    @In(create = true, required = false)
    private transient CoreSession documentManager;

    @In(required = true)
    private transient TypeManager typeManager;

    private transient WebLayoutManager layoutManager;

    private WebLayoutManager getLayoutManager() throws Exception {
        if (layoutManager == null) {
            layoutManager = Framework.getService(WebLayoutManager.class);
        }

        return layoutManager;
    }

    @Factory(value = "currentDocumentLink", scope = EVENT)
    public DocumentModel getCurrentDocumentLink() {
        DocumentModel doc = navigationContext.getChangeableDocument();
        DocumentModel docLink = doc.getAdapter(DocumentLinkAdapter.class);

        if (docLink != null) {
            return docLink;
        }
        return doc;
    }

    protected DocumentLinkAdapter saveDocumentLink(DocumentLinkAdapter docLink) throws ClientException {
        return docLink.save();
    }

    public String createDocumentLinkInCurrentPath(DocumentModel target) throws ClientException {
        String path = navigationContext.getCurrentDocument().getPathAsString();
        return createDocumentLink(target, path);
    }

    public String createDocumentLink(DocumentModel target, String path) throws ClientException {
        DocumentLinkAdapter docLink = DocumentLinkHelper.createDocumentLink(
                documentManager, target, path, "DocumentLink");

        docLink.setProperty("dublincore", "title", target.getTitle());

        docLink = saveDocumentLink(docLink);

        documentManager.save();
        facesMessages.add(FacesMessage.SEVERITY_INFO,
                resourcesAccessor.getMessages().get("document_created"),
                resourcesAccessor.getMessages().get(
                        docLink.getType()));
        eventManager.raiseEventsOnDocumentCreate(docLink);
        return navigationContext.navigateToDocument(docLink, "after-create");
    }

    public String updateDocument() throws ClientException {
        try {
            DocumentModel docLink = getCurrentDocumentLink();
            docLink = saveDocumentLink((DocumentLinkAdapter) docLink);
            documentManager.save();
            facesMessages.add(FacesMessage.SEVERITY_INFO,
                    resourcesAccessor.getMessages().get("document_modified"),
                    resourcesAccessor.getMessages().get(
                            docLink.getType()));
            eventManager.raiseEventsOnDocumentChange(docLink);
            return navigationContext.navigateToDocument(docLink,
                    "after-edit");
        } catch (Throwable t) {
            throw EJBExceptionHandler.wrapException(t);
        }
    }


    public List<SelectItem> getAvailableSchemas() throws ClientException {
        List<SelectItem> selectItemList = new ArrayList<SelectItem>();

        // XXX dummy impl !!!
        selectItemList.add(new SelectItem("dublincore", "dublincore"));
        selectItemList.add(new SelectItem("note", "note"));
        selectItemList.add(new SelectItem("file", "file"));
        selectItemList.add(new SelectItem("files", "files"));

        return selectItemList;
    }

    public List<String> getAutomaticLayoutsForEdit() throws Exception {
        DocumentLinkAdapter link = (DocumentLinkAdapter) getCurrentDocumentLink();

        List<String> unmaskedSchemas = link.getUnmaskedSchemas();

        List<String> layouts = new ArrayList<String>();

        if (unmaskedSchemas == null) {
            return layouts;
        }

        for (String schema : unmaskedSchemas) {
            if (getLayoutManager().getLayoutDefinition(schema) != null) {
                layouts.add(schema);
            }
        }

        return layouts;
    }

    public boolean isLayoutAvailableForEdit(String layoutName) throws Exception {
        return getAutomaticLayoutsForEdit().contains(layoutName);
    }
}
