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

/**
 *
 * Constants used by DocumentLink system
 *
 *  @author <a href="mailto:td@nuxeo.com">Thierry Delprat</a>
 *
 *
 */
public interface DocumentLinkConstants {

    /**
     * Name of the schema used to define target document
     */
    public static final String DOCUMENT_LINK_SCHEMA_NAME = "documentlink";

    /**
     * Name of the field inside the dedicatde schema used to define the target document
     */
    public static final String DOCUMENT_LINK_FIELD_NAME = "linkedDocumentRef";

}
