/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.makarov_s_a.daoproject;

import com.makarov_s_a.daoproject.annotations.Column;
import com.makarov_s_a.daoproject.annotations.Id;
import com.makarov_s_a.daoproject.annotations.Table;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author makarov_s_a
 */
public class ReflectionJdbcDaoImplementation<T> implements ReflectionJdbcDao<T> {

	private final Class entityBeanClass;
	private final Statement statement;
	private final List<Field> fieldList;
	private final List<Field> keyFieldList;
	private final String tableName;
	private final Map<String, String> javaToSqlTypes;
	private final Map<Field, String> fieldsNamesMap;

	public ReflectionJdbcDaoImplementation(Statement statement, Class entityBeanClass, Map<String, String> javaToSqlTypes) throws SQLException {
		this.entityBeanClass = entityBeanClass;
		this.statement = statement;
		this.fieldList = getFields();
		this.keyFieldList = getKeyFields();
		this.fieldsNamesMap = getFieldsNamesMap();
		this.tableName = getTableName();
		this.javaToSqlTypes = javaToSqlTypes;
	}

	/**
	 * Вставка объекта в соответствующую таблицу БД
	 *
	 * @param object объект, поля которого вставляются в запись в таблице
	 */
	public void insert(T object) throws SQLException, IllegalAccessException {
		String sqlString = "INSERT INTO "
				+ object.getClass().getSimpleName()
				+ " (";
		List<Object> valuesList = getFieldsValues(object);
		for (Field field : fieldList) {
			field.setAccessible(true);
			sqlString += fieldsNamesMap.get(field) + (", ");
		}
		sqlString = sqlString.substring(0, sqlString.length() - 2) + ")";
		sqlString += " VALUES (";
		for (Object value : valuesList) {
			if (value != null) {
				if (String.class.equals(value.getClass()) || Date.class.equals(value.getClass())) {
					sqlString += "'" + value.toString()
							.substring(0, Math.min(255, value.toString().length())) + "'" + ", ";
				} else {
					System.out.println(value.getClass().toString() + "   " + char.class.toString());
					sqlString += value.toString() + ", ";
				}
			}
		}
		sqlString = sqlString.substring(0, sqlString.length() - 2) + ")";
		System.out.println(sqlString);
		statement.executeUpdate(sqlString);
	}

	/**
	 * Обновление соответствующей записи в таблице БД.
	 *
	 * @param object объект, поля которого обновляются в записи в таблице; идентификация записи происходит по ключевым
	 * полям объекта.
	 */
	public void update(T object) throws SQLException, InstantiationException, IllegalAccessException {
		String sqlString = "UPDATE " + tableName + " SET ";
		for (Field field : fieldList) {
			if (String.class.equals(field.getType()) || Date.class.equals(field.getType())) {
				sqlString += fieldsNamesMap.get(field) + " = '" + field.get(object).toString()
						.substring(0, Math.min(255, field.get(object).toString().length())) + "', ";
			} else {
				sqlString += fieldsNamesMap.get(field) + " = " + field.get(object).toString() + ", ";
			}
		}
		sqlString = sqlString.substring(0, sqlString.length() - 2);
		sqlString += " WHERE ";
		for (Field field : keyFieldList) {
			sqlString += fieldsNamesMap.get(field) + " = " + field.get(object).toString() + " AND ";
		}
		sqlString = sqlString.substring(0, sqlString.length() - 5);
		System.out.println(sqlString);
		statement.executeUpdate(sqlString);
	}

	/**
	 * Удаление объекта из таблицы БД. Идентификация записи происходит по ключевым полям объекта. Остальные поля объекта
	 * в методе не используются и могут быть не заполнены.
	 *
	 * @param key удаляемый объект с заполненными ключевыми полями
	 */
	public void deleteByKey(T key) throws SQLException, IllegalAccessException {
		String sqlString = "DELETE FROM " + tableName + " WHERE ";
		for (Field field : keyFieldList) {
			sqlString += fieldsNamesMap.get(field) + " = " + field.get(key).toString() + " AND ";
		}
		sqlString = sqlString.substring(0, sqlString.length() - 5);
		System.out.println(sqlString);
		statement.executeUpdate(sqlString);
	}

	/**
	 * Выборка из таблицы БД объекта. Идентификация записи происходит по ключевым полям объекта. Остальные поля объекта в
	 * методе не используются и могут быть не заполнены.
	 *
	 * @param key выбираемый объект с заполненными ключевыми полями
	 * @return выбранный из БД объект со всеми заполненными полями
	 */
	public T selectByKey(T key) throws SQLException, InstantiationException, IllegalAccessException {
		Object selectedObject = null;
		String sqlString = "SELECT * FROM " + tableName + " WHERE ";
		for (Field field : keyFieldList) {
			sqlString += fieldsNamesMap.get(field) + " = " + field.get(key).toString() + " AND ";
		}
		sqlString = sqlString.substring(0, sqlString.length() - 5);
		System.out.println(sqlString);
		ResultSet rs = statement.executeQuery(sqlString);
		while (rs.next()) {
			selectedObject = getObjectFromResultSet(rs);
		}
		return (T) selectedObject;
	}

