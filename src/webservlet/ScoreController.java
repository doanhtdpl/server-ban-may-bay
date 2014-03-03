/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webservlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import libCore.LogUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author LinhTA
 */
public class ScoreController extends ServerServlet{
    
    private static final Logger log = Logger.getLogger(ScoreController.class);
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doProcess(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        try {
			doProcess(req, resp);
		} catch (Exception ex) {
			log.error(LogUtil.stackTrace(ex));
		}
    
    }

    private void doProcess(HttpServletRequest req, HttpServletResponse resp) {
        
        ScoreAction.getInstance().handle(req, resp);
    }
    
    
}