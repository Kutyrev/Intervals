package com.github.kutyrev.intervals.database

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class DatabaseWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams)  {

    //val KEY_RESULT = "result"

    override fun doWork(): Result {

        val operation = inputData.getInt("OPERATION", 0)

        //Create test data
        if(operation == 42) {
   /*         val newTestEntity: ListEntity = ListEntity("Main")
            AppDelegate.repository.insertAllLists(newTestEntity)
            return Result.success()*/
        }

       /* if(operation == 43) {
            val listEntities:List<ListEntity> = AppDelegate.repository.getAllLists()


            val output: Data = ListEntity.serialize(listEntities)


            return Result.success(output)
        }*/

        return Result.failure()

    }
}