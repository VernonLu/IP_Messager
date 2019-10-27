package vernon.Model;

import vernon.Data.Constant;
import vernon.Data.UserInfo;

import javax.swing.table.DefaultTableModel;

public class UserListModel extends DefaultTableModel {



    private UserListModel() {
        for(String column : Constant.USER_TABLE_TITLE){
            addColumn(column);
        }
    }
    private static UserListModel instance = new UserListModel();
    public static UserListModel getInstance(){
        return instance;
    }

    //
    public boolean isCellEditable(int row, int column){
        return false;
    }

    //添加用户信息
    public boolean AddUserInfo(UserInfo userInfo){
        if(null != userInfo){
            for(int i = 0; i<getRowCount(); i++){
                UserInfo temp = GetUserRow(i);
                if (temp.equals(userInfo)){//Return false if there already has the user.
                    return false;
                }
            }
            //Add user to user list.
            String[] rows = {userInfo.getUserName(),userInfo.getHostName(),userInfo.getIpAddress(),userInfo.getPort()};
            instance.addRow(rows);
            return true;
        }
        return false;
    }

    //获取指定用户信息
    public UserInfo GetUserRow(int index){
        if(getColumnCount()>=4){
            String userName = instance.getValueAt(index,0).toString();
            String hostName = instance.getValueAt(index,1).toString();
            String ipAddress = instance.getValueAt(index,2).toString();
            String port = instance.getValueAt(index,3).toString();
            UserInfo userInfo = new UserInfo();
            userInfo.setUserName(userName);
            userInfo.setHostName(hostName);
            userInfo.setIpAddress(ipAddress);
            userInfo.setPort(port);
            return userInfo;
        }
        else return null;
    }

    //获取IP地址
    public String GetIPAddress(int index){
        return instance.getValueAt(index,2).toString();
    }

    //获取端口
    public int GetPort(int index){
        return Integer.parseInt(instance.getValueAt(index,3).toString());
    }

    //通过IP从列表删除指定用户
    public boolean RemoveWithIP(String IP){
        for(int i = 0; i<getRowCount(); i++){
            if(getValueAt(i,2).toString().equals(IP)){
                instance.removeRow(i);
                return true;
            }
        }
        return false;
    }

    //获取用户信息
    public String GetUserInfo(String IP){
        for(int i = 0;i< getRowCount();i++){
            if(getValueAt(i,2).toString().equals(IP)){
                UserInfo userInfo = GetUserRow(i);
                return userInfo.getUserName()+"("+userInfo.getHostName()+")";
            }
        }
        return null;
    }

    //重置，用于刷新
    public void Reset(){
        for(int i = instance.getRowCount() - 1; i > 0; i--){
            instance.removeRow(i);
        }
        System.out.println("User List Table reset");
    }
}
