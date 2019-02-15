package com.example.geotag

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.io.File
import java.io.FileWriter

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var locations: MutableList<Tag>
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    fun toggleSpinner(visable:Boolean? = null){
        val loadingCircle = findViewById<RelativeLayout>(R.id.loadingPanel)
        if(visable != null && visable) {
            loadingCircle.visibility = View.VISIBLE
            return
        } else if(visable != null && !visable ){
            loadingCircle.visibility = View.GONE
            return
        }

        if(loadingCircle.visibility == View.GONE){
            loadingCircle.visibility = View.VISIBLE
        } else {
            loadingCircle.visibility = View.GONE
        }
    }

    fun addLocation(building:String,floor:Int,room:String, desc :String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {

            val locationRequest = LocationRequest.create()?.apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult?) {
                    super.onLocationResult(p0)
                    p0 ?: return
                    locations.add(Tag(p0.locations.last(), building, floor, room, desc))
                    viewAdapter.notifyItemInserted(locations.size)
                    fusedLocationClient.removeLocationUpdates(this)
                }
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

        } else {
            //TODO ask for permision
            Toast.makeText(this, "No permission for location", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //get revant views
        val addLocationButton = findViewById<Button>(R.id.addLocationButton)
        val buildingText = findViewById<EditText>(R.id.buildingInput)
        val floorNumber = findViewById<EditText>(R.id.floorNumber)
        val roomNumber = findViewById<EditText>(R.id.roomNumber)
        val descriptionEditText = findViewById<EditText>(R.id.descriptionInput)
        var saveButton = findViewById<Button>(R.id.saveButton)


        //setup list
        locations = mutableListOf()
        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapter(locations)

        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
            setHasFixedSize(false)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        //set up location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Set Save button behavior
        saveButton.setOnClickListener{

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) and
                ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {

                val builder  = AlertDialog.Builder(this)
                val fileNameInput = EditText(this)
                fileNameInput.inputType = InputType.TYPE_CLASS_TEXT

                builder
                    .setTitle("Save Locations")
                    .setView(fileNameInput)
                    .setPositiveButton("Save",DialogInterface.OnClickListener { dialog, _ ->
                        val fileName = fileNameInput.text.toString() + ".csv"
                        var contentsBuilder = StringBuilder()
                        for (tag in locations) {
                            contentsBuilder.append(tag.toString() + "\n")
                        }
                        val fileContents = contentsBuilder.toString()
                        val path = this.getExternalFilesDir("tags")

                        val fileLocation = File(path, fileName)

                        if (fileLocation.createNewFile()) {
                            val fw = FileWriter(fileLocation)
                            fw.write(fileContents)
                            fw.flush()
                            fw.close()
                            Toast.makeText(this,"File Writen",Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "can't make file", Toast.LENGTH_SHORT).show()
                        }
                    })
                    .setNegativeButton("Cancel",{_,_ ->
                        //nothing
                    })
                val dialog = builder.create()
                dialog.show()

            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    , 0)
            }


        }

        addLocationButton.addLocationButton.setOnClickListener {
            if (!(roomNumber.text.toString() == "" && descriptionEditText.text.toString() == "")) {
                if (floorNumber.text.toString() == "") {
                    floorNumber.setText("0")
                }
                addLocationButton.isClickable = false
                toggleSpinner(true)
                addLocation(
                    buildingText.text.toString(),
                    floorNumber.text.toString().toInt(),
                    roomNumber.text.toString(),
                    descriptionEditText.text.toString()
                )
                toggleSpinner(false)
                addLocationButton.isClickable = true
            } else {
                Toast.makeText(this, "Give a room number or a descsription", Toast.LENGTH_SHORT).show()
            }

        }


    }
}
