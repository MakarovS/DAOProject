/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.makarov_s_a.daoproject;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author makarov_s_a
 */
public interface ReflectionJdbcDao<T> {
	/**
	 * Вставка объекта в соответствующую таблицу БД
	 *
	 * @param object объект, поля которого вставляются в запись в таблице
	 */
	public void insert(T object) throws SQLException, IllegalAccessException;

	/**
	 * Обновление соответствующей записи в таблице БД.
	 *
	 * @param object объект, поля которого обновляются в записи в таблице; идентификация записи происходит по ключевым
	 * полям объекта.
	 */
	public void update(T object) throws SQLException, InstantiationException, IllegalAccessException;

	/**
	 * Удаление объекта из таблицы БД. Идентификация записи происходит по ключевым полям объекта. Остальные поля объекта
	 * в методе не используются и могут быть не заполнены.
	 *
	 * @param key удаляемый объект с заполненными ключевыми полями
	 */
	public void deleteByKey(T key) throws SQLException, IllegalAccessException ;

	/**
	 * Выборка из таблицы БД объекта. Идентификация записи происходит по ключевым полям объекта. Остальные поля объекта в
	 * методе не используются и могут быть не заполнены.
	 *
	 * @param key выбираемый объект с заполненными ключевыми полями
	 * @return выбранный из БД объект со всеми заполненными полями
	 */
	public T selectByKey(T key) throws SQLException, InstantiationException, IllegalAccessException;

	/**
	 * Выборка всех объектов из соответствующей таблицы БД;
	 *
	 * @return список выбранных объектов
	 */
	public List<T> selectAll() throws SQLException, InstantiationException, IllegalAccessException;

	/**
	 * Создание таблицы в БД;
	 *
	 */
	public void createTable() throws SQLException, InstantiationException, IllegalAccessException;

	/**
	 * Удаление таблицы в БД;
	 *
	 */
	public void dropTable() throws SQLException, InstantiationException, IllegalAccessException;
}
