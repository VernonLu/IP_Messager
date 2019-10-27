package vernon.Thread;

import vernon.Data.Constant;
import vernon.Data.SendList;
import vernon.Data.UserInfo;
import vernon.Model.FileListModel;
import vernon.Model.UserListModel;
import vernon.UI.MainWnd;
import vernon.UI.MessageDialog;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UDPThread extends Thread{
    private static InetAddress multiCastAddress = null;//多播
    private MulticastSocket multicastSocket = null;
    private ServerTCPThread server = null;//TCP的server
    private MainWnd mainWnd = null;//主界面
    private boolean isRun = true;//判断程序是否运行
    private DatagramPacket sendPacket;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(Constant.DATE_PATTERN);

    @Override
    public void run() {
        byte[] data = new byte[Constant.MESSAGE_LENGTH];
        DatagramPacket receivePacket = new DatagramPacket(data,data.length);
        sendPacket = new DatagramPacket(data,data.length);
        Online();

        try{
            while(isRun && null != multicastSocket){
                System.out.println("Receiving...");
                multicastSocket.receive(receivePacket);
                System.out.println("Packet received.");
                HandlePacket(receivePacket);

                //Clear packet after receive
                data = new byte[Constant.MESSAGE_LENGTH];
                receivePacket.setData(data);
            }
        }
        catch (IOException e){
            System.err.println(e);
        }

    }

    public UDPThread(){
        try{
            initNet();
        }
        catch (Exception e){
            return;
        }
        mainWnd = new MainWnd();
        SetButtonEvent();
    }

    private void initNet(){
        try{
            multiCastAddress = InetAddress.getByName(Constant.MULTICAST_ADDR);
            multicastSocket = new MulticastSocket(Constant.PORT);
            server = new ServerTCPThread(Constant.PORT);
            server.start();
            multicastSocket.setTimeToLive(2);
        }
        catch (UnknownHostException e){
            String errMessage = "Unknown Address";
            ShowException(errMessage,e);
        }
        catch (IOException e){
            String errMessage = "Port " + Constant.PORT + " already been used";
            ShowException(errMessage,e);
        }
        try{//加入组播
            multicastSocket.joinGroup(multiCastAddress);
        }
        catch (IOException e){
            String errMessage = "Can't join multicast group";
            ShowException(errMessage, e);
        }
    }

    //解析获取到的信息
    private void HandlePacket(DatagramPacket packet){
        byte[] data = packet.getData();
        String content = new String(data);
        content = content.trim();
        if(content.startsWith("[vernon]")){
            //去除信息头
            content = content.substring(8);

            //获取信息类型
            String typeStr = content.substring(1,3);
            int type;

            try{
                type = Integer.parseInt(typeStr);
            }
            catch (NumberFormatException e){
                return;
            }

            //获取文件内容
            content = content.substring(4);

            System.out.println("Receiving message type " + type + " content " + content);

            //根据信息类型处理信息
            switch (type){
                //用户上线
                case 10:{
                    if (UserListModel.getInstance().AddUserInfo(UserInfo.toUserInfo(content))){
                        Online(packet.getAddress(),packet.getPort());
                        int userNumber = UserListModel.getInstance().getRowCount();
                        mainWnd.SetUserNumber(userNumber);
                    }
                }break;
                //用户离线
                case 11:{
                    UserListModel.getInstance().RemoveWithIP(packet.getAddress().getHostAddress());
                }break;
                //用户聊天
                case 20:{
                    System.out.println("Message received");
                    ShowMessageDialog(content,packet.getAddress().getHostAddress());
                }break;
            }
        }
    }

    //打包信息并发送
    private void SendMessage(String header, String content, InetAddress address, int port){
        try{
            byte[] data = (header + content).getBytes();
            sendPacket.setData(data);
            sendPacket.setLength(data.length);
            sendPacket.setAddress(address);
            sendPacket.setPort(port);
            System.out.println(("Sending message:" + header +" " + content + " to " + address + " on " + port));
            multicastSocket.send(sendPacket);
            data = new byte[Constant.MESSAGE_LENGTH];
            sendPacket.setData(data);
        }
        catch (IOException e){
            String errMessage = "Failed to send UDP Message";
            ShowException(errMessage, e);
        }

    }

    //向所有用户发送自己的信息
    private void Online(){
        String header = "[vernon][10]";
        String content = UserInfo.GetSelf().toString();
        SendMessage(header, content, multiCastAddress, Constant.PORT);
        System.out.println("I'm online");
    }

    //向指定用户发送自己的信息
    private void Online(InetAddress address, int port){
        String header = "[vernon][10]";
        String content = UserInfo.GetSelf().toString();
        SendMessage(header, content, address, port);
    }

    //下线时通知其他用户
    private void Offline(){
        String header = "[vernon][11]";
        String content = UserInfo.GetSelf().toString();
        SendMessage(header, content, multiCastAddress, Constant.PORT);
        try{
            multicastSocket.leaveGroup(multiCastAddress);
        }
        catch (IOException e){

        }
    }

    //给用户发送信息
    private void SendData(String content, String IP, int port){
        try{
            String header = "[vernon][20]";
            InetAddress address = InetAddress.getByName(IP);
            SendMessage(header, content, address, port);
            System.out.println("Message sent");
        }
        catch (UnknownHostException e){
            //无法连接到指定用户
            String errMessage = "Unknown IP address";
            ShowException(errMessage, e);
        }
    }

    //解析收到的文本内容，并显示在弹窗中
    private void ShowMessageDialog(String content, String IP){
        System.out.println("Content length: " + content.length());
        if(content.length()>21){
            String time = content.substring(0,19);
            content = content.substring(19);
            int begin = content.indexOf("<");
            int end = content.lastIndexOf(">");
            if( begin == -1 || end == -1){
                return;
            }
            String path = content.substring(begin+1,end);

            content = content.substring(end+1);
            new MessageDialog(time,UserListModel.getInstance().GetUserInfo(IP),path,content,IP,Constant.PORT);
        }

    }

    //显示错误弹窗
    private void ShowException(String msg, Exception e){
        JOptionPane.showMessageDialog(null,msg+":"+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
    }

    //添加按钮点击事件
    private void SetButtonEvent(){
        //添加刷新按钮点击事件
        mainWnd.GetRefreshButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserListModel.getInstance().Reset();
                Online();
            }
        });

        //添加发送按钮点击事件
        mainWnd.GetSendButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date now = new Date();
                String time = dateFormat.format(now);
                String content = mainWnd.GetContent();
                int index = mainWnd.GetSelectedRow();
                content = time + content;

                String IP;
                int port;
                try{
                    IP = UserListModel.getInstance().GetIPAddress(index);
                    port = UserListModel.getInstance().GetPort(index);
                }
                catch (ArrayIndexOutOfBoundsException ex){
                    String errMessage = "You haven't select user";
                    ShowException(errMessage, ex);
                    return;
                }

                //文件上传
                SendList.sendList().AddToSendList(IP);
                //数据打包传送
                SendData(content, IP, port);
                //清空文字输入框
                mainWnd.ClearTextArea();
                //清空文件列表
                FileListModel.getInstance().ClearAll();
                mainWnd.SetFileList("");

            }
        });


        //添加窗口退出监听事件
        mainWnd.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Closing");
                ApplicationQuit();
            }
        });
    }


    private void CloseAll(){
        interrupt();
        CloseUDP();
        if(null != server){
            server.CloseServer();
        }
    }

    //关闭广播端口
    private void CloseUDP(){
        if (null != multicastSocket){
            try{
                multicastSocket.leaveGroup(multiCastAddress);
                multicastSocket.close();
            }
            catch (IOException e){

            }
        }
    }

    //退出程序
    private void ApplicationQuit(){
        Offline();
        CloseAll();
        System.out.println("Application Quit.");
        System.exit(0);
    }
}
