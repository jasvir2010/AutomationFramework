package unittest;
import org.apache.commons.net.ftp.FTPFile;
import org.testng.annotations.Test;

import fileutilities.FTPOperations;

public class TestFTPOperations {


	@Test(enabled=false)
	public void connectFTP() {

		try {

			FTPOperations ftpops=new FTPOperations("FTP Server","User","Password");
			ftpops.connect();
			System.out.println(ftpops.ftp.printWorkingDirectory());
			FTPFile [] files = ftpops.ftp.listFiles("file folder");
            for(FTPFile file:files) {
            	System.out.println(file.getName());
            }

			ftpops.downloadFile("remote ftp file(source)",
					"local file path(destination)");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
