package fileutilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * March 4, 2022
 * @author saurabh.shukla
 */

public class SFTPOperations {

	JSch jsch = new JSch();
	Session session;
	Channel channel;
	ChannelSftp sftpChannel;

	Logger logger;
	public SFTPOperations() {

		logger= LogManager.getLogger("SFTP Operations Logs");	
	}

	public void deleteFile(String fileName) {
		logger.info("Deleting File");
		try {

			sftpChannel.rm("//"+fileName);
			logger.info("File Deleted");

		} catch (SftpException e) {

			e.printStackTrace();
		}

	}

	@SuppressWarnings("rawtypes")
	public void copyFile(String fileName, String newFile) {

		try (
				OutputStream os = new FileOutputStream(newFile);
				BufferedOutputStream bos = new BufferedOutputStream(os);

				){
			byte[] buffer = new byte[1024];

			Vector fileList=sftpChannel.ls("//");

			for(int i=0;i<fileList.size();i++) {

				String fDetail=fileList.get(i).toString();

				if(fDetail.contains(fileName) && !fDetail.contains(".tmp")) {

					logger.info(fDetail); 
					break;
				}
			}

			BufferedInputStream bis = new BufferedInputStream(sftpChannel.get(fileName));


			int readCount;
			while ((readCount = bis.read(buffer)) > 0) {

				bos.write(buffer, 0, readCount);
			}

		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Check the existence of the file on sftp server. It will return true or false after waiting for given waiting time.
	 * 
	 * @param folder
	 * @param fileName
	 * @param waitInSeconds
	 * @return
	 */
	public boolean checkFile(String folder, String fileName, int waitInSeconds) {
		boolean fileFound = false;

		try {

			while(waitInSeconds>0) {

				sftpChannel.cd(folder);

				Vector fileList=sftpChannel.ls("./");

				for(int i=0;i<fileList.size();i++) {

					String fDetail=fileList.get(i).toString();

					if(fDetail.contains(fileName) || !fDetail.contains(".tmp")) {

						if(fDetail.contains(fileName)) return true;
					}

				}
				Thread.sleep(1000);
				waitInSeconds--;
			}
		}
		catch(InterruptedException e) {

			e.printStackTrace();
		}
		catch(Exception e) {

			e.printStackTrace();
		}
		return fileFound;
	}


	@SuppressWarnings("rawtypes")
	public void copyFile(String folder, String fileName, String newFile) {

		try (
				OutputStream os = new FileOutputStream(newFile);
				BufferedOutputStream bos = new BufferedOutputStream(os);

				){
			byte[] buffer = new byte[1024];

			sftpChannel.cd(folder);

			Vector fileList=sftpChannel.ls("./");

			for(int i=0;i<fileList.size();i++) {

				String fDetail=fileList.get(i).toString();

				if(fDetail.contains(fileName) && !fDetail.contains(".tmp")) {

					logger.info(fDetail); 

					break;
				}
			}

			BufferedInputStream bis = new BufferedInputStream(sftpChannel.get(fileName));


			int readCount;
			while ((readCount = bis.read(buffer)) > 0) {

				bos.write(buffer, 0, readCount);
			}

		}
		catch(Exception e) {}
	}


	public void downloadFile(String sourceFile, String destFile) {
		logger.info("Downloading File");
		try {
			logger.info(sftpChannel.getHome());

			sftpChannel.get(sourceFile, destFile);

			logger.info("File downloaded");

		} catch (SftpException e) {
			e.printStackTrace();
		}

	}

	public void connect(String sftpUsername,String sftpPassword,String sftpHost, int sftpPort) {

		try {

			logger.info("Connecting SFTP");
			session = jsch.getSession(sftpUsername, sftpHost, sftpPort);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(sftpPassword);
			session.connect();

			channel = session.openChannel("sftp");
			channel.connect();

			sftpChannel = (ChannelSftp) channel;
			logger.info("SFTP Connected");

		}
		catch(Exception e) {

		}
	}

	public void connect(String sftpUsername,String sftpPassword,String sftpHost, int sftpPort, boolean knownHost) {

		try {

			logger.info("Connecting SFTP");
			session = jsch.getSession(sftpUsername, sftpHost, sftpPort);
			jsch.setKnownHosts(System.getProperty("user.home")+File.separator+".ssh"+File.separator+ "known_hosts");	
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(sftpPassword);
			session.connect();

			channel = session.openChannel("sftp");
			channel.connect();

			sftpChannel = (ChannelSftp) channel;
			logger.info("SFTP Connected");

		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}


	public void disconnect() {

		if(session!=null) session.disconnect();
		if(channel!=null) channel.disconnect();

	}


}
