/**
 * All Rights Reserved
 */
package com.uudream.web ;
//${controllerPkg}
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import ${servicePkg}.${className}Service;

import java.util.Map;
import java.util.Collection;

import ${modelPkg}.${className};

import  com.uudream.web.common.AbstractController;

/**
 * @author ${author}
 * @date   ${date}
 *          Auto generated, please update the author when you implementing this controller.
 */
@RestController
public class ${className}Controller extends AbstractController{
	
	private final static Logger LOGGER = LoggerFactory.getLogger(${className}Controller.class);
	
	@Autowired
	private ${className}Service ${lowerName}Service; 
	

}
