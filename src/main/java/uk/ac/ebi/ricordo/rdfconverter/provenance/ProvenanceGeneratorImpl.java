/*
 * Copyright 2012 EMBL-EBI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.ricordo.rdfconverter.provenance;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 15/11/12
 *         Time: 16:29
 */
public class ProvenanceGeneratorImpl implements ProvenanceGenerator {
    public static final String FOAFURI ="http://xmlns.com/foaf/0.1/";
    public static final String FOAFPREFIX="foaf";
    public static final String PAVURI ="http://purl.org/pav/";
    public static final String PAVPREFIX="pav";
    public static final String PROVOURI ="http://www.w3.org/TR/prov-o/";
    public static final String PROVOPREFIX="provo";
    public static final String SKOSURI ="http://www.w3.org/2004/02/skos/core#";
    public static final String SKOSPREFIX="skos";
    public static final String VOIDSURI ="http://rdfs.org/ns/void#";
    public static final String VOIDPREFIX="void";
    public static final String BASEURI ="#";
    public static final String BASEPREFIX="";

    private String vd_homepage;
    private String vd_title;
    private String vd_description;
    private String vd_creator;
    private String vd_primaryTopic;

    private String dd_homepage;
    private String dd_title;
    private String dd_description;
    private String dd_license;
    private String dd_publisher;
    private String dd_uriSpace;
    private String dd_importedBy;
    private String dd_importedFrom;
    private String dd_sparqlEndpoint;
    private String dd_vocabulary;
    private String dd_version;
    private String dd_previousVersion;
    private String dd_dataDump;

    private String provenanceFolder;
    private String provenanceFileName;

    private com.hp.hpl.jena.rdf.model.Model rdfModel = ModelFactory.createDefaultModel();
    private Resource dataset = rdfModel.createResource(VOIDSURI+"Dataset");

    public void generateProvenace(){
        setNS();
        createVoidDescription();
        createDatasetDescription();
        writeToFile();
    }

    private void setNS(){
        rdfModel.setNsPrefix(FOAFPREFIX, FOAFURI);
        rdfModel.setNsPrefix(PAVPREFIX, PAVURI);
        rdfModel.setNsPrefix(PROVOPREFIX, PROVOURI);
        rdfModel.setNsPrefix(SKOSPREFIX, SKOSURI);
        rdfModel.setNsPrefix(VOIDPREFIX, VOIDSURI);
        rdfModel.setNsPrefix(BASEPREFIX, BASEURI);
        rdfModel.setNsPrefix("dcterms", DCTerms.NS);
        rdfModel.setNsPrefix("xsd","http://www.w3.org/2001/XMLSchema#");
    }

    private void createVoidDescription(){
        Resource voidDescription = rdfModel.createResource(vd_homepage);
        Resource datasetDescription = rdfModel.createResource(VOIDSURI+"DatasetDescription");
        voidDescription.addProperty(RDF.type, datasetDescription);
        voidDescription.addLiteral(DCTerms.title, vd_title);
        voidDescription.addLiteral(DCTerms.description, vd_description);
        voidDescription.addProperty(DCTerms.creator, rdfModel.createResource(vd_creator));
        voidDescription.addLiteral(DCTerms.created, Calendar.getInstance().getTime().toString());
        voidDescription.addProperty(rdfModel.createProperty(FOAFURI + "primaryTopic"), rdfModel.createResource(vd_primaryTopic));
    }

