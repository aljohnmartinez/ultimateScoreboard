package com.example.scoreboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

public class VolleyballSettings extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private int maxSetIndex;
    private int maxPtIndex;
    private String[] maxSetArray;
    private String[] maxPointArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volleyball_settings);

        maxSetArray = getResources().getStringArray(R.array.max_set_options);
        maxPointArray = getResources().getStringArray(R.array.max_point_options);

        Spinner maxSetSpinner = (Spinner) findViewById(R.id.maxSetOption);
        ArrayAdapter<String> maxSetAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, maxSetArray);
        maxSetSpinner.setAdapter(maxSetAdapter);
        maxSetSpinner.setOnItemSelectedListener(this);

        Spinner maxPtsSpinner = (Spinner) findViewById(R.id.maxPtsOption);
        ArrayAdapter<String> maxPtsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, maxPointArray);
        maxPtsSpinner.setAdapter(maxPtsAdapter);
        maxPtsSpinner.setOnItemSelectedListener(this);
    }

    public void startGame (View v) {
        // Get values
        EditText teamAName = (EditText) findViewById(R.id.teamAName);
        String teamNameA = teamAName.getText().length() == 0 ? teamAName.getHint().toString() : teamAName.getText().toString();
        EditText teamBName = (EditText) findViewById(R.id.teamBName);
        String teamNameB = teamBName.getText().length() == 0 ? teamBName.getHint().toString() : teamBName.getText().toString();

        Switch possessionSwitch = (Switch) findViewById(R.id.firstServeSwitch);
        boolean isTeamAFirstServe = !possessionSwitch.isChecked();

        Intent startGameIntent = new Intent(this, VolleyballScoreboardActivity.class);
        startGameIntent.putExtra(VolleyballScoreboardActivity.MAX_SETS, maxSetArray[maxSetIndex]);
        startGameIntent.putExtra(VolleyballScoreboardActivity.MAX_PTS, maxPointArray[maxPtIndex]);
        startGameIntent.putExtra(VolleyballScoreboardActivity.TEAM_A_NAME, teamNameA);
        startGameIntent.putExtra(VolleyballScoreboardActivity.TEAM_B_NAME, teamNameB);
        startGameIntent.putExtra(VolleyballScoreboardActivity.FIRST_SERVE, isTeamAFirstServe);
        startActivity(startGameIntent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.maxSetOption:
                maxSetIndex = position;
                break;
            case R.id.maxPtsOption:
                maxPtIndex = position;
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /*
      POINTS TO CONSIDER:
      - Maximum number of sets
      - Maximum points in a set
      - Player line-up (both teams)
      - Starting six
      - First ball possession (First service)
      - Court assignment
     */
}
