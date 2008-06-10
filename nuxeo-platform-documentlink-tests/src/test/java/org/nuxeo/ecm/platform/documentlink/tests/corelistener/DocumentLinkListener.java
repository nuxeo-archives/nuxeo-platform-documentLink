package org.nuxeo.ecm.platform.documentlink.tests.corelistener;

import static org.nuxeo.ecm.core.api.event.DocumentEventTypes.DOCUMENT_CREATED;

import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.event.CoreEvent;
import org.nuxeo.ecm.core.listener.AbstractEventListener;
import org.nuxeo.ecm.platform.documentrepository.api.helper.DocRepositoryHelper;

public class DocumentLinkListener extends AbstractEventListener {

    public static final String AUTO_CREATE_LINK = "AutoCreateLink";

    @Override
    public void handleEvent(CoreEvent coreEvent) throws Exception {

    }

    //@Override
    public void handleEvent_old(CoreEvent coreEvent) throws Exception {

        Object source = coreEvent.getSource();
        if (source instanceof DocumentModel) {
            DocumentModel doc = (DocumentModel) source;
            String eventId = coreEvent.getEventId();

            if (!eventId.equals(DOCUMENT_CREATED)) {
                return;
            }

            if (doc.getContextData().containsKey(AUTO_CREATE_LINK)) {
                doc.getContextData().remove(AUTO_CREATE_LINK);
                DocumentModel docInRepo = doc.clone();
                CoreSession coreSession = CoreInstance.getInstance().getSession(
                        doc.getSessionId());
                DocumentModel context = coreSession.getDocument(new PathRef(
                        doc.getPath().removeLastSegments(1).toString()));
                doc = DocRepositoryHelper.createDocumentInCentralRepository(
                        coreSession, docInRepo, context);
            }

        }

    }
}
