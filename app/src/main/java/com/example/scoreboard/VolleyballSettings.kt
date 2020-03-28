package com.example.scoreboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.scoreboard.databinding.ActivityVolleyballSettingsBinding

class VolleyballSettings : AppCompatActivity(), OnItemSelectedListener {

    private var maxSetIndex: Int = 0
    private var maxPtIndex: Int = 0

    private lateinit var maxSetArray: Array<String>
    private lateinit var maxPointArray: Array<String>

    private lateinit var binding: ActivityVolleyballSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_volleyball_settings)

        maxSetArray = resources.getStringArray(R.array.max_set_options)
        maxPointArray = resources.getStringArray(R.array.max_point_options)

        val maxSetAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, maxSetArray)
        binding.maxSetsOption.adapter = maxSetAdapter
        binding.maxSetsOption.onItemSelectedListener = this

        val maxPtsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, maxPointArray)
        binding.maxPtsOption.adapter = maxPtsAdapter
        binding.maxPtsOption.onItemSelectedListener = this
    }

    fun startGame(v: View?) {
        // Get values
        val teamNameA = if (binding.leftTeamName.text.isNotEmpty()) binding.leftTeamName.text.toString() else binding.leftTeamName.hint.toString()
        val teamNameB = if (binding.rightTeamName.text.isNotEmpty()) binding.rightTeamName.text.toString() else binding.rightTeamName.hint.toString()

        val isTeamAFirstServe = !binding.firstServeSwitch.isChecked

        val startGameIntent = Intent(this, VolleyballScoreboardActivity::class.java)
        startGameIntent.putExtra(VolleyballScoreboardActivity.MAX_SETS, maxSetArray[maxSetIndex])
        startGameIntent.putExtra(VolleyballScoreboardActivity.MAX_PTS, maxPointArray[maxPtIndex])
        startGameIntent.putExtra(VolleyballScoreboardActivity.TEAM_A_NAME, teamNameA)
        startGameIntent.putExtra(VolleyballScoreboardActivity.TEAM_B_NAME, teamNameB)
        startGameIntent.putExtra(VolleyballScoreboardActivity.FIRST_SERVE, isTeamAFirstServe)
        startActivity(startGameIntent)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.id) {
            R.id.max_sets_option -> maxSetIndex = position
            R.id.max_pts_option -> maxPtIndex = position
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