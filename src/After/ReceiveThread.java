//接收信息线程，接收后发送到相关目标的JPanel

package After;

import GUI.ChatProgramGUI;
import GUI.LoginGUI;

import javax.imageio.IIOException;
import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ReceiveThread extends Thread{
    private Socket socket;
    private ChatProgramGUI window;

    public ReceiveThread(ChatProgramGUI window, Socket socket){
        this.window = window;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream is = socket.getInputStream();
            DataInputStream dis = new DataInputStream(is);

            while (true){
                String msg = dis.readUTF();

                //因为收到的消息格式是namefrom#msg
                String[] parts = msg.split("#");
                if(!parts[0].equals("系统消息")) {
                    //发送给主窗口
                    window.receiveMessage(parts[0], parts[1]);
                }else {
                    worn(parts[1]);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(window.frame, "与服务器断开连接", "系统信息", JOptionPane.WARNING_MESSAGE);
            window.frame.dispose();
        }
    }

    //两种用户消息，小窗口形式
    private void worn(String flag){
        if(flag.equals("0")) {
            JOptionPane.showMessageDialog(window.frame, "对方不在线或已下线，上线时会收到你的信息", "系统信息", JOptionPane.INFORMATION_MESSAGE);
        }
        else if(flag.equals("1")){
            JOptionPane.showMessageDialog(window.frame, "其他用户登录此账号", "系统信息", JOptionPane.WARNING_MESSAGE);
            window.frame.dispose();
        }

    }
}
