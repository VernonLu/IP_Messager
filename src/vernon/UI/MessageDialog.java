package vernon.UI;

import vernon.Data.Constant;
import vernon.Data.FileInfo;
import vernon.Thread.ClientTCPThread;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;

public class MessageDialog extends JFrame {

    private String time;
    private String sender;


    private String IP;
    private int port;
    private String content;
    private Vector<FileInfo> fileList = new Vector<FileInfo>();

    private JFrame messageDialogFrame;
    private JButton btnFile;
    private JButton btnClose;


    private ClientTCPThread clientTCPThread;


    public MessageDialog(String time,String sender,String path,String content,String IP,int port){
        this.time = time;
        this.sender = sender;
        this.IP = IP;
        this.port = 0;
        this.port = port;
        this.content = content;
        initFileList(path);
        init();
    }

    private void init(){
        System.out.println("Show message.");
        messageDialogFrame = new JFrame(Constant.MESSAGE_TITLE);
        messageDialogFrame.setSize(Constant.MESSAGE_DIALOG_WIDTH,Constant.MESSAGE_DIALOG_HEIGHT);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        messageDialogFrame.setLocation(
                (screenSize.width - Constant.MESSAGE_DIALOG_WIDTH)/2,
                (screenSize.height - Constant.MESSAGE_DIALOG_HEIGHT)/2);
        messageDialogFrame.setResizable(false);
        final JPanel mainPanel = new JPanel(new BorderLayout());

        //region Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED),"Message From:"));
        topPanel.setPreferredSize(Constant.MESSAGE_TOP_PANEL_SIZE);

        JLabel senderLabel = new JLabel(sender);
        senderLabel.setPreferredSize(new Dimension(380, 10));

        JLabel timeLabel = new JLabel(time);
        timeLabel.setPreferredSize(new Dimension(380, 10));

        topPanel.add(senderLabel);
        topPanel.add(timeLabel);
        //endregion

        //region Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout(8,8));


        initFileListBtn();
        centerPanel.add(btnFile,BorderLayout.NORTH);

        JTextArea textArea = new JTextArea();
        textArea.append(content);
        textArea.setEditable(false);
        centerPanel.add(textArea);
        //endregion

        // region Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setPreferredSize(Constant.MESSAGE_BOTTOM_PANEL_SIZE);
        btnClose = new JButton(Constant.CLOSE_BUTTON);
        btnClose.setPreferredSize(Constant.BUTTON_SHORT);
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                messageDialogFrame.dispose();
                return;
            }
        });
        bottomPanel.add(btnClose);
        // endregion

        mainPanel.add(topPanel,BorderLayout.NORTH);
        mainPanel.add(centerPanel,BorderLayout.CENTER);
        mainPanel.add(bottomPanel,BorderLayout.SOUTH);
        messageDialogFrame.add(mainPanel);
        messageDialogFrame.setVisible(true);
    }

    private void initFileList(String path){
        System.out.println("Analyzing files...");
        StringTokenizer tokenizer = new StringTokenizer(path,"|");
        int count = 0;
        while (tokenizer.hasMoreTokens()){
            System.out.println("File " + ++count);
            String filePath = tokenizer.nextToken();
            System.out.println(filePath);
            FileInfo fileInfo = FileInfo.stringToFileInfo(filePath);
            if (null != fileInfo){
                fileList.add(fileInfo);
            }
        }
    }

    public void initFileListBtn(){

        String str = "File: ";
        btnFile = new JButton(str);

        if(fileList.size()==0){
            btnFile.setEnabled(false);
        }
        else {
            for(FileInfo fileInfo : fileList){
                str += fileInfo.getFileName() + " ";
            }
        }
        btnFile.setText(str);
        btnFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ReceiveFile();
            }
        });

    }

    private void ReceiveFile(){
        if (null == clientTCPThread){
            FileInfo fileInfo = fileList.get(0);
            fileList.remove(0);
            DirSelectDialog chooser = new DirSelectDialog(this,"Select saving path");
            File dir = chooser.getSelectedFile();
            if (dir == null) return;
            if (dir.exists() && dir.canRead()){
                System.out.println("Receiving File...");
                clientTCPThread = new ClientTCPThread(this,IP,port,fileInfo.getFilePath(),dir.getAbsolutePath());
                System.out.println("Starting Client TCP thread...");
                clientTCPThread.start();
            }
            else {
                initFileListBtn();
            }
        }
        else if(clientTCPThread.isRun()){
            ConfirmStop();
        }
    }

    private boolean ConfirmStop(){
        if (null != clientTCPThread && clientTCPThread.isRun()){
            int quitOption = JOptionPane.showConfirmDialog(null,"You really want to quit without saving files?","Warning",JOptionPane.OK_CANCEL_OPTION);
            if (quitOption == JOptionPane.OK_OPTION){
                clientTCPThread.QuitReceive();
                clientTCPThread = null;
                return true;
            }
        }

        return false;
    }



}