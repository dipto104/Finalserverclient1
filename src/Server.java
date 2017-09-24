import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by Dipto on 9/24/2017.
 */
public class Server {

    // Vector to store active clients
    static Vector<ClientHandler> ar = new Vector<>();

    // counter for clients
    static int i = 0;

    public static void main(String[] args) throws IOException
    {
        // server is listening on port 1234
        ServerSocket ss = new ServerSocket(1234);

        Socket s;

        // running infinite loop for getting
        // client request
        while (true)
        {
            // Accept the incoming request
            s = ss.accept();

            System.out.println("New client request received : " + s);

            // obtain input and output streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos=new DataOutputStream(s.getOutputStream());
            InputStream is=s.getInputStream();
            OutputStream os =s.getOutputStream();

            System.out.println("Creating a new handler for this client...");

            // Create a new handler object for handling this request.
            ClientHandler mtch = new ClientHandler(s,"client " + i, dis,dos, is,os);

            // Create a new Thread with this object.
            Thread t = new Thread(mtch);

            System.out.println("Adding this client to active client list");

            // add this client to active clients list
            ar.add(mtch);

            // start the thread.
            t.start();

            // increment i for new client.
            // i is used for naming only, and can be replaced
            // by any naming scheme
            i++;

        }
    }
}

// ClientHandler class
class ClientHandler implements Runnable
{
    Scanner scn = new Scanner(System.in);
    private String name;
     InputStream is;
     OutputStream os;
    DataInputStream dis;
    DataOutputStream dos;
    Socket s;
    boolean isloggedin;

    // constructor
    public ClientHandler(Socket s, String name,DataInputStream dis,DataOutputStream dos,
                         InputStream is, OutputStream os) {
        this.dis = dis;
        this.dos=dos;
        this.os = os;
        this.is=is;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
    }

    @Override
    public void run() {

        String received;
        while (true)
        {
            try
            {
                // receive the string
                received = dis.readUTF();
                StringTokenizer st = new StringTokenizer(received, "#");
                String filesize = st.nextToken();
                String recipient = st.nextToken();
                long length=Long.parseLong(filesize);

                System.out.println(received);

                if(received.equals("logout")){
                    this.isloggedin=false;
                    this.s.close();
                    break;
                }
                byte[] bytes = new byte[(int) length];

                int offset = 0;
                int numRead = 0;

                while (numRead >= 0) {
                    numRead = is.read(bytes, offset, bytes.length - offset);
                    offset += numRead;
                    System.out.println(bytes.length+" length of byte array");
                }


                if (offset < bytes.length) {
                    throw new IOException("Could not completely read file " );
                }

                for (ClientHandler mc : Server.ar)
                {
                    // if the recipient is found, write on its
                    // output stream

                    if (mc.name.equals(recipient) && mc.isloggedin==true)
                    {
                        mc.dos.writeUTF(filesize);

                        mc.os.write(bytes);
                        break;
                    }
                }
            } catch (IOException e) {

                e.printStackTrace();
            }

        }
        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
