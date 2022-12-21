package mailutility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


/**
 * March 4, 2022
 * @author saurabh.shukla
 */

public class Mailing
{
    MimeMessage message;
    Multipart multipart;
    String messagetxt;

    public Mailing(String emailHost)
    {
        Properties property = new Properties();

        property.setProperty("mail.smtp.host", emailHost);
        
        property.put("mail.smtp.port", "25");

        Session session = Session.getDefaultInstance(property);

        this.message = new MimeMessage(session);
        this.multipart = new MimeMultipart();
    }

    public void sendAction(String senderAddress)
            throws AddressException, MessagingException
    {
        senderAddress(senderAddress);

        Transport.send(this.message);
    }

    private void senderAddress(String senderAddress)
            throws AddressException, MessagingException
    {
        this.message.setFrom(new InternetAddress(senderAddress));
    }

    public void setAddress(Message.RecipientType category, String... addresses)
            throws AddressException, MessagingException
    {
        ArrayList<String> recipients = parseAddress(addresses);
        for (String address : recipients) {
            this.message.addRecipient(category, new InternetAddress(address));
        }
    }

    public ArrayList<String> parseAddress(String... addresses) throws AddressException, MessagingException
    {
        int numberofArgs=0;
        ArrayList<String> addressList=new ArrayList<>();

        //counting number arguments supplied by user.
        numberofArgs=addresses.length;

        if(numberofArgs==1)									// expecting comma separated values
        {
            String[] r=addresses;
            if(r[0].contains(","))							//if comma is present in argument
            {
                String recievers=r[0], address;

                int lastComma=recievers.lastIndexOf(","); 	//position of last comma
                int numberOfEmails;

                if((lastComma)==(recievers.length()-1))		//to check if last email is void
                    recievers=recievers.substring(0, lastComma);		//trim last comma

                numberOfEmails=recievers.length()-(r[0].replace(",","")).length() + 1 ;	//number of commas +1 = number of emails

                for( int i=0; i<numberOfEmails; i++ )
                {
                    //if there is no comma left in string, it is expected that only one address is left.
                    if(recievers.contains(",")==false)
                    {
                        addressList.add(recievers);

                    }
                    else	//two or more addresses available in string.
                    {
                        address=recievers.substring(0,recievers.indexOf(","));
                        addressList.add(address);

                        recievers=recievers.substring(recievers.indexOf(",")+1, recievers.length());
                    }
                }		//end for loop
            }		//end inner if
            else
            {
                if(addresses[0].length()>1)
                {
                    addressList.add(addresses[0]);
                }

            }
        }		//end outer if
        else
        {
            if(addresses.length>0)			//if at least one argument has been added expecting that no comma is present argument value
            {
                for(String address:addresses)
                {
                    addressList.add(address);
                }
            }
        }
        return addressList;
    }

    public ArrayList<String> getAddresses(Message.RecipientType type)
            throws MessagingException
    {
        Address[] recipients = this.message.getRecipients(type);
        ArrayList<String> address = new ArrayList<>();
        for (int i = 0; i < recipients.length; i++)
        {
            InternetAddress addressType = (InternetAddress)recipients[i];

            address.add(addressType.getAddress());
        }
        return address;
    }

    public void addMessage(String msg)
            throws MessagingException
    {
        BodyPart messageBodyPart = new MimeBodyPart();

        messageBodyPart.setText(msg);

        this.multipart.addBodyPart(messageBodyPart);
    }

    public String getMessageContent()
            throws IOException, MessagingException
    {
        return (String)this.message.getContent();
    }

    public void addSubject(String sub)
            throws MessagingException
    {
        this.message.setSubject(sub);
    }

    public String getSubject()
            throws MessagingException
    {
        return this.message.getSubject();
    }

    public void attachFile(String filePath)
            throws MessagingException
    {
        BodyPart messageBodyPart = new MimeBodyPart();

        this.multipart.addBodyPart(messageBodyPart);

        DataSource source = new FileDataSource(filePath);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(new File(filePath).getName());
    }

    public void setContent()
            throws MessagingException
    {
        this.message.setContent(this.multipart);
    }
}

