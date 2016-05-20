/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.makarov_s_a.daoproject.tests;

import com.makarov_s_a.daoproject.ReflectionJdbcDao;
import com.makarov_s_a.daoproject.ReflectionJdbcDaoImplementation;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author makarov_s_a
 */
public class DAOTests {

	private static ReflectionJdbcDao<JavaBean> daoInstance;
	private static Connection connection;
	private static Statement statement;
	
	private final long firstId = 100;
	private final long secondId = 1;
	private final String someField = "someStringValue";
	private final Date currentDate = new Date(new java.util.Date().getTime());
	private final int count = 5;

	public DAOTests() {
	}

	@BeforeClass
	public static void setUpClass() throws SQLException {
		connection = DriverManager.getConnection("jdbc:derby:memory:codejava/webdb;create=true");
		statement = connection.createStatement();
		daoInstance = new ReflectionJdbcDaoImplementation<>(statement, JavaBean.class, getJavaToSqlTypes());
	}

	@AfterClass
	public static void tearDownClass() throws SQLException {
		statement.close();
		connection.close();
	}

	@Before
	public void setUp() throws SQLException, IllegalAccessException, InstantiationException {
		daoInstance.createTable();
	}

	@After
	public void tearDown() throws SQLException, IllegalAccessException, InstantiationException {
		daoInstance.dropTable();
	}

	@Test
	public void testInsertJavaBean() throws SQLException, IllegalAccessException, InstantiationException {
		System.out.println("Test insert with JavaBean");
		JavaBean javaBean = new JavaBean(firstId, secondId, someField, currentDate);
		daoInstance.insert(javaBean);
		assertEquals(daoInstance.selectAll().size(), 1);
		System.out.println("Success");
	}

	@Test
	public void testDeleteByKeyJavaBean() throws SQLException, IllegalAccessException, InstantiationException {
		System.out.println("Test deleteByKey with JavaBean");
		JavaBean javaBean = new JavaBean(firstId, secondId, someField, currentDate);
		daoInstance.insert(javaBean);
		assertEquals(daoInstance.selectAll().size(), 1);
		JavaBean keyBean = new JavaBean(firstId, secondId, null, null);
		daoInstance.deleteByKey(keyBean);
		assertEquals(daoInstance.selectAll().size(), 0);
		System.out.println("Success");
	}

	@Test
	public void testUpdateJavaBean() throws SQLException, IllegalAccessException, InstantiationException {
		System.out.println("Test updateByKey with JavaBean");
		JavaBean javaBean = new JavaBean(firstId, secondId, someField, currentDate);
		daoInstance.insert(javaBean);
		JavaBean anotherJavaBean = new JavaBean(firstId, secondId, "someOtherStringValue", currentDate);
		daoInstance.update(anotherJavaBean);
		JavaBean keyBean = new JavaBean(firstId, secondId, null, null);
		JavaBean selectedBean = daoInstance.selectByKey(keyBean);
		assertNotSame(selectedBean, javaBean);
		assertEquals(selectedBean, anotherJavaBean);
		System.out.println("Success");
	}

	@Test
	public void testSelectByKeyJavaBean() throws SQLException, IllegalAccessException, InstantiationException {
		System.out.println("Test selectByKey with JavaBean");
		JavaBean javaBean = new JavaBean(firstId, secondId, someField, currentDate);
		daoInstance.insert(javaBean);
		JavaBean keyBean = new JavaBean(firstId, secondId, null, null);
		JavaBean selectedBean = daoInstance.selectByKey(keyBean);
		assertEquals(javaBean, selectedBean);
		System.out.println("Success");
	}

	@Test
	public void testSelectAllJavaBean() throws SQLException, IllegalAccessException, InstantiationException {
		System.out.println("Test selectAll with JavaBean");
		for (int i = 0; i < count; i++) {
			JavaBean javaBean = new JavaBean(i * 100, i, someField, currentDate);
			daoInstance.insert(javaBean);
		}
		assertEquals(daoInstance.selectAll().size(), count);
		for (int i = 0; i < count; i++) {
			JavaBean javaBean = new JavaBean(i * 100, i, someField, currentDate);
			daoInstance.deleteByKey(javaBean);
		}
		assertEquals(daoInstance.selectAll().size(), 0);
		System.out.println("Success");
	}

	private static Map<String, String> getJavaToSqlTypes() {
		Map<String, String> javaToSqlTypesMap = new HashMap<>();
		javaToSqlTypesMap.put("int", "INT");
		javaToSqlTypesMap.put("long", "BIGINT");
		javaToSqlTypesMap.put("double", "DOUBLE");
		javaToSqlTypesMap.put("float", "FLOAT");
		javaToSqlTypesMap.put("String", "VARCHAR(255)");
		javaToSqlTypesMap.put("Date", "DATE");
		return javaToSqlTypesMap;
	}
}
