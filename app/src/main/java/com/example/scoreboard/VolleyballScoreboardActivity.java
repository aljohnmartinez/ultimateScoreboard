package com.example.scoreboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volleyball_scoreboard);
        setScores = new int[2][5];
        TextView teamAName = (TextView) findViewById(R.id.teamNameA);
        teamAName.setText("Team A");
        TextView teamBName = (TextView) findViewById(R.id.teamNameB);
        teamBName.setText("Team B");
    }

    public void add(View v) {
        TextView currentScore;
        switch(v.getId()) {
            case R.id.player_A1:
            case R.id.player_A2:
            case R.id.player_A3:
            case R.id.player_A4:
            case R.id.player_A5:
            case R.id.player_A6:
                currentScore = (TextView) findViewById(R.id.scoreA);
                if (setNumber % 2 == 1) {
                    scoreTeamA++;
                    currentScore.setText(String.valueOf(scoreTeamA));
                } else {
                    scoreTeamB++;
                    currentScore.setText(String.valueOf(scoreTeamB));
                }
                break;
            case R.id.player_B1:
            case R.id.player_B2:
            case R.id.player_B3:
            case R.id.player_B4:
            case R.id.player_B5:
            case R.id.player_B6:
                currentScore = (TextView) findViewById(R.id.scoreB);
                if (setNumber % 2 == 1) {
                    scoreTeamB++;
                    currentScore.setText(String.valueOf(scoreTeamB));
                } else {
                    scoreTeamA++;
                    currentScore.setText(String.valueOf(scoreTeamA));
                }
                break;
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
    }

    private void afterWin(boolean isSetWinnerTeamA) {
        // Save set score
        setScores[0][setNumber-1] = scoreTeamA;
        setScores[1][setNumber-1] = scoreTeamB;

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
            switch(i+1) {
                case 1:     updateSetScore(R.id.set1A, R.id.set1B, 1);
                            break;
                case 2:     updateSetScore(R.id.set2A, R.id.set2B, 2);
                            break;
                case 3:     updateSetScore(R.id.set3A, R.id.set3B, 3);
                            break;
                case 4:     updateSetScore(R.id.set4A, R.id.set4B, 4);
                            break;
                case 5:     updateSetScore(R.id.set5A, R.id.set5B, 5);
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
        scoreA.setText(String.valueOf(setScores[0][setNo-1]));
        scoreB.setText(String.valueOf(setScores[1][setNo-1]));
    }

    // To-do:
    public void undo(View v) {
    }
}