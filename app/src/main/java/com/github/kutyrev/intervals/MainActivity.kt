package com.github.kutyrev.intervals

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.github.kutyrev.intervals.database.ListEntity


class MainActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<MainListFragment>(R.id.fragment_container_view)
            }
        }

        /*   viewModel.eventsLifeData.observe(this, Observer {
               result -> println("Test")
           })*/


        /*val myAddData: Data = workDataOf("OPERATION" to 42)

        val uploadWorkRequest: WorkRequest =
                OneTimeWorkRequestBuilder<DatabaseWorker>()
                        .setInputData(myAddData)
                        .build()

        val createButton : Button = findViewById(R.id.add_in_list);

        createButton.setOnClickListener{

            WorkManager
                    .getInstance(applicationContext)
                    .enqueue(uploadWorkRequest)
        }*/

        //val myReadData: Data = workDataOf("OPERATION" to 43)

        //val readButton : Button = findViewById(R.id.read_from_list);

        //readButton.setOnClickListener{

        /*  val readWorkRequest: WorkRequest =
                  OneTimeWorkRequestBuilder<DatabaseWorker>()
                          .setInputData(myReadData)
                          .build()

          WorkManager
                  .getInstance(applicationContext)
                  .enqueue(readWorkRequest)

          WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(readWorkRequest.id)
                  .observe(this, Observer { info ->
                      if (info != null && info.state.isFinished) {
                          //info.outputData.get
                      }
                  })

      //}*/


        /*WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(uploadWorkRequest.id)
                .observe(this, Observer { info ->
                    if (info != null && info.state.isFinished) {

                    }
                })*/

        //val listEntities:List<ListEntity> = AppDelegate.repository.getAllLists()




    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_settings -> {
                supportFragmentManager.commit {
                    val fragment = SettingsFragment()
                    setReorderingAllowed(true)
                    addToBackStack("")
                    replace(R.id.fragment_container_view, fragment)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    fun goToDetailFragment(list: ListEntity){
        supportFragmentManager.commit {
            val fragment = DetailFragment(list)
            setReorderingAllowed(true)
            addToBackStack("")
            replace(R.id.fragment_container_view, fragment)
        }
    }

}