package vernon.Thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ServerTCPThread extends Thread{

    private ServerSocket serverSocket;
    private boolean isRun = false;
    private Vector<ServerThread> serverThreads = new Vector<ServerThread>();

    public ServerTCPThread(int port) throws IOException{
        System.out.println("Creating Server TCP Thread...");
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        isRun = true;
        try{
            while(isRun && null != serverSocket){
                System.out.println("Server TCP Thread running...");
                Socket client = serverSocket.accept();
                System.out.println("Accepted");
                ServerThread serverThread = new ServerThread(this, client);
                serverThreads.add(serverThread);
                serverThread.start();
            }
        }
        catch (IOException e){
            System.out.println("Server IO exception: "+e.getMessage());
            CloseServer();
        }
    }

    public void Remove(ServerThread serverThread){
        serverThreads.remove(serverThread);
    }

    //关闭服务器
    public void CloseServer(){
        System.out.println("Close Server TCP Thread...");
        isRun = false;
        for (ServerThread serverThread : serverThreads){
            serverThread.Close();
        }
        try{
            if (null != serverSocket){
                serverSocket.close();
            }
            serverSocket = null;
        }
        catch (IOException e){
            System.out.println("Failed to close Server Socket" + e.getMessage());
        }
    }
}
