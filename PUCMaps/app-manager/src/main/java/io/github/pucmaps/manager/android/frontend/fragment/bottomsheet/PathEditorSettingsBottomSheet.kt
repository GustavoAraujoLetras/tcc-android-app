package io.github.pucmaps.manager.android.frontend.fragment.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.pucmaps.android.maps.builder.helper.PathEditorSettings
import io.github.pucmaps.android.maps.builder.helper.PathEditorShape
import io.github.pucmaps.manager.android.R


class PathEditorSettingsBottomSheet(
    private val currentSetting: PathEditorSettings
) : BottomSheetDialogFragment() {

    private lateinit var saveButton: AppCompatButton
    private lateinit var circlesRadioButton: AppCompatRadioButton
    private lateinit var linesRadioButton: AppCompatRadioButton
    private lateinit var noneRadioButton: AppCompatRadioButton
    private lateinit var newCircleSwitch: SwitchCompat
    private lateinit var connectCirclesSwitch: SwitchCompat

    var onSettingsChanged: (PathEditorSettings) -> Unit = {}
    var onSaveMap: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutRes = R.layout.bottom_sheet_editing_path_settings
        val view = inflater.inflate(layoutRes, container, false)

        saveButton = view.findViewById(R.id.save_button)
        circlesRadioButton = view.findViewById(R.id.circles_radio_view)
        linesRadioButton = view.findViewById(R.id.lines_radio_view)
        noneRadioButton = view.findViewById(R.id.none_radio_view)
        newCircleSwitch = view.findViewById(R.id.create_circles_switch_view)
        connectCirclesSwitch = view.findViewById(R.id.connect_circles_switch_view)

        with(currentSetting) {
            circlesRadioButton.isChecked = this.shape == PathEditorShape.Circle
            linesRadioButton.isChecked = this.shape == PathEditorShape.Line
            noneRadioButton.isChecked = this.shape == null
            newCircleSwitch.isChecked = this.newCirclesEnabled
            connectCirclesSwitch.isChecked = this.connectCirclesEnabled
        }

        circlesRadioButton.setOnCheckedChangeListener { _, _ -> updateSettings() }
        circlesRadioButton.isHapticFeedbackEnabled = true

        linesRadioButton.setOnCheckedChangeListener { _, _ -> updateSettings() }
        linesRadioButton.isHapticFeedbackEnabled = true

        newCircleSwitch.setOnCheckedChangeListener { _, _ -> updateSettings() }
        newCircleSwitch.isHapticFeedbackEnabled = true

        connectCirclesSwitch.setOnCheckedChangeListener { _, _ -> updateSettings() }
        connectCirclesSwitch.isHapticFeedbackEnabled = true

        saveButton.setOnClickListener { save() }
        saveButton.isEnabled = false

        return view
    }

    override fun getTheme(): Int {
        return R.style.Widget_PucMaps_BottomSheetDialog
    }

    private fun assembleSettings(): PathEditorSettings {
        return PathEditorSettings(
            shape = when {
                circlesRadioButton.isChecked -> PathEditorShape.Circle
                linesRadioButton.isChecked -> PathEditorShape.Line
                noneRadioButton.isChecked -> null
                else -> throw Exception("Unable to choose shape")
            },
            newCirclesEnabled = newCircleSwitch.isChecked,
            connectCirclesEnabled = connectCirclesSwitch.isChecked
        )
    }

    private fun updateSettings() {
        onSettingsChanged(assembleSettings())
    }

    private fun save() {
        onSaveMap()
        dismiss()
    }

}