/*
 * Copyright (C) 2009 University of Washington
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.widgets;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.IntegerData;
import org.javarosa.core.model.data.LongData;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.listeners.WidgetChangedListener;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.widget.EditText;

/**
 * Widget that restricts values to integers.
 * 
 * @author Carl Hartung (carlhartung@gmail.com)
 */
public class IntegerWidget extends StringWidget {
	
	int number_type;

    public IntegerWidget(Context context, FormEntryPrompt prompt, boolean secret, int num_type) {
        super(context, prompt, secret);

        mAnswer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mAnswerFontsize);
        
        this.number_type=num_type;

        // needed to make long readonly text scroll
        mAnswer.setHorizontallyScrolling(false);
        if(!secret) {
        	mAnswer.setSingleLine(false);
        }

        // only allows numbers and no periods
        mAnswer.setKeyListener(new DigitsKeyListener(true, false));

        // ints can only hold 2,147,483,648. we allow 999,999,999
        InputFilter[] fa = new InputFilter[1];
        if(number_type==1){
        	fa[0] = new InputFilter.LengthFilter(9);
        }
        else{
        	fa[0] = new InputFilter.LengthFilter(15);
        }
        
        
        mAnswer.setFilters(fa);

        if (prompt.isReadOnly()) {
            setBackgroundDrawable(null);
            setFocusable(false);
            setClickable(false);
        }
        
        if (getCurrentAnswer() != null){
        	if(number_type==1){
        		Integer i = (Integer) getCurrentAnswer().getValue();
        		if (i != null) {
                    mAnswer.setText(i.toString());
                }
        	}
        	else{
        		Long i= (Long) getCurrentAnswer().getValue();
        		if (i != null) {
                    mAnswer.setText(i.toString());
                }
        	}
        }
    }
    
    public IntegerWidget(Context context, FormEntryPrompt prompt, boolean secret, int num_type, WidgetChangedListener wcl) {
        super(context, prompt, secret, wcl);

        mAnswer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mAnswerFontsize);
        
        this.number_type=num_type;

        // needed to make long readonly text scroll
        mAnswer.setHorizontallyScrolling(false);
        if(!secret) {
        	mAnswer.setSingleLine(false);
        }

        // only allows numbers and no periods
        mAnswer.setKeyListener(new DigitsKeyListener(true, false));

        // ints can only hold 2,147,483,648. we allow 999,999,999
        InputFilter[] fa = new InputFilter[1];
        if(number_type==1){
        	fa[0] = new InputFilter.LengthFilter(9);
        }
        else{
        	fa[0] = new InputFilter.LengthFilter(15);
        }
        
        
        mAnswer.setFilters(fa);

        if (prompt.isReadOnly()) {
            setBackgroundDrawable(null);
            setFocusable(false);
            setClickable(false);
        }
        
        if (getCurrentAnswer() != null){
        	if(number_type==1){
        		Integer i = (Integer) getCurrentAnswer().getValue();
        		if (i != null) {
                    mAnswer.setText(i.toString());
                }
        	}
        	else{
        		Long i= (Long) getCurrentAnswer().getValue();
        		if (i != null) {
                    mAnswer.setText(i.toString());
                }
        	}
        }
    }
    
    @Override
    protected void setTextInputType(EditText mAnswer) {
    	if(secret) {
        	mAnswer.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
        	mAnswer.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }


    @Override
    public IAnswerData getAnswer() {
        String s = mAnswer.getText().toString().trim();
        if (s == null || s.equals("")) {
            return null;
        } else {
            try {
            	if(number_type==1){
            		return new IntegerData(Integer.parseInt(s));
            	}
            	else{
            		return new LongData(Long.parseLong(s));
            	}
            } catch (Exception NumberFormatException) {
                return null;
            }
        }
    }

}
