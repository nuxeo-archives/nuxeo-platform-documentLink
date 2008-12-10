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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.runtime.api.Framework;

/**
 * Helper class to handle Unrestricted core sessions
 *
 * @author <a href="mailto:td@nuxeo.com">Thierry Delprat</a>
 *
 */
public class SessionHelper {

    protected LoginContext lc;

    protected CoreSession documentManager;

    protected String currentRepositoryName;

    public void release() {
        releaseCoreSession();
        if (lc != null) {
            try {
                lc.logout();
            } catch (LoginException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    protected void releaseCoreSession() {
        if (documentManager != null) {
            // XXX cleanup to be done here
            // documentManager.
        }
        documentManager = null;
        currentRepositoryName = null;
    }

    public CoreSession getUnrestrictedDocumentManager(String coreRepositoryName)
            throws Exception {
        if (documentManager == null
                || !coreRepositoryName.equals(currentRepositoryName)) {
            try {
                lc = Framework.login();
            } catch (LoginException e) {
                throw new ClientException(
                        "Unable to login as System user to get unrestricted CoreSession",
                        e);
            }

            RepositoryManager mgr = Framework.getService(RepositoryManager.class);

            if (lc == null) {
                // Unit Tests
                Map<String, Serializable> ctx = new HashMap<String, Serializable>();
                ctx.put("username", "system");
                documentManager = mgr.getRepository(coreRepositoryName).open(ctx);
            } else {
                documentManager = mgr.getRepository(coreRepositoryName).open();
            }

            currentRepositoryName = coreRepositoryName;
        }
        return documentManager;
    }

}
