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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 10-Feb-2012
 *         Time: 15:16:27
 */
public class SBMLConstants {
    public static final String URI ="http://identifiers.org/biomodels.db/";
    public static final String URIPREFIX="biodb";

    public static final String BQURI ="http://biomodels.net/biology-qualifiers#";
    public static final String BQURIPREFIX ="bqbiol";
    public static final String BMURI ="http://biomodels.net/model-qualifiers#";
    public static final String BMURIPREFIX ="bqmodel";
    public static final String RSMURI ="http://biomodels.net/qualifiers#";
    public static final String RSMURIPREFIX ="sbmlRdf";
    public static final String VCARDPREFIX ="vCard";
    public static final String TERMSPREFIX ="dcterms";


    private static Model m = ModelFactory.createDefaultModel();

    /*
    * SBML model-element graph
    * SBMLModel --hasSBMLElement-- SBMLElement
    *
    * Class list
    */

    public static final Resource SBMLMODEL = m.createResource(RSMURI + "SBMLModel" );

    public static final Resource ALGEBRAICRULE_CLASS = m.createResource(RSMURI + "AlgebraicRule" );
    public static final Resource ASSIGNMENTRULE_CLASS = m.createResource(RSMURI + "AssignmentRule" );
    public static final Resource COMPARTMENT_CLASS = m.createResource(RSMURI + "Compartment" );
    public static final Resource CONSTRAINT_CLASS = m.createResource(RSMURI + "Constraint" );
    public static final Resource DELAY_CLASS = m.createResource(RSMURI + "Delay" );
    public static final Resource EVENT_CLASS = m.createResource(RSMURI + "Event" );
    public static final Resource EVENTASSIGNMENT_CLASS = m.createResource(RSMURI + "EventAssignment" );
    public static final Resource FUNCTIONDEF_CLASS= m.createResource(RSMURI + "FunctionDef" );
    public static final Resource INITIALASSIGNMENT_CLASS = m.createResource(RSMURI + "InitialAssignment" );
    public static final Resource KINETICLAW_CLASS= m.createResource(RSMURI + "KineticLaw" );
    public static final Resource LOCALPARAMETER_CLASS = m.createResource(RSMURI + "LocalParameter" );
    public static final Resource MODIFIERSPECIESREFERENCE_CLASS = m.createResource(RSMURI + "ModifierSpeciesReference" );
    public static final Resource PARAMETER_CLASS = m.createResource(RSMURI + "Parameter" );
    public static final Resource PRIORITY_CLASS = m.createResource(RSMURI + "Priority" );
    public static final Resource RATERULE_CLASS = m.createResource(RSMURI + "RateRule" );
    public static final Resource REACTION_CLASS= m.createResource(RSMURI + "Reaction" );
    public static final Resource SPECIES_CLASS = m.createResource(RSMURI + "Species" );
    public static final Resource SPECIESREF_CLASS = m.createResource(RSMURI + "SpeciesReference" );
    public static final Resource TRIGGER_CLASS = m.createResource(RSMURI + "Trigger" );
    public static final Resource UNIT_CLASS= m.createResource(RSMURI + "SBMLUnit" );
    public static final Resource UNITSDEF_CLASS= m.createResource(RSMURI + "SBMLUnitsDef" );


