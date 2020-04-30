package io.github.pucmaps.manager.android.frontend.fragment.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.github.pucmaps.android.builder.model.GPSPoint
import io.github.pucmaps.android.builder.model.Place
import io.github.pucmaps.manager.android.R

class VertexDetailsBottomSheet(private val vertex: GPSPoint) : BottomSheetDialogFragment() {
    private lateinit var latitudeTextField: TextInputLayout
    private lateinit var longitudeTextField: TextInputLayout
    private lateinit var placeNameTextField: TextInputLayout
    private lateinit var isPlaceSwitch: SwitchMaterial
    private lateinit var saveButton: MaterialButton
    private lateinit var deleteButton: MaterialButton

    var onDeleteVertex: () -> Unit = { }
    var onSaveVertex: (GPSPoint) -> Unit = { }
    var onSavePlace: (GPSPoint, Place) -> Unit = { _, _ -> }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutRes = R.layout.bottom_sheet_vertex_details
        return inflater.inflate(layoutRes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        latitudeTextField = view.findViewById(R.id.latitude_text)
        longitudeTextField = view.findViewById(R.id.longitude_text)
        placeNameTextField = view.findViewById(R.id.place_name_text)
        isPlaceSwitch = view.findViewById(R.id.is_place_switch)
        deleteButton = view.findViewById(R.id.delete_button)
        saveButton = view.findViewById(R.id.save_button)

        with(vertex) {
            latitudeTextField.text = "${latitude.degrees}"
            longitudeTextField.text = "${longitude.degrees}"
        }

        isPlaceSwitch.setOnCheckedChangeListener { _, isChecked ->
            placeNameTextField.visibility = if (isChecked) {
                View.VISIBLE
            } else {
                (placeNameTextField.editText as? TextInputEditText)?.text?.clear()
                View.GONE
            }
        }

        saveButton.setOnClickListener { save() }
        deleteButton.setOnClickListener { onDeleteVertex() }
    }

    override fun getTheme(): Int {
        return R.style.Widget_PucMaps_BottomSheetDialog
    }

    private fun save() {
        val vertex = this.vertex.copy()

        if (isPlaceSwitch.isChecked) {
            val placeName = placeNameTextField.text!!
            val place = Place(placeName, vertex)

            onSavePlace(vertex, place)
            return
        } else {
            onSaveVertex(vertex)
        }
    }

    private var TextInputLayout.text: String?
        get() {
            return (this.editText as? TextInputEditText)?.text?.toString()
        }
        set(value) {
            (this.editText as? TextInputEditText)?.setText(value)
        }

}