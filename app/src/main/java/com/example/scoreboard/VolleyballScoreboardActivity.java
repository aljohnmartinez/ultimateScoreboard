package com.example.scoreboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class VolleyballScoreboardActivity extends AppCompatActivity {
    // Constant values
    public static final int BASE_SCORE = 25;
    public static final int TIEBREAKER_SCORE = 15;
    public static final int MAX_IN_PLAYERS = 6;

    // Game scoring values
    public int scoreTeamA = 0;
    public int scoreTeamB = 0;
    public int setsWonA = 0;
    public int setsWonB = 0;
    public int setNumber = 1;
    public int maxSets;
    public int rotA = 0;
    public int rotB = 0;
    public int maxScore;
    public int[][] setScores;
    public int[][] teamAScores;
    public int[][] teamBScores;

    // UI Components
    public Button[] teamAButtons = new Button[MAX_IN_PLAYERS];
    public Button[] teamBButtons = new Button[MAX_IN_PLAYERS];
    public TextView[] ballPosLabel = new TextView[2];
    public TextView[] teamNameLabel = new TextView[2];
    public TextView[] teamScoreLabel = new TextView[2];
    public String teamAName;
    public String teamBName;

    public String[] teamAPlayers = new String[6];
    public String[] teamBPlayers = new String[6];
    public boolean isTeamABallPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volleyball_scoreboard);
        maxScore = BASE_SCORE;
        maxSets = 5;

        setScores = new int[2][maxSets];
        teamAScores = new int[7][maxSets];
        teamBScores = new int[7][maxSets];

        teamAName = "Team ATeam ATeam ATeam A";
        teamBName = "Team B";

        // Displays respective names of teams
        teamNameLabel[0] = (TextView) findViewById(R.id.teamNameA);
        teamNameLabel[1] = (TextView) findViewById(R.id.teamNameB);
        teamNameLabel[0].setText(teamAName);
        teamNameLabel[1].setText(teamBName);

        // Ball possession label
        isTeamABallPos = true;
        ballPosLabel[0] = (TextView) findViewById(R.id.ballA);
        ballPosLabel[1] = (TextView) findViewById(R.id.ballB);
        toggleBallPossession(0, 1);

        // Score label
        teamScoreLabel[0] = (TextView) findViewById(R.id.scoreA);
        teamScoreLabel[1] = (TextView) findViewById(R.id.scoreB);

        // Display respective names of players
        teamAButtons[0] = (Button) findViewById(R.id.player_A1);
        teamAButtons[1] = (Button) findViewById(R.id.player_A2);
        teamAButtons[2] = (Button) findViewById(R.id.player_A3);
        teamAButtons[3] = (Button) findViewById(R.id.player_A4);
        teamAButtons[4] = (Button) findViewById(R.id.player_A5);
        teamAButtons[5] = (Button) findViewById(R.id.player_A6);
        teamBButtons[0] = (Button) findViewById(R.id.player_B1);
        teamBButtons[1] = (Button) findViewById(R.id.player_B2);
        teamBButtons[2] = (Button) findViewById(R.id.player_B3);
        teamBButtons[3] = (Button) findViewById(R.id.player_B4);
        teamBButtons[4] = (Button) findViewById(R.id.player_B5);
        teamBButtons[5] = (Button) findViewById(R.id.player_B6);
        for (int i = 0; i < MAX_IN_PLAYERS; i++) {
            teamAPlayers[i] = "A" + String.valueOf(i + 1);
            teamBPlayers[i] = "B" + String.valueOf(i + 1);
            teamAButtons[i].setText(teamAPlayers[i]);
            teamBButtons[i].setText(teamBPlayers[i]);
        }
    }

    /*
       Point System Notes:
       - Player scores array column size is total players + 1
         additional 1 is allocated for opponent errors
     */

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
        if (isScoreFromLeft) {
            if (setNumber % 2 == 1) scoreA(buttonOffset);
            else scoreB(buttonOffset);
        } else {
            if (setNumber % 2 == 1) scoreB(buttonOffset);
            else scoreA(buttonOffset);
        }

        // Determine if a team wins the set
        if ((scoreTeamA - scoreTeamB) >= 2 && scoreTeamA >= maxScore) {
            Toast.makeText(this, "Team A wins! Score: " + scoreTeamA + "-" + scoreTeamB, Toast.LENGTH_SHORT).show();
            setsWonA++;
            postSetProcess();
        } else if ((scoreTeamB - scoreTeamA) >= 2 && scoreTeamB >= maxScore) {
            Toast.makeText(this, "Team B wins! Score: " + scoreTeamB + "-" + scoreTeamA, Toast.LENGTH_SHORT).show();
            setsWonB++;
            postSetProcess();
        }

        // Score Logs
        String s = "(" + scoreTeamA + "): ";
        String t = "(" + scoreTeamB + "): ";
        for (int i = 0; i <= 6; i++) {
            s = s.concat("[" + String.valueOf(teamAScores[i][setNumber - 1]) + "] ");
            t = t.concat("[" + String.valueOf(teamBScores[i][setNumber - 1]) + "] ");
        }
        Log.e("Team A", s);
        Log.e("Team B", t);
    }

    // Player from Team A (team in left) gets a point
    private void scoreA(int buttonOffset) {
        teamAScores[((rotA + buttonOffset) % MAX_IN_PLAYERS) + 1][setNumber - 1]++;
        scoreTeamA++;
        // change
        if (setNumber % 2 == 1) {
            teamScoreLabel[0].setText(String.valueOf(scoreTeamA));
            toggleBallPossession(0, 1);
            if (!isTeamABallPos) {
                rotateA(teamAButtons);
                isTeamABallPos = true;
            }
        } else {
            teamScoreLabel[1].setText(String.valueOf(scoreTeamA));
            toggleBallPossession(1, 0);
            if (!isTeamABallPos) {
                rotateA(teamBButtons);
                isTeamABallPos = true;
            }
        }
    }

    // Player from Team B (team in right) gets a point
    private void scoreB(int buttonOffset) {
        teamBScores[((rotB + buttonOffset) % MAX_IN_PLAYERS) + 1][setNumber - 1]++;
        scoreTeamB++;
        // change
        if (setNumber % 2 == 1) {
            teamScoreLabel[1].setText(String.valueOf(scoreTeamB));
            toggleBallPossession(1, 0);
            if (isTeamABallPos) {
                rotateB(teamBButtons);
                isTeamABallPos = false;
            }
        } else {
            teamScoreLabel[0].setText(String.valueOf(scoreTeamB));
            toggleBallPossession(0, 1);
            if (isTeamABallPos) {
                rotateB(teamAButtons);
                isTeamABallPos = false;
            }
        }
    }

    // SUPPLEMENTAL FUNCTIONS (for code optimality)
    private void toggleBallPossession(int showIndex, int hideIndex) {
        ballPosLabel[showIndex].setVisibility(View.VISIBLE);
        ballPosLabel[hideIndex].setVisibility(View.INVISIBLE);
    }

    private void rotateA(Button[] teamAButtonLocation) {
        rotA = (rotA + 1) % MAX_IN_PLAYERS;

        for (int i = 0; i < MAX_IN_PLAYERS; i++) {
            teamAButtonLocation[i].setText(teamAPlayers[(i + rotA) % MAX_IN_PLAYERS]);
        }
    }

    private void rotateB(Button[] teamBButtonLocation) {
        rotB = (rotB + 1) % MAX_IN_PLAYERS;

        for (int i = 0; i < MAX_IN_PLAYERS; i++) {
            teamBButtonLocation[i].setText(teamBPlayers[(i + rotB) % MAX_IN_PLAYERS]);
        }
    }

    private void postSetProcess() {
        // Save set score
        setScores[0][setNumber - 1] = scoreTeamA;
        setScores[1][setNumber - 1] = scoreTeamB;

        changePlayerCourt(false);

        for (int i = 1; i <= setNumber; i++) {
            switch (i) {
                case 1:
                    updateSetScore(R.id.set1A, R.id.set1B, 1);
                    break;
                case 2:
                    updateSetScore(R.id.set2A, R.id.set2B, 2);
                    break;
                case 3:
                    updateSetScore(R.id.set3A, R.id.set3B, 3);
                    break;
                case 4:
                    updateSetScore(R.id.set4A, R.id.set4B, 4);
                    break;
                case 5:
                    updateSetScore(R.id.set5A, R.id.set5B, 5);
                    break;
            }
        }

        if (setsWonA == 3) {
            Toast.makeText(this, "Team A wins the game!", Toast.LENGTH_SHORT).show();
        } else if (setsWonB == 3) {
            Toast.makeText(this, "Team B wins the game!", Toast.LENGTH_SHORT).show();
        } else {
            // Update values (primitive)
            setNumber++;
            scoreTeamA = 0;
            scoreTeamB = 0;
            rotA = 0;
            rotB = 0;
            isTeamABallPos = setNumber % 2 == 1;
            toggleBallPossession(0, 1);

            // Update values (App screen)
            TextView setNo = (TextView) findViewById(R.id.setNumber);
            setNo.setText(String.valueOf(setNumber));

            TextView scoreTeamA = (TextView) findViewById(R.id.scoreA);
            scoreTeamA.setText("0");
            TextView scoreTeamB = (TextView) findViewById(R.id.scoreB);
            scoreTeamB.setText("0");
            maxScore = setNumber < maxSets ? BASE_SCORE : TIEBREAKER_SCORE;
        }
    }

    private void changePlayerCourt(boolean isLastSet) {
        int rotationA = 0;
        int rotationB = 0;

        if (isLastSet) {
            rotationA = rotB;
            rotationB = rotA;
            teamScoreLabel[0].setText(String.valueOf(scoreTeamB));
            teamScoreLabel[1].setText(String.valueOf(scoreTeamA));
        } else {
            teamScoreLabel[0].setText("0");
            teamScoreLabel[1].setText("0");
        }

        for (int i = 0; i < MAX_IN_PLAYERS; i++) {
            if (setNumber % 2 == 1) {
                teamAButtons[i].setText(teamBPlayers[(i + rotationB) % MAX_IN_PLAYERS]);
                teamBButtons[i].setText(teamAPlayers[(i + rotationA) % MAX_IN_PLAYERS]);
            } else {
                teamAButtons[i].setText(teamAPlayers[(i + rotationA) % MAX_IN_PLAYERS]);
                teamBButtons[i].setText(teamBPlayers[(i + rotationB) % MAX_IN_PLAYERS]);
            }
        }

        // Update team name in app screen
        if (setNumber % 2 == 1) {
            teamNameLabel[0].setText(teamBName);
            teamNameLabel[1].setText(teamAName);
        } else {
            teamNameLabel[0].setText(teamAName);
            teamNameLabel[1].setText(teamBName);
        }
    }

    private void updateSetScore(int teamA, int teamB, int setNo) {
        TextView scoreA = (TextView) (setNumber % 2 == 0 ? findViewById(teamA) : findViewById(teamB));
        TextView scoreB = (TextView) (setNumber % 2 == 0 ? findViewById(teamB) : findViewById(teamA));
        scoreA.setText(String.valueOf(setScores[0][setNo - 1]));
        scoreB.setText(String.valueOf(setScores[1][setNo - 1]));
    }

    // TODO: Undo point
    public void undo(View v) {
    }

    // TODO: Add confirmation dialog to discard progress
    @Override
    public void onBackPressed() {

    }
}