/**
 * Copyright(c) 2011-2015 by UCredit Inc.
 * All Rights Reserved
 */
package ${mapperPkg};
import java.util.Map;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.ucredit.uudream.web.common.QueryInfo;
import ${modelPkg}.${className};


/**
 * @author ${author}
 * @date   ${date}
 */
@Repository
public interface ${className}Mapper {
	
	 int insert(${className} model);
	
	 int update(${className} model);
	
	 int delete(Number id);
	
	 ${className} selectById(Number id);
	
	 int selectCountByConditions(${className} model);
	
	 Collection<${className}> selectByConditions(${className} model);
	
	 int selectCountByMap(Map<String, Object>  paramsMap);
	
	 Collection<${className}> selectByMap(Map<String, Object>  paramsMap);
	 
	 List<${className}> selectQueryInfo(QueryInfo info);
	 
	 int selectQueryInfoSize(QueryInfo info);
}
