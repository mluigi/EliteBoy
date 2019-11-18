package m.luigi.eliteboy


import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_search_systems.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.luigi.eliteboy.adapters.SystemsSuggestionsAdapter
import m.luigi.eliteboy.elitedangerous.edsm.EDSMApi
import m.luigi.eliteboy.elitedangerous.edsm.SearchData
import m.luigi.eliteboy.util.*


class SearchSystemsFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {
    private var systemsSuggestions = ArrayList<String>()
    private var referenceSystem = ""
    private var sysName = ""
    var searchJob: Job = Job()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        launch {
            (activity as MainActivity).mainToolbar.title = "Search systems (50 ly)"
        }

        return inflater.inflate(R.layout.fragment_search_systems, container, false)
    }

    //TODO: Save and restore search parameters onResume
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
                    searchJob = this@SearchSystemsFragment.launch {
                        this@with.clearListSelection()
                        this@with.dismissDropDown()
                        referenceSystem = it.toString()
                        if (referenceSystem.length > 2) {
                            arrayAdapter.clear()
                            arrayAdapter.notifyDataSetInvalidated()
                            onDefault {
                                systemsSuggestions.clear()
                                runWhenOnline {
                                    systemsSuggestions.addAll(
                                        EDSMApi.findSystemsByName(
                                            referenceSystem,
                                            showInformation = false,
                                            showCoordinates = false,
                                            limit = 5
                                        ).map { it.name!! }
                                    )
                                }
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
                        view.hideKeyboard()
                    }
                    true
                }

                setOnItemClickListener { _, _, position, _ ->
                    clearFocus()
                    setText(systemsSuggestions[position])
                    view.hideKeyboard()
                }
            }
            with(systemName) {
                threshold = 3
                addTextChangedListener {
                    searchJob.cancel()
                    searchJob = this@SearchSystemsFragment.launch {
                        clearListSelection()
                        dismissDropDown()
                        sysName = it.toString()
                        if (sysName.length > 2) {
                            arrayAdapter.clear()
                            arrayAdapter.notifyDataSetInvalidated()
                            onDefault {
                                systemsSuggestions.clear()
                                runWhenOnline {
                                    systemsSuggestions.addAll(
                                        EDSMApi.findSystemsByName(
                                            sysName,
                                            showInformation = false,
                                            showCoordinates = false,
                                            limit = 5
                                        ).map { it.name!! }
                                    )
                                }
                            }
                            arrayAdapter.notifyDataSetChanged()
                            if (hasFocus()) {
                                showDropDown()
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
                        view.hideKeyboard()

                    }
                    true
                }

                setOnItemClickListener { _, _, position, _ ->
                    clearFocus()
                    setText(systemsSuggestions[position])
                    view.hideKeyboard()
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
            factionCard.setOnClickListener {
                makeAlertDialog(factionStates, "Faction State", factionText).show()
            }

            searchButton.setOnClickListener {
                Navigation.findNavController(it).navigate(
                    R.id.action_searchSystemsFragment_to_foundFragment,
                    bundleOf(
                        "systemSearchData" to SearchData(
                            systemName = systemName.text.toString(),
                            refSystem = refSystem.text.toString(),
                            allegiance = allegianceText.text.toString(),
                            government = govText.text.toString(),
                            economy = economyText.text.toString(),
                            security = secText.text.toString(),
                            factionState = factionText.text.toString()
                        ),
                        "origin" to "SearchSystemFragment"
                    )
                )
            }
        }
    }
}
