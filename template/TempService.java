/**
 * Copyright(c) 2011-2015 by UCredit Inc.
 * All Rights Reserved
 */
package ${servicePkg};
import java.util.Map;
import java.util.Collection;
import java.util.List;
import com.ucredit.uudream.web.common.QueryInfo;
import ${modelPkg}.${className};
/**
 * @author ${author}
 * @date   ${date}
 */
public interface ${className}Service {
	 
    /**
     * 添加纪录
     * @param model - 模型对象
     * @return
     */
	 int insert(${className} model);
	 
	 /**
	  * 修改记录
	  * @param model - 模型对象
	  * @return
	  */
	 int update(${className} model);
	 
	 /**
	  * 删除记录
	  * @param id - 主键
	  * @return
	  */
	 int delete(Number id);
	 
	 /**
	  * 根据id查询记录
	  * @param id - 主键
	  * @return 
	  */
	 ${className} selectById(Number id);
	 
	 /**
	  * 条件查询记录数
	  * @param model
	  * @return 返回结果数
	  */
	 int selectCountByConditions(${className} model);
	 
	 /**
	  * 条件查询
	  * @param model
	  * @return 结果列表
	  */
	 Collection<${className}> selectByConditions(${className} model);
	 
	 /**
	  * 条件查询结果数
	  * @param paramsMap
	  * @return
	  */
	 int selectCountByMap(Map<String, Object>  paramsMap);
	 
	 /**
      * 条件查询
      * @param paramsMap
      * @return 满足条件的结果
      */
	 Collection<${className}> selectByMap(Map<String, Object>  paramsMap);
	 
	 /**
      * 条件查询
      * @param QueryInfo info
      * @return 满足条件的结果
      */
	 List<${className}> selectQueryInfo(QueryInfo info);
     
	 /**
      * 根据条件查询总记录数
      * @param QueryInfo info
      * @return int
      */
     int selectQueryInfoSize(QueryInfo info);
	 
}
