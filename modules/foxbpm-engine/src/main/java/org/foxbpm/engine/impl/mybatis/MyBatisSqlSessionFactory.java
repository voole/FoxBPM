/**
 * Copyright 1996-2014 FoxBPM ORG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author ych
 */
package org.foxbpm.engine.impl.mybatis;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.foxbpm.engine.db.PersistentObject;
import org.foxbpm.engine.exception.FoxBPMException;
import org.foxbpm.engine.impl.entity.DeploymentEntity;
import org.foxbpm.engine.impl.entity.ProcessDefinitionEntity;
import org.foxbpm.engine.impl.entity.ProcessInstanceEntity;
import org.foxbpm.engine.impl.entity.ResourceEntity;
import org.foxbpm.engine.impl.entity.TaskEntity;
import org.foxbpm.engine.impl.entity.TokenEntity;
import org.foxbpm.engine.impl.interceptor.Session;
import org.foxbpm.engine.impl.interceptor.SessionFactory;
import org.foxbpm.engine.sqlsession.ISqlSession;
import org.foxbpm.engine.sqlsession.ISqlSessionFactory;

public class MyBatisSqlSessionFactory implements ISqlSessionFactory,SessionFactory {

	private SqlSessionFactory sqlSessionFactory;
	protected static Map<Class<?>,String>  insertStatements = new ConcurrentHashMap<Class<?>, String>();
	protected static Map<Class<?>,String>  updateStatements = new ConcurrentHashMap<Class<?>, String>();
	protected static Map<Class<?>,String>  deleteStatements = new ConcurrentHashMap<Class<?>, String>();
	protected static Map<Class<?>,String>  selectStatements = new ConcurrentHashMap<Class<?>, String>();
	static{
		insertStatements.put(ProcessInstanceEntity.class, "insertProcessInstance");
		insertStatements.put(TaskEntity.class, "insertTask");
		insertStatements.put(TokenEntity.class, "insertToken");
		insertStatements.put(DeploymentEntity.class, "insertDeployment");
		insertStatements.put(ProcessDefinitionEntity.class, "insertProcessDefinition");
		insertStatements.put(ResourceEntity.class, "insertResource");
		
		updateStatements.put(ProcessInstanceEntity.class, "updateProcessInstance");
		updateStatements.put(TaskEntity.class, "updateTask");
		updateStatements.put(TokenEntity.class, "updateToken");
		updateStatements.put(DeploymentEntity.class, "updateDeployment");
		updateStatements.put(ProcessDefinitionEntity.class, "updateProcessDefinition");
		updateStatements.put(ResourceEntity.class, "updateResource");
		
		selectStatements.put(ProcessInstanceEntity.class, "selectProcessInstanceById");
		selectStatements.put(TaskEntity.class, "selectTaskById");
		selectStatements.put(TokenEntity.class, "selectTokenById");
		selectStatements.put(DeploymentEntity.class, "selectDeploymentById");
		selectStatements.put(ProcessDefinitionEntity.class, "selectProcessDefinitionById");
		selectStatements.put(ResourceEntity.class, "selectResourceById");
	}
	
	public void init(DataSource dataSource) {
		if (sqlSessionFactory == null) {
			InputStream inputStream = null;
			try {
				inputStream = Resources.getResourceAsStream("mybatis/mapping/mappings.xml");
				TransactionFactory transactionFactory = new ManagedTransactionFactory();
				Environment environment = new Environment("default", transactionFactory, dataSource);
		        Reader reader = new InputStreamReader(inputStream);
		        Properties properties = new Properties();
//		        if(databaseType != null) {
//		          properties.put("limitBefore" , DbSqlSessionFactory.databaseSpecificLimitBeforeStatements.get(databaseType));
//		          properties.put("limitAfter" , DbSqlSessionFactory.databaseSpecificLimitAfterStatements.get(databaseType));
//		          properties.put("limitBetween" , DbSqlSessionFactory.databaseSpecificLimitBetweenStatements.get(databaseType));
//		          properties.put("limitOuterJoinBetween" , DbSqlSessionFactory.databaseOuterJoinLimitBetweenStatements.get(databaseType));
//		          properties.put("orderBy" , DbSqlSessionFactory.databaseSpecificOrderByStatements.get(databaseType));
//		          properties.put("limitBeforeNativeQuery" , ObjectUtils.toString(DbSqlSessionFactory.databaseSpecificLimitBeforeNativeQueryStatements.get(databaseType)));
//		        }
		        XMLConfigBuilder parser = new XMLConfigBuilder(reader,"", properties);
		        Configuration configuration = parser.getConfiguration();
		        configuration.setEnvironment(environment);
		        configuration = parser.parse();
		        sqlSessionFactory = new DefaultSqlSessionFactory(configuration);
			} catch (Exception e) {
				throw new RuntimeException(
						"Error while building ibatis SqlSessionFactory: "
								+ e.getMessage(), e);
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private SqlSession getSqlSession(){
		SqlSession sqlSession = null;
		if(sqlSessionFactory == null){
			throw new FoxBPMException("mybatis sqlsession工厂创建失败");
		}
		sqlSession = sqlSessionFactory.openSession();
		if(sqlSession == null){
			throw  new FoxBPMException("mybatis sqlSession创建失败");
		}
		return sqlSession;
	}
	
	public static String getInsertStatement(PersistentObject object) {
	    return insertStatements.get(object.getClass());
	}

	public static String getUpdateStatement(PersistentObject object) {
	    return updateStatements.get(object.getClass());
	}

	public static String getDeleteStatement(Class<?> persistentObjectClass) {
	    return deleteStatements.get(persistentObjectClass);
	}

	public static String getSelectStatement(Class<?> persistentObjectClass) {
		  return selectStatements.get(persistentObjectClass);
	}

	public Class<?> getSessionType() {
		return ISqlSession.class;
	}

	public Session openSession() {
		return new MybatisSqlSession(getSqlSession());
	}
}
