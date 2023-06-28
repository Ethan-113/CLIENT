//驱动，群和个人的库写在一起方便修改和调用

package After;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class IPMysql {
    private static String signal;
    private static String group;

    static {
        Properties pro = new Properties();
        InputStream in = IPMysql.class.getResourceAsStream("mysql.properties");
        try {
            pro.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        signal = pro.getProperty("SIGNAL");
        group = pro.getProperty("GROUP");
    }
    public static String toSignal(){
        return signal;
    }

    public static String toGroup(){return group;}
}
