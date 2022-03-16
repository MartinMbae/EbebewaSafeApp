package com.example.ebebewa_app.activities.registration.steps;


import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;

import ernestoyaquello.com.verticalstepperform.Step;
import com.example.ebebewa_app.R;

public class InviteAgentCode extends  Step<InviteAgentCode.AgentID>  {


    private TextInputEditText agent_id;


    public InviteAgentCode(String title, String subtitle) {
        super(title, subtitle);

    }

    @Override
    public AgentID getStepData() {

        String agent_idString = agent_id.getText() != null ? agent_id.getText().toString() :  "AG001";
        return new AgentID(agent_idString);
    }

    @NonNull
    @Override
    protected View createStepContentLayout() {

        // We create this step view by inflating an XML layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.step_agent_code, null, false);

        agent_id = view.findViewById(R.id.agent_id);


        return view;
    }

    @Override
    protected void onStepOpened(boolean animated) {
        // No need to do anything here
        markAsCompleted(true);
    }

    @Override
    protected void onStepClosed(boolean animated) {
        // No need to do anything here

    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {
        // No need to do anything here
    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {
        // No need to do anything here
    }


    @Override
    public String getStepDataAsHumanReadableString() {
        return "All fields are set";
    }

    @Override
    public void restoreStepData(AgentID data) {
        if (agent_id != null) agent_id.setText(data.agent_id);

    }

    @Override
    protected IsDataValid isStepDataValid(AgentID stepData) {
        return new IsDataValid(true);
    }

public static class AgentID{

    public String agent_id;

    public AgentID(String agent_id) {
        this.agent_id = agent_id;
    }
}

}
