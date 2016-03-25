/**
 * All Rights Reserved
 */
package ${servicePkg}.impl;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ${mapperPkg}.${className}Mapper;
import ${servicePkg}.${className}Service;
import com.ucredit.uudream.web.common.QueryInfo;
import java.util.Map;
import java.util.Collection;
import java.util.List;
import ${modelPkg}.${className};

/**
 * @author ${author}
 * @date   ${date}
 */
@Service
public class ${className}ServiceImpl implements ${className}Service{
	private final static Logger LOGGER = LoggerFactory.getLogger(${className}Service.class);
	
	@Autowired
    private ${className}Mapper ${lowerName}Mapper;

    public int insert(${className} model) {
    	${className}ServiceImpl.LOGGER.debug("insert ${className}[{}]", ToStringBuilder.reflectionToString(model));
    	return ${lowerName}Mapper.insert(model);
    }
	
    public int update(${className} model) {
    	${className}ServiceImpl.LOGGER.debug("update ${className}[{}]", ToStringBuilder.reflectionToString(model));
    	return ${lowerName}Mapper.update(model);
    }
	
    public int delete(Number id) {
    	${className}ServiceImpl.LOGGER.debug("delete ${className} by id [{}]", id);
    	return ${lowerName}Mapper.delete(id);
    }
	
    public ${className} selectById(Number id) {
    	${className}ServiceImpl.LOGGER.debug("select ${className} by id [{}]", id);
    	return ${lowerName}Mapper.selectById(id);
    }
	
    public int selectCountByConditions(${className} model) {
    	${className}ServiceImpl.LOGGER.debug("select ${className} count by conditions [{}]", ToStringBuilder.reflectionToString(model));
    	return ${lowerName}Mapper.selectCountByConditions(model);
    }
	
    public Collection<${className}> selectByConditions(${className} model) {
    	${className}ServiceImpl.LOGGER.debug("select ${className} by conditions [{}]", ToStringBuilder.reflectionToString(model));
    	return ${lowerName}Mapper.selectByConditions(model);
    }
	
    public int selectCountByMap(Map<String, Object>  paramsMap) {
    	${className}ServiceImpl.LOGGER.debug("select ${className} count by conditions [{}]", ToStringBuilder.reflectionToString(paramsMap));
    	return ${lowerName}Mapper.selectCountByMap(paramsMap);
    }
	
    public Collection<${className}> selectByMap(Map<String, Object>  paramsMap) {
    	${className}ServiceImpl.LOGGER.debug("select ${className} by conditions [{}]", ToStringBuilder.reflectionToString(paramsMap));
    	return ${lowerName}Mapper.selectByMap(paramsMap);
    }
    
    public List<${className}> selectQueryInfo(QueryInfo info){
        ${className}ServiceImpl.LOGGER.debug("select ${className} by QueryInfo [{}]", ToStringBuilder.reflectionToString(info));
        return ${lowerName}Mapper.selectQueryInfo(info);
    }
    
    public int selectQueryInfoSize(QueryInfo info){
        ${className}ServiceImpl.LOGGER.debug("select ${className} count by QueryInfo [{}]", ToStringBuilder.reflectionToString(info));
        return ${lowerName}Mapper.selectQueryInfoSize(info);
    }
    
}
