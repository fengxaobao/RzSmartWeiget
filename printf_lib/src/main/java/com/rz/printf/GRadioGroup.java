package com.rz.printf;

import android.view.View;
import android.view.ViewParent;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.widget.AppCompatRadioButton;

import java.util.ArrayList;
import java.util.List;

public class GRadioGroup {
    List<RadioButton> radios = new ArrayList<>();

    public GRadioGroup(RadioButton... radios) {
        super();
        for (RadioButton rb : radios) {
            this.radios.add(rb);
            rb.setOnClickListener(onClick);
        }
    }

    public GRadioGroup(View activity, int... radiosIDs) {
        super();
        for (int radioButtonID : radiosIDs) {
            RadioButton rb = (RadioButton) activity.findViewById(radioButtonID);
            if (rb != null) {
                this.radios.add(rb);
                rb.setOnClickListener(onClick);
            }
        }
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //let's deselect all radios in group
            for (RadioButton rb : radios) {
                ViewParent p = rb.getParent();
                if (p.getClass().equals(RadioGroup.class)) {
                    //if RadioButton belongs to RadioGroup,
                    // then deselect all radios in it
                    RadioGroup rg = (RadioGroup) p;
                    rg.clearCheck();
                } else {
                    //if RadioButton DOES NOT belong to RadioGroup,
                    //just deselect it
                    rb.setChecked(false);
                }
            }
            //now let's select currently clicked RadioButton
            if (v.getClass().equals(AppCompatRadioButton.class)) {
                RadioButton rb = (RadioButton) v;
                rb.setChecked(true);
            }
        }
    };
}
