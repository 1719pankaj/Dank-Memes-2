package com.example.dankmemes2.Fragments

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.dankmemes2.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.fragment_onboarding.*
import kotlinx.android.synthetic.main.fragment_onboarding.view.*

class OnboardingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_onboarding, container, false)

        view.nextWpermission.setOnClickListener { dexter() }
        view.nextWOpermission.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingFragment_to_configFragment)
        }

        return view
    }

    fun dexter() {
        Dexter.withContext(context).withPermissions(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport){
                    if (p0.areAllPermissionsGranted()) {
                        findNavController().navigate(R.id.action_onboardingFragment_to_configFragment)
                    } else {
                        onboardingMessageTV.text = "If you want to continue without permission click continue without functionality Or grant permission"
                        nextWOpermission.visibility = View.VISIBLE
                    }
                }

                override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, p1: PermissionToken?) {
                    p1?.continuePermissionRequest()
                }
            }).check()
    }

}