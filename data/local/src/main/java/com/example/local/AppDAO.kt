package com.example.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the task table.
 */
@Dao
interface AppDAO {

    /**
     * Observes list of tasks.
     *
     * @return all tasks.
     */
    @Query("SELECT * FROM app_data")
    fun observeAll(): Flow<List<AppData>>

    /**
     * Observes a single task.
     *
     * @param taskId the task id.
     * @return the task with taskId.
     */
    @Query("SELECT * FROM app_data WHERE appName = :appName")
    fun observeByName(appName: String): Flow<AppData>

    /**
     * Select all tasks from the tasks table.
     *
     * @return all tasks.
     */
    @Query("SELECT * FROM app_data")
    suspend fun getAll(): List<AppData>

    /**
     * Select a task by id.
     *
     * @param taskId the task id.
     * @return the task with taskId.
     */
    @Query("SELECT * FROM app_data WHERE appName = :appName")
    suspend fun getByName(appName: String): AppData?

    /**
     * Insert or update a task in the database. If a task already exists, replace it.
     *
     * @param task the task to be inserted or updated.
     */
    @Upsert
    suspend fun upsert(app: AppData)

    /**
     * Insert or update tasks in the database. If a task already exists, replace it.
     *
     * @param tasks the tasks to be inserted or updated.
     */
    @Upsert
    suspend fun upsertAll(tasks: List<AppData>)


    /**
     * Delete a task by id.
     *
     * @return the number of tasks deleted. This should always be 1.
     */
    @Query("DELETE FROM app_data WHERE appName = :appName")
    suspend fun deleteByName(appName: String): Int

    /**
     * Delete all tasks.
     */
    @Query("DELETE FROM app_data")
    suspend fun deleteAll()

    /**
     * Delete all completed tasks from the table.
     *
     * @return the number of tasks deleted.
     */
    @Query("DELETE FROM app_data WHERE isCompleted = 1")
    suspend fun deleteCompleted(): Int
}