/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webservlet;

import Model.Request.ClientRequest;
import Security.Authenticate;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;
import webservlet.Action.GetInboxAction;

/**
 *
 * @author PhamTanLong
 */
public class GetInboxController extends ServerServlet{
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //do nothing
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        try {
            doProcess(req, resp);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            GetInboxAction.responseDefault(resp);
        }
    
    }
    
    private void doProcess(HttpServletRequest req, HttpServletResponse resp) {
        
        ClientRequest request = new ClientRequest(req);
        
        //DEBUG
        Authenticate auth = new Authenticate(request._appId,request._sign,request._fbID,request._meID);
      
        if(auth.checkAuth()) //DEBUG
        {
            GetInboxAction action = new GetInboxAction();
            action.handle(request, resp); 
        }
        else
        {
            GetInboxAction.responseDefault(resp);
        }
    }
}
