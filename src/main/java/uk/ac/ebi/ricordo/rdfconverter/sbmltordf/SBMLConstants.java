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

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 10-Feb-2012
 *         Time: 15:16:27
 */
public class SBMLConstants {
    public static final String URI ="http://www.ebi.ac.uk/ricordo/toolbox/sbmlo#";
    public static final String URIPREFIX="sbmlo";

    public static final String BQURI ="http://biomodels.net/biology-qualifiers#";
    public static final String BQURIPREFIX ="bqbiol";
    public static final String BMURI ="http://biomodels.net/model-qualifiers#";
    public static final String BMURIPREFIX ="bqmodel";

    private static Model m = ModelFactory.createDefaultModel();

    public static final Resource SBMLMODEL = m.createResource(URI + "SBMLModel" );

    public static final Property SPECIESELEOF = m.createProperty(URI + "speciesElementOf" );
    public static final Property COMPELEOF = m.createProperty(URI + "compartmentElementOf" );
    public static final Property PARAMELEOF = m.createProperty(URI + "parameterElementOf" );
    public static final Property RULEELEOF = m.createProperty(URI + "ruleElementOf" );
    public static final Property REACTELEOF = m.createProperty(URI + "reactionElementOf" );
    public static final Property REACTANTELEOF = m.createProperty(URI + "reactantElementOf" );
    public static final Property PRODUCTELEOF = m.createProperty(URI + "productElementOf" );
    public static final Property MODIFIERELEOF = m.createProperty(URI + "modifierElementOf" );
    public static final Property KINETICELEOF = m.createProperty(URI + "kineticLawElementOf" );
    public static final Property EVENTELEOF = m.createProperty(URI + "eventElementOf" );
    public static final Property FUNCTIONELEOF = m.createProperty(URI + "functionDefElementOf" );
    public static final Property UNITSDEFELEOF = m.createProperty(URI + "unitsDefElementOf" );
    public static final Property UNITELEOF = m.createProperty(URI + "unitElementOf" );

    public static final Property HASNAME = m.createProperty(URI + "hasName" );
    public static final Property HASID = m.createProperty(URI + "hasId" );
    public static final Property HASMetaID = m.createProperty(URI + "hasMetaId" );
    public static final Property HASSIZE = m.createProperty(URI + "hasSize" );
    public static final Property HASCOMPARTMENT = m.createProperty(URI + "hasCompartment" );
    public static final Property HASINITCON = m.createProperty(URI + "hasInitialConcentration" );
    public static final Property HASSBOTERM = m.createProperty(URI + "hasSboTerm" );
    public static final Property HASVALUE = m.createProperty(URI + "hasValue" );
    public static final Property HASVAR = m.createProperty(URI + "hasVariable" );
    public static final Property HASSPECIES = m.createProperty(URI + "hasSpecies" );
    public static final Property HASKIND = m.createProperty(URI + "hasKind" );
    public static final Property HASMULTIPLIER = m.createProperty(URI + "hasMultiplier" );
    public static final Property HASSCALE = m.createProperty(URI + "hasScale" );
    public static final Property HASUNITS = m.createProperty(URI + "hasUnits" );

    public static Property createProperty(String namespace, String qualifier){
        return m.createProperty(namespace + qualifier );
    }
}
