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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.common.collections.ScopeType;
import org.nuxeo.common.collections.ScopedMap;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DataModel;
import org.nuxeo.ecm.core.api.DataModelMap;
import org.nuxeo.ecm.core.api.DocumentException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.impl.DataModelMapImpl;
import org.nuxeo.ecm.core.api.model.DocumentPart;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.schema.DocumentType;

/**
 * DocumentLinkAdapter implementation.
 * This implementation wraps the standard DocumentModelImpl to proxy get/set methods on link or target document.
 *
 * @author <a href="mailto:td@nuxeo.com">Thierry Delprat</a>
 *
 */
public class DocumentLinkAdapterImpl implements DocumentLinkAdapter {

    private static final long serialVersionUID = 1L;

    protected DocumentModel proxy;

    protected DocumentModel target;


    protected String adapter_sid=null;


    /**
     * Constructs an Adapter given a link documentModel
     *
     * @param proxy
     * @throws DocumentLinkException
     */
    public DocumentLinkAdapterImpl(DocumentModel proxy)
            throws DocumentLinkException {
        this.proxy = proxy;

        if (getSessionId()!=null)
            this.target = resolveTargetDocument(proxy);
    }

    /**
     * Constructs an Adapter given a link documentModel and a target documentModel
     * @param proxy
     * @param target
     */
    public DocumentLinkAdapterImpl(DocumentModel proxy, DocumentModel target) {
        this.proxy = proxy;
        setTargetDocument(target);
    }

    protected DocumentModel resolveTargetDocument(DocumentModel proxy)
            throws DocumentLinkException {

        String ref = (String) proxy.getProperty(
                DocumentLinkConstants.DOCUMENT_LINK_SCHEMA_NAME,
                DocumentLinkConstants.DOCUMENT_LINK_FIELD_NAME);

        if (ref == null || "".equals(ref)) {
            return null;
        }

        DocumentRef targetRef = null;
        if (ref.startsWith("/")) {
            targetRef = new PathRef(ref);
        } else {
            targetRef = new IdRef(ref);
        }

        String sid = getSessionId();
        if (sid == null) {
            throw new DocumentLinkException(
                    "can not create a link on a deconnected DocumentModel");
        }

        try {
            DocumentModel target = CoreInstance.getInstance().getSession(sid).getDocument(
                    targetRef);
            return target;
        } catch (ClientException e) {
            throw new DocumentLinkException(
                    "Unable to resolve linked document", e);
        }
    }

    // specifc interafce impl


    /**
     * @see DocumentLinkAdapter.getUnmaskedSchemas
     */
    public List<String> getUnmaskedSchemas()
    {
        List<String>  unmaskedSchemas = new ArrayList<String>();

        unmaskedSchemas.addAll((List<String>)Arrays.asList(target.getDeclaredSchemas()));
        unmaskedSchemas.removeAll((List<String>)Arrays.asList(proxy.getDeclaredSchemas()));
        unmaskedSchemas.addAll(getPassThoughtSchemas());

        return unmaskedSchemas;
    }

    /**
     * @see DocumentLinkAdapter.getPassThoughtSchemas
     */
    public List<String> getPassThoughtSchemas()
    {
        String [] schemas = (String []) proxy.getProperty(
                DocumentLinkConstants.DOCUMENT_LINK_SCHEMA_NAME,"passthroughtSchemas");
        if (schemas==null)
            return new ArrayList<String>();

        return (List<String>)Arrays.asList(schemas);
    }


    public void reconnect(String sid) throws DocumentLinkException
    {
        adapter_sid=sid;
        this.target = resolveTargetDocument(proxy);
    }

    public DocumentLinkAdapter save() throws ClientException
    {
        return save(false);
    }

    /**
     * @see DocumentLinkAdapter.save
     */
    public DocumentLinkAdapter save(Boolean skipTarget) throws ClientException
    {
        String sid = getSessionId();
        CoreSession session = CoreInstance.getInstance().getSession(sid);

        proxy = session.saveDocument(proxy);
        if (!skipTarget)
            target = session.saveDocument(target);

        // XXX : need to set "this" as adapter on proxy !!!

        return this;
    }

