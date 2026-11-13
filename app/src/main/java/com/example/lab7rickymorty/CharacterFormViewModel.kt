package com.example.lab7rickymorty

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Valores exactos que usa el API de Rick & Morty
val CHARACTER_STATUS_OPTIONS = listOf("Alive", "Dead", "unknown")
val CHARACTER_GENDER_OPTIONS = listOf("Female", "Male", "Genderless", "unknown")

data class CharacterFormState(
    val name: String = "",
    val status: String = "unknown",
    val species: String = "",
    val gender: String = "unknown",
    val image: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val showNameError: Boolean = false,
    val isSaved: Boolean = false,
    val notFound: Boolean = false
)

class CharacterFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val characterRepository: CharacterRepository
) : ViewModel() {

    // null = crear un personaje nuevo; con valor = editar ese personaje
    private val editingId: Int? = savedStateHandle.toRoute<CharacterForm>().id
    val isEditing: Boolean = editingId != null

    private val _state = MutableStateFlow(CharacterFormState(isLoading = isEditing))
    val state: StateFlow<CharacterFormState> = _state.asStateFlow()

    init {
        if (editingId != null) {
            loadCharacter(editingId)
        }
    }

    // El formulario solo necesita los valores iniciales, por eso aquí basta con first():
    // se lee una vez y luego el usuario es quien cambia los campos
    private fun loadCharacter(id: Int) {
        viewModelScope.launch {
            val item = characterRepository.getCharacterById(id).first()
            if (item == null) {
                _state.value = _state.value.copy(isLoading = false, notFound = true)
            } else {
                val character = item.character
                _state.value = _state.value.copy(
                    name = character.name,
                    status = character.status,
                    species = character.species,
                    gender = character.gender,
                    image = character.image,
                    isLoading = false
                )
            }
        }
    }

    fun onNameChange(value: String) {
        _state.value = _state.value.copy(name = value, showNameError = false)
    }

    fun onStatusChange(value: String) {
        _state.value = _state.value.copy(status = value)
    }

    fun onSpeciesChange(value: String) {
        _state.value = _state.value.copy(species = value)
    }

    fun onGenderChange(value: String) {
        _state.value = _state.value.copy(gender = value)
    }

    fun onImageChange(value: String) {
        _state.value = _state.value.copy(image = value)
    }

    fun save() {
        val current = _state.value
        if (current.isSaving) return
        if (current.name.isBlank()) {
            _state.value = current.copy(showNameError = true)
            return
        }

        viewModelScope.launch {
            _state.value = current.copy(isSaving = true)

            val character = Character(
                id = editingId ?: 0, // al crear, el repository asigna el id real
                name = current.name.trim(),
                status = current.status,
                species = current.species.trim(),
                gender = current.gender,
                image = current.image.trim()
            )

            if (editingId == null) {
                characterRepository.createCharacter(character)
            } else {
                characterRepository.updateCharacter(character)
            }

            _state.value = _state.value.copy(isSaving = false, isSaved = true)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CharacterFormViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    characterRepository = appContainer().characterRepository
                )
            }
        }
    }
}
