package it.polito.mad.buddybench. activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView

import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.squareup.picasso.Picasso
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.BitmapUtils
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.classes.ValidationUtils.Companion.changeColor
import it.polito.mad.buddybench.classes.ValidationUtils.Companion.validateEmail
import it.polito.mad.buddybench.classes.ValidationUtils.Companion.validateString
import it.polito.mad.buddybench.dialogs.EditSportsDialog
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.utils.Utils
import org.json.JSONObject

import java.time.LocalDate
import java.time.format.DateTimeFormatter


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        // ** Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Profile"
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.setNavigationOnClickListener { finish() }
        // ** Profile Data
        val stringedProfile = savedInstanceState?.getString("profile") ?: intent.getStringExtra("profile")!!
        profile = Profile.fromJSON(JSONObject(stringedProfile))
        this.closeContextMenu()
        // ** Profile TextFields Edit
        val nameEdit = findViewById<EditText>(R.id.nameEdit)
        nameEdit.doOnTextChanged { text, _, _, _ ->
            changeColor(nameEdit, true, resources)
            profile.name = text.toString()
        }
        nameEdit.setText(profile.name)

        val surnameEdit = findViewById<EditText>(R.id.surnameEdit)
        surnameEdit.doOnTextChanged { text, _, _, _ ->
            changeColor(surnameEdit, true, resources)
            profile.surname = text.toString()
        }
        surnameEdit.setText(profile.surname)

        val nicknameEdit = findViewById<EditText>(R.id.nicknameEdit)
        nicknameEdit.doOnTextChanged { text, _, _, _ ->
            changeColor(nicknameEdit, true, resources)
            profile.nickname = text.toString()
        }
        nicknameEdit.setText(profile.nickname)

        val emailEdit = findViewById<EditText>(R.id.Email)
        emailEdit.doOnTextChanged { text, _, _, _ ->
            changeColor(emailEdit, true, resources)
            profile.email = text.toString()
        }
        emailEdit.setText(profile.email)

        val localityEdit = findViewById<EditText>(R.id.localityEdit)
        localityEdit.setText(profile.location)
        localityEdit.doOnTextChanged { text, _, _, _ ->
            changeColor(localityEdit, true, resources)
            profile.location = text.toString()
            //localityEdit.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.location,0)
        }


        birthdateListener.value = profile.birthdate
        val birthdayButtonEdit = findViewById<EditText>(R.id.birthdayEditButton)
        birthdateListener.observe(this) {
            birthdayButtonEdit.setText(it.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
            profile.birthdate = it
        }
        // ** Profile Image
        imageEdit = findViewById(R.id.imageEdit)
        Picasso.with(applicationContext).load("${profile.imageUri}")
            .placeholder(R.drawable.person).into(imageEdit)

        val cardViewImage = findViewById<CardView>(R.id.cardView)
        registerForContextMenu(cardViewImage)

        sportContainer = findViewById(R.id.sportsContainerEdit)
        sportContainer.removeAllViews()

        // ** Populate sport cards
        populateSportCardsEdit(this, sportContainer)

        // ** Add Sports Button
        addSportButton = findViewById(R.id.add_sport_button)
        addSportButton.setOnClickListener { openSportSelectionDialog() }
        checkCameraPermission()


        restoreDialog(savedInstanceState)

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
    }

    private fun onGalleryImageReturned(response: androidx.activity.result.ActivityResult) {
        //TODO: on rotation the image is not returned
        if (response.resultCode != Activity.RESULT_OK || response.data == null) return
        imageUri = response.data?.data
        val bitmap = BitmapUtils.uriToBitmap(contentResolver, imageUri!!)
        imageEdit.setImageBitmap(bitmap)
        profile.imageUri = imageUri

    }


    private fun checkCameraPermission(): Boolean {
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

    private fun formValidation(): Boolean {
        val nameEdit = findViewById<EditText>(R.id.nameEdit)
        val nicknameEdit = findViewById<EditText>(R.id.nicknameEdit)
        val surnameEdit = findViewById<EditText>(R.id.surnameEdit)
        val emailEdit = findViewById<EditText>(R.id.Email)
        val localityEdit = findViewById<EditText>(R.id.localityEdit)
        var flag = true
        var flagEmail = true
        val drawableError = R.drawable.error
        if (!changeColor(nameEdit, validateString(nameEdit.text.toString()), resources)) {
            nameEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0)
            flag = false
        }

        if(!changeColor(surnameEdit, validateString(surnameEdit.text.toString()), resources)){
            surnameEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0)
            flag = false
        }
        if (!changeColor(nicknameEdit, validateString(nicknameEdit.text.toString()), resources)) {
            nicknameEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0)
            flag = false
        }
        if (!changeColor(emailEdit, validateEmail(emailEdit.text.toString()), resources)) {
            emailEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0)
            flagEmail = false
        }
        if (!changeColor(localityEdit, validateString(localityEdit.text.toString()), resources)) {
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
        val emailEdit = findViewById<EditText>(R.id.Email)
        profile.name = nameEdit.text.toString()
        profile.surname = surnameEdit.text.toString()
        profile.nickname = nicknameEdit.text.toString()
        profile.location = localityEdit.text.toString()
        profile.birthdate = birthdateListener.value!!
        profile.email = emailEdit.text.toString()
        profile.imageUri = imageUri ?: profile.imageUri
        val newProfileJSON = profile.toJSON().toString()
        intent.putExtra("newProfile", newProfileJSON)
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
            //the user must be 18+ yo??
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
        val newSports = mutableListOf<Sport>()
        val alreadySelectedSports = profile.sports
        for (selectedSport in selectedItems) {
            try {
                checkNotNull(selectedSport)
                val newSport = Sport(selectedSport, Skills.NEWBIE, 0)
                newSports.add(newSport)
            } catch (e: java.lang.IllegalStateException) {
                continue
            }
        }
        profile.sports = newSports.plus(alreadySelectedSports)
        populateSportCardsEdit(this, sportContainer)
        dialog.dismiss()
        editSportDialog = null
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        dialog.dismiss()
        editSportDialog = null
        return
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("profile", profile.toJSON().toString())
        println("On saving state....")
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
            println("edit sport aperto")
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



    private fun populateSportCardsEdit(
        context: AppCompatActivity,
        sportContainer: LinearLayout,
        onDeleteSport: () -> Unit = {},
        onSkillSelected: () -> Unit = {},
    ) {

        sportContainer.removeAllViews()
        println("populating")
        if (profile.sports.isEmpty()) {
            val emptySportsText = TextView(context)
            emptySportsText.text = context.getString(R.string.no_sports)
            sportContainer.addView(emptySportsText)
            return
        }


        for (sport in profile.sports) {
            println("Sport: ${sport.name}")
            val sportCard = LayoutInflater.from(context).inflate(R.layout.card_sport_edit, null, false);

            // TODO: Add listener to delete button
            val deleteButton = sportCard.findViewById<FrameLayout>(R.id.button_close)
            deleteButton.setOnClickListener {
                val newSports = profile.sports.filter { sportInList -> sportInList.name != sport.name}
                profile.sports = newSports
                onDeleteSport()
                this.populateSportCardsEdit(context, sportContainer)
            }

            // ** Sport card dynamic values
            val sportName = sportCard.findViewById<TextView>(R.id.sport_card_name);
            val sportIcon = sportCard.findViewById<ImageView>(R.id.sport_card_icon);
            val sportSkillLevel = sportCard.findViewById<CardView>(R.id.skill_level_card)
            val sportSkillLevelText = sportCard.findViewById<TextView>(R.id.skill_level_card_text)
            val sportGamesPlayed = sportCard.findViewById<TextView>(R.id.games_played_text)

            // ** Sport skill level edit
            sportSkillLevel.setOnClickListener() {
                //Creating the instance of PopupMenu
                val popup = PopupMenu(context, sportSkillLevel)
                popup.menuInflater.inflate(R.menu.skill_level_edit, popup.menu)
                popup.setOnMenuItemClickListener {
                    profile.updateSkillLevel(sport, Skills.fromJSON(it.title.toString().uppercase())!!)
                    onSkillSelected()
                    populateSportCardsEdit(context, sportContainer)
                    true
                }
                popupOpened = popup
                popup.setOnDismissListener { println("jjjjjjjjjj") }
                popup.show()
            }

            sportName.text = Utils.capitalize(sport.name.toString())
            sportIcon.setImageResource(Sports.sportToIconDrawable(sport.name))
            // TODO: Non funziona
            // sportSkillLevel.setBackgroundColor(Skills.skillToColor(sport.skill))
            sportSkillLevelText.text = Utils.formatString(sport.skill.toString())
            sportGamesPlayed.text = String.format(context.resources.getString(R.string.games_played), sport.matchesPlayed)

            // ** Add card to container
            sportContainer.addView(sportCard)

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
}