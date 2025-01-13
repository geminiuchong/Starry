package com.example.group3_starry.ui.signout

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.group3_starry.R
import com.example.group3_starry.StartScreenActivity
import com.google.firebase.auth.FirebaseAuth

class SignOutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_out, container, false)

        val buttonSignOut = view.findViewById<Button>(R.id.buttonSignOut)

        // Set up Sign Out button
        buttonSignOut.setOnClickListener {
            showSignOutConfirmationDialog()
        }

        return view
    }

    private fun showSignOutConfirmationDialog() {
        // Show confirmation dialog
        AlertDialog.Builder(requireContext())
            .setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Yes") { _, _ ->
                // Perform sign out
                FirebaseAuth.getInstance().signOut()

                // Redirect to StartScreenActivity
                val intent = Intent(requireContext(), StartScreenActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("No", null)
            .show()
    }
}
