
package org.odk.collect.android.views;

import org.javarosa.core.model.FormIndex;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.form.api.FormEntryCaption;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.preferences.PreferencesActivity;
import org.odk.collect.android.widgets.IBinaryWidget;
import org.odk.collect.android.widgets.QuestionWidget;
import org.odk.collect.android.widgets.WidgetFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewParent;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class is
 * 
 * @author carlhartung
 */
public class ODKView extends ScrollView implements OnLongClickListener {

    // starter random number for view IDs
    private final static int VIEW_ID = 12345;  
    
    private final static String t = "CLASSNAME";
    private final static int TEXTSIZE = 21;

    private LinearLayout mView;
    private LinearLayout.LayoutParams mLayout;
    private ArrayList<QuestionWidget> widgets;
    
    private int mQuestionFontsize;

    public final static String FIELD_LIST = "field-list";


    public ODKView(Context context, FormEntryPrompt questionPrompt, FormEntryCaption[] groups, WidgetFactory factory) {
        this(context, new FormEntryPrompt[] {
            questionPrompt
        }, groups, factory);
    }


    public ODKView(Context context, FormEntryPrompt[] questionPrompts, FormEntryCaption[] groups, WidgetFactory factory) {
        super(context);
        
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        String question_font =
                settings.getString(PreferencesActivity.KEY_FONT_SIZE, Collect.DEFAULT_FONTSIZE);

        mQuestionFontsize = new Integer(question_font).intValue();



        widgets = new ArrayList<QuestionWidget>();

        mView = new LinearLayout(getContext());
        mView.setOrientation(LinearLayout.VERTICAL);
        mView.setGravity(Gravity.TOP);
        mView.setPadding(0, 7, 0, 0);

        mLayout =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
        mLayout.setMargins(10, 0, 10, 0);

        //Figure out if we share hint text between questions
        String hintText = null;
        if(questionPrompts.length > 1) {
        	hintText = questionPrompts[0].getHelpText();
	        for (FormEntryPrompt p : questionPrompts) {
	        	//If something doesn't have hint text at all,
	        	//bail
	        	String curHintText = p.getHelpText();
        		//Otherwise see if it matches
        		if(curHintText == null || !curHintText.equals(hintText)) {
        			//If not, we can't do this trick
        			hintText = null;
        			break;
        		}
	        }
        }

        // display which group you are in as well as the question
        addGroupText(groups);
        
        addHintText(hintText);
        
        boolean first = true;
        int id = 0;
        
        for (FormEntryPrompt p : questionPrompts) {
            if (!first) {
                View divider = new View(getContext());
                divider.setBackgroundResource(android.R.drawable.divider_horizontal_bright);
                divider.setMinimumHeight(3);
                mView.addView(divider);
            } else {
                first = false;
            }

            // if question or answer type is not supported, use text widget
            QuestionWidget qw =
                factory.createWidgetFromPrompt(p, getContext());
            qw.setLongClickable(true);
            qw.setOnLongClickListener(this);
            qw.setId(VIEW_ID + id++);
            
            //Suppress the hint text if we bubbled it
            if(hintText != null) {
            	qw.hideHintText();
            }

            widgets.add(qw);
            mView.addView((View) qw, mLayout);


        }

        addView(mView);

    }


    /**
     * @return a HashMap of answers entered by the user for this set of widgets
     */
    public HashMap<FormIndex, IAnswerData> getAnswers() {
        HashMap<FormIndex, IAnswerData> answers = new HashMap<FormIndex, IAnswerData>();
        Iterator<QuestionWidget> i = widgets.iterator();
        while (i.hasNext()) {
            /*
             * The FormEntryPrompt has the FormIndex, which is where the answer gets stored. The
             * QuestionWidget has the answer the user has entered.
             */
            QuestionWidget q = i.next();
            FormEntryPrompt p = q.getPrompt();
            answers.put(p.getIndex(), q.getAnswer());
        }

        return answers;
    }
    
