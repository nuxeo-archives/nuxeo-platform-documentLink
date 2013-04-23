# Nuxeo DocumentLink Addon

## Table of Contents

1. [Overview](#-overview)
1. [How does it work](#-how-does-it-work)
1. [DocumentLink and Repository](#-documentlink-and-repository)
1. [Using DocumentLink](#-using-documentlink)
1. [Using the adapter](#-using-the-adapter)
1. [Using the DocRepository](#-using-the-docrepository)

The *nuxeo-platform-documentLink* addon provides services, and adapters to handle "application level" proxies pointing to DocumentModels.

## Overview 

There are several use cases that require having the same document available from several places. Typical use cases include publication (same document visible in multiple sections), personal workspaces (user want to have some document into his workspace without copying them) ... Nuxeo Core provides a build-in feature called Proxies that can be used in most cases. The current limitations of proxy system in Nuxeo Core 1.4.x include :

* A proxy can only point to a checked-in DocumentModel (ie : a version)

* A proxy is always totally equivalent to the target DocumentModel (ie : same schemas and field values)

DocumentLink provides a proxy system implementation on top of the Core using the DocumentModel adapter system. DocumentLink extends the Core proxy system to provide some additional features :

* A DocumentLink can point to a checked-in or checked-out DocumentModel

* A DocumentLink can have it's own schemas and fields.

* A DocumentLink can mask some of the schemas/fields of the target DocumentModel

As an example, if you have a DocumentModel DocA with title "Document A" and description "description A", you can create a DocumentLink DLA that will point to DocA and have title "DocLink A" but will always return the description contained in DocA.

## How does it work

The DocumentLink system uses DocumentModelAdapter to adapt the default DocumentModel implementation to a specific implementation that handles the logic for dispatching attributes access across the DocumentLink and the target Document.

The DocumentLink adapter (DocumentLinkAdapter)implements the DocumentModel interface and will by default have the felowing behavior :

* get internal DocumentModel value if the target schema/field is available.

* return the value stored inside the target DocumentModel otherwise.

The reference of the target DocumentModel is stored in a dedicated schemas (named documentLink).

This specific schema contains :

* target DocumentRef

* target DocumentRepository

* list of schema that will never be masked by the DocumentLink

This means that for this schema the DocumentLink will always return the values stored in the target DocumentModel even if the DocumentLink it self has these schemas.

* list of fields xPaths that will never be masked by the DocumentLink

The DocumentLink package also provides a indexing wrapper that will be used during indexing. This allows the DocumentLink to be indexed as expected.

## DocumentLink and Repository

In implementation projects using DocumentLink, we usually don't really care to know where the real (target) DocumentModel will be stored, because the user will probably always manipulate the DocumentModel through DocumentLinks.

In a way, it means that the real DocumentModels are "somewhere is space" and the user navigate via a hierarchy of DocumentLinks.

For that purpose, the documentLink package provide a DocRepository service that will manage the storage of the real DocumentModel.

"Real DocumentModel" won't be stored "in space" but in a hidden folder structure that will dispatch the DocumentModels in a hidden tree.

The repository storage is pluggable so you can define :

* the sub directory tree structure

* how rights are set

## Using DocumentLink

In order to use DocumentLink feature you need to install the nuxeo-platform-documenntlink packages into your nuxeo. For that, just copy the jars into nuxeo.ear/plugins and restart your server.

On a stock Nuxeo EP, documentLink won't give you much visible features : it provides some new API, but since the default Web Application does not use it, it won't be really useful. The DocumentLink Addon must be considered as a new API.

One of the DocumentLink package is dedicated to unit tests (nuxeo-platform-documentlink-tests), it can be used as a good sample of code for using all DocumentLink APIs.

Nevertheless, here are some simple examples :

## Using the adapter

The DocumentLinkAdapter is available on any DocumentModel that has the DocumentLink Schema. Using the adapter you can define the target DocumentModel and the masked schemas, but you can also have a full access to the DocumentModel API .


    String startPath="/default-domain/ws1";

    // create a simple DocumentModel
    DocumentModel doc = coreSession.createDocumentModel(startPath, "file", "File");
    doc.setProperty("dublincore", "title", "MyDoc");
    doc.setProperty("dublincore", "coverage", "MyDocCoverage");
    doc = coreSession.createDocument(doc);

    // create a DocumentModel with the DocumentLink type
    DocumentModel link = coreSession.createDocumentModel(startPath, "link","DocumentLink");
    link.setProperty("dublincore", "title", "MyLinkToDoc");
    link = coreSession.createDocument(link);


    // get the DocumentLinkAdapter
    DocumentLinkAdapter adaptedLink = link.getAdapter(DocumentLinkAdapter.class);
    // set the target DocumentModel
    adaptedLink.setTargetDocument(doc);

    // check property access

    // check property accessor pass-throught
    String cover1 = (String) link.getProperty("dublincore", "coverage");
    String cover2 = (String) adaptedLink.getProperty("dublincore","coverage");
    assertNotNull(cover2);
    assertEquals("MyDocCoverage", cover2);

    // check automatic override (title)
    String title0 = (String) doc.getProperty("dublincore", "title");
    String title1 = (String) link.getProperty("dublincore", "title");
    String title2 = (String) adaptedLink.getProperty("dublincore", "title");
    
    // adapter and documentModel direct Access return the same value
    assertEquals(title1, title2);
    // DocumentLink do not return the title of the target since it's overridden
    assertFalse(title1.equals(title0));

## Using the DocRepository

DocumentLink provides static helpers to help you create DocumentLinks and use the DocRepository.
    
    DocumentModel dm = coreSession.createDocumentModel("File");
    dm.setProperty("dublincore", "title", "testme");
    
    // create a new DocumentLink of type DocumentLink
    // create it in / path
    // make it point to the dm DocumentModel that will be stored somewhere in the DocRepository
    DocumentLinkAdapter adaptedLink = DocumentLinkHelper
                    .createDocumentInCentralRepository(coreSession, dm, "/",
                                    "DocumentLink");
    coreSession.save();
    
    // get the create target DocumentModel
    DocumentModel targetDoc = adaptedLink.getTargetDocument();
    
    // get the Repository
    DocRepository repo = DocRepositoryHelper.getDocumentRepository(coreSession);
    
    // get all the DocumentLinks
    DocumentModelList proxies = repo.getProxiesForDocument(targetDoc.getRef());
    assertTrue(proxies.size()==1);
    
    // create a second DocumentLink pointing to the same target
    DocumentLinkAdapter secondLink=DocumentLinkHelper.createDocumentLink(targetDoc, "/");
    coreSession.save();
    
    // get all the DocumentLinks
    proxies = repo.getProxiesForDocument(targetDoc.getRef());
    assertTrue(proxies.size()==2);