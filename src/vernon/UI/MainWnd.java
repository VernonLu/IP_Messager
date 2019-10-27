package vernon.UI;

import vernon.Data.Constant;
import vernon.Data.FileInfo;
import vernon.Model.FileListModel;
import vernon.Model.UserListModel;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class MainWnd extends JFrame{
    //整体布局
    private JPanel mainPanel;

    private JTable userTable;

    //User number
    private String user_number = "1";
    private JLabel userNumber = new JLabel(user_number);

    //Button used to refresh the user list
    private JButton btnRefresh = new JButton(Constant.REFRESH_BUTTON);

    private JTextArea textArea = new JTextArea();
    //Button used to send out message and file
    private JButton btnSend = new JButton(Constant.SEND_BUTTON);
    //Button used to add file
    private JButton btnFile = new JButton(Constant.FILE_BUTTON);
    //List of file ready to be sent
    private JLabel fileList = new JLabel("");

    public MainWnd(){
        init();
    }

    public void init() {
        setTitle(Constant.APP_TITLE);
        setSize(Constant.DEFAULT_WINDOW_WIDTH, Constant.DEFAULT_WINDOW_HEIGHT);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(
                (screenSize.width - Constant.DEFAULT_WINDOW_WIDTH) / 2,
                (screenSize.height - Constant.DEFAULT_WINDOW_HEIGHT) / 2);
        setResizable(false);

        initArea();
        setVisible(true);
    }

    //Initialize three areas and add them to main area
    private void initArea(){
        mainPanel = new JPanel(new BorderLayout());

    //region Top Area
        FlowLayout mainFlowLayout = new FlowLayout();
        mainFlowLayout.setAlignment(FlowLayout.LEFT);
        JPanel topPanel = new JPanel(mainFlowLayout);
        topPanel.setPreferredSize(new Dimension(Constant.DEFAULT_WINDOW_WIDTH,Constant.TOP_AREA_HEIGHT));

        //region User List
        JScrollPane userScrollPane = new JScrollPane();
        userScrollPane.setPreferredSize(Constant.USER_LIST_SIZE);

        //Initialize user table
        UserListModel model = UserListModel.getInstance();
        //model.AddUserInfo(UserInfo.GetSelf());
        userTable = new JTable(model);
        TableRowSorter sorter = new TableRowSorter(model);

        userTable.setRowSorter(sorter);
        userTable.setFillsViewportHeight(true);
        userTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumn ipColumn = userTable.getColumn(Constant.USER_TABLE_TITLE[2]);
        ipColumn.setPreferredWidth(120);
        userScrollPane.setViewportView(userTable);
        //endregion

        // region User Count and Refresh Button
        FlowLayout topRightFlowLayout = new FlowLayout();
        topRightFlowLayout.setAlignOnBaseline(true);
        topRightFlowLayout.setAlignment(FlowLayout.CENTER);
        JPanel topRightPanel = new JPanel(topRightFlowLayout);
        topRightPanel.setPreferredSize(Constant.TOP_RIGHT_SIZE);

        //Initialize user number title and add to top right panel
        JLabel userNumTitle = new JLabel(Constant.USER_NUM);
        userNumTitle.setAlignmentX(JLabel.CENTER);
        topRightPanel.add(userNumTitle);

        //Initialize user number text and add to top right panel
        userNumber = new JLabel(user_number);
        userNumber.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        topRightPanel.add(userNumber);

        //Initialize refresh button and add to top right panel
        btnRefresh.setSize(Constant.BUTTON_SHORT);
        btnRefresh.setMnemonic(KeyEvent.VK_R);
        topRightPanel.add(btnRefresh);

        //endregion

        //Add user list scroll panel to top panel
        topPanel.add(userScrollPane);

        //Add top right panel to top panel
        topPanel.add(topRightPanel);

        //Add top panel top main panel
        mainPanel.add(topPanel,BorderLayout.NORTH);
    //endregion

    //region Center Area
        JScrollPane centerScrollPane = new JScrollPane();
        centerScrollPane.setPreferredSize(new Dimension(Constant.DEFAULT_WINDOW_WIDTH,Constant.CENTER_AREA_HEIGHT));
        centerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        textArea.setLineWrap(true);

        centerScrollPane.setViewportView(textArea);

        mainPanel.add(centerScrollPane,BorderLayout.CENTER);
    //endregion

    // region Bottom Area
        JPanel bottomPanel = new JPanel(new FlowLayout());

        fileList = new JLabel("");
        fileList.setPreferredSize(Constant.FILE_LIST_SIZE);

        btnFile.setPreferredSize(Constant.BUTTON_LONG);
        btnFile.setMnemonic(KeyEvent.VK_F);
        btnFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClickFileBtn();
            }
        });

        btnSend.setPreferredSize(Constant.BUTTON_SHORT);
        btnSend.setMnemonic(KeyEvent.VK_S);

        bottomPanel.add(fileList);
        bottomPanel.add(btnFile);
        bottomPanel.add(btnSend);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    // endregion

        add(mainPanel);
    }


    //Show file choose window when file button is clicked
    private void ClickFileBtn(){
        FileListDialog dialog = new FileListDialog(this);
    }

    //Set file name list text
    public void SetFileList(String fileName){
        fileList.setText(fileName);
    }

    //Clear text area
    public void ClearTextArea(){
        textArea.setText("");
    }

    public JButton GetSendButton(){
        return btnSend;
    }

    public JButton GetRefreshButton(){
        return btnRefresh;
    }

    public int GetSelectedRow(){
        int row = userTable.getSelectedRow();
        return row;
    }

    public String GetContent(){
        String fileStr = "";
        for(int i = 0;i<FileListModel.getInstance().getRowCount();i++){
            FileInfo fileInfo = FileListModel.getInstance().GetFileRow(i);
            fileStr += "[" + (fileInfo.isFile()?"1":"0") +"]" + "?" + fileInfo.getFilePath() + "?" + fileInfo.getFileName();
        }
        return "<" + fileStr + ">" + textArea.getText();
    }

    public void SetUserNumber(int number){
        user_number = Integer.toString(number);
        userNumber = new JLabel(user_number);
    }

}