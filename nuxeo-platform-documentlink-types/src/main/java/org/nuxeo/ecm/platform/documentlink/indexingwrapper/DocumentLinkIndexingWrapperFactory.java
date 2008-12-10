package org.nuxeo.ecm.platform.documentlink.indexingwrapper;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.search.api.indexingwrapper.DocumentIndexingWrapperFactory;
import org.nuxeo.ecm.platform.documentlink.api.DocumentLinkAdapter;

public class DocumentLinkIndexingWrapperFactory implements
        DocumentIndexingWrapperFactory {

    public DocumentModel getIndexingWrapper(DocumentModel doc) {
        DocumentLinkAdapter dl = doc.getAdapter(DocumentLinkAdapter.class);
        if (dl == null) {
            return doc;
        } else {
            return dl;
        }
    }

}