    /**
     * @see DocumentLinkAdapter.getCanWriteOnTargetDocument
     */
    public boolean getCanWriteOnTargetDocument() throws ClientException
    {
        String sid = getSessionId();
        CoreSession session = CoreInstance.getInstance().getSession(sid);

        return session.hasPermission(target.getRef(), SecurityConstants.WRITE);
    }

    /**
     * @see DocumentLinkAdapter.isBroken
     */
    public boolean isBroken() {
        if (target == null)
            return true;
        String sid = getSessionId();
        try {
            return !CoreInstance.getInstance().getSession(sid).exists(
                    target.getRef());
        } catch (ClientException e) {
            return true;
        }
    }

    /**
     * @see DocumentLinkAdapter.setTargetDocument
     */
    public void setTargetDocument(DocumentModel target) {
        proxy.setProperty(DocumentLinkConstants.DOCUMENT_LINK_SCHEMA_NAME,
                DocumentLinkConstants.DOCUMENT_LINK_FIELD_NAME,
                target.getRef().toString());

        this.target = target;
    }

    /**
     * @see DocumentLinkAdapter.getLinkDocument
     */
    public DocumentModel getLinkDocument() {
        return proxy;
    }

    /**
     * @see DocumentLinkAdapter.getTargetDocument
     */
    public DocumentModel getTargetDocument() {
        return target;
    }



