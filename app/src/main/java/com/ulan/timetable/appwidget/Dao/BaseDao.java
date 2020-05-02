package com.ulan.timetable.appwidget.Dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * From https://github.com/SubhamTyagi/TimeTable
 */
class BaseDao {

    /**
     * 插入
     *
     * @param db            数据库
     * @param tableName     要操作的表名
     * @param contentValues 表中个字段的键值对（键的名字要和表中字段名一致，区分大小写）
     */
    static void insert(SQLiteDatabase db, String tableName, ContentValues contentValues) {
        db.insert(tableName, null, contentValues);
    }

    /**
     * 插入或替换
     * 约束值不存在，进行插入
     * 约束值存在，新数据替换掉旧数据
     * 约束值：建表时UNIQUE规定的字段
     *
     * @param db            数据库
     * @param tableName     要操作的表名
     * @param contentValues 表中个字段的键值对（键的名字要和表中字段名一致，区分大小写）
     */
    static void insertOrReplace(SQLiteDatabase db, String tableName, ContentValues contentValues) {
        db.insertWithOnConflict(tableName, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * 插入或替换
     * 约束值不存在，进行插入
     * 约束值存在，则忽略
     * 约束值：建表时UNIQUE规定的字段
     *
     * @param db            数据库
     * @param tableName     要操作的表名
     * @param contentValues 表中个字段的键值对（键的名字要和表中字段名一致，区分大小写）
     */
    static void insertOrIgnore(SQLiteDatabase db, String tableName, ContentValues contentValues) {
        db.insertWithOnConflict(tableName, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * 删除
     *
     * @param db          数据库
     * @param tableName   要操作的表名
     * @param whereClause where条件
     * @param whereArgs   where条件语句中的字段值
     */
    static void delete(SQLiteDatabase db, String tableName, String whereClause, String[] whereArgs) {
        db.delete(tableName, whereClause, whereArgs);
    }

    static int update(SQLiteDatabase db, String tableName, ContentValues values, String whereClause, String[] whereArgs) {
        return db.update(tableName, values, whereClause, whereArgs);
    }

    /**
     * 查询
     *
     * @param db            数据库
     * @param tableName     要操作的表名
     * @param selection     条件语句，where部分
     * @param selectionArgs where语句中的字段值
     */
    static Cursor query(SQLiteDatabase db, String tableName, String selection, String[] selectionArgs) {
        return queryComplex(db, tableName, null, selection, selectionArgs, null, null, null, null);
    }

    /**
     * 复杂查询
     *
     * @param db            数据库
     * @param tableName     要操作的表名
     * @param columns       要获取的字段数组
     * @param selection     条件语句，where部分
     * @param selectionArgs where语句中的字段值
     * @param groupBy       与SQL中的group by语句一样
     * @param having        group bu中的having语句
     * @param orderBy       SQL中的排序语句
     * @param limit         限制返回的数据与偏移量
     */
    static Cursor queryComplex(SQLiteDatabase db, String tableName, String[] columns, String selection, String[] selectionArgs, String
            groupBy, String having, String orderBy, String limit) {
        return db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }
}
