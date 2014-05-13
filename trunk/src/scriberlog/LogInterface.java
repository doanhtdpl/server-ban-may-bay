/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scriberlog;

/**
 *
 * @author LinhTA
 */
import java.util.List;

public interface  LogInterface {
    public boolean  writeLog(String category, String logData);
    public boolean  writeLogs(List<String> categorys, List<String> logs);
}
