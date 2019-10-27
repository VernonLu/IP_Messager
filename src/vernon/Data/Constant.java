package vernon.Data;

import java.awt.*;

public class Constant {
    public static String APP_TITLE = "IP Messager";
    public static String USER_NUM = "在线用户:";
    public static String REFRESH_BUTTON = "刷新(R)";
    public static String SEND_BUTTON = "发送(S)";
    public static String FILE_BUTTON = "添加文件(F)";
    public static String DIR_BUTTON = "添加文件夹";
    public static String DELETE_BUTTON = "删除";
    public static String CLOSE_BUTTON = "关闭";
    public static String[] USER_TABLE_TITLE = {"用户名","主机名","IP地址","端口号"};
    public static int DEFAULT_WINDOW_WIDTH = 450;
    public static int DEFAULT_WINDOW_HEIGHT = 440;
    public static int TOP_AREA_HEIGHT = 150;
    public static int CENTER_AREA_HEIGHT = 250;

    public static Dimension USER_LIST_SIZE = new Dimension(348,140);
    public static Dimension TOP_RIGHT_SIZE = new Dimension(80,140);
    public static Dimension BUTTON_LONG = new Dimension(100,25);
    public static Dimension BUTTON_SHORT = new Dimension(80,25);
    public static Dimension FILE_LIST_SIZE = new Dimension(200,30);


    public static String FILE_LIST_DIALOG_TITLE = "Choose files";
    public static int FILE_LIST_DIALOG_WIDTH = 400;
    public static int FILE_LIST_DIALOG_HEIGHT = 200;
    public static String[] FILE_TABLE_TITLE = {"文件名","文件路径","是否为文件"};
    public static Dimension FILE_LIST_TABLE_SIZE = new Dimension(400,125);


    public static String MESSAGE_TITLE = "New Message";
    public static int MESSAGE_DIALOG_WIDTH = 400;
    public static int MESSAGE_DIALOG_HEIGHT = 300;
    public static Dimension MESSAGE_TOP_PANEL_SIZE = new Dimension(400,70);
    public static Dimension MESSAGE_BOTTOM_PANEL_SIZE = new Dimension(400,40);


    public static int MESSAGE_LENGTH = 8192;
    public static int PORT = 37608;
    public static String MULTICAST_ADDR = "236.232.12.25";
    public static String DATE_PATTERN = "yyyy/MM/dd HH:mm:ss";

}
