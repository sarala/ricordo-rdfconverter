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
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

import com.hp.hpl.jena.vocabulary.VCARD;
import org.apache.commons.validator.UrlValidator;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.sbml.jsbml.*;
import org.sbml.jsbml.xml.XMLNode;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.net.URL;
import java.util.Date;
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
    Logger logger = Logger.getLogger(SBMLtoRDFCreatorImpl.class);
    private com.hp.hpl.jena.rdf.model.Model rdfModel;
    private Model sbmlModel;
    private String outputFolder ="";
    private String modelId="";
    private HashMap<String, Resource> resourceMap = new HashMap<String, Resource>();
    private String modelns ="";

    public void generateSBMLtoRDFFromURL(String modelId){
        this.modelId = modelId;
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
        generateSBMLtoRDF(document);
    }

    public void generateSBMLtoRDFFromFile(String modelId, File file) {
        this.modelId = modelId;
        SBMLReader reader  = new SBMLReader();
        SBMLDocument document = null;
        try {
            document = reader.readSBML(file);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        generateSBMLtoRDF(document);
    }

    private void generateSBMLtoRDF(SBMLDocument document){
        readSBML(document);
        writeToFile();
    }

    private void readSBML(SBMLDocument document){
        sbmlModel = document.getModel();
        rdfModel = ModelFactory.createDefaultModel();

        modelns =  SBMLConstants.URI + modelId + "#";
        rdfModel.setNsPrefix(SBMLConstants.URIPREFIX, modelns);
        rdfModel.setNsPrefix(SBMLConstants.BQURIPREFIX, SBMLConstants.BQURI);
        rdfModel.setNsPrefix(SBMLConstants.BMURIPREFIX, SBMLConstants.BMURI);
        rdfModel.setNsPrefix(SBMLConstants.RSMURIPREFIX, SBMLConstants.RSMURI);
        rdfModel.setNsPrefix(SBMLConstants.VCARDPREFIX, VCARD.getURI());
        rdfModel.setNsPrefix(SBMLConstants.TERMSPREFIX, DCTerms.getURI());

        createModelResource();

    }

    private void createModelResource(){
        Resource modelResource = rdfModel.createResource(modelns+sbmlModel.getMetaId());

        createUnitsDefs(modelResource);
        createParameters(modelResource);
        createModel(modelResource);
        createCompartments(modelResource);
        createSpecies(modelResource);
        createReactions(modelResource);
        createInitialAssignment(modelResource);
        createRules(modelResource);
        createConstraint(modelResource);
        createEvents(modelResource);
        createFunctionDefs(modelResource);

    }

    private void createUnitsDefs(Resource modelResource) {
        ListOf<UnitDefinition> listOfUnitDefinitions = sbmlModel.getListOfUnitDefinitions();
        for (UnitDefinition unitDefinition : listOfUnitDefinitions) {
            Resource unitResource = rdfModel.createResource(modelns + unitDefinition.getMetaId());
            unitResource.addProperty(RDF.type, SBMLConstants.UNITSDEF_CLASS);
            if (!unitDefinition.getName().isEmpty())
                unitResource.addProperty(SBMLConstants.NAME, unitDefinition.getName());
            if (!unitDefinition.getSBOTermID().isEmpty())
                sboIdentifiersOrg(unitResource, unitDefinition.getSBOTermID());
            extractRDF(unitResource, unitDefinition.getCVTerms());
            modelResource.addProperty(SBMLConstants.UNITSDEF, unitResource);
            if(!unitDefinition.getNotesString().isEmpty())
                unitResource.addProperty(SBMLConstants.NOTES, getNotesAsText(unitDefinition.getNotesString()));

            resourceMap.put(unitDefinition.getId(), unitResource);
            createListOfUnits(unitDefinition, unitResource);

        }
    }

    private void createListOfUnits(UnitDefinition unitDefinition, Resource unitDefResource) {
        ListOf<Unit> listOfUnits = unitDefinition.getListOfUnits();
        for (Unit unit : listOfUnits) {
            Resource unitResource = rdfModel.createResource(modelns + unit.getMetaId());
            unitResource.addProperty(RDF.type, SBMLConstants.UNIT_CLASS);
            unitResource.addLiteral(SBMLConstants.KIND, unit.getKind());
            unitResource.addLiteral(SBMLConstants.MULTIPLIER, unit.getMultiplier());
            unitResource.addLiteral(SBMLConstants.SCALE, unit.getScale());
            unitResource.addLiteral(SBMLConstants.EXPONENT, unit.getExponent());
            if (!unitDefinition.getSBOTermID().isEmpty())
                sboIdentifiersOrg(unitResource, unitDefinition.getSBOTermID());
            extractRDF(unitResource, unitDefinition.getCVTerms());
            if(!unit.getNotesString().isEmpty())
                unitResource.addProperty(SBMLConstants.NOTES, getNotesAsText(unit.getNotesString()));
            unitDefResource.addProperty(SBMLConstants.UNIT, unitResource);
        }
    }

    private void setUnits( Resource resource, Property property, String units){
        if(!units.isEmpty()){
            Resource unitResource =  resourceMap.get(units);
            if(unitResource != null)
                resource.addProperty(property, unitResource);
            else
                resource.addLiteral(property, units);
        }
    }

    private void createParameters(Resource modelResource) {
        ListOf<Parameter> listOfParameters = sbmlModel.getListOfParameters();
        for (Parameter parameter : listOfParameters) {
            Resource parameterResource = rdfModel.createResource(modelns + parameter.getMetaId());
            parameterResource.addProperty(RDF.type, SBMLConstants.PARAMETER_CLASS);
            if (!parameter.getName().isEmpty())
                parameterResource.addProperty(SBMLConstants.NAME, parameter.getName());
            parameterResource.addLiteral(SBMLConstants.VALUE, parameter.getValue());
            setUnits(parameterResource,SBMLConstants.UNITS,parameter.getUnits());
            parameterResource.addLiteral(SBMLConstants.CONSTANT, parameter.getConstant());
            if (!parameter.getSBOTermID().isEmpty())
                sboIdentifiersOrg(parameterResource, parameter.getSBOTermID());
            extractRDF(parameterResource, parameter.getCVTerms());
            if(!parameter.getNotesString().isEmpty())
                parameterResource.addProperty(SBMLConstants.NOTES, getNotesAsText(parameter.getNotesString()));
            modelResource.addProperty(SBMLConstants.PARAMETER, parameterResource);
            resourceMap.put(parameter.getId(), parameterResource);

        }
    }

    private void createModel(Resource modelResource){
        modelResource.addProperty(RDF.type, SBMLConstants.SBMLMODEL);
        if(!sbmlModel.getName().isEmpty())
            modelResource.addProperty(SBMLConstants.NAME, sbmlModel.getName());
        setUnits(modelResource,SBMLConstants.SUBSTANCEUNITS,sbmlModel.getSubstanceUnits());
        setUnits(modelResource,SBMLConstants.TIMEUNITS,sbmlModel.getTimeUnits());
        setUnits(modelResource,SBMLConstants.VOLUMEUNITS,sbmlModel.getVolumeUnits());
        setUnits(modelResource,SBMLConstants.AREAUNITS,sbmlModel.getAreaUnits());
        setUnits(modelResource,SBMLConstants.LENGTHUNIT,sbmlModel.getLengthUnits());
        setUnits(modelResource,SBMLConstants.EXTENTUNITS,sbmlModel.getExtentUnits());
        if(!sbmlModel.getConversionFactor().isEmpty())
            modelResource.addProperty(SBMLConstants.CONVERSIONfACTOR, resourceMap.get(sbmlModel.getConversionFactor()));
        extractRDF(modelResource, sbmlModel.getCVTerms());
        if(!sbmlModel.getNotesString().isEmpty())
            modelResource.addProperty(SBMLConstants.NOTES, getNotesAsText(sbmlModel.getNotesString()));

        if(modelId.startsWith("BIOMD"))
            modelResource.addLiteral(SBMLConstants.CURATED, true);
        else
            modelResource.addLiteral(SBMLConstants.CURATED, false);

        createHistory(modelResource);
    }

    private void createHistory(Resource modelResource){
        History history = sbmlModel.getHistory();
        List<Creator> listOfCreator = history.getListOfCreators();
        for(Creator creator : listOfCreator){
            Resource creatorResource = rdfModel.createResource();
            Resource nameResource = rdfModel.createResource();
            if(!creator.getFamilyName().isEmpty())
                nameResource.addProperty(VCARD.Family, creator.getFamilyName());
            if(!creator.getGivenName().isEmpty())
                nameResource.addProperty(VCARD.Given, creator.getGivenName());
            creatorResource.addProperty(VCARD.N,nameResource);

            if(!creator.getEmail().isEmpty())
                creatorResource.addProperty(VCARD.EMAIL,creator.getEmail());

            Resource organisationResource = rdfModel.createResource();
            if(!creator.getOrganization().isEmpty())
                organisationResource.addProperty(VCARD.Orgname, creator.getOrganization());
            creatorResource.addProperty(VCARD.ORG,organisationResource);

            modelResource.addProperty(DCTerms.creator, creatorResource);
        }

        if(history.getCreatedDate()!=null){
            Resource createdResource = rdfModel.createResource();
            createdResource.addProperty(SBMLConstants.DCTERMSW3CDTF, history.getCreatedDate().toString());
            modelResource.addProperty(DCTerms.created, createdResource);
        }

        List<Date> modifierDates= history.getListOfModifiedDates();
        for (Date modifierDate : modifierDates){
            Resource modifierResource = rdfModel.createResource();
            modifierResource.addProperty(SBMLConstants.DCTERMSW3CDTF, modifierDate.toString());
            modelResource.addProperty(DCTerms.modified, modifierResource);
        }
    }

    private void createCompartments(Resource modelResource) {
        ListOf<Compartment> listOfCompartments = sbmlModel.getListOfCompartments();
        for (Compartment compartment : listOfCompartments) {
            Resource compartmentResource = rdfModel.createResource(modelns + compartment.getMetaId());
            compartmentResource.addProperty(RDF.type, SBMLConstants.COMPARTMENT_CLASS);
            if (!compartment.getName().isEmpty())
                compartmentResource.addProperty(SBMLConstants.NAME, compartment.getName());
            compartmentResource.addLiteral(SBMLConstants.SPATIALDIMENSIONS, compartment.getSpatialDimensions());
            compartmentResource.addLiteral(SBMLConstants.SIZE, compartment.getSize());
            setUnits(compartmentResource,SBMLConstants.UNITS,compartment.getUnits());
            compartmentResource.addLiteral(SBMLConstants.CONSTANT, compartment.getConstant());
            if (!compartment.getSBOTermID().isEmpty())
                sboIdentifiersOrg(compartmentResource, compartment.getSBOTermID());
            extractRDF(compartmentResource, compartment.getCVTerms());
            if(!compartment.getNotesString().isEmpty())
                compartmentResource.addProperty(SBMLConstants.NOTES, getNotesAsText(compartment.getNotesString()));
            modelResource.addProperty(SBMLConstants.COMPARTMENT, compartmentResource);
            resourceMap.put(compartment.getId(), compartmentResource);
        }
    }

    private void createSpecies(Resource modelResource) {
        ListOf<Species> listOfSpecies = sbmlModel.getListOfSpecies();
        for (Species species : listOfSpecies) {
            Resource speciesResource = rdfModel.createResource(modelns + species.getMetaId());
            speciesResource.addProperty(RDF.type, SBMLConstants.SPECIES_CLASS);
            if (!species.getName().isEmpty())
                speciesResource.addProperty(SBMLConstants.NAME, species.getName());
            speciesResource.addProperty(SBMLConstants.INCOMPARTMENT, resourceMap.get(species.getCompartment()));
            speciesResource.addLiteral(SBMLConstants.INITIALAMOUNT, species.getInitialAmount());
            speciesResource.addLiteral(SBMLConstants.INITIALCONCENTRATION, species.getInitialConcentration());
            setUnits(speciesResource,SBMLConstants.SUBSTANCEUNITS,species.getSubstanceUnits());
            speciesResource.addLiteral(SBMLConstants.HASONLYSUBSTANCEUNITS, species.hasOnlySubstanceUnits());
            speciesResource.addLiteral(SBMLConstants.BOUNDARYCONDITIONS, species.getBoundaryCondition());
            speciesResource.addLiteral(SBMLConstants.CONSTANT, species.getConstant());
            if (species.getConversionFactor()!=null)
                speciesResource.addProperty(SBMLConstants.CONVERSIONfACTOR, resourceMap.get(species.getConversionFactor()));
            if (!species.getSBOTermID().isEmpty())
                sboIdentifiersOrg(speciesResource, species.getSBOTermID());
            extractRDF(speciesResource, species.getCVTerms());
            if(!species.getNotesString().isEmpty())
                speciesResource.addProperty(SBMLConstants.NOTES, getNotesAsText(species.getNotesString()));
            modelResource.addProperty(SBMLConstants.SPECIES, speciesResource);
            resourceMap.put(species.getId(), speciesResource);
        }
    }

    private void createInitialAssignment(Resource modelResource){
        ListOf<InitialAssignment> listOfInitalAssig = sbmlModel.getListOfInitialAssignments();
        for(InitialAssignment initialAssignment : listOfInitalAssig){
            Resource initAssigResource = rdfModel.createResource(modelns + listOfInitalAssig.getMetaId());
            initAssigResource.addProperty(RDF.type,SBMLConstants.INITIALASSIGNMENT_CLASS);
            initAssigResource.addProperty(SBMLConstants.SYMBOL, resourceMap.get(initialAssignment.getVariable()));
            if (!initialAssignment.getSBOTermID().isEmpty())
                sboIdentifiersOrg(initAssigResource, initialAssignment.getSBOTermID());
            extractRDF(initAssigResource, initialAssignment.getCVTerms());
            if(!initialAssignment.getNotesString().isEmpty())
                initAssigResource.addProperty(SBMLConstants.NOTES, getNotesAsText(initialAssignment.getNotesString()));
            modelResource.addProperty(SBMLConstants.INITASSIGNMENT,initAssigResource);
        }

    }

    private void createReactions(Resource modelResource) {
        ListOf<Reaction> listOfReactions = sbmlModel.getListOfReactions();
        for (Reaction reaction : listOfReactions) {
            Resource reactionResource = rdfModel.createResource(modelns + reaction.getMetaId());
            reactionResource.addProperty(RDF.type, SBMLConstants.REACTION_CLASS);
            reactionResource.addProperty(SBMLConstants.NAME, reaction.getName());
            reactionResource.addLiteral(SBMLConstants.REVERSIBLE, reaction.getReversible());
            reactionResource.addLiteral(SBMLConstants.FAST, reaction.getFast());
            if(!reaction.getCompartment().isEmpty())
                reactionResource.addProperty(SBMLConstants.INCOMPARTMENT, resourceMap.get(reaction.getCompartment()));
            if (!reaction.getSBOTermID().isEmpty())
                sboIdentifiersOrg(reactionResource, reaction.getSBOTermID());
            extractRDF(reactionResource, reaction.getCVTerms());
            if(!reaction.getNotesString().isEmpty())
                reactionResource.addProperty(SBMLConstants.NOTES, getNotesAsText(reaction.getNotesString()));

            modelResource.addProperty(SBMLConstants.REACTION, reactionResource);

            createReactants(reaction, reactionResource);
            createProducts(reaction, reactionResource);
            createModifiers(reaction, reactionResource);
            createKineticLaw(reaction, reactionResource);
        }
    }


    private void createReactants(Reaction reaction, Resource reactionResource) {
        ListOf<SpeciesReference> listOfSpeciesReferences = reaction.getListOfReactants();
        for (SpeciesReference speciesReference : listOfSpeciesReferences) {
            Resource speciesRefResource = rdfModel.createResource(modelns + speciesReference.getMetaId());
            speciesRefResource.addProperty(RDF.type, SBMLConstants.SPECIESREF_CLASS);
            if (!speciesReference.getName().isEmpty())
                speciesRefResource.addProperty(SBMLConstants.NAME, speciesReference.getName());
            speciesRefResource.addProperty(SBMLConstants.SPECIES, resourceMap.get(speciesReference.getSpecies()));
            speciesRefResource.addLiteral(SBMLConstants.STOICHIOMETRY, speciesReference.getStoichiometry());
            if (!speciesReference.getSBOTermID().isEmpty())
                sboIdentifiersOrg(speciesRefResource, speciesReference.getSBOTermID());
            extractRDF(speciesRefResource, speciesReference.getCVTerms());
            if(!speciesReference.getNotesString().isEmpty())
                speciesRefResource.addProperty(SBMLConstants.NOTES, getNotesAsText(speciesReference.getNotesString()));
            reactionResource.addProperty(SBMLConstants.REACTANT, speciesRefResource);
            resourceMap.put(speciesReference.getId(), speciesRefResource);
        }
    }

    private void createProducts(Reaction reaction, Resource reactionResource) {
        ListOf<SpeciesReference> listOfSpeciesReferences = reaction.getListOfProducts();
        for (SpeciesReference speciesReference : listOfSpeciesReferences) {
            Resource speciesRefResource = rdfModel.createResource(modelns + speciesReference.getMetaId());
            speciesRefResource.addProperty(RDF.type, SBMLConstants.SPECIESREF_CLASS);
            if (!speciesReference.getName().isEmpty())
                speciesRefResource.addProperty(SBMLConstants.NAME, speciesReference.getName());
            speciesRefResource.addProperty(SBMLConstants.SPECIES, resourceMap.get(speciesReference.getSpecies()));
            speciesRefResource.addLiteral(SBMLConstants.STOICHIOMETRY, speciesReference.getStoichiometry());
            if (!speciesReference.getSBOTermID().isEmpty())
                sboIdentifiersOrg(speciesRefResource, speciesReference.getSBOTermID());
            extractRDF(speciesRefResource, speciesReference.getCVTerms());
            if(!speciesReference.getNotesString().isEmpty())
                speciesRefResource.addProperty(SBMLConstants.NOTES, getNotesAsText(speciesReference.getNotesString()));
            reactionResource.addProperty(SBMLConstants.PRODUCT, speciesRefResource);
            resourceMap.put(speciesReference.getId(), speciesRefResource);
        }
    }

    private void createModifiers(Reaction reaction, Resource reactionResource) {
        ListOf<ModifierSpeciesReference> listOfSpeciesReferences = reaction.getListOfModifiers();
        for (ModifierSpeciesReference speciesReference : listOfSpeciesReferences) {
            Resource speciesRefResource = rdfModel.createResource(modelns + speciesReference.getMetaId());
            speciesRefResource.addProperty(RDF.type, SBMLConstants.MODIFIERSPECIESREFERENCE_CLASS);
            if (!speciesReference.getName().isEmpty())
                speciesRefResource.addProperty(SBMLConstants.NAME, speciesReference.getName());
            speciesRefResource.addProperty(SBMLConstants.SPECIES, resourceMap.get(speciesReference.getSpecies()));
            if (!speciesReference.getSBOTermID().isEmpty())
                sboIdentifiersOrg(speciesRefResource, speciesReference.getSBOTermID());
            extractRDF(speciesRefResource, speciesReference.getCVTerms());
            if(!speciesReference.getNotesString().isEmpty())
                speciesRefResource.addProperty(SBMLConstants.NOTES, getNotesAsText(speciesReference.getNotesString()));
            reactionResource.addProperty(SBMLConstants.MODIFIER, speciesRefResource);
            resourceMap.put(speciesReference.getId(), speciesRefResource);
        }
    }

    private void createKineticLaw(Reaction reaction, Resource reactionResource) {
        KineticLaw kineticLaw = reaction.getKineticLaw();
        if(kineticLaw==null) return;
        Resource kineticLawResource = rdfModel.createResource(modelns+kineticLaw.getMetaId());
        kineticLawResource.addProperty(RDF.type,SBMLConstants.KINETICLAW_CLASS);
        if(!kineticLaw.getSBOTermID().isEmpty())
            sboIdentifiersOrg(kineticLawResource, kineticLaw.getSBOTermID());
        extractRDF(kineticLawResource,kineticLaw.getCVTerms());
        if(!kineticLaw.getNotesString().isEmpty())
            kineticLawResource.addProperty(SBMLConstants.NOTES, getNotesAsText(kineticLaw.getNotesString()));
        reactionResource.addProperty(SBMLConstants.KINETICLAW, kineticLawResource);
        createLocalParameters(kineticLaw,kineticLawResource);
    }

    private void createLocalParameters(KineticLaw kineticLaw, Resource kineticLawResource){
        ListOf<LocalParameter> listOfLocalParameter = kineticLaw.getListOfLocalParameters();
        for(LocalParameter localParameter : listOfLocalParameter){
            Resource localParamResource = rdfModel.createResource(modelns+localParameter.getMetaId());
            localParamResource.addProperty(RDF.type, SBMLConstants.LOCALPARAMETER_CLASS);
            if (!localParameter.getName().isEmpty())
                localParamResource.addProperty(SBMLConstants.NAME, localParameter.getName());
            localParamResource.addLiteral(SBMLConstants.VALUE, localParameter.getValue());
            setUnits(localParamResource,SBMLConstants.UNITS,localParameter.getUnits());
            if(!localParameter.getSBOTermID().isEmpty())
                sboIdentifiersOrg(localParamResource, localParameter.getSBOTermID());
            extractRDF(localParamResource,localParameter.getCVTerms());
            if(!localParameter.getNotesString().isEmpty())
                localParamResource.addProperty(SBMLConstants.NOTES, getNotesAsText(localParameter.getNotesString()));
            kineticLawResource.addProperty(SBMLConstants.LOCALPARAMETERS, localParamResource);
        }

    }



    private void createRules(Resource modelResource) {
        ListOf<Rule> listOfRules = sbmlModel.getListOfRules();
        for (Rule rule : listOfRules) {
            Resource ruleResource = rdfModel.createResource(modelns + rule.getMetaId());
            if (ruleResource instanceof AssignmentRule) {
                ruleResource.addProperty(RDF.type, SBMLConstants.ASSIGNMENTRULE_CLASS);
                ruleResource.addProperty(SBMLConstants.VARIABLE, resourceMap.get(((AssignmentRule) rule).getVariable()));
            }
            if (ruleResource instanceof RateRule){
                ruleResource.addProperty(RDF.type, SBMLConstants.RATERULE_CLASS);
                ruleResource.addProperty(SBMLConstants.VARIABLE, resourceMap.get(((RateRule) rule).getVariable()));
            }
            if (ruleResource instanceof AlgebraicRule){
                ruleResource.addProperty(RDF.type, SBMLConstants.ALGEBRAICRULE_CLASS);
            }
            if (!rule.getSBOTermID().isEmpty())
                sboIdentifiersOrg(ruleResource, rule.getSBOTermID());
            extractRDF(ruleResource, rule.getCVTerms());
            if(!rule.getNotesString().isEmpty())
                ruleResource.addProperty(SBMLConstants.NOTES, getNotesAsText(rule.getNotesString()));
            modelResource.addProperty(SBMLConstants.RULE, ruleResource);
        }
    }

    private void createConstraint(Resource modelResource){
        ListOf<Constraint> listOfConstraints = sbmlModel.getListOfConstraints();
        for (Constraint constraint : listOfConstraints){
            Resource constraintResource = rdfModel.createResource(modelns+constraint.getMetaId());
            constraintResource.addProperty(RDF.type, SBMLConstants.CONSTRAINT_CLASS);
            constraintResource.addLiteral(SBMLConstants.MESSAGE, constraint.getMessageString());
            if (!constraint.getSBOTermID().isEmpty())
                sboIdentifiersOrg(constraintResource, constraint.getSBOTermID());
            extractRDF(constraintResource, constraint.getCVTerms());
            if(!constraint.getNotesString().isEmpty())
                constraintResource.addProperty(SBMLConstants.NOTES, getNotesAsText(constraint.getNotesString()));
            modelResource.addProperty(SBMLConstants.CONSTRAINT, constraintResource);
        }
    }


    private void createEvents(Resource modelResource) {
        ListOf<Event> listOfEvents = sbmlModel.getListOfEvents();
        for (Event event : listOfEvents) {
            Resource eventResource = rdfModel.createResource(modelns + event.getMetaId());
            eventResource.addProperty(RDF.type, SBMLConstants.EVENT_CLASS);
            if(!event.getName().isEmpty())
                eventResource.addProperty(SBMLConstants.NAME, event.getName());
            eventResource.addLiteral(SBMLConstants.USEVALUESFROMTRIGGERTIME, event.getUseValuesFromTriggerTime());
            if (!event.getSBOTermID().isEmpty())
                sboIdentifiersOrg(eventResource, event.getSBOTermID());
            extractRDF(eventResource, event.getCVTerms());
            if(!event.getNotesString().isEmpty())
                eventResource.addProperty(SBMLConstants.NOTES, getNotesAsText(event.getNotesString()));
            modelResource.addProperty(SBMLConstants.EVENT, eventResource);

            createTrigger(event,eventResource);
            createPriority(event, eventResource);
            createDelay(event, eventResource);
            createEventAssignment(event,eventResource);
        }
    }

    private void createTrigger(Event event, Resource eventResource){
        Trigger trigger = event.getTrigger();
        if(trigger==null) return;
        Resource triggerResource = rdfModel.createResource(modelns + trigger.getMetaId());
        triggerResource.addProperty(RDF.type, SBMLConstants.TRIGGER_CLASS);
        triggerResource.addLiteral(SBMLConstants.INITIALVALUE, trigger.getInitialValue());
        triggerResource.addLiteral(SBMLConstants.PERSISTANT,trigger.getPersistent());
        if (!trigger.getSBOTermID().isEmpty())
            sboIdentifiersOrg(triggerResource, trigger.getSBOTermID());
        extractRDF(triggerResource, trigger.getCVTerms());
        if(!trigger.getNotesString().isEmpty())
            triggerResource.addProperty(SBMLConstants.NOTES, getNotesAsText(trigger.getNotesString()));
        eventResource.addProperty(SBMLConstants.TRIGGER,triggerResource);
    }

    private void createPriority(Event event, Resource eventResource){
        Priority priority = event.getPriority();
        if(priority==null) return;
        Resource priorityResource = rdfModel.createResource(modelns + priority.getMetaId());
        priorityResource.addProperty(RDF.type, SBMLConstants.PRIORITY_CLASS);
        if (!priority.getSBOTermID().isEmpty())
            sboIdentifiersOrg(priorityResource, priority.getSBOTermID());
        extractRDF(priorityResource, priority.getCVTerms());
        if(!priority.getNotesString().isEmpty())
            priorityResource.addProperty(SBMLConstants.NOTES, getNotesAsText(priority.getNotesString()));
        eventResource.addProperty(SBMLConstants.PRIORITY,priorityResource);
    }

    private void createDelay(Event event, Resource eventResource){
        Delay delay = event.getDelay();
        if(delay==null) return;
        Resource delayResource = rdfModel.createResource(modelns + delay.getMetaId());
        delayResource.addProperty(RDF.type, SBMLConstants.DELAY_CLASS);
        if (!delay.getSBOTermID().isEmpty())
            sboIdentifiersOrg(delayResource, delay.getSBOTermID());
        extractRDF(delayResource, delay.getCVTerms());
        if(!delay.getNotesString().isEmpty())
            delayResource.addProperty(SBMLConstants.NOTES, getNotesAsText(delay.getNotesString()));
        eventResource.addProperty(SBMLConstants.DELAY,delayResource);
    }

    private void createEventAssignment(Event event, Resource eventResource){
        ListOf<EventAssignment> eventAssignments = event.getListOfEventAssignments();
        for(EventAssignment eventAssignment: eventAssignments){
            Resource eventAssignmentResource = rdfModel.createResource(modelns + eventAssignment.getMetaId());
            eventAssignmentResource.addProperty(RDF.type, SBMLConstants.EVENTASSIGNMENT_CLASS);
            if(!eventAssignment.getVariable().isEmpty())
                eventAssignmentResource.addProperty(SBMLConstants.VARIABLE, resourceMap.get(eventAssignment.getVariable()));
            if (!eventAssignment.getSBOTermID().isEmpty())
                sboIdentifiersOrg(eventAssignmentResource, eventAssignment.getSBOTermID());
            extractRDF(eventAssignmentResource, eventAssignment.getCVTerms());
            if(!eventAssignment.getNotesString().isEmpty())
                eventAssignmentResource.addProperty(SBMLConstants.NOTES, getNotesAsText(eventAssignment.getNotesString()));
            eventResource.addProperty(SBMLConstants.EVENTASSIGNMENT,eventAssignmentResource);
        }
    }

    private void createFunctionDefs(Resource modelResource) {
        ListOf<FunctionDefinition> listOfFunctionDefinitions = sbmlModel.getListOfFunctionDefinitions();
        for (FunctionDefinition functionDefinition : listOfFunctionDefinitions) {
            Resource functionResource = rdfModel.createResource(modelns + functionDefinition.getMetaId());
            functionResource.addProperty(RDF.type, SBMLConstants.FUNCTIONDEF_CLASS);
            if(!functionDefinition.getName().isEmpty())
                modelResource.addProperty(SBMLConstants.NAME, functionDefinition.getName());
            if (!functionDefinition.getSBOTermID().isEmpty())
                sboIdentifiersOrg(functionResource, functionDefinition.getSBOTermID());
            extractRDF(functionResource, functionDefinition.getCVTerms());
            if(!functionDefinition.getNotesString().isEmpty())
                functionResource.addProperty(SBMLConstants.NOTES, getNotesAsText(functionDefinition.getNotesString()));
            modelResource.addProperty(SBMLConstants.FUNCTIONDEF, functionResource);
        }
    }

    private String getNotesAsText(String notes){
        return Jsoup.parse(notes).text();
    }



    private void extractRDF(Resource resource, List<CVTerm> cvTerms) {
        if(cvTerms==null){
            return;
        }

        for(CVTerm cvterm: cvTerms){
            for(String uri : cvterm.getResources()){
                UrlValidator urlValidator = new UrlValidator();
                if(urlValidator.isValid(uri)){
                    Resource annotationResource = getModifiedAnnotationResource(uri);
                    if(cvterm.getQualifierType() == CVTerm.Type.MODEL_QUALIFIER){
                        resource.addProperty(SBMLConstants.createProperty(SBMLConstants.BMURI,cvterm.getModelQualifierType().getElementNameEquivalent()), annotationResource);
                    }else  if(cvterm.getQualifierType() == CVTerm.Type.BIOLOGICAL_QUALIFIER){
                        resource.addProperty(SBMLConstants.createProperty(SBMLConstants.BQURI, cvterm.getBiologicalQualifierType().getElementNameEquivalent()), annotationResource);
                    }
                }else{
                    logger.warn("Invalid resource url in " + modelId + ": "+ uri);
                }

            }
        }
    }

    //Trying to comply with EBI rdf uri rules
    private Resource getModifiedAnnotationResource(String annotationUri) {
        Resource annotationResource = rdfModel.createResource(annotationUri);
        String annotationNS = annotationUri.substring(0, annotationUri.lastIndexOf("/"));
        String mappedString = MappingExtractor.identMap.get(annotationNS);
        if(mappedString!=null){
            String sameAsString = mappedString+annotationUri.substring(annotationUri.lastIndexOf("/"));
            Resource sameAsResource = rdfModel.createResource(sameAsString);
            sameAsResource.addProperty(OWL.sameAs, annotationResource);
            return sameAsResource;
        }
        else {
            return annotationResource;
        }
    }

    private void sboIdentifiersOrg(Resource resource, String sboterm){
        Resource annotationResource = rdfModel.createResource("http://identifiers.org/biomodels.sbo/"+sboterm);
        resource.addProperty(SBMLConstants.createProperty(SBMLConstants.BQURI, "is"), annotationResource);
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
