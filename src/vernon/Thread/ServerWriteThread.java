package vernon.Thread;

import vernon.Data.Message;
import vernon.Data.SendList;

import java.io.*;
import java.net.Socket;

public class ServerWriteThread extends Thread {
    private String path;

    private ServerThread serverThread;
    private Socket clientSocket;
    private ObjectOutputStream objectOutputStream;
    public boolean isPauseSend = false;
    public boolean isStopSend = false;

    public ServerWriteThread(ServerThread serverThread, Socket socket, ObjectOutputStream objectOutputStream, Object object){
        this.serverThread = serverThread;
        this.clientSocket = socket;
        this.objectOutputStream = objectOutputStream;
        this.path = object.toString();
    }

    @Override
    public void run() {
        System.out.println("Server Write Thread running...");
        try{
            if(null == clientSocket) return;
            String ip = clientSocket.getInetAddress().getHostAddress();
            File file = new File(path);
            String parent = file.getParent();
            if(null != file
                    && file.exists()
                    && file.canRead()
                    && SendList.sendList().isAllowSend(ip, path)){
                Send(file, parent);
                System.out.println("Finish Send.");
                ForceClientQuit(92, "Finish send.");
            }
            else {
                objectOutputStream.writeObject(new Message(11,"Invalid file."));
                objectOutputStream.flush();
                ForceClientQuit(91, "Send failed.");
            }
        }
        catch (IOException e){
            System.out.println("IO Exception" + e.getMessage());
        }
    }

    private void Send(File file, String parent) throws IOException{
        System.out.println("Sending...");
        if(null!= file
                && file.exists()
                && file.canRead()
                && !file.getName().equals("")){
            //发送目录
            if (file.isDirectory()){
                objectOutputStream.writeObject(new Message(20,file.getAbsolutePath().substring(parent.length())));
                objectOutputStream.flush();
                for (File child : file.listFiles()){
                    Send(child,parent);
                }
            }

            //发送文件
            if(file.isFile()){
                objectOutputStream.writeObject(new Message(21,file.getAbsolutePath().substring(parent.length())));
                objectOutputStream.flush();
                objectOutputStream.writeObject(new Message(22,file.length()));
                objectOutputStream.flush();
                DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                int readLength;
                while(true) {
                    try{
                        if (isPauseSend){
                            join();
                        }
                    }
                    catch (InterruptedException e){
                        System.out.println("Unable to send. Pause.");
                        isPauseSend =  false;
                    }
                    if (isStopSend){
                        System.out.println("Stop send");
                        break;
                    }
                    byte[] data = new byte[1024];
                    readLength = dataInputStream.read(data);
                    System.out.println("Length:" + readLength);
                    if (readLength == -1){
                        break;
                    }
                    objectOutputStream.write(data,0,readLength);
                    objectOutputStream.flush();
                }
                dataInputStream.close();
                if (!isStopSend){
                    System.out.println("Stop send");
                    objectOutputStream.writeObject(new Message(31,file.getName()));
                    objectOutputStream.flush();
                }
            }
        }
        else {
            objectOutputStream.writeObject(new Message(11,"Invalid file."));
            objectOutputStream.flush();
            ForceClientQuit(91,"Force To Quit");
        }
    }


    public void ForceClientQuit(int type, String msg){
        Message message = new Message(type,msg);
        try{
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        }
        catch (IOException e){

        }
        serverThread.Close();
    }

}
