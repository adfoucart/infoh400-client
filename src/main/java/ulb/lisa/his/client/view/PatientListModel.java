/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulb.lisa.his.client.view;

import java.util.ArrayList;
import javax.swing.AbstractListModel;
import org.hl7.fhir.dstu3.model.Patient;

/**
 *
 * @author Adrien Foucart
 */
public class PatientListModel extends AbstractListModel {
    ArrayList<Patient> patients;
    
    public PatientListModel(ArrayList p){
        patients = p;
    }
    
    @Override
    public int getSize() {
        return patients.size();
    }

    @Override
    public Object getElementAt(int index) {
        Patient p = patients.get(index);
        return p.getNameFirstRep().getFamily() + " " + p.getNameFirstRep().getGivenAsSingleString() + " (" + p.getIdElement().getIdPart() + ")";
    }
}