    /* Property list
    * */
    public static final Property COMPARTMENT = m.createProperty(RSMURI + "compartment" );
    public static final Property CONSTRAINT = m.createProperty(RSMURI + "constraint" );
    public static final Property DELAY = m.createProperty(RSMURI + "delay" );
    public static final Property EVENT = m.createProperty(RSMURI + "event" );
    public static final Property EVENTASSIGNMENT = m.createProperty(RSMURI + "eventAssignment" );
    public static final Property FUNCTIONDEF = m.createProperty(RSMURI + "functionDef" );
    public static final Property INITASSIGNMENT = m.createProperty(RSMURI + "initialAssignment" );
    public static final Property KINETICLAW = m.createProperty(RSMURI + "kineticLaw" );
    public static final Property LOCALPARAMETERS = m.createProperty(RSMURI + "localParameters" );
    public static final Property MODIFIER = m.createProperty(RSMURI + "modifier" );
    public static final Property PARAMETER = m.createProperty(RSMURI + "parameter" );
    public static final Property PRIORITY = m.createProperty(RSMURI + "priority" );
    public static final Property PRODUCT = m.createProperty(RSMURI + "product" );
    public static final Property REACTANT = m.createProperty(RSMURI + "reactant" );
    public static final Property REACTION = m.createProperty(RSMURI + "reaction" );
    public static final Property RULE = m.createProperty(RSMURI + "rule" );
    public static final Property SPECIES = m.createProperty(RSMURI + "species" );
    public static final Property TRIGGER = m.createProperty(RSMURI + "trigger" );
    public static final Property UNIT = m.createProperty(RSMURI + "unit" );
    public static final Property UNITSDEF = m.createProperty(RSMURI + "unitsDef" );


    public static final Property AREAUNITS = m.createProperty(RSMURI + "areaUnits" );
    public static final Property BOUNDARYCONDITIONS = m.createProperty(RSMURI + "boundaryCondition" );
    public static final Property CONSTANT = m.createProperty(RSMURI + "constant" );
    public static final Property CONVERSIONfACTOR = m.createProperty(RSMURI + "conversionFactor" );
    public static final Property EXPONENT = m.createProperty(RSMURI + "exponent" );
    public static final Property EXTENTUNITS = m.createProperty(RSMURI + "extentUnits" );
    public static final Property HASONLYSUBSTANCEUNITS = m.createProperty(RSMURI + "hasOnlySubstanceUnits" );
    public static final Property INCOMPARTMENT = m.createProperty(RSMURI + "inCompartment" );
    public static final Property REVERSIBLE = m.createProperty(RSMURI + "reversible" );
    public static final Property FAST = m.createProperty(RSMURI + "fast" );
    public static final Property INITIALAMOUNT = m.createProperty(RSMURI + "initialAmount" );
    public static final Property INITIALCONCENTRATION = m.createProperty(RSMURI + "initialConcentration" );
    public static final Property INITIALVALUE = m.createProperty(RSMURI + "initialValue" );
    public static final Property KIND = m.createProperty(RSMURI + "kind" );
    public static final Property LENGTHUNIT = m.createProperty(RSMURI + "lengthUnit" );
    public static final Property MESSAGE = m.createProperty(RSMURI + "message" );
    public static final Property MULTIPLIER = m.createProperty(RSMURI + "multipler" );
    public static final Property NAME = m.createProperty(RSMURI + "name" );
    public static final Property NOTES = m.createProperty(RSMURI + "notes" );
    public static final Property PERSISTANT = m.createProperty(RSMURI + "persistant" );
    public static final Property SCALE = m.createProperty(RSMURI + "scale" );
    public static final Property SIZE = m.createProperty(RSMURI + "size" );
    public static final Property SPATIALDIMENSIONS = m.createProperty(RSMURI + "spatialDimensions" );
    public static final Property STOICHIOMETRY = m.createProperty(RSMURI + "stoichiometry" );
    public static final Property SUBSTANCEUNITS = m.createProperty(RSMURI + "substanceUnits" );
    public static final Property SYMBOL = m.createProperty(RSMURI + "symbol" );
    public static final Property TIMEUNITS = m.createProperty(RSMURI + "timeUnits" );
    public static final Property UNITS = m.createProperty(RSMURI + "units" );
    public static final Property VALUE = m.createProperty(RSMURI + "value" );
    public static final Property VARIABLE = m.createProperty(RSMURI + "variable" );
    public static final Property VOLUMEUNITS = m.createProperty(RSMURI + "volumeUnits" );
    public static final Property USEVALUESFROMTRIGGERTIME = m.createProperty(RSMURI + "useValuesFromTriggerTime" );

    public static final Property CURATED = m.createProperty(BMURI + "curated" );

    public static final Property DCTERMSW3CDTF = m.createProperty(DCTerms.getURI() + "W3CDTF" );

    public static Property createProperty(String namespace, String qualifier){
        return m.createProperty(namespace + qualifier );
    }
}
