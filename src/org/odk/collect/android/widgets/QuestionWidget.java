package org.odk.collect.android.widgets;

import java.util.regex.Matcher;

import org.javarosa.core.model.FormIndex;
import org.javarosa.core.model.data.AnswerDataFactory;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.listeners.WidgetChangedListener;
import org.odk.collect.android.preferences.PreferencesActivity;
import org.odk.collect.android.views.MediaLayout;
import org.odk.collect.android.views.ShrinkingTextView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class QuestionWidget extends LinearLayout {

    @SuppressWarnings("unused")
    private final static String t = "QuestionWidget";

    private LinearLayout.LayoutParams mLayout;
    protected FormEntryPrompt mPrompt;

    protected final int mQuestionFontsize;
    protected final int mAnswerFontsize;
    protected final static String ACQUIREFIELD = "acquire";

    private TextView mQuestionText;
    private ShrinkingTextView mHelpText;
    protected boolean hasListener;
    private View toastView;
    
    //Whether this question widget needs to request focus on
    //its next draw, due to a new element having been added (which couldn't have
    //requested focus yet due to having not been layed out)
    protected boolean focusPending = false;
    
    protected WidgetChangedListener widgetChangedListener;


    public QuestionWidget(Context context, FormEntryPrompt p) {
    	this(context, p, null);
    }
    
    public QuestionWidget(Context context, FormEntryPrompt p, WidgetChangedListener w){
    	super(context);
    	
    	if(w!=null){
    		hasListener = false;
    		widgetChangedListener = w;
    	}
    	
    	hasListener = (w != null);
    	
    	
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            String question_font =
                settings.getString(PreferencesActivity.KEY_FONT_SIZE, Collect.DEFAULT_FONTSIZE);
            	mQuestionFontsize = new Integer(question_font).intValue();
            mAnswerFontsize = mQuestionFontsize + 2;

            mPrompt = p;

            setOrientation(LinearLayout.VERTICAL);
            setGravity(Gravity.TOP);
            setPadding(0, 7, 0, 0);

            mLayout =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
            mLayout.setMargins(10, 0, 10, 0);

            addQuestionText(p);
            addHelpText(p);
    }


    public FormEntryPrompt getPrompt() {
        return mPrompt;
    }


    // Abstract methods
    public abstract IAnswerData getAnswer();


    public abstract void clearAnswer();


    public abstract void setFocus(Context context);


    public abstract void setOnLongClickListener(OnLongClickListener l);
    
    
    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }
        @Override public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
    
    public void notifyWarning(String text) {
    	this.setBackgroundDrawable(this.getContext().getResources().getDrawable(R.drawable.bubble_warn));
    	
    	if(this.toastView == null) {
    		this.toastView = View.inflate(this.getContext(), R.layout.toast_view, this).findViewById(R.id.toast_view_root);
    		focusPending = true;
    	} else {
    		if(this.toastView.getVisibility() != View.VISIBLE) {
    			this.toastView.setVisibility(View.VISIBLE);
    			focusPending = true;
    		}
    	}
    	TextView messageView = (TextView)this.toastView.findViewById(R.id.message);
    	messageView.setText(text);
    	
    	//If the toastView already exists, we can just scroll to it right now
    	//if not, we actually have to do it later, when we lay this all back out
    	if(!focusPending) {
            requestViewOnScreen(messageView);
    	}
    }
    
    public void notifyInvalid(String text) {
    	this.setBackgroundDrawable(this.getContext().getResources().getDrawable(R.drawable.bubble_invalid));
    	
    	if(this.toastView == null) {
    		this.toastView = View.inflate(this.getContext(), R.layout.toast_view, this).findViewById(R.id.toast_view_root);
    		focusPending = true;
    	} else {
    		if(this.toastView.getVisibility() != View.VISIBLE) {
    			this.toastView.setVisibility(View.VISIBLE);
    			focusPending = true;
    		}
    	}
    	TextView messageView = (TextView)this.toastView.findViewById(R.id.message);
    	messageView.setText(text);
    	
    	//If the toastView already exists, we can just scroll to it right now
    	//if not, we actually have to do it later, when we lay this all back out
    	if(!focusPending) {
            requestViewOnScreen(messageView);
    	}
    }
    
    private void requestViewOnScreen(View view) {
        Rect toShow = new Rect();
	    view.getDrawingRect(toShow);
	    view.requestRectangleOnScreen(toShow);
    }
    
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		
		//If we're coming back in after we just laid out adding a new element that needs
		//focus, we can now scroll to it, since it's actually had its spacing declared.
		if(changed && focusPending) {
			focusPending = false;
			if(this.toastView == null) {
				//NOTE: This shouldn't be possible, but if it doesn't happen
                //we don't wanna crash. Look here if focus isn't getting grabbed
                //for some reason (there's no other negative consequence)
			} else {
				TextView messageView = (TextView)this.toastView.findViewById(R.id.message);
		    	requestViewOnScreen(messageView);
			}
		}
	}
    
    private void stripUnderlines(TextView textView) {
        Spannable s = (Spannable)textView.getText();
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span: spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }    


    /**
     * Add a Views containing the question text, audio (if applicable), and image (if applicable).
     * To satisfy the RelativeLayout constraints, we add the audio first if it exists, then the
     * TextView to fit the rest of the space, then the image if applicable.
     */
    protected void addQuestionText(FormEntryPrompt p) {
        String imageURI = p.getImageText();
        String audioURI = p.getAudioText();
        String videoURI = p.getSpecialFormQuestionText("video");
        String qrCodeContent = p.getSpecialFormQuestionText("qrcode");

        // shown when image is clicked
        String bigImageURI = p.getSpecialFormQuestionText("big-image");

        // Add the text view. Textview always exists, regardless of whether there's text.
        mQuestionText = new TextView(getContext());
        mQuestionText.setText(p.getLongText());
        mQuestionText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mQuestionFontsize);
        mQuestionText.setTypeface(null, Typeface.BOLD);
        mQuestionText.setPadding(0, 0, 0, 7);
        mQuestionText.setId(38475483); // assign random id

        if(p.getLongText()!= null){
        	if(p.getLongText().contains("\u260E")){
        		if(Linkify.addLinks(mQuestionText,Linkify.PHONE_NUMBERS)){
        			stripUnderlines(mQuestionText);
        		}
        		else{
        			System.out.println("this should be an error I'm thinking?");
        		}
        	}
        }
        // Wrap to the size of the parent view
        mQuestionText.setHorizontallyScrolling(false);

        if (p.getLongText() == null) {
            mQuestionText.setVisibility(GONE);
        }

        // Create the layout for audio, image, text
        MediaLayout mediaLayout = new MediaLayout(getContext());
        mediaLayout.setAVT(mQuestionText, audioURI, imageURI, videoURI, bigImageURI, qrCodeContent);

        addView(mediaLayout, mLayout);
    }
    
    public void updateHelpSize(int newMax) {
    	if(mHelpText != null) {
    		mHelpText.updateMaxHeight(newMax);
    	}
    }

	/**
     * Add a TextView containing the help text.
     */
    private void addHelpText(FormEntryPrompt p) {

        String s = p.getHelpText();

        if (s != null && !s.equals("")) {
            mHelpText = new ShrinkingTextView(getContext(),this.getMaxHintHeight());
            mHelpText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mQuestionFontsize - 3);
            mHelpText.setPadding(0, -5, 0, 7);
            // wrap to the widget of view
            mHelpText.setHorizontallyScrolling(false);
            mHelpText.setText(s);
            mHelpText.setTypeface(null, Typeface.ITALIC);

            addView(mHelpText, mLayout);
        }
    }

    protected int getMaxHintHeight() {
		return -1;
	}


	/**
     * Every subclassed widget should override this, adding any views they may contain, and calling
     * super.cancelLongPress()
     */
    public void cancelLongPress() {
        super.cancelLongPress();
        if (mQuestionText != null) {
            mQuestionText.cancelLongPress();
        }
        if (mHelpText != null) {
            mHelpText.cancelLongPress();
        }
    }
    
    protected IAnswerData getCurrentAnswer() {
    	IAnswerData current = mPrompt.getAnswerValue();
    	if(current == null) { return null; }
    	return getTemplate().cast(current.uncast());
    }
    
    protected IAnswerData getTemplate() {
    	return AnswerDataFactory.template(mPrompt.getControlType(), mPrompt.getDataType());
    }


	public void hideHintText() {
		mHelpText.setVisibility(View.GONE);
	}
	
	public FormIndex getFormId(){
		return mPrompt.getIndex();
	}
	
	public void setChangedListener(WidgetChangedListener wcl){
		widgetChangedListener = wcl;
		hasListener = true;
	}
	
	public void widgetEntryChanged(){
		if(this.toastView != null) {
			this.toastView.setVisibility(View.GONE);
			this.setBackgroundDrawable(null);
		}
		if(hasListener){
			widgetChangedListener.widgetEntryChanged();
		}
	}
}
