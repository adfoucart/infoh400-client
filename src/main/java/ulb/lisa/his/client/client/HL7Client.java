/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulb.lisa.his.client.client;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.message.ADT_A01;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import ca.uhn.hl7v2.model.v23.segment.PID;
import ca.uhn.hl7v2.parser.Parser;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import ulb.lisa.his.model.Patient;

/**
 *
 * @author Adrien Foucart
 */
public class HL7Client {
    
    private static int last_seqnumber = 0;
    
    public static boolean send_ADT_A01(Patient p, String host, int port){
        try {
            last_seqnumber += 1;
            
            ADT_A01 adt = new ADT_A01();
            adt.initQuickstart("ADT", "A01", "P");
            
            // Populate MSH segment
            MSH msh = adt.getMSH();
            msh.getSendingApplication().getNamespaceID().setValue("HIS-Client");
            msh.getSequenceNumber().setValue(String.valueOf(last_seqnumber));
            
            // Populate PID segment
            PID pid = adt.getPID();
            pid.getPatientName(0).getFamilyName().setValue(p.getPerson().getNameFamily());
            pid.getPatientName(0).getGivenName().setValue(p.getPerson().getNameGiven());
            pid.getPatientIDInternalID(0).getID().setValue(p.getPerson().getNationalNumber());
            // Date of birth
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            Date dob = p.getPerson().getBirthdate();
            pid.getDateOfBirth().getTimeOfAnEvent().setValue(dob);
            pid.getSex().setValue(p.getPerson().getGender());
            
            HapiContext ctx = new DefaultHapiContext();
            Parser parser = ctx.getPipeParser();
            String encodedMsg = parser.encode(adt);
            System.out.println("Encoded message:");
            System.out.println(encodedMsg);
            
            Connection conn = ctx.newClient(host, port, false);
            Initiator init = conn.getInitiator();
            Message rsp = init.sendAndReceive(adt);
            
            String rspString = parser.encode(rsp);
            System.out.println("Response:");
            System.out.println(rspString);
            
            conn.close();
            
            return rsp.getName().equals("ACK");
        } catch (HL7Exception | IOException | LLPException ex) {
            Logger.getLogger(HL7Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;        
    }
    
}
