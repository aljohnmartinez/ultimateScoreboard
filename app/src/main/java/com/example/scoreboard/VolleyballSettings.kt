package com.example.scoreboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData

class VolleyballSettings : AppCompatActivity(), OnItemSelectedListener {


    private val maxSetIndex = MutableLiveData<Int>()
    private val maxPtIndex = MutableLiveData<Int>()

    lateinit var maxSetArray : Array<String>
    lateinit var maxPointArray : Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volleyball_settings)

        maxSetArray = resources.getStringArray(R.array.max_set_options)
        maxPointArray = resources.getStringArray(R.array.max_point_options)

        val maxSetSpinner = findViewById<View>(R.id.maxSetOption) as Spinner
        val maxSetAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, maxSetArray)
        maxSetSpinner.adapter = maxSetAdapter
        maxSetSpinner.onItemSelectedListener = this

        val maxPtsSpinner = findViewById<View>(R.id.maxPtsOption) as Spinner
        val maxPtsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, maxPointArray)
        maxPtsSpinner.adapter = maxPtsAdapter
        maxPtsSpinner.onItemSelectedListener = this
    }

    fun startGame(v: View?) {
        // Get values
        val teamAName = findViewById<View>(R.id.teamAName) as EditText
        val teamNameA = if (teamAName.text.isEmpty()) teamAName.hint.toString() else teamAName.text.toString()

        val teamBName = findViewById<View>(R.id.teamBName) as EditText
        val teamNameB = if (teamBName.text.isEmpty()) teamBName.hint.toString() else teamBName.text.toString()

        val possessionSwitch = findViewById<View>(R.id.firstServeSwitch) as Switch
        val isTeamAFirstServe = !possessionSwitch.isChecked

        val startGameIntent = Intent(this, VolleyballScoreboardActivity::class.java)
        startGameIntent.putExtra(VolleyballScoreboardActivity.MAX_SETS, maxSetArray[maxSetIndex.value!!])
        startGameIntent.putExtra(VolleyballScoreboardActivity.MAX_PTS, maxPointArray[maxPtIndex.value!!])
        startGameIntent.putExtra(VolleyballScoreboardActivity.TEAM_A_NAME, teamNameA)
        startGameIntent.putExtra(VolleyballScoreboardActivity.TEAM_B_NAME, teamNameB)
        startGameIntent.putExtra(VolleyballScoreboardActivity.FIRST_SERVE, isTeamAFirstServe)
        startActivity(startGameIntent)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.id) {
            R.id.maxSetOption -> maxSetIndex.value = position
            R.id.maxPtsOption -> maxPtIndex.value = position
            else -> {
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {} /*
      POINTS TO CONSIDER:
      - Maximum number of sets
      - Maximum points in a set
      - Player line-up (both teams)
      - Starting six
      - First ball possession (First service)
      - Court assignment
     */
}