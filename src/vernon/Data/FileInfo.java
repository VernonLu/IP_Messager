package vernon.Data;

import java.io.Serializable;

public class FileInfo implements Serializable {
    private String fileName;
    private String filePath;
    private boolean isFile = true;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public static FileInfo stringToFileInfo(String str) {
        try {
            int begin = str.indexOf("[");
            int end = str.indexOf("]");
            if (begin == -1 || end == -1) {
                return null;
            }
            int type = Integer.parseInt(str.substring(begin + 1, end));

            str = str.substring(end + 1);
            begin = str.indexOf("?");
            end = str.lastIndexOf("?");
            if (begin == -1 || end == -1) {
                return null;
            }
            String path = str.substring(begin + 1, end);

            String name = str.substring(end + 1);

            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileName(name);
            fileInfo.setFilePath(path);
            fileInfo.setFile(type == 1 ? true : false);

            return fileInfo;
        } catch (NumberFormatException e) {
            return null;
        } catch (Exception e) {
            return null;
        }

    }
}