    private void createDatasetDescription(){
        Resource datasetDescription = rdfModel.createResource(vd_primaryTopic);
        datasetDescription.addProperty(RDF.type, dataset);
        datasetDescription.addLiteral(DCTerms.title, dd_title);
        datasetDescription.addLiteral(DCTerms.description, dd_description);
        datasetDescription.addProperty(rdfModel.createProperty(FOAFURI + "homepage"), rdfModel.createResource(dd_homepage));
        datasetDescription.addProperty(DCTerms.license, rdfModel.createResource(dd_license));
        datasetDescription.addLiteral(rdfModel.createProperty(VOIDSURI + "uriSpace"), dd_uriSpace);
        datasetDescription.addProperty(DCTerms.publisher, rdfModel.createResource(dd_publisher));
        datasetDescription.addProperty(rdfModel.createProperty(PAVURI + "importedFrom"), rdfModel.createResource(dd_importedFrom));
        datasetDescription.addProperty(rdfModel.createProperty(PAVURI + "importedBy"), rdfModel.createResource(dd_importedBy));
        datasetDescription.addProperty(rdfModel.createProperty(VOIDSURI + "sparqlEndPoint"), rdfModel.createResource(dd_sparqlEndpoint));
        String [] vocabularies = dd_vocabulary.split(",");
        for(int i = 0; i <vocabularies.length; i++){
            datasetDescription.addProperty(rdfModel.createProperty(VOIDSURI + "vocabulary"), rdfModel.createResource(vocabularies[i]));
        }
        datasetDescription.addLiteral(rdfModel.createProperty(PAVURI + "version" ), dd_version);
        datasetDescription.addProperty(rdfModel.createProperty(PAVURI + "previousVersion"), rdfModel.createResource(dd_previousVersion));
        datasetDescription.addLiteral(rdfModel.createProperty(PAVURI + "importedOn" ), Calendar.getInstance().getTime().toString());
        datasetDescription.addProperty(rdfModel.createProperty(VOIDSURI + "dataDump"), rdfModel.createResource(dd_dataDump));

    }

    private void writeToFile(){
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(provenanceFolder + provenanceFileName+".ttl");
            rdfModel.write(fileOutputStream, "TURTLE");
            fileOutputStream.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void setDd_dataDump(String dd_dataDump) {
        this.dd_dataDump = dd_dataDump;
    }

    public void setVd_homepage(String vd_homepage) {
        this.vd_homepage = vd_homepage;
    }

    public void setVd_title(String vd_title) {
        this.vd_title = vd_title;
    }

    public void setVd_description(String vd_description) {
        this.vd_description = vd_description;
    }

    public void setVd_creator(String vd_creator) {
        this.vd_creator = vd_creator;
    }

    public void setVd_primaryTopic(String vd_primaryTopic) {
        this.vd_primaryTopic = vd_primaryTopic;
    }

    public void setDd_homepage(String dd_homepage) {
        this.dd_homepage = dd_homepage;
    }

    public void setDd_title(String dd_title) {
        this.dd_title = dd_title;
    }

    public void setDd_description(String dd_description) {
        this.dd_description = dd_description;
    }

    public void setDd_license(String dd_license) {
        this.dd_license = dd_license;
    }

    public void setDd_publisher(String dd_publisher) {
        this.dd_publisher = dd_publisher;
    }

    public void setDd_uriSpace(String dd_uriSpace) {
        this.dd_uriSpace = dd_uriSpace;
    }

    public void setDd_importedBy(String dd_importedBy) {
        this.dd_importedBy = dd_importedBy;
    }

    public void setDd_importedFrom(String dd_importedFrom) {
        this.dd_importedFrom = dd_importedFrom;
    }

    public void setDd_sparqlEndpoint(String dd_sparqlEndpoint) {
        this.dd_sparqlEndpoint = dd_sparqlEndpoint;
    }

    public void setDd_vocabulary(String dd_vocabulary) {
        this.dd_vocabulary = dd_vocabulary;
    }

    public void setDd_version(String dd_version) {
        this.dd_version = dd_version;
    }

    public void setDd_previousVersion(String dd_previousVersion) {
        this.dd_previousVersion = dd_previousVersion;
    }

    public void setProvenanceFolder(String provenanceFolder) {
        this.provenanceFolder = provenanceFolder;
    }

    public void setProvenanceFileName(String provenanceFileName) {
        this.provenanceFileName = provenanceFileName;
    }
}
