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
import org.javarosa.core.model.data.StringData;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.listeners.WidgetChangedListener;

import android.content.Context;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TextKeyListener;
import android.text.method.TextKeyListener.Capitalize;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TableLayout;

/**
 * The most basic widget that allows for entry of any text.
 * 
 * @author Carl Hartung (carlhartung@gmail.com)
 * @author Yaw Anokwa (yanokwa@gmail.com)
 */
public class StringWidget extends QuestionWidget implements OnClickListener {

    boolean mReadOnly = false;
    protected EditText mAnswer;
    protected boolean secret = false;
    Context cntx;

    public StringWidget(Context context, FormEntryPrompt prompt, boolean secret) {
        super(context, prompt);
        cntx = context;
        mAnswer = new EditText(context);
        mAnswer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mAnswerFontsize);
        mAnswer.setImeOptions(0x10000000);
        mAnswer.setOnClickListener(this);
        TableLayout.LayoutParams params = new TableLayout.LayoutParams();
        params.setMargins(7, 5, 7, 5);
        mAnswer.setLayoutParams(params);
        
        this.secret = secret;
        
        if(!secret) {
        	// capitalize the first letter of the sentence
        	mAnswer.setKeyListener(new TextKeyListener(Capitalize.SENTENCES, false));
        }
        setTextInputType(mAnswer);

        // needed to make long read only text scroll
        mAnswer.setHorizontallyScrolling(false);
        if(!secret) {
        	mAnswer.setSingleLine(false);
        }

        if (prompt != null) {
            mReadOnly = prompt.isReadOnly();
            String s = prompt.getAnswerText();
            if (s != null) {
                mAnswer.setText(s);
            }

            if (mReadOnly) {
                if (s == null) {
                    mAnswer.setText("---");
                }
                mAnswer.setBackgroundDrawable(null);
                mAnswer.setFocusable(false);
                mAnswer.setClickable(false);
            }
        }

        addView(mAnswer);
    }
    
    public StringWidget(Context context, FormEntryPrompt prompt, boolean secret, WidgetChangedListener wcl) {
        super(context, prompt, wcl);
        cntx = context;
        mAnswer = new EditText(context);
        mAnswer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mAnswerFontsize);
        mAnswer.setImeOptions(0x10000000);
        mAnswer.setOnClickListener(this);
        TableLayout.LayoutParams params = new TableLayout.LayoutParams();
        params.setMargins(7, 5, 7, 5);
        mAnswer.setLayoutParams(params);
        
        this.secret = secret;
        
        if(!secret) {
        	// capitalize the first letter of the sentence
        	mAnswer.setKeyListener(new TextKeyListener(Capitalize.SENTENCES, false));
        }
        setTextInputType(mAnswer);

        // needed to make long read only text scroll
        mAnswer.setHorizontallyScrolling(false);
        if(!secret) {
        	mAnswer.setSingleLine(false);
        }

        if (prompt != null) {
            mReadOnly = prompt.isReadOnly();
            String s = prompt.getAnswerText();
            if (s != null) {
                mAnswer.setText(s);
            }

            if (mReadOnly) {
                if (s == null) {
                    mAnswer.setText("---");
                }
                mAnswer.setBackgroundDrawable(null);
                mAnswer.setFocusable(false);
                mAnswer.setClickable(false);
            }
        }

        addView(mAnswer);
    }
    
    protected void setTextInputType(EditText mAnswer) {
    	if(secret) {
        	mAnswer.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        	mAnswer.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    @Override
    public void clearAnswer() {
        mAnswer.setText(null);
    }


    @Override
    public IAnswerData getAnswer() {
        String s = mAnswer.getText().toString().trim();
        if (s == null || s.equals("")) {
            return null;
        } else {
            return new StringData(s);
        }
    }


    @Override
    public void setFocus(Context context) {
        // Put focus on text input field and display soft keyboard if appropriate.
        mAnswer.requestFocus();
        InputMethodManager inputManager =
            (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!mReadOnly) {
            inputManager.showSoftInput(mAnswer, 0);
            /*
             * If you do a multi-question screen after a "add another group" dialog, this won't
             * automatically pop up. It's an Android issue.
             * 
             * That is, if I have an edit text in an activity, and pop a dialog, and in that
             * dialog's button's OnClick() I call edittext.requestFocus() and
             * showSoftInput(edittext, 0), showSoftinput() returns false. However, if the edittext
             * is focused before the dialog pops up, everything works fine. great.
             */
        } else {
            inputManager.hideSoftInputFromWindow(mAnswer.getWindowToken(), 0);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.isAltPressed() == true) {
            return false;
        }
        if(hasListener){
        	widgetChangedListener.widgetEntryChanged();
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        mAnswer.setOnLongClickListener(l);
    }


    @Override
    public void cancelLongPress() {
        super.cancelLongPress();
        mAnswer.cancelLongPress();
    }

	@Override
	public void onClick(View v) {
		setFocus(cntx);
        mAnswer.setImeOptions(0x00000000);
	}

}
