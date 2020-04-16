package com.elevintech.motorbro.AddParts

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.widget.Toast
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Model.BikeParts
import com.elevintech.motorbro.TypeOf.TypeOfPartsActivity
import com.elevintech.motorbro.Utils.Utils
import kotlinx.android.synthetic.main.activity_add_parts.*
import java.text.DecimalFormat
import java.util.*
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elevintech.motorbro.Achievements.AchievementManager
import com.elevintech.motorbro.Model.Achievement
import com.elevintech.motorbro.Model.BikeInfo
import com.elevintech.motorbro.TypeOf.TypeOfBrandActivity
import com.elevintech.motorbro.Utils.Constants
import com.elevintech.myapplication.R
import com.github.florent37.runtimepermission.RuntimePermission
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.row_bike.view.*
import java.io.File


class AddPartsActivity : AppCompatActivity() {

    var selectedBike = BikeInfo()
    var birthDayInMilliseconds = 0.toLong()

    private lateinit var mDateSetListener: DatePickerDialog.OnDateSetListener
    private var isForEditParts = false
    private var editBikeId = ""
    private var editBikeImageUrl = ""

    lateinit var bottomSheetDialog: BottomSheetDialog

    var imageUri: Uri? = null
    var OPEN_CAMERA = 10
    var OPEN_GALLERY = 11

    companion object {
        const val SELECT_PART_TYPE = 1
        const val SELECT_PART_BRAND = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.elevintech.myapplication.R.layout.activity_add_parts)

        // uri exposure fix
        var builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        bottomSheetDialog = BottomSheetDialog(this)

        // get the intent if from parts fragment edit
        isForEditParts = intent.getBooleanExtra("isForEditParts", false)

        if (isForEditParts) {
            // assign the values and assign parts details
            val bikeParts: BikeParts? = intent.getParcelableExtra("bikeParts")

            if (bikeParts != null) {
                //assign the values also change the saving
                dateText.setText(bikeParts.date)
                odometerText.setText(bikeParts.odometer.toString())
                typeOfPartsText.setText(bikeParts.typeOfParts)
                brandText.setText(bikeParts.brand)
                priceText.setText(bikeParts.price.toString())
                noteText.setText(bikeParts.note)
                if (bikeParts.bikeId != ""){
                    MotoroBroDatabase().getBikeById(bikeParts.bikeId){
                        selectedBike = it
                        bikeText.setText(it.brand + " " + it.model + " (${it.nickname})")
                    }
                }
                if (bikeParts.imageUrl != ""){
                    editBikeImageUrl = bikeParts.imageUrl
                    Glide.with(this).load(bikeParts.imageUrl).into(mainProfilePhoto)
                    mainProfilePhoto.visibility = View.VISIBLE
                    emptyImageIcon.visibility = View.GONE
                }
                editBikeId = bikeParts.id
                deleteLayout.visibility = View.VISIBLE

                deleteLayout.setOnClickListener {
                    createAlertDialog(bikeParts)

                }
            }
        } else {
            deleteLayout.visibility = View.GONE
        }

        backButton.setOnClickListener {
            finish()
        }

        checkMarkButton.setOnClickListener {
            saveBikePartsData()
        }

        dateText.setOnClickListener {
            setDatePickerAction()
            openDatePicker()
        }

        typeOfPartsText.setOnClickListener {
            val intent = Intent(applicationContext, TypeOfPartsActivity::class.java)
            intent.putExtra("fromAddExtra", true)

            val parts = typeOfPartsText.text.toString()
            if (parts.isNotEmpty()){
                intent.putExtra("previousParts", parts)
            }

            startActivityForResult(intent, SELECT_PART_TYPE)
        }

        brandText.setOnClickListener {
            val intent = Intent(applicationContext, TypeOfBrandActivity::class.java)
            intent.putExtra("fromAddExtra", true)

            val brands = brandText.text.toString()
            if (brands.isNotEmpty()){
                intent.putExtra("previousBrands", brands)
            }
            startActivityForResult(intent, SELECT_PART_BRAND)
        }

        noteText.setOnClickListener {
            noteText.getParent().requestDisallowInterceptTouchEvent(true);
        }

        bikeText.setOnClickListener {
            openBikePicker()
        }

