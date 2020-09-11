package dao.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import logic.User;

public interface UserMapper {

	@Insert("insert into useraccount "
			+ "(userid, username, password, birthday, phoneno,"
			+ "postcode, address, email) "
			+ "values(#{userid}, #{username}, #{password}, #{birthday}, "
			+ "#{phoneno}, #{postcode}, #{address}, #{email})")
	void insert(User user);

	@Select({"<script>",
			"select * from useraccount ",
			"<if test='userid != null'> where userid=#{userid}</if>",
			"<if test='userid == null'> where userid != 'admin'</if>",
			"<if test='userids != null'> and userid in "
			+ "<foreach collection='userids' item='id' separator=',' "
			+ "open='(' close=')'>#{id}</foreach></if>",
			"</script>"})
	List<User> select(Map<String, Object> param);

	@Update("update useraccount set username=#{username}, "
			+ "birthday=#{birthday}, phoneno=#{phoneno}, postcode=#{postcode},"
			+ "address=#{address}, email=#{email} where userid=#{userid}")
	void update(User user);

	@Delete("delete from useraccount where userid=#{userid}")
	void delete(Map<String, Object> param);


}
