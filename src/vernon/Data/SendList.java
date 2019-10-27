package vernon.Data;

import vernon.Model.FileListModel;

import java.util.Hashtable;

public class SendList {
    private Hashtable<String,String> list; //文件列表

    private SendList(){
        this.list = new Hashtable<String, String>();
    }
    private static SendList instance = new SendList();
    public static SendList sendList(){return instance;};

    //检查该IP是否能被传送该文件
    public boolean isAllowSend(String IP,String path){
        if(list.containsKey(IP)){
            //获取IP
            String value = list.get(IP);
            if(value.indexOf(path)!=-1){
                System.out.println("Allow Send.");
                return true;
            }
        }
        return false;
    }
    //将文件添加至传送列表
    public void AddToSendList(String IP){
        if(list.containsKey(IP)){
            String value = list.get(IP);
            value += FileListModel.getInstance().GetFileList();
            list.put(IP,value);
        }
        else {
            list.put(IP, FileListModel.getInstance().GetFileList());
        }
    }

    //将文件从列表删除
    public boolean RemoveFromSendList(String IP, String path){
        if(isAllowSend(IP, path)){
            String value = list.get(IP);
            int begin = value.indexOf(path);
            if(begin!=-1){
                value = value.substring(begin,begin+path.length()+1);
                list.put(IP, value);
                return true;
            }
        }
        return false;
    }

}