        imgMainProfile.setOnClickListener {
            askUploadSource()
        }

    }

    private fun openBikePicker(){

        val progressDialog = Utils().easyProgressDialog(this, "Gathering your bikes...")
        progressDialog.show()

        MotoroBroDatabase().getUserBikes { bikes ->

            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_select_bike, null)
            val bikeAdapter = GroupAdapter<ViewHolder>()

            for (bike in bikes.filter { !it.deleted }){
                bikeAdapter.add(BikeItem(bike))
            }

            view.findViewById<RecyclerView>(R.id.bikeRecyclerView).isNestedScrollingEnabled = false
            view.findViewById<RecyclerView>(R.id.bikeRecyclerView).layoutManager = LinearLayoutManager(this)
            view.findViewById<RecyclerView>(R.id.bikeRecyclerView).adapter = bikeAdapter

            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.show()

            progressDialog.dismiss()

        }

    }

    private fun onBikeSelect(bike: BikeInfo){
        selectedBike = bike
        bikeText.setText(bike.brand + " " + bike.model + " (${bike.nickname})")
        bottomSheetDialog.dismiss()
    }

    private fun deleteParts(bikeParts: BikeParts) {
        val showDialog = Utils().showProgressDialog(this, "Deleting bike part")
        MotoroBroDatabase().deleteBikeParts(bikeParts) {
            if (Constants.RESULT_STRING.SUCCESS == it) {
                // Add on activity result here
                showDialog.dismiss()
                finish()
            } else {
                //Toast.makeText(this, "Unable to delete this part. Please check your internet connection", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onTouch(view: View, event: MotionEvent): Boolean {

            view.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(false)
            }

        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            if (data != null){
                if (requestCode == SELECT_PART_TYPE){
                    var partType = data!!.getStringExtra("selectedPart").toString()
                    typeOfPartsText.setText(partType)
                }

                if (requestCode == SELECT_PART_BRAND){
                    var brandType = data!!.getStringExtra("selectedBrand").toString()
                    brandText.setText(brandType)
                }
            }
        }

        if (requestCode == OPEN_GALLERY){
            if (data!= null){
                imageUri = data!!.data
                mainProfilePhoto.setImageURI(imageUri)
                mainProfilePhoto.visibility = View.VISIBLE
                emptyImageIcon.visibility = View.GONE
            }
        }

        if (requestCode == OPEN_CAMERA){
            if (imageUri!= null){
                mainProfilePhoto.setImageURI(imageUri)
                mainProfilePhoto.visibility = View.VISIBLE
                emptyImageIcon.visibility = View.GONE
            }
        }

    }

    private fun createAlertDialog(bikeParts: BikeParts){
        // instantiate dialog
        val builder = AlertDialog.Builder(this)
        val optionDialog = AlertDialog.Builder(this).create()
        // instantiate the view for the dialog
        val viewInflated = layoutInflater.inflate(R.layout.dialog_delete_item, null)
        // inflate the view in the dialog
        builder.setView(viewInflated)
        // set the dialog title
        builder.setTitle("Delete item?")
        builder.setCancelable(true) // can be set to false, to make the dialog undismissable
        builder.setPositiveButton("Yes",
            DialogInterface.OnClickListener { dialog, which ->
                println("works")
                // dismiss dialog after
                dialog.dismiss()
                deleteParts(bikeParts)
            })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
            })

        builder.show()
    }


    private fun openDatePicker() {

        // INSTANTIATE CALENDAR
        var cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, 0)

        var year = cal.get(Calendar.YEAR)
        var month = cal.get(Calendar.MONTH)
        var day = cal.get(Calendar.DAY_OF_MONTH)

        // INSTANTIATE DATE PICKER DIALOG
        var dialog = DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day)

        // SET DATE PICKER DIALOG STYLE
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // SHOW THE DATE PICKER DIALOG
        dialog.show()

    }

    private fun setDatePickerAction(){

        // SET ACTION AFTER SELECTING THE DATE
        mDateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            // FORMAT  DAY and MONTH to always show two digits (add zero if single digit)
            var mFormat = DecimalFormat("00");
            var monthString = mFormat.format(month + 1)
            var dayString = mFormat.format(day)

            birthDayInMilliseconds = Utils().convertDateToMilliseconds(year,month,day,12, 0, 0)

            // PUT THE SELECTED DATE ON THE DATE PLACEHOLDER
            dateText.setText("${year}-${monthString}-${dayString}")

            // TODO: How to get current AGE?
            // If todays month is greater than current month
            // if todays day is greater than or equals to day
        }
    }

    private fun askUploadSource(){

        val options = arrayOf("Open Gallery", "Capture Photo")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Please pick your image source")
        builder.setItems(options){ _, which ->

            if(which == 0){

                // ASK PERMISSION TO OPEN GALLERY
                RuntimePermission.askPermission(this)
                    .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .onAccepted{

                        // OPEN GALLERY
                        openGallery()

                    }
                    .ask()

            }else if(which == 1){

                // ASK PERMISSION TO OPEN CAMERA
                RuntimePermission.askPermission(this)
                    .request(Manifest.permission.CAMERA)
                    .onAccepted{

                        // OPEN CAMERA
                        openCamera()
                    }
                    .ask()

            }
        }
        builder.show()
    }

    fun openCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        var filename = UUID.randomUUID().toString() + ".jpg"
        var file = File(this.externalCacheDir, filename)
        imageUri = Uri.fromFile(file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, OPEN_CAMERA)
    }

    fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, OPEN_GALLERY)
    }

    fun saveBikePartsData() {

        if (validateFields()){

            val showDialog = Utils().showProgressDialog(this, "Saving bike part")
            val bikeParts = BikeParts()
            //bikeParts.date = dateText.text.toString()
            //bikeParts.dateLong = Utils().convertDateToTimestamp(dateText.text.toString(), "yyyy-MM-dd")

            // Based on Raff Lim's changes change this to the current timestamp instead.
            bikeParts.dateLong = Utils().getCurrentTimestamp()
            //bikeParts.odometer = odometerText.text.toString().toDouble()
            bikeParts.typeOfParts = typeOfPartsText.text.toString()
            bikeParts.brand = brandText.text.toString()
            bikeParts.price = priceText.text.toString().toDouble()
            //bikeParts.note = noteText.text.toString()
            bikeParts.userId = FirebaseAuth.getInstance().uid!!
            bikeParts.bikeId = selectedBike.bikeId
            bikeParts.imageUrl = editBikeImageUrl

            bikeParts.id = editBikeId
            val database = MotoroBroDatabase()

            if (isForEditParts) {
                database.editBikeParts(bikeParts) {
                    if (it == "Success") {
                        database.saveHistory("bike-parts", it!!, bikeParts.typeOfParts){

                            // ADDITIONAL STEP, UPLOAD IMAGE IF IT EXIST
                            if(imageUri != null){
                                database.uploadImageToFirebaseStorage(imageUri!!){ imageUrl ->
                                    database.editBikePartsImageUrl(bikeParts.id, imageUrl){
                                        showDialog.dismiss()
                                        Toast.makeText(this, "Successfully edited bike part", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                }
                            } else {
                                showDialog.dismiss()
                                Toast.makeText(this, "Successfully edited bike part", Toast.LENGTH_SHORT).show()
                                finish()
                            }

                        }
                    } else {
                        Toast.makeText(this, "Error editing bike parts. Please check your internet connection.", Toast.LENGTH_SHORT).show()
                    }
                    //Toast.makeText(this, "Error editing bike parts. Please check your internet connection.", Toast.LENGTH_SHORT).show()
                }
            } else {
                database.saveBikeParts(bikeParts) { bikePartId ->
                    if (bikePartId != null) {
                        database.saveHistory("bike-parts", bikePartId!!, bikeParts.typeOfParts){
                            if(imageUri != null){
                                database.uploadImageToFirebaseStorage(imageUri!!){imageUrl ->
                                    database.editBikePartsImageUrl(bikePartId, imageUrl){
                                        AchievementManager().setAchievementAsAchieved( Achievement.Names.FIRST_PART_SERVICE )
                                        AchievementManager().incrementAchievementProgress( Achievement.Names.CREATED_PART_SERVICE, 1)
                                        showDialog.dismiss()
                                        Toast.makeText(this, "Successfully saved bike part", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                }
                            } else {
                                AchievementManager().setAchievementAsAchieved( Achievement.Names.FIRST_PART_SERVICE )
                                AchievementManager().incrementAchievementProgress( Achievement.Names.CREATED_PART_SERVICE, 1)
                                showDialog.dismiss()
                                Toast.makeText(this, "Successfully saved bike part", Toast.LENGTH_SHORT).show()
                                finish()
                            }

                        }
                    } else {
                        Toast.makeText(this, "Error saving bike parts. Please check your internet connection.", Toast.LENGTH_SHORT).show()
                    }
                    //Toast.makeText(this, "Error saving bike parts. Please check your internet connection.", Toast.LENGTH_SHORT).show()
                }
            }

        } else {
            Toast.makeText(this, "Please fill up all the fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateFields(): Boolean {

        return (!(
                    //dateText.text.toString() == "" ||
//                    odometerText.text.toString()== ""||
                    typeOfPartsText.text.toString() == ""||
                    brandText.text.toString() == ""||
                    priceText.text.toString() == ""
                ))


    }

    inner class BikeItem(val bike: BikeInfo): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.bikeName.text = bike.nickname
            viewHolder.itemView.plateNumberText.text = bike.plateNumber
            viewHolder.itemView.deleteButton.visibility = View.INVISIBLE

            viewHolder.itemView.setOnClickListener { onBikeSelect(bike) }

            if (bike.imageUrl != "")
                Glide.with(this@AddPartsActivity).load(bike.imageUrl).into(viewHolder.itemView.bikeImageView)

            if (bike.primary)
                viewHolder.itemView.isPrimary.visibility = View.VISIBLE

        }

        override fun getLayout(): Int {
            return R.layout.row_bike

        }
    }

}
