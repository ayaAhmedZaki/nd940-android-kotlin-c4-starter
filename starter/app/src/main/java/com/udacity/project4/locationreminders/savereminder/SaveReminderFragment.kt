package com.udacity.project4.locationreminders.savereminder

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*
import java.util.concurrent.TimeUnit

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding
    private lateinit var geofencingClient: GeofencingClient
    // A PendingIntent for the Broadcast Receiver that handles geofence transitions.
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel
        geofencingClient = LocationServices.getGeofencingClient(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            //            Navigate to another fragment to get the user location
            _viewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }

        binding.saveReminder.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription.value
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude.value
            val longitude = _viewModel.longitude.value


//            TODO: use the user entered reminder details to:
//             1) add a geofencing request
//             2) save the reminder to the local db
            if (latitude != null && longitude != null){
                addGeofence(latitude, longitude)
            }

            _viewModel.validateAndSaveReminder(ReminderDataItem(title , description , location , latitude , longitude))

            //add backToReminderList to observe when data is valid and save data we can navigate back to reminderList
            _viewModel.backToReminderList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if(it){
                    view.findNavController().navigate(R.id.action_saveReminderFragment_to_reminderListFragment)
                    _viewModel.backToReminderList()
                }
            })


        }
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(latitude: Double?, longitude: Double?) {
        val uniqueID = UUID.randomUUID().toString()

        val geofence = Geofence.Builder()
            .setRequestId(uniqueID)
            .setCircularRegion(latitude!!,
                longitude!!,
                GEOFENCE_RADIUS_IN_METERS
            )
            .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            // Add the geofences to be monitored by geofencing service.
            .addGeofence(geofence)
            .build()

        // Add the new geofence request with the new geofence
        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
            addOnSuccessListener {
                Toast.makeText(requireContext(), R.string.geofences_added,
                    Toast.LENGTH_SHORT)
                    .show()
                Log.e("Add Geofence", geofence.requestId)
                //viewModel.geofenceActivated()
            }
            addOnFailureListener {
                Toast.makeText(requireContext(), R.string.geofences_not_added,
                    Toast.LENGTH_SHORT).show()
                if ((it.message != null)) {
                    Log.w(TAG, it.message!!)
                }
            }
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }

    companion object{
        private const val TAG = "SaveReminderFragment"
        const val GEOFENCE_RADIUS_IN_METERS = 100f
        val GEOFENCE_EXPIRATION_IN_MILLISECONDS: Long = TimeUnit.HOURS.toMillis(1)
        internal const val ACTION_GEOFENCE_EVENT =
            "HuntMainActivity.treasureHunt.action.ACTION_GEOFENCE_EVENT"
    }
}
