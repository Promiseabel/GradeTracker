package ca.unb.mobiledev.group18project.ui.settings

import android.content.Context
import ca.unb.mobiledev.group18project.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ca.unb.mobiledev.group18project.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*
        val textView: TextView = binding.textSettings
        settingsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/


        val alertSpinner: Spinner = binding.alerts
// Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.alert,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            alertSpinner.adapter = adapter
        }


        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val savedPosition = sharedPref?.getInt("SelectedAlertPosition", 0) ?: 0
        alertSpinner.setSelection(savedPosition)

        alertSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedText = parent.getItemAtPosition(position).toString()
                selectedAlert = selectedText

                selectedAlertValue = alertChoices[selectedText] ?: 0

                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
                with (sharedPref.edit()) {
                    putInt("SelectedAlertPosition", position)
                    apply()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


        val scaleSpinner: Spinner = binding.gpaScale
// Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gpaScale,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            scaleSpinner.adapter = adapter
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object selectedItems{

        var selectedAlert = "1 minute before"
        //var selectedScheme = "4.0"

        var selectedAlertValue: Int = 0

        val alertChoices = mapOf(
            //mapped to hours
            "1 minute before" to 0,
            "1 hour before" to 1,
            "12 hours before" to 12,
            "1 day before" to 24,
            "2 days before" to 48,
            "1 week before" to 168  // 7 days * 24 hours
        )
    }

}