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

package uk.ac.ebi.ricordo.rdfconverter.tordf;

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

    private String bv_title;
    private String bv_description;
    private String bv_createdBy;
    private String bv_primaryTopic;

    private String bd_homepage;
    private String bd_title;
    private String bd_description;
    private String bd_contributor;
    private String bd_uriSpace;
    private String bd_wasDerivedFrom;
    private String bd_sparqlEndpoint;
    private String bd_dataDump;
    private String bd_subset;

    private String lv_license;
    private String lv_version;
    private String lv_dataDump;
    private String lv_previousVersion;
    private String lv_vocabulary;
    private String lv_subset_title_uriSpace;

    private String provenanceFolder;
    private String provenanceFileName;

    private com.hp.hpl.jena.rdf.model.Model rdfModel = ModelFactory.createDefaultModel();
    private Resource dataset = rdfModel.createResource(VOIDSURI+"Dataset");

    public void generateProvenace(){
        setNS();
        createVoidDescription();
        createDatasetDescription();
        latestVersionDescription();
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
        Resource voidDescription = rdfModel.createResource(BASEURI);
        Resource datasetDescription = rdfModel.createResource(VOIDSURI+"DatasetDescription");
        voidDescription.addProperty(RDF.type, datasetDescription);
        voidDescription.addLiteral(DCTerms.title, bv_title);
        voidDescription.addLiteral(DCTerms.description, bv_description);
        voidDescription.addProperty(rdfModel.createProperty(PAVURI + "createdBy"), rdfModel.createResource(bv_createdBy));
        voidDescription.addLiteral(rdfModel.createProperty(PAVURI + "createdOn" ), Calendar.getInstance().getTime().toString());
        voidDescription.addProperty(rdfModel.createProperty(FOAFURI + "primaryTopic"),rdfModel.createResource(bv_primaryTopic));
    }

    private void createDatasetDescription(){
        Resource datasetDescription = rdfModel.createResource(bv_primaryTopic);
        datasetDescription.addProperty(RDF.type, dataset);
        datasetDescription.addProperty(rdfModel.createProperty(FOAFURI + "homepage"), rdfModel.createResource(bd_homepage));
        datasetDescription.addLiteral(DCTerms.title, bd_title);
        datasetDescription.addLiteral(DCTerms.description, bd_description);
        String [] contributors = bd_contributor.split(",");
        for(int i = 0; i <contributors.length; i++){
            datasetDescription.addProperty(DCTerms.contributor, rdfModel.createResource(contributors[i]));
        }
        datasetDescription.addLiteral(rdfModel.createProperty(VOIDSURI + "uriSpace"), bd_uriSpace);
        datasetDescription.addProperty(rdfModel.createProperty(PROVOURI + "wasDerivedFrom"), rdfModel.createResource(bd_wasDerivedFrom));
        datasetDescription.addProperty(rdfModel.createProperty(VOIDSURI + "sparqlEndPoint"), rdfModel.createResource(bd_sparqlEndpoint));
        datasetDescription.addProperty(rdfModel.createProperty(VOIDSURI + "dataDump"), rdfModel.createResource(bd_dataDump));
        datasetDescription.addProperty(rdfModel.createProperty(VOIDSURI + "subset"),rdfModel.createResource(bd_subset));
    }

    private void latestVersionDescription(){
        Resource latestVD = rdfModel.createResource(bd_subset);
        latestVD.addProperty(RDF.type, dataset);
        latestVD.addProperty(DCTerms.license, rdfModel.createResource(lv_license));
        latestVD.addLiteral(rdfModel.createProperty(PROVOURI + "generatedAtTime"), Calendar.getInstance().getTime().toString());
        latestVD.addLiteral(rdfModel.createProperty(PAVURI + "createdOn" ), Calendar.getInstance().getTime().toString());
        latestVD.addLiteral(rdfModel.createProperty(PAVURI + "version" ), lv_version);
        latestVD.addProperty(rdfModel.createProperty(VOIDSURI + "dataDump"),rdfModel.createResource(lv_dataDump));
        latestVD.addProperty(rdfModel.createProperty(PAVURI + "previousVersion"), rdfModel.createResource(lv_previousVersion));
        String [] vocabularies = lv_vocabulary.split(",");
        for(int i = 0; i <vocabularies.length; i++){
            latestVD.addProperty(rdfModel.createProperty(VOIDSURI + "vocabulary"), rdfModel.createResource(vocabularies[i]));
        }

        String [] subsets = lv_subset_title_uriSpace.split(";");
        for(int i=0; i<subsets.length;i++){
            String [] subsetDescriptions = subsets[i].split(",");
            if(subsetDescriptions.length!=3){
                break;
            }
            Resource subsetResource = rdfModel.createResource(subsetDescriptions[0]);
            latestVD.addProperty(rdfModel.createProperty(VOIDSURI + "subset"), subsetResource);
            subsetResource.addProperty(RDF.type, dataset);
            subsetResource.addLiteral(DCTerms.title, subsetDescriptions[1]);
            subsetResource.addLiteral(rdfModel.createProperty(VOIDSURI + "uriSpace"), subsetDescriptions[2]);
        }
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



    public void setBv_title(String bv_title) {
        this.bv_title = bv_title;
    }

    public void setBv_description(String bv_description) {
        this.bv_description = bv_description;
    }

    public void setBv_createdBy(String bv_createdBy) {
        this.bv_createdBy = bv_createdBy;
    }

    public void setBv_primaryTopic(String bv_primaryTopic) {
        this.bv_primaryTopic = bv_primaryTopic;
    }

    public void setBd_homepage(String bd_homepage) {
        this.bd_homepage = bd_homepage;
    }

    public void setBd_title(String bd_title) {
        this.bd_title = bd_title;
    }

    public void setBd_description(String bd_description) {
        this.bd_description = bd_description;
    }

    public void setBd_contributor(String bd_contributor) {
        this.bd_contributor = bd_contributor;
    }

    public void setBd_uriSpace(String bd_uriSpace) {
        this.bd_uriSpace = bd_uriSpace;
    }

    public void setBd_wasDerivedFrom(String bd_wasDerivedFrom) {
        this.bd_wasDerivedFrom = bd_wasDerivedFrom;
    }

    public void setBd_sparqlEndpoint(String bd_sparqlEndpoint) {
        this.bd_sparqlEndpoint = bd_sparqlEndpoint;
    }

    public void setBd_dataDump(String bd_dataDump) {
        this.bd_dataDump = bd_dataDump;
    }

    public void setBd_subset(String bd_subset) {
        this.bd_subset = bd_subset;
    }

    public void setLv_license(String lv_license) {
        this.lv_license = lv_license;
    }

    public void setLv_version(String lv_version) {
        this.lv_version = lv_version;
    }

    public void setLv_dataDump(String lv_dataDump) {
        this.lv_dataDump = lv_dataDump;
    }

    public void setLv_previousVersion(String lv_previousVersion) {
        this.lv_previousVersion = lv_previousVersion;
    }

    public void setLv_vocabulary(String lv_vocabulary) {
        this.lv_vocabulary = lv_vocabulary;
    }

    public void setLv_subset_title_uriSpace(String lv_subset_title_uriSpace) {
        this.lv_subset_title_uriSpace = lv_subset_title_uriSpace;
    }

    public void setProvenanceFolder(String provenanceFolder) {
        this.provenanceFolder = provenanceFolder;
    }

    public void setProvenanceFileName(String provenanceFileName) {
        this.provenanceFileName = provenanceFileName;
    }

}
