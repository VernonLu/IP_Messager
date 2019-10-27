package vernon.Thread;

import vernon.Data.Message;
import vernon.UI.MessageDialog;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientTCPThread extends Thread{
    private boolean isRun = true;
    private boolean isSending = false;
    private Socket server = null;
    private MessageDialog messageDialog;
    private String path;//Source path
    private String savePath;//Local save path
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private DataOutputStream dataOutputStream;
    private long fileLength;
    private String fileName;

    public ClientTCPThread(MessageDialog messageDialog,String IP, int port, String path, String savePath){
        System.out.println("Creating client TCP thread...");
        try{
            this.messageDialog = messageDialog;
            this.path = path;
            this.savePath = savePath;
            this.server = new Socket(IP,port);
            this.outputStream = new ObjectOutputStream(new BufferedOutputStream(new DataOutputStream(server.getOutputStream())));
            WriteObject(10, path);
            this.inputStream = new ObjectInputStream(new BufferedInputStream(new DataInputStream(server.getInputStream())));
        }
        catch (UnknownHostException e){
            System.out.println("Unknown Host Exception:");
            System.out.println(e.getMessage());
            CloseClient();
        }
        catch (IOException e){
            System.out.println("IO Exception ");
            System.out.println(e.getMessage());
            CloseClient();
        }

    }

    @Override
    public void run() {
        System.out.println("Client TCP Thread running...");
        try{
            if (null == inputStream || null == outputStream) return;
            while(isRun){
                if (!isSending){//
                    Object object = inputStream.readObject();
                    if(object instanceof Message){
                        Message message = (Message)object;
                        int type = message.getType();
                        switch (type){
                            case 11://Invalid file path
                                JOptionPane.showMessageDialog(null,message.getObject());
                                break;
                            case 20://Receiving directory
                                CreateDirectory(message.getObject());
                                break;
                            case 21://Receiving file
                                CreateFile(message.getObject());
                                break;
                            case 22://Change to file receive state
                                ReceiveFile(message.getObject());
                                break;
                            case 31://Stop receive file
                                PauseReceive();
                                break;
                            case 91://Server quit
                                ServerQuit();
                                break;
                            case 92://Finish sending file
                                ReceiveSuccess();
                                break;
                            default:
                                System.out.println("(Client):Unknown message type.");
                        }
                    }
                }
                else {
                    while(isRun){
                        int readLength;
                        byte data[] = new byte[1024];
                        readLength = inputStream.read(data);
                        System.out.println("Receiving File Length:" + readLength);
                        if (readLength == -1){
                            break;
                        }
                        else {
                            System.out.println("Writing file");
                            dataOutputStream.write(data,0,readLength);
                        }
                    }
                    CloseFile();
                    isSending = false;
                }

            }
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
        catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
    }

    //向服务器发送请求
    private void WriteObject(int type, Object object){
        try{
            outputStream.writeObject(new Message(type,object));
            outputStream.flush();
        }
        catch (IOException e){
            System.out.println("Unable to write object");
        }
    }

    //开始接收文件
    private void ReceiveFile(Object object){
        isSending = true;
        if (object instanceof Long){
            fileLength = (Long)object;
            System.out.println("File Length:" + fileLength);
        }
    }

    //创建目录
    private void CreateDirectory(Object object){
        try{
            String path = object.toString();
            path = savePath + path;
            System.out.println("Saving to:" + path);
            File file = new File(path);
            if(!file.exists()){
                file.mkdirs();
            }
        }catch (RuntimeException e){
            System.out.println("Failed to create dir:" + path + " " + e.getMessage());
        }
    }

    //创建文件
    private void CreateFile(Object object){
        try{
            String path = object.toString();
            path = savePath + path;
            System.out.println("Saving to:" + path);
            File file = new File(path);
            if (file.exists()){
                System.out.println("Deleting existing file...");
                file.delete();
            }
            else {
                File parentDir = file.getParentFile();
                System.out.println("Parent dir path:" + parentDir.getAbsolutePath());
                if (!parentDir.exists()){
                    System.out.println("Creating parent dir path...");
                    parentDir.mkdirs();
                }
            }
            fileName = file.getName();
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        }
        catch (IOException e){
            System.out.println("Failed to create file:" + fileName + " " + e.getMessage());
        }
    }

    //退出接收文件
    public void QuitReceive(){
        System.out.println("Quit receiving.");
        WriteObject(90, null);
        isRun = false;
        CloseFile();
        CloseClient();
        messageDialog.initFileListBtn();
    }

    //暂停接收文件
    private void PauseReceive(){
        System.out.println("Pause send");
        WriteObject(40,null);
    }

    //文件接收成功
    private void ReceiveSuccess(){
        System.out.println("File download success");
        JOptionPane.showMessageDialog(null,"File download success");
        ServerQuit();
    }

    //关闭文件输出流
    private void CloseFile(){
        try{
            if(null != dataOutputStream){
                dataOutputStream.flush();
                dataOutputStream.close();
            }

        }
        catch (IOException e){
            System.out.println("Failed to close data output stream" + e.getMessage());
        }
    }

    //断开连接时关闭文件流
    private void CloseClient(){
        System.out.println("Closing Client...");
        try{
            if (null != outputStream){
                outputStream.close();
            }
            if (null != inputStream){
                inputStream.close();
            }
            if (null != server){
                server.close();
            }
            if (null != dataOutputStream){
                dataOutputStream.close();
            }
        }
        catch (IOException e){
            System.out.println("Error occurred when closing client: "+e.getMessage());
        }
    }

    //退出
    private void ServerQuit(){
        isRun = false;
        CloseClient();
    }

    //判断是否在运行
    public boolean isRun(){
        return isRun;
    }

}
