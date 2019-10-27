package vernon.Thread;

import vernon.Data.Message;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread{
    private Socket clientSocket = null;
    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;
    private boolean isRun = true;
    private ServerWriteThread serverWriteThread = null;
    private ServerTCPThread serverTCPThread;

    public ServerThread(ServerTCPThread serverTCPThread, Socket client){
        System.out.println("Creating Server Thread...");
        try{
            this.serverTCPThread = serverTCPThread;
            this.clientSocket = client;
            objectInputStream = new ObjectInputStream(new BufferedInputStream(new DataInputStream(client.getInputStream())));
            objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new DataOutputStream(client.getOutputStream())));
        }
        catch(IOException e){
            System.out.println("Unable to get input or output stream: " + e.getMessage());
            Close();
        }
    }

    @Override
    public void run() {
        System.out.println("Server Thread running...");
        try{
            if(null == objectInputStream || null == objectOutputStream) return;
            while(isRun){
                System.out.println("Reading Object...");
                Object obj = objectInputStream.readObject();
                if(obj instanceof Message){
                    Message message = (Message)obj;
                    int type = message.getType();
                    System.out.println(type);
                    switch (type){
                        case 10://开始传输
                            serverWriteThread = new ServerWriteThread(this, clientSocket, objectOutputStream, message.getObject());
                            serverWriteThread.start();
                            break;
                        case 40://暂停传输
                            serverWriteThread.isPauseSend = true;
                            break;
                        case 41://恢复发送
                            serverWriteThread.interrupt();
                            break;
                        case 90://停止传输、关闭线程
                            isRun = false;
                            serverWriteThread.isStopSend = true;
                            Close();
                            break;
                        default:
                            System.out.println("(Server):Unknown message type.");
                    }
                }
            }
        }
        catch (ClassNotFoundException e){
            System.out.println("Class Not Found Exception: " + e.getMessage());
        }
        catch (IOException e){
            System.out.println("IO Exception: " + e.getMessage());
        }
    }

    //Close ServerThread
    public void Close(){
        System.out.println("Closing Server Thread...");
        //Close Client
        try{
            serverTCPThread.Remove(this);
        }
        catch (RuntimeException e){}
        try{
            if (null != objectOutputStream){
                objectOutputStream.close();
                objectOutputStream = null;
            }
            if (null != objectInputStream){
                objectInputStream.close();
                objectInputStream = null;
            }
            if (null != clientSocket){
                clientSocket.close();
                clientSocket = null;
            }
        }
        catch (IOException e){
            System.out.println("Failed to close ServerThread: " + e.getMessage());
        }
    }
}
