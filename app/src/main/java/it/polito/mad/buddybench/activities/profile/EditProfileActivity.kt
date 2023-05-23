package it.polito.mad.buddybench.activities.profile

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BlendMode
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf

import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.findcourt.CourtsDiffUtils
import it.polito.mad.buddybench.classes.BitmapUtils
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.classes.ValidationUtils
import it.polito.mad.buddybench.classes.ValidationUtils.Companion.changeColor
import it.polito.mad.buddybench.classes.ValidationUtils.Companion.validateEmail
import it.polito.mad.buddybench.classes.ValidationUtils.Companion.validateString
import it.polito.mad.buddybench.dialogs.EditSportsDialog
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.viewmodels.ImageViewModel
import it.polito.mad.buddybench.viewmodels.UserViewModel
import org.json.JSONObject

import java.time.LocalDate
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity(), EditSportsDialog.NoticeDialogListener {

    enum class ActivityState{
        DatePickerOpened,
        EditSportsOpened,
        ContextMenuOpened
    }
    // ** Data
    private lateinit var profile: Profile
    private var datePicker: DatePickerDialog? = null
    private var editSportDialog: EditSportsDialog? = null
    private var contextMenu: ContextMenu? = null
    private var birthdateListener: MutableLiveData<LocalDate> = MutableLiveData()
    private var dialogOpened: ActivityState? = null
    private var tempCalendarDate: LocalDate? = null
    private var popupOpened: PopupMenu? = null
    private var tempSelectedSport: ArrayList<Sports>? = null
    private var oldEmail: String? = null
    private lateinit var sportsRecyclerView: RecyclerView
    private val imageViewModel by viewModels<ImageViewModel> ()
    // ** Profile Image
    private val launcherCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onCameraImageReturned(it)
        }
    private val launcherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onGalleryImageReturned(it)
        }

    private lateinit var imageEdit: ImageView
    private var imageUri: Uri? = null

    // ** Sports
    private lateinit var addSportButton: ImageButton
    private lateinit var sportContainer: LinearLayout

    private var newImage: Boolean = false

    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        val backToProfile = findViewById<ShapeableImageView>(R.id.come_back)
        backToProfile.setOnClickListener {
            finish()
        }

        val saveButton = findViewById<ShapeableImageView>(R.id.save_button)
        saveButton.setOnClickListener {
            saveProfile()
        }

        // ** Profile Data
        val stringedProfile = savedInstanceState?.getString("profile") ?: intent.getStringExtra("profile")!!
        profile = Profile.fromJSON(JSONObject(stringedProfile))
        this.closeContextMenu()
        // ** Profile TextFields Edit
        val nameEdit = findViewById<EditText>(R.id.nameEdit)
        val nameEditBox = findViewById<TextInputLayout>(R.id.first_name_box)
        nameEdit.backgroundTintMode = PorterDuff.Mode.CLEAR
        nameEdit.doOnTextChanged { text, _, _, _ ->
            changeColor(nameEdit, true, resources)
            val validated = validateString(text.toString())
            //if(!validated) nameEditBox.boxStrokeColor = Color.RED else
            nameEditBox.boxStrokeColor = Color.BLUE
            profile.name = text.toString()
        }
        nameEdit.setText(profile.name)

        val surnameEditBox = findViewById<TextInputLayout>(R.id.surname_box)
        val surnameEdit = findViewById<EditText>(R.id.surnameEdit)
        surnameEdit.doOnTextChanged { text, _, _, _ ->
            changeColor(surnameEdit, true, resources)
            val validated = validateString(text.toString())
            //if(!validated) surnameEditBox.boxStrokeColor = Color.RED else
            surnameEditBox.boxStrokeColor = Color.BLUE
            profile.surname = text.toString()
        }
        surnameEdit.backgroundTintMode = PorterDuff.Mode.CLEAR
        surnameEdit.setText(profile.surname)

        val nicknameEditBox = findViewById<TextInputLayout>(R.id.nickname_box)
        val nicknameEdit = findViewById<EditText>(R.id.nicknameEdit)
        nicknameEdit.doOnTextChanged { text, _, _, _ ->
            changeColor(nicknameEdit, true, resources)
            val validated = validateString(text.toString())
            //if(!validated) nicknameEditBox.boxStrokeColor = Color.RED else
            nicknameEditBox.boxStrokeColor = Color.BLUE
            profile.nickname = text.toString().trim()
        }
        nicknameEdit.backgroundTintMode = PorterDuff.Mode.CLEAR
        nicknameEdit.setText(profile.nickname)



        val localityBox = findViewById<TextInputLayout>(R.id.locality_box)
        val localityEdit = findViewById<EditText>(R.id.localityEdit)
        localityEdit.backgroundTintMode = PorterDuff.Mode.CLEAR
        localityEdit.setText(profile.location)
        localityEdit.doOnTextChanged { text, _, _, _ ->
            changeColor(localityEdit, true, resources)
            val validated = validateString(text.toString())
            //if(!validated) localityBox.boxStrokeColor = Color.RED else
            localityBox.boxStrokeColor = Color.BLUE
            profile.location = text.toString()
        }

        birthdateListener.value = profile.birthdate
        val birthdayButtonEdit = findViewById<EditText>(R.id.birthdayEditButton)
        birthdateListener.observe(this) {
            birthdayButtonEdit.setText(it.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
            profile.birthdate = it
        }
        birthdayButtonEdit.backgroundTintMode = PorterDuff.Mode.CLEAR

        // ** Profile Image
        imageEdit = findViewById(R.id.profile_image)
        try{
            imageViewModel.getUserImage(profile.email,{
                imageEdit.setImageResource(R.drawable.person)

            }){
                Glide.with(this)
                    .load(it)
                    .into(imageEdit)
            }
        } catch (_: Exception){
            imageEdit.setImageResource(R.drawable.person)
        }
        //resizeImageView()
        val cardViewImage = findViewById<ImageView>(R.id.camera_button)
        cardViewImage.setOnClickListener {
            val popup = PopupMenu(this, it)
            popup.menuInflater.inflate(R.menu.menu_camera_edit, popup.menu)
            popup.setOnMenuItemClickListener {
                onContextItemSelected(it)
            }
            popup.show()
        }


        sportsRecyclerView = findViewById<RecyclerView>(R.id.sports_container)
        sportsRecyclerView.layoutManager = LinearLayoutManager(this).let {
            it.orientation = RecyclerView.HORIZONTAL
            it
        }


        userViewModel.setSports(profile.sports)
        val sportRemoveCallback:(Sport) -> Unit =   { sport: Sport ->
            userViewModel.removeSport(sport)

        }
        val sportSkillCallback:(Sport, View) -> Unit =   { sport: Sport, sportSkill: View ->
            val popup = PopupMenu(this, sportSkill)
            popup.menuInflater.inflate(R.menu.skill_level_edit, popup.menu)
            popup.setOnMenuItemClickListener {
                sport.skill =  Skills.fromJSON(it.title.toString().uppercase())!!
                userViewModel.updateSport(sport)
                popup.dismiss()
                true
            }
            popupOpened = popup
            popup.show()
        }

        val achievementAddCallback: (Sport, String) -> Unit = {
            sport, achievement ->
            userViewModel.addAchievement(sport, achievement)
        }

        val achievementRemoveCallback: (Sport, String) -> Unit = {
                sport, achievement ->
            userViewModel.removeAchievement(sport, achievement)
        }

        sportsRecyclerView.adapter = SportsAdapter(userViewModel.sports, true, sportRemoveCallback, sportSkillCallback, achievementRemoveCallback, achievementAddCallback)

        userViewModel.sports.observe(this){

            val diff = SportsDiffUtils(userViewModel.oldSports, it)
            val diffResult = DiffUtil.calculateDiff(diff)
            diffResult.dispatchUpdatesTo(sportsRecyclerView.adapter!!)
            profile.sports = it

            if(it.isEmpty()){
                findViewById<TextView>(R.id.empty_sports).visibility= View.VISIBLE
            } else{
                findViewById<TextView>(R.id.empty_sports).visibility= View.GONE

            }
        }

        //sportContainer = findViewById(R.id.sportsContainerEdit)
        //sportContainer.removeAllViews()

        // ** Populate sport cards
        /*profile.populateSportCards(this, sportContainer,
            edit = true,
            onSkillSelected = { sportSkillLevel, sport ->

                val popup = PopupMenu(this, sportSkillLevel)
                popup.menuInflater.inflate(R.menu.skill_level_edit, popup.menu)
                popup.setOnMenuItemClickListener {
                    profile.updateSkillLevel(sport, Skills.fromJSON(it.title.toString().uppercase())!!)
                    profile.refreshSportsCard(this, sportContainer, sport)
                    true
                }
                popupOpened = popup
                popup.show()
            })*/



        //Creating the instance of PopupMenu





        // ** Add Sports Button
        addSportButton = findViewById(R.id.add_sport_button)
        addSportButton.setOnClickListener { openSportSelectionDialog() }
        checkCameraPermission()


        restoreDialog(savedInstanceState)

    }

    private  fun resizeImageView(){
        val iv = findViewById<ImageView>(R.id.profile_image)
        val ll = findViewById<LinearLayout>(R.id.imageContainer)
        ll.post {
            val width = ll.width
            val height = ll.height

            if(width == height) return@post
            val diameter = width.coerceAtMost(height)

            iv.layoutParams = FrameLayout.LayoutParams(diameter, diameter)
            iv.requestLayout()
        }
    }

    private fun openGallery() {
        if (checkCameraPermission()) {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            launcherGallery.launch(galleryIntent)
        }
    }

    private fun openCamera() {
        if (checkCameraPermission()) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "New Picture")
            values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
            imageUri =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            launcherCamera.launch(cameraIntent)
        }
    }

    private fun onCameraImageReturned(response: androidx.activity.result.ActivityResult) {
        //TODO: on rotation the image is not returned. Its possible to retrieve the bitmap of the snapped photo in the bundle, but the methods are deprecated or requiring API 33
        if (response.resultCode != Activity.RESULT_OK || imageUri == null) return
        val bitmap = BitmapUtils.uriToBitmap(contentResolver, imageUri!!)
        imageEdit.setImageBitmap(bitmap)
        profile.imageUri = imageUri
        newImage = true

    }

    private fun onGalleryImageReturned(response: androidx.activity.result.ActivityResult) {
        //TODO: on rotation the image is not returned
        if (response.resultCode != Activity.RESULT_OK || response.data == null) return
        imageUri = response.data?.data
        val bitmap = BitmapUtils.uriToBitmap(contentResolver, imageUri!!)
        imageEdit.setImageBitmap(bitmap)
        profile.imageUri = imageUri
        newImage = true

    }

    private fun checkSdkVersion(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    private fun checkCameraPermission(): Boolean {
        return if(checkSdkVersion()) {
            checkCameraPermissionFromSdk32()
        } else {
            checkCameraPermissionUntilSdk31()
        }
    }

    private fun checkCameraPermissionUntilSdk31(): Boolean {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            val permission =
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permission, 121)
            return false
        }
        return true
    }

    private fun checkCameraPermissionFromSdk32(): Boolean {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_DENIED
        ) {
            val permission =
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES)
            requestPermissions(permission, 121)
            return false
        }
        return true
    }

    private fun formValidation(): Boolean {
        val nameEdit = findViewById<EditText>(R.id.nameEdit)
        val nameBox = findViewById<TextInputLayout>(R.id.first_name_box)

        val nicknameEdit = findViewById<EditText>(R.id.nicknameEdit)
        val nicknameBox = findViewById<TextInputLayout>(R.id.nickname_box)

        val surnameEdit = findViewById<EditText>(R.id.surnameEdit)
        val surnameBox = findViewById<TextInputLayout>(R.id.surname_box)

//        val emailEdit = findViewById<EditText>(R.id.Email)
        val localityEdit = findViewById<EditText>(R.id.localityEdit)
        val localityBox = findViewById<TextInputLayout>(R.id.locality_box)

        var flag = true
        val flagEmail = true
        val drawableError = R.drawable.error
        if (!changeColor(nameEdit, validateString(nameEdit.text.toString()), resources)) {
            nameBox.boxStrokeColor = Color.RED
            nameEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0)
            flag = false
        }

        if(!changeColor(surnameEdit, validateString(surnameEdit.text.toString()), resources)){
            surnameBox.boxStrokeColor = Color.RED
            surnameEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0)
            flag = false
        }
        if (!changeColor(nicknameEdit, validateString(nicknameEdit.text.toString()), resources)) {
            nicknameBox.boxStrokeColor = Color.RED
            nicknameEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0)
            flag = false
        }

        if (!changeColor(localityEdit, validateString(localityEdit.text.toString()), resources)) {
            localityBox.boxStrokeColor = Color.RED
            localityEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0)
            flag = false
        }
        if (birthdateListener.value == null) {
            flag = false
        }
        if (!flag){
            val toast = Toast.makeText(
                this,
                resources.getText(R.string.errorOnEdit),
                Toast.LENGTH_SHORT
            )
            toast.show()
            return false
        } else if (!flagEmail){
            val toast = Toast.makeText(
                this,
                resources.getText(R.string.errorOnEmail),
                Toast.LENGTH_SHORT
            )
            toast.show()
            return false
        }
        return true
    }

    private fun prepareEdit() {
        val nameEdit = findViewById<EditText>(R.id.nameEdit)
        val surnameEdit = findViewById<EditText>(R.id.surnameEdit)
        val nicknameEdit = findViewById<EditText>(R.id.nicknameEdit)
        val localityEdit = findViewById<EditText>(R.id.localityEdit)
//        val emailEdit = findViewById<EditText>(R.id.Email)
        profile.name = nameEdit.text.toString()
        profile.surname = surnameEdit.text.toString()
        profile.nickname = nicknameEdit.text.toString()
        profile.location = localityEdit.text.toString()
        profile.birthdate = birthdateListener.value!!
//        profile.email = emailEdit.text.toString()
        profile.imageUri = imageUri ?: profile.imageUri
        profile.sports = userViewModel.getAllSports().toMutableList()
        val newProfileJSON = profile.toJSON().toString()
        intent.putExtra("newProfile", newProfileJSON)
        intent.putExtra("newImage", newImage)
    }


    private fun finishActivity() {
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    /**
     * Open sport selection dialog
     */
    private fun openSportSelectionDialog(selectedItems: ArrayList<Sports>? = ArrayList()) {
        editSportDialog = EditSportsDialog(profile, selectedItems!!)
        editSportDialog!!.show(supportFragmentManager, "edit_sports")
    }

    fun showDatePickerDialog(v: View?) {
        val myDateListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val date = LocalDate.of(year, month + 1, day)
            if(!date.isAfter(LocalDate.now())){
                birthdateListener.value = date
            } else {
                val toast = Toast.makeText(
                    this,
                    resources.getText(R.string.errorBirthdate),
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }

        }
        datePicker = DatePickerDialog(
            this,
            myDateListener,
            tempCalendarDate?.year?:profile.birthdate.year,
            tempCalendarDate?.monthValue?:profile.birthdate.monthValue,
            tempCalendarDate?.dayOfMonth?:profile.birthdate.dayOfMonth
        )
        tempCalendarDate = null
        datePicker!!.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_profile_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.edit -> {
                if (!formValidation()) {
                    return false
                }
                prepareEdit()
                finishActivity()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, selectedItems: ArrayList<Sports>) {
        // ** Add new selected sports to user profile (and save to shared preferences)
        var newSports = mutableListOf<Sport>()
        val alreadySelectedSports = userViewModel.sports.value!!.filter { it.skill != Skills.NULL }
        for (selectedSport in selectedItems) {
            try {
                checkNotNull(selectedSport)
                val newSport = profile.sports.find { it.name == selectedSport } ?:Sport(selectedSport, Skills.NEWBIE, 0,0,
                    mutableListOf<String>()
                )
                newSport.skill = Skills.NEWBIE
                newSports.add(newSport)
            } catch (e: java.lang.IllegalStateException) {
                continue
            }
        }
        newSports.forEach {    userViewModel.addSport(it) }


        dialog.dismiss()
        editSportDialog = null
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        dialog.dismiss()
        editSportDialog = null
        return
    }


    override fun onSaveInstanceState(outState: Bundle) {
        profile.imageUri = imageUri?:profile.imageUri
        outState.putString("profile", profile.toJSON().toString())

        if (dialogOpened != null) {
            outState.putString("dialog", dialogOpened?.name)
            if(tempCalendarDate != null && dialogOpened == ActivityState.DatePickerOpened){
                outState.putString("tempCalendarDate", tempCalendarDate!!.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
            }
            if(dialogOpened == ActivityState.EditSportsOpened && tempSelectedSport != null ){
                outState.putStringArrayList("tempSelectedSport", tempSelectedSport!!.map { sport -> sport.name } as java.util.ArrayList<String>)
            }
        }
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {

        dialogOpened = if (datePicker?.isShowing == true){
            val year = datePicker?.datePicker?.year
            val month = datePicker?.datePicker?.month
            val day = datePicker?.datePicker?.dayOfMonth
            tempCalendarDate = LocalDate.of(year!!, month!!, day!!)
            ActivityState.DatePickerOpened
        }

        else if (editSportDialog?.showsDialog == true){
            tempSelectedSport = editSportDialog?.selectedItems
            ActivityState.EditSportsOpened
        }
        else
            null



        popupOpened?.dismiss()
        datePicker?.dismiss()
        editSportDialog?.dismiss()
        super.onPause()

    }


    override fun onResume() {
        restoreDialog(null)
        super.onResume()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = menuInflater
        contextMenu = menu
        inflater.inflate(R.menu.menu_camera_edit, menu)
    }


    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.takePhoto -> {
                openCamera()
                true
            }
            R.id.goToGallery -> {
                openGallery()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun restoreDialog(savedInstanceState: Bundle?){
        val dialogOpenPreviously = if (savedInstanceState?.getString("dialog") != null)
            ActivityState.valueOf(savedInstanceState.getString("dialog")!!) else dialogOpened

        /*if (dialogOpenPreviously == ActivityState.ContextMenuOpened) {
            cardViewImage.showContextMenu()
        }*/
        if (dialogOpenPreviously == ActivityState.DatePickerOpened) {
            tempCalendarDate = tempCalendarDate ?: LocalDate.parse(savedInstanceState?.getString("tempCalendarDate"), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            showDatePickerDialog(null)

        }
        if (dialogOpenPreviously == ActivityState.EditSportsOpened) {

            tempSelectedSport = tempSelectedSport?:savedInstanceState?.getStringArrayList("tempSelectedSport")?.map { s -> Sports.fromJSON(s) } as ArrayList<Sports>
            if (tempSelectedSport != null)
                editSportDialog?.selectedItems = tempSelectedSport!!
            openSportSelectionDialog(tempSelectedSport)
            tempSelectedSport = null
        }

    }

    private fun saveProfile() {
        val intent = Intent()
        val b = bundleOf("newProfile" to profile.toJSON().toString())
        intent.putExtras(b)
        setResult(-1, intent)
        finish()
    }
}