package com.example.scoreboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class VolleyballScoreboardActivity extends AppCompatActivity {
    public int scoreTeamA = 0;
    public int scoreTeamB = 0;
    public int setsWonA = 0;
    public int setsWonB = 0;
    public int setNumber = 1;
    public int maxSets = 5;
    public int[][] setScores;
    public static final int BASE_SCORE = 25;
    public static final int TIEBREAKER_SCORE = 15;
    public int maxScore = BASE_SCORE;
    public String[] teamAPlayers = new String[6];
    public String[] teamBPlayers = new String[6];
    public Button[] teamAButtons = new Button[6];
    public Button[] teamBButtons = new Button[6];
    public int[][] teamAScores = new int[7][maxSets];
    public int[][] teamBScores = new int[7][maxSets];
    public int rotA = 0;
    public int rotB = 0;
    public boolean isTeamABallPos;
    public TextView[] ballPosLabel = new TextView[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volleyball_scoreboard);
        setScores = new int[2][maxSets];
        TextView teamAName = (TextView) findViewById(R.id.teamNameA);
        teamAName.setText("Team A");
        TextView teamBName = (TextView) findViewById(R.id.teamNameB);
        teamBName.setText("Team B");

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
        for (int i = 0; i < 6; i++) {
            teamAPlayers[i] = String.valueOf(i + 1);
            teamBPlayers[i] = String.valueOf(i + 1);
            teamAButtons[i].setText(teamAPlayers[i]);
            teamBButtons[i].setText(teamBPlayers[i]);
        }

        ballPosLabel[0] = (TextView) findViewById(R.id.ballA);
        ballPosLabel[1] = (TextView) findViewById(R.id.ballB);

        isTeamABallPos = true;
        ballPosLabel[0].setVisibility(View.VISIBLE);
        ballPosLabel[1].setVisibility(View.INVISIBLE);
    }

    public void add_1(View v) {
        if (v.getId() == R.id.player_A1) add(true, 0);
        else add(false, 0);
    }

    public void add_2(View v) {
        if (v.getId() == R.id.player_A2) add(true, 1);
        else add(false, 1);
    }

    public void add_3(View v) {
        if (v.getId() == R.id.player_A3) add(true, 2);
        else add(false, 2);
    }

    public void add_4(View v) {
        if (v.getId() == R.id.player_A4) add(true, 3);
        else add(false, 3);
    }

    public void add_5(View v) {
        if (v.getId() == R.id.player_A5) add(true, 4);
        else add(false, 4);
    }

    public void add_6(View v) {
        if (v.getId() == R.id.player_A6) add(true, 5);
        else add(false, 5);
    }

    private void add(boolean isScoreFromLeft, int buttonOffset) {
        if (isScoreFromLeft) {
            if (setNumber % 2 == 1) {
                addA(R.id.scoreA, buttonOffset);
            } else {
                addB(R.id.scoreB, buttonOffset);
            }
        } else {
            if (setNumber % 2 == 1) {
                addB(R.id.scoreB, buttonOffset);
            } else {
                addA(R.id.scoreB, buttonOffset);
            }
        }

        if ((scoreTeamA - scoreTeamB) >= 2 && scoreTeamA >= maxScore) {
            Toast.makeText(this, "Team A wins! Score: " + scoreTeamA + "-" + scoreTeamB, Toast.LENGTH_SHORT).show();
            setsWonA++;
            afterWin(true);
        } else if ((scoreTeamB - scoreTeamA) >= 2 && scoreTeamB >= maxScore) {
            Toast.makeText(this, "Team B wins! Score: " + scoreTeamB + "-" + scoreTeamA, Toast.LENGTH_SHORT).show();
            setsWonB++;
            afterWin(false);
        }

        String s = "Team A: ";
        String t = "Team B: ";
        for (int i = 1; i <= 6; i++) {
            s = s.concat("[" + String.valueOf(teamAScores[i][setNumber - 1]) + "] ");
            t = t.concat("[" + String.valueOf(teamBScores[i][setNumber - 1]) + "] ");
        }
        Log.e("SCORE! ", s.concat(t));

    }

    private void addA(int viewId, int buttonOffset) {
        teamAScores[((rotA + buttonOffset) % 6) + 1][setNumber - 1]++;
        scoreTeamA++;
        TextView currentScore = (TextView) findViewById(viewId);
        currentScore.setText(String.valueOf(scoreTeamA));
        if (!isTeamABallPos) {
            // change
            ballPosLabel[0].setVisibility(View.VISIBLE);
            ballPosLabel[1].setVisibility(View.INVISIBLE);
            isTeamABallPos = true;
            rotA = (rotA + 1) % 6;

            for (int i = 0; i < 6; i++) {
                teamAButtons[i].setText(teamAPlayers[(i + rotA) % 6]);
            }
        }
    }

    private void addB(int viewId, int buttonOffset) {
        teamBScores[((rotB + buttonOffset) % 6) + 1][setNumber - 1]++;
        scoreTeamB++;
        TextView currentScore = (TextView) findViewById(viewId);
        currentScore.setText(String.valueOf(scoreTeamB));
        if (isTeamABallPos) {
            // change
            ballPosLabel[0].setVisibility(View.INVISIBLE);
            ballPosLabel[1].setVisibility(View.VISIBLE);
            isTeamABallPos = false;
            rotB = (rotB + 1) % 6;

            for (int i = 0; i < 6; i++) {
                teamBButtons[i].setText(teamBPlayers[(i + rotA) % 6]);
            }
        }
    }

    private void afterWin(boolean isSetWinnerTeamA) {
        // Save set score
        setScores[0][setNumber - 1] = scoreTeamA;
        setScores[1][setNumber - 1] = scoreTeamB;

        // Update set score in app screen
        TextView teamAName = (TextView) findViewById(R.id.teamNameA);
        TextView teamBName = (TextView) findViewById(R.id.teamNameB);

        if (setNumber % 2 == 1) {
            teamAName.setText("Team B");
            teamBName.setText("Team A");
        } else {
            teamAName.setText("Team A");
            teamBName.setText("Team B");
        }

        for (int i = 0; i < setNumber; i++) {
            switch (i + 1) {
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

    private void updateSetScore(int teamA, int teamB, int setNo) {
        TextView scoreA = (TextView) (setNumber % 2 == 0 ? findViewById(teamA) : findViewById(teamB));
        TextView scoreB = (TextView) (setNumber % 2 == 0 ? findViewById(teamB) : findViewById(teamA));
        scoreA.setText(String.valueOf(setScores[0][setNo - 1]));
        scoreB.setText(String.valueOf(setScores[1][setNo - 1]));
    }

    // To-do:
    public void undo(View v) {
    }

    @Override
    public void onBackPressed() {

    }
}