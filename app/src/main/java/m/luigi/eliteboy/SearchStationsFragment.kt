package m.luigi.eliteboy


import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.MultiAutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_search_stations.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.luigi.eliteboy.adapters.GenericSuggestionsAdapter
import m.luigi.eliteboy.adapters.SystemsSuggestionsAdapter
import m.luigi.eliteboy.elitedangerous.edsm.EDSMApi
import m.luigi.eliteboy.elitedangerous.edsm.SearchData
import m.luigi.eliteboy.util.*

class SearchStationsFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {
    private var systemsSuggestions = ArrayList<String>()
    private var referenceSystem = ""
    var searchJob: Job = Job()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        launch {
            (activity as MainActivity).mainToolbar.title = "Search stations (50 ly)"
        }
        return inflater.inflate(R.layout.fragment_search_stations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launch {

            val arrayAdapter = SystemsSuggestionsAdapter(
                view.context,
                R.layout.support_simple_spinner_dropdown_item,
                systemsSuggestions
            )
            //TODO: Find another way to write this part of code, also in NearestFragment
            with(refSystem) {
                threshold = 3
                addTextChangedListener {
                    searchJob.cancel()
                    searchJob = this@SearchStationsFragment.launch {
                        this@with.clearListSelection()
                        this@with.dismissDropDown()
                        referenceSystem = it.toString()
                        if (referenceSystem.length > 2) {
                            arrayAdapter.clear()
                            arrayAdapter.notifyDataSetInvalidated()
                            onDefault {
                                systemsSuggestions.clear()
                                systemsSuggestions.addAll(
                                    EDSMApi.findSystemsByName(
                                        referenceSystem,
                                        showInformation = false,
                                        showCoordinates = false,
                                        limit = 5
                                    ).map { it.name!! }
                                )
                            }
                            arrayAdapter.notifyDataSetChanged()
                            if (this@with.hasFocus()) {
                                this@with.showDropDown()
                            }
                        }
                    }
                }
                setAdapter(arrayAdapter)
                this.setOnKeyListener { _, keyCode, _ ->
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        clearFocus()
                        if (systemsSuggestions.isNotEmpty()) {
                            setText(systemsSuggestions[0])
                        }
                        view.let { v ->
                            val imm = ContextCompat.getSystemService(
                                view.context,
                                InputMethodManager::class.java
                            )
                            imm?.hideSoftInputFromWindow(v.windowToken, 0)
                        }
                    }
                    true
                }

                setOnItemClickListener { _, _, position, _ ->
                    clearFocus()
                    setText(systemsSuggestions[position])
                    view.let { v ->
                        val imm = ContextCompat.getSystemService(
                            view.context,
                            InputMethodManager::class.java
                        )
                        imm?.hideSoftInputFromWindow(v.windowToken, 0)
                    }
                }
            }
            allegianceCard.setOnClickListener {
                makeAlertDialog(allegianceTypes, "Allegiance", allegianceText).show()
            }
            governmentCard.setOnClickListener {
                makeAlertDialog(governmentTypes, "Government", govText).show()
            }
            economyCard.setOnClickListener {
                makeAlertDialog(economyTypes, "Economy", economyText).show()
            }
            securityCard.setOnClickListener {
                makeAlertDialog(securityTypes, "Security", secText).show()
            }

            val shipAlertDialog =
                makeMultiChoiceAlertDialog(
                    shipIdToShipName.values.toTypedArray(),
                    "Ships",
                    shipText
                )
            shipsCard.setOnClickListener {
                shipAlertDialog.show()
            }

            val modulesAdapter = GenericSuggestionsAdapter(
                view.context,
                R.layout.support_simple_spinner_dropdown_item,
                modulesList.reversed() as ArrayList<String>
            )

            with(modulesAutoComplete) {
                threshold = 2
                setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
                setAdapter(modulesAdapter)
                setOnKeyListener { _, keyCode, _ ->
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        clearFocus()
                        view.let { v ->
                            val imm = ContextCompat.getSystemService(
                                view.context,
                                InputMethodManager::class.java
                            )
                            imm?.hideSoftInputFromWindow(v.windowToken, 0)
                        }
                    }
                    true
                }
            }

            val commAdapter = GenericSuggestionsAdapter(
                view.context,
                R.layout.support_simple_spinner_dropdown_item,
                commoditiesList
            )

            with(commoditiesAutoComplete) {
                threshold = 2
                setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
                setAdapter(commAdapter)
                setOnKeyListener { _, keyCode, _ ->
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        clearFocus()
                        view.let { v ->
                            val imm = ContextCompat.getSystemService(
                                view.context,
                                InputMethodManager::class.java
                            )
                            imm?.hideSoftInputFromWindow(v.windowToken, 0)
                        }
                    }
                    true
                }
            }

            searchButton.setOnClickListener {
                Navigation.findNavController(it).navigate(
                    R.id.action_searchStationsFragment_to_foundFragment,
                    bundleOf(
                        "stationSearchData" to SearchData(
                            allegiance = allegianceText.text.toString(),
                            government = govText.text.toString(),
                            economy = economyText.text.toString(),
                            security = secText.text.toString(),
                            ships = shipText.text.toString(),
                            modules = modulesAutoComplete.text.toString(),
                            commodities = commoditiesAutoComplete.text.toString()
                        ),
                        "origin" to "SearchStationsFragment"
                    )
                )
            }
        }
    }
}