    // DocumentModel Interface implementation

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.clone
     */
    public DocumentModel clone() throws CloneNotSupportedException {
        return proxy.clone();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.copyContent
     */
    public void copyContent(DocumentModel sourceDoc) {
        proxy.copyContent(sourceDoc);
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.copyContextData
     */
    public void copyContextData(DocumentModel otherDocument) {
        proxy.copyContextData(otherDocument);
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.followTransition
     */
    public boolean followTransition(String transition) throws ClientException {
        return proxy.followTransition(transition);
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getACP
     */
    public ACP getACP() {
        return proxy.getACP();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getAdapter
     */
    public <T> T getAdapter(Class<T> itf) {
        return getAdapter(itf, false);
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getAdapter
     */
    public <T> T getAdapter(Class<T> itf, boolean refreshCache) {
        T adapter = proxy.getAdapter(itf);
        if (adapter == null) {
            adapter = target.getAdapter(itf);
        }
        return adapter;
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getAllowedStateTransitions
     */
    public Collection<String> getAllowedStateTransitions()
            throws ClientException {
        return proxy.getAllowedStateTransitions();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getCacheKey
     */
    public String getCacheKey() {
        return proxy.getCacheKey();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getContextData
     */
    public ScopedMap getContextData() {
        return proxy.getContextData();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getContextData
     */
    public Serializable getContextData(String key) {
        return proxy.getContextData(key);
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getContextData
     */
    public Serializable getContextData(ScopeType scope, String key) {
        return proxy.getContextData(scope, key);
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel
     */
    public String getCurrentLifeCycleState() throws ClientException {
        return proxy.getCurrentLifeCycleState();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getDataModel
     */
    public DataModel getDataModel(String schema) {
        DataModel dm = proxy.getDataModel(schema);
        if (dm == null || getPassThoughtSchemas().contains(schema)) {
            dm = target.getDataModel(schema);
        }
        return dm;
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getDataModels
     */
    public DataModelMap getDataModels() {
        DataModelMap map = new DataModelMapImpl();
        map.putAll(proxy.getDataModels());
        map.putAll(target.getDataModels());
        return map;
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getDataModelsCollection
     */
    public Collection<DataModel> getDataModelsCollection() {
        Collection<DataModel> collec = proxy.getDataModelsCollection();
        collec.addAll(target.getDataModelsCollection());
        return collec;
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getDeclaredFacets
     */
    public Set<String> getDeclaredFacets() {
        return proxy.getDeclaredFacets();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getDeclaredSchemas
     */
    public String[] getDeclaredSchemas() {

        List<String> schemas = new ArrayList<String>();
        schemas.addAll(Arrays.asList(proxy.getDeclaredSchemas()));
        schemas.addAll(Arrays.asList(target.getDeclaredSchemas()));

        String[] schemaArray = new String[schemas.size()];
        schemas.toArray(schemaArray);
        return schemaArray;
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getDocumentType
     */
    public DocumentType getDocumentType() {
        return target.getDocumentType();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getFlags
     */
    public long getFlags() {
        return proxy.getFlags();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getId
     */
    public String getId() {
        return proxy.getId();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getLifeCyclePolicy
     */
    public String getLifeCyclePolicy() throws ClientException {
        return proxy.getLifeCyclePolicy();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getLock
     */
    public String getLock() {
        return proxy.getLock();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getName
     */
    public String getName() {
        return proxy.getName();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getParentRef
     */
    public DocumentRef getParentRef() {
        return proxy.getRef();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getPart
     */
    public DocumentPart getPart(String schema) {
        DocumentPart dp = proxy.getPart(schema);
        if (dp == null || getPassThoughtSchemas().contains(schema)) {
            dp = target.getPart(schema);
        }
        return dp;
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getParts
     */
    public DocumentPart[] getParts() {
        DocumentPart[] proxyParts = proxy.getParts();
        DocumentPart[] targetParts = target.getParts();

        List<DocumentPart> allparts = new ArrayList<DocumentPart>();

        allparts.addAll(Arrays.asList(proxyParts));
        allparts.addAll(Arrays.asList(targetParts));

        DocumentPart[] parts = new DocumentPart[allparts.size()];

        allparts.toArray(parts);
        return parts;
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getPath
     */
    public Path getPath() {
        return proxy.getPath();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getPathAsString
     */
    public String getPathAsString() {
        return proxy.getPathAsString();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getPrefetch
     */
    public Map<String, Serializable> getPrefetch() {
        Map<String, Serializable> prefetchs = target.getPrefetch();
        prefetchs.putAll(proxy.getPrefetch());
        return prefetchs;
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getProperties
     */
    public Map<String, Object> getProperties(String schemaName) {
        Map<String, Object> props = target.getProperties(schemaName);
        if (getPassThoughtSchemas().contains(schemaName))
        {
            props.putAll(proxy.getProperties(schemaName));
        }
        return props;
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getProperty
     */
    public Property getProperty(String xpath) throws PropertyException {

        Property prop = null;

        try {
            prop = proxy.getProperty(xpath);
        }
        catch (PropertyException e) {
            // ignore and try again
        }
        if (prop == null) {
            prop = target.getProperty(xpath);
        }
        return prop;
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getProperty
     */
    public Object getProperty(String schemaName, String name) {

        // always return the icon of the target doc
        if (schemaName.equals("common") && name.equals("icon"))
            return target.getProperty("common", "icon");

        Object prop = proxy.getProperty(schemaName, name);
        if (prop == null || getPassThoughtSchemas().contains(schemaName)) {
            prop = target.getProperty(schemaName, name);
        }
        return prop;
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getPropertyValue
     */
    public Serializable getPropertyValue(String xpath) throws PropertyException {
        Serializable propValue = null;
        try  {
            propValue=  proxy.getPropertyValue(xpath);
        }
        catch (PropertyException e) {
            // ignore and try again
        }

        if (propValue == null ) {
            propValue = target.getPropertyValue(xpath);
        }
        return propValue;
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getRef
     */
    public DocumentRef getRef() {
        return proxy.getRef();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getRepositoryName
     */
    public String getRepositoryName() {
        return proxy.getRepositoryName();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getSessionId
     */
    public String getSessionId() {
        if (adapter_sid!=null)
            return adapter_sid;

        adapter_sid = proxy.getSessionId();

        return adapter_sid;
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getSourceId
     */
    public String getSourceId() {
        return proxy.getSourceId();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getSystemProp
     */
    public <T extends Serializable> T getSystemProp(String systemProperty,
            Class<T> type) throws ClientException, DocumentException {
        T prop = proxy.getSystemProp(systemProperty, type);
        if (prop == null) {
            prop = target.getSystemProp(systemProperty, type);
        }
        return prop;
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getTitle
     */
    public String getTitle() {
        String title = (String) getProperty("dublincore", "title");
        if (title != null) {
            return title;
        }
        title = getName();
        if (title != null) {
            return title;
        }
        return getId();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getType
     */
    public String getType() {
        return proxy.getType();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.getVersionLabel
     */
    public String getVersionLabel() {
        return proxy.getVersionLabel();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.hasFacet
     */
    public boolean hasFacet(String facet) {
        if (getDeclaredFacets().contains(facet))
            return true;
        return false;
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.hasSchema
     */
    public boolean hasSchema(String schema) {
        if (proxy.hasSchema(schema))
            return true;
        if (target.hasSchema(schema))
            return true;
        return false;
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.isDownloadable
     */
    public boolean isDownloadable() {
        return proxy.isDownloadable();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.isFolder
     */
    public boolean isFolder() {
        return proxy.isFolder();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.isLifeCycleLoaded
     */
    public boolean isLifeCycleLoaded() {
        return proxy.isLifeCycleLoaded();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.isLocked
     */
    public boolean isLocked() {
        return proxy.isLocked();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.isProxy
     */
    public boolean isProxy() {
        return proxy.isProxy();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.isVersion
     */
    public boolean isVersion() {
        return proxy.isVersion();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.isVersionable
     */
    public boolean isVersionable() {
        return proxy.isVersionable();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.prefetchCurrentLifecycleState
     */
    public void prefetchCurrentLifecycleState(String lifecycle) {
        proxy.prefetchCurrentLifecycleState(lifecycle);
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.prefetchLifeCyclePolicy
     */
    public void prefetchLifeCyclePolicy(String lifeCyclePolicy) {
        proxy.prefetchLifeCyclePolicy(lifeCyclePolicy);
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.prefetchProperty
     */
    public void prefetchProperty(String id, Object value) {
        proxy.prefetchProperty(id, value);
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.putContextData
     */
    public void putContextData(String key, Serializable value) {
        proxy.putContextData(key, value);
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.putContextData
     */
    public void putContextData(ScopeType scope, String key, Serializable value) {
        proxy.putContextData(scope, key, value);
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.reset
     */
    public void reset() {
        proxy.reset();
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.setACP
     */
    public void setACP(ACP acp, boolean overwrite) {
        proxy.setACP(acp, overwrite);
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.setLock
     */
    public void setLock(String key) throws ClientException {
        proxy.setLock(key);
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.setPathInfo
     */
    public void setPathInfo(String parentPath, String name) {
        proxy.setPathInfo(parentPath, name);
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.setProperties
     */
    public void setProperties(String schemaName, Map<String, Object> data) {
        if (proxy.hasSchema(schemaName) && !getPassThoughtSchemas().contains(schemaName)) {
            proxy.setProperties(schemaName, data);
        } else if (target.hasSchema(schemaName)) {
            target.setProperties(schemaName, data);
        }
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.setProperty
     */
    public void setProperty(String schemaName, String name, Object value) {
        if (proxy.hasSchema(schemaName) && !getPassThoughtSchemas().contains(schemaName)) {
            proxy.setProperty(schemaName, name, value);
        } else if (target.hasSchema(schemaName)) {
            target.setProperty(schemaName, name, value);
        }
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.setPropertyValue
     */
    public void setPropertyValue(String xpath, Serializable value)
            throws PropertyException {
        try {
            proxy.setPropertyValue(xpath, value);
        } catch (PropertyException e) {
            target.setPropertyValue(xpath, value);
        }
    }

    /**
     * @see org.nuxeo.ecm.core.api.DocumentModel.unlock
     */
    public void unlock() throws ClientException {
        proxy.unlock();
    }

}