	/**
	 * Выборка всех объектов из соответствующей таблицы БД;
	 *
	 * @return список выбранных объектов
	 */
	public List<T> selectAll() throws SQLException, InstantiationException, IllegalAccessException {
		List<T> resultList = new ArrayList<>();

		String sqlString = "SELECT * FROM " + tableName;

		try (ResultSet rs = statement.executeQuery(sqlString)) {
			while (rs.next()) {
				resultList.add((T) getObjectFromResultSet(rs));
			}
		}
		return resultList;
	}

	/**
	 * Создание таблицы в БД;
	 *
	 */
	public void createTable() throws SQLException, InstantiationException, IllegalAccessException {
		String sqlString = "CREATE TABLE " + tableName + " (";
		for (Field field : fieldList) {
			sqlString += fieldsNamesMap.get(field) + " " + javaToSqlTypes.get(field.getType().getSimpleName()) + ", ";
		}
		sqlString += "PRIMARY KEY (";
		for (Field field : keyFieldList) {
			sqlString += fieldsNamesMap.get(field) + ", ";
		}
		sqlString = sqlString.substring(0, sqlString.length() - 2) + "))";
		System.out.println(sqlString);
		statement.executeUpdate(sqlString);
	}

	/**
	 * Удаление таблицы в БД;
	 *
	 */
	public void dropTable() throws SQLException, InstantiationException, IllegalAccessException {
		String sqlString = "DROP TABLE " + tableName;
		System.out.println(sqlString);
		statement.executeUpdate(sqlString);
	}

	/**
	 * Получение всех полей типа;
	 *
	 * @return список полей типа
	 */
	private List<Field> getFields() {
		List<Field> fieldList = new ArrayList<>();
		for (Field field : entityBeanClass.getDeclaredFields()) {
			fieldList.add(field);
		}
		return fieldList;
	}

	/**
	 * Получение всех ключевых полей типа;
	 *
	 * @return список ключевых полей типа
	 */
	private List<Field> getKeyFields() {
		List<Field> keyFieldList = new ArrayList<>();
		for (Field field : entityBeanClass.getDeclaredFields()) {
			field.setAccessible(true);
			Annotation[] annotations = field.getDeclaredAnnotations();
			if (Arrays.asList(annotations).contains(field.getAnnotation(Id.class))) {
				keyFieldList.add(field);
			}
		}
		return keyFieldList;
	}

	/**
	 * Получение всех имен полей типа. Если у поля есть аннотация Column с параметром name - используется значение
	 * параметра name. В ином случае имя типа получается заменой lower camel case на подчеркивания.
	 *
	 * @return словарь имен полей типа
	 */
	private Map<Field, String> getFieldsNamesMap() {
		Map<Field, String> fieldsNamesMap = new HashMap<>();
		for (Field field : entityBeanClass.getDeclaredFields()) {
			field.setAccessible(true);
			Column columnAnnotation = (Column) field.getAnnotation(Column.class);
			if (null != columnAnnotation && !"NOT_SPECIFIED".equals(columnAnnotation.name())) {
				fieldsNamesMap.put(field, columnAnnotation.name());
			} else {
				fieldsNamesMap.put(field, transfromFieldName(field.getName()));
			}
		}
		return fieldsNamesMap;
	}

	/**
	 * Получение всех значений полей типа;
	 *
	 * @return список значений полей типа
	 */
	private List<Object> getFieldsValues(Object object) throws IllegalAccessException {
		List<Object> valuesList = new ArrayList<>();
		for (Field field : fieldList) {
			field.setAccessible(true);
			valuesList.add(field.get(object));
		}
		return valuesList;
	}

	/**
	 * Получение имени таблицы;
	 *
	 * @return строка с названием таблицы
	 */
	private String getTableName() {
		Table tableAnnotation = (Table) entityBeanClass.getAnnotation(Table.class);
		return tableAnnotation.name();
	}

	/**
	 * Получение объекта из ResultSet;
	 *
	 * @return полученный объект
	 */
	private Object getObjectFromResultSet(ResultSet rs) throws SQLException, InstantiationException, IllegalAccessException {
		Object selectedObject = entityBeanClass.newInstance();
		for (Field field : fieldList) {
			field.setAccessible(true);
			Object fieldValue = rs.getObject(fieldsNamesMap.get(field));
			field.set(selectedObject, fieldValue);
		}
		return selectedObject;
	}

	/**
	 * Получение строки заменой lower camel case на подчеркивания;
	 *
	 * @return полученная строка
	 */
	private String transfromFieldName(String name) {
		return name.replaceAll("([A-Z])", "_$1").toLowerCase();
	}
}
