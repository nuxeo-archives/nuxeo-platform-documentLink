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

package org.nuxeo.ecm.platform.documentrepository.service;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.documentrepository.api.DocRepository;
import org.nuxeo.ecm.platform.documentrepository.service.api.DocumentRepositoryManager;
import org.nuxeo.ecm.platform.documentrepository.service.plugin.DocumentRepositoryPlugin;
import org.nuxeo.ecm.platform.documentrepository.service.plugin.DocumentRepositoryPluginDescriptor;
import org.nuxeo.ecm.platform.documentrepository.service.plugin.base.DefaultDocumentRepositoryPlugin;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.ComponentName;
import org.nuxeo.runtime.model.DefaultComponent;

public class DocumentRepositoryConfigurationService extends DefaultComponent
        implements DocumentRepositoryManager {

    public static final String EP_PLUGIN = "repositoryPlugin";

    public static final ComponentName NAME = new ComponentName(
            "org.nuxeo.ecm.platform.documentrepository.service.DocumentRepositoryConfigurationService");

    protected DocumentRepositoryPlugin repoPlugin;

    protected Map<String, String> params = new HashMap<String, String>();

    @Override
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor) {

        if (EP_PLUGIN.equals(extensionPoint)) {
            registerPugin((DocumentRepositoryPluginDescriptor) contribution);
        }
    }

    @Override
    public void unregisterContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor) {
    }

    // internal

    private void registerPugin(DocumentRepositoryPluginDescriptor descriptor) {
        // clear params if needed
        if (descriptor.isResetParams()) {
            params.clear();
        }
        // merge parameters if needed
        if (descriptor.getParameters() != null) {
            params.putAll(descriptor.getParameters());
        }

        if (descriptor.getPluginClass() != null) {
            try {
                repoPlugin = (DocumentRepositoryPlugin) descriptor.getPluginClass().newInstance();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        getRepoPlugin().init(params);
    }

    protected DocumentRepositoryPlugin getRepoPlugin() {
        if (repoPlugin == null) {
            repoPlugin = new DefaultDocumentRepositoryPlugin();
            repoPlugin.init(params);
        }
        return repoPlugin;
    }

    // service interface

    public DocRepository getDocumentRepository(CoreSession coreSession,
            DocumentModel context) throws ClientException {
        return getRepoPlugin().getDocumentRepository(coreSession, context);
    }

    public DocumentModel createDocumentInRepository(CoreSession clientSession, DocumentModel repo,
            DocumentModel doc) throws ClientException {
        return getRepoPlugin().createDocument(clientSession, repo, doc);
    }

    public DocumentModel createDocumentInRepository(CoreSession clientSession,DocumentModel repo,
            String typeName, String title) throws ClientException {
        return getRepoPlugin().createDocument(clientSession,repo, typeName, title);
    }

}
