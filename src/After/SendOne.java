//发送一条信息

package After;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class SendOne {
    private Socket socket;
    private String to;

    public SendOne(Socket socket, String to){
        this.socket = socket;
        this.to = to;
    }

    public void Send_this(String message){
        try {
            OutputStream os=socket.getOutputStream();
            DataOutputStream dos=new DataOutputStream(os);

            //对信息进行格式包装
            String msg_send = to + "#" + message;

            //输入为UTF格式
            dos.writeUTF(msg_send);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
