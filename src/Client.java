import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Dipto on 9/24/2017.
 */
public class Client {
    StringProperty sproperty = new SimpleStringProperty();

    Socket connectionSocket;
    ///DataInputStream dis;
    //DataOutputStream dos;





    public static void main(String args[]) throws IOException {


        // sendMessage thread
        final String s;
        Socket connectionSocket = new Socket("localhost", 1234);
       DataInputStream dis= new DataInputStream(connectionSocket.getInputStream());
        DataOutputStream dos = new DataOutputStream(connectionSocket.getOutputStream());

        OutputStream os =connectionSocket.getOutputStream();
        InputStream is=connectionSocket.getInputStream();
            Thread sendMessage = new Thread(new Runnable() {
                @Override
                public void run() {

                    while(true) {
                        // read the message to deliver.
                        Scanner sc = new Scanner(System.in);
                        String msg = sc.nextLine();


                        try {
                            // write on the output stream

                            File file = new File("D:\\level 3  term 2\\computer network sessonal\\gohan.mp4");
                            FileInputStream fis = new FileInputStream(file);
                            BufferedInputStream bis = new BufferedInputStream(fis);

                            //Get socket's output stream


                            //Read File Contents into contents array
                            byte[] contents;
                            long fileLength = file.length();
                            long current = 0;
                            msg=Long.toString(fileLength)+"#"+msg;
                            dos.writeUTF(msg);

                            long start = System.nanoTime();
                            while(current!=fileLength){
                                int size = 100;
                                if(fileLength - current >= size)
                                    current += size;
                                else{
                                    size = (int)(fileLength - current);
                                    current = fileLength;
                                }
                                contents = new byte[size];
                                bis.read(contents, 0, size);
                                os.write(contents);
                                System.out.print("Sending file ... "+(current*100)/fileLength+"% complete!\n");
                            }

                            os.flush();
                            //File transfer done. Close the socket connection!
                            //connectionSocket.close();

                            System.out.println("File sent succesfully!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            });

            // readMessage thread



            Thread readMessage = new Thread(new Runnable() {
                @Override
                public void run() {

                    while (true) {
                        try {
                            // read the message sent to this client
                           // String msg = dis.readUTF();
                            byte[] contents = new byte[100];

                            //Initialize the FileOutputStream to the output file's full path.
                            FileOutputStream fos = new FileOutputStream("D:\\level 3  term 2\\computer network sessonal\\rough\\gohan.mp4");
                            BufferedOutputStream bos = new BufferedOutputStream(fos);


                            //No of bytes read in one read() call
                            int bytesRead = 0;

                            while((bytesRead=is.read(contents))!=-1)
                                bos.write(contents, 0, bytesRead);

                            bos.flush();
                           // connectionSocket.close();

                            System.out.println("File saved successfully!");
                            //s=s+"\n"+msg;
                            //System.out.println(msg);
                        } catch (IOException e) {

                            e.printStackTrace();
                        }

                    }
                }
            });
            sendMessage.start();
            readMessage.start();
        }


    }








