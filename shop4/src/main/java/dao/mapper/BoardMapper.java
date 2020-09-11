package dao.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import logic.Board;

public interface BoardMapper {

	@Select("select ifnull(max(num),0) from board")
	int maxnum();

	@Insert("insert into board "
			+ "(num, name, pass, subject, content, regdate, file1, "
			+ "readcnt, grp, grplevel, grpstep ) "
			+ "values (#{num}, #{name}, #{pass}, #{subject}, #{content}, now(), "
			+ "#{fileurl}, 0, #{grp}, #{grplevel}, #{grpstep} )")
	void insert(Board board);

	@Select({"<script>",
		"select count(*) from board ",
		"<if test='searchtype != null and searchcontent != null'> "
		+ "where #{searchtype} like #{searchcontent} </if>",
		"</script>"	})
	int count(Map<String, Object> param);

	@Select({"<script>",
		"select num, name, pass, subject,"
			+ "content, file1 fileurl, regdate, readcnt, grp,"
			+ "grplevel, grpstep from board",
		"<if test='searchtype != null and searchcontent != null'> "
		+ "where #{searchtype} like #{searchcontent} </if> "
		+ "order by grp desc, grpstep asc limit #{startrow}, #{limit}",
		"</script>"	})
	List<Board> list(Map<String, Object> param);

	@Select("select num, name, pass, subject,"
			+ "content, file1 fileurl, regdate, readcnt, grp,"
			+ "grplevel, grpstep from board where num=#{num}")
	Board detail(Map<String, Object> param);

	@Update("update board set "
			+ "readcnt = (readcnt+1) where num=#{num}")
	void readcnt(Map<String, Object> param);

	@Update("update board set grpstep = grpstep +1 "
				+ "where grp = #{grp} and grpstep > #{grpstep}")
	void updateGrpStep(Map<String, Object> param);

	@Update("update board set "
				+ "name=#{name}, subject=#{subject}, content=#{content}, "
				+ "file1=#{fileurl} where num=#{num}")
	void update(Board board);

	@Delete("delete from board where num=#{num}")
	void delete(Board board);

	@Select("select name, count(*) cnt from board "
			+ "group by name order by cnt desc LIMIT 0,7")
	List<Map<String, Object>> graph1();

	@Select("SELECT date_format(regdate,'%Y-%m-%d') date, count(*) cnt "
			+ "from board GROUP BY date order by date desc LIMIT 0,7")
	List<Map<String,Object>> graph2();


}
