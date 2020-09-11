package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import dao.mapper.UserMapper;
import logic.User;

@Repository
public class UserDao {
	@Autowired
	private SqlSessionTemplate template;
	Map<String,Object> param = new HashMap<>();
	
	public void insert(User user) {
		template.getMapper(UserMapper.class).insert(user);
	}
	public User selectOne(String userid) {
		param.clear();
		param.put("userid",userid);	
		return template.getMapper(UserMapper.class).select(param).get(0);
	}	
	public void update(User user) {
		template.getMapper(UserMapper.class).update(user);
	}
	public void delete(String userid) {
		param.clear();
		param.put("userid", userid);
		template.getMapper(UserMapper.class).delete(param);
	}
	public List<User> list() {
		return template.getMapper(UserMapper.class).select(null);
	}
	public List<User> list(String[] idchks) {
		param.clear();
		param.put("userids", idchks);
		return template.getMapper(UserMapper.class).select(param);
	}
}
