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

package uk.ac.ebi.ricordo.rdfconverter.reactometordf;

import org.apache.log4j.Logger;
import org.biopax.paxtools.impl.BioPAXElementImpl;
import org.biopax.paxtools.io.BioPAXIOHandler;
import org.biopax.paxtools.io.SimpleIOHandler;
import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.level3.*;
import uk.ac.ebi.ricordo.rdfconverter.util.MappingExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 05/11/12
 *         Time: 15:38
 */
public class ReactomeModifierImpl implements ReactomeModifier{
    Logger logger = Logger.getLogger(ReactomeModifierImpl.class);
    private MappingExtractor bioPaxMappingExtractor;
    private Model model = null;
    private BioPAXIOHandler handler = new SimpleIOHandler();
    private String outputFolder;

    public void parseReactomeModel(File inputFile){
        readModel(inputFile);
        replaceXref();
//        replaceModelNs();
        writeModel(inputFile);
    }

    private void readModel(File inputFile){
        FileInputStream inputStream = null;
        try {
             inputStream = new FileInputStream(inputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        model = handler.convertFromOWL(inputStream);
    }

    private void writeModel(File inputFile) {
        try {
            FileOutputStream outputStream = new FileOutputStream(outputFolder + inputFile.getName().replaceAll(" ","_"));
            handler.convertToOWL(model,outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void replaceID(Model model, BioPAXElement el, String newRDFId) {
        if(el.getRDFId().equals(newRDFId))
            return;
        model.remove(el);
        try {
            Method m = BioPAXElementImpl.class.getDeclaredMethod("setRDFId", String.class);
            m.setAccessible(true);
            m.invoke(el, newRDFId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        model.add(el);
    }

    private void replaceXref(){
        HashMap<BioPAXElement ,String> xRefReplaceMap = new HashMap<BioPAXElement ,String>();

        for (BioSource bioSource : model.getObjects(BioSource.class)) {
            String newid= createNewId(bioSource.getXref());
            if(newid!=null) {
                xRefReplaceMap.put(bioSource,newid);
            }
        }

        for (Entity entity : model.getObjects(Pathway.class)) {
            String newid= createNewId(entity.getXref());
            if(newid!=null) {
                xRefReplaceMap.put(entity,newid);
            }
        }

        for (EntityReference entityReference : model.getObjects(EntityReference.class)) {
            String newid= createNewId(entityReference.getXref());
            if(newid!=null) {
                xRefReplaceMap.put(entityReference,newid);
            }
        }

        for (ControlledVocabulary controlledVocabulary : model.getObjects(ControlledVocabulary.class)) {
            String newid= createNewId(controlledVocabulary.getXref());
            if(newid!=null) {
                xRefReplaceMap.put(controlledVocabulary,newid);
            }
        }

        for (Evidence evidence : model.getObjects(Evidence.class)) {
            String newid= createNewId(evidence.getXref());
            if(newid!=null) {
                xRefReplaceMap.put(evidence,newid);
            }
        }

        for (Provenance provenance : model.getObjects(Provenance.class)) {
            String newid= createNewId(provenance.getXref());
            if(newid!=null) {
                xRefReplaceMap.put(provenance,newid);
            }
        }

        for (PublicationXref publicationXref : model.getObjects(PublicationXref.class)) {
            if(publicationXref.getDb()==null || bioPaxMappingExtractor.getIdentMap().get(publicationXref.getDb())==null){
                continue;
            }
            else {
                String newid= bioPaxMappingExtractor.getIdentMap().get(publicationXref.getDb())+publicationXref.getId();
                xRefReplaceMap.put(publicationXref,newid);
            }
        }
        replaceMap(xRefReplaceMap);
    }

    private String createNewId(Set <Xref> xrefSet){
        for (Xref xref : xrefSet) {
            if(xref.getDb()==null || bioPaxMappingExtractor.getIdentMap().get(xref.getDb())==null || !(xref instanceof UnificationXref)){
                continue;
            }
            if(xref.getDb().equals("Reactome")){
                return bioPaxMappingExtractor.getIdentMap().get("Reactome")+xref.getId()+"."+xref.getIdVersion();
            }
            else {
                return bioPaxMappingExtractor.getIdentMap().get(xref.getDb())+xref.getId();
            }
        }
        return null;
    }

    private void replaceMap(HashMap<BioPAXElement ,String> replaceMap){
        for (Map.Entry<BioPAXElement, String> bioPAXElementStringEntry : replaceMap.entrySet()) {
            BioPAXElement el = bioPAXElementStringEntry.getKey();
            String newRDFId = bioPAXElementStringEntry.getValue();

            BioPAXElement existingSimilar = model.getByID(newRDFId);

            logger.info(el.getRDFId() + " ====> " + newRDFId);

             if(existingSimilar==null)
                replaceID(model,bioPAXElementStringEntry.getKey(),bioPAXElementStringEntry.getValue());

        }

    }

    public void setBioPaxMappingExtractor(MappingExtractor bioPaxMappingExtractor) {
        this.bioPaxMappingExtractor = bioPaxMappingExtractor;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }
}
