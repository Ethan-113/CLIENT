//创建一个发送信息的线程，但是其实并没有用到，只是备用

package After;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class SendThread extends Thread{
    private Socket socket;
    private String to;

    public SendThread(Socket socket, String to){
        this.socket = socket;
        this.to = to;
    }

    public void run(){
        try {
            OutputStream os=socket.getOutputStream();
            DataOutputStream dos=new DataOutputStream(os);

            while (true){
                Scanner sc = new Scanner(System.in);
                String msg = sc.nextLine();

                //对信息进行格式包装
                String msg_send = to + "#" + msg;

                //输入为UTF格式
                dos.writeUTF(msg_send);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
