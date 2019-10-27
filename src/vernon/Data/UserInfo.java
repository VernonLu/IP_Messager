package vernon.Data;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.StringTokenizer;

public class UserInfo {
    private String userName;
    private String hostName;
    private String ipAddress;
    private String port;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }


    public String getHostName() {
        return hostName;
    }


    public String getIpAddress() {
        return ipAddress;
    }


    public String getPort() {
        return port;
    }


    //获取本机信息
    public static UserInfo GetSelf(){
        try{
            Properties properties = System.getProperties();
            String userName = properties.get("user.name").toString();

            InetAddress address = InetAddress.getLocalHost();
            String IP = address.getHostAddress();
            String hostName = address.getHostName();

            UserInfo userInfo = new UserInfo();
            userInfo.setUserName(userName);
            userInfo.setHostName(hostName);
            userInfo.setIpAddress(IP);
            userInfo.setPort(Integer.toString(Constant.PORT));
            return userInfo;
        }
        catch (UnknownHostException e){
            return null;
        }

    }

    //将字符串内容转化为UserInfo
    public static UserInfo toUserInfo(String str){
        //拆分字符串
        StringTokenizer tokenizer = new StringTokenizer(str,"|");

        if (tokenizer.countTokens()==4){
            UserInfo userInfo = new UserInfo();
            userInfo.setUserName(tokenizer.nextToken());
            userInfo.setHostName(tokenizer.nextToken());
            userInfo.setIpAddress(tokenizer.nextToken());
            userInfo.setPort(tokenizer.nextToken());
            return userInfo;
        }
        else return null;

    }

    //将用户信息转化为字符串
    @Override
    public String toString() {//将UserInfo转化为字符串
        return userName+"|"+hostName+"|"+ipAddress+"|"+port;
    }

    //判断是否为同一个用户
    public boolean equals(UserInfo userInfo) {//通过IP判断是否为同用户
        return userInfo.getIpAddress().equals(ipAddress);
    }

}
