/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulb.lisa.his.client.client;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import java.util.ArrayList;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;

/**
 *
 * @author Adrien Foucart
 */
public class FHIRClient {
    public FhirContext ctx;
    public IGenericClient client;
    public String serverBase;
    
    public FHIRClient(){
        ctx = FhirContext.forDstu3();
        serverBase = "http://fhirtest.uhn.ca/baseDstu3";
        client = ctx.newRestfulGenericClient(serverBase);
    }
    
    public ArrayList<Patient> searchPatientsByName(String name){
        ArrayList<Patient> searchResponse = new ArrayList();
        
        IQuery<Bundle> query = client.search()
                                .forResource(Patient.class)
                                .where(Patient.NAME.matches().value(name))
                                .returnBundle(Bundle.class);
        Bundle response = query.execute();
        
        response.getEntry().stream().forEach((e) -> {
            searchResponse.add( (Patient) e.getResource() );
        });
        
        return searchResponse;
    }
    
    public Patient getPatientById(String id){  
        Patient patient = client.read().resource(Patient.class).withId(id).execute();
        
        return patient;
    }
}
