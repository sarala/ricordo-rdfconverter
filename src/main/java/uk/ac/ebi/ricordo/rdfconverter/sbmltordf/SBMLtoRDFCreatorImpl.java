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

package uk.ac.ebi.ricordo.rdfconverter.sbmltordf;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import org.sbml.jsbml.*;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 10-Feb-2012
 *         Time: 10:42:35
 */
public class SBMLtoRDFCreatorImpl implements SBMLtoRDFCreator {
    private com.hp.hpl.jena.rdf.model.Model rdfModel;
    private Model sbmlModel;
    private String outputFolder ="";
    private String modelId="";
    private HashMap<String, Resource> resourceMap = new HashMap<String, Resource>();

    public void generateSBMLtoRDF(String modelId){
        this.modelId = modelId;
        readSBML();
        writeToFile();
    }

    private void readSBML(){
        SBMLReader reader  = new SBMLReader();
        SBMLDocument document = null;
        try {
            URL url= new URL("http://www.ebi.ac.uk/biomodels-main/download?mid="+modelId+"&anno=url");
            document = reader.readSBMLFromStream(url.openStream());
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sbmlModel = document.getModel();
        rdfModel = ModelFactory.createDefaultModel();
        rdfModel.setNsPrefix(SBMLConstants.URIPREFIX, SBMLConstants.URI);
        rdfModel.setNsPrefix(SBMLConstants.BQURIPREFIX, SBMLConstants.BQURI);
        rdfModel.setNsPrefix(SBMLConstants.BMURIPREFIX, SBMLConstants.BMURI);

        createModelResource();

    }

    private void createModelResource(){
        Resource modelResource = rdfModel.createResource(SBMLConstants.URI+modelId);
        modelResource.addProperty(SBMLConstants.HASNAME, sbmlModel.getName());

        modelResource.addProperty(RDF.type, SBMLConstants.SBMLMODEL);

        extractRDF(modelResource, sbmlModel.getCVTerms());
        createUnitsDefs(modelResource);
        createCompartments(modelResource);
        createSpecies(modelResource);
        createReactions(modelResource);
        createParameters(modelResource);
        createRules(modelResource);
        createEvents(modelResource);
        createFunctionDefs(modelResource);

    }

    private void createCompartments(Resource modelResource) {
        ListOf<Compartment> listOfCompartments = sbmlModel.getListOfCompartments();
        for(int i=0; i< listOfCompartments.size(); i++){
            Compartment compartment = listOfCompartments.get(i);
            Resource compartmentResource = rdfModel.createResource(SBMLConstants.URI+modelId+"_"+compartment.getId());
            compartmentResource.addProperty(SBMLConstants.HASID, compartment.getId());
            compartmentResource.addProperty(SBMLConstants.HASMetaID, compartment.getMetaId());
            if(!compartment.getSBOTermID().isEmpty())compartmentResource.addProperty(SBMLConstants.HASSBOTERM, compartment.getSBOTermID());
            compartmentResource.addLiteral(SBMLConstants.HASSIZE, compartment.getSize());
            compartmentResource.addProperty(SBMLConstants.COMPELEOF, modelResource);
            setUnits(compartment.getUnits(), compartmentResource);

            resourceMap.put(compartment.getId(),compartmentResource);
            extractRDF(compartmentResource,compartment.getCVTerms());
        }
    }

    private void createSpecies(Resource modelResource) {
        ListOf<Species> listOfSpecies = sbmlModel.getListOfSpecies();
        for(int i = 0; i < listOfSpecies.size(); i++){
            Species species = listOfSpecies.get(i);
            Resource speciesResource = rdfModel.createResource(SBMLConstants.URI+modelId+"_"+species.getId());
            speciesResource.addProperty(SBMLConstants.HASID, species.getId());
            speciesResource.addProperty(SBMLConstants.HASNAME, species.getName());
            speciesResource.addProperty(SBMLConstants.HASMetaID, species.getMetaId());
            speciesResource.addLiteral(SBMLConstants.HASINITCON, species.getInitialConcentration());
            speciesResource.addProperty(SBMLConstants.HASCOMPARTMENT, resourceMap.get(species.getCompartment()));
            if(!species.getSBOTermID().isEmpty())speciesResource.addProperty(SBMLConstants.HASSBOTERM, species.getSBOTermID());
            speciesResource.addProperty(SBMLConstants.SPECIESELEOF, modelResource);
            setUnits(species.getUnits(), speciesResource);

            resourceMap.put(species.getId(),speciesResource);
            extractRDF(speciesResource,species.getCVTerms());
        }
    }

    private void createReactions(Resource modelResource) {
        ListOf<Reaction> listOfReactions = sbmlModel.getListOfReactions();
        for(int i = 0; i < listOfReactions.size(); i++){
            Reaction reaction = listOfReactions.get(i);
            Resource reactionResource = rdfModel.createResource(SBMLConstants.URI+modelId+"_"+reaction.getId());
            reactionResource.addProperty(SBMLConstants.HASID, reaction.getId());
            reactionResource.addProperty(SBMLConstants.HASNAME, reaction.getName());
            reactionResource.addProperty(SBMLConstants.HASMetaID, reaction.getMetaId());
            if(!reaction.getSBOTermID().isEmpty())reactionResource.addProperty(SBMLConstants.HASSBOTERM, reaction.getSBOTermID());

            reactionResource.addProperty(SBMLConstants.REACTELEOF, modelResource);
            extractRDF(reactionResource,reaction.getCVTerms());

            createReactants(reaction, reactionResource);
            createProducts(reaction, reactionResource);
            createModifiers(reaction, reactionResource);
            createKineticLaw(reaction, reactionResource);
        }
    }


    private void createReactants(Reaction reaction, Resource reactionResource) {
        ListOf<SpeciesReference> listOfSpeciesReferences = reaction.getListOfReactants();
        for(int i = 0; i < listOfSpeciesReferences.size(); i++){
            SimpleSpeciesReference speciesReference = listOfSpeciesReferences.get(i);
            Resource speciesRefResource = rdfModel.createResource(SBMLConstants.URI+modelId+"_"+reaction.getId()+"_reactantSpeciesRef_"+i);
            speciesRefResource.addProperty(SBMLConstants.HASSPECIES, resourceMap.get(speciesReference.getSpecies()));
            if(!speciesReference.getSBOTermID().isEmpty())speciesRefResource.addProperty(SBMLConstants.HASSBOTERM, speciesReference.getSBOTermID());

            speciesRefResource.addProperty(SBMLConstants.REACTANTELEOF, reactionResource);
            extractRDF(speciesRefResource,speciesReference.getCVTerms());
        }
    }

    private void createProducts(Reaction reaction, Resource reactionResource) {
        ListOf<SpeciesReference> listOfSpeciesReferences = reaction.getListOfProducts();
        for(int i = 0; i < listOfSpeciesReferences.size(); i++){
            SimpleSpeciesReference speciesReference = listOfSpeciesReferences.get(i);
            Resource speciesRefResource = rdfModel.createResource(SBMLConstants.URI+modelId+"_"+reaction.getId()+"_productsSpeciesRef_"+i);
            speciesRefResource.addProperty(SBMLConstants.HASSPECIES, resourceMap.get(speciesReference.getSpecies()));
            if(!speciesReference.getSBOTermID().isEmpty())speciesRefResource.addProperty(SBMLConstants.HASSBOTERM, speciesReference.getSBOTermID());

            speciesRefResource.addProperty(SBMLConstants.PRODUCTELEOF, reactionResource);
            extractRDF(speciesRefResource,speciesReference.getCVTerms());
        }
    }

    private void createModifiers(Reaction reaction, Resource reactionResource) {
        ListOf<ModifierSpeciesReference> listOfSpeciesReferences = reaction.getListOfModifiers();
        for(int i = 0; i < listOfSpeciesReferences.size(); i++){
            SimpleSpeciesReference speciesReference = listOfSpeciesReferences.get(i);
            Resource speciesRefResource = rdfModel.createResource(SBMLConstants.URI+modelId+"_"+reaction.getId()+"_modifierSpeciesRef_"+i);
            speciesRefResource.addProperty(SBMLConstants.HASSPECIES, resourceMap.get(speciesReference.getSpecies()));
            if(!speciesReference.getSBOTermID().isEmpty())speciesRefResource.addProperty(SBMLConstants.HASSBOTERM, speciesReference.getSBOTermID());

            speciesRefResource.addProperty(SBMLConstants.MODIFIERELEOF, reactionResource);
            extractRDF(speciesRefResource,speciesReference.getCVTerms());
        }
    }

    private void createKineticLaw(Reaction reaction, Resource reactionResource) {
        KineticLaw kineticLaw = reaction.getKineticLaw();
        Resource kineticLawResource = rdfModel.createResource(SBMLConstants.URI+modelId+"_"+reaction.getId()+"_kineticLaw");
        if(!kineticLaw.getSBOTermID().isEmpty())kineticLawResource.addProperty(SBMLConstants.HASSBOTERM, kineticLaw.getSBOTermID());
        kineticLawResource.addProperty(SBMLConstants.KINETICELEOF, reactionResource);
        extractRDF(kineticLawResource,kineticLaw.getCVTerms());
    }

    private void createParameters(Resource modelResource) {
        ListOf<Parameter> listOfParameters = sbmlModel.getListOfParameters();
        for(int i = 0; i < listOfParameters.size(); i++){
            Parameter parameter = listOfParameters.get(i);
            Resource parameterResource = rdfModel.createResource(SBMLConstants.URI+modelId+"_"+parameter.getId());
            parameterResource.addProperty(SBMLConstants.HASID, parameter.getId());
            parameterResource.addProperty(SBMLConstants.HASNAME, parameter.getName());
            parameterResource.addProperty(SBMLConstants.HASMetaID, parameter.getMetaId());
            if(!parameter.getSBOTermID().isEmpty())parameterResource.addProperty(SBMLConstants.HASSBOTERM, parameter.getSBOTermID());
            parameterResource.addLiteral(SBMLConstants.HASVALUE, parameter.getValue());
            parameterResource.addProperty(SBMLConstants.PARAMELEOF, modelResource);
            setUnits(parameter.getUnits(), parameterResource);

            resourceMap.put(parameter.getId(),parameterResource);
            extractRDF(parameterResource,parameter.getCVTerms());
        }
    }

    private void createRules(Resource modelResource) {
        ListOf<Rule> listOfRules = sbmlModel.getListOfRules();
        for(int i=0; i<listOfRules.size(); i++){
            Rule rule = listOfRules.get(i);
            Resource ruleResource = rdfModel.createResource(SBMLConstants.URI+modelId+"_"+rule.getMetaId());
            ruleResource.addProperty(SBMLConstants.HASMetaID, rule.getMetaId());
            if(ruleResource instanceof AssignmentRule)
                ruleResource.addProperty(SBMLConstants.HASVAR, resourceMap.get(((AssignmentRule)rule).getVariable()));
            if(ruleResource instanceof RateRule)
                ruleResource.addProperty(SBMLConstants.HASVAR, resourceMap.get(((RateRule) rule).getVariable()));
            if(!rule.getSBOTermID().isEmpty())ruleResource.addProperty(SBMLConstants.HASSBOTERM, rule.getSBOTermID());
            ruleResource.addProperty(SBMLConstants.RULEELEOF, modelResource);
            extractRDF(ruleResource,rule.getCVTerms());
        }
    }


    private void createEvents(Resource modelResource) {
        ListOf<Event> listOfEvents = sbmlModel.getListOfEvents();
        for(int i=0; i<listOfEvents.size(); i++){
            Event event = listOfEvents.get(i);
            Resource eventResource = rdfModel.createResource(SBMLConstants.URI+modelId+"_"+event.getId());
            eventResource.addProperty(SBMLConstants.HASID, event.getId());
            eventResource.addProperty(SBMLConstants.HASMetaID, event.getMetaId());
            eventResource.addProperty(SBMLConstants.HASNAME, event.getName());
            if(!event.getSBOTermID().isEmpty())eventResource.addProperty(SBMLConstants.HASSBOTERM, event.getSBOTermID());
            eventResource.addProperty(SBMLConstants.EVENTELEOF, modelResource);
            extractRDF(eventResource,event.getCVTerms());
        }
    }

    private void createFunctionDefs(Resource modelResource) {
        ListOf<FunctionDefinition> listOfFunctionDefinitions = sbmlModel.getListOfFunctionDefinitions();
        for(int i=0; i<listOfFunctionDefinitions.size(); i++){
            FunctionDefinition functionDefinition = listOfFunctionDefinitions.get(i);
            Resource functionResource = rdfModel.createResource(SBMLConstants.URI+modelId+"_"+functionDefinition.getId());
            functionResource.addProperty(SBMLConstants.HASMetaID, functionDefinition.getMetaId());
            functionResource.addProperty(SBMLConstants.HASID, functionDefinition.getId());
            if(!functionDefinition.getSBOTermID().isEmpty())functionResource.addProperty(SBMLConstants.HASSBOTERM, functionDefinition.getSBOTermID());
            functionResource.addProperty(SBMLConstants.FUNCTIONELEOF, modelResource);
            extractRDF(functionResource,functionDefinition.getCVTerms());
        }
    }

    private void createUnitsDefs(Resource modelResource) {
        ListOf<UnitDefinition> listOfUnitDefinitions = sbmlModel.getListOfUnitDefinitions();
        for(int i=0; i<listOfUnitDefinitions.size(); i++){
            UnitDefinition unitDefinition = listOfUnitDefinitions.get(i);
            Resource unitResource = rdfModel.createResource(SBMLConstants.URI+modelId+"_"+unitDefinition.getId());
            unitResource.addProperty(SBMLConstants.HASMetaID, unitDefinition.getMetaId());
            unitResource.addProperty(SBMLConstants.HASID, unitDefinition.getId());
            unitResource.addProperty(SBMLConstants.HASNAME, unitDefinition.getName());
            if(!unitDefinition.getSBOTermID().isEmpty())unitResource.addProperty(SBMLConstants.HASSBOTERM, unitDefinition.getSBOTermID());
            unitResource.addProperty(SBMLConstants.UNITSDEFELEOF, modelResource);
            extractRDF(unitResource,unitDefinition.getCVTerms());
            resourceMap.put(unitDefinition.getId(),unitResource);

            createListOfUnits(unitDefinition, unitResource);
        }
    }

    private void createListOfUnits(UnitDefinition unitDefinition, Resource unitDefResource) {
        ListOf<Unit> listOfUnits = unitDefinition.getListOfUnits();
        for(int i=0; i<listOfUnits.size(); i++){
            Unit unit = listOfUnits.get(i);
            Resource unitResource = rdfModel.createResource(SBMLConstants.URI+modelId+"_"+unitDefinition.getId()+"_unit_"+i);
            unitResource.addLiteral(SBMLConstants.HASKIND, unit.getKind());
            unitResource.addLiteral(SBMLConstants.HASMULTIPLIER, unit.getMultiplier());
            unitResource.addLiteral(SBMLConstants.HASSCALE, unit.getScale());
            if(!unitDefinition.getSBOTermID().isEmpty())unitResource.addProperty(SBMLConstants.HASSBOTERM, unitDefinition.getSBOTermID());
            unitResource.addProperty(SBMLConstants.UNITELEOF, unitDefResource);
            extractRDF(unitResource,unitDefinition.getCVTerms());

        }
    }

    private void setUnits(String units, Resource resource){
        if(!units.isEmpty()){
            Resource unitResource =  resourceMap.get(units);
            if(unitResource != null)
                resource.addProperty(SBMLConstants.HASUNITS, unitResource);
            else
                resource.addLiteral(SBMLConstants.HASUNITS, units);
        }
    }

    private void extractRDF(Resource resource, List<CVTerm> cvTerms) {
        if(cvTerms==null){
            return;
        }

        for(CVTerm cvterm: cvTerms){
            for(String uri : cvterm.getResources()){
                Resource annotationResource = rdfModel.createResource(uri);
                if(cvterm.getQualifierType() == CVTerm.Type.MODEL_QUALIFIER){
                    resource.addProperty(SBMLConstants.createProperty(SBMLConstants.BMURI,cvterm.getModelQualifierType().getElementNameEquivalent()), annotationResource);
                }else  if(cvterm.getQualifierType() == CVTerm.Type.BIOLOGICAL_QUALIFIER){
                    resource.addProperty(SBMLConstants.createProperty(SBMLConstants.BQURI, cvterm.getBiologicalQualifierType().getElementNameEquivalent()), annotationResource);
                }
            }
        }
    }

    private void writeToFile(){
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(outputFolder +modelId+".rdf");
            rdfModel.write(fileOutputStream, "RDF/XML-ABBREV");
            fileOutputStream.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

}
