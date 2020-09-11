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

import dao.mapper.BoardMapper;
import logic.Board;

@Repository
public class BoardDao {
	@Autowired
	private SqlSessionTemplate template;
	private Map<String,Object> param = new HashMap<>();
	private String boardcolumn = "select num, name, pass, subject,"
			+ "content, file1 fileurl, regdate, readcnt, grp,"
			+ "grplevel, grpstep from board";
	
	public int maxnum() {
		return template.getMapper(BoardMapper.class).maxnum();
	}

	public void insert(Board board) {
		template.getMapper(BoardMapper.class).insert(board);
	}

	public int count(String searchtype, String searchcontent) {
		param.clear();
		param.put("searchtype",searchtype);
		param.put("searchcontent", "%" + searchcontent + "%");
		return template.getMapper(BoardMapper.class).count(param);
	}

	public List<Board> list(Integer pageNum, int limit, String searchtype, String searchcontent) {
		param.clear();
		param.put("searchcontent", "%" + searchcontent + "%");
		param.put("startrow", (pageNum - 1) * limit);
		param.put("limit", limit);		
		return template.getMapper(BoardMapper.class).list(param);
	}

	public Board detail(int num) {
		param.clear();
		param.put("num", num);
		return template.getMapper(BoardMapper.class).detail(param);
	}

	public void readcnt(int num) {
		param.clear();
		param.put("num", num);
		template.getMapper(BoardMapper.class).readcnt(param);
	}

	public void updateGrpStep(Board board) {
		param.clear();
		param.put("grp", board.getGrp());
		param.put("grpstep", board.getGrpstep());
		System.out.println("update= "+ board.getGrp());
		System.out.println("update= "+ board.getGrpstep());
		template.getMapper(BoardMapper.class).updateGrpStep(param);
	}

	public void update(Board board) {
		template.getMapper(BoardMapper.class).update(board);
	}

	public void delete(Board board) {
		template.getMapper(BoardMapper.class).delete(board);
	}

	public List<Map<String,Object>> graph1() {
		return template.getMapper(BoardMapper.class).graph1();
	}

	public List<Map<String,Object>> graph2() {
		return template.getMapper(BoardMapper.class).graph2();
	}



}
