package vernon.Model;

import vernon.Data.Constant;
import vernon.Data.FileInfo;

import javax.swing.table.DefaultTableModel;

public class FileListModel extends DefaultTableModel {

    private FileListModel() {
        for(String column : Constant.FILE_TABLE_TITLE){
            addColumn(column);
        }
    }

    private static FileListModel instance = new FileListModel();

    public static FileListModel getInstance(){
        return instance;
    }

    public boolean isCellEditable(int row, int column){
        return false;
    }

    public boolean AddFileInfo(FileInfo fileInfo){
        if(null != fileInfo){
            for(int i = 0; i<getRowCount(); i++){
                FileInfo temp = GetFileRow(i);
                if (temp.equals(fileInfo)){
                    return false;
                }
            }
            String isFile = fileInfo.isFile()?"是":"否";
            String[] rows = {fileInfo.getFileName(),fileInfo.getFilePath(),isFile};
            addRow(rows);
            return true;
        }
        return false;
    }

    public FileInfo GetFileRow(int index){
        if(getColumnCount()>=3){
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileName(getValueAt(index,0).toString());
            fileInfo.setFilePath(getValueAt(index,1).toString());
            String isFile = getValueAt(index,0).toString();
            fileInfo.setFile(isFile=="否"?false:true);
            return fileInfo;
        }
        else return null;
    }

    public String GetFileList(){
        String path = "";
        for(int i = 0;i<instance.getRowCount();i++){
            FileInfo fileInfo = instance.GetFileRow(i);
            if(null == fileInfo) break;
            path += fileInfo.getFilePath() + "|";
        }
        return path;
    }

    //Remove file list
    public void ClearAll(){
        for(int i = instance.getRowCount() - 1; i > 0; i--){
            instance.removeRow(i);
        }
    }

}
