package unittest;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage.RecipientType;

import org.testng.annotations.Test;

import mailutility.Mailing;

public class TestEmail {

	@Test(enabled=false)
	public void sendTestMail() {


		try {
			
			Mailing smail = new Mailing("10.234.15.16");
			smail.addMessage("Test Email Body");
			smail.attachFile("File to attach");
			smail.setContent();

			smail.addSubject("Test Email");

			smail.setAddress(RecipientType.TO, "email id");
			smail.setAddress(RecipientType.CC, "email id");
			smail.sendAction("email id");
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}
}
