package vernon.UI;

import javax.swing.*;
import java.awt.*;

public class DirSelectDialog extends JFileChooser{

    public DirSelectDialog(Component parent, String title, int mode) {//打开文件
        super(System.getProperty("user.dir"));
        setFileSelectionMode(mode);
        setDialogTitle(title);
        showOpenDialog(parent);
    }


    public DirSelectDialog(Component parent,String title) {//保存文件
        setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        setDialogTitle(title);
        showSaveDialog(parent);
    }
}
