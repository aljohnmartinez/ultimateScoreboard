package com.example.scoreboard

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class VolleyballScoreboardActivity : AppCompatActivity() {
    // Game scoring values
    var scoreTeamA = 0
    var scoreTeamB = 0
    var setsWonA = 0
    var setsWonB = 0
    var setNumber = 1
    var setsToWin = 0
    var maxSets = 0
    var rotA = 0
    var rotB = 0
    var pointsToWin = 0
    var pointsToChangeCourt = 0
    lateinit var setScores: Array<IntArray>
    lateinit var teamAScores: Array<IntArray>
    lateinit var teamBScores: Array<IntArray>

    // UI Components
    var teamAButtons = arrayOfNulls<TextView>(MAX_IN_PLAYERS + 1)
    var teamBButtons = arrayOfNulls<TextView>(MAX_IN_PLAYERS + 1)
    var ballPosLabel = arrayOfNulls<TextView>(2)
    var teamNameLabel = arrayOfNulls<TextView>(2)
    var teamScoreLabel = arrayOfNulls<TextView>(2)
    lateinit var setScoreList: Array<Array<TextView?>>
    var teamAName: String? = null
    var teamBName: String? = null
    var teamAPlayers = arrayOfNulls<String>(6)
    var teamBPlayers = arrayOfNulls<String>(6)
    var isTeamABallPos : Boolean = false // Track server from the previous play = false
    var isTeamAFirstServe : Boolean = false // Track first server (indicated by user) = false
    var isTeamALeftPos : Boolean = false // Track if team registered in left court is in its position = false
    var isLastSetChangeCourtDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volleyball_scoreboard)

        val maxSetStr = intent.getStringExtra(MAX_SETS)
        setsToWin = Integer.valueOf(maxSetStr)

        val maxPoints = intent.getStringExtra(MAX_PTS)
        pointsToWin = Integer.valueOf(maxPoints)

        maxSets = setsToWin * 2 - 1

        setScores = Array(2) { IntArray(maxSets) }
        teamAScores = Array(7) { IntArray(maxSets) }
        teamBScores = Array(7) { IntArray(maxSets) }
        teamAName = intent.getStringExtra(TEAM_A_NAME)
        teamBName = intent.getStringExtra(TEAM_B_NAME)
        isTeamABallPos = intent.getBooleanExtra(FIRST_SERVE, false)
        isTeamAFirstServe = isTeamABallPos
        isTeamALeftPos = true
        isLastSetChangeCourtDone = false
        bindUI()

        // Displays respective names of teams
        teamNameLabel[0]!!.text = teamAName
        teamNameLabel[1]!!.text = teamBName
        if (isTeamABallPos) toggleBallPossession(0, 1) else toggleBallPossession(1, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.volleyball_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_reset -> {
                reset()
                true
            }
            R.id.action_undo -> {
                undo(null)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Takes note of the UI elements
    private fun bindUI() {
        teamNameLabel[0] = findViewById<View>(R.id.teamNameA) as TextView
        teamNameLabel[1] = findViewById<View>(R.id.teamNameB) as TextView

        // Ball possession label
        ballPosLabel[0] = findViewById<View>(R.id.ballA) as TextView
        ballPosLabel[1] = findViewById<View>(R.id.ballB) as TextView

        // Score label
        teamScoreLabel[0] = findViewById<View>(R.id.scoreA) as TextView
        teamScoreLabel[1] = findViewById<View>(R.id.scoreB) as TextView

        // Display respective names of players
        teamAButtons[0] = findViewById<View>(R.id.player_A0) as TextView
        teamAButtons[1] = findViewById<View>(R.id.player_A1) as TextView
        teamAButtons[2] = findViewById<View>(R.id.player_A2) as TextView
        teamAButtons[3] = findViewById<View>(R.id.player_A3) as TextView
        teamAButtons[4] = findViewById<View>(R.id.player_A4) as TextView
        teamAButtons[5] = findViewById<View>(R.id.player_A5) as TextView
        teamAButtons[6] = findViewById<View>(R.id.player_A6) as TextView
        teamBButtons[0] = findViewById<View>(R.id.player_B0) as TextView
        teamBButtons[1] = findViewById<View>(R.id.player_B1) as TextView
        teamBButtons[2] = findViewById<View>(R.id.player_B2) as TextView
        teamBButtons[3] = findViewById<View>(R.id.player_B3) as TextView
        teamBButtons[4] = findViewById<View>(R.id.player_B4) as TextView
        teamBButtons[5] = findViewById<View>(R.id.player_B5) as TextView
        teamBButtons[6] = findViewById<View>(R.id.player_B6) as TextView
        for (i in 0 until MAX_IN_PLAYERS) {
            teamAPlayers[i] = "A" + (i + 1).toString()
            teamBPlayers[i] = "B" + (i + 1).toString()
            teamAButtons[i + 1]!!.text = teamAPlayers[i]
            teamBButtons[i + 1]!!.text = teamBPlayers[i]
        }

        // Binding of set scores list
        setScoreList = Array(2) { Array<TextView?>(5) {TextView(this)} }
        setScoreList[0][0] = findViewById<View>(R.id.set1A) as TextView
        setScoreList[0][1] = findViewById<View>(R.id.set2A) as TextView
        setScoreList[0][2] = findViewById<View>(R.id.set3A) as TextView
        setScoreList[0][3] = findViewById<View>(R.id.set4A) as TextView
        setScoreList[0][4] = findViewById<View>(R.id.set5A) as TextView
        setScoreList[1][0] = findViewById<View>(R.id.set1B) as TextView
        setScoreList[1][1] = findViewById<View>(R.id.set2B) as TextView
        setScoreList[1][2] = findViewById<View>(R.id.set3B) as TextView
        setScoreList[1][3] = findViewById<View>(R.id.set4B) as TextView
        setScoreList[1][4] = findViewById<View>(R.id.set5B) as TextView
        when (setsToWin) {
            1 -> {
                setScoreList[0][1]!!.visibility = View.GONE
                setScoreList[1][1]!!.visibility = View.GONE
                setScoreList[0][2]!!.visibility = View.GONE
                setScoreList[1][2]!!.visibility = View.GONE
                setScoreList[0][3]!!.visibility = View.GONE
                setScoreList[1][3]!!.visibility = View.GONE
                setScoreList[0][4]!!.visibility = View.GONE
                setScoreList[1][4]!!.visibility = View.GONE
            }
            2 -> {
                setScoreList[0][3]!!.visibility = View.GONE
                setScoreList[1][3]!!.visibility = View.GONE
                setScoreList[0][4]!!.visibility = View.GONE
                setScoreList[1][4]!!.visibility = View.GONE
            }
            else -> {
            }
        }
    }

    private fun reset() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder
                .setTitle(R.string.reset_label)
                .setMessage("Do you want to reset the game?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id -> finish() }
                .setNegativeButton("No") { dialog, which -> dialog.cancel() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    /*
       Point System Note:
       Player scores array column size is total players + 1:
       index 0 is reserved for opponent errors + 6 players
     */
    // Credit point for opponent's error
    fun add_0(v: View) {
        if (v.id == R.id.player_A0) score(true, -1) else score(false, -1)
    }

    // Credit point for player in Zone 1
    fun add_1(v: View) {
        if (v.id == R.id.player_A1) score(true, 0) else score(false, 0)
    }

    // Credit point for player in Zone 2
    fun add_2(v: View) {
        if (v.id == R.id.player_A2) score(true, 1) else score(false, 1)
    }

    // Credit point for player in Zone 3
    fun add_3(v: View) {
        if (v.id == R.id.player_A3) score(true, 2) else score(false, 2)
    }

    // Credit point for player in Zone 4
    fun add_4(v: View) {
        if (v.id == R.id.player_A4) score(true, 3) else score(false, 3)
    }

    // Credit point for player in Zone 5
    fun add_5(v: View) {
        if (v.id == R.id.player_A5) score(true, 4) else score(false, 4)
    }

    // Credit point for player in Zone 6
    fun add_6(v: View) {
        if (v.id == R.id.player_A6) score(true, 5) else score(false, 5)
    }

    // Add point
    private fun score(isScoreFromLeft: Boolean, buttonOffset: Int) {
        if (isScoreFromLeft == isTeamALeftPos) scoreA(buttonOffset) else scoreB(buttonOffset)

        // Score Logs
        var s = "($scoreTeamA): "
        var t = "($scoreTeamB): "
        for (i in 0..6) {
            s = s + "[" + teamAScores[i][setNumber - 1].toString() + "] "
            t = t + "[" + teamBScores[i][setNumber - 1].toString() + "] "
        }
        Log.e("Team A", s)
        Log.e("Team B", t)

        // Determine if a team wins the set
        if (scoreTeamA - scoreTeamB >= 2 && scoreTeamA >= pointsToWin) {
            setsWonA++
            postSetProcess()
        } else if (scoreTeamB - scoreTeamA >= 2 && scoreTeamB >= pointsToWin) {
            setsWonB++
            postSetProcess()
        } else if (setNumber == maxSets && !isLastSetChangeCourtDone &&
                (scoreTeamA == pointsToChangeCourt || scoreTeamB == pointsToChangeCourt)) {
            changePlayerCourt(true)
            isLastSetChangeCourtDone = true
        }
    }

    // Player from Team A (team in left) gets a point
    private fun scoreA(buttonOffset: Int) {
        var scoreIndex = 0
        if (buttonOffset >= 0) scoreIndex = (rotA + buttonOffset) % MAX_IN_PLAYERS + 1
        teamAScores[scoreIndex][setNumber - 1]++
        scoreTeamA++
        // change
        if (isTeamALeftPos) {
            teamScoreLabel[0]!!.text = scoreTeamA.toString()
            toggleBallPossession(0, 1)
            if (!isTeamABallPos) {
                rotateA(teamAButtons)
                isTeamABallPos = !isTeamABallPos
            }
        } else {
            teamScoreLabel[1]!!.text = scoreTeamA.toString()
            toggleBallPossession(1, 0)
            if (!isTeamABallPos) {
                rotateA(teamBButtons)
                isTeamABallPos = !isTeamABallPos
            }
        }
    }

    // Player from Team B (team in right) gets a point
    private fun scoreB(buttonOffset: Int) {
        var scoreIndex = 0
        if (buttonOffset >= 0) scoreIndex = (rotB + buttonOffset) % MAX_IN_PLAYERS + 1
        teamBScores[scoreIndex][setNumber - 1]++
        scoreTeamB++
        // change
        if (isTeamALeftPos) {
            teamScoreLabel[1]!!.text = scoreTeamB.toString()
            toggleBallPossession(1, 0)
            if (isTeamABallPos) {
                rotateB(teamBButtons)
                isTeamABallPos = !isTeamABallPos
            }
        } else {
            teamScoreLabel[0]!!.text = scoreTeamB.toString()
            toggleBallPossession(0, 1)
            if (isTeamABallPos) {
                rotateB(teamAButtons)
                isTeamABallPos = !isTeamABallPos
            }
        }
    }

    private fun postSetProcess() {
        // Save set score
        setScores[0][setNumber - 1] = scoreTeamA
        setScores[1][setNumber - 1] = scoreTeamB
        updateSetScores()
        togglePlayerButtons(false)
        val alertDialogBuilder = AlertDialog.Builder(this)
        if (setsWonA == setsToWin) {
            alertDialogBuilder
                    .setMessage("$teamAName wins the match!")
                    .setCancelable(false)
                    .setPositiveButton("OK") { dialog, id -> dialog.cancel() }
        } else if (setsWonB == setsToWin) {
            alertDialogBuilder
                    .setMessage("$teamBName wins the match!")
                    .setCancelable(false)
                    .setPositiveButton("OK") { dialog, id -> dialog.cancel() }
        } else {
            // Update values (primitive)
            setNumber++
            scoreTeamA = 0
            scoreTeamB = 0
            rotA = 0
            rotB = 0

            // Update values (App screen)
            val setNo = findViewById<View>(R.id.setNumber) as TextView
            setNo.text = setNumber.toString()
            isTeamALeftPos = !isTeamALeftPos
            isTeamABallPos = isTeamALeftPos == isTeamAFirstServe
            if (isTeamABallPos == isTeamALeftPos) toggleBallPossession(0, 1) else toggleBallPossession(1, 0)
            if (setNumber == maxSets) {
                pointsToWin = if (pointsToWin == 10) 10 else TIEBREAKER_SCORE
                pointsToChangeCourt = Math.ceil(pointsToWin.toDouble() / 2).toInt()
            }
            val title: String
            val message: String
            title = if (setScores[0][setNumber - 2] > setScores[1][setNumber - 2]) teamAName + " wins Set " + (setNumber - 1) + "!" else teamBName + " wins Set " + (setNumber - 1) + "!"
            message = if (setsWonA > setsWonB) "$teamAName now leads $setsWonA-$setsWonB." else if (setsWonB > setsWonA) "$teamBName now leads $setsWonB-$setsWonA." else " Game is now tied $setsWonA-$setsWonB."
            alertDialogBuilder
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Start next set!") { dialog, id ->
                        changePlayerCourt(false)
                        togglePlayerButtons(true)
                        dialog.cancel()
                    }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun changePlayerCourt(isLastSet: Boolean) {
        var rotationA = 0
        var rotationB = 0
        if (isLastSet) {
            rotationA = rotA
            rotationB = rotB
            teamScoreLabel[0]!!.text = scoreTeamB.toString()
            teamScoreLabel[1]!!.text = scoreTeamA.toString()
            isTeamALeftPos = !isTeamALeftPos
            if (isTeamABallPos) toggleBallPossession(1, 0) else toggleBallPossession(0, 1)
            rotA = rotationB
            rotB = rotationA
        } else {
            teamScoreLabel[0]!!.setText(R.string.default_score_label)
            teamScoreLabel[1]!!.setText(R.string.default_score_label)
        }
        for (i in 0 until MAX_IN_PLAYERS) {
            if (isTeamALeftPos) {
                teamAButtons[i + 1]!!.text = teamAPlayers[(i + rotationA) % MAX_IN_PLAYERS]
                teamBButtons[i + 1]!!.text = teamBPlayers[(i + rotationB) % MAX_IN_PLAYERS]
            } else {
                teamAButtons[i + 1]!!.text = teamBPlayers[(i + rotationB) % MAX_IN_PLAYERS]
                teamBButtons[i + 1]!!.text = teamAPlayers[(i + rotationA) % MAX_IN_PLAYERS]
            }
        }

        // Update team name in app screen
        if (isTeamALeftPos) {
            teamNameLabel[0]!!.text = teamAName
            teamNameLabel[1]!!.text = teamBName
        } else {
            teamNameLabel[0]!!.text = teamBName
            teamNameLabel[1]!!.text = teamAName
        }
        updateSetScores()
    }

    // SUPPLEMENTAL FUNCTIONS (for code optimality)
    private fun rotateA(teamAButtonLocation: Array<TextView?>) {
        rotA = (rotA + 1) % MAX_IN_PLAYERS
        for (i in 0 until MAX_IN_PLAYERS) {
            teamAButtonLocation[i + 1]!!.text = teamAPlayers[(i + rotA) % MAX_IN_PLAYERS]
        }
    }

    private fun rotateB(teamBButtonLocation: Array<TextView?>) {
        rotB = (rotB + 1) % MAX_IN_PLAYERS
        for (i in 0 until MAX_IN_PLAYERS) {
            teamBButtonLocation[i + 1]!!.text = teamBPlayers[(i + rotB) % MAX_IN_PLAYERS]
        }
    }

    private fun toggleBallPossession(showIndex: Int, hideIndex: Int) {
        ballPosLabel[showIndex]!!.visibility = View.VISIBLE
        ballPosLabel[hideIndex]!!.visibility = View.INVISIBLE
    }

    private fun togglePlayerButtons(status: Boolean) {
        for (i in 1..MAX_IN_PLAYERS) {
            teamAButtons[i]!!.isEnabled = status
            teamBButtons[i]!!.isEnabled = status
        }
    }

    private fun updateSetScores() {
        for (i in 1..setNumber) {
            if (isTeamALeftPos) {
                setScoreList[0][i - 1]!!.text = setScores[0][i - 1].toString()
                setScoreList[1][i - 1]!!.text = setScores[1][i - 1].toString()
            } else {
                setScoreList[0][i - 1]!!.text = setScores[1][i - 1].toString()
                setScoreList[1][i - 1]!!.text = setScores[0][i - 1].toString()
            }
        }
    }

    fun undo(v: View?) {}

    companion object {
        // Constant values
        const val TEAM_A_NAME = "teamAName"
        const val TEAM_B_NAME = "teamBName"
        const val MAX_SETS = "maxSets"
        const val MAX_PTS = "maxPts"
        const val FIRST_SERVE = "isLeftFirstServe"
        const val TIEBREAKER_SCORE = 15
        const val MAX_IN_PLAYERS = 6
    }
}