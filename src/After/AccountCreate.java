//创建账户相关

package After;

import java.sql.*;

public class AccountCreate {
    private String name;
    private String password;

    public AccountCreate(String name, String password){
        this.name = name;
        this.password = password;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException{
        return DriverManager.getConnection(new IPMysql().toSignal(),
                "root","admin");
    }

    private Connection getConnection_Group() throws SQLException {
        return DriverManager.getConnection(new IPMysql().toGroup(),
                "root", "admin");
    }

    //创建账号
    private boolean account_create(){
        String sql_create = "insert into users_list values(null, ?, ?)";
        //是否存在该账号
        boolean exist = false;

        try (Connection c = getConnection();
             Connection c_group = getConnection_Group();
             Statement if_exist = c.createStatement();
             Statement create_list = c.createStatement();
             PreparedStatement create_account = c.prepareStatement(sql_create);
             Statement group = c_group.createStatement())
        {
            String sql_exist = "select * from users_list where name = '"+this.name+"'";
            ResultSet rs = if_exist.executeQuery(sql_exist);

            //返回不为空时说明账号存在
            if(rs.next())
                exist = true;

            //账号不存在时进行创建
            if(!exist){
                create_account.setString(1, this.name);
                create_account.setString(2, this.password);
                create_account.execute();

                //创建好友列表
                create_list.execute(friend_list());

                //在群聊列表也创建姓名
                group.execute(group_list());

                //创建成功返回true
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        //创建失败返回false
        return false;
    }

    //创建好友列表
    private String friend_list(){
        String sql="CREATE TABLE "+this.name+" (" +
                "newornot varchar(10)," +
                "name varchar(255)," +
                "message varchar(255)" +
                ")DEFAULT CHARSET=utf8";
        return sql;
    }

    private String group_list(){
        String sql="CREATE TABLE "+this.name+" (" +
                "name_group varchar(255)" +
                ")DEFAULT CHARSET=utf8";
        return sql;
    }

    //创建账号
    public boolean createSignal(){
        boolean success = account_create();
        return success;
    }

}
