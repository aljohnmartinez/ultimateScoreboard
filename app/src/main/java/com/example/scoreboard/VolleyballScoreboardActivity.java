package com.example.scoreboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class VolleyballScoreboardActivity extends AppCompatActivity {
    // Constant values
    public static final String TEAM_A_NAME = "teamAName";
    public static final String TEAM_B_NAME = "teamBName";
    public static final String MAX_SETS = "maxSets";
    public static final String MAX_PTS = "maxPts";
    public static final String FIRST_SERVE = "isLeftFirstServe";
    public static final int TIEBREAKER_SCORE = 15;
    public static final int MAX_IN_PLAYERS = 6;

    // Game scoring values
    public int scoreTeamA = 0;
    public int scoreTeamB = 0;
    public int setsWonA = 0;
    public int setsWonB = 0;
    public int setNumber = 1;
    public int setsToWin;
    public int maxSets;
    public int rotA = 0;
    public int rotB = 0;
    public int pointsToWin;
    public int pointsToChangeCourt;
    public int[][] setScores;
    public int[][] teamAScores;
    public int[][] teamBScores;

    // UI Components
    public TextView[] teamAButtons = new TextView[MAX_IN_PLAYERS + 1];
    public TextView[] teamBButtons = new TextView[MAX_IN_PLAYERS + 1];
    public TextView[] ballPosLabel = new TextView[2];
    public TextView[] teamNameLabel = new TextView[2];
    public TextView[] teamScoreLabel = new TextView[2];
    public TextView[][] setScoreList;
    public String teamAName;
    public String teamBName;

    public String[] teamAPlayers = new String[6];
    public String[] teamBPlayers = new String[6];
    public boolean isTeamABallPos;      // Track server from the previous play
    public boolean isTeamAFirstServe;   // Track first server (indicated by user)
    public boolean isTeamALeftPos;      // Track if team registered in left court is in its position
    public boolean isLastSetChangeCourtDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volleyball_scoreboard);

        String maxSetStr = getIntent().getStringExtra(MAX_SETS);
        setsToWin = Integer.valueOf(maxSetStr);
        String maxPoints = getIntent().getStringExtra(MAX_PTS);
        pointsToWin = Integer.valueOf(maxPoints);
        maxSets = (setsToWin * 2) - 1;

        setScores = new int[2][maxSets];
        teamAScores = new int[7][maxSets];
        teamBScores = new int[7][maxSets];

        teamAName = getIntent().getStringExtra(TEAM_A_NAME);
        teamBName = getIntent().getStringExtra(TEAM_B_NAME);

        isTeamAFirstServe = isTeamABallPos = getIntent().getBooleanExtra(FIRST_SERVE, false);
        isTeamALeftPos = true;
        isLastSetChangeCourtDone = false;

        bindUI();

        // Displays respective names of teams
        teamNameLabel[0].setText(teamAName);
        teamNameLabel[1].setText(teamBName);

        if (isTeamABallPos) toggleBallPossession(0, 1);
        else toggleBallPossession(1, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.volleyball_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset:
                reset();
                return true;

            case R.id.action_undo:
                undo(null);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    // Takes note of the UI elements
    private void bindUI() {
        teamNameLabel[0] = (TextView) findViewById(R.id.teamNameA);
        teamNameLabel[1] = (TextView) findViewById(R.id.teamNameB);

        // Ball possession label
        ballPosLabel[0] = (TextView) findViewById(R.id.ballA);
        ballPosLabel[1] = (TextView) findViewById(R.id.ballB);

        // Score label
        teamScoreLabel[0] = (TextView) findViewById(R.id.scoreA);
        teamScoreLabel[1] = (TextView) findViewById(R.id.scoreB);

        // Display respective names of players
        teamAButtons[0] = (TextView) findViewById(R.id.player_A0);
        teamAButtons[1] = (TextView) findViewById(R.id.player_A1);
        teamAButtons[2] = (TextView) findViewById(R.id.player_A2);
        teamAButtons[3] = (TextView) findViewById(R.id.player_A3);
        teamAButtons[4] = (TextView) findViewById(R.id.player_A4);
        teamAButtons[5] = (TextView) findViewById(R.id.player_A5);
        teamAButtons[6] = (TextView) findViewById(R.id.player_A6);
        teamBButtons[0] = (TextView) findViewById(R.id.player_B0);
        teamBButtons[1] = (TextView) findViewById(R.id.player_B1);
        teamBButtons[2] = (TextView) findViewById(R.id.player_B2);
        teamBButtons[3] = (TextView) findViewById(R.id.player_B3);
        teamBButtons[4] = (TextView) findViewById(R.id.player_B4);
        teamBButtons[5] = (TextView) findViewById(R.id.player_B5);
        teamBButtons[6] = (TextView) findViewById(R.id.player_B6);
        for (int i = 0; i < MAX_IN_PLAYERS; i++) {
            teamAPlayers[i] = "A" + String.valueOf(i + 1);
            teamBPlayers[i] = "B" + String.valueOf(i + 1);
            teamAButtons[i + 1].setText(teamAPlayers[i]);
            teamBButtons[i + 1].setText(teamBPlayers[i]);
        }

        // Binding of set scores list
        setScoreList = new TextView[2][5];
        setScoreList[0][0] = (TextView) findViewById(R.id.set1A);
        setScoreList[0][1] = (TextView) findViewById(R.id.set2A);
        setScoreList[0][2] = (TextView) findViewById(R.id.set3A);
        setScoreList[0][3] = (TextView) findViewById(R.id.set4A);
        setScoreList[0][4] = (TextView) findViewById(R.id.set5A);
        setScoreList[1][0] = (TextView) findViewById(R.id.set1B);
        setScoreList[1][1] = (TextView) findViewById(R.id.set2B);
        setScoreList[1][2] = (TextView) findViewById(R.id.set3B);
        setScoreList[1][3] = (TextView) findViewById(R.id.set4B);
        setScoreList[1][4] = (TextView) findViewById(R.id.set5B);

        // Modify the scores to be displayed in the list
        switch (setsToWin) {
            case 1:
                setScoreList[0][1].setVisibility(View.GONE);
                setScoreList[1][1].setVisibility(View.GONE);
                setScoreList[0][2].setVisibility(View.GONE);
                setScoreList[1][2].setVisibility(View.GONE);
            case 2:
                setScoreList[0][3].setVisibility(View.GONE);
                setScoreList[1][3].setVisibility(View.GONE);
                setScoreList[0][4].setVisibility(View.GONE);
                setScoreList[1][4].setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void reset() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle(R.string.reset_label)
                .setMessage("Do you want to reset the game?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /*
       Point System Note:
       Player scores array column size is total players + 1:
       index 0 is reserved for opponent errors + 6 players
     */

    // Credit point for opponent's error
    public void add_0(View v) {
        if (v.getId() == R.id.player_A0) score(true, -1);
        else score(false, -1);
    }

    // Credit point for player in Zone 1
    public void add_1(View v) {
        if (v.getId() == R.id.player_A1) score(true, 0);
        else score(false, 0);
    }

    // Credit point for player in Zone 2
    public void add_2(View v) {
        if (v.getId() == R.id.player_A2) score(true, 1);
        else score(false, 1);
    }

    // Credit point for player in Zone 3
    public void add_3(View v) {
        if (v.getId() == R.id.player_A3) score(true, 2);
        else score(false, 2);
    }

    // Credit point for player in Zone 4
    public void add_4(View v) {
        if (v.getId() == R.id.player_A4) score(true, 3);
        else score(false, 3);
    }

    // Credit point for player in Zone 5
    public void add_5(View v) {
        if (v.getId() == R.id.player_A5) score(true, 4);
        else score(false, 4);
    }

    // Credit point for player in Zone 6
    public void add_6(View v) {
        if (v.getId() == R.id.player_A6) score(true, 5);
        else score(false, 5);
    }

    // Add point
    private void score(boolean isScoreFromLeft, int buttonOffset) {
        if (isScoreFromLeft == isTeamALeftPos) scoreA(buttonOffset);
        else scoreB(buttonOffset);

        // Score Logs
        String s = "(" + scoreTeamA + "): ";
        String t = "(" + scoreTeamB + "): ";
        for (int i = 0; i < 7; i++) {
            s = s.concat("[" + String.valueOf(teamAScores[i][setNumber - 1]) + "] ");
            t = t.concat("[" + String.valueOf(teamBScores[i][setNumber - 1]) + "] ");
        }
        Log.e("Team A", s);
        Log.e("Team B", t);

        // Determine if a team wins the set
        if ((scoreTeamA - scoreTeamB) >= 2 && scoreTeamA >= pointsToWin) {
            setsWonA++;
            postSetProcess();
        } else if ((scoreTeamB - scoreTeamA) >= 2 && scoreTeamB >= pointsToWin) {
            setsWonB++;
            postSetProcess();
        } else if (setNumber == maxSets && !isLastSetChangeCourtDone &&
                (scoreTeamA == pointsToChangeCourt || scoreTeamB == pointsToChangeCourt)) {
            changePlayerCourt(true);
            isLastSetChangeCourtDone = true;
        }
    }

    // Player from Team A (team in left) gets a point
    private void scoreA(int buttonOffset) {
        int scoreIndex = 0;
        if (buttonOffset >= 0) scoreIndex = ((rotA + buttonOffset) % MAX_IN_PLAYERS) + 1;
        teamAScores[scoreIndex][setNumber - 1]++;
        scoreTeamA++;
        // change
        if (isTeamALeftPos) {
            teamScoreLabel[0].setText(String.valueOf(scoreTeamA));
            toggleBallPossession(0, 1);
            if (!isTeamABallPos) {
                rotateA(teamAButtons);
                isTeamABallPos = !isTeamABallPos;
            }
        } else {
            teamScoreLabel[1].setText(String.valueOf(scoreTeamA));
            toggleBallPossession(1, 0);
            if (!isTeamABallPos) {
                rotateA(teamBButtons);
                isTeamABallPos = !isTeamABallPos;
            }
        }
    }

    // Player from Team B (team in right) gets a point
    private void scoreB(int buttonOffset) {
        int scoreIndex = 0;
        if (buttonOffset >= 0) scoreIndex = ((rotB + buttonOffset) % MAX_IN_PLAYERS) + 1;
        teamBScores[scoreIndex][setNumber - 1]++;
        scoreTeamB++;
        // change
        if (isTeamALeftPos) {
            teamScoreLabel[1].setText(String.valueOf(scoreTeamB));
            toggleBallPossession(1, 0);
            if (isTeamABallPos) {
                rotateB(teamBButtons);
                isTeamABallPos = !isTeamABallPos;
            }
        } else {
            teamScoreLabel[0].setText(String.valueOf(scoreTeamB));
            toggleBallPossession(0, 1);
            if (isTeamABallPos) {
                rotateB(teamAButtons);
                isTeamABallPos = !isTeamABallPos;
            }
        }
    }

    private void postSetProcess() {
        // Save set score
        setScores[0][setNumber - 1] = scoreTeamA;
        setScores[1][setNumber - 1] = scoreTeamB;

        updateSetScores();
        togglePlayerButtons(false);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        if (setsWonA == setsToWin) {
            alertDialogBuilder
                    .setMessage(teamAName + " wins the match!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

        } else if (setsWonB == setsToWin) {
            alertDialogBuilder
                    .setMessage(teamBName + " wins the match!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
        } else {
            // Update values (primitive)
            setNumber++;
            scoreTeamA = 0;
            scoreTeamB = 0;
            rotA = 0;
            rotB = 0;

            // Update values (App screen)
            TextView setNo = (TextView) findViewById(R.id.setNumber);
            setNo.setText(String.valueOf(setNumber));

            isTeamALeftPos = !isTeamALeftPos;
            isTeamABallPos = (isTeamALeftPos == isTeamAFirstServe);
            if (isTeamABallPos == isTeamALeftPos) toggleBallPossession(0, 1);
            else toggleBallPossession(1, 0);

            if (setNumber == maxSets) {
                if (pointsToWin == 10) pointsToWin = 10;
                else pointsToWin = TIEBREAKER_SCORE;
                pointsToChangeCourt = (int) Math.ceil(((double) pointsToWin) / 2);
            }

            String title;
            String message;
            if (setScores[0][setNumber - 2] > setScores[1][setNumber - 2])
                title = teamAName + " wins Set " + (setNumber - 1) + "!";
            else
                title = teamBName + " wins Set " + (setNumber - 1) + "!";

            if (setsWonA > setsWonB)
                message = teamAName + " now leads " + setsWonA + "-" + setsWonB + ".";
            else if (setsWonB > setsWonA)
                message = teamBName + " now leads " + setsWonB + "-" + setsWonA + ".";
            else
                message = " Game is now tied " + setsWonA + "-" + setsWonB + ".";

            alertDialogBuilder
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Start next set!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            changePlayerCourt(false);
                            togglePlayerButtons(true);
                            dialog.cancel();
                        }
                    });
        }
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void changePlayerCourt(boolean isLastSet) {
        int rotationA = 0;
        int rotationB = 0;

        if (isLastSet) {
            rotationA = rotA;
            rotationB = rotB;
            teamScoreLabel[0].setText(String.valueOf(scoreTeamB));
            teamScoreLabel[1].setText(String.valueOf(scoreTeamA));

            isTeamALeftPos = !isTeamALeftPos;
            if (isTeamABallPos) toggleBallPossession(1, 0);
            else toggleBallPossession(0, 1);

            rotA = rotationB;
            rotB = rotationA;
        } else {
            teamScoreLabel[0].setText(R.string.default_score_label);
            teamScoreLabel[1].setText(R.string.default_score_label);
        }

        for (int i = 0; i < MAX_IN_PLAYERS; i++) {
            if (isTeamALeftPos) {
                teamAButtons[i + 1].setText(teamAPlayers[(i + rotationA) % MAX_IN_PLAYERS]);
                teamBButtons[i + 1].setText(teamBPlayers[(i + rotationB) % MAX_IN_PLAYERS]);
            } else {
                teamAButtons[i + 1].setText(teamBPlayers[(i + rotationB) % MAX_IN_PLAYERS]);
                teamBButtons[i + 1].setText(teamAPlayers[(i + rotationA) % MAX_IN_PLAYERS]);
            }
        }

        // Update team name in app screen
        if (isTeamALeftPos) {
            teamNameLabel[0].setText(teamAName);
            teamNameLabel[1].setText(teamBName);
        } else {
            teamNameLabel[0].setText(teamBName);
            teamNameLabel[1].setText(teamAName);
        }

        updateSetScores();
    }

    // SUPPLEMENTAL FUNCTIONS (for code optimality)
    private void rotateA(TextView[] teamAButtonLocation) {
        rotA = (rotA + 1) % MAX_IN_PLAYERS;

        for (int i = 0; i < MAX_IN_PLAYERS; i++) {
            teamAButtonLocation[i + 1].setText(teamAPlayers[(i + rotA) % MAX_IN_PLAYERS]);
        }
    }

    private void rotateB(TextView[] teamBButtonLocation) {
        rotB = (rotB + 1) % MAX_IN_PLAYERS;

        for (int i = 0; i < MAX_IN_PLAYERS; i++) {
            teamBButtonLocation[i + 1].setText(teamBPlayers[(i + rotB) % MAX_IN_PLAYERS]);
        }
    }

    private void toggleBallPossession(int showIndex, int hideIndex) {
        ballPosLabel[showIndex].setVisibility(View.VISIBLE);
        ballPosLabel[hideIndex].setVisibility(View.INVISIBLE);
    }

    private void togglePlayerButtons(boolean status) {
        for (int i = 1; i <= MAX_IN_PLAYERS; i++) {
            teamAButtons[i].setEnabled(status);
            teamBButtons[i].setEnabled(status);
        }
    }

    private void updateSetScores() {
        for (int i = 1; i <= setNumber; i++) {
            if (isTeamALeftPos) {
                setScoreList[0][i - 1].setText(String.valueOf(setScores[0][i - 1]));
                setScoreList[1][i - 1].setText(String.valueOf(setScores[1][i - 1]));
            } else {
                setScoreList[0][i - 1].setText(String.valueOf(setScores[1][i - 1]));
                setScoreList[1][i - 1].setText(String.valueOf(setScores[0][i - 1]));
            }
        }
    }

    public void undo(View v) {
    }
}