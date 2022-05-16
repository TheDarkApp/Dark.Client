package lab.maxb.dark.presentation.repository.room.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import kotlinx.coroutines.flow.Flow
import lab.maxb.dark.presentation.repository.room.model.RecognitionTaskDTO
import lab.maxb.dark.presentation.repository.room.model.RecognitionTaskImageCrossref
import lab.maxb.dark.presentation.repository.room.model.RecognitionTaskName
import lab.maxb.dark.presentation.repository.room.relations.RecognitionTaskWithNamesAndImages
import lab.maxb.dark.presentation.repository.room.relations.RecognitionTaskWithOwnerAndImage

@Dao
interface RecognitionTaskDAO {
    @Insert(onConflict = REPLACE)
    suspend fun addRecognitionTask(task: RecognitionTaskDTO)

    @Insert(onConflict = REPLACE)
    suspend fun addRecognitionTaskNames(names: List<RecognitionTaskName>)

    @Insert(onConflict = IGNORE)
    suspend fun addRecognitionTaskImages(images: List<RecognitionTaskImageCrossref>)

    @Transaction
    suspend fun addRecognitionTask(task: RecognitionTaskDTO,
                           names: List<RecognitionTaskName>,
                           images: List<RecognitionTaskImageCrossref>) {
        deleteRecognitionTask(task)
        addRecognitionTask(task)
        addRecognitionTaskNames(names)
        addRecognitionTaskImages(images)
    }

    @Update
    suspend fun updateRecognitionTask(task: RecognitionTaskDTO)

    @Delete
    suspend fun deleteRecognitionTask(task: RecognitionTaskDTO)

    @Transaction
    suspend fun deleteRecognitionTasks(exclude: List<String>) {
        getAllRecognitionTasksIds().forEach {
            if (it !in exclude)
                deleteRecognitionTask(it)
        }
    }

    @Query("DELETE FROM recognition_task WHERE id=:id")
    suspend fun deleteRecognitionTask(id: String)

    @Query("SELECT id FROM recognition_task")
    suspend fun getAllRecognitionTasksIds(): List<String>

    @Transaction
    @Query("SELECT * FROM recognition_task ORDER BY reviewed")
    fun getAllRecognitionTasks(): Flow<List<RecognitionTaskWithOwnerAndImage>?>

    @Transaction
    @Query("SELECT * FROM recognition_task WHERE id = :id")
    fun getRecognitionTask(id: String): Flow<RecognitionTaskWithNamesAndImages?>

    @Transaction
    @Query("DELETE FROM recognition_task")
    suspend fun clear()
}