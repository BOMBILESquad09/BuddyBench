package it.polito.mad.buddybench.activities.myreservations

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.internal.managers.FragmentComponentManager
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.court.CourtActivity
import it.polito.mad.buddybench.activities.court.ReviewsActivity
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.enums.Visibilities
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.utils.Utils
import it.polito.mad.buddybench.viewmodels.ReservationViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class ReservationViewHolder(val viewModel: ReservationViewModel, v: View, private val launcher: ActivityResultLauncher<Intent>) :
    RecyclerView.ViewHolder(v) {

    private val courtName: TextView = v.findViewById(R.id.card_invitation_court)
    private val courtAddress: TextView = v.findViewById(R.id.card_invitation_address)
    private val courtPhone: TextView = v.findViewById(R.id.card_reservation_phone_number)
    private val slot: TextView = v.findViewById(R.id.textView5)
    private val iconSport: ImageView = v.findViewById(R.id.imageView)
    private val iconReservationVisibility: ImageView = v.findViewById(R.id.visibility_icon)
    var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")
    private var manageBtn: TextView = v.findViewById(R.id.manage_btn)
    private var telephoneIcon: ImageView = v.findViewById(R.id.telephone)
    private var positionIcon: ImageView = v.findViewById(R.id.position)
    lateinit var reservation: ReservationDTO
    val view: View = v

    fun bind(reservationDTO: ReservationDTO) {
        reservation = reservationDTO
        // ** Card color
        val card = view.findViewById<CardView>(R.id.card_inner)
        card.setCardBackgroundColor(
            Sports.getSportColor(
                Sports.valueOf(reservation.court.sport),
                view.context
            )
        )

        courtName.text = reservation.court.name
        courtAddress.text = "${reservation.court.location}, ${reservation.court.address}"
        courtPhone.text = reservation.court.phoneNumber
        val iconDrawable = ContextCompat.getDrawable(
            view.context,
            Sports.sportToIconDrawableAlternative(
                Sports.fromJSON(
                    reservation.court.sport
                )!!
            )
        )


        telephoneIcon.setOnClickListener {
            val number = Uri.parse("tel:" + reservation.court.phoneNumber);
            val dial = Intent(Intent.ACTION_DIAL, number)
            launcher.launch(dial)
        }

        positionIcon.setOnClickListener {
            val uri = "geo:0,0?q=" + reservation.court.address
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            launcher.launch(intent)
        }


        val wrappedDrawable = DrawableCompat.wrap(iconDrawable!!)
        iconSport.setImageDrawable(wrappedDrawable)
        slot.text = "${reservation.startTime.format(formatter)} - ${reservation.endTime.format(formatter)}"

        manageBtn.setTextColor(
            Sports.getSportColor(
                Sports.valueOf(reservation.court.sport),
                view.context
            ))
        manageBtn.text = "Manage"
        if (reservation.userOrganizer.email != Firebase.auth.currentUser!!.email!!) {

            manageBtn.text = "Invited"
            manageBtn.setOnClickListener {
                SendInvitationsBottomSheet(reservation, true).show(
                    (FragmentComponentManager.findActivity(
                        view.context
                    ) as AppCompatActivity).supportFragmentManager, "InvitationBottomSheet")

            }
        } else if (LocalDate.now() > reservation.date || (LocalDate.now() == reservation.date && LocalTime.now() > reservation.endTime)){
            manageBtn.text = "Review"
            manageBtn.setOnClickListener{
                launchReview(reservation)
            }
        } else if(LocalDate.now() == reservation.date && LocalTime.now() < reservation.endTime &&  LocalTime.now() > reservation.startTime ){
            manageBtn.text = "Started"
        }
        else {
            manageBtn.setOnClickListener {
                if (LocalDate.now() > reservation.date || (LocalDate.now() == reservation.date && LocalTime.now() > reservation.startTime)) {
                    launchReview(reservation)
                } else {
                    val mbs = ManageBottomSheet(
                        {
                            launchEditReservation(reservation)
                        },
                        {
                            SendInvitationsBottomSheet(reservation).show(
                                (FragmentComponentManager.findActivity(
                                    view.context
                                ) as AppCompatActivity).supportFragmentManager, "InvitationBottomSheet"
                            )
                        },
                        {
                            val dialogCard = LayoutInflater.from(view.context).inflate(R.layout.dialog_visibility, null)
                            val confirm  = dialogCard.findViewById<View>(R.id.confirm)
                            confirm.isFocusable = false
                            confirm.isClickable = false
                            val radioGroup = dialogCard.findViewById<RadioGroup>(R.id.visibility_group)

                            val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
                            builder.setView(dialogCard)
                            val dialog: AlertDialog = builder.create()
                            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                            dialog.show()
                            dialogCard.findViewById<View>(R.id.cancel).setOnClickListener {
                                dialog.dismiss()
                            }

                            val checkedButton: RadioButton =  when(reservation.visibility){
                                Visibilities.PRIVATE -> radioGroup.findViewById(R.id.private_visibility)
                                Visibilities.ON_REQUEST -> radioGroup.findViewById(R.id.on_request_visibility)
                                Visibilities.PUBLIC -> radioGroup.findViewById(R.id.public_visibility)
                            }
                            checkedButton.isChecked = true


                            radioGroup.setOnCheckedChangeListener { radioGroup, i ->
                                confirm.isFocusable = true
                                confirm.isClickable = true

                            }

                            confirm.setOnClickListener{

                                val newVisibility = when(radioGroup.checkedRadioButtonId){
                                    R.id.private_visibility -> Visibilities.PRIVATE
                                    R.id.on_request_visibility -> Visibilities.ON_REQUEST
                                    else -> Visibilities.PRIVATE
                                }

                                if(newVisibility != Visibilities.PRIVATE && reservation.requests.isNotEmpty() ){
                                    Utils.openGeneralProblemDialog("Error", "Please, before changing visibility accept or refuse the pending join requests.", view.context)
                                    dialog.dismiss()
                                    return@setOnClickListener
                                }

                                viewModel.setVisibility(reservation, newVisibility, {
                                                                                    dialog.dismiss()
                                }, {
                                    reservation.visibility = it
                                    setVisibilityIcon()
                                    dialog.dismiss()

                                })

                            }
                        },
                        {
                            JoinRequestsBottomSheet(it).show(
                                (FragmentComponentManager.findActivity(
                                    view.context
                                ) as AppCompatActivity).supportFragmentManager, "JointRequestBottomSheet"
                            )
                        },
                        reservation
                    )
                    mbs.show(
                        (FragmentComponentManager.findActivity(view.context) as AppCompatActivity).supportFragmentManager,
                        ""
                    )
                }
            }
        }


        iconReservationVisibility.setOnClickListener {
            visibilityInfoDialog()
        }
        setVisibilityIcon()
    }

    fun setVisibilityIcon(){
        if (reservation.visibility.toString() == "PRIVATE")
            iconReservationVisibility.setImageDrawable(view.context.getDrawable(R.drawable.private_visibility))
        else
            iconReservationVisibility.setImageDrawable(view.context.getDrawable(R.drawable.global_visibility))

    }



    private fun launchReview(reservation: ReservationDTO) {
        val intent = Intent(view.context, ReviewsActivity::class.java)
        intent.putExtra("court_name", reservation.court.name)
        intent.putExtra("court_sport", reservation.court.sport)
        launcher.launch(intent)
    }

    private fun launchEditReservation(reservation: ReservationDTO) {
        val intent = Intent(view.context, CourtActivity::class.java)
        intent.putExtra("edit", true)
        intent.putExtra("courtName", reservation.court.name)
        intent.putExtra("sport", reservation.court.sport)
        intent.putExtra("date", reservation.date.toString())
        intent.putExtra("email", Firebase.auth.currentUser!!.email)
        intent.putExtra("startTime", reservation.startTime.hour)
        intent.putExtra("endTime", reservation.endTime.hour)
        intent.putExtra("equipment", reservation.equipment)
        intent.putExtra("id", reservation.id)
        launcher.launch(intent)

    }

    private fun visibilityInfoDialog(){
        val builder = AlertDialog.Builder(view.context)
        val dialogLayout = LayoutInflater.from(view.context).inflate(R.layout.dialog_visibility_info, null)
        builder.setView(dialogLayout)
        val dialog: AlertDialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val dialogTypeText : TextView = dialogLayout.findViewById(R.id.dialog_visibility_type)
        dialogTypeText.text = reservation.visibility.name.replace("_"," ")

        val dialogIcon : ImageView = dialogLayout.findViewById(R.id.dialog_icon)
        val dialogText: TextView = dialogLayout.findViewById(R.id.dialog_text)


        when(reservation.visibility){
            Visibilities.PRIVATE -> {
                dialogIcon.setImageDrawable(view.context.getDrawable(R.drawable.private_visibility))
                dialogText.text = "Players can only participate by invitation from the organizer"
            }
            Visibilities.ON_REQUEST -> {
                dialogIcon.setImageDrawable(view.context.getDrawable(R.drawable.global_visibility))
                dialogText.text = "Any user can send a request to the organizer to participate the game"

            }
            else -> {}
        }

        dialog.show()
    }




}