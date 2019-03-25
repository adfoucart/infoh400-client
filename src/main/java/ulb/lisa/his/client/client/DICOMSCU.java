/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulb.lisa.his.client.client;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.FindSOPClassSCU;
import com.pixelmed.network.IdentifierHandler;
import com.pixelmed.network.MoveSOPClassSCU;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import ulb.lisa.his.client.view.MainWindow;

/**
 *
 * @author Adrien Foucart
 */
public class DICOMSCU {
    
    private class FindScuIdentifierHandler extends IdentifierHandler {
        
        public ArrayList<String> receivedStudyInstanceUIDs = new ArrayList();
        
        @Override
        public void doSomethingWithIdentifier(AttributeList identifier) throws DicomException {
            System.out.println("Received C-FIND response:");
            Set<AttributeTag> receivedTags = identifier.keySet();
            for( AttributeTag tag: receivedTags ){
                System.out.println(tag.toString() + " :: " + identifier.get(tag).getSingleStringValueOrEmptyString());
            }
            
            receivedStudyInstanceUIDs.add(identifier.get(TagFromName.StudyInstanceUID).getSingleStringValueOrEmptyString());
        }
        
    }
    
    public ArrayList<String> doFindScu(String searchPatientName){
        try {
            SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet((String[])null);
            AttributeList identifier = new AttributeList();
            
            FindScuIdentifierHandler handler = new FindScuIdentifierHandler();
            
            //specify attributes to retrieve and pass in any search criteria
            //query root of "study" to retrieve studies
            
            identifier.putNewAttribute(TagFromName.QueryRetrieveLevel).addValue("STUDY");
            identifier.putNewAttribute(TagFromName.PatientName,specificCharacterSet).addValue(searchPatientName);
            identifier.putNewAttribute(TagFromName.PatientID,specificCharacterSet);
            identifier.putNewAttribute(TagFromName.PatientBirthDate);
            identifier.putNewAttribute(TagFromName.PatientSex);
            identifier.putNewAttribute(TagFromName.StudyInstanceUID);
            identifier.putNewAttribute(TagFromName.SOPInstanceUID);
            identifier.putNewAttribute(TagFromName.StudyDescription);
            identifier.putNewAttribute(TagFromName.StudyDate);
            
            //retrieve all studies belonging to patient with name 'Bowen'
            new FindSOPClassSCU("localhost",
                    4242,
                    "ORTHANC",
                    "FINDSCU",
                    SOPClass.StudyRootQueryRetrieveInformationModelFind,
                    identifier,
                    handler);
            
            return handler.receivedStudyInstanceUIDs;
        } catch (DicomException | DicomNetworkException | IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public void doMoveScu(String studyInstanceUID) {
        try {
            AttributeList identifier = new AttributeList();
            { AttributeTag t = TagFromName.QueryRetrieveLevel; Attribute a = new CodeStringAttribute(t); a.addValue("STUDY"); identifier.put(t,a); }
            { AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue(studyInstanceUID); identifier.put(t,a); }
            new MoveSOPClassSCU("localhost",4242,"ORTHANC","MOVESCU","STORESCP",SOPClass.StudyRootQueryRetrieveInformationModelMove,identifier);
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }
    
}
