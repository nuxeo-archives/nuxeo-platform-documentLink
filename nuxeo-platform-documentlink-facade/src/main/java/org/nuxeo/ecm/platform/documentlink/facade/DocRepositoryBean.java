package org.nuxeo.ecm.platform.documentlink.facade;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.platform.documentrepository.api.DocRepository;
import org.nuxeo.runtime.api.Framework;

@Stateless
@Remote(DocRepository.class)
@Local(DocRepository.class)
public class DocRepositoryBean implements DocRepository {

    protected static DocRepository service;

    protected DocRepository getService() throws ClientException {
        if (service == null) {
            service = Framework.getLocalService(DocRepository.class);
        }

        if (service == null) {
            throw new ClientException(
                    "Unable to get local service for DocRepository");
        }

        return service;
    }

    public DocumentModel createDocument(DocumentModel doc)
            throws ClientException {
        return getService().createDocument(doc);

    }

    public DocumentModel createDocument(String type, String title)
            throws ClientException {
        return getService().createDocument(type, title);
    }

    public DocumentModel getDocument(DocumentRef docRef) throws ClientException {

        return getService().getDocument(docRef);
    }

    public DocumentModelList getProxiesForDocument(DocumentRef docref)
            throws ClientException {
        return getService().getProxiesForDocument(docref);
    }

    public DocumentModel getRepoDoc() {
        try {
            return getService().getRepoDoc();
        } catch (ClientException e) {
            return null;
        }
    }

    public void removeDocument(DocumentRef docRef) throws ClientException {
        getService().removeDocument(docRef);
    }

}
