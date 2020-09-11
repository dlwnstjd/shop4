package dao.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import logic.Sale;

public interface SaleMapper {

	@Select("SELECT IFNULL(MAX(saleid),0) FROM sale")
	int maxSaleid();

	@Insert("insert into sale (saleid, userid, saledate) "
				+ "values (#{saleid}, #{userid}, now())")
	void insert(Sale sale);

	@Select("select * from sale where userid = #{userid}")
	List<Sale> list(Map<String, Object> param);

}
