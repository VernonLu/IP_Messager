package vernon.UI;

import vernon.Data.Constant;
import vernon.Data.FileInfo;
import vernon.Model.FileListModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class FileListDialog extends JDialog{
    private MainWnd owner;
    private JFrame mainFrame;
    private JPanel mainPanel;
    private FileListModel tableModel = null;
    private JTable fileListTable = null;
    private JButton fileBtn;
    private JButton dirBtn;
    private JButton deleteBtn;
    private JButton closeBtn;

    public FileListDialog(MainWnd owner){
        //
        this.owner = owner;
        mainFrame = new JFrame(Constant.FILE_LIST_DIALOG_TITLE);
        mainFrame.setSize(new Dimension(Constant.FILE_LIST_DIALOG_WIDTH,Constant.FILE_LIST_DIALOG_HEIGHT));
        mainFrame.setResizable(false);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                CloseWindow();
            }
        });
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int posX = (screenSize.width - Constant.FILE_LIST_DIALOG_WIDTH) / 2;
        int posY = (screenSize.height - Constant.FILE_LIST_DIALOG_HEIGHT) /2 ;
        mainFrame.setLocation(posX, posY);

        mainPanel = new JPanel(new FlowLayout());

        initFileTable();

        initFileButton();
        initDirButton();
        initDelButton();
        initCloseButtons();
        mainPanel.add(fileBtn);
        mainPanel.add(dirBtn);
        mainPanel.add(deleteBtn);
        mainPanel.add(closeBtn);

        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);
    }

    private void initFileTable(){
        tableModel = FileListModel.getInstance();

        fileListTable = new JTable(tableModel);

        JScrollPane fileScrollPane = new JScrollPane();
        fileScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        fileScrollPane.setPreferredSize(Constant.FILE_LIST_TABLE_SIZE);
        fileScrollPane.setViewportView(fileListTable);
        mainPanel.add(fileScrollPane);
    }

    //Buttons
    private void initFileButton(){
        fileBtn = new JButton(Constant.FILE_BUTTON);
        fileBtn.setPreferredSize(Constant.BUTTON_LONG);
        fileBtn.setMnemonic(KeyEvent.VK_F);
        fileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.getSelectedFile();
                chooser.setDialogTitle(Constant.FILE_BUTTON);
                chooser.showOpenDialog(null);
                File file = chooser.getSelectedFile();

                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileName(file.getName());
                fileInfo.setFilePath(file.getAbsolutePath());
                fileInfo.setFile(true);

                tableModel.AddFileInfo(fileInfo);

            }
        });
    }

    private void initDirButton(){
        dirBtn = new JButton(Constant.DIR_BUTTON);
        dirBtn.setPreferredSize(Constant.BUTTON_LONG);
        dirBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DirSelectDialog chooser = new DirSelectDialog(null, "Choose Directory", JFileChooser.DIRECTORIES_ONLY);
                File file = chooser.getSelectedFile();

                FileInfo fileInfo = new FileInfo();
                fileInfo.setFile(false);
                fileInfo.setFileName(file.getName());
                fileInfo.setFilePath(file.getAbsolutePath());

                tableModel.AddFileInfo(fileInfo);
            }
        });
    }

    private void initDelButton(){
        deleteBtn = new JButton(Constant.DELETE_BUTTON);
        deleteBtn.setPreferredSize(Constant.BUTTON_SHORT);
        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] rows = fileListTable.getSelectedRows();
                //从后向前遍历防止删除时出错
                for(int i = rows.length - 1; i>=0; i--){
                    tableModel.removeRow(rows[i]);
                }
            }
        });

    }

    private void initCloseButtons(){
        closeBtn = new JButton(Constant.CLOSE_BUTTON);
        closeBtn.setPreferredSize(Constant.BUTTON_SHORT);
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CloseWindow();
            }
        });
    }

    private String GetFileNames(){
        String str = "";
        for(int i = 0;i<tableModel.getRowCount();i++){
            str += tableModel.getValueAt(i,0)+"|";
        }
        return str;
    }

    private void CloseWindow(){
        owner.SetFileList(GetFileNames());

        //Close choose file dialog
        mainFrame.dispose();
    }

}
