package ca.unb.mobiledev.group18project.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel(){
    private val _text = MutableLiveData<String>().apply {
        value = "This is the Settings Section"
    }
    val text: LiveData<String> = _text
}