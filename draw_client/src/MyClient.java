import java.io.*;
import java.net.*;

public class MyClient {
    public static Socket smtpSocket = null;
    public static DataOutputStream os = null;
    public static DataInputStream is = null;
    public MyClient()
    {
        try
        {
            smtpSocket = new Socket("127.0.0.1", 8086);
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    public static void main(String[] args) {
        try {
            smtpSocket = new Socket("hostname", 25);
            os = new DataOutputStream(smtpSocket.getOutputStream());
            is = new DataInputStream(smtpSocket.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: hostname");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: hostname");
        }

        if (smtpSocket != null && os != null && is != null) {
            try {

                os.writeBytes("HELLO\n");
                os.close();
                is.close();
                smtpSocket.close();
            } catch (UnknownHostException e) {
                System.err.println("Trying to connect to unknown host: " + e);
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
    }
}
