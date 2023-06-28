//创建客户线程

package After;

import GUI.ChatProgramGUI;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public class Client {
    public String name;
    public Socket socket;
    static String IP;
    public int PORT = 1027;
    static {
        Properties pro = new Properties();
        InputStream in = Client.class.getResourceAsStream("mysql.properties");
        try {
            pro.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        IP = pro.getProperty("IP");
    }

    public Client(String name) throws IOException, SQLException {
        this.name = name;
        //配置文件修改服务器的IP地址
        this.socket = new Socket(IP, PORT);
        new DataOutputStream(socket.getOutputStream()).writeUTF(this.name);
    }

    //创建发送信息的线程，设计时的设想，到最后发现不好实现，备用
    public void chat(String name_opposite){
        new SendThread(socket, name_opposite).start();
    }

    //发送单条信息
    public void send_one(String name_opposite, String message){
        new SendOne(socket, name_opposite).Send_this(message);
    }

    public void receive(ChatProgramGUI window){
        new ReceiveThread(window, socket).start();
    }
}
