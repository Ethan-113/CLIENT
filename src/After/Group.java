//群聊相关操作

package After;

import java.io.PipedInputStream;
import java.sql.*;
import java.util.ArrayList;

public class Group {
    private String name_group;
    private String originName_group;

    private int rank;

    public Group(String name_group){
        this.name_group = "__g__"+name_group;
        this.originName_group = name_group;

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(new IPMysql().toGroup(),
                "root","admin");
    }

    //在数据库中创建有关表
    public void create_group(String owner, String member, int rank) throws SQLException{
        setRank(rank);

        String sql="CREATE TABLE "+this.name_group+" (" +
                "name_member varchar(255)" +
                ")DEFAULT CHARSET=utf8";

        try(Connection c = getConnection();
            Statement s = c.createStatement())
        {
            s.execute(sql);
        }

        origin_member(owner, member);
        add_list();
    }

    //添加初始的用户
    private void origin_member(String owner, String member) throws SQLException{
        add(owner);
        add(member);
    }

    //初建群时添加初始成员所用
    private void add(String name) throws SQLException{
        ArrayList<String> list = all_members();
        String sql_self = "insert into "+ name + " value(?)";
        String sql_group = "insert into "+ this.name_group +" value(?)";

        try (Connection c = getConnection();
             PreparedStatement self = c.prepareStatement(sql_self);
             PreparedStatement group = c.prepareStatement(sql_group))
        {
            //冲突检查
            if(!list.contains(name))
            {
                self.setString(1, this.originName_group);
                self.execute();

                group.setString(1, name);
                group.execute();

            }
        }
    }

    //在某人的列表中添加当前群聊
    public boolean add_in(String name) throws SQLException{
        boolean success = false;
        int rank;
        int num_member = 0;

        ArrayList<String> list = all_members();
        String sql_self = "insert into "+ name + " value(?)";
        String sql_group = "insert into "+ this.name_group +" value(?)";
        String sql_getRank = "select * from groups_list where group_name = '" + this.originName_group + "'";
        String sql_memberNum = "select * from " + this.name_group;

        try (Connection c = getConnection();
             PreparedStatement self = c.prepareStatement(sql_self);
             PreparedStatement group = c.prepareStatement(sql_group);
             Statement getRank = c.createStatement();
             Statement memberNum = c.createStatement())
        {
            ResultSet rs_rank = getRank.executeQuery(sql_getRank);
            rs_rank.next();
            rank = rs_rank.getInt("rank");

            ResultSet rs_numMember = memberNum.executeQuery(sql_memberNum);
            while (rs_numMember.next()){
                num_member++;
            }

            //冲突检查
            if(!list.contains(name) & num_member<rank*10)
            {
                self.setString(1, this.originName_group);
                self.execute();

                group.setString(1, name);
                group.execute();

                success = true;
            }
        }

        return success;
    }

    //将某人从当前群聊删除
    public void del_out(String name) throws  SQLException{
        ArrayList<String> list = all_members();
        String sql_self = "delete from " + name + " where name_group = '" + this.originName_group + "'";
        String sql_group = "delete from " + this.name_group + " where name_member = '" + name + "'";

        try (Connection c = getConnection();
             Statement self = c.createStatement();
             Statement group = c.createStatement())
        {
            //查询是否存在
            if(list.contains(name))
            {
                self.execute(sql_self);
                group.execute(sql_group);
            }
        }
    }

    //返回该群的所有成员
    public ArrayList<String> all_members() throws SQLException{
        ArrayList<String> list = new ArrayList<>();
        String sql = "select * from "+this.name_group;

        try (Connection c = getConnection();
             Statement s = c.createStatement())
        {
            ResultSet rs = s.executeQuery(sql);
            while(rs.next())
            {
                String name = rs.getString("name_member");
                list.add(name);
            }
        }

        return list;
    }

    //将当前群名添加到数据库的群名表中
    private void add_list() throws SQLException{
        String sql = "insert into groups_list value(?, ?)";

        try(Connection c = getConnection();
            PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, this.originName_group);
            ps.setInt(2, this.rank);
            ps.execute();
        }
    }

    private void setRank(int rank){
        this.rank = rank;
    }
}
