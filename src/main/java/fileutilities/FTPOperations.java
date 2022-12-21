package fileutilities;

        import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 * March 4, 2022
 * @author saurabh.shukla
 */

public class FTPOperations {

    public FTPClient ftp = null;
    String host;
    String user;
    String pwd;

    public FTPOperations(String host, String user, String pwd) {
       this.host =host;
       this.user=user;
       this.pwd=pwd;

    }

    public void downloadFile(String remoteFilePath, String localFilePath) {
        try  {
            FileOutputStream fos = new FileOutputStream(localFilePath);
            this.ftp.retrieveFile(remoteFilePath, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (this.ftp.isConnected()) {
            try {
                this.ftp.logout();
                this.ftp.disconnect();
            } catch (IOException f) {
                // do nothing as file is already downloaded from FTP server
            }
        }
    }

    public boolean connect() throws Exception {

    	 boolean connectionFlag =false;
    	 ftp = new FTPClient();
         int reply;
         ftp.connect(host);
         reply = ftp.getReplyCode();
         if (!FTPReply.isPositiveCompletion(reply)) {
             ftp.disconnect();
             throw new Exception("Exception in connecting to FTP Server");
         }
         connectionFlag=  ftp.login(user, pwd);
         ftp.setFileType(FTP.BINARY_FILE_TYPE);
         ftp.enterLocalPassiveMode();

         return connectionFlag;
    }
}