    /* (non-Javadoc)
	 * @see android.widget.LinearLayout#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int newHeight = MeasureSpec.getSize(heightMeasureSpec);
		int oldHeight = this.getMeasuredHeight();
		
		if(oldHeight == 0 || Math.abs(((newHeight * 1.0 - oldHeight) / oldHeight)) > .2) {
			for(QuestionWidget qw : this.widgets) { 
				qw.updateHelpSize(newHeight / 4);
			}
		}
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
				
//		double change = ((this.getMeasuredHeight() * 1.0 - gmh) / this.getMeasuredHeight()); 
//		System.out.println("Old: " + gmh + ". New: "+ this.getMeasuredHeight() + ". CurrentHeight: " + height);
//		
//		if(gmh == -1 || gmh == 0 || change > .2) {
//			//If the view size change has changed by more than 20% 
//			if(mHelpText != null) {
//				mHelpText.updateMaxHeight((this.getMeasuredHeight() - this.getScrollY())/ 3);
//			}
//		}
	}


    /**
     * // * Add a TextView containing the hierarchy of groups to which the question belongs. //
     */
    private void addGroupText(FormEntryCaption[] groups) {
        StringBuffer s = new StringBuffer("");
        String t = "";
        int i;
        // list all groups in one string
        for (FormEntryCaption g : groups) {
            i = g.getMultiplicity() + 1;
            t = g.getLongText();
            if (t != null) {
                s.append(t);
                if (g.repeats() && i > 0) {
                    s.append(" (" + i + ")");
                }
                s.append(" > ");
            }
        }

        // build view
        if (s.length() > 0) {
            TextView tv = new TextView(getContext());
            tv.setText(s.substring(0, s.length() - 3));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TEXTSIZE - 4);
            tv.setPadding(0, 0, 0, 5);
            mView.addView(tv, mLayout);
        }
    }
    
    private void addHintText(String hintText) {
        if (hintText != null && !hintText.equals("")) {
            TextView mHelpText = new TextView(getContext());
            mHelpText = new TextView(getContext());
            mHelpText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mQuestionFontsize - 3);
            mHelpText.setPadding(0, -5, 0, 7);
            // wrap to the widget of view
            mHelpText.setHorizontallyScrolling(false);
            mHelpText.setText(hintText);
            mHelpText.setTypeface(null, Typeface.ITALIC);

            mView.addView(mHelpText, mLayout);
        }
    }
    
    


    public void setFocus(Context context) {
        if (widgets.size() > 0) {
            widgets.get(0).setFocus(context);
        }
    }


    /**
     * Called when another activity returns information to answer this question.
     * 
     * @param answer
     */
    public void setBinaryData(Object answer) {
        boolean set = false;
        for (QuestionWidget q : widgets) {
            if (q instanceof IBinaryWidget) {
                if (((IBinaryWidget) q).isWaitingForBinaryData()) {
                    ((IBinaryWidget) q).setBinaryData(answer);
                    set = true;
                    break;
                }
            }
        }

        if (!set) {
            Log.w(t, "Attempting to return data to a widget or set of widgets no looking for data");
        }
    }


    /**
     * @return true if the answer was cleared, false otherwise.
     */
    public boolean clearAnswer() {
        // If there's only one widget, clear the answer.
        // If there are more, then force a long-press to clear the answer.
        if (widgets.size() == 1 && !widgets.get(0).getPrompt().isReadOnly()) {
            widgets.get(0).clearAnswer();
            return true;
        } else {
            return false;
        }
    }


    public ArrayList<QuestionWidget> getWidgets() {
        return widgets;
    }


    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        for (int i = 0; i < widgets.size(); i++) {
            QuestionWidget qw = widgets.get(i);
            qw.setOnFocusChangeListener(l);
        }
    }


    @Override
    public boolean onLongClick(View v) {
        return false;
    }
    

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();
        for (QuestionWidget qw : widgets) {
            qw.cancelLongPress();
        }
    }

}